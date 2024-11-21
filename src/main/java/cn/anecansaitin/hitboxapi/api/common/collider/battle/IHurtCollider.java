package cn.anecansaitin.hitboxapi.api.common.collider.battle;

import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface IHurtCollider extends ICollider<Entity, Void>, INBTSerializable<CompoundTag> {
    float modifyDamage(float damage);
}
