package net.vg.sagittary.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModLangProvider::new);
        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModLootProvider::new);
        pack.addProvider(ModTagProvider::new);
        pack.addProvider(ModRegistryDataGenerator::new);

    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
//        registryBuilder.add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap);
//        registryBuilder.add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap);
    }
}