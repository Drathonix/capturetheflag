package com.drathonix.capturetheflag.common.bridge;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface IMixinAbstractFurnaceBlockEntity {
    @Nullable
    UUID ctf$getOwner();
    void ctf$setOwner(UUID owner);

    Map<Long, UUID> ref = new Long2ObjectOpenHashMap<>();
}
