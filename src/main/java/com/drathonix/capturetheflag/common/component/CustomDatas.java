package com.drathonix.capturetheflag.common.component;

import com.drathonix.capturetheflag.common.ClassType;
import com.drathonix.capturetheflag.common.system.CustomItem;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CustomDatas {
    public static void whenPresent(@NotNull DataComponentHolder holder, @NotNull Consumer<CompoundTag> consumer){
        CustomData data = holder.get(DataComponents.CUSTOM_DATA);
        if(data != null) {
            data = data.update(consumer);
        }
        if(holder instanceof ItemStack i){
            i.set(DataComponents.CUSTOM_DATA,data);
        }
    }

    public static <T> @Nullable T get(@NotNull DataComponentHolder holder, @NotNull Function<CompoundTag,T> function){
        CustomData data = holder.get(DataComponents.CUSTOM_DATA);
        if(data != null) {
            return function.apply(data.copyTag());
        }
        return null;
    }

    public static <T> @NotNull T getDefaulted(@NotNull DataComponentHolder holder, @NotNull Function<CompoundTag,T> function, @NotNull Supplier<T> defaulter){
        T out = get(holder, function);
        return out == null ? defaulter.get() : out;
    }

    public static void require(@NotNull DataComponentHolder holder, @NotNull Consumer<CompoundTag> consumer){
        CustomData data = holder.get(DataComponents.CUSTOM_DATA);
        if(data == null) {
            data = CustomData.of(new CompoundTag());
        }
        data = data.update(consumer);
        if(holder instanceof ItemStack i){
            i.set(DataComponents.CUSTOM_DATA,data);
        }
    }

    public static void setOwner(@NotNull DataComponentHolder holder, @Nullable UUID uuid){
        if(uuid != null) {
            require(holder, c -> c.putUUID("ctf_owner", uuid));
        }
        else{
            require(holder, c -> c.remove("ctf_owner"));
        }
    }

    public static @Nullable UUID getOwner(@NotNull DataComponentHolder holder){
        return get(holder,tag->tag.contains("ctf_owner") ? tag.getUUID("ctf_owner") : null);
    }

    public static void setVanish(@NotNull DataComponentHolder holder, boolean value){
        if(value) {
            require(holder, c -> c.putBoolean("ctf_vanish", true));
        }
        else{
            require(holder, c -> c.remove("ctf_vanish"));
        }
    }

    public static boolean getVanish(@NotNull DataComponentHolder holder){
        return getDefaulted(holder,tag->tag.contains("ctf_vanish"),()->false);
    }

    public static void setClass(@NotNull DataComponentHolder holder, @Nullable ClassType type){
        require(holder,c->c.putString("ctf_class",type.name()));
    }

    public static int getSoulBound(@NotNull DataComponentHolder holder){
        return getDefaulted(holder,tag->tag.contains("ctf_soul_bound") ? tag.getInt("ctf_soul_bound") : -1,()->-1);
    }

    public static void setSoulBound(@NotNull DataComponentHolder holder, int value){
        require(holder, c -> c.putInt("ctf_soul_bound", value));
    }

    public static @NotNull ClassType getClass(@NotNull DataComponentHolder holder){
        return getDefaulted(holder,
                tag->tag.contains("ctf_class") ? (ClassType)ClassType.valueOf(ClassType.class,tag.getString("ctf_class")) : ClassType.NONE,
                ()->ClassType.NONE);
    }

    public static void setCustomItemType(@NotNull DataComponentHolder holder, @NotNull CustomItem item){
        require(holder,c->c.putString("ctf_custom_item",item.name()));
    }

    public static @NotNull CustomItem getCustomItem(@NotNull DataComponentHolder holder){
        return getDefaulted(holder,
                tag->tag.contains("ctf_custom_item") ? CustomItem.valueOf(CustomItem.class,tag.getString("ctf_custom_item")) : CustomItem.NONE,
                ()->CustomItem.NONE);
    }
}
