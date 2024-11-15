package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.system.CustomItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemUtils.class)
public class MixinItemUtils {
    @Inject(method = "createFilledResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/item/ItemStack;",at = @At("HEAD"), cancellable = true)
    private static void bigBucket(ItemStack itemStack, Player player, ItemStack itemStack2, boolean bl, CallbackInfoReturnable<ItemStack> cir){
        if(CustomItem.BIGBUCKET.is(itemStack)){
            cir.setReturnValue(itemStack.copy());
        }
    }
}
