package com.drathonix.capturetheflag.common.gui;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.component.CustomDatas;
import com.drathonix.capturetheflag.common.config.CTFConfig;
import com.drathonix.capturetheflag.common.gui.base.ChestGUIMenu;
import com.drathonix.capturetheflag.common.gui.base.GUISlot;
import com.drathonix.capturetheflag.common.gui.base.TickingGUISlot;
import com.drathonix.capturetheflag.common.util.regis.EnchantmentRetriever;
import com.drathonix.capturetheflag.common.util.regis.ItemRetriever;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.awt.*;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Holy enchanter mechanics:
 *
 * Lapis count displayed.
 * <=64 = lapis, >64 = block.
 * Items are selected by clicking inventory slots.
 * Armor is displayed on the left and is selectable.
 * Available enchantments are displayed with their current level, cost in xp and lapis to upgrade, and their maximum level.
 */
public class HolyEnchanter extends ChestGUIMenu {
    private static final int center = 3;
    private GUISlot itemSlot;

    private GUISlot selectedSlot = null;
    @SuppressWarnings("all")
    private Pair<Integer, Holder<Enchantment>>[] power;
    private GUISlot[] enchantSlots;
    private int costToEnchantLapis = 0;
    private int costToEnchantLevels = 0;

    public HolyEnchanter(int id, Inventory inventory, Container container) {
        super(MenuType.GENERIC_9x5, id, inventory, container, 5);
    }

