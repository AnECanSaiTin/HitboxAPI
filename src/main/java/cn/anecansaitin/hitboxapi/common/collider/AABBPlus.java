package cn.anecansaitin.hitboxapi.common.collider;

import cn.anecansaitin.hitboxapi.api.common.collider.IAABB;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AABBPlus<T, D> implements IAABB<T, D> {
    private final Vector3f halfExtents;
    private final Vector3f center;
    private final Vector3f globalCenter = new Vector3f();
    private final Vector3f globalMin = new Vector3f();
    private final Vector3f globalMax = new Vector3f();
    public boolean isDirty;
    private boolean disable;

    public AABBPlus(Vector3f center, Vector3f halfExtents) {
        this.center = center;
        this.halfExtents = halfExtents;
        center.sub(halfExtents, globalMin);
        center.add(halfExtents, globalMax);
        globalCenter.set(center);
        isDirty = true;
    }

    public AABBPlus(AABB aabb) {
        this(aabb.getCenter().toVector3f(), new Vector3f((float) (aabb.getXsize() / 2), (float) (aabb.getYsize() / 2), (float) (aabb.getZsize() / 2)));
    }

    @Override
    public void prepareColliding(BoxPoseStack poseStack) {
        if (isDirty || poseStack.isDirty()) {
            BoxPoseStack.Pose pose = poseStack.last();
            Vector3f posOffset = pose.position;
            Quaternionf rotOffset = pose.rotation;
            rotOffset.transform(this.center, globalCenter).add(posOffset);
            globalCenter.sub(halfExtents, globalMin);
            globalCenter.add(halfExtents, globalMax);
        }
    }

    @Override
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Override
    public boolean disable() {
        return disable;
    }

    @Override
    public Vector3f getHalfExtents() {
        return halfExtents;
    }

    @Override
    public Vector3f getLocalCenter() {
        return center;
    }

    @Override
    public Vector3f getGlobalCenter() {
        return globalCenter;
    }

    @Override
    public Vector3f getGlobalMin() {
        return globalMin;
    }

    @Override
    public Vector3f getGlobalMax() {
        return globalMax;
    }

    public void markDirty() {
        isDirty = true;
    }
}
