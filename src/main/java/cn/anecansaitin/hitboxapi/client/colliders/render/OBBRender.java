package cn.anecansaitin.hitboxapi.client.colliders.render;

import cn.anecansaitin.hitboxapi.common.colliders.ICollision;
import cn.anecansaitin.hitboxapi.common.colliders.OBB;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

public final class OBBRender implements IColliderRender{
    public static final OBBRender INSTANCE = new OBBRender();

    @Override
    public void render(ICollision collision, PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha) {
        OBB obb = (OBB) collision;
        Vector3f center = obb.center;
        Vector3f halfExtents = obb.halfExtents;
        poseStack.pushPose();
        poseStack.translate(center.x, center.y, center.z);
        poseStack.mulPose(obb.rotation);
        LevelRenderer.renderLineBox(poseStack, buffer, -halfExtents.x, -halfExtents.y, -halfExtents.z, halfExtents.x, halfExtents.y, halfExtents.z, red, green, blue, alpha);
        poseStack.popPose();
    }
}
