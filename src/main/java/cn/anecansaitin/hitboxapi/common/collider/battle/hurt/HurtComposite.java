package cn.anecansaitin.hitboxapi.common.collider.battle.hurt;

import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.common.collider.basic.Composite;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class HurtComposite extends Composite<Entity, Void> implements IHurtCollider {
    public float scale;

    public HurtComposite(float scale, Vector3f position, Quaternionf rotation, List<Pair<String, ICollider<Entity, Void>>> collisions) {
        super(position, rotation, collisions);
        this.scale = scale;
    }

    @Override
    public float modifyDamage(float damage) {
        return damage * scale;
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
        listTag.add(FloatTag.valueOf(scale));

        ListTag boxList = new ListTag();
        tag.put("1", boxList);

        for (int i = 0; i < colliders.length; i++) {
            IHurtCollider collider = (IHurtCollider) colliders[i];
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
        scale = list.getFloat(7);
        ListTag boxList = nbt.getList("1", CompoundTag.TAG_COMPOUND);

        colliders = new IHurtCollider[boxList.size()];
        colliderNames = new String[boxList.size()];
        collisionMap.clear();

        for (int i = 0; i < boxList.size(); i++) {
            CompoundTag box = boxList.getCompound(i);
            byte type = box.getByte("0");
            String name = box.getString("1");
            CompoundTag compound = box.getCompound("2");

            IHurtCollider collider = switch (type) {
                case 0 -> new HurtOBB(0, new Vector3f(), new Vector3f(), new Quaternionf());
                case 1 -> new HurtSphere(0, new Vector3f(), 0);
                case 2 -> new HurtCapsule(0, new Vector3f(), 0, 0, new Quaternionf());
                case 3 -> new HurtAABB(0, new Vector3f(), new Vector3f());
                case 4 -> new HurtRay(0, new Vector3f(), new Vector3f(), 0);
                case 5 -> new HurtComposite(0, new Vector3f(), new Quaternionf(), new ArrayList<>());
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };

            collider.deserializeNBT(provider, compound);
            colliders[i] = collider;
            colliderNames[i] = name;
            collisionMap.put(name, new IntObjectImmutablePair<>(i, collider));
        }
    }
}
