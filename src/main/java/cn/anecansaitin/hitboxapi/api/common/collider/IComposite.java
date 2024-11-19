package cn.anecansaitin.hitboxapi.api.common.collider;

import cn.anecansaitin.hitboxapi.client.collider.render.CompositeRender;
import cn.anecansaitin.hitboxapi.api.client.collider.ICollisionRender;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface IComposite<T, D> extends ICollider<T, D> {
    Vector3f getLocalPosition();

    Quaternionf getLocalRotation();

    Vector3f getGlobalPosition();

    Quaternionf getGlobalRotation();

    int getCollidersCount();

    ICollider<T, D> getCollider(int index);

    @Override
    default ColliderTyep getType() {
        return ColliderTyep.COMPOSITE;
    }

    @Override
    default ICollisionRender getRenderer() {
        return CompositeRender.INSTANCE;
    }
}
