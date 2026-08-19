// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose helper for wild wolf low-health hunting and sustenance healing.
 */
public final class WildHuntHelper {

    public static final float SUSTENANCE_HEAL_AMOUNT = 4.0F; // 2 hearts
    public static final float STOP_HEALTH_THRESHOLD_PERCENT = 80.0F;

    private WildHuntHelper() {
    }

    /**
     * Checks whether an entity qualifies as valid prey for wild wolf hunting.
     */
    public static boolean isPrey(LivingEntity entity) {
        if (entity == null || !entity.isAlive()) {
            return false;
        }
        return entity instanceof Sheep
                || entity instanceof Rabbit
                || entity instanceof Chicken
                || entity instanceof Fox;
    }

    /**
     * Checks whether a wild wolf is in desperate low-health state to hunt prey.
     */
    public static boolean shouldHuntPrey(Wolf wolf) {
        if (wolf == null || wolf.isTame() || wolf.level() == null || wolf.getMaxHealth() <= 0) {
            return false;
        }
        int threshold = DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_WILD_HUNT_HEALTH_THRESHOLD);
        if (threshold <= 0) {
            return false;
        }
        float healthPercent = (wolf.getHealth() / wolf.getMaxHealth()) * 100.0F;
        return healthPercent < threshold;
    }

    /**
     * Checks whether a wild wolf should continue hunting or cease due to recovery.
     */
    public static boolean shouldContinueHunting(Wolf wolf, LivingEntity target) {
        if (wolf == null || wolf.isTame() || target == null || !target.isAlive() || wolf.getMaxHealth() <= 0) {
            return false;
        }
        float healthPercent = (wolf.getHealth() / wolf.getMaxHealth()) * 100.0F;
        return healthPercent < STOP_HEALTH_THRESHOLD_PERCENT;
    }

    /**
     * Applies sustenance healing to a wild wolf upon defeating prey.
     */
    public static void applySustenanceHealing(Wolf wolf) {
        if (wolf != null && !wolf.isTame() && wolf.isAlive()) {
            wolf.heal(SUSTENANCE_HEAL_AMOUNT);
        }
    }
}
