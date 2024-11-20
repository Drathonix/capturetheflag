package com.drathonix.capturetheflag.common;

import com.drathonix.capturetheflag.common.system.SpecialAbility;
import com.drathonix.capturetheflag.common.util.ToolTier;
import com.drathonix.capturetheflag.common.util.ToolType;
import com.drathonix.capturetheflag.common.util.regis.EnchantmentRetriever;
import com.drathonix.capturetheflag.common.util.regis.MobEffectRetriever;
import com.vicious.persist.annotations.C_NAME;
import com.vicious.persist.annotations.Save;
import com.vicious.persist.annotations.Typing;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.*;

@C_NAME("ctf_class_type")
@SuppressWarnings("all")
public enum ClassType {
    NONE,
    MINER{
        {
            abilities.add(SpecialAbility.SMELTLER);
            passiveEffects.put(retriever(MobEffects.DIG_SPEED),1);
            territorialEffects.put(retriever(MobEffects.DIG_SPEED),2);
            toolTiers.put(ToolType.PICKAXE,ToolTier.IRON);
            enchantments.put(location(Enchantments.FORTUNE), 3);
            enchantments.put(location(Enchantments.EFFICIENCY), 5);
            enchantments.put(location(Enchantments.KNOCKBACK), 2);
            enchantments.put(location(Enchantments.PUNCH), 1);
            enchantments.put(location(Enchantments.DEPTH_STRIDER), 3);

            recipes.add(ResourceLocation.fromNamespaceAndPath("minecraft","blast_furnace"));
            recipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"blastpickaxe"));

