package com.drathonix.finallib.common.inventory.wrapper.special;

import com.drathonix.finallib.common.inventory.wrapper.InventoryWrapper;
import com.drathonix.finallib.common.inventory.wrapper.SuperWrapper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InsertionBlockedInventoryWrapper extends SuperWrapper {
    public InsertionBlockedInventoryWrapper(InventoryWrapper wrapper){
        super(wrapper);
    }

    @Override
    public ItemStack insertIntoSlot(int slot, @NotNull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public @NotNull ItemStack insertItem(@NotNull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public boolean allowPlayerPlace(int index, ItemStack stack) {
        return false;
    }
}
