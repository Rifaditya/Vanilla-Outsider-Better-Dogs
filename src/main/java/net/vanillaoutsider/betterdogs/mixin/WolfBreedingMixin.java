// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.mixin;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.util.WolfScaleGeneticsHelper;
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
        WolfPersonality babyPersonality = betterdogs$calculateOffspringPersonality(level, p1, p2, baby.getRandom());
        babyExt.betterdogs$setPersonality(babyPersonality);

        // Inherit offspring scale with continuous variance
        float p1Scale = 1.0f;
        var s1 = parent1.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.SCALE);
        if (s1 != null) {
            p1Scale = (float) s1.getBaseValue();
        }
        float p2Scale = 1.0f;
        var s2 = parent2.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.SCALE);
        if (s2 != null) {
            p2Scale = (float) s2.getBaseValue();
        }

        float babyScale = WolfScaleGeneticsHelper.calculateOffspringScale(level, p1Scale, p2Scale, baby.getRandom());
        var babyScaleAttr = baby.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.SCALE);
        if (babyScaleAttr != null) {
            babyScaleAttr.setBaseValue(babyScale);
        }

        // Inherit genetics via DasikLibrary Genetics Engine
        net.dasik.social.api.genetics.GeneticsEngine.inheritGenetics(baby, parent1, parent2, babyPersonality.name().toLowerCase(java.util.Locale.ROOT));

        // Process inbreeding lineage, runt scale penalties, smoke particles, and advancement triggers
        net.vanillaoutsider.betterdogs.util.WolfInbreedingHelper.processBreedingLineage(baby, parent1, parent2, level);

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
    private WolfPersonality betterdogs$calculateOffspringPersonality(ServerLevel level, WolfPersonality p1, WolfPersonality p2, RandomSource random) {
        int roll = (random != null ? random.nextInt(100) : 0);

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
     * Get one of the other two personalities (zero-allocation).
     */
    @Unique
    private WolfPersonality betterdogs$getOther(WolfPersonality exclude, int index) {
        return switch (exclude) {
            case NORMAL -> (index % 2 == 0) ? WolfPersonality.AGGRESSIVE : WolfPersonality.PACIFIST;
            case AGGRESSIVE -> (index % 2 == 0) ? WolfPersonality.NORMAL : WolfPersonality.PACIFIST;
            case PACIFIST -> (index % 2 == 0) ? WolfPersonality.NORMAL : WolfPersonality.AGGRESSIVE;
        };
    }

    /**
     * Get the third personality that isn't either of the two given (zero-allocation).
     */
    @Unique
    private WolfPersonality betterdogs$getThird(WolfPersonality a, WolfPersonality b) {
        if (a != WolfPersonality.NORMAL && b != WolfPersonality.NORMAL) return WolfPersonality.NORMAL;
        if (a != WolfPersonality.AGGRESSIVE && b != WolfPersonality.AGGRESSIVE) return WolfPersonality.AGGRESSIVE;
        return WolfPersonality.PACIFIST;
    }
}
