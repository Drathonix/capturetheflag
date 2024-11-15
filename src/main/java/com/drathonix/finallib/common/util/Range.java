package com.drathonix.finallib.common.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Range {
    public static final Range HOTBAR = new Range(0,9);
    public static final Range MAIN_INVENTORY = new Range(9,36);
    public static final Range ARMOR = new Range(0,4);
    public static final Range OFFHAND = new Range(0,1);

    private final int start;
    private final int end;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public void forEach(Consumer<Integer> consumer){
        for (int i = start; i < end; i++) {
            consumer.accept(i);
        }
    }

    public void forEachWithBreaker(Predicate<Integer> consumerBreaker){
        for (int i = start; i < end; i++) {
            if(consumerBreaker.test(i)){
                return;
            }
        }
    }

    public <T> T forEach(T defaultValue, Function<Integer,T> function){
        for (int i = start; i < end; i++) {
            T out = function.apply(i);
            if(out != null){
                return out;
            }
        }
        return defaultValue;
    }

    public int getStart(){
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "(" + start + "," + end + ")";
    }
}
