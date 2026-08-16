// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose AI goal for untamed wild pack territory posturing and standoff behaviors.
 */
public class WildWolfTerritorialGoal extends Goal {

    private final Wolf wolf;
    private Wolf intruder;
    private int standoffTicks = 0;

    public WildWolfTerritorialGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.wolf.isTame() || this.wolf.isBaby() || this.wolf.getTarget() != null) {
            return false;
        }

        Level level = this.wolf.level();
        if (level == null || level.isClientSide()) {
            return false;
        }

        List<Wolf> nearbyWolves = level.getEntitiesOfClass(
            Wolf.class,
            this.wolf.getBoundingBox().inflate(12.0),
            w -> w.isAlive() && w != this.wolf && !w.isBaby() && (w.isTame() || w.distanceToSqr(this.wolf) >= 16.0)
        );

        if (nearbyWolves.isEmpty()) {
            return false;
        }

        this.intruder = nearbyWolves.get(0);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.getTarget() != null || this.intruder == null || !this.intruder.isAlive()) {
            return false;
        }
        return this.standoffTicks < 100 && this.wolf.distanceToSqr(this.intruder) <= 144.0;
    }

    @Override
    public void start() {
        this.standoffTicks = 0;
    }

    @Override
    public void stop() {
        this.intruder = null;
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.intruder == null) {
            return;
        }

        this.standoffTicks++;
        this.wolf.getLookControl().setLookAt(this.intruder, 20.0F, 20.0F);

        net.vanillaoutsider.betterdogs.WolfPersonality personality = net.vanillaoutsider.betterdogs.WolfPersonality.NORMAL;
        if (this.wolf instanceof WolfExtensions ext) {
            personality = ext.betterdogs$getPersonality();
        }

        if (personality == net.vanillaoutsider.betterdogs.WolfPersonality.PACIFIST) {
            if (this.wolf.getNavigation().isDone() && this.standoffTicks % 20 == 0) {
                Vec3 awayPos = DefaultRandomPos.getPosAway(this.wolf, 8, 4, this.intruder.position());
                if (awayPos != null) {
                    this.wolf.getNavigation().moveTo(awayPos.x, awayPos.y, awayPos.z, 1.1);
                }
            }
        } else if (personality == net.vanillaoutsider.betterdogs.WolfPersonality.AGGRESSIVE) {
            if (this.wolf.distanceToSqr(this.intruder) > 16.0) {
                this.wolf.getNavigation().moveTo(this.intruder, 1.2);
            } else {
                this.wolf.getNavigation().stop();
                if (this.wolf.level() instanceof ServerLevel serverLevel) {
                    if (this.wolf.getRandom().nextFloat() < 0.1f) {
                        serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, this.wolf.getX(), this.wolf.getY() + 0.4, this.wolf.getZ(), 1, 0.1, 0.1, 0.1, 0.0);
                    }
                }
            }
        } else {
            if (this.wolf.distanceToSqr(this.intruder) < 25.0) {
                Vec3 awayPos = DefaultRandomPos.getPosAway(this.wolf, 4, 2, this.intruder.position());
                if (awayPos != null) {
                    this.wolf.getNavigation().moveTo(awayPos.x, awayPos.y, awayPos.z, 1.0);
                }
            } else {
                this.wolf.getNavigation().stop();
            }
        }
    }
}
