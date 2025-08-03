package net.vg.amethyst.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.vg.amethyst.Amethyst;
import net.vg.amethyst.component.ArrowParts;
import net.vg.amethyst.dispenser.ComponentArrowDispenseBehavior;
import net.vg.amethyst.entity.AmethystArrowEntity;
import net.vg.amethyst.entity.ComponentArrowEntity;
import net.vg.amethyst.item.AmethystArrowItem;
import net.vg.amethyst.item.ComponentArrowItem;
import net.vg.amethyst.menu.FletchingTableMenu;
import net.vg.amethyst.util.Identifier;
import net.vg.amethyst.util.RecipeSystem;
import net.vg.amethyst.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ObjectRegistry {
    public static final List<Util.ModItem> MOD_ITEMS = new ArrayList<>();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Amethyst.MOD_ID, Registries.ITEM);
    public static final Registrar<Item> ITEM_REGISTRAR = ITEMS.getRegistrar();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Amethyst.MOD_ID, Registries.BLOCK);
    public static final Registrar<Block> BLOCK_REGISTRAR = BLOCKS.getRegistrar();
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Amethyst.MOD_ID, Registries.ENTITY_TYPE);
    public static final Registrar<EntityType<?>> ENTITY_REGISTRAR = ENTITIES.getRegistrar();
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Amethyst.MOD_ID, Registries.MENU);
    public static final Registrar<MenuType<?>> MENU_TYPE_REGISTRAR = MENU_TYPES.getRegistrar();
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Amethyst.MOD_ID, Registries.DATA_COMPONENT_TYPE);
    public static final Registrar<DataComponentType<?>> DATA_COMPONENT_TYPE_REGISTRAR = DATA_COMPONENT_TYPES.getRegistrar();

    public static final RegistrySupplier<Item> AMETHYST_ARROW_ITEM = Util.registerItem(ITEMS, ITEM_REGISTRAR, Identifier.of("amethyst_arrow"), () -> {
        ResourceKey<Item> itemKey = Util.createItemKey("amethyst_arrow");
        return new AmethystArrowItem(new Item.Properties().setId(itemKey));
    });
    
    public static final RegistrySupplier<Item> COMPONENT_ARROW_ITEM = Util.registerItem(ITEMS, ITEM_REGISTRAR, Identifier.of("component_arrow"), () -> {
        ResourceKey<Item> itemKey = Util.createItemKey("component_arrow");
        return new ComponentArrowItem(new Item.Properties().setId(itemKey));
    });
    
    public static final RegistrySupplier<EntityType<AmethystArrowEntity>> AMETHYST_ARROW_ENTITY = Util.registerEntity(ENTITIES, ENTITY_REGISTRAR, Identifier.of("amethyst_arrow"), () -> 
        EntityType.Builder.<AmethystArrowEntity>of(AmethystArrowEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.of("amethyst_arrow"))));
            
    public static final RegistrySupplier<EntityType<ComponentArrowEntity>> COMPONENT_ARROW_ENTITY = Util.registerEntity(ENTITIES, ENTITY_REGISTRAR, Identifier.of("component_arrow"), () -> 
        EntityType.Builder.<ComponentArrowEntity>of(ComponentArrowEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.of("component_arrow"))));
    
    public static final RegistrySupplier<MenuType<FletchingTableMenu>> FLETCHING_TABLE_MENU_TYPE = MENU_TYPES.register(Identifier.of("fletching_table"), () -> 
        new MenuType<>(FletchingTableMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    
    public static final RegistrySupplier<DataComponentType<ArrowParts>> ARROW_PARTS = DATA_COMPONENT_TYPES.register(Identifier.of("arrow_parts"), () ->
        DataComponentType.<ArrowParts>builder()
            .persistent(ArrowParts.CODEC)
            .networkSynchronized(ArrowParts.STREAM_CODEC)
            .build());

    static {
        RecipeSystem.RecipeData amethystArrowRecipe = RecipeSystem.RecipeData.shaped(
            RecipeCategory.COMBAT,
            4,
            new String[]{"X", "Y", "Z"},
            java.util.Map.of(
                'X', Items.AMETHYST_SHARD,
                'Y', Items.STICK,
                'Z', Items.FEATHER
            )
        );
        
        Util.ModItem amethystArrowModItem = new Util.ModItem(
            AMETHYST_ARROW_ITEM,
            null,
            amethystArrowRecipe,
            "Amethyst Arrow"
        );
        MOD_ITEMS.add(amethystArrowModItem);
        
        // Component arrow - no recipe (crafted through fletching table)
        Util.ModItem componentArrowModItem = new Util.ModItem(
            COMPONENT_ARROW_ITEM,
            null,
            null,
            "Component Arrow"
        );
        MOD_ITEMS.add(componentArrowModItem);
    }

    public static void init() {
        ITEMS.register();
        BLOCKS.register();
        ENTITIES.register();
        MENU_TYPES.register();
        DATA_COMPONENT_TYPES.register();
    }
    
    public static void registerDispenserBehaviors() {
        // Register dispenser behavior for amethyst arrows after registries are resolved
        DispenserBlock.registerProjectileBehavior(AMETHYST_ARROW_ITEM.get());
        
        // Register custom dispenser behavior for component arrows to preserve NBT data
        DispenserBlock.registerBehavior(COMPONENT_ARROW_ITEM.get(), new ComponentArrowDispenseBehavior((ComponentArrowItem) COMPONENT_ARROW_ITEM.get()));
    }

    public static <T extends Block> RegistrySupplier<T> registerWithItem(String name, Supplier<T> block) {
        return Util.registerWithItem(BLOCKS, BLOCK_REGISTRAR, ITEMS, ITEM_REGISTRAR, Identifier.of(name), block);
    }

}
