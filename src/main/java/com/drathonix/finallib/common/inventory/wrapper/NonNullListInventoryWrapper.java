package com.drathonix.finallib.common.inventory.wrapper;

import com.drathonix.finallib.common.util.Range;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

public class NonNullListInventoryWrapper implements InventoryWrapper{
    private final NonNullList<ItemStack> list;
    private final int inventoryNumber;
    private final Range range;
    private final Runnable onChanged;

    public NonNullListInventoryWrapper(NonNullList<ItemStack> list, int inventoryNumber, Range range){
        this(list,inventoryNumber,range,()->{});
    }

    public NonNullListInventoryWrapper(NonNullList<ItemStack> list, int inventoryNumber, Range range, Runnable onChanged){
        this.list = list;
        this.inventoryNumber = inventoryNumber;
        this.range = range;
        this.onChanged = onChanged;
    }

    @Override
    public int getInventoryNumber() {
        return inventoryNumber;
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public ItemStack getItemStack(int i) {
        return list.get(i);
    }

    @Override
    public ItemStack removeItem(int index, int amount) {
        ItemStack result = ContainerHelper.removeItem(list,index,amount);
        setChanged();
        return result;
    }

    @Override
    public void setItemStack(int i, ItemStack itemStack) {
        list.set(i,itemStack);
        setChanged();
    }

    @Override
    public void setChanged() {
        onChanged.run();
    }

    @Override
    public Range getRange() {
        return range;
    }

    @Override
    public String toString() {
        return "NonNullListInventoryWrapper{" + inventoryNumber + ": " + range + '}';
    }
}
