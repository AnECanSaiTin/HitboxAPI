package cn.anecansaitin.hitboxapi;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Capsule implements ICollision {
    private final Vector3f center;
    private final Quaternionf rotation;
    private final Vector3f direction;
    private float height;
    private float radius;
    private boolean shouldUpdateDirection;

    public Capsule(Vector3f center, float radius, float height, Quaternionf rotation) {
        this.center = center;
        this.radius = radius;
        this.height = height;
        this.rotation = rotation;
        direction = new Vector3f(0, 0, 1);
        rotation.transform(direction);
    }

    public boolean isCollision(Capsule other) {
        //计算头尾点最值
        Vector3f pointA1 = direction.mul(height, new Vector3f()).add(center);
        Vector3f pointA2 = direction.mul(-height, new Vector3f()).add(center);

        Vector3f pointB1 = other.direction.mul(other.height, new Vector3f()).add(other.center);
        Vector3f pointB2 = other.direction.mul(-other.height, new Vector3f()).add(other.center);

        // 求两条线段的最短距离
        float distance = CollisionUtil.getClosestDistanceBetweenSegmentsSqr(pointA1, pointA2, pointB1, pointB2);

        //求两个球半径和
        float totalRadius = (float) Math.pow(radius + other.radius, 2);
        //距离小于等于半径和则碰撞
        return distance <= totalRadius;
    }

    @Override
    public boolean isColliding(ICollision other) {
        if (shouldUpdateDirection) {
            updateDirection();
            shouldUpdateDirection = false;
        }

        return switch (other.getType()) {
            case CAPSULE -> isCollision((Capsule) other);
            default -> false;
        };
    }

    @Override
    public Collision getType() {
        return Collision.CAPSULE;
    }

    public Vector3f getCenter() {
        return center;
    }

    public void setCenter(Vector3f center) {
        this.center.set(center);
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation.set(rotation);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void markRotationDirty() {
        shouldUpdateDirection = true;
    }

    private void updateDirection() {
        direction.set(0, 0, 1);
        rotation.transform(direction);
    }
}
