// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.ai;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.AdultDisciplineHelper;
import net.vanillaoutsider.betterdogs.util.WolfDebugLogger;

/**
 * AI Goal executing adult wolf correction/discipline on misbehaving puppies.
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
        if (this.wolf == null || this.wolf.isBaby() || !this.wolf.isTame()) {
            return false;
        }
        if (this.wolf.isOrderedToSit() || this.wolf.isInSittingPose()) {
            return false;
        }
        if (!(this.wolf instanceof WolfExtensions ext)) {
            return false;
        }
        if (ext.betterdogs$isSocialModeActive() && ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.DISCIPLINE) {
            LivingEntity target = ext.betterdogs$getSocialTarget();
            if (target instanceof Wolf baby && AdultDisciplineHelper.canDiscipline(this.wolf, baby)) {
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
        if (this.hasHit) {
            return false;
        }
        if (this.offendingBaby == null || !this.offendingBaby.isAlive()) {
            return false;
        }
        return this.wolf instanceof WolfExtensions ext && ext.betterdogs$isSocialModeActive();
    }

    @Override
    public void tick() {
        if (this.offendingBaby == null) {
            return;
        }
        BetterDogsConfig config = BetterDogsConfig.get();
        this.wolf.getLookControl().setLookAt(this.offendingBaby, config.getCorrectionLookSpeed(), config.getCorrectionLookSpeed());
        this.wolf.getNavigation().moveTo(this.offendingBaby, config.getCorrectionSpeedModifier());

        double distSqr = this.wolf.distanceToSqr(this.offendingBaby);
        double reachSqr = (double) (this.wolf.getBbWidth() * 2.0F * this.wolf.getBbWidth() * 2.0F + this.offendingBaby.getBbWidth());
        reachSqr += config.getCorrectionReachBuffer();

        if (distSqr <= reachSqr) {
            this.wolf.swing(InteractionHand.MAIN_HAND);
            if (this.wolf.level() instanceof ServerLevel serverLevel) {
                boolean success = this.wolf.doHurtTarget(serverLevel, this.offendingBaby);
                if (success) {
                    this.hasHit = true;
                    AdultDisciplineHelper.applyDisciplineFeedback(this.wolf, this.offendingBaby, serverLevel);
                    onHitBaby();
                }
            }
        }
    }

    private void onHitBaby() {
        float baseChance = DynamicGameRuleManager.getChance(this.wolf.level(), BetterDogsGameRules.BD_BLOOD_FEUD_PERCENT);
        int affinity = 0;
        if (this.wolf instanceof WolfExtensions ext) {
            affinity = ext.betterdogs$getAffinity(this.offendingBaby.getStringUUID());
        }

        float chance = AdultDisciplineHelper.calculateBloodFeudChance(baseChance, affinity);

        if (this.wolf.getRandom().nextFloat() < chance) {
            if (this.offendingBaby instanceof WolfExtensions babyExt) {
                babyExt.betterdogs$setBloodFeudTarget(this.wolf.getStringUUID());
            }
            if (this.wolf instanceof WolfExtensions myExt) {
                myExt.betterdogs$setBloodFeudTarget(this.offendingBaby.getStringUUID());
            }
            WolfDebugLogger.log(this.wolf, "Social", "BLOOD FEUD DECLARE: Adult " + this.wolf.getUUID() + " vs Baby " + this.offendingBaby.getUUID() + " (Chance: " + chance + ")");
        }
    }

    @Override
    public void stop() {
        if (this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
        if (this.offendingBaby instanceof WolfExtensions babyExt) {
            babyExt.betterdogs$setBeingDisciplined(false);
        }
        this.offendingBaby = null;
        this.hasHit = false;
        this.wolf.getNavigation().stop();
    }
}
