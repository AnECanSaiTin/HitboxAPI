package cn.anecansaitin.hitboxapi.client.colliders.render;

import cn.anecansaitin.hitboxapi.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.common.collider.IComposite;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CompositeRender implements ICollisionRender {
    public static final CompositeRender INSTANCE = new CompositeRender();

    @Override
    public void render(ICollider<?, ?> collision, PoseStack poseStack, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        IComposite<?, ?> composite = (IComposite<?, ?>) collision;
        Vector3f position = composite.getLocalPosition();
        Quaternionf rotation = composite.getLocalRotation();
        poseStack.pushPose();
        poseStack.translate(position.x, position.y, position.z);
        poseStack.mulPose(rotation);

        for (int i = 0; i < composite.getCollidersCount(); i++) {
            ICollider<?, ?> c = composite.getCollider(i);

            if (c.disable()) {
                continue;
            }

            c.getRenderer().render(c, poseStack, buffer, red, green, blue, alpha);
        }

        poseStack.popPose();
    }
}
