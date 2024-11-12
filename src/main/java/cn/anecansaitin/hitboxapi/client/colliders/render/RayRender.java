package cn.anecansaitin.hitboxapi.client.colliders.render;

import cn.anecansaitin.hitboxapi.common.colliders.ICollider;
import cn.anecansaitin.hitboxapi.common.colliders.Ray;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

public class RayRender implements ICollisionRender {
    public static final RayRender INSTANCE = new RayRender();

    @Override
    public void render(ICollider collision, PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha) {
        Ray ray = (Ray) collision;
        PoseStack.Pose pose = poseStack.last();
        Vector3f origin = ray.origin;
        Vector3f end = ray.getEnd();
        Vector3f direction = ray.direction;
        buffer.addVertex(pose, origin.x, origin.y, origin.z).setColor(red, green, blue, alpha).setNormal(pose, direction.x, direction.y, direction.z);
        buffer.addVertex(pose, end.x, end.y, end.z).setColor(red, green, blue, alpha).setNormal(pose, direction.x, direction.y, direction.z);
    }
}
