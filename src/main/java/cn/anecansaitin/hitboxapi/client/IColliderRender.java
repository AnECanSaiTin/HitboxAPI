package cn.anecansaitin.hitboxapi.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;

public interface IColliderRender {
    void render(PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha);
}
