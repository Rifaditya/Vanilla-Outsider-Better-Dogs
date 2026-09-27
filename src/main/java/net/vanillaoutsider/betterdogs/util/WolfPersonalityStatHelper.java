// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.UUID;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for calculating and applying dynamic personality-based entity attributes.
 */
public class WolfPersonalityStatHelper {

    public static final UUID PERSONALITY_HEALTH_UUID = UUID.fromString("d0a51c01-7001-4b1a-9f5e-bd0000000001");
    public static final UUID PERSONALITY_DAMAGE_UUID = UUID.fromString("d0a51c01-7001-4b1a-9f5e-bd0000000002");
    public static final UUID PERSONALITY_SPEED_UUID = UUID.fromString("d0a51c01-7001-4b1a-9f5e-bd0000000003");

    public static final double BASE_UNTAMED_WOLF_HEALTH = 20.0D;
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

        double defaultBaseHealth = wolf.isTame() ? BASE_WOLF_HEALTH : BASE_UNTAMED_WOLF_HEALTH;
        double defaultBaseDamage = BASE_WOLF_DAMAGE;
        double defaultBaseSpeed = BASE_WOLF_SPEED;

        double targetHealth = defaultBaseHealth;
        double targetDamage = defaultBaseDamage;
        double targetSpeed = defaultBaseSpeed;

        switch (personality) {
            case AGGRESSIVE -> {
                int healthOffset = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_AGGRO_HEALTH, -10);
                int dmgPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_AGGRO_DMG_PCT, 15);
                int speedPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_AGGRO_SPEED_PCT, 15);
                targetHealth = Math.max(10.0D, defaultBaseHealth + healthOffset);
                targetDamage = Math.max(1.0D, defaultBaseDamage * (1.0D + (dmgPct / 100.0D)));
                targetSpeed = Math.max(0.1D, defaultBaseSpeed * (1.0D + (speedPct / 100.0D)));
            }
            case PACIFIST -> {
                int healthOffset = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_PACI_HEALTH, 20);
                int dmgPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_PACI_DMG_PCT, -15);
                int speedPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_PACI_SPEED_PCT, -10);
                targetHealth = Math.max(10.0D, defaultBaseHealth + healthOffset);
                targetDamage = Math.max(1.0D, defaultBaseDamage * (1.0D + (dmgPct / 100.0D)));
                targetSpeed = Math.max(0.1D, defaultBaseSpeed * (1.0D + (speedPct / 100.0D)));
            }
            case NORMAL -> {
                int healthOffset = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_NORMAL_HEALTH, 0);
                int dmgPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_NORMAL_DMG_PCT, 0);
                int speedPct = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_NORMAL_SPEED_PCT, 0);
                targetHealth = Math.max(10.0D, defaultBaseHealth + healthOffset);
                targetDamage = Math.max(1.0D, defaultBaseDamage * (1.0D + (dmgPct / 100.0D)));
            }
        }

        if (wolf instanceof net.vanillaoutsider.betterdogs.WolfExtensions ext && ext.betterdogs$isInbred()) {
            targetHealth = Math.max(10.0D, targetHealth * 0.75D);
            targetDamage = Math.max(1.0D, targetDamage * 0.75D);
            targetSpeed = Math.max(0.1D, targetSpeed * 0.85D);
        }

        AttributeInstance healthAttr = wolf.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            if (healthAttr.getBaseValue() != defaultBaseHealth) {
                healthAttr.setBaseValue(defaultBaseHealth);
            }
            healthAttr.removeModifier(PERSONALITY_HEALTH_UUID);
            double healthMod = targetHealth - defaultBaseHealth;
            if (healthMod != 0.0D) {
                healthAttr.addTransientModifier(new AttributeModifier(
                    PERSONALITY_HEALTH_UUID,
                    "Better Dogs Personality Health",
                    healthMod,
                    AttributeModifier.Operation.ADDITION
                ));
            }
            if (wolf.getHealth() > wolf.getMaxHealth()) {
                wolf.setHealth(wolf.getMaxHealth());
            }
        }

        AttributeInstance damageAttr = wolf.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null) {
            if (damageAttr.getBaseValue() != defaultBaseDamage) {
                damageAttr.setBaseValue(defaultBaseDamage);
            }
            damageAttr.removeModifier(PERSONALITY_DAMAGE_UUID);
            double damageMod = targetDamage - defaultBaseDamage;
            if (damageMod != 0.0D) {
                damageAttr.addTransientModifier(new AttributeModifier(
                    PERSONALITY_DAMAGE_UUID,
                    "Better Dogs Personality Damage",
                    damageMod,
                    AttributeModifier.Operation.ADDITION
                ));
            }
        }

        AttributeInstance speedAttr = wolf.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            if (speedAttr.getBaseValue() != defaultBaseSpeed) {
                speedAttr.setBaseValue(defaultBaseSpeed);
            }
            speedAttr.removeModifier(PERSONALITY_SPEED_UUID);
            double speedMod = targetSpeed - defaultBaseSpeed;
            if (speedMod != 0.0D) {
                speedAttr.addTransientModifier(new AttributeModifier(
                    PERSONALITY_SPEED_UUID,
                    "Better Dogs Personality Speed",
                    speedMod,
                    AttributeModifier.Operation.ADDITION
                ));
            }
        }
    }
}
