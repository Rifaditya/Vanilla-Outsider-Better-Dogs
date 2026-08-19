// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.util.WildHuntHelper;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

/**
 * Dedicated single-purpose AI goal for wild wolves to desperately hunt small prey only when hurt.
 * Wild wolves hunt when health falls below bd_wild_hunt_health_threshold (default 50%).
 * Upon defeating prey, restores +4.0 HP (2 hearts) sustenance healing.
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
        if (!WildHuntHelper.shouldHuntPrey(this.wolf)) {
            return false;
        }

        Level level = this.wolf.level();
        if (level == null || level.isClientSide()) {
            return false;
        }

        List<Animal> potentialPrey = level.getEntitiesOfClass(
                Animal.class,
                this.wolf.getBoundingBox().inflate(16.0),
                WildHuntHelper::isPrey
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
        LivingEntity currentTarget = this.wolf.getTarget();
        return WildHuntHelper.shouldContinueHunting(this.wolf, currentTarget);
    }

    @Override
    public void tick() {
        if (this.targetPrey != null && !this.targetPrey.isAlive()) {
            WildHuntHelper.applySustenanceHealing(this.wolf);
            this.targetPrey = null;
            this.wolf.setTarget(null);
        }
    }

    @Override
    public void stop() {
        this.targetPrey = null;
        this.wolf.setTarget(null);
    }
}
