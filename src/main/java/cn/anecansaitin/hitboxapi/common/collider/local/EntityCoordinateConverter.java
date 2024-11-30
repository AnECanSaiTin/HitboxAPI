package cn.anecansaitin.hitboxapi.common.collider.local;

import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/// Entity坐标转换器
public class EntityCoordinateConverter implements ICoordinateConverter {
    private final Entity entity;
    private final short[] version = new short[2];
    private final Vector3f position = new Vector3f();
    private float yRot;
    private final Quaternionf rotation = new Quaternionf();

    public EntityCoordinateConverter(Entity entity) {
        this.entity = entity;
    }

    @Override
    public short positionVersion() {
        return version[0];
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    @Override
    public short rotationVersion() {
        return version[1];
    }

    @Override
    public Quaternionf getRotation() {
        return rotation;
    }

    public void update() {
        Vec3 pos = entity.position();

        if (!position.equals((float) pos.x, (float) pos.y, (float) pos.z)) {
            position.set(pos.x, pos.y, pos.z);
            version[0]++;
        }

//        float rot = entity.getPreciseBodyRotation(0);
//        float rot = entity.getYRot();
//
//        if (yRot != -rot) {
//            yRot = -rot;
//            rotation.rotationY(yRot * Mth.DEG_TO_RAD);
//            version[1]++;
//        }
    }
}