            mobDamageTakenMultiplier=0.8F;
            playerDamageTakenMultiplier=0.95F;
            mobTargetChance=0.8F;
        }
    },
    BUILDER{
        {
            abilities.add(SpecialAbility.COBBLER);
            territorialEffects.put(retriever(MobEffects.REGENERATION),1);
            toolTiers.put(ToolType.SHOVEL,ToolTier.IRON);
            enchantments.put(location(Enchantments.INFINITY), 1);
            enchantments.put(location(Enchantments.KNOCKBACK), 2);
            enchantments.put(location(Enchantments.PUNCH), 1);
            enchantments.put(location(Enchantments.DEPTH_STRIDER), 3);
            enchantments.put(location(Enchantments.SWIFT_SNEAK), 3);

            recipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"bricklayer"));
            recipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"bigbucket"));
            recipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"bigbucketempty"));


            mobDamageTakenMultiplier=0.6F;
            playerDamageTakenMultiplier=0.95F;
            mobTargetChance=0.7F;
            arrowDamageTakenMultiplier=0.1F;
        }
    },
    WARRIOR{
        {
            abilities.add(SpecialAbility.BLOODLUST);
            passiveEffects.put(retriever(MobEffects.HEALTH_BOOST),2);
            toolTiers.put(ToolType.SWORD,ToolTier.IRON);
            enchantments.put(location(Enchantments.SHARPNESS),3);
            enchantments.put(location(Enchantments.THORNS),1);
            enchantments.put(location(Enchantments.FIRE_ASPECT),2);
            enchantments.put(location(Enchantments.WIND_BURST),1);
            enchantments.put(location(Enchantments.DENSITY),2);
            enchantments.put(location(Enchantments.BREACH),1);
            mobDamageTakenMultiplier=1.1F;

            recipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"reflectorshield"));
            recipes.add(ResourceLocation.fromNamespaceAndPath("minecraft","mace"));
        }
    },
    RANGER {
        {
            abilities.add(SpecialAbility.SCROUNGER);
            enchantments.put(location(Enchantments.FLAME),1);
            enchantments.put(location(Enchantments.CHANNELING), 1);
            enchantments.put(location(Enchantments.QUICK_CHARGE), 2);
            enchantments.put(location(Enchantments.MULTISHOT), 1);
            enchantments.put(location(Enchantments.PUNCH), 2);
            enchantments.put(location(Enchantments.PIERCING), 4);
            enchantments.put(location(Enchantments.POWER), 4);
            bowRespawn = true;
            arrowRespawnAmount = 16;
            arrowDamageMultiplier=1.2F;

            recipes.add(ResourceLocation.fromNamespaceAndPath("minecraft","crossbow"));
            recipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"trident"));
            recipes.add(ResourceLocation.fromNamespaceAndPath(CTF.modid,"longbow"));

        }
    },
    RAISER {
        {
            enabled = false;
        }
    };

    private static MobEffectRetriever retriever(Holder<MobEffect> effect) {
        return new MobEffectRetriever(effect.unwrapKey().get().location());
    }

    @Save
    public boolean enabled = true;

    @Typing({MobEffectRetriever.class,Integer.class})
    @Save(description = "Effects constantly granted at all times")
    public Map<MobEffectRetriever,Integer> passiveEffects = new HashMap<>();

    @Typing({MobEffectRetriever.class,Integer.class})
    @Save(description = "Effects constantly granted while in own territory")
    public Map<MobEffectRetriever,Integer> territorialEffects = new HashMap<>();

    @Typing({EnchantmentRetriever.class,Integer.class})
    @Save(description = "Enchantments and their max power levels this class has access to.")
    public Map<EnchantmentRetriever, Integer> enchantments = new HashMap<>();

    @Typing({ToolType.class,ToolTier.class})
    @Save(description = "Tools to respawn with")
    public EnumMap<ToolType, ToolTier> toolTiers = new EnumMap<>(ToolType.class);

    @Save(description = "The special passive abilities this class has.")
    @Typing(SpecialAbility.class)
    public Set<SpecialAbility> abilities = new HashSet<>();

    @Save(description = "Special recipes only this class has access to.")
    @Typing(ResourceLocation.class)
    public Set<ResourceLocation> recipes = new HashSet<>();

    @Save(description = "Determines if the class respawns with a bow.")
    public boolean bowRespawn = false;

    @Save(description = "Number of arrows to respawn with")
    public int arrowRespawnAmount = 0;

    @Save(description = "Amount of steak to respawn with")
    public int steakRespawnAmount = 12;

    @Save(description = "Multiplies the resulting arrow dammage dealt.")
    public float arrowDamageMultiplier=1F;

    @Save(description = "Multiplies the damage taken from mobs")
    public float mobDamageTakenMultiplier = 1F;

    @Save(description = "Multiplies the damage taken from players")
    public float playerDamageTakenMultiplier = 1F;

    @Save(description = "Multiplies the damage taken from arrows")
    public float arrowDamageTakenMultiplier = 1F;

    @Save(description = "Changes the likelihood of a player being detected by an entity.")
    public float mobTargetChance = 1F;
    ClassType(){
        toolTiers.put(ToolType.PICKAXE, ToolTier.STONE);
        toolTiers.put(ToolType.SWORD, ToolTier.STONE);
        toolTiers.put(ToolType.AXE, ToolTier.STONE);
        enchantments.put(location(Enchantments.PROTECTION), 2);
        enchantments.put(location(Enchantments.UNBREAKING), 3);
        enchantments.put(location(Enchantments.PROJECTILE_PROTECTION), 3);
        enchantments.put(location(Enchantments.RESPIRATION), 3);
        enchantments.put(location(Enchantments.FEATHER_FALLING), 4);
        enchantments.put(location(Enchantments.DEPTH_STRIDER), 1);
        enchantments.put(location(Enchantments.EFFICIENCY), 2);
        enchantments.put(location(Enchantments.SHARPNESS), 2);
        enchantments.put(location(Enchantments.FORTUNE), 1);
        enchantments.put(location(Enchantments.PIERCING), 2);
        enchantments.put(location(Enchantments.LOYALTY), 3);
        enchantments.put(location(Enchantments.POWER), 2);
        enchantments.put(location(Enchantments.SWIFT_SNEAK),1);
    }

    public void onRespawn(ServerPlayer sp){
        for (ResourceLocation recipe : recipes) {
            sp.getRecipeBook().add(ResourceKey.create(Registries.RECIPE,recipe));
        }
    }

    private static EnchantmentRetriever location(ResourceKey<Enchantment> key){
        return new EnchantmentRetriever(key.location());
    }

    public String wikiURL() {
        return switch (this){
            case MINER -> {
                yield "https://github.com/Drathonix/capturetheflag/wiki/breaker";
            }
            case BUILDER -> {
                yield "https://github.com/Drathonix/capturetheflag/wiki/architect";
            }
            case WARRIOR -> {
                yield "https://github.com/Drathonix/capturetheflag/wiki/slayer";
            }
            case RANGER -> {
                yield "https://github.com/Drathonix/capturetheflag/wiki/ranger";
            }
            default -> {
                yield "https://github.com/Drathonix/capturetheflag/wiki/gameplay";
            }
        };
    }
}
