package cn.anecansaitin.hitboxapi.colliders;

import cn.anecansaitin.hitboxapi.client.IColliderRender;
import org.joml.Vector3f;

public final class Sphere implements ICollision {
    public static final Sphere EMPTY = new Sphere(new Vector3f(), 0);
    private final Vector3f center;
    private float radius;

    public Sphere(Vector3f center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public Vector3f getCenter() {
        return center;
    }

    public void setCenter(Vector3f center) {
        this.center.set(center);
    }

    public void setCenter(float x, float y, float z) {
        this.center.set(x, y, z);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public boolean isColliding(ICollision other) {
        return false;
    }

    @Override
    public void preIsColliding() {
    }

    @Override
    public Collision getType() {
        return Collision.SPHERE;
    }

    @Override
    public IColliderRender getRenderer() {
        return null;
    }
}
