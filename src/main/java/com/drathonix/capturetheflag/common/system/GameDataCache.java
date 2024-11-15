package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.phasing.GamePhase;
import com.drathonix.capturetheflag.common.system.phasing.GamePhaseConfig;
import com.drathonix.capturetheflag.common.util.GeneralUtil;
import com.vicious.persist.annotations.PersistentPath;
import com.vicious.persist.annotations.Save;
import com.vicious.persist.annotations.Typing;
import com.vicious.persist.mappify.registry.Stringify;
import com.vicious.persist.shortcuts.PersistShortcuts;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameDataCache {

    static {
        Stringify.register(AABB.class,str->{
            String[] values = str.split(",");
            return new AABB(Double.parseDouble(values[0]),
                    Double.parseDouble(values[1]),
                    Double.parseDouble(values[2]),
                    Double.parseDouble(values[3]),
                    Double.parseDouble(values[4]),
                    Double.parseDouble(values[5]));
        },aabb-> aabb.minX + "," + aabb.minY + "," + aabb.minZ + ","
                + aabb.maxX + "," + aabb.maxY + "," +aabb.maxZ);
        Stringify.register(BoundingBox.class,str->{
            String[] values = str.split(",");
            return new BoundingBox(Integer.parseInt(values[0]),
                    Integer.parseInt(values[1]),
                    Integer.parseInt(values[2]),
                    Integer.parseInt(values[3]),
                    Integer.parseInt(values[4]),
                    Integer.parseInt(values[5]));
        },aabb-> aabb.minX() + "," + aabb.minY() + "," + aabb.minZ() + ","
                + aabb.maxX() + "," + aabb.maxY() + "," +aabb.maxZ());
    }

    @Save
    @Typing(BoundingBox.class)
    public static List<BoundingBox> permanentStructureAABBs = new ArrayList<>();

    @Save
    public static int gamePhaseIndex = 0;

    @Save
    public static int periodSeconds = 0;

    @Save
    public static BlockPos center = new BlockPos(0,0,0);

    public static GamePhase getGamePhase(){
        if(gamePhaseIndex < 0 || gamePhaseIndex >= GamePhaseConfig.phases.size()){
            return new GamePhase();
        }
        return GamePhaseConfig.phases.get(gamePhaseIndex);
    }

    @PersistentPath
    public static String path = "vicious/cache/game_data.gon";

    public static void init(){
        PersistShortcuts.init(GameDataCache.class);
    }

    public static void save(){
        PersistShortcuts.saveAsFile(GameDataCache.class);
    }

    public static void clearAndSave(){
        permanentStructureAABBs.clear();
        PersistShortcuts.saveAsFile(GameDataCache.class);
    }

    public static void tickSecond(MinecraftServer server){
        GamePhase phase = getGamePhase();
        if(phase.periodSeconds == -1){
            return;
        }
        else if(periodSeconds >= phase.periodSeconds){
            phase.onEnd();
            gamePhaseIndex++;
            gamePhaseIndex = Math.min(gamePhaseIndex,GamePhaseConfig.phases.size());
            phase = getGamePhase();
            periodSeconds=0;
            phase.onStart();
        }
        else{
            phase.onTick(periodSeconds);
        }
        periodSeconds++;
    }

    private static long k = 0;
    public static void tick(MinecraftServer server) {
        long p = System.currentTimeMillis();
        if(k <= p){
            k=p+1000;
            tickSecond(server);
        }
    }

    public static void forceStart() {
        gamePhaseIndex++;
        gamePhaseIndex = Math.min(gamePhaseIndex,GamePhaseConfig.phases.size());
        getGamePhase().onStart();
    }

    public static void start() {
        GeneralUtil.sendToAllPlayers(Component.literal("Starting the game!").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
        GameGenerator.start(CTF.server.overworld(),center);
        for (TeamState value : TeamState.values()) {
            value.onStart();
        }
        for (ServerPlayer player : CTF.server.getPlayerList().getPlayers()) {
            player.getInventory().clearContent();
            if(CTFPlayerData.get(player).getTeamState() == null){
                player.setGameMode(GameType.SPECTATOR);
            }
            else{
                CTFPlayerData.get(player).requireClassType(c->{
                    CTF.respawn(player);
                });
            }
        }
        CTF.server.overworld().setDayTime(6000);
        CTF.server.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false,CTF.server);
        CTF.server.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false,CTF.server);
        CTF.server.overworld().resetWeatherCycle();
        periodSeconds=0;
    }

    public static void generateWaiting() {
        generateWaiting(new BlockPos(0,0,0));
    }

    public static void generateWaiting(BlockPos center) {
        GameDataCache.center = center;
        gamePhaseIndex=0;
        getGamePhase().onStart();
    }

    public static void forceFinish(){
        gamePhaseIndex = GamePhaseConfig.phases.size()-1;
        getGamePhase().onStart();
    }

    public static void finish() {
        getGamePhase().onStart();
        Component winner;
        if(TeamState.RED.flagsCaptured > TeamState.BLUE.flagsCaptured){
            winner = Component.literal("RED HAS WON THE GAME").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RED);
        }
        else if(TeamState.BLUE.flagsCaptured > TeamState.RED.flagsCaptured){
            winner = Component.literal("BLUE HAS WON THE GAME").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE);
        }
        else{
            winner = Component.literal("TIE.").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD);
        }
        for (ServerPlayer serverPlayer : CTF.server.getPlayerList().getPlayers()) {
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(winner));
            serverPlayer.playNotifySound(SoundEvents.WITHER_DEATH, SoundSource.MASTER,1f,1f);
        }
    }
}
