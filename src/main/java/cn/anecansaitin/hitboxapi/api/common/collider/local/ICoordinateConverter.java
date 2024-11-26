package cn.anecansaitin.hitboxapi.api.common.collider.local;

import org.joml.Quaternionf;
import org.joml.Vector3f;

/// 名字要重新命名
public interface ICoordinateConverter {
    /// 是否需要更新位置
    short positionVersion();

    /// 位置
    Vector3f getPosition();

    /// 是否需要更新旋转
    short rotationVersion();

    /// 旋转
    Quaternionf getRotation();
}
