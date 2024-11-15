package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.gui.HolyEnchanter;
import com.drathonix.finallib.common.inventory.wrapper.InventoryWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(EnchantingTableBlock.class)
public class MixinEnchantingTableBlock {
    @Inject(method = "getMenuProvider",at = @At("HEAD"),cancellable = true)
    public void intercept(BlockState blockState, Level level, BlockPos blockPos, CallbackInfoReturnable<MenuProvider> cir){
        cir.setReturnValue(new SimpleMenuProvider((id,inv,player)-> new HolyEnchanter(id,inv, InventoryWrapper.of(NonNullList.withSize(9*5, ItemStack.EMPTY),0)), Component.literal("Holy Enchanter").withStyle(Style.EMPTY.withBold(true).withColor(Color.magenta.getRGB()))));
    }
}
