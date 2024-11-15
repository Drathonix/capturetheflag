package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.config.ItemsConfig;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.CustomItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ProjectileWeaponItem.class)
public abstract class MixinProjectileWeaponItem {
    @Shadow protected void shoot(ServerLevel arg, LivingEntity arg2, InteractionHand arg3, ItemStack arg4, List<ItemStack> list, float g, float h, boolean bl, LivingEntity arg5){

    }
    @Inject(method = "createProjectile",at = @At(value = "RETURN"))
    public void shootLongBow(Level level, LivingEntity livingEntity, ItemStack itemStack, ItemStack itemStack2, boolean bl, CallbackInfoReturnable<Projectile> cir){
        if(livingEntity instanceof ServerPlayer sp) {
            if(cir.getReturnValue() instanceof Arrow arrow) {
                CTFPlayerData dat = CTFPlayerData.get(sp);
                CTFPlayerData.get(sp).requireClassType(type->{
                    arrow.setBaseDamage(arrow.getBaseDamage()*type.arrowDamageMultiplier);
                });
                if (CustomItem.LONGBOW.is(itemStack)) {
                    if (dat.getClassType() == ClassType.RANGER) {
                        arrow.setBaseDamage(arrow.getBaseDamage() * ItemsConfig.longBowDamageMultiplierRanger);
                    } else {
                        arrow.setBaseDamage(arrow.getBaseDamage() * ItemsConfig.longBowDamageMultiplier);
                    }
                }
            }
        }
    }
}
