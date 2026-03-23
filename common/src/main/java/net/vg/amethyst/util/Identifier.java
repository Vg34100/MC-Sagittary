package net.vg.amethyst.util;

import net.minecraft.resources.ResourceLocation;
import net.vg.amethyst.Amethyst;

public class Identifier {
    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(Amethyst.MOD_ID, path);
    }

    public static ResourceLocation of(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
