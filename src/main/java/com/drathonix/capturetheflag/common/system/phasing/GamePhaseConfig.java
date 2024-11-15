package com.drathonix.capturetheflag.common.system.phasing;

import com.vicious.persist.annotations.PersistentPath;
import com.vicious.persist.annotations.Save;
import com.vicious.persist.annotations.Typing;
import com.vicious.persist.shortcuts.PersistShortcuts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GamePhaseConfig {
    @PersistentPath
    public static String path = "config/capturetheflag/phases.txt";

    @Save
    @Typing(GamePhase.class)
    public static List<GamePhase> phases = new ArrayList<>();

    public static void init() {
        //PersistShortcuts.init(GamePhaseConfig.class);
    }

    static {
        phases.add(GamePhase.of("waiting")
                .period(-1)
                .displayName("Waiting")
                .flags(PhaseFlag.INFINITE_CLASS_SWAP,PhaseFlag.WAITING_BOX, PhaseFlag.INVULN));
        phases.add(GamePhase.of("starting")
                .period(30)
                .displayName("Starting")
                .flags(PhaseFlag.INFINITE_CLASS_SWAP,PhaseFlag.INITIALIZER,PhaseFlag.INVULN));
        phases.add(GamePhase.of("preparation1")
                .period(30*60)
                .displayName("Preparation")
                .flags(PhaseFlag.ONE_CLASS_SWAP,PhaseFlag.RESTRICTED,PhaseFlag.HOME,PhaseFlag.IN_PLAY));
        phases.add(GamePhase.of("combat1")
                .period(15*60)
                .displayName("Combat")
                .flags(PhaseFlag.TIME_LOSS,PhaseFlag.IN_PLAY));
        phases.add(GamePhase.of("preparation2")
                .period(20*60)
                .displayName("Recovery")
                .flags(PhaseFlag.ONE_CLASS_SWAP,PhaseFlag.RESTRICTED,PhaseFlag.HOME,PhaseFlag.IN_PLAY));
        phases.add(GamePhase.of("combat2")
                .period(30*60)
                .displayName("Combat")
                .flags(PhaseFlag.IN_PLAY));
        phases.add(GamePhase.of("finished")
                .period(-1)
                .displayName("Finished")
                .flags(PhaseFlag.FINISHER,PhaseFlag.HOME,PhaseFlag.INFINITE_CLASS_SWAP,PhaseFlag.IN_PLAY));
    }
}
