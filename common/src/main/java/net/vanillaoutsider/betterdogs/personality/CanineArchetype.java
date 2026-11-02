// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs.personality;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * Extensible Canine Personality Archetypes governing behavioral AI priorities,
 * roaming limits, threat responses, and baseline stat modifiers.
 */
public enum CanineArchetype implements StringRepresentable {
    GUARDIAN("guardian", 0, 1.0, 1.2, 0.95),
    PLAYFUL("playful", 1, 0.95, 0.9, 1.15),
    ADVENTURER("adventurer", 2, 1.1, 1.0, 1.1),
    GLUTTON("glutton", 3, 1.15, 0.9, 0.95),
    LAZY("lazy", 4, 1.25, 0.85, 0.85),
    FEARLESS("fearless", 5, 1.0, 1.15, 1.05);

    public static final Codec<CanineArchetype> CODEC = StringRepresentable.fromEnum(CanineArchetype::values);

    private final String serializedName;
    private final int id;
    private final double healthModifier;
    private final double damageModifier;
    private final double speedModifier;

    CanineArchetype(String serializedName, int id, double healthModifier, double damageModifier, double speedModifier) {
        this.serializedName = serializedName;
        this.id = id;
        this.healthModifier = healthModifier;
        this.damageModifier = damageModifier;
        this.speedModifier = speedModifier;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    public int getId() {
        return this.id;
    }

    public double getHealthModifier() {
        return this.healthModifier;
    }

    public double getDamageModifier() {
        return this.damageModifier;
    }

    public double getSpeedModifier() {
        return this.speedModifier;
    }

    public String getTranslationKey() {
        return "betterdogs.archetype." + this.serializedName;
    }

    public String getDescriptionTranslationKey() {
        return "betterdogs.archetype." + this.serializedName + ".desc";
    }

    public static CanineArchetype fromId(int id) {
        for (CanineArchetype archetype : values()) {
            if (archetype.id == id) {
                return archetype;
            }
        }
        return GUARDIAN;
    }

    public static CanineArchetype fromSerializedName(String name) {
        if (name == null || name.isEmpty()) {
            return GUARDIAN;
        }
        for (CanineArchetype archetype : values()) {
            if (archetype.serializedName.equalsIgnoreCase(name)) {
                return archetype;
            }
        }
        return GUARDIAN;
    }

    public static CanineArchetype defaultFallback() {
        return GUARDIAN;
    }
}
