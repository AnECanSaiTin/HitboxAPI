package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.IColliderRender;
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
                    case RAY -> CollisionUtil.isCollision((Ray) other, (OBB) this);
                };
            }
            case SPHERE -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isCollision((Sphere) this, (OBB) other);
                    case SPHERE -> CollisionUtil.isCollision((Sphere) this, (Sphere) other);
                    case CAPSULE -> CollisionUtil.isCollision((Capsule) other, (Sphere) this);
                    case AABB -> CollisionUtil.isCollision((Sphere) this, (AABB) other);
                    case RAY -> CollisionUtil.isCollision((Ray) other, (Sphere) this);
                };
            }
            case CAPSULE -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isCollision((Capsule) this, (OBB) other);
                    case SPHERE -> CollisionUtil.isCollision((Capsule) this, (Sphere) other);
                    case CAPSULE -> CollisionUtil.isCollision((Capsule) this, (Capsule) other);
                    case AABB -> CollisionUtil.isCollision((Capsule) this, (AABB) other);
                    case RAY -> CollisionUtil.isCollision((Ray) other, (Capsule) this);
                };
            }
            case AABB -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isCollision((OBB) other, new OBB((AABB) this));
                    case SPHERE -> CollisionUtil.isCollision((Sphere) other, (AABB) this);
                    case CAPSULE -> CollisionUtil.isCollision((Capsule) other, (AABB) this);
                    case AABB -> ((AABB) this).intersects((AABB) other);
                    case RAY -> CollisionUtil.isCollision((Ray) other, (AABB) this);
                };
            }
            case RAY -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isCollision((Ray) this, (OBB) other);
                    case SPHERE -> CollisionUtil.isCollision((Ray) this, (Sphere) other);
                    case CAPSULE -> CollisionUtil.isCollision((Ray) this, (Capsule) other);
                    case AABB -> CollisionUtil.isCollision((Ray) this, (AABB) other);
                    case RAY -> CollisionUtil.isCollision((Ray) this, (Ray) other);
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
