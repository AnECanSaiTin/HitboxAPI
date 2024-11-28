package cn.anecansaitin.hitboxapi.common.collider.local;

import cn.anecansaitin.hitboxapi.api.common.collider.local.ILocalCapsule;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LocalCapsule<T, D> implements ILocalCapsule<T, D> {
    private float height;
    private float radius;
    private final Vector3f localCenter;
    private final Quaternionf localRotation;
    private final Vector3f globalCenter = new Vector3f();
    private final Quaternionf globalRotation = new Quaternionf();
    private final Vector3f globalDirection = new Vector3f();
    private final ICoordinateConverter parent;
    /// 0 - 中心点, 1 - 旋转
    private final short[] version = new short[2];
    /// 0 - 中心点, 1 - 旋转
    private final boolean[] dirty = new boolean[]{true, true};
    private boolean disable;

    public LocalCapsule(float height, float radius, Vector3f localCenter, Quaternionf localRotation, ICoordinateConverter parent) {
        this.height = height;
        this.radius = radius;
        this.localCenter = localCenter;
        this.localRotation = localRotation;
        this.parent = parent;
        version[0] = (short) (parent.positionVersion() - 1);
        version[1] = (short) (parent.rotationVersion() - 1);
    }

    @Override
    public Vector3f getLocalCenter() {
        return localCenter;
    }

    @Override
    public void setLocalCenter(Vector3f center) {
        dirty[0] = true;
        this.localCenter.set(center);
    }

    @Override
    public Quaternionf getLocalRotation() {
        return localRotation;
    }

    @Override
    public void setLocalRotation(Quaternionf rotation) {
        dirty[1] = true;
        this.localRotation.set(rotation);
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public Vector3f getCenter() {
        update();
        return globalCenter;
    }

    @Override
    public void setCenter(Vector3f center) {
        dirty[0] = true;
        version[0] = parent.positionVersion();
        version[1] = parent.rotationVersion();
        Vector3f position = parent.getPosition();
        Quaternionf rotation = parent.getRotation().conjugate(new Quaternionf());

        localCenter.set(center).sub(position).rotate(rotation);
        globalCenter.set(center);
    }

    @Override
    public Quaternionf getRotation() {
        update();
        return globalRotation;
    }

    @Override
    public void setRotation(Quaternionf rotation) {
        dirty[1] = true;
        version[1] = parent.rotationVersion();
        localRotation.set(rotation).mul(parent.getRotation().conjugate(new Quaternionf()));
        globalRotation.set(rotation);
    }

    @Override
    public Vector3f getDirection() {
        update();
        return globalDirection;
    }

    @Override
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Override
    public boolean disable() {
        return disable;
    }

    protected void setCenterDirty() {
        dirty[0] = true;
    }

    protected void setRotationDirty() {
        dirty[1] = true;
    }

    private void update() {
        if (parent.rotationVersion() != version[1] || dirty[1]) {
            dirty[1] = false;
            Quaternionf rotation = parent.getRotation();
            rotation.mul(localRotation, globalRotation);
            globalDirection.set(0, 1, 0).rotate(globalRotation);
            version[1] = parent.rotationVersion();
        }

        if (parent.positionVersion() != version[0] || parent.rotationVersion() != version[1] || dirty[0]) {
            dirty[0] = false;
            version[0] = parent.positionVersion();
            version[1] = parent.rotationVersion();
            Vector3f position = parent.getPosition();
            Quaternionf rotation = parent.getRotation();
            rotation.transform(localCenter, globalCenter).add(position);
        }
    }
}
