package cn.anecansaitin.hitboxapi.client.collider.render;

import cn.anecansaitin.hitboxapi.api.client.collider.ICollisionRender;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.api.common.collider.IRay;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;

public class RayRender implements ICollisionRender {
    public static final RayRender INSTANCE = new RayRender();

    @Override
    public void render(ICollider<?, ?> collision, PoseStack poseStack, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        IRay<?, ?> ray = (IRay<?, ?>) collision;
        PoseStack.Pose pose = poseStack.last();
        Vector3f origin = ray.getLocalOrigin();
        Vector3f end = ray.getLocalEnd();
        Vector3f direction = ray.getLocalDirection();
        buffer.addVertex(pose, origin.x, origin.y, origin.z).setColor(red, green, blue, alpha).setNormal(pose, direction.x, direction.y, direction.z);
        buffer.addVertex(pose, end.x, end.y, end.z).setColor(red, green, blue, alpha).setNormal(pose, direction.x, direction.y, direction.z);
    }
}
