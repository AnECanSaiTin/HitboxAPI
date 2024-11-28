package cn.anecansaitin.hitboxapi.common.attachment;

import cn.anecansaitin.hitboxapi.api.common.attachment.IEntityColliderHolder;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHitCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.common.collider.battle.hit.*;
import cn.anecansaitin.hitboxapi.common.collider.battle.hurt.*;
import cn.anecansaitin.hitboxapi.common.collider.local.EntityCoordinateConverter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntityColliderHolder implements IEntityColliderHolder {
    /**
     * 受击判定
     */
    private final Map<String, IHurtCollider> hurtBox = new HashMap<>();

    /**
     * 攻击判定
     */
    private final Map<String, IHitCollider> hitBox = new HashMap<>();

    private EntityCoordinateConverter converter;

    /// 存储碰撞箱的修改行为记录，用于增量更新。
    /// 每个Compound含义如下：
    ///
    /// - "0" 修改类型
    ///     - true 增加
    ///     - false 删除
    /// - "1" 集合类型
    ///     - true Hurt
    ///     - false Hit
    /// - "2" 碰撞箱名称
    /// - "3" 碰撞箱类型
    /// - "4" 碰撞箱完整序列化
    private final ArrayList<CompoundTag> changeLog = new ArrayList<>();

    /// 仅用于注册
    @Deprecated()
    public EntityColliderHolder() {
    }

    public EntityColliderHolder(Entity entity) {
        converter = new EntityCoordinateConverter(entity);
    }

    @Override
    public Map<String, IHurtCollider> getHurtBox() {
        return hurtBox;
    }

    @Override
    public Map<String, IHitCollider> getHitBox() {
        return hitBox;
    }

    @Override
    public void addHurtBox(String name, IHurtCollider collider) {
        hurtBox.put(name, collider);
        addChangeLog(name, collider);
    }

    @Override
    public void addHitBox(String name, IHitCollider collider) {
        hitBox.put(name, collider);
        addChangeLog(name, collider);
    }

    @Override
    public void removeHurtBox(String name) {
        if (hurtBox.remove(name) != null) {
            addRemoveLog(name, true);
        }
    }

    @Override
    public void removeHitBox(String name) {
        if (hitBox.remove(name) != null) {
            addRemoveLog(name, false);
        }
    }

    @Override
    public EntityCoordinateConverter getCoordinateConverter() {
        return converter;
    }

    @Override
    public boolean shouldUpdate() {
        return !changeLog.isEmpty() || checkChildUpdate();
    }

    @Override
    public CompoundTag getUpdate() {
        CompoundTag tag = new CompoundTag();
        if (!changeLog.isEmpty()) {
            ListTag list = new ListTag();
            tag.put("0", list);
            list.addAll(changeLog);
            changeLog.clear();
        }

        ListTag hurt = new ListTag();

        for (Map.Entry<String, IHurtCollider> entry : hurtBox.entrySet()) {
            IHurtCollider collider = entry.getValue();

            if (!collider.shouldUpdate()) {
                continue;
            }

            CompoundTag hurtTag = new CompoundTag();
            hurt.add(hurtTag);
            hurtTag.putString("0", entry.getKey());
            hurtTag.put("1", collider.getUpdate());
        }

        if (!hurt.isEmpty()) {
            tag.put("1", hurt);
        }

        ListTag hit = new ListTag();

        for (Map.Entry<String, IHitCollider> entry : hitBox.entrySet()) {
            IHitCollider collider = entry.getValue();

            if (!collider.shouldUpdate()) {
                continue;
            }

            CompoundTag hitTag = new CompoundTag();
            hit.add(hitTag);
            hitTag.putString("0", entry.getKey());
            hitTag.put("1", collider.getUpdate());
        }

        if (!hit.isEmpty()) {
            tag.put("2", hit);
        }

        return tag;
    }

    @Override
    public void update(CompoundTag tag) {
        for (Tag t : tag.getList("0", CompoundTag.TAG_COMPOUND)) {
            CompoundTag box = (CompoundTag) t;
            boolean modifyType = box.getBoolean("0");
            boolean mapType = box.getBoolean("1");
            String name = box.getString("2");

            if (modifyType) {
                byte boxType = box.getByte("3");
                CompoundTag serializer = box.getCompound("4");

                if (mapType) {
                    IHurtCollider collider = switch (boxType) {
                        case 0 -> new HurtLocalOBB(0, new Vector3f(), new Vector3f(), new Quaternionf(), converter);
                        case 1 -> new HurtLocalSphere(0, new Vector3f(), 0, converter);
                        case 2 -> new HurtLocalCapsule(0, 0, 0, new Vector3f(), new Quaternionf(), converter);
                        case 3 -> new HurtLocalAABB(0, new Vector3f(), new Vector3f(), converter);
                        case 4 -> new HurtLocalRay(0, new Vector3f(), new Vector3f(), 0, converter);
                        case 5 -> new HurtLocalComposite(0, new Vector3f(), new Quaternionf(), converter);
                        default -> throw new IllegalStateException("Unexpected value: " + boxType);
                    };

                    collider.deserializeNBT(null, serializer);
                    hurtBox.put(name, collider);
                } else {
                    IHitCollider collider = switch (boxType) {
                        case 0 ->
                                new HitLocalOBB(0, null, new Vector3f(), new Vector3f(), new Quaternionf(), converter);
                        case 1 -> new HitLocalSphere(0, null, new Vector3f(), 0, converter);
                        case 2 -> new HitLocalCapsule(0, null, 0, 0, new Vector3f(), new Quaternionf(), converter);
                        case 3 -> new HitLocalAABB(0, null, new Vector3f(), new Vector3f(), converter);
                        case 4 -> new HitLocalRay(0, null, new Vector3f(), new Vector3f(), 0, converter);
                        case 5 -> new HitLocalComposite(0, null, new Vector3f(), new Quaternionf(), converter);
                        default -> throw new IllegalStateException("Unexpected value: " + boxType);
                    };

                    collider.deserializeNBT(null, serializer);
                    hitBox.put(name, collider);
                }
            } else {
                if (mapType) {
                    hurtBox.remove(name);
                } else {
                    hitBox.remove(name);
                }
            }
        }

        for (Tag t : tag.getList("1", CompoundTag.TAG_COMPOUND)) {
            CompoundTag hurtTag = (CompoundTag) t;
            String name = hurtTag.getString("0");
            IHurtCollider collider = hurtBox.get(name);

            if (collider == null) {
                continue;
            }

            collider.update(hurtTag.getCompound("1"));
        }

        for (Tag t : tag.getList("2", CompoundTag.TAG_COMPOUND)) {
            CompoundTag hitTag = (CompoundTag) t;
            String name = hitTag.getString("0");
            IHitCollider collider = hitBox.get(name);

            if (collider == null) {
                continue;
            }

            collider.update(hitTag.getCompound("1"));
        }
    }

    private void addChangeLog(String name, ICollider<Entity, Void> collider) {
        CompoundTag root = new CompoundTag();
        changeLog.add(root);
        root.putBoolean("0", true);
        root.putString("2", name);
        byte type = switch (collider.getType()) {
            case OBB -> (byte) 0;
            case SPHERE -> (byte) 1;
            case CAPSULE -> (byte) 2;
            case AABB -> (byte) 3;
            case RAY -> (byte) 4;
            case COMPOSITE -> (byte) 5;
        };
        root.putByte("3", type);

        if (collider instanceof IHurtCollider hurtCollider) {
            root.putBoolean("1", true);
            root.put("4", hurtCollider.serializeNBT(null));
        } else {
            root.putBoolean("1", false);
            root.put("4", ((IHitCollider) collider).serializeNBT(null));
        }
    }

    private void addRemoveLog(String name, boolean mapType) {
        CompoundTag root = new CompoundTag();
        changeLog.add(root);
        root.putBoolean("0", false);
        root.putBoolean("1", mapType);
        root.putString("2", name);
    }

    private boolean checkChildUpdate() {
        for (IHurtCollider value : hurtBox.values()) {
            if (value.shouldUpdate()) {
                return true;
            }
        }

        for (IHitCollider value : hitBox.values()) {
            if (value.shouldUpdate()) {
                return true;
            }
        }

        return false;
    }
}
