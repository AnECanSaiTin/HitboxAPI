package cn.anecansaitin.hitboxapi.api.common.collider;

import cn.anecansaitin.hitboxapi.client.collider.render.CapsuleRender;
import cn.anecansaitin.hitboxapi.api.client.collider.ICollisionRender;
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
