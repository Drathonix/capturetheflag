package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.bridge.IMixinAbstractFurnaceBlockEntity;
import com.drathonix.capturetheflag.common.component.CustomDatas;
import com.drathonix.capturetheflag.common.config.CTFConfig;
import com.drathonix.capturetheflag.common.config.ItemsConfig;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.system.phasing.PhaseFlag;
import com.drathonix.capturetheflag.common.system.stands.ArmorStandMarkers;
import com.drathonix.capturetheflag.common.util.DirectionUtil;
import com.drathonix.capturetheflag.common.util.GeneralUtil;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.*;
import dev.architectury.utils.value.IntValue;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class CTFEventHandler {

    public static void init() {
        EntityEvent.LIVING_DEATH.register(CTFEventHandler::onLivingDeath);
        EntityEvent.LIVING_HURT.register(CTFEventHandler::onLivingHurt);
        PlayerEvent.PLAYER_CLONE.register(CTFEventHandler::onPlayerClone);
        PlayerEvent.DROP_ITEM.register(CTFEventHandler::onPlayerDrop);
        PlayerEvent.PICKUP_ITEM_PRE.register(CTFEventHandler::onPlayerPickup);
        BlockEvent.PLACE.register(CTFEventHandler::onBlockPlace);
        BlockEvent.BREAK.register(CTFEventHandler::onBlockBreak);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(CTFEventHandler::onClickBlock);
        InteractionEvent.LEFT_CLICK_BLOCK.register(CTFEventHandler::onHitBlock);
        TickEvent.SERVER_POST.register(GameDataCache::tick);
    }

    private static EventResult onPlayerPickup(Player player, ItemEntity itemEntity, ItemStack stack) {
        if(player instanceof ServerPlayer sp && !itemEntity.isRemoved()) {
            TeamState state = CTFPlayerData.get(sp).getTeamState();
            if(state != null) {
                int lapis = 0;
                if(stack.getItem() == Items.LAPIS_LAZULI){
                    lapis = stack.getCount();
                }
                if(stack.getItem() == Items.LAPIS_BLOCK){
                    lapis = stack.getCount()*9;
                }
                if(stack.getItem() == Items.ENDER_PEARL){
                    lapis = stack.getCount()*64;
                }
                if(lapis > 0) {

                    state.growLapis(lapis);
                    MutableComponent message = Component.literal("Your team collected " + lapis + " lapis, you now have " + state.getLapis() + ".");
                    for (ServerPlayer serverPlayer : sp.getServer().getPlayerList().getPlayers()) {
                        if (CTFPlayerData.get(serverPlayer).getTeamState() == state) {
                            serverPlayer.sendSystemMessage(message);
                        }
                    }
                    itemEntity.kill(sp.serverLevel());
                    return EventResult.interruptFalse();
                }
            }
        }
        return EventResult.pass();
    }

    private static EventResult onPlayerDrop(Player player, ItemEntity itemEntity) {
        if(CustomDatas.getSoulBound(itemEntity.getItem()) != -1){
            return EventResult.interruptFalse();
        }
        return EventResult.interruptTrue();
    }

    private static EventResult onLivingHurt(LivingEntity livingEntity, DamageSource source, float v) {
        if(GameDataCache.getGamePhase().flags.contains(PhaseFlag.INVULN)){
            return EventResult.interruptFalse();
        }
        if(source.getEntity() instanceof ServerPlayer sp){
            CTFPlayerData data = CTFPlayerData.get(sp);
            if(data.getTeamState() != null) {
                if(GameDataCache.getGamePhase().flags.contains(PhaseFlag.RESTRICTED) && data.getTeamState().getOpposite().isWithinTerritory(livingEntity)) {
                    return EventResult.interruptFalse();
                }
            }
        }
        return EventResult.pass();
    }


    public synchronized static boolean shouldCancelBlockEvent(ServerLevel level, BlockPos pos) {
        boolean[] cancel = new boolean[]{false};
        ArmorStandMarkers.forEachStand(ArmorStandMarkers.clearBeacon, stand -> {
            if (pos.getY() >= stand.getBlockY()) {
                if (stand.blockPosition().equals(new BlockPos(pos.getX(), (int) stand.getY(), pos.getZ()))) {
                    cancel[0] = true;
                    return true;
                }
            }
            return false;
        });
        if (cancel[0]) {
            return true;
        }
        for (BoundingBox permanentStructureAABB : GameDataCache.permanentStructureAABBs) {
            if (permanentStructureAABB.isInside(pos)) {
                return true;
            }
        }
        /*
        Map<Structure, LongSet> structures = level.structureManager().getAllStructuresAt(pos);
        for (Map.Entry<Structure, LongSet> entry : structures.entrySet()) {
            if(GameGenerator.protectedStructures.contains(level.registryAccess().lookup(Registries.STRUCTURE).get().getKey(entry.getKey()))){
                System.out.println("Cancel b/c protected");
                return true;
            }
        }*/
        return false;
    }

    private synchronized static InteractionResult onClickBlock(Player player, InteractionHand interactionHand, BlockPos pos, Direction direction) {
        if(player instanceof ServerPlayer sp) {
            if (shouldCancelBlockEvent(sp.serverLevel(),pos)){
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

    private static InteractionResult onHitBlock(Player player, InteractionHand interactionHand, BlockPos pos, Direction direction) {
        if(player instanceof ServerPlayer sp) {
            if (shouldCancelBlockEvent(sp.serverLevel(),pos)){
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

    private synchronized static EventResult onBlockBreak(Level level, BlockPos pos, BlockState state, ServerPlayer sp, IntValue xp) {
        if (sp != null && level instanceof ServerLevel sl) {
            if (shouldCancelBlockEvent(sl, pos)) {
                return EventResult.interruptFalse();
            }
            ItemStack stack = sp.getItemInHand(InteractionHand.MAIN_HAND);
            if (CustomItem.BLASTPICKAXE.is(stack)) {
                if (!state.getTags().filter(tag -> tag == BlockTags.MINEABLE_WITH_PICKAXE).toList().isEmpty()) {
                    //Prevent recursive actions.
                    CustomDatas.setCustomItemType(stack, CustomItem.NONE);
                    for (BlockPos blockPos : DirectionUtil.adjacents(Direction.orderedByNearest(sp)[0]).translated(pos)) {
                        if (!shouldCancelBlockEvent(sl, blockPos)
                                && !level.getBlockState(blockPos).getTags().filter(tag -> tag == BlockTags.MINEABLE_WITH_PICKAXE).toList().isEmpty()) {
                            sp.gameMode.destroyBlock(blockPos);
                        }
                    }
                    CustomDatas.setCustomItemType(stack, CustomItem.BLASTPICKAXE);
                    level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.1F, 1F);
                }
            }
        }
        return EventResult.pass();
    }

    private synchronized static EventResult onBlockPlace(Level level, BlockPos pos, BlockState state, Entity placer) {
        if (placer instanceof ServerPlayer sp) {
            if (shouldCancelBlockEvent(sp.serverLevel(), pos)) {
                return EventResult.interruptFalse();
            }
            if (state.getBlock() instanceof AbstractFurnaceBlock) {
                CTFPlayerData.get(sp).requireClassType(type -> {
                    if (type.abilities.contains(SpecialAbility.SMELTLER)) {
                        IMixinAbstractFurnaceBlockEntity.ref.put(pos.asLong(), sp.getUUID());
                    }
                });
            }
            if (CustomItem.BRICKLAYER.is(sp.getOffhandItem())) {
                Direction dir = orderedByNearestSpecial(sp)[0].getOpposite();
                BlockPos pk = pos.relative(dir);
                ItemStack mainHand = sp.getItemInHand(InteractionHand.MAIN_HAND);
                Block block = ((BlockItem) mainHand.getItem()).getBlock();
                CTFPlayerData.get(sp).requireClassType(type -> {
                    int k = type == ClassType.BUILDER ? ItemsConfig.brickLayerPlacementBuilder : ItemsConfig.brickLayerPlacement;
                    BlockPos ps = pk;
                    while (level.getEntitiesOfClass(LivingEntity.class,new AABB(ps)).isEmpty() && !mainHand.isEmpty() && k > 0 && level.isInWorldBounds(ps)) {
                        try {
                            if (!shouldCancelBlockEvent(sp.serverLevel(), ps)) {
                                level.setBlockAndUpdate(ps, block.getStateForPlacement(new BlockPlaceContext(sp, InteractionHand.MAIN_HAND, mainHand, BlockHitResult.miss(new Vec3(ps), dir.getOpposite(), ps))));
                            }
                        } catch (Throwable t) {
                            t.printStackTrace();
                            return;
                        }
                        if (!sp.hasInfiniteMaterials()) {
                            mainHand.shrink(1);
                        }
                        ps = ps.relative(dir);
                        k--;
                    }
                });
            }
        }
        return EventResult.pass();
    }

    private synchronized static void onPlayerClone(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean wonGame) {
        if (!wonGame) {
            CTF.respawn(newPlayer);
        }
    }

    private synchronized static EventResult onLivingDeath(LivingEntity livingEntity, DamageSource source) {
        if (source.getEntity() instanceof ServerPlayer sp) {
            CTFPlayerData data = CTFPlayerData.get(sp);
            data.requireClassType(type -> {
                for (SpecialAbility ability : type.abilities) {
                    ability.onKill(sp);
                }
            });
        }
        if(livingEntity instanceof ServerPlayer sp){
            CTFPlayerData data = CTFPlayerData.get(sp);
            data.requireTeam(teamState -> {
                if(data.hasFlag()){
                    GeneralUtil.sendToAllPlayers(Component.empty()
                            .withStyle(ChatFormatting.GREEN)
                            .withStyle(ChatFormatting.BOLD)
                            .append(sp.getDisplayName())
                            .append(Component.literal(" has dropped the " + teamState.getOpposite().name().toLowerCase() + " it has been returned and placed on a " + GeneralUtil.convertSeconds(CTFConfig.stealFlagCooldownTime) + " second re-steal cooldown!")));
                    teamState.getOpposite().cooldownEnd = System.currentTimeMillis()+CTFConfig.stealFlagCooldownTime*1000;
                    data.setHasFlag(false);
                }
            });
        }
        return EventResult.pass();
    }

    public static Direction[] orderedByNearestSpecial(Entity entity) {
        float f = entity.getViewXRot(1.0F) * 0.017453292F;
        float g = -entity.getViewYRot(1.0F) * 0.017453292F;
        float h = Mth.sin(f);
        float i = Mth.cos(f);
        float j = Mth.sin(g);
        float k = Mth.cos(g);
        boolean bl = j > 0.0F;
        boolean bl2 = h < 0.0F;
        boolean bl3 = k > 0.0F;
        float l = bl ? j : -j;
        float m = (bl2 ? -h : h)*0.33f;
        float n = bl3 ? k : -k;
        float o = l * i;
        float p = n * i;
        Direction direction = bl ? Direction.EAST : Direction.WEST;
        Direction direction2 = bl2 ? Direction.UP : Direction.DOWN;
        Direction direction3 = bl3 ? Direction.SOUTH : Direction.NORTH;
        if (l > n) {
            if (m > o) {
                return makeDirectionArray(direction2, direction, direction3);
            } else {
                return p > m ? makeDirectionArray(direction, direction3, direction2) : makeDirectionArray(direction, direction2, direction3);
            }
        } else if (m > p) {
            return makeDirectionArray(direction2, direction3, direction);
        } else {
            return o > m ? makeDirectionArray(direction3, direction, direction2) : makeDirectionArray(direction3, direction2, direction);
        }
    }
    private static Direction[] makeDirectionArray (Direction direction, Direction direction2, Direction direction3){
        return new Direction[]{direction, direction2, direction3, direction3.getOpposite(), direction2.getOpposite(), direction.getOpposite()};
    }
}
