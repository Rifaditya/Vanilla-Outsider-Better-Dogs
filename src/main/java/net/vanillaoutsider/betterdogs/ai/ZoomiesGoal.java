package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.vanillaoutsider.betterdogs.WolfExtensions;

import java.util.EnumSet;

/**
 * Zoomies Goal.
 * Behavior: Runs around faster than normal logic.
 */
public class ZoomiesGoal extends Goal {
    private final Wolf wolf;
    private final WolfExtensions wolfExt;
    private double speedModifier = 1.4; // +40% speed

    public ZoomiesGoal(Wolf wolf) {
        this.wolf = wolf;
        this.wolfExt = (WolfExtensions) wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return wolfExt.betterdogs$getSocialAction() == WolfExtensions.SocialAction.ZOOMIES;
    }

    @Override
    public void start() {
        // Initial random run
        wolf.getNavigation().stop();
        runRandomly();
    }

    @Override
    public boolean canContinueToUse() {
        return wolfExt.betterdogs$getSocialAction() == WolfExtensions.SocialAction.ZOOMIES;
    }

    @Override
    public void tick() {
        // If we finished our path or stopped, pick a new spot immediately (Hyperactive)
        if (wolf.getNavigation().isDone() || wolf.getRandom().nextInt(10) == 0) {
            runRandomly();
        }
        
        // Spin/Look around randomly sometimes
        if (wolf.getRandom().nextFloat() < 0.05f) {
            wolf.getLookControl().setLookAt(wolf.getX() + wolf.getRandom().nextGaussian(), wolf.getEyeY(), wolf.getZ() + wolf.getRandom().nextGaussian());
        }
    }

    private void runRandomly() {
        // Try to find a position ~10 blocks away
        Vec3 target = DefaultRandomPos.getPos(wolf, 10, 4);
        if (target != null) {
            wolf.getNavigation().moveTo(target.x, target.y, target.z, speedModifier);
        }
    }
}
