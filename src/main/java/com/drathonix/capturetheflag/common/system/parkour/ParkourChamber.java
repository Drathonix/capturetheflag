package com.drathonix.capturetheflag.common.system.parkour;

import com.drathonix.capturetheflag.common.component.CustomDatas;
import com.drathonix.capturetheflag.common.config.CTFConfig;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.CustomItem;
import com.drathonix.capturetheflag.common.system.TeamState;
import com.vicious.persist.annotations.Save;
import com.vicious.persist.annotations.Typing;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

import java.util.*;

public class ParkourChamber {
    @Save
    public BlockPos start;
    @Save
    public BlockPos crate;
    @Save
    public ParkourDifficulty difficulty;
    @Save
    public AABB crateAABB;
    @Save
    public AABB startAABB;
    @Save
    public AABB parkourBoundingBox;
    @Save
    @Typing(Boolean.class)
    private List<Boolean> completedByTeams = new ArrayList<>(List.of(false,false));

    private Map<UUID,Long> timeouts = new HashMap<>();
    private Map<UUID,Integer> attempts = new HashMap<>();

    public ParkourChamber(){

    }

    public ParkourChamber(BlockPos start, BlockPos crate, AABB parkourBoundingBox, ParkourDifficulty difficulty) {
        this.start = start;
        this.crate = crate;
        this.crateAABB = new AABB(crate.above());
        this.startAABB = new AABB(start.above()).inflate(1);
        this.parkourBoundingBox = parkourBoundingBox;
        this.difficulty = difficulty;
    }

    public void tick(ServerLevel level){
        level.getEntitiesOfClass(ServerPlayer.class, parkourBoundingBox).forEach(sp -> {
            CTFPlayerData data = CTFPlayerData.get(sp);
            if(sp.isInLava()){
                incrementAttemptsAndTeleportToStart(sp);
                int atmps = difficulty.attemptsPerMinute-attempts.getOrDefault(sp.getUUID(),0);
                if(atmps > 0) {
                    sp.sendSystemMessage(Component.literal("You have failed the parkour. You have " + atmps + " attempts left.").withStyle(ChatFormatting.RED));
                    sp.playNotifySound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.MASTER,1f,1f);
                }
                else{
                    sp.sendSystemMessage(Component.literal("You have failed the parkour. You need to wait " + getTimeout()/1000 + " seconds before attempting again.")
                            .withStyle(ChatFormatting.RED)
                            .withStyle(ChatFormatting.BOLD));
                    sp.playNotifySound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.MASTER,1f,1f);
                    attempts.put(sp.getUUID(), 0);
                }
                return;
            }
            if(startAABB.intersects(sp.getBoundingBox())){
                if(data.hasParkourBundle()){
                    unlockBundle(sp);
                }
                return;
            }
            if(data.getTeamState() != null && completedByTeams.get(data.getTeamState().ordinal())){
                sp.sendSystemMessage(Component.literal("Your team has already completed this parkour.").withStyle(ChatFormatting.RED));
                sp.playNotifySound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.MASTER,1f,1f);
                sendToStart(sp);
                return;
            }
            if(onTimeout(sp)){
                sp.sendSystemMessage(Component.literal("You can't attempt this parkour for " + ((timeouts.get(sp.getUUID())-System.currentTimeMillis())/1000) + " seconds.").withStyle(ChatFormatting.RED));
                sp.playNotifySound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.MASTER,1f,1f);
                sendToStart(sp);
                return;
            }
        });
        level.getEntitiesOfClass(ServerPlayer.class, crateAABB).forEach(sp -> {
            CTFPlayerData data = CTFPlayerData.get(sp);
            if(!data.hasParkourBundle()){
                if(sp.getInventory().add(difficulty.generateRewardBundle())) {
                    data.setHasParkourBundle(true);
                    sp.sendSystemMessage(Component.literal("You have obtained a reward bundle. Make it back to the start to unlock it").withStyle(ChatFormatting.GREEN));
                    sp.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER,1f,1f);
                }
            }
        });
    }

    private void unlockBundle(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if(CustomItem.PARKOUR_BUNDLE.is(stack) && CustomDatas.getLocked(stack)){
                TeamState teamState = TeamState.get(player);
                if(teamState != null){
                    completedByTeams.set(teamState.ordinal(), true);
                }
                CustomDatas.setLocked(stack,false);
                inventory.setItem(i,stack);
                player.sendSystemMessage(Component.literal("Congratulations on completing the " + difficulty.saveName() + " parkour! Your reward bundle has been unlocked!").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER,1f,1f);
                CTFPlayerData.get(player).setHasParkourBundle(false);
                return;
            }
        }
        for (ServerPlayer other : player.serverLevel().getEntitiesOfClass(ServerPlayer.class, parkourBoundingBox)) {
            if(other != player){
                removeBundle(other);
                sendToStart(other);
                player.sendSystemMessage(Component.literal("Another player completed the parkour first.").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
            }
        }
    }

    private boolean onTimeout(ServerPlayer player){
        if(timeouts.containsKey(player.getUUID())){
            return System.currentTimeMillis() < timeouts.get(player.getUUID());
        }
        return false;
    }

    private void sendToStart(ServerPlayer player){
        Vec3 dest = start.getCenter();
        player.teleportTo(dest.x,dest.y,dest.z);
        player.setRemainingFireTicks(0);
    }

    private void putOnTimeout(ServerPlayer player){
        timeouts.put(player.getUUID(), System.currentTimeMillis()+getTimeout());
    }

    private long getTimeout(){
        return CTFConfig.parkourTimeout*1000;
    }

    private void incrementAttemptsAndTeleportToStart(ServerPlayer player){
        attempts.put(player.getUUID(),attempts.getOrDefault(player.getUUID(),0)+1);
        if(attempts.get(player.getUUID()) >= difficulty.attemptsPerMinute){
            putOnTimeout(player);
        }
        removeBundle(player);
        sendToStart(player);
    }

    private void removeBundle(ServerPlayer player) {
        CTFPlayerData data = CTFPlayerData.get(player);
        if(data.hasParkourBundle()){
            data.setHasParkourBundle(false);
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if(CustomDatas.getLocked(stack) && CustomItem.PARKOUR_BUNDLE.is(stack)){
                    inventory.setItem(i,ItemStack.EMPTY);
                    break;
                }
            }
        }
    }
}
