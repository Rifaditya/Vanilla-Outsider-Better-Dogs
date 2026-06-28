// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.WolfPersistentData;

import java.util.Optional;
import java.util.UUID;

public class WolfFlankAttackGoal extends MeleeAttackGoal {
    private final Wolf wolf;
    private int flankPathTimer = 0;

    public WolfFlankAttackGoal(Wolf wolf, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(wolf, speedModifier, followingTargetEvenIfNotSeen);
        this.wolf = wolf;
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.wolf.getTarget();
        if (target == null) {
            return;
        }

        // If the flanking GameRule is disabled, just use the standard MeleeAttackGoal logic.
        if (!net.dasik.social.api.gamerule.DynamicGameRuleManager.getBoolean(this.wolf.level(), BetterDogsGameRules.BD_PACK_FLANKING_TACTICS)) {
            return;
        }

        // If we are close enough to strike, or almost in strike range, don't try to flank anymore.
        // MeleeAttackRange is usually ~4.0 sqr. We use 9.0 to give them 3 blocks to charge in.
        if (this.wolf.distanceToSqr(target) <= 9.0) {
            return;
        }

        // Only followers attempt to flank. The leader/lone wolf attacks directly.
        Optional<UUID> leaderUuid = WolfPersistentData.getWolfData(this.wolf).leaderUuid();
        if (leaderUuid.isEmpty()) {
            return;
        }

        if (--this.flankPathTimer <= 0) {
            this.flankPathTimer = 10 + this.wolf.getRandom().nextInt(10); // Throttle path recalculation

            // Deterministic flank assignment (Left or Right) based on Entity ID
            boolean isRightFlank = this.wolf.getId() % 2 == 0;
            Vec3 targetPos = target.position();
            Vec3 forward = target.getLookAngle().multiply(1.0, 0.0, 1.0).normalize();
            
            // Fallback if target is looking straight up/down
            if (forward.lengthSqr() < 0.01) {
                forward = new Vec3(1.0, 0.0, 0.0);
            }

            Vec3 flankOffset;
            if (isRightFlank) {
                // Cross product for right vector
                flankOffset = new Vec3(-forward.z, 0.0, forward.x).scale(2.5);
            } else {
                // Cross product for left vector
                flankOffset = new Vec3(forward.z, 0.0, -forward.x).scale(2.5);
            }
            
            // Move slightly behind the target
            flankOffset = flankOffset.subtract(forward.scale(1.5));
            
            Vec3 destination = targetPos.add(flankOffset);
            
            // Overwrite the vanilla path with the flank path
            this.wolf.getNavigation().moveTo(destination.x, target.getY(), destination.z, 1.2D);
        }
    }
}
