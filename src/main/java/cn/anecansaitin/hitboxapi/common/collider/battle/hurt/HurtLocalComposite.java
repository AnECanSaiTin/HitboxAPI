package cn.anecansaitin.hitboxapi.common.collider.battle.hurt;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import cn.anecansaitin.hitboxapi.common.collider.local.LocalComposite;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class HurtLocalComposite extends LocalComposite<IHurtCollider, Entity, Void> implements IHurtCollider {
    private float scale;
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
    ///   - 第0位 (1 << 0): 表示 倍率
    ///   - 第1位 (1 << 1): 表示 局部坐标
    ///   - 第2位 (1 << 2): 表示 局部旋转
    ///   - 第4位 (1 << 3): 表示 禁用
    ///
    /// 例如，值 0b00000101 表示 倍率 和 局部旋转 需要更新。
    private byte update;

    public HurtLocalComposite(float scale, Vector3f position, Quaternionf rotation, ICoordinateConverter parent) {
        super(position, rotation, parent);
        this.scale = scale;
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
    public void setCollider(int index, IHurtCollider collider) {
        addChangeLog((byte) 3, index, getColliderName(index), collider);
        super.setCollider(index, collider);
    }

    @Override
    public void setCollider(String name, IHurtCollider collider) {
        addChangeLog((byte) 3, getColliderIndex(name), name, collider);
        super.setCollider(name, collider);
    }

    @Override
    public void setCollider(int index, String name, IHurtCollider collider) {
        addChangeLog((byte) 3, index, name, collider);
        super.setCollider(index, name, collider);
    }

    @Override
    public void addCollider(IHurtCollider collider) {
        addChangeLog((byte) 0, getCollidersCount(), String.valueOf(getCollidersCount()), collider);
        super.addCollider(collider);
    }

    @Override
    public void addCollider(String name, IHurtCollider collider) {
        if (contains(name)) {
            setCollider(name, collider);
            return;
        }

        addChangeLog((byte) 0, getCollidersCount(), name, collider);
        super.addCollider(name, collider);
    }

    @Override
    public void addCollider(int index, String name, IHurtCollider collider) {
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
    public void setDisable(boolean disable) {
        update |= 1 << 3;
        super.setDisable(disable);
    }

    @Override
    public float modifyDamage(float damage) {
        return damage * scale;
    }

    @Override
    public void setScale(float scale) {
        update |= 1;
        this.scale = scale;
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
        listTag.add(FloatTag.valueOf(scale));

        ListTag boxList = new ListTag();
        tag.put("1", boxList);

        for (int i = 0, length = getCollidersCount(); i < length; i++) {
            IHurtCollider collider = getCollider(i);
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

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        getLocalPosition().set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        getLocalRotation().set(list.getFloat(3), list.getFloat(4), list.getFloat(5), list.getFloat(6));
        scale = list.getFloat(7);
        ListTag boxList = nbt.getList("1", CompoundTag.TAG_COMPOUND);
        ICoordinateConverter child = getConverter();

        for (int i = 0; i < boxList.size(); i++) {
            CompoundTag box = boxList.getCompound(i);
            byte type = box.getByte("0");
            String name = box.getString("1");
            CompoundTag compound = box.getCompound("2");

            IHurtCollider collider = switch (type) {
                case 0 -> new HurtLocalOBB(0, new Vector3f(), new Vector3f(), new Quaternionf(), child);
                case 1 -> new HurtLocalSphere(0, new Vector3f(), 0, child);
                case 2 -> new HurtLocalCapsule(0, 0, 0, new Vector3f(), new Quaternionf(), child);
                case 3 -> new HurtLocalAABB(0, new Vector3f(), new Vector3f(), child);
                case 4 -> new HurtLocalRay(0, new Vector3f(), new Vector3f(), 0, child);
                case 5 -> new HurtLocalComposite(0, new Vector3f(), new Quaternionf(), child);
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };

            collider.deserializeNBT(provider, compound);
            addCollider(name, collider);
        }
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
            list.add(FloatTag.valueOf(scale));
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
            tag.putBoolean("1", disable());
        }
        if (!changeLog.isEmpty()) {
            update |= 1 << 4;
            ListTag changeTag = new ListTag();
            tag.put("2", changeTag);
            changeTag.addAll(changeLog);
            changeLog.clear();
        }

        ListTag boxTag = new ListTag();

        for (int i = 0, length = getCollidersCount(); i < length; i++) {
            IHurtCollider collider = getCollider(i);

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
            tag.put("3", boxTag);
            update |= 1 << 5;
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
            scale = list.getFloat(index++);
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
            setDisable(tag.getBoolean("1"));
        }
        if ((update & 1 << 4) != 0) {
            ListTag logs = tag.getList("2", CompoundTag.TAG_COMPOUND);
            ICoordinateConverter child = getConverter();

            for (Tag value : logs) {
                CompoundTag log = (CompoundTag) value;
                byte modifyType = log.getByte("0");

                if (modifyType == 1) {
                    super.removeCollider(log.getInt("1"));
                } else {
                    int i = log.getInt("1");
                    String name = log.getString("2");
                    IHurtCollider collider = switch (log.getByte("3")) {
                        case 0 -> new HurtLocalOBB(0, new Vector3f(), new Vector3f(), new Quaternionf(), child);
                        case 1 -> new HurtLocalSphere(0, new Vector3f(), 0, child);
                        case 2 -> new HurtLocalCapsule(0, 0, 0, new Vector3f(), new Quaternionf(), child);
                        case 3 -> new HurtLocalAABB(0, new Vector3f(), new Vector3f(), child);
                        case 4 -> new HurtLocalRay(0, new Vector3f(), new Vector3f(), 0, child);
                        case 5 -> new HurtLocalComposite(0, new Vector3f(), new Quaternionf(), child);
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
        if ((update & 1 << 5) != 0) {
            ListTag boxTag = tag.getList("3", CompoundTag.TAG_COMPOUND);

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

    private void addChangeLog(byte changeType, int index, String name, IHurtCollider collider) {
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
