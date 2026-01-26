package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import java.util.EnumSet;

/**
 * Begging Goal.
 * Behavior: Looks at the owner with a "begging" pose (tilted head, following closely).
 */
public class BeggingGoal extends Goal {
    private final Wolf wolf;
    private final WolfExtensions wolfExt;
    private LivingEntity owner;

    public BeggingGoal(Wolf wolf) {
        this.wolf = wolf;
        this.wolfExt = (WolfExtensions) wolf;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (wolfExt.betterdogs$getScheduler().isEventActive("begging")) {
            if (this.wolf.isOrderedToSit()) return false;
            this.owner = wolf.getOwner();
            return this.owner != null && this.owner.isAlive();
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return wolfExt.betterdogs$getScheduler().isEventActive("begging") && this.owner != null && this.owner.isAlive();
    }

    @Override
    public void start() {
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.wolf.getLookControl().setLookAt(this.owner, 10.0F, (float)this.wolf.getMaxHeadXRot());
        if (this.wolf.distanceToSqr(this.owner) > 4.0D) {
            this.wolf.getNavigation().moveTo(this.owner, 1.0D);
        } else {
            this.wolf.getNavigation().stop();
        }
    }
}
