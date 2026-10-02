package net.vg.sagittary.client;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.vg.sagittary.entity.ComponentArrowEntity;

/** Pre-render-state entity renderer. */
public class ComponentArrowRenderer extends ArrowRenderer<ComponentArrowEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");

    public ComponentArrowRenderer(EntityRendererProvider.Context context) { super(context); }

    @Override
    public ResourceLocation getTextureLocation(ComponentArrowEntity entity) { return TEXTURE; }
}
