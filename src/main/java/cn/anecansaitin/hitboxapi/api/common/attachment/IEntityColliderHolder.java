package cn.anecansaitin.hitboxapi.api.common.attachment;

import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.common.collider.local.EntityCoordinateConverter;
import net.minecraft.world.entity.Entity;

import java.util.Map;

public interface IEntityColliderHolder {
    Map<String, ICollider<Entity, Void>> getHurtBox();

    Map<String, ICollider<Entity, Void>> getHitBox();

    EntityCoordinateConverter getCoordinateConverter();
}
