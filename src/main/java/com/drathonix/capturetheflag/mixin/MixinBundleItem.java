package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.component.CustomDatas;
import com.drathonix.capturetheflag.common.system.CustomItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class MixinBundleItem {
    @Inject(method="dropContent(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Z",at = @At("HEAD"),cancellable = true)
    public void lockSystem(ItemStack itemStack, Player player, CallbackInfoReturnable<Boolean> cir){
        if(player instanceof ServerPlayer sp && CustomDatas.getLocked(itemStack)){
            cir.setReturnValue(false);
            sp.sendSystemMessage(Component.literal("This bundle is locked! Cannot open.").withStyle(ChatFormatting.RED));
        }
    }
    @Inject(method="dropContent(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Z",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;"))
    public void deleteBundleOnEmpty(ItemStack itemStack, Player player, CallbackInfoReturnable<Boolean> cir){
        if(CustomItem.PARKOUR_BUNDLE.is(itemStack)) {
            BundleContents contents = itemStack.get(DataComponents.BUNDLE_CONTENTS);
            if (contents != null && contents.isEmpty()) {
                Inventory inventory = player.getInventory();
                int slot = inventory.findSlotMatchingItem(itemStack);
                inventory.setItem(slot, ItemStack.EMPTY);
            }
        }
    }
}
