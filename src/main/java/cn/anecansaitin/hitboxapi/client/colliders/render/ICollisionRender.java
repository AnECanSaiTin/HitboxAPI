package cn.anecansaitin.hitboxapi.client.colliders.render;

import cn.anecansaitin.hitboxapi.common.collider.ICollider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public interface ICollisionRender {
    void render(ICollider<? ,?> collision, PoseStack poseStack, VertexConsumer buffer, float red, float green, float blue, float alpha);
}
