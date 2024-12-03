package cn.anecansaitin.hitboxapi.api.common.collider.battle;

import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ILocalCollider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public interface IHitCollider extends ILocalCollider<Entity, Void>, INBTSerializable<CompoundTag>, IIncremental<CompoundTag> {
    float getDamage();

    void setDamage(float damage);

    ResourceKey<DamageType> getDamageType();

    void setDamageType(ResourceKey<DamageType> damageType);

    @Override
    default <O> void onCollide(Entity entity, O otherEntity, ICollider<O, ?> other, @Nullable Void data) {
        if (!(otherEntity instanceof Entity enemy)) {
            return;
        }

        float damage;

        if (!(other instanceof IHurtCollider hurtCollider)) {
            damage = getDamage();
        } else {
            damage = hurtCollider.modifyDamage(getDamage());
        }

        DamageSources damageSources = entity.damageSources();
        enemy.hurt(damageSources.source(getDamageType(), entity), damage);
    }
}
