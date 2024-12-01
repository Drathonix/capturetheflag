package com.drathonix.capturetheflag.common.system.parkour;

import com.drathonix.capturetheflag.common.bridge.IMixinArmorStand;
import com.drathonix.capturetheflag.common.bridge.IMixinStructureTemplate;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.GameDataCache;
import com.drathonix.capturetheflag.common.system.GameGenerator;
import com.drathonix.capturetheflag.common.system.stands.ArmorStandMarkers;
import com.drathonix.capturetheflag.common.system.stands.ParkourStart;
import com.drathonix.capturetheflag.common.util.GeneralUtil;
import com.drathonix.finallib.common.util.weighted.WeightedList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class WaitingParkourChamber extends ParkourChamber {
    private final ServerLevel level;
    private final BlockPos pos;

    public WaitingParkourChamber(ServerLevel level, BlockPos pos){
        this.level = level;
        this.pos = pos;
    }

    @Nullable
    private BoundingBox generatedZone = null;
    public void ungenerate(){
        if(generatedZone != null){
            BlockState state = Blocks.AIR.defaultBlockState();
            for (int x = generatedZone.minX(); x <= generatedZone.maxX(); x++) {
                for (int y = generatedZone.minY(); y <= generatedZone.maxY(); y++) {
                    for (int z = generatedZone.minZ(); z <= generatedZone.maxZ(); z++) {
                        level.setBlockAndUpdate(new BlockPos(x,y,z), state);
                    }
                }
            }
            for (ArmorStand stand : level.getEntitiesOfClass(ArmorStand.class, parkourBoundingBox)) {
                stand.kill(level);
            }
        }
    }

    public void destroy(){
        ungenerate();
        GameDataCache.parkourChambers.remove(this);
    }

    private final ParkourGenerator.Selections selections = new ParkourGenerator.Selections();

    private final Random random = new Random();

    public void init(){
        regenerate();
        GameDataCache.parkourChambers.add(this);
    }

    public void regenerate(){
        ungenerate();
        StructureTemplate template = selections.select(random, new WeightedList<>(List.of(ParkourDifficulty.values())));
        BlockPos offset = new BlockPos(0,0,0);
        if(template instanceof IMixinStructureTemplate mixin){
            for (StructureTemplate.StructureEntityInfo structureEntityInfo : mixin.ctf$getStructureEntityInfo()) {
                if(structureEntityInfo.nbt.contains("CustomName") && structureEntityInfo.nbt.getString("CustomName").contains("pk_")){
                    offset=new BlockPos(-structureEntityInfo.blockPos.getX(),-structureEntityInfo.blockPos.getY(),-structureEntityInfo.blockPos.getZ());
                    break;
                }
            }
        }
        generatedZone = GameGenerator.generateRuleless(level,pos.offset(offset),template);
        parkourBoundingBox = GeneralUtil.convertToAABB(generatedZone);
        ArmorStand start = null;
        ArmorStand crate = null;
        for (ArmorStand candidate : level.getEntitiesOfClass(ArmorStand.class, parkourBoundingBox)) {
            if(start != null && crate != null){
                break;
            }
            if(start == null && candidate instanceof IMixinArmorStand mixin && mixin.ctf$getMarker().contains("pk_")) {
                start = candidate;
                continue;
            }
            if(crate == null && candidate instanceof IMixinArmorStand mixin && mixin.ctf$getMarker().equals(ArmorStandMarkers.parkourCrate)) {
                crate = candidate;
            }
        }
        if(start == null) {
            return;
        }
        ParkourStart modifier = (ParkourStart) ArmorStandMarkers.getModifier(((IMixinArmorStand)start).ctf$getMarker());
        BlockPos pos = start.blockPosition();
        if(crate != null) {
            this.start=pos;
            this.crate = crate.blockPosition();
            this.difficulty = modifier.difficulty;
            crate.setItemSlot(EquipmentSlot.HEAD,modifier.difficulty.bundleType.getDefaultInstance());
            this.crateAABB = new AABB(this.crate.above());
            this.startAABB = new AABB(this.start.above()).inflate(1);
        }
        start.kill(level);
    }

    @Override
    protected void grantBundle(ServerPlayer sp, CTFPlayerData data) {
        data.setHasParkourBundle(true);
        sp.sendSystemMessage(Component.literal("You have reached the end point, return to start to win!").withStyle(ChatFormatting.GREEN));
        sp.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER,1f,1f);
    }

    @Override
    protected void unlockBundle(ServerPlayer player) {
        onWin(player);
        regenerate();
        onBeatenBy(player);
        sendToStart(player);
    }

    @Override
    protected long getTimeout() {
        return 0L;
    }

    @Override
    protected void putOnTimeout(ServerPlayer player) {

    }

    protected void onWin(ServerPlayer player){
        player.sendSystemMessage(Component.literal("Congratulations on completing the " + difficulty.saveName() + " parkour!!").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
        player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER,1f,1f);
        CTFPlayerData.get(player).setHasParkourBundle(false);
    }

    @Override
    protected void incrementAttemptsAndTeleportToStart(ServerPlayer player) {
        removeBundle(player);
        sendToStart(player);
    }

    @Override
    protected void removeBundle(ServerPlayer player) {
        CTFPlayerData data = CTFPlayerData.get(player);
        data.setHasParkourBundle(false);
    }
}