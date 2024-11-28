package cn.anecansaitin.hitboxapi.api.common.collider.local;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface ICoordinateConverter {
    /// 位置版本
    short positionVersion();

    /// 位置
    Vector3f getPosition();

    /// 旋转版本
    short rotationVersion();

    /// 旋转
    Quaternionf getRotation();
}
