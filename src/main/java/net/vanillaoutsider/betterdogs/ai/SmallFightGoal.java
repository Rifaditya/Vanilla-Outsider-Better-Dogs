// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose AI Goal for harmless social sparring / play fighting between compatible tamed dogs.
 * Runs for ~6 seconds with mock pounces, panting/playful growls, and happy villager particles. Deals 0 damage.
 */
public class SmallFightGoal extends Goal {

    private final Wolf wolf;
    private Wolf partner;
    private int fightTicks = 0;

    public SmallFightGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isTame() || this.wolf.isOrderedToSit() || this.wolf.getTarget() != null) {
            return false;
        }

        if (!(this.wolf instanceof WolfExtensions ext) || ext.betterdogs$getPlayFightCooldown() > 0 || ext.betterdogs$hasBloodFeud()) {
            return false;
        }

        Level level = this.wolf.level();
        if (level == null || level.isClientSide()) {
            return false;
        }

        // 1 in 40 tick chance when idle to search for a partner
        if (this.wolf.getRandom().nextInt(40) != 0) {
            return false;
        }

        List<Wolf> nearbyWolves = level.getEntitiesOfClass(
            Wolf.class,
            this.wolf.getBoundingBox().inflate(6.0),
            w -> w != this.wolf &&
                 w.isAlive() &&
                 w.isTame() &&
                 !w.isOrderedToSit() &&
                 w.getTarget() == null &&
                 w instanceof WolfExtensions pExt &&
                 pExt.betterdogs$getPlayFightCooldown() == 0 &&
                 !pExt.betterdogs$hasBloodFeud() &&
                 (this.wolf.getOwnerUUID() != null && this.wolf.getOwnerUUID().equals(w.getOwnerUUID()))
        );

        if (nearbyWolves.isEmpty()) {
            return false;
        }

        this.partner = nearbyWolves.get(this.wolf.getRandom().nextInt(nearbyWolves.size()));
        return this.partner != null;
    }

    @Override
    public void start() {
        this.fightTicks = 120; // 6 seconds
    }

    @Override
    public boolean canContinueToUse() {
        if (this.fightTicks <= 0 || this.partner == null || !this.partner.isAlive()) {
            return false;
        }

        if (this.wolf.isOrderedToSit() || this.partner.isOrderedToSit()) {
            return false;
        }

        if (this.wolf.getTarget() != null || this.partner.getTarget() != null) {
            return false;
        }

        return this.wolf.distanceToSqr(this.partner) <= 64.0;
    }

    @Override
    public void tick() {
        if (this.partner == null) {
            return;
        }

        this.fightTicks--;
        this.wolf.getLookControl().setLookAt(this.partner, 30.0F, 30.0F);
        this.wolf.getNavigation().moveTo(this.partner, 1.15);

        double distSqr = this.wolf.distanceToSqr(this.partner);
        if (distSqr <= 3.5) {
            // Mock pounce / tussle action
            if (this.fightTicks % 25 == 0) {
                this.wolf.getJumpControl().jump();

                Level level = this.wolf.level();
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        (this.wolf.getX() + this.partner.getX()) * 0.5,
                        (this.wolf.getY() + this.partner.getY()) * 0.5 + 0.5,
                        (this.wolf.getZ() + this.partner.getZ()) * 0.5,
                        3,
                        0.2, 0.2, 0.2, 0.0
                    );

                    float pitch = 1.3F + this.wolf.getRandom().nextFloat() * 0.4F;
                    if (this.wolf.getRandom().nextBoolean()) {
                        serverLevel.playSound(null, this.wolf.getX(), this.wolf.getY(), this.wolf.getZ(), SoundEvents.WOLF_GROWL, SoundSource.NEUTRAL, 0.6F, pitch);
                    } else {
                        serverLevel.playSound(null, this.wolf.getX(), this.wolf.getY(), this.wolf.getZ(), SoundEvents.WOLF_PANT, SoundSource.NEUTRAL, 0.8F, pitch);
                    }
                }
            }
        }
    }

    @Override
    public void stop() {
        if (this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setPlayFightCooldown(600); // 30s cooldown
        }
        if (this.partner instanceof WolfExtensions partnerExt) {
            partnerExt.betterdogs$setPlayFightCooldown(600); // 30s cooldown
        }
        this.partner = null;
        this.wolf.getNavigation().stop();
    }
}
