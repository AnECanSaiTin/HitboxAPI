package cn.anecansaitin.hitboxapi.common.collider.battle.hit;

import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHitCollider;
import cn.anecansaitin.hitboxapi.common.collider.basic.Composite;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class HitComposite extends Composite<Entity, Void> implements IHitCollider {
    public float damage;
    public ResourceKey<DamageType> damageType;

    public HitComposite(float damage, ResourceKey<DamageType> damageType, Vector3f position, Quaternionf rotation, List<Pair<String, ICollider<Entity, Void>>> collisions) {
        super(position, rotation, collisions);
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

        for (int i = 0; i < colliders.length; i++) {
            IHitCollider collider = (IHitCollider) colliders[i];
            String name = colliderNames[i];
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
        position.set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
        rotation.set(list.getFloat(3), list.getFloat(4), list.getFloat(5), list.getFloat(6));
        damage = list.getFloat(7);
        damageType = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(nbt.getString("1")));
        ListTag boxList = nbt.getList("2", CompoundTag.TAG_COMPOUND);

        colliders = new IHitCollider[boxList.size()];
        colliderNames = new String[boxList.size()];
        collisionMap.clear();

        for (int i = 0; i < boxList.size(); i++) {
            CompoundTag box = boxList.getCompound(i);
            byte type = box.getByte("0");
            String name = box.getString("1");
            CompoundTag compound = box.getCompound("2");

            IHitCollider collider = switch (type) {
                case 0 -> new HitOBB(0, null, new Vector3f(), new Vector3f(), new Quaternionf());
                case 1 -> new HitSphere(0, null, new Vector3f(), 0);
                case 2 -> new HitCapsule(0, null, new Vector3f(), 0, 0, new Quaternionf());
                case 3 -> new HitAABB(0, null, new Vector3f(), new Vector3f());
                case 4 -> new HitRay(0, null, new Vector3f(), new Vector3f(), 0);
                case 5 -> new HitComposite(0, null, new Vector3f(), new Quaternionf(), new ArrayList<>());
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };

            collider.deserializeNBT(provider, compound);
            colliders[i] = collider;
            colliderNames[i] = name;
            collisionMap.put(name, new IntObjectImmutablePair<>(i, collider));
        }
    }
}
