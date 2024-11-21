package cn.anecansaitin.hitboxapi.common.collider.battle.hurt;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.common.collider.basic.OBB;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HurtOBB extends OBB<Entity, Void> implements IHurtCollider {
    public float scale;

    public HurtOBB(float scale, Vector3f localCenter, Vector3f halfExtents, Quaternionf localRotation) {
        super(localCenter, halfExtents, localRotation);
        this.scale = scale;
    }

    @Override
    public float modifyDamage(float damage) {
        return scale * damage;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        tag.put("0", listTag);
        listTag.add(FloatTag.valueOf(localCenter.x));
        listTag.add(FloatTag.valueOf(localCenter.y));
        listTag.add(FloatTag.valueOf(localCenter.z));
        listTag.add(FloatTag.valueOf(halfExtents.x));
        listTag.add(FloatTag.valueOf(halfExtents.y));
        listTag.add(FloatTag.valueOf(halfExtents.z));
        listTag.add(FloatTag.valueOf(localRotation.x));
        listTag.add(FloatTag.valueOf(localRotation.y));
        listTag.add(FloatTag.valueOf(localRotation.z));
        listTag.add(FloatTag.valueOf(localRotation.w));
        listTag.add(FloatTag.valueOf(scale));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        localCenter.set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        halfExtents.set(list.getFloat(3), list.getFloat(4), list.getFloat(5));
        localRotation.set(list.getFloat(6), list.getFloat(7), list.getFloat(8), list.getFloat(9));
        scale = list.getFloat(10);
    }
}
