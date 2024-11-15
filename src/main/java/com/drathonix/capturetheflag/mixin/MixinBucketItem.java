package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.system.CustomItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class MixinBucketItem {
    @Inject(method = "getEmptySuccessItem",at = @At("HEAD"), cancellable = true)
    private static void bigBucket(ItemStack itemStack, Player player, CallbackInfoReturnable<ItemStack> cir){
        if(CustomItem.BIGBUCKET.is(itemStack)){
            cir.setReturnValue(itemStack.copy());
        }
    }
}
