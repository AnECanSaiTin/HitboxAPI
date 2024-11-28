package cn.anecansaitin.hitboxapi.api.common.attachment;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHitCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.battle.IIncremental;
import cn.anecansaitin.hitboxapi.common.collider.local.EntityCoordinateConverter;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;

/// 这仅仅是一个简单的示例
public interface IEntityColliderHolder extends IIncremental<CompoundTag> {
    Map<String, IHurtCollider> getHurtBox();

    Map<String, IHitCollider> getHitBox();

    void addHurtBox(String name, IHurtCollider collider);

    void addHitBox(String name, IHitCollider collider);

    void removeHurtBox(String name);

    void removeHitBox(String name);

    EntityCoordinateConverter getCoordinateConverter();
}
