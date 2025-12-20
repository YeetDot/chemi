package com.yeetdot.chemi.screen.custom;

import com.yeetdot.chemi.screen.ModScreenHandlers;
import com.yeetdot.chemi.screen.slot.CrucibleFuelSlot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CrucibleScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final World world;

    public CrucibleScreenHandler(int syncId, PlayerInventory inventory, BlockPos pos) {
        this(syncId, inventory, inventory.player.getEntityWorld().getBlockEntity(pos), new ArrayPropertyDelegate(4));
    }

    public CrucibleScreenHandler(int syncId, PlayerInventory inventory, BlockEntity entity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.CRUCIBLE_SCREEN_HANDLER, syncId);

        this.inventory = (Inventory) entity;
        this.world = entity.getWorld();
        this.propertyDelegate = arrayPropertyDelegate;

        this.addSlot(new Slot(this.inventory, 0, 56, 7));
        this.addSlot(new Slot(this.inventory, 1, 56, 17));
        this.addSlot(new Slot(this.inventory, 2, 56, 27));
        this.addSlot(new CrucibleFuelSlot(this, this.inventory, 3, 56, 53));
        this.addSlot(new FurnaceOutputSlot(inventory.player, this.inventory, 4, 116, 25));
        this.addSlot(new FurnaceOutputSlot(inventory.player, this.inventory, 5, 116, 35));

        this.addPlayerSlots(inventory, 8, 84);
        this.addProperties(arrayPropertyDelegate);
    }

    public boolean isFuel(ItemStack item) {
        return this.world.getFuelRegistry().isFuel(item);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < this.inventory.size()) {
                if (!this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.isFuel(itemStack2) ? !this.insertItem(itemStack2, 3, 4, false) : !this.insertItem(itemStack2, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public boolean isBurning() {
        return this.propertyDelegate.get(2) > 0;
    }

    public float getFuelProgress() {
        int i = this.propertyDelegate.get(3);
        if (i == 0) {
            i = 200;
        }
        return MathHelper.clamp((float)this.propertyDelegate.get(2) / (float)i, 0.0f, 1.0f);
    }

    public float getCookProgress() {
        int i = this.propertyDelegate.get(1);
        if (i == 0) {
            i = 200;
        }
        return MathHelper.clamp((float)this.propertyDelegate.get(0) / (float)i, 0.0f, 1.0f);
    }
}
