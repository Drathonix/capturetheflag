package com.drathonix.capturetheflag.common.bridge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public interface IMixinArmorStand extends IMixinEntity{
    Vec3 ctf$getTruePosition();
}
