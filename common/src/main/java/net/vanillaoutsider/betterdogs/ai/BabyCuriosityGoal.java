// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

/**
 * AI Goal for baby wolf curiosity.
 * Non-aggressive baby wolves wander towards and stare at nearby entities or inert objects (flowers, plants, leaves).
 */
public class BabyCuriosityGoal extends Goal {

    private final Wolf wolf;
    private LivingEntity targetEntity;
    private BlockPos targetBlock;
    private int timer;
    private final double speedModifier;

    public BabyCuriosityGoal(Wolf wolf, double speedModifier) {
        this.wolf = wolf;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isBaby()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE) {
                return false;
            }
        }
        if (this.wolf.getTarget() != null || this.wolf.isOrderedToSit() || this.wolf.isLeashed()) {
            return false;
        }
        if (this.wolf.getRandom().nextInt(reducedTickDelay(60)) != 0) {
            return false;
        }
        return findSomethingInteresting();
    }

    private boolean findSomethingInteresting() {
        if (this.wolf.getRandom().nextFloat() < 0.6f) {
            Predicate<LivingEntity> predicate = (e) -> e != this.wolf && e.isAlive() && this.wolf.distanceToSqr(e) < 100.0;
            List<LivingEntity> nearby = this.wolf.level().getEntitiesOfClass(LivingEntity.class, this.wolf.getBoundingBox().inflate(10.0), predicate);
            if (!nearby.isEmpty()) {
                this.targetEntity = nearby.get(this.wolf.getRandom().nextInt(nearby.size()));
                this.targetBlock = null;
                return true;
            }
        }

        BlockPos origin = this.wolf.blockPosition();
        for (int i = 0; i < 30; i++) {
            BlockPos pos = origin.offset(this.wolf.getRandom().nextInt(13) - 6, this.wolf.getRandom().nextInt(5) - 2, this.wolf.getRandom().nextInt(13) - 6);
            BlockState state = this.wolf.level().getBlockState(pos);
            if (isInterestingBlock(state)) {
                this.targetBlock = pos;
                this.targetEntity = null;
                return true;
            }
        }
        return false;
    }

    public static boolean isInterestingBlock(BlockState state) {
        if (state == null) {
            return false;
        }
        return state.is(Blocks.SHORT_GRASS) || state.is(Blocks.TALL_GRASS)
                || state.is(Blocks.FERN) || state.is(Blocks.LARGE_FERN)
                || state.is(Blocks.DANDELION) || state.is(Blocks.POPPY)
                || state.is(Blocks.BLUE_ORCHID) || state.is(Blocks.ALLIUM)
                || state.is(Blocks.AZURE_BLUET) || state.is(Blocks.RED_TULIP)
                || state.is(Blocks.ORANGE_TULIP) || state.is(Blocks.WHITE_TULIP)
                || state.is(Blocks.PINK_TULIP) || state.is(Blocks.OXEYE_DAISY)
                || state.is(Blocks.CORNFLOWER) || state.is(Blocks.LILY_OF_THE_VALLEY)
                || state.is(Blocks.WITHER_ROSE) || state.is(Blocks.SUNFLOWER)
                || state.is(Blocks.LILAC) || state.is(Blocks.ROSE_BUSH)
                || state.is(Blocks.PEONY) || state.is(Blocks.PUMPKIN)
                || state.is(Blocks.MELON) || state.is(Blocks.SWEET_BERRY_BUSH)
                || state.is(Blocks.OAK_LEAVES) || state.is(Blocks.BIRCH_LEAVES)
                || state.is(Blocks.SPRUCE_LEAVES) || state.is(Blocks.JUNGLE_LEAVES)
                || state.is(Blocks.ACACIA_LEAVES) || state.is(Blocks.DARK_OAK_LEAVES)
                || state.is(Blocks.MANGROVE_LEAVES) || state.is(Blocks.AZALEA_LEAVES)
                || state.is(Blocks.FLOWERING_AZALEA_LEAVES) || state.is(Blocks.CHERRY_LEAVES)
                || state.is(Blocks.PALE_OAK_LEAVES);
    }

    @Override
    public void start() {
        this.timer = 40 + this.wolf.getRandom().nextInt(80);
        if (this.targetEntity != null) {
            this.wolf.getNavigation().moveTo(this.targetEntity, this.speedModifier);
        } else if (this.targetBlock != null) {
            this.wolf.getNavigation().moveTo(this.targetBlock.getX() + 0.5, this.targetBlock.getY(), this.targetBlock.getZ() + 0.5, this.speedModifier);
        }
    }

    @Override
    public void tick() {
        if (this.targetEntity != null) {
            this.wolf.getLookControl().setLookAt(this.targetEntity, 30.0f, (float) this.wolf.getMaxHeadXRot());
            if (this.wolf.distanceToSqr(this.targetEntity) < 6.25) {
                this.wolf.getNavigation().stop();
            }
        } else if (this.targetBlock != null) {
            this.wolf.getLookControl().setLookAt(this.targetBlock.getX() + 0.5, this.targetBlock.getY() + 0.5, this.targetBlock.getZ() + 0.5, 30.0f, (float) this.wolf.getMaxHeadXRot());
            if (this.wolf.blockPosition().closerThan(this.targetBlock, 3.0)) {
                this.wolf.getNavigation().stop();
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
        this.wolf.getNavigation().stop();
    }
}
