package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.system.CTFEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class MixinPistonBaseBlock {
    @Inject(method = "isPushable",at = @At("HEAD"),cancellable = true)
    private static void preventPushingStructureBlocks(BlockState blockState, Level level, BlockPos blockPos, Direction direction, boolean bl, Direction direction2, CallbackInfoReturnable<Boolean> cir){
        if(level instanceof ServerLevel sl && CTFEventHandler.shouldCancelBlockEvent(sl,blockPos)){
            cir.setReturnValue(false);
        }
    }
}
