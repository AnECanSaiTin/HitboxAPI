package cn.anecansaitin.hitboxapi.common.listener;

import cn.anecansaitin.hitboxapi.HitboxApi;
import cn.anecansaitin.hitboxapi.api.common.collider.ColliderUtil;
import cn.anecansaitin.hitboxapi.api.common.collider.IAABB;
import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHitCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.common.HitboxDataAttachments;
import cn.anecansaitin.hitboxapi.api.common.attachment.IEntityColliderHolder;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.common.collider.basic.AABBPlus;
import cn.anecansaitin.hitboxapi.common.network.S2CBattleColliderIncrementalSyne;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = HitboxApi.MODID)
public class LevelTick {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();

        if (level.isClientSide) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        //todo 这里可能需要多线程
        for (Entity entity : serverLevel.getAllEntities()) {
            Optional<IEntityColliderHolder> data = entity.getExistingData(HitboxDataAttachments.COLLISION);

            if (data.isEmpty()) continue;

            IEntityColliderHolder entityColliderHolder = data.get();
            collision(entity, entityColliderHolder);
            incremental(entity, entityColliderHolder);
        }
    }

    private static void collision(Entity entity, IEntityColliderHolder holder) {
        Map<String, IHitCollider> hitBox = holder.getHitBox();

        //没有攻击盒则返回
        if (hitBox.isEmpty()) return;

        Collection<IHitCollider> hitBoxValues = hitBox.values();

        //更新坐标变换栈
        holder.getCoordinateConverter().update();

        IAABB<?, ?> fastCollider = holder.getFastHitCollider();
        List<Entity> entities;

        if (fastCollider == null) {
            entities = entity.level().getEntities(entity, entity.getBoundingBox().inflate(5));
        } else {
            Vector3f min = fastCollider.getMin();
            Vector3f max = fastCollider.getMax();
            entities = entity.level().getEntities(entity, new AABB(min.x - 5, min.y - 5, min.z - 5, max.x + 5, max.y + 5, max.z + 5));
        }

        enemyFor:
        for (Entity enemy : entities) {
            Optional<IEntityColliderHolder> enemyData = enemy.getExistingData(HitboxDataAttachments.COLLISION);

            if (enemyData.isEmpty()) {
                vanillaAABB(hitBoxValues, entity, enemy);
                continue;
            }

            IEntityColliderHolder enemyColliderHolder = enemyData.get();
            Map<String, IHurtCollider> hurtBox = enemyColliderHolder.getHurtBox();

            // 没有受击盒则跳过
            if (hurtBox.isEmpty()) {
                vanillaAABB(hitBoxValues, entity, enemy);
                continue;
            }

            enemyColliderHolder.getCoordinateConverter().update();

            for (ICollider<Entity, Void> hit : hitBoxValues) {
                for (ICollider<Entity, Void> hurt : hurtBox.values()) {
                    if (!ColliderUtil.colliding(hit, entity, null, hurt, enemy, null)) {
                        continue;
                    }

                    continue enemyFor;
                }

                vanillaAABB(hitBoxValues, entity, enemy);
            }
        }
    }

    private static void incremental(Entity entity, IEntityColliderHolder holder) {
        if (!holder.shouldUpdate()) {
            return;
        }

        // 发送增量更新
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new S2CBattleColliderIncrementalSyne(entity.getId(), holder));
    }

    private static void vanillaAABB(Collection<IHitCollider> hitboxCollection, Entity hitter, Entity enemy) {
        AABB box = enemy.getBoundingBox();
        Vector3f center = box.getCenter().toVector3f();
        Vector3f half = new Vector3f((float) box.maxX, (float) box.maxY, (float) box.maxZ).sub(center);
        AABBPlus<Entity, Void> aabbPlus = new AABBPlus<>(half, center);

        for (IHitCollider hit : hitboxCollection) {
            ColliderUtil.colliding(hit, hitter, null, aabbPlus, enemy, null);
        }
    }
}
