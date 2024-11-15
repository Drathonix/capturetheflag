package com.drathonix.finallib.common.inventory.wrapper;

import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface IInventoryFilter {
    boolean slotAcceptsItem(int slot, ItemStack stack);
}
