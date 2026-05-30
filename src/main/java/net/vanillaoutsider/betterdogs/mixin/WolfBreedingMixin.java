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
 * 
 * Breeding Rules:
 * - Same + Same = 80% same, 10% each other
 * - Normal + Aggressive = 40% Normal, 40% Aggressive, 20% Pacifist
 * - Normal + Pacifist = 40% Normal, 40% Pacifist, 20% Aggressive
 * - Aggressive + Pacifist = 50% Normal, 25% Aggressive, 25% Pacifist (diluted)
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
        WolfExtensions ext2 = (WolfExtensions) parent2;
        WolfExtensions babyExt = (WolfExtensions) baby;

        // Get parent personalities (default to NORMAL if not set)
        WolfPersonality p1 = ext1.betterdogs$hasPersonality() ? ext1.betterdogs$getPersonality()
                : WolfPersonality.NORMAL;
        WolfPersonality p2 = ext2.betterdogs$hasPersonality() ? ext2.betterdogs$getPersonality()
                : WolfPersonality.NORMAL;

        // Calculate baby personality based on genetics
        WolfPersonality babyPersonality = betterdogs$calculateOffspringPersonality(level, p1, p2);
        babyExt.betterdogs$setPersonality(babyPersonality);

        // Ensure both parents have their stats rolled/applied so they have valid values
        if (!WolfPersistentData.arePersistedStatsRolled(parent1)) {
            WolfStatManager.applyPersonalityStats(parent1, p1);
        }
        if (!WolfPersistentData.arePersistedStatsRolled(parent2)) {
            WolfStatManager.applyPersonalityStats(parent2, p2);
        }

        // Get latest parent data records
        WolfPersistentData d1 = WolfPersistentData.getWolfData(parent1);
        WolfPersistentData d2 = WolfPersistentData.getWolfData(parent2);

        // Retrieve stat bonuses directly from records
        float parent1Hp = d1.healthBonus();
        float parent2Hp = d2.healthBonus();
        float parent1Dmg = d1.damageMod();
        float parent2Dmg = d2.damageMod();
        float parent1Speed = d1.speedMod();
        float parent2Speed = d2.speedMod();

        // Roll mutation values using the standard triangular range stats for the baby's personality
        net.minecraft.util.RandomSource rand = level.getRandom();
        float mutationHp = 0.0f;
        float mutationDmg = 0.0f;
        float mutationSpeed = 0.0f;

        switch (babyPersonality) {
            case NORMAL -> {
                mutationHp = rand.triangle(-2.0f, 10.0f);
                mutationDmg = rand.triangle(-0.05f, 0.25f);
                mutationSpeed = rand.triangle(-0.025f, 0.175f);
            }
            case AGGRESSIVE -> {
                mutationHp = rand.triangle(-5.0f, 11.0f);
                mutationDmg = rand.triangle(0.15f, 0.25f);
                mutationSpeed = rand.triangle(0.075f, 0.175f);
            }
            case PACIFIST -> {
                mutationHp = rand.triangle(11.0f, 15.0f);
                mutationDmg = rand.triangle(-0.20f, 0.30f);
                mutationSpeed = rand.triangle(-0.15f, 0.20f);
            }
        }

        // Check if breeding is inbred (parent-offspring or siblings sharing at least one parent)
        boolean inbred = betterdogs$checkInbreeding(parent1, parent2, d1, d2);

        // Genetic Recovery: if breeding is NOT inbred, but a parent was inbred,
        // we use their reconstructed (original) stats for the inheritance calculation.
        if (!inbred) {
            if (d1.inbred()) {
                parent1Hp = (parent1Hp + 6.0f) / 0.6f;
                parent1Dmg = (parent1Dmg + 0.20f) / 0.6f;
                parent1Speed = (parent1Speed + 0.15f) / 0.6f;
            }
            if (d2.inbred()) {
                parent2Hp = (parent2Hp + 6.0f) / 0.6f;
                parent2Dmg = (parent2Dmg + 0.20f) / 0.6f;
                parent2Speed = (parent2Speed + 0.15f) / 0.6f;
            }
        }

        // Apply inheritance formula: Average of parents + mutation
        float avgHp = (parent1Hp + parent2Hp + mutationHp) / 3.0f;
        float avgDmg = (parent1Dmg + parent2Dmg + mutationDmg) / 3.0f;
        float avgSpeed = (parent1Speed + parent2Speed + mutationSpeed) / 3.0f;

        float finalHp;
        float finalDmg;
        float finalSpeed;

        if (inbred) {
            // Apply severe genetic penalties: 0.6x multiplier + flat penalties
            finalHp = avgHp * 0.6f - 6.0f;
            finalDmg = avgDmg * 0.6f - 0.20f;
            finalSpeed = avgSpeed * 0.6f - 0.15f;
        } else {
            finalHp = avgHp;
            finalDmg = avgDmg;
            finalSpeed = avgSpeed;
        }

        // Clamp final modifiers to protect against excessive penalties and ensure stability
        finalHp = Math.max(finalHp, -30.0f);
        finalDmg = Math.max(finalDmg, -0.8f);
        finalSpeed = Math.max(finalSpeed, -0.6f);

        // Write stats to offspring
        babyExt.betterdogs$setHealthBonus(finalHp);
        babyExt.betterdogs$setDamageMod(finalDmg);
        babyExt.betterdogs$setSpeedMod(finalSpeed);
        babyExt.betterdogs$setStatsRolled(true);

        // Persist kinship data (parents UUIDs and inbred flag)
        WolfPersistentData.setPersistedParentsAndInbred(baby, parent1.getUUID(), parent2.getUUID(), inbred);

        if (inbred) {
            net.minecraft.server.level.ServerPlayer player = parent1.getLoveCause();
            if (player == null) {
                player = parent2.getLoveCause();
            }
            if (player != null) {
                net.vanillaoutsider.betterdogs.BetterDogs.INBRED_WOLF.trigger(player);
            }
        } else if (d1.inbred() || d2.inbred()) {
            net.minecraft.server.level.ServerPlayer player = parent1.getLoveCause();
            if (player == null) {
                player = parent2.getLoveCause();
            }
            if (player != null) {
                net.vanillaoutsider.betterdogs.BetterDogs.OUTCROSS_WOLF.trigger(player);
            }
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

    @Unique
    private boolean betterdogs$checkInbreeding(Wolf parent1, Wolf parent2, WolfPersistentData d1, WolfPersistentData d2) {
        UUID u1 = parent1.getUUID();
        UUID u2 = parent2.getUUID();

        UUID p1_1 = d1.parent1Uuid().orElse(null);
        UUID p1_2 = d1.parent2Uuid().orElse(null);

        UUID p2_1 = d2.parent1Uuid().orElse(null);
        UUID p2_2 = d2.parent2Uuid().orElse(null);

        // Parent-offspring check
        if (u1.equals(p2_1) || u1.equals(p2_2) || u2.equals(p1_1) || u2.equals(p1_2)) {
            return true;
        }

        // Sibling check (sharing at least one parent)
        if (p1_1 != null && (p1_1.equals(p2_1) || p1_1.equals(p2_2))) {
            return true;
        }
        if (p1_2 != null && (p1_2.equals(p2_1) || p1_2.equals(p2_2))) {
            return true;
        }

        return false;
    }
}
