package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.component.CustomDatas;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public enum CustomItem {
    NONE,
    BLASTPICKAXE,
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
}
