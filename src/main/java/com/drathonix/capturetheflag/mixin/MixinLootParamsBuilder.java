package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.Skill;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LootParams.Builder.class)
public abstract class MixinLootParamsBuilder {
    @Shadow @Final private ContextMap.Builder params;

    @Shadow @Final private ServerLevel level;

    @Shadow @Final private Map<ResourceLocation, LootParams.DynamicDrop> dynamicDrops;

    @Shadow public abstract <T> LootParams.Builder withParameter(ContextKey<T> par1, Object par2);

    @Inject(method = "create",at = @At(value = "RETURN"), cancellable = true)
    public void interceptCreation(ContextKeySet contextKeySet, CallbackInfoReturnable<LootParams> cir){
        Entity e = params.getOptionalParameter(LootContextParams.THIS_ENTITY);
        ItemStack stack = params.getOptionalParameter(LootContextParams.TOOL);
        if(e instanceof ServerPlayer sp && stack != null && !stack.isEmpty()){
            CTFPlayerData data = CTFPlayerData.get(sp);
            if(sp.getY() > 64){
                return;
            }
            if(data.hasSkill(Skill.FORTUNATE)){
                float chance = 0;
                if(sp.getY() < -20){
                    chance=0.5F;
                }
                else{
                    chance = (64F-(float)sp.getY())/84F;
                }
                if(data.getClassType() == Skill.FORTUNATE.classType){
                    chance*=2F;
                }
                if(Math.random() <= chance){
                    LootParams p = cir.getReturnValue();
                    stack = stack.copy();
                    Holder<Enchantment> holder = sp.server.registryAccess().get(Enchantments.FORTUNE).get();
                    int lvl = stack.getEnchantments().getLevel(holder)+1;
                    stack.enchant(holder,lvl);
                    this.params.withParameter(LootContextParams.TOOL,stack);
                    ContextMap contextMap = this.params.create(contextKeySet);
                    cir.setReturnValue(new LootParams(p.getLevel(),contextMap,dynamicDrops,p.getLuck()));
                }
            }
        }
    }
}
