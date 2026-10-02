package net.vg.sagittary.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.vg.sagittary.component.ArrowComponent;
import net.vg.sagittary.item.ComponentArrowItem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentArrowTextureManager {
    private static final Map<String, Identifier> TEXTURE_CACHE = new ConcurrentHashMap<>();
    
    // Base component texture paths
    private static final String TIP_PATH = "components/tips/";
    private static final String SHAFT_PATH = "components/shafts/";
    private static final String FLETCHING_PATH = "components/fletching/";
    
    /**
     * Gets the texture identifier for a component arrow based on its components
     */
    public static Identifier getTextureForComponents(ArrowComponent tip, ArrowComponent shaft, ArrowComponent fletching) {
        String cacheKey = tip.getMaterialName() + "_" + shaft.getMaterialName() + "_" + fletching.getMaterialName();
        
        return TEXTURE_CACHE.computeIfAbsent(cacheKey, key -> {
            // For now, return a composed identifier - actual texture composition will be handled by the renderer
            return net.vg.sagittary.util.ModIds.of("item/component_arrow_" + cacheKey);
        });
    }
    
    /**
     * Gets the texture for a ComponentArrowItem stack
     */
    public static Identifier getTextureForStack(ItemStack stack) {
        if (!(stack.getItem() instanceof ComponentArrowItem)) {
            return net.vg.sagittary.util.ModIds.of("item/component_arrow");
        }
        
        ArrowComponent tip = ComponentArrowItem.getTipFromStack(stack);
        ArrowComponent shaft = ComponentArrowItem.getShaftFromStack(stack);
        ArrowComponent fletching = ComponentArrowItem.getFletchingFromStack(stack);
        
        return getTextureForComponents(tip, shaft, fletching);
    }
    
    /**
     * Gets the Identifier for a component texture
     */
    public static Identifier getTipTexture(ArrowComponent tip) {
        return net.vg.sagittary.util.ModIds.of(TIP_PATH + tip.getMaterialName());
    }
    
    public static Identifier getShaftTexture(ArrowComponent shaft) {
        return net.vg.sagittary.util.ModIds.of(SHAFT_PATH + shaft.getMaterialName());
    }
    
    public static Identifier getFletchingTexture(ArrowComponent fletching) {
        return net.vg.sagittary.util.ModIds.of(FLETCHING_PATH + fletching.getMaterialName());
    }
    
    /**
     * Gets the base arrow texture
     */
    public static Identifier getBaseTexture() {
        return net.vg.sagittary.util.ModIds.of("item/component_arrow");
    }
    
    /**
     * Clears the texture cache (useful for resource pack reloads)
     */
    public static void clearCache() {
        TEXTURE_CACHE.clear();
    }
}
