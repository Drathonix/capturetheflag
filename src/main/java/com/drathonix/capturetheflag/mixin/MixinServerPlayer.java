package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.bridge.IMixinServerPlayer;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.*;
import com.drathonix.capturetheflag.common.system.phasing.PhaseFlag;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends MixinPlayer implements IMixinServerPlayer {
    @Shadow @Final public MinecraftServer server;
    @Shadow public ServerGamePacketListenerImpl connection;

    @Shadow public abstract ServerLevel serverLevel();

    @Shadow public abstract void teleportTo(double d, double e, double f);

    @Shadow public abstract void sendSystemMessage(Component arg);

    @Unique
    private CTFPlayerData ctf$pdata = new CTFPlayerData();
    @Unique
    @Nullable
    private BlockPos ctf$previous = null;

    @Inject(method = "readAdditionalSaveData",at = @At("HEAD"))
    public void injectRead(CompoundTag compoundTag, CallbackInfo ci){
        ctf$pdata.readNBT(compoundTag.getCompound("ctf_data"));
    }

    @Inject(method = "addAdditionalSaveData",at = @At("HEAD"))
    public void injectSave(CompoundTag compoundTag, CallbackInfo ci){
        compoundTag.put("ctf_data",ctf$pdata.writeNBT());
    }

    @Override
    public CTFPlayerData ctf$getData() {
        return ctf$pdata;
    }


    @Unique
    private int ctf$counter = 1;
    @Inject(method = "tick",at = @At("RETURN"))
    public synchronized void playerTick(CallbackInfo ci){
        ServerPlayer sp = (ServerPlayer) (Object) this;
        if(GameDataCache.getGamePhase().flags.contains(PhaseFlag.WAITING_BOX)){
            if(GameGenerator.waitingBox.aabb != null && !GameGenerator.waitingBox.aabb.intersects(getBoundingBox())){
                Vec3 dest = new Vec3(GameDataCache.center.atY(0).above(GameGenerator.waitingBox.y+1));
                teleportTo(dest.x,dest.y,dest.z);
            }
        }
        if(GameDataCache.getGamePhase().flags.contains(PhaseFlag.IN_PLAY)) {
            ctf$pdata.requireClassType(type -> {
                ctf$pdata.requireTeam(team -> {
                    if (ctf$counter % 20 == 0) {
                        for (SpecialAbility ability : type.abilities) {
                            ability.tickSecond(sp);
                        }
                    }
                    if (ctf$counter % 100 == 0) {
                        if (team.isWithinTerritory(sp)) {
                            type.territorialEffects.forEach((effect, power) -> {
                                if (power > 0) {
                                    sp.addEffect(new MobEffectInstance(effect.get(), 120, power - 1,false,false));
                                }
                            });
                        } else {
                            type.passiveEffects.forEach((effect, power) -> {
                                if (power > 0) {
                                    sp.addEffect(new MobEffectInstance(effect.get(), 120, power - 1,false,false));
                                }
                            });
                        }
                        if (ctf$pdata.hasFlag()) {
                            sp.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120, 0));
                        }
                        sp.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 0));
                    }
                });
                AttributeMap map = sp.getAttributes();
                if (!CustomItem.BRICKLAYER.is(sp.getOffhandItem())) {
                    if (map.hasModifier(Attributes.BLOCK_INTERACTION_RANGE, CTFAttribute.brickLayerRange.id())) {
                        Multimap<Holder<Attribute>, AttributeModifier> multimap = HashMultimap.create();
                        multimap.put(Attributes.BLOCK_INTERACTION_RANGE, CTFAttribute.brickLayerRange);
                        map.removeAttributeModifiers(multimap);
                    }
                    if (map.hasModifier(Attributes.BLOCK_INTERACTION_RANGE, CTFAttribute.lesserBrickLayerRange.id())) {
                        Multimap<Holder<Attribute>, AttributeModifier> multimap = HashMultimap.create();
                        multimap.put(Attributes.BLOCK_INTERACTION_RANGE, CTFAttribute.lesserBrickLayerRange);
                        map.removeAttributeModifiers(multimap);
                    }
                } else {
                    if (type == ClassType.ARCHITECT) {
                        if (!map.hasModifier(Attributes.BLOCK_INTERACTION_RANGE, CTFAttribute.brickLayerRange.id())) {
                            Multimap<Holder<Attribute>, AttributeModifier> multimap = HashMultimap.create();
                            multimap.put(Attributes.BLOCK_INTERACTION_RANGE, CTFAttribute.brickLayerRange);
                            map.addTransientAttributeModifiers(multimap);
                        }
                    } else {
                        if (!map.hasModifier(Attributes.BLOCK_INTERACTION_RANGE, CTFAttribute.lesserBrickLayerRange.id())) {
                            Multimap<Holder<Attribute>, AttributeModifier> multimap = HashMultimap.create();
                            multimap.put(Attributes.BLOCK_INTERACTION_RANGE, CTFAttribute.lesserBrickLayerRange);
                            map.addTransientAttributeModifiers(multimap);
                        }
                    }
                }

            });
            if (ctf$counter % 50 == 0) {
                AABB box = new AABB(blockPosition()).inflate(8);
                for (TeamState value : TeamState.values()) {
                    if (value.isWithinTerritory(box)) {
                        value.getQuadrant().selectEdges(blockPosition(), GameDataCache.center, box.contains(new Vec3(GameDataCache.center)), edgePosition -> {
                            edgePosition = edgePosition.above(2);
                            for (int i = -2; i < 3; i++) {
                                serverLevel().sendParticles((ServerPlayer) (Object) this, new DustParticleOptions(value.color(), 2f), true, edgePosition.getX(), edgePosition.getY() + i, edgePosition.getZ(), 1, 0.0f, 0.0f, 0.0f, 0.0f);
                            }
                        });
                    }
                }
            }
            ctf$pdata.requireTeam(team->{
                if(GameDataCache.viewProtectedRegionsAt(blockPosition(),region->region.type.allowEntry(serverLevel(),blockPosition(),region,sp) ? null : true,()->false)){
                    BlockPos spwn = team.getSpawn();
                    if (spwn != null) {
                        Vec3 dest = spwn.getCenter();
                        if (ctf$previous != null && GameDataCache.viewProtectedRegionsAt(ctf$previous,region->region.type.allowEntry(serverLevel(),ctf$previous,region,sp) ? null : false,()->true)) {
                            dest = new Vec3(ctf$previous);
                        }
                        teleportTo(dest.x, dest.y, dest.z);
                        sendSystemMessage(Component.literal("You cannot enter this area."));
                    }
                }
                if(ctf$pdata.hasFlag() && GameDataCache.viewProtectedRegionsAt(blockPosition(),region->{
                    if(region.team == team && region.type == ProtectedRegion.Type.SPAWN_ZONE){
                        return true;
                    }
                    return null;
                },()->false)){
                    team.captureTheFlag(sp);
                }
            });
            if (ctf$counter >= 1000) {
                ctf$counter = 1;
            }
            if(onGround()) {
                ctf$previous = blockPosition();
            }
            ctf$counter++;
        }
    }

    @Inject(method="restoreFrom", at=@At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setHealth(F)V",ordinal=1))
    public void restoreInventory(ServerPlayer serverPlayer, boolean bl, CallbackInfo ci){
        getInventory().replaceWith(serverPlayer.getInventory());
        this.ctf$pdata = CTFPlayerData.get(serverPlayer);
    }
}
