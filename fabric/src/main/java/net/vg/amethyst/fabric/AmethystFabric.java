package net.vg.amethyst.fabric;

import net.vg.amethyst.Amethyst;
import net.vg.amethyst.registry.ObjectRegistry;
import net.fabricmc.api.ModInitializer;

public final class AmethystFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        Amethyst.init();
        
        // Register dispenser behaviors after registries are ready
        ObjectRegistry.registerDispenserBehaviors();
    }
}
