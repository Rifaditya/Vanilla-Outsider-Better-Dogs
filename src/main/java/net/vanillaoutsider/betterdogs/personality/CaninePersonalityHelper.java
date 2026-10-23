// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.personality;

import java.util.UUID;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Deterministic helper for rolling and retrieving canine personality archetypes.
 */
public final class CaninePersonalityHelper {

    private CaninePersonalityHelper() {
    }

    /**
     * Resolves or rolls the canine archetype for the given wolf.
     * If already assigned, returns existing archetype; otherwise rolls deterministically from UUID.
     */
    public static CanineArchetype getOrRollArchetype(Wolf wolf) {
        if (wolf == null) {
            return CanineArchetype.GUARDIAN;
        }

        if (WolfPersistentData.hasPersistedCanineArchetype(wolf)) {
            return WolfPersistentData.getPersistedCanineArchetype(wolf);
        }

        CanineArchetype rolled = rollFromUUID(wolf.getUUID(), wolf.level());
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setCanineArchetype(rolled);
        } else {
            WolfPersistentData.setPersistedCanineArchetype(wolf, rolled);
        }
        return rolled;
    }

    /**
     * Deterministically rolls a CanineArchetype based on 64-bit UUID hashing and world GameRules.
     *
     * @param uuid  Entity UUID
     * @param level World level (can be null for headless / test environments)
     * @return Assigned CanineArchetype
     */
    public static CanineArchetype rollFromUUID(UUID uuid, Level level) {
        if (uuid == null) {
            return CanineArchetype.GUARDIAN;
        }

        if (level != null && !BetterDogsGameRules.isArchetypesEnabled(level)) {
            return CanineArchetype.GUARDIAN;
        }

        int distributionMode = level != null ? BetterDogsGameRules.getArchetypeDistributionMode(level) : 0;

        long seed = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();
        // High-entropy 64-bit splitmix mixing to prevent bit clustering across random UUIDs
        seed ^= (seed >>> 30);
        seed *= 0xbf58476d1ce4e5b9L;
        seed ^= (seed >>> 27);
        seed *= 0x94d049bb133111ebL;
        seed ^= (seed >>> 31);

        if (distributionMode == 1) {
            // Mode 1: Guardian Heavy (35% Guardian, 13% each others)
            long roll = Math.floorMod(seed, 100L);
            if (roll < 35) {
                return CanineArchetype.GUARDIAN;
            } else if (roll < 48) {
                return CanineArchetype.PLAYFUL;
            } else if (roll < 61) {
                return CanineArchetype.ADVENTURER;
            } else if (roll < 74) {
                return CanineArchetype.GLUTTON;
            } else if (roll < 87) {
                return CanineArchetype.LAZY;
            } else {
                return CanineArchetype.FEARLESS;
            }
        }

        // Mode 0: Balanced uniform distribution (1/6 each)
        long index = Math.floorMod(seed, 6L);
        return CanineArchetype.fromId((int) index);
    }
}
