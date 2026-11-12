package net.vg.sagittary.client;

import net.minecraft.client.renderer.item.ItemProperties;
//? if >=1.21 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ChargedProjectiles;
//? }
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.vg.sagittary.item.QuiverItem;
import net.vg.sagittary.registry.ObjectRegistry;

import java.util.List;

/** Predicates backing the model overrides derived from modern item definitions. */
public final class LegacyItemProperties {
    private LegacyItemProperties() {}

    public static void register() {
        for (var item : List.of(ObjectRegistry.IRON_BOW_ITEM.get(), ObjectRegistry.COMPOUND_BOW_ITEM.get())) {
            ItemProperties.register(item, net.vg.sagittary.util.ModIds.of("minecraft", "pull"),
                    (stack, level, entity, seed) -> entity != null && entity.getUseItem() == stack
                            ? (net.vg.sagittary.util.WeaponCompatibility.useDuration(stack, entity) - entity.getUseItemRemainingTicks()) / 20.0F : 0);
            ItemProperties.register(item, net.vg.sagittary.util.ModIds.of("minecraft", "pulling"),
                    (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0);
        }
        for (var item : List.of(ObjectRegistry.IRON_CROSSBOW_ITEM.get(), ObjectRegistry.REPEATER_CROSSBOW_ITEM.get())) {
            ItemProperties.register(item, net.vg.sagittary.util.ModIds.of("minecraft", "pull"),
                    (stack, level, entity, seed) -> entity != null && entity.getUseItem() == stack && !CrossbowItem.isCharged(stack)
                            ? (net.vg.sagittary.util.WeaponCompatibility.useDuration(stack, entity) - entity.getUseItemRemainingTicks()) / (float) chargeDuration(stack, entity) : 0);
            ItemProperties.register(item, net.vg.sagittary.util.ModIds.of("minecraft", "pulling"),
                    (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack && !CrossbowItem.isCharged(stack) ? 1 : 0);
            ItemProperties.register(item, net.vg.sagittary.util.ModIds.of("minecraft", "charged"),
                    (stack, level, entity, seed) -> CrossbowItem.isCharged(stack) ? 1 : 0);
            ItemProperties.register(item, net.vg.sagittary.util.ModIds.of("minecraft", "firework"),
                    //? if >=1.21 {
                    (stack, level, entity, seed) -> stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY)
                            .contains(Items.FIREWORK_ROCKET) ? 1 : 0);
                    //? } else {
                    /*(stack, level, entity, seed) -> CrossbowItem.containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 1 : 0);
                    *///? }
        }
        for (var item : List.of(ObjectRegistry.QUIVER_ITEM.get(), ObjectRegistry.HUNTER_QUIVER_ITEM.get(), ObjectRegistry.RANGER_QUIVER_ITEM.get())) {
            ItemProperties.register(item, net.vg.sagittary.util.ModIds.of("fullness"),
                    (stack, level, entity, seed) -> {
                        return (float) QuiverItem.getArrowStacks(stack).stream().mapToInt(net.minecraft.world.item.ItemStack::getCount).sum() / QuiverItem.getCapacity(stack);
                    });
        }
    }

    private static int chargeDuration(net.minecraft.world.item.ItemStack stack, net.minecraft.world.entity.LivingEntity entity) {
        //? if >=1.21 {
        return CrossbowItem.getChargeDuration(stack, entity);
        //? } else {
        /*return CrossbowItem.getChargeDuration(stack);
        *///? }
    }
}
