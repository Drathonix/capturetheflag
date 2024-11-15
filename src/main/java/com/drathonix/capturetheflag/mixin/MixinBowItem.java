package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.config.ItemsConfig;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.CustomItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BowItem.class)
public class MixinBowItem extends MixinProjectileWeaponItem{

    @Redirect(method = "releaseUsing",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BowItem;shoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;Ljava/util/List;FFZLnet/minecraft/world/entity/LivingEntity;)V"))
    public void shootLongBow(BowItem instance, ServerLevel level, LivingEntity livingEntity, InteractionHand interactionHand, ItemStack stack, List list, float f, float g, boolean b, LivingEntity livingEntity2){
        if(livingEntity instanceof ServerPlayer sp && CustomItem.LONGBOW.is(stack)) {
                if (CTFPlayerData.get(sp).getClassType() == ClassType.RANGER) {
                    f = f * ItemsConfig.longBowVelocityMultiplierRanger;
                } else {
                    f = f * ItemsConfig.longBowVelocityMultiplier;
                }

        }
        super.shoot(level,livingEntity,interactionHand,stack,list,f,g,b,livingEntity2);
    }
}
