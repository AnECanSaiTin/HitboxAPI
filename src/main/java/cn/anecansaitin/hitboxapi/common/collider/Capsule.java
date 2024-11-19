package cn.anecansaitin.hitboxapi.common.collider;

import cn.anecansaitin.hitboxapi.api.common.collider.ICapsule;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Capsule<T, D> implements ICapsule<T, D> {
    public float height;
    public float radius;
    public final Vector3f localCenter;
    public final Quaternionf localRotation;
    public final Vector3f globalCenter;
    public final Vector3f globalDirection;

    public boolean disable;
    private boolean isDirty;

    public Capsule(Vector3f localCenter, float radius, float height, Quaternionf localRotation) {
        this.localCenter = localCenter;
        this.globalCenter = new Vector3f(localCenter);
        this.radius = radius;
        this.height = height;
        this.localRotation = localRotation;
        globalDirection = new Vector3f(0, 1, 0);
        localRotation.transform(globalDirection);
    }

    @Override
    public void prepareColliding(BoxPoseStack poseStack) {
        if (isDirty || poseStack.isDirty()) {
            updateDirection(poseStack);
            isDirty = false;
        }
    }

    @Override
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public void markDirty() {
        isDirty = true;
    }

    private void updateDirection(BoxPoseStack poseStack) {
        BoxPoseStack.Pose pose = poseStack.last();
        Vector3f posOffset = pose.position;
        Quaternionf rotOffset = pose.rotation;
        rotOffset.transform(this.localCenter, globalCenter).add(posOffset);
        Quaternionf rotation = rotOffset.mul(this.localRotation, new Quaternionf());

        globalDirection.set(0, 1, 0);
        rotation.transform(globalDirection);
    }

    @Override
    public boolean disable() {
        return disable;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public Vector3f getLocalCenter() {
        return localCenter;
    }

    @Override
    public Quaternionf getLocalRotation() {
        return localRotation;
    }

    @Override
    public Vector3f getGlobalDirection() {
        return globalDirection;
    }

    @Override
    public Vector3f getGlobalCenter() {
        return globalCenter;
    }
}
