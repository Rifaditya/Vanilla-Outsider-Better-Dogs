package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

import java.util.EnumSet;

public class WolfStormAnxietyGoal extends Goal {

    private final Wolf wolf;

    public WolfStormAnxietyGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!wolf.isTame()) return false;
        if (!wolf.level().isThundering()) return false;
        return wolf.getRandom().nextFloat() < BetterDogsConfig.get().stormAnxietyTriggerChance;
    }

    @Override
    public boolean canContinueToUse() {
        BetterDogsConfig config = BetterDogsConfig.get();
        return wolf.level().isThundering() && wolf.getRandom().nextFloat() < (1.0f - config.getStormAnxietyStopChance());
    }

    @Override
    public void start() {
        BetterDogsConfig config = BetterDogsConfig.get();
        if (!wolf.isOrderedToSit()) {
            Vec3 target = DefaultRandomPos.getPos(wolf, config.getStormAnxietyPaceRange(), config.getStormAnxietyPaceVerticalRange());
            if (target != null) {
                wolf.getNavigation().moveTo(target.x, target.y, target.z, 1.0);
            }
        }
    }

    @Override
    public void tick() {
        BetterDogsConfig config = BetterDogsConfig.get();
        if (wolf.getRandom().nextFloat() < config.stormWhineChance) {
            wolf.playSound(SoundEvents.GENERIC_HURT, 1.0f, 2.0f);
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
}
