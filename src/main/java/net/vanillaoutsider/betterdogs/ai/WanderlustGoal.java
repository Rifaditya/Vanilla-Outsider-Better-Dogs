package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.scheduler.events.WanderlustDogEvent;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import org.jspecify.annotations.Nullable;

public class WanderlustGoal extends WaterAvoidingRandomStrollGoal {

    private final Wolf wolf;

    public WanderlustGoal(Wolf wolf, double speedModifier) {
        super(wolf, speedModifier);
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        if (this.wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getScheduler().isEventActive(WanderlustDogEvent.ID)) {
                 return true;
            }
        }
        return false;
    }

    @Override
    protected @Nullable Vec3 getPosition() {
        if (this.wolf.level().getFluidState(this.wolf.blockPosition()).is(net.minecraft.tags.FluidTags.WATER)) {
             return super.getPosition(); 
        }
        BetterDogsConfig config = BetterDogsConfig.get();
        return DefaultRandomPos.getPos(this.wolf, config.getWanderlustRange(), config.getWanderlustVerticalRange());
    }
}
