package net.vanillaoutsider.betterdogs.ai;

import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

/**
 * AI Goal: Aggressive adult corrects a baby wolf that is attacking the owner.
 * One hit, then may trigger blood feud (configurable 5% chance).
 */
public class AdultCorrectionGoal extends Goal {

    private final Wolf wolf;
    private Wolf offendingBaby;
    private boolean hasHit;

    public AdultCorrectionGoal(Wolf wolf) {
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        // Must be adult, tamed, aggressive
        if (wolf.isBaby() || !wolf.isTame())
            return false;
            
        if (wolf.isOrderedToSit())
            return false;

        if (!(wolf instanceof WolfExtensions ext))
            return false;

        // EVENT-DRIVEN TRIGGER:
        // We only run if the Event System has specifically assigned us a DISCIPLINE task.
        if (ext.betterdogs$isSocialModeActive() && ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.DISCIPLINE) {
            LivingEntity target = ext.betterdogs$getSocialTarget();
            if (target instanceof Wolf baby) {
                this.offendingBaby = baby;
                return true;
            }
        }

        return false;
    }



    @Override
    public void start() {
        this.hasHit = false;
        // Event System (CorrectionEvent) handles the Social State setup.
        // We just execute the violence.
    }

    @Override
    public boolean canContinueToUse() {
        // Stop after one hit
        if (hasHit)
            return false;

        if (offendingBaby == null || !offendingBaby.isAlive())
            return false;
            
        // [V2] Verify Social Mode is still ticking
        if (wolf instanceof WolfExtensions ext && !ext.betterdogs$isSocialModeActive()) {
            return false;
        }

        return true;
    }

    @Override
    public void tick() {
        if (offendingBaby == null) return;

        this.wolf.getLookControl().setLookAt(offendingBaby, 30.0F, 30.0F);
        this.wolf.getNavigation().moveTo(offendingBaby, 1.0D);

        // Attack Logic
        double distSqr = this.wolf.distanceToSqr(offendingBaby);
        double reachSqr = (double)(this.wolf.getBbWidth() * 2.0F * this.wolf.getBbWidth() * 2.0F + offendingBaby.getBbWidth());
        
        // Minor reach buffer
        reachSqr += 1.0D;

        if (distSqr <= reachSqr) {
            this.wolf.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            
            // [V2] BITE LOGIC: Gatekeeper allows this because SocialTarget matches.
            
            if (this.wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                boolean success = this.wolf.doHurtTarget(serverLevel, offendingBaby);
                if (success) {
                    hasHit = true;
                    onHitBaby();
                }
            }
        }
    }

    private void onHitBaby() {
        // [V2] Blood Feud Trigger
        // 5% chance (default) to start a permanent vendetta
        if (this.wolf.getRandom().nextFloat() < (BetterDogsConfig.get().getBloodFeudChance() / 100.0f)) {
            if (this.offendingBaby instanceof WolfExtensions babyExt) {
                babyExt.betterdogs$setBloodFeudTarget(this.wolf.getStringUUID());
            }
            if (this.wolf instanceof WolfExtensions myExt) {
                myExt.betterdogs$setBloodFeudTarget(this.offendingBaby.getStringUUID());
            }
            net.vanillaoutsider.betterdogs.BetterDogs.LOGGER.info("BLOOD FEUD DECLARE: Adult " + this.wolf.getUUID() + " vs Baby " + this.offendingBaby.getUUID());
        }
    }

    @Override
    public void stop() {
        // [V2] CLEANUP:
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0); // Unlock Master Brain
        }
        
        if (offendingBaby instanceof WolfExtensions babyExt) {
             babyExt.betterdogs$setBeingDisciplined(false); // Remove Dunce Cap
        }
        
        // wolf.setTarget(null); // Not needed, Gatekeeper handled it.
        this.offendingBaby = null;
        this.hasHit = false;
        wolf.getNavigation().stop();
    }
}
