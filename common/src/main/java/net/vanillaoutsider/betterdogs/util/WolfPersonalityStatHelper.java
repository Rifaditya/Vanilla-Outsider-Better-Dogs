// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for calculating and applying dynamic personality-based entity attributes.
 */
public class WolfPersonalityStatHelper {

    public static final double BASE_WOLF_HEALTH = 30.0D;
    public static final double BASE_WOLF_DAMAGE = 4.0D;
    public static final double BASE_WOLF_SPEED = 0.30D;

    public static void applyPersonalityStats(Wolf wolf, WolfPersonality personality) {
        if (wolf == null || personality == null) {
            return;
        }

        Level level = wolf.getCommandSenderWorld();
        if (level == null) {
            return;
        }

        double targetHealth = BASE_WOLF_HEALTH;
        double targetDamage = BASE_WOLF_DAMAGE;
        double targetSpeed = BASE_WOLF_SPEED;

        switch (personality) {
            case AGGRESSIVE -> {
                int healthOffset = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_AGGRO_HEALTH, -10);
                int dmgPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_AGGRO_DMG_PCT, 15);
                int speedPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_AGGRO_SPEED_PCT, 15);
                targetHealth = Math.max(10.0D, BASE_WOLF_HEALTH + healthOffset);
                targetDamage = Math.max(1.0D, BASE_WOLF_DAMAGE * (1.0D + (dmgPct / 100.0D)));
                targetSpeed = Math.max(0.1D, BASE_WOLF_SPEED * (1.0D + (speedPct / 100.0D)));
            }
            case PACIFIST -> {
                int healthOffset = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_PACI_HEALTH, 20);
                int dmgPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_PACI_DMG_PCT, -15);
                int speedPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_PACI_SPEED_PCT, -10);
                targetHealth = Math.max(10.0D, BASE_WOLF_HEALTH + healthOffset);
                targetDamage = Math.max(1.0D, BASE_WOLF_DAMAGE * (1.0D + (dmgPct / 100.0D)));
                targetSpeed = Math.max(0.1D, BASE_WOLF_SPEED * (1.0D + (speedPct / 100.0D)));
            }
            case NORMAL -> {
                int healthOffset = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_NORMAL_HEALTH, 0);
                int dmgPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_NORMAL_DMG_PCT, 0);
                int speedPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_NORMAL_SPEED_PCT, 0);
                targetHealth = Math.max(10.0D, BASE_WOLF_HEALTH + healthOffset);
                targetDamage = Math.max(1.0D, BASE_WOLF_DAMAGE * (1.0D + (dmgPct / 100.0D)));
            }
        }

        if (wolf instanceof net.vanillaoutsider.betterdogs.WolfExtensions ext && ext.betterdogs$isInbred()) {
            targetHealth = Math.max(10.0D, targetHealth * 0.75D);
            targetDamage = Math.max(1.0D, targetDamage * 0.75D);
            targetSpeed = Math.max(0.1D, targetSpeed * 0.85D);
        }

        AttributeInstance healthAttr = wolf.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            double oldMax = healthAttr.getBaseValue();
            healthAttr.setBaseValue(targetHealth);
            if (targetHealth > oldMax && wolf.getHealth() < (float) targetHealth) {
                wolf.setHealth((float) Math.min(targetHealth, wolf.getHealth() + (targetHealth - oldMax)));
            }
        }

        AttributeInstance damageAttr = wolf.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null) {
            damageAttr.setBaseValue(targetDamage);
        }

        AttributeInstance speedAttr = wolf.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(targetSpeed);
        }
    }
}
