package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.ArrowType;
import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import com.drathonix.capturetheflag.common.util.GeneralUtil;
import com.google.common.collect.Sets;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.*;


public enum Skill {
    SNIFFER(ClassType.BREAKER){
        @Override
        public ItemStack getIcon() {
            ItemStack out = new ItemStack(Items.SNIFFER_EGG);
            out.set(DataComponents.ITEM_NAME, Component.literal("Sniffer's Blessing").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN));
            ItemLore lore = ItemLore.EMPTY;
            lore = lore.withLineAdded(Component.literal("[Breaker Skill]").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GREEN)));
            lore = lore.withLineAdded(Component.literal("Clicking a block with a pickaxe will detect ores or air.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Has a range of 5 blocks.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Only reports the first block detected.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("The pickaxe must be able to mine the ore to detect it.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Using the ability consumes durability equal to the detected block's value.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Range is extended by 2 for breakers.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            out.set(DataComponents.LORE, lore);
            return out;
        }
        public void onClickBlock(ServerPlayer player, ServerLevel level, BlockPos pos, Direction direction){
            direction = direction.getOpposite();
            ItemStack stack = player.getItemBySlot(EquipmentSlot.MAINHAND);
            Item item = stack.getItem();
            Set<Block> detectables = detection.get(item);
            if(detectables == null){
                return;
            }
            int dist = 5;
            if(CTFPlayerData.get(player).getClassType() == classType){
                dist+=2;
            }
            int i = 0;
            while(i < dist){
                Block block = level.getBlockState(pos).getBlock();
                if(detectables.contains(block)){
                    int cost = durabilityCost.getOrDefault(block,1);
                    stack.setDamageValue(stack.getDamageValue()+cost);
                    player.setItemSlot(EquipmentSlot.MAINHAND,stack);
                    GeneralUtil.displayInActionBar(block.getName().append(" detected!").withStyle(ChatFormatting.AQUA),player);
                    return;
                }
                pos = pos.relative(direction);
                i++;
            }
            GeneralUtil.displayInActionBar(Component.literal("Didn't detect anything.").withStyle(ChatFormatting.RED),player);
        }
        private final Map<Block, Integer> durabilityCost = new HashMap<>();

        private final Map<Item, Set<Block>> detection = new HashMap<>();
        {
            durabilityCost.put(Blocks.CAVE_AIR,1);
            durabilityCost.put(Blocks.COPPER_ORE,2);
            durabilityCost.put(Blocks.COAL_ORE,3);
            durabilityCost.put(Blocks.IRON_ORE,4);
            durabilityCost.put(Blocks.REDSTONE_ORE,6);
            durabilityCost.put(Blocks.GOLD_ORE,8);
            durabilityCost.put(Blocks.LAPIS_ORE,12);
            durabilityCost.put(Blocks.DIAMOND_ORE,16);

            durabilityCost.put(Blocks.DEEPSLATE_COPPER_ORE,2);
            durabilityCost.put(Blocks.DEEPSLATE_COAL_ORE,3);
            durabilityCost.put(Blocks.DEEPSLATE_IRON_ORE,4);
            durabilityCost.put(Blocks.DEEPSLATE_REDSTONE_ORE,6);
            durabilityCost.put(Blocks.DEEPSLATE_GOLD_ORE,8);
            durabilityCost.put(Blocks.DEEPSLATE_LAPIS_ORE,12);
            durabilityCost.put(Blocks.DEEPSLATE_DIAMOND_ORE,16);

            detection.put(Items.WOODEN_PICKAXE, Sets.newHashSet(Blocks.CAVE_AIR,Blocks.COAL_ORE));
            detection.put(Items.STONE_PICKAXE, Sets.newHashSet(Blocks.CAVE_AIR,Blocks.COAL_ORE,Blocks.IRON_ORE,Blocks.COPPER_ORE,Blocks.LAPIS_ORE,Blocks.DEEPSLATE_COAL_ORE,Blocks.DEEPSLATE_IRON_ORE,Blocks.DEEPSLATE_COPPER_ORE,Blocks.DEEPSLATE_LAPIS_ORE));
            detection.put(Items.GOLDEN_PICKAXE, Sets.newHashSet(Blocks.CAVE_AIR,Blocks.COAL_ORE,Blocks.IRON_ORE,Blocks.COPPER_ORE,Blocks.LAPIS_ORE,Blocks.DEEPSLATE_COAL_ORE,Blocks.DEEPSLATE_IRON_ORE,Blocks.DEEPSLATE_COPPER_ORE,Blocks.DEEPSLATE_LAPIS_ORE));
            detection.put(Items.IRON_PICKAXE, Sets.newHashSet(Blocks.CAVE_AIR,Blocks.COAL_ORE,Blocks.IRON_ORE,Blocks.COPPER_ORE,Blocks.LAPIS_ORE,Blocks.REDSTONE_ORE,Blocks.GOLD_ORE,Blocks.DIAMOND_ORE,Blocks.DEEPSLATE_COAL_ORE,Blocks.DEEPSLATE_IRON_ORE,Blocks.DEEPSLATE_COPPER_ORE,Blocks.DEEPSLATE_LAPIS_ORE,Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE_DIAMOND_ORE));
            detection.put(Items.DIAMOND_PICKAXE, Sets.newHashSet(Blocks.CAVE_AIR,Blocks.COAL_ORE,Blocks.IRON_ORE,Blocks.COPPER_ORE,Blocks.LAPIS_ORE,Blocks.REDSTONE_ORE,Blocks.GOLD_ORE,Blocks.DIAMOND_ORE,Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE_DIAMOND_ORE));
            detection.put(Items.NETHERITE_PICKAXE, Sets.newHashSet(Blocks.CAVE_AIR,Blocks.COAL_ORE,Blocks.IRON_ORE,Blocks.COPPER_ORE,Blocks.LAPIS_ORE,Blocks.REDSTONE_ORE,Blocks.GOLD_ORE,Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE_DIAMOND_ORE));
        }
    },
    FORTUNATE(ClassType.BREAKER){
        @Override
        public ItemStack getIcon() {
            ItemStack out = new ItemStack(Items.EMERALD);
            out.set(DataComponents.ITEM_NAME, Component.literal("Fortunate").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.LIGHT_PURPLE));
            ItemLore lore = ItemLore.EMPTY;
            lore = lore.withLineAdded(Component.literal("[Breaker Skill]").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GREEN)));
            lore = lore.withLineAdded(Component.literal("Tool fortune level is occasionally increased by 1.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("This has no effect above y64 and gets stronger deeper underground.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Chance maxes out below y-20 at 50%.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Chance for Breakers is doubled.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            out.set(DataComponents.LORE, lore);
            return out;
        }
    },
    COBBLER(ClassType.ARCHITECT){
        @Override
        public void tickSecond(ServerPlayer player) {
            NonNullList<ItemStack> stacks = player.getInventory().items;
            CTFPlayerData data = CTFPlayerData.get(player);
            Item reward = data.hasSkill(FORTIFIER) ? Items.NETHER_BRICKS : Items.COBBLESTONE;
            int k = 0;
            for (ItemStack stack : stacks) {
                if(stack.getItem() == reward){
                    k+=stack.getCount();
                }
                if(k >= 128){
                    return;
                }
            }
            int rate = data.getClassType() == classType ? 2 : 1;
            int emptySlot = -1;
            for (int i = 0; i < stacks.size(); i++) {
                ItemStack stack = stacks.get(i);
                if(stack.getItem() == reward && stack.getCount() < stack.getMaxStackSize()) {
                    stack.grow(rate);
                    return;
                }
                if(stack.isEmpty() && emptySlot == -1){
                    emptySlot = i;
                }
            }
            if(emptySlot != -1){
                stacks.set(emptySlot, new ItemStack(reward,2));
            }
        }

        @Override
        public ItemStack getIcon() {
            ItemStack out = new ItemStack(Items.COBBLESTONE);
            out.set(DataComponents.ITEM_NAME, Component.literal("Cobbler").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
            ItemLore lore = ItemLore.EMPTY;
            lore = lore.withLineAdded(Component.literal("[Architect Skill]").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GREEN)));
            lore = lore.withLineAdded(Component.literal("Generates 1 cobblestone constantly.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Maxes out at 128 stacks in the inventory.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Architects produce 2 times faster.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            out.set(DataComponents.LORE, lore);
            return out;
        }
    },
    FORTIFIER(ClassType.ARCHITECT){
        @Override
        public ItemStack getIcon() {
            ItemStack out = new ItemStack(Items.NETHER_BRICKS);
            out.set(DataComponents.ITEM_NAME, Component.literal("Fortifier").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
            ItemLore lore = ItemLore.EMPTY;
            lore = lore.withLineAdded(Component.literal("[Architect Skill]").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GREEN)));
            lore = lore.withLineAdded(Component.literal("30% chance to not consume durability.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Applies after unbreaking.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("The cobbler ability generates netherbrick instead.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Netherbricks are immune to the blast pickaxe").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Architects have a 40% chance to unbreak.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            out.set(DataComponents.LORE, lore);
            return out;
        }
    },
    BLOOD_LUST(ClassType.SLAYER){
        @Override
        public ItemStack getIcon() {
            ItemStack out = new ItemStack(Items.FIRE_CHARGE);
            out.set(DataComponents.ITEM_NAME, Component.literal("Blood Lust").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
            ItemLore lore = ItemLore.EMPTY;
            lore = lore.withLineAdded(Component.literal("[Slayer Skill]").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GREEN)));
            lore = lore.withLineAdded(Component.literal("Killing an entity grants 7 seconds of Strength and Swiftness.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Duration for Slayers increased to 10.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            out.set(DataComponents.LORE, lore);
            return out;
        }

        @Override
        public void onKill(ServerPlayer player) {
            player.playSound(SoundEvents.WOLF_GROWL);
            int duration = CTFPlayerData.get(player).getClassType() == classType ? 10 : 8;
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,duration*20));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,duration*20));
        }
    },
    BULWARK(ClassType.SLAYER){
        @Override
        public ItemStack getIcon() {
            ItemStack out = new ItemStack(Items.TURTLE_SCUTE);
            out.set(DataComponents.ITEM_NAME, Component.literal("Bulwark").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
            ItemLore lore = ItemLore.EMPTY;
            lore = lore.withLineAdded(Component.literal("[Slayer Skill]").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GREEN)));
            lore = lore.withLineAdded(Component.literal("Blocking an attack grants 3 seconds of Resistance.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Works even if the attack has broken the shield.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Essentially a brief 20% damage reduction.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Duration increased to 5 for slayers.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            out.set(DataComponents.LORE, lore);
            return out;
        }

        @Override
        public void onBlock(ServerPlayer player) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, CTFPlayerData.get(player).getClassType() == classType ? 100 : 60,0,false,false,true));
        }
    },
    SCROUNGER(ClassType.RANGER){
        @Override
        public ItemStack getIcon() {
            ItemStack out = new ItemStack(Items.ARROW);
            out.set(DataComponents.ITEM_NAME, Component.literal("Scrounger").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN));
            ItemLore lore = ItemLore.EMPTY;
            lore = lore.withLineAdded(Component.literal("[Ranger Skill]").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GREEN)));
            lore = lore.withLineAdded(Component.literal("20% chance per second to generate an arrow").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("50% chance for rangers").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            out.set(DataComponents.LORE, lore);
            return out;
        }
        @Override
        public void tickSecond(ServerPlayer player) {
            if(Math.random() > (CTFPlayerData.get(player).getClassType() == classType ? 0.5F : 0.2F)){
                return;
            }
            NonNullList<ItemStack> stacks = player.getInventory().items;
            ArrowType type = ArrowType.NORMAL;
            int k = 0;
            for (ItemStack stack : stacks) {
                if(stack.getItem() == Items.ARROW || stack.getItem() == Items.TIPPED_ARROW){
                    k+=stack.getCount();
                }
                if(k >= type.amount){
                    return;
                }
            }
            int emptySlot = -1;
            for (int i = 0; i < stacks.size(); i++) {
                ItemStack stack = stacks.get(i);
                if(stack.getItem() == type.type && stack.getCount() < type.amount) {
                    stack.grow(1);
                    return;
                }
                if(stack.isEmpty() && emptySlot == -1){
                    emptySlot = i;
                }
            }
            if(emptySlot != -1){
                stacks.set(emptySlot, type.createStack());
            }
        }
    },
    SIXTH_SENSE(ClassType.RANGER){
        @Override
        public ItemStack getIcon() {
            ItemStack out = new ItemStack(Items.ENDER_EYE);
            out.set(DataComponents.ITEM_NAME, Component.literal("Sixth Sense").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN));
            ItemLore lore = ItemLore.EMPTY;
            lore = lore.withLineAdded(Component.literal("[Ranger Skill]").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GREEN)));
            lore = lore.withLineAdded(Component.literal("Players and mobs within a 6 block radius will glow.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Sneaking players are undetected.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Range is increased by 2 for rangers.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)));
            out.set(DataComponents.LORE, lore);
            return out;
        }
        @Override
        public void tickSecond(ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, new AABB(player.blockPosition()).inflate(CTFPlayerData.get(player).getClassType() == classType ? 8 : 6))) {
                if(living.isInvisible()){
                    continue;
                }
                if(living instanceof ServerPlayer sp){
                    if(sp.isCrouching() || TeamState.get(sp) == TeamState.get(player)){
                        continue;
                    }
                }
                living.addEffect(new MobEffectInstance(MobEffects.GLOWING,100,0,false,false,false));
            }
        }
    };

    public final ClassType classType;

    Skill(ClassType classType) {
        this.classType = classType;
    }
    public void onClickBlock(ServerPlayer player, ServerLevel level, BlockPos pos, Direction direction){}
    public void onBlock(ServerPlayer player){}
    public void tickSecond(ServerPlayer player){}
    public void onKill(ServerPlayer player){}
    public abstract ItemStack getIcon();
}
