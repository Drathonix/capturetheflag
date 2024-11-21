package com.drathonix.capturetheflag.common.util.regis;

import com.drathonix.capturetheflag.common.component.CustomDatas;
import com.vicious.persist.annotations.Save;
import com.vicious.persist.annotations.Typing;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class ItemStackRetriever {
    @Save
    public ItemRetriever item;
    @Save
    public int size;
    @Save
    @Typing({EnchantmentRetriever.class,Integer.class})
    public Map<EnchantmentRetriever, Integer> enchantments = new HashMap<>();
    @Save
    public boolean vanishing = false;

    public ItemStackRetriever(){}

    public ItemStackRetriever(ItemStack stack) {
        this.item = new ItemRetriever(BuiltInRegistries.ITEM.getKey(stack.getItem()));
        this.size=stack.getCount();
        for (Holder<Enchantment> enchantmentHolder : stack.getEnchantments().keySet()) {
            enchantments.put(new EnchantmentRetriever(enchantmentHolder),stack.getEnchantments().getLevel(enchantmentHolder));
        }
    }

    public ItemStackRetriever(ItemStack stack, boolean vanishing) {
        this.item = new ItemRetriever(BuiltInRegistries.ITEM.getKey(stack.getItem()));
        this.size=stack.getCount();
        this.vanishing = vanishing;
        for (Holder<Enchantment> enchantmentHolder : stack.getEnchantments().keySet()) {
            enchantments.put(new EnchantmentRetriever(enchantmentHolder),stack.getEnchantments().getLevel(enchantmentHolder));
        }
    }

    public ItemStack get() {
        ItemStack stack = new ItemStack(item.get(),size);
        for (EnchantmentRetriever enchantmentRetriever : enchantments.keySet()) {
            stack.enchant(enchantmentRetriever.get(),enchantments.get(enchantmentRetriever));
        }
        CustomDatas.setVanish(stack,vanishing);
        return stack;
    }
}
