package net.vg.amethyst.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.vg.amethyst.Amethyst;
import net.neoforged.fml.common.Mod;
import net.vg.amethyst.client.AmethystClient;
import net.vg.amethyst.registry.ObjectRegistry;
import net.vg.amethyst.screen.FletchingTableScreen;

@Mod(Amethyst.MOD_ID)
public final class AmethystNeoForge {
    public AmethystNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        // Run our common setup.
        Amethyst.init();
        
        // Add setup listeners
        modEventBus.addListener(this::commonSetup);
        if(FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::clientSetup);
            modEventBus.addListener(this::registerEntityRenderers);
            modEventBus.addListener(this::registerMenuScreens);
        }
    }
    
    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ObjectRegistry::registerDispenserBehaviors);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        // Other client setup can go here if needed
    }
    
    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        // Register menu screens at the proper time for NeoForge
        event.register(ObjectRegistry.FLETCHING_TABLE_MENU_TYPE.get(), FletchingTableScreen::new);
    }
    
    private void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Register entity renderers at the proper time for NeoForge
        event.registerEntityRenderer(ObjectRegistry.AMETHYST_ARROW_ENTITY.get(), AmethystClient.AmethystArrowRenderer::new);
        event.registerEntityRenderer(ObjectRegistry.COMPONENT_ARROW_ENTITY.get(), AmethystClient.ComponentArrowRenderer::new);
    }
}
