package com.drathonix.capturetheflag.common.config;

import com.vicious.persist.annotations.PersistentPath;
import com.vicious.persist.annotations.Save;
import com.vicious.persist.shortcuts.PersistShortcuts;

public class ItemsConfig {
    @PersistentPath
    public static String path = "config/capturetheflag/items.txt";

    @Save
    public static float reflectorShieldPower = 0.04F;
    @Save
    public static float reflectorShieldPowerWarrior = 0.075F;

    @Save
    public static float tridentArmorPierceRanger = 0.15F;
    @Save
    public static float tridentArmorPierce = 0.075F;

    @Save
    public static float longBowVelocityMultiplier = 1.5F;
    @Save
    public static float longBowVelocityMultiplierRanger = 2.0F;

    @Save
    public static float longBowDamageMultiplier = 0.36F;
    @Save
    public static float longBowDamageMultiplierRanger = 0.45F;

    @Save
    public static int brickLayerPlacement = 3;
    @Save(description = "Requires restart to apply")
    public static int brickLayerRangeBoost = 1;
    @Save
    public static int brickLayerPlacementBuilder = 6;
    @Save(description = "Requires restart to apply")
    public static int brickLayerRangeBoostBuilder = 3;


    public static void init(){
        PersistShortcuts.init(ItemsConfig.class);
    }
}
