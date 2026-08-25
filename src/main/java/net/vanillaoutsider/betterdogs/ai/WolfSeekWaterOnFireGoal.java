// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Dedicated single-purpose AI Goal for wolves on fire to urgently locate and sprint
 * into nearby water bodies (16m), or execute panic evasion if no water is reachable.
 */
public class WolfSeekWaterOnFireGoal extends Goal {

    private final Wolf wolf;
    private final double waterSprintSpeed;
    private final double panicSpeed;
    private BlockPos targetWaterPos;
    private boolean wasSitting;
    private int searchCooldown;

    public WolfSeekWaterOnFireGoal(Wolf wolf) {
        this(wolf, 1.4D, 1.3D);
    }

    public WolfSeekWaterOnFireGoal(Wolf wolf, double waterSprintSpeed, double panicSpeed) {
        this.wolf = wolf;
        this.waterSprintSpeed = waterSprintSpeed;
        this.panicSpeed = panicSpeed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (wolf == null || wolf.level() == null || wolf.level().isClientSide()) {
            return false;
        }
        if (wolf.isPassenger()) {
            return false;
        }
        if (!wolf.isOnFire() && wolf.getRemainingFireTicks() <= 0) {
            return false;
        }

        Level level = wolf.level();
        if (!DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_WOLVES_SEEK_WATER_ON_FIRE)) {
            return false;
        }

        if (wolf.isOrderedToSit()) {
            boolean allowBreakSit = DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_WOLVES_BREAK_SIT_ON_FIRE);
            if (!allowBreakSit) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (wolf.isPassenger()) {
            return false;
        }
        // Continue while on fire or until safely extinguished
        if (wolf.isOnFire() || wolf.getRemainingFireTicks() > 0) {
            return true;
        }
        // If just entered water, allow 1 tick to settle
        return false;
    }

    @Override
    public void start() {
        if (wolf.isOrderedToSit()) {
            this.wasSitting = true;
            wolf.setOrderedToSit(false);
            wolf.setInSittingPose(false);
        } else {
            this.wasSitting = false;
        }

        this.searchCooldown = 0;
        findAndPathToWaterOrPanic();
    }

    @Override
    public void tick() {
        Level level = wolf.level();
        if (level == null) {
            return;
        }

        // If in water, the fire will extinguish rapidly
        if (wolf.isInWater()) {
            if (!wolf.isOnFire() && wolf.getRemainingFireTicks() <= 0) {
                wolf.getNavigation().stop();
                if (this.wasSitting) {
                    wolf.setOrderedToSit(true);
                    wolf.setInSittingPose(true);
                    this.wasSitting = false;
                }
                return;
            }
        }

        if (this.searchCooldown-- <= 0 || wolf.getNavigation().isDone()) {
            this.searchCooldown = 20; // Re-evaluate path/water every second
            findAndPathToWaterOrPanic();
        }

        // Distress audio & particle cues
        if (level instanceof ServerLevel serverLevel) {
            if (wolf.tickCount % 25 == 0) {
                SoundEvent whineSound = ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().whineSound().value();
                serverLevel.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(),
                        whineSound, wolf.getSoundSource(), 1.0f, 1.3f);
            }

            if (wolf.tickCount % 4 == 0) {
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        wolf.getRandomX(0.4), wolf.getRandomY() + 0.3, wolf.getRandomZ(0.4),
                        2, 0.05, 0.05, 0.05, 0.01);
            }
        }
    }

    @Override
    public void stop() {
        this.targetWaterPos = null;
        this.searchCooldown = 0;
        wolf.getNavigation().stop();

        if (this.wasSitting && !wolf.isOnFire() && wolf.getRemainingFireTicks() <= 0) {
            wolf.setOrderedToSit(true);
            wolf.setInSittingPose(true);
            this.wasSitting = false;
        }
    }

    private void findAndPathToWaterOrPanic() {
        BlockPos water = findNearestWater(wolf, 16, 4);
        if (water != null) {
            this.targetWaterPos = water;
            wolf.getNavigation().moveTo(water.getX() + 0.5D, water.getY() + 0.5D, water.getZ() + 0.5D, this.waterSprintSpeed);
        } else {
            this.targetWaterPos = null;
            Vec3 panicTarget = DefaultRandomPos.getPos(wolf, 8, 4);
            if (panicTarget != null) {
                wolf.getNavigation().moveTo(panicTarget.x, panicTarget.y, panicTarget.z, this.panicSpeed);
            }
        }
    }

    /**
     * Scans for the nearest accessible water block within the given horizontal and vertical bounds.
     */
    public static BlockPos findNearestWater(Wolf wolf, int horizontalRadius, int verticalRadius) {
        if (wolf == null || wolf.level() == null) {
            return null;
        }
        Level level = wolf.level();
        BlockPos origin = wolf.blockPosition();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockPos closest = null;
        double minDistanceSq = Double.MAX_VALUE;

        for (int y = -verticalRadius; y <= verticalRadius; y++) {
            for (int x = -horizontalRadius; x <= horizontalRadius; x++) {
                for (int z = -horizontalRadius; z <= horizontalRadius; z++) {
                    mutable.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (level.getFluidState(mutable).is(FluidTags.WATER)) {
                        double distSq = origin.distSqr(mutable);
                        if (distSq < minDistanceSq) {
                            // Ensure there's space above or water itself is enterable
                            BlockPos above = mutable.above();
                            if (!level.getBlockState(above).isSolidRender()) {
                                minDistanceSq = distSq;
                                closest = mutable.immutable();
                            }
                        }
                    }
                }
            }
        }
        return closest;
    }
}
