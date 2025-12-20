package com.yeetdot.chemi.block.entity;

import com.yeetdot.chemi.Chemi;
import com.yeetdot.chemi.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<CrucibleBlockEntity> CRUCIBLE = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Chemi.MOD_ID, "crucible"), FabricBlockEntityTypeBuilder.create(CrucibleBlockEntity::new, ModBlocks.CRUCIBLE).build());

    public static void registerBlockEntities() {}
}
