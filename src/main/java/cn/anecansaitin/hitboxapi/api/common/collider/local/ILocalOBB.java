package cn.anecansaitin.hitboxapi.api.common.collider.local;

import cn.anecansaitin.hitboxapi.api.common.collider.IOBB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/// 局部坐标系OBB碰撞箱
public interface ILocalOBB<T, D> extends IOBB<T, D>, ILocalCollider<T, D> {
    /// 局部坐标系中心点
    Vector3f getLocalCenter();

    /// 设置局部坐标系中心点
    void setLocalCenter(Vector3f center);

    /// 局部坐标系旋转
    Quaternionf getLocalRotation();

    /// 设置局部坐标系旋转
    void setLocalRotation(Quaternionf rotation);
}
