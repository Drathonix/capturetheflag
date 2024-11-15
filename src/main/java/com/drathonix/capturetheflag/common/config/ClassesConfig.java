package com.drathonix.capturetheflag.common.config;

import com.drathonix.capturetheflag.common.ClassType;
import com.vicious.persist.annotations.PersistentPath;
import com.vicious.persist.annotations.Save;
import com.vicious.persist.annotations.Typing;
import com.vicious.persist.shortcuts.PersistShortcuts;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassesConfig {
    @PersistentPath
    public static String path = "config/capturetheflag/classes.txt";

    @Save
    @Typing(ClassType.class)
    public static Set<ClassType> classes = new HashSet<>(List.of(ClassType.values()));

    @Save
    public static Component test = Component.literal("test");

    public static void init(){
        PersistShortcuts.init(ClassesConfig.class);
    }
}
