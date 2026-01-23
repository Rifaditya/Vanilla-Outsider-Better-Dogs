package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Goal for domestic retaliation.
 * Used by baby wolves to strike back at owner 2 times.
 * Used by aggressive adults to intervene with 3 strikes.
 */
public class DomesticRetaliationGoal extends MeleeAttackGoal {

    private final Wolf wolf;

    public DomesticRetaliationGoal(Wolf wolf) {
        super(wolf, 1.2, true);
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getStrikesRemaining() <= 0) {
                return false;
            }
            
            // Re-target owner or current target if valid
            LivingEntity target = wolf.getTarget();
            if (target == null || !target.isAlive()) {
                ext.betterdogs$setStrikesRemaining(0);
                return false;
            }
            
            return super.canUse();
        }
        return false;
    }

    @Override
    public void stop() {
        super.stop();
        // Clear target if strikes are gone
        if (wolf instanceof WolfExtensions ext && ext.betterdogs$getStrikesRemaining() <= 0) {
            wolf.setTarget(null);
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (wolf instanceof WolfExtensions ext) {
            return ext.betterdogs$getStrikesRemaining() > 0 && super.canContinueToUse();
        }
        return false;
    }
}
