// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class PersonalityFollowOwnerGoal extends FollowOwnerGoal {
    private final Wolf wolf;
    private final double baseSpeedModifier;

    public PersonalityFollowOwnerGoal(Wolf wolf, double speedModifier, float minDistance, float maxDistance) {
        super(wolf, speedModifier, minDistance, maxDistance);
        this.wolf = wolf;
        this.baseSpeedModifier = speedModifier;
    }

    public float getStartDistance() {
        if (this.wolf instanceof WolfExtensions ext) {
            WolfPersonality personality = ext.betterdogs$getPersonality();
            return switch (personality) {
                case AGGRESSIVE -> 50.0f;
                case PACIFIST -> 5.0f;
                case NORMAL -> 10.0f;
            };
        }
        return 10.0f;
    }

    public float getStopDistance() {
        return 2.0f;
    }

    public float getTeleportThreshold() {
        float startDist = getStartDistance();
        if (startDist > 16.0f) {
            return 32.0f;
        }
        return startDist * 2.0f;
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null || owner.isSpectator() || this.wolf.isOrderedToSit() || this.wolf.isLeashed()) {
            return false;
        }
        float startDist = getStartDistance();
        return this.wolf.distanceToSqr(owner) >= (startDist * startDist);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.getNavigation().isDone() || this.wolf.isOrderedToSit() || this.wolf.isLeashed()) {
            return false;
        }
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null) {
            return false;
        }
        float stopDist = getStopDistance();
        return this.wolf.distanceToSqr(owner) > (stopDist * stopDist);
    }

    @Override
    public void tick() {
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null) {
            return;
        }

        this.wolf.getLookControl().setLookAt(owner, 10.0f, (float) this.wolf.getMaxHeadXRot());
        double distSqr = this.wolf.distanceToSqr(owner);
        float teleportThresh = getTeleportThreshold();

        if (distSqr >= (teleportThresh * teleportThresh)) {
            teleportToOwner(owner);
            return;
        }

        float startDist = getStartDistance();
        if (distSqr >= (startDist * startDist)) {
            double speed = this.baseSpeedModifier;
            if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE) {
                speed *= 1.1;
            }
            if (distSqr > 64.0) {
                speed *= 1.35;
            }
            this.wolf.getNavigation().moveTo(owner, speed);
        } else {
            this.wolf.getNavigation().stop();
        }
    }

    private void teleportToOwner(LivingEntity owner) {
        for (int i = 0; i < 10; ++i) {
            double dx = (this.wolf.getRandom().nextFloat() - 0.5) * 4.0;
            double dy = (this.wolf.getRandom().nextFloat() - 0.5) * 4.0;
            double dz = (this.wolf.getRandom().nextFloat() - 0.5) * 4.0;
            if (this.wolf.randomTeleport(owner.getX() + dx, owner.getY() + dy, owner.getZ() + dz, false)) {
                break;
            }
        }
    }
}
