package com.drathonix.capturetheflag.unified;
import com.drathonix.capturetheflag.common.CTF;

//? if fabric {
import net.fabricmc.api.ModInitializer;

public class ENTRY implements ModInitializer {
        @Override
        public void onInitialize() {
                CTF.init();
        }
}
//?}
