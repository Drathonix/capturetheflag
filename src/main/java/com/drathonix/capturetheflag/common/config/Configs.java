package com.drathonix.capturetheflag.common.config;

import com.drathonix.capturetheflag.common.system.GameDataCache;
import com.drathonix.capturetheflag.common.system.GameGenerator;
import com.drathonix.capturetheflag.common.system.phasing.GamePhaseConfig;

public class Configs {
    public static void reload(){
        CTFConfig.init();
        ClassesConfig.init();
        GameGenerator.init();
        ItemsConfig.init();
        GamePhaseConfig.init();
    }
}
