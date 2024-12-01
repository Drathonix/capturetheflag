package com.drathonix.capturetheflag.common.system.stands;

import com.drathonix.capturetheflag.common.bridge.IMixinArmorStand;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.GameDataCache;
import com.drathonix.capturetheflag.common.system.ProtectedRegion;
import com.drathonix.capturetheflag.common.system.TeamState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FlagStand implements ArmorStandModifier {
    private static final float floatRate = 0.005F;
    private final TeamState teamState;
    private long tickCount = 0;
    private AABB box;

    public FlagStand(TeamState teamState) {
        this.teamState = teamState;
    }

    @Override
    public synchronized void onTick(ArmorStand stand, String marker) {
        tickCount++;
        float y;
        if(tickCount <= 180) {
            y=floatRate;
        }
        else {
            y=-floatRate;
        }
        if(tickCount >= 360){
            tickCount = 0;
        }
        Vec3 pos = new Vec3(stand.getX(), stand.getY()+y, stand.getZ());
        stand.setYRot(tickCount*2);
        stand.setPos(pos);

        if(box != null) {
            if(stand.level() instanceof ServerLevel sl) {
                for (ServerPlayer player : sl.getEntitiesOfClass(ServerPlayer.class, box)) {
                    CTFPlayerData data = CTFPlayerData.get(player);
                    if(data.getTeamState() != teamState){
                        teamState.takeTheFlag(player);
                    }
                }
            }
        }
    }

    @Override
    public void onLoad(ArmorStand stand, String marker) {
        if(stand.level() instanceof ServerLevel sl) {
            BlockPos pos = stand.blockPosition().above(5);
            Level level = stand.level();
            boolean past = false;
            while(pos.getY() <= level.getMaxY()){
                if(!past && !level.getBlockState(pos).isAir()){
                    past=true;
                    continue;
                }
                level.setBlock(pos, Blocks.AIR.defaultBlockState(),0);
                pos = pos.offset(0,1,0);
            }
            pos = stand.blockPosition();
            BoundingBox box = new BoundingBox(pos.getX(),pos.getY(),pos.getZ(),pos.getX()+1,level.getMaxY(),pos.getZ()+1);
            GameDataCache.protect(box, ProtectedRegion.Type.GENERIC_AREA_PROTECTOR);

            stand.setItemSlot(EquipmentSlot.HEAD, teamState == TeamState.RED ? Items.RED_WOOL.getDefaultInstance() : Items.BLUE_WOOL.getDefaultInstance());
            stand.setInvisible(true);
            if (stand instanceof IMixinArmorStand mixin) {
                Vec3 p = mixin.ctf$getTruePosition();
                stand.setPos(p.add(-0.5));
                this.box = new AABB(new BlockPos((int) p.x, (int) p.y, (int) p.z).above()).inflate(1);
                sl.setChunkForced(stand.chunkPosition().x, stand.chunkPosition().z, true);
            }
        }
    }
}
