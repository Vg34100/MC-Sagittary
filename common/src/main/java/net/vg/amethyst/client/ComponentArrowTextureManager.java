package net.vg.amethyst.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.vg.amethyst.component.ArrowComponent;
import net.vg.amethyst.item.ComponentArrowItem;
import net.vg.amethyst.util.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentArrowTextureManager {
    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new ConcurrentHashMap<>();
    
    // Base component texture paths
    private static final String TIP_PATH = "components/tips/";
    private static final String SHAFT_PATH = "components/shafts/";
    private static final String FLETCHING_PATH = "components/fletching/";
    
    /**
     * Gets the texture identifier for a component arrow based on its components
     */
    public static ResourceLocation getTextureForComponents(ArrowComponent tip, ArrowComponent shaft, ArrowComponent fletching) {
        String cacheKey = tip.getMaterialName() + "_" + shaft.getMaterialName() + "_" + fletching.getMaterialName();
        
        return TEXTURE_CACHE.computeIfAbsent(cacheKey, key -> {
            // For now, return a composed identifier - actual texture composition will be handled by the renderer
            return Identifier.of("item/component_arrow_" + cacheKey);
        });
    }
    
    /**
     * Gets the texture for a ComponentArrowItem stack
     */
    public static ResourceLocation getTextureForStack(ItemStack stack) {
        if (!(stack.getItem() instanceof ComponentArrowItem)) {
            return Identifier.of("item/component_arrow");
        }
        
        ArrowComponent tip = ComponentArrowItem.getTipFromStack(stack);
        ArrowComponent shaft = ComponentArrowItem.getShaftFromStack(stack);
        ArrowComponent fletching = ComponentArrowItem.getFletchingFromStack(stack);
        
        return getTextureForComponents(tip, shaft, fletching);
    }
    
    /**
     * Gets the ResourceLocation for a component texture
     */
    public static ResourceLocation getTipTexture(ArrowComponent tip) {
        return Identifier.of(TIP_PATH + tip.getMaterialName());
    }
    
    public static ResourceLocation getShaftTexture(ArrowComponent shaft) {
        return Identifier.of(SHAFT_PATH + shaft.getMaterialName());
    }
    
    public static ResourceLocation getFletchingTexture(ArrowComponent fletching) {
        return Identifier.of(FLETCHING_PATH + fletching.getMaterialName());
    }
    
    /**
     * Gets the base arrow texture
     */
    public static ResourceLocation getBaseTexture() {
        return Identifier.of("item/component_arrow");
    }
    
    /**
     * Clears the texture cache (useful for resource pack reloads)
     */
    public static void clearCache() {
        TEXTURE_CACHE.clear();
    }
}