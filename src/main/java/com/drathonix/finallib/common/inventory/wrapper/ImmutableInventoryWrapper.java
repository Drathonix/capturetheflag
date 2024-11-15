package com.drathonix.finallib.common.inventory.wrapper;

import com.drathonix.finallib.common.util.Range;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

public class ImmutableInventoryWrapper implements InventoryWrapper{
    private final int id;

    public ImmutableInventoryWrapper(int id) {
        this.id=id;
    }

    @Override
    public int getInventoryNumber() {
        return id;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public Range getRange() {
        return new Range(0,0);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItemStack(int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStack(int i, ItemStack itemStack) {}

    @Override
    public void setChanged() {}

    @Override
    public boolean slotAcceptsItem(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull ItemStack insertItem(@NotNull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public boolean canFit(ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull ItemStack extractItem(@NotNull ItemStack requested, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public void transferMax(ItemStack stack, InventoryWrapper targetInventory) {

    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean allowPlayerTake(int index) {
        return false;
    }

    @Override
    public boolean allowPlayerPlace(int index, ItemStack stack) {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + getInventoryNumber() + "}";
    }

}
