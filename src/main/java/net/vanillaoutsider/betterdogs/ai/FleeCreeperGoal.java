// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.ai;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * AI Goal for tamed wolves to flee from swelling/ignited creepers at 1.5x sprint speed.
 */
public class FleeCreeperGoal extends AvoidEntityGoal<Creeper> {

    private final Wolf wolf;

    public FleeCreeperGoal(Wolf wolf) {
        super(wolf, Creeper.class, 10.0f, 1.5, 1.5,
                (livingEntity) -> {
                    if (livingEntity instanceof Creeper creeper) {
                        return creeper.isIgnited() || creeper.getSwellDir() > 0;
                    }
                    return false;
                });
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        // Only for tamed wolves
        if (!wolf.isTame()) {
            return false;
        }

        // Sitting, leashed, or sentry/guard dogs cannot flee
        if (wolf.isOrderedToSit() || wolf.isLeashed()) {
            return false;
        }

        if (wolf instanceof WolfExtensions ext && (ext.betterdogs$isGuardMode() || ext.betterdogs$isSittingManually())) {
            return false;
        }

        // Verify GameRule is enabled
        if (!DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_CREEPER_AWARENESS) &&
            !DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_CREEPER_EVASION_ENABLED)) {
            return false;
        }

        return super.canUse();
    }

    @Override
    public void tick() {
        super.tick();
        // Spawn emergency sprint smoke trails at the feet of the fleeing dog
        if (this.toAvoid != null && this.wolf.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SMOKE,
                    this.wolf.getX(), this.wolf.getY() + 0.1, this.wolf.getZ(),
                    3, 0.1, 0.05, 0.1, 0.02
            );
        }
    }
}
