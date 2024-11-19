package cn.anecansaitin.hitboxapi.api.common.collider;

import cn.anecansaitin.hitboxapi.api.client.collider.ICollisionRender;
import cn.anecansaitin.hitboxapi.client.collider.render.SphereRender;
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
