package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * AI Goal for Aggressive personality wolves.
 * Automatically targets hostile mobs near the owner.
 */
public class AggressiveTargetGoal extends NearestAttackableTargetGoal<Monster> {

    private final Wolf wolf;
    private int simDistRefreshTimer = 0;
    private int cachedSimDist = 10;

    public AggressiveTargetGoal(Wolf wolf) {
        super(wolf, Monster.class, 10, true, false, null);
        this.wolf = wolf;
        this.targetConditions.selector((target, level) -> isValidTarget(target));
    }

    private boolean isValidTarget(LivingEntity target) {
        // Must have an owner
        LivingEntity owner = wolf.getOwner();
        if (owner == null)
            return false;

        // Dynamic Simulation Cap for detection range
        BetterDogsConfig config = BetterDogsConfig.get();
        // Use Game Rule default
        double maxRange = BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_AGGRO_DETECT_RANGE);
        if (maxRange > 16.0) {
            double safeLimit = (cachedSimDist * 16.0) - config.getAggressiveDetectionBuffer();
            if (maxRange > safeLimit) {
                maxRange = safeLimit;
            }
        }

        // Target must be within range of owner (Adults only)
        if (!wolf.isBaby() && target.distanceTo(owner) > maxRange)
            return false;

        // Don't attack creepers
        if (target instanceof Creeper)
            return false;

        // Don't attack the warden (too dangerous)
        return !target.getType().getDescriptionId().contains("warden");
    }

    @Override
    public boolean canUse() {
        // Only for tamed wolves with Aggressive personality
        if (!wolf.isTame())
            return false;

        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getPersonality() != WolfPersonality.AGGRESSIVE)
                return false;
        } else {
            return false;
        }

        // Must have an owner nearby
        LivingEntity owner = wolf.getOwner();
        if (owner == null)
            return false;

        // Refresh Simulation Distance Cache (every 5 seconds)
        if (--this.simDistRefreshTimer <= 0) {
            this.simDistRefreshTimer = 100;
            if (owner.level().getServer() != null) {
                this.cachedSimDist = owner.level().getServer().getPlayerList().getSimulationDistance();
            }
        }

        double chaseDist = BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_AGGRO_CHASE_DIST);
        if (chaseDist > 16.0) {
            double safeLimit = (cachedSimDist * 16.0) - BetterDogsConfig.get().getAggressiveDetectionBuffer();
            if (chaseDist > safeLimit) {
                chaseDist = safeLimit;
            }
        }

        if (!wolf.isBaby() && wolf.distanceTo(owner) > chaseDist)
            return false;

        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        // Stop if too far from owner
        LivingEntity owner = wolf.getOwner();
        if (owner == null)
            return false;

        double chaseDist = BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_AGGRO_CHASE_DIST);
        if (chaseDist > 16.0) {
            double safeLimit = (cachedSimDist * 16.0) - BetterDogsConfig.get().getAggressiveDetectionBuffer();
            if (chaseDist > safeLimit) {
                chaseDist = safeLimit;
            }
        }

        if (!wolf.isBaby() && wolf.distanceTo(owner) > chaseDist)
            return false;

        return super.canContinueToUse();
    }
}
