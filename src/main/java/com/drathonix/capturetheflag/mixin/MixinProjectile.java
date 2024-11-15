package com.drathonix.capturetheflag.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Projectile.class)
public abstract class MixinProjectile extends MixinEntity{
    @Shadow @Nullable
    public abstract Entity getOwner();
}
