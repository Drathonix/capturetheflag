package com.drathonix.capturetheflag.common.util;

import com.drathonix.capturetheflag.common.CTF;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.ArrayList;
import java.util.List;


public class GeneralUtil {
    public static String convertSeconds(long tseconds) {
        int m2s = 60;
        int h2m2s = 60*m2s;
        int d2h2m2s = 24*h2m2s;
        long days = tseconds/d2h2m2s;
        long hours = (tseconds%d2h2m2s)/h2m2s;
        long minutes = ((tseconds%d2h2m2s)%h2m2s)/m2s;
        long seconds = (((tseconds%d2h2m2s))%h2m2s)%m2s;
        StringBuilder out = new StringBuilder();
        if(days > 0){
            out.append(days).append('d').append(' ');
        }
        if(hours > 0){
            out.append(hours).append('h').append(' ');
        }
        if(minutes > 0){
            out.append(minutes).append('m').append(' ');
        }
        if(seconds > 0){
            out.append(seconds).append('s');
        }
        return out.toString().endsWith(" ") ? out.substring(0,out.length()-1) : out.toString();
    }

    public static void sendToAllPlayers(Component message){
        for (ServerPlayer player : CTF.server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(message);
        }
    }

    public static void sendToAllPlayers(SoundEvent sound) {
        for (ServerPlayer player : CTF.server.getPlayerList().getPlayers()) {
            player.playNotifySound(sound, SoundSource.MASTER,1f,1f);
        }
    }

    public static <T> List<T> shuffle(List<T> list){
        List<T> out = new ArrayList<T>();
        List<T> copy = new ArrayList<>(list);
        while(!copy.isEmpty()){
            out.add(copy.remove((int)(Math.random()*copy.size())));
        }
        return out;
    }
}
