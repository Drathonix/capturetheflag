package com.drathonix.capturetheflag.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.List;

public class DirectionUtil {
    public static Adjacents adjacents(Direction dir){
        if(dir == Direction.NORTH || dir == Direction.SOUTH){
            return new Adjacents(Direction.EAST,Direction.WEST,Direction.UP,Direction.DOWN);
        }
        if(dir == Direction.EAST || dir == Direction.WEST){
            return new Adjacents(Direction.SOUTH,Direction.NORTH,Direction.UP,Direction.DOWN);
        }
        else{
            return new Adjacents(Direction.SOUTH,Direction.NORTH,Direction.EAST,Direction.WEST);
        }
    }

    public record Adjacents(Direction left, Direction right, Direction up, Direction down){

        public BlockPos[] translated(BlockPos pos){
            BlockPos[] ret = new BlockPos[8];
            ret[0]=pos.relative(left);
            ret[1]=pos.relative(right);
            ret[2]=pos.relative(down);
            ret[3]=pos.relative(up);
            ret[4]=pos.relative(left).relative(up);
            ret[5]=pos.relative(left).relative(down);
            ret[6]=pos.relative(right).relative(up);
            ret[7]=pos.relative(right).relative(down);
            return ret;
        }
    }
}
