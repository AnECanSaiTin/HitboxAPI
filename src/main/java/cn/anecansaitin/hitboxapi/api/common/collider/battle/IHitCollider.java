package cn.anecansaitin.hitboxapi.api.common.collider.battle;

import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public interface IHitCollider extends ICollider<Entity, Void>, INBTSerializable<CompoundTag> {
    float getDamage();

    ResourceKey<DamageType> getDamageType();

    @Override
    default <O> void onCollide(Entity entity, O otherEntity, ICollider<O, ?> other, @Nullable Void data) {
        if (!(otherEntity instanceof Entity enemy)) {
            return;
        }

        if (!(other instanceof IHurtCollider hurtCollider)) {
            return;
        }

        DamageSources damageSources = entity.damageSources();
        enemy.hurt(damageSources.source(getDamageType(), entity), hurtCollider.modifyDamage(getDamage()));
    }
}
