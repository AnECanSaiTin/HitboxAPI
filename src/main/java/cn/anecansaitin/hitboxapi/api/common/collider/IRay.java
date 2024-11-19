package cn.anecansaitin.hitboxapi.api.common.collider;

import cn.anecansaitin.hitboxapi.api.client.collider.ICollisionRender;
import cn.anecansaitin.hitboxapi.client.collider.render.RayRender;
import org.joml.Vector3f;

public interface IRay<T, D> extends ICollider<T, D> {
    float getLength();

    Vector3f getLocalOrigin();

    Vector3f getLocalDirection();

    Vector3f getGlobalOrigin();

    Vector3f getGlobalDirection();

    default Vector3f getLocalEnd() {
        return getLocalDirection().mul(getLength(), new Vector3f()).add(getLocalOrigin());
    }

    default Vector3f getGlobalEnd() {
        return getGlobalDirection().mul(getLength(), new Vector3f()).add(getGlobalOrigin());
    }

    @Override
    default ColliderTyep getType() {
        return ColliderTyep.RAY;
    }

    @Override
    default ICollisionRender getRenderer() {
        return RayRender.INSTANCE;
    }
}
