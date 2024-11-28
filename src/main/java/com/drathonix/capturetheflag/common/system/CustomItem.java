package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.component.CustomDatas;
import com.drathonix.capturetheflag.common.gui.ClassSelection;
import com.drathonix.capturetheflag.common.gui.SkillSelection;
import com.drathonix.capturetheflag.common.injected.CTFPlayerData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public enum CustomItem {
    NONE{
        @Override
        public ItemStack getAsStack(Random random) {
            return ItemStack.EMPTY;
        }
    },
    BLASTPICKAXE{
        @Override
        public int maxifyEnchant(Holder<Enchantment> retriever, int n) {
            if(retriever.unwrapKey().get() == Enchantments.EFFICIENCY){
                return Math.min(n,2);
            }
            if(retriever.unwrapKey().get() == Enchantments.FORTUNE){
                return Math.min(n,1);
            }
            return n;
        }

        @Override
        public ItemStack getAsStack(Random random) {
            ItemStack stack = new ItemStack(Items.NETHERITE_PICKAXE,1);
            CustomDatas.setCustomItemType(stack,this);
            CustomDatas.setSoulBound(stack,3);
            stack.set(DataComponents.ITEM_NAME, Component.literal("Blast Pickaxe").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));
            ItemLore lore = ItemLore.EMPTY;
            lore = CustomDatas.addSoulBoundLore(lore);
            lore = lore.withLineAdded(Component.literal("Only craftable by breakers")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withBold(true)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Breaks a 3x3 area.")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Still consumes durability as if every block was manually broken.")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Reduced Enchantability.")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            stack.set(DataComponents.LORE,lore);
            return stack;
        }
    },
    BRICKLAYER{
        @Override
        public ItemStack getAsStack(Random random) {
            ItemStack stack = new ItemStack(Items.NETHERITE_SHOVEL,1);
            CustomDatas.setCustomItemType(stack,this);
            CustomDatas.setSoulBound(stack,3);
            stack.set(DataComponents.ITEM_NAME, Component.literal("Bricklayer").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));
            ItemLore lore = ItemLore.EMPTY;
            lore = CustomDatas.addSoulBoundLore(lore);
            lore = lore.withLineAdded(Component.literal("Only craftable by Architects")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withBold(true)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Extends block reach by 2 (*1).")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Places 6 (*3) blocks at once towards you.")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("This item's ability only works in the offhand.")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("'*' indicates the value for non-architects.")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            stack.set(DataComponents.LORE,lore);
            return stack;
        }
    },
    LUMBERJAXE{
        @Override
        public ItemStack getAsStack(Random random) {
            return ItemStack.EMPTY;
        }
    },
    BIGBUCKET{
        @Override
        public ItemStack getAsStack(@Nullable Random random) {
            boolean filled = true;
            if(random != null){
                filled = random.nextBoolean();
            }
            ItemStack stack = new ItemStack(filled ? Items.WATER_BUCKET : Items.BUCKET,1);
            CustomDatas.setCustomItemType(stack,this);
            CustomDatas.setSoulBound(stack,3);
            if(filled) {
                stack.set(DataComponents.ITEM_NAME, Component.literal("Bottomless Water Bucket").withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.BOLD));
                ItemLore lore = ItemLore.EMPTY;
                lore = CustomDatas.addSoulBoundLore(lore);
                lore = lore.withLineAdded(Component.literal("Only craftable by Architects")
                        .withStyle(Style.EMPTY.withItalic(false)
                                .withBold(true)
                                .withColor(ChatFormatting.YELLOW)));
                lore = lore.withLineAdded(Component.literal("Infinite water bucket.")
                        .withStyle(Style.EMPTY.withItalic(false)
                                .withColor(ChatFormatting.YELLOW)));
                lore = lore.withLineAdded(Component.literal("With great power comes great responsibility.")
                        .withStyle(Style.EMPTY.withItalic(false)
                                .withColor(ChatFormatting.YELLOW)));
                stack.set(DataComponents.LORE, lore);
            }
            else{
                stack.set(DataComponents.ITEM_NAME, Component.literal("Bottomless Bucket").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                ItemLore lore = ItemLore.EMPTY;
                lore = CustomDatas.addSoulBoundLore(lore);
                lore = lore.withLineAdded(Component.literal("Only craftable by Architects")
                        .withStyle(Style.EMPTY.withItalic(false)
                                .withBold(true)
                                .withColor(ChatFormatting.YELLOW)));
                lore = lore.withLineAdded(Component.literal("Infinitely empty, never fills.")
                        .withStyle(Style.EMPTY.withItalic(false)
                                .withColor(ChatFormatting.YELLOW)));
                stack.set(DataComponents.LORE, lore);
            }
            return stack;
        }
    },
    MACE{
        @Override
        public ItemStack getAsStack(@Nullable Random random) {
            ItemStack stack = new ItemStack(Items.MACE,1);
            CustomDatas.setCustomItemType(stack,this);
            CustomDatas.setSoulBound(stack,3);
            stack.set(DataComponents.ITEM_NAME, Component.literal("Heavy Mace").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD));
            ItemLore lore = ItemLore.EMPTY;
            lore = CustomDatas.addSoulBoundLore(lore);
            lore = lore.withLineAdded(Component.literal("Only craftable by Slayers")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withBold(true)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Deals bonus damage depending on how far you fall.")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Negates fall damage on a successful hit.")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            stack.set(DataComponents.LORE,lore);
            return stack;
        }
    },
    REFLECTORSHIELD{
        @Override
        public ItemStack getAsStack(Random random) {
            ItemStack stack = new ItemStack(Items.SHIELD,1);
            CustomDatas.setCustomItemType(stack,this);
            CustomDatas.setSoulBound(stack,3);
            stack.set(DataComponents.ITEM_NAME, Component.literal("Mirror Shield").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD));
            stack.enchant(CTF.server.registryAccess().get(Enchantments.UNBREAKING).get(),1);
            stack.set(DataComponents.BASE_COLOR, DyeColor.BLACK);
            stack.set(DataComponents.BANNER_PATTERNS, new BannerPatternLayers.Builder().add(CTF.server.registryAccess().get(BannerPatterns.CURLY_BORDER).get(),DyeColor.WHITE).build());
            ItemLore lore = ItemLore.EMPTY;
            lore = CustomDatas.addSoulBoundLore(lore);
            lore = lore.withLineAdded(Component.literal("Only craftable by Slayers")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withBold(true)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Reflects 7.5% (*4%) damage on a successful block")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("'*' indicates the value for non-slayers.")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            stack.set(DataComponents.LORE,lore);
            return stack;
        }
    },
    TRIDENT{
        @Override
        public ItemStack getAsStack(@Nullable Random random) {
            ItemStack stack = new ItemStack(Items.TRIDENT,1);
            CustomDatas.setCustomItemType(stack,this);
            CustomDatas.setSoulBound(stack,3);
            stack.set(DataComponents.ITEM_NAME, Component.literal("Piercing Trident").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
            ItemLore lore = ItemLore.EMPTY;
            lore = CustomDatas.addSoulBoundLore(lore);
            lore = lore.withLineAdded(Component.literal("Only craftable by Rangers")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withBold(true)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("A loyal trident. Has 15% (7.5%) armor piercing damage only when thrown.")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("'*' indicates the value for non-rangers.")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            stack.set(DataComponents.LORE,lore);
            return stack;
        }
    },
    LONGBOW{
        @Override
        public ItemStack getAsStack(@Nullable Random random) {
            ItemStack stack = new ItemStack(Items.BOW,1);
            CustomDatas.setCustomItemType(stack,this);
            CustomDatas.setSoulBound(stack,3);
            stack.set(DataComponents.ITEM_NAME, Component.literal("Longbow").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
            ItemLore lore = ItemLore.EMPTY;
            lore = CustomDatas.addSoulBoundLore(lore);
            lore = lore.withLineAdded(Component.literal("Only craftable by Rangers")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withBold(true)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("Deals slightly more damamge than a normal bow with 1.5 (*1.25) times velocity.")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            lore = lore.withLineAdded(Component.literal("'*' indicates the value for non-rangers.")
                    .withStyle(Style.EMPTY.withItalic(false)
                            .withColor(ChatFormatting.YELLOW)));
            stack.set(DataComponents.LORE,lore);
            return stack;
        }
    },
    CROSSBOW{
        @Override
        public ItemStack getAsStack(@Nullable Random random) {
            return ItemStack.EMPTY;
        }
    },
    LESSER_ATTUNEMENT_GEM{
        @Override
        public ItemStack getAsStack(@Nullable Random random) {
            ItemStack stack = new ItemStack(Items.AMETHYST_SHARD,1);
            CustomDatas.setCustomItemType(stack,this);
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,true);
            stack.set(DataComponents.ITEM_NAME, Component.literal("Lesser Attunement Gem").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD));
            ItemLore lore = ItemLore.EMPTY;
            lore = lore.withLineAdded(Component.literal("Click to change your passive abilities.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW).withBold(true)));
            stack.set(DataComponents.LORE,lore);
            return stack;
        }

        @Override
        public void onClick(ServerPlayer player, InteractionHand interactionHand) {
            SkillSelection.open(player,true);
        }
    },
    GREATER_ATTUNEMENT_GEM{
        @Override
        public ItemStack getAsStack(@Nullable Random random) {
            ItemStack stack = new ItemStack(Items.EMERALD,1);
            CustomDatas.setCustomItemType(stack,this);
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,true);
            stack.set(DataComponents.ITEM_NAME, Component.literal("Greater Attunement Gem").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD));
            ItemLore lore = ItemLore.EMPTY;
            lore = lore.withLineAdded(Component.literal("Click to change your class AND passive abilities.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW).withBold(true)));
            stack.set(DataComponents.LORE,lore);
            return stack;
        }
        @Override
        public void onClick(ServerPlayer player, InteractionHand interactionHand) {
            ClassSelection.open(player,true);
        }
    },
    MAC_GUFFEN_CUBE{
        @Override
        public ItemStack getAsStack(@Nullable Random random) {
            ItemStack stack = new ItemStack(Items.LIGHT_BLUE_STAINED_GLASS,1);
            CustomDatas.setCustomItemType(stack,this);
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,true);
            stack.set(DataComponents.ITEM_NAME, Component.literal("Mac-Guffen Cube").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.OBFUSCATED));
            ItemLore lore = ItemLore.EMPTY;
            lore = lore.withLineAdded(Component.literal("Click to permanently gain an additional skill slot.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GREEN).withBold(true)));
            lore = lore.withLineAdded(Component.literal("Blessed by the mines, for which you now yearn for.").withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GREEN).withBold(true)));
            stack.set(DataComponents.LORE,lore);
            return stack;
        }

        @Override
        public void onClick(ServerPlayer player, InteractionHand interactionHand) {
            int ms = CTFPlayerData.get(player).getMaxSkills()+1;
            CTFPlayerData.get(player).setMaxSkills(ms);
            player.sendSystemMessage(Component.literal("Increased max skills to: " + ms).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN));
            ItemStack stack = player.getItemInHand(interactionHand);
            stack.shrink(1);
            player.setItemInHand(interactionHand,stack);
            SkillSelection.open(player,false);
        }
    },
    PARKOUR_BUNDLE{
        @Override
        public ItemStack getAsStack(Random random) {
            return ItemStack.EMPTY;
        }
    };

    private static final List<CustomItem> options = List.of(BLASTPICKAXE,BRICKLAYER,BIGBUCKET,MACE,REFLECTORSHIELD,TRIDENT,LONGBOW);

    public static ItemStack randomClassSpecific(Random random) {
        return options.get(random.nextInt(options.size())).getAsStack(random);
    }

    public ItemStack getAsStack(){
        return getAsStack(null);
    }

    public abstract ItemStack getAsStack(@Nullable Random random);

    public boolean is(DataComponentHolder holder){
        return this != NONE && CustomDatas.getCustomItem(holder) == this;
    }

    public void set(DataComponentHolder holder) {
        CustomDatas.setCustomItemType(holder,this);
    }

    public ResourceLocation recipeName(){
        return ResourceLocation.fromNamespaceAndPath("capturetheflag",name().toLowerCase());
    }

    public int maxifyEnchant(Holder<Enchantment> retriever, int n){
        return n;
    }

    public void onClick(ServerPlayer player, InteractionHand interactionHand) {

    }
}
