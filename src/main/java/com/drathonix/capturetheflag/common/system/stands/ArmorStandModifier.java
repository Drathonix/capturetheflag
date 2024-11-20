package com.drathonix.capturetheflag.common.system.stands;

import com.drathonix.capturetheflag.common.system.GameDataCache;
import com.drathonix.capturetheflag.common.system.ProtectedRegion;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.function.Consumer;

public interface ArmorStandModifier {
    void onTick(ArmorStand stand, String marker);
    void onLoad(ArmorStand stand, String marker);
    default void viewProtectedRegions(ArmorStand stand, Consumer<ProtectedRegion> consumer){
        GameDataCache.viewProtectedRegionsAt(stand.blockPosition(),consumer);
    }
}
