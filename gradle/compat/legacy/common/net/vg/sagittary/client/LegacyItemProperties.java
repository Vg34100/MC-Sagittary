package net.vg.sagittary.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.vg.sagittary.item.QuiverItem;
import net.vg.sagittary.registry.ObjectRegistry;

import java.util.List;

/** Predicates backing the model overrides derived from modern item definitions. */
public final class LegacyItemProperties {
    private LegacyItemProperties() {}

    public static void register() {
        for (var item : List.of(ObjectRegistry.IRON_BOW_ITEM.get(), ObjectRegistry.COMPOUND_BOW_ITEM.get())) {
            ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pull"),
                    (stack, level, entity, seed) -> entity != null && entity.getUseItem() == stack
                            ? (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F : 0);
            ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pulling"),
                    (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0);
        }
        for (var item : List.of(ObjectRegistry.IRON_CROSSBOW_ITEM.get(), ObjectRegistry.REPEATER_CROSSBOW_ITEM.get())) {
            ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pull"),
                    (stack, level, entity, seed) -> entity != null && entity.getUseItem() == stack && !CrossbowItem.isCharged(stack)
                            ? (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / (float) CrossbowItem.getChargeDuration(stack, entity) : 0);
            ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pulling"),
                    (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack && !CrossbowItem.isCharged(stack) ? 1 : 0);
            ItemProperties.register(item, ResourceLocation.withDefaultNamespace("charged"),
                    (stack, level, entity, seed) -> CrossbowItem.isCharged(stack) ? 1 : 0);
            ItemProperties.register(item, ResourceLocation.withDefaultNamespace("firework"),
                    (stack, level, entity, seed) -> stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY)
                            .contains(Items.FIREWORK_ROCKET) ? 1 : 0);
        }
        for (var item : List.of(ObjectRegistry.QUIVER_ITEM.get(), ObjectRegistry.HUNTER_QUIVER_ITEM.get(), ObjectRegistry.RANGER_QUIVER_ITEM.get())) {
            ItemProperties.register(item, ResourceLocation.fromNamespaceAndPath("sagittary", "fullness"),
                    (stack, level, entity, seed) -> {
                        var contents = stack.get(DataComponents.BUNDLE_CONTENTS);
                        return contents == null ? 0 : (float) QuiverItem.getTotalArrowCount(contents) / QuiverItem.getCapacity(stack);
                    });
        }
    }
}
