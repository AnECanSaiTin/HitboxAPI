package cn.anecansaitin.hitboxapi.api.common.collider;

import org.joml.Vector3f;

/// 射线碰撞箱
public interface IRay<T, D> extends ICollider<T, D> {
    /// 长度
    float getLength();

    /// 设置长度
    void setLength(float length);

    /// 起点
    Vector3f getOrigin();

    /// 终点
    Vector3f getEnd();

    /// 方向
    Vector3f getDirection();

    /// 设置方向
    void setDirection(Vector3f direction);

    @Override
    default ColliderTyep getType() {
        return ColliderTyep.RAY;
    }
}
