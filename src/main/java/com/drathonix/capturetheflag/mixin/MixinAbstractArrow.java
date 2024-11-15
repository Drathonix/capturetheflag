package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(AbstractArrow.class)
public abstract class MixinAbstractArrow extends MixinProjectile{
    @Redirect(method = "onHitEntity",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    public boolean damageBuff(Entity instance, DamageSource damageSource, float v){
        float[] v1 = new float[]{v};
        if(getOwner() instanceof ServerPlayer sp){
            CTFPlayerData.get(sp).requireClassType(type->{
                if(type != null){
                    v1[0]*=type.arrowDamageMultiplier;
                }
            });
        }
        return instance.hurtOrSimulate(damageSource, v1[0]);
    }
}
