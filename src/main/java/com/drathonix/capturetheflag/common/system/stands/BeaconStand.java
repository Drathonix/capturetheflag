package com.drathonix.capturetheflag.common.system.stands;

import com.drathonix.capturetheflag.common.system.GameDataCache;
import com.drathonix.capturetheflag.common.system.ProtectedRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class BeaconStand implements ArmorStandModifier{
    @Override
    public void onTick(ArmorStand stand, String marker) {
        if(stand.isAlive() && stand.level() instanceof ServerLevel sl){
            stand.kill(sl);
        }
    }

    @Override
    public void onLoad(ArmorStand stand, String marker) {
        BlockPos pos = stand.blockPosition();
        Level level = stand.level();
        boolean past = false;
        while(pos.getY() <= level.getMaxY()){
            level.setBlock(pos, Blocks.AIR.defaultBlockState(),0);
            pos = pos.offset(0,1,0);
        }
        pos = stand.blockPosition();
        BoundingBox box = new BoundingBox(pos.getX(),pos.getY(),pos.getZ(),pos.getX()+1,level.getMaxY(),pos.getZ()+1);
        GameDataCache.protect(box, ProtectedRegion.Type.GENERIC_AREA_PROTECTOR);
        stand.setInvisible(true);
    }
}
