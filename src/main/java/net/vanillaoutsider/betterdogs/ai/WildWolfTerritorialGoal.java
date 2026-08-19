// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import java.util.EnumSet;
import net.dasik.social.api.group.GroupMember;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Wild Wolf Territorial Rivalry.
 * If two wild pack leaders meet within 96 blocks, they must either fight or retreat.
 * Outcome depends on personalities:
 * - Aggressive leaders will fight for dominance until one yields (merging packs).
 * - Pacifist/Normal leaders will retreat in opposite directions to maintain territory.
 */
public class WildWolfTerritorialGoal extends Goal {
    private final Wolf wolf;
    private LivingEntity rival;
    private double searchRadius = 96.0;
    private int cooldown = 0;
    private Behavior behavior = Behavior.STARE;
    private int behaviorTicks = 0;
    private boolean isFatal = false;
    private int queryThrottleTimer = 0;

    private enum Behavior {
        STARE,
        FIGHT,
        RETREAT
    }

    private final TargetingConditions targeting;

    public WildWolfTerritorialGoal(Wolf wolf) {
        this.wolf = wolf;
        this.targeting = TargetingConditions.forNonCombat().range(searchRadius).selector((living, level) -> {
            if (!(living instanceof Wolf other)) return false;
            if (other.isTame() || ((GroupMember)other).getLeader() != null || other == this.wolf) return false;
            
            // Exclusive Disputes Check (v3.3.1)
            if (DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_TERRITORIAL_EXCLUSIVE_DISPUTES)) {
                WolfExtensions otherExt = (WolfExtensions) other;
                // Rival is busy with someone else
                if (otherExt.betterdogs$getSocialAction() != WolfExtensions.SocialAction.NONE && otherExt.betterdogs$getSocialTarget() != this.wolf) {
                    if (DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_DEBUGGING)) {
                        net.vanillaoutsider.betterdogs.BetterDogs.LOGGER.info("[Territory Debug] " + this.wolf.getName().getString() + " skipped rival " + other.getName().getString() + " (REASON: Busy with " + (otherExt.betterdogs$getSocialTarget() != null ? otherExt.betterdogs$getSocialTarget().getName().getString() : "Unknown") + ")");
                    }
                    return false;
                }
            }
            return true;
        });
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!DynamicGameRuleManager.getBoolean(this.wolf.level(), BetterDogsGameRules.BD_TERRITORIAL_RIVALRY)) {
            return false;
        }
        if (this.wolf.isTame() || ((GroupMember)this.wolf).getLeader() != null) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$getSocialAction() != WolfExtensions.SocialAction.NONE) {
            return false;
        }
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }
        if (this.queryThrottleTimer > 0) {
            this.queryThrottleTimer--;
            return false;
        }

        // Reset query throttling timer for the next check (2-4 seconds / 40-80 ticks)
        this.queryThrottleTimer = 40 + this.wolf.getRandom().nextInt(41);

        if (this.wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            this.searchRadius = DynamicGameRuleManager.getInt(serverLevel, BetterDogsGameRules.BD_TERRITORIAL_SEARCH_RADIUS);
            this.rival = serverLevel.getNearestEntity(Wolf.class, this.targeting.range(this.searchRadius), this.wolf, this.wolf.getX(), this.wolf.getY(), this.wolf.getZ(), this.wolf.getBoundingBox().inflate(this.searchRadius));
            if (this.rival != null) {
                this.debugLog("Targeting rival leader: " + this.rival.getName().getString() + " at " + this.rival.position());
            }
        }
        return this.rival != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.rival == null || !this.rival.isAlive() || this.wolf.isTame() || ((GroupMember)this.wolf).getLeader() != null) {
            return false;
        }
        // Goal finishes if they are far enough apart after a retreat
        if (this.behavior == Behavior.RETREAT && this.wolf.distanceToSqr(this.rival) > searchRadius * searchRadius) {
            return false;
        }
        return this.behaviorTicks < 1200; // Max 1 minute conflict
    }

    @Override
    public void start() {
        this.behavior = Behavior.STARE;
        this.behaviorTicks = 0;
        this.wolf.getNavigation().stop();
        if (this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(this.rival, WolfExtensions.SocialAction.TERRITORIAL_DISPUTE, 1200);
        }
        if (this.rival instanceof WolfExtensions rivalExt) {
            rivalExt.betterdogs$setSocialState(this.wolf, WolfExtensions.SocialAction.TERRITORIAL_DISPUTE, 1200);
        }
    }

    @Override
    public void stop() {
        if (this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
        if (this.rival instanceof WolfExtensions rivalExt && rivalExt.betterdogs$getSocialTarget() == this.wolf) {
            rivalExt.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
        this.rival = null;
        int maxCooldown = DynamicGameRuleManager.getBoolean(this.wolf.level(), BetterDogsGameRules.BD_DEBUGGING) ? 20 : 400;
        this.cooldown = maxCooldown;
    }

    @Override
    public void tick() {
        this.behaviorTicks++;
        if (this.rival == null) return;

        this.wolf.getLookControl().setLookAt(this.rival, 30.0f, 30.0f);

        switch (this.behavior) {
            case STARE -> {
                if (this.behaviorTicks > 40) { // 2 seconds stare-down
                    this.decideOutcome();
                }
            }
            case FIGHT -> {
                this.wolf.getNavigation().moveTo(this.rival, 1.3);
                if (this.wolf.distanceToSqr(this.rival) < 4.0) {
                    if (this.behaviorTicks % 20 == 0 && this.wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        this.wolf.doHurtTarget(serverLevel, this.rival);
                    }
                }
                // Check if someone yields (or dies if fatal)
                if (this.rival instanceof Wolf rivalWolf) {
                    if (!this.isFatal) {
                        if (this.rival.getHealth() < this.rival.getMaxHealth() * 0.4) {
                            net.vanillaoutsider.betterdogs.util.WolfTerritorialRivalryHelper.mergePacks(this.wolf, rivalWolf, this.searchRadius);
                            this.behaviorTicks = 2000;
                        } else if (this.wolf.getHealth() < this.wolf.getMaxHealth() * 0.4) {
                            net.vanillaoutsider.betterdogs.util.WolfTerritorialRivalryHelper.mergePacks(rivalWolf, this.wolf, this.searchRadius);
                            this.behaviorTicks = 2000;
                        }
                    } else {
                        if (this.rival.isRemoved() || !this.rival.isAlive()) {
                            net.vanillaoutsider.betterdogs.util.WolfTerritorialRivalryHelper.mergePacks(this.wolf, rivalWolf, this.searchRadius);
                            this.behaviorTicks = 2000;
                        } else if (this.wolf.isRemoved() || !this.wolf.isAlive()) {
                            net.vanillaoutsider.betterdogs.util.WolfTerritorialRivalryHelper.mergePacks(rivalWolf, this.wolf, this.searchRadius);
                            this.behaviorTicks = 2000;
                        }
                    }
                }
            }
            case RETREAT -> {
                if (this.wolf.getNavigation().isDone() || this.behaviorTicks % 40 == 0) {
                    Vec3 awayPos = DefaultRandomPos.getPosAway(this.wolf, 24, 7, this.rival.position());
                    if (awayPos != null) {
                        this.wolf.getNavigation().moveTo(awayPos.x, awayPos.y, awayPos.z, 1.2);
                    }
                }
            }
        }
    }

    private void decideOutcome() {
        if (!(this.rival instanceof Wolf rivalWolf)) {
            return;
        }

        // Use deterministic seed for both wolves to ensure synchronized decisions
        long seed = (long) Math.min(this.wolf.getId(), this.rival.getId()) << 32 | (long) Math.max(this.wolf.getId(), this.rival.getId());
        java.util.Random seededRandom = new java.util.Random(seed);

        net.vanillaoutsider.betterdogs.util.WolfTerritorialRivalryHelper.RivalryOutcome outcome =
                net.vanillaoutsider.betterdogs.util.WolfTerritorialRivalryHelper.evaluateOutcome(this.wolf.level(), this.wolf, rivalWolf, seededRandom);

        this.debugLog("Territory outcome evaluated: " + outcome + " | " + this.wolf.getName().getString() + " vs " + rivalWolf.getName().getString());

        switch (outcome) {
            case WAR -> {
                this.behavior = Behavior.FIGHT;
                this.isFatal = seededRandom.nextInt(100) < DynamicGameRuleManager.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERRITORIAL_FATAL_CHANCE);
                net.vanillaoutsider.betterdogs.util.WolfTerritorialRivalryHelper.startWar(this.wolf, rivalWolf);
            }
            case MERGE -> {
                boolean iAmDominant = net.vanillaoutsider.betterdogs.util.WolfTerritorialRivalryHelper.isMoreDominant(this.wolf, rivalWolf);
                if (iAmDominant) {
                    net.vanillaoutsider.betterdogs.util.WolfTerritorialRivalryHelper.mergePacks(this.wolf, rivalWolf, this.searchRadius);
                } else {
                    net.vanillaoutsider.betterdogs.util.WolfTerritorialRivalryHelper.mergePacks(rivalWolf, this.wolf, this.searchRadius);
                }
                this.behaviorTicks = 2000;
            }
            case RETREAT -> {
                this.behavior = Behavior.RETREAT;
            }
        }
    }

    private void debugLog(String message) {
        if (DynamicGameRuleManager.getBoolean(this.wolf.level(), BetterDogsGameRules.BD_DEBUGGING)) {
            net.vanillaoutsider.betterdogs.BetterDogs.LOGGER.info("[Territory Debug] " + message);
        }
    }
}
