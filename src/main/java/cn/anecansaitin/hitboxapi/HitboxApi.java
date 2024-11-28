package cn.anecansaitin.hitboxapi;

import cn.anecansaitin.hitboxapi.common.HitboxDataAttachments;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(HitboxApi.MODID)
public class HitboxApi {
    //todo 懒加载碰撞箱本身也需要一个被修改标记
    public static final String MODID = "hitboxapi";
    private static final Logger LOGGER = LogUtils.getLogger();

    public HitboxApi(IEventBus modEventBus, ModContainer modContainer) {
        HitboxDataAttachments.ATTACHMENTS.register(modEventBus);
    }
}
