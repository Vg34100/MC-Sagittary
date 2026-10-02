package net.vg.sagittary.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.vg.sagittary.client.SagittaryClient;
import net.vg.sagittary.client.TopazPulseRenderer;
//? if >=26.1 {
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//? } else {
/*import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
*///? }
//? if >=26.1 {
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
//? } else {
/*import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
*///? }

public final class SagittaryFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //? if >=26.1 {
        net.minecraft.client.KeyMapping.Category.register(SagittaryClient.QUIVER_CYCLE.getCategory().id());
        KeyMappingHelper.registerKeyMapping(SagittaryClient.QUIVER_CYCLE);
        //? } else {
        /*KeyBindingHelper.registerKeyBinding(SagittaryClient.QUIVER_CYCLE);
        *///? }
        SagittaryClient.init();
        //? if >=26.1 {
        LevelRenderEvents.BEFORE_GIZMOS.register(context -> TopazPulseRenderer.render());
        //? } else {
        /*WorldRenderEvents.LAST.register(context -> TopazPulseRenderer.render(context.matrixStack(), context.camera().getPosition()));
        *///? }
    }
}
