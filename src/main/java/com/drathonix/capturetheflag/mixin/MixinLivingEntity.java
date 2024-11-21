package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.bridge.IMixinServerPlayer;
import com.drathonix.capturetheflag.common.config.ItemsConfig;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.CustomItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends MixinEntity{

    @Shadow public abstract ItemStack getItemBlockingWith();

    @Shadow public abstract float getHealth();

    @Shadow public abstract boolean canAttack(LivingEntity arg);

    @Shadow public abstract void setHealth(float f);

    @Shadow public abstract void remove(Entity.RemovalReason arg);

    @Shadow protected abstract float getDamageAfterArmorAbsorb(DamageSource arg, float f);

    @Inject(method = "hurtServer",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"))
    public void reflectorShield(ServerLevel serverLevel, DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir){
        ItemStack itemStack = this.getItemBlockingWith();
        if(CustomItem.REFLECTORSHIELD.is(itemStack)){
            Entity e = damageSource.getDirectEntity();
            serverLevel.registryAccess().get(DamageTypes.THORNS).ifPresent(thorns->{
                float pwr = ItemsConfig.reflectorShieldPower;
                if(this instanceof IMixinServerPlayer mixin && mixin.ctf$getData().getClassType() == ClassType.SLAYER) {
                    pwr = ItemsConfig.reflectorShieldPowerWarrior;
                }
                e.hurtServer(serverLevel, new DamageSource(thorns, damageSource.getDirectEntity(), LivingEntity.class.cast(this)), f * pwr);
            });
        }
    }
    @SuppressWarnings("all")
    @Redirect(method="actuallyHurt",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    public synchronized float damageModification(LivingEntity instance, DamageSource damageSource, float f){
        if(damageSource != null && ServerPlayer.class.isInstance(this)){
            ServerPlayer sp = ServerPlayer.class.cast(this);
            CTFPlayerData data = CTFPlayerData.get(sp);
            ClassType type = data.getClassType();
            if(type != null) {
                if (damageSource.getEntity() instanceof Player) {
                    f*=type.playerDamageTakenMultiplier;
                }
                else{
                    f*=type.mobDamageTakenMultiplier;
                }
                if(damageSource.getDirectEntity() instanceof AbstractArrow) {
                    f*=type.arrowDamageTakenMultiplier;
                }
            }
        }
        return getDamageAfterArmorAbsorb(damageSource,f);
    }
}
