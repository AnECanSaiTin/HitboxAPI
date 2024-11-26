package cn.anecansaitin.hitboxapi.client.collider.render;

import cn.anecansaitin.hitboxapi.api.client.collider.CollisionRenderUtil;
import cn.anecansaitin.hitboxapi.api.client.collider.ICollisionRender;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.api.common.collider.IRay;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class EntityRayRender implements ICollisionRender<Entity> {
    public static final EntityRayRender INSTANCE = new EntityRayRender();

    @Override
    public void render(Entity entity, ICollider<Entity, ?> collision, PoseStack poseStack, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        IRay<Entity, ?> ray = (IRay<Entity, ?>) collision;
        Vec3 position = entity.position();
        Vector3f origin = ray.getOrigin();
        Vector3f end = ray.getEnd();
        CollisionRenderUtil.renderRay(
                poseStack, buffer,
                (float) (origin.x - position.x), (float) (origin.y - position.y), (float) (origin.z - position.z),
                (float) (end.x - position.x), (float) (end.y - position.y), (float) (end.z - position.z),
                red, green, blue, alpha
        );
    }
}
