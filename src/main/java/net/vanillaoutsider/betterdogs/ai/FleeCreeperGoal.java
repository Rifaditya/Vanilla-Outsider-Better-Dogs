package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;

/**
 * AI Goal for tamed wolves to flee from hissing creepers.
 * Only triggers when creeper is about to explode.
 */
public class FleeCreeperGoal extends AvoidEntityGoal<Creeper> {

    private final Wolf wolf;

    public FleeCreeperGoal(Wolf wolf) {
        super(wolf, Creeper.class, 6.0f, 1.0, 1.5,
                (livingEntity) -> {
                    if (livingEntity instanceof Creeper creeper) {
                        return creeper.isIgnited() || creeper.getSwellDir() > 0;
                    }
                    return false;
                });
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        // Only for tamed wolves
        if (!wolf.isTame())
            return false;

        return super.canUse();
    }
}
