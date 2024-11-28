package com.drathonix.capturetheflag.common.system.stands;

import com.drathonix.capturetheflag.common.system.GameDataCache;
import com.drathonix.capturetheflag.common.system.ProtectedRegion;
import com.drathonix.capturetheflag.common.system.parkour.ParkourChamber;
import com.drathonix.capturetheflag.common.system.parkour.ParkourDifficulty;
import com.drathonix.capturetheflag.common.util.GeneralUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class ParkourStart implements ArmorStandModifier {
    public final ParkourDifficulty difficulty;

    public ParkourStart(ParkourDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void onTick(ArmorStand stand, String marker) {
    }

    @Override
    public void onLoad(ArmorStand stand, String marker) {
        //stand.setInvisible(true);
    }
}
