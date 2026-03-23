package net.vg.amethyst.client;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.ResourceLocation;
import net.vg.amethyst.entity.AmethystArrowEntity;
import net.vg.amethyst.entity.ComponentArrowEntity;
import net.vg.amethyst.registry.ObjectRegistry;
import net.vg.amethyst.screen.FletchingTableScreen;

public class AmethystClient {
    public static void init() {
        initEntityRenderers();
        initScreens();
        initItemRenderers();

    }
    
    public static void initItemRenderers() {
        // Component arrow textures are handled via JSON model system with CompositeModel
        // No additional registration needed - models are loaded from JSON
    }
    
    public static void initEntityRenderers() {
        EntityRendererRegistry.register(ObjectRegistry.AMETHYST_ARROW_ENTITY, AmethystArrowRenderer::new);
        EntityRendererRegistry.register(ObjectRegistry.COMPONENT_ARROW_ENTITY, ComponentArrowRenderer::new);
    }
    
    public static void initScreens() {
        MenuRegistry.registerScreenFactory(ObjectRegistry.FLETCHING_TABLE_MENU_TYPE.get(), FletchingTableScreen::new);
    }
    
    public static class AmethystArrowRenderer extends ArrowRenderer<AmethystArrowEntity, ArrowRenderState> {
        public static final ResourceLocation ARROW_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");
        
        public AmethystArrowRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public ResourceLocation getTextureLocation(ArrowRenderState renderState) {
            return ARROW_LOCATION;
        }

        @Override
        public ArrowRenderState createRenderState() {
            return new ArrowRenderState();
        }
    }
    
    public static class ComponentArrowRenderer extends ArrowRenderer<ComponentArrowEntity, ArrowRenderState> {
        public static final ResourceLocation ARROW_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");
        
        public ComponentArrowRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public ResourceLocation getTextureLocation(ArrowRenderState renderState) {
            return ARROW_LOCATION;
        }

        @Override
        public ArrowRenderState createRenderState() {
            return new ArrowRenderState();
        }
    }
}
