package com.drathonix.capturetheflag.common.gui.base;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ChestGUIMenu extends ChestMenu {
    protected final Container container;
    protected final Inventory inventory;
    protected final ServerPlayer player;
    protected final CTFPlayerData data;

    public static final ItemStack yellow = Items.YELLOW_STAINED_GLASS_PANE.getDefaultInstance();
    public static final ItemStack black = Items.BLACK_STAINED_GLASS_PANE.getDefaultInstance();
    static {
        black.set(DataComponents.ITEM_NAME, Component.literal(""));
        yellow.set(DataComponents.ITEM_NAME, Component.literal(""));
    }

    private final Map<Integer,TickingGUISlot> tickables = new HashMap<>();
    private ScheduledFuture<?> tickTask;

    public ChestGUIMenu(MenuType<?> menuType, int id, Inventory inventory, Container container, int rows) {
        super(menuType, id, inventory, container, rows);
        this.container=container;
        this.inventory=inventory;
        if(inventory.player instanceof ServerPlayer serverPlayer) {
            this.player = serverPlayer;
            this.data = CTFPlayerData.get(player);
            setup();
            tick();
            tickTask = CTF.executor.scheduleAtFixedRate(this::tick,100,100, TimeUnit.MILLISECONDS);
        }
        else {
            this.player = null;
            this.data = null;
        }

    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        tickTask.cancel(true);
    }

    public void setup(){
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public void overwrite(int index, BiFunction<Container,Integer,GUISlot> func, ItemStack stack){
        Slot old = slots.get(index);
        GUISlot slot = func.apply(old.container,old.getContainerSlot());
        slots.set(index,slot);
        slot.index=index;
        slot.set(stack);
        if(slot instanceof TickingGUISlot t) {
            tickables.put(index, t);
        }
    }

    public void overwrite(int index, BiFunction<Container,Integer,GUISlot> func){
        Slot old = slots.get(index);
        GUISlot slot = func.apply(old.container,old.getContainerSlot());
        slots.set(index,slot);
        slot.index=index;
        if(slot instanceof TickingGUISlot t) {
            tickables.put(index, t);
        }
    }


    @Override
    public void clicked(int slotIdx, int useless, ClickType clickType, Player player) {
        if(slotIdx >= 0 && slotIdx < slots.size()) {
            if (player instanceof ServerPlayer sp) {
                Slot slot = slots.get(slotIdx);
                if (slot instanceof GUISlot gui) {
                    //Blocks by default.
                    if (!gui.handleClick(sp)) {
                        super.clicked(slotIdx, useless, clickType, player);
                    }
                }
            }
        }
    }

    public synchronized void tick(){
        if(inventory.player instanceof ServerPlayer serverPlayer) {
            tickables.forEach((index, slot) -> {
                slot.handleTick(serverPlayer);
            });
        }
    }

    public void surround(int slot, ItemStack stack){
        overwrite(slot +1, GUISlot::new,stack);
        overwrite(slot -1, GUISlot::new,stack);
        overwrite(slot +1-9, GUISlot::new,stack);
        overwrite(slot -9, GUISlot::new,stack);
        overwrite(slot -1-9, GUISlot::new,stack);
        overwrite(slot +1+9, GUISlot::new,stack);
        overwrite(slot +9, GUISlot::new,stack);
        overwrite(slot -1+9, GUISlot::new,stack);
    }
}
