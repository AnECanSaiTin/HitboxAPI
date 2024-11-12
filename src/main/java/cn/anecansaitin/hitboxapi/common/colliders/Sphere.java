package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.ICollisionRender;
import cn.anecansaitin.hitboxapi.client.colliders.render.SphereRender;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class Sphere implements ICollider {
    public static final Sphere EMPTY = new Sphere(new Vector3f(), 0);
    public final Vector3f center;
    public float radius;
    public final Vector3f globalCenter;

    public boolean disable;

    public Sphere(Vector3f center, float radius) {
        this.center = center;
        this.globalCenter = new Vector3f(center);
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
        rotOffset.transform(this.center, globalCenter).add(posOffset);
    }

    @Override
    public Collision getType() {
        return Collision.SPHERE;
    }

    @Override
    public ICollisionRender getRenderer() {
        return SphereRender.INSTANCE;
    }

    @Override
    public boolean disable() {
        return disable;
    }
}
