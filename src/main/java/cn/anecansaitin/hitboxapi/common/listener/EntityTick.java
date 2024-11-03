package cn.anecansaitin.hitboxapi.common.listener;

import cn.anecansaitin.hitboxapi.HitboxApi;
import cn.anecansaitin.hitboxapi.common.CollisionHolder;
import cn.anecansaitin.hitboxapi.common.HitboxDataAttachments;
import cn.anecansaitin.hitboxapi.common.colliders.ICollision;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//@EventBusSubscriber(modid = HitboxApi.MODID)
public class EntityTick {
//    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Optional<CollisionHolder> data = entity.getExistingData(HitboxDataAttachments.COLLISION);

        if (data.isEmpty()) return;

        CollisionHolder collisionHolder = data.get();
        Map<String, ICollision> hitBox = collisionHolder.hitBox;

        if (hitBox.isEmpty()) return;

        List<Entity> entities = entity.level().getEntities(entity, entity.getBoundingBox().inflate(5));

        for (ICollision collision : hitBox.values()) {
            for (int i = 0, entitiesSize = entities.size(); i < entitiesSize; i++) {
                Entity enemy = entities.get(i);
                Optional<CollisionHolder> enemyData = enemy.getExistingData(HitboxDataAttachments.COLLISION);

                if (enemyData.isEmpty()) continue;

                entities.remove(i);

                for (ICollision hurtBox : enemyData.get().hurtBox.values()) {
                    if (!collision.isColliding(hurtBox)) continue;

                    if (entity instanceof Player player) {
                        player.sendSystemMessage(Component.literal("击中"));
                    }
                }
            }
        }
    }
}
