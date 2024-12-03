package cn.anecansaitin.hitboxapi.client.collider.render;

import cn.anecansaitin.hitboxapi.api.client.collider.ColliderRenderUtil;
import cn.anecansaitin.hitboxapi.api.client.collider.IColliderRender;
import cn.anecansaitin.hitboxapi.api.common.collider.IAABB;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class EntityAABBRender implements IColliderRender<Entity> {
    public static final EntityAABBRender INSTANCE = new EntityAABBRender();

    @Override
    public void render(Entity entity, ICollider<Entity, ?> collision, PoseStack poseStack, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        IAABB<Entity, ?> aabb = (IAABB<Entity, ?>) collision;
        Vec3 position = entity.position();
        Vector3f center = aabb.getCenter();
        Vector3f halfExtents = aabb.getHalfExtents();
        ColliderRenderUtil.renderAABB(
                poseStack, buffer,
                (float) (center.x - halfExtents.x - position.x), (float) (center.y - halfExtents.y - position.y), (float) (center.z - halfExtents.z - position.z),
                (float) (center.x + halfExtents.x - position.x), (float) (center.y + halfExtents.y - position.y), (float) (center.z + halfExtents.z - position.z),
                red, green, blue, alpha
        );
    }
}
