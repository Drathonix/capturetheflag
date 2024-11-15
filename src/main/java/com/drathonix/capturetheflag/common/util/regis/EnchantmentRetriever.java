package com.drathonix.capturetheflag.common.util.regis;

import com.drathonix.capturetheflag.common.CTF;
import com.vicious.persist.mappify.registry.Stringify;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentRetriever extends RegistryRetriever<Holder<Enchantment>> {
    static {
        Stringify.register(EnchantmentRetriever.class, EnchantmentRetriever::new, EnchantmentRetriever::serializable);
    }

    public EnchantmentRetriever(String key) {
        super(key);
    }

    public EnchantmentRetriever(ResourceLocation key) {
        super(key);
    }

    public EnchantmentRetriever(Holder<Enchantment> holder) {
        super(holder.unwrapKey().get().location());
    }

    @Override
    Holder<Enchantment> retrieve() {
        return CTF.server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(location).get();
    }
}
