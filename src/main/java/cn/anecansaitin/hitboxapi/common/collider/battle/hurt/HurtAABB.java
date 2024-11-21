package cn.anecansaitin.hitboxapi.common.collider.battle.hurt;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.common.collider.basic.AABBPlus;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3f;

public class HurtAABB extends AABBPlus<Entity, Void> implements IHurtCollider {
    public float scale;

    public HurtAABB(float scale, Vector3f center, Vector3f halfExtents) {
        super(center, halfExtents);
        this.scale = scale;
    }

    public HurtAABB(float scale, AABB aabb) {
        super(aabb);
        this.scale = scale;
    }

    @Override
    public float modifyDamage(float damage) {
        return damage * scale;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
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
        center.set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        halfExtents.set(list.getFloat(3), list.getFloat(4), list.getFloat(5));
        scale = list.getFloat(6);
    }
}
