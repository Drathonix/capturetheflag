package com.drathonix.capturetheflag.common.bridge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public interface IMixinArmorStand extends IMixinEntity{
    Vec3 ctf$getTruePosition();
    String ctf$getMarker();
    int ctf$getTicks();
    void ctf$setTicks(int val);

    default void ctf$resetTicker(){
        ctf$setTicks(0);
    }

    default void ctf$tick(){
        ctf$setTicks(ctf$getTicks() + 1);
    }
}
