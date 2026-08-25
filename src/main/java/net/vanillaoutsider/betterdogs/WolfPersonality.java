// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.dasik.social.util.FastRandom;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Wolf personality types that affect combat behavior.
 * Assigned randomly on tame and stored permanently in NBT.
 */
public enum WolfPersonality {
    /** Vanilla behavior - attacks what owner attacks */
    NORMAL(0),

    /** Auto-attacks hostile mobs near owner */
    AGGRESSIVE(1),

    /** Won't attack when player attacks - only defends when player is hurt */
    PACIFIST(2);

    private final int id;

    WolfPersonality(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Randomly select a personality based on Game Rules.
     * Uses Game Rules (Integers representing Percentages) for per-world storage.
     * Defaults are pulled from Global Config if Game Rules are unset or new world.
     */
    public static WolfPersonality random(Level level) {
        if (level == null || level.isClientSide()) {
            return NORMAL;
        }

        int normal = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_SPAWN_NORMAL_PERCENT);
        int aggressive = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_SPAWN_AGGRO_PERCENT);
        int pacifist = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_SPAWN_PACI_PERCENT);

        int total = normal + aggressive + pacifist;
        int bound = total > 0 ? total : 100;
        int roll = FastRandom.INSTANCE.nextInt(bound);
        
        if (DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_DEBUGGING)) {
            BetterDogs.LOGGER.info("Personality Roll: {}/{} (Chances: N:{}, A:{}, P:{})", 
                roll, total, normal, aggressive, pacifist);
        }

        if (roll < normal)
            return NORMAL;
        if (roll < (normal + aggressive))
            return AGGRESSIVE;
        return PACIFIST;
    }

    /**
     * Cycles to the next personality in the enum.
     */
    public WolfPersonality next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    /**
     * Get personality from NBT id value
     */
    public static WolfPersonality fromId(int id) {
        for (WolfPersonality p : values()) {
            if (p.id == id)
                return p;
        }
        return NORMAL;
    }
}
