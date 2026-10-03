// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dedicated single-purpose helper for harmless packmate social sparring / play fighting.
 */
public final class SmallFightHelper {

    public static final int SPARRING_DURATION_TICKS = 120; // 6 seconds
    public static final double DEFAULT_PARTNER_RADIUS = 6.0;
    public static final double DEFAULT_SPEED_MODIFIER = 1.15;
    public static final double MAX_SPARRING_DISTANCE = 10.0;
    public static final double MAX_SPARRING_DISTANCE_SQR = 100.0; // 10 blocks squared abort
    public static final int BASELINE_SPARRING_COOLDOWN_TICKS = 1200; // 60 seconds (1200 ticks) baseline
    public static final int MIN_SPARRING_COOLDOWN_TICKS = 1200; // 60 seconds (1200 ticks)
    public static final int MAX_SPARRING_COOLDOWN_TICKS = 1800; // 90 seconds (1800 ticks)

    public static final int MIN_POUNCE_THRESHOLD = 3;
    public static final int MAX_POUNCE_THRESHOLD = 5;
    public static final float CORRECTION_DAMAGE_ADULT = 1.0F; // 0.5 hearts
    public static final float CORRECTION_DAMAGE_PUPPY = 0.0F; // Gentle reprimand growl, adult patience

    public static final String NBT_KEY_PLAY_PENALTY_TICKS = "BD_PlayPenaltyTicks";

    private static final Map<UUID, Long> COOLDOWN_EXPIRY = new ConcurrentHashMap<>();
    private static final Map<SparringSessionKey, SparringSessionData> ACTIVE_SESSIONS = new ConcurrentHashMap<>();
    private static final Map<UUID, UUID> LAST_CORRECTION_DISCIPLINER = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> CORRECTION_SNAP_EXPIRY = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> PLAY_PENALTY_TRACKER = new ConcurrentHashMap<>();

    public static final class SparringSessionKey {
        private final UUID first;
        private final UUID second;

