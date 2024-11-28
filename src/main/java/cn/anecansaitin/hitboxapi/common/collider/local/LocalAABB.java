package cn.anecansaitin.hitboxapi.common.collider.local;

import cn.anecansaitin.hitboxapi.api.common.collider.local.ILocalAABB;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LocalAABB<T, D> implements ILocalAABB<T, D> {
    private final Vector3f localCenter;
    private final Vector3f halfExtents;
    private final Vector3f globalCenter;
    private final ICoordinateConverter parent;
    /// 0 - 中心点, 1 - 旋转
    private final short[] version = new short[2];
    /// 中心点
    private boolean dirty = true;
    private boolean disable;

    public LocalAABB(Vector3f localCenter, Vector3f halfExtents, ICoordinateConverter parent) {
        this.localCenter = localCenter;
        this.halfExtents = halfExtents;
        this.globalCenter = new Vector3f();
        this.parent = parent;
        version[0] = (short) (parent.positionVersion() -1);
        version[1] = (short) (parent.rotationVersion() - 1);
    }

    @Override
    public Vector3f getLocalCenter() {
        return localCenter;
    }

    @Override
    public void setLocalCenter(Vector3f center) {
        dirty = true;
        localCenter.set(center);
    }

    @Override
    public Vector3f getLocalMin() {
        return localCenter.sub(halfExtents, new Vector3f());
    }

    @Override
    public Vector3f getLocalMax() {
        return localCenter.add(halfExtents, new Vector3f());
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
        dirty = true;
        version[0] = parent.positionVersion();
        version[1] = parent.rotationVersion();
        Vector3f position = parent.getPosition();
        Quaternionf rotation = parent.getRotation().conjugate(new Quaternionf());

        localCenter.set(center).sub(position).rotate(rotation);
        globalCenter.set(center);
    }

    @Override
    public Vector3f getMin() {
        return getCenter().sub(halfExtents, new Vector3f());
    }

    @Override
    public Vector3f getMax() {
        return getCenter().add(halfExtents, new Vector3f());
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

        version[0] = parent.positionVersion();
        version[1] = parent.rotationVersion();
        dirty = false;
        Vector3f position = parent.getPosition();
        Quaternionf rotation = parent.getRotation();
        rotation.transform(localCenter, globalCenter).add(position);
    }
}
