package cn.anecansaitin.hitboxapi.api.common.collider;

import org.joml.Vector3f;

/// 球体碰撞箱
public interface ISphere<T, D> extends ICollider<T, D> {
    /// 半径
    float getRadius();

    /// 设置半径
    void setRadius(float radius);

    /// 中心点
    Vector3f getCenter();

    /// 设置中心点
    void setCenter(Vector3f center);

    @Override
    default ColliderTyep getType() {
        return ColliderTyep.SPHERE;
    }
}
