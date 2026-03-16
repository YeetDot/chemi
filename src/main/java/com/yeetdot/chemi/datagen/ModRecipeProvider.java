package com.yeetdot.chemi.datagen;

import com.yeetdot.chemi.Chemi;
import com.yeetdot.chemi.datagen.recipe.CrucibleRecipeJsonBuilder;
import com.yeetdot.chemi.item.ModItems;
import com.yeetdot.chemi.recipe.CountedIngredient;
import com.yeetdot.chemi.recipe.CrucibleRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    public static RegistryKey<Recipe<?>> registryKeyOf(String id) {
        return RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(Chemi.MOD_ID, id));
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                CrucibleRecipeJsonBuilder.create(
                        List.of(CountedIngredient.ofItemAndCount(Items.GOLD_INGOT, 2), CountedIngredient.ofItemAndCount(ModItems.SILVER_INGOT, 1)), 10, new ItemStack(ModItems.ELECTRUM_INGOT, 3), ItemStack.EMPTY, 0)
                        .criterion(hasItem(ModItems.SILVER_INGOT), conditionsFromItem(ModItems.SILVER_INGOT))
                        .offerTo(recipeExporter, registryKeyOf("electrum"));
                CrucibleRecipeJsonBuilder.create(
                        List.of(CountedIngredient.ofItemAndCount(Items.IRON_INGOT, 3), CountedIngredient.ofItemAndCount(Items.COAL, 1)), 5, new ItemStack(Items.WAXED_EXPOSED_CUT_COPPER_STAIRS, 3), new ItemStack(Items.COCOA_BEANS), 0.5)
                        .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                        .offerTo(recipeExporter, registryKeyOf("steel"));
            }
        };
    }

    @Override
    public String getName() {
        return "Chemi Recipes";
    }
}
