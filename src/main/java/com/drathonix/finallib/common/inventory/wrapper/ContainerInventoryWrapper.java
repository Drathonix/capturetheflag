package com.drathonix.finallib.common.inventory.wrapper;

import com.drathonix.finallib.common.util.Range;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerInventoryWrapper implements InventoryWrapper{
    private final Container container;
    private final int number;
    private final Range range;

    public ContainerInventoryWrapper(Container container, int number, Range range){
        this.container = container;
        this.number = number;
        this.range = range;
    }
    @Override
    public int getInventoryNumber() {
        return number;
    }

    @Override
    public int getSize() {
        return container.getContainerSize();
    }

    @Override
    public ItemStack getItemStack(int i) {
        return container.getItem(i);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return container.removeItem(slot,amount);
    }

    @Override
    public void setItemStack(int i, @NotNull ItemStack itemStack) {
        container.setItem(i,itemStack);
    }

    @Override
    public void setChanged() {
        container.setChanged();
    }

    @Override
    public Range getRange() {
        return range;
    }

    @Override
    public String toString() {
        return "ContainerInventoryWrapper{" + number + ":" + range + '}';
    }

}
