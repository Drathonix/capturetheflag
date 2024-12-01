package com.drathonix.capturetheflag.common.bridge;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;

public interface IMixinStructureTemplate {
    List<StructureTemplate.StructureEntityInfo> ctf$getStructureEntityInfo();
}
