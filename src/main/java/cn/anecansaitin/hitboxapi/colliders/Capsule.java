package cn.anecansaitin.hitboxapi.colliders;

import cn.anecansaitin.hitboxapi.client.IColliderRender;
import it.unimi.dsi.fastutil.Pair;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class Capsule implements ICollision {
    private final Vector3f center;
    private final Quaternionf rotation;
    private final Vector3f direction;
    private float height;
    private float radius;
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
        return null;
    }

    public Vector3f getCenter() {
        return center;
    }

    public void setCenter(Vector3f center) {
        this.center.set(center);
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation.set(rotation);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Vector3f getDirection() {
        return direction;
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
