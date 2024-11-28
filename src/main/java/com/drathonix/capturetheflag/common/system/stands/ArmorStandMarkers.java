package com.drathonix.capturetheflag.common.system.stands;

import com.drathonix.capturetheflag.common.system.TeamState;
import com.drathonix.capturetheflag.common.system.parkour.ParkourDifficulty;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.apache.commons.lang3.builder.Diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ArmorStandMarkers {
    private static final Map<String, List<ArmorStand>> markedStands = new HashMap<>();
    private static final Map<String, ArmorStandModifier> modifiers = new HashMap<>();
    public static final String clearBeacon = "clear_air";
    public static final String blueFlag = "blue_flag";
    public static final String redFlag = "red_flag";
    public static final String blueSpawn = "blue_spawn";
    public static final String redSpawn = "red_spawn";
    public static final String parkourCrate = "parkour_crate";
    public static final String parkourStartEasy = "pk_easy";
    public static final String parkourStartMedium = "pk_medium";
    public static final String parkourStartHard = "pk_hard";
    public static final String parkourStartBrutal = "pk_brutal";
    public static final String parkourStartChampion = "pk_champion";


    public static synchronized void add(ArmorStand armorStand, String flag) {
        flag = cleanFlag(flag);
        markedStands.computeIfAbsent(flag, k -> new ArrayList<>()).add(armorStand);
    }

    public static synchronized void registerModifier(String marker, ArmorStandModifier modifier) {
        modifiers.put(marker, modifier);
    }

    public static void forEachStand(String marker, Predicate<ArmorStand> predicate) {
        List<ArmorStand> stands = markedStands.getOrDefault(marker, new ArrayList<>());
        for (int i = 0; i < stands.size(); i++) {
            ArmorStand stand = stands.get(i);
            if(stand.isRemoved()){
                stands.remove(i);
                i--;
            }
            else{
                if(predicate.test(stand)){
                    return;
                }
            }
        }
    }

    private static String cleanFlag(String flag) {
        if(flag.startsWith("[") && flag.endsWith("]")) {
            return flag.substring(1, flag.length() - 1);
        }
        return flag;
    }

    public static ArmorStandModifier getModifier(String marker) {
        return modifiers.getOrDefault(marker,ArmorStandModifier.NOTHING);
    }

    static {
        registerModifier(blueFlag, new FlagStand(TeamState.BLUE));
        registerModifier(redFlag, new FlagStand(TeamState.RED));
        registerModifier(blueSpawn, new SpawnStand(TeamState.BLUE));
        registerModifier(redSpawn, new SpawnStand(TeamState.RED));
        registerModifier(clearBeacon, new BeaconStand());
        registerModifier(parkourStartEasy, new ParkourStart(ParkourDifficulty.EASY));
        registerModifier(parkourStartMedium, new ParkourStart(ParkourDifficulty.MEDIUM));
        registerModifier(parkourStartHard, new ParkourStart(ParkourDifficulty.HARD));
        registerModifier(parkourStartBrutal, new ParkourStart(ParkourDifficulty.BRUTAL));
        registerModifier(parkourStartChampion, new ParkourStart(ParkourDifficulty.CHAMPION));
        registerModifier(parkourCrate, new ParkourCrate());
    }
}
