package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.config.ItemsConfig;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThrownTrident.class)
public abstract class MixinThrownTrident extends MixinAbstractArrow {
    @Redirect(method = "onHitEntity",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    public boolean pierceDamage(Entity instance, DamageSource source, float v) {
        float multi = ItemsConfig.tridentArmorPierce;
        if(instance.hurtOrSimulate(source, 0.01F) && instance instanceof LivingEntity le){
            if(getOwner() instanceof ServerPlayer sp){
                ClassType type = CTFPlayerData.get(sp).getClassType();
                if(type == ClassType.RANGER){
                    multi = ItemsConfig.tridentArmorPierceRanger;
                }
            }
            instance.hurtOrSimulate(damageSources().magic(),Math.min(le.getHealth(),v*multi));
        }
        return instance.hurtOrSimulate(source, (v-0.01F)*(1F-multi));
    }
}
