package net.vg.sagittary.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.vg.sagittary.client.SagittaryClient;

public final class SagittaryFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SagittaryClient.init();
    }
}
