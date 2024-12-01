package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.bridge.IMixinArmorStand;
import com.drathonix.capturetheflag.common.system.stands.ArmorStandMarkers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public abstract class MixinArmorStand extends MixinLivingEntity implements IMixinArmorStand {
    @Unique
    public String ctf$marker = null;
    @Unique
    public Vec3 ctf$startPosition = null;

    @Unique
    private static boolean performArmorStandOverwrite = true;

    @Unique
    private int ctf$ticks = 0;

    @Inject(method = "readAdditionalSaveData",at = @At("RETURN"))
    public void markSpecial(CompoundTag compoundTag, CallbackInfo ci){
        if(performArmorStandOverwrite && compoundTag.contains("CustomName")){
            String name = getCustomName().getString();
            if(name.startsWith("[") && name.endsWith("]")){
               ctf$marker = name.substring(1, name.length()-1);
               ArmorStandMarkers.add((ArmorStand)(Object)this,ctf$marker);
            }
        }
        if(ctf$isSpecial()){
            if(compoundTag.contains("ctf_start")){
                ctf$startPosition = Vec3.CODEC.decode(NbtOps.INSTANCE,compoundTag.get("ctf_start")).getOrThrow().getFirst();
            }
            else{
                ctf$startPosition=position();
            }
            ArmorStandMarkers.getModifier(ctf$marker).onLoad((ArmorStand)(Object)this,ctf$marker);
        }
    }

    @Inject(method = "addAdditionalSaveData",at = @At("RETURN"))
    public void addStartPos(CompoundTag compoundTag, CallbackInfo ci){
        if(ctf$startPosition != null){
            compoundTag.put("ctf_start",Vec3.CODEC.encodeStart(NbtOps.INSTANCE,ctf$startPosition).getOrThrow());
        }
    }

    @Inject(method = "tick",at = @At("RETURN"))
    public void handleSpecialTick(CallbackInfo ci){
        if(ctf$isSpecial()){
            ArmorStandMarkers.getModifier(ctf$marker).onTick((ArmorStand)(Object)this,ctf$marker);
        }
    }

    @Override
    public boolean ctf$isSpecial() {
        return ctf$marker != null;
    }

    @Inject(method = "hasPhysics",at = @At("HEAD"),cancellable = true)
    public void cancelPhysics(CallbackInfoReturnable<Boolean> cir){
        if(ctf$isSpecial()){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "ignoreExplosion",at = @At("HEAD"),cancellable = true)
    public void cancelExplo(CallbackInfoReturnable<Boolean> cir){
        if(ctf$isSpecial()){
            cir.setReturnValue(true);
        }
    }
    @Inject(method = "isMarker",at = @At("HEAD"),cancellable = true)
    public void marker(CallbackInfoReturnable<Boolean> cir){
        if(ctf$isSpecial()){
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "kill",at = @At("RETURN"))
    public void onKill(ServerLevel serverLevel, CallbackInfo ci){
        if(ctf$isSpecial()){
            ArmorStandMarkers.remove(ArmorStand.class.cast(this),ctf$marker);
        }
    }

    @Override
    public String ctf$getMarker() {
        return ctf$marker;
    }

    @Override
    public int ctf$getTicks() {
        return ctf$ticks;
    }

    @Override
    public void ctf$setTicks(int val) {
        this.ctf$ticks=val;
    }

    @Override
    public Vec3 ctf$getTruePosition() {
        return ctf$startPosition;
    }
}
