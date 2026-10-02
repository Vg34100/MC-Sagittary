package net.vg.sagittary.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
//? if >=26.1 {
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
//? } else {
/*import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
*///? }
import net.minecraft.core.HolderLookup;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.storage.loot.LootPool;
//import net.minecraft.world.level.storage.loot.LootTable;
//import net.minecraft.world.level.storage.loot.entries.LootItem;
//import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
//import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
//import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
//import net.vg.sagittary.registry.ObjectRegistry;
//import net.vg.sagittary.registry.WildCropConfig;

import java.util.concurrent.CompletableFuture;

//? if >=26.1 {
public class ModLootProvider extends FabricBlockLootSubProvider {
//? } else {
/*public class ModLootProvider extends FabricBlockLootTableProvider {
*///? }
    protected ModLootProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // EXAMPLE: Wild Carrot (Single)
        //    this.add(ObjectRegistry.WILD_CARROTS.get(),
        //            createShearsDispatchTable(ObjectRegistry.WILD_CARROTS.get(),
        //                    applyExplosionDecay(ObjectRegistry.WILD_CARROTS.get(),
        //                            LootItem.lootTableItem(Items.CARROT))));

        // Generate loot tables for all registered wild crops
//        for (var wildCrop : ObjectRegistry.WILD_CROPS) {
//            String blockName = wildCrop.getId().getPath();
//            Item dropItem = ObjectRegistry.WILD_CROP_DROPS.get(blockName);
//
//            this.add(wildCrop.get(),
//                    createShearsDispatchTable(wildCrop.get(),
//                            applyExplosionDecay(wildCrop.get(),
//                                    LootItem.lootTableItem(dropItem))));
//        }
//        // Generate loot tables for all registered wild crops
//        for (var wildCrop : ObjectRegistry.WILD_CROPS) {
//            String blockName = wildCrop.getId().getPath();
//
//            // Get the crop config
//            WildCropConfig config = ObjectRegistry.WILD_CROP_CONFIGS.get(blockName);
//
//            // Create the loot table based on the configuration
//            if (config.hasSeed()) {
//                // For crops with seeds (wheat, beetroot)
//                createCropWithSeedLootTable(wildCrop.get(), config);
//            } else {
//                // For crops without seeds (carrots, potatoes)
//                createSimpleCropLootTable(wildCrop.get(), config);
//            }
//        }
//
//    }
//
//    private void createSimpleCropLootTable(Block block, WildCropConfig config) {
//        // Create loot table for crops without seeds
//        // Create the base entry for the crop drop
//        LootItem.Builder<?> cropDropBuilder = LootItem.lootTableItem(config.getDropItem());
//
//        // Apply count function if we have a range
//        if (!config.getCropRange().isFixed()) {
//            cropDropBuilder.apply(SetItemCountFunction.setCount(
//                    UniformGenerator.between(
//                            (float) config.getCropRange().getMin(),
//                            (float) config.getCropRange().getMax()
//                    )
//            ));
//        }
//
//        // Use the existing shearsDispatchTable helper method
//        this.add(block, createShearsDispatchTable(
//                block,
//                applyExplosionDecay(block, cropDropBuilder)
//        ));
//    }
//
//    private void createCropWithSeedLootTable(Block block, WildCropConfig config) {
//        // First pool: Either block (with shears) or crop item
//        LootPool.Builder mainPool = LootPool.lootPool()
//                .setRolls(ConstantValue.exactly(1.0F))
//                .add(
//                        // When using shears, get the block itself
//                        LootItem.lootTableItem(block)
//                                .when(hasShears())
//                                .otherwise(
//                                        // Crop drop with count function if needed
//                                        createCountedItemBuilder(
//                                                config.getDropItem(),
//                                                config.getCropRange().getMin(),
//                                                config.getCropRange().getMax()
//                                        )
//                                )
//                );
//
//        // Create the base loot table with the main pool
//        LootTable.Builder lootTable = LootTable.lootTable().withPool(mainPool);
//
//        // Add seed pool only if we have seeds
//        if (config.hasSeed()) {
//            // Create a condition that is true when NOT using shears
//
//            LootItem.Builder<?> seedBuilder = LootItem.lootTableItem(config.getSeedItem())
//                    .when(hasShears().invert()); // This inverts the condition
//
//            // Apply count function if range is specified
//            if (!config.getSeedRange().isFixed()) {
//                seedBuilder.apply(SetItemCountFunction.setCount(
//                        UniformGenerator.between(
//                                (float) config.getSeedRange().getMin(),
//                                (float) config.getSeedRange().getMax()
//                        )
//                ));
//            }
//
//            // Apply explosion decay
//            seedBuilder = applyExplosionDecay(config.getSeedItem(), seedBuilder);
//
//            // Create the seed pool
//            LootPool.Builder seedPool = LootPool.lootPool()
//                    .setRolls(ConstantValue.exactly(1.0F))
//                    .add(seedBuilder);
//
//            // Add seed pool to the loot table
//            lootTable.withPool(seedPool);
//        }
//
//        this.add(block, lootTable);
//    }
//
//    private LootItem.Builder<?> createCountedItemBuilder(Item item, int min, int max) {
//        LootItem.Builder<?> builder = LootItem.lootTableItem(item);
//
//        if (min != max) {
//            builder.apply(SetItemCountFunction.setCount(
//                    UniformGenerator.between((float) min, (float) max)
//            ));
//        }
//
//        return applyExplosionDecay(item, builder);
    }
}
