package com.drathonix.finallib.common.inventory.wrapper;

import com.drathonix.finallib.common.inventory.wrapper.special.FilteredInventoryWrapper;
import com.drathonix.finallib.common.inventory.wrapper.special.InsertionBlockedInventoryWrapper;
import com.drathonix.finallib.common.inventory.wrapper.special.OverridenInventoryWrapper;
import com.drathonix.finallib.common.util.Range;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public interface InventoryWrapper extends Container {

    int getInventoryNumber();

    int getSize();

    Range getRange();

    default boolean isEmpty(){
        return getRange().forEach(true, i->{
            if(!getItemStack(i).isEmpty()){
                return false;
            }
            return null;
        });
    }

    ItemStack getItemStack(int i);

    ItemStack removeItem(int slot, int amount);

    void setItemStack(int i, @NotNull ItemStack itemStack);

    void setChanged();

    default void setItem(int slot, @NotNull ItemStack itemStack) {
        setItemStack(slot, itemStack);
    }

    default boolean contains(ItemStack stack){
        AtomicInteger remaining = new AtomicInteger(stack.getCount());
        return getRange().forEach(false,i->{
            ItemStack s =getItemStack(i);
            if(ItemStack.isSameItemSameComponents(stack,s)){
                remaining.getAndAdd(-s.getCount());
                return remaining.get() <= 0;
            }
            return null;
        });
    }

    default void growSlot(int slot, int amount){
        getItemStack(slot).grow(amount);
    }

    default boolean contains(Predicate<ItemStack> predicate, int n){
        AtomicInteger remaining = new AtomicInteger(n);
        return getRange().forEach(false,i->{
            ItemStack s =getItemStack(i);
            if(predicate.test(s)){
                remaining.getAndAdd(-s.getCount());
                return remaining.get() <= 0;
            }
            return null;
        });
    }

    default boolean slotAcceptsItem(int slot, ItemStack stack){
        return true;
    }

    default @NotNull ItemStack insertItem(@NotNull ItemStack stack){
        return insertItem(stack,false);
    }

    default @NotNull ItemStack insertItem(@NotNull ItemStack stack, boolean simulate){
        return unoverriddenInsertItem(this,stack,simulate);
    }

    static @NotNull ItemStack unoverriddenInsertItem(InventoryWrapper wrapper, @NotNull ItemStack stack){
        return unoverriddenInsertItem(wrapper,stack,false);
    }

    static @NotNull ItemStack unoverriddenInsertItem(InventoryWrapper wrapper, @NotNull ItemStack stack, boolean simulate){
        if(stack.isEmpty()){
            return ItemStack.EMPTY;
        }
        ItemStack result = stack.copy();
        int end = wrapper.getRange().getEnd();
        for (int i = wrapper.getRange().getStart(); i < end && !result.isEmpty(); i++) {
            result = InventoryWrapper.unoverriddenInsertIntoSlot(wrapper,i, result, simulate);
        }
        return result;
    }

    static ItemStack unoverriddenInsertIntoSlot(InventoryWrapper wrapper, int slot, @NotNull ItemStack stack, boolean simulate){
        if(stack.isEmpty()){
            return ItemStack.EMPTY;
        }
        if(!wrapper.slotAcceptsItem(slot,stack)){
            return stack;
        }
        if(wrapper.isEmpty(slot)){
            int k = (int)Math.min(wrapper.getRemainingSpace(slot),stack.getCount());
            if(k == 0){
                return stack;
            }
            ItemStack overwrite = stack.copy();
            overwrite.setCount(k);
            ItemStack out = stack.copy();
            out.shrink(k);
            if(!simulate) {
                wrapper.setItemStack(slot, overwrite);
            }
            return out;
        }
        if (!wrapper.matches(slot, stack)) {
            return stack;
        }
        int k = (int)Math.min(wrapper.getRemainingSpace(slot),stack.getCount());
        if(k == 0){
            return stack;
        }
        ItemStack out = stack.copy();
        out.shrink(k);
        if(!simulate) {
            wrapper.growSlot(slot,k);
        }
        return out;
    }


    default boolean canFit(ItemStack stack){
        //Use unoverridden to ensure the logic is just checking for slots that can contain the item.
        return unoverriddenInsertItem(this,stack,true).isEmpty();
    }

    default ItemStack insertIntoSlot(int slot, @NotNull ItemStack stack, boolean simulate){
        return InventoryWrapper.unoverriddenInsertIntoSlot(this,slot,stack,simulate);
    }

    default ItemStack swap(int slot, @NotNull ItemStack stack){
        ItemStack current = getItemStack(slot);
        setItemStack(slot,ItemStack.EMPTY);
        ItemStack s = insertIntoSlot(slot,stack, false);
        if(!s.isEmpty()){
            setItemStack(slot,current);
            return stack;
        }
        else{
            return current;
        }
    }

    default long getRemainingSpace(int slot){
        return getMaxStackSize(slot)-getCount(slot);
    }

    default int getCount(int slot){
        return getItemStack(slot).getCount();
    }

    default long getMaxStackSize(int slot){
        if(isEmpty(slot)){
            return 64;
        }
        return getItemStack(slot).getMaxStackSize();
    }

    default boolean isEmpty(int slot){
        return getItemStack(slot).isEmpty();
    }

    default boolean matches(int slot, ItemStack stack){
        return ItemStack.isSameItemSameComponents(getItemStack(slot),stack);
    }

    default @NotNull ItemStack extractItem(@NotNull ItemStack requested){
        return extractItem(requested,false);
    }

    default @NotNull ItemStack extractItem(@NotNull ItemStack requested, boolean simulate){
        if(requested.isEmpty()){
            return ItemStack.EMPTY;
        }
        ItemStack remaining = requested.copy();
        int end = getRange().getEnd();
        for (int i = getRange().getStart(); i < end && remaining.getCount() > 0; i++) {
            ItemStack current = getItemStack(i);
            if(ItemStack.isSameItemSameComponents(current,requested)){
                int change = Math.min(remaining.getCount(),current.getCount());
                if(!simulate) {
                    removeItem(i,change);
                }
            }
        }
        ItemStack result = requested.copy();
        result.setCount(requested.getCount()- remaining.getCount());
        return result;
    }

    default void transferMax(ItemStack stack, InventoryWrapper targetInventory){
        if(stack.isEmpty()){
            return;
        }
        getRange().forEach(null,i->{
            ItemStack current = getItemStack(i);
            if(ItemStack.isSameItemSameComponents(current,stack)){
                ItemStack result = targetInventory.insertItem(current);
                current.setCount(result.getCount());
                setChanged();
                if(result.isEmpty()){
                    return true;
                }
            }
            return null;
        });
    }

    @Override
    default int getContainerSize() {
        return getSize();
    }

    @Override
    default @NotNull ItemStack getItem(int i) {
        return getItemStack(i);
    }

    @Override
    default @NotNull ItemStack removeItemNoUpdate(int i) {
        ItemStack out = getItem(i);
        setItemStack(i,ItemStack.EMPTY);
        return out;
    }

    @Override
    default boolean stillValid(Player player) {
        return true;
    }

    @Override
    default void clearContent() {
        getRange().forEach(i-> setItemStack(i,ItemStack.EMPTY));
    }

    @Override
    default boolean canPlaceItem(int i, ItemStack itemStack) {
        return slotAcceptsItem(i,itemStack);
    }

    default boolean allowPlayerTake(int index){
        return true;
    }

    default boolean allowPlayerPlace(int index, ItemStack stack){
        return true;
    }

    default void acceptChangePacket(int slotIndex, ItemStack newState){
        setItemStack(slotIndex,newState);
    }

    static InventoryWrapper empty() {
        return of(NonNullList.create(),0);
    }

    static NonNullListInventoryWrapper of(NonNullList<ItemStack> list, int number, Runnable onChanged){
        return new NonNullListInventoryWrapper(list,number,new Range(0,list.size()),onChanged);
    }
    static NonNullListInventoryWrapper of(NonNullList<ItemStack> list, int number){
        return new NonNullListInventoryWrapper(list, number, new Range(0,list.size()));
    }
    static ContainerInventoryWrapper of(Container container, int number){
        return new ContainerInventoryWrapper(container,number,new Range(0,container.getContainerSize()));
    }

    static NonNullListInventoryWrapper ofLimited(NonNullList<ItemStack> list, int number, Range range, Runnable onChanged){
        return new NonNullListInventoryWrapper(list,number,range,onChanged);
    }
    static NonNullListInventoryWrapper ofLimited(NonNullList<ItemStack> list, Range range, int number){
        return new NonNullListInventoryWrapper(list, number, range);
    }
    static ContainerInventoryWrapper ofLimited(Container container, Range range, int number){
        return new ContainerInventoryWrapper(container,number,range);
    }
    static FilteredInventoryWrapper filtered(InventoryWrapper wrapper, IInventoryFilter filter){
        return new FilteredInventoryWrapper(wrapper,filter);
    }
    static InsertionBlockedInventoryWrapper insertionBlocked(InventoryWrapper wrapper){
        return new InsertionBlockedInventoryWrapper(wrapper);
    }
    static OverridenInventoryWrapper withId(InventoryWrapper wrapper, int id){
        return new OverridenInventoryWrapper(wrapper,id);
    }

    default InsertionBlockedInventoryWrapper insertionBlocked(){
        return new InsertionBlockedInventoryWrapper(this);
    }
    default OverridenInventoryWrapper withId(int id){
        return new OverridenInventoryWrapper(this,id);
    }
    default ImmutableInventoryWrapper immutify(){
        return new ImmutableInventoryWrapper(getInventoryNumber());
    }

    default NonNullList<ItemStack> getContents() {
        NonNullList<ItemStack> lst = NonNullList.withSize(getSize(), ItemStack.EMPTY);
        getRange().forEach(i->{
           lst.set(i,getItemStack(i));
        });
        return lst;
    }
}
