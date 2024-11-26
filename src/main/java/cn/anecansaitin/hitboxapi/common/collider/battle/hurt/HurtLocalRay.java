package cn.anecansaitin.hitboxapi.common.collider.battle.hurt;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import cn.anecansaitin.hitboxapi.common.collider.local.LocalRay;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3f;

public class HurtLocalRay extends LocalRay<Entity, Void> implements IHurtCollider {
    private float scale;

    public HurtLocalRay(float scale, Vector3f LocalOrigin, Vector3f localDirection, float length, ICoordinateConverter parent) {
        super(LocalOrigin, localDirection, length, parent);
        this.scale = scale;
    }

    @Override
    public float modifyDamage(float damage) {
        return scale * damage;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        Vector3f origin = getLocalOrigin();
        Vector3f direction = getLocalDirection();
        float length = getLength();
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        tag.put("0", listTag);
        listTag.add(FloatTag.valueOf(origin.x));
        listTag.add(FloatTag.valueOf(origin.y));
        listTag.add(FloatTag.valueOf(origin.z));
        listTag.add(FloatTag.valueOf(direction.x));
        listTag.add(FloatTag.valueOf(direction.y));
        listTag.add(FloatTag.valueOf(direction.z));
        listTag.add(FloatTag.valueOf(length));
        listTag.add(FloatTag.valueOf(scale));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        getLocalOrigin().set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        getLocalDirection().set(list.getFloat(3), list.getFloat(4), list.getFloat(5));
        setLength(list.getFloat(6));
        scale = list.getFloat(7);
    }
}
