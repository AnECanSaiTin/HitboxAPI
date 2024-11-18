package cn.anecansaitin.hitboxapi.common.collider;

import cn.anecansaitin.hitboxapi.client.colliders.render.ICollisionRender;
import cn.anecansaitin.hitboxapi.client.colliders.render.SphereRender;
import org.joml.Vector3f;

public interface ISphere<T, D> extends ICollider<T, D> {
    float getRadius();

    Vector3f getLocalCenter();

    Vector3f getGlobalCenter();

    @Override
    default ColliderTyep getType() {
        return ColliderTyep.SPHERE;
    }

    @Override
    default ICollisionRender getRenderer() {
        return SphereRender.INSTANCE;
    }
}
