package cn.anecansaitin.hitboxapi.common.collider;

import cn.anecansaitin.hitboxapi.client.colliders.render.CapsuleRender;
import cn.anecansaitin.hitboxapi.client.colliders.render.ICollisionRender;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface ICapsule<T, D> extends ICollider<T, D> {
    float getHeight();

    float getRadius();

    Vector3f getLocalCenter();

    Quaternionf getLocalRotation();

    Vector3f getGlobalDirection();

    Vector3f getGlobalCenter();

    @Override
    default ColliderTyep getType() {
        return ColliderTyep.CAPSULE;
    }

    @Override
    default ICollisionRender getRenderer() {
        return CapsuleRender.INSTANCE;
    }
}
