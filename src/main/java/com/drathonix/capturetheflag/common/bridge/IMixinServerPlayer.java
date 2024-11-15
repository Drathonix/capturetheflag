package com.drathonix.capturetheflag.common.bridge;

import com.drathonix.capturetheflag.common.injected.CTFPlayerData;

public interface IMixinServerPlayer {
    CTFPlayerData ctf$getData();
}
