package com.yeetdot.chemi;

import com.yeetdot.chemi.block.ModBlocks;
import com.yeetdot.chemi.block.entity.ModBlockEntities;
import com.yeetdot.chemi.item.ModItems;
import com.yeetdot.chemi.recipe.ModRecipes;
import com.yeetdot.chemi.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chemi implements ModInitializer {
	public static final String MOD_ID = "chemi";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
        ModBlocks.registerBlocks();
        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();
        ModRecipes.registerRecipes();
        ModItems.registerItems();

		LOGGER.info("Hello Fabric world!");
	}
}