// Verified against: WolfSafetyMixin.java (26.1.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to handle cliff safety behavior for wolves.
 * Optimizes object allocations by using a reusable MutableBlockPos.
 */
@Mixin(Wolf.class)
public abstract class WolfSafetyMixin {

    @Unique
    private final BlockPos.MutableBlockPos betterdogs$mutablePos = new BlockPos.MutableBlockPos();

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$onTickSafety(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        if (!wolf.level().isClientSide() && wolf.isTame()) {
            betterdogs$checkTargetCliffSafety();
            betterdogs$checkMovementCliffSafety();
        }
    }

    @Unique
    private void betterdogs$checkMovementCliffSafety() {
        Wolf wolf = (Wolf) (Object) this;
        if (!DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_CLIFF_SAFETY))
            return;

        if (wolf.getDeltaMovement().horizontalDistanceSqr() < 0.0001)
            return;

        Vec3 velocity = wolf.getDeltaMovement();
        Vec3 lookaheadPos = wolf.position().add(velocity.scale(5.0));
        
        int hazardX = net.minecraft.util.Mth.floor(lookaheadPos.x);
        int hazardY = net.minecraft.util.Mth.floor(lookaheadPos.y);
        int hazardZ = net.minecraft.util.Mth.floor(lookaheadPos.z);

        net.minecraft.world.level.Level level = wolf.level();

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
    private void betterdogs$checkTargetCliffSafety() {
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.getTarget() == null)
            return;

        if (!DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_CLIFF_SAFETY))
            return;

        double yDiff = wolf.getY() - wolf.getTarget().getY();
        boolean dangerDetected = false;

        if (yDiff > 3.0 && wolf.onGround()) {
            dangerDetected = true;
        } else if (!wolf.getTarget().onGround()) {
            boolean groundFound = false;
            LivingEntity target = wolf.getTarget();
            int targetX = net.minecraft.util.Mth.floor(target.getX());
            int targetY = net.minecraft.util.Mth.floor(target.getY());
            int targetZ = net.minecraft.util.Mth.floor(target.getZ());
            for (int i = 1; i <= 4; i++) {
                this.betterdogs$mutablePos.set(targetX, targetY - i, targetZ);
                if (!wolf.level().isEmptyBlock(this.betterdogs$mutablePos)) {
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
            Vec3 targetPos = wolf.getTarget().position();
            wolf.setTarget(null);
            Vec3 retreatPos = DefaultRandomPos.getPosAway(wolf, 4, 1, targetPos);
            if (retreatPos != null) {
                wolf.getNavigation().moveTo(retreatPos.x, retreatPos.y, retreatPos.z, 1.0);
            }
        }
    }
}
