package cn.anecansaitin.hitboxapi.client.colliders.render;

import cn.anecansaitin.hitboxapi.common.colliders.ICollision;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public class AABBRender implements IColliderRender{
    public static final AABBRender INSTANCE = new AABBRender();

    @Override
    public void render(ICollision collision, PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha) {
        AABB aabb = (AABB) collision;
        LevelRenderer.renderLineBox(poseStack, buffer, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, red, green, blue, alpha);
    }
}
