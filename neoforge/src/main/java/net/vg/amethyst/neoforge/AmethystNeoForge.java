package net.vg.amethyst.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.vg.amethyst.Amethyst;
import net.neoforged.fml.common.Mod;
import net.vg.amethyst.client.AmethystClient;

@Mod(Amethyst.MOD_ID)
public final class AmethystNeoForge {
    public AmethystNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        // Run our common setup.
        Amethyst.init();

        if(FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::clientSetup);
        }
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(AmethystClient::init);
    }
}
