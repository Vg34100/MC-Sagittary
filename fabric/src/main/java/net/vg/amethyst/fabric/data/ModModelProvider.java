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
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.block.Block;
import net.vg.amethyst.registry.ObjectRegistry;
import net.vg.amethyst.component.ArrowComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public  class ModModelProvider extends FabricModelProvider {
    private final FabricDataOutput dataOutput;
    
    public ModModelProvider(FabricDataOutput output) {
        super(output);
        this.dataOutput = output;
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
    }

    @Override
    public void generateItemModels(ItemModelGenerators gen) {
        gen.generateFlatItem(ObjectRegistry.AMETHYST_ARROW_ITEM.get().asItem(), ModelTemplates.FLAT_ITEM);

        ArrowComponent[] tips = {
                ArrowComponent.FLINT_TIP,
                ArrowComponent.AMETHYST_TIP,
                ArrowComponent.COPPER_TIP
        };
        ArrowComponent[] shafts = {
                ArrowComponent.STICK_SHAFT,
                ArrowComponent.BAMBOO_SHAFT,
                ArrowComponent.BLAZE_ROD_SHAFT
        };
        ArrowComponent[] fletchings = {
                ArrowComponent.FEATHER_FLETCHING,
                ArrowComponent.PAPER_FLETCHING,
                ArrowComponent.PHANTOM_MEMBRANE_FLETCHING
        };

//        fallback
        TextureMapping defaultMapping = TextureMapping.layered(
                ResourceLocation.fromNamespaceAndPath("amethyst", "item/component_arrow"),
                ResourceLocation.fromNamespaceAndPath("amethyst", "item/flint_tip"),
                ResourceLocation.fromNamespaceAndPath("amethyst", "item/stick_shaft")
        );

        ModelTemplates.THREE_LAYERED_ITEM.create(
                ResourceLocation.fromNamespaceAndPath("amethyst", "item/component_arrow"),
                defaultMapping,
                gen.modelOutput
        );


        for (ArrowComponent tip : tips) {
            for (ArrowComponent shaft : shafts) {
                for (ArrowComponent fletch : fletchings) {
                    String tipName = tip.getMaterialName();
                    String shaftName = shaft.getMaterialName();
                    String fletchName = fletch.getMaterialName();

                    String comboName = "component_arrow_" + tipName + "_" + shaftName + "_" + fletchName;

                    TextureMapping mapping = TextureMapping.layered(
//                            ResourceLocation.fromNamespaceAndPath("amethyst", "item/component_arrow"),
                            ResourceLocation.fromNamespaceAndPath("amethyst", "item/" + tipName + "_tip"),
                            ResourceLocation.fromNamespaceAndPath("amethyst", "item/" + shaftName + "_shaft"),
                            ResourceLocation.fromNamespaceAndPath("amethyst", "item/" + fletchName + "_fletching")

                    );

                    ModelTemplates.THREE_LAYERED_ITEM.create(
                            ResourceLocation.fromNamespaceAndPath("amethyst", "item/" + comboName),
                            mapping,
                            gen.modelOutput
                    );
                }
            }
        }
        
        // Generate the complete component arrow JSON with all combinations
        generateCompleteComponentArrowJson(gen);
    }
    
    /**
     * Generates the complete items/component_arrow.json file with all possible component combinations
     * This manually creates the JSON file since Minecraft's datagen doesn't support custom component selectors
     */
    private void generateCompleteComponentArrowJson(ItemModelGenerators gen) {
        try {
            // All components
            ArrowComponent[] tips = {ArrowComponent.FLINT_TIP, ArrowComponent.AMETHYST_TIP, ArrowComponent.COPPER_TIP};
            ArrowComponent[] shafts = {ArrowComponent.STICK_SHAFT, ArrowComponent.BAMBOO_SHAFT, ArrowComponent.BLAZE_ROD_SHAFT};
            ArrowComponent[] fletchings = {ArrowComponent.FEATHER_FLETCHING, ArrowComponent.PAPER_FLETCHING, ArrowComponent.PHANTOM_MEMBRANE_FLETCHING};
            
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{\n");
            jsonBuilder.append("  \"model\": {\n");
            jsonBuilder.append("    \"type\": \"minecraft:select\",\n");
            jsonBuilder.append("    \"component\": \"amethyst:arrow_parts\",\n");
            jsonBuilder.append("    \"property\": \"minecraft:component\",\n");
            jsonBuilder.append("    \"cases\": [\n");
            
            boolean first = true;
            for (ArrowComponent tip : tips) {
                for (ArrowComponent shaft : shafts) {
                    for (ArrowComponent fletching : fletchings) {
                        if (!first) {
                            jsonBuilder.append(",\n");
                        }
                        first = false;
                        
                        String modelName = "component_arrow_" + tip.getMaterialName() + "_" + shaft.getMaterialName() + "_" + fletching.getMaterialName();
                        
                        jsonBuilder.append("      {\n");
                        jsonBuilder.append("        \"when\": {\n");
                        jsonBuilder.append("          \"tip\": \"").append(tip.getMaterialName()).append("\",\n");
                        jsonBuilder.append("          \"shaft\": \"").append(shaft.getMaterialName()).append("\",\n");
                        jsonBuilder.append("          \"fletching\": \"").append(fletching.getMaterialName()).append("\"\n");
                        jsonBuilder.append("        },\n");
                        jsonBuilder.append("        \"model\": {\n");
                        jsonBuilder.append("          \"type\": \"minecraft:model\",\n");
                        jsonBuilder.append("          \"model\": \"amethyst:item/").append(modelName).append("\"\n");
                        jsonBuilder.append("        }\n");
                        jsonBuilder.append("      }");
                    }
                }
            }
            
            jsonBuilder.append("\n    ],\n");
            jsonBuilder.append("    \"fallback\": {\n");
            jsonBuilder.append("      \"type\": \"minecraft:model\",\n");
            jsonBuilder.append("      \"model\": \"amethyst:item/component_arrow\"\n");
            jsonBuilder.append("    }\n");
            jsonBuilder.append("  }\n");
            jsonBuilder.append("}\n");
            
            // Write to the generated folder with a different name to avoid datagen conflicts
            Path outputPath = this.dataOutput.getOutputFolder().resolve("assets/amethyst/items/component_arrow_complete.json");
            Files.createDirectories(outputPath.getParent());
            Files.writeString(outputPath, jsonBuilder.toString());
            
            // Also write to the project root for easy copying
            Path projectRootPath = this.dataOutput.getOutputFolder().getParent().getParent().getParent().getParent().resolve("common/src/main/resources/assets/amethyst/items/component_arrow.json");
            Files.writeString(projectRootPath, jsonBuilder.toString());
            
            System.out.println("Generated complete component_arrow.json with " + (tips.length * shafts.length * fletchings.length) + " combinations!");
            System.out.println("Generated at: " + outputPath);
            System.out.println("Also saved to project root: " + projectRootPath);
            System.out.println("Copy the content to your items/component_arrow.json file!");
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate component_arrow.json", e);
        }
    }
}

