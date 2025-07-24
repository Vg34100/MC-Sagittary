package net.vg.amethyst.fabric.data;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.vg.amethyst.registry.ObjectRegistry;

import java.util.List;

public  class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        //  EXAMPLE: Wild Carrot (Single)
        //    // Create texture mappings for the three crop stages
        //    TextureMapping carrotsStage1 = TextureMapping.crop(
        //            ResourceLocation.fromNamespaceAndPath("wildcrops", "block/wild_carrots_1"));
        //    TextureMapping carrotsStage2 = TextureMapping.crop(
        //            ResourceLocation.fromNamespaceAndPath("wildcrops", "block/wild_carrots_2"));
        //    TextureMapping carrotsStage3 = TextureMapping.crop(
        //            ResourceLocation.fromNamespaceAndPath("wildcrops", "block/wild_carrots_3"));
        //
        //    // Create resource locations for the models
        //    ResourceLocation carrotsModel1 = ResourceLocation.fromNamespaceAndPath("wildcrops", "block/wild_carrots_1");
        //    ResourceLocation carrotsModel2 = ResourceLocation.fromNamespaceAndPath("wildcrops", "block/wild_carrots_2");
        //    ResourceLocation carrotsModel3 = ResourceLocation.fromNamespaceAndPath("wildcrops", "block/wild_carrots_3");
        //
        //    // Generate the actual models
        //    ModelTemplates.CROP.create(carrotsModel1, carrotsStage1, blockModelGenerators.modelOutput);
        //    ModelTemplates.CROP.create(carrotsModel2, carrotsStage2, blockModelGenerators.modelOutput);
        //    ModelTemplates.CROP.create(carrotsModel3, carrotsStage3, blockModelGenerators.modelOutput);
        //
        //    // Create variants for block state
        //    Variant variant1 = new Variant(carrotsModel1);
        //    Variant variant2 = new Variant(carrotsModel2);
        //    Variant variant3 = new Variant(carrotsModel3);
        //
        //    // Create weighted list of variants
        //    List<Weighted<Variant>> weightedVariants = List.of(
        //            new Weighted<>(variant1, 1),
        //            new Weighted<>(variant2, 1),
        //            new Weighted<>(variant3, 1)
        //    );
        //
        //    // Use the static factory method to create the WeightedList
        //    WeightedList<Variant> variantList = WeightedList.of(weightedVariants);
        //
        //    // Create MultiVariant with weighted variants
        //    MultiVariant multiVariant = new MultiVariant(variantList);
        //
        //    // Register the block state
        //    blockModelGenerators.blockStateOutput.accept(
        //            MultiVariantGenerator.dispatch(ObjectRegistry.WILD_CARROTS.get(), multiVariant)
        //    );

        // Generate models for all registered wild crops
//        for (var crop : ObjectRegistry.WILD_CROPS) {
//            String cropName = crop.getId().getPath();
//            generateWildCropModels(blockModelGenerators, cropName);
//        }
    }

//    private void generateWildCropModels(BlockModelGenerators blockModelGenerators, String cropName) {
//        // Create texture mappings for the three crop stages
//        TextureMapping stage1 = TextureMapping.crop(
//                ResourceLocation.fromNamespaceAndPath("wildcrops", "block/" + cropName + "_1"));
//        TextureMapping stage2 = TextureMapping.crop(
//                ResourceLocation.fromNamespaceAndPath("wildcrops", "block/" + cropName + "_2"));
//        TextureMapping stage3 = TextureMapping.crop(
//                ResourceLocation.fromNamespaceAndPath("wildcrops", "block/" + cropName + "_3"));
//
//        // Create resource locations for the models
//        ResourceLocation model1 = ResourceLocation.fromNamespaceAndPath("wildcrops", "block/" + cropName + "_1");
//        ResourceLocation model2 = ResourceLocation.fromNamespaceAndPath("wildcrops", "block/" + cropName + "_2");
//        ResourceLocation model3 = ResourceLocation.fromNamespaceAndPath("wildcrops", "block/" + cropName + "_3");
//
//        // Generate the actual models
//        ModelTemplates.CROP.create(model1, stage1, blockModelGenerators.modelOutput);
//        ModelTemplates.CROP.create(model2, stage2, blockModelGenerators.modelOutput);
//        ModelTemplates.CROP.create(model3, stage3, blockModelGenerators.modelOutput);
//
//        // Create variants for block state
//        Variant variant1 = new Variant(model1);
//        Variant variant2 = new Variant(model2);
//        Variant variant3 = new Variant(model3);
//
//        // Create a weighted list of variants
//        List<Weighted<Variant>> weightedVariants = List.of(
//                new Weighted<>(variant1, 1),
//                new Weighted<>(variant2, 1),
//                new Weighted<>(variant3, 1)
//        );
//
//        WeightedList<Variant> variantList = WeightedList.of(weightedVariants);
//
//        // Create MultiVariant with weighted variants
//        MultiVariant multiVariant = new MultiVariant(variantList);
//
//        // Get the block from registry
//        Block block = ObjectRegistry.WILD_CROPS.stream()
//                .filter(b -> b.getId().getPath().equals(cropName))
//                .findFirst()
//                .orElseThrow()
//                .get();
//
//        // Register the block state
//        blockModelGenerators.blockStateOutput.accept(
//                MultiVariantGenerator.dispatch(block, multiVariant)
//        );
//    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        // EXAMPLE: Wild Carrot (Single)
        //    itemModelGenerators.itemModelOutput.accept(
        //            ObjectRegistry.WILD_CARROTS.get().asItem(),
        //            ItemModelUtils.plainModel(
        //                    ModelTemplates.FLAT_ITEM.create(
        //                            ModelLocationUtils.getModelLocation(ObjectRegistry.WILD_CARROTS.get().asItem()),
        //                            TextureMapping.layer0(
        //                                    ResourceLocation.fromNamespaceAndPath("wildcrops", "block/wild_carrots_1")),
        //                            itemModelGenerators.modelOutput)
        //            )
        //    );
        itemModelGenerators.generateFlatItem(ObjectRegistry.AMETHYST_ARROW_ITEM.get().asItem(), ModelTemplates.FLAT_ITEM);

        // Generate item models for all registered wild crops
//        for (var crop : ObjectRegistry.WILD_CROPS) {
//            String cropName = crop.getId().getPath();
//
//            itemModelGenerators.itemModelOutput.accept(
//                    crop.get().asItem(),
//                    ItemModelUtils.plainModel(
//                            ModelTemplates.FLAT_ITEM.create(
//                                    ModelLocationUtils.getModelLocation(crop.get().asItem()),
//                                    TextureMapping.layer0(
//                                            ResourceLocation.fromNamespaceAndPath("wildcrops", "block/" + cropName + "_1")),
//                                    itemModelGenerators.modelOutput)
//                    )
//            );
//        }
    }
}

