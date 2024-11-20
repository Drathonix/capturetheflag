package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NearestAttackableTargetGoal.class)
public class MixinNearestAttackableTargetGoal {
    @Redirect(method="findTarget",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getNearestPlayer(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;Lnet/minecraft/world/entity/LivingEntity;DDD)Lnet/minecraft/world/entity/player/Player;"))
    public Player handleSpecialDetection(ServerLevel instance, TargetingConditions targetingConditions, LivingEntity livingEntity, double v, double g, double h){
        Player plr = instance.getNearestPlayer(targetingConditions, livingEntity,v,g,h);
        if(plr instanceof ServerPlayer player) {
            CTFPlayerData data = CTFPlayerData.get(player);
            if(data.getClassType() != null){
                float chance = data.getClassType().mobTargetChance;
                if(chance < 1F){
                    if(Math.random() > chance){
                        return null;
                    }
                }
            }
        }
        return plr;
    }
}
