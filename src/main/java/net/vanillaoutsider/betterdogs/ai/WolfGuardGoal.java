// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.util.WolfGuardHelper;

/**
 * Dedicated single-purpose AI Goal for anchored territory patrolling and sentinel post enforcement.
 */
public class WolfGuardGoal extends Goal {

    private final Wolf wolf;
    private int patrolDelay = 0;
    private int auraTicks = 0;

    public WolfGuardGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isTame() || this.wolf.isInSittingPose() || this.wolf.isLeashed()) {
            return false;
        }

        if (!(this.wolf instanceof WolfExtensions ext)) {
            return false;
        }

        return ext.betterdogs$isGuarding() && ext.betterdogs$getGuardPos() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.patrolDelay = 0;
        this.auraTicks = 0;
    }

    @Override
    public void tick() {
        if (!(this.wolf instanceof WolfExtensions ext)) {
            return;
        }

        BlockPos guardPos = ext.betterdogs$getGuardPos();
        if (guardPos == null) {
            return;
        }

        WolfPersonality personality = ext.betterdogs$getPersonality();
        int radius = WolfGuardHelper.getPatrolRadius(personality);
        double distSqr = this.wolf.distanceToSqr(guardPos.getX() + 0.5, guardPos.getY() + 0.5, guardPos.getZ() + 0.5);
        double maxDistSqr = radius * radius;

        // If wolf wandered outside territory, sprint/leash back to post
        if (distSqr > maxDistSqr) {
            this.wolf.getNavigation().moveTo(guardPos.getX() + 0.5, guardPos.getY(), guardPos.getZ() + 0.5, 1.25);
            return;
        }

        // Periodic Pacifist Soothing/Regen Aura
        if (personality == WolfPersonality.PACIFIST) {
            this.auraTicks++;
            if (this.auraTicks >= 40) {
                this.auraTicks = 0;
                applyPacifistAura(guardPos);
            }
        }

        // Patrol waypoint navigation
        if (--this.patrolDelay <= 0) {
            this.patrolDelay = 60 + this.wolf.getRandom().nextInt(60);

            // Pick random waypoint within radius around guardPos
            int offsetX = this.wolf.getRandom().nextInt(radius * 2 + 1) - radius;
            int offsetZ = this.wolf.getRandom().nextInt(radius * 2 + 1) - radius;
            BlockPos targetPos = guardPos.offset(offsetX, 0, offsetZ);

            this.wolf.getNavigation().moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 1.0);
        }
    }

    private void applyPacifistAura(BlockPos guardPos) {
        var level = this.wolf.level();
        if (level == null) {
            return;
        }

        AABB auraBox = new AABB(guardPos).inflate(4.0);
        var players = level.getEntitiesOfClass(Player.class, auraBox, p -> p.isAlive() && this.wolf.isOwnedBy(p));
        for (Player player : players) {
            if (player.getHealth() < player.getMaxHealth()) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, true));
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.0, player.getZ(), 2, 0.2, 0.2, 0.2, 0.02);
                }
            }
        }
    }
}
