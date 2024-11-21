package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.config.CTFConfig;
import com.drathonix.capturetheflag.common.gui.ClassSelection;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.phasing.PhaseFlag;
import com.drathonix.capturetheflag.common.util.GeneralUtil;
import com.drathonix.finallib.common.inventory.wrapper.InventoryWrapper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;


public class CTFCommands {
    public static void init(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("class").executes(ctx->{
            if(ctx.getSource().getEntity() instanceof ServerPlayer sp){
                if(CTFPlayerData.get(sp).allowChangeClass){
                    sp.openMenu(new SimpleMenuProvider((id, inv, player)-> new ClassSelection(id,inv, InventoryWrapper.of(NonNullList.withSize(9*3, ItemStack.EMPTY),0)), Component.literal("Class Selection").withStyle(Style.EMPTY.withBold(true).withColor(Color.GREEN.getRGB()))));
                }
                else{
                    sp.sendSystemMessage(Component.literal("You cannot change your class right now.").withStyle(ChatFormatting.RED));
                }
            }
            return 1;
        }));
        dispatcher.register(Commands.literal("spawn").executes(ctx->{
            if(!GameDataCache.getGamePhase().flags.contains(PhaseFlag.HOME)){
                ctx.getSource().sendFailure(Component.literal("You cannot use this command right now"));
                return 0;
            }
            if(ctx.getSource().getEntity() instanceof ServerPlayer sp){
                CTFPlayerData data = CTFPlayerData.get(sp);
                data.requireTeam(team->{
                    if(System.currentTimeMillis() >= data.homeCooldownEnd){
                        Vec3 vec = team.getSpawn().getCenter();
                        sp.teleportTo(vec.x,vec.y,vec.z);
                        data.homeCooldownEnd =System.currentTimeMillis()+5*60*1000;
                    }
                    else{
                        sp.sendSystemMessage(Component.literal("/flag is on cooldown for " + GeneralUtil.convertSeconds(data.homeCooldownEnd) + ", cannot teleport.").withStyle(ChatFormatting.RED));
                    }
                });
            }
            return 1;
        }));
        dispatcher.register(Commands.literal("forceteam").requires(ctx->ctx.hasPermission(Commands.LEVEL_MODERATORS))
                .then(Commands.argument("team",StringArgumentType.string())
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(ctx->{
                                    TeamState state;
                                    try {
                                        state = TeamState.valueOf(StringArgumentType.getString(ctx, "team"));
                                    } catch (Exception e){
                                        ctx.getSource().sendFailure(Component.literal("No such team : " + StringArgumentType.getString(ctx, "team")));
                                        return 0;
                                    }
                                    EntityArgument.getPlayers(ctx, "players").forEach(player->{
                                        player.sendSystemMessage(Component.literal("You have been assigned to team " + state.name() + " use '/class' to select your class."));
                                        CTFPlayerData data = CTFPlayerData.get(player);
                                        data.setTeamState(state);
                                        if(data.getClassType() == null) {
                                            data.setClassType(ClassType.BREAKER);
                                        }
                                    });
                                    return 1;
                                }))));
        dispatcher.register(Commands.literal("start").requires(ctx->ctx.hasPermission(Commands.LEVEL_MODERATORS)).executes(ctx->{
            GameDataCache.forceStart();
            return 1;
        }));
        dispatcher.register(Commands.literal("waiting").requires(ctx->ctx.hasPermission(Commands.LEVEL_MODERATORS)).then(Commands.argument("center", BlockPosArgument.blockPos()).executes(ctx->{
            try {
                GameDataCache.generateWaiting(BlockPosArgument.getBlockPos(ctx, "center"));
            } catch (Throwable e){
                e.printStackTrace();
            }
            return 1;
        })).executes(ctx->{
            GameDataCache.generateWaiting();
            return 1;
        }));
        dispatcher.register(Commands.literal("plustime").requires(ctx->ctx.hasPermission(Commands.LEVEL_MODERATORS)).then(Commands.argument("time", IntegerArgumentType.integer()).executes(ctx->{
            GameDataCache.periodSeconds+=IntegerArgumentType.getInteger(ctx, "time");
            GeneralUtil.sendToAllPlayers(Component.literal("Increased stage time to " + GameDataCache.periodSeconds + " seconds."));
            return 1;
        })));
        dispatcher.register(Commands.literal("loadcached").requires(ctx->ctx.hasPermission(Commands.LEVEL_MODERATORS)).executes(ctx->{
            try {
                GameDataCache.init();
            } catch (Throwable t){
                t.printStackTrace();
            }
            return 1;
        }));
        dispatcher.register(Commands.literal("savecache").requires(ctx->ctx.hasPermission(Commands.LEVEL_MODERATORS)).executes(ctx->{
            try {
                GameDataCache.save();
            } catch (Throwable t){
                t.printStackTrace();
            }
            return 1;
        }));
        dispatcher.register(Commands.literal("wiki").executes(ctx->{
            String url = "https://github.com/Drathonix/capturetheflag/wiki/gameplay";
            if(ctx.getSource().getEntity() instanceof ServerPlayer sp){
                ClassType type = CTFPlayerData.get(sp).getClassType();
                if(type != null){
                    url = type.wikiURL();
                }
            }
            ctx.getSource().sendSystemMessage(Component.literal(url).setStyle(Style.EMPTY.withUnderlined(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,url)).withColor(ChatFormatting.GREEN)));
            return 1;
        }));
        dispatcher.register(Commands.literal("nextphase").requires(ctx->ctx.hasPermission(Commands.LEVEL_MODERATORS)).executes(ctx->{
            GameDataCache.nextPhase();
            return 1;
        }));
        dispatcher.register(Commands.literal("randomizeteams").requires(ctx->ctx.hasPermission(Commands.LEVEL_MODERATORS)).executes(ctx->{
            List<ServerPlayer> shuffled = GeneralUtil.shuffle(CTF.server.getPlayerList().getPlayers());
            int cutoff = shuffled.size()/2;
            for (int i = 0; i < shuffled.size(); i++) {
                TeamState state;
                if(i < cutoff){
                    state=TeamState.BLUE;
                }
                else{
                    state=TeamState.RED;
                }
                CTFPlayerData data = CTFPlayerData.get(shuffled.get(i));
                data.setTeamState(state);
                shuffled.get(i).sendSystemMessage(Component.literal("You have been assigned to team " + state.name() + " use '/class' to select your class."));
                if(data.getClassType() == null) {
                    data.setClassType(ClassType.BREAKER);
                }
            }
            return 1;
        }));
        dispatcher.register(Commands.literal("showteams").executes(ctx->{
            StringBuilder blueTeam = new StringBuilder();
            StringBuilder redTeam = new StringBuilder();
            for (ServerPlayer player : CTF.server.getPlayerList().getPlayers()) {
                CTFPlayerData data = CTFPlayerData.get(player);
                if(data.getTeamState() == TeamState.BLUE){
                    if(!blueTeam.isEmpty()){
                        blueTeam.append(", ");
                    }
                    else{
                        blueTeam.append(player.getGameProfile().getName());
                    }
                }
                else {
                    if(!redTeam.isEmpty()){
                        redTeam.append(", ");
                    }
                    else{
                        redTeam.append(player.getGameProfile().getName());
                    }
                }
            }
            ctx.getSource().sendSystemMessage(Component.literal("Team Blue: " + blueTeam).withStyle(ChatFormatting.BLUE));
            ctx.getSource().sendSystemMessage(Component.literal("Team Red: " + redTeam).withStyle(ChatFormatting.RED));
            return 1;
        }));
        dispatcher.register(Commands.literal("initplayer").requires(ctx->ctx.hasPermission(Commands.LEVEL_MODERATORS))
                .then(Commands.argument("team",StringArgumentType.string())
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(ctx->{
                                    TeamState state;
                                    try {
                                        state = TeamState.valueOf(StringArgumentType.getString(ctx, "team"));
                                    } catch (Exception e){
                                        ctx.getSource().sendFailure(Component.literal("No such team : " + StringArgumentType.getString(ctx, "team")));
                                        return 0;
                                    }
                                    EntityArgument.getPlayers(ctx, "players").forEach(player->{
                                        player.sendSystemMessage(Component.literal("You have been assigned to team " + state.name() + " use '/class' to select your class."));
                                        CTFPlayerData data = CTFPlayerData.get(player);
                                        data.setTeamState(state);
                                        if(data.getClassType() == null) {
                                            data.setClassType(ClassType.BREAKER);
                                        }
                                        state.onStart(player);
                                        player.getInventory().clearContent();
                                        CTFPlayerData.get(player).requireClassType(c->{
                                            CTF.respawn(player);
                                        });
                                        player.giveExperienceLevels(CTFConfig.startingLevels);
                                    });
                                    return 1;
                                }))));
    }
}
