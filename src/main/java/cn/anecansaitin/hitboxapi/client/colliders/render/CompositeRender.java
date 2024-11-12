package cn.anecansaitin.hitboxapi.client.colliders.render;

import cn.anecansaitin.hitboxapi.common.colliders.Composite;
import cn.anecansaitin.hitboxapi.common.colliders.ICollider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CompositeRender implements ICollisionRender {
    public static final CompositeRender INSTANCE = new CompositeRender();

    @Override
    public void render(ICollider collision, PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha) {
        Composite composite = (Composite) collision;
        Vector3f position = composite.position;
        Quaternionf rotation = composite.rotation;
        poseStack.pushPose();

        poseStack.translate(position.x, position.y, position.z);
        poseStack.mulPose(rotation);

        for (int i = 0; i < composite.getCollisionCount(); i++) {
            ICollider c = composite.getCollision(i);
            c.getRenderer().render(c, poseStack, buffer, entity, red, green, blue, alpha);
        }

        poseStack.popPose();
    }
}
