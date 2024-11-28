package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.system.GameDataCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerExplosion.class)
public class MixinServerExplosion {
    @Redirect(method = "calculateExplodedPositions",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ExplosionDamageCalculator;shouldBlockExplode(Lnet/minecraft/world/level/Explosion;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;F)Z"))
    public boolean protectFromExplo(ExplosionDamageCalculator instance, Explosion explosion, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, float f){
        boolean shouldExplode = GameDataCache.viewProtectedRegionsAt(blockPos,region->{
            if(!region.type.allowBlockExplode(blockGetter,blockPos)){
                return false;
            }
            return null;
        },()->true);
        return shouldExplode && instance.shouldBlockExplode(explosion,blockGetter,blockPos,blockState,f);
    }
}
