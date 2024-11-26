package cn.anecansaitin.hitboxapi.common.collider.battle.hit;

import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHitCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.local.ICoordinateConverter;
import cn.anecansaitin.hitboxapi.common.collider.local.LocalComposite;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HitLocalComposite extends LocalComposite<IHitCollider, Entity, Void> implements IHitCollider {
    public float damage;
    public ResourceKey<DamageType> damageType;

    public HitLocalComposite(float damage, ResourceKey<DamageType> damageType, Vector3f position, Quaternionf rotation, ICoordinateConverter parent) {
        super(position, rotation, parent);
        this.damage = damage;
        this.damageType = damageType;
    }

    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public ResourceKey<DamageType> getDamageType() {
        return damageType;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        Vector3f position = getLocalPosition();
        Quaternionf rotation = getLocalRotation();
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        tag.put("0", listTag);
        listTag.add(FloatTag.valueOf(position.x));
        listTag.add(FloatTag.valueOf(position.y));
        listTag.add(FloatTag.valueOf(position.z));
        listTag.add(FloatTag.valueOf(rotation.x));
        listTag.add(FloatTag.valueOf(rotation.y));
        listTag.add(FloatTag.valueOf(rotation.z));
        listTag.add(FloatTag.valueOf(rotation.w));
        listTag.add(FloatTag.valueOf(damage));

        tag.putString("1", damageType.location().toString());
        ListTag boxList = new ListTag();
        tag.put("2", boxList);

        for (int i = 0, length = getCollidersCount(); i < length; i++) {
            IHitCollider collider = getCollider(i);
            String name = getColliderName(i);
            byte type = switch (collider.getType()) {
                case OBB -> 0;
                case SPHERE -> 1;
                case CAPSULE -> 2;
                case AABB -> 3;
                case RAY -> 4;
                case COMPOSITE -> 5;
            };

            CompoundTag box = new CompoundTag();
            boxList.add(box);
            box.putByte("0", type);
            box.putString("1", name);
            box.put("2", collider.serializeNBT(provider));
        }

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("0", FloatTag.TAG_FLOAT);
        getLocalPosition().set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        getLocalRotation().set(list.getFloat(3), list.getFloat(4), list.getFloat(5), list.getFloat(6));
        damage = list.getFloat(7);
        damageType = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(nbt.getString("1")));
        ListTag boxList = nbt.getList("2", CompoundTag.TAG_COMPOUND);
        ICoordinateConverter child = getConverter();

        for (int i = 0; i < boxList.size(); i++) {
            CompoundTag box = boxList.getCompound(i);
            byte type = box.getByte("0");
            String name = box.getString("1");
            CompoundTag compound = box.getCompound("2");

            IHitCollider collider = switch (type) {
                case 0 -> new HitLocalOBB(0, null, new Vector3f(), new Vector3f(), new Quaternionf(), child);
                case 1 -> new HitLocalSphere(0, null, new Vector3f(), 0, child);
                case 2 -> new HitLocalCapsule(0, null, 0, 0, new Vector3f(), new Quaternionf(), child);
                case 3 -> new HitLocalAABB(0, null, new Vector3f(), new Vector3f(), child);
                case 4 -> new HitLocalRay(0, null, new Vector3f(), new Vector3f(), 0, child);
                case 5 -> new HitLocalComposite(0, null, new Vector3f(), new Quaternionf(), child);
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };

            collider.deserializeNBT(provider, compound);
            addCollider(name, collider);
        }
    }
}
