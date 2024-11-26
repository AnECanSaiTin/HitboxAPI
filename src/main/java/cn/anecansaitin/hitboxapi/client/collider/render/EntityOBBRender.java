package cn.anecansaitin.hitboxapi.client.collider.render;

import cn.anecansaitin.hitboxapi.api.client.collider.CollisionRenderUtil;
import cn.anecansaitin.hitboxapi.api.client.collider.ICollisionRender;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.api.common.collider.IOBB;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityOBBRender implements ICollisionRender<Entity> {
    public static final EntityOBBRender INSTANCE = new EntityOBBRender();

    @Override
    public void render(Entity entity, ICollider<Entity, ?> collision, PoseStack poseStack, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        IOBB<Entity, ?> obb = (IOBB<Entity, ?>) collision;
        Vec3 position = entity.position();
        Vector3f center = obb.getCenter();
        Vector3f halfExtents = obb.getHalfExtents();
        Quaternionf rotation = obb.getRotation();
        CollisionRenderUtil.renderOBB(
                poseStack, buffer,
                (float) (center.x() - position.x()), (float) (center.y() - position.y()), (float) (center.z() - position.z()),
                rotation,
                halfExtents.x(), halfExtents.y(), halfExtents.z(),
                red, green, blue, alpha
        );
    }
}
