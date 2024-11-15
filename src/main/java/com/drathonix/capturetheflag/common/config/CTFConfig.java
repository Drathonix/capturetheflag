package com.drathonix.capturetheflag.common.config;

import com.drathonix.capturetheflag.common.CTF;
import com.drathonix.capturetheflag.common.util.regis.EnchantmentRetriever;
import com.drathonix.capturetheflag.common.util.regis.ItemRetriever;
import com.vicious.persist.annotations.PersistentPath;
import com.vicious.persist.annotations.Save;
import com.vicious.persist.annotations.Typing;
import com.vicious.persist.shortcuts.PersistShortcuts;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CTFConfig {
    @PersistentPath
    public static String path = "config/capturetheflag/main.txt";

    @Save(description = "Icons for enchantments in the holy enchanter")
    @Typing({EnchantmentRetriever.class, ItemRetriever.class})
    public static Map<EnchantmentRetriever,ItemRetriever> enchantmentToItem = new HashMap<>();
    
    @Save(description = "The base level cost for each enchantment.")
    @Typing({EnchantmentRetriever.class, Integer.class})
    public static Map<EnchantmentRetriever,Integer> enchantmentToLevelBase = new HashMap<>();

    @Typing(ResourceLocation.class)
    @Save(description = "These recipes cannot be crafted by crafters and players without permissions.")
    public static Set<ResourceLocation> protectedRecipes = new HashSet<>();

    @Save(description = "Time in seconds during which a flag is immune to being stolen after it has either been dropped (on death) or captured")
    public static long stealFlagCooldownTime = 30;

    @Save(description = "Number of flags to win.")
    public static int winCondition = 3;

    public static void init() {
        {
            protectedRecipes.add(ResourceLocation.fromNamespaceAndPath("minecraft","blast_furnace"));
            protectedRecipes.add(ResourceLocation.fromNamespaceAndPath("minecraft","crossbow"));
            protectedRecipes.add(ResourceLocation.fromNamespaceAndPath("minecraft","mace"));
            protectedRecipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"bigbucket"));
            protectedRecipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"bigbucketempty"));
            protectedRecipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"blastpickaxe"));
            protectedRecipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"bricklayer"));
            protectedRecipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"longbow"));
            protectedRecipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"reflectorshield"));
            protectedRecipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"trident"));
        }
        {
            enchantmentToLevelBase(location(Enchantments.PROTECTION), 4);
            enchantmentToLevelBase(location(Enchantments.UNBREAKING), 2);
            enchantmentToLevelBase(location(Enchantments.PROJECTILE_PROTECTION), 3);
            enchantmentToLevelBase(location(Enchantments.SWIFT_SNEAK), 3);
            enchantmentToLevelBase(location(Enchantments.RESPIRATION), 3);
            enchantmentToLevelBase(location(Enchantments.FEATHER_FALLING), 4);
            enchantmentToLevelBase(location(Enchantments.DEPTH_STRIDER), 4);
            enchantmentToLevelBase(location(Enchantments.EFFICIENCY), 4);
            enchantmentToLevelBase(location(Enchantments.FORTUNE), 8);
            enchantmentToLevelBase(location(Enchantments.SHARPNESS), 6);
            enchantmentToLevelBase(location(Enchantments.SWEEPING_EDGE), 5);
            enchantmentToLevelBase(location(Enchantments.FIRE_ASPECT), 8);
            enchantmentToLevelBase(location(Enchantments.FLAME), 16);
            enchantmentToLevelBase(location(Enchantments.POWER), 6);
            enchantmentToLevelBase(location(Enchantments.INFINITY), 12);
            enchantmentToLevelBase(location(Enchantments.PIERCING), 6);
            enchantmentToLevelBase(location(Enchantments.MULTISHOT), 12);
            enchantmentToLevelBase(location(Enchantments.QUICK_CHARGE), 8);
            enchantmentToLevelBase(location(Enchantments.CHANNELING), 12);
            enchantmentToLevelBase(location(Enchantments.PUNCH), 6);
            enchantmentToLevelBase(location(Enchantments.KNOCKBACK), 6);
            //Loyalty is given in crafting, but is still enchantable.
            enchantmentToLevelBase(location(Enchantments.LOYALTY), 1);
            enchantmentToLevelBase(location(Enchantments.THORNS), 12);
            enchantmentToLevelBase(location(Enchantments.WIND_BURST), 12);
            enchantmentToLevelBase(location(Enchantments.DENSITY), 12);
            enchantmentToLevelBase(location(Enchantments.BREACH), 6);
        }
        {
            enchantmentToItem(location(Enchantments.PROTECTION), Items.IRON_CHESTPLATE);
            enchantmentToItem(location(Enchantments.UNBREAKING), Items.OBSIDIAN);
            enchantmentToItem(location(Enchantments.PROJECTILE_PROTECTION), Items.ARROW);
            enchantmentToItem(location(Enchantments.SWIFT_SNEAK), Items.SCULK);
            enchantmentToItem(location(Enchantments.RESPIRATION), Items.GLASS_BOTTLE);
            enchantmentToItem(location(Enchantments.FEATHER_FALLING), Items.FEATHER);
            enchantmentToItem(location(Enchantments.DEPTH_STRIDER), Items.FIREWORK_ROCKET);
            enchantmentToItem(location(Enchantments.EFFICIENCY), Items.GOLDEN_PICKAXE);
            enchantmentToItem(location(Enchantments.FORTUNE), Items.EMERALD);
            enchantmentToItem(location(Enchantments.SHARPNESS), Items.IRON_SWORD);
            enchantmentToItem(location(Enchantments.SWEEPING_EDGE), Items.GOLDEN_SWORD);
            enchantmentToItem(location(Enchantments.FIRE_ASPECT), Items.FLINT_AND_STEEL);
            enchantmentToItem(location(Enchantments.FLAME), Items.FLINT_AND_STEEL);
            enchantmentToItem(location(Enchantments.POWER), Items.SPECTRAL_ARROW);
            enchantmentToItem(location(Enchantments.INFINITY), Items.END_PORTAL_FRAME);
            enchantmentToItem(location(Enchantments.PIERCING), Items.SPECTRAL_ARROW);
            enchantmentToItem(location(Enchantments.MULTISHOT), Items.SLIME_BALL);
            enchantmentToItem(location(Enchantments.QUICK_CHARGE), Items.STRING);
            enchantmentToItem(location(Enchantments.CHANNELING), Items.LIGHTNING_ROD);
            enchantmentToItem(location(Enchantments.PUNCH), Items.PISTON);
            enchantmentToItem(location(Enchantments.KNOCKBACK), Items.PISTON);
            enchantmentToItem(location(Enchantments.LOYALTY), Items.BONE);
            enchantmentToItem(location(Enchantments.THORNS), Items.SWEET_BERRIES);
            enchantmentToItem(location(Enchantments.WIND_BURST), Items.WIND_CHARGE);
            enchantmentToItem(location(Enchantments.DENSITY), Items.HEAVY_CORE);
            enchantmentToItem(location(Enchantments.BREACH), Items.TNT);
        }
        PersistShortcuts.init(CTFConfig.class);
    }

    private static void enchantmentToItem(ResourceLocation location, Item item) {
        enchantmentToItem.put(new EnchantmentRetriever(location), new ItemRetriever(BuiltInRegistries.ITEM.getKey(item)));
    }

    private static void enchantmentToLevelBase(ResourceLocation location, int n) {
        enchantmentToLevelBase.put(new EnchantmentRetriever(location), n);
    }

    private static ResourceLocation location(ResourceKey<Enchantment> key){
        return key.location();
    }
}
