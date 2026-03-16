package com.yeetdot.chemi.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.*;

public record CrucibleRecipe(List<CountedIngredient> ingredients, int duration, ItemStack mainOutput, ItemStack subOutput, double subOutputDropChance) implements MultiOutputWithRNGRecipe<CrucibleRecipeInput> {
    public static final PacketCodec<ByteBuf, OptionalDouble> OPTIONAL_DOUBLE = PacketCodecs.DOUBLE.xmap(value -> value == 0 ? OptionalDouble.empty() : OptionalDouble.of(value - 1), value -> value.isPresent() ? value.getAsDouble() + 1 : 0);

    @Override
    public boolean matches(CrucibleRecipeInput input, World world) {
        // Build available pool
        Map<Item, Integer> available = new HashMap<>();

        for (ItemStack stack : input.inputs()) {
            if (stack.isEmpty()) continue;
            available.merge(stack.getItem(), stack.getCount(), Integer::sum);
        }

        // Try to satisfy each counted ingredient
        for (CountedIngredient ci : ingredients) {
            int needed = ci.count();
            int consumed = 0;

            for (Map.Entry<Item, Integer> entry : available.entrySet()) {
                if (entry.getValue() <= 0) continue;
                if (ci.ingredient().test(new ItemStack(entry.getKey()))) {
                    int take = Math.min(entry.getValue(), needed - consumed);
                    consumed += take;
                    entry.setValue(entry.getValue() - take);
                }

                if (consumed >= needed) break;
            }

            if (consumed < needed) {
                return false;
            }
        }

        return true;
    }



    @Override
    public ItemStack craft(CrucibleRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return mainOutput.copy();
    }

    @Override
    public List<ItemStack> craftAll(CrucibleRecipeInput input) {
        List<ItemStack> result = new ArrayList<>();
        result.add(mainOutput.copy());
        result.add(subOutput.copy());
        return result;
    }

    @Override
    public RecipeSerializer<? extends Recipe<CrucibleRecipeInput>> getSerializer() {
        return ModRecipes.CRUCIBLE_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<CrucibleRecipeInput>> getType() {
        return ModRecipes.CRUCIBLE_TYPE;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        List<Ingredient> ingredients1 = new ArrayList<>();
        for (CountedIngredient countedIngredient : ingredients) {
            ingredients1.add(countedIngredient.ingredient());
        }
        return IngredientPlacement.forShapeless(ingredients1);
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return null;
    }

    public static class Serializer implements RecipeSerializer<CrucibleRecipe> {
        public static final MapCodec<CrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                CountedIngredient.CODEC.listOf(1, 3).fieldOf("ingredients").forGetter(CrucibleRecipe::ingredients),
                Codec.INT.fieldOf("duration").forGetter(CrucibleRecipe::duration),
                ItemStack.CODEC.fieldOf("mainoutput").forGetter(CrucibleRecipe::mainOutput),
                ItemStack.OPTIONAL_CODEC.fieldOf("suboutput").forGetter(CrucibleRecipe::subOutput),
                Codec.DOUBLE.optionalFieldOf("sub_output_drop_chance", 0.0D).forGetter(CrucibleRecipe::subOutputDropChance)
        ).apply(inst, CrucibleRecipe::new));
        public static final PacketCodec<RegistryByteBuf, CrucibleRecipe> PACKET_CODEC = PacketCodec.of(
                ((recipe, buf) -> {
                    CountedIngredient.PACKET_CODEC.collect(PacketCodecs.toList()).encode(buf, recipe.ingredients);
                    buf.writeInt(recipe.duration);
                    ItemStack.PACKET_CODEC.encode(buf, recipe.mainOutput());
                    ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, recipe.subOutput());
                    buf.writeDouble(recipe.subOutputDropChance());
                }),
                buf -> {
                    List<CountedIngredient> ingredients = CountedIngredient.PACKET_CODEC.collect(PacketCodecs.toList()).decode(buf);
                    int duration = buf.readInt();
                    ItemStack main = ItemStack.PACKET_CODEC.decode(buf);
                    ItemStack sub = ItemStack.PACKET_CODEC.decode(buf);
                    double subOutputDropChance = buf.readDouble();
                    return new CrucibleRecipe(ingredients, duration, main, sub, subOutputDropChance);
                });
//                tuple(
//                CountedIngredient.PACKET_CODEC.collect(PacketCodecs.toList()), CrucibleRecipe::ingredients,
//                PacketCodecs.INTEGER, CrucibleRecipe::duration,
//                ItemStack.PACKET_CODEC, CrucibleRecipe::mainOutput,
//                ItemStack.OPTIONAL_PACKET_CODEC, CrucibleRecipe::subOutput,
//                PacketCodecs.DOUBLE, CrucibleRecipe::subOutputDropChance,
//                CrucibleRecipe::new);
        @Override
        public MapCodec<CrucibleRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, CrucibleRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}

