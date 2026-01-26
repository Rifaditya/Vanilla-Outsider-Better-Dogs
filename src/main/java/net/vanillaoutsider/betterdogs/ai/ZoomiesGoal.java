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
        return wolfExt.betterdogs$getSocialAction() == WolfExtensions.SocialAction.ZOOMIES;
    }

    @Override
    public void start() {
        wolf.getNavigation().stop();
        runRandomly();
    }

    @Override
    public boolean canContinueToUse() {
        return wolfExt.betterdogs$getSocialAction() == WolfExtensions.SocialAction.ZOOMIES;
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
        Vec3 target = DefaultRandomPos.getPos(wolf, config.getZoomiesRange(), config.getZoomiesVerticalRange());
        if (target != null) {
            wolf.getNavigation().moveTo(target.x, target.y, target.z, speedModifier);
        }
    }
}
