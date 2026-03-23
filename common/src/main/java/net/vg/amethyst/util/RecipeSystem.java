package net.vg.amethyst.util;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;

import java.util.Map;

public class RecipeSystem {
    public enum RecipeType {
        SHAPELESS,
        SHAPED
    }

    // Enhanced recipe data with category and flexible mappings
    public record RecipeData(
            RecipeType type,
            RecipeCategory category,
            int outputCount,
            Item[] ingredients,
            String[] pattern,
            Map<Character, Item> ingredientMap  // For shaped recipes only
    ) {
        // Helper method for shapeless recipes
        public static RecipeData shapeless(RecipeCategory category, int outputCount, Item... ingredients) {
            return new RecipeData(
                    RecipeType.SHAPELESS,
                    category,
                    outputCount,
                    ingredients,
                    null,
                    null  // No mapping needed for shapeless
            );
        }

        public static RecipeData shapeless(RecipeCategory category, Item... ingredients) {
            return shapeless(category, 1, ingredients);
        }

        // Helper method for shaped recipes
        public static RecipeData shaped(
                RecipeCategory category,
                int outputCount,
                String[] pattern,
                Map<Character, Item> ingredientMap
        ) {
            return new RecipeData(
                    RecipeType.SHAPED,
                    category,
                    outputCount,
                    ingredientMap.values().toArray(new Item[0]),  // Convert map values to array
                    pattern,
                    ingredientMap
            );
        }

        public static RecipeData shaped(
                RecipeCategory category,
                String[] pattern,
                Map<Character, Item> ingredientMap
        ) {
            return shaped(category, 1, pattern, ingredientMap);
        }
    }
}