package cn.anecansaitin.hitboxapi.common.collider.battle.hit;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHitCollider;
import cn.anecansaitin.hitboxapi.common.collider.basic.OBB;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HitOBB extends OBB<Entity, Void> implements IHitCollider, INBTSerializable<CompoundTag> {
    public float damage;
    public ResourceKey<DamageType> damageType;

    public HitOBB(float damage, ResourceKey<DamageType> damageType, Vector3f localCenter, Vector3f halfExtents, Quaternionf localRotation) {
        super(localCenter, halfExtents, localRotation);
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
        listTag.add(FloatTag.valueOf(damage));
        tag.putString("1", damageType.location().toString());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        localCenter.set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        halfExtents.set(list.getFloat(3), list.getFloat(4), list.getFloat(5));
        localRotation.set(list.getFloat(6), list.getFloat(7), list.getFloat(8), list.getFloat(9));
        damage = list.getFloat(10);
        damageType = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(nbt.getString("1")));
    }
}
