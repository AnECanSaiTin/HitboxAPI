package cn.anecansaitin.hitboxapi.api.client.collider;

import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public interface IColliderRender<T> {
    void render(T entity, ICollider<T ,?> collision, PoseStack poseStack, VertexConsumer buffer, float red, float green, float blue, float alpha);
}
