// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import java.util.Locale;

/**
 * Density tiers for visual particle cues across Better Dogs mechanics.
 */
public enum ParticleDensity {
    NONE(0, 0),
    LOW(1, 1),
    MEDIUM(2, 3),
    HIGH(3, 6);

    private final int level;
    private final int defaultCount;

    ParticleDensity(int level, int defaultCount) {
        this.level = level;
        this.defaultCount = defaultCount;
    }

    public int getLevel() {
        return this.level;
    }

    public int getDefaultCount() {
        return this.defaultCount;
    }

    public static ParticleDensity fromInt(int level) {
        return switch (level) {
            case 0 -> NONE;
            case 1 -> LOW;
            case 3 -> HIGH;
            default -> MEDIUM;
        };
    }

    public static ParticleDensity fromString(String name) {
        if (name == null || name.trim().isEmpty()) {
            return MEDIUM;
        }
        try {
            return valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return MEDIUM;
        }
    }
}
