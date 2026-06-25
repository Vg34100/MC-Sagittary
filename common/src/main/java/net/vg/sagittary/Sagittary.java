package net.vg.sagittary;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.vg.sagittary.component.ArrowComponent;
import net.vg.sagittary.item.ComponentArrowItem;
import net.vg.sagittary.loot.SagittaryLootModifier;
import net.vg.sagittary.registry.ObjectRegistry;

public final class Sagittary {
    public static final String MOD_ID = "sagittary";

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> SAGITTARY_TAB = TABS.register("sagittary_tab", () ->
            CreativeTabRegistry.create(
                    builder -> builder
                            .title(Component.translatable("itemGroup.sagittary"))
                            .icon(() -> ComponentArrowItem.createComponentArrow(
                                    ArrowComponent.AMETHYST_TIP,
                                    ArrowComponent.STICK_SHAFT,
                                    ArrowComponent.FEATHER_FLETCHING
                            ))
                            .displayItems((parameters, output) -> {
                                // Add quiver
                                output.accept(new ItemStack(ObjectRegistry.QUIVER_ITEM.get()));

                                // Add bows and crossbows
                                output.accept(new ItemStack(ObjectRegistry.IRON_BOW_ITEM.get()));
                                output.accept(new ItemStack(ObjectRegistry.COMPOUND_BOW_ITEM.get()));
                                output.accept(new ItemStack(ObjectRegistry.IRON_CROSSBOW_ITEM.get()));
                                output.accept(new ItemStack(ObjectRegistry.REPEATER_CROSSBOW_ITEM.get()));

                                // Add component arrows with different tips
                                output.accept(ComponentArrowItem.createComponentArrow(
                                        ArrowComponent.FLINT_TIP, ArrowComponent.STICK_SHAFT, ArrowComponent.FEATHER_FLETCHING));
                                output.accept(ComponentArrowItem.createComponentArrow(
                                        ArrowComponent.AMETHYST_TIP, ArrowComponent.STICK_SHAFT, ArrowComponent.FEATHER_FLETCHING));
                                output.accept(ComponentArrowItem.createComponentArrow(
                                        ArrowComponent.COPPER_TIP, ArrowComponent.STICK_SHAFT, ArrowComponent.FEATHER_FLETCHING));
                                output.accept(ComponentArrowItem.createComponentArrow(
                                        ArrowComponent.SLIME_TIP, ArrowComponent.STICK_SHAFT, ArrowComponent.FEATHER_FLETCHING));
                                output.accept(ComponentArrowItem.createComponentArrow(
                                        ArrowComponent.GLOWSTONE_TIP, ArrowComponent.STICK_SHAFT, ArrowComponent.FEATHER_FLETCHING));
                                output.accept(ComponentArrowItem.createComponentArrow(
                                        ArrowComponent.ECHO_SHARD_TIP, ArrowComponent.STICK_SHAFT, ArrowComponent.FEATHER_FLETCHING));
                                output.accept(ComponentArrowItem.createComponentArrow(
                                        ArrowComponent.ENDER_PEARL_TIP, ArrowComponent.STICK_SHAFT, ArrowComponent.FEATHER_FLETCHING));
                                output.accept(ComponentArrowItem.createComponentArrow(
                                        ArrowComponent.IRON_TIP, ArrowComponent.STICK_SHAFT, ArrowComponent.FEATHER_FLETCHING));
                                output.accept(ComponentArrowItem.createComponentArrow(
                                        ArrowComponent.GOLD_TIP, ArrowComponent.STICK_SHAFT, ArrowComponent.FEATHER_FLETCHING));
                                output.accept(ComponentArrowItem.createComponentArrow(
                                        ArrowComponent.DIAMOND_TIP, ArrowComponent.STICK_SHAFT, ArrowComponent.FEATHER_FLETCHING));

                                // A few interesting combinations
                                output.accept(ComponentArrowItem.createComponentArrow(
                                        ArrowComponent.DIAMOND_TIP, ArrowComponent.BREEZE_ROD_SHAFT, ArrowComponent.PHANTOM_MEMBRANE_FLETCHING));
                                output.accept(ComponentArrowItem.createComponentArrow(
                                        ArrowComponent.ECHO_SHARD_TIP, ArrowComponent.BONE_SHAFT, ArrowComponent.FEATHER_FLETCHING));
                            })
            )
    );

    public static void init() {
        ObjectRegistry.init();
        TABS.register();
        SagittaryLootModifier.init();
    }
}
