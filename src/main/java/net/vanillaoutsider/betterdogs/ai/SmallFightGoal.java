package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;

import java.util.EnumSet;

/**
 * AI Goal for the "Small Fight" event.
 * Wolves will target their assigned partner and attack them.
 * This relies on WolfCombatHooks to prevent lethal damage.
 */
public class SmallFightGoal extends Goal {

    private final Wolf wolf;
    private LivingEntity target;

    public SmallFightGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); // Don't block JUMP?
    }

    @Override
    public boolean canUse() {
        if (!(this.wolf instanceof WolfExtensions ext)) return false;

        // Trigger only if Social Mode is active AND action is PLAY_FIGHT
        if (ext.betterdogs$isSocialModeActive() && ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.PLAY_FIGHT) {
            this.target = ext.betterdogs$getSocialTarget();
            return this.target != null && this.target.isAlive();
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (!(this.wolf instanceof WolfExtensions ext)) return false;
        
        // Stop if social mode ended (Timer expired)
        if (!ext.betterdogs$isSocialModeActive()) return false;
        if (ext.betterdogs$getSocialAction() != WolfExtensions.SocialAction.PLAY_FIGHT) return false;
        
        return this.target != null && this.target.isAlive();
    }

    @Override
    public void start() {
        // Clear normal target to prevent confusion? 
        // No, keep it clean.
    }

    @Override
    public void stop() {
        this.target = null;
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.target == null) return;

        this.wolf.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        this.wolf.getNavigation().moveTo(this.target, 1.0D); // Normal speed

        // Attack Logic
        // Check distance
        double distSqr = this.wolf.distanceToSqr(this.target);
        double reachSqr = this.getAttackReachSqr(this.target);
        
        if (distSqr <= reachSqr) {
            this.wolf.doHurtTarget(this.wolf.level(), this.target);
            // No need for cooldown? Vanilla attack has cooldown.
        }
    }
    
    // Copied from MeleeAttackGoal
    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return (double)(this.wolf.getBbWidth() * 2.0F * this.wolf.getBbWidth() * 2.0F + attackTarget.getBbWidth());
    }
}
