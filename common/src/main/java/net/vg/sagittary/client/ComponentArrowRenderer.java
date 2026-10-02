package net.vg.sagittary.client;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;
import net.vg.sagittary.entity.ComponentArrowEntity;

public class ComponentArrowRenderer extends ArrowRenderer<ComponentArrowEntity, ArrowRenderState> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/projectiles/arrow.png");

    public ComponentArrowRenderer(EntityRendererProvider.Context context) { super(context); }

    @Override
    public Identifier getTextureLocation(ArrowRenderState state) { return TEXTURE; }

    @Override
    public ArrowRenderState createRenderState() { return new ArrowRenderState(); }
}
