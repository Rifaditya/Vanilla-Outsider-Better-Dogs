// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.command;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.net.URI;

/**
 * Builds resilient chat footer for commands using Component.translatableWithFallback,
 * preventing untranslated key displays on vanilla clients.
 */
public final class BetterDogsSupportFooter {
    public static final String KOFI_URL = "https://ko-fi.com/dasikigaijin";
    public static final String DISCORD_URL = "https://discord.gg/EV99bgAFqb";

    private BetterDogsSupportFooter() {}

    public static Component build() {
        MutableComponent discordPrompt = Component.translatableWithFallback(
                "dasiklibrary.support.discord.chat_prompt",
                "💬 Community: "
        ).withStyle(ChatFormatting.BLUE);

        MutableComponent discordLink = Component.translatableWithFallback(
                "dasiklibrary.support.discord.chat_link",
                "[Join Discord]"
        ).withStyle(Style.EMPTY
                .withColor(ChatFormatting.AQUA)
                .withBold(true)
                .withUnderlined(true)
                .withClickEvent(new ClickEvent.OpenUrl(URI.create(DISCORD_URL)))
                .withHoverEvent(new HoverEvent.ShowText(Component.translatableWithFallback(
                        "dasiklibrary.support.discord.chat_hover",
                        "Click to open official Discord server"
                )))
        );

        MutableComponent discordSection = discordPrompt.append(discordLink);
        MutableComponent separator = Component.literal(" • ").withStyle(ChatFormatting.DARK_GRAY);

        MutableComponent kofiPrompt = Component.translatableWithFallback(
                "dasiklibrary.support.kofi.chat_prompt",
                "☕ Enjoying the mod? "
        ).withStyle(ChatFormatting.GRAY);

        MutableComponent kofiLink = Component.translatableWithFallback(
                "dasiklibrary.support.kofi.chat_link",
                "[Support on Ko-fi]"
        ).withStyle(Style.EMPTY
                .withColor(ChatFormatting.GOLD)
                .withBold(true)
                .withUnderlined(true)
                .withClickEvent(new ClickEvent.OpenUrl(URI.create(KOFI_URL)))
                .withHoverEvent(new HoverEvent.ShowText(Component.translatableWithFallback(
                        "dasiklibrary.support.kofi.chat_hover",
                        "Click to open Ko-fi in browser"
                )))
        );

        MutableComponent kofiSection = kofiPrompt.append(kofiLink);

        return discordSection.append(separator).append(kofiSection);
    }
}
