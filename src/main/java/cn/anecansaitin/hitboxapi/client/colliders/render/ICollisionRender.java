package cn.anecansaitin.hitboxapi.client.colliders.render;

import cn.anecansaitin.hitboxapi.common.colliders.ICollision;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;

public interface ICollisionRender {
    void render(ICollision collision, PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha);
}
