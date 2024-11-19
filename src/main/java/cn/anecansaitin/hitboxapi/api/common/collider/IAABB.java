package cn.anecansaitin.hitboxapi.api.common.collider;

import cn.anecansaitin.hitboxapi.client.collider.render.AABBRender;
import cn.anecansaitin.hitboxapi.api.client.collider.ICollisionRender;
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
