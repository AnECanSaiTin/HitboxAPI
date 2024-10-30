package cn.anecansaitin.hitboxapi.common;

import cn.anecansaitin.hitboxapi.HitboxApi;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class HitboxDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, HitboxApi.MODID);

    public static final Supplier<AttachmentType<CollisionHolder>> COLLISION = ATTACHMENTS.register("collision", () -> AttachmentType.builder(() -> new CollisionHolder()).build());
}
