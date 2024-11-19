package cn.anecansaitin.hitboxapi.api.common.attachment;

import cn.anecansaitin.hitboxapi.common.collider.BoxPoseStack;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Map;

public interface IEntityColliderHolder {
    Map<String, ICollider<Entity, Void>> getHurtBox();

    Map<String, ICollider<Entity, Void>> getHitBox();

    BoxPoseStack getPoseStack();
}
