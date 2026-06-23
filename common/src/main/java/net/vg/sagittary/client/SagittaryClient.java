package net.vg.sagittary.client;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.gui.MenuScreenRegistry;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;
import net.vg.sagittary.entity.ComponentArrowEntity;
import net.vg.sagittary.registry.ObjectRegistry;
import net.vg.sagittary.screen.FletchingTableScreen;

public class SagittaryClient {
    public static void init() {
        initEntityRenderers();
        initScreens();
        initItemRenderers();

    }
    
    public static void initItemRenderers() {
        // Item appearance is driven by custom_model_data on 1.21.1.
    }
    
    public static void initEntityRenderers() {
        // REMOVED: Amethyst arrow is now part of the component system
        // EntityRendererRegistry.register(ObjectRegistry.AMETHYST_ARROW_ENTITY, AmethystArrowRenderer::new);
        EntityRendererRegistry.register(ObjectRegistry.COMPONENT_ARROW_ENTITY, ComponentArrowRenderer::new);
    }
    
    public static void initScreens() {
        MenuScreenRegistry.registerScreenFactory(ObjectRegistry.FLETCHING_TABLE_MENU_TYPE.get(), FletchingTableScreen::new);
    }
    
    // REMOVED: AmethystArrowRenderer - amethyst arrow is now part of the component system

    public static class ComponentArrowRenderer extends ArrowRenderer<ComponentArrowEntity, ArrowRenderState> {
        public static final Identifier ARROW_LOCATION = Identifier.withDefaultNamespace("textures/entity/projectiles/arrow.png");
        
        public ComponentArrowRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public Identifier getTextureLocation(ArrowRenderState renderState) {
            return ARROW_LOCATION;
        }

        @Override
        public ArrowRenderState createRenderState() {
            return new ArrowRenderState();
        }
    }
}
