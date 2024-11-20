package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.ArrowType;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


public enum SpecialAbility {
    SMELTLER,
    COBBLER{
        @Override
        public void tickSecond(ServerPlayer player) {
            NonNullList<ItemStack> stacks = player.getInventory().items;
            int k = 0;
            for (ItemStack stack : stacks) {
                if(stack.getItem() == Items.COBBLESTONE){
                    k+=stack.getCount();
                }
                if(k >= 64){
                    return;
                }
            }
            int emptySlot = -1;
            for (int i = 0; i < stacks.size(); i++) {
                ItemStack stack = stacks.get(i);
                if(stack.getItem() == Items.COBBLESTONE && stack.getCount() < stack.getMaxStackSize()) {
                    stack.grow(1);
                    return;
                }
                if(stack.isEmpty() && emptySlot == -1){
                    emptySlot = i;
                }
            }
            if(emptySlot != -1){
                stacks.set(emptySlot, Items.COBBLESTONE.getDefaultInstance());
            }
        }
    },
    BLOODLUST{
        @Override
        public void onKill(ServerPlayer player) {
            player.playSound(SoundEvents.BLAZE_SHOOT);
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,10*20));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,10*20));
        }
    },
    SCROUNGER{
        @Override
        public void tickSecond(ServerPlayer player) {
            NonNullList<ItemStack> stacks = player.getInventory().items;
            ArrowType type = ArrowType.NORMAL;
            int k = 0;
            for (ItemStack stack : stacks) {
                if(stack.getItem() == Items.ARROW || stack.getItem() == Items.TIPPED_ARROW){
                    k+=stack.getCount();
                }
                if(k >= type.amount){
                    return;
                }
            }
            int emptySlot = -1;
            for (int i = 0; i < stacks.size(); i++) {
                ItemStack stack = stacks.get(i);
                if(stack.getItem() == type.type && stack.getCount() < type.amount) {
                    stack.grow(1);
                    return;
                }
                if(stack.isEmpty() && emptySlot == -1){
                    emptySlot = i;
                }
            }
            if(emptySlot != -1){
                stacks.set(emptySlot, type.createStack());
            }
        }
    };

    public void tickSecond(ServerPlayer player){}
    public void onKill(ServerPlayer player){}
}
