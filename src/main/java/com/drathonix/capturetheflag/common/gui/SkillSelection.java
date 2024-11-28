package com.drathonix.capturetheflag.common.gui;

import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.config.CTFConfig;
import com.drathonix.capturetheflag.common.config.SkillsConfig;
import com.drathonix.capturetheflag.common.gui.base.ChestGUIMenu;
import com.drathonix.capturetheflag.common.gui.base.GUISlot;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.CustomItem;
import com.drathonix.capturetheflag.common.system.Skill;
import com.drathonix.capturetheflag.common.util.GeneralUtil;
import com.drathonix.finallib.common.inventory.wrapper.InventoryWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashSet;
import java.util.Set;

public class SkillSelection extends ChestGUIMenu {
    private final boolean usedGem;
    private SkillSlot[] skillSlots;
    private Set<Skill> previous;
    public SkillSelection(int id, Inventory inventory, InventoryWrapper wrapper, boolean usedGem) {
        super(MenuType.GENERIC_9x5, id, inventory, wrapper, 5);
        this.usedGem = usedGem;
    }

    public static void open(Player player, boolean usedGem) {
        player.openMenu(new SimpleMenuProvider((id,inv,plr)-> new SkillSelection(id,inv,InventoryWrapper.of(NonNullList.withSize(9*5, ItemStack.EMPTY),0),usedGem),
                Component.literal("Skill Selection").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD)));
    }

    @Override
    public void setup() {
        skillSlots = new SkillSlot[Skill.values().length];
        previous = new HashSet<>(CTFPlayerData.get(player).getSkills());
        fillGlass();
        Skill[] options = Skill.values();
        for (int i = 0; i < options.length; i++) {
            Skill skill = options[i];
            int row = 9*(i < 4 ? 1 : 3);
            int col = 1+(i%4)*2;
            int k = row+col;
            try {
                skillSlot(skill, k);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void skillSlot(Skill skill, int cen) {
        overwrite(cen,(c,s)->{
            SkillSlot slot = new SkillSlot(c,s,skill);
            skillSlots[skill.ordinal()]=slot;
            return slot;
        });
    }

    @Override
    public void removed(Player player) {
        if(player instanceof ServerPlayer sp) {
            Set<Skill> newSkills = getSelections();
            Inventory inventory = sp.getInventory();
            if(usedGem && !newSkills.equals(previous)) {
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack stack = inventory.getItem(i);
                    if (CustomItem.LESSER_ATTUNEMENT_GEM.is(stack)) {
                        stack.shrink(1);
                        inventory.setItem(i, stack);
                        break;
                    }
                }
            }
            CTFPlayerData.get(sp).setSkills(newSkills);
        }
    }

    public void fillGlass(){
        for (int i = 0; i < 9 * 5; i++) {
            slots.get(i).set(black);
        }
    }

    private Set<Skill> getSelections() {
        Set<Skill> newSkills = new HashSet<>();
        for (SkillSlot skillSlot : skillSlots) {
            if (skillSlot.getItem().getOrDefault(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,false)) {
                newSkills.add(skillSlot.skill);
            }
        }
        return newSkills;
    }

    private int totalSkills(){
        return getSelections().size();
    }

    private int totalClassSkills(ClassType type){
        Set<Skill> selections = getSelections();
        selections.removeIf(s->s.classType!=type);
        return selections.size();
    }

    private class SkillSlot extends GUISlot {
        public final Skill skill;

        public SkillSlot(Container container, int slot, Skill skill) {
            super(container, slot);
            this.skill = skill;
            ItemStack initial = skill.getIcon();
            if(previous.contains(skill)){
                initial.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,true);
                surround(slot,yellow);
            }
            set(initial);
            onClick(sp->{
                ClassType type = CTFPlayerData.get(sp).getClassType();
                ItemStack stack = getItem();
                boolean value = stack.getOrDefault(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,false);
                GeneralUtil.playSound(sp,SoundEvents.UI_BUTTON_CLICK);
                if(!value && CTFPlayerData.get(sp).getMaxSkills() < totalSkills()+1){
                    sp.sendSystemMessage(Component.literal("Cannot select this skill, too many skills selected. Deselect one first."));
                    return true;
                }
                if(!value && skill.classType != type && totalClassSkills(type) < SkillsConfig.requiredNumberOfClassSkills){
                    sp.sendSystemMessage(Component.literal("Cannot select this skill. You need to select " + SkillsConfig.requiredNumberOfClassSkills + " skills of your class first."));
                    return true;
                }
                stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,!value);
                ItemStack opt = !value ? yellow : black;
                surround(slot,opt);
                if(value){
                    for (SkillSlot skillSlot : skillSlots) {
                        if(skillSlot.getItem().getOrDefault(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,false)){
                            surround(skillSlot.getContainerSlot(),yellow);
                        }
                    }
                }
                set(stack);
                return true;
            });
        }
    }
}
