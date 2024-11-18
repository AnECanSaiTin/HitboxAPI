package cn.anecansaitin.hitboxapi.common.collider;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Ray<T, D> implements IRay<T, D> {
    public final Vector3f LocalOrigin; // 射线的起点
    public final Vector3f globalOrigin; // 射线的起点
    public final Vector3f localDirection;
    public final Vector3f globalDirection;
    public final float length; // 射线的长度

    public boolean disable;

    public Ray(Vector3f LocalOrigin, Vector3f localDirection, float length) {
        this.LocalOrigin = LocalOrigin;
        this.globalOrigin = new Vector3f(LocalOrigin);
        this.localDirection = localDirection.normalize();
        this.globalDirection = new Vector3f(localDirection);
        this.length = length;
    }

    @Override
    public void prepareColliding(BoxPoseStack poseStack) {
        if (!poseStack.isDirty()) {
            return;
        }

        BoxPoseStack.Pose pose = poseStack.last();
        Vector3f posOffset = pose.position;
        Quaternionf rotOffset = pose.rotation;
        rotOffset.transform(this.LocalOrigin, globalOrigin).add(posOffset);
        rotOffset.transform(this.localDirection, globalDirection);
    }

    @Override
    public boolean disable() {
        return disable;
    }

    @Override
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Override
    public float getLength() {
        return length;
    }

    @Override
    public Vector3f getLocalOrigin() {
        return LocalOrigin;
    }

    @Override
    public Vector3f getLocalDirection() {
        return localDirection;
    }

    @Override
    public Vector3f getGlobalOrigin() {
        return globalOrigin;
    }

    @Override
    public Vector3f getGlobalDirection() {
        return globalDirection;
    }
}
