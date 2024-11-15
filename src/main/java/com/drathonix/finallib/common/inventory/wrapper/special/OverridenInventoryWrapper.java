package com.drathonix.finallib.common.inventory.wrapper.special;

import com.drathonix.finallib.common.inventory.wrapper.InventoryWrapper;
import com.drathonix.finallib.common.inventory.wrapper.SuperWrapper;

public class OverridenInventoryWrapper extends SuperWrapper {
    private final int id;

    public OverridenInventoryWrapper(InventoryWrapper wrapper, int id){
        super(wrapper);
        this.id = id;
    }

    @Override
    public int getInventoryNumber() {
        return id;
    }
}
