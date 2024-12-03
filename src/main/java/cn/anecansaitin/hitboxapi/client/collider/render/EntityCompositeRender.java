package cn.anecansaitin.hitboxapi.client.collider.render;

import cn.anecansaitin.hitboxapi.api.client.collider.IColliderRender;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.api.common.collider.IComposite;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;

public class EntityCompositeRender implements IColliderRender<Entity> {
    public static final EntityCompositeRender INSTANCE = new EntityCompositeRender();

    @Override
    public void render(Entity entity, ICollider<Entity, ?> collision, PoseStack poseStack, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        IComposite<ICollider<Entity, ?>, Entity, ?> composite = (IComposite<ICollider<Entity, ?>, Entity, ?>) collision;

        for (int i = 0; i < composite.getCollidersCount(); i++) {
            ICollider<Entity, ?> c = composite.getCollider(i);

            if (c.disable()) {
                continue;
            }

            IColliderRender<Entity> renderer = switch (c.getType()){
                case OBB -> EntityOBBRender.INSTANCE;
                case SPHERE -> EntitySphereRender.INSTANCE;
                case CAPSULE -> EntityCapsuleRender.INSTANCE;
                case AABB -> EntityAABBRender.INSTANCE;
                case RAY -> EntityRayRender.INSTANCE;
                case COMPOSITE -> EntityCompositeRender.INSTANCE;
            };

            renderer.render(entity, c, poseStack, buffer, red, green, blue, alpha);
        }
    }
}
