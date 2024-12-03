package cn.anecansaitin.hitboxapi.common;

import cn.anecansaitin.hitboxapi.HitboxApi;
import cn.anecansaitin.hitboxapi.common.attachment.EntityColliderHolder;
import cn.anecansaitin.hitboxapi.api.common.attachment.IEntityColliderHolder;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class HitboxDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, HitboxApi.MODID);

    public static final Supplier<AttachmentType<IEntityColliderHolder>> COLLISION = ATTACHMENTS.register("collision", () -> AttachmentType.builder(() -> (IEntityColliderHolder) null).build());
}
