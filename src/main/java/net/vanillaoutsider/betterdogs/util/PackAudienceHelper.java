// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dedicated single-purpose helper for pack audience reactions and spectator wagging during play sparring.
 * Evaluates nearby packmate dogs within 6 blocks of the sparring midpoint and coordinates attentive head-tracking
 * and excited tail wagging without interfering with the ongoing sparring session.
 */
public final class PackAudienceHelper {

    public static final double AUDIENCE_RADIUS = 6.0;
    public static final double AUDIENCE_RADIUS_SQR = AUDIENCE_RADIUS * AUDIENCE_RADIUS; // 36.0 blocks squared
    public static final int MAX_AUDIENCE_COUNT = 3;
    public static final int WAG_DURATION_TICKS = 30; // 1.5 seconds, refreshed every 20 ticks during sparring

    private static final Map<UUID, Integer> AUDIENCE_WAG_TRACKER = new ConcurrentHashMap<>();

    private PackAudienceHelper() {
    }

    /**
     * Calculates the geometric midpoint between two wolves.
     */
    public static Vec3 calculateMidpoint(Wolf wolfA, Wolf wolfB) {
        if (wolfA == null && wolfB == null) {
            return Vec3.ZERO;
        }
        if (wolfA == null) {
            return wolfB.position();
        }
        if (wolfB == null) {
            return wolfA.position();
        }
        return new Vec3(
                (wolfA.getX() + wolfB.getX()) * 0.5,
                (wolfA.getY() + wolfB.getY()) * 0.5,
                (wolfA.getZ() + wolfB.getZ()) * 0.5
        );
    }

    /**
     * Calculates the geometric midpoint between two coordinate vectors.
     */
    public static Vec3 calculateMidpoint(Vec3 posA, Vec3 posB) {
        if (posA == null && posB == null) {
            return Vec3.ZERO;
        }
        if (posA == null) {
            return posB;
        }
        if (posB == null) {
            return posA;
        }
        return new Vec3(
                (posA.x + posB.x) * 0.5,
                (posA.y + posB.y) * 0.5,
                (posA.z + posB.z) * 0.5
        );
    }

    /**
     * Checks if two wolves share the same owner.
     */
    public static boolean isCoOwned(Wolf a, Wolf b) {
        if (a == null || b == null) {
            return false;
        }
        net.minecraft.world.entity.EntityReference<LivingEntity> refA = a.getOwnerReference();
        net.minecraft.world.entity.EntityReference<LivingEntity> refB = b.getOwnerReference();
        if (refA != null && refB != null) {
            return refA.getUUID().equals(refB.getUUID());
        }
        LivingEntity ownerA = a.getOwner();
        LivingEntity ownerB = b.getOwner();
        return ownerA != null && ownerA == ownerB;
    }

    /**
     * Evaluates whether a candidate wolf meets audience eligibility criteria:
     * - Tamed wolf sharing the same owner as the sparring participants
     * - Alive
     * - Not in combat (no target)
     * - Not sitting manually
     * - Not participating in the fight (or another social mode)
     * - Not subdued / on play penalty
     * - Within 6 blocks of the sparring midpoint
     */
    public static boolean isEligibleAudience(Wolf wolfA, Wolf wolfB, Vec3 midpoint, Wolf candidate) {
        if (candidate == null || !candidate.isAlive() || !candidate.isTame()) {
            return false;
        }
        if (wolfA == null && wolfB == null) {
            return false;
        }
        Wolf referenceWolf = (wolfA != null) ? wolfA : wolfB;
        if (!isCoOwned(referenceWolf, candidate)) {
            return false;
        }
        if (candidate == wolfA || candidate == wolfB) {
            return false;
        }
        if (candidate.getUUID() != null) {
            if (wolfA != null && candidate.getUUID().equals(wolfA.getUUID())) {
                return false;
            }
            if (wolfB != null && candidate.getUUID().equals(wolfB.getUUID())) {
                return false;
            }
        }
        if (candidate.getTarget() != null) {
            return false;
        }
        if (candidate.isOrderedToSit()) {
            return false;
        }
        if (candidate instanceof WolfExtensions ext) {
            if (ext.betterdogs$isSittingManually() || ext.betterdogs$isSocialModeActive() || ext.betterdogs$isSubdued()) {
                return false;
            }
        }
        if (midpoint != null && candidate.distanceToSqr(midpoint.x, midpoint.y, midpoint.z) > AUDIENCE_RADIUS_SQR) {
            return false;
        }
        return true;
    }

