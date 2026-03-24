package net.vg.sagittary.util;

import net.minecraft.resources.ResourceLocation;
import net.vg.sagittary.Sagittary;

public class Identifier {
    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(Sagittary.MOD_ID, path);
    }

    public static ResourceLocation of(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
