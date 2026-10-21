// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.11
package net.vanillaoutsider.betterdogs.genetics;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose helper for canine physical genetics:
 * health, speed, scale inheritance, and acoustic scale pitch modulation.
 */
public final class CanineGeneticsHelper {

    // Saturated bounds constants for physical wolf genetics
    public static final float MIN_HEALTH = 10.0f;
    public static final float MAX_HEALTH = 60.0f;
    public static final float HEALTH_STD_DEV = 2.0f;

    public static final float MIN_SPEED = 0.15f;
    public static final float MAX_SPEED = 0.45f;
    public static final float SPEED_STD_DEV = 0.015f;

    public static final float MIN_SCALE = 0.70f;
    public static final float MAX_SCALE = 1.30f;
    public static final float SCALE_STD_DEV = 0.05f;

    public static final float MIN_PITCH = 0.50f;
    public static final float MAX_PITCH = 2.0f;

    private CanineGeneticsHelper() {
    }

    /**
     * Calculates child stat using intermediate inheritance with Gaussian variance:
     * Stat_child = (p1 + p2) / 2 + (gaussian * stdDev)
     * Clamped strictly between min and max.
     */
    public static float calculateOffspringStat(float p1, float p2, float stdDev, float min, float max, RandomSource rand) {
        float gaussian = (rand != null) ? (float) rand.nextGaussian() : 0.0f;
        float midpoint = (p1 + p2) / 2.0f;
        float rawStat = midpoint + (gaussian * stdDev);
        return clamp(rawStat, min, max);
    }

    /**
     * Modulates voice pitch inversely proportional to scale:
     * Pitch = basePitch / sqrt(safeScale)
     * Clamped between MIN_PITCH and MAX_PITCH.
     */
    public static float calculateVoicePitch(float basePitch, float scale) {
        float safeScale = Math.max(0.1f, scale);
        float pitch = basePitch / (float) Math.sqrt(safeScale);
        return clamp(pitch, MIN_PITCH, MAX_PITCH);
    }

    /**
     * Obtains the modulated voice pitch for a wolf based on its WolfExtensions scale.
     * Defaults scale to 1.0f if wolf is null or extension is not present.
     */
    public static float getModulatedVoicePitch(Wolf wolf, float basePitch) {
        if (wolf == null) {
            return calculateVoicePitch(basePitch, 1.0f);
        }
        float scale = (wolf instanceof WolfExtensions ext) ? ext.betterdogs$getSocialScale() : 1.0f;
        return calculateVoicePitch(basePitch, scale);
    }

    /**
     * Saturating clamp utility between min and max.
     */
    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }
}
