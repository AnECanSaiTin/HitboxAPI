package cn.anecansaitin.hitboxapi.common.colliders;

import cn.anecansaitin.hitboxapi.client.colliders.render.ICollisionRender;
import cn.anecansaitin.hitboxapi.client.colliders.render.OBBRender;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class OBB implements ICollider {
    public final Vector3f[] vertices = new Vector3f[8];
    public final Vector3f[] axes = new Vector3f[3];
    public final Vector3f center;
    public final Vector3f halfExtents;
    public final Quaternionf rotation;
    public final Vector3f globalCenter;

    public boolean disable;
    private boolean isDirty;

    public OBB() {
        this(new Vector3f(), new Vector3f(1, 1, 1), new Quaternionf());
    }

    public OBB(Vector3f center, Vector3f halfExtents, Quaternionf rotation) {
        axes[0] = new Vector3f(1, 0, 0);
        axes[1] = new Vector3f(0, 1, 0);
        axes[2] = new Vector3f(0, 0, 1);

        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vector3f();
        }

        this.center = center;
        this.globalCenter = new Vector3f(center);
        this.halfExtents = halfExtents;
        this.rotation = rotation;
        isDirty = true;
    }

    public OBB(AABB aabb) {
        this(aabb.getCenter().toVector3f(), new Vector3f((float) aabb.getXsize(), (float) aabb.getYsize(), (float) aabb.getZsize()).mul(0.5f), new Quaternionf());
    }

    private void update(BoxPoseStack poseStack) {
        BoxPoseStack.Pose pose = poseStack.last();
        Vector3f posOffset = pose.position;
        Quaternionf rotOffset = pose.rotation;
        Vector3f center = rotOffset.transform(this.center, globalCenter).add(posOffset);
        Quaternionf rotation = rotOffset.mul(this.rotation, new Quaternionf());

        // 旋转轴向
        axes[0].set(1, 0, 0);
        axes[1].set(0, 1, 0);
        axes[2].set(0, 0, 1);


        for (Vector3f axe : axes) {
            rotation.transform(axe);
        }

        Vector3f v = new Vector3f();

        // 计算顶点
        vertices[0].set(center).add(axes[0].mul(halfExtents.x, v)).add(axes[1].mul(halfExtents.y, v)).add(axes[2].mul(halfExtents.z, v));
        vertices[1].set(center).add(axes[0].mul(halfExtents.x, v)).add(axes[1].mul(halfExtents.y, v)).sub(axes[2].mul(halfExtents.z, v));
        vertices[2].set(center).add(axes[0].mul(halfExtents.x, v)).sub(axes[1].mul(halfExtents.y, v)).add(axes[2].mul(halfExtents.z, v));
        vertices[3].set(center).add(axes[0].mul(halfExtents.x, v)).sub(axes[1].mul(halfExtents.y, v)).sub(axes[2].mul(halfExtents.z, v));
        vertices[4].set(center).sub(axes[0].mul(halfExtents.x, v)).add(axes[1].mul(halfExtents.y, v)).add(axes[2].mul(halfExtents.z, v));
        vertices[5].set(center).sub(axes[0].mul(halfExtents.x, v)).add(axes[1].mul(halfExtents.y, v)).sub(axes[2].mul(halfExtents.z, v));
        vertices[6].set(center).sub(axes[0].mul(halfExtents.x, v)).sub(axes[1].mul(halfExtents.y, v)).add(axes[2].mul(halfExtents.z, v));
        vertices[7].set(center).sub(axes[0].mul(halfExtents.x, v)).sub(axes[1].mul(halfExtents.y, v)).sub(axes[2].mul(halfExtents.z, v));
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
    public Collision getType() {
        return Collision.OBB;
    }

    @Override
    public ICollisionRender getRenderer() {
        return OBBRender.INSTANCE;
    }

    @Override
    public boolean disable() {
        return disable;
    }
}
