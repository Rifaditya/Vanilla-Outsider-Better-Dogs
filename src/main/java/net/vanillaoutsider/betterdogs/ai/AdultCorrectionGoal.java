package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.minecraft.server.level.ServerLevel;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.BetterDogs;

public class AdultCorrectionGoal extends Goal {

    private final Wolf wolf;
    private Wolf offendingBaby;
    private boolean hasHit;

    public AdultCorrectionGoal(Wolf wolf) {
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        if (wolf.isBaby() || !wolf.isTame()) return false;
        if (wolf.isOrderedToSit()) return false;
        if (!(wolf instanceof WolfExtensions ext)) return false;
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
    }

    @Override
    public boolean canContinueToUse() {
        if (hasHit) return false;
        if (offendingBaby == null || !offendingBaby.isAlive()) return false;
        if (wolf instanceof WolfExtensions ext && !ext.betterdogs$isSocialModeActive()) return false;
        return true;
    }

    @Override
    public void tick() {
        if (offendingBaby == null) return;
        BetterDogsConfig config = BetterDogsConfig.get();
        this.wolf.getLookControl().setLookAt(offendingBaby, config.getCorrectionLookSpeed(), config.getCorrectionLookSpeed());
        this.wolf.getNavigation().moveTo(offendingBaby, config.getCorrectionSpeedModifier());
        double distSqr = this.wolf.distanceToSqr(offendingBaby);
        double reachSqr = (double)(this.wolf.getBbWidth() * 2.0F * this.wolf.getBbWidth() * 2.0F + offendingBaby.getBbWidth());
        reachSqr += config.getCorrectionReachBuffer();
        if (distSqr <= reachSqr) {
            this.wolf.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            if (this.wolf.level() instanceof ServerLevel serverLevel) {
                boolean success = this.wolf.doHurtTarget(serverLevel, offendingBaby);
                if (success) {
                    hasHit = true;
                    onHitBaby();
                }
            }
        }
    }

    private void onHitBaby() {
        float chance = BetterDogsGameRules.getChance(wolf.level(), BetterDogsGameRules.BD_BLOOD_FEUD_PERCENT);
        if (this.wolf.getRandom().nextFloat() < chance) {
            if (this.offendingBaby instanceof WolfExtensions babyExt) {
                babyExt.betterdogs$setBloodFeudTarget(this.wolf.getStringUUID());
            }
            if (this.wolf instanceof WolfExtensions myExt) {
                myExt.betterdogs$setBloodFeudTarget(this.offendingBaby.getStringUUID());
            }
            BetterDogs.LOGGER.info("BLOOD FEUD DECLARE: Adult " + this.wolf.getUUID() + " vs Baby " + this.offendingBaby.getUUID());
        }
    }

    @Override
    public void stop() {
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
        if (offendingBaby instanceof WolfExtensions babyExt) {
             babyExt.betterdogs$setBeingDisciplined(false);
        }
        this.offendingBaby = null;
        this.hasHit = false;
        wolf.getNavigation().stop();
    }
}
