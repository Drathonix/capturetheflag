package com.drathonix.capturetheflag.common.gui;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.gui.base.ChestGUIMenu;
import com.drathonix.capturetheflag.common.gui.base.GUISlot;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.CustomItem;
import com.drathonix.capturetheflag.common.system.GameDataCache;
import com.drathonix.capturetheflag.common.system.Skill;
import com.drathonix.capturetheflag.common.system.phasing.PhaseFlag;
import com.drathonix.finallib.common.inventory.wrapper.InventoryWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ClassSelection extends ChestGUIMenu {
    private final InventoryWrapper inv;
    private final boolean usedGem;
    private GUISlot[] classSlots;
    private ClassType previous;
    private ClassType selection;
    public ClassSelection(int id, Inventory inventory, InventoryWrapper wrapper, boolean usedGem) {
        super(MenuType.GENERIC_9x3, id, inventory, wrapper, 3);
        this.inv=wrapper;
        this.usedGem = usedGem;
    }

    public static void open(ServerPlayer sp, boolean usedGem) {
        sp.openMenu(new SimpleMenuProvider((id, inv, plr)-> new ClassSelection(id,inv,InventoryWrapper.of(NonNullList.withSize(9*5, ItemStack.EMPTY),0),usedGem),
                Component.literal("Class Selection").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD)));
    }

    @Override
    public void setup() {
        previous = CTFPlayerData.get(player).getClassType();
        selection=previous;
        fillGlass();
        classSlots = new GUISlot[4];
        ItemStack miner = Items.DIAMOND_PICKAXE.getDefaultInstance();
        miner.set(DataComponents.ITEM_NAME, Component.literal("[Breaker]").setStyle(Style.EMPTY.withBold(true).withColor(Color.CYAN.getRGB())));
        miner.set(DataComponents.LORE, ItemLore.EMPTY
                .withLineAdded(literal("Master of the Deep, an essential support class towards resource collection. Has a significant advantage in the underground."))
                .withLineAdded(literal("+Additional Enchantments: Fortune II-III, Efficiency III-V, Depth Strider II-III, Knockback I-II, Punch I"))
                .withLineAdded(literal("+Respawns: Iron Pickaxe, Stone Axe, Stone Sword"))
                .withLineAdded(literal("+Permanent Haste I, Haste II territory only"))
                .withLineAdded(literal("+20% less damage from mobs, and 20% less likely to be detected. 5% less damage from players."))
                .withLineAdded(literal("+Blast Pickaxe and Blast Furnace recipes available."))
                .withLineAdded(literal("+Rock").withStyle(Style.EMPTY.withBold(true)))
                .withLineAdded(literal("+Stone").withStyle(Style.EMPTY.withBold(true)))
        );
        ItemStack builder = Items.DIAMOND_SHOVEL.getDefaultInstance();
        builder.set(DataComponents.ITEM_NAME, Component.literal("[Architect]").setStyle(Style.EMPTY.withBold(true).withColor(Color.ORANGE.darker().getRGB())));
        builder.set(DataComponents.LORE, ItemLore.EMPTY
                .withLineAdded(literal("The creator of cities, this defensive class specializes in building defences quickly. Has a significant advantage above ground."))
                .withLineAdded(literal("+Additional Enchantments: Swiftsneak II-III, Depth Strider II-III, Punch I, Infinity"))
                .withLineAdded(literal("+Respawns: Stone Pickaxe, Stone Axe, Stone Sword, Iron Shovel"))
                .withLineAdded(literal("+Permanent Regeneration I in territory only"))
                .withLineAdded(literal("+40% less damage from mobs, and 30% less likely to be detected. 10% less damage from arrows, 5% less damage from players."))
                .withLineAdded(literal("+Bricklayer, Lumberjaxe, and Big Bucket recipes available."))
                .withLineAdded(literal("+Let there be light.").withStyle(Style.EMPTY.withBold(true)))
        );
        ItemStack warrior = Items.DIAMOND_SWORD.getDefaultInstance();
        warrior.set(DataComponents.ITEM_NAME, Component.literal("[Slayer]").setStyle(Style.EMPTY.withBold(true).withColor(Color.RED.getRGB())));
        warrior.set(DataComponents.LORE, ItemLore.EMPTY
                .withLineAdded(literal("The destroyer of civilizations, this offensive class specializes in defeating the enemy with sheer force of might. Has a significant advantage in hand-to-hand combat, but is vulnerable to mobs."))
                .withLineAdded(literal("+Additional Enchantments: Thorns I, Fire Aspect I-II, Sweeping Edge I-III, Sharpness III, Wind Burst I, Density II, Breach I"))
                .withLineAdded(literal("+Respawns: Stone Pickaxe, Stone Axe, Iron Sword"))
                .withLineAdded(literal("+Permanent Health Boost I in territory only"))
                .withLineAdded(literal("+10% more damage from mobs"))
                .withLineAdded(literal("+Mace and Reflector Shield recipes available."))
                .withLineAdded(literal("+Never Dies").withStyle(Style.EMPTY.withBold(true)))
        );
        ItemStack ranger = Items.CROSSBOW.getDefaultInstance();
        ranger.set(DataComponents.ITEM_NAME, Component.literal("[Ranger]").setStyle(Style.EMPTY.withBold(true).withColor(Color.RED.getRGB())));
        ranger.set(DataComponents.LORE, ItemLore.EMPTY
                .withLineAdded(literal("The eagle-eyed watcher, this ranged offensive class specializes in defeating the enemy with strategic positioning and distance. Has a significant advantage in long-range combat, but is vulnerable to mobs."))
                .withLineAdded(literal("+Additional Enchantments: Punch II, Power III, Loyalty III, Flame I"))
                .withLineAdded(literal("+Respawns: Stone Pickaxe, Stone Axe, Stone Sword, Bow, 16 Arrows"))
                .withLineAdded(literal("+Trident and Longbow recipes available."))
                .withLineAdded(literal("+Nobody expects.").withStyle(Style.EMPTY.withBold(true)))
        );
        classSlot(0,ClassType.BREAKER,miner);
        classSlot(1,ClassType.ARCHITECT,builder);
        classSlot(2,ClassType.SLAYER,warrior);
        classSlot(3,ClassType.RANGER,ranger);
    }

    private MutableComponent literal(String s) {
        return Component.literal(s).withStyle(Style.EMPTY.withItalic(false));
    }

    @Override
    public void removed(Player player) {
        if(player instanceof ServerPlayer sp) {
            if(usedGem) {
                Inventory inventory = player.getInventory();
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack stack = inventory.getItem(i);
                    if (CustomItem.GREATER_ATTUNEMENT_GEM.is(stack)) {
                        stack.shrink(1);
                        inventory.setItem(i, stack);
                        break;
                    }
                }
            }
            CTFPlayerData.get(sp).assignRandomSkills();
            CTF.executor.schedule(()->{
                SkillSelection.open(player,false);
            },100, TimeUnit.MILLISECONDS);
        }
    }

    public void fillGlass(){
        ItemStack stack = new ItemStack(Items.LIGHT_GRAY_STAINED_GLASS_PANE);
        stack.set(DataComponents.ITEM_NAME, Component.literal(""));
        for (int i = 0; i < 9 * 3; i++) {
            slots.get(i).set(stack);
        }
    }

    private void classSlot(int index, ClassType type, ItemStack stack){
        boolean set = CTFPlayerData.get(player).getClassType() == type;
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,set);
        int cen = 9+1+index*2;
        overwrite(cen,(c,s)->{
            classSlots[index] = new GUISlot(c,s);
            classSlots[index].onClick(sp->{
                for (GUISlot classSlot : classSlots) {
                    classSlot.getItem().set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,false);
                }
                classSlots[index].getItem().set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,true);
                CTFPlayerData.get(sp).setClassType(type);
                sp.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.MASTER,1f,1f);
                sp.sendSystemMessage(Component.literal("Selected " + type.name() + " as your class."));
                inv.getRange().forEach(i->{
                   if(inv.getItemStack(i).getItem() == Items.YELLOW_STAINED_GLASS_PANE) {
                       inv.setItem(i,ItemStack.EMPTY);
                   }
                });
                surround(cen,yellow);
                return true;
            });
            return classSlots[index];
        },stack);
        if(set){
            surround(cen,yellow);
        }
    }
}
