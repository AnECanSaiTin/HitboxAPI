package cn.anecansaitin.hitboxapi.common.attachment;

import cn.anecansaitin.hitboxapi.common.collider.*;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class EntityColliderHolder implements IEntityColliderHolder{
    /**
     * 受击判定
     */
    public Map<String, ICollider<Entity, Void>> hurtBox = new HashMap<>();

    /**
     * 攻击判定
     */
    public Map<String, ICollider<Entity, Void>> hitBox = new HashMap<>();

    public BoxPoseStack poseStack = new BoxPoseStack();

    @Override
    public Map<String, ICollider<Entity, Void>> getHurtBox() {
        return hurtBox;
    }

    @Override
    public Map<String, ICollider<Entity, Void>> getHitBox() {
        return hitBox;
    }

    @Override
    public BoxPoseStack getPoseStack() {
        return poseStack;
    }
}
