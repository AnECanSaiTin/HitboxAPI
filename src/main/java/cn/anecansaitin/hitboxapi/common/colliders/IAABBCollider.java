package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.AABBRender;
import cn.anecansaitin.hitboxapi.client.colliders.render.ICollisionRender;
import org.joml.Vector3f;

public interface IAABBCollider extends ICollider {
    default Vector3f hitboxApi$getGlobalOffset() {
        return null;
    }

    @Override
    default ICollisionRender getRenderer() {
        return AABBRender.INSTANCE;
    }

    @Override
    default Collision getType() {
        return Collision.AABB;
    }

    @Override
    default void prepareColliding(BoxPoseStack poseStack) {
        hitboxApi$preIsColliding(poseStack);
    }

    @Override
    default boolean disable() {
        return false;
    }

    default boolean hitboxApi$disable() {
        return disable();
    }

    default void hitboxApi$preIsColliding(BoxPoseStack poseStack) {
    }
}
