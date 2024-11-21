package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.bridge.IMixinEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity implements IMixinEntity {
    @Shadow @Nullable
    public abstract Component getCustomName();

    @Shadow public abstract Vec3 position();

    @Shadow public abstract DamageSources damageSources();

    @Shadow public abstract BlockPos blockPosition();

    @Shadow public abstract AABB getBoundingBox();

    @Shadow public abstract boolean onGround();

    @Inject(method = "isNoGravity",at = @At("HEAD"),cancellable = true)
    public void cancelPhysics(CallbackInfoReturnable<Boolean> cir){
        if(ctf$isSpecial()){
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isInvulnerable",at = @At("HEAD"),cancellable = true)
    public void isInvuln(CallbackInfoReturnable<Boolean> cir){
        if(ctf$isSpecial()){
            cir.setReturnValue(true);
        }
    }
}
