package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.config.CTFConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CrafterBlock.class)
public class MixinCrafterBlock {
    @Inject(method = "getPotentialResults",at = @At("RETURN"), cancellable = true)
    private static void cancelProtected(ServerLevel serverLevel, CraftingInput craftingInput, CallbackInfoReturnable<Optional<RecipeHolder<CraftingRecipe>>> cir){
        Optional<RecipeHolder<CraftingRecipe>> opt = cir.getReturnValue();
        if(opt.isPresent()){
            RecipeHolder<CraftingRecipe> recipe = opt.get();
            if(CTFConfig.protectedRecipes.contains(recipe.id().location())){
                cir.setReturnValue(Optional.empty());
            }
        }
    }
}
