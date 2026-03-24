package net.vg.amethyst.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.vg.amethyst.registry.ObjectRegistry;

public  class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
    }

    @Override
    public void generateItemModels(ItemModelGenerators gen) {
        gen.generateFlatItem(ObjectRegistry.AMETHYST_ARROW_ITEM.get().asItem(), ModelTemplates.FLAT_ITEM);
        // The component-arrow model set is already checked into generated/resources.
        // Keep datagen minimal on 1.21.1 until the custom model generation path is ported.
    }
}

