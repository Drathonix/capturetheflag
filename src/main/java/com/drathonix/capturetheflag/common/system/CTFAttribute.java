package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.config.ItemsConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class CTFAttribute {
    public static final AttributeModifier brickLayerRange = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(CTF.modid,"brick_range"), ItemsConfig.brickLayerRangeBoost, AttributeModifier.Operation.ADD_VALUE);
    public static final AttributeModifier lesserBrickLayerRange = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(CTF.modid,"brick_range_lesser"),ItemsConfig.brickLayerRangeBoostBuilder, AttributeModifier.Operation.ADD_VALUE);
}
