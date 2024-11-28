package cn.anecansaitin.hitboxapi.api.common.collider.battle;

import net.minecraft.nbt.Tag;

public interface IIncremental<T extends Tag> {
    /// 是否需要更新
    boolean shouldUpdate();

    /// 更新数据至NBT
    T getUpdate();

    /// 更新数据
    void update(T tag);
}
