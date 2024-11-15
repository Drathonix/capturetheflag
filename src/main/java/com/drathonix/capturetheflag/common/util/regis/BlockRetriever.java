package com.drathonix.capturetheflag.common.util.regis;

import com.drathonix.capturetheflag.common.CTF;
import com.vicious.persist.mappify.registry.Stringify;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

public class BlockRetriever extends RegistryRetriever<Holder<Block>> {
    static {
        Stringify.register(BlockRetriever.class, BlockRetriever::new, BlockRetriever::serializable);
    }

    public BlockRetriever(String key) {
        super(key);
    }

    public BlockRetriever(ResourceLocation key) {
        super(key);
    }

    public BlockRetriever(Holder<Block> holder) {
        super(holder.unwrapKey().get().location());
    }

    public BlockRetriever(Block block) {
        super(BuiltInRegistries.BLOCK.getKey(block));
    }

    @Override
    Holder<Block> retrieve() {
        return CTF.server.registryAccess().lookupOrThrow(Registries.BLOCK).get(location).get();
    }
}
