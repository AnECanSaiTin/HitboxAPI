package cn.anecansaitin.hitboxapi;

import org.joml.Vector3f;

public final class Sphere {
    private final Vector3f center;
    private float radius;

    public Sphere(Vector3f center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public boolean isCollision(Sphere other) {
        return center.distanceSquared(other.center) <= (radius + other.radius) * (radius + other.radius);
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
}
