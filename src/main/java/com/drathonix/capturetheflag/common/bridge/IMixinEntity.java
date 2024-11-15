package com.drathonix.capturetheflag.common.bridge;

public interface IMixinEntity {
    default boolean ctf$isSpecial(){
        return false;
    }
}
