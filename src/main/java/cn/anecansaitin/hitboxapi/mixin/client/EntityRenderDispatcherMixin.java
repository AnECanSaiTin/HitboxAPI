package cn.anecansaitin.hitboxapi.mixin.client;

import cn.anecansaitin.hitboxapi.client.colliders.render.ICollisionRender;
import cn.anecansaitin.hitboxapi.common.CollisionHolder;
import cn.anecansaitin.hitboxapi.common.HitboxDataAttachments;
import cn.anecansaitin.hitboxapi.common.colliders.ICollision;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Inject(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderVector(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lorg/joml/Vector3f;Lnet/minecraft/world/phys/Vec3;I)V"))
    private static void hitboxApi$renderColliders(PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha, CallbackInfo ci) {
        Optional<CollisionHolder> data = entity.getExistingData(HitboxDataAttachments.COLLISION);

        if (data.isEmpty()) return;

        CollisionHolder holder = data.get();

        for (ICollision collision : holder.hurtBox.values()) {
            hitboxApi$renderCollider(collision, poseStack, buffer, entity, 0, 1, 0, alpha);
        }

        for (ICollision collision : holder.hitBox.values()) {
            hitboxApi$renderCollider(collision, poseStack, buffer, entity, 1, 0, 0, alpha);
        }
    }

    @Unique
    private static void hitboxApi$renderCollider(ICollision collision, PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha) {
        ICollisionRender renderer = collision.getRenderer();

        if (renderer == null) return;

        renderer.render(collision, poseStack, buffer, entity, red, green, blue, alpha);
    }
}
