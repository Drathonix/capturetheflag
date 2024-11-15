package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.bridge.IMixinInventory;
import com.drathonix.capturetheflag.common.component.CustomDatas;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;

@Mixin(Player.class)
public abstract class MixinPlayer extends MixinLivingEntity{
    @Shadow public abstract Inventory getInventory();

    @Redirect(method = "destroyVanishingCursedItems",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;has(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/component/DataComponentType;)Z"))
    public boolean handleCustomVanishTag(ItemStack stack, DataComponentType<?> arg){
        return EnchantmentHelper.has(stack,arg) || CustomDatas.getVanish(stack) || CustomDatas.getSoulBound(stack) == 0;
    }

    @Redirect(method="dropEquipment",at = @At(value="INVOKE",target="Lnet/minecraft/world/entity/player/Inventory;dropAll()V"))
    public void dropSpecific(Inventory instance){
        if(instance instanceof IMixinInventory mixin){
            mixin.dropSpecific();
        }
    }
}
