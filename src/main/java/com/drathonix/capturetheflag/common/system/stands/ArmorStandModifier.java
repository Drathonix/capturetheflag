package com.drathonix.capturetheflag.common.system.stands;

import net.minecraft.world.entity.decoration.ArmorStand;

public interface ArmorStandModifier {
    void onTick(ArmorStand stand, String marker);
    void onLoad(ArmorStand stand, String marker);
}
