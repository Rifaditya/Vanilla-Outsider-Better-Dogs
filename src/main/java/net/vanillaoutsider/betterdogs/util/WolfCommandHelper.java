// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs.util;

import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;

public class WolfCommandHelper {

    public static int inspectTargets(CommandSourceStack source, Collection<? extends Entity> targets) {
        int count = 0;
        for (Entity entity : targets) {
            if (entity instanceof Wolf wolf) {
                count++;
                float scale = (wolf instanceof WolfExtensions ext) ? ext.betterdogs$getSocialScale() : 1.0f;

                var speedAttr = wolf.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
                float speed = (speedAttr != null) ? (float) speedAttr.getValue() : 0.30f;

                var healthAttr = wolf.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
                float maxHealth = (healthAttr != null) ? (float) healthAttr.getValue() : 20.0f;
                float currentHealth = wolf.getHealth();

                float pitchFactor = CanineGeneticsHelper.calculateVoicePitch(1.0f, scale);

                String name = wolf.getDisplayName().getString();
                String id = wolf.getStringUUID().substring(0, 8);

                source.sendSuccess(() -> Component.literal(String.format(java.util.Locale.ROOT,
                        "§6[Better Dogs]§r §e%s§r (§7%s§r): Scale: §b%.2fx§r | Speed: §b%.3f§r | Health: §b%.1f/%.1f§r | Pitch Timbre: §b%.3fx§r",
                        name, id, scale, speed, currentHealth, maxHealth, pitchFactor
                )), false);
            }
        }

        if (count == 0) {
            source.sendFailure(Component.literal("§c[Better Dogs] No valid wolves found in target selection."));
        } else {
            final int finalCount = count;
            source.sendSuccess(() -> Component.literal("§6[Better Dogs]§r Inspected §a" + finalCount + "§r dog(s)."), false);
        }
        return count;
    }
}
