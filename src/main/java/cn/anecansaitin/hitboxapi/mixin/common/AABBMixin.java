package cn.anecansaitin.hitboxapi.mixin.common;

import cn.anecansaitin.hitboxapi.common.colliders.IAABBCollision;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AABB.class)
public class AABBMixin implements IAABBCollision {
}
