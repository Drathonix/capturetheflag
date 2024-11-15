package com.drathonix.capturetheflag.common.injected;

import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.bridge.IMixinServerPlayer;
import com.drathonix.capturetheflag.common.system.TeamState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CTFPlayerData {
    private TeamState teamState;
    private ClassType classType;
    private boolean hasFlag;
    public long homeCooldown = 0;
    public boolean allowChangeClass = true;

    public @Nullable ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public @Nullable TeamState getTeamState() {
        return teamState;
    }

    public void setTeamState(TeamState teamState) {
        this.teamState = teamState;
    }

    public @NotNull CompoundTag writeNBT(){
        CompoundTag tag = new CompoundTag();
        if(teamState != null) {
            tag.putInt("team", teamState.ordinal());
        }
        if(classType != null) {
            tag.putInt("classtype", classType.ordinal());
        }
        tag.putBoolean("hasflag", hasFlag);
        tag.putBoolean("allowchangeclass", allowChangeClass);
        return tag;
    }

    public void readNBT(@NotNull CompoundTag tag){
        if(tag.contains("team")) {
            teamState = TeamState.values()[tag.getInt("team")];
        }
        if(tag.contains("classtype")) {
            classType = ClassType.values()[tag.getInt("classtype")];
        }
        hasFlag = tag.getBoolean("hasflag");
        allowChangeClass = tag.getBoolean("allowchangeclass");
    }

    public static @NotNull CTFPlayerData get(ServerPlayer plr){
        if(plr instanceof IMixinServerPlayer mixin){
            return mixin.ctf$getData();
        }
        throw new IllegalStateException("IMixinServerPlayer not injected.");
    }

    public void requireClassType(Consumer<ClassType> consumer) {
        if(classType != null) {
            consumer.accept(classType);
        }
    }

    public void requireTeam(Consumer<TeamState> consumer) {
        if(teamState != null) {
            consumer.accept(teamState);
        }
    }

    public void setHasFlag(boolean b) {
        this.hasFlag=b;
    }

    public boolean hasFlag() {
        return hasFlag;
    }
}
