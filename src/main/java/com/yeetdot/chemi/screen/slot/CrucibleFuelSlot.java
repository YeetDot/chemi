package com.yeetdot.chemi.screen.slot;

import com.yeetdot.chemi.screen.custom.CrucibleScreenHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.FurnaceFuelSlot;
import net.minecraft.screen.slot.Slot;

public class CrucibleFuelSlot extends Slot {
    private final CrucibleScreenHandler handler;

    public CrucibleFuelSlot(CrucibleScreenHandler handler, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.handler = handler;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return this.handler.isFuel(stack);
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return FurnaceFuelSlot.isBucket(stack) ? 1 : super.getMaxItemCount(stack);
    }
}
