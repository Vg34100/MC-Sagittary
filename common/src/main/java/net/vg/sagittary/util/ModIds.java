package net.vg.sagittary.util;

import net.vg.sagittary.Sagittary;

public final class ModIds {
    public static net.minecraft.resources.Identifier parse(String value) {
        //? if >=1.21 {
        return net.minecraft.resources.Identifier.parse(value);
        //? } else {
        /*return new net.minecraft.resources.Identifier(value);
        *///? }
    }
    public static net.minecraft.resources.Identifier of(String path) {
        return of(Sagittary.MOD_ID, path);
    }

    public static net.minecraft.resources.Identifier of(String namespace, String path) {
        //? if >=1.21 {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath(namespace, path);
        //? } else {
        /*return new net.minecraft.resources.Identifier(namespace, path);
        *///? }
    }
}
