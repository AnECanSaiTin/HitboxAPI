package cn.anecansaitin.hitboxapi.client.collider.render;

import cn.anecansaitin.hitboxapi.api.client.collider.ICollisionRender;
import cn.anecansaitin.hitboxapi.api.common.collider.IAABB;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Vector3f;

public class AABBRender implements ICollisionRender {
    public static final AABBRender INSTANCE = new AABBRender();

    @Override
    public void render(ICollider<?, ?> collision, PoseStack poseStack, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        IAABB<?, ?> aabb = (IAABB<?, ?>) collision;
        Vector3f center = aabb.getLocalCenter();
        Vector3f halfExtents = aabb.getHalfExtents();
        LevelRenderer.renderLineBox(poseStack, buffer, center.x - halfExtents.x, center.y - halfExtents.y, center.z - halfExtents.z, center.x + halfExtents.x, center.y + halfExtents.y, center.z + halfExtents.z, red, green, blue, alpha);
    }
}
