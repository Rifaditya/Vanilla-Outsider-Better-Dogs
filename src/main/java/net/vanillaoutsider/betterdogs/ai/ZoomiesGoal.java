// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose AI goal for high-speed playful sprint loops (zoomies) around the owner.
 */
public class ZoomiesGoal extends Goal {

    private final Wolf wolf;
    private Player owner;
    private int cooldown = 0;

    public ZoomiesGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isTame() || this.wolf.isOrderedToSit() || this.wolf.getTarget() != null) {
            return false;
        }
        if (!(this.wolf instanceof WolfExtensions ext) || ext.betterdogs$getZoomiesTicks() <= 0) {
            return false;
        }

        LivingEntity livingOwner = this.wolf.getOwner();
        if (!(livingOwner instanceof Player player)) {
            return false;
        }
        this.owner = player;
        return this.wolf.distanceToSqr(this.owner) <= 256.0;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.isOrderedToSit() || this.wolf.getTarget() != null || this.owner == null || !this.owner.isAlive()) {
            return false;
        }
        return this.wolf instanceof WolfExtensions ext && ext.betterdogs$getZoomiesTicks() > 0;
    }

    @Override
    public void start() {
        this.cooldown = 0;
        this.pickNewZoomiesTarget();
    }

    @Override
    public void stop() {
        this.owner = null;
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.owner == null) {
            return;
        }

        if (this.wolf.getNavigation().isDone() || ++this.cooldown % 15 == 0) {
            this.pickNewZoomiesTarget();
        }

        if (this.wolf.level() instanceof ServerLevel serverLevel) {
            if (this.wolf.getRandom().nextFloat() < 0.35f) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.wolf.getX(), this.wolf.getY() + 0.3, this.wolf.getZ(), 1, 0.2, 0.2, 0.2, 0.02);
            }
            if (this.wolf.getRandom().nextFloat() < 0.2f) {
                serverLevel.sendParticles(ParticleTypes.POOF, this.wolf.getX(), this.wolf.getY() + 0.1, this.wolf.getZ(), 1, 0.1, 0.1, 0.1, 0.01);
            }
        }
    }

    private void pickNewZoomiesTarget() {
        if (this.owner == null) {
            return;
        }
        Vec3 pos = DefaultRandomPos.getPosTowards(this.wolf, 8, 4, this.owner.position(), (float) Math.PI / 2F);
        if (pos == null) {
            pos = DefaultRandomPos.getPos(this.wolf, 6, 3);
        }
        if (pos != null) {
            this.wolf.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.5);
        }
    }
}
