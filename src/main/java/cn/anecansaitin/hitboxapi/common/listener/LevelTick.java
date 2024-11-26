package cn.anecansaitin.hitboxapi.common.listener;

import cn.anecansaitin.hitboxapi.HitboxApi;
import cn.anecansaitin.hitboxapi.api.common.collider.ColliderUtil;
import cn.anecansaitin.hitboxapi.common.HitboxDataAttachments;
import cn.anecansaitin.hitboxapi.api.common.attachment.IEntityColliderHolder;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

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
            collision(entity);
        }
    }

    private static void collision(Entity entity) {
        Optional<IEntityColliderHolder> data = entity.getExistingData(HitboxDataAttachments.COLLISION);

        if (data.isEmpty()) return;

        IEntityColliderHolder entityColliderHolder = data.get();
        Map<String, ICollider<Entity, Void>> hitBox = entityColliderHolder.getHitBox();

        //没有攻击盒则返回
        if (hitBox.isEmpty()) return;

        Collection<ICollider<Entity, Void>> hitBoxValues = hitBox.values();

        //更新坐标变换栈
        entityColliderHolder.getCoordinateConverter().update();

        //todo 如何判断检测的范围
        List<Entity> entities = entity.level().getEntities(entity, entity.getBoundingBox().inflate(5));

        enemyFor:
        for (Entity enemy : entities) {
            Optional<IEntityColliderHolder> enemyData = enemy.getExistingData(HitboxDataAttachments.COLLISION);

            if (enemyData.isEmpty()) {
                continue;
            }

            IEntityColliderHolder enemyColliderHolder = enemyData.get();
            Map<String, ICollider<Entity, Void>> hurtBox = enemyColliderHolder.getHurtBox();

            // 没有受击盒则跳过
            if (hurtBox.isEmpty()) continue;

            enemyColliderHolder.getCoordinateConverter().update();

            for (ICollider<Entity, Void> hit : hitBoxValues) {
                for (ICollider<Entity, Void> hurt : hurtBox.values()) {
                    if (!ColliderUtil.colliding(hit, entity, null, hurt, enemy, null)) {
                        continue;
                    }

                    continue enemyFor;
                }
            }
        }
    }
}
