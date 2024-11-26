package cn.anecansaitin.hitboxapi.common.collider.basic;

import cn.anecansaitin.hitboxapi.api.common.collider.ISphere;
import org.joml.Vector3f;

public class Sphere<T, D> implements ISphere<T, D> {
    private float radius;
    private final Vector3f center;
    private boolean disable;

    public Sphere(Vector3f center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public Vector3f getCenter() {
        return center;
    }

    @Override
    public void setCenter(Vector3f center) {
        this.center.set(center);
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
    public String toString() {
        return "Sphere{" +
                "radius=" + radius +
                ", center=" + center +
                ", disable=" + disable +
                '}';
    }
}
