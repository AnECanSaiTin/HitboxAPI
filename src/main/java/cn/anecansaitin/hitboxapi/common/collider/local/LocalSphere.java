package cn.anecansaitin.hitboxapi.common.collider.local;

import cn.anecansaitin.hitboxapi.api.common.collider.local.ILocalSphere;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LocalSphere<T, D> implements ILocalSphere<T, D> {
    private float radius;
    private final Vector3f localCenter;
    private final Vector3f globalCenter = new Vector3f();
    private final ICoordinateConverter parent;
    private final short[] version = new short[2];
    private boolean dirty = true;

    private boolean disable;

    public LocalSphere(Vector3f localCenter, float radius, ICoordinateConverter parent) {
        this.localCenter = localCenter;
        this.radius = radius;
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
        dirty = true;
        this.localCenter.set(center);
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
        dirty = true;
        version[0] = parent.positionVersion();
        version[1] = parent.rotationVersion();
        Vector3f position = parent.getPosition();
        Quaternionf rotation = parent.getRotation().conjugate(new Quaternionf());

        localCenter.set(center).sub(position).rotate(rotation);
        globalCenter.set(center);
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
        dirty = true;
    }

    private void update() {
        if (parent.positionVersion() == version[0] && parent.rotationVersion() == version[1] && !dirty) {
            return;
        }

        dirty = false;
        version[0] = parent.positionVersion();
        version[1] = parent.rotationVersion();
        Vector3f position = parent.getPosition();
        Quaternionf rotation = parent.getRotation();
        rotation.transform(this.localCenter, globalCenter).add(position);
    }
}
