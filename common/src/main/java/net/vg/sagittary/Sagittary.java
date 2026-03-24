package net.vg.sagittary;

import net.vg.sagittary.registry.ObjectRegistry;

public final class Sagittary {
    public static final String MOD_ID = "sagittary";

    public static void init() {
        // Write common init code here.
        ObjectRegistry.init();
    }
}
