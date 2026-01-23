package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

import java.util.EnumSet;

/**
 * AI Goal for Wolf anxiety during thunderstorms.
 * - Whines occasionally.
 * - Shakes (uses wet dog shake animation as fear response).
 * - Paces around nervously (if not sitting).
 */
public class WolfStormAnxietyGoal extends Goal {

    private final Wolf wolf;

    public WolfStormAnxietyGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Only trigger if tamed
        if (!wolf.isTame())
            return false;

        // Only during thunderstorms
        if (!wolf.level().isThundering())
            return false;

        // Random chance to start being anxious (1% per tick)
        return wolf.getRandom().nextFloat() < BetterDogsConfig.get().stormAnxietyTriggerChance;
    }

    @Override
    public boolean canContinueToUse() {
        // Continue as long as it thundering and we haven't finished our "anxiety
        // episode"
        return wolf.level().isThundering() && wolf.getRandom().nextFloat() < 0.98; // Small chance to stop
    }

    @Override
    public void start() {
        // If not sitting, maybe pick a random spot to pace to
        if (!wolf.isOrderedToSit()) {
            Vec3 target = DefaultRandomPos.getPos(wolf, 3, 2);
            if (target != null) {
                wolf.getNavigation().moveTo(target.x, target.y, target.z, 1.0);
            }
        }
    }

    @Override
    public void tick() {
        BetterDogsConfig config = BetterDogsConfig.get();
        // 1. Whining Sound (Low chance)
        if (wolf.getRandom().nextFloat() < config.stormWhineChance) { // 2% chance per tick
            // Try standard GENERIC_HURT if available
            wolf.playSound(SoundEvents.GENERIC_HURT, 1.0f, 2.0f);
        }

        // 2. Shaking (Visual fear) - move head around nervously
        if (wolf.getRandom().nextFloat() < 0.05) {
            wolf.getLookControl().setLookAt(
                    wolf.getX() + (wolf.getRandom().nextDouble() - 0.5) * 10,
                    wolf.getEyeY(),
                    wolf.getZ() + (wolf.getRandom().nextDouble() - 0.5) * 10,
                    10.0f,
                    wolf.getMaxHeadXRot());
        }
    }

    @Override
    public void stop() {
        wolf.getNavigation().stop();
    }
}
