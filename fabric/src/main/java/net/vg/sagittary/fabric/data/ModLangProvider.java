package net.vg.sagittary.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.vg.sagittary.registry.ObjectRegistry;
import net.vg.sagittary.util.Util;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModLangProvider extends FabricLanguageProvider {
    protected ModLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        // EXAMPLE: Wild Carrot (Single)
        //  translationBuilder.add(ObjectRegistry.WILD_CARROTS.get().asItem(), "Wild Carrots");
        // Generate translations for all wild crops
        translationBuilder.add(ObjectRegistry.AMETHYST_ARROW_ITEM.get().asItem(), "Amethyst Arrow");
//        for (var crop : ObjectRegistry.WILD_CROPS) {
//            String path = crop.getId().getPath();
//            String displayName = Arrays.stream(path.split("_"))
//                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
//                    .collect(Collectors.joining(" "));
//
//            translationBuilder.add(crop.get().asItem(), displayName);
//        }
//
//        for (Util.ModItem modItem : ObjectRegistry.MOD_ITEMS) {
//            translationBuilder.add(
//                    modItem.item().get(),
//                    modItem.displayName()
//            );
//        }
    }
}
