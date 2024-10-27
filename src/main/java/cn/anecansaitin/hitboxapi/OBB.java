package cn.anecansaitin.hitboxapi;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.entity.PartEntity;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * OBB碰撞箱<br>
 * 一种可以旋转的碰撞箱<br>
 */
public final class OBB {
    public static final OBB EMPTY = new OBB(new Vector3f(), new Vector3f(), new Quaternionf());
    private static final Vector3f[] aabbV = {new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), new Vector3f(0, 0, 1)};
    private static final Quaternionf aabbQ = new Quaternionf();
    private final Vector3f center;
    private final Quaternionf rotationQ;
    private final Vector3f[] rotationV;
    private final Vector3f halfExtents;
    private boolean rotationDirt;

    public OBB(Vector3f center, Vector3f halfExtents, Quaternionf rotation) {//todo 缺少obb碰撞箱的可视化
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

    private boolean getSeparatingPlane(Vector3f RPos, Vector3f Plane, OBB box1, OBB box2) {
        return (Math.abs(RPos.dot(Plane)) >
                (Math.abs((box1.rotationV[0].mul(box1.halfExtents.x, new Vector3f())).dot(Plane)) +
                        Math.abs((box1.rotationV[1].mul(box1.halfExtents.y, new Vector3f())).dot(Plane)) +
                        Math.abs((box1.rotationV[2].mul(box1.halfExtents.z, new Vector3f())).dot(Plane)) +
                        Math.abs((box2.rotationV[0].mul(box2.halfExtents.x, new Vector3f())).dot(Plane)) +
                        Math.abs((box2.rotationV[1].mul(box2.halfExtents.y, new Vector3f())).dot(Plane)) +
                        Math.abs((box2.rotationV[2].mul(box2.halfExtents.z, new Vector3f())).dot(Plane))));
    }

    public boolean isCollision(OBB obb) {
        if (rotationDirt) {
            updateRotationV();
        }

        if (obb.rotationDirt) {
            obb.updateRotationV();
        }

        Vector3f RPos = obb.center.sub(this.center, new Vector3f());

        return !(getSeparatingPlane(RPos, this.rotationV[0], this, obb) ||
                getSeparatingPlane(RPos, this.rotationV[1], this, obb) ||
                getSeparatingPlane(RPos, this.rotationV[2], this, obb) ||
                getSeparatingPlane(RPos, obb.rotationV[0], this, obb) ||
                getSeparatingPlane(RPos, obb.rotationV[1], this, obb) ||
                getSeparatingPlane(RPos, obb.rotationV[2], this, obb) ||
                getSeparatingPlane(RPos, this.rotationV[0].cross(obb.rotationV[0], new Vector3f()), this, obb) ||
                getSeparatingPlane(RPos, this.rotationV[0].cross(obb.rotationV[1], new Vector3f()), this, obb) ||
                getSeparatingPlane(RPos, this.rotationV[0].cross(obb.rotationV[2], new Vector3f()), this, obb) ||
                getSeparatingPlane(RPos, this.rotationV[1].cross(obb.rotationV[0], new Vector3f()), this, obb) ||
                getSeparatingPlane(RPos, this.rotationV[1].cross(obb.rotationV[1], new Vector3f()), this, obb) ||
                getSeparatingPlane(RPos, this.rotationV[1].cross(obb.rotationV[2], new Vector3f()), this, obb) ||
                getSeparatingPlane(RPos, this.rotationV[2].cross(obb.rotationV[0], new Vector3f()), this, obb) ||
                getSeparatingPlane(RPos, this.rotationV[2].cross(obb.rotationV[1], new Vector3f()), this, obb) ||
                getSeparatingPlane(RPos, this.rotationV[2].cross(obb.rotationV[2], new Vector3f()), this, obb));
    }

    public boolean isCollision(Entity entity) {
        if (rotationDirt) {
            updateRotationV();
        }

        if (entity.isMultipartEntity()) {
            PartEntity<?>[] parts = entity.getParts();

            for (PartEntity<?> part : parts) {
                if (new OBB(part.getBoundingBox()).isCollision(this)) {
                    return true;
                }
            }
        } else {
            return new OBB(entity.getBoundingBox()).isCollision(this);
        }

        return false;
    }

    /**
     * 在修改过旋转之后必须调用一次该方法
     */
    public void setRotationDirt() {
        rotationDirt = true;
    }

    private void updateRotationV() {
        Vector3f localX = new Vector3f(1, 0, 0);
        Vector3f localY = new Vector3f(0, 1, 0);
        Vector3f localZ = new Vector3f(0, 0, 1);
        rotationQ.transform(localX);
        rotationQ.transform(localY);
        rotationQ.transform(localZ);
        this.rotationV[0] = localX;
        this.rotationV[1] = localY;
        this.rotationV[2] = localZ;
    }

    public void move(float distance) {
        Vector3f forwardDirection = new Vector3f(0, 0, 1);
        forwardDirection.rotate(rotationQ, forwardDirection);

        Vector3f newPosition = new Vector3f(center);
        newPosition.add(new Vector3f(forwardDirection).mul(distance));

        center.set(newPosition);
    }

    public Vector3f center() {
        return center;
    }

    public Quaternionf rotation() {
        return rotationQ;
    }

    public Vector3f halfExtents() {
        return halfExtents;
    }
}

