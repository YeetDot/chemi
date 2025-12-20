package com.yeetdot.chemi.recipe;

import com.yeetdot.chemi.Chemi;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final RecipeSerializer<CrucibleRecipe> CRUCIBLE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(Chemi.MOD_ID, "crucible"),
            new CrucibleRecipe.Serializer()
    );

    public static final RecipeType<CrucibleRecipe> CRUCIBLE_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(Chemi.MOD_ID, "crucible"),
            new RecipeType<CrucibleRecipe>() {
                @Override
                public String toString() {
                    return "crucible";
                }
            }
    );

    public static void registerRecipes() {}
}
