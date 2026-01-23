package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import java.util.List;

/**
 * Goal for aggressive adult wolves to intervene when a baby wolf attacks its owner.
 */
public class InterventionGoal extends TargetGoal {
    private final Wolf wolf;
    private Wolf offendingBaby;

    public InterventionGoal(Wolf wolf) {
        super(wolf, false);
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        if (!(wolf instanceof WolfExtensions ext) || ext.betterdogs$getPersonality() != WolfPersonality.AGGRESSIVE) {
            return false;
        }

        if (wolf.isBaby()) return false;

        LivingEntity owner = wolf.getOwner();
        if (owner == null) return false;

        // Look for nearby babies attacking the owner
        List<Wolf> nearbyWolves = wolf.level().getEntitiesOfClass(Wolf.class, wolf.getBoundingBox().inflate(10.0));
        for (Wolf nearby : nearbyWolves) {
            if (nearby.isBaby() && nearby.isTame() && nearby.getTarget() != null && nearby.getTarget().equals(owner)) {
                this.offendingBaby = nearby;
                return true;
            }
        }

        return false;
    }

    @Override
    public void start() {
        if (offendingBaby != null) {
            wolf.setTarget(offendingBaby);
            if (wolf instanceof WolfExtensions ext) {
                ext.betterdogs$setStrikesRemaining(3);
            }
        }
        super.start();
    }
}
