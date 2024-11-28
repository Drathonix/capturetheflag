package com.drathonix.capturetheflag.common;

import com.drathonix.capturetheflag.common.component.CustomDatas;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.Nullable;

public enum ArrowType {
    NORMAL(64, Items.ARROW, null),
    SLOWNESS(32, Items.TIPPED_ARROW,Potions.SLOWNESS),
    WEAKNESS(16, Items.TIPPED_ARROW, Potions.WEAKNESS),
    DAMAGE(8, Items.TIPPED_ARROW,Potions.HARMING),
    POISON(8, Items.TIPPED_ARROW,Potions.POISON);

    public final int amount;
    public final Item type;
    @Nullable public final Holder<Potion> effect;

    ArrowType(int amount, Item type, @Nullable Holder<Potion> effect) {
        this.amount = amount;
        this.type = type;
        this.effect = effect;
    }

    public ItemStack createStack() {
        ItemStack stack = new ItemStack(type);
        if(type == Items.ARROW){
            return stack;
        }
        else if(type == Items.TIPPED_ARROW && effect != null){
            stack.set(DataComponents.POTION_CONTENTS,new PotionContents(effect));
            return stack;
        }
        throw new IllegalStateException(type + " is not a valid arrow type");
    }
}
