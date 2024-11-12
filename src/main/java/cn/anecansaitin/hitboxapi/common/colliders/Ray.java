package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.ICollisionRender;
import cn.anecansaitin.hitboxapi.client.colliders.render.RayRender;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class Ray implements ICollision{
    public final Vector3f origin; // 射线的起点
    public final Vector3f globalOrigin; // 射线的起点
    public final Vector3f direction;
    public final Vector3f globalDirection;
    public final float length; // 射线的长度

    public boolean disable;

    public Ray(Vector3f origin, Vector3f direction, float length) {
        this.origin = origin;
        this.globalOrigin = new Vector3f(origin);
        this.direction = direction.normalize();
        this.globalDirection = new Vector3f(direction);
        this.length = length;
    }

    public Vector3f getEnd() {
        return globalDirection.mul(length, new Vector3f()).add(globalOrigin);
    }

    @Override
    public void prepareColliding(BoxPoseStack poseStack) {
        if (!poseStack.isDirty()) {
            return;
        }

        BoxPoseStack.Pose pose = poseStack.last();
        Vector3f posOffset = pose.position;
        Quaternionf rotOffset = pose.rotation;
        rotOffset.transform(this.origin, globalOrigin).add(posOffset);
        rotOffset.transform(this.direction, globalDirection);
    }

    @Override
    public Collision getType() {
        return Collision.RAY;
    }

    @Override
    public boolean disable() {
        return disable;
    }

    @Override
    public ICollisionRender getRenderer() {
        return RayRender.INSTANCE;
    }
}
