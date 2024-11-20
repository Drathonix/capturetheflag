package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.config.CTFConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.chat.numbers.FixedFormat;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.List;

public class CTFScoreboard  {
    private static Objective timeObjective;

    public static void initialize(MinecraftServer server) {
        ServerScoreboard serverScoreboard = server.getScoreboard();
        timeObjective=serverScoreboard.getObjective("ctf");
        if (timeObjective == null) {
            timeObjective = serverScoreboard.addObjective("ctf", ObjectiveCriteria.DUMMY, Component.literal("Capture The Flag").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD), ObjectiveCriteria.RenderType.INTEGER, true, new StyledFormat(Style.EMPTY.withColor(ChatFormatting.GREEN).withBold(true)));
            serverScoreboard.setDisplayObjective(DisplaySlot.SIDEBAR,timeObjective);
            ScoreAccess access = server.getScoreboard().getOrCreatePlayerScore(ScoreHolder.forNameOnly("Flags To Win: "),timeObjective,true);
            access.set(CTFConfig.winCondition);
            access = server.getScoreboard().getOrCreatePlayerScore(ScoreHolder.forNameOnly("Red Flags: "),timeObjective,true);
            access.set(TeamState.RED.flagsCaptured);
            access = CTF.server.getScoreboard().getOrCreatePlayerScore(ScoreHolder.forNameOnly("Blue Flags: "),timeObjective,true);
            access.set(TeamState.BLUE.flagsCaptured);
        }
    }

    public static void tick(MinecraftServer server){
        ScoreAccess access = server.getScoreboard().getOrCreatePlayerScore(ScoreHolder.forNameOnly("Phase Time: "),timeObjective,true);
        access.set((int)GameDataCache.timeRemaining());
    }

    public static void updateSB(){
        ScoreAccess access = CTF.server.getScoreboard().getOrCreatePlayerScore(ScoreHolder.forNameOnly("Phase Time: "),timeObjective,true);
        access.set((int)GameDataCache.timeRemaining());
        access = CTF.server.getScoreboard().getOrCreatePlayerScore(ScoreHolder.forNameOnly("Red Flags: "),timeObjective,true);
        access.set(TeamState.RED.flagsCaptured);
        access = CTF.server.getScoreboard().getOrCreatePlayerScore(ScoreHolder.forNameOnly("Blue Flags: "),timeObjective,true);
        access.set(TeamState.BLUE.flagsCaptured);
    }
}
