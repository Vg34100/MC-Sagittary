package net.vg.sagittary.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.minecraft.client.Minecraft;
import net.vg.sagittary.Sagittary;
import net.vg.sagittary.client.TopazPulseRenderer;
import net.vg.sagittary.client.SagittaryClient;

@EventBusSubscriber(modid = Sagittary.MOD_ID, value = Dist.CLIENT)
public final class SagittaryNeoForgeRenderHooks {
    private SagittaryNeoForgeRenderHooks() {}

    @SubscribeEvent
    //? if >=26.1 {
    public static void onRenderLevel(RenderLevelStageEvent.AfterLevel event) {
        TopazPulseRenderer.render();
    }
    //? } else {
    /*public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            TopazPulseRenderer.render(event.getPoseStack(), event.getCamera().getPosition());
        }
    }
    *///? }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (SagittaryClient.handleQuiverScroll(Minecraft.getInstance(), event.getScrollDeltaY())) {
            event.setCanceled(true);
        }
    }
}
