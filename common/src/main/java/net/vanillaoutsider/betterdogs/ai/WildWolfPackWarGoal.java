// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

/**
 * Dedicated single-purpose AI Goal for wild wolf pack members engaging in a Territorial Pack War.
 * Followers battle rival pack members while pack alphas duel.
 */
public class WildWolfPackWarGoal extends Goal {

    private final Wolf wolf;
    private Wolf rivalTarget;
    private int cooldown = 0;

    public WildWolfPackWarGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.wolf.isTame()) {
            return false;
        }

        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        if (!(this.wolf instanceof WolfExtensions ext)) {
            return false;
        }

        UUID leaderUuid = ext.betterdogs$getLeaderUUID();
        if (leaderUuid == null) {
            return false;
        }

        Level level = this.wolf.getCommandSenderWorld();
        if (level == null || level.isClientSide()) {
            return false;
        }

        List<Wolf> leaders = level.getEntitiesOfClass(
            Wolf.class,
            this.wolf.getBoundingBox().inflate(32.0),
            w -> w.isAlive() && w.getUUID().equals(leaderUuid)
        );

        if (leaders.isEmpty()) {
            return false;
        }

        Wolf leader = leaders.get(0);
        LivingEntity leaderTarget = leader.getTarget();
        if (!(leaderTarget instanceof Wolf rivalAlpha)) {
            return false;
        }

        // Pacifists only join if directly attacked
        WolfPersonality personality = ext.betterdogs$getPersonality();
        if (personality == WolfPersonality.PACIFIST) {
            LivingEntity attacker = this.wolf.getLastHurtByMob();
            if (attacker == null || (this.wolf.tickCount - this.wolf.getLastHurtByMobTimestamp() > 100)) {
                return false;
            }
            if (!(attacker instanceof Wolf rivalWolf) || !isMemberOfRivalPack(rivalWolf, rivalAlpha)) {
                return false;
            }
            this.rivalTarget = (Wolf) attacker;
            return true;
        }

        return findRivalTarget(level, rivalAlpha);
    }

    private boolean isMemberOfRivalPack(Wolf wolf, Wolf rivalAlpha) {
        if (wolf == rivalAlpha) return true;
        if (wolf instanceof WolfExtensions wExt) {
            UUID lUuid = wExt.betterdogs$getLeaderUUID();
            return lUuid != null && lUuid.equals(rivalAlpha.getUUID());
        }
        return false;
    }

    private boolean findRivalTarget(Level level, Wolf rivalAlpha) {
        List<Wolf> rivals = level.getEntitiesOfClass(
            Wolf.class,
            this.wolf.getBoundingBox().inflate(16.0),
            w -> w != this.wolf && w.isAlive() && !w.isTame() && isMemberOfRivalPack(w, rivalAlpha) && w != rivalAlpha
        );

        if (!rivals.isEmpty()) {
            this.rivalTarget = rivals.get(this.wolf.getRandom().nextInt(rivals.size()));
            return true;
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.rivalTarget != null &&
               this.rivalTarget.isAlive() &&
               !this.wolf.isTame() &&
               this.wolf.distanceToSqr(this.rivalTarget) <= 256.0;
    }

    @Override
    public void start() {
        this.wolf.setTarget(this.rivalTarget);
    }

    @Override
    public void stop() {
        this.rivalTarget = null;
        this.wolf.setTarget(null);
        this.cooldown = 40;
    }

    @Override
    public void tick() {
        if (this.rivalTarget == null) {
            return;
        }

        this.wolf.getLookControl().setLookAt(this.rivalTarget, 30.0F, 30.0F);
        this.wolf.getNavigation().moveTo(this.rivalTarget, 1.25);

        if (this.wolf.distanceToSqr(this.rivalTarget) <= 3.0) {
            this.wolf.doHurtTarget(this.rivalTarget);
        }
    }
}
