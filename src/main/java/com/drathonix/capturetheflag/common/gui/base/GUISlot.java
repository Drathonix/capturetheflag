package com.drathonix.capturetheflag.common.gui.base;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class GUISlot extends Slot {
    private Predicate<ServerPlayer> onClick = (sp)->true;

    public GUISlot(Container container, int slot) {
        super(container, slot, 0,0);
    }

    public void onClick(Predicate<ServerPlayer> function){
        this.onClick = function;
    }

    public boolean handleClick(@NotNull ServerPlayer player){
        return onClick.test(player);
    }


}
