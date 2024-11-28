package com.drathonix.capturetheflag.common.system.parkour;

import com.drathonix.capturetheflag.common.bridge.IMixinArmorStand;
import com.drathonix.capturetheflag.common.system.*;
import com.drathonix.capturetheflag.common.system.stands.ArmorStandMarkers;
import com.drathonix.capturetheflag.common.system.stands.ParkourStart;
import com.drathonix.capturetheflag.common.util.GeneralUtil;
import com.drathonix.capturetheflag.common.util.Quadrant;
import com.drathonix.finallib.common.util.weighted.WeightedList;
import com.vicious.persist.annotations.Save;
import com.vicious.persist.annotations.Typing;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

public class ParkourGenerator {
    @Save
    @Typing(YBand.class)
    public List<YBand> bands = new ArrayList<>();

    public ParkourGenerator() {
        bands.add(new YBand(255,255).surfaceMode().maximum(2));
        bands.add(new YBand(0,48).maximum(3).without(ParkourDifficulty.EASY));
        bands.add(new YBand(-48,-1).maximum(4).without(ParkourDifficulty.EASY).without(ParkourDifficulty.MEDIUM));
    }

    public void generate(Random random, ServerLevel level, BlockPos center){
        for (YBand band : bands) {
            band.generate(random, level, center);
        }
    }

    public boolean needsUpdate() {
        return !toAdd.isEmpty();
    }

    public static class Selections extends EnumMap<ParkourDifficulty,List<StructureTemplate>> {
        public Selections() {
            super(ParkourDifficulty.class);
            reset();
        }
        public void reset(){
            for (ParkourDifficulty value : ParkourDifficulty.values()) {
                reset(value);
            }
        }
        public StructureTemplate select(Random random, WeightedList<ParkourDifficulty> options){
            ParkourDifficulty difficulty = options.getRandom(random);
            List<StructureTemplate> structures = get(difficulty);
            if(structures == null || structures.isEmpty()){
                reset(difficulty);
                structures = get(difficulty);
            }
            return structures.remove(random.nextInt(structures.size()));
        }
        public void reset(ParkourDifficulty value){
            remove(value);
            put(value,value.copyOfTemplates());
        }
    }

    private final static List<BoundingBox> toAdd = new ArrayList<>();

    public synchronized void addGDCs(ServerLevel level){
        while(!toAdd.isEmpty()) {
            addToGDC(level,toAdd.removeLast());
        }
    }

    private synchronized void addToGDC(ServerLevel level, BoundingBox box) {
        AABB aabb = GeneralUtil.convertToAABB(box);
        ArmorStand start = null;
        ArmorStand crate = null;
        for (ArmorStand candidate : level.getEntitiesOfClass(ArmorStand.class, aabb)) {
            if(start != null && crate != null){
                break;
            }
            if(start == null && candidate instanceof IMixinArmorStand mixin && mixin.ctf$getMarker().contains("pk_")) {
                start = candidate;
                continue;
            }
            if(crate == null && candidate instanceof IMixinArmorStand mixin && mixin.ctf$getMarker().equals(ArmorStandMarkers.parkourCrate)) {
                crate = candidate;
            }
        }
        if(start == null) {
            System.out.println("NULL START!");
            return;
        }
        ParkourStart modifier = (ParkourStart) ArmorStandMarkers.getModifier(((IMixinArmorStand)start).ctf$getMarker());
        BlockPos pos = start.blockPosition();
        if(crate != null) {
            ParkourChamber chamber = new ParkourChamber(pos, crate.blockPosition(), aabb, modifier.difficulty);
            crate.setItemSlot(EquipmentSlot.HEAD,modifier.difficulty.bundleType.getDefaultInstance());
            GameDataCache.parkourChambers.add(chamber);
        }
        start.kill(level);
    }

    public static class YBand {
        public YBand(){
            difficulties.add(ParkourDifficulty.EASY);
            difficulties.add(ParkourDifficulty.MEDIUM);
            difficulties.add(ParkourDifficulty.HARD);
            difficulties.add(ParkourDifficulty.BRUTAL);
            difficulties.add(ParkourDifficulty.CHAMPION);
        };
        public YBand(int ymin, int ymax){
            this();
            this.yMax=ymax;
            this.yMin=ymin;
        }

        @Save
        public int yMax = 64;
        @Save
        public int yMin = 0;
        @Save
        public int maximumSpawnedPerQuadrant = 4;
        @Save
        public int attempts = 5;
        @Save
        @Typing(ParkourDifficulty.class)
        public WeightedList<ParkourDifficulty> difficulties = new WeightedList<>();
        @Save
        public boolean surfaceMode;
        @Save
        public int maxDistCenter = 10;
        @Save
        public int minDistFromBorder = 20;
        @Save(description = "Min Distance from the border corner. Ensure this is 10 blocks greater than the flags.")
        public int minSquareDistanceFromCorner = GameGenerator.borderRadius-105;

        public YBand without(ParkourDifficulty parkourDifficulty) {
            difficulties.remove(parkourDifficulty);
            return this;
        }

        public synchronized void generate(Random random, ServerLevel level, BlockPos center){
            for (Quadrant value : Quadrant.values()) {
                generateQuadrant(random,level,center,value,new Selections());
            }
        }

        private synchronized void generateQuadrant(Random random, ServerLevel level, BlockPos center, Quadrant quadrant, Selections selections) {
            int y = yMax;
            if(yMax != yMin) {
                int height = (yMax - yMin);
                int cen = yMax-(height/2);
                int dev = (int) ((height / 2F) * random.nextGaussian());
                y = cen + dev;
            }
            boolean isTerritory = TeamState.byQuadrant(quadrant) != null;
            WeightedList<ParkourDifficulty> options = difficulties;
            if(isTerritory && surfaceMode) {
                options = new WeightedList<>(options);
                options.remove(ParkourDifficulty.HARD);
                options.remove(ParkourDifficulty.BRUTAL);
                options.remove(ParkourDifficulty.CHAMPION);
            }
            for (int i = 0; i < maximumSpawnedPerQuadrant; i++) {
                StructureTemplate selection = selections.select(random,options);
                for (int j = attempts; j > 0; j--) {
                    BlockPos position = !isTerritory ? quadrant.selectRandomPosition(maxDistCenter,GameGenerator.borderRadius- minDistFromBorder,center) :
                            quadrant.selectRandomPositionAngular(maxDistCenter,GameGenerator.borderRadius- minDistFromBorder, minSquareDistanceFromCorner, center);
                    position = position.atY(y);
                    try {
                        BoundingBox box = GameGenerator.generateUnsafeRandomRotate(level, position, quadrant, selection, ProtectedRegion.Type.PARKOUR_ZONE);
                        toAdd.add(box);
                        break;
                    } catch (Exception e){
                        if(!(e instanceof CannotGenerateException)) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        public YBand surfaceMode() {
            this.surfaceMode = true;
            return this;
        }

        public YBand maximum(int i) {
            maximumSpawnedPerQuadrant=i;
            return this;
        }
    }
}
