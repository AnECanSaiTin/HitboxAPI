package cn.anecansaitin.hitboxapi.common.collider;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class OBB<T, D> implements IOBB<T, D> {
    public final Vector3f halfExtents;
    public final Vector3f localCenter;
    public final Quaternionf localRotation;
    public final Vector3f[] globalVertices = new Vector3f[8];
    public final Vector3f[] globalAxes = new Vector3f[3];
    public final Vector3f globalCenter;

    public boolean disable;
    private boolean isDirty;

    public OBB() {
        this(new Vector3f(), new Vector3f(1, 1, 1), new Quaternionf());
    }

    public OBB(Vector3f localCenter, Vector3f halfExtents, Quaternionf localRotation) {
        globalAxes[0] = new Vector3f(1, 0, 0);
        globalAxes[1] = new Vector3f(0, 1, 0);
        globalAxes[2] = new Vector3f(0, 0, 1);

        for (int i = 0; i < globalVertices.length; i++) {
            globalVertices[i] = new Vector3f();
        }

        this.localCenter = localCenter;
        this.globalCenter = new Vector3f(localCenter);
        this.halfExtents = halfExtents;
        this.localRotation = localRotation;
        isDirty = true;
    }

    public OBB(IAABB<T, D> aabb) {
        this(new Vector3f(aabb.getGlobalCenter()), new Vector3f(aabb.getHalfExtents()), new Quaternionf());
    }

    private void update(BoxPoseStack poseStack) {
        BoxPoseStack.Pose pose = poseStack.last();
        Vector3f posOffset = pose.position;
        Quaternionf rotOffset = pose.rotation;
        Vector3f center = rotOffset.transform(this.localCenter, globalCenter).add(posOffset);
        Quaternionf rotation = rotOffset.mul(this.localRotation, new Quaternionf());

        // 旋转轴向
        globalAxes[0].set(1, 0, 0);
        globalAxes[1].set(0, 1, 0);
        globalAxes[2].set(0, 0, 1);


        for (Vector3f axe : globalAxes) {
            rotation.transform(axe);
        }

        Vector3f v = new Vector3f();

        // 计算顶点
        globalVertices[0].set(center).add(globalAxes[0].mul(halfExtents.x, v)).add(globalAxes[1].mul(halfExtents.y, v)).add(globalAxes[2].mul(halfExtents.z, v));
        globalVertices[1].set(center).add(globalAxes[0].mul(halfExtents.x, v)).add(globalAxes[1].mul(halfExtents.y, v)).sub(globalAxes[2].mul(halfExtents.z, v));
        globalVertices[2].set(center).add(globalAxes[0].mul(halfExtents.x, v)).sub(globalAxes[1].mul(halfExtents.y, v)).add(globalAxes[2].mul(halfExtents.z, v));
        globalVertices[3].set(center).add(globalAxes[0].mul(halfExtents.x, v)).sub(globalAxes[1].mul(halfExtents.y, v)).sub(globalAxes[2].mul(halfExtents.z, v));
        globalVertices[4].set(center).sub(globalAxes[0].mul(halfExtents.x, v)).add(globalAxes[1].mul(halfExtents.y, v)).add(globalAxes[2].mul(halfExtents.z, v));
        globalVertices[5].set(center).sub(globalAxes[0].mul(halfExtents.x, v)).add(globalAxes[1].mul(halfExtents.y, v)).sub(globalAxes[2].mul(halfExtents.z, v));
        globalVertices[6].set(center).sub(globalAxes[0].mul(halfExtents.x, v)).sub(globalAxes[1].mul(halfExtents.y, v)).add(globalAxes[2].mul(halfExtents.z, v));
        globalVertices[7].set(center).sub(globalAxes[0].mul(halfExtents.x, v)).sub(globalAxes[1].mul(halfExtents.y, v)).sub(globalAxes[2].mul(halfExtents.z, v));
    }

    public void markUpdate() {
        isDirty = true;
    }

    @Override
    public void prepareColliding(BoxPoseStack poseStack) {
        if (isDirty || poseStack.isDirty()) {
            update(poseStack);
            isDirty = false;
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
        return localCenter;
    }

    @Override
    public Quaternionf getLocalRotation() {
        return localRotation;
    }

    @Override
    public Vector3f[] getGlobalVertices() {
        return globalVertices;
    }

    @Override
    public Vector3f[] getGlobalAxes() {
        return globalAxes;
    }

    @Override
    public Vector3f getGlobalCenter() {
        return globalCenter;
    }
}
