// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfZoomiesHelper;

/**
 * Dedicated single-purpose AI goal for high-speed playful sprint loops (zoomies) around the owner.
 */
public class ZoomiesGoal extends Goal {

    private final Wolf wolf;
    private int cooldown = 0;

    public ZoomiesGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.wolf == null || !this.wolf.isTame()) {
            return false;
        }
        if (this.wolf.isOrderedToSit() || this.wolf.isInSittingPose() || this.wolf.getTarget() != null) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
            return false;
        }
        if (!BetterDogsGameRules.isZoomiesEnabled(this.wolf.level())) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getZoomiesTicks() > 0) {
                return true;
            }
            EntitySocialScheduler scheduler = ext.betterdogs$getScheduler();
            if (scheduler != null && scheduler.isEventActive("zoomies")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.isOrderedToSit() || this.wolf.isInSittingPose() || this.wolf.getTarget() != null) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$isGuardMode()) {
                return false;
            }
            if (ext.betterdogs$getZoomiesTicks() > 0) {
                return true;
            }
            EntitySocialScheduler scheduler = ext.betterdogs$getScheduler();
            return scheduler != null && scheduler.isEventActive("zoomies");
        }
        return false;
    }

    @Override
    public void start() {
        this.cooldown = 0;
        this.wolf.getNavigation().stop();
        this.pickNewZoomiesTarget();
    }

    @Override
    public void stop() {
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.wolf.getNavigation().isDone() || ++this.cooldown % 15 == 0) {
            this.pickNewZoomiesTarget();
        }

        WolfZoomiesHelper.tickZoomiesParticles(this.wolf);
    }

    private void pickNewZoomiesTarget() {
        LivingEntity livingOwner = this.wolf.getOwner();
        Vec3 target = null;
        if (livingOwner instanceof Player player && this.wolf.distanceToSqr(player) <= 256.0D) {
            target = DefaultRandomPos.getPosTowards(this.wolf, 8, 4, player.position(), (float) Math.PI / 2.0F);
        }
        if (target == null) {
            target = LandRandomPos.getPos(this.wolf, 8, 4);
        }

        if (target != null) {
            this.wolf.getNavigation().moveTo(target.x, target.y, target.z, 1.5D);
        }
    }
}
