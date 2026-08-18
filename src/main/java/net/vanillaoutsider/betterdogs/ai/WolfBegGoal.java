// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.DogTreatHelper;

import java.util.EnumSet;
import java.util.List;

/**
 * Dedicated single-purpose AI goal for begging when nearby players hold the dog's favorite treat or food within 5 blocks.
 */
public class WolfBegGoal extends Goal {

    public static final float DEFAULT_LOOK_DISTANCE = 5.0F;

    private final Wolf wolf;
    private Player player;
    private final float lookDistance;
    private int lookTime;

    public WolfBegGoal(Wolf wolf, float lookDistance) {
        this.wolf = wolf;
        this.lookDistance = lookDistance;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isTame() || this.wolf.isOrderedToSit() || this.wolf.isInSittingPose() || this.wolf.getTarget() != null) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$isGuardMode() || ext.betterdogs$isSittingManually()) {
                return false;
            }
        }

        Level level = this.wolf.level();
        if (level == null || level.isClientSide()) {
            return false;
        }

        List<Player> nearbyPlayers = level.getEntitiesOfClass(
                Player.class,
                this.wolf.getBoundingBox().inflate(this.lookDistance),
                p -> p.isAlive() && DogTreatHelper.isHoldingFoodOrTreat(this.wolf, p)
        );

        if (nearbyPlayers.isEmpty()) {
            return false;
        }

        this.player = nearbyPlayers.get(0);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.player == null || !this.player.isAlive()) {
            return false;
        }
        if (this.wolf.isOrderedToSit() || this.wolf.isInSittingPose() || this.wolf.getTarget() != null) {
            return false;
        }
        if (this.wolf.distanceToSqr(this.player) > (double) (this.lookDistance * this.lookDistance)) {
            return false;
        }
        return this.lookTime > 0 && DogTreatHelper.isHoldingFoodOrTreat(this.wolf, this.player);
    }

    @Override
    public void start() {
        this.wolf.setIsInterested(true);
        this.wolf.getNavigation().stop();
        this.lookTime = this.adjustedTickDelay(40 + this.wolf.getRandom().nextInt(40));
    }

    @Override
    public void stop() {
        this.wolf.setIsInterested(false);
        this.player = null;
    }

    @Override
    public void tick() {
        if (this.player == null) {
            return;
        }
        this.wolf.getNavigation().stop();
        this.wolf.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10.0F, (float) this.wolf.getMaxHeadXRot());
        this.lookTime--;
    }
}
