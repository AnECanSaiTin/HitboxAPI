package cn.anecansaitin.hitboxapi.api.common.collider;

import cn.anecansaitin.hitboxapi.api.client.collider.ICollisionRender;
import cn.anecansaitin.hitboxapi.client.collider.render.OBBRender;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface IOBB<T, D> extends ICollider<T, D> {
    Vector3f getHalfExtents();

    Vector3f getLocalCenter();

    Quaternionf getLocalRotation();

    Vector3f getGlobalCenter();

    Vector3f[] getGlobalVertices();

    Vector3f[] getGlobalAxes();


    @Override
    default ColliderTyep getType() {
        return ColliderTyep.OBB;
    }

    @Override
    default ICollisionRender getRenderer() {
        return OBBRender.INSTANCE;
    }
}
