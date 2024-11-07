package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.AABBRender;
import cn.anecansaitin.hitboxapi.client.colliders.render.IColliderRender;

public interface IAABBCollision extends ICollision {
    @Override
    default IColliderRender getRenderer() {
        return AABBRender.INSTANCE;
    }

    @Override
    default Collision getType() {
        return Collision.AABB;
    }

    @Override
    default void preIsColliding() {
    }

    @Override
    default boolean isColliding(ICollision other) {
        return ICollision.super.isColliding(other);
    }
}
