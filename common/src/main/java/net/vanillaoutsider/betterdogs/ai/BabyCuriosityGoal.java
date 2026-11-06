// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.block.state.BlockState;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.util.BabyCuriosityHelper;

/**
 * AI Goal for baby wolf exploratory curiosity.
 */
public class BabyCuriosityGoal extends Goal {

    private final Wolf wolf;
    private LivingEntity targetEntity;
    private BlockPos targetBlock;
    private int timer;
    private boolean playedFeedback = false;
    private final double speedModifier;

    public BabyCuriosityGoal(Wolf wolf, double speedModifier) {
        this.wolf = wolf;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!BabyCuriosityHelper.canExhibitCuriosity(this.wolf)) {
            return false;
        }

        WolfPersonality personality = WolfPersistentData.getPersistedPersonality(this.wolf);
        int delay = BabyCuriosityHelper.calculateCuriosityDelay(personality);
        if (delay <= 0) {
            return false;
        }

        if (this.wolf.getRandom().nextInt(reducedTickDelay(delay)) != 0) {
            return false;
        }

        return findSomethingInteresting();
    }

    private boolean findSomethingInteresting() {
        // 60% chance for entity, 40% for foliage block
        if (this.wolf.getRandom().nextFloat() < 0.6f) {
            List<LivingEntity> nearby = this.wolf.level().getEntitiesOfClass(
                    LivingEntity.class,
                    this.wolf.getBoundingBox().inflate(10.0D),
                    e -> BabyCuriosityHelper.isCuriousEntity(e, this.wolf)
            );
            if (!nearby.isEmpty()) {
                this.targetEntity = nearby.get(this.wolf.getRandom().nextInt(nearby.size()));
                this.targetBlock = null;
                return true;
            }
        }

        // Find interesting block within 6 blocks
        BlockPos origin = this.wolf.blockPosition();
        for (int i = 0; i < 30; i++) {
            BlockPos pos = origin.offset(
                    this.wolf.getRandom().nextInt(13) - 6,
                    this.wolf.getRandom().nextInt(5) - 2,
                    this.wolf.getRandom().nextInt(13) - 6
            );
            BlockState state = this.wolf.level().getBlockState(pos);
            if (BabyCuriosityHelper.isInterestingBlock(state)) {
                this.targetBlock = pos;
                this.targetEntity = null;
                return true;
            }
        }

        return false;
    }

    @Override
    public void start() {
        this.timer = 40 + this.wolf.getRandom().nextInt(80); // 2-6 seconds
        this.playedFeedback = false;
        if (this.targetEntity != null) {
            this.wolf.getNavigation().moveTo(this.targetEntity, this.speedModifier);
        } else if (this.targetBlock != null) {
            this.wolf.getNavigation().moveTo(this.targetBlock.getX() + 0.5D, this.targetBlock.getY(), this.targetBlock.getZ() + 0.5D, this.speedModifier);
        }
    }

    @Override
    public void tick() {
        if (this.targetEntity != null) {
            this.wolf.getLookControl().setLookAt(this.targetEntity, 30.0F, (float) this.wolf.getMaxHeadXRot());
            if (this.wolf.distanceToSqr(this.targetEntity) <= BabyCuriosityHelper.CLOSE_INSPECT_DISTANCE_SQ) {
                this.wolf.getNavigation().stop();
                if (!this.playedFeedback) {
                    BabyCuriosityHelper.playCuriosityFeedback(this.wolf);
                    this.playedFeedback = true;
                }
            }
        } else if (this.targetBlock != null) {
            this.wolf.getLookControl().setLookAt(this.targetBlock.getX() + 0.5D, this.targetBlock.getY() + 0.5D, this.targetBlock.getZ() + 0.5D, 30.0F, (float) this.wolf.getMaxHeadXRot());
            if (this.wolf.blockPosition().closerThan(this.targetBlock, 3.0D)) {
                this.wolf.getNavigation().stop();
                if (!this.playedFeedback) {
                    BabyCuriosityHelper.playCuriosityFeedback(this.wolf);
                    this.playedFeedback = true;
                }
            }
        }
        this.timer--;
    }

    @Override
    public boolean canContinueToUse() {
        return this.timer > 0 && (this.targetEntity == null || this.targetEntity.isAlive()) && !this.wolf.isOrderedToSit() && this.wolf.getTarget() == null;
    }

    @Override
    public void stop() {
        this.targetEntity = null;
        this.targetBlock = null;
        this.playedFeedback = false;
        this.wolf.getNavigation().stop();
    }
}
