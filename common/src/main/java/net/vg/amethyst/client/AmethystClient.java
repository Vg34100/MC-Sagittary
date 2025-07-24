package net.vg.amethyst.client;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.ResourceLocation;
import net.vg.amethyst.entity.AmethystArrowEntity;
import net.vg.amethyst.registry.ObjectRegistry;

public class AmethystClient {
    public static void init() {
        EntityRendererRegistry.register(ObjectRegistry.AMETHYST_ARROW_ENTITY, AmethystArrowRenderer::new);
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
}
