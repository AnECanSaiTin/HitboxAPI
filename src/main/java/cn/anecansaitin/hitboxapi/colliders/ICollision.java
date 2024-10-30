package cn.anecansaitin.hitboxapi.colliders;

import cn.anecansaitin.hitboxapi.client.IColliderRender;
import net.minecraft.world.phys.AABB;

public interface ICollision {
    default boolean isColliding(ICollision other) {
        preIsColliding();
        other.preIsColliding();

        switch (getType()) {
            case OBB -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isCollision((OBB) this, (OBB) other);
                    case SPHERE -> CollisionUtil.isCollision((Sphere) other, (OBB) this);
                    case CAPSULE -> CollisionUtil.isCollision((Capsule) other, (OBB) this);
                    case AABB -> CollisionUtil.isCollision((OBB) this, new OBB((AABB) other));
                };
            }
            case SPHERE -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isCollision((Sphere) this, (OBB) other);
                    case SPHERE -> CollisionUtil.isCollision((Sphere) this, (Sphere) other);
                    case CAPSULE -> CollisionUtil.isCollision((Capsule) other, (Sphere) this);
                    case AABB -> CollisionUtil.isCollision((Sphere) this, (AABB) other);
                };
            }
            case CAPSULE -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isCollision((Capsule) this, (OBB) other);
                    case SPHERE -> CollisionUtil.isCollision((Capsule) this, (Sphere) other);
                    case CAPSULE -> CollisionUtil.isCollision((Capsule) this, (Capsule) other);
                    case AABB -> CollisionUtil.isCollision((Capsule) this, (AABB) other);
                };
            }
            case AABB -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isCollision((OBB) other, new OBB((AABB) this));
                    case SPHERE -> CollisionUtil.isCollision((Sphere) other, (AABB) this);
                    case CAPSULE -> CollisionUtil.isCollision((Capsule) other, (AABB) this);
                    case AABB -> ((AABB) this).intersects((AABB) other);
                };
            }
            default -> {
                return false;
            }
        }
    }

    void preIsColliding();

    Collision getType();

    IColliderRender getRenderer();
}
