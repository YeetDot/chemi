package com.yeetdot.chemi.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public record CrucibleRecipeInput(List<ItemStack> inputs) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return inputs.get(slot);
    }

    @Override
    public int size() {
        return inputs.size();
    }
}
