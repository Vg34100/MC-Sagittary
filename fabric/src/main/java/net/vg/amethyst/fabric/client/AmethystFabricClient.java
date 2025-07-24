package net.vg.amethyst.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.vg.amethyst.client.AmethystClient;

public final class AmethystFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AmethystClient.init();
    }
}
