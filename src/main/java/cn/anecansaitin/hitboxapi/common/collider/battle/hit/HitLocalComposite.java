package cn.anecansaitin.hitboxapi.common.collider.battle.hit;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHitCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import cn.anecansaitin.hitboxapi.common.collider.local.LocalComposite;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class HitLocalComposite extends LocalComposite<IHitCollider, Entity, Void> implements IHitCollider {
    private float damage;
    private ResourceKey<DamageType> damageType;
    /// 存储碰撞箱的修改行为记录，用于增量更新。
    /// 每个Compound含义如下：
    ///
    /// - "0" 修改类型
    ///     - 0 增加
    ///     - 1 删除
    ///     - 2 插入
    ///     - 3 修改
    /// - "1" 索引
    /// - "2" 碰撞箱名称
    /// - "3" 碰撞箱类型
    /// - "4" 碰撞箱完整序列化
    private final ArrayList<CompoundTag> changeLog = new ArrayList<>();
    /// 用于存储更新状态的字节。
    /// 每个位的含义如下：
    ///
    ///   - 第0位 (1 << 0): 表示 伤害
    ///   - 第1位 (1 << 1): 表示 局部坐标
    ///   - 第2位 (1 << 2): 表示 局部旋转
    ///   - 第3位 (1 << 3): 表示 伤害类型
    ///   - 第4位 (1 << 4): 表示 禁用
    ///   - 第5位 (1 << 5): 表示 操作记录
    ///   - 第6位 (1 << 6): 表示 子碰撞箱更新
    ///
    /// 例如，值 0b00000101 表示 伤害 和 局部旋转 需要更新。
    private byte update;

    public HitLocalComposite(float damage, ResourceKey<DamageType> damageType, Vector3f position, Quaternionf rotation, ICoordinateConverter parent) {
        super(position, rotation, parent);
        this.damage = damage;
        this.damageType = damageType;
    }

    @Override
    public void setLocalPosition(Vector3f position) {
        update |= 1 << 1;
        super.setLocalPosition(position);
    }

    @Override
    public void setLocalRotation(Quaternionf rotation) {
        update |= 1 << 2;
        super.setLocalRotation(rotation);
    }

    @Override
    public void setCollider(int index, IHitCollider collider) {
        addChangeLog((byte) 3, index, getColliderName(index), collider);
        super.setCollider(index, collider);
    }

    @Override
    public void setCollider(int index, String name, IHitCollider collider) {
        addChangeLog((byte) 3, index, name, collider);
        super.setCollider(index, name, collider);
    }

    @Override
    public void setDisable(boolean disable) {
        update |= 1 << 4;
        super.setDisable(disable);
    }

    @Override
    public void addCollider(IHitCollider collider) {
        addChangeLog((byte) 0, getCollidersCount(), String.valueOf(getCollidersCount()), collider);
        super.addCollider(collider);
    }

    @Override
    public void addCollider(String name, IHitCollider collider) {
        if (contains(name)) {
            setCollider(name, collider);
            return;
        }

        addChangeLog((byte) 0, getCollidersCount(), name, collider);
        super.addCollider(name, collider);
    }

    @Override
    public void addCollider(int index, String name, IHitCollider collider) {
        addChangeLog((byte) 2, index, name, collider);
        super.addCollider(index, name, collider);
    }

    @Override
    public void removeCollider(int index) {
        addRemoveLog(index);
        super.removeCollider(index);
    }

    @Override
    public void removeCollider(String name) {
        addRemoveLog(getColliderIndex(name));
        super.removeCollider(name);
    }

    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public void setDamage(float damage) {
        update |= 1;
        this.damage = damage;
    }

    @Override
    public ResourceKey<DamageType> getDamageType() {
        return damageType;
    }

    @Override
    public void setDamageType(ResourceKey<DamageType> damageType) {
        update |= 1 << 3;
        this.damageType = damageType;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        // 如果序列化了碰撞箱，则不再需要变化记录，避免重复生成子碰撞箱。
        changeLog.clear();
        Vector3f position = getLocalPosition();
        Quaternionf rotation = getLocalRotation();
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        tag.put("0", listTag);
        listTag.add(FloatTag.valueOf(position.x));
        listTag.add(FloatTag.valueOf(position.y));
        listTag.add(FloatTag.valueOf(position.z));
        listTag.add(FloatTag.valueOf(rotation.x));
        listTag.add(FloatTag.valueOf(rotation.y));
        listTag.add(FloatTag.valueOf(rotation.z));
        listTag.add(FloatTag.valueOf(rotation.w));
        listTag.add(FloatTag.valueOf(damage));

        tag.putString("1", damageType.location().toString());
        ListTag boxList = new ListTag();
        tag.put("2", boxList);

        for (int i = 0, length = getCollidersCount(); i < length; i++) {
            IHitCollider collider = getCollider(i);
            String name = getColliderName(i);
            byte type = switch (collider.getType()) {
                case OBB -> 0;
                case SPHERE -> 1;
                case CAPSULE -> 2;
                case AABB -> 3;
                case RAY -> 4;
                case COMPOSITE -> 5;
            };

            CompoundTag box = new CompoundTag();
            boxList.add(box);
            box.putByte("0", type);
            box.putString("1", name);
            box.put("2", collider.serializeNBT(provider));
        }

        tag.putBoolean("3", disable);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        getLocalPosition().set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        getLocalRotation().set(list.getFloat(3), list.getFloat(4), list.getFloat(5), list.getFloat(6));
        damage = list.getFloat(7);
        damageType = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(nbt.getString("1")));
        ListTag boxList = nbt.getList("2", CompoundTag.TAG_COMPOUND);
        ICoordinateConverter child = getConverter();

        for (int i = 0; i < boxList.size(); i++) {
            CompoundTag box = boxList.getCompound(i);
            byte type = box.getByte("0");
            String name = box.getString("1");
            CompoundTag compound = box.getCompound("2");

            IHitCollider collider = switch (type) {
                case 0 -> new HitLocalOBB(0, null, new Vector3f(), new Vector3f(), new Quaternionf(), child);
                case 1 -> new HitLocalSphere(0, null, new Vector3f(), 0, child);
                case 2 -> new HitLocalCapsule(0, null, 0, 0, new Vector3f(), new Quaternionf(), child);
                case 3 -> new HitLocalAABB(0, null, new Vector3f(), new Vector3f(), child);
                case 4 -> new HitLocalRay(0, null, new Vector3f(), new Vector3f(), 0, child);
                case 5 -> new HitLocalComposite(0, null, new Vector3f(), new Quaternionf(), child);
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };

            collider.deserializeNBT(provider, compound);
            addCollider(name, collider);
        }

        setDisable(nbt.getBoolean("3"));
    }

    @Override
    public boolean shouldUpdate() {
        return update != 0 || !changeLog.isEmpty() || checkChildUpdate();
    }

    @Override
    public CompoundTag getUpdate() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        tag.put("0", list);

        if ((update & 1) != 0) {
            list.add(FloatTag.valueOf(damage));
        }
        if ((update & 1 << 1) != 0) {
            Vector3f center = getLocalPosition();
            list.add(FloatTag.valueOf(center.x));
            list.add(FloatTag.valueOf(center.y));
            list.add(FloatTag.valueOf(center.z));
        }
        if ((update & 1 << 2) != 0) {
            Quaternionf rotation = getLocalRotation();
            list.add(FloatTag.valueOf(rotation.x));
            list.add(FloatTag.valueOf(rotation.y));
            list.add(FloatTag.valueOf(rotation.z));
            list.add(FloatTag.valueOf(rotation.w));
        }
        if ((update & 1 << 3) != 0) {
            tag.putString("1", damageType.location().toString());
        }
        if ((update & 1 << 4) != 0) {
            tag.putBoolean("2", disable());
        }
        if (!changeLog.isEmpty()) {
            update |= 1 << 5;
            ListTag changeTag = new ListTag();
            tag.put("3", changeTag);
            changeTag.addAll(changeLog);
            changeLog.clear();
        }

        ListTag boxTag = new ListTag();

        for (int i = 0, length = getCollidersCount(); i < length; i++) {
            IHitCollider collider = getCollider(i);

            if (!collider.shouldUpdate()) {
                continue;
            }

            CompoundTag boxUpdate = collider.getUpdate();
            CompoundTag boxWrap = new CompoundTag();
            boxWrap.putInt("0", i);
            boxWrap.put("1", boxUpdate);
            boxTag.add(boxWrap);
        }

        if (!boxTag.isEmpty()) {
            tag.put("4", boxTag);
            update |= 1 << 6;
        }

        tag.putByte("-1", update);
        update = 0;
        return tag;
    }

    @Override
    public void update(CompoundTag tag) {
        byte update = tag.getByte("-1");
        ListTag list = tag.getList("0", FloatTag.TAG_FLOAT);
        int index = 0;

        if ((update & 1) != 0) {
            damage = list.getFloat(index++);
        }
        if ((update & 1 << 1) != 0) {
            setPositionDirty();
            getLocalPosition().set(list.getFloat(index++), list.getFloat(index++), list.getFloat(index++));
        }
        if ((update & 1 << 2) != 0) {
            setRotationDirty();
            getLocalRotation().set(list.getFloat(index++), list.getFloat(index++), list.getFloat(index++), list.getFloat(index));
        }
        if ((update & 1 << 3) != 0) {
            damageType = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(tag.getString("1")));
        }
        if ((update & 1 << 4) != 0) {
            setDisable(tag.getBoolean("2"));
        }
        if ((update & 1 << 5) != 0) {
            ListTag logs = tag.getList("3", CompoundTag.TAG_COMPOUND);
            ICoordinateConverter child = getConverter();

            for (Tag value : logs) {
                CompoundTag log = (CompoundTag) value;
                byte modifyType = log.getByte("0");

                if (modifyType == 1) {
                    super.removeCollider(log.getInt("1"));
                } else {
                    int i = log.getInt("1");
                    String name = log.getString("2");
                    IHitCollider collider = switch (log.getByte("3")) {
                        case 0 -> new HitLocalOBB(0, null, new Vector3f(), new Vector3f(), new Quaternionf(), child);
                        case 1 -> new HitLocalSphere(0, null, new Vector3f(), 0, child);
                        case 2 -> new HitLocalCapsule(0, null, 0, 0, new Vector3f(), new Quaternionf(), child);
                        case 3 -> new HitLocalAABB(0, null, new Vector3f(), new Vector3f(), child);
                        case 4 -> new HitLocalRay(0, null, new Vector3f(), new Vector3f(), 0, child);
                        case 5 -> new HitLocalComposite(0, null, new Vector3f(), new Quaternionf(), child);
                        default -> throw new IllegalStateException("Unexpected value: " + log.getByte("3"));
                    };
                    collider.deserializeNBT(null, log.getCompound("4"));

                    switch (modifyType) {
                        case 0 -> super.addCollider(name, collider);
                        case 2 -> super.addCollider(i, name, collider);
                        case 3 -> super.setCollider(i, name, collider);
                    }
                }
            }
        }
        if ((update & 1 << 6) != 0) {
            ListTag boxTag = tag.getList("4", CompoundTag.TAG_COMPOUND);

            for (int i = 0; i < boxTag.size(); i++) {
                CompoundTag boxWrap = boxTag.getCompound(i);
                int boxIndex = boxWrap.getInt("0");
                CompoundTag boxUpdate = boxWrap.getCompound("1");
                getCollider(boxIndex).update(boxUpdate);
            }
        }
    }

    private boolean checkChildUpdate() {
        for (int i = 0, length = getCollidersCount(); i < length; i++) {
            if (getCollider(i).shouldUpdate()) {
                return true;
            }
        }

        return false;
    }

    private void addChangeLog(byte changeType, int index, String name, IHitCollider collider) {
        CompoundTag tag = new CompoundTag();
        changeLog.add(tag);
        tag.putByte("0", changeType);
        tag.putInt("1", index);
        tag.putString("2", name);
        byte type = switch (collider.getType()) {
            case OBB -> (byte) 0;
            case SPHERE -> (byte) 1;
            case CAPSULE -> (byte) 2;
            case AABB -> (byte) 3;
            case RAY -> (byte) 4;
            case COMPOSITE -> (byte) 5;
        };
        tag.putByte("3", type);
        tag.put("4", collider.serializeNBT(null));
    }

    private void addRemoveLog(int index) {
        CompoundTag tag = new CompoundTag();
        changeLog.add(tag);
        tag.putByte("0", (byte) 1);
        tag.putInt("1", index);
    }
}
