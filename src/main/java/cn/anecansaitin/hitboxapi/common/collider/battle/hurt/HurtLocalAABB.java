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

    public HurtLocalAABB(float scale, Vector3f center, Vector3f halfExtents, ICoordinateConverter parent) {
        super(center, halfExtents, parent);
        this.scale = scale;
    }

    @Override
    public float modifyDamage(float damage) {
        return damage * scale;
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
}
