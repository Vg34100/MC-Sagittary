package net.vg.sagittary.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.vg.sagittary.client.SagittaryClient;
import net.vg.sagittary.client.TopazPulseRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

public final class SagittaryFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SagittaryClient.init();
        LevelRenderEvents.BEFORE_GIZMOS.register(context -> TopazPulseRenderer.render());
    }
}
