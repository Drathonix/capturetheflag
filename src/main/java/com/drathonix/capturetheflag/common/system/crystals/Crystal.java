package com.drathonix.capturetheflag.common.system.crystals;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.system.GameGenerator;
import com.drathonix.capturetheflag.common.system.TeamState;
import com.drathonix.capturetheflag.common.util.Quadrant;
import com.vicious.persist.annotations.Save;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Random;

public enum Crystal {
    COPPER(Blocks.STONE,Blocks.COPPER_ORE,"copper_crystal1"),
    IRON(Blocks.STONE,Blocks.IRON_ORE,"iron_crystal1"),
    DIAMOND(Blocks.DEEPSLATE,Blocks.DEEPSLATE_DIAMOND_ORE,"diamond_crystal1"){
        {
            generateTerritory=false;
            numberPerQuadrant=1;
            maxReplacementChance=0.5F;
            minReplacementChance=0.05F;
        }
    };

    public final Block target;
    public final Block replacement;
    public final StructureTemplate structure;

    Crystal(Block target, Block replacement, String name) {
        this.target = target;
        this.structure = CTF.server.getStructureManager().get(ResourceLocation.fromNamespaceAndPath(CTF.modid,"crystals/" + name)).get();
        this.replacement = replacement;
    }

    private static final Random RANDOM = new Random();

    @Save
    public boolean generateTerritory = true;
    @Save
    public float maxReplacementChance = 1F;
    @Save
    public float minReplacementChance = 0.1F;
    @Save
    public int numberPerQuadrant = 2;
    @Save(description = "Min Distance from the border corner. Ensure this is 10 blocks greater than the flags.")
    public int minSquareDistanceFromCorner = GameGenerator.borderRadius-105;
    @Save(description = "Min Distance from center.")
    public int minDistanceFromCenter = 10;

    public void generate(ServerLevel level, BlockPos center){
        for (Quadrant value : Quadrant.values()) {
            if(generateTerritory || TeamState.byQuadrant(value) == null){
                for (int i = 0; i < numberPerQuadrant; i++) {
                    generateQuadrant(level,center,value);
                }
            }
        }
    }

    public void generateQuadrant(ServerLevel level, BlockPos center, Quadrant quadrant){
        BoundingBox area = GameGenerator.generateAngularRandomOffset(level,quadrant,structure,center.atY(level.getMaxY()),minSquareDistanceFromCorner,20,minDistanceFromCenter,5,null,pos->{
            return pos.atY(pos.getY()-(int)(structure.getSize().getY()/2.5F));
        });
        if(area != null) {
            float ydiff = area.maxY()-area.minY();
            float vec = (maxReplacementChance-minReplacementChance)/ydiff;
            for (int x = area.minX(); x <= area.maxX(); x++) {
                for (int y = area.minY(); y <= area.maxY(); y++) {
                    float yvec = y-area.minY();
                    for (int z = area.minZ(); z <= area.maxZ(); z++) {
                        float chance = Math.clamp(vec*yvec,minReplacementChance,maxReplacementChance);
                        if(RANDOM.nextFloat() < chance){
                            BlockState state = level.getBlockState(new BlockPos(x,y,z));
                            if(state.getBlock() == target){
                                level.setBlockAndUpdate(new BlockPos(x,y,z),replacement.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
    }
}
