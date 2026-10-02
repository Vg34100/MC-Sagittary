package net.vg.sagittary.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.vg.sagittary.Sagittary;
import net.neoforged.fml.common.Mod;
import net.vg.sagittary.client.QuiverTooltip;
import net.vg.sagittary.client.QuiverTooltipRenderer;
import net.vg.sagittary.client.SagittaryClient;
import net.vg.sagittary.registry.ObjectRegistry;
import net.vg.sagittary.screen.FletchingTableScreen;

@Mod(Sagittary.MOD_ID)
public final class SagittaryNeoForge {
    public SagittaryNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        // Run our common setup.
        Sagittary.init();
        
        // Add setup listeners
        modEventBus.addListener(this::commonSetup);
        //? if >=26.1 {
        if(FMLEnvironment.getDist().isClient()) {
        //? } else {
        /*if(FMLEnvironment.dist.isClient()) {
        *///? }
            modEventBus.addListener(this::clientSetup);
            modEventBus.addListener(this::registerEntityRenderers);
            modEventBus.addListener(this::registerMenuScreens);
            modEventBus.addListener(this::registerTooltipComponents);
            modEventBus.addListener(this::registerKeys);
            // Payload types must be known before NeoForge's registration event,
            // not first installed during the later client-setup work queue.
            SagittaryClient.initNetworking();
        }
    }
    
    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ObjectRegistry::registerDispenserBehaviors);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            SagittaryClient.initItemRenderers();
            SagittaryClient.initQuiverControls();
        });
    }

    private void registerKeys(RegisterKeyMappingsEvent event) {
        //? if >=26.1 {
        event.registerCategory(SagittaryClient.QUIVER_CYCLE.getCategory());
        //? }
        event.register(SagittaryClient.QUIVER_CYCLE);
    }
    
    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        // Register menu screens at the proper time for NeoForge
        event.register(ObjectRegistry.FLETCHING_TABLE_MENU_TYPE.get(), FletchingTableScreen::new);
    }
    
    private void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Register entity renderers at the proper time for NeoForge
        // REMOVED: Amethyst arrow is now part of the component system
        // event.registerEntityRenderer(ObjectRegistry.AMETHYST_ARROW_ENTITY.get(), SagittaryClient.AmethystArrowRenderer::new);
        event.registerEntityRenderer(ObjectRegistry.COMPONENT_ARROW_ENTITY.get(), net.vg.sagittary.client.ComponentArrowRenderer::new);
    }

    private void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(QuiverTooltip.class, QuiverTooltipRenderer::new);
    }
}
