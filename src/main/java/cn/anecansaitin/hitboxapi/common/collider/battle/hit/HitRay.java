package cn.anecansaitin.hitboxapi.common.collider.battle.hit;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHitCollider;
import cn.anecansaitin.hitboxapi.common.collider.basic.Ray;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3f;

public class HitRay extends Ray<Entity, Void> implements IHitCollider, INBTSerializable<CompoundTag> {
    public float damage;
    public ResourceKey<DamageType> damageType;

    public HitRay(float damage, ResourceKey<DamageType> damageType, Vector3f LocalOrigin, Vector3f localDirection, float length) {
        super(LocalOrigin, localDirection, length);
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
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        tag.put("0", listTag);
        listTag.add(FloatTag.valueOf(localOrigin.x));
        listTag.add(FloatTag.valueOf(localOrigin.y));
        listTag.add(FloatTag.valueOf(localOrigin.z));
        listTag.add(FloatTag.valueOf(localDirection.x));
        listTag.add(FloatTag.valueOf(localDirection.y));
        listTag.add(FloatTag.valueOf(localDirection.z));
        listTag.add(FloatTag.valueOf(length));
        listTag.add(FloatTag.valueOf(damage));
        tag.putString("1", damageType.location().toString());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        localOrigin.set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        localDirection.set(list.getFloat(3), list.getFloat(4), list.getFloat(5));
        length = list.getFloat(6);
        damage = list.getFloat(7);
        damageType = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(nbt.getString("1")));
    }
}
