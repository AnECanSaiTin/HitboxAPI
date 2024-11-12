package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.ICollisionRender;

public interface ICollider {
    default boolean isColliding(BoxPoseStack poseStack, ICollider other, BoxPoseStack otherPoseStack) {
        return ColliderUtil.isColliding(this, poseStack, other, otherPoseStack);
    }

    default boolean isColliding(ICollider other) {
        return ColliderUtil.isColliding(this, other);
    }

    void prepareColliding(BoxPoseStack poseStack);

    Collision getType();

    ICollisionRender getRenderer();

    boolean disable();
}
