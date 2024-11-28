package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.Skill;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class MixinItemStack {
    @Inject(method="processDurabilityChange",at = @At("RETURN"), cancellable = true)
    public void fortify(int i, ServerLevel serverLevel, ServerPlayer serverPlayer, CallbackInfoReturnable<Integer> cir){
        if(cir.getReturnValue() > 0){
            if(CTFPlayerData.get(serverPlayer).hasSkill(Skill.FORTIFIER)){
                float chance = CTFPlayerData.get(serverPlayer).getClassType() == Skill.FORTIFIER.classType ? 0.4F : 0.3F;
                if(Math.random() < chance){
                    cir.setReturnValue(0);
                }
            }
        }
    }
}
