package net.vg.sagittary.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.vg.sagittary.Sagittary;
import net.vg.sagittary.registry.ObjectRegistry;

@Mod(Sagittary.MOD_ID)
public final class SagittaryForge {
    public SagittaryForge() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Sagittary.MOD_ID, bus);
        Sagittary.init();
        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(ObjectRegistry::registerDispenserBehaviors));
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> SagittaryForgeClient.init(bus));
    }
}
