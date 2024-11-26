package cn.anecansaitin.hitboxapi.common.collider.battle.hit;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHitCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import cn.anecansaitin.hitboxapi.common.collider.local.LocalAABB;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3f;

public class HitLocalAABB extends LocalAABB<Entity, Void> implements IHitCollider {
    private float damage;
    private ResourceKey<DamageType> damageType;

    public HitLocalAABB(float damage, ResourceKey<DamageType> damageType, Vector3f center, Vector3f halfExtents, ICoordinateConverter parent) {
        super(center, halfExtents, parent);
        this.damage = damage;
        this.damageType = damageType;
    }

    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public ResourceKey<DamageType> getDamageType() {
        return damageType;
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
        list.add(FloatTag.valueOf(damage));
        tag.putString("1", damageType.location().toString());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        getLocalCenter().set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        getHalfExtents().set(list.getFloat(3), list.getFloat(4), list.getFloat(5));
        damage = list.getFloat(6);
        damageType = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(nbt.getString("1")));
    }
}
