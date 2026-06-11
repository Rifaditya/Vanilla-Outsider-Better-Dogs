// Verified against: PersonalityFollowOwnerGoal.java (26.1.2+), EntityGetter.java (26.2+)
package net.vanillaoutsider.betterdogs.ai;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.dasik.social.core.EntitySocialScheduler;
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
    private float betterdogs$followerSpacingOffset = 0.0f;
    private int spacingThrottleTimer = 0;

    private static final java.util.function.Predicate<net.minecraft.world.entity.Mob> IS_MONSTER =
        m -> m instanceof net.minecraft.world.entity.monster.Monster;

    public PersonalityFollowOwnerGoal(Wolf wolf, double speedModifier, boolean leavesAllowed) {
        super(wolf, speedModifier, 10.0f, 2.0f);
        this.wolf = wolf;
        this.speedModifier = speedModifier;
        this.recalcTimer = 0;
        this.spacingThrottleTimer = wolf.getRandom().nextInt(20);
    }

    public static class FollowerSpacingCache {
        private static final java.util.Map<java.util.UUID, CacheEntry> cache = new java.util.concurrent.ConcurrentHashMap<>();

        public static class CacheEntry {
            public final int followerCount;
            public final long expiryTime;

            public CacheEntry(int followerCount, long expiryTime) {
                this.followerCount = followerCount;
                this.expiryTime = expiryTime;
            }
        }

        public static int getCount(java.util.UUID ownerUuid, long currentTime) {
            CacheEntry entry = cache.get(ownerUuid);
            if (entry != null && currentTime < entry.expiryTime) {
                return entry.followerCount;
            }
            return -1;
        }

        public static int getLastKnownCount(java.util.UUID ownerUuid) {
            CacheEntry entry = cache.get(ownerUuid);
            return entry != null ? entry.followerCount : 0;
        }

        public static void update(java.util.UUID ownerUuid, int count, long currentTime, int lifetime) {
            cache.put(ownerUuid, new CacheEntry(count, currentTime + lifetime));
            java.util.Iterator<java.util.Map.Entry<java.util.UUID, CacheEntry>> iterator = cache.entrySet().iterator();
            while (iterator.hasNext()) {
                if (currentTime > iterator.next().getValue().expiryTime + 600) {
                    iterator.remove();
                }
            }
        }
    }

    private void betterdogs$applyFollowerSpacing(int N) {
        if (N <= 1) {
            betterdogs$followerSpacingOffset = 0.0f;
            return;
        }
        float multiplier = DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MULTIPLIER) / 100.0f;
        float maxExtra = DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MAX) / 10.0f;
        betterdogs$followerSpacingOffset = Math.min((float) Math.sqrt(N - 1) * multiplier, maxExtra);
    }

    private void betterdogs$updateFollowerSpacingOffset() {
        if (this.spacingThrottleTimer > 0) {
            return;
        }
        LivingEntity owner = wolf.getOwner();
        if (owner == null || wolf.level() == null) {
            betterdogs$followerSpacingOffset = 0.0f;
            return;
        }

        java.util.UUID ownerUuid = owner.getUUID();
        long currentTime = wolf.level().getGameTime();

        // 1. Try to read from the cooperative cache
        int cachedCount = FollowerSpacingCache.getCount(ownerUuid, currentTime);
        if (cachedCount != -1) {
            this.spacingThrottleTimer = 20 + wolf.getRandom().nextInt(21);
            betterdogs$applyFollowerSpacing(cachedCount);
            return;
        }

        // 2. Cache Miss: Perform dynamic-radius search
        int lastCount = FollowerSpacingCache.getLastKnownCount(ownerUuid);
        double scanRadius = Math.min(32.0 + lastCount * 0.5, 64.0);

        java.util.List<Wolf> activeFollowers = wolf.level().getEntitiesOfClass(Wolf.class, owner.getBoundingBox().inflate(scanRadius));
        int N = 0;
        for (Wolf w : activeFollowers) {
            if (w.isTame() && w.getOwner() == owner && !w.isOrderedToSit() && !w.isLeashed()) {
                N++;
            }
        }

        // 3. Update the cooperative cache
        int lifetime = 20 + wolf.getRandom().nextInt(21);
        FollowerSpacingCache.update(ownerUuid, N, currentTime, lifetime);

        // 4. Apply the spacing offset
        this.spacingThrottleTimer = lifetime;
        betterdogs$applyFollowerSpacing(N);
    }

    private float getStartDistance() {
        if (!wolf.isTame()) {
            return 10.0f;
        }
        float dist = 10.0f;
        if (wolf instanceof WolfExtensions ext) {
            BetterDogsConfig config = BetterDogsConfig.get();
            dist = (float) switch (ext.betterdogs$getPersonality()) {
                case AGGRESSIVE -> DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_AGGRO_FOLLOW_START);
                case PACIFIST -> DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_PACI_FOLLOW_START);
                case NORMAL -> DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_NORMAL_FOLLOW_START);
            };

            // Dynamic Simulation Cap for Aggressive dogs (or any dog with large range)
            if (dist > 16.0f) {
                float maxSafeDist = (cachedSimDist * 16.0f) - config.getAggressiveDetectionBuffer();
                if (dist > maxSafeDist) {
                    dist = maxSafeDist;
                }
            }

            // Yo-Yo Fix: If Wanderlust is active, we must allow the dog to wander further
            // than the follow start distance.
            // Otherwise, they walk 5 blocks, get pulled back, and repeat.
            EntitySocialScheduler scheduler = ext.betterdogs$getScheduler();
            if (scheduler != null && scheduler.isEventActive("wanderlust")) {
                float wanderTolerance = config.getWanderlustRange() + 5.0f;
                if (dist < wanderTolerance) {
                    dist = wanderTolerance;
                }
            }
        }
        float finalBase = wolf.isBaby() ? dist * BetterDogsConfig.get().getBabyFollowMultiplier() : dist;
        return finalBase + betterdogs$followerSpacingOffset;
    }

    private float getStopDistance() {
        if (!wolf.isTame()) {
            return 2.0f;
        }
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
        float finalBase = wolf.isBaby() ? dist * BetterDogsConfig.get().getBabyFollowMultiplier() : dist;
        return finalBase + betterdogs$followerSpacingOffset;
    }

    @Override
    public boolean canUse() {
        if (this.spacingThrottleTimer > 0) {
            this.spacingThrottleTimer--;
        }
        if (wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
            return false;
        }
        LivingEntity owner = wolf.getOwner();
        if (owner == null || owner.isSpectator()) {
            return false;
        }
        betterdogs$updateFollowerSpacingOffset();
        float startDist = getStartDistance();
        if (wolf.distanceToSqr(owner) < (startDist * startDist)) {
            return false;
        }
        if (wolf.isOrderedToSit() || wolf.isLeashed()) {
            return false;
        }
        
        // IndyPets Compatibility: Respect independence
        if (net.vanillaoutsider.betterdogs.util.IndyPetsCompatibility.isIndependent(wolf)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
            return false;
        }
        if (wolf.getNavigation().isDone()) {
            return false;
        }
        if (wolf.isOrderedToSit() || wolf.isLeashed()) {
            return false;
        }
        
        // IndyPets Compatibility: Respect independence
        if (net.vanillaoutsider.betterdogs.util.IndyPetsCompatibility.isIndependent(wolf)) {
            return false;
        }

        LivingEntity owner = wolf.getOwner();
        if (owner == null) {
            return false;
        }
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
        if (this.spacingThrottleTimer > 0) {
            this.spacingThrottleTimer--;
        }
        LivingEntity owner = wolf.getOwner();
        if (owner == null) {
            return;
        }

        // Refresh Simulation Distance Cache (every 5 seconds)
        if (--this.simDistRefreshTimer <= 0) {
            this.simDistRefreshTimer = 100;
            if (owner.level().getServer() != null) {
                this.cachedSimDist = owner.level().getServer().getPlayerList().getSimulationDistance();
            }
        }

        wolf.getLookControl().setLookAt(owner, 10.0f, (float) wolf.getMaxHeadXRot());
        
        if (wolf instanceof WolfExtensions ext && ext.betterdogs$getPushWaitTimer() > 0) {
            wolf.getNavigation().stop();
            if (!wolf.isLeashed() && !wolf.isPassenger()) {
                float startDist = getStartDistance();
                double teleportMultiplier = BetterDogsConfig.get().getTeleportMultiplier();
                double teleportThreshold = (startDist * teleportMultiplier) * (startDist * teleportMultiplier);

                if (wolf.distanceToSqr(owner) >= teleportThreshold) {
                    teleportToOwner(owner);
                }
            }
            return;
        }

        if (this.recalcTimer <= 1) {
            betterdogs$updateFollowerSpacingOffset();
        }
        if (--this.recalcTimer <= 0) {
            this.recalcTimer = 10;
            if (!wolf.isLeashed() && !wolf.isPassenger()) {
                float startDist = getStartDistance();
                double distSqr = wolf.distanceToSqr(owner);
                
                if (wolf instanceof WolfExtensions ext) {
                    // BEHAVIORAL ENHANCEMENT: Aggressive Scouting
                    if (ext.betterdogs$getPersonality() == net.vanillaoutsider.betterdogs.WolfPersonality.AGGRESSIVE 
                        && distSqr < (startDist * startDist) && distSqr > 4.0) {
                        
                        if (wolf.getRandom().nextFloat() < 0.1f) { // 10% chance to scout per recalc
                            recalcTimer = 40; // Scout for longer
                            double scoutX = owner.getX() + owner.getLookAngle().x * 5.0 + (wolf.getRandom().nextDouble() - 0.5) * 3.0;
                            double scoutZ = owner.getZ() + owner.getLookAngle().z * 5.0 + (wolf.getRandom().nextDouble() - 0.5) * 3.0;
                            wolf.getNavigation().moveTo(scoutX, owner.getY(), scoutZ, speedModifier);
                            return; // Skip normal follow
                        }
                    }

                    // BEHAVIORAL ENHANCEMENT: Pacifist Alarm
                    if (ext.betterdogs$getPersonality() == net.vanillaoutsider.betterdogs.WolfPersonality.PACIFIST 
                        && wolf.level().getGameTime() % 40 == 0) {
                        
                        java.util.List<net.minecraft.world.entity.Mob> hostiles = wolf.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
                            wolf.getBoundingBox().inflate(12.0), 
                            IS_MONSTER);
                        
                        if (!hostiles.isEmpty()) {
                            wolf.playSound(net.minecraft.sounds.SoundEvents.GENERIC_HURT, 1.0f, 2.0f);
                        }
                    }
                }

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
            if (wolf.randomTeleport(owner.getX() + dx, owner.getY() + dy, owner.getZ() + dz, false)) {
                break;
            }
        }
    }
}
