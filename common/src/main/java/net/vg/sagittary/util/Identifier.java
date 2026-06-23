package net.vg.sagittary.util;

import net.vg.sagittary.Sagittary;

public class Identifier {
    public static net.minecraft.resources.Identifier of(String path) {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath(Sagittary.MOD_ID, path);
    }

    public static net.minecraft.resources.Identifier of(String namespace, String path) {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath(namespace, path);
    }
}
