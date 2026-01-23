package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import java.util.EnumSet;
import java.util.Set;
import java.util.Comparator;

/**
 * AI Goal for wild wolves to hunt prey only when hurt.
 * Replaces vanilla always-hunting behavior.
 */
public class HuntWhenHurtGoal extends Goal {

    private final Wolf wolf;
    private static final float HEALTH_THRESHOLD = 0.5f; // 50% health

    // Prey animal types by name (safer than class references)
    private static final Set<String> PREY_TYPES = Set.of("sheep", "rabbit", "chicken");

    public HuntWhenHurtGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        // Only for wild (non-tamed) wolves
        if (wolf.isTame())
            return false;

        // Only hunt when health is low
        float healthPercent = wolf.getHealth() / wolf.getMaxHealth();
        if (healthPercent >= HEALTH_THRESHOLD)
            return false;

        return true;
    }

    @Override
    public void start() {
        // Find nearest prey by checking entity type name
        Animal nearestPrey = wolf.level().getEntitiesOfClass(
                Animal.class,
                wolf.getBoundingBox().inflate(16.0),
                entity -> {
                    String typeName = entity.getType().getDescriptionId().toLowerCase();
                    return PREY_TYPES.stream().anyMatch(typeName::contains);
                }).stream().min(Comparator.comparingDouble(wolf::distanceTo)).orElse(null);

        if (nearestPrey != null) {
            wolf.setTarget(nearestPrey);
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (wolf.isTame())
            return false;

        // Stop hunting if we're healed enough
        float healthPercent = wolf.getHealth() / wolf.getMaxHealth();
        if (healthPercent >= 0.8f)
            return false;

        // Continue if target is alive
        return wolf.getTarget() != null && wolf.getTarget().isAlive();
    }

    @Override
    public void stop() {
        wolf.setTarget(null);
    }
}
