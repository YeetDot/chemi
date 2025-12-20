package com.yeetdot.chemi.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public interface MultiOutputWithRNGRecipe<T extends RecipeInput> extends Recipe<T> {
    List<ItemStack> craftAll(T input);
}
