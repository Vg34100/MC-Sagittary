package net.vg.sagittary.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.vg.sagittary.Sagittary;
import net.vg.sagittary.component.ArrowParts;
import net.vg.sagittary.component.QuiverSelection;
import net.vg.sagittary.dispenser.ComponentArrowDispenseBehavior;
import net.vg.sagittary.entity.ComponentArrowEntity;
import net.vg.sagittary.item.ComponentArrowItem;
import net.vg.sagittary.item.QuiverItem;
import net.vg.sagittary.item.QuiverTier;
import net.vg.sagittary.compat.trinkets.TrinketsCompat;
import net.vg.sagittary.item.IronBowItem;
import net.vg.sagittary.item.IronCrossbowItem;
import net.vg.sagittary.item.CompoundBowItem;
import net.vg.sagittary.item.RepeaterCrossbowItem;
import net.vg.sagittary.menu.FletchingTableMenu;
import net.vg.sagittary.util.ModIds;
import net.vg.sagittary.util.RecipeSystem;
import net.vg.sagittary.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ObjectRegistry {
    public static final List<Util.ModItem> MOD_ITEMS = new ArrayList<>();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Sagittary.MOD_ID, Registries.ITEM);
    public static final Registrar<Item> ITEM_REGISTRAR = ITEMS.getRegistrar();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Sagittary.MOD_ID, Registries.BLOCK);
    public static final Registrar<Block> BLOCK_REGISTRAR = BLOCKS.getRegistrar();
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Sagittary.MOD_ID, Registries.ENTITY_TYPE);
    public static final Registrar<EntityType<?>> ENTITY_REGISTRAR = ENTITIES.getRegistrar();
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Sagittary.MOD_ID, Registries.MENU);
    public static final Registrar<MenuType<?>> MENU_TYPE_REGISTRAR = MENU_TYPES.getRegistrar();
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Sagittary.MOD_ID, Registries.DATA_COMPONENT_TYPE);
    public static final Registrar<DataComponentType<?>> DATA_COMPONENT_TYPE_REGISTRAR = DATA_COMPONENT_TYPES.getRegistrar();

    // REMOVED: Amethyst arrow is now part of the component system, not a separate item
    // public static final RegistrySupplier<Item> AMETHYST_ARROW_ITEM = Util.registerItem(ITEMS, ITEM_REGISTRAR, ModIds.of("amethyst_arrow"), () -> {
    //     return new AmethystArrowItem(Util.itemProperties(Util.createItemKey("amethyst_arrow")));
    // });
    
    public static final RegistrySupplier<Item> COMPONENT_ARROW_ITEM = Util.registerItem(ITEMS, ITEM_REGISTRAR, ModIds.of("component_arrow"), () -> {
        return new ComponentArrowItem(Util.itemProperties(Util.createItemKey("component_arrow")));
    });

    public static final RegistrySupplier<Item> QUIVER_ITEM = Util.registerItem(ITEMS, ITEM_REGISTRAR, ModIds.of("quiver"), () -> {
        return new QuiverItem(TrinketsCompat.makeBackEquippable(Util.itemProperties(Util.createItemKey("quiver"))), QuiverTier.BASIC);
    });

    public static final RegistrySupplier<Item> HUNTER_QUIVER_ITEM = Util.registerItem(ITEMS, ITEM_REGISTRAR, ModIds.of("hunter_quiver"), () ->
        new QuiverItem(TrinketsCompat.makeBackEquippable(Util.itemProperties(Util.createItemKey("hunter_quiver"))), QuiverTier.HUNTER));

    public static final RegistrySupplier<Item> RANGER_QUIVER_ITEM = Util.registerItem(ITEMS, ITEM_REGISTRAR, ModIds.of("ranger_quiver"), () ->
        new QuiverItem(TrinketsCompat.makeBackEquippable(Util.itemProperties(Util.createItemKey("ranger_quiver"))), QuiverTier.RANGER));

    public static final RegistrySupplier<Item> HUNTER_QUIVER_UPGRADE = Util.registerItem(ITEMS, ITEM_REGISTRAR, ModIds.of("hunter_quiver_upgrade"), () ->
        new Item(Util.itemProperties(Util.createItemKey("hunter_quiver_upgrade"))));

    public static final RegistrySupplier<Item> RANGER_QUIVER_UPGRADE = Util.registerItem(ITEMS, ITEM_REGISTRAR, ModIds.of("ranger_quiver_upgrade"), () ->
        new Item(Util.itemProperties(Util.createItemKey("ranger_quiver_upgrade"))));

    public static final RegistrySupplier<Item> IRON_BOW_ITEM = Util.registerItem(ITEMS, ITEM_REGISTRAR, ModIds.of("iron_bow"), () -> {
        return new IronBowItem(Util.itemProperties(Util.createItemKey("iron_bow")).durability(576));
    });

    public static final RegistrySupplier<Item> IRON_CROSSBOW_ITEM = Util.registerItem(ITEMS, ITEM_REGISTRAR, ModIds.of("iron_crossbow"), () -> {
        return new IronCrossbowItem(Util.itemProperties(Util.createItemKey("iron_crossbow")).durability(652));
    });

    public static final RegistrySupplier<Item> COMPOUND_BOW_ITEM = Util.registerItem(ITEMS, ITEM_REGISTRAR, ModIds.of("compound_bow"), () -> {
        return new CompoundBowItem(Util.itemProperties(Util.createItemKey("compound_bow")).durability(500));
    });

    public static final RegistrySupplier<Item> REPEATER_CROSSBOW_ITEM = Util.registerItem(ITEMS, ITEM_REGISTRAR, ModIds.of("repeater_crossbow"), () -> {
        return new RepeaterCrossbowItem(Util.itemProperties(Util.createItemKey("repeater_crossbow")).durability(400));
    });

    // REMOVED: Amethyst arrow entity is now part of the component system
    // public static final RegistrySupplier<EntityType<AmethystArrowEntity>> AMETHYST_ARROW_ENTITY = Util.registerEntity(ENTITIES, ENTITY_REGISTRAR, ModIds.of("amethyst_arrow"), () ->
    //     EntityType.Builder.<AmethystArrowEntity>of(AmethystArrowEntity::new, MobCategory.MISC)
    //         .sized(0.5F, 0.5F)
    //         .clientTrackingRange(4)
    //         .updateInterval(20)
    //         .build(ResourceKey.create(Registries.ENTITY_TYPE, ModIds.of("amethyst_arrow"))));
            
    public static final RegistrySupplier<EntityType<ComponentArrowEntity>> COMPONENT_ARROW_ENTITY = Util.registerEntity(ENTITIES, ENTITY_REGISTRAR, ModIds.of("component_arrow"), () ->
        EntityType.Builder.<ComponentArrowEntity>of(ComponentArrowEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            //? if >=26.1 {
            .build(ResourceKey.create(Registries.ENTITY_TYPE, ModIds.of("component_arrow"))));
            //? } else {
            /*// This parameter only checks a DFU schema; the registry supplier
            // above still owns sagittary:component_arrow's identity.
            .build("minecraft:arrow"));
            *///? }
    
    public static final RegistrySupplier<MenuType<FletchingTableMenu>> FLETCHING_TABLE_MENU_TYPE = MENU_TYPES.register(ModIds.of("fletching_table"), () ->
        new MenuType<>(FletchingTableMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    
    public static final RegistrySupplier<DataComponentType<ArrowParts>> ARROW_PARTS = DATA_COMPONENT_TYPES.register(ModIds.of("arrow_parts"), () ->
        DataComponentType.<ArrowParts>builder()
            .persistent(ArrowParts.CODEC)
            .networkSynchronized(ArrowParts.STREAM_CODEC)
            .build());

    // A count-one arrow prototype identifies the selected type independently of
    // bundle order, stack splitting, and vanilla's transient bundle selection.
    public static final RegistrySupplier<DataComponentType<QuiverSelection>> QUIVER_SELECTION = DATA_COMPONENT_TYPES.register(ModIds.of("quiver_selection"), () ->
        DataComponentType.<QuiverSelection>builder()
            .persistent(QuiverSelection.CODEC)
            .networkSynchronized(QuiverSelection.STREAM_CODEC)
            .build());

    static {
        // REMOVED: Amethyst arrow is now part of the component system
        // RecipeSystem.RecipeData amethystArrowRecipe = RecipeSystem.RecipeData.shaped(
        //     RecipeCategory.COMBAT,
        //     4,
        //     new String[]{"X", "Y", "Z"},
        //     java.util.Map.of(
        //         'X', Items.AMETHYST_SHARD,
        //         'Y', Items.STICK,
        //         'Z', Items.FEATHER
        //     )
        // );
        //
        // Util.ModItem amethystArrowModItem = new Util.ModItem(
        //     AMETHYST_ARROW_ITEM,
        //     null,
        //     amethystArrowRecipe,
        //     "Amethyst Arrow"
        // );
        // MOD_ITEMS.add(amethystArrowModItem);

        // Component arrow - no recipe (crafted through fletching table)
        Util.ModItem componentArrowModItem = new Util.ModItem(
            COMPONENT_ARROW_ITEM,
            null,
            null,
            "Component Arrow"
        );
        MOD_ITEMS.add(componentArrowModItem);

        // Quiver - stores arrows like a bundle
        RecipeSystem.RecipeData quiverRecipe = RecipeSystem.RecipeData.shaped(
            RecipeCategory.COMBAT,
            1,
            new String[]{" LS", "L L", "LL "},
            java.util.Map.of(
                'L', Items.LEATHER,
                'S', Items.STRING
            )
        );

        Util.ModItem quiverModItem = new Util.ModItem(
            QUIVER_ITEM,
            null,
            quiverRecipe,
            "Quiver"
        );
        MOD_ITEMS.add(quiverModItem);

        // Iron Bow - more durable bow
        RecipeSystem.RecipeData ironBowRecipe = RecipeSystem.RecipeData.shaped(
            RecipeCategory.COMBAT,
            1,
            new String[]{" IS", "I S", " IS"},
            java.util.Map.of(
                'I', Items.IRON_INGOT,
                'S', Items.STRING
            )
        );
        MOD_ITEMS.add(new Util.ModItem(IRON_BOW_ITEM, null, ironBowRecipe, "Iron Bow"));

        // Iron Crossbow - more durable crossbow
        RecipeSystem.RecipeData ironCrossbowRecipe = RecipeSystem.RecipeData.shaped(
            RecipeCategory.COMBAT,
            1,
            new String[]{"IBI", "STS", " I "},
            java.util.Map.of(
                'I', Items.IRON_INGOT,
                'B', Items.BOW,
                'S', Items.STRING,
                'T', Items.TRIPWIRE_HOOK
            )
        );
        MOD_ITEMS.add(new Util.ModItem(IRON_CROSSBOW_ITEM, null, ironCrossbowRecipe, "Iron Crossbow"));

        // Compound Bow - shoots 3 arrows
        RecipeSystem.RecipeData compoundBowRecipe = RecipeSystem.RecipeData.shaped(
            RecipeCategory.COMBAT,
            1,
            new String[]{" BS", "BIB", " BS"},
            java.util.Map.of(
                'B', Items.BLAZE_ROD,
                'I', Items.IRON_INGOT,
                'S', Items.STRING
            )
        );
        MOD_ITEMS.add(new Util.ModItem(COMPOUND_BOW_ITEM, null, compoundBowRecipe, "Compound Bow"));

        // Repeater Crossbow - rapid fire
        RecipeSystem.RecipeData repeaterCrossbowRecipe = RecipeSystem.RecipeData.shaped(
            RecipeCategory.COMBAT,
            1,
            new String[]{"SCS", "RIR", " R "},
            java.util.Map.of(
                'C', Items.CROSSBOW,
                'R', Items.REDSTONE,
                'I', Items.IRON_INGOT,
                'S', Items.STRING
            )
        );
        MOD_ITEMS.add(new Util.ModItem(REPEATER_CROSSBOW_ITEM, null, repeaterCrossbowRecipe, "Repeater Crossbow"));
    }

    public static void init() {
        ITEMS.register();
        BLOCKS.register();
        ENTITIES.register();
        MENU_TYPES.register();
        DATA_COMPONENT_TYPES.register();
    }
    
    public static void registerDispenserBehaviors() {
        // REMOVED: Amethyst arrow is now part of the component system
        // DispenserBlock.registerProjectileBehavior(AMETHYST_ARROW_ITEM.get());

        // Register custom dispenser behavior for component arrows to preserve NBT data
        DispenserBlock.registerBehavior(COMPONENT_ARROW_ITEM.get(), new ComponentArrowDispenseBehavior((ComponentArrowItem) COMPONENT_ARROW_ITEM.get()));
    }

    public static <T extends Block> RegistrySupplier<T> registerWithItem(String name, Supplier<T> block) {
        return Util.registerWithItem(BLOCKS, BLOCK_REGISTRAR, ITEMS, ITEM_REGISTRAR, ModIds.of(name), block);
    }

}
