package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.scheduler.events.CorrectionDogEvent;
import net.minecraft.world.phys.AABB;
import java.util.List;

/**
 * AI Goal: Aggressive baby bites owner ONCE when provoked (retaliationTarget set).
 * After one attack, target is cleared and goal stops.
 */
public class BabyBiteBackGoal extends Goal {

    private final Wolf wolf;
    private LivingEntity retaliationTarget;
    private boolean hasAttacked;
    private int attackDelay;

    public BabyBiteBackGoal(Wolf wolf) {
        this.wolf = wolf;
        // CLAIM MUTEX to prevent Standard Melee/Wander/etc from running
        this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Must be baby, tamed, aggressive
        if (!wolf.isBaby() || !wolf.isTame())
            return false;

        if (wolf.isOrderedToSit())
            return false;

        if (!(wolf instanceof WolfExtensions ext))
            return false;

        if (ext.betterdogs$getPersonality() != WolfPersonality.AGGRESSIVE)
            return false;

        // PRIORITY CHECK: Yield to Blood Feud
        if (ext.betterdogs$hasBloodFeud())
            return false;
            
        // [V2] SAFETY CHECK: If Social Mode is ALREADY active (e.g. Correction), don't interrupt.
        // Wait for the chain reaction hierarchy.
        // [V2] SAFETY CHECK: If Social Mode is ALREADY active (e.g. Correction), don't interrupt.
        // Wait for the chain reaction hierarchy.
        // EXCEPTION: If the event IS Retaliation (set by the Event System), we MUST run!
        if (ext.betterdogs$isSocialModeActive()) {
            if (ext.betterdogs$getSocialAction() != WolfExtensions.SocialAction.RETALIATION) {
                 return false;
            }
        }

        // TRIGGER: Check if the Social Target has been set (by provocation injection in Mixin)
        LivingEntity socialTarget = ext.betterdogs$getSocialTarget();
        WolfExtensions.SocialAction socialAction = ext.betterdogs$getSocialAction();
        LivingEntity owner = wolf.getOwner();
        
        // STRICT CONTEXT: Only activate if the Action is RETALIATION
        if (socialTarget != null && socialTarget == owner && socialAction == WolfExtensions.SocialAction.RETALIATION) {
            this.retaliationTarget = owner;
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        this.hasAttacked = false;
        this.attackDelay = 0;
        
        // [V2] LOCKDOWN: Confirm Social Mode with a specific duration (100 ticks / 5 sec failsafe)
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(this.retaliationTarget, WolfExtensions.SocialAction.RETALIATION, 100);
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (hasAttacked) return false;
        if (retaliationTarget == null || !retaliationTarget.isAlive()) return false;
        
        // [V2] Verify the Social System is still active
        if (wolf instanceof WolfExtensions ext && !ext.betterdogs$isSocialModeActive()) {
            return false;
        }

        return true;
    }

    @Override
    public void tick() {
        if (retaliationTarget == null) return;

        this.wolf.getLookControl().setLookAt(retaliationTarget, 30.0F, 30.0F);
        this.wolf.getNavigation().moveTo(retaliationTarget, 1.2D);
        this.attackDelay = Math.max(this.attackDelay - 1, 0);

        // Attack Logic - INCREASED REACH (approx 2 blocks + widths)
        double distSqr = this.wolf.distanceToSqr(retaliationTarget);
        double reachSqr = (double)(this.wolf.getBbWidth() * 2.0F * this.wolf.getBbWidth() * 2.0F + retaliationTarget.getBbWidth());
        
        // TUNE: Add a flat buffer to ensure bite lands even if collision stops movement early
        reachSqr += 4.0D; 

        if (distSqr <= reachSqr) {
            if (this.attackDelay <= 0) {
                this.attackDelay = 20;
                this.wolf.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                
                // [V2] BITE LOGIC:
                // No need for transient switch hacks anymore!
                // The Gatekeeper (Mixin) sees "Social Active + Target Matches", so it allows setTarget/doHurtTarget calls.
                
                if (this.wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                     // We pass the Social Target, which the Gatekeeper approves.
                     boolean success = this.wolf.doHurtTarget(serverLevel, retaliationTarget);
                     
                     if (success) {
                         this.hasAttacked = true;
                         
                         // [V3] ADULT CORRECTION TRIGGER:
                         // "I just bit the owner. I am guilty."
                         // Find ONE nearby Enforcer to punish me.
                         AABB searchBox = this.wolf.getBoundingBox().inflate(10.0);
                         List<Wolf> nearbyWolves = this.wolf.level().getEntitiesOfClass(
                                 Wolf.class,
                                 searchBox,
                                 w -> w != this.wolf && !w.isBaby() && w.isTame() && w.getOwner() == this.wolf.getOwner()
                         );
                         
                         for (Wolf adult : nearbyWolves) {
                             if (adult instanceof WolfExtensions adultExt) {
                                 // Requirement: Enforcer must be AGGRESSIVE and NOT busy.
                                 if (adultExt.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE 
                                     && !adultExt.betterdogs$isSocialModeActive()) {
                                     
                                     // Found Enforcer! INJECT BEHAVIOR.
                                     // 100 ticks = 5 seconds to deliver the discipline stick.
                                     adultExt.betterdogs$getScheduler().injectBehavior(
                                         CorrectionDogEvent.ID, 
                                         100, 
                                         this.wolf // Context = Me (The Sinner)
                                     );
                                     break; // Only ONE enforcer needs to respond. No gang-ups.
                                 }
                             }
                         }
                     }
                }
            }
        }
    }

    @Override
    public void stop() {
        // [V2] CLEANUP: Explicitly unlock the Master Brain
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0); // Clears target -> Gatekeeper unlocks Main Brain
        }
        
        wolf.getNavigation().stop();
        this.retaliationTarget = null;
        this.hasAttacked = false;
    }
}
