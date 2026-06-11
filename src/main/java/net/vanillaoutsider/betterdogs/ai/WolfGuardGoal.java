// Verified against: Wolf.java (26.1.2+)
package net.vanillaoutsider.betterdogs.ai;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

public class WolfGuardGoal extends Goal {
    private final Wolf wolf;
    private final WolfExtensions ext;
    private double theta = 0.0; // Current angle in radians
    private boolean movingToOuter = true; // For Normal's Radial Star Patrol
    private BlockPos currentPatrolTarget = null;
    private int patrolPauseTimer = 0;
    private int alertCooldown = 0;
    private boolean isAlertActive = false;

    public WolfGuardGoal(Wolf wolf) {
        this.wolf = wolf;
        this.ext = (WolfExtensions) wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!wolf.isTame() || !ext.betterdogs$isGuardMode() || ext.betterdogs$getGuardPos() == null || ext.betterdogs$isSittingManually()) {
            return false;
        }

        net.minecraft.world.entity.LivingEntity target = wolf.getTarget();
        if (target != null && target.isAlive()) {
            BlockPos post = ext.betterdogs$getGuardPos();
            double distSqr = target.distanceToSqr(post.getX() + 0.5, post.getY() + 0.5, post.getZ() + 0.5);
            double maxChase = ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE ? 1024.0 : 400.0;
            if (distSqr <= maxChase) {
                return false; // Yield to combat goals
            }
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        this.theta = wolf.getRandom().nextDouble() * Math.PI * 2;
        this.currentPatrolTarget = null;
        this.patrolPauseTimer = 0;
        this.movingToOuter = true;
        this.isAlertActive = false;
    }

    @Override
    public void stop() {
        wolf.getNavigation().stop();
        if (ext.betterdogs$getPersonality() == WolfPersonality.NORMAL && !ext.betterdogs$isSittingManually()) {
            wolf.setOrderedToSit(false);
        }
    }

