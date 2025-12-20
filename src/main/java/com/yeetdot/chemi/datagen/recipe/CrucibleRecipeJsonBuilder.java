package com.yeetdot.chemi.datagen.recipe;

import com.yeetdot.chemi.recipe.CountedIngredient;
import com.yeetdot.chemi.recipe.CrucibleRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CrucibleRecipeJsonBuilder {
    private static final RecipeCategory category = RecipeCategory.MISC;
    private final List<CountedIngredient> ingredients;
    private final int duration;
    private final ItemStack mainoutput;
    private final ItemStack suboutput;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    private CrucibleRecipeJsonBuilder(List<CountedIngredient> ingredients, int duration, ItemStack mainoutput, ItemStack suboutput) {
        this.ingredients = ingredients;
        this.duration = duration;
        this.mainoutput = mainoutput;
        this.suboutput = suboutput;
    }

    public static CrucibleRecipeJsonBuilder create(List<CountedIngredient> ingredients, int duration, ItemStack mainoutput, ItemStack suboutput) {
        return new CrucibleRecipeJsonBuilder(ingredients, duration, mainoutput, suboutput);
    }

    public CrucibleRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
        this.criteria.put(string, advancementCriterion);
        return this;
    }

    public void offerTo(RecipeExporter recipeExporter, RegistryKey<Recipe<?>> recipeKey) {
        this.validate(recipeKey);
        Advancement.Builder builder = recipeExporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
                .rewards(AdvancementRewards.Builder.recipe(recipeKey))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        this.criteria.forEach(builder::criterion);
        CrucibleRecipe crucibleRecipe = new CrucibleRecipe(this.ingredients, this.duration, this.mainoutput, this.suboutput);
        recipeExporter.accept(recipeKey, crucibleRecipe, builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + category.getName() + "/")));
    }

    private void validate(RegistryKey<Recipe<?>> recipeKey) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeKey.getValue());
        }
    }
}
