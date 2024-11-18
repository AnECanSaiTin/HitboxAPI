package cn.anecansaitin.hitboxapi.common.attachment;

import cn.anecansaitin.hitboxapi.common.collider.BoxPoseStack;
import cn.anecansaitin.hitboxapi.common.collider.ICollider;
import net.minecraft.world.entity.Entity;

import java.util.Map;

public interface IEntityColliderHolder {
    Map<String, ICollider<Entity, Void>> getHurtBox();

    Map<String, ICollider<Entity, Void>> getHitBox();

    BoxPoseStack getPoseStack();
}
