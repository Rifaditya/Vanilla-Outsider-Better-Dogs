// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.11
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import org.jetbrains.annotations.Nullable;

/**
 * Single-purpose server-side guard synchronizing non-commanded sitting pose state
 * during movement, navigation, target acquisition, and non-cancelled damage transitions.
 */
public final class WolfSittingPoseHelper {

    public static final double HORIZONTAL_MOVEMENT_EPSILON_SQR = 1.0e-4;
    /** Exceeds resting vertical gravity (-0.0784/tick) while catching jumps, knockback, and steep drops. */
    public static final double VERTICAL_MOVEMENT_EPSILON = 0.15;

    private WolfSittingPoseHelper() {
    }

    /**
     * Pure predicate determining whether an unordered sitting pose should be cleared.
     */
    public static boolean shouldClearSittingPose(
            boolean isOrderedToSit,
            boolean isInSittingPose,
            boolean isPassenger,
            boolean isPerformingScoot,
            boolean navigationDone,
            boolean hasTarget,
            double horizontalMovementSqr,
            double verticalMovement) {
        if (isOrderedToSit || !isInSittingPose || isPassenger || isPerformingScoot) {
            return false;
        }
        if (!navigationDone || hasTarget) {
            return true;
        }
        if (Double.isFinite(horizontalMovementSqr) && horizontalMovementSqr > HORIZONTAL_MOVEMENT_EPSILON_SQR) {
            return true;
        }
        return Double.isFinite(verticalMovement) && Math.abs(verticalMovement) > VERTICAL_MOVEMENT_EPSILON;
    }

    /**
     * Evaluates whether a wolf currently holds an unordered sitting pose while moving, navigating, or targeting.
     */
    public static boolean shouldClearSittingPose(@Nullable Wolf wolf) {
        if (wolf == null) {
            return false;
        }
        if (wolf.isOrderedToSit() || !wolf.isInSittingPose() || wolf.isPassenger()) {
            return false;
        }
        if (wolf instanceof WolfExtensions ext && ext.betterdogs$isPerformingScoot()) {
            return false;
        }
        PathNavigation navigation = wolf.getNavigation();
        boolean navigationDone = navigation == null || navigation.isDone();
        boolean hasTarget = wolf.getTarget() != null;
        Vec3 delta = wolf.getDeltaMovement();
        double horizontalSqr = delta != null ? delta.horizontalDistanceSqr() : 0.0;
        double vertical = delta != null ? delta.y : 0.0;
        return shouldClearSittingPose(
                false,
                true,
                false,
                false,
                navigationDone,
                hasTarget,
                horizontalSqr,
                vertical);
    }

    /**
     * Zero-allocation server tick guard clearing unordered sitting pose only on real state transitions.
     *
     * @return true if the sitting pose bit transitioned from true to false, false otherwise.
     */
    public static boolean syncSittingPoseOnTick(@Nullable Wolf wolf) {
        if (wolf == null) {
            return false;
        }
        Level level = wolf.level();
        if (level != null && level.isClientSide()) {
            return false;
        }
        if (shouldClearSittingPose(wolf)) {
            wolf.setInSittingPose(false);
            return true;
        }
        return false;
    }

    /**
     * Clears unordered sitting pose at AI goal/target transitions without disturbing ordered sit or scoot goals.
     */
    public static boolean clearUnorderedSittingPose(@Nullable Wolf wolf) {
        if (wolf == null || wolf.isOrderedToSit() || !wolf.isInSittingPose() || wolf.isPassenger()) {
            return false;
        }
        Level level = wolf.level();
        if (level != null && level.isClientSide()) {
            return false;
        }
        if (wolf instanceof WolfExtensions ext && ext.betterdogs$isPerformingScoot()) {
            return false;
        }
        wolf.setInSittingPose(false);
        return true;
    }

    /**
     * Clears non-commanded sitting pose when a wolf takes non-cancelled damage.
     */
    public static boolean clearSittingPoseOnDamage(@Nullable Wolf wolf) {
        if (wolf == null || wolf.isOrderedToSit() || !wolf.isInSittingPose() || wolf.isPassenger()) {
            return false;
        }
        Level level = wolf.level();
        if (level != null && level.isClientSide()) {
            return false;
        }
        wolf.setInSittingPose(false);
        return true;
    }

    /**
     * Evaluates whether a wolf is safely at rest on solid ground and eligible for lazy idle sitting pose.
     */
    public static boolean canEnterLazyRestingPose(@Nullable Wolf wolf) {
        if (wolf == null || !wolf.isAlive() || !wolf.isTame()) {
            return false;
        }
        if (wolf.isOrderedToSit() || wolf.isPassenger() || wolf.isLeashed()) {
            return false;
        }
        if (wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
            return false;
        }
        return wolf.onGround() && !wolf.isInWater() && !wolf.isOnFire() && wolf.getTarget() == null;
    }
}
