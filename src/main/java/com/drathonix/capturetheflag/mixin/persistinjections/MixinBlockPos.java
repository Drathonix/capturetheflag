package com.drathonix.capturetheflag.mixin.persistinjections;

import com.vicious.persist.mappify.registry.Initializers;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockPos.class)
public class MixinBlockPos {
    static{
        Initializers.registerCustomConstructor(BlockPos.class,map->new BlockPos((Integer) map.get("x"), (Integer) map.get("y"), (Integer) map.get("z")));
    }
}
