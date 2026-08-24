// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfHazardHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to handle cliff safety and thermal hazard reactions for wolves.
 * Optimizes object allocations by using a reusable MutableBlockPos.
 */
@Mixin(Wolf.class)
public abstract class WolfSafetyMixin {

    @Unique
    private final BlockPos.MutableBlockPos betterdogs$mutablePos = new BlockPos.MutableBlockPos();

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$onTickSafety(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        Level level = wolf.level();
        if (level != null && !level.isClientSide() && wolf.isTame()) {
            betterdogs$checkTargetCliffSafety(wolf, level);
            betterdogs$checkMovementCliffSafety(wolf, level);
            betterdogs$checkEmergencyThermalSafety(wolf, level);
        }
    }

    @Unique
    private void betterdogs$checkEmergencyThermalSafety(Wolf wolf, Level level) {
        if (!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_CLIFF_SAFETY, true)) {
            return;
        }

        BlockPos currentPos = wolf.blockPosition();

        if (wolf.isOnFire()) {
            BlockPos waterPos = WolfHazardHelper.findNearbyWater(level, currentPos, 6);
            if (waterPos != null) {
                wolf.getNavigation().moveTo(waterPos.getX() + 0.5, waterPos.getY(), waterPos.getZ() + 0.5, 1.4);
                return;
            }
        }

        this.betterdogs$mutablePos.set(currentPos.getX(), currentPos.getY() - 1, currentPos.getZ());
        if (WolfHazardHelper.isDirectHazard(level, currentPos) || WolfHazardHelper.isDirectHazard(level, this.betterdogs$mutablePos)) {
            wolf.getNavigation().stop();
            Vec3 escapeImpulse = WolfHazardHelper.calculateEscapeVector(wolf, currentPos);
            wolf.setDeltaMovement(escapeImpulse);
        }
    }

    @Unique
    private void betterdogs$checkMovementCliffSafety(Wolf wolf, Level level) {
        if (!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_CLIFF_SAFETY, true)) {
            return;
        }

        if (wolf.getDeltaMovement().horizontalDistanceSqr() < 0.0001) {
            return;
        }

        Vec3 velocity = wolf.getDeltaMovement();
        int hazardX = Mth.floor(wolf.getX() + velocity.x * 5.0);
        int hazardY = Mth.floor(wolf.getY() + velocity.y * 5.0);
        int hazardZ = Mth.floor(wolf.getZ() + velocity.z * 5.0);

        boolean solidGround = false;
        for (int i = 0; i <= 3; i++) {
            this.betterdogs$mutablePos.set(hazardX, hazardY - i, hazardZ);
            if (!level.isEmptyBlock(this.betterdogs$mutablePos)) {
                solidGround = true;
                break;
            }
        }

        if (!solidGround) {
            wolf.getNavigation().stop();
            wolf.setDeltaMovement(Vec3.ZERO);
            wolf.setShiftKeyDown(true);
        }
    }

    @Unique
    private void betterdogs$checkTargetCliffSafety(Wolf wolf, Level level) {
        LivingEntity target = wolf.getTarget();
        if (target == null) {
            return;
        }

        if (!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_CLIFF_SAFETY, true)) {
            return;
        }

        double yDiff = wolf.getY() - target.getY();
        boolean dangerDetected = false;

        if (yDiff > 3.0 && wolf.onGround()) {
            dangerDetected = true;
        } else if (!target.onGround()) {
            boolean groundFound = false;
            int targetX = Mth.floor(target.getX());
            int targetY = Mth.floor(target.getY());
            int targetZ = Mth.floor(target.getZ());
            for (int i = 1; i <= 4; i++) {
                this.betterdogs$mutablePos.set(targetX, targetY - i, targetZ);
                if (!level.isEmptyBlock(this.betterdogs$mutablePos)) {
                    groundFound = true;
                    break;
                }
            }
            if (!groundFound) {
                dangerDetected = true;
            }
        }

        if (dangerDetected) {
            wolf.getNavigation().stop();
            Vec3 targetPos = target.position();
            wolf.setTarget(null);
            Vec3 retreatPos = DefaultRandomPos.getPosAway(wolf, 4, 1, targetPos);
            if (retreatPos != null) {
                wolf.getNavigation().moveTo(retreatPos.x, retreatPos.y, retreatPos.z, 1.0);
            }
        }
    }
}
