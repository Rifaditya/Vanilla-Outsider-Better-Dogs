package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

/**
 * AI Goal for Aggressive personality wolves.
 * Automatically targets hostile mobs near the owner.
 */
public class AggressiveTargetGoal extends NearestAttackableTargetGoal<Monster> {

    private final Wolf wolf;

    public AggressiveTargetGoal(Wolf wolf) {
        super(wolf, Monster.class, 10, true, false, (target, level) -> isValidTarget(wolf, target));
        this.wolf = wolf;
    }

    private static boolean isValidTarget(Wolf wolf, LivingEntity target) {
        // Must have an owner
        LivingEntity owner = wolf.getOwner();
        if (owner == null)
            return false;

        // Target must be within range of owner (Adults only)
        if (!wolf.isBaby() && target.distanceTo(owner) > BetterDogsConfig.get().aggressiveDetectionRange)
            return false;

        // Don't attack creepers
        if (target instanceof Creeper)
            return false;

        // Don't attack the warden (too dangerous)
        // Check resource location path for "warden"
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

        if (!wolf.isBaby() && wolf.distanceTo(owner) > BetterDogsConfig.get().aggressiveChaseDistance)
            return false;

        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        // Stop if too far from owner
        LivingEntity owner = wolf.getOwner();
        if (owner == null)
            return false;

        if (!wolf.isBaby() && wolf.distanceTo(owner) > BetterDogsConfig.get().aggressiveChaseDistance)
            return false;

        return super.canContinueToUse();
    }
}
