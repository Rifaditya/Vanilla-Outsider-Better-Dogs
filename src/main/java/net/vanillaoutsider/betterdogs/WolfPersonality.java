// Verified against: WolfPersonality.java (26.1.2+)
package net.vanillaoutsider.betterdogs;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import java.util.Random;
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
    private static final Random RANDOM = new Random();

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
        // Fetch raw integers (e.g. 60, 20, 20)
        // We can just use the integer values as weights.
        int normal = 0;
        int aggressive = 0;
        int pacifist = 0;

        if (level.isClientSide()) {
             // Client side fallback (shouldn't happen for spawning logic, but safe)
             return NORMAL;
        } else {
             normal = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TAME_NORMAL_PERCENT);
             aggressive = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TAME_AGGRO_PERCENT);
             pacifist = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_TAME_PACI_PERCENT);
        }

        int total = normal + aggressive + pacifist;

        int roll = RANDOM.nextInt(total > 0 ? total : 100);
        
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
