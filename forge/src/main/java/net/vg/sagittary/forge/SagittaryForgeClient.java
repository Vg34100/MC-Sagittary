package net.vg.sagittary.forge;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.vg.sagittary.client.QuiverTooltip;
import net.vg.sagittary.client.QuiverTooltipRenderer;
import net.vg.sagittary.client.SagittaryClient;
import net.vg.sagittary.registry.ObjectRegistry;
import net.vg.sagittary.screen.FletchingTableScreen;

/** Physical-client-only hooks; never resolved by the dedicated-server entrypoint. */
final class SagittaryForgeClient {
    static void init(IEventBus bus) {
        SagittaryClient.initNetworking();
        bus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            MenuScreens.register(ObjectRegistry.FLETCHING_TABLE_MENU_TYPE.get(), FletchingTableScreen::new);
            SagittaryClient.initItemRenderers();
            SagittaryClient.initQuiverControls();
        }));
        bus.addListener((RegisterKeyMappingsEvent event) -> event.register(SagittaryClient.QUIVER_CYCLE));
        bus.addListener((EntityRenderersEvent.RegisterRenderers event) ->
                event.registerEntityRenderer(ObjectRegistry.COMPONENT_ARROW_ENTITY.get(), net.vg.sagittary.client.ComponentArrowRenderer::new));
        bus.addListener((RegisterClientTooltipComponentFactoriesEvent event) ->
                event.register(QuiverTooltip.class, QuiverTooltipRenderer::new));
        MinecraftForge.EVENT_BUS.addListener((InputEvent.MouseScrollingEvent event) -> {
            if (SagittaryClient.handleQuiverScroll(net.minecraft.client.Minecraft.getInstance(), event.getScrollDelta())) event.setCanceled(true);
        });
        MinecraftForge.EVENT_BUS.addListener((RenderLevelStageEvent event) -> {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER)
                net.vg.sagittary.client.TopazPulseRenderer.render(event.getPoseStack(), event.getCamera().getPosition());
        });
    }
}
