package cn.anecansaitin.hitboxapi.mixin.common;

import cn.anecansaitin.hitboxapi.api.common.attachment.IEntityColliderHolder;
import cn.anecansaitin.hitboxapi.common.HitboxDataAttachments;
import cn.anecansaitin.hitboxapi.common.network.S2CBattleColliderFullSyne;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.bundle.PacketAndPayloadAcceptor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Shadow @Final private Entity entity;

    /**
     * 为实体同步添加碰撞箱数据
     */
    @Inject(method = "sendPairingData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;sendPairingData(Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V"))
    public void hitboxApi$sendPairingData(ServerPlayer player, PacketAndPayloadAcceptor<ClientGamePacketListener> acceptor, CallbackInfo ci){
        Optional<IEntityColliderHolder> optData = entity.getExistingData(HitboxDataAttachments.COLLISION);

        if (optData.isEmpty()) return;

        acceptor.accept(new S2CBattleColliderFullSyne(entity.getId(), optData.get()));
    }
}
