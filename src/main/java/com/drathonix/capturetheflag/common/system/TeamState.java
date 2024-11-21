package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.config.CTFConfig;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.phasing.PhaseFlag;
import com.drathonix.capturetheflag.common.util.GeneralUtil;
import com.drathonix.capturetheflag.common.util.Quadrant;
import com.vicious.persist.annotations.Save;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.commands.SetSpawnCommand;
import net.minecraft.server.commands.TitleCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public enum TeamState {
    RED(Quadrant.NORTHWEST),
    BLUE(Quadrant.SOUTHEAST);

    @NotNull
    private final Quadrant quadrant;
    @Save
    public int flagsCaptured = 0;
    @Save
    public long cooldownEnd = 0;
    @Save
    private int lapisCount = 0;

    @Nullable
    @Save
    public BlockPos spawn;

    TeamState(@NotNull Quadrant quadrant) {
        this.quadrant = quadrant;
    }

    public static @Nullable TeamState byQuadrant(Quadrant quadrant) {
        for (TeamState value : TeamState.values()) {
            if(value.quadrant == quadrant){
                return value;
            }
        }
        return null;
    }

    public int getLapis() {
        return lapisCount;
    }

    public void removeLapis(int amount) {
        lapisCount -= amount;
    }

    public void growLapis(int amount) {
        lapisCount += amount;
    }

    public @Nullable AABB getTerritory() {
        return quadrant.getBoundingBox();
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }

    public TeamState getOpposite(){
        return this == RED ? BLUE : RED;
    }

    public boolean isWithinTerritory(LivingEntity livingEntity) {
        if(quadrant.getBoundingBox() != null) {
            return quadrant.getBoundingBox().intersects(livingEntity.getBoundingBox());
        }
        return false;
    }

    public boolean isWithinTerritory(AABB box) {
        if(quadrant.getBoundingBox() != null) {
            return quadrant.getBoundingBox().intersects(box);
        }
        return false;
    }

    public void takeTheFlag(ServerPlayer player) {
        if(cooldownEnd == -1) {
            return;
        }
        if(System.currentTimeMillis() >= cooldownEnd){
            GeneralUtil.sendToAllPlayers(Component.literal(player.getGameProfile().getName() + " has stolen the " + this.name().toLowerCase()+ " flag! Get them!").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
            GeneralUtil.sendToAllPlayers(SoundEvents.ENDER_DRAGON_GROWL);
            CTFPlayerData.get(player).setHasFlag(true);
            cooldownEnd=-1;
        }
    }

    public void captureTheFlag(ServerPlayer player) {
        CTFPlayerData data = CTFPlayerData.get(player);
        if(data.hasFlag()){
            GeneralUtil.sendToAllPlayers(Component.literal(player.getGameProfile().getName() + " has captured the " + this.getOpposite().name().toLowerCase()+ " flag! Their flag cannot be stolen again for " + GeneralUtil.convertSeconds(CTFConfig.stealFlagCooldownTime) + "!").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
            GeneralUtil.sendToAllPlayers(SoundEvents.ENDER_DRAGON_GROWL);
            flagsCaptured++;
            this.getOpposite().cooldownEnd = System.currentTimeMillis()+CTFConfig.stealFlagCooldownTime*1000;
            if(GameDataCache.getGamePhase().flags.contains(PhaseFlag.TIME_LOSS)){
                GeneralUtil.sendToAllPlayers(Component.literal("The phase time has decreased by 5 minutes!").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                GameDataCache.periodSeconds+=300;
            }
            data.setHasFlag(false);
            if(flagsCaptured >= CTFConfig.winCondition){
                GameDataCache.forceFinish();
            }
            CTFScoreboard.updateSB();
        }
    }

    public void onStart(){
        for (ServerPlayer player : CTF.server.getPlayerList().getPlayers()) {
            if(get(player) == this) {
                onStart(player);
            }
        }
    }

    public @Nullable BlockPos getSpawn(){
        if(spawn != null){
            return spawn;
        }
        return null;
    }

    public int color() {
        return this == RED ? Color.RED.getRGB() : Color.BLUE.getRGB();
    }

    public boolean isWithinTerritory(BlockPos ctf$previous) {
        if(quadrant.getBoundingBox() != null) {
            return quadrant.getBoundingBox().contains(new Vec3(ctf$previous));
        }
        return false;
    }

    public void reset() {
        flagsCaptured=0;
        cooldownEnd=0;
        lapisCount=0;
    }

    public static @Nullable TeamState get(ServerPlayer player){
        return CTFPlayerData.get(player).getTeamState();
    }

    public void onStart(ServerPlayer player) {
        BlockPos spwn = getSpawn();
        if(spwn == null){
            throw new RuntimeException("Bad spawn");
        }
        Vec3 cnt = spwn.getCenter();
        player.setRespawnPosition(CTF.server.overworld().dimension(),spwn,0f,true,false);
        player.teleportTo(cnt.x,cnt.y,cnt.z);
    }
}
