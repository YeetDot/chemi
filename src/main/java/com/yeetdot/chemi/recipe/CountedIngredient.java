package com.yeetdot.chemi.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;

public record CountedIngredient(Ingredient ingredient, int count) {
    public static final Codec<CountedIngredient> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(CountedIngredient::ingredient),
                    Codec.INT.fieldOf("count").forGetter(CountedIngredient::count)
            ).apply(inst, CountedIngredient::new));

    public static final PacketCodec<RegistryByteBuf, CountedIngredient> PACKET_CODEC =
            PacketCodec.tuple(
                    Ingredient.PACKET_CODEC, CountedIngredient::ingredient,
                    PacketCodecs.INTEGER, CountedIngredient::count,
                    CountedIngredient::new
            );

    public static CountedIngredient ofItemAndCount(ItemConvertible item, int count) {
        return new CountedIngredient(Ingredient.ofItem(item), count);
    }
}
