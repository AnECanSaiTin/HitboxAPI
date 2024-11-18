package cn.anecansaitin.hitboxapi.common.collider;

import cn.anecansaitin.hitboxapi.client.colliders.render.AABBRender;
import cn.anecansaitin.hitboxapi.client.colliders.render.ICollisionRender;
import org.joml.Vector3f;

public interface IAABB<T, D> extends ICollider<T, D> {
    Vector3f getHalfExtents();

    Vector3f getLocalCenter();

    Vector3f getGlobalCenter();

    Vector3f getGlobalMin();

    Vector3f getGlobalMax();

    @Override
    default ColliderTyep getType() {
        return ColliderTyep.AABB;
    }

    @Override
    default ICollisionRender getRenderer() {
        return AABBRender.INSTANCE;
    }
}
