package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.IColliderRender;
import org.joml.Vector3f;

public class Ray implements ICollision{
    public final Vector3f origin; // 射线的起点
    public final Vector3f direction;
    public final float length; // 射线的长度

    public Ray(Vector3f origin, Vector3f direction, float length) {
        this.origin = origin;
        this.direction = direction.normalize();
        this.length = length;
    }

    public Vector3f getEnd() {
        return direction.mul(length, new Vector3f()).add(origin);
    }

    @Override
    public void preIsColliding() {
    }

    @Override
    public Collision getType() {
        return Collision.RAY;
    }

    @Override
    public IColliderRender getRenderer() {
        return null;
    }
}
