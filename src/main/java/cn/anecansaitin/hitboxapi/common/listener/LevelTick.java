package cn.anecansaitin.hitboxapi.common.listener;

import cn.anecansaitin.hitboxapi.HitboxApi;
import cn.anecansaitin.hitboxapi.common.HitboxDataAttachments;
import cn.anecansaitin.hitboxapi.common.attachment.IEntityColliderHolder;
import cn.anecansaitin.hitboxapi.common.collider.BoxPoseStack;
import cn.anecansaitin.hitboxapi.common.collider.ICollider;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
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
            collision(Minecraft.getInstance().player);
        } else {
            ServerLevel serverLevel = (ServerLevel) level;

            //todo 这里可能需要多线程
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof Player) {
                    continue;
                }

                collision(entity);
            }
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
        updatePose(entity, entityColliderHolder);

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

            updatePose(enemy, enemyColliderHolder);

            for (ICollider<Entity, Void> hit : hitBoxValues) {
                for (ICollider<Entity, Void> hurt : hurtBox.values()) {
                    if (!hit.isColliding(entityColliderHolder.getPoseStack(), hurt, enemyColliderHolder.getPoseStack())) {
                        continue;
                    }

                    hit.onCollide(entity, enemy, hurt, null);
                    hurt.onCollide(enemy, entity, hit, null);
                    continue enemyFor;
                }
            }
        }
    }

    private static void updatePose(Entity entity, IEntityColliderHolder entityColliderHolder) {
        BoxPoseStack.Pose pose = entityColliderHolder.getPoseStack().last();
        Vector3f posePos = pose.position;
        Vec3 position = entity.position();
        float x = (float) position.x;
        float y = (float) position.y;
        float z = (float) position.z;

        if (!posePos.equals(x, y, z)) {
            posePos.set(x, y, z);
            pose.isDirty = true;
        }
    }
}
