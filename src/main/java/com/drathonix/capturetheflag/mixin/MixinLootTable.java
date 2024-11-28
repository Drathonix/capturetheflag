package com.drathonix.capturetheflag.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(LootTable.class)
public class MixinLootTable {
    @Redirect(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V"))
    public void removeBanned(LootTable instance, LootContext lootContext, Consumer<ItemStack> consumer){
        instance.getRandomItems(lootContext, stack -> {
            if(stack.getItem() == Items.SADDLE){
                consumer.accept(new ItemStack(Items.LAPIS_LAZULI,32));
            }
            else {
                consumer.accept(stack);
            }
        });
    }
}
