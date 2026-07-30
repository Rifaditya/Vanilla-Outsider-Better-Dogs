// Verified against: WolfStormAnxietyGoal.java (26.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.ai;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import java.util.EnumSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

public class WolfStormAnxietyGoal extends Goal {

    private final Wolf wolf;

    public WolfStormAnxietyGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_STORM_ANXIETY)) return false;
        if (!wolf.isTame()) return false;
        if (!wolf.level().isThundering()) return false;

        // Check soothed state (soothing lasts 12000 ticks / 10 minutes)
        long soothedTime = ((WolfExtensions) wolf).betterdogs$getSoothedTime();
        if (wolf.level().getGameTime() - soothedTime < 12000L) return false;

        WolfExtensions ext = (WolfExtensions) wolf;
        WolfPersonality personality = ext.betterdogs$hasPersonality() 
                ? ext.betterdogs$getPersonality() 
                : WolfPersonality.NORMAL;

        float multiplier = switch (personality) {
            case PACIFIST -> 3.0f;
            case NORMAL -> 1.0f;
            case AGGRESSIVE -> 0.0f;
        };

        if (multiplier <= 0.0f) return false;
        return wolf.getRandom().nextFloat() < (BetterDogsConfig.get().stormAnxietyTriggerChance * multiplier);
    }

    @Override
    public boolean canContinueToUse() {
        if (!DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_STORM_ANXIETY)) return false;
        
        // Check soothed state
        long soothedTime = ((WolfExtensions) wolf).betterdogs$getSoothedTime();
        if (wolf.level().getGameTime() - soothedTime < 12000L) return false;

        BetterDogsConfig config = BetterDogsConfig.get();
        return wolf.level().isThundering() && wolf.getRandom().nextFloat() < (1.0f - config.getStormAnxietyStopChance());
    }

    @Override
    public void start() {
        BetterDogsConfig config = BetterDogsConfig.get();
        if (!wolf.isOrderedToSit()) {
            net.minecraft.core.BlockPos shelterPos = findShelterTarget();
            if (shelterPos != null) {
                wolf.getNavigation().moveTo(shelterPos.getX(), shelterPos.getY(), shelterPos.getZ(), 1.0);
            } else {
                Vec3 target = DefaultRandomPos.getPos(wolf, config.getStormAnxietyPaceRange(), config.getStormAnxietyPaceVerticalRange());
                if (target != null) {
                    wolf.getNavigation().moveTo(target.x, target.y, target.z, 1.0);
                }
            }
        }
    }

    @Override
    public void tick() {
        BetterDogsConfig config = BetterDogsConfig.get();
        if (wolf.getRandom().nextFloat() < config.stormWhineChance) {
            wolf.playSound(SoundEvents.GENERIC_HURT, 1.0f, 2.0f);
        }

        if (!wolf.isOrderedToSit() && wolf.getNavigation().isDone()) {
            net.minecraft.core.BlockPos shelterPos = findShelterTarget();
            if (shelterPos != null) {
                wolf.getNavigation().moveTo(shelterPos.getX(), shelterPos.getY(), shelterPos.getZ(), 1.0);
            } else {
                Vec3 target = DefaultRandomPos.getPos(wolf, config.getStormAnxietyPaceRange(), config.getStormAnxietyPaceVerticalRange());
                if (target != null) {
                    wolf.getNavigation().moveTo(target.x, target.y, target.z, 1.0);
                }
            }
        }

        if (wolf.getRandom().nextFloat() < config.getStormAnxietyLookChance()) {
            double spread = config.getStormAnxietyLookSpread();
            wolf.getLookControl().setLookAt(
                    wolf.getX() + (wolf.getRandom().nextDouble() - 0.5) * spread,
                    wolf.getEyeY(),
                    wolf.getZ() + (wolf.getRandom().nextDouble() - 0.5) * spread,
                    10.0f,
                    wolf.getMaxHeadXRot());
        }
    }

    @Override
    public void stop() {
        wolf.getNavigation().stop();
    }

    private net.minecraft.core.BlockPos findShelterTarget() {
        net.minecraft.world.entity.LivingEntity owner = wolf.getOwner();
        net.minecraft.core.BlockPos basePos = (owner != null && wolf.distanceToSqr(owner) < 1024.0) 
                ? owner.blockPosition() 
                : wolf.blockPosition();

        if (!wolf.level().canSeeSky(basePos) && isSafeStandBlock(basePos)) {
            if (owner != null && wolf.distanceToSqr(owner) <= 9.0) {
                return wolf.blockPosition();
            }
            return basePos;
        }

        net.minecraft.core.BlockPos.MutableBlockPos mutable = new net.minecraft.core.BlockPos.MutableBlockPos();
        net.minecraft.core.BlockPos bestPos = null;
        double bestDistance = Double.MAX_VALUE;

        for (int x = -12; x <= 12; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -12; z <= 12; z++) {
                    mutable.set(basePos.getX() + x, basePos.getY() + y, basePos.getZ() + z);
                    if (!wolf.level().hasChunkAt(mutable)) continue;
                    if (!wolf.level().canSeeSky(mutable) && isSafeStandBlock(mutable)) {
                        double dist = wolf.blockPosition().distSqr(mutable);
                        if (dist < bestDistance) {
                            bestDistance = dist;
                            bestPos = mutable.immutable();
                        }
                    }
                }
            }
        }
        return bestPos;
    }

    private boolean isSafeStandBlock(net.minecraft.core.BlockPos pos) {
        net.minecraft.world.level.Level level = wolf.level();
        return level.isEmptyBlock(pos) 
                && !level.isEmptyBlock(pos.below()) 
                && !level.getBlockState(pos.below()).is(net.minecraft.world.level.block.Blocks.LAVA)
                && !level.getBlockState(pos.below()).is(net.minecraft.world.level.block.Blocks.MAGMA_BLOCK)
                && !level.getBlockState(pos.below()).is(net.minecraft.world.level.block.Blocks.FIRE)
                && !level.getBlockState(pos.below()).is(net.minecraft.world.level.block.Blocks.SOUL_FIRE);
    }
}
