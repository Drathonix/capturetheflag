package com.drathonix.capturetheflag.common.gui.base;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;

import java.util.function.Consumer;

public class TickingGUISlot extends GUISlot{
    private Consumer<ServerPlayer> tickAction = (sp)->{};

    public TickingGUISlot(Container container, int slot) {
        super(container, slot);
    }

    public void onTick(Consumer<ServerPlayer> tickAction){
        this.tickAction = tickAction;
    }

    public synchronized void handleTick(ServerPlayer player){
        tickAction.accept(player);
    }
}
