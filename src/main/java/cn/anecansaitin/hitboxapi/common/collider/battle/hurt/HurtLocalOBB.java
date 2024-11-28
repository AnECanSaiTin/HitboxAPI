package cn.anecansaitin.hitboxapi.common.collider.battle.hurt;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import cn.anecansaitin.hitboxapi.common.collider.local.LocalOBB;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HurtLocalOBB extends LocalOBB<Entity, Void> implements IHurtCollider {
    private float scale;
    /// 用于存储更新状态的字节。
    /// 每个位的含义如下：
    ///
    ///   - 第0位 (1 << 0): 表示 倍率
    ///   - 第1位 (1 << 1): 表示 局部中心点
    ///   - 第2位 (1 << 2): 表示 局部旋转
    ///   - 第3位 (1 << 3): 表示 轴半长
    ///   - 第4位 (1 << 4): 表示 禁用
    ///
    /// 例如，值 0b00000101 表示 倍率 和 局部旋转 需要更新。
    private byte update;

    public HurtLocalOBB(float scale, Vector3f halfExtents, Vector3f localCenter, Quaternionf localRotation, ICoordinateConverter parent) {
        super(halfExtents, localCenter, localRotation, parent);
        this.scale = scale;
    }

    @Override
    public float modifyDamage(float damage) {
        return scale * damage;
    }

    @Override
    public void setScale(float scale) {

    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        Vector3f center = getLocalCenter();
        Vector3f halfExtents = getHalfExtents();
        Quaternionf rotation = getLocalRotation();
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        tag.put("0", listTag);
        listTag.add(FloatTag.valueOf(center.x));
        listTag.add(FloatTag.valueOf(center.y));
        listTag.add(FloatTag.valueOf(center.z));
        listTag.add(FloatTag.valueOf(halfExtents.x));
        listTag.add(FloatTag.valueOf(halfExtents.y));
        listTag.add(FloatTag.valueOf(halfExtents.z));
        listTag.add(FloatTag.valueOf(rotation.x));
        listTag.add(FloatTag.valueOf(rotation.y));
        listTag.add(FloatTag.valueOf(rotation.z));
        listTag.add(FloatTag.valueOf(rotation.w));
        listTag.add(FloatTag.valueOf(scale));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        getLocalCenter().set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        getHalfExtents().set(list.getFloat(3), list.getFloat(4), list.getFloat(5));
        getLocalRotation().set(list.getFloat(6), list.getFloat(7), list.getFloat(8), list.getFloat(9));
        scale = list.getFloat(10);
    }

    @Override
    public boolean shouldUpdate() {
        return update != 0;
    }

    @Override
    public CompoundTag getUpdate() {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        tag.putByte("-1", update);
        tag.put("0", listTag);

        if ((update & 1) != 0) {
            listTag.add(FloatTag.valueOf(scale));
        }
        if ((update & 1 << 1) != 0) {
            Vector3f center = getLocalCenter();
            listTag.add(FloatTag.valueOf(center.x));
            listTag.add(FloatTag.valueOf(center.y));
            listTag.add(FloatTag.valueOf(center.z));
        }
        if ((update & 1 << 2) != 0) {
            Quaternionf rotation = getLocalRotation();
            listTag.add(FloatTag.valueOf(rotation.x));
            listTag.add(FloatTag.valueOf(rotation.y));
            listTag.add(FloatTag.valueOf(rotation.z));
            listTag.add(FloatTag.valueOf(rotation.w));
        }
        if ((update & 1 << 3) != 0) {
            Vector3f halfExtents = getHalfExtents();
            listTag.add(FloatTag.valueOf(halfExtents.x));
            listTag.add(FloatTag.valueOf(halfExtents.y));
            listTag.add(FloatTag.valueOf(halfExtents.z));
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
            getLocalCenter().set(list.getFloat(index++), list.getFloat(index++), list.getFloat(index++));
        }
        if ((update & 1 << 2) != 0) {
            getLocalRotation().set(list.getFloat(index++), list.getFloat(index++), list.getFloat(index++), list.getFloat(index++));
        }
        if ((update & 1 << 3) != 0) {
            getHalfExtents().set(list.getFloat(index++), list.getFloat(index++), list.getFloat(index));
        }
        if ((update & 1 << 4) != 0) {
            setDisable(tag.getBoolean("1"));
        }
    }
}
