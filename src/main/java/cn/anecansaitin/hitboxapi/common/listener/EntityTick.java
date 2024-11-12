package cn.anecansaitin.hitboxapi.common.listener;

import cn.anecansaitin.hitboxapi.common.ColliderHolder;
import cn.anecansaitin.hitboxapi.common.HitboxDataAttachments;
import cn.anecansaitin.hitboxapi.common.colliders.ICollider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//@EventBusSubscriber(modid = HitboxApi.MODID)
public class EntityTick {
//    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Optional<ColliderHolder> data = entity.getExistingData(HitboxDataAttachments.COLLISION);

        if (data.isEmpty()) return;

        ColliderHolder colliderHolder = data.get();
        Map<String, ICollider> hitBox = colliderHolder.hitBox;

        if (hitBox.isEmpty()) return;

        List<Entity> entities = entity.level().getEntities(entity, entity.getBoundingBox().inflate(5));

        for (ICollider collision : hitBox.values()) {
            for (int i = 0, entitiesSize = entities.size(); i < entitiesSize; i++) {
                Entity enemy = entities.get(i);
                Optional<ColliderHolder> enemyData = enemy.getExistingData(HitboxDataAttachments.COLLISION);

                if (enemyData.isEmpty()) continue;

                entities.remove(i);

                for (ICollider hurtBox : enemyData.get().hurtBox.values()) {
                    if (!collision.isColliding(hurtBox)) continue;

                    if (entity instanceof Player player) {
                        player.sendSystemMessage(Component.literal("击中"));
                    }
                }
            }
        }
    }
}
