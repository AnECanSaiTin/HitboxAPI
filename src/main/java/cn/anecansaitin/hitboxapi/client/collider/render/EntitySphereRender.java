package cn.anecansaitin.hitboxapi.client.collider.render;

import cn.anecansaitin.hitboxapi.api.client.collider.ColliderRenderUtil;
import cn.anecansaitin.hitboxapi.api.client.collider.IColliderRender;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.api.common.collider.ISphere;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class EntitySphereRender implements IColliderRender<Entity> {
    public static final EntitySphereRender INSTANCE = new EntitySphereRender();

    @Override
    public void render(Entity entity, ICollider<Entity, ?> collision, PoseStack poseStack, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        ISphere<Entity, ?> sphere = (ISphere<Entity, ?>) collision;
        Vec3 position = entity.position();
        Vector3f center = sphere.getCenter();
        float radius = sphere.getRadius();
        ColliderRenderUtil.renderSphere(
                poseStack, buffer,
                (float) (center.x - position.x), (float) (center.y - position.y), (float) (center.z - position.z),
                radius,
                red, green, blue, alpha
        );
    }
}
