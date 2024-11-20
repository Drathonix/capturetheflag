package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.component.CustomDatas;
import com.drathonix.capturetheflag.common.util.regis.EnchantmentRetriever;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public enum CustomItem {
    NONE,
    BLASTPICKAXE{
        @Override
        public int maxifyEnchant(Holder<Enchantment> retriever, int n) {
            if(retriever.unwrapKey().get() == Enchantments.EFFICIENCY){
                return Math.min(n,2);
            }
            if(retriever.unwrapKey().get() == Enchantments.FORTUNE){
                return Math.min(n,1);
            }
            return n;
        }
    },
    BRICKLAYER,
    LUMBERJAXE,
    BIGBUCKET,
    MACE,
    REFLECTORSHIELD,
    TRIDENT,
    LONGBOW,
    CROSSBOW;

    public boolean is(DataComponentHolder holder){
        return this != NONE && CustomDatas.getCustomItem(holder) == this;
    }

    public void set(DataComponentHolder holder) {
        CustomDatas.setCustomItemType(holder,this);
    }

    public ResourceLocation recipeName(){
        return ResourceLocation.fromNamespaceAndPath("capturetheflag",name().toLowerCase());
    }

    public int maxifyEnchant(Holder<Enchantment> retriever, int n){
        return n;
    }
}
