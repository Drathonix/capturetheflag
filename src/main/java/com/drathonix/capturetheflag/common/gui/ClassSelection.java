package com.drathonix.capturetheflag.common.gui;

import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.gui.base.ChestGUIMenu;
import com.drathonix.capturetheflag.common.gui.base.GUISlot;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.GameDataCache;
import com.drathonix.capturetheflag.common.system.phasing.PhaseFlag;
import dev.architectury.utils.GameInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.awt.*;

public class ClassSelection extends ChestGUIMenu {
    private GUISlot[] classSlots;
    private ClassType previous;
    public ClassSelection(int id, Inventory inventory, Container container) {
        super(MenuType.GENERIC_9x3, id, inventory, container, 3);
    }

    @Override
    public void setup() {
        previous = CTFPlayerData.get(player).getClassType();
        fillGlass();
        classSlots = new GUISlot[4];
        ItemStack miner = Items.DIAMOND_PICKAXE.getDefaultInstance();
        miner.set(DataComponents.ITEM_NAME, Component.literal("[Breaker]").setStyle(Style.EMPTY.withBold(true).withColor(Color.CYAN.getRGB())));
        miner.set(DataComponents.LORE, ItemLore.EMPTY
                .withLineAdded(literal("Master of the Deep, an essential support class towards resource collection. Has a significant advantage in the underground."))
                .withLineAdded(literal("+Additional Enchantments: Fortune II-III, Efficiency III-V, Depth Strider II-III, Knockback I-II, Punch I"))
                .withLineAdded(literal("+Respawns: Iron Pickaxe, Stone Axe, Stone Sword"))
                .withLineAdded(literal("+Permanent Haste I, Haste II territory only"))
                .withLineAdded(literal("+Smelter: 3x furnace speed for all types, but only you can access them."))
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
                .withLineAdded(literal("+Cobbler: Passively generates 1 cobblestone per second. Can be stored in chests for later. Will not work with more than one full stack in the inventory."))
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
                .withLineAdded(literal("+Bloodlust: Gain 5 seconds of speed I and 4 seconds of Strength I after killing a living being."))
                .withLineAdded(literal("+10% more damage from mobs"))
                .withLineAdded(literal("+Mace and Reflector Shield recipes available."))
                .withLineAdded(literal("+Never Dies").withStyle(Style.EMPTY.withBold(true)))
        );
        ItemStack ranger = Items.CROSSBOW.getDefaultInstance();
        ranger.set(DataComponents.ITEM_NAME, Component.literal("[Ranger]").setStyle(Style.EMPTY.withBold(true).withColor(Color.RED.getRGB())));
        ranger.set(DataComponents.LORE, ItemLore.EMPTY
                .withLineAdded(literal("The eagle-eyed watcher, this ranged offensive class specializes in defeating the enemy with strategic positioning and distance. Has a significant advantage in long-range combat, but is vulnerable to mobs."))
                .withLineAdded(literal("+Additional Enchantments: Punch II, Power III-IV, Quickdraw II, Multishot I, Loyalty III, Flame I"))
                .withLineAdded(literal("+Respawns: Stone Pickaxe, Stone Axe, Stone Sword, Bow, 16 Arrows"))
                .withLineAdded(literal("+Permanent Speed I in territory only"))
                .withLineAdded(literal("+Scrounging: Receives 1 arrow per second, up to 64, Can reduce generation rate and arrow cap in exchange for potion arrows. Only rangers can use these arrows."))
                .withLineAdded(literal("+20% increase ranged weapon damage."))
                .withLineAdded(literal("+Trident, Longbow, and Crossbow recipes available."))
                .withLineAdded(literal("+Nobody expects.").withStyle(Style.EMPTY.withBold(true)))
        );
        classSlot(0,ClassType.MINER,miner);
        classSlot(1,ClassType.BUILDER,builder);
        classSlot(2,ClassType.WARRIOR,warrior);
        classSlot(3,ClassType.RANGER,ranger);
    }

    private MutableComponent literal(String s) {
        return Component.literal(s).withStyle(Style.EMPTY.withItalic(false));
    }

    @Override
    public void removed(Player player) {
        if(CTFPlayerData.get(this.player).getClassType() != previous && GameDataCache.getGamePhase().flags.contains(PhaseFlag.ONE_CLASS_SWAP)){
            CTFPlayerData.get(this.player).allowChangeClass=false;
            this.player.sendSystemMessage(Component.literal("You can no longer change your class during this phase.").withStyle(ChatFormatting.RED));
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
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,CTFPlayerData.get(player).getClassType() == type);
        overwrite(9+1+index*2,(c,s)->{
            classSlots[index] = new GUISlot(c,s);
            classSlots[index].onClick(sp->{
                for (GUISlot classSlot : classSlots) {
                    classSlot.getItem().set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,false);
                }
                classSlots[index].getItem().set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,true);
                CTFPlayerData.get(sp).setClassType(type);
                sp.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.MASTER,1f,1f);
                return true;
            });
            return classSlots[index];
        },stack);
    }
}
