package cn.anecansaitin.hitboxapi.common;

import cn.anecansaitin.hitboxapi.HitboxApi;
import cn.anecansaitin.hitboxapi.common.network.S2CBattleColliderFullSyne;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = HitboxApi.MODID, bus = EventBusSubscriber.Bus.MOD)
public class HitboxNetwork {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event){
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                S2CBattleColliderFullSyne.TYPE,
                S2CBattleColliderFullSyne.CODEC,
                S2CBattleColliderFullSyne::handle
        );
    }
}
