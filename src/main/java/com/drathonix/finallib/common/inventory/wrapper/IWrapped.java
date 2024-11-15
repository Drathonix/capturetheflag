package com.drathonix.finallib.common.inventory.wrapper;

public interface IWrapped<T> {
    T getWrapped();

    default boolean wraps(Object target){
        Object wrapped = getWrapped();
        if(wrapped == target){
            return true;
        }
        if(wrapped instanceof IWrapped<?> internal){
            return internal.wraps(target);
        }
        return false;
    }
}
