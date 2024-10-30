package cn.anecansaitin.hitboxapi.colliders;

import cn.anecansaitin.hitboxapi.client.IColliderRender;

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
                };
            }
            case SPHERE -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isCollision((Sphere) this, (OBB) other);
                    case SPHERE -> CollisionUtil.isCollision((Sphere) this, (Sphere) other);
                    case CAPSULE -> CollisionUtil.isCollision((Capsule) other, (Sphere) this);
                };
            }
            case CAPSULE -> {
                return switch (other.getType()) {
                    case OBB -> CollisionUtil.isCollision((Capsule) this, (OBB) other);
                    case SPHERE -> CollisionUtil.isCollision((Capsule) this, (Sphere) other);
                    case CAPSULE -> CollisionUtil.isCollision((Capsule) this, (Capsule) other);
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
