package cn.anecansaitin.hitboxapi.common.collider.battle.hurt;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.common.collider.basic.Sphere;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3f;

public class HurtSphere extends Sphere<Entity, Void> implements IHurtCollider {
    public float scale;

    public HurtSphere(float scale, Vector3f localCenter, float radius) {
        super(localCenter, radius);
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
        listTag.add(FloatTag.valueOf(radius));
        listTag.add(FloatTag.valueOf(scale));
        return null;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        localCenter.set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        radius = list.getFloat(3);
        scale = list.getFloat(4);
    }
}
