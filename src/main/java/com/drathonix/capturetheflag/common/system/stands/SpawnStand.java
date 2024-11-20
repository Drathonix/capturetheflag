package com.drathonix.capturetheflag.common.system.stands;

import com.drathonix.capturetheflag.common.bridge.IMixinArmorStand;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.TeamState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SpawnStand implements ArmorStandModifier {
    private final TeamState teamState;

    public SpawnStand(TeamState teamState) {
        this.teamState = teamState;
    }

    @Override
    public void onTick(ArmorStand stand, String marker) {
        if(stand.isAlive() && stand.level() instanceof ServerLevel sl){
            stand.kill(sl);
        }
    }

    @Override
    public void onLoad(ArmorStand stand, String marker) {
        if(stand.level() instanceof ServerLevel sl) {
            teamState.spawn=stand.blockPosition();
            stand.setItemSlot(EquipmentSlot.HEAD, teamState == TeamState.RED ? Items.RED_WOOL.getDefaultInstance() : Items.BLUE_WOOL.getDefaultInstance());
            stand.setInvisible(true);
            sl.setChunkForced(stand.chunkPosition().x, stand.chunkPosition().z, true);
        }
    }
}
