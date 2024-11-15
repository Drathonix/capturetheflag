package com.drathonix.finallib.common.inventory.wrapper.special;

import com.drathonix.finallib.common.inventory.wrapper.IInventoryFilter;
import com.drathonix.finallib.common.inventory.wrapper.InventoryWrapper;
import com.drathonix.finallib.common.inventory.wrapper.SuperWrapper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FilteredInventoryWrapper extends SuperWrapper {
    private final IInventoryFilter filter;

    public FilteredInventoryWrapper(InventoryWrapper wrapper, IInventoryFilter filter){
        super(wrapper);
        this.filter = filter;
    }

    @Override
    public boolean slotAcceptsItem(int slot, ItemStack stack) {
        return filter.slotAcceptsItem(slot,stack);
    }

    public ItemStack insertIntoSlot(int slot, @NotNull ItemStack stack, boolean simulate){
        if(stack.isEmpty()){
            return ItemStack.EMPTY;
        }
        if(!slotAcceptsItem(slot,stack)){
            return stack;
        }
        if(!isEmpty(slot)) {
            if (!matches(slot, stack)) {
                return stack;
            }
        }
        int k = (int)Math.min(getRemainingSpace(slot),stack.getCount());
        if(k == 0){
            return stack;
        }
        ItemStack overwrite = stack.copy();
        overwrite.setCount(k+getCount(slot));
        ItemStack out = stack.copy();
        out.shrink(k);
        if(!simulate) {
            setItemStack(slot, overwrite);
        }
        return out;
    }

    public @NotNull ItemStack insertItem(@NotNull ItemStack stack, boolean simulate){
        if(stack.isEmpty()){
            return ItemStack.EMPTY;
        }
        ItemStack result = stack.copy();
        int end = getRange().getEnd();
        for (int i = getRange().getStart(); i < end && !result.isEmpty(); i++) {
            result = insertIntoSlot(i, result, simulate);
        }
        return result;
    }

    @Override
    public boolean allowPlayerPlace(int index, ItemStack stack) {
        return slotAcceptsItem(index,stack);
    }
}
