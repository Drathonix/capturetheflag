package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.phasing.PhaseFlag;
import com.vicious.persist.annotations.Save;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;

public class ProtectedRegion {
    @Save
    public Type type;
    @Save
    public BoundingBox aabb;
    @Nullable
    @Save(raw=true)
    public TeamState team;

    public ProtectedRegion(Type type, BoundingBox aabb) {
        this.type = type;
        this.aabb = aabb;
    }

    public ProtectedRegion(Type type, BoundingBox aabb, @Nullable TeamState team) {
        this.type = type;
        this.aabb = aabb;
        this.team=team;
    }

    public ProtectedRegion(){}

    public enum Type {
        GENERIC_AREA_PROTECTOR,
        TERRITORY{
            @Override
            public boolean allowEntry(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer player) {
                if(region.team != TeamState.get(player)){
                    return !GameDataCache.getGamePhase().flags.contains(PhaseFlag.RESTRICTED);
                }
                return true;
            }

            @Override
            public boolean allowHurtByEntity(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer inside, Entity outside) {
                if (outside instanceof ServerPlayer) {
                    return !GameDataCache.getGamePhase().flags.contains(PhaseFlag.RESTRICTED);
                }
                return true;
            }

            @Override
            public boolean allowNaturalBlockChange() {
                return true;
            }

            @Override
            public boolean allowBlockPlace(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer player) {
                return true;
            }

            @Override
            public boolean allowBlockInteract(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer player) {
                return true;
            }

            @Override
            public boolean allowBlockBreak(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer player) {
                return true;
            }

            @Override
            public boolean allowBlockExplode(BlockGetter blockGetter, BlockPos blockPos) {
                return true;
            }

            @Override
            public boolean blockStructures() {
                return false;
            }
        },
        HOLY_ENCHANTER{
            @Override
            public boolean allowBlockInteract(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer player) {
                return true;
            }
        },
        PARKOUR_ZONE{
            @Override
            public boolean allowBlockInteract(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer player) {
                return true;
            }
        },
        SPAWN_ZONE{
            //Ban attacking players within the spawn area
            @Override
            public boolean allowHurtByEntity(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer inside, Entity outside) {
                return false;
            }

            //Prevent players within a spawn-zone from attacking outsiders.
            @Override
            public boolean allowAttackOther(ServerLevel level, BlockPos pos, ProtectedRegion region, Entity inside, Entity outside) {
                return !(outside instanceof ServerPlayer);
            }

            //Only friendlies can enter this area.
            @Override
            public boolean allowEntry(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer player) {
                return region.team == TeamState.get(player);
            }
            //Allow friendlies to interact with blocks in the spawn zone. (the barrel)
            @Override
            public boolean allowBlockInteract(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer player) {
                return region.team == TeamState.get(player);
            }
        },
        FLAG{
            //Ban attacking players within the flag area while safe.
            @Override
            public boolean allowHurtByEntity(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer inside, Entity outside) {
                return System.currentTimeMillis() <= CTFPlayerData.get(inside).safetyCooldownEnd;
            }
            //Only enemies can enter this area.
            @Override
            public boolean allowEntry(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer player) {
                return region.team != TeamState.get(player);
            }
        };

        public boolean allowBlockInteract(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer player) {
            return false;
        }

        public boolean allowBlockBreak(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer player) {
            return false;
        }

        public boolean allowBlockPlace(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer player) {
            return false;
        }

        public boolean allowHurtByEntity(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer inside, Entity outside){
            return true;
        }
        public boolean allowAttackOther(ServerLevel level, BlockPos pos, ProtectedRegion region, Entity inside, Entity outside){
            return true;
        }
        public boolean allowEntry(ServerLevel level, BlockPos pos, ProtectedRegion region, ServerPlayer player){
            return true;
        }

        public boolean allowNaturalBlockChange() {
            return false;
        }

        public boolean blockStructures() {
            return true;
        }

        public boolean allowBlockExplode(BlockGetter blockGetter, BlockPos blockPos) {
            return false;
        }
    }

}
