package cn.anecansaitin.hitboxapi.common.collider.basic;

import cn.anecansaitin.hitboxapi.api.common.collider.ISphere;
import cn.anecansaitin.hitboxapi.common.collider.BoxPoseStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Sphere<T, D> implements ISphere<T, D> {
    public static final Sphere<?, ?> EMPTY = new Sphere<>(new Vector3f(), 0);
    public float radius;
    public final Vector3f localCenter;
    public final Vector3f globalCenter;

    public boolean disable;

    public Sphere(Vector3f localCenter, float radius) {
        this.localCenter = localCenter;
        this.globalCenter = new Vector3f(localCenter);
        this.radius = radius;
    }

    @Override
    public void prepareColliding(BoxPoseStack poseStack) {
        if (!poseStack.isDirty()) {
            return;
        }

        BoxPoseStack.Pose pose = poseStack.last();
        Vector3f posOffset = pose.position;
        Quaternionf rotOffset = pose.rotation;
        rotOffset.transform(this.localCenter, globalCenter).add(posOffset);
    }

    @Override
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Override
    public boolean disable() {
        return disable;
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
    public Vector3f getGlobalCenter() {
        return globalCenter;
    }
}
