package cn.anecansaitin.hitboxapi.common.collider.local;

import cn.anecansaitin.hitboxapi.api.common.collider.local.ILocalOBB;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LocalOBB<T, D> implements ILocalOBB<T, D> {
    private final Vector3f halfExtents;
    private final Vector3f localCenter;
    private final Quaternionf localRotation;
    private final Vector3f globalCenter = new Vector3f();
    private final Quaternionf globalRotation = new Quaternionf();
    private final Vector3f[] vertices = new Vector3f[8];
    private final Vector3f[] axes = new Vector3f[3];
    private final ICoordinateConverter parent;
    private final short[] version = new short[2];
    private boolean disable;

    public LocalOBB(Vector3f halfExtents, Vector3f localCenter, Quaternionf localRotation, ICoordinateConverter parent) {
        this.halfExtents = halfExtents;
        this.localCenter = localCenter;
        this.localRotation = localRotation;
        this.parent = parent;
        version[0] = (short) (parent.positionVersion() - 1);
        version[1] = (short) (parent.rotationVersion() - 1);

        for (int i = 0, globalVerticesLength = vertices.length; i < globalVerticesLength; i++) {
            vertices[i] = new Vector3f();
        }

        for (int i = 0, globalAxesLength = axes.length; i < globalAxesLength; i++) {
            axes[i] = new Vector3f();
        }
    }

    @Override
    public Vector3f getLocalCenter() {
        return localCenter;
    }

    @Override
    public void setLocalCenter(Vector3f center) {
        this.localCenter.set(center);
    }

    @Override
    public Quaternionf getLocalRotation() {
        return localRotation;
    }

    @Override
    public void setLocalRotation(Quaternionf rotation) {
        this.localRotation.set(rotation);
    }

    @Override
    public Vector3f getHalfExtents() {
        return halfExtents;
    }

    @Override
    public void setHalfExtents(Vector3f halfExtents) {
        this.halfExtents.set(halfExtents);
    }

    @Override
    public Vector3f getCenter() {
        update();
        return globalCenter;
    }

    @Override
    public void setCenter(Vector3f center) {
        Vector3f position = parent.getPosition();
        Quaternionf rotation = parent.getRotation().conjugate(new Quaternionf());

        localCenter.set(center).sub(position).rotate(rotation);
        globalCenter.set(center);
        version[0] = parent.positionVersion();
        version[1] = parent.rotationVersion();
    }

    @Override
    public Quaternionf getRotation() {
        update();
        return globalRotation;
    }

    @Override
    public void setRotation(Quaternionf rotation) {
        localRotation.set(rotation).mul(parent.getRotation().conjugate(new Quaternionf()));
        globalRotation.set(rotation);
        version[1] = parent.rotationVersion();
    }

    @Override
    public Vector3f[] getVertices() {
        return vertices;
    }

    @Override
    public Vector3f[] getAxes() {
        return axes;
    }

    @Override
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Override
    public boolean disable() {
        return disable;
    }

    private void update() {
        if (version[0] != parent.positionVersion() && version[1] == parent.rotationVersion()) {
            //仅进行位置移动
            Vector3f position = parent.getPosition();
            Quaternionf rotation = parent.getRotation();
            Vector3f center = rotation.transform(this.localCenter, new Vector3f()).add(position);
            Vector3f offset = center.sub(globalCenter, new Vector3f());
            globalCenter.set(center);

            for (Vector3f vertex : vertices) {
                vertex.add(offset);
            }

            version[0] = parent.positionVersion();
            return;
        } else if (version[0] == parent.positionVersion() && version[1] == parent.rotationVersion()) {
            return;
        }

        Vector3f position = parent.getPosition();
        Quaternionf rotation = parent.getRotation();
        Vector3f center = rotation.transform(this.localCenter, globalCenter).add(position);
        rotation.mul(this.localRotation, globalRotation);

        // 旋转轴向
        axes[0].set(1, 0, 0);
        axes[1].set(0, 1, 0);
        axes[2].set(0, 0, 1);


        for (Vector3f axe : axes) {
            globalRotation.transform(axe);
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

        version[0] = parent.positionVersion();
        version[1] = parent.rotationVersion();
    }
}
