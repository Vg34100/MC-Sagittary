package net.vg.sagittary.fabric;

import net.vg.sagittary.Sagittary;
import net.vg.sagittary.registry.ObjectRegistry;
import net.fabricmc.api.ModInitializer;

public final class SagittaryFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        Sagittary.init();
        
        // Register dispenser behaviors after registries are ready
        ObjectRegistry.registerDispenserBehaviors();
    }
}
