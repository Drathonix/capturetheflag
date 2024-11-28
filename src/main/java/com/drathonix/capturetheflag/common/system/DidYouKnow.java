package com.drathonix.capturetheflag.common.system;

import com.drathonix.capturetheflag.common.util.GeneralUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DidYouKnow {
    public static void announce(){
        GeneralUtil.sendToAllPlayers(Component.literal("Did you know: ").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withBold(true)).append(rand()));
        GeneralUtil.sendToAllPlayers(SoundEvents.NOTE_BLOCK_PLING.value());
    }

    private static final List<Component> options = new ArrayList<>();

    static {
        options.add(Component.literal("Each parkour chamber can be beaten only once by your team!").withStyle(ChatFormatting.GREEN));
        options.add(Component.literal("Harder parkour chambers have higher rewards and higher attempts per minute. Don't sleep on them!").withStyle(ChatFormatting.GREEN));
        options.add(Component.literal("Enchanting an item will give it one level of Lesser Soulbound, it will not be destroyed when the level is lost.").withStyle(ChatFormatting.GREEN));
        options.add(Component.literal("You can't enter your own flag zone, but after 30 seconds any enter that has entered it can be attacked.").withStyle(ChatFormatting.GREEN));
        options.add(Component.literal("Enderpearls equate to 32 lapis, kill those Endermen!").withStyle(ChatFormatting.GREEN));
        options.add(Component.literal("Saddles don't exist anymore.").withStyle(ChatFormatting.RED));
        options.add(Component.literal("There is a barrel in your spawn only openable by your team.").withStyle(ChatFormatting.GREEN));
        options.add(Component.literal("Look out for strange formations underwater, they could be caves!").withStyle(ChatFormatting.GREEN));
        options.add(Component.literal("You can't be hurt in your spawn, but you also can't hurt others.").withStyle(ChatFormatting.GREEN));
        options.add(Component.literal("Don't try to break the parkour chambers... Please.").withStyle(ChatFormatting.RED));
        options.add(Component.literal("You have a near 1/100 chance of getting a super rare item from harder parkour chambers. 99% of gamblers quick before they hit big!").withStyle(ChatFormatting.GREEN));
        options.add(Component.literal("Put the bucket away. You will doom yourselves.").withStyle(ChatFormatting.RED));
        options.add(Component.literal("Diamond Crystals only generate in neutral territory. You may have to fight for your reward!").withStyle(ChatFormatting.GREEN));
        options.add(Component.literal("Only Hard-Champion difficulty parkour chambers spawn below y-48, also more of them spawn, going deep is worth it.").withStyle(ChatFormatting.GREEN));
        options.add(Component.literal("You can change your skills by crafting a ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal("Lesser Attunement Gem").setStyle(Style.EMPTY
                        .withUnderlined(true)
                        .withColor(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Drathonix/capturetheflag/wiki/attunement-gems#lesser-attunement-gem")))));
        options.add(Component.literal("You can change your class by crafting a ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal("Greater Attunement Gem").setStyle(Style.EMPTY
                        .withUnderlined(true)
                        .withColor(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Drathonix/capturetheflag/wiki/attunement-gems#greater-attunement-gem")))));

    }

    private static final List<Component> opts = new ArrayList<>(options);
    private static Component rand() {
        if(opts.isEmpty()){
            opts.addAll(GeneralUtil.shuffle(options));
        }
        return opts.removeFirst();
    }
}
