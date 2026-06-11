// Verified against: Wolf.java (26.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import java.util.Random;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.util.WolfStatManager;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for Wolf breeding to implement personality genetics.
 */
@Mixin(Wolf.class)
public abstract class WolfBreedingMixin {

    @Unique
    private static final Random betterdogs$RANDOM = new Random();

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
        WolfExtensions ext2 = (WolfExtensions) partner;
        WolfExtensions babyExt = (WolfExtensions) baby;

        // Get parent personalities (default to NORMAL if not set)
        WolfPersonality p1 = ext1.betterdogs$hasPersonality() ? ext1.betterdogs$getPersonality()
                : WolfPersonality.NORMAL;
        WolfPersonality p2 = ext2.betterdogs$hasPersonality() ? ext2.betterdogs$getPersonality()
                : WolfPersonality.NORMAL;

        // Calculate baby personality based on genetics
        WolfPersonality babyPersonality = betterdogs$calculateOffspringPersonality(level, p1, p2);
        babyExt.betterdogs$setPersonality(babyPersonality);

        // Inherit genetics via DasikLibrary Genetics Engine
        net.dasik.social.api.genetics.GeneticsEngine.inheritGenetics(baby, parent1, parent2, babyPersonality.name().toLowerCase(java.util.Locale.ROOT));

        // Get parent and baby genetics for advancement triggers
        var babyGen = net.dasik.social.api.genetics.GeneticsEngine.getGenetics(baby);
        var p1Gen = net.dasik.social.api.genetics.GeneticsEngine.getGenetics(parent1);
        var p2Gen = net.dasik.social.api.genetics.GeneticsEngine.getGenetics(parent2);

        if (babyGen.inbred()) {
            net.minecraft.server.level.ServerPlayer player = parent1.getLoveCause();
            if (player == null) {
                player = parent2.getLoveCause();
            }
            if (player != null) {
                net.vanillaoutsider.betterdogs.BetterDogs.INBRED_WOLF.trigger(player);
            }
        } else if (p1Gen.inbred() || p2Gen.inbred()) {
            net.minecraft.server.level.ServerPlayer player = parent1.getLoveCause();
            if (player == null) {
                player = parent2.getLoveCause();
            }
            if (player != null) {
                net.vanillaoutsider.betterdogs.BetterDogs.OUTCROSS_RUNT.trigger(player);
            }
        }

        // Roll chance for ground food refusal
        if (DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_ENABLE_REFUSE_GROUND_FOOD)) {
            int chance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_REFUSE_GROUND_FOOD_CHANCE);
            boolean refuses = baby.getRandom().nextInt(100) < chance;
            WolfPersistentData.setRefusesGroundFood(baby, refuses);
        }
    }

    /**
     * Calculate offspring personality based on parent personalities.
     * Uses configurable percentages from Game Rules.
     */
    @Unique
    private WolfPersonality betterdogs$calculateOffspringPersonality(ServerLevel level, WolfPersonality p1, WolfPersonality p2) {
        int roll = betterdogs$RANDOM.nextInt(100);

        // Same personality parents: configurable same%, remaining split between others
        if (p1 == p2) {
            int sameChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_BREED_SAME_CHANCE);
            int otherChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_BREED_SAME_OTHER_CHANCE);
            if (roll < sameChance)
                return p1;
            if (roll < (sameChance + otherChance))
                return betterdogs$getOther(p1, 0);
            return betterdogs$getOther(p1, 1);
        }

        // Aggressive + Pacifist = Diluted genes (configurable Normal%, remaining split)
        if ((p1 == WolfPersonality.AGGRESSIVE && p2 == WolfPersonality.PACIFIST) ||
                (p1 == WolfPersonality.PACIFIST && p2 == WolfPersonality.AGGRESSIVE)) {
            int normalChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_BREED_DILUTED_NORMAL_CHANCE);
            int otherChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_BREED_DILUTED_OTHER_CHANCE);
            if (roll < normalChance)
                return WolfPersonality.NORMAL;
            if (roll < (normalChance + otherChance))
                return WolfPersonality.AGGRESSIVE;
            return WolfPersonality.PACIFIST;
        }

        // Normal + Other = configurable distribution
        if (p1 == WolfPersonality.NORMAL || p2 == WolfPersonality.NORMAL) {
            WolfPersonality other = (p1 == WolfPersonality.NORMAL) ? p2 : p1;
            WolfPersonality third = betterdogs$getThird(WolfPersonality.NORMAL, other);
            int dominantChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_BREED_MIXED_DOMINANT_CHANCE);
            int recessiveChance = DynamicGameRuleManager.getInt(level,
                    BetterDogsGameRules.BD_BREED_MIXED_RECESSIVE_CHANCE);

            if (roll < dominantChance)
                return WolfPersonality.NORMAL;
            if (roll < (dominantChance + recessiveChance))
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
    @Unique
    private WolfPersonality betterdogs$getOther(WolfPersonality exclude, int index) {
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
    @Unique
    private WolfPersonality betterdogs$getThird(WolfPersonality a, WolfPersonality b) {
        for (WolfPersonality p : WolfPersonality.values()) {
            if (p != a && p != b)
                return p;
        }
        return WolfPersonality.NORMAL;
    }
}
