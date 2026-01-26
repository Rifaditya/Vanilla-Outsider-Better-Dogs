package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

public class PersonalityFollowOwnerGoal extends FollowOwnerGoal {

    private final Wolf wolf;
    private final double speedModifier;
    private int recalcTimer;
    private int simDistRefreshTimer = 0;
    private int cachedSimDist = 10;

    public PersonalityFollowOwnerGoal(Wolf wolf, double speedModifier, boolean leavesAllowed) {
        super(wolf, speedModifier, 10.0f, 2.0f);
        this.wolf = wolf;
        this.speedModifier = speedModifier;
        this.recalcTimer = 0;
    }

    private float getStartDistance() {
        if (!wolf.isTame()) return 10.0f;
        float dist = 10.0f;
        if (wolf instanceof WolfExtensions ext) {
            BetterDogsConfig config = BetterDogsConfig.get();
            dist = (float) switch (ext.betterdogs$getPersonality()) {
                case AGGRESSIVE -> BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_AGGRO_FOLLOW_START);
                case PACIFIST -> BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_PACI_FOLLOW_START);
                case NORMAL -> BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_NORMAL_FOLLOW_START);
            };

            // Dynamic Simulation Cap for Aggressive dogs (or any dog with large range)
            if (dist > 16.0f) {
                float maxSafeDist = (cachedSimDist * 16.0f) - config.getAggressiveDetectionBuffer();
                if (dist > maxSafeDist) {
                    dist = maxSafeDist;
                }
            }
        }
        return wolf.isBaby() ? dist * BetterDogsConfig.get().getBabyFollowMultiplier() : dist;
    }

    private float getStopDistance() {
        if (!wolf.isTame()) return 2.0f;
        float dist = 2.0f;
        if (wolf instanceof WolfExtensions ext) {
            BetterDogsConfig config = BetterDogsConfig.get();
            dist = switch (ext.betterdogs$getPersonality()) {
                case AGGRESSIVE -> config.aggressiveFollowStop;
                case PACIFIST -> config.pacifistFollowStop;
                case NORMAL -> config.normalFollowStop;
                default -> 2.0f;
            };
        }
        return wolf.isBaby() ? dist * BetterDogsConfig.get().getBabyFollowMultiplier() : dist;
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = wolf.getOwner();
        if (owner == null || owner.isSpectator()) return false;
        float startDist = getStartDistance();
        if (wolf.distanceToSqr(owner) < (startDist * startDist)) return false;
        if (wolf.isOrderedToSit() || wolf.isLeashed()) return false;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (wolf.getNavigation().isDone()) return false;
        if (wolf.isOrderedToSit() || wolf.isLeashed()) return false;
        LivingEntity owner = wolf.getOwner();
        if (owner == null) return false;
        float stopDist = getStopDistance();
        return wolf.distanceToSqr(owner) > (stopDist * stopDist);
    }

    @Override
    public void stop() {
        wolf.getNavigation().stop();
        super.stop();
    }

    @Override
    public void tick() {
        LivingEntity owner = wolf.getOwner();
        if (owner == null) return;

        // Refresh Simulation Distance Cache (every 5 seconds)
        if (--this.simDistRefreshTimer <= 0) {
            this.simDistRefreshTimer = 100;
            if (owner.level().getServer() != null) {
                this.cachedSimDist = owner.level().getServer().getPlayerList().getSimulationDistance();
            }
        }

        wolf.getLookControl().setLookAt(owner, 10.0f, (float) wolf.getMaxHeadXRot());
        if (--this.recalcTimer <= 0) {
            this.recalcTimer = 10;
            if (!wolf.isLeashed() && !wolf.isPassenger()) {
                float startDist = getStartDistance();
                double distSqr = wolf.distanceToSqr(owner);
                if (distSqr >= (startDist * startDist)) {
                    double dynamicSpeed = speedModifier;
                    double catchUpThreshold = BetterDogsConfig.get().getFollowCatchUpThreshold();
                    if (distSqr > (catchUpThreshold * catchUpThreshold)) {
                        dynamicSpeed *= BetterDogsConfig.get().getFollowCatchUpSpeed();
                    }
                    wolf.getNavigation().moveTo(owner, dynamicSpeed);
                } else {
                    wolf.getNavigation().stop();
                }
            }
        }
        if (!wolf.isLeashed() && !wolf.isPassenger()) {
            float startDist = getStartDistance();
            double teleportMultiplier = BetterDogsConfig.get().getTeleportMultiplier();
            double teleportThreshold = (startDist * teleportMultiplier) * (startDist * teleportMultiplier);
            
            if (wolf.distanceToSqr(owner) >= teleportThreshold) {
                teleportToOwner(owner);
            }
        }
    }

    private void teleportToOwner(LivingEntity owner) {
        double spread = BetterDogsConfig.get().getTeleportToOwnerSpread();
        for (int i = 0; i < 10; ++i) {
            double dx = (wolf.getRandom().nextFloat() - 0.5) * spread;
            double dy = (wolf.getRandom().nextFloat() - 0.5) * spread;
            double dz = (wolf.getRandom().nextFloat() - 0.5) * spread;
            if (wolf.randomTeleport(owner.getX() + dx, owner.getY() + dy, owner.getZ() + dz, false))
                break;
        }
    }
}
