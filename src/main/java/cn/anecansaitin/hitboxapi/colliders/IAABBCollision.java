package cn.anecansaitin.hitboxapi.colliders;

import cn.anecansaitin.hitboxapi.client.IColliderRender;

public interface IAABBCollision extends ICollision {
    IColliderRender renderer = (poseStack, buffer, entity, red, green, blue, alpha) -> {};

    @Override
    default IColliderRender getRenderer() {
        return renderer;
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
