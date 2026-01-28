package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.scheduler.events.CorrectionDogEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.server.level.ServerLevel;
import java.util.List;

public class BabyBiteBackGoal extends Goal {

    private final Wolf wolf;
    private LivingEntity retaliationTarget;
    private boolean hasAttacked;
    private int attackDelay;

    public BabyBiteBackGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!wolf.isBaby() || !wolf.isTame()) return false;
        if (wolf.isOrderedToSit()) return false;
        if (!(wolf instanceof WolfExtensions ext)) return false;
        if (ext.betterdogs$getPersonality() != WolfPersonality.AGGRESSIVE) return false;
        if (ext.betterdogs$hasBloodFeud()) return false;
        if (ext.betterdogs$isSocialModeActive()) {
            if (ext.betterdogs$getSocialAction() != WolfExtensions.SocialAction.RETALIATION) {
                 return false;
            }
        }
        LivingEntity socialTarget = ext.betterdogs$getSocialTarget();
        WolfExtensions.SocialAction socialAction = ext.betterdogs$getSocialAction();
        LivingEntity owner = wolf.getOwner();
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
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(this.retaliationTarget, WolfExtensions.SocialAction.RETALIATION, BetterDogsConfig.get().getCorrectionDuration());
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (hasAttacked) return false;
        if (retaliationTarget == null || !retaliationTarget.isAlive()) return false;
        if (wolf instanceof WolfExtensions ext && !ext.betterdogs$isSocialModeActive()) return false;
        return true;
    }

    @Override
    public void tick() {
        if (retaliationTarget == null) return;
        BetterDogsConfig config = BetterDogsConfig.get();
        this.wolf.getLookControl().setLookAt(retaliationTarget, config.getBiteBackLookSpeed(), config.getBiteBackLookSpeed());
        this.wolf.getNavigation().moveTo(retaliationTarget, config.getBiteBackSpeedModifier());
        this.attackDelay = Math.max(this.attackDelay - 1, 0);
        double distSqr = this.wolf.distanceToSqr(retaliationTarget);
        double reachSqr = (double)(this.wolf.getBbWidth() * 2.0F * this.wolf.getBbWidth() * 2.0F + retaliationTarget.getBbWidth());
        reachSqr += config.getBiteBackReachBuffer(); 
        if (distSqr <= reachSqr) {
            if (this.attackDelay <= 0) {
                this.attackDelay = config.getBiteBackAttackDelay();
                this.wolf.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                if (this.wolf.level() instanceof ServerLevel serverLevel) {
                     boolean success = this.wolf.doHurtTarget(serverLevel, retaliationTarget);
                     if (success) {
                         this.hasAttacked = true;
                         double searchDist = config.getCorrectionSearchRange();
                         AABB searchBox = this.wolf.getBoundingBox().inflate(searchDist);
                         List<Wolf> nearbyWolves = this.wolf.level().getEntitiesOfClass(
                                 Wolf.class,
                                 searchBox,
                                 w -> w != this.wolf && !w.isBaby() && w.isTame() && w.getOwner() == this.wolf.getOwner()
                         );
                         for (Wolf adult : nearbyWolves) {
                             if (adult instanceof WolfExtensions adultExt) {
                                 if (adultExt.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE && !adultExt.betterdogs$isSocialModeActive()) {
                                     adultExt.betterdogs$getOrInitializeScheduler().injectBehavior(CorrectionDogEvent.ID, config.getCorrectionDuration(), this.wolf);
                                     break;
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
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
        wolf.getNavigation().stop();
        this.retaliationTarget = null;
        this.hasAttacked = false;
    }
}
