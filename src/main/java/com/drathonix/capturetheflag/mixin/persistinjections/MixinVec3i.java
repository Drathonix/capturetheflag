package com.drathonix.capturetheflag.mixin.persistinjections;

import com.vicious.persist.annotations.Save;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3i.class)
public class MixinVec3i {
    @Save
    @Shadow
    private int x;
    @Save
    @Shadow
    private int y;
    @Save
    @Shadow
    private int z;
}
