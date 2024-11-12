package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.AABBRender;
import cn.anecansaitin.hitboxapi.client.colliders.render.ICollisionRender;

public interface IAABBCollision extends ICollision {
    @Override
    default ICollisionRender getRenderer() {
        return AABBRender.INSTANCE;
    }

    @Override
    default Collision getType() {
        return Collision.AABB;
    }

    @Override
    default void preIsColliding(BoxPoseStack poseStack) {
    }
}