        public SparringSessionKey(UUID a, UUID b) {
            if (a == null || b == null) {
                this.first = a;
                this.second = b;
            } else if (a.compareTo(b) <= 0) {
                this.first = a;
                this.second = b;
            } else {
                this.first = b;
                this.second = a;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SparringSessionKey that)) return false;
            return java.util.Objects.equals(first, that.first) && java.util.Objects.equals(second, that.second);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(first, second);
        }
    }

    public static final class SparringSessionData {
        private final int threshold;
        private int pounceCount;

        public SparringSessionData(int threshold) {
            this.threshold = threshold;
            this.pounceCount = 0;
        }

        public int getThreshold() {
            return threshold;
        }

        public int getPounceCount() {
            return pounceCount;
        }

        public int incrementPounces() {
            return ++pounceCount;
        }
    }

    private SmallFightHelper() {
    }

    /**
     * Applies the baseline post-sparring cooldown (1200–1800 ticks, 60–90 seconds) to the wolf.
     */
    public static void applyCooldown(Wolf wolf) {
        applyCooldown(wolf, 0);
    }

    /**
     * Applies post-sparring cooldown with an optional leading delay (e.g. for ongoing sparring session duration).
     */
    public static void applyCooldown(Wolf wolf, int extraDelayTicks) {
        if (wolf == null) {
            return;
        }
        int cooldown = MIN_SPARRING_COOLDOWN_TICKS;
        if (wolf.getRandom() != null) {
            cooldown += wolf.getRandom().nextInt(MAX_SPARRING_COOLDOWN_TICKS - MIN_SPARRING_COOLDOWN_TICKS + 1);
        }
        long gameTime = (wolf.level() != null) ? wolf.level().getGameTime() : 0L;
        long targetExpiry = gameTime + extraDelayTicks + cooldown;
        setCooldown(wolf, targetExpiry);
    }

    /**
     * Sets the sparring cooldown expiry timestamp for a wolf.
     */
    public static void setCooldown(Wolf wolf, long expiryGameTime) {
        if (wolf == null) {
            return;
        }
        UUID uuid = wolf.getUUID();
        if (uuid != null) {
            COOLDOWN_EXPIRY.put(uuid, expiryGameTime);
        }
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSparringCooldownUntil(expiryGameTime);
        }
    }

    /**
     * Sets the sparring cooldown expiry timestamp by UUID (for test suites and direct tracking).
     */
    public static void setCooldown(UUID wolfUuid, long expiryGameTime) {
        if (wolfUuid != null) {
            COOLDOWN_EXPIRY.put(wolfUuid, expiryGameTime);
        }
    }

    /**
     * Checks whether the wolf is currently on sparring cooldown.
     */
    public static boolean isOnCooldown(Wolf wolf) {
        if (wolf == null) {
            return false;
        }
        long currentGameTime = (wolf.level() != null) ? wolf.level().getGameTime() : 0L;
        return isOnCooldown(wolf, currentGameTime);
    }

    /**
     * Checks whether the wolf is on sparring cooldown at a specific game time.
     */
    public static boolean isOnCooldown(Wolf wolf, long currentGameTime) {
        if (wolf == null) {
            return false;
        }
        if (wolf instanceof WolfExtensions ext) {
            long extExpiry = ext.betterdogs$getSparringCooldownUntil();
            if (extExpiry > 0L && currentGameTime < extExpiry) {
                return true;
            }
        }
        UUID uuid = wolf.getUUID();
        if (uuid != null) {
            return isOnCooldown(uuid, currentGameTime);
        }
        return false;
    }

    /**
     * Checks whether a wolf UUID is on sparring cooldown at a specific game time.
     */
    public static boolean isOnCooldown(UUID wolfUuid, long currentGameTime) {
        if (wolfUuid == null) {
            return false;
        }
        Long expiry = COOLDOWN_EXPIRY.get(wolfUuid);
        if (expiry != null) {
            if (currentGameTime < expiry) {
                return true;
            } else {
                COOLDOWN_EXPIRY.remove(wolfUuid);
            }
        }
        return false;
    }

    /**
     * Clears all recorded sparring cooldowns (useful for unit testing).
     */
    public static void clearCooldowns() {
        COOLDOWN_EXPIRY.clear();
    }

    /**
     * Checks whether the wolf is eligible to initiate or participate in play sparring.
     */
    public static boolean isEligibleForPlay(Wolf wolf) {
        if (wolf == null || !wolf.isAlive() || !wolf.isTame() || wolf.isOrderedToSit() || wolf.isLeashed() || wolf.getTarget() != null) {
            return false;
        }
        if (!BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_DOG_PLAY_FIGHTING, true)) {
            return false;
        }
        if (isOnCooldown(wolf)) {
            return false;
        }
        if (getPlayPenaltyTicks(wolf) > 0) {
            return false;
        }
        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getPlayPenaltyTicks() > 0) {
                return false;
            }
            return !ext.betterdogs$isSocialModeActive() && !ext.betterdogs$hasBloodFeud();
        }
        return false;
    }

    /**
     * Pure state evaluation overload for test suites and headless verification.
     */
    public static boolean isEligibleForPlay(boolean isAlive, boolean isTame, boolean isSitting, boolean isLeashed,
                                            boolean hasTarget, boolean isOnCooldown, int penaltyTicks,
                                            boolean isSocialActive, boolean hasBloodFeud) {
        if (!isAlive || !isTame || isSitting || isLeashed || hasTarget || isOnCooldown || penaltyTicks > 0 || isSocialActive || hasBloodFeud) {
            return false;
        }
        return true;
    }

    /**
     * Calculates the penalty duration in ticks from minutes (default 10 mins = 12,000 ticks).
     */
    public static int calculatePenaltyTicks(int minutes) {
        return Math.max(0, minutes) * 1200;
    }

    /**
     * Calculates the penalty duration in ticks based on the world's playPenaltyMinutes GameRule.
     */
    public static int calculatePenaltyTicks(net.minecraft.world.level.Level level) {
        return calculatePenaltyTicks(BetterDogsGameRules.getPlayPenaltyMinutes(level));
    }

    /**
     * Gets the remaining penalty ticks for a wolf.
     */
    public static int getPlayPenaltyTicks(Wolf wolf) {
        if (wolf == null) {
            return 0;
        }
        if (wolf instanceof WolfExtensions ext) {
            int extTicks = ext.betterdogs$getPlayPenaltyTicks();
            if (extTicks > 0) {
                return extTicks;
            }
        }
        UUID uuid = wolf.getUUID();
        return uuid != null ? PLAY_PENALTY_TRACKER.getOrDefault(uuid, 0) : 0;
    }

    /**
     * Sets the penalty ticks for a wolf.
     */
    public static void setPlayPenaltyTicks(Wolf wolf, int ticks) {
        if (wolf == null) {
            return;
        }
        int safeTicks = Math.max(0, ticks);
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setPlayPenaltyTicks(safeTicks);
        }
        UUID uuid = wolf.getUUID();
        if (uuid != null) {
            if (safeTicks > 0) {
                PLAY_PENALTY_TRACKER.put(uuid, safeTicks);
            } else {
                PLAY_PENALTY_TRACKER.remove(uuid);
            }
        }
    }

    /**
     * Gets the remaining penalty ticks for a wolf UUID.
     */
    public static int getPlayPenaltyTicks(UUID wolfUuid) {
        if (wolfUuid == null) {
            return 0;
        }
        return PLAY_PENALTY_TRACKER.getOrDefault(wolfUuid, 0);
    }

    /**
     * Sets the penalty ticks for a wolf UUID.
     */
    public static void setPlayPenaltyTicks(UUID wolfUuid, int ticks) {
        if (wolfUuid == null) {
            return;
        }
        int safeTicks = Math.max(0, ticks);
        if (safeTicks > 0) {
            PLAY_PENALTY_TRACKER.put(wolfUuid, safeTicks);
        } else {
            PLAY_PENALTY_TRACKER.remove(wolfUuid);
        }
    }

    /**
     * Decrements the penalty ticks by 1 for a wolf UUID down to 0 safely.
     */
    public static int decrementPlayPenalty(UUID wolfUuid) {
        if (wolfUuid == null) {
            return 0;
        }
        Integer current = PLAY_PENALTY_TRACKER.get(wolfUuid);
        if (current == null || current <= 0) {
            PLAY_PENALTY_TRACKER.remove(wolfUuid);
            return 0;
        }
        int next = current - 1;
        if (next > 0) {
            PLAY_PENALTY_TRACKER.put(wolfUuid, next);
        } else {
            PLAY_PENALTY_TRACKER.remove(wolfUuid);
        }
        return next;
    }

    /**
     * Clears all in-memory penalty tracking entries (useful for unit testing).
     */
    public static void clearPlayPenalties() {
        PLAY_PENALTY_TRACKER.clear();
    }

    /**
     * Reads the penalty ticks value from a CompoundTag.
     */
    public static int readPlayPenaltyNbt(net.minecraft.nbt.CompoundTag tag) {
        if (tag == null) {
            return 0;
        }
        return tag.getIntOr(NBT_KEY_PLAY_PENALTY_TICKS, 0);
    }

    /**
     * Writes the penalty ticks value into a CompoundTag.
     */
    public static void writePlayPenaltyNbt(net.minecraft.nbt.CompoundTag tag, int ticks) {
        if (tag == null) {
            return;
        }
        if (ticks > 0) {
            tag.putInt(NBT_KEY_PLAY_PENALTY_TICKS, ticks);
        }
    }

    /**
     * Checks whether two wolves are compatible packmates that can play spar together.
     */
    public static boolean canPlayTogether(Wolf wolfA, Wolf wolfB) {
        if (!isEligibleForPlay(wolfA) || !isEligibleForPlay(wolfB) || wolfA == wolfB) {
            return false;
        }
        LivingEntity ownerA = wolfA.getOwner();
        LivingEntity ownerB = wolfB.getOwner();
        if (ownerA == null || ownerA != ownerB) {
            return false;
        }
        return wolfA.distanceToSqr(wolfB) <= (DEFAULT_PARTNER_RADIUS * DEFAULT_PARTNER_RADIUS);
    }

    /**
     * Finds an eligible packmate for play fighting in the local vicinity.
     */
    public static Wolf findPlayPartner(Wolf wolf, double radius) {
        if (!isEligibleForPlay(wolf) || wolf.level() == null || wolf.level().isClientSide()) {
            return null;
        }
        List<Wolf> nearbyWolves = wolf.level().getEntitiesOfClass(
                Wolf.class,
                wolf.getBoundingBox().inflate(radius),
                w -> canPlayTogether(wolf, w)
        );
        if (nearbyWolves.isEmpty()) {
            return null;
        }
        return nearbyWolves.get(wolf.getRandom().nextInt(nearbyWolves.size()));
    }

    /**
     * Starts a play sparring session between two wolves.
     */
    public static void startPlaySession(Wolf wolfA, Wolf wolfB) {
        if (canPlayTogether(wolfA, wolfB)) {
            if (wolfA instanceof WolfExtensions extA) {
                extA.betterdogs$setSocialState(wolfB, WolfExtensions.SocialAction.PLAY_FIGHT, SPARRING_DURATION_TICKS);
            }
            if (wolfB instanceof WolfExtensions extB) {
                extB.betterdogs$setSocialState(wolfA, WolfExtensions.SocialAction.PLAY_FIGHT, SPARRING_DURATION_TICKS);
            }
            applyCooldown(wolfA, SPARRING_DURATION_TICKS);
            applyCooldown(wolfB, SPARRING_DURATION_TICKS);

            if (wolfA.getUUID() != null && wolfB.getUUID() != null) {
                int threshold = MIN_POUNCE_THRESHOLD;
                if (wolfA.getRandom() != null) {
                    threshold += wolfA.getRandom().nextInt(MAX_POUNCE_THRESHOLD - MIN_POUNCE_THRESHOLD + 1);
                }
                initSession(wolfA.getUUID(), wolfB.getUUID(), threshold);
            }

            // Award Roughhousing advancement to nearby players (within 16 blocks) witnessing sparring
            WolfAdvancementHelper.awardRoughhousingNearby(wolfA, wolfB);
        }
    }

    /**
     * Retrieves or creates a sparring session with a randomized pounce threshold (3–5 pounces).
     */
    public static SparringSessionData getOrCreateSession(UUID a, UUID b, net.minecraft.util.RandomSource random) {
        if (a == null || b == null) return null;
        SparringSessionKey key = new SparringSessionKey(a, b);
        return ACTIVE_SESSIONS.computeIfAbsent(key, k -> {
            int threshold = MIN_POUNCE_THRESHOLD;
            if (random != null) {
                threshold += random.nextInt(MAX_POUNCE_THRESHOLD - MIN_POUNCE_THRESHOLD + 1);
            }
            return new SparringSessionData(threshold);
        });
    }

    /**
     * Explicitly registers or overrides a sparring session threshold (useful for testing).
     */
    public static void initSession(UUID a, UUID b, int threshold) {
        if (a == null || b == null) return;
        SparringSessionKey key = new SparringSessionKey(a, b);
        ACTIVE_SESSIONS.put(key, new SparringSessionData(threshold));
    }

    /**
     * Gets the current cumulative pounce count for a sparring session between two wolf UUIDs.
     */
    public static int getPounceCount(UUID a, UUID b) {
        if (a == null || b == null) return 0;
        SparringSessionKey key = new SparringSessionKey(a, b);
        SparringSessionData session = ACTIVE_SESSIONS.get(key);
        return session != null ? session.getPounceCount() : 0;
    }

    /**
     * Gets the current cumulative pounce count for a sparring session between two wolves.
     */
    public static int getPounceCount(Wolf a, Wolf b) {
        if (a == null || b == null) return 0;
        return getPounceCount(a.getUUID(), b.getUUID());
    }

    /**
     * Gets the pounce threshold for a sparring session between two wolf UUIDs.
     */
    public static int getPounceThreshold(UUID a, UUID b) {
        if (a == null || b == null) return 0;
        SparringSessionKey key = new SparringSessionKey(a, b);
        SparringSessionData session = ACTIVE_SESSIONS.get(key);
        return session != null ? session.getThreshold() : 0;
    }

    /**
     * Gets the pounce threshold for a sparring session between two wolves.
     */
    public static int getPounceThreshold(Wolf a, Wolf b) {
        if (a == null || b == null) return 0;
        return getPounceThreshold(a.getUUID(), b.getUUID());
    }

    /**
     * Increments the pounce count for the session between two wolf UUIDs.
     */
    public static int incrementPounces(UUID a, UUID b) {
        if (a == null || b == null) return 0;
        SparringSessionData session = getOrCreateSession(a, b, null);
        return session != null ? session.incrementPounces() : 0;
    }

    /**
     * Ends the active sparring session between two wolf UUIDs.
     */
    public static void endSession(UUID a, UUID b) {
        if (a == null || b == null) return;
        SparringSessionKey key = new SparringSessionKey(a, b);
        ACTIVE_SESSIONS.remove(key);
    }

    /**
     * Ends the active sparring session between two wolves.
     */
    public static void endSession(Wolf a, Wolf b) {
        if (a == null || b == null) return;
        endSession(a.getUUID(), b.getUUID());
    }

    /**
     * Clears all active sparring session data (useful for unit testing).
     */
    public static void clearSessions() {
        ACTIVE_SESSIONS.clear();
    }

    /**
     * Calculates hierarchy correction snap damage:
     * - Adults receive 0.5 hearts (1.0f) damage.
     * - Puppies receive 0.0f damage (gentle reprimand growl, adult patience).
     */
    public static float calculateCorrectionDamage(Wolf victim) {
        if (victim == null || victim.isBaby()) {
            return CORRECTION_DAMAGE_PUPPY;
        }
        return CORRECTION_DAMAGE_ADULT;
    }

    /**
     * Overload for test suites and state evaluation without entity instantiation.
     */
    public static float calculateCorrectionDamage(boolean isBaby) {
        return isBaby ? CORRECTION_DAMAGE_PUPPY : CORRECTION_DAMAGE_ADULT;
    }

    /**
     * Records a hierarchy correction snap event.
     */
    public static void recordCorrectionSnap(Wolf discipliningDog, Wolf victim) {
        if (discipliningDog == null || victim == null) return;
        long gameTime = (victim.level() != null) ? victim.level().getGameTime() : 0L;
        long expiry = gameTime + BASELINE_SPARRING_COOLDOWN_TICKS;
        recordCorrectionSnap(discipliningDog.getUUID(), victim.getUUID(), expiry);
        if (victim instanceof WolfExtensions ext) {
            ext.betterdogs$setLastCorrectionSnapSource(discipliningDog.getUUID());
            ext.betterdogs$setLastCorrectionSnapExpiry(expiry);
        }
    }

    /**
     * Records a hierarchy correction snap event by UUID with an explicit expiry timestamp.
     */
    public static void recordCorrectionSnap(UUID discipliningId, UUID victimId, long expiryGameTime) {
        if (discipliningId == null || victimId == null) return;
        LAST_CORRECTION_DISCIPLINER.put(victimId, discipliningId);
        CORRECTION_SNAP_EXPIRY.put(victimId, expiryGameTime);
    }

    /**
     * Checks whether the attacker recently executed a hierarchy correction snap on the victim.
     */
    public static boolean isRecentCorrectionSnap(Wolf victim, LivingEntity attacker) {
        if (victim == null || !(attacker instanceof Wolf discipliningWolf)) return false;
        long gameTime = (victim.level() != null) ? victim.level().getGameTime() : 0L;
        return isRecentCorrectionSnap(victim.getUUID(), discipliningWolf.getUUID(), gameTime);
    }

    /**
     * Checks whether an attacker UUID recently executed a hierarchy correction snap on a victim UUID at a specific game time.
     */
    public static boolean isRecentCorrectionSnap(UUID victimId, UUID attackerId, long currentGameTime) {
        if (victimId == null || attackerId == null) return false;
        UUID discipliner = LAST_CORRECTION_DISCIPLINER.get(victimId);
        if (discipliner == null || !discipliner.equals(attackerId)) return false;
        Long expiry = CORRECTION_SNAP_EXPIRY.get(victimId);
        if (expiry != null && expiry > 0L && currentGameTime > expiry) {
            LAST_CORRECTION_DISCIPLINER.remove(victimId);
            CORRECTION_SNAP_EXPIRY.remove(victimId);
            return false;
        }
        return true;
    }

    /**
     * Convenience overload for checking recent correction snaps without specific game time.
     */
    public static boolean isRecentCorrectionSnap(UUID victimId, UUID attackerId) {
        return isRecentCorrectionSnap(victimId, attackerId, 0L);
    }

    /**
     * Clears all recorded correction snap interactions (useful for unit testing).
     */
    public static void clearCorrectionSnaps() {
        LAST_CORRECTION_DISCIPLINER.clear();
        CORRECTION_SNAP_EXPIRY.clear();
    }

    /**
     * Executes the calm dog's hierarchy correction snap on the playful dog.
     */
    public static void executeCorrectionSnap(Wolf calmDog, Wolf playfulDog) {
        if (calmDog == null || playfulDog == null) {
            return;
        }

        // 1. Record hierarchy correction snap before damage so The Muzzle & Retaliation Suppression activate immediately
        recordCorrectionSnap(calmDog, playfulDog);

        ServerLevel serverLevel = (calmDog.level() instanceof ServerLevel sl) ? sl :
                ((playfulDog.level() instanceof ServerLevel sl2) ? sl2 : null);

        // 2. Audio: SoundEvents.WOLF_GROWL / WolfSoundSet growl (pitch ~0.8–1.0F)
        float growlPitch = 0.8F + (calmDog.getRandom() != null ? calmDog.getRandom().nextFloat() * 0.2F : 0.1F);
        if (serverLevel != null) {
            SoundEvent growlSound = SoundEvents.WOLF_GROWL_BABY.value();
            if (calmDog instanceof WolfAccessor accessor) {
                try {
                    growlSound = accessor.betterdogs$invokeGetSoundSet().growlSound().value();
                } catch (Throwable ignored) {
                }
            }
            serverLevel.playSound(null, calmDog.getX(), calmDog.getY(), calmDog.getZ(),
                    growlSound, SoundSource.NEUTRAL, 1.0F, growlPitch);
        }

        // 3. Animation: Attack swing / head tilt
        calmDog.swing(InteractionHand.MAIN_HAND);
        calmDog.setIsInterested(true);
        if (calmDog.getLookControl() != null) {
            calmDog.getLookControl().setLookAt(playfulDog, 30.0F, 30.0F);
        }

        // 4. Damage calculation:
        // - If victim is an adult: deals 0.5 hearts (1.0f) damage.
        // - If victim is a puppy (victim.isBaby()): deals 0.0 damage (gentle reprimand growl, adult patience).
        float damage = calculateCorrectionDamage(playfulDog);
        if (damage > 0.0F && serverLevel != null) {
            DamageSource source = serverLevel.damageSources().mobAttack(calmDog);
            playfulDog.hurtServer(serverLevel, source, damage);
        }

        // 5. Playful dog response: whimpers (SoundEvents.WOLF_WHINE / WolfSoundSet whine)
        float whinePitch = 1.2F + (playfulDog.getRandom() != null ? playfulDog.getRandom().nextFloat() * 0.2F : 0.1F);
        if (serverLevel != null) {
            SoundEvent whineSound = SoundEvents.WOLF_WHINE_BABY.value();
            if (playfulDog instanceof WolfAccessor accessor) {
                try {
                    whineSound = accessor.betterdogs$invokeGetSoundSet().whineSound().value();
                } catch (Throwable ignored) {
                }
            }
            serverLevel.playSound(null, playfulDog.getX(), playfulDog.getY(), playfulDog.getZ(),
                    whineSound, SoundSource.NEUTRAL, 1.0F, whinePitch);
        }

        // 6. Playful dog 10-minute penalty state application
        int penaltyMinutes = BetterDogsGameRules.getPlayPenaltyMinutes(playfulDog.level());
        int penaltyTicks = calculatePenaltyTicks(penaltyMinutes);
        setPlayPenaltyTicks(playfulDog, penaltyTicks);

        // 7. Award Know Your Place advancement to nearby players (within 16 blocks) witnessing hierarchy correction snap
        WolfAdvancementHelper.awardKnowYourPlaceNearby(calmDog, playfulDog);

        // 8. Playful dog immediately terminates play session
        terminatePlaySession(calmDog, playfulDog);
    }

    /**
     * Terminates a sparring session between two wolves immediately and resets goals and movement.
     */
    public static void terminatePlaySession(Wolf wolfA, Wolf wolfB) {
        if (wolfA != null) {
            if (wolfA instanceof WolfExtensions extA) {
                extA.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
            }
            applyCooldown(wolfA);
            if (wolfA.getNavigation() != null) {
                wolfA.getNavigation().stop();
            }
        }
        if (wolfB != null) {
            if (wolfB instanceof WolfExtensions extB) {
                extB.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
            }
            applyCooldown(wolfB);
            if (wolfB.getNavigation() != null) {
                wolfB.getNavigation().stop();
            }
        }
        if (wolfA != null && wolfB != null) {
            endSession(wolfA, wolfB);
        }
    }

    /**
     * Handles a sparring pounce event, evaluating pounce thresholds and triggering a hierarchy correction snap if applicable.
     *
     * @return true if a hierarchy correction snap was triggered, false if play feedback occurred normally.
     */
    public static boolean handlePounce(Wolf pouncer, Wolf target) {
        if (pouncer == null || target == null) {
            return false;
        }
        UUID idA = pouncer.getUUID();
        UUID idB = target.getUUID();
        if (idA == null || idB == null) {
            return false;
        }

        SparringSessionData session = getOrCreateSession(idA, idB, pouncer.getRandom());
        if (session == null) {
            return false;
        }

        int currentPounces = session.incrementPounces();

        // Check if one is playful and the other is NOT playful
        boolean playfulPouncer = WolfDispositionHelper.isPlayful(pouncer);
        boolean playfulTarget = WolfDispositionHelper.isPlayful(target);

        if (playfulPouncer != playfulTarget) {
            Wolf playfulDog = playfulPouncer ? pouncer : target;
            Wolf calmDog = playfulPouncer ? target : pouncer;

            if (currentPounces >= session.getThreshold()) {
                executeCorrectionSnap(calmDog, playfulDog);
                return true;
            }
        }

        // Standard sparring play feedback (happy villager particles & playful sounds)
        applyPlayFeedback(pouncer, target);
        return false;
    }

    /**
     * Plays sensory cues (particles and audio) and increases inter-dog affinity.
     */
    public static void applyPlayFeedback(Wolf wolf, Wolf partner) {
        if (wolf == null || partner == null || wolf.level() == null || !(wolf.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.sendParticles(
                ParticleTypes.HAPPY_VILLAGER,
                (wolf.getX() + partner.getX()) * 0.5,
                (wolf.getY() + partner.getY()) * 0.5 + 0.5,
                (wolf.getZ() + partner.getZ()) * 0.5,
                3,
                0.2, 0.2, 0.2, 0.0
        );

        SoundEvent playSound = SoundEvents.WOLF_AMBIENT_BABY.value();
        float playVolume = 0.7F;
        float playPitch = 1.3F + (wolf.getRandom() != null ? wolf.getRandom().nextFloat() * 0.3F : 0.15F);

        if (wolf instanceof WolfAccessor accessor) {
            try {
                var soundSet = accessor.betterdogs$invokeGetSoundSet();
                int roll = wolf.getRandom() != null ? wolf.getRandom().nextInt(3) : 0;
                if (roll == 0) {
                    playSound = soundSet.pantSound().value();
                    playVolume = 0.75F;
                    playPitch = 1.1F + (wolf.getRandom() != null ? wolf.getRandom().nextFloat() * 0.2F : 0.1F);
                } else if (roll == 1) {
                    playSound = soundSet.growlSound().value();
                    playVolume = 0.6F;
                    playPitch = 1.35F + (wolf.getRandom() != null ? wolf.getRandom().nextFloat() * 0.25F : 0.15F);
                } else {
                    playSound = soundSet.ambientSound().value();
                    playVolume = 0.8F;
                    playPitch = 1.25F + (wolf.getRandom() != null ? wolf.getRandom().nextFloat() * 0.25F : 0.15F);
                }
            } catch (Throwable ignored) {
            }
        }
        serverLevel.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(),
                playSound, SoundSource.NEUTRAL, playVolume, playPitch);

        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$adjustAffinity(partner.getStringUUID(), 1);
        }
        if (partner instanceof WolfExtensions partnerExt) {
            partnerExt.betterdogs$adjustAffinity(wolf.getStringUUID(), 1);
        }

        // Award Roughhousing advancement to nearby players (within 16 blocks) witnessing sparring
        WolfAdvancementHelper.awardRoughhousingNearby(wolf, partner);
    }

    /**
     * Evaluates and coordinates pack audience reactions via PackAudienceHelper.
     */
    public static int evaluateAudience(Wolf wolfA, Wolf wolfB) {
        return PackAudienceHelper.tickAudience(wolfA, wolfB);
    }
}
