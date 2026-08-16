// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose AI goal for wild wolves to desperately hunt small prey only when hurt.
 * Wild wolves hunt when health falls below bd_wild_hunt_health_threshold (default 50%).
 */
public class HuntWhenHurtGoal extends Goal {

    private final Wolf wolf;
    private LivingEntity targetPrey;

    public HuntWhenHurtGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.wolf.isTame()) {
            return false;
        }

        Level level = this.wolf.level();
        if (level == null || level.isClientSide()) {
            return false;
        }

        int threshold = BetterDogsGameRules.getInt(level, BetterDogsGameRules.BD_WILD_HUNT_HEALTH_THRESHOLD, 50);
        if (threshold <= 0) {
            return false;
        }

        float healthPercent = (this.wolf.getHealth() / this.wolf.getMaxHealth()) * 100.0f;
        if (healthPercent >= threshold) {
            return false;
        }

        List<Animal> potentialPrey = level.getEntitiesOfClass(
            Animal.class,
            this.wolf.getBoundingBox().inflate(16.0),
            e -> e.isAlive() && (
                e.getType() == EntityType.SHEEP ||
                e.getType() == EntityType.RABBIT ||
                e.getType() == EntityType.CHICKEN ||
                e.getType() == EntityType.FOX
            )
        );

        this.targetPrey = potentialPrey.stream()
            .min(Comparator.comparingDouble(this.wolf::distanceToSqr))
            .orElse(null);

        return this.targetPrey != null;
    }

    @Override
    public void start() {
        if (this.targetPrey != null) {
            this.wolf.setTarget(this.targetPrey);
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.isTame()) {
            return false;
        }
        float healthPercent = (this.wolf.getHealth() / this.wolf.getMaxHealth()) * 100.0f;
        if (healthPercent >= 80.0f) {
            return false;
        }
        LivingEntity currentTarget = this.wolf.getTarget();
        return currentTarget != null && currentTarget.isAlive();
    }

    @Override
    public void stop() {
        this.targetPrey = null;
        if (this.wolf.getTarget() != null && !this.wolf.getTarget().isAlive()) {
            this.wolf.setTarget(null);
        }
    }
}
