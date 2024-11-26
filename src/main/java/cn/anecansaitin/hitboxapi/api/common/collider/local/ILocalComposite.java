package cn.anecansaitin.hitboxapi.api.common.collider.local;

import cn.anecansaitin.hitboxapi.api.common.collider.IComposite;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface ILocalComposite<C extends ILocalCollider<T, D>, T, D> extends IComposite<C, T, D> {
    /// @return  局部坐标系坐标
    Vector3f getLocalPosition();

    /// 设置局部坐标系坐标
    void setLocalPosition(Vector3f position);

    /// @return 局部坐标系旋转
    Quaternionf getLocalRotation();

    /// 设置局部坐标系旋转
    void setLocalRotation(Quaternionf rotation);

    /// @return 坐标
    Vector3f getPosition();

    /// @return 旋转
    Quaternionf getRotation();

    /// @return 坐标转换器
    ICoordinateConverter getConverter();
}
