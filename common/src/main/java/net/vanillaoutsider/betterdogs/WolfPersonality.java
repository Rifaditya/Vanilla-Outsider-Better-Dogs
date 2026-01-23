package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import java.util.Random;

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
     * Randomly select a personality based on config distribution.
     */
    public static WolfPersonality random() {
        BetterDogsConfig config = BetterDogsConfig.get();
        int normal = config.tamingChanceNormal;
        int aggressive = config.tamingChanceAggressive;
        int pacifist = config.tamingChancePacifist;
        int total = normal + aggressive + pacifist;

        int roll = RANDOM.nextInt(total > 0 ? total : 100);

        if (roll < normal)
            return NORMAL;
        if (roll < (normal + aggressive))
            return AGGRESSIVE;
        return PACIFIST;
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
