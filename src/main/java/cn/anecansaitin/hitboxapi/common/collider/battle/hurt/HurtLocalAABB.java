package cn.anecansaitin.hitboxapi.common.collider.battle.hurt;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import cn.anecansaitin.hitboxapi.common.collider.local.LocalAABB;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3f;

public class HurtLocalAABB extends LocalAABB<Entity, Void> implements IHurtCollider {
    private float scale;
    /// 用于存储更新状态的字节。
    /// 每个位的含义如下：
    ///
    ///   - 第0位 (1 << 0): 表示 倍率
    ///   - 第1位 (1 << 1): 表示 局部中心点
    ///   - 第2位 (1 << 2): 表示 轴半长
    ///   - 第4位 (1 << 3): 表示 禁用
    ///
    /// 例如，值 0b00000101 表示 倍率 和 轴半长 需要更新。
    private byte update = 0;

    public HurtLocalAABB(float scale, Vector3f center, Vector3f halfExtents, ICoordinateConverter parent) {
        super(center, halfExtents, parent);
        this.scale = scale;
    }

    @Override
    public void setLocalCenter(Vector3f center) {
        update |= 1 << 1;
        super.setLocalCenter(center);
    }

    @Override
    public void setHalfExtents(Vector3f halfExtents) {
        update |= 1 << 2;
        super.setHalfExtents(halfExtents);
    }

    @Override
    public void setCenter(Vector3f center) {
        update |= 1 << 1;
        super.setCenter(center);
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
        Vector3f center = getLocalCenter();
        Vector3f halfExtents = getHalfExtents();
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        tag.put("0", list);
        list.add(FloatTag.valueOf(center.x));
        list.add(FloatTag.valueOf(center.y));
        list.add(FloatTag.valueOf(center.z));
        list.add(FloatTag.valueOf(halfExtents.x));
        list.add(FloatTag.valueOf(halfExtents.y));
        list.add(FloatTag.valueOf(halfExtents.z));
        list.add(FloatTag.valueOf(scale));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        getLocalCenter().set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        getHalfExtents().set(list.getFloat(3), list.getFloat(4), list.getFloat(5));
        scale = list.getFloat(6);
    }

    @Override
    public boolean shouldUpdate() {
        return update != 0;
    }

    @Override
    public CompoundTag getUpdate() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        tag.putByte("-1", update);
        tag.put("0", list);

        if ((update & 1) != 0) {
            list.add(FloatTag.valueOf(scale));
        }
        if ((update & 1 << 1) != 0) {
            Vector3f center = getLocalCenter();
            list.add(FloatTag.valueOf(center.x));
            list.add(FloatTag.valueOf(center.y));
            list.add(FloatTag.valueOf(center.z));
        }
        if ((update & 1 << 2) != 0) {
            Vector3f halfExtents = getHalfExtents();
            list.add(FloatTag.valueOf(halfExtents.x));
            list.add(FloatTag.valueOf(halfExtents.y));
            list.add(FloatTag.valueOf(halfExtents.z));
        }
        if ((update & 1 << 3) != 0) {
            tag.putBoolean("1", disable());
        }

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
            setCenterDirty();
            getLocalCenter().set(list.getFloat(index++), list.getFloat(index++), list.getFloat(index++));
        }
        if ((update & 1 << 2) != 0) {
            getHalfExtents().set(list.getFloat(index++), list.getFloat(index++), list.getFloat(index));
        }
        if ((update & 1 << 3) != 0) {
            setDisable(tag.getBoolean("1"));
        }
    }
}
