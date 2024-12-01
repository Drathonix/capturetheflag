package com.drathonix.capturetheflag.common.injected;

import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.bridge.IMixinServerPlayer;
import com.drathonix.capturetheflag.common.config.CTFConfig;
import com.drathonix.capturetheflag.common.config.SkillsConfig;
import com.drathonix.capturetheflag.common.system.Skill;
import com.drathonix.capturetheflag.common.system.TeamState;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class CTFPlayerData {
    private final Set<Skill> skills = new HashSet<>();
    private int maxSkills = 2;
    private TeamState teamState;
    private ClassType classType;
    private boolean hasFlag;
    public long homeCooldownEnd = 0;
    //Not saved.
    public long safetyCooldownEnd = -1;
    //Not saved.
    private boolean hasParkourBundle = false;

    public CTFPlayerData(){
        assignRandomSkills();
    }

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
        tag.putBoolean("hasparkourbundle", hasParkourBundle);
        tag.putInt("maxskills", maxSkills);
        tag.putLong("homecooldown", homeCooldownEnd);
        int[] skills = new int[this.skills.size()];
        int i = 0;
        for (Skill skill : this.skills) {
            skills[i]= skill.ordinal();
            i++;
        }
        tag.putIntArray("skills", skills);
        return tag;
    }

    public void readNBT(@NotNull CompoundTag tag){
        if(tag.contains("team")) {
            teamState = TeamState.values()[tag.getInt("team")];
        }
        if(tag.contains("classtype")) {
            classType = ClassType.values()[tag.getInt("classtype")];
        }
        if(tag.contains("hasflag")) {
            hasFlag = tag.getBoolean("hasflag");
        }
        if(tag.contains("hasparkourbundle")) {
            hasParkourBundle = tag.getBoolean("hasparkourbundle");
        }
        if(tag.contains("maxskills")) {
            maxSkills = tag.getInt("maxskills");
        }
        if(tag.contains("homecooldown")){
            homeCooldownEnd = tag.getLong("homecooldown");
        }
        if(tag.contains("skills")){
            skills.clear();
            int[] list = tag.getIntArray("skills");
            for (int i : list) {
                skills.add(Skill.values()[i]);
            }
        }
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
        this.safetyCooldownEnd = b ? System.currentTimeMillis()+CTFConfig.safetyDuration*1000L : -1;
    }

    public boolean hasFlag() {
        return hasFlag;
    }

    public boolean hasParkourBundle() {
        return hasParkourBundle;
    }

    public void setHasParkourBundle(boolean b) {
        this.hasParkourBundle=b;
    }

    public boolean hasSkill(Skill specialAbility) {
        return skills.contains(specialAbility);
    }

    public Collection<Skill> getSkills() {
        return skills;
    }

    public int getMaxSkills(){
        return maxSkills;
    }

    public void setMaxSkills(int skills){
        this.maxSkills=skills;
    }

    public void setSkills(Set<Skill> newSkills) {
        this.skills.clear();
        this.skills.addAll(newSkills);
    }


    public void assignRandomSkills() {
        skills.clear();
        int n = SkillsConfig.requiredNumberOfClassSkills;
        List<Skill> classSkills = new ArrayList<>(List.of(Skill.values()));
        classSkills.removeIf(s->s.classType != classType);
        while(n > 0 && !classSkills.isEmpty()){
            skills.add(classSkills.remove((int)(Math.random()*classSkills.size())));
            n--;
        }
        List<Skill> otherSkills = new ArrayList<>(List.of(Skill.values()));
        while(maxSkills > skills.size() && !otherSkills.isEmpty()){
            skills.add(otherSkills.remove((int)(Math.random()*otherSkills.size())));
        }
    }

    public void onRespawn() {
        hasParkourBundle=false;
        safetyCooldownEnd=-1;
    }
}
