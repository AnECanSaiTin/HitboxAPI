package cn.anecansaitin.hitboxapi.common.network;

import cn.anecansaitin.hitboxapi.HitboxApi;
import cn.anecansaitin.hitboxapi.api.common.attachment.IEntityColliderHolder;
import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHitCollider;
import cn.anecansaitin.hitboxapi.api.common.collider.battle.IHurtCollider;
import cn.anecansaitin.hitboxapi.common.HitboxDataAttachments;
import cn.anecansaitin.hitboxapi.common.attachment.EntityColliderHolder;
import cn.anecansaitin.hitboxapi.common.collider.battle.hit.*;
import cn.anecansaitin.hitboxapi.common.collider.battle.hurt.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 完整发送整个碰撞箱（仅Battle类型）
 */
public record S2CBattleColliderFullSyne(int id, IEntityColliderHolder holder) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<S2CBattleColliderFullSyne> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(HitboxApi.MODID, "s_2_c_battle_collider_full_syne"));
    private static final HolderLookup.Provider PROVIDER = HolderLookup.Provider.create(Stream.empty());

    public static final StreamCodec<ByteBuf, S2CBattleColliderFullSyne> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            (p) -> p.id,
            ByteBufCodecs.COMPOUND_TAG,
            (p) -> toNBT(p.holder),
            (id, nbt) -> new S2CBattleColliderFullSyne(id, fromNBT(nbt))
    );

    private static CompoundTag toNBT(IEntityColliderHolder holder) {
        CompoundTag tag = new CompoundTag();
        ListTag hit = new ListTag();

        for (Map.Entry<String, ICollider<Entity, Void>> entry : holder.getHitBox().entrySet()) {
            ICollider<Entity, Void> value = entry.getValue();

            if (!(value instanceof IHitCollider hitCollider)) {
                continue;
            }

            String key = entry.getKey();
            CompoundTag nbt = hitCollider.serializeNBT(PROVIDER);
            byte type = switch (hitCollider.getType()) {
                case OBB -> 0;
                case SPHERE -> 1;
                case CAPSULE -> 2;
                case AABB -> 3;
                case RAY -> 4;
                case COMPOSITE -> 5;
            };

            CompoundTag box = new CompoundTag();
            box.putByte("0", type);
            box.putString("1", key);
            box.put("2", nbt);
            hit.add(box);
        }

        ListTag hurt = new ListTag();

        for (Map.Entry<String, ICollider<Entity, Void>> entry : holder.getHitBox().entrySet()) {
            ICollider<Entity, Void> value = entry.getValue();

            if (!(value instanceof IHurtCollider hurtCollider)) {
                continue;
            }

            String key = entry.getKey();
            CompoundTag nbt = hurtCollider.serializeNBT(PROVIDER);
            byte type = switch (hurtCollider.getType()) {
                case OBB -> 0;
                case SPHERE -> 1;
                case CAPSULE -> 2;
                case AABB -> 3;
                case RAY -> 4;
                case COMPOSITE -> 5;
            };

            CompoundTag box = new CompoundTag();
            box.putByte("0", type);
            box.putString("1", key);
            box.put("2", nbt);
            hurt.add(box);
        }

        tag.put("0", hit);
        tag.put("1", hurt);
        return tag;
    }

    private static IEntityColliderHolder fromNBT(CompoundTag tag) {
        EntityColliderHolder holder = new EntityColliderHolder();
        ListTag hit = tag.getList("0", CompoundTag.TAG_COMPOUND);

        for (int i = 0; i < hit.size(); i++) {
            CompoundTag box = hit.getCompound(i);
            byte type = box.getByte("0");
            String key = box.getString("1");
            CompoundTag nbt = box.getCompound("2");

            IHitCollider hitCollider = switch (type) {
                case 0 -> new HitOBB(0, null, new Vector3f(), new Vector3f(), new Quaternionf());
                case 1 -> new HitSphere(0, null, new Vector3f(), 0);
                case 2 -> new HitCapsule(0, null, new Vector3f(), 0, 0, new Quaternionf());
                case 3 -> new HitAABB(0, null, new Vector3f(), new Vector3f());
                case 4 -> new HitRay(0, null, new Vector3f(), new Vector3f(), 0);
                case 5 -> new HitComposite(0, null, new Vector3f(), new Quaternionf(), new ArrayList<>());
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };

            hitCollider.deserializeNBT(PROVIDER, nbt);
            holder.getHitBox().put(key, hitCollider);
        }

        ListTag hurt = tag.getList("1", CompoundTag.TAG_COMPOUND);

        for (int i = 0; i < hurt.size(); i++) {
            CompoundTag box = hurt.getCompound(i);
            byte type = box.getByte("0");
            String key = box.getString("1");
            CompoundTag nbt = box.getCompound("2");

            IHurtCollider hurtCollider = switch (type) {
                case 0 -> new HurtOBB(0, new Vector3f(), new Vector3f(), new Quaternionf());
                case 1 -> new HurtSphere(0, new Vector3f(), 0);
                case 2 -> new HurtCapsule(0, new Vector3f(), 0, 0, new Quaternionf());
                case 3 -> new HurtAABB(0, new Vector3f(), new Vector3f());
                case 4 -> new HurtRay(0, new Vector3f(), new Vector3f(), 0);
                case 5 -> new HurtComposite(0, new Vector3f(), new Quaternionf(), new ArrayList<>());
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };

            hurtCollider.deserializeNBT(PROVIDER, nbt);
            holder.getHurtBox().put(key, hurtCollider);
        }

        return holder;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(S2CBattleColliderFullSyne payload, IPayloadContext context) {
        context
                .player()
                .level()
                .getEntity(payload.id)
                .setData(HitboxDataAttachments.COLLISION, payload.holder);
    }
}
