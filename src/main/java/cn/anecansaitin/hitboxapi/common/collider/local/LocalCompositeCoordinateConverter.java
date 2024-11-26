package cn.anecansaitin.hitboxapi.common.collider.local;

import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LocalCompositeCoordinateConverter implements ICoordinateConverter {
    private final short[] version = new short[2];
    private final LocalComposite<?, ?, ?> composite;

    public LocalCompositeCoordinateConverter(LocalComposite<?, ?, ?> composite) {
        this.composite = composite;
    }

    @Override
    public short positionVersion() {
        return version[0];
    }

    @Override
    public Vector3f getPosition() {
        return composite.getPosition();
    }

    @Override
    public short rotationVersion() {
        return version[1];
    }

    @Override
    public Quaternionf getRotation() {
        return composite.getRotation();
    }

    public void update() {
        version[0]++;
        version[1]++;
    }
}
