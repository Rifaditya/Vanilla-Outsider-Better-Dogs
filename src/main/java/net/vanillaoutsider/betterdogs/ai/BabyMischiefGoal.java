// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.WolfMischiefHelper;

/**
 * Dedicated single-purpose AI goal for puppy playful antics, chasing critters, and bounding around adults.
 */
public class BabyMischiefGoal extends Goal {

    private final Wolf wolf;
    private LivingEntity target;
    private int playTicks = 0;
    private int cooldown = 0;

    public BabyMischiefGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isBaby() || this.wolf.isOrderedToSit() || this.wolf.getTarget() != null) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$isGuardMode() || ext.betterdogs$isSocialModeActive()) {
                return false;
            }
        }
        if (this.wolf.getRandom().nextInt(60) != 0) {
            return false;
        }

        this.target = WolfMischiefHelper.findMischiefTarget(this.wolf, 8.0);
        return this.target != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.isOrderedToSit() || this.wolf.getTarget() != null || this.target == null || !this.target.isAlive()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isSocialModeActive()) {
            return false;
        }
        return this.playTicks < 140;
    }

    @Override
    public void start() {
        this.playTicks = 0;
        this.cooldown = 0;
        if (this.target != null) {
            this.wolf.getNavigation().moveTo(this.target, 1.2);
        }
    }

    @Override
    public void stop() {
        this.target = null;
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.target == null) {
            return;
        }

        this.playTicks++;
        this.wolf.getLookControl().setLookAt(this.target, 10.0F, (float) this.wolf.getMaxHeadXRot());

        if (this.wolf.distanceToSqr(this.target) <= 2.25) {
            if (this.wolf.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.wolf.getX(), this.wolf.getY() + 0.3, this.wolf.getZ(), 1, 0.1, 0.1, 0.1, 0.02);
            }
        } else {
            if (++this.cooldown % 10 == 0) {
                this.wolf.getNavigation().moveTo(this.target, 1.2);
            }
        }
    }
}
