package cn.anecansaitin.hitboxapi.mixin.common;

import cn.anecansaitin.hitboxapi.common.colliders.BoxPoseStack;
import cn.anecansaitin.hitboxapi.common.colliders.IAABBCollision;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AABB.class)
public abstract class AABBMixin implements IAABBCollision {
    @Shadow public abstract Vec3 getCenter();

    @Unique
    public final Vector3f hitboxApi$globalOffset = new Vector3f();

    @Unique
    public boolean hitboxApi$disable;

    @Override
    public Vector3f hitboxApi$getGlobalOffset() {
        return hitboxApi$globalOffset;
    }

    @Override
    public void hitboxApi$preIsColliding(BoxPoseStack poseStack) {
        if (!poseStack.isDirty()) {
            return;
        }

        BoxPoseStack.Pose pose = poseStack.last();
        Vector3f posOffset = pose.position;
        Quaternionf rotOffset = pose.rotation;
        Vec3 vec3 = this.getCenter();
        rotOffset.transform((float) vec3.x, (float) vec3.y, (float) vec3.z, hitboxApi$globalOffset).add(posOffset);
    }

    @Override
    public boolean hitboxApi$disable() {
        return hitboxApi$disable;
    }
}
