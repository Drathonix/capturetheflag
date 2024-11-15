package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.gui.ClassSelection;
import com.drathonix.capturetheflag.common.gui.HolyEnchanter;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.phasing.PhaseFlag;
import com.drathonix.capturetheflag.common.system.stands.ArmorStandMarkers;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import java.awt.*;


public class CTFCommands {
    public static void init(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("class").executes(ctx->{
            if(ctx.getSource().getEntity() instanceof ServerPlayer sp){
                if(CTFPlayerData.get(sp).allowChangeClass){
                    sp.openMenu(new SimpleMenuProvider((id, inv, player)-> new ClassSelection(id,inv, InventoryWrapper.of(NonNullList.withSize(9*3, ItemStack.EMPTY),0)), Component.literal("Class Selection").withStyle(Style.EMPTY.withBold(true).withColor(Color.YELLOW.getRGB()))));
                }
                else{
                    sp.sendSystemMessage(Component.literal("You cannot change your class right now.").withStyle(ChatFormatting.RED));
                }
            }
            return 1;
        }));
        dispatcher.register(Commands.literal("flag").executes(ctx->{
            if(!GameDataCache.getGamePhase().flags.contains(PhaseFlag.HOME)){
                ctx.getSource().sendFailure(Component.literal("You cannot use this command right now"));
                return 0;
            }
            if(ctx.getSource().getEntity() instanceof ServerPlayer sp){
                CTFPlayerData data = CTFPlayerData.get(sp);
                data.requireTeam(team->{
                    if(System.currentTimeMillis() >= data.homeCooldown){
                        Vec3 vec = team.getSpawn().getCenter();
                        sp.teleportTo(vec.x,vec.y,vec.z);
                    }
                    else{
                        sp.sendSystemMessage(Component.literal("/flag is on cooldown for " + GeneralUtil.convertSeconds(data.homeCooldown) + ", cannot teleport.").withStyle(ChatFormatting.RED));
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
                                            CTFPlayerData.get(player).setClassType(ClassType.MINER);
                                        }
                                    });
                                    return 1;
                                }))));
        dispatcher.register(Commands.literal("start").requires(ctx->ctx.hasPermission(Commands.LEVEL_MODERATORS)).executes(ctx->{
            GameDataCache.forceStart();
            return 1;
        }));
        dispatcher.register(Commands.literal("waiting").requires(ctx->ctx.hasPermission(Commands.LEVEL_MODERATORS)).then(Commands.argument("center", BlockPosArgument.blockPos()).executes(ctx->{
            GameDataCache.generateWaiting(BlockPosArgument.getBlockPos(ctx,"center"));
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
    }
}
