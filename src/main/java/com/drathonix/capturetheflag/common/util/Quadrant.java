package com.drathonix.capturetheflag.common.util;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Random;
import java.util.function.Consumer;

public enum Quadrant {
    //++
    SOUTHEAST(Direction.SOUTH,Direction.EAST){
        @Override
        public Quadrant getOpposite() {
            return NORTHWEST;
        }
        public @NotNull Rotation getRotation() {
            return Rotation.NONE;
        }
    },
    //+-
    SOUTHWEST(Direction.SOUTH,Direction.WEST){
        @Override
        public Quadrant getOpposite() {
            return NORTHEAST;
        }
        public @NotNull Rotation getRotation() {
            return Rotation.CLOCKWISE_90;
        }
    },
    //-+
    NORTHEAST(Direction.NORTH,Direction.EAST){
        @Override
        public Quadrant getOpposite() {
            return SOUTHWEST;
        }
        public @NotNull Rotation getRotation() {
            return Rotation.COUNTERCLOCKWISE_90;
        }
    },
    //--
    NORTHWEST(Direction.NORTH,Direction.WEST){
        @Override
        public Quadrant getOpposite() {
            return SOUTHEAST;
        }
        public @NotNull Rotation getRotation() {
            return Rotation.CLOCKWISE_180;
        }
    };


    private final Direction NS;
    private final Direction EW;
    @Nullable
    private AABB boundingBox = null;

    Quadrant(Direction NS, Direction EW) {
        this.NS = NS;
        this.EW = EW;
    }

    public abstract Quadrant getOpposite();

    public BlockPos transform(BlockPos positiveRelative){
        return new BlockPos(positiveRelative.getX()*EW.getStepX(),positiveRelative.getY(),positiveRelative.getZ()*NS.getStepZ());
    }

    private static final Random random = new Random();

    public BlockPos selectRandomPositionAngular(@Range(from = 0,to=Long.MAX_VALUE) int minDistanceCentralEdge,
                                            @Range(from = 0,to=Long.MAX_VALUE) int exclusionaryEdge,
                                            @Range(from = 0,to=Long.MAX_VALUE) int exclusionarySquare, @NotNull BlockPos center){
        int x=-1,z=-1;
        if(random.nextBoolean()) {
            x = random.nextInt(minDistanceCentralEdge, exclusionarySquare);
        }
        else{
            z = random.nextInt(minDistanceCentralEdge, exclusionarySquare);
        }
        if(z == -1){
            z = random.nextInt(minDistanceCentralEdge,exclusionaryEdge);
        }
        if(x == -1){
            x = random.nextInt(minDistanceCentralEdge,exclusionaryEdge);
        }
        return transform(new BlockPos(x,0,z)).offset(center);
    }

    public StructurePlaceSettings makeStructurePlaceSettings() {
        StructurePlaceSettings placeSettings = new StructurePlaceSettings();
        placeSettings.setRotation(getRotation());
        return placeSettings;
    }

    public void createAABB(BlockPos center, int borderSize, int maxY, int minY){
        center = center.atY(0);
        boundingBox = new AABB(
                new Vec3(center),
                new Vec3(
                        transform(new BlockPos(borderSize,0,borderSize))
                        .offset(center)
                )
        ).setMaxY(maxY).setMinY(minY);
    }

    public abstract @NotNull Rotation getRotation();

    public @Nullable AABB getBoundingBox() {
        return boundingBox;
    }

    public void selectEdges(BlockPos position, BlockPos worldCenter, boolean centralized, Consumer<BlockPos> consumer) {
        if(boundingBox == null){
            return;
        }
        if(centralized){
            selectEdge(new BlockPos(worldCenter).relative(NS,2),Direction.SOUTH,consumer,5);
            selectEdge(new BlockPos(worldCenter).relative(EW,2),Direction.EAST,consumer,5);
        }
        else{
            selectEdge(new BlockPos(worldCenter.getX(),position.getY(),position.getZ()),Direction.SOUTH,consumer,2);
            consumer.accept(new BlockPos(position.getX(),position.getY(),worldCenter.getZ()));
            consumer.accept(new BlockPos(worldCenter.getX(),position.getY(),position.getZ()));
            selectEdge(new BlockPos(position.getX(),position.getY(),worldCenter.getZ()),Direction.EAST,consumer,2);
        }
    }
    private void selectEdge(BlockPos plr, Direction dir, Consumer<BlockPos> consumer, int dist){
        for (int i = 1; i < dist; i++) {
            BlockPos p1 = plr.relative(dir);
            if(boundingBox.inflate(0.1).contains(new Vec3(p1))) {
                consumer.accept(p1);
            }
            p1 = plr.relative(dir.getOpposite());
            if(boundingBox.inflate(0.1).contains(new Vec3(p1))) {
                consumer.accept(p1);
            }
        }
    }

    public BoundingBox castedBoundingBox() {
        return new BoundingBox((int)boundingBox.minX,(int)boundingBox.minY,(int)boundingBox.minZ,(int)boundingBox.maxX,(int)boundingBox.maxY,(int)boundingBox.maxZ);
    }
}
