package cn.anecansaitin.hitboxapi.common.attachment;

import cn.anecansaitin.hitboxapi.api.common.attachment.IEntityColliderHolder;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.common.collider.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class EntityColliderHolder implements IEntityColliderHolder {
    /**
     * 受击判定
     */
    public Map<String, ICollider<Entity, Void>> hurtBox = new HashMap<>();

    /**
     * 攻击判定
     */
    public Map<String, ICollider<Entity, Void>> hitBox = new HashMap<>();

    public BoxPoseStack poseStack = new BoxPoseStack();
    public boolean isDirty = true;

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

    @Override
    public void markDirty() {
        isDirty = true;
    }

    @Override
    public void updatePoseStack(Entity entity) {
        BoxPoseStack.Pose pose = getPoseStack().last();
        Vector3f posePos = pose.position;
        Vec3 position = entity.position();
        float x = (float) position.x;
        float y = (float) position.y;
        float z = (float) position.z;

        if (!posePos.equals(x, y, z)) {
            posePos.set(x, y, z);
            pose.isDirty = true;
        } else {
            pose.isDirty = false;
        }

        if (isDirty) {
            pose.isDirty = true;
        }
    }
}
