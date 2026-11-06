// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.ai;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.dasik.social.api.group.GroupMember;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * AI Goal for Aggressive personality wolves.
 * Proactively stalks and targets approaching hostile mobs within an expanded 16-block detection perimeter,
 * emitting intimidating growls and alert particles upon acquisition.
 */
public class AggressiveTargetGoal extends NearestAttackableTargetGoal<Monster> {

    public static final double DEFAULT_DETECT_RANGE = 16.0D;
    public static final double GUARD_AGGRO_RANGE = 24.0D;
    public static final double GUARD_NORMAL_RANGE = 16.0D;

    private final Wolf wolf;
    private int simDistRefreshTimer = 0;
    private int cachedSimDist = 10;

    public AggressiveTargetGoal(Wolf wolf) {
        super(wolf, Monster.class, 10, true, false, null);
        this.wolf = wolf;
        this.targetConditions.selector((target, level) -> isValidTarget(target));
    }

    public static boolean isEligibleTarget(Wolf wolf, LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (target instanceof Creeper) {
            return false;
        }
        try {
            if (target.getType().getDescriptionId().contains("warden")) {
                return false;
            }
        } catch (Throwable ignored) {
            // Null-safe headless test environment
        }

        if (wolf != null) {
            if (wolf.isBaby() || wolf.isOrderedToSit() || wolf.isInSittingPose()) {
                return false;
            }
            if (wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
                BlockPos guardPos = ext.betterdogs$getGuardPos();
                if (guardPos != null) {
                    double maxRange = ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE ? GUARD_AGGRO_RANGE : GUARD_NORMAL_RANGE;
                    return target.distanceToSqr(guardPos.getX() + 0.5, guardPos.getY() + 0.5, guardPos.getZ() + 0.5) <= (maxRange * maxRange);
                }
            }
            LivingEntity anchor = wolf.getOwner();
            if (anchor != null) {
                int detectRange = (int) DEFAULT_DETECT_RANGE;
                if (wolf.level() != null) {
                    try {
                        detectRange = DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_AGGRO_DETECT_RANGE);
                    } catch (Throwable ignored) {
                        detectRange = (int) DEFAULT_DETECT_RANGE;
                    }
                }
                return target.distanceToSqr(anchor) <= (double) (detectRange * detectRange);
            }
        }
        return true;
    }

    private boolean isValidTarget(LivingEntity target) {
        if (this.wolf == null || target == null || !target.isAlive()) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
            BlockPos guardPos = ext.betterdogs$getGuardPos();
            if (guardPos == null) {
                return false;
            }

            // Line of sight check to avoid targeting mobs through floors/caves
            if (!this.wolf.getSensing().hasLineOfSight(target)) {
                return false;
            }

            double maxRange = ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE ? GUARD_AGGRO_RANGE : GUARD_NORMAL_RANGE;
            if (target.distanceToSqr(guardPos.getX() + 0.5, guardPos.getY() + 0.5, guardPos.getZ() + 0.5) > (maxRange * maxRange)) {
                return false;
            }

            return !isExemptMonster(target);
        }

        // Must have an anchor (Owner or Leader)
        LivingEntity anchor = getAnchor();
        if (anchor == null) {
            return false;
        }

        // Dynamic Simulation Cap for detection range
        double maxRange = DEFAULT_DETECT_RANGE;
        if (this.wolf.level() != null) {
            try {
                maxRange = DynamicGameRuleManager.getInt(this.wolf.level(), BetterDogsGameRules.BD_AGGRO_DETECT_RANGE);
                BetterDogsConfig config = BetterDogsConfig.get();
                if (maxRange > 16.0) {
                    double safeLimit = (this.cachedSimDist * 16.0) - config.getAggressiveDetectionBuffer();
                    if (maxRange > safeLimit) {
                        maxRange = safeLimit;
                    }
                }
            } catch (Throwable ignored) {
                maxRange = DEFAULT_DETECT_RANGE;
            }
        }

        if (!this.wolf.isBaby() && target.distanceTo(anchor) > maxRange) {
            return false;
        }

        return !isExemptMonster(target);
    }

    private static boolean isExemptMonster(LivingEntity target) {
        if (target instanceof Creeper) {
            return true;
        }
        try {
            return target.getType().getDescriptionId().contains("warden");
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Override
    public boolean canUse() {
        if (this.wolf == null || this.wolf.isBaby() || this.wolf.isOrderedToSit() || this.wolf.isInSittingPose()) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
            if (ext.betterdogs$isSittingManually()) {
                return false;
            }

            WolfPersonality personality = ext.betterdogs$getPersonality();
            if (personality != WolfPersonality.AGGRESSIVE && personality != WolfPersonality.NORMAL) {
                return false;
            }

            return super.canUse();
        }

        LivingEntity anchor = getAnchor();
        if (anchor == null) {
            return false;
        }

        boolean isWildEnabled = false;
        if (this.wolf.level() != null) {
            try {
                isWildEnabled = DynamicGameRuleManager.getBoolean(this.wolf.level(), BetterDogsGameRules.BD_WILD_PERSONALITY_BEHAVIOR);
            } catch (Throwable ignored) {
                isWildEnabled = false;
            }
        }

        if (!this.wolf.isTame() && (!isWildEnabled || !(this.wolf instanceof GroupMember member) || member.getLeader() == null)) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getPassiveOverrideTicks() > 0) {
                return false;
            }
            if (ext.betterdogs$getPersonality() != WolfPersonality.AGGRESSIVE) {
                return false;
            }
        } else {
            return false;
        }

        // Refresh Simulation Distance Cache
        if (--this.simDistRefreshTimer <= 0) {
            this.simDistRefreshTimer = 100;
            if (anchor.level() != null && anchor.level().getServer() != null) {
                this.cachedSimDist = anchor.level().getServer().getPlayerList().getSimulationDistance();
            }
        }

        double chaseDist = 16.0D;
        if (this.wolf.level() != null) {
            try {
                chaseDist = DynamicGameRuleManager.getInt(this.wolf.level(), BetterDogsGameRules.BD_AGGRO_CHASE_DIST);
                double safeLimit = (this.cachedSimDist * 16.0) - BetterDogsConfig.get().getAggressiveDetectionBuffer();
                if (chaseDist > safeLimit) {
                    chaseDist = safeLimit;
                }
            } catch (Throwable ignored) {
                chaseDist = 16.0D;
            }
        }

        if (this.wolf.distanceTo(anchor) > chaseDist) {
            return false;
        }

        return super.canUse();
    }

    private LivingEntity getAnchor() {
        if (this.wolf == null) {
            return null;
        }
        if (this.wolf.isTame()) {
            return this.wolf.getOwner();
        } else if (this.wolf instanceof GroupMember member) {
            return member.getLeader();
        }
        return null;
    }

    @Override
    public void start() {
        super.start();
        playGrowlFeedback();
    }

    private void playGrowlFeedback() {
        if (this.wolf == null || this.wolf.level() == null || this.wolf.level().isClientSide()) {
            return;
        }
        if (this.wolf.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(
                    null,
                    this.wolf.getX(),
                    this.wolf.getY(),
                    this.wolf.getZ(),
                    SoundEvents.WOLF_GROWL_BABY.value(),
                    SoundSource.NEUTRAL,
                    1.0F,
                    (this.wolf.isBaby() ? 1.0F : 0.7F) + this.wolf.getRandom().nextFloat() * 0.15F
            );
            if (this.target != null) {
                serverLevel.sendParticles(
                        ParticleTypes.ANGRY_VILLAGER,
                        this.wolf.getX(),
                        this.wolf.getY() + 0.8,
                        this.wolf.getZ(),
                        2,
                        0.2,
                        0.2,
                        0.2,
                        0.0
                );
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf == null || this.wolf.isOrderedToSit() || this.wolf.isInSittingPose()) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
            if (ext.betterdogs$isSittingManually()) {
                return false;
            }

            BlockPos guardPos = ext.betterdogs$getGuardPos();
            if (guardPos == null) {
                return false;
            }

            LivingEntity activeTarget = this.wolf.getTarget();
            if (activeTarget == null || !activeTarget.isAlive()) {
                return false;
            }

            double targetDistFromPostSqr = activeTarget.distanceToSqr(guardPos.getX() + 0.5, guardPos.getY() + 0.5, guardPos.getZ() + 0.5);
            double maxChase = ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE ? 1024.0 : 400.0;
            if (targetDistFromPostSqr > maxChase) {
                return false;
            }

            return super.canContinueToUse();
        }

        LivingEntity anchor = getAnchor();
        if (anchor == null) {
            return false;
        }

        double chaseDist = 16.0D;
        if (this.wolf.level() != null) {
            try {
                chaseDist = DynamicGameRuleManager.getInt(this.wolf.level(), BetterDogsGameRules.BD_AGGRO_CHASE_DIST);
                double safeLimit = (this.cachedSimDist * 16.0) - BetterDogsConfig.get().getAggressiveDetectionBuffer();
                if (chaseDist > safeLimit) {
                    chaseDist = safeLimit;
                }
            } catch (Throwable ignored) {
                chaseDist = 16.0D;
            }
        }

        if (this.wolf.distanceTo(anchor) > chaseDist) {
            return false;
        }

        return super.canContinueToUse();
    }
}
