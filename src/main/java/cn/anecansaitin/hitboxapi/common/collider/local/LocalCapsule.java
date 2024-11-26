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
    private final short[] version = new short[2];
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

    private void update() {
        if (parent.positionVersion() == version[0] && parent.rotationVersion() == version[1]) {
            return;
        }

        Vector3f position = parent.getPosition();
        Quaternionf rotation = parent.getRotation();
        rotation.transform(localCenter, globalCenter).add(position);
        rotation.mul(localRotation, globalRotation);
        version[0] = parent.positionVersion();
        version[1] = parent.rotationVersion();
    }
}
