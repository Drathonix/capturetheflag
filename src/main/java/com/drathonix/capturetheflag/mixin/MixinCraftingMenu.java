package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.config.CTFConfig;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(CraftingMenu.class)
public class MixinCraftingMenu {
    /*@Redirect(method = "slotChangedCraftingGrid",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;"))
    private static <T extends Recipe<I>,I extends RecipeInput> Optional<RecipeHolder<CraftingRecipe>> hideProtecteds(RecipeManager instance, RecipeType<T> arg, I craftingInput, Level serverLevel, RecipeHolder<T> recipeHolder){
        Optional<RecipeHolder<CraftingRecipe>> optional = serverLevel.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, (CraftingInput) craftingInput, serverLevel, (RecipeHolder<CraftingRecipe>) recipeHolder);
        if(optional.isPresent()){
            ResourceLocation id = optional.get().id().location();
            if(CTFConfig.protectedRecipes.contains(id)){
                if(player instanceof Server)
            }
            else{
                return optional;
            }
        }
        return optional;
    }*/

    @Inject(method = "slotChangedCraftingGrid",at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z"),locals = LocalCapture.CAPTURE_FAILSOFT,cancellable = true)
    private static void hideProtecteds(AbstractContainerMenu abstractContainerMenu, ServerLevel serverLevel, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer, RecipeHolder<CraftingRecipe> recipeHolder, CallbackInfo ci, CraftingInput craftingInput, ServerPlayer serverPlayer, ItemStack itemStack, Optional<RecipeHolder<CraftingRecipe>> optional){
        if(optional.isPresent()){
            ResourceLocation id = optional.get().id().location();
            if(CTFConfig.protectedRecipes.contains(id)){
                ClassType type = CTFPlayerData.get(serverPlayer).getClassType();
                if(type != null && type.recipes.contains(id)){
                    return;
                }
                ci.cancel();
            }
        }
    }
}
