package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.bridge.IMixinInventory;
import com.drathonix.capturetheflag.common.component.CustomDatas;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;

@Mixin(Inventory.class)
public class MixinInventory implements IMixinInventory {
    @Shadow @Final private List<NonNullList<ItemStack>> compartments;

    @Shadow @Final public Player player;

    @Shadow @Final public NonNullList<ItemStack> items;

    @Override
    public void dropSpecific() {
        for (NonNullList<ItemStack> list : this.compartments) {
            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemstack = list.get(i);
                int sb = CustomDatas.getSoulBound(itemstack);
                if (sb > 0) {
                    CustomDatas.setSoulBound(itemstack, sb - 1);
                    ItemLore original = itemstack.get(DataComponents.LORE);
                    if (original != null) {
                        ItemLore lore = ItemLore.EMPTY;
                        if (sb - 1 > 0) {
                            lore = CustomDatas.addSoulBoundLore(lore,sb-1);
                        } else {
                            lore = lore.withLineAdded(Component.literal("Vanishing Curse").withStyle(Style.EMPTY.withColor(ChatFormatting.RED).withBold(true).withItalic(false)));
                        }
                        List<Component> comps = original.styledLines();
                        for (int j = 1; j < comps.size(); j++) {
                            lore = lore.withLineAdded(comps.get(j));
                        }
                        itemstack.set(DataComponents.LORE, lore);
                        list.set(i,itemstack);
                    }
                } else {
                    sb = CustomDatas.getLesserSoulBound(itemstack);
                    if (sb > 0) {
                        CustomDatas.setLesserSoulBound(itemstack, sb - 1);
                        ItemLore original = itemstack.get(DataComponents.LORE);
                        if (original != null) {
                            ItemLore lore = ItemLore.EMPTY;
                            if (sb - 1 > 0) {
                                lore = CustomDatas.addSoulBoundLore(lore, sb - 1);
                            }
                            List<Component> comps = original.styledLines();
                            for (int j = 1; j < comps.size(); j++) {
                                lore = lore.withLineAdded(comps.get(j));
                            }
                            itemstack.set(DataComponents.LORE, lore);
                            list.set(i,itemstack);
                        }
                    } else if (!itemstack.isEmpty()) {
                        this.player.drop(itemstack, true, false);
                        list.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
}
