package com.drathonix.capturetheflag.common.system.stands;

import com.drathonix.capturetheflag.common.bridge.IMixinArmorStand;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.TeamState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ParkourCrate implements ArmorStandModifier{
    private static final float floatRate = 0.005F;
    @Override
    public synchronized void onTick(ArmorStand stand, String marker) {
        if(stand instanceof IMixinArmorStand mixin) {
            /*float y;
            if (mixin.ctf$getTicks() >= 360) {
                mixin.ctf$resetTicker();
            }
            if (mixin.ctf$getTicks() < 180) {
                y = floatRate;
            } else {
                y = -floatRate;
            }
            Vec3 pos = new Vec3(stand.getX(), stand.getY() + y, stand.getZ());
            stand.setPos(pos);*/
            stand.setYRot(mixin.ctf$getTicks() * 8);
            mixin.ctf$tick();
        }
    }

    @Override
    public void onLoad(ArmorStand stand, String marker) {
        stand.setInvisible(true);
        stand.setInvulnerable(true);
    }
}
