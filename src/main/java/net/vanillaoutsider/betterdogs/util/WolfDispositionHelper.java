// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import java.util.UUID;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

/**
 * Dedicated single-purpose helper for additive personality + full-spectrum deterministic UUID-seeded behavioral variance.
 */
public final class WolfDispositionHelper {

    public static final long SALT_FETCH = 0xFE7C401L;
    public static final long SALT_FETCH_REACTION = 0xFE7C402L;
    public static final long SALT_STORM = 0x5708301L;
    public static final long SALT_HOWL = 0x4031701L;
    public static final long SALT_HOOVER = 0x800F3E1L;

    private WolfDispositionHelper() {
    }

    /**
     * Calculates a deterministic integer roll in [0, 99] using bit-mixing hashing.
     */
    public static int getSeededRoll(UUID uuid, long salt) {
        if (uuid == null) {
            return 50;
        }
        long seed = uuid.getMostSignificantBits() ^ (uuid.getLeastSignificantBits() * 31L) ^ salt;
        seed = (seed ^ (seed >>> 16)) * 0x85ebca6bL;
        seed = (seed ^ (seed >>> 13)) * 0xc2b2ae35L;
        seed = seed ^ (seed >>> 16);
        return (int) (Math.abs(seed) % 100L);
    }

    /**
     * Calculates a deterministic integer offset in [minOffset, maxOffset] using bit-mixing hashing.
     */
    public static int getSeededOffset(UUID uuid, long salt, int minOffset, int maxOffset) {
        if (uuid == null) {
            return 0;
        }
        long seed = uuid.getMostSignificantBits() ^ (uuid.getLeastSignificantBits() * 37L) ^ salt;
        seed = (seed ^ (seed >>> 16)) * 0x85ebca6bL;
        seed = (seed ^ (seed >>> 13)) * 0xc2b2ae35L;
        seed = seed ^ (seed >>> 16);
        int range = Math.max(1, maxOffset - minOffset + 1);
        int roll = (int) (Math.abs(seed) % (long) range);
        return minOffset + roll;
    }

    public static WolfPersonality getPersonality(Wolf wolf) {
        if (wolf instanceof WolfExtensions ext) {
            WolfPersonality p = ext.betterdogs$getPersonality();
            return p != null ? p : WolfPersonality.NORMAL;
        }
        return WolfPersonality.NORMAL;
    }

    /**
     * Calculates effective fetch reluctance % (Aggressive: 10%, Normal: 30%, Pacifist: 60% + [-100, +100] offset, clamped 0-100%).
     */
    public static int getFetchReluctanceChance(UUID uuid, WolfPersonality personality) {
        WolfPersonality p = personality != null ? personality : WolfPersonality.NORMAL;
        int base = switch (p) {
            case AGGRESSIVE -> 10;
            case PACIFIST -> 60;
            case NORMAL -> 30;
        };
        int offset = getSeededOffset(uuid, SALT_FETCH, -100, 100);
        return Math.max(0, Math.min(100, base + offset));
    }

    /**
     * Evaluates if this dog is willing to fetch items.
     */
    public static boolean shouldFetch(Wolf wolf) {
        if (wolf == null) {
            return true;
        }
        int reluctanceChance = getFetchReluctanceChance(wolf.getUUID(), getPersonality(wolf));
        if (reluctanceChance <= 0) {
            return true;
        }
        if (reluctanceChance >= 100) {
            return false;
        }
        int roll = getSeededRoll(wolf.getUUID(), SALT_FETCH);
        return roll >= reluctanceChance;
    }

    /**
     * For non-fetching dogs, evaluates if the dog curiously tilts head or completely ignores (~50% each).
     */
    public static boolean shouldHeadTiltOnIgnoredStick(Wolf wolf) {
        if (wolf == null) {
            return false;
        }
        int roll = getSeededRoll(wolf.getUUID(), SALT_FETCH_REACTION);
        return roll < 50;
    }

    /**
     * Calculates effective storm fearlessness % (Aggressive: 80%, Normal: 40%, Pacifist: 10% + [-100, +100] offset, clamped 0-100%).
     */
    public static int getStormFearlessChance(UUID uuid, WolfPersonality personality) {
        WolfPersonality p = personality != null ? personality : WolfPersonality.NORMAL;
        int base = switch (p) {
            case AGGRESSIVE -> 80;
            case PACIFIST -> 10;
            case NORMAL -> 40;
        };
        int offset = getSeededOffset(uuid, SALT_STORM, -100, 100);
        return Math.max(0, Math.min(100, base + offset));
    }

    /**
     * Evaluates if this dog is fearless during thunderstorms.
     */
    public static boolean isStormFearless(Wolf wolf) {
        if (wolf == null) {
            return false;
        }
        int fearlessChance = getStormFearlessChance(wolf.getUUID(), getPersonality(wolf));
        if (fearlessChance <= 0) {
            return false;
        }
        if (fearlessChance >= 100) {
            return true;
        }
        int roll = getSeededRoll(wolf.getUUID(), SALT_STORM);
        return roll < fearlessChance;
    }

    /**
     * Calculates effective quiet howling % (Aggressive: 10%, Normal: 25%, Pacifist: 60% + [-100, +100] offset, clamped 0-100%).
     */
    public static int getQuietHowlerChance(UUID uuid, WolfPersonality personality) {
        WolfPersonality p = personality != null ? personality : WolfPersonality.NORMAL;
        int base = switch (p) {
            case AGGRESSIVE -> 10;
            case PACIFIST -> 60;
            case NORMAL -> 25;
        };
        int offset = getSeededOffset(uuid, SALT_HOWL, -100, 100);
        return Math.max(0, Math.min(100, base + offset));
    }

    /**
     * Evaluates if this dog is a quiet observer who avoids pack howling.
     */
    public static boolean isQuietHowler(Wolf wolf) {
        if (wolf == null) {
            return false;
        }
        int quietChance = getQuietHowlerChance(wolf.getUUID(), getPersonality(wolf));
        if (quietChance <= 0) {
            return false;
        }
        if (quietChance >= 100) {
            return true;
        }
        int roll = getSeededRoll(wolf.getUUID(), SALT_HOWL);
        return roll < quietChance;
    }

    /**
     * Calculates effective Hoover / ground food scavenging chance % 
     * (Aggressive: 70%, Normal: 35%, Pacifist: 10% + [-100, +100] offset, clamped 0-100%).
     */
    public static int getHooverChance(UUID uuid, WolfPersonality personality) {
        WolfPersonality p = personality != null ? personality : WolfPersonality.NORMAL;
        int base = switch (p) {
            case AGGRESSIVE -> 70;
            case NORMAL -> 35;
            case PACIFIST -> 10;
        };
        int offset = getSeededOffset(uuid, SALT_HOOVER, -100, 100);
        return Math.max(0, Math.min(100, base + offset));
    }

    /**
     * Evaluates if this dog has the Hoover quirk (eagerly scavenges ground food even at full health).
     */
    public static boolean isHooverScavenger(Wolf wolf) {
        if (wolf == null) {
            return false;
        }
        int hooverChance = getHooverChance(wolf.getUUID(), getPersonality(wolf));
        if (hooverChance <= 0) {
            return false;
        }
        if (hooverChance >= 100) {
            return true;
        }
        int roll = getSeededRoll(wolf.getUUID(), SALT_HOOVER);
        return roll < hooverChance;
    }
}
