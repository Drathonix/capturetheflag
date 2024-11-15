package com.drathonix.capturetheflag.common.util;

import com.drathonix.capturetheflag.common.component.CustomDatas;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum ToolType {
    SHOVEL{
        @Override
        public ItemStack getItem(ToolTier tier) {
            ItemStack stack;
            switch (tier) {
                case WOOD -> {
                    stack = new ItemStack(Items.WOODEN_SHOVEL);
                }
                case STONE -> {
                    stack = new ItemStack(Items.STONE_SHOVEL);
                }
                case IRON -> {
                    stack = new ItemStack(Items.IRON_SHOVEL);
                }
                case GOLD -> {
                    stack = new ItemStack(Items.GOLDEN_SHOVEL);
                }
                case DIAMOND -> {
                    stack = new ItemStack(Items.DIAMOND_SHOVEL);
                }
                case NETHERITE -> {
                    stack = new ItemStack(Items.NETHERITE_SHOVEL);
                }
                default -> {
                    return ItemStack.EMPTY;
                }
            }
            CustomDatas.setVanish(stack,true);
            return stack;
        }
    },
    AXE{
        @Override
        public ItemStack getItem(ToolTier tier) {
            ItemStack stack;
            switch (tier) {
                case WOOD -> {
                    stack = new ItemStack(Items.WOODEN_AXE);
                }
                case STONE -> {
                    stack = new ItemStack(Items.STONE_AXE);
                }
                case IRON -> {
                    stack = new ItemStack(Items.IRON_AXE);
                }
                case GOLD -> {
                    stack = new ItemStack(Items.GOLDEN_AXE);
                }
                case DIAMOND -> {
                    stack = new ItemStack(Items.DIAMOND_AXE);
                }
                case NETHERITE -> {
                    stack = new ItemStack(Items.NETHERITE_AXE);
                }
                default -> {
                    return ItemStack.EMPTY;
                }
            }
            CustomDatas.setVanish(stack,true);
            return stack;
        }
    },
    PICKAXE{
        @Override
        public ItemStack getItem(ToolTier tier) {
            ItemStack stack;
            switch (tier) {
                case WOOD -> {
                    stack = new ItemStack(Items.WOODEN_PICKAXE);
                }
                case STONE -> {
                    stack = new ItemStack(Items.STONE_PICKAXE);
                }
                case IRON -> {
                    stack = new ItemStack(Items.IRON_PICKAXE);
                }
                case GOLD -> {
                    stack = new ItemStack(Items.GOLDEN_PICKAXE);
                }
                case DIAMOND -> {
                    stack = new ItemStack(Items.DIAMOND_PICKAXE);
                }
                case NETHERITE -> {
                    stack = new ItemStack(Items.NETHERITE_PICKAXE);
                }
                default -> {
                    return ItemStack.EMPTY;
                }
            }
            CustomDatas.setVanish(stack,true);
            return stack;
        }
    },
    HOE{
        @Override
        public ItemStack getItem(ToolTier tier) {
            ItemStack stack;
            switch (tier) {
                case WOOD -> {
                    stack = new ItemStack(Items.WOODEN_HOE);
                }
                case STONE -> {
                    stack = new ItemStack(Items.STONE_HOE);
                }
                case IRON -> {
                    stack = new ItemStack(Items.IRON_HOE);
                }
                case GOLD -> {
                    stack = new ItemStack(Items.GOLDEN_HOE);
                }
                case DIAMOND -> {
                    stack = new ItemStack(Items.DIAMOND_HOE);
                }
                case NETHERITE -> {
                    stack = new ItemStack(Items.NETHERITE_HOE);
                }
                default -> {
                    return ItemStack.EMPTY;
                }
            }
            CustomDatas.setVanish(stack,true);
            return stack;
        }
    },
    SWORD{
        @Override
        public ItemStack getItem(ToolTier tier) {
            ItemStack stack;
            switch (tier) {
                case WOOD -> {
                    stack = new ItemStack(Items.WOODEN_SWORD);
                }
                case STONE -> {
                    stack = new ItemStack(Items.STONE_SWORD);
                }
                case IRON -> {
                    stack = new ItemStack(Items.IRON_SWORD);
                }
                case GOLD -> {
                    stack = new ItemStack(Items.GOLDEN_SWORD);
                }
                case DIAMOND -> {
                    stack = new ItemStack(Items.DIAMOND_SWORD);
                }
                case NETHERITE -> {
                    stack = new ItemStack(Items.NETHERITE_SWORD);
                }
                default -> {
                    return ItemStack.EMPTY;
                }
            }
            CustomDatas.setVanish(stack,true);
            return stack;
        }
    };

    public abstract ItemStack getItem(ToolTier tier);
}
