package com.drathonix.capturetheflag.common.system.phasing;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.GameDataCache;
import com.drathonix.capturetheflag.common.system.GameGenerator;
import com.drathonix.capturetheflag.common.system.TeamState;
import com.drathonix.capturetheflag.common.util.GeneralUtil;
import com.vicious.persist.annotations.Save;
import com.vicious.persist.annotations.Typing;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.scores.Team;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GamePhase {
    @Save
    public String key;
    @Save
    public String displayName;
    @Save
    public long periodSeconds;

    @Save
    @Typing(PhaseFlag.class)
    public Set<PhaseFlag> flags = new HashSet<>();

    @Save
    @Typing(Long.class)
    public Set<Long> warningIntervals = new HashSet<>();

    @Save
    public Component startMessage;
    @Save
    public Component endMessage;

    {
        warningIntervals.add(5L);
        warningIntervals.add(10L);
        warningIntervals.add(20L);
        warningIntervals.add(60L);
        warningIntervals.add(60L*2);
        warningIntervals.add(60L*5);
        warningIntervals.add(60L*10);
        warningIntervals.add(60L*15);
        warningIntervals.add(60L*20);
        warningIntervals.add(60L*30);
    }

    public static GamePhase of(String key) {
        GamePhase gamePhase = new GamePhase();
        gamePhase.key = key;
        return gamePhase;
    }

    public GamePhase displayName(String displayName) {
        this.displayName = displayName;
        startMessage = Component.literal(displayName + " has started and will last for " + GeneralUtil.convertSeconds(periodSeconds) + "!").withStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.GOLD));
        endMessage = Component.literal(displayName + " has ended!").withStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.RED));
        return this;
    }

    public GamePhase period(long periodSeconds) {
        this.periodSeconds = periodSeconds;
        return this;
    }

    public GamePhase flags(PhaseFlag... flags) {
        this.flags.addAll(List.of(flags));
        return this;
    }

    public void onTick(long periodSeconds){
        if(warningIntervals.contains(this.periodSeconds-periodSeconds)){
            timeWarning(periodSeconds);
        }
    }

    private void timeWarning(long periodSeconds) {
        GeneralUtil.sendToAllPlayers(Component.literal("There are " + GeneralUtil.convertSeconds(this.periodSeconds-periodSeconds) + " left of " + displayName).withStyle(ChatFormatting.GREEN));
    }

    public void onEnd() {
        GeneralUtil.sendToAllPlayers(endMessage);
        GeneralUtil.sendToAllPlayers(SoundEvents.ENDER_DRAGON_GROWL);
        if(flags.contains(PhaseFlag.FREEZE)){
            for (ServerPlayer player : CTF.server.getPlayerList().getPlayers()) {
                player.removeAllEffects();
            }
        }
    }

    public void onStart() {
        if(flags.contains(PhaseFlag.FREEZE)){
            for (ServerPlayer player : CTF.server.getPlayerList().getPlayers()) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (periodSeconds*20),255));
            }
        }
        if(flags.contains(PhaseFlag.WAITING_BOX)){
            GeneralUtil.sendToAllPlayers(Component.literal("Waiting for players! Set spawn to waiting box!").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
            GameGenerator.buildWaitingBox(CTF.server.overworld(), GameDataCache.center);
            return;
        }
        if(flags.contains(PhaseFlag.FINISHER)){
            GeneralUtil.sendToAllPlayers(Component.literal("Game Finished.").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
            GameDataCache.finish();
            return;
        }
        GeneralUtil.sendToAllPlayers(startMessage);
        if(flags.contains(PhaseFlag.INITIALIZER)) {
            GameDataCache.start();
            GeneralUtil.sendToAllPlayers(Component.literal("Welcome to Capture The Flag! Use /wiki for a link to your class' custom abilities and recipes. Good luck!").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
            return;
        }
        if(flags.contains(PhaseFlag.RESTRICTED)) {
            GeneralUtil.sendToAllPlayers(Component.literal("You cannot enter enemy territory during this time! Prepare your defenses and equipment!").withStyle(ChatFormatting.RED));
            for (TeamState value : TeamState.values()) {
                value.cooldownEnd=0;
            }
            for (ServerPlayer player : CTF.server.getPlayerList().getPlayers()) {
                CTFPlayerData.get(player).setHasFlag(false);
            }
        }
        else{
            GeneralUtil.sendToAllPlayers(Component.literal("You can now enter enemy territory! Capture the flag!").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
        }
        if(flags.contains(PhaseFlag.INFINITE_CLASS_SWAP)) {
            GeneralUtil.sendToAllPlayers(Component.literal("You can switch your class and skills during this phase (/class)").withStyle(ChatFormatting.GOLD));
        }
        if(flags.contains(PhaseFlag.TIME_LOSS)) {
            GeneralUtil.sendToAllPlayers(Component.literal("Capturing the flag during this phase will reduce the stage time by 5 minutes!").withStyle(ChatFormatting.GOLD));
        }
    }
}
