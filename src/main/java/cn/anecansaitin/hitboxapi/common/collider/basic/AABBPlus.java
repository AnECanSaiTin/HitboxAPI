package cn.anecansaitin.hitboxapi.common.collider.basic;

import cn.anecansaitin.hitboxapi.api.common.collider.IAABB;
import org.joml.Vector3f;

public class AABBPlus<T, D> implements IAABB<T, D> {
    private final Vector3f halfExtents;
    private final Vector3f center;
    private boolean disable;

    public AABBPlus(Vector3f halfExtents, Vector3f center) {
        this.halfExtents = halfExtents;
        this.center = center;
    }

    @Override
    public Vector3f getHalfExtents() {
        return halfExtents;
    }

    @Override
    public void setHalfExtents(Vector3f halfExtents) {
        this.halfExtents.set(halfExtents);
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
    public Vector3f getMin() {
        return center.sub(halfExtents, new Vector3f());
    }

    @Override
    public Vector3f getMax() {
        return center.add(halfExtents, new Vector3f());
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
        return "AABBPlus{" +
                "halfExtents=" + halfExtents +
                ", center=" + center +
                ", disable=" + disable +
                '}';
    }
}
