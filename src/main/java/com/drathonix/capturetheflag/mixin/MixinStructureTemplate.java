package com.drathonix.capturetheflag.mixin;

import com.drathonix.capturetheflag.common.bridge.IMixinStructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(StructureTemplate.class)
public class MixinStructureTemplate implements IMixinStructureTemplate {
    @Shadow @Final private List<StructureTemplate.StructureEntityInfo> entityInfoList;

    @Override
    public List<StructureTemplate.StructureEntityInfo> ctf$getStructureEntityInfo() {
        return entityInfoList;
    }
}
