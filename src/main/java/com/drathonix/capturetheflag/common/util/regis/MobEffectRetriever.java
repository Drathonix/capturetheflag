package com.drathonix.capturetheflag.common.util.regis;

import com.drathonix.capturetheflag.common.CTF;
import com.vicious.persist.mappify.registry.Stringify;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class MobEffectRetriever extends RegistryRetriever<Holder<MobEffect>> {
    static {
        Stringify.register(MobEffectRetriever.class, MobEffectRetriever::new, MobEffectRetriever::serializable);
    }

    public MobEffectRetriever(String key) {
        super(key);
    }

    public MobEffectRetriever(ResourceLocation key) {
        super(key);
    }

    @Override
    Holder<MobEffect> retrieve() {
        return BuiltInRegistries.MOB_EFFECT.get(location).get();
    }
}
