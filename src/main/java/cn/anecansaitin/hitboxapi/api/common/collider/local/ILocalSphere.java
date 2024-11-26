package cn.anecansaitin.hitboxapi.api.common.collider.local;

import cn.anecansaitin.hitboxapi.api.common.collider.ISphere;
import org.joml.Vector3f;

public interface ILocalSphere<T, D> extends ISphere<T, D>, ILocalCollider<T, D> {
    /// @return 局部坐标系中心
    Vector3f getLocalCenter();

    /// 设置局部坐标系中心
    void setLocalCenter(Vector3f center);
}
