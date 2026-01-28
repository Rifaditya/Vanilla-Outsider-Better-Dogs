package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.vanillaoutsider.betterdogs.WolfExtensions;

import java.util.EnumSet;

public class ZoomiesGoal extends Goal {
    private final Wolf wolf;
    private final WolfExtensions wolfExt;
    private double speedModifier;

    public ZoomiesGoal(Wolf wolf) {
        this.wolf = wolf;
        this.wolfExt = (WolfExtensions) wolf;
        this.speedModifier = BetterDogsConfig.get().getZoomiesSpeedModifier();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (wolf.isOrderedToSit()) return false;
        net.vanillaoutsider.social.core.EntitySocialScheduler scheduler = wolfExt.betterdogs$getScheduler();
        return scheduler != null && scheduler.isEventActive("zoomies");
    }

    @Override
    public void start() {
        wolf.getNavigation().stop();
        runRandomly();
    }

    @Override
    public boolean canContinueToUse() {
        return wolfExt.betterdogs$getScheduler().isEventActive("zoomies");
    }

    @Override
    public void tick() {
        BetterDogsConfig config = BetterDogsConfig.get();
        if (wolf.getNavigation().isDone() || wolf.getRandom().nextInt(config.getZoomiesNewSpotChance()) == 0) {
            runRandomly();
        }
        if (wolf.getRandom().nextFloat() < config.getZoomiesLookAroundChance()) {
            wolf.getLookControl().setLookAt(wolf.getX() + wolf.getRandom().nextGaussian(), wolf.getEyeY(), wolf.getZ() + wolf.getRandom().nextGaussian());
        }
    }

    private void runRandomly() {
        BetterDogsConfig config = BetterDogsConfig.get();
        // Use LandRandomPos to prefer land and avoid water
        Vec3 target = net.minecraft.world.entity.ai.util.LandRandomPos.getPos(wolf, config.getZoomiesRange(), config.getZoomiesVerticalRange());
        
        if (target != null) {
            // Safety Check: Verify target is actually on solid ground
            net.minecraft.core.BlockPos targetBlock = net.minecraft.core.BlockPos.containing(target);
            // Check 2 blocks down to be safe (sometimes pos is eye level or slightly above)
            boolean safeInfo = !wolf.level().isEmptyBlock(targetBlock.below()) || !wolf.level().isEmptyBlock(targetBlock.below(2));
            
            if (safeInfo) {
                wolf.getNavigation().moveTo(target.x, target.y, target.z, speedModifier);
            }
        }
    }
}
