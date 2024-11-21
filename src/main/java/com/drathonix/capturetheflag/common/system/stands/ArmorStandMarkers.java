package com.drathonix.capturetheflag.common.system.stands;

import com.drathonix.capturetheflag.common.system.TeamState;
import net.minecraft.world.entity.decoration.ArmorStand;

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
        return modifiers.get(marker);
    }

    static {
        registerModifier(blueFlag, new FlagStand(TeamState.BLUE));
        registerModifier(redFlag, new FlagStand(TeamState.RED));
        registerModifier(blueSpawn, new SpawnStand(TeamState.BLUE));
        registerModifier(redSpawn, new SpawnStand(TeamState.RED));
        registerModifier(clearBeacon, new BeaconStand());
    }
}