    @Override
    public void tick() {
        BlockPos post = ext.betterdogs$getGuardPos();
        if (post == null) return;

        WolfPersonality personality = ext.betterdogs$getPersonality();
        double distToPostSqr = wolf.distanceToSqr(post.getX(), post.getY(), post.getZ());

        // 1. Spawning very subtle ambient particles (1 particle every 80 ticks / 4s at feet with zero velocity)
        if (wolf.tickCount % 80 == 0 && wolf.level() instanceof ServerLevel serverLevel) {
            double px = wolf.getX() + (wolf.getRandom().nextDouble() - 0.5) * 0.2;
            double py = wolf.getY() + 0.05;
            double pz = wolf.getZ() + (wolf.getRandom().nextDouble() - 0.5) * 0.2;
            switch (personality) {
                case AGGRESSIVE -> serverLevel.sendParticles(new net.minecraft.core.particles.DustParticleOptions(0xFF3333, 0.5f), px, py, pz, 1, 0, 0, 0, 0);
                case NORMAL -> serverLevel.sendParticles(new net.minecraft.core.particles.DustParticleOptions(0xFFD700, 0.5f), px, py, pz, 1, 0, 0, 0, 0);
                case PACIFIST -> serverLevel.sendParticles(new net.minecraft.core.particles.DustParticleOptions(0x00FF88, 0.5f), px, py, pz, 1, 0, 0, 0, 0);
            }
        }

        // 2. Alert Cooldown Tick
        if (alertCooldown > 0) {
            alertCooldown--;
        }

        // 3. Pacifist Sentinel Watchdog Actions (Alarm)
        if (personality == WolfPersonality.PACIFIST) {
            if (wolf.tickCount % 20 == 0) {
                // Watchdog Alarm (Whining and note particles when hostiles approach within 16 blocks)
                List<Monster> enemies = wolf.level().getEntitiesOfClass(Monster.class, wolf.getBoundingBox().inflate(16.0, 16.0, 16.0), enemy -> {
                    double dy = Math.abs(enemy.getY() - wolf.getY());
                    if (dy > 16.0) return false;
                    if (dy <= 4.0) return true;
                    return wolf.getSensing().hasLineOfSight(enemy);
                });
                this.isAlertActive = !enemies.isEmpty();
                if (this.isAlertActive) {
                    if (alertCooldown <= 0) {
                        alertCooldown = 60; // 3 seconds alert cooldown
                        wolf.playSound(((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().growlSound().value(), 1.0f, 1.0f);
                        if (wolf.level() instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.NOTE, wolf.getX(), wolf.getY() + 1.2, wolf.getZ(), 4, 0.2, 0.1, 0.2, 0.0);
                        }
                    }
                }
            }

            if (this.isAlertActive) {
                // When alert is active, pacifist stands at the post block
                if (distToPostSqr > 2.25) {
                    wolf.setOrderedToSit(false);
                    wolf.getNavigation().moveTo(post.getX(), post.getY(), post.getZ(), 1.0);
                } else {
                    wolf.getNavigation().stop();
                }
                return;
            }
        }

        // 4. Combat Chase Range Enforcement (Clears targets if lured too far from post)
        if (wolf.getTarget() != null) {
            double targetDistFromPostSqr = wolf.getTarget().distanceToSqr(post.getX(), post.getY(), post.getZ());
            double maxChase = personality == WolfPersonality.AGGRESSIVE ? 1024.0 : 400.0; // 32 blocks sqr vs 20 blocks sqr
            if (targetDistFromPostSqr > maxChase) {
                wolf.setTarget(null);
                wolf.getNavigation().stop();
            } else {
                // If currently fighting/chasing, skip patrol movement
                if (personality == WolfPersonality.NORMAL) {
                    wolf.setOrderedToSit(false);
                }
                return;
            }
        }

        // 5. Patrol Range / GameRules
        int patrolRange = switch (personality) {
            case AGGRESSIVE -> DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_GUARD_PATROL_RANGE_AGGRESSIVE);
            case NORMAL -> DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_GUARD_PATROL_RANGE_NORMAL);
            case PACIFIST -> DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_GUARD_PATROL_RANGE_PACIFIST);
        };

        // 6. Stationary Sentinel Check (Normal personality when patrolRange = 0)
        if (personality == WolfPersonality.NORMAL && patrolRange <= 0) {
            if (distToPostSqr > 1.5) {
                wolf.setOrderedToSit(false);
                wolf.getNavigation().moveTo(post.getX(), post.getY(), post.getZ(), 1.0);
            } else {
                wolf.getNavigation().stop();
                if (!wolf.isInSittingPose()) {
                    wolf.setOrderedToSit(true);
                }
            }
            return;
        }

        // If Normal sentinel is moving, ensure orderedToSit is false
        if (personality == WolfPersonality.NORMAL && wolf.isInSittingPose()) {
            wolf.setOrderedToSit(false);
        }

        // 7. Pacing Timer
        if (patrolPauseTimer > 0) {
            patrolPauseTimer--;
            if (personality == WolfPersonality.AGGRESSIVE) {
                // Look outward from post (at eye level)
                double dx = wolf.getX() - post.getX();
                double dz = wolf.getZ() - post.getZ();
                wolf.getLookControl().setLookAt(wolf.getX() + dx, wolf.getEyeY(), wolf.getZ() + dz, 10.0f, (float) wolf.getMaxHeadXRot());
            } else {
                // Look at post (at eye level)
                wolf.getLookControl().setLookAt(post.getX() + 0.5, post.getY() + 1.0, post.getZ() + 0.5, 10.0f, (float) wolf.getMaxHeadXRot());
            }
            return;
        }

        // 8. Mathematical Patrol Pathing Loops
        if (currentPatrolTarget == null || wolf.getNavigation().isDone() || distToPostSqr > (patrolRange * patrolRange * 1.5)) {
            patrolPauseTimer = 30 + wolf.getRandom().nextInt(30); // Pause for 1.5 - 3 seconds

            switch (personality) {
                case AGGRESSIVE -> {
                    // Perimeter Sweep: circular path at 80% range
                    double radius = patrolRange * 0.8;
                    theta += (Math.PI / 6.0) + (wolf.getRandom().nextDouble() * 0.05); // step 30 degrees
                    if (theta >= Math.PI * 2) {
                        theta -= Math.PI * 2;
                    }
                    double tx = post.getX() + radius * Math.cos(theta);
                    double tz = post.getZ() + radius * Math.sin(theta);
                    currentPatrolTarget = new BlockPos((int) tx, post.getY(), (int) tz);
                }
                case NORMAL -> {
                    // Radial Star Patrol: alternate between walking out and walking back
                    if (movingToOuter) {
                        double angle = wolf.getRandom().nextDouble() * Math.PI * 2;
                        double dist = 2.0 + wolf.getRandom().nextDouble() * (patrolRange - 2.0);
                        double tx = post.getX() + dist * Math.cos(angle);
                        double tz = post.getZ() + dist * Math.sin(angle);
                        currentPatrolTarget = new BlockPos((int) tx, post.getY(), (int) tz);
                        movingToOuter = false;
                    } else {
                        currentPatrolTarget = post;
                        movingToOuter = true;
                    }
                }
                case PACIFIST -> {
                    // Orbital Circle: circular pacing close to post (r = 2)
                    double radius = Math.min(2.0, patrolRange);
                    theta += (Math.PI / 8.0);
                    if (theta >= Math.PI * 2) {
                        theta -= Math.PI * 2;
                    }
                    double tx = post.getX() + radius * Math.cos(theta);
                    double tz = post.getZ() + radius * Math.sin(theta);
                    currentPatrolTarget = new BlockPos((int) tx, post.getY(), (int) tz);
                }
            }

            if (currentPatrolTarget != null) {
                wolf.getNavigation().moveTo(currentPatrolTarget.getX(), currentPatrolTarget.getY(), currentPatrolTarget.getZ(), 0.85);
            }
        }
    }
}
