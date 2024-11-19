package cn.anecansaitin.hitboxapi.client.collider.render;

import cn.anecansaitin.hitboxapi.api.client.collider.ICollisionRender;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.api.common.collider.IOBB;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Vector3f;

public class OBBRender implements ICollisionRender {
    public static final OBBRender INSTANCE = new OBBRender();

    @Override
    public void render(ICollider<?, ?> collision, PoseStack poseStack, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        IOBB<?, ?> obb = (IOBB<?, ?>) collision;
        Vector3f center = obb.getLocalCenter();
        Vector3f halfExtents = obb.getHalfExtents();
        poseStack.pushPose();
        poseStack.translate(center.x, center.y, center.z);
        poseStack.mulPose(obb.getLocalRotation());
        LevelRenderer.renderLineBox(poseStack, buffer, -halfExtents.x, -halfExtents.y, -halfExtents.z, halfExtents.x, halfExtents.y, halfExtents.z, red, green, blue, alpha);
        poseStack.popPose();
    }
}
