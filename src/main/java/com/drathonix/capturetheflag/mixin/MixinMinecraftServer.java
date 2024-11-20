package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.system.CTFScoreboard;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Inject(method = "<init>",at = @At("RETURN"))
    public void capture(Thread thread, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer dataFixer, Services services, ChunkProgressListenerFactory chunkProgressListenerFactory, CallbackInfo ci){
        CTF.server = MinecraftServer.class.cast(this);
    }

    @Inject(method="readScoreboard",at = @At("RETURN"))
    public void ensureCustomPresent(DimensionDataStorage dimensionDataStorage, CallbackInfo ci){
        CTFScoreboard.initialize(MinecraftServer.class.cast(this));
    }
}
