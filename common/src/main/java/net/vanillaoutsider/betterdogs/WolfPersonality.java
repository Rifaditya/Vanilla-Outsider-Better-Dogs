// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.minecraft.util.RandomSource;

public enum WolfPersonality {
    NORMAL,
    AGGRESSIVE,
    PACIFIST;

    public String getId() {
        return name().toLowerCase();
    }

    public WolfPersonality next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public static WolfPersonality random(RandomSource random) {
        RandomSource rnd = random != null ? random : RandomSource.create();
        int roll = rnd.nextInt(100);
        if (roll < 20) {
            return AGGRESSIVE;
        } else if (roll < 40) {
            return PACIFIST;
        } else {
            return NORMAL;
        }
    }

    public static WolfPersonality random() {
        return random(RandomSource.create());
    }

    public static WolfPersonality fromName(String name) {
        return fromString(name);
    }

    public static WolfPersonality fromString(String name) {
        if (name == null) return NORMAL;
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NORMAL;
        }
    }
}
