package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.CapsuleRender;
import cn.anecansaitin.hitboxapi.client.colliders.render.IColliderRender;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class Capsule implements ICollision {
    public final Vector3f center;
    public final Quaternionf rotation;
    public final Vector3f direction;
    public float height;
    public float radius;
    private boolean shouldUpdateDirection;

    public Capsule(Vector3f center, float radius, float height, Quaternionf rotation) {
        this.center = center;
        this.radius = radius;
        this.height = height;
        this.rotation = rotation;
        direction = new Vector3f(0, 0, 1);
        rotation.transform(direction);
    }

    @Override
    public void preIsColliding() {
        if (shouldUpdateDirection) {
            updateDirection();
        }
    }

    @Override
    public Collision getType() {
        return Collision.CAPSULE;
    }

    @Override
    public IColliderRender getRenderer() {
        return CapsuleRender.INSTANCE;
    }

    public void markRotationDirty() {
        shouldUpdateDirection = true;
    }

    private void updateDirection() {
        direction.set(0, 0, 1);
        rotation.transform(direction);
        shouldUpdateDirection = false;
    }
}
