package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.system.CTFEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FlowingFluid.class)
public abstract class MixinFlowingFluid {
    @Shadow protected abstract void spreadTo(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState);

    @Redirect(method = "spread",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V"))
    public void preventSpreadIntoBadArea(FlowingFluid instance, LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState){
        if(levelAccessor instanceof ServerLevel sl && CTFEventHandler.shouldCancelBlockEvent(sl,blockPos)){
            return;
        }
        spreadTo(levelAccessor,blockPos,blockState,direction,fluidState);
    }

    @Redirect(method = "spreadToSides",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V"))
    public void preventSpreadIntoBadArea2(FlowingFluid instance, LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState){
        if(levelAccessor instanceof ServerLevel sl && CTFEventHandler.shouldCancelBlockEvent(sl,blockPos)){
            return;
        }
        spreadTo(levelAccessor,blockPos,blockState,direction,fluidState);
    }
}
