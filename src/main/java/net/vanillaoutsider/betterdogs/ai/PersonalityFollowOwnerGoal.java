package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

/**
 * A custom FollowOwnerGoal that adjusts its start/stop distance based on the
 * Wolf's personality.
 * This allows "Granular" control via the config.
 */
public class PersonalityFollowOwnerGoal extends FollowOwnerGoal {

    private final Wolf wolf;
    private final double speedModifier;
    private int recalcTimer;

    public PersonalityFollowOwnerGoal(Wolf wolf, double speedModifier, boolean leavesAllowed) {
        super(wolf, speedModifier, 10.0f, 2.0f, leavesAllowed);
        this.wolf = wolf;
        this.speedModifier = speedModifier;
        this.recalcTimer = 0;
    }

    private float getStartDistance() {
        if (!wolf.isTame())
            return 10.0f; // Fallback

        if (wolf instanceof WolfExtensions ext) {
            try {
                BetterDogsConfig config = BetterDogsConfig.get();
                return switch (ext.betterdogs$getPersonality()) {
                    case AGGRESSIVE -> config.aggressiveFollowStart;
                    case PACIFIST -> config.pacifistFollowStart;
                    case NORMAL -> config.normalFollowStart;
                    default -> 10.0f;
                };
            } catch (Exception e) {
                return 10.0f;
            }
        }
        return 10.0f;
    }

    private float getStopDistance() {
        if (!wolf.isTame())
            return 2.0f;

        if (wolf instanceof WolfExtensions ext) {
            try {
                BetterDogsConfig config = BetterDogsConfig.get();
                return switch (ext.betterdogs$getPersonality()) {
                    case AGGRESSIVE -> config.aggressiveFollowStop;
                    case PACIFIST -> config.pacifistFollowStop;
                    case NORMAL -> config.normalFollowStop;
                    default -> 2.0f;
                };
            } catch (Exception e) {
                return 2.0f;
            }
        }
        return 2.0f;
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = wolf.getOwner();
        if (owner == null)
            return false;
        if (owner.isSpectator())
            return false;

        // Use OUR dynamic start distance
        // Logic: if distance < startDistance, we don't need to move (return false)
        // So we need distance to be >= startDistance squared
        float startDist = getStartDistance();
        if (wolf.distanceToSqr(owner) < (startDist * startDist)) {
            return false;
        }

        // Respect sitting/leashed
        if (wolf.isOrderedToSit() || wolf.isLeashed())
            return false;

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (wolf.getNavigation().isDone())
            return false;
        if (wolf.isOrderedToSit() || wolf.isLeashed())
            return false;

        LivingEntity owner = wolf.getOwner();
        if (owner == null)
            return false;

        float stopDist = getStopDistance();
        return wolf.distanceToSqr(owner) > (stopDist * stopDist);
    }

    @Override
    public void stop() {
        wolf.getNavigation().stop();
        super.stop();
    }

    @Override
    public void tick() {
        // We override tick to implement our custom logic using dynamic distances
        LivingEntity owner = wolf.getOwner();
        if (owner == null)
            return;

        wolf.getLookControl().setLookAt(owner, 10.0f, (float) wolf.getMaxHeadXRot());

        if (--this.recalcTimer <= 0) {
            this.recalcTimer = 10;

            if (!wolf.isLeashed() && !wolf.isPassenger()) {
                float startDist = getStartDistance();
                if (wolf.distanceToSqr(owner) >= (startDist * startDist)) {
                    wolf.getNavigation().moveTo(owner, speedModifier);
                } else {
                    wolf.getNavigation().stop();
                }
            }
        }

        // Manual Teleport Logic since we can't call super.teleportToOwner() directly if
        // private
        // Or if we want custom thresholds.
        if (!wolf.isLeashed() && !wolf.isPassenger()) {
            float startDist = getStartDistance();

            // Teleport if very far (12 blocks usually, or 2x start distance)
            // 144 is 12^2
            if (wolf.distanceToSqr(owner) >= 144.0 || wolf.distanceToSqr(owner) >= (startDist * startDist * 2)) {
                teleportToOwner(owner);
            }
        }
    }

    private void teleportToOwner(LivingEntity owner) {
        for (int i = 0; i < 10; ++i) {
            double dx = (wolf.getRandom().nextFloat() - 0.5) * 4.0;
            double dy = (wolf.getRandom().nextFloat() - 0.5) * 4.0;
            double dz = (wolf.getRandom().nextFloat() - 0.5) * 4.0;
            if (wolf.randomTeleport(owner.getX() + dx, owner.getY() + dy, owner.getZ() + dz, false))
                break;
        }
    }
}
