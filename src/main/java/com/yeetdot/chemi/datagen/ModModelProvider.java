package com.yeetdot.chemi.datagen;

import com.yeetdot.chemi.block.ModBlocks;
import com.yeetdot.chemi.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Models;
import net.minecraft.item.Item;

import java.util.List;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CRUCIBLE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        List<Item> generatedModelItems = List.of(
                ModItems.ELECTRUM_INGOT,
                ModItems.SILVER_INGOT
        );

        List<Item> handheldModelItems = List.of(

        );

        generatedModelItems.forEach(item -> itemModelGenerator.register(item, Models.GENERATED));
        handheldModelItems.forEach(item -> itemModelGenerator.register(item, Models.HANDHELD));
    }
}
