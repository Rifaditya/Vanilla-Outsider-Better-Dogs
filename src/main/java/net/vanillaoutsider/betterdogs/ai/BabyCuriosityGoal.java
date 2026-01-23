package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

/**
 * AI Goal for baby wolf curiosity.
 * Passive baby wolves will wander towards and stare at nearby entities or "inert objects" (blocks like grass, flowers).
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
        if (!wolf.isBaby()) return false;
        
        // Only non-aggressive personalities exhibit curiosity
        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE) return false;
        }

        // Don't trigger if busy
        if (wolf.getTarget() != null || wolf.isOrderedToSit() || wolf.isLeashed()) return false;

        // Chance to trigger curiosity (about once every 5-10 seconds of idle time)
        if (wolf.getRandom().nextInt(reducedTickDelay(60)) != 0) return false;

        // Try to find something interesting
        return findSomethingInteresting();
    }

    private boolean findSomethingInteresting() {
        // 60% chance for entity, 40% for block
        if (wolf.getRandom().nextFloat() < 0.6f) {
            // Find nearby entity (passive mob, monster, or player) within 10 blocks
            Predicate<LivingEntity> predicate = (e) -> e != wolf && e.isAlive() && wolf.distanceToSqr(e) < 100.0;
            List<LivingEntity> nearby = wolf.level().getEntitiesOfClass(LivingEntity.class, wolf.getBoundingBox().inflate(10.0), predicate);
            if (!nearby.isEmpty()) {
                targetEntity = nearby.get(wolf.getRandom().nextInt(nearby.size()));
                targetBlock = null;
                return true;
            }
        }

        // Find interesting block (Grass, Flowers, Leaves, etc.) within 6 blocks
        BlockPos origin = wolf.blockPosition();
        for (int i = 0; i < 30; i++) {
            BlockPos pos = origin.offset(wolf.getRandom().nextInt(13) - 6, wolf.getRandom().nextInt(5) - 2, wolf.getRandom().nextInt(13) - 6);
            BlockState state = wolf.level().getBlockState(pos);
            if (isInterestingBlock(state)) {
                targetBlock = pos;
                targetEntity = null;
                return true;
            }
        }

        return false;
    }

    private boolean isInterestingBlock(BlockState state) {
        // Trees (Leaves), Grass, Flowers, and other "inert objects"
        return state.is(Blocks.TALL_GRASS) || state.is(Blocks.SHORT_GRASS) || 
               state.is(Blocks.FERN) || state.is(Blocks.LARGE_FERN) ||
               state.is(Blocks.DANDELION) || state.is(Blocks.POPPY) ||
               state.is(Blocks.BLUE_ORCHID) || state.is(Blocks.ALLIUM) ||
               state.is(Blocks.AZURE_BLUET) || state.is(Blocks.RED_TULIP) ||
               state.is(Blocks.ORANGE_TULIP) || state.is(Blocks.WHITE_TULIP) ||
               state.is(Blocks.PINK_TULIP) || state.is(Blocks.OXEYE_DAISY) ||
               state.is(Blocks.CORNFLOWER) || state.is(Blocks.LILY_OF_THE_VALLEY) ||
               state.is(Blocks.WITHER_ROSE) || state.is(Blocks.SUNFLOWER) ||
               state.is(Blocks.LILAC) || state.is(Blocks.ROSE_BUSH) ||
               state.is(Blocks.PEONY) || state.is(Blocks.PUMPKIN) ||
               state.is(Blocks.MELON) || state.is(Blocks.SWEET_BERRY_BUSH) ||
               state.is(Blocks.OAK_LEAVES) || state.is(Blocks.BIRCH_LEAVES) ||
               state.is(Blocks.SPRUCE_LEAVES) || state.is(Blocks.JUNGLE_LEAVES) ||
               state.is(Blocks.ACACIA_LEAVES) || state.is(Blocks.DARK_OAK_LEAVES) ||
               state.is(Blocks.MANGROVE_LEAVES) || state.is(Blocks.AZALEA_LEAVES) ||
               state.is(Blocks.FLOWERING_AZALEA_LEAVES) || state.is(Blocks.CHERRY_LEAVES);
    }

    @Override
    public void start() {
        timer = 40 + wolf.getRandom().nextInt(80); // 2-6 seconds
        if (targetEntity != null) {
            wolf.getNavigation().moveTo(targetEntity, speedModifier);
        } else if (targetBlock != null) {
            wolf.getNavigation().moveTo(targetBlock.getX() + 0.5, targetBlock.getY(), targetBlock.getZ() + 0.5, speedModifier);
        }
    }

    @Override
    public void tick() {
        if (targetEntity != null) {
            wolf.getLookControl().setLookAt(targetEntity, 30.0f, (float) wolf.getMaxHeadXRot());
            if (wolf.distanceToSqr(targetEntity) < 6.25) { // Within 2.5 blocks
                wolf.getNavigation().stop();
            }
        } else if (targetBlock != null) {
            wolf.getLookControl().setLookAt(targetBlock.getX() + 0.5, targetBlock.getY() + 0.5, targetBlock.getZ() + 0.5, 30.0f, (float) wolf.getMaxHeadXRot());
            if (wolf.blockPosition().closerThan(targetBlock, 3.0)) {
                wolf.getNavigation().stop();
            }
        }
        timer--;
    }

    @Override
    public boolean canContinueToUse() {
        return timer > 0 && (targetEntity == null || targetEntity.isAlive()) && !wolf.isOrderedToSit() && wolf.getTarget() == null;
    }

    @Override
    public void stop() {
        targetEntity = null;
        targetBlock = null;
        wolf.getNavigation().stop();
    }
}
