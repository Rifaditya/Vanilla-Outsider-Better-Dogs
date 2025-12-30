package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.ai.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Wolf.class)
public abstract class WolfGoalsMixin extends Mob {

    // Dummy constructor required for extending Mob
    protected WolfGoalsMixin() {
        super(null, null);
    }

    /**
     * Inject custom AI goals into the wolf's goal selector
     */
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void betterdogs$addCustomGoals(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;

        // High priority goals (lower number = higher priority)

        // Safety: Flee from creepers (tamed only) - priority 1
        this.goalSelector.addGoal(1, new FleeCreeperGoal(wolf));

        // Safety: Avoid hazards (all wolves) - priority 1
        this.goalSelector.addGoal(1, new AvoidHazardsGoal(wolf));

        // Wild wolf: Eat food from ground - priority 3
        this.goalSelector.addGoal(3, new EatGroundFoodGoal(wolf));

        // Target goals

        // Aggressive personality: Auto-target hostiles - priority 2
        this.targetSelector.addGoal(2, new AggressiveTargetGoal(wolf));

        // Pacifist personality: Revenge for owner - priority 2
        this.targetSelector.addGoal(2, new PacifistRevengeGoal(wolf));

        // Wild wolf: Hunt when hurt - priority 3
        this.targetSelector.addGoal(3, new HuntWhenHurtGoal(wolf));
    }
}
