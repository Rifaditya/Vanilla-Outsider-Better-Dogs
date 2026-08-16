// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class PersonalityFollowOwnerGoal extends FollowOwnerGoal {
    private final Wolf wolf;
    private final double baseSpeedModifier;

    public PersonalityFollowOwnerGoal(Wolf wolf, double speedModifier, float minDistance, float maxDistance) {
        super(wolf, speedModifier, minDistance, maxDistance);
        this.wolf = wolf;
        this.baseSpeedModifier = speedModifier;
    }

    private float betterdogs$followerSpacingOffset = 0.0f;
    private int spacingThrottleTimer = 0;

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

        public static void update(java.util.UUID ownerUuid, int count, long currentTime, int lifetime) {
            cache.put(ownerUuid, new CacheEntry(count, currentTime + lifetime));
        }
    }

    private void updateFollowerSpacing(LivingEntity owner) {
        if (owner == null || this.wolf.level() == null) {
            this.betterdogs$followerSpacingOffset = 0.0f;
            return;
        }

        java.util.UUID ownerUuid = owner.getUUID();
        long currentTime = this.wolf.level().getGameTime();
        int count = FollowerSpacingCache.getCount(ownerUuid, currentTime);

        if (count < 0) {
            var nearbyWolves = this.wolf.level().getEntitiesOfClass(
                Wolf.class,
                owner.getBoundingBox().inflate(16.0),
                w -> w.isTame() && w.isOwnedBy(owner) && !w.isOrderedToSit()
            );
            count = nearbyWolves.size();
            FollowerSpacingCache.update(ownerUuid, count, currentTime, 40);
        }

        if (count <= 1) {
            this.betterdogs$followerSpacingOffset = 0.0f;
        } else {
            float multiplier = net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.getInt(
                this.wolf.level(),
                net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MULTIPLIER,
                100
            ) / 100.0f;
            float maxExtra = net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.getInt(
                this.wolf.level(),
                net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MAX,
                50
            ) / 10.0f;
            this.betterdogs$followerSpacingOffset = Math.min((float) Math.sqrt(count - 1) * multiplier, maxExtra);
        }
    }

    public float getStartDistance() {
        float baseDist = 10.0f;
        if (this.wolf instanceof WolfExtensions ext) {
            WolfPersonality personality = ext.betterdogs$getPersonality();
            baseDist = switch (personality) {
                case AGGRESSIVE -> 50.0f;
                case PACIFIST -> 5.0f;
                case NORMAL -> 10.0f;
            };
        }
        return baseDist + this.betterdogs$followerSpacingOffset;
    }

    public float getStopDistance() {
        return 2.0f + this.betterdogs$followerSpacingOffset;
    }

    public float getTeleportThreshold() {
        float startDist = getStartDistance();
        if (startDist > 16.0f) {
            return 32.0f;
        }
        return startDist * 2.0f;
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null || owner.isSpectator() || this.wolf.isOrderedToSit() || this.wolf.isLeashed()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isGuarding()) {
            return false;
        }
        updateFollowerSpacing(owner);
        float startDist = getStartDistance();
        return this.wolf.distanceToSqr(owner) >= (startDist * startDist);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.getNavigation().isDone() || this.wolf.isOrderedToSit() || this.wolf.isLeashed()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isGuarding()) {
            return false;
        }
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null) {
            return false;
        }
        float stopDist = getStopDistance();
        return this.wolf.distanceToSqr(owner) > (stopDist * stopDist);
    }

    @Override
    public void tick() {
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null) {
            return;
        }

        if (++this.spacingThrottleTimer % 20 == 0) {
            updateFollowerSpacing(owner);
        }

        this.wolf.getLookControl().setLookAt(owner, 10.0f, (float) this.wolf.getMaxHeadXRot());
        double distSqr = this.wolf.distanceToSqr(owner);
        float teleportThresh = getTeleportThreshold();

        if (distSqr >= (teleportThresh * teleportThresh)) {
            teleportToOwner(owner);
            return;
        }

        float startDist = getStartDistance();
        if (distSqr >= (startDist * startDist)) {
            double speed = this.baseSpeedModifier;
            if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE) {
                speed *= 1.1;
            }
            speed = net.vanillaoutsider.betterdogs.util.WolfCatchupHelper.calculateCatchupSpeed(this.wolf, owner, speed);
            this.wolf.getNavigation().moveTo(owner, speed);
        } else {
            this.wolf.getNavigation().stop();
        }
    }

    private void teleportToOwner(LivingEntity owner) {
        for (int i = 0; i < 10; ++i) {
            double dx = (this.wolf.getRandom().nextFloat() - 0.5) * 4.0;
            double dy = (this.wolf.getRandom().nextFloat() - 0.5) * 4.0;
            double dz = (this.wolf.getRandom().nextFloat() - 0.5) * 4.0;
            if (this.wolf.randomTeleport(owner.getX() + dx, owner.getY() + dy, owner.getZ() + dz, false)) {
                break;
            }
        }
    }
}
