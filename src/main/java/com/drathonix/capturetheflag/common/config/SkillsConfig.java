package com.drathonix.capturetheflag.common.config;

import com.vicious.persist.annotations.PersistentPath;
import com.vicious.persist.annotations.Save;
import com.vicious.persist.shortcuts.PersistShortcuts;

public class SkillsConfig {
    @PersistentPath
    public static String path = "config/capturetheflag/skills.txt";

    public static int requiredNumberOfClassSkills=1;

    public static void init(){
        PersistShortcuts.init(SkillsConfig.class);
    }
}
