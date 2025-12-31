package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.server.level.ServerLevel;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

/**
 * Mixin for Wolf breeding to implement personality genetics.
 * 
 * Breeding Rules:
 * - Same + Same = 80% same, 10% each other
 * - Normal + Aggressive = 40% Normal, 40% Aggressive, 20% Pacifist
 * - Normal + Pacifist = 40% Normal, 40% Pacifist, 20% Aggressive
 * - Aggressive + Pacifist = 50% Normal, 25% Aggressive, 25% Pacifist (diluted)
 */
@Mixin(Wolf.class)
public abstract class WolfBreedingMixin {

    private static final Random RANDOM = new Random();

    @Inject(method = "getBreedOffspring", at = @At("RETURN"))
    private void betterdogs$inheritPersonality(ServerLevel level, AgeableMob partner,
            CallbackInfoReturnable<Wolf> cir) {
        Wolf baby = cir.getReturnValue();
        if (baby == null)
            return;

        Wolf parent1 = (Wolf) (Object) this;

        if (!(partner instanceof Wolf parent2))
            return;

        WolfExtensions ext1 = (WolfExtensions) parent1;
        WolfExtensions ext2 = (WolfExtensions) parent2;
        WolfExtensions babyExt = (WolfExtensions) baby;

        // Get parent personalities (default to NORMAL if not set)
        WolfPersonality p1 = ext1.betterdogs$hasPersonality() ? ext1.betterdogs$getPersonality()
                : WolfPersonality.NORMAL;
        WolfPersonality p2 = ext2.betterdogs$hasPersonality() ? ext2.betterdogs$getPersonality()
                : WolfPersonality.NORMAL;

        // Calculate baby personality based on genetics
        WolfPersonality babyPersonality = calculateOffspringPersonality(p1, p2);
        babyExt.betterdogs$setPersonality(babyPersonality);
    }

    /**
     * Calculate offspring personality based on parent personalities.
     * Uses configurable percentages from BetterDogsConfig.
     */
    private WolfPersonality calculateOffspringPersonality(WolfPersonality p1, WolfPersonality p2) {
        BetterDogsConfig config = BetterDogsConfig.Companion.get();
        int roll = RANDOM.nextInt(100);

        // Same personality parents: configurable same%, remaining split between others
        if (p1 == p2) {
            int sameChance = config.getBreedingSameParentChance();
            int otherChance = config.getBreedingSameParentOtherChance();
            if (roll < sameChance)
                return p1;
            if (roll < (sameChance + otherChance))
                return getOther(p1, 0);
            return getOther(p1, 1);
        }

        // Aggressive + Pacifist = Diluted genes (configurable Normal%, remaining split)
        if ((p1 == WolfPersonality.AGGRESSIVE && p2 == WolfPersonality.PACIFIST) ||
                (p1 == WolfPersonality.PACIFIST && p2 == WolfPersonality.AGGRESSIVE)) {
            int normalChance = config.getBreedingDilutedNormalChance();
            int otherChance = config.getBreedingDilutedOtherChance();
            if (roll < normalChance)
                return WolfPersonality.NORMAL;
            if (roll < (normalChance + otherChance))
                return WolfPersonality.AGGRESSIVE;
            return WolfPersonality.PACIFIST;
        }

        // Normal + Other = configurable distribution
        if (p1 == WolfPersonality.NORMAL || p2 == WolfPersonality.NORMAL) {
            WolfPersonality other = (p1 == WolfPersonality.NORMAL) ? p2 : p1;
            WolfPersonality third = getThird(WolfPersonality.NORMAL, other);
            int dominantChance = config.getBreedingMixedDominantChance();
            int recessiveChance = config.getBreedingMixedRecessiveChance();

            if (roll < dominantChance)
                return WolfPersonality.NORMAL;
            if (roll < (dominantChance * 2))
                return other;
            return third;
        }

        // Fallback: equal chance
        if (roll < 33)
            return p1;
        if (roll < 66)
            return p2;
        return WolfPersonality.NORMAL;
    }

    /**
     * Get one of the other two personalities.
     */
    private WolfPersonality getOther(WolfPersonality exclude, int index) {
        WolfPersonality[] others = new WolfPersonality[2];
        int i = 0;
        for (WolfPersonality p : WolfPersonality.values()) {
            if (p != exclude) {
                others[i++] = p;
            }
        }
        return others[index % 2];
    }

    /**
     * Get the third personality that isn't either of the two given.
     */
    private WolfPersonality getThird(WolfPersonality a, WolfPersonality b) {
        for (WolfPersonality p : WolfPersonality.values()) {
            if (p != a && p != b)
                return p;
        }
        return WolfPersonality.NORMAL;
    }
}
