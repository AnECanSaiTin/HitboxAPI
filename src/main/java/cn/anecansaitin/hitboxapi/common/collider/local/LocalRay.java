package cn.anecansaitin.hitboxapi.common.collider.local;

import cn.anecansaitin.hitboxapi.api.common.collider.local.ILocalRay;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LocalRay<T, D> implements ILocalRay<T, D> {
    private final Vector3f localOrigin; // 射线的起点
    private final Vector3f localDirection;
    private final Vector3f globalOrigin = new Vector3f(); // 射线的起点
    private final Vector3f globalDirection = new Vector3f();
    private float length; // 射线的长度
    private final ICoordinateConverter parent;
    private final short[] version = new short[2];

    private boolean disable;

    public LocalRay(Vector3f localOrigin, Vector3f localDirection, float length, ICoordinateConverter parent) {
        this.localOrigin = localOrigin;
        this.localDirection = localDirection;
        this.length = length;
        this.parent = parent;
        version[0] = (short) (parent.positionVersion() - 1);
        version[1] = (short) (parent.rotationVersion() - 1);
    }

    @Override
    public Vector3f getLocalOrigin() {
        return localOrigin;
    }

    @Override
    public void setLocalOrigin(Vector3f localOrigin) {
        this.localOrigin.set(localOrigin);
    }

    @Override
    public Vector3f getLocalDirection() {
        return localDirection;
    }

    @Override
    public void setLocalDirection(Vector3f localDirection) {
        this.localDirection.set(localDirection);
    }

    @Override
    public float getLength() {
        return length;
    }

    @Override
    public void setLength(float length) {
        this.length = length;
    }

    @Override
    public Vector3f getOrigin() {
        update();
        return globalOrigin;
    }

    @Override
    public Vector3f getEnd() {
        update();
        return globalDirection.mul(length, new Vector3f()).add(globalOrigin);
    }

    @Override
    public Vector3f getDirection() {
        update();
        return globalDirection;
    }

    @Override
    public void setDirection(Vector3f direction) {
        Quaternionf rotation = parent.getRotation().conjugate(new Quaternionf());
        localDirection.set(direction).rotate(rotation);
        globalDirection.set(direction);
        version[1] = parent.rotationVersion();
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
        if (parent.positionVersion() != version[0] || parent.rotationVersion() != version[1]) {
            Vector3f position = parent.getPosition();
            Quaternionf rotation = parent.getRotation();
            rotation.transform(this.localOrigin, globalOrigin).add(position);
            rotation.transform(this.localDirection, globalDirection);
            version[0] = parent.positionVersion();
            version[1] = parent.rotationVersion();
        }
    }
}