    /**
     * Pure state evaluation overload for test suites and headless verification.
     */
    public static boolean isEligibleAudience(boolean isTame, boolean isCoOwned, boolean isAlive,
                                            boolean hasCombatTarget, boolean isSittingManually,
                                            boolean isParticipant, double distanceToMidpoint) {
        if (!isTame || !isCoOwned || !isAlive || hasCombatTarget || isSittingManually || isParticipant) {
            return false;
        }
        return distanceToMidpoint <= AUDIENCE_RADIUS;
    }

    /**
     * Finds up to 3 nearby eligible pack audience wolves closest to the sparring midpoint.
     */
    public static List<Wolf> findAudience(Wolf wolfA, Wolf wolfB) {
        if (wolfA == null || wolfB == null || wolfA.level() == null || wolfA.level().isClientSide()) {
            return Collections.emptyList();
        }
        Vec3 midpoint = calculateMidpoint(wolfA, wolfB);
        AABB searchBox = new AABB(
                midpoint.x - AUDIENCE_RADIUS, midpoint.y - AUDIENCE_RADIUS, midpoint.z - AUDIENCE_RADIUS,
                midpoint.x + AUDIENCE_RADIUS, midpoint.y + AUDIENCE_RADIUS, midpoint.z + AUDIENCE_RADIUS
        );
        List<Wolf> candidates = wolfA.level().getEntitiesOfClass(
                Wolf.class,
                searchBox,
                candidate -> isEligibleAudience(wolfA, wolfB, midpoint, candidate)
        );
        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }
        return candidates.stream()
                .sorted(Comparator.comparingDouble(w -> w.distanceToSqr(midpoint.x, midpoint.y, midpoint.z)))
                .limit(MAX_AUDIENCE_COUNT)
                .toList();
    }

    /**
     * Applies audience reactions (head look and excited tail wagging) to the audience wolves.
     *
     * @return Number of audience wolves that reacted.
     */
    public static int applyAudienceReactions(Wolf wolfA, Wolf wolfB, List<Wolf> audienceWolves) {
        if (audienceWolves == null || audienceWolves.isEmpty()) {
            return 0;
        }
        Vec3 midpoint = calculateMidpoint(wolfA, wolfB);
        int reacted = 0;
        for (Wolf audienceDog : audienceWolves) {
            if (audienceDog == null) {
                continue;
            }
            applyAudienceReaction(audienceDog, midpoint);
            reacted++;
        }
        return reacted;
    }

    /**
     * Applies audience reaction to a single spectator dog.
     */
    public static void applyAudienceReaction(Wolf audienceDog, Vec3 midpoint) {
        if (audienceDog == null || midpoint == null) {
            return;
        }
        // 1. Turn head toward the sparring midpoint
        if (audienceDog.getLookControl() != null) {
            audienceDog.getLookControl().setLookAt(midpoint.x, midpoint.y + (audienceDog.getEyeHeight() * 0.5), midpoint.z, 30.0F, 30.0F);
        }
        // 2. Perk ears / interested posture
        audienceDog.setIsInterested(true);
        // 3. Keep dog stationary without moving into the fight
        if (audienceDog.getNavigation() != null) {
            audienceDog.getNavigation().stop();
        }
        // 4. Wag tail in excitement
        if (audienceDog instanceof WolfExtensions ext) {
            ext.betterdogs$setSpectatorWagTicks(WAG_DURATION_TICKS);
        }
        if (audienceDog.getUUID() != null) {
            setAudienceWagTicks(audienceDog.getUUID(), WAG_DURATION_TICKS);
        }
    }

    /**
     * Coordinates the audience evaluation and reaction routine for active sparring wolves.
     *
     * @return Number of audience wolves reacting.
     */
    public static int tickAudience(Wolf wolfA, Wolf wolfB) {
        if (wolfA == null || wolfB == null) {
            return 0;
        }
        List<Wolf> audience = findAudience(wolfA, wolfB);
        return applyAudienceReactions(wolfA, wolfB, audience);
    }

    // ========== In-Memory Spectator Wag Tracking (for headless testing & state tracking) ==========

    public static int getAudienceWagTicks(UUID wolfUuid) {
        if (wolfUuid == null) {
            return 0;
        }
        return AUDIENCE_WAG_TRACKER.getOrDefault(wolfUuid, 0);
    }

    public static void setAudienceWagTicks(UUID wolfUuid, int ticks) {
        if (wolfUuid == null) {
            return;
        }
        if (ticks > 0) {
            AUDIENCE_WAG_TRACKER.put(wolfUuid, ticks);
        } else {
            AUDIENCE_WAG_TRACKER.remove(wolfUuid);
        }
    }

    public static void clearAudienceWagTracker() {
        AUDIENCE_WAG_TRACKER.clear();
    }
}
