// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.ai;

import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.WanderlustHelper;
import org.jspecify.annotations.Nullable;

/**
 * Dedicated single-purpose AI Goal for autonomous exploratory roaming surges (Wanderlust).
 */
public class WanderlustGoal extends WaterAvoidingRandomStrollGoal {

    private final Wolf wolf;

    public WanderlustGoal(Wolf wolf, double speedModifier) {
        super(wolf, speedModifier);
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        if (!WanderlustHelper.isEligibleForWanderlust(this.wolf)) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext) {
            EntitySocialScheduler scheduler = ext.betterdogs$getScheduler();
            if (scheduler != null && scheduler.isEventActive("wanderlust")) {
                return super.canUse();
            }
        }

        if (WanderlustHelper.shouldTriggerWanderlust(this.wolf, this.wolf.getRandom())) {
            return super.canUse();
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (!WanderlustHelper.isEligibleForWanderlust(this.wolf)) {
            return false;
        }
        return super.canContinueToUse();
    }

    @Override
    protected @Nullable Vec3 getPosition() {
        if (this.wolf.level().getFluidState(this.wolf.blockPosition()).is(FluidTags.WATER)) {
            return super.getPosition();
        }

        LivingEntity owner = this.wolf.getOwner();
        if (owner != null) {
            Vec3 pos = WanderlustHelper.calculateWanderlustPosition(this.wolf, owner);
            if (pos != null) {
                return pos;
            }
        }

        return super.getPosition();
    }
}
