package net.vg.amethyst;

import net.vg.amethyst.registry.ObjectRegistry;

public final class Amethyst {
    public static final String MOD_ID = "amethyst";

    public static void init() {
        // Write common init code here.
        ObjectRegistry.init();
    }
}
