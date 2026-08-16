// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.WolfMischiefHelper;

/**
 * Dedicated single-purpose AI goal for adult disciplinary correction of misbehaving puppies.
 */
public class AdultCorrectionGoal extends Goal {

    private final Wolf wolf;
    private Wolf annoyingPuppy;

    public AdultCorrectionGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.wolf.isBaby() || this.wolf.getTarget() != null) {
            return false;
        }

        Level level = this.wolf.level();
        if (level == null) {
            return false;
        }

        List<Wolf> nearbyPuppies = level.getEntitiesOfClass(
            Wolf.class,
            this.wolf.getBoundingBox().inflate(2.0),
            w -> w.isAlive() && w.isBaby() && (w instanceof WolfExtensions ext && ext.betterdogs$getCalmTicks() <= 0)
        );

        if (nearbyPuppies.isEmpty()) {
            return false;
        }

        this.annoyingPuppy = nearbyPuppies.get(0);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        if (this.annoyingPuppy != null) {
            WolfMischiefHelper.performDiscipline(this.wolf, this.annoyingPuppy);
        }
    }

    @Override
    public void stop() {
        this.annoyingPuppy = null;
    }
}
