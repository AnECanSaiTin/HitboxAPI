package cn.anecansaitin.hitboxapi.client.collider.render;

import cn.anecansaitin.hitboxapi.api.client.collider.ColliderRenderUtil;
import cn.anecansaitin.hitboxapi.api.client.collider.IColliderRender;
import cn.anecansaitin.hitboxapi.api.common.collider.ICapsule;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityCapsuleRender implements IColliderRender<Entity> {
    public static final EntityCapsuleRender INSTANCE = new EntityCapsuleRender();

    @Override
    public void render(Entity entity, ICollider<Entity, ?> collision, PoseStack poseStack, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        ICapsule<Entity, ?> capsule = (ICapsule<Entity, ?>) collision;
        Vec3 position = entity.position();
        Vector3f center = capsule.getCenter();
        Quaternionf rotation = capsule.getRotation();
        float radius = capsule.getRadius();
        float height = capsule.getHeight();
        ColliderRenderUtil.renderCapsule(
                poseStack, buffer,
                (float) (center.x() - position.x()), (float) (center.y() - position.y()), (float) (center.z() - position.z()),
                radius, height, rotation,
                red, green, blue, alpha
        );
    }
}
