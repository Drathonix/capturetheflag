package com.drathonix.capturetheflag.common;

import com.drathonix.capturetheflag.common.bridge.IMixinAbstractFurnaceBlockEntity;
import com.drathonix.capturetheflag.common.component.CustomDatas;
import com.drathonix.capturetheflag.common.config.Configs;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.*;
import com.drathonix.capturetheflag.common.util.DirectionUtil;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import com.vicious.persist.Persist;
import com.vicious.persist.mappify.registry.Stringify;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.*;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractFurnaceBlock;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CTF {
    public static String modid = "capturetheflag";

    public static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    public static MinecraftServer server;


    public static void init() {
        Stringify.register(ResourceLocation.class, CTF::safeRL,CTF::safeRL);
        Stringify.register(Component.class,CTF::parseComponent, CTF::componentJsonify);
        Stringify.register(MutableComponent.class,CTF::parseComponent, CTF::componentJsonify);
        Stringify.register(BlockPos.class,str->{
            String[] sep = str.split("/");
            return new BlockPos(Integer.parseInt(sep[0]),Integer.parseInt(sep[1]),Integer.parseInt(sep[2]));
        },pos-> pos.getX() + "/" + pos.getY() + "/" + pos.getZ());
        CTFEventHandler.init();
        CommandRegistrationEvent.EVENT.register((dispatcher,registry,selection)->{
            CTFCommands.init(dispatcher);
        });
    }

    public static void postServerInitted(){
        //Persist.doC_NAMEScan();
        Configs.reload();
        executor.scheduleAtFixedRate(GameDataCache::save,60,60, TimeUnit.SECONDS);
    }

    public static String safeRL(ResourceLocation rl){
        return rl.toString().replaceFirst(":","/");
    }

    public static ResourceLocation safeRL(String rl){
        return ResourceLocation.parse(rl.replaceFirst("/",":"));
    }

    public synchronized static void respawn(ServerPlayer sp){
        sp.addEffect(new MobEffectInstance(MobEffects.SATURATION,15));
        CTFPlayerData data = CTFPlayerData.get(sp);
        data.requireTeam(team->{
            sp.setRespawnPosition(CTF.server.overworld().dimension(),team.getSpawn(),0f,true,false);
        });
        data.requireClassType(type->{
            type.respawnGear.forEach(item->{
                sp.getInventory().add(item.get());
            });
            type.onRespawn(sp);
        });
    }

    public static <T extends Component> T parseComponent(String json){
        return (T)ComponentSerialization.FLAT_CODEC.decode(JsonOps.INSTANCE,JsonParser.parseString(json)).getOrThrow().getFirst();
    }

    public static <T extends Component> String componentJsonify(T component){
        return ComponentSerialization.FLAT_CODEC.encodeStart(JsonOps.INSTANCE,component).getOrThrow().getAsString();
    }
}
