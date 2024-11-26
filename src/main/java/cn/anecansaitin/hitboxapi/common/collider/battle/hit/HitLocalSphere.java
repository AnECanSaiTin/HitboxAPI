package cn.anecansaitin.hitboxapi.common.collider.battle.hit;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHitCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import cn.anecansaitin.hitboxapi.common.collider.local.LocalSphere;
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

public class HitLocalSphere extends LocalSphere<Entity, Void> implements IHitCollider {
    public float damage;
    public ResourceKey<DamageType> damageType;

    public HitLocalSphere(float damage, ResourceKey<DamageType> damageType, Vector3f localCenter, float radius, ICoordinateConverter parent) {
        super(localCenter, radius, parent);
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
        float radius = getRadius();
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        tag.put("0", listTag);
        listTag.add(FloatTag.valueOf(center.x));
        listTag.add(FloatTag.valueOf(center.y));
        listTag.add(FloatTag.valueOf(center.z));
        listTag.add(FloatTag.valueOf(radius));
        listTag.add(FloatTag.valueOf(damage));
        tag.putString("1", damageType.location().toString());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        getLocalCenter().set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        setRadius(list.getFloat(3));
        damage = list.getFloat(4);
        damageType = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(nbt.getString("1")));
    }
}
