package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.system.crystals.Crystal;
import com.drathonix.capturetheflag.common.system.parkour.ParkourGenerator;
import com.drathonix.capturetheflag.common.util.Quadrant;
import com.drathonix.capturetheflag.common.util.regis.BlockRetriever;
import com.vicious.persist.annotations.PersistentPath;
import com.vicious.persist.annotations.Save;
import com.vicious.persist.annotations.Typing;
import com.vicious.persist.shortcuts.PersistShortcuts;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class GameGenerator {
    public static final RandomSource RANDOM = RandomSource.create();

    @PersistentPath
    public static String path = "config/capturetheflag/generator.txt";

    @Save(description = "Number of blocks the border is from the central game position.")
    public static int borderRadius = 210;

    @Save(description = "Holy enchanter generation settings.")
    public static HolyEnchanterGenerator holyEnchanter = new HolyEnchanterGenerator();

    @Save(description = "Challenge spawner generation settings.")
    public static ChallengeSpawnerGenerator challengeSpawner = new ChallengeSpawnerGenerator();

    @Save(description = "Flag generator generation settings.")
    public static FlagGenerator flags = new FlagGenerator();

    @Save(description = "Spawn generator generation settings.")
    public static SpawnGenerator spawns = new SpawnGenerator();

    @Save(description = "The waiting box generation settings")
    public static WaitingBoxGenerator waitingBox = new WaitingBoxGenerator();

    @Save(description = "Parkour Generator Settings")
    public static ParkourGenerator parkourGenerator = new ParkourGenerator();

    @Save(description = "Valid ground blocks")
    @Typing(BlockRetriever.class)
    public static Set<BlockRetriever> groundBlocks = new HashSet<>();


    @Typing(BlockRetriever.class)
    @Save(description = "if detected a structure will not spawn here.")
    public static Set<BlockRetriever> invalidBlocks = new HashSet<>();

    @Save(description = "Structures to block breaking and placing in.")
    @Typing(ResourceLocation.class)
    public static Set<ResourceLocation> protectedStructures = new HashSet<>();

    @Save(description = "WIll generate bedrock walls if enabled")
    public static boolean bedrockWalls=true;
    
    static {
        groundBlocks.add(new BlockRetriever(Blocks.GRASS_BLOCK));
        groundBlocks.add(new BlockRetriever(Blocks.DIRT));
        groundBlocks.add(new BlockRetriever(Blocks.SAND));
        groundBlocks.add(new BlockRetriever(Blocks.GRAVEL));
        groundBlocks.add(new BlockRetriever(Blocks.CLAY));
        groundBlocks.add(new BlockRetriever(Blocks.DRIPSTONE_BLOCK));
        groundBlocks.add(new BlockRetriever(Blocks.MOSS_BLOCK));
        groundBlocks.add(new BlockRetriever(Blocks.STONE));
        groundBlocks.add(new BlockRetriever(Blocks.ANDESITE));
        groundBlocks.add(new BlockRetriever(Blocks.GRANITE));
        groundBlocks.add(new BlockRetriever(Blocks.DIORITE));
        groundBlocks.add(new BlockRetriever(Blocks.DEEPSLATE));
        groundBlocks.add(new BlockRetriever(Blocks.TUFF));
        groundBlocks.add(new BlockRetriever(Blocks.SCULK));

        groundBlocks.add(new BlockRetriever(Blocks.COAL_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.COPPER_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.IRON_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.GOLD_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.LAPIS_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.REDSTONE_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.DIAMOND_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.EMERALD_ORE));

        groundBlocks.add(new BlockRetriever(Blocks.DEEPSLATE_COAL_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.DEEPSLATE_COPPER_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.DEEPSLATE_IRON_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.DEEPSLATE_GOLD_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.DEEPSLATE_LAPIS_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.DEEPSLATE_REDSTONE_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.DEEPSLATE_DIAMOND_ORE));
        groundBlocks.add(new BlockRetriever(Blocks.DEEPSLATE_EMERALD_ORE));

        groundBlocks.add(new BlockRetriever(Blocks.SUSPICIOUS_GRAVEL));
        groundBlocks.add(new BlockRetriever(Blocks.SUSPICIOUS_SAND));
        invalidBlocks.add(new BlockRetriever(Blocks.WATER));
        invalidBlocks.add(new BlockRetriever(Blocks.LAVA));
        protectedStructures.add(ResourceLocation.fromNamespaceAndPath("capturetheflag","blue_flag"));
        protectedStructures.add(ResourceLocation.fromNamespaceAndPath("capturetheflag","red_flag"));
        protectedStructures.add(ResourceLocation.fromNamespaceAndPath("capturetheflag","holy_enchanter"));
        protectedStructures.add(ResourceLocation.fromNamespaceAndPath("capturetheflag","challenge_spawner"));
    }

    public static void generate(ServerLevel level, BlockPos center){
        GameDataCache.clearAndSave();
        center = center.atY(0);
        CTF.server.setDifficulty(Difficulty.PEACEFUL,true);
        for (Quadrant value : Quadrant.values()) {
            value.createAABB(center,borderRadius,level.getMaxY(),level.getMinY());
        }
        for (TeamState value : TeamState.values()) {
            GameDataCache.protect(value.getQuadrant().castedBoundingBox(), ProtectedRegion.Type.TERRITORY,value);
        }

        setBorder(level,center);
        flags.generateForTeams(level,center);
        spawns.generateForTeams(level,center);
        for (Crystal value : Crystal.values()) {
            value.generate(level,center);
        }
        parkourGenerator.generate(new Random(),level,center);
        holyEnchanter.generate(level,center);
        if(bedrockWalls) {
            generateWalls(level, center);
        }
        GameDataCache.save();
    }

    private static void generateWalls(ServerLevel level, BlockPos center) {
        BlockState state = Blocks.BEDROCK.defaultBlockState();
        for (int i = -borderRadius; i < borderRadius; i++) {
            for(int y = level.getMinY(); y <= level.getMaxY(); y++){
                level.setBlock(center.offset(borderRadius,y,i),state,0);
                level.setBlock(center.offset(i,y,borderRadius),state,0);
                level.setBlock(center.offset(-borderRadius,y,i),state,0);
                level.setBlock(center.offset(i,y,-borderRadius),state,0);
            }
        }
    }

    public static void setBorder(ServerLevel level, BlockPos center){
        WorldBorder border = level.getWorldBorder();
        border.setDamagePerBlock(0.0D);
        border.setWarningBlocks(0);
        border.setCenter(center.getX()+0.5D,center.getZ()+0.5D);
        border.setSize(borderRadius*2);
    }

    public static void buildWaitingBox(ServerLevel overworld, BlockPos center) {
        generate(overworld,center);
        waitingBox.generate(overworld,center);
    }

    public static void start(ServerLevel overworld, BlockPos center){
        waitingBox.ungenerate(overworld,center);
    }

    public static class WaitingBoxGenerator {
        @Save
        public int radius = 9;

        @Save
        public int height = 7;

        @Save
        public int y = 200;

        public AABB aabb;

        public synchronized void generate(ServerLevel level, BlockPos center){
            center = center.atY(y);
            level.setDefaultSpawnPos(center.above(),0);
            generate(level,radius,height,radius,center,Blocks.BARRIER.defaultBlockState());
            aabb = new AABB(center.getX()-radius,200,center.getZ()-radius,
                    center.getX()+radius,200+height,center.getZ()+radius);
            for (ServerPlayer player : CTF.server.getPlayerList().getPlayers()) {
                Vec3 dest = new Vec3(center.above());
                player.teleportTo(dest.x,dest.y,dest.z);
            }
        }

        public synchronized void ungenerate(ServerLevel level, BlockPos center){
            center = center.atY(y);
            generate(level,radius,height,radius,center,Blocks.AIR.defaultBlockState());
        }

        private static void generate(Level world, int radiusx, int heighty, int radiusz, BlockPos center, BlockState data){
            //Floor
            for (int x = -radiusx; x <= radiusx; x++) {
                for (int z = -radiusz; z < radiusz; z++) {
                    world.setBlockAndUpdate(center.offset(x,0,z), data);
                }
            }
            //Ceiling
            for (int x = -radiusx; x <= radiusx; x++) {
                for (int z = -radiusz; z < radiusz; z++) {
                    world.setBlockAndUpdate(center.offset(x,heighty,z), data);
                }
            }
            //x walls
            for (int z = -radiusz; z < radiusz; z++) {
                for (int y = 0; y < heighty; y++) {
                    world.setBlockAndUpdate(center.offset(-radiusx,y,z), data);
                    world.setBlockAndUpdate(center.offset(radiusx,y,z), data);
                }
            }
            //z walls
            for (int x = -radiusx; x < radiusx; x++) {
                for (int y = 0; y < heighty; y++) {
                    world.setBlockAndUpdate(center.offset(x,y,-radiusz), data);
                    world.setBlockAndUpdate(center.offset(x,y,radiusx), data);
                }
            }
        }
    }

    public static class HolyEnchanterGenerator {
        @Save(description = "Number of holy enchanters to generate per quadrant")
        public int numberPerQuadrant = 2;
        @Save(description = "Min Distance from the border corner. Ensure this is 10 blocks greater than the flags.")
        public int minSquareDistanceFromCorner = borderRadius-105;
        @Save(description = "Min Distance from center.")
        public int minDistanceFromCenter = 20;

        public void generate(ServerLevel level, BlockPos worldCenter){
            StructureTemplate template = level.getStructureManager().get(ResourceLocation.fromNamespaceAndPath("capturetheflag","holy_enchanter")).get();
            for (Quadrant quadrant : Quadrant.values()) {
                for (int i = 0; i < numberPerQuadrant; i++) {
                    generateAngularRandom(level,quadrant,template,worldCenter.atY(level.getMaxY()),minSquareDistanceFromCorner,20,minDistanceFromCenter,5, ProtectedRegion.Type.HOLY_ENCHANTER);
                }
            }
        }
    }

    public static class ChallengeSpawnerGenerator {
        @Save(description = "Number of challenge spawners to generate per neutral quadrant")
        public int numberPerQuadrant = 3;
        @Save(description = "Min Distance from border.")
        public int minDistanceFromBorder = borderRadius-105;
        @Save(description = "Min Distance from center.")
        public int minDistanceFromCenter = 20;
    }

    public static class FlagGenerator {
        @Save(description = "Distance from center.")
        public int distanceFromCenter = 120;

        public void generate(ServerLevel level, BlockPos worldCenter, TeamState team) {
            StructureTemplate template = level.getStructureManager().get(ResourceLocation.fromNamespaceAndPath("capturetheflag", team == TeamState.BLUE ? "blue_flag" : "red_flag")).get();
            CannotGenerateException e = generateStatic(level, team.getQuadrant(), template, new BlockPos(distanceFromCenter, level.getMaxY(), distanceFromCenter),worldCenter, ProtectedRegion.Type.FLAG);
            if (e != null) {
                throw e;
            }
        }

        public void generateForTeams(ServerLevel level, BlockPos center) {
            for (TeamState value : TeamState.values()) {
                generate(level,center,value);
            }
        }
    }

    public static class SpawnGenerator {
        @Save(description = "Distance from center.")
        public int distanceFromCenter = 145;

        public void generate(ServerLevel level, BlockPos worldCenter, TeamState team) {
            StructureTemplate template = level.getStructureManager().get(ResourceLocation.fromNamespaceAndPath("capturetheflag", team == TeamState.BLUE ? "blue_spawn" : "red_spawn")).get();
            CannotGenerateException e = generateStatic(level, team.getQuadrant(), template, new BlockPos(distanceFromCenter, level.getMaxY(), distanceFromCenter),worldCenter, ProtectedRegion.Type.SPAWN_ZONE);
            if (e != null) {
                throw e;
            }
        }

        public void generateForTeams(ServerLevel level, BlockPos center) {
            for (TeamState value : TeamState.values()) {
                generate(level,center,value);
            }
        }
    }


    public static @Nullable BoundingBox generateAngularRandom(ServerLevel level, Quadrant quadrant, StructureTemplate template, BlockPos worldCenter, int cornerDist, int borderEdge, int centerEdge, int attempts, @Nullable ProtectedRegion.Type type){
        worldCenter = centralizeOnPoint(worldCenter, quadrant, template);
        BlockPos pos = quadrant.selectRandomPositionAngular(centerEdge,
                borderRadius-borderEdge,
                borderRadius-cornerDist,
                worldCenter);
        for (int i = attempts; i > 0; i--) {
            try {
                return generateUnsafe(level, pos, quadrant, template, type);
            } catch (CannotGenerateException e){}
        }
        return null;
    }
    public static @Nullable BoundingBox generateAngularRandomOffset(ServerLevel level, Quadrant quadrant, StructureTemplate template, BlockPos worldCenter, int cornerDist, int borderEdge, int centerEdge, int attempts, @Nullable ProtectedRegion.Type type, Function<BlockPos,BlockPos> offsetter){
        worldCenter = centralizeOnPoint(worldCenter, quadrant, template);
        BlockPos pos = quadrant.selectRandomPositionAngular(centerEdge,
                borderRadius-borderEdge,
                borderRadius-cornerDist,
                worldCenter);
        for (int i = attempts; i > 0; i--) {
            try {
                return generateUnsafeRandomRotate(level, pos, quadrant, template, type, offsetter);
            } catch (CannotGenerateException e){}
        }
        return null;
    }

    public static @Nullable CannotGenerateException generateStatic(ServerLevel level, Quadrant quadrant, StructureTemplate template, BlockPos positivePosition, BlockPos worldCenter, @Nullable ProtectedRegion.Type type){
        BlockPos pos = centralizeOnPoint(positivePosition,quadrant,template);
        pos = quadrant.transform(pos).offset(worldCenter);
        try {
            generateUnsafe(level, pos, quadrant, template,type);
        } catch (CannotGenerateException e){
            return e;
        }
        return null;
    }

    public static BoundingBox generateUnsafe(ServerLevel level, BlockPos pos, Quadrant quadrant, StructureTemplate template, @Nullable ProtectedRegion.Type type){
        pos = findSafeGround(level,pos);
        StructurePlaceSettings settings = quadrant.makeStructurePlaceSettings();
        BoundingBox box = template.getBoundingBox(settings,pos);
        checkCollidesWithOtherStructure(box);
        if(type != null) {
            GameDataCache.protect(box, type, TeamState.byQuadrant(quadrant));
        }
        template.placeInWorld(level, pos, pos, settings,RANDOM,2);
        return box;
    }

    public static BoundingBox generateUnsafe(ServerLevel level, BlockPos pos, Quadrant quadrant, StructureTemplate template, @Nullable ProtectedRegion.Type type, Function<BlockPos,BlockPos> offsetter){
        pos = findSafeGround(level,pos);
        pos = offsetter.apply(pos);
        StructurePlaceSettings settings = quadrant.makeStructurePlaceSettings();
        BoundingBox box = template.getBoundingBox(settings,pos);
        checkCollidesWithOtherStructure(box);
        if(type != null) {
            GameDataCache.protect(box, type, TeamState.byQuadrant(quadrant));
        }
        template.placeInWorld(level, pos, pos, settings,RANDOM,2);
        return box;
    }

    public static BoundingBox generateUnsafeRandomRotate(ServerLevel level, BlockPos pos, Quadrant quadrant, StructureTemplate template, @Nullable ProtectedRegion.Type type, Function<BlockPos,BlockPos> offsetter){
        pos = findSafeGround(level,pos);
        pos = offsetter.apply(pos);
        StructurePlaceSettings settings = new StructurePlaceSettings();
        settings.setRotation(Rotation.getRandom(RANDOM));
        BoundingBox box = template.getBoundingBox(settings,pos);
        checkCollidesWithOtherStructure(box);
        if(type != null) {
            GameDataCache.protect(box, type, TeamState.byQuadrant(quadrant));
        }
        template.placeInWorld(level, pos, pos, settings,RANDOM,2);
        return box;
    }

    public static BoundingBox generateUnsafeRandomRotate(ServerLevel level, BlockPos pos, Quadrant quadrant, StructureTemplate template, @Nullable ProtectedRegion.Type type){
        pos = findSafeGround(level,pos);
        StructurePlaceSettings settings = new StructurePlaceSettings();
        settings.setRotation(Rotation.getRandom(RANDOM));
        BoundingBox box = template.getBoundingBox(settings,pos);
        checkCollidesWithOtherStructure(box);
        if(type != null) {
            GameDataCache.protect(box, type, TeamState.byQuadrant(quadrant));
        }
        template.placeInWorld(level, pos, pos, settings,RANDOM,2);
        return box;
    }

    private static BlockPos centralizeOnPoint(BlockPos pos, Quadrant quadrant, StructureTemplate template){
        Vec3i offset = template.getSize(quadrant.getRotation());
        return pos.offset((int) -Math.floor(offset.getX()/2F), 0, (int) -Math.floor(offset.getZ()/2F));
    }

    private static void checkCollidesWithOtherStructure(BoundingBox box){
        for (ProtectedRegion region : GameDataCache.protectedRegions) {
            if(region.type.blockStructures() && region.aabb.intersects(box)){
                throw new CannotGenerateException("Structure will collide with another one");
            }
        }
    }

    private static BlockPos findSafeGround(ServerLevel level, BlockPos pos){
        BlockState state = level.getBlockState(pos);
        while(!groundBlocks.contains(new BlockRetriever(state.getBlock()))){
            if(invalidBlocks.contains(new BlockRetriever(state.getBlock()))){
                throw new CannotGenerateException("Did not find safe ground. Invalid block is in the way.");
            }
            pos = pos.offset(0,-1,0);
            if(pos.getY() < level.getMinY()){
                throw new CannotGenerateException("Did not find safe ground");
            }
            state = level.getBlockState(pos);
        }
        return pos;
    }

    public static void init(){
        PersistShortcuts.init(GameGenerator.class);
    }
}
