package com.drathonix.capturetheflag.common.util.regis;

import com.drathonix.capturetheflag.common.CTF;
import com.vicious.persist.mappify.registry.Stringify;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public class ItemRetriever extends RegistryRetriever<Holder<Item>> {
    static {
        Stringify.register(ItemRetriever.class, ItemRetriever::new, ItemRetriever::serializable);
    }

    public ItemRetriever(String key) {
        super(key);
    }

    public ItemRetriever(ResourceLocation key) {
        super(key);
    }

    @Override
    Holder<Item> retrieve() {
        return BuiltInRegistries.ITEM.get(location).get();
    }
}
