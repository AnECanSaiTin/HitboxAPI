package cn.anecansaitin.hitboxapi.colliders;

import cn.anecansaitin.hitboxapi.client.IColliderRender;
import net.minecraft.world.phys.AABB;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * OBB碰撞箱<br>
 * 一种可以旋转的碰撞箱<br>
 */
public final class OBB implements ICollision {
    public static final OBB EMPTY = new OBB(new Vector3f(), new Vector3f(), new Quaternionf());
    private final Vector3f center;
    private final Quaternionf rotationQ;
    private final Vector3f[] rotationV;
    private final Vector3f halfExtents;
    private boolean shouldUpdateRotationV;
    private static final Vector3f[] aabbV = {new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), new Vector3f(0, 0, 1)};
    private static final Quaternionf aabbQ = new Quaternionf();

    public OBB(Vector3f center, Vector3f halfExtents, Quaternionf rotation) {
        this.center = center;
        this.halfExtents = halfExtents;
        this.rotationV = new Vector3f[3];
        this.rotationQ = rotation;
        updateRotationV();
    }

    public OBB(AABB aabb) {
        center = aabb.getCenter().toVector3f();
        halfExtents = new Vector3f((float) (aabb.getXsize() / 2), (float) (aabb.getYsize() / 2), (float) (aabb.getZsize() / 2));
        rotationV = aabbV;
        rotationQ = aabbQ;
    }

    /**
     * 在修改过旋转之后必须调用一次该方法
     */
    public void markRotationDirty() {
        shouldUpdateRotationV = true;
    }

    private void updateRotationV() {
        Vector3f localX = new Vector3f(1, 0, 0);
        Vector3f localY = new Vector3f(0, 1, 0);
        Vector3f localZ = new Vector3f(0, 0, 1);
        rotationQ.transform(localX);
        rotationQ.transform(localY);
        rotationQ.transform(localZ);
        rotationV[0] = localX;
        rotationV[1] = localY;
        rotationV[2] = localZ;
        shouldUpdateRotationV = false;
    }

    public void move(float distance) {
        Vector3f forwardDirection = new Vector3f(0, 0, 1);
        forwardDirection.rotate(rotationQ, forwardDirection);

        Vector3f newPosition = new Vector3f(center);
        newPosition.add(new Vector3f(forwardDirection).mul(distance));

        center.set(newPosition);
    }

    public Vector3f getCenter() {
        return center;
    }

    public Quaternionf getRotation() {
        return rotationQ;
    }

    public Vector3f[] getRotationV() {
        return rotationV;
    }

    public Vector3f getHalfExtents() {
        return halfExtents;
    }

    @Override
    public void preIsColliding() {
        if (shouldUpdateRotationV) {
            updateRotationV();
        }
    }

    @Override
    public Collision getType() {
        return Collision.OBB;
    }

    @Override
    public IColliderRender getRenderer() {
        return null;
    }
}