    @Override
    public void setup() {
        enchantSlots = new GUISlot[9];
        power = new Pair[9];
        try {
            if (this.data.getTeamState() == null || this.data.getClassType() == null) {
                return;
            }
            overwriteInventorySlots();
            fillGlass();
            overwrite(9 * 2 + center, (c, s) -> {
                itemSlot = new GUISlot(c, s);
                return itemSlot;
            });
            itemSlot.set(ItemStack.EMPTY);
            overwrite(9+center-1,(c,s)->{
                TickingGUISlot xpSlot = new TickingGUISlot(c, s);
                xpSlot.onTick(sp -> {
                    ItemStack stack;
                    if (sp.experienceLevel <= 0) {
                        stack = new ItemStack(Items.GLASS_BOTTLE);
                        stack.set(DataComponents.ITEM_NAME, Component.literal("No Levels").setStyle(Style.EMPTY.withBold(true).withColor(Color.RED.getRGB())));
                        stack.set(DataComponents.LORE, ItemLore.EMPTY.withLineAdded(Component.literal("You need to get more experience")
                                .withStyle(Style.EMPTY.withColor(Color.RED.getRGB()).withItalic(false)))
                        );
                    } else {
                        stack = new ItemStack(Items.EXPERIENCE_BOTTLE);
                        if (sp.experienceLevel >= costToEnchantLevels) {
                            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                            stack.set(DataComponents.ITEM_NAME, Component.literal("Levels Sufficient").setStyle(Style.EMPTY.withBold(true).withColor(Color.GREEN.getRGB())));
                            stack.set(DataComponents.LORE, ItemLore.EMPTY.withLineAdded(Component.literal("You have enough levels to enchant")
                                    .withStyle(Style.EMPTY.withColor(Color.YELLOW.getRGB()).withItalic(false)))
                            );
                        } else {
                            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                            stack.set(DataComponents.ITEM_NAME, Component.literal("Levels Insufficient").setStyle(Style.EMPTY.withBold(true).withColor(Color.RED.getRGB())));
                            stack.set(DataComponents.LORE, ItemLore.EMPTY.withLineAdded(Component.literal("You do not have enough levels to enchant")
                                    .withStyle(Style.EMPTY.withColor(Color.ORANGE.getRGB()).withItalic(false)))
                            );
                        }
                    }
                    xpSlot.set(stack);
                });
                return xpSlot;
            });
            overwrite(9 * 3 + center-1, (c, s) -> {
                TickingGUISlot lapisSlot = new TickingGUISlot(c, s);
                lapisSlot.onTick(sp -> {
                    ItemStack stack;
                    if (data.getTeamState().getLapis() <= 0) {
                        stack = new ItemStack(Items.RED_DYE);
                        stack.set(DataComponents.ITEM_NAME, Component.literal("No Lapis").setStyle(Style.EMPTY.withBold(true).withColor(Color.RED.getRGB())));
                        stack.set(DataComponents.LORE, ItemLore.EMPTY.withLineAdded(Component.literal("You are out of lapis")
                                .withStyle(Style.EMPTY.withColor(Color.RED.getRGB()).withItalic(false)))
                    );
                    } else {
                        if (data.getTeamState().getLapis() <= 64) {
                            stack = new ItemStack(Items.LAPIS_LAZULI);
                        } else {
                            stack = new ItemStack(Items.LAPIS_BLOCK);
                        }
                        if (data.getTeamState().getLapis() > costToEnchantLapis) {
                            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                            stack.set(DataComponents.ITEM_NAME, Component.literal("Lapis Sufficient (" + data.getTeamState().getLapis() + ")").setStyle(Style.EMPTY.withBold(true).withColor(Color.GREEN.getRGB())));
                            stack.set(DataComponents.LORE, ItemLore.EMPTY.withLineAdded(Component.literal("You have enough lapis to enchant")
                                    .withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withItalic(false)))
                            );
                        } else {
                            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                            stack.set(DataComponents.ITEM_NAME, Component.literal("Lapis Insufficient (" + data.getTeamState().getLapis() + ")").setStyle(Style.EMPTY.withBold(true).withColor(Color.RED.getRGB())));
                            stack.set(DataComponents.LORE, ItemLore.EMPTY.withLineAdded(Component.literal("You do not have enough lapis to enchant")
                                    .withStyle(Style.EMPTY.withColor(Color.ORANGE.getRGB()).withItalic(false)))
                            );
                        }
                    }
                    lapisSlot.set(stack);
                });
                return lapisSlot;
            });
            overwrite(9 * 3 + center, (c, s) -> {
                TickingGUISlot enchantSlot = new TickingGUISlot(c, s);
                enchantSlot.onClick(sp -> {
                    onClickEnchant(sp);
                    return true;
                });
                enchantSlot.onTick(sp -> {
                    ItemStack stack;
                    if (selectedSlot == null) {
                        stack = new ItemStack(Items.ORANGE_STAINED_GLASS_PANE);
                        stack.set(DataComponents.ITEM_NAME, Component.literal("[Select an Item]").setStyle(Style.EMPTY.withBold(true).withColor(Color.ORANGE.getRGB())));
                        stack.set(DataComponents.LORE, ItemLore.EMPTY
                                .withLineAdded(Component.literal("Click an item in your inventory to select it for enchanting")
                                        .withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withItalic(false))));
                        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
                    } else {
                        if (data.getTeamState().getLapis() < costToEnchantLapis) {
                            stack = new ItemStack(Items.RED_STAINED_GLASS_PANE);
                            stack.set(DataComponents.ITEM_NAME, Component.literal("[Not enough lapis]").setStyle(Style.EMPTY.withBold(true).withColor(Color.RED.getRGB())));
                            stack.set(DataComponents.LORE, ItemLore.EMPTY
                                    .withLineAdded(Component.literal("Requires " + costToEnchantLevels + " levels")
                                            .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false)))
                                    .withLineAdded(Component.literal("Requires " + costToEnchantLapis + " lapis")
                                            .withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withItalic(false))));
                            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
                        } else if (player.experienceLevel < costToEnchantLevels) {
                            stack = new ItemStack(Items.RED_STAINED_GLASS_PANE);
                            stack.set(DataComponents.ITEM_NAME, Component.literal("[Not enough levels]").setStyle(Style.EMPTY.withBold(true).withColor(Color.RED.getRGB())));
                            stack.set(DataComponents.LORE, ItemLore.EMPTY
                                    .withLineAdded(Component.literal("Requires " + costToEnchantLevels + " levels")
                                            .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false)))
                                    .withLineAdded(Component.literal("Requires " + costToEnchantLapis + " lapis")
                                            .withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withItalic(false))));
                            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
                        } else if(!noConflicts()){
                            stack = new ItemStack(Items.YELLOW_STAINED_GLASS_PANE);
                            stack.set(DataComponents.ITEM_NAME, Component.literal("[Conflicting Enchantments!]").setStyle(Style.EMPTY.withBold(true).withColor(Color.RED.getRGB())));
                            stack.set(DataComponents.LORE, ItemLore.EMPTY
                                    .withLineAdded(Component.literal("Remove conflicting enchantments.")
                                            .withStyle(Style.EMPTY.withColor(ChatFormatting.RED).withItalic(false))));
                            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
                        }
                        else {
                            stack = new ItemStack(Items.LIME_STAINED_GLASS_PANE);
                            stack.set(DataComponents.ITEM_NAME, Component.literal("[Enchant]").setStyle(Style.EMPTY.withBold(true).withColor(Color.GREEN.getRGB())));
                            stack.set(DataComponents.LORE, ItemLore.EMPTY
                                    .withLineAdded(Component.literal("Click to increase your item's enchantments")
                                            .withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withItalic(false)))
                                    .withLineAdded(Component.literal("Requires " + costToEnchantLevels + " levels")
                                            .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false)))
                                    .withLineAdded(Component.literal("Requires " + costToEnchantLapis + " lapis")
                                            .withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withItalic(false))));
                            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                        }
                    }
                    enchantSlot.set(stack);
                });
                return enchantSlot;
            });
            ItemStack g = Items.WHITE_STAINED_GLASS_PANE.getDefaultInstance();
            g.set(DataComponents.ITEM_NAME,Component.literal(""));
            for (int i = 0; i < 9; i++) {
                int row = i / 3;
                int col = i % 3;
                int finalI = i;
                overwrite(9 * (row + 1) + center+2 + col, (c, s) -> {
                    GUISlot slot = new GUISlot(c, s);
                    slot.onClick(sp -> {
                        increaseEnchantPower(finalI, sp);
                        return true;
                    });
                    enchantSlots[finalI] = slot;
                    return slot;
                },g);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void fillGlass(){
        ItemStack stack = new ItemStack(Items.PURPLE_STAINED_GLASS_PANE);
        ItemStack stack2 = new ItemStack(Items.BLUE_STAINED_GLASS_PANE);
        stack.set(DataComponents.ITEM_NAME, Component.literal(""));
        stack2.set(DataComponents.ITEM_NAME, Component.literal(""));
        for (int i = 0; i < 9 * 5; i++) {
            int k = i%9;
            if(k >= center-1 && k <= center+1){
                slots.get(i).set(stack2);
            }
            else{
                slots.get(i).set(stack);
            }
        }
    }

    public void overwriteInventorySlots(){
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if(slot.container instanceof Inventory){
                overwrite(i,(c,s)->{
                    GUISlot guiSlot = new GUISlot(c,s);
                    guiSlot.onClick(sp->{
                        onPlayerInventorySlotClick(sp,guiSlot);
                        return true;
                    });
                    return guiSlot;
                });
            }
        }
    }

    public synchronized void onPlayerInventorySlotClick(ServerPlayer player, GUISlot slot){
        selectedSlot=slot;
        ItemStack slotStack = slot.getItem();
        IdMap<Holder<Enchantment>> holders = CTF.server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).asHolderIdMap();
        int k = 0;
        Iterator<Holder<Enchantment>> iterator = holders.iterator();
        Arrays.fill(power, null);
        ItemStack s = Items.WHITE_STAINED_GLASS_PANE.getDefaultInstance();
        s.set(DataComponents.ITEM_NAME,Component.literal(""));
        for (GUISlot enchantSlot : enchantSlots) {
            enchantSlot.set(s);
        }
        while(k < 9 && iterator.hasNext()){
            Holder<Enchantment> holder = iterator.next();
            int mp = data.getClassType().enchantments.getOrDefault(new EnchantmentRetriever(holder),0);
            if (holder.value().isSupportedItem(slotStack) && mp > 0) {
                int pwr = slotStack.getEnchantments().getLevel(holder);
                power[k] = Pair.of(pwr, holder);
                ItemRetriever item = CTFConfig.enchantmentToItem.get(new EnchantmentRetriever(holder));
                if(item != null) {
                    ItemStack stack = item.get().value().getDefaultInstance();
                    stack.setCount(Math.max(1, pwr));
                    stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, pwr > 0);
                    stack.set(DataComponents.ITEM_NAME, holder.value().description());
                    stack.set(DataComponents.LORE,ItemLore.EMPTY
                            .withLineAdded(Component.literal("New Level: " + pwr)
                                    .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false)))
                            .withLineAdded(Component.literal("Max Level: " + mp)
                                    .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false))));
                    enchantSlots[k].set(stack);
                    k++;
                }
            }
        }
        itemSlot.set(slotStack);

    }

    public void onClickEnchant(ServerPlayer sp){
        if(data.getTeamState().getLapis() >= costToEnchantLapis && sp.experienceLevel >= costToEnchantLevels && noConflicts()) {
            ItemStack current = selectedSlot.getItem();
            for (Pair<Integer, Holder<Enchantment>> pair : power) {
                if (pair != null) {
                    Holder<Enchantment> holder = pair.getSecond();
                    if (holder == null) {
                        break;
                    }
                    int pwr = pair.getFirst();
                    current.enchant(holder, pwr);
                    CustomDatas.setVanish(current, false);
                }
            }
            sp.setExperienceLevels(sp.experienceLevel-costToEnchantLevels);
            data.getTeamState().removeLapis(costToEnchantLapis);
            sp.serverLevel().playSound(null,sp.blockPosition(),SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS);
        }
    }

    private boolean noConflicts() {
        l1: for (Pair<Integer, Holder<Enchantment>> pair : power) {
            for (Pair<Integer, Holder<Enchantment>> pair2 : power) {
                if(pair == null){
                    break l1;
                }
                if(pair2 == null){
                    break;
                }
                if(Enchantment.areCompatible(pair.getSecond(),pair2.getSecond())){
                    return false;
                }
            }
        }
        return true;
    }

    public void increaseEnchantPower(int i, ServerPlayer sp) {
        if(power[i] != null) {
            Holder<Enchantment> holder = power[i].getSecond();
            if (holder != null) {
                int maxPower = data.getClassType().enchantments.get(new EnchantmentRetriever(holder));
                power[i] = power[i].mapFirst(k -> Math.max(selectedSlot.getItem().getEnchantments().getLevel(holder), (k + 1) % (maxPower+1)));
                ItemStack stack = enchantSlots[i].getItem();
                stack.setCount(Math.max(1, power[i].getFirst()));
                stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, power[i].getFirst() > 0);
                stack.set(DataComponents.LORE,ItemLore.EMPTY
                        .withLineAdded(Component.literal("New Level: " + power[i].getFirst())
                                .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false)))
                        .withLineAdded(Component.literal("Max Level: " + maxPower)
                                .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false))));
                enchantSlots[i].set(stack);
                recalcuateCosts();
            }
        }
        sp.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(),SoundSource.BLOCKS,1f,1F);
    }

    public void recalcuateCosts(){
        ItemStack current = selectedSlot.getItem();
        int previousLapisPower = 0;
        int newLapisPower = 0;
        int levelsToConsume = 0;
        for (Pair<Integer, Holder<Enchantment>> pair : power) {
            if (pair == null) {
                break;
            }
            Holder<Enchantment> enchantment = pair.getSecond();
            int itemPower = current.getEnchantments().getLevel(enchantment);
            previousLapisPower += itemPower;
            int newPower = pair.getFirst();
            newLapisPower += newPower;
            levelsToConsume += (newPower - itemPower) * CTFConfig.enchantmentToLevelBase.get(new EnchantmentRetriever(enchantment));
        }
        costToEnchantLapis = fibonacci(newLapisPower)-fibonacci(previousLapisPower);
        costToEnchantLevels = levelsToConsume;
    }

    private static int fibonacci(int n) {
        int k = 0;
        int x = 0;
        int y = 1;
        for (int i = 0; i < n; i++) {
            int fib = x+y;
            x = y;
            y = fib;
            k+=fib;
        }
        return (int) (k*1.5);
    }
}