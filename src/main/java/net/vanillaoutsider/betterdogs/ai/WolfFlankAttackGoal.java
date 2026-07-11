// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.WolfPersistentData;

import java.util.Optional;
import java.util.UUID;

public class WolfFlankAttackGoal extends MeleeAttackGoal {
    private final Wolf wolf;
    private final double speedModifier;
    private int pathRecalcTimer = 0;
    private int ticksUntilNextAttack = 0;

    public WolfFlankAttackGoal(Wolf wolf, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(wolf, speedModifier, followingTargetEvenIfNotSeen);
        this.wolf = wolf;
        this.speedModifier = speedModifier;
    }

    @Override
    public void start() {
        super.start();
        this.pathRecalcTimer = 0;
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.wolf.getTarget();
        if (target == null) {
            return;
        }

        // Look at target
        this.wolf.getLookControl().setLookAt(target, 30.0F, 30.0F);

        // Check if flanking is enabled and target is out of melee range
        boolean shouldFlank = net.dasik.social.api.gamerule.DynamicGameRuleManager.getBoolean(this.wolf.level(), BetterDogsGameRules.BD_PACK_FLANKING_TACTICS)
                && this.wolf.distanceToSqr(target) > 9.0;

        if (shouldFlank) {
            boolean hasLeader = false;
            if (this.wolf.isTame()) {
                hasLeader = this.wolf.getOwner() != null;
            } else {
                hasLeader = WolfPersistentData.getWolfData(this.wolf).leaderUuid().isPresent();
            }

            if (hasLeader) {
                // Determine if this wolf should flank based on Approach Time (distance / speed) in the active local pack
                boolean isFlanker = false;
                LivingEntity ownerEntity = this.wolf.getOwner();

                if (this.wolf.isTame() && ownerEntity != null) {
                    java.util.List<Wolf> activePack = this.wolf.level().getEntitiesOfClass(
                            Wolf.class,
                            this.wolf.getBoundingBox().inflate(32.0),
                            w -> w.isTame() && w.getOwner() == ownerEntity && w.getTarget() == target && !w.isOrderedToSit()
                    );
                    
                    if (activePack.size() > 1) {
                        // Sort active pack by Approach Time (distance / speed) ascending, using ID as tie-breaker
                        activePack.sort((w1, w2) -> {
                            double t1 = w1.distanceTo(target) / Math.max(w1.getAttributeValue(Attributes.MOVEMENT_SPEED), 0.01);
                            double t2 = w2.distanceTo(target) / Math.max(w2.getAttributeValue(Attributes.MOVEMENT_SPEED), 0.01);
                            if (t1 != t2) {
                                return Double.compare(t1, t2); // Ascending (shortest approach time first)
                            }
                            return Integer.compare(w1.getId(), w2.getId()); // Deterministic tie-breaker
                        });
                        
                        // Slower to arrive (bottom 50%) dogs perform flanking maneuvers, while the closest 50% charge straight
                        int flankCount = activePack.size() / 2; // Keep at least half the pack engaging directly
                        int myIndex = activePack.indexOf(this.wolf);
                        if (myIndex >= activePack.size() - flankCount) {
                            isFlanker = true;
                        }
                    }
                } else if (!this.wolf.isTame()) {
                    // Wild wolves pack sorting based on approach time
                    Optional<UUID> leaderUuid = WolfPersistentData.getWolfData(this.wolf).leaderUuid();
                    if (leaderUuid.isPresent()) {
                        java.util.List<Wolf> activePack = this.wolf.level().getEntitiesOfClass(
                                Wolf.class,
                                this.wolf.getBoundingBox().inflate(32.0),
                                w -> !w.isTame() && WolfPersistentData.getWolfData(w).leaderUuid().equals(leaderUuid) && w.getTarget() == target
                        );
                        
                        if (activePack.size() > 1) {
                            activePack.sort((w1, w2) -> {
                                double t1 = w1.distanceTo(target) / Math.max(w1.getAttributeValue(Attributes.MOVEMENT_SPEED), 0.01);
                                double t2 = w2.distanceTo(target) / Math.max(w2.getAttributeValue(Attributes.MOVEMENT_SPEED), 0.01);
                                if (t1 != t2) {
                                    return Double.compare(t1, t2);
                                }
                                return Integer.compare(w1.getId(), w2.getId());
                            });
                            
                            int flankCount = activePack.size() / 2;
                            int myIndex = activePack.indexOf(this.wolf);
                            if (myIndex >= activePack.size() - flankCount) {
                                isFlanker = true;
                            }
                        }
                    }
                }

                if (isFlanker) {
                    if (--this.pathRecalcTimer <= 0) {
                        this.pathRecalcTimer = 4 + this.wolf.getRandom().nextInt(5); // Staggered 4-8 tick updates
                        
                        Vec3 targetPos = target.position();
                        Vec3 forward = target.getLookAngle().multiply(1.0, 0.0, 1.0).normalize();
                        
                        if (forward.lengthSqr() < 0.01) {
                            forward = new Vec3(1.0, 0.0, 0.0);
                        }

                        // Determine flank side contextually based on where the wolf is physically standing relative to target
                        Vec3 toWolf = this.wolf.position().subtract(targetPos);
                        double cross = forward.x * toWolf.z - forward.z * toWolf.x;
                        boolean isRightFlank = cross > 0.0;

                        Vec3 flankOffset;
                        if (isRightFlank) {
                            flankOffset = new Vec3(-forward.z, 0.0, forward.x).scale(4.5); // Wide 4.5 block radius
                        } else {
                            flankOffset = new Vec3(forward.z, 0.0, -forward.x).scale(4.5); // Wide 4.5 block radius
                        }
                        
                        flankOffset = flankOffset.subtract(forward.scale(2.0)); // Rear 2.0 block shift
                        Vec3 destination = targetPos.add(flankOffset);
                        
                        // Flanking wolves move at standard combat speed
                        this.wolf.getNavigation().moveTo(destination.x, target.getY(), destination.z, this.speedModifier);
                    }
                } else {
                    shouldFlank = false; // Slower to arrive / closer wolves: attack directly
                }
            } else {
                shouldFlank = false; // Lone/Leader wolf: attack directly
            }
        }

        if (!shouldFlank) {
            // Standard direct attack movement
            if (--this.pathRecalcTimer <= 0) {
                this.pathRecalcTimer = 4 + this.wolf.getRandom().nextInt(7);
                
                // Slow down straight-charging wolves (lone/leader/closest wolves) to 50% speed during approach (distance > 3 blocks)
                double currentSpeed = this.speedModifier;
                if (this.wolf.distanceToSqr(target) > 9.0) {
                    currentSpeed = this.speedModifier * 0.5D;
                }
                
                this.wolf.getNavigation().moveTo(target, currentSpeed);
            }
        }

        // Ticking attack cooldown and triggers
        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        this.checkAndPerformAttackInternal(target);
    }

    private void checkAndPerformAttackInternal(LivingEntity target) {
        if (this.ticksUntilNextAttack <= 0 && this.wolf.isWithinMeleeAttackRange(target) && this.wolf.getSensing().hasLineOfSight(target)) {
            this.ticksUntilNextAttack = this.adjustedTickDelay(20);
            this.wolf.swing(InteractionHand.MAIN_HAND);
            this.wolf.doHurtTarget(MeleeAttackGoal.getServerLevel(this.wolf), target);
        }
    }
}
