// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

public class PersonalityFollowOwnerGoal extends FollowOwnerGoal {
    private final Wolf wolf;
    private final double baseSpeedModifier;

    public PersonalityFollowOwnerGoal(Wolf wolf, double speedModifier, float minDistance, float maxDistance) {
        super(wolf, speedModifier, minDistance, maxDistance);
        this.wolf = wolf;
        this.baseSpeedModifier = speedModifier;
    }

    public PersonalityFollowOwnerGoal(Wolf wolf, double speedModifier, boolean leavesAllowed) {
        super(wolf, speedModifier, 10.0f, 2.0f);
        this.wolf = wolf;
        this.baseSpeedModifier = speedModifier;
    }

    private float betterdogs$followerSpacingOffset = 0.0f;
    private int spacingThrottleTimer = 0;
    private int recalcTimer = 0;

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
            float multiplier = BetterDogsGameRules.getInt(
                this.wolf.level(),
                BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MULTIPLIER,
                100
            ) / 100.0f;
            float maxExtra = BetterDogsGameRules.getInt(
                this.wolf.level(),
                BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MAX,
                50
            ) / 10.0f;
            this.betterdogs$followerSpacingOffset = Math.min((float) Math.sqrt(count - 1) * multiplier, maxExtra);
        }
    }

    public static float calculateBaseStartDistance(Wolf wolf) {
        float baseDist = 10.0f;
        if (wolf instanceof WolfExtensions ext) {
            WolfPersonality personality = ext.betterdogs$getPersonality();
            if (personality != null) {
                baseDist = switch (personality) {
                    case AGGRESSIVE -> (float) BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 25);
                    case PACIFIST -> (float) BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_PACI_FOLLOW_START, 5);
                    case NORMAL -> (float) BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_NORMAL_FOLLOW_START, 10);
                };
            }

            CanineArchetype archetype = ext.betterdogs$getCanineArchetype();
            if (archetype == CanineArchetype.ADVENTURER) {
                baseDist = Math.max(baseDist, 16.0f);
            } else if (archetype == CanineArchetype.LAZY) {
                baseDist = Math.min(baseDist, 4.0f);
            }
        }
        return baseDist;
    }

    public static float getEffectiveStartDistance(Wolf wolf) {
        float baseDist = calculateBaseStartDistance(wolf);
        if (wolf == null) {
            return baseDist;
        }
        java.util.UUID ownerUuid = wolf.getOwner() != null
                ? wolf.getOwner().getUUID()
                : net.vanillaoutsider.betterdogs.ai.play.PlayChoreographyHelper.getOwnerUUID(wolf);
        int followerCount = ownerUuid != null ? FollowerSpacingCache.getLastKnownCount(ownerUuid) : 0;
        if (followerCount <= 1 || wolf.level() == null) {
            return baseDist;
        }
        float multiplier = BetterDogsGameRules.getInt(
            wolf.level(),
            BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MULTIPLIER,
            100
        ) / 100.0f;
        float maxExtra = BetterDogsGameRules.getInt(
            wolf.level(),
            BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MAX,
            50
        ) / 10.0f;
        float offset = Math.min((float) Math.sqrt(followerCount - 1) * multiplier, maxExtra);
        return baseDist + offset;
    }

    public float getStartDistance() {
        return calculateBaseStartDistance(this.wolf) + this.betterdogs$followerSpacingOffset;
    }

    public float getStopDistance() {
        float stopDist = 2.0f;
        if (this.wolf instanceof WolfExtensions ext) {
            CanineArchetype archetype = ext.betterdogs$getCanineArchetype();
            if (archetype == CanineArchetype.LAZY) {
                stopDist = 1.5f;
            } else if (archetype == CanineArchetype.ADVENTURER) {
                stopDist = 3.0f;
            }
        }
        return stopDist + this.betterdogs$followerSpacingOffset;
    }

    public static float calculateTeleportThreshold(float startDist) {
        return Math.max(startDist + 16.0f, 32.0f);
    }

    public float getTeleportThreshold() {
        return calculateTeleportThreshold(getStartDistance());
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null || owner.isSpectator() || this.wolf.isOrderedToSit() || this.wolf.isLeashed() || this.wolf.isPassenger()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
            return false;
        }
        updateFollowerSpacing(owner);
        float startDist = getStartDistance();
        return this.wolf.distanceToSqr(owner) >= (startDist * startDist);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wolf.getNavigation().isDone() || this.wolf.isOrderedToSit() || this.wolf.isLeashed() || this.wolf.isPassenger()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
            return false;
        }
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null || owner.isSpectator()) {
            return false;
        }
        float stopDist = getStopDistance();
        return this.wolf.distanceToSqr(owner) > (stopDist * stopDist);
    }

    @Override
    public void start() {
        this.recalcTimer = 0;
        if (this.wolf.isInSittingPose() && !this.wolf.isOrderedToSit()) {
            this.wolf.setInSittingPose(false);
        }
    }

    @Override
    public void stop() {
        this.wolf.getNavigation().stop();
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$getCanineArchetype() == CanineArchetype.LAZY) {
            if (!this.wolf.isOrderedToSit() && this.wolf.getRandom().nextFloat() < 0.35f) {
                this.wolf.setInSittingPose(true);
            }
        }
        super.stop();
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

        if (!this.wolf.isLeashed() && !this.wolf.isPassenger()) {
            float teleportThreshold = getTeleportThreshold();
            if (this.wolf.distanceToSqr(owner) >= (teleportThreshold * teleportThreshold)) {
                teleportToOwner(owner);
                return;
            }
        }

        if (--this.recalcTimer <= 0) {
            this.recalcTimer = 10;
            double speed = this.baseSpeedModifier;
            if (this.wolf instanceof WolfExtensions ext) {
                WolfPersonality personality = ext.betterdogs$getPersonality();
                if (personality == WolfPersonality.AGGRESSIVE) {
                    speed *= 1.2;
                }
                CanineArchetype archetype = ext.betterdogs$getCanineArchetype();
                if (archetype != null) {
                    speed *= archetype.getSpeedModifier();
                }
            }
            speed = net.vanillaoutsider.betterdogs.util.WolfCatchupHelper.calculateCatchupSpeed(this.wolf, owner, speed);
            this.wolf.getNavigation().moveTo(owner, speed);
        }
    }

    private void teleportToOwner(LivingEntity owner) {
        if (this.wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            net.minecraft.world.phys.Vec3 safePos = net.vanillaoutsider.betterdogs.util.WolfTeleportHelper.findSafeTeleportPos(this.wolf, serverLevel, owner.blockPosition());
            if (safePos != null) {
                this.wolf.teleportTo(serverLevel, safePos.x, safePos.y, safePos.z, java.util.Set.of(), this.wolf.getYRot(), this.wolf.getXRot(), false);
                this.wolf.getNavigation().stop();
            }
        }
    }
}
