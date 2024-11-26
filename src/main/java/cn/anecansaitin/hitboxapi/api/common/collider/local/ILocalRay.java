package cn.anecansaitin.hitboxapi.api.common.collider.local;

import cn.anecansaitin.hitboxapi.api.common.collider.IRay;
import org.joml.Vector3f;

public interface ILocalRay<T, D> extends IRay<T, D>, ILocalCollider<T, D> {
    /// @return 局部坐标系起点
    Vector3f getLocalOrigin();

    /// 设置局部坐标系起点
    void setLocalOrigin(Vector3f localOrigin);

    /// @return 局部坐标系方向
    Vector3f getLocalDirection();

    /// 设置局部坐标系方向
    void setLocalDirection(Vector3f localDirection);
}
