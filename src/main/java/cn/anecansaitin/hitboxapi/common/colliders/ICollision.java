package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.ICollisionRender;

public interface ICollision {
    default boolean isColliding(BoxPoseStack poseStack, ICollision other, BoxPoseStack otherPoseStack) {
        return CollisionUtil.isColliding(this, poseStack, other, otherPoseStack);
    }

    default boolean isColliding(ICollision other) {
        return CollisionUtil.isColliding(this, other);
    }

    void preIsColliding(BoxPoseStack poseStack);

    Collision getType();

    ICollisionRender getRenderer();

    default boolean disable() {
        return false;
    }
}
