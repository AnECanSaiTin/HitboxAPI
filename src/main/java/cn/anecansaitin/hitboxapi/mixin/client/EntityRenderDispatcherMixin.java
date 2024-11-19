package cn.anecansaitin.hitboxapi.mixin.client;

import cn.anecansaitin.hitboxapi.api.client.collider.ICollisionRender;
import cn.anecansaitin.hitboxapi.common.HitboxDataAttachments;
import cn.anecansaitin.hitboxapi.api.common.attachment.IEntityColliderHolder;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
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
        Optional<IEntityColliderHolder> data = entity.getExistingData(HitboxDataAttachments.COLLISION);

        if (data.isEmpty()) return;

        IEntityColliderHolder holder = data.get();

        for (ICollider<Entity, Void> collision : holder.getHurtBox().values()) {
            hitboxApi$renderCollider(collision, poseStack, buffer, 0, 1, 0, alpha);
        }

        for (ICollider<Entity, Void> collision : holder.getHitBox().values()) {
            hitboxApi$renderCollider(collision, poseStack, buffer, 1, 0, 0, alpha);
        }
    }

    @Unique
    private static void hitboxApi$renderCollider(ICollider<Entity, Void> collision, PoseStack poseStack, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        if (collision.disable()) return;

        ICollisionRender renderer = collision.getRenderer();

        if (renderer == null) return;

        renderer.render(collision, poseStack, buffer, red, green, blue, alpha);
    }
}
