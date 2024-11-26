package cn.anecansaitin.hitboxapi.api.common.collider;

import org.joml.Quaternionf;
import org.joml.Vector3f;

/// 胶囊体碰撞箱
///
/// 性能优于OBB碰撞箱
public interface ICapsule<T, D> extends ICollider<T, D> {
    /// @return 高度
    float getHeight();

    /// 设置高度
    void setHeight(float height);

    /// @return 半径
    float getRadius();

    /// 设置半径
    void setRadius(float radius);

    /// @return 中心点
    Vector3f getCenter();

    /// 设置中心点
    void setCenter(Vector3f center);

    /// @return 旋转
    Quaternionf getRotation();

    /// 设置旋转
    void setRotation(Quaternionf rotation);

    /// @return 方向向量
    Vector3f getDirection();

    @Override
    default ColliderTyep getType() {
        return ColliderTyep.CAPSULE;
    }
}
