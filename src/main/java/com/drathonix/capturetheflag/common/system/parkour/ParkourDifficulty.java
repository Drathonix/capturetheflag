package com.drathonix.capturetheflag.common.system.parkour;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.component.CustomDatas;
import com.drathonix.capturetheflag.common.system.CustomItem;
import com.drathonix.finallib.common.util.weighted.IWeighted;
import com.vicious.persist.annotations.Save;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum ParkourDifficulty implements IWeighted {
    EASY(5,20,1, Items.PINK_BUNDLE){
        @Override
        public List<ItemStack> generateRewards() {
            List<ItemStack> out = new ArrayList<>();
            out.add(new ItemStack(Items.COPPER_INGOT,random.nextInt(8,32)));
            out.add(new ItemStack(Items.COPPER_BLOCK,random.nextInt(0,3)));
            out.add(new ItemStack(Items.IRON_INGOT,random.nextInt(2,16)));
            out.add(new ItemStack(Items.LAPIS_LAZULI,random.nextInt(1,8)));
            out.add(new ItemStack(Items.DIAMOND,random.nextInt(0,1)));
            return out;
        }
    },
    MEDIUM(6,44,2, Items.LIGHT_BLUE_BUNDLE){
        @Override
        public List<ItemStack> generateRewards() {
            List<ItemStack> out = new ArrayList<>();
            out.add(new ItemStack(Items.COPPER_BLOCK,random.nextInt(1,4)));
            out.add(new ItemStack(Items.IRON_INGOT,random.nextInt(4,24)));
            out.add(new ItemStack(Items.LAPIS_LAZULI,random.nextInt(2,20)));
            out.add(new ItemStack(Items.DIAMOND,random.nextInt(1,4)));
            if(random.nextFloat() < 0.2F) {
                out.add(CustomItem.randomClassSpecific(random));
            }
            return out;
        }
    },
    HARD(7,30,3, Items.ORANGE_BUNDLE){
        @Override
        public List<ItemStack> generateRewards() {
            List<ItemStack> out = new ArrayList<>();
            out.add(new ItemStack(Items.IRON_INGOT,random.nextInt(8,36)));
            out.add(new ItemStack(Items.LAPIS_LAZULI,random.nextInt(6,28)));
            out.add(new ItemStack(Items.DIAMOND,random.nextInt(2,8)));
            out.add(new ItemStack(Items.EXPERIENCE_BOTTLE,random.nextInt(1,4)));
            out.add(CustomItem.randomClassSpecific(random));
            if(random.nextFloat() < 0.2F) {
                out.add(CustomItem.LESSER_ATTUNEMENT_GEM.getAsStack(random));
            }
            return out;
        }
    },
    BRUTAL(4,5,5,Items.RED_BUNDLE){
        @Override
        public List<ItemStack> generateRewards() {
            List<ItemStack> out = new ArrayList<>();
            out.add(new ItemStack(Items.IRON_INGOT,random.nextInt(16,48)));
            out.add(new ItemStack(Items.LAPIS_LAZULI,random.nextInt(20,40)));
            out.add(new ItemStack(Items.DIAMOND,random.nextInt(4,16)));
            out.add(new ItemStack(Items.EXPERIENCE_BOTTLE,random.nextInt(4,12)));
            out.add(CustomItem.randomClassSpecific(random));
            if(random.nextFloat() < 0.2F) {
                out.add(CustomItem.randomClassSpecific(random));
            }
            if(random.nextFloat() < 0.4F) {
                out.add(CustomItem.LESSER_ATTUNEMENT_GEM.getAsStack(random));
            }
            if(random.nextFloat() < 0.2F) {
                out.add(CustomItem.GREATER_ATTUNEMENT_GEM.getAsStack(random));
            }
            return out;
        }
    },
    CHAMPION(1,1,10,Items.BLACK_BUNDLE){
        @Override
        public List<ItemStack> generateRewards() {
            List<ItemStack> out = new ArrayList<>();
            out.add(new ItemStack(Items.IRON_INGOT,random.nextInt(32,64)));
            out.add(new ItemStack(Items.LAPIS_LAZULI,random.nextInt(32,64)));
            out.add(new ItemStack(Items.DIAMOND,random.nextInt(8,20)));
            out.add(new ItemStack(Items.EXPERIENCE_BOTTLE,random.nextInt(8,14)));
            out.add(CustomItem.randomClassSpecific(random));
            out.add(CustomItem.randomClassSpecific(random));
            out.add(CustomItem.LESSER_ATTUNEMENT_GEM.getAsStack(random));
            for (int i = 0; i < 2; i++) {
                if(random.nextFloat() < i*0.2F) {
                    out.add(CustomItem.randomClassSpecific(random));
                }
            }
            if(random.nextFloat() < 0.5F) {
                out.add(CustomItem.LESSER_ATTUNEMENT_GEM.getAsStack(random));
            }
            if(random.nextFloat() < 0.4F) {
                out.add(CustomItem.GREATER_ATTUNEMENT_GEM.getAsStack(random));
            }
            return out;
        }
    };

    private static final Random random = new Random();

    @Save
    public int weight;
    @Save
    public int attemptsPerMinute;
    public final Item bundleType;
    public final int count;
    private final List<StructureTemplate> templates = new ArrayList<>();

    ParkourDifficulty(int count, int weight, int attemptsPerMinute, Item bundleType) {
        this.count = count;
        this.weight=weight;
        this.attemptsPerMinute = attemptsPerMinute;
        this.bundleType = bundleType;
        for (int i = 1; i <= count; i++) {
            templates.add(CTF.server.getStructureManager().get(ResourceLocation.fromNamespaceAndPath(CTF.modid,"parkour/"+ saveName() + i)).get());
        }
    }

    public String saveName(){
        return this.name().toLowerCase();
    }

    public List<StructureTemplate> copyOfTemplates(){
        return new ArrayList<>(templates);
    }

    public ItemStack generateRewardBundle(){
        ItemStack rewardBundle = new ItemStack(bundleType);
        rewardBundle.set(DataComponents.ITEM_NAME, Component.literal("Parkour Bundle").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
        List<ItemStack> c = generateRewards();
        for (int i = 0; i < c.size(); i++) {
            if(c.get(i).isEmpty()){
                c.remove(i);
                i--;
            }
        }
        if(random.nextFloat() < 0.003F*(ordinal()+1)){
            c.add(CustomItem.MAC_GUFFEN_CUBE.getAsStack(random));
        }
        BundleContents contents = new BundleContents(c);
        rewardBundle.set(DataComponents.BUNDLE_CONTENTS,contents);
        CustomDatas.setCustomItemType(rewardBundle,CustomItem.PARKOUR_BUNDLE);
        CustomDatas.setLocked(rewardBundle,true);
        return rewardBundle;
    }

    public abstract List<ItemStack> generateRewards();

    @Override
    public int getWeight() {
        return weight;
    }
}
