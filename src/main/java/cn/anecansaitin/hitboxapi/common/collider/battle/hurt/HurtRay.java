package cn.anecansaitin.hitboxapi.common.collider.battle.hurt;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.common.collider.basic.Ray;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3f;

public class HurtRay extends Ray<Entity, Void> implements IHurtCollider {
    public float scale;

    public HurtRay(float scale, Vector3f LocalOrigin, Vector3f localDirection, float length) {
        super(LocalOrigin, localDirection, length);
        this.scale = scale;
    }

    @Override
    public float modifyDamage(float damage) {
        return scale * damage;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        tag.put("0", listTag);
        listTag.add(FloatTag.valueOf(localOrigin.x));
        listTag.add(FloatTag.valueOf(localOrigin.y));
        listTag.add(FloatTag.valueOf(localOrigin.z));
        listTag.add(FloatTag.valueOf(localDirection.x));
        listTag.add(FloatTag.valueOf(localDirection.y));
        listTag.add(FloatTag.valueOf(localDirection.z));
        listTag.add(FloatTag.valueOf(length));
        listTag.add(FloatTag.valueOf(scale));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        localOrigin.set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        localDirection.set(list.getFloat(3), list.getFloat(4), list.getFloat(5));
        length = list.getFloat(6);
        scale = list.getFloat(7);
    }
}
