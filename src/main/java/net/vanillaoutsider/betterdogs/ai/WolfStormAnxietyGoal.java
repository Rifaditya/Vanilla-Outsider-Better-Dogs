// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import net.vanillaoutsider.betterdogs.util.WolfStormHelper;

import java.util.EnumSet;

/**
 * Dedicated single-purpose AI goal for thunderstorm anxiety, shelter pathing, and whimpering reactions.
 */
public class WolfStormAnxietyGoal extends Goal {

    private final Wolf wolf;

    public WolfStormAnxietyGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!WolfStormHelper.isStormAnxietyActive(this.wolf)) {
            return false;
        }
        float multiplier = WolfStormHelper.getPersonalityMultiplier(this.wolf);
        if (multiplier <= 0.0f) {
            return false;
        }
        return this.wolf.getRandom().nextFloat() < (0.25f * multiplier);
    }

    @Override
    public boolean canContinueToUse() {
        return WolfStormHelper.isStormAnxietyActive(this.wolf);
    }

    @Override
    public void start() {
        if (!this.wolf.isOrderedToSit()) {
            BlockPos shelterPos = WolfStormHelper.findShelterTarget(this.wolf);
            if (shelterPos != null) {
                this.wolf.getNavigation().moveTo(shelterPos.getX() + 0.5, shelterPos.getY(), shelterPos.getZ() + 0.5, 1.25);
            } else {
                Vec3 target = DefaultRandomPos.getPos(this.wolf, 8, 4);
                if (target != null) {
                    this.wolf.getNavigation().moveTo(target.x, target.y, target.z, 1.0);
                }
            }
        }
    }

    @Override
    public void tick() {
        if (this.wolf.getRandom().nextFloat() < 0.05F) {
            try {
                var soundSet = ((WolfAccessor) this.wolf).betterdogs$invokeGetSoundSet();
                if (soundSet != null && soundSet.whineSound() != null) {
                    this.wolf.playSound(soundSet.whineSound().value(), 1.0F, 1.0F);
                }
            } catch (Exception ignored) {
            }
        }

        // Particle feedback while outdoors in rain/thunder
        if (this.wolf.level() instanceof ServerLevel serverLevel) {
            if (this.wolf.level().canSeeSky(this.wolf.blockPosition()) && this.wolf.getRandom().nextFloat() < 0.15F) {
                serverLevel.sendParticles(ParticleTypes.SPLASH, this.wolf.getRandomX(0.8), this.wolf.getRandomY() + 0.3, this.wolf.getRandomZ(0.8), 2, 0.1, 0.1, 0.1, 0.02);
            }
        }

        if (!this.wolf.isOrderedToSit() && this.wolf.getNavigation().isDone()) {
            BlockPos shelterPos = WolfStormHelper.findShelterTarget(this.wolf);
            if (shelterPos != null) {
                this.wolf.getNavigation().moveTo(shelterPos.getX() + 0.5, shelterPos.getY(), shelterPos.getZ() + 0.5, 1.25);
            } else {
                Vec3 target = DefaultRandomPos.getPos(this.wolf, 8, 4);
                if (target != null) {
                    this.wolf.getNavigation().moveTo(target.x, target.y, target.z, 1.0);
                }
            }
        }

        if (this.wolf.getRandom().nextFloat() < 0.2F) {
            double spread = 4.0;
            this.wolf.getLookControl().setLookAt(
                    this.wolf.getX() + (this.wolf.getRandom().nextDouble() - 0.5) * spread,
                    this.wolf.getEyeY(),
                    this.wolf.getZ() + (this.wolf.getRandom().nextDouble() - 0.5) * spread,
                    10.0F,
                    (float) this.wolf.getMaxHeadXRot()
            );
        }
    }

    @Override
    public void stop() {
        this.wolf.getNavigation().stop();
    }
}
