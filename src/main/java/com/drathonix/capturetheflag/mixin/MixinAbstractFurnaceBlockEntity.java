package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.bridge.IMixinAbstractFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class MixinAbstractFurnaceBlockEntity implements IMixinAbstractFurnaceBlockEntity {
    @Unique
    @Nullable
    private UUID ctf$owner = null;

   /* @Inject(method = "saveAdditional",at = @At("RETURN"))
    public void customDataSave(CompoundTag compoundTag, HolderLookup.Provider provider, CallbackInfo ci){
        if(ctf$owner != null) {
       //     compoundTag.putUUID("ctf_owner", ctf$owner);
        //}
    }
    @Inject(method = "loadAdditional",at = @At("RETURN"))
    public void customDataLoad(CompoundTag compoundTag, HolderLookup.Provider provider, CallbackInfo ci) {
       // if(compoundTag.contains("ctf_owner")){
       //     ctf$owner = compoundTag.getUUID("ctf_owner");
       // }
    }

    @Inject(method = "<init>",at = @At("RETURN"))
    public void customDataInit(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState, RecipeType recipeType, CallbackInfo ci){
        //ctf$owner = IMixinAbstractFurnaceBlockEntity.ref.get(blockPos.asLong());
    }*/


    @Inject(method = "getTotalCookTime",at = @At("RETURN"), cancellable = true)
    private static void minerBoost(ServerLevel serverLevel, AbstractFurnaceBlockEntity abstractFurnaceBlockEntity, CallbackInfoReturnable<Integer> cir){
        //if(abstractFurnaceBlockEntity instanceof IMixinAbstractFurnaceBlockEntity mixin && mixin.ctf$getOwner() != null) {
            cir.setReturnValue(cir.getReturnValueI() / 3);
       // }
    }

    @Override
    public @Nullable UUID ctf$getOwner() {
        return ctf$owner;
    }

    @Override
    public void ctf$setOwner(@Nullable UUID ctf$owner) {
        this.ctf$owner = ctf$owner;
    }
}
