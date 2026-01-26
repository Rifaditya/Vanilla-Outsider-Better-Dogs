package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.minecraft.server.level.ServerLevel;
import net.vanillaoutsider.betterdogs.BetterDogs;
// In 1.21.11 project, likely BetterDogs.kt or Java. Checking for logger access.
// If BetterDogs.LOGGER is not available, System.out usually works or no log.
// But wait, WolfMixin usually has logger.

public class AdultCorrectionGoal extends Goal {

    private final Wolf wolf;
    private Wolf offendingBaby;
    private boolean hasHit;

    public AdultCorrectionGoal(Wolf wolf) {
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        if (wolf.isBaby() || !wolf.isTame())
            return false;
        if (wolf.isOrderedToSit())
            return false;
        if (!(wolf instanceof WolfExtensions))
            return false;

        WolfExtensions ext = (WolfExtensions) wolf;

        if (ext.betterdogs$isSocialModeActive()
                && ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.DISCIPLINE) {
            LivingEntity target = ext.betterdogs$getSocialTarget();
            if (target instanceof Wolf) {
                this.offendingBaby = (Wolf) target;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.hasHit = false;
    }

    @Override
    public boolean canContinueToUse() {
        if (hasHit)
            return false;
        if (offendingBaby == null || !offendingBaby.isAlive())
            return false;
        if (wolf instanceof WolfExtensions) {
            WolfExtensions ext = (WolfExtensions) wolf;
            if (!ext.betterdogs$isSocialModeActive())
                return false;
        }
        return true;
    }

    @Override
    public void tick() {
        if (offendingBaby == null)
            return;
        BetterDogsConfig config = BetterDogsConfig.Companion.get();
        // Using float look speed from config
        this.wolf.getLookControl().setLookAt(offendingBaby, config.getCorrectionLookSpeed(),
                config.getCorrectionLookSpeed());
        this.wolf.getNavigation().moveTo(offendingBaby, config.getCorrectionSpeedModifier());

        double distSqr = this.wolf.distanceToSqr(offendingBaby);
        double reachSqr = (double) (this.wolf.getBbWidth() * 2.0F * this.wolf.getBbWidth() * 2.0F
                + offendingBaby.getBbWidth());
        reachSqr += config.getCorrectionReachBuffer();

        if (distSqr <= reachSqr) {
            this.wolf.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            if (this.wolf.level() instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel) this.wolf.level();
                boolean success = this.wolf.doHurtTarget(serverLevel, offendingBaby);
                if (success) {
                    hasHit = true;
                    onHitBaby();
                }
            }
        }
    }

    private void onHitBaby() {
        // Config chance for blood feud (int 0-100)
        int chancePct = BetterDogsConfig.Companion.get().getBloodFeudChance();
        float chance = chancePct / 100.0f;

        if (this.wolf.getRandom().nextFloat() < chance) {
            if (this.offendingBaby instanceof WolfExtensions) {
                WolfExtensions babyExt = (WolfExtensions) this.offendingBaby;
                babyExt.betterdogs$setBloodFeudTarget(this.wolf.getStringUUID());
            }
            if (this.wolf instanceof WolfExtensions) {
                WolfExtensions myExt = (WolfExtensions) this.wolf;
                myExt.betterdogs$setBloodFeudTarget(this.offendingBaby.getStringUUID());
            }
            // BetterDogs.LOGGER.info("BLOOD FEUD DECLARE");
        }
    }

    @Override
    public void stop() {
        if (wolf instanceof WolfExtensions) {
            WolfExtensions ext = (WolfExtensions) wolf;
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
        if (offendingBaby instanceof WolfExtensions) {
            WolfExtensions babyExt = (WolfExtensions) offendingBaby;
            babyExt.betterdogs$setBeingDisciplined(false);
        }
        this.offendingBaby = null;
        this.hasHit = false;
        wolf.getNavigation().stop();
    }
}
