package com.yeetdot.chemi.block.entity;

import com.yeetdot.chemi.Chemi;
import com.yeetdot.chemi.recipe.CountedIngredient;
import com.yeetdot.chemi.recipe.CrucibleRecipe;
import com.yeetdot.chemi.recipe.CrucibleRecipeInput;
import com.yeetdot.chemi.recipe.ModRecipes;
import com.yeetdot.chemi.screen.custom.CrucibleScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CrucibleBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);
    private final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 100;
    private int burnTime = 0;
    private int maxBurnTime = 100;

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUCIBLE, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> burnTime;
                    case 3 -> maxBurnTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                    case 2 -> burnTime = value;
                    case 3 -> maxBurnTime = value;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        boolean dirty = false;

        if (isBurning()) burnTime--;

        Optional<RecipeEntry<CrucibleRecipe>> recipe = getCurrentRecipe(world);

        if (recipe.isPresent() && hasRecipe(world)) {
            if (canAcceptRecipeOutput(recipe.get(), new CrucibleRecipeInput(inventory.subList(0, 3)), inventory)) {
                this.maxProgress = getCurrentRecipe(world).get().value().duration();
                if (!isBurning()) {
                    resetProgress();
                    tryConsumeFuel(world.getFuelRegistry());
                }

                if(isBurning()) {
                    progress++;
                    dirty = true;
                    if (hasFinishedCrafting()) {
                        craftItem(world);
                        resetProgress();
                    }
                }
            }
        } else {
            resetProgress();
        }

        if (dirty) markDirty(world, pos, state);
    }

    private boolean isBurning() {
        return burnTime > 0;
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem(World world) {
        CrucibleRecipeInput input = new CrucibleRecipeInput(inventory.subList(0, 3));
        Optional<RecipeEntry<CrucibleRecipe>> recipe = getCurrentRecipe(world);
        if (recipe.isPresent()) {
            if (recipe.get().value().matches(input, world)) {
                if (canAcceptRecipeOutput(recipe.get(), input, inventory)) {
                    ItemStack mainOutput = inventory.get(4);
                    ItemStack subOutput = inventory.get(5);
                    ItemStack mainOutput1 = recipe.get().value().mainOutput().copy();
                    ItemStack subOutput1 = recipe.get().value().subOutput().copy();
                    if (mainOutput.isEmpty()) {
                        inventory.set(4, mainOutput1.copy());
                    } else {
                        mainOutput.increment(mainOutput1.getCount());
                    }
                    if (subOutput.isEmpty()) {
                        double chance = recipe.get().value().subOutputDropChance();
                        int baseCount = subOutput1.getCount();
                        int count = 0;

                        // guaranteed drops for integer part
                        if (chance >= 1.0) {
                            count += (int) Math.floor(chance) * baseCount;
                            chance = chance % 1.0;
                        }

                        // uniform drop for fractional part
                        if (baseCount == 1) {
                            if (world.getRandom().nextDouble() < chance) {
                                count += 1;
                            }
                        } else {
                            count += world.getRandom().nextInt((int) Math.ceil(baseCount * chance) + 1);
                        }

                        inventory.set(5, new ItemStack(subOutput1.getItem(), count));
                    } else {
                        double chance = recipe.get().value().subOutputDropChance();
                        int baseCount = subOutput1.getCount();
                        int count = 0;

                        // guaranteed drops for integer part
                        if (chance >= 1.0) {
                            count += (int) Math.floor(chance) * baseCount;
                            chance = chance % 1.0;
                        }

                        // uniform drop for fractional part
                        if (baseCount == 1) {
                            if (world.getRandom().nextDouble() < chance) {
                                count += 1;
                            }
                        } else {
                            count += world.getRandom().nextInt((int) Math.ceil(baseCount * chance) + 1);
                        }

                        subOutput.increment(count);

                    }
                    consumeIngredients(recipe.get().value().ingredients(), inventory);
                }
            }
        }
    }

    private void tryConsumeFuel(FuelRegistry fuelRegistry) {
        ItemStack fuel = inventory.get(3);

        if (fuel.isEmpty()) return;

        int fuelTime = fuelRegistry.getFuelTicks(fuel);

        if (fuelTime > 0) {
            burnTime = fuelTime;
            maxBurnTime = fuelTime;
            fuel.decrement(1);
        }
    }

    private static boolean canAcceptRecipeOutput(RecipeEntry<? extends CrucibleRecipe> recipe, CrucibleRecipeInput input, DefaultedList<ItemStack> inventory) {

        List<ItemStack> outputs = recipe.value().craftAll(input);
        ItemStack output1 = outputs.get(0);
        ItemStack output2 = outputs.get(1);

        ItemStack slot4 = inventory.get(4);
        ItemStack slot5 = inventory.get(5);

        boolean canPlace1 = slot4.isEmpty() ||
                (ItemStack.areItemsAndComponentsEqual(slot4, output1) && slot4.getCount() + output1.getCount() <= slot4.getMaxCount());

        boolean canPlace2 = slot5.isEmpty() ||
                (ItemStack.areItemsAndComponentsEqual(slot5, output2) && slot5.getCount() + output2.getCount() <= slot5.getMaxCount());

        return canPlace1 && canPlace2;
    }

    public static void consumeIngredients (List<CountedIngredient> ingredients, List<ItemStack> inventory) {
        for (CountedIngredient ci : ingredients) {
            int remaining = ci.count();

            for (ItemStack stack : inventory) {
                if (remaining <= 0) break;
                if (!ci.ingredient().test(stack)) continue;

                int taken = Math.min(stack.getCount(), remaining);
                stack.decrement(taken);
                remaining -= taken;
            }
        }
    }

    private boolean hasFinishedCrafting() {
        return this.progress >= this.maxProgress;
    }

    private boolean hasRecipe(World world) {
        Optional<RecipeEntry<CrucibleRecipe>> recipe = getCurrentRecipe(world);
        return recipe.isPresent();
    }

    private Optional<RecipeEntry<CrucibleRecipe>> getCurrentRecipe(World world) {
        List<ItemStack> inputs = List.of(inventory.get(0), inventory.get(1), inventory.get(2));
        return this.getWorld().getServer().getRecipeManager().getFirstMatch(ModRecipes.CRUCIBLE_TYPE, new CrucibleRecipeInput(inputs), world);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("blockentityname.chemi.crucible");
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        ItemScatterer.spawn(world, pos, this);
        super.onBlockReplaced(pos, oldState);
    }

    @Override
    protected void readData(ReadView view) {
        Inventories.readData(view, inventory);
        progress = view.getInt("crucible.progress", 0);
        maxProgress = view.getInt("crucible.max_progress", 0);
        burnTime = view.getInt("crucible.burn_time", 0);
        maxBurnTime = view.getInt("crucible.max_burn_time", 0);
        super.readData(view);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData(view, inventory);
        view.putInt("crucible.progress", progress);
        view.putInt("crucible.max_progress", maxProgress);
        view.putInt("crucible.burn_time", burnTime);
        view.putInt("crucible.max_burn_time", maxBurnTime);
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CrucibleScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
