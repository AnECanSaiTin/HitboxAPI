package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.IColliderRender;
import cn.anecansaitin.hitboxapi.client.colliders.render.SphereRender;
import org.joml.Vector3f;

public final class Sphere implements ICollision {
    public static final Sphere EMPTY = new Sphere(new Vector3f(), 0);
    public final Vector3f center;
    public float radius;

    public Sphere(Vector3f center, float radius) {
        this.center = center;
        this.radius = radius;
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
        return SphereRender.INSTANCE;
    }
}
