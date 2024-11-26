package cn.anecansaitin.hitboxapi.common.collider.battle.hit;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHitCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import cn.anecansaitin.hitboxapi.common.collider.local.LocalOBB;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HitLocalOBB extends LocalOBB<Entity, Void> implements IHitCollider {
    private float damage;
    private ResourceKey<DamageType> damageType;

    public HitLocalOBB(float damage, ResourceKey<DamageType> damageType, Vector3f halfExtents, Vector3f localCenter, Quaternionf localRotation, ICoordinateConverter parent) {
        super(halfExtents, localCenter, localRotation, parent);
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
        listTag.add(FloatTag.valueOf(damage));
        tag.putString("1", damageType.location().toString());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        getLocalCenter().set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        getHalfExtents().set(list.getFloat(3), list.getFloat(4), list.getFloat(5));
        getLocalRotation().set(list.getFloat(6), list.getFloat(7), list.getFloat(8), list.getFloat(9));
        damage = list.getFloat(10);
        damageType = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(nbt.getString("1")));
    }
}
