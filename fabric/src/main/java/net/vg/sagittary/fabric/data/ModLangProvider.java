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
        // REMOVED: Amethyst arrow is now part of the component system
        // translationBuilder.add(ObjectRegistry.AMETHYST_ARROW_ITEM.get().asItem(), "Amethyst Arrow");

        // Add translations for mod items
        translationBuilder.add(ObjectRegistry.COMPONENT_ARROW_ITEM.get().asItem(), "Component Arrow");
        translationBuilder.add(ObjectRegistry.QUIVER_ITEM.get().asItem(), "Quiver");
        translationBuilder.add(ObjectRegistry.IRON_BOW_ITEM.get().asItem(), "Iron Bow");
        translationBuilder.add(ObjectRegistry.COMPOUND_BOW_ITEM.get().asItem(), "Compound Bow");
        translationBuilder.add(ObjectRegistry.IRON_CROSSBOW_ITEM.get().asItem(), "Iron Crossbow");
        translationBuilder.add(ObjectRegistry.REPEATER_CROSSBOW_ITEM.get().asItem(), "Repeater Crossbow");
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
