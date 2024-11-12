package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.CapsuleRender;
import cn.anecansaitin.hitboxapi.client.colliders.render.ICollisionRender;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class Capsule implements ICollision {
    public final Vector3f center;
    public final Quaternionf rotation;
    public final Vector3f direction;
    public float height;
    public float radius;
    public final Vector3f globalCenter;

    public boolean disable;
    private boolean isDirty;

    public Capsule(Vector3f center, float radius, float height, Quaternionf rotation) {
        this.center = center;
        this.globalCenter = new Vector3f(center);
        this.radius = radius;
        this.height = height;
        this.rotation = rotation;
        direction = new Vector3f(0, 1, 0);
        rotation.transform(direction);
    }

    @Override
    public void prepareColliding(BoxPoseStack poseStack) {
        if (isDirty || poseStack.isDirty()) {
            updateDirection(poseStack);
            isDirty = false;
        }
    }

    @Override
    public Collision getType() {
        return Collision.CAPSULE;
    }

    @Override
    public ICollisionRender getRenderer() {
        return CapsuleRender.INSTANCE;
    }

    public void markDirty() {
        isDirty = true;
    }

    private void updateDirection(BoxPoseStack poseStack) {
        BoxPoseStack.Pose pose = poseStack.last();
        Vector3f posOffset = pose.position;
        Quaternionf rotOffset = pose.rotation;
        rotOffset.transform(this.center, globalCenter).add(posOffset);
        Quaternionf rotation = rotOffset.mul(this.rotation, new Quaternionf());

        direction.set(0, 1, 0);
        rotation.transform(direction);
    }

    @Override
    public boolean disable() {
        return disable;
    }
}
