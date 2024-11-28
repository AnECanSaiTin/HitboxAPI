package cn.anecansaitin.hitboxapi.common.network;

import cn.anecansaitin.hitboxapi.HitboxApi;
import cn.anecansaitin.hitboxapi.api.common.attachment.IEntityColliderHolder;
import cn.anecansaitin.hitboxapi.common.HitboxDataAttachments;
import cn.anecansaitin.hitboxapi.common.attachment.EntityColliderHolder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;
import java.util.stream.Stream;

public record S2CBattleColliderIncrementalSyne(int id, CompoundTag tag) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<S2CBattleColliderIncrementalSyne> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(HitboxApi.MODID, "s_2_c_battle_collider_incremental_syne"));
    private static final HolderLookup.Provider PROVIDER = HolderLookup.Provider.create(Stream.empty());
    public static final StreamCodec<ByteBuf, S2CBattleColliderIncrementalSyne> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            (p) -> p.id,
            ByteBufCodecs.COMPOUND_TAG,
            (p) -> p.tag,
            S2CBattleColliderIncrementalSyne::new
    );

    public S2CBattleColliderIncrementalSyne(int id, IEntityColliderHolder holder) {
        this(id, holder.getUpdate());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(S2CBattleColliderIncrementalSyne payload, IPayloadContext context) {
        Entity entity = context
                .player()
                .level()
                .getEntity(payload.id);

        Optional<IEntityColliderHolder> existingData = entity.getExistingData(HitboxDataAttachments.COLLISION);
        IEntityColliderHolder holder;

        if (existingData.isEmpty()) {
            holder = new EntityColliderHolder(entity);
            entity.setData(HitboxDataAttachments.COLLISION, holder);
        } else {
            holder = existingData.get();
        }

        holder.update(payload.tag);
    }
}
