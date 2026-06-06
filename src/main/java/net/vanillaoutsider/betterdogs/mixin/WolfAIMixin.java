// Verified against: Wolf.java (26.1.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.ai.AdultCorrectionGoal;
import net.vanillaoutsider.betterdogs.ai.AggressiveTargetGoal;
import net.vanillaoutsider.betterdogs.ai.MoveToVehicleGoal;
import net.vanillaoutsider.betterdogs.ai.AvoidHazardsGoal;
import net.vanillaoutsider.betterdogs.ai.BabyBiteBackGoal;
import net.vanillaoutsider.betterdogs.ai.BabyCuriosityGoal;
import net.vanillaoutsider.betterdogs.ai.BabyMischiefGoal;
import net.vanillaoutsider.betterdogs.ai.BeggingGoal;
import net.vanillaoutsider.betterdogs.ai.BloodFeudGoal;
import net.vanillaoutsider.betterdogs.ai.EatGroundFoodGoal;
import net.vanillaoutsider.betterdogs.ai.FleeCreeperGoal;
import net.vanillaoutsider.betterdogs.ai.GroupHowlGoal;
import net.vanillaoutsider.betterdogs.ai.PacifistRevengeGoal;
import net.vanillaoutsider.betterdogs.ai.PersonalityFollowOwnerGoal;
import net.vanillaoutsider.betterdogs.ai.SmallFightGoal;
import net.vanillaoutsider.betterdogs.ai.WanderlustGoal;
import net.vanillaoutsider.betterdogs.ai.WildWolfHuntGoal;
import net.vanillaoutsider.betterdogs.ai.WildWolfPackWarGoal;
import net.vanillaoutsider.betterdogs.ai.WildWolfTerritorialGoal;
import net.vanillaoutsider.betterdogs.ai.WolfFetchGoal;
import net.vanillaoutsider.betterdogs.ai.WolfGiftGoal;
import net.vanillaoutsider.betterdogs.ai.WolfGuardGoal;
import net.vanillaoutsider.betterdogs.ai.WolfStormAnxietyGoal;
import net.vanillaoutsider.betterdogs.ai.ZoomiesGoal;
import net.vanillaoutsider.betterdogs.ai.group.WildWolfFollowLeaderGoal;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to register custom goals and attributes for the Wolf entity.
 */
@Mixin(Wolf.class)
public abstract class WolfAIMixin extends TamableAnimal {

    protected WolfAIMixin() {
        super(null, null);
    }

    /**
     * Add knockback resistance to wolves (50% reduction)
     */
    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void betterdogs$addKnockbackResistance(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.getReturnValue().add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void betterdogs$registerCustomGoals(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;

        this.goalSelector.addGoal(2, new WolfGuardGoal(wolf));
        this.goalSelector.addGoal(8, new WolfGiftGoal(wolf));
        this.goalSelector.addGoal(1, new FleeCreeperGoal(wolf));
        this.goalSelector.addGoal(1, new AvoidHazardsGoal(wolf));
        this.goalSelector.addGoal(1, new MoveToVehicleGoal(wolf));
        this.goalSelector.addGoal(3, new EatGroundFoodGoal(wolf));
        this.goalSelector.addGoal(4, new WildWolfTerritorialGoal(wolf));
        this.goalSelector.addGoal(4, new WildWolfPackWarGoal(wolf));

        this.targetSelector.addGoal(2, new AggressiveTargetGoal(wolf));
        this.targetSelector.addGoal(2, new PacifistRevengeGoal(wolf));

        this.targetSelector.addGoal(1, new BloodFeudGoal(wolf));
        this.goalSelector.addGoal(0, new BabyBiteBackGoal(wolf));
        this.goalSelector.addGoal(4, new AdultCorrectionGoal(wolf));
        this.goalSelector.addGoal(4, new SmallFightGoal(wolf));
        this.goalSelector.addGoal(5, new BabyMischiefGoal(wolf));
        this.goalSelector.addGoal(6, new ZoomiesGoal(wolf));
        this.goalSelector.addGoal(7, new BeggingGoal(wolf));
        this.goalSelector.addGoal(7, new WolfFetchGoal(wolf));

        Set<WrappedGoal> goalsToRemove = new HashSet<>();
        for (WrappedGoal goal : this.targetSelector.getAvailableGoals()) {
            if (goal.getGoal().toString().contains("NonTameRandomTargetGoal")) {
                goalsToRemove.add(goal);
            }
        }
        for (WrappedGoal goal : goalsToRemove) {
            this.targetSelector.removeGoal(goal.getGoal());
        }

        Set<WrappedGoal> followGoalsToRemove = new HashSet<>();
        for (WrappedGoal goal : this.goalSelector.getAvailableGoals()) {
            if (goal.getGoal() instanceof FollowOwnerGoal) {
                followGoalsToRemove.add(goal);
            }
        }
        for (WrappedGoal goal : followGoalsToRemove) {
            this.goalSelector.removeGoal(goal.getGoal());
        }

        this.goalSelector.addGoal(6, new PersonalityFollowOwnerGoal(wolf, 1.0, false));

        TargetingConditions.Selector preySelector = (entity, level) -> entity instanceof Sheep
                || entity instanceof Rabbit || entity instanceof Chicken;

        this.targetSelector.addGoal(4, new WildWolfHuntGoal<>(
                wolf,
                Animal.class,
                false,
                preySelector));

        if (DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_STORM_ANXIETY)) {
            this.goalSelector.addGoal(6, new WolfStormAnxietyGoal(wolf));
        }

        this.goalSelector.addGoal(7, new GroupHowlGoal(wolf));
        this.goalSelector.addGoal(7, new BabyCuriosityGoal(wolf, 0.8));
        this.goalSelector.addGoal(8, new WanderlustGoal(wolf, 1.0));
        
        // DasikLibrary Wild Pack Group Logic (Disabled automatically if Tamed)
        this.goalSelector.addGoal(5, new WildWolfFollowLeaderGoal(wolf));
    }
}
