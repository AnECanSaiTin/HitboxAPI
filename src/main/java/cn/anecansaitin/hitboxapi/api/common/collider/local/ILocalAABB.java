package cn.anecansaitin.hitboxapi.api.common.collider.local;

import cn.anecansaitin.hitboxapi.api.common.collider.IAABB;
import org.joml.Vector3f;

/// 局部坐标系包围盒
public interface ILocalAABB<T, D> extends IAABB<T, D>, ILocalCollider<T, D> {
    /// @return 局部坐标系中心点
    Vector3f getLocalCenter();

    /// 设置局部坐标系中心点
    void setLocalCenter(Vector3f center);

    /// @return 局部坐标系最小点
    Vector3f getLocalMin();

    /// @return 局部坐标系最大点
    Vector3f getLocalMax();

    /// 依据世界坐标设置局部坐标系中心点
    @Override
    void setCenter(Vector3f center);
}
