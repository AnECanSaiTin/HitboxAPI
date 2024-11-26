package cn.anecansaitin.hitboxapi.common.attachment;

import cn.anecansaitin.hitboxapi.api.common.attachment.IEntityColliderHolder;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.common.collider.local.EntityCoordinateConverter;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class EntityColliderHolder implements IEntityColliderHolder {
    /**
     * 受击判定
     */
    public Map<String, ICollider<Entity, Void>> hurtBox = new HashMap<>();

    /**
     * 攻击判定
     */
    public Map<String, ICollider<Entity, Void>> hitBox = new HashMap<>();

    public EntityCoordinateConverter converter;

    /// 仅用于注册
    @Deprecated()
    public EntityColliderHolder() {
    }

    public EntityColliderHolder(Entity entity) {
        converter = new EntityCoordinateConverter(entity);
    }

    @Override
    public Map<String, ICollider<Entity, Void>> getHurtBox() {
        return hurtBox;
    }

    @Override
    public Map<String, ICollider<Entity, Void>> getHitBox() {
        return hitBox;
    }

    @Override
    public EntityCoordinateConverter getCoordinateConverter() {
        return converter;
    }
}
