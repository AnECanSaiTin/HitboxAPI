package cn.anecansaitin.hitboxapi.common.collider.battle.hurt;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import cn.anecansaitin.hitboxapi.common.collider.local.LocalRay;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3f;

public class HurtLocalRay extends LocalRay<Entity, Void> implements IHurtCollider {
    private float scale;
    /// 用于存储更新状态的字节。
    /// 每个位的含义如下：
    ///
    ///   - 第0位 (1 << 0): 表示 倍率
    ///   - 第1位 (1 << 1): 表示 局部起点
    ///   - 第2位 (1 << 2): 表示 局部方向
    ///   - 第3位 (1 << 3): 表示 长度
    ///   - 第5位 (1 << 4): 表示 禁用
    ///
    /// 例如，值 0b00000101 表示 倍率 和 局部方向 需要更新。
    private byte update;

    public HurtLocalRay(float scale, Vector3f LocalOrigin, Vector3f localDirection, float length, ICoordinateConverter parent) {
        super(LocalOrigin, localDirection, length, parent);
        this.scale = scale;
    }

    @Override
    public void setLocalOrigin(Vector3f localOrigin) {
        update |= 1 << 1;
        super.setLocalOrigin(localOrigin);
    }

    @Override
    public void setLocalDirection(Vector3f localDirection) {
        update |= 1 << 2;
        super.setLocalDirection(localDirection);
    }

    @Override
    public void setLength(float length) {
        update |= 1 << 3;
        super.setLength(length);
    }

    @Override
    public void setDirection(Vector3f direction) {
        update |= 1 << 2;
        super.setDirection(direction);
    }

    @Override
    public void setDisable(boolean disable) {
        update |= 1 << 4;
        super.setDisable(disable);
    }

    @Override
    public float modifyDamage(float damage) {
        return scale * damage;
    }

    @Override
    public void setScale(float scale) {
        update |= 1;
        this.scale = scale;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        Vector3f origin = getLocalOrigin();
        Vector3f direction = getLocalDirection();
        float length = getLength();
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        tag.put("0", listTag);
        listTag.add(FloatTag.valueOf(origin.x));
        listTag.add(FloatTag.valueOf(origin.y));
        listTag.add(FloatTag.valueOf(origin.z));
        listTag.add(FloatTag.valueOf(direction.x));
        listTag.add(FloatTag.valueOf(direction.y));
        listTag.add(FloatTag.valueOf(direction.z));
        listTag.add(FloatTag.valueOf(length));
        listTag.add(FloatTag.valueOf(scale));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        getLocalOrigin().set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        getLocalDirection().set(list.getFloat(3), list.getFloat(4), list.getFloat(5));
        setLength(list.getFloat(6));
        scale = list.getFloat(7);
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
            Vector3f origin = getLocalOrigin();
            list.add(FloatTag.valueOf(origin.x));
            list.add(FloatTag.valueOf(origin.y));
            list.add(FloatTag.valueOf(origin.z));
        }
        if ((update & 1 << 2) != 0) {
            Vector3f direction = getLocalDirection();
            list.add(FloatTag.valueOf(direction.x));
            list.add(FloatTag.valueOf(direction.y));
            list.add(FloatTag.valueOf(direction.z));
        }
        if ((update & 1 << 3) != 0) {
            list.add(FloatTag.valueOf(getLength()));
        }
        if ((update & 1 << 4) != 0) {
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
            setOriginOrDirectionDirty();
            getLocalOrigin().set(list.getFloat(index++), list.getFloat(index++), list.getFloat(index++));
        }
        if ((update & 1 << 2) != 0) {
            setOriginOrDirectionDirty();
            getLocalDirection().set(list.getFloat(index++), list.getFloat(index++), list.getFloat(index++));
        }
        if ((update & 1 << 3) != 0) {
            setLength(list.getFloat(index));
        }
        if ((update & 1 << 4) != 0) {
            setDisable(tag.getBoolean("1"));
        }
    }
}
