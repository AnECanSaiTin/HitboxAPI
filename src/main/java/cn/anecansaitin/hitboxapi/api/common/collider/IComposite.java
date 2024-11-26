package cn.anecansaitin.hitboxapi.api.common.collider;

/// 复合碰撞箱
///
/// 组合多个碰撞箱为一个整体，可嵌套
public interface IComposite<C extends ICollider<T, D>, T, D> extends ICollider<T, D> {
    /// 碰撞箱数量
    int getCollidersCount();

    /// 获取碰撞箱
    C getCollider(int index);

    /// 设置指定索引碰撞箱
    void setCollider(int index, C collider);

    /// 添加碰撞箱
    void addCollider(C collider);

    /// 移除指定索引碰撞箱
    void removeCollider(int index);

    @Override
    default ColliderTyep getType() {
        return ColliderTyep.COMPOSITE;
    }
}
