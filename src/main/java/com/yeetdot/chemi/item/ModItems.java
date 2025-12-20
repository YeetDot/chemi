package com.yeetdot.chemi.item;

import com.yeetdot.chemi.Chemi;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    public static final Item SILVER_INGOT = register("silver_ingot", Item::new);
    public static final Item ELECTRUM_INGOT = register("electrum_ingot", Item::new);



    private static Item register(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(Chemi.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Chemi.MOD_ID, name)))));
    }

    public static void registerItems() {}
}
