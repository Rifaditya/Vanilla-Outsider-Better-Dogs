// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Creeper;

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
        if (!this.wolf.isTame() || this.wolf.isOrderedToSit() || this.wolf.isLeashed()) {
            return false;
        }
        return super.canUse();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.toAvoid != null && this.wolf.getCommandSenderWorld() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SMOKE,
                    this.wolf.getX(), this.wolf.getY() + 0.1, this.wolf.getZ(),
                    3, 0.1, 0.05, 0.1, 0.02
            );
        }
    }
}
