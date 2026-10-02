package net.vg.sagittary.util;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.vg.sagittary.Sagittary;
import net.vg.sagittary.registry.ObjectRegistry;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.vg.sagittary.registry.ObjectRegistry.ITEMS;
import static net.vg.sagittary.registry.ObjectRegistry.ITEM_REGISTRAR;

public class Util {
    public static Item.Properties itemProperties(ResourceKey<Item> key) {
        //? if >=26.1 {
        return new Item.Properties().setId(key);
        //? } else {
        /*return new Item.Properties();
        *///? }
    }

    //? if >=26.1 {
    public static net.minecraft.world.InteractionResult useResult(net.minecraft.world.InteractionResult result, net.minecraft.world.item.ItemStack stack) {
        return result;
    }
    //? } else {
    /*public static net.minecraft.world.InteractionResultHolder<net.minecraft.world.item.ItemStack> useResult(net.minecraft.world.InteractionResult result, net.minecraft.world.item.ItemStack stack) {
        return new net.minecraft.world.InteractionResultHolder<>(result, stack);
    }
    *///? }

    // Record to hold all item metadata
    public record ModItem(
            RegistrySupplier<Item> item,
            ModelTemplate modelTemplate,
            RecipeSystem.RecipeData recipe,
            String displayName
    ) {}

    // Helper method to create resource locations with the mod namespace
    public static Identifier createResource(String path) {
        return Identifier.fromNamespaceAndPath(Sagittary.MOD_ID, path);
    }

    // Helper method to create a ResourceKey for a block
    public static ResourceKey<Block> createBlockKey(String path) {
        Identifier resourceLocation = createResource(path);
        return ResourceKey.create(Registries.BLOCK, resourceLocation);
    }

    // Helper method to create a ResourceKey for an item
    public static ResourceKey<Item> createItemKey(String path) {
        Identifier resourceLocation = createResource(path);
        return ResourceKey.create(Registries.ITEM, resourceLocation);
    }

    // Helper method to register items with all their metadata
    public static ModItem registerModItem(String path, Supplier<Item> itemSupplier, ModelTemplate model, RecipeSystem.RecipeData recipe) {
        // Convert path to display name (e.g., "glazed_carrot" -> "Glazed Carrot")
        String displayName = Arrays.stream(path.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));

        // Create a ResourceLocation for the item
        Identifier resourceLocation = net.vg.sagittary.util.ModIds.of(path);

        // Create a ResourceKey for the item
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, resourceLocation);

        // The supplier passed in should already create an item with setId()
        // We don't need to modify it here - that's the responsibility of the caller
        ModItem modItem = new ModItem(
                registerItem(ITEMS, ITEM_REGISTRAR, resourceLocation, itemSupplier),
                model,
                recipe,
                displayName
        );

        ObjectRegistry.MOD_ITEMS.add(modItem);
        return modItem;
    }

// ===================================================
    public static <T extends Block> RegistrySupplier<T> registerWithItem(DeferredRegister<Block> registerB, Registrar<Block> registrarB, DeferredRegister<Item> registerI, Registrar<Item> registrarI, Identifier name, Supplier<T> block) {
        // Register the block
        RegistrySupplier<T> toReturn = registerWithoutItem(registerB, registrarB, name, block);

        // Create a ResourceKey for the block item
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, name);

        registerItem(registerI, registrarI, name, () -> {
            Item.Properties properties = itemProperties(itemKey);
            return new BlockItem(toReturn.get(), properties);
        });

        return toReturn;
    }

    public static <T extends Block> RegistrySupplier<T> registerWithoutItem(DeferredRegister<Block> register, Registrar<Block> registrar, Identifier path, Supplier<T> block) {
        if (Platform.isNeoForge()) {
            return register.register(path.getPath(), block);
        }
        return registrar.register(path, block);
    }

    public static <T extends Item> RegistrySupplier<T> registerItem(DeferredRegister<Item> register, Registrar<Item> registrar, Identifier path, Supplier<T> itemSupplier) {
        if (Platform.isNeoForge()) {
            return register.register(path.getPath(), itemSupplier);
        }
        return registrar.register(path, itemSupplier);
    }

    public static <T extends EntityType<?>> RegistrySupplier<T> registerEntity(DeferredRegister<EntityType<?>> register, Registrar<EntityType<?>> registrar, Identifier path, Supplier<T> entitySupplier) {
        if (Platform.isNeoForge()) {
            return register.register(path.getPath(), entitySupplier);
        }
        return registrar.register(path, entitySupplier);
    }
}
