package net.vg.amethyst.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.vg.amethyst.registry.ObjectRegistry;
//import net.vg.amethyst.registry.WildCropConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModTagProvider extends FabricTagProvider<Biome> {
    //  EXAMPLE: Wild Carrot (Single)
    //  public static final TagKey<Biome> WILD_CARROT_BIOME_TAG = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("wildcrops", "spawns_wild_carrot"));

    // Map for all biome tags
//    private static final Map<String, TagKey<Biome>> WILD_CROP_BIOME_TAGS = new HashMap<>();

//    // Initialize all biome tags
//    static {
//        for (var crop : ObjectRegistry.WILD_CROPS) {
//            String cropName = crop.getId().getPath();
//            String tagName = "spawns_" + cropName.substring(5); // Remove "wild_" prefix
//
//            WILD_CROP_BIOME_TAGS.put(cropName,
//                    TagKey.create(Registries.BIOME,
//                            ResourceLocation.fromNamespaceAndPath("wildcrops", tagName)));
//        }
//    }


    public ModTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BIOME, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //  EXAMPLE: Wild Carrot (Single)
        //    getOrCreateTagBuilder(WILD_CARROT_BIOME_TAG).setReplace(false)
        //            .add(Biomes.FOREST)
        //            .add(Biomes.FLOWER_FOREST)
        //            .add(Biomes.BIRCH_FOREST)
        //            .add(Biomes.OLD_GROWTH_BIRCH_FOREST)
        //            .add(Biomes.DARK_FOREST)
        //            .add(Biomes.PLAINS)
        //            .add(Biomes.SUNFLOWER_PLAINS)
        //            .add(Biomes.MEADOW);

//        // Add tags for each crop
//        for (var crop : ObjectRegistry.WILD_CROPS) {
//            String cropName = crop.getId().getPath();
//            TagKey<Biome> tag = WILD_CROP_BIOME_TAGS.get(cropName);
//
//            WildCropConfig config = ObjectRegistry.WILD_CROP_CONFIGS.get(cropName);
//
//            getOrCreateTagBuilder(tag).setReplace(false)
//                            .addAll(config.getValidBiomes());

//            // For now, let's use the same biomes for all crops
//            // You can customize this for each crop type if needed
//            getOrCreateTagBuilder(tag).setReplace(false)
//                    .add(Biomes.FOREST)
//                    .add(Biomes.FLOWER_FOREST)
//                    .add(Biomes.BIRCH_FOREST)
//                    .add(Biomes.OLD_GROWTH_BIRCH_FOREST)
//                    .add(Biomes.DARK_FOREST)
//                    .add(Biomes.PLAINS)
//                    .add(Biomes.SUNFLOWER_PLAINS)
//                    .add(Biomes.MEADOW);
//        }
    }
}
