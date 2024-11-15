package com.drathonix.finallib.common.inventory.wrapper;

import com.drathonix.finallib.common.util.Range;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A Super Wrapper is a type of InventoryWrapper that wraps another inventory wrapper. These wrappers are intended for
 * overriding typical functionality such as adding slot filtering or blocking certain interactions.
 * Implementations must properly override all methods. If your wrapper is not working correctly its your fault, not this
 * class.
 */

public class SuperWrapper implements InventoryWrapper,IWrapped<InventoryWrapper>{
    protected final InventoryWrapper wrapper;

    public SuperWrapper(InventoryWrapper wrapper) {
        this.wrapper=wrapper;
    }

    @Override
    public int getInventoryNumber() {
        return wrapper.getInventoryNumber();
    }

    @Override
    public int getSize() {
        return wrapper.getSize();
    }

    @Override
    public Range getRange() {
        return wrapper.getRange();
    }

    @Override
    public boolean isEmpty() {
        return wrapper.isEmpty();
    }

    @Override
    public ItemStack getItemStack(int i) {
        return wrapper.getItemStack(i);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return wrapper.removeItem(slot,amount);
    }

    @Override
    public void setItemStack(int i, @NotNull ItemStack itemStack) {
        wrapper.setItemStack(i,itemStack);
    }

    @Override
    public void setChanged() {
        wrapper.setChanged();
    }

    @Override
    public boolean slotAcceptsItem(int slot, ItemStack stack) {
        return wrapper.slotAcceptsItem(slot,stack);
    }

    @Override
    public ItemStack insertIntoSlot(int slot, @NotNull ItemStack stack, boolean simulate) {
        return wrapper.insertIntoSlot(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack insertItem(@NotNull ItemStack stack, boolean simulate) {
        return wrapper.insertItem(stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(@NotNull ItemStack requested, boolean simulate) {
        return wrapper.extractItem(requested,simulate);
    }

    @Override
    public void transferMax(ItemStack stack, InventoryWrapper targetInventory) {
        wrapper.transferMax(stack,targetInventory);
    }


    @Override
    public boolean allowPlayerTake(int index) {
        return wrapper.allowPlayerTake(index);
    }

    @Override
    public boolean allowPlayerPlace(int index, ItemStack stack) {
        return wrapper.allowPlayerPlace(index,stack);
    }

    @Override
    public InventoryWrapper getWrapped() {
        return wrapper;
    }

    @Override
    public void acceptChangePacket(int slotIndex, ItemStack newState) {
        wrapper.acceptChangePacket(slotIndex,newState);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + getInventoryNumber() + " : " + getWrapped() + "}";
    }

    @Override
    public NonNullList<ItemStack> getContents() {
        return wrapper.getContents();
    }
}
