package com.drathonix.capturetheflag.common.system.stands;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class BeaconStand implements ArmorStandModifier{
    @Override
    public void onTick(ArmorStand stand, String marker) {

    }

    @Override
    public void onLoad(ArmorStand stand, String marker) {
        BlockPos pos =stand.blockPosition();
        Level level = stand.level();
        while(pos.getY() <= level.getMaxY()){
            level.setBlock(pos, Blocks.AIR.defaultBlockState(),0);
            pos = pos.offset(0,1,0);
        }
        stand.setInvisible(true);
    }
}
