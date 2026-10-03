// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

public interface WolfExtensions {
    WolfPersonality betterdogs$getPersonality();
    void betterdogs$setPersonality(WolfPersonality personality);
    boolean betterdogs$hasPersonality();

    float betterdogs$getSocialScale();
    void betterdogs$setSocialScale(float scale);

    long betterdogs$getDnaSeed();

    String betterdogs$getFavoriteTreat();

    void betterdogs$setFavoriteTreat(String treat);

    long betterdogs$getSoothedTime();

    void betterdogs$setSoothedTime(long time);

    net.minecraft.core.BlockPos betterdogs$getSoundLocationTarget();
    void betterdogs$setSoundLocationTarget(net.minecraft.core.BlockPos pos);

    int betterdogs$getPassiveOverrideTicks();
    void betterdogs$setPassiveOverrideTicks(int ticks);

    String betterdogs$getNemesisEntityType();
    void betterdogs$setNemesisEntityType(String type);

    long betterdogs$getNemesisExpiryTime();
    void betterdogs$setNemesisExpiryTime(long time);

    void betterdogs$setCollarColor(net.minecraft.world.item.DyeColor color);

    java.util.UUID betterdogs$getParentUUID1();
    void betterdogs$setParentUUID1(java.util.UUID uuid);

    java.util.UUID betterdogs$getParentUUID2();
    void betterdogs$setParentUUID2(java.util.UUID uuid);

    boolean betterdogs$isInbred();
    void betterdogs$setInbred(boolean inbred);

    boolean betterdogs$isGuarding();
    void betterdogs$setGuarding(boolean guarding);

    net.minecraft.core.BlockPos betterdogs$getGuardPos();
    void betterdogs$setGuardPos(net.minecraft.core.BlockPos pos);

    boolean betterdogs$isUpForAdoption();
    void betterdogs$setUpForAdoption(boolean adoption);

    long betterdogs$getLastGiftDay();
    void betterdogs$setLastGiftDay(long day);

    int betterdogs$getZoomiesTicks();
    void betterdogs$setZoomiesTicks(int ticks);

    boolean betterdogs$hasFetchedStick();
    void betterdogs$setHasFetchedStick(boolean fetched);

    int betterdogs$getCalmTicks();
    void betterdogs$setCalmTicks(int ticks);

    int betterdogs$getHowlingTicks();
    void betterdogs$setHowlingTicks(int ticks);

    int betterdogs$getFeedCount();
    void betterdogs$setFeedCount(int count);

    net.minecraft.world.entity.LivingEntity betterdogs$getRetaliationTarget();
    void betterdogs$setRetaliationTarget(net.minecraft.world.entity.LivingEntity target, int ticks);
    int betterdogs$getRetaliationTicks();

    String betterdogs$getBloodFeudTarget();
    void betterdogs$setBloodFeudTarget(String targetUuid);
    boolean betterdogs$hasBloodFeud();

    int betterdogs$getPlayFightCooldown();
    void betterdogs$setPlayFightCooldown(int ticks);

    java.util.UUID betterdogs$getLeaderUUID();
    void betterdogs$setLeaderUUID(java.util.UUID uuid);
    boolean betterdogs$isPackLeader();
    void betterdogs$setPackLeader(boolean isLeader);

    int betterdogs$getWanderlustTicks();
    void betterdogs$setWanderlustTicks(int ticks);

    // ========== Pack Play Sparring Cooldown (Step 1) ==========
    default long betterdogs$getSparringCooldownUntil() {
        return 0L;
    }
    default void betterdogs$setSparringCooldownUntil(long gameTime) {
    }

    // ========== Hierarchy Correction Snap (Step 3) ==========
    default java.util.UUID betterdogs$getLastCorrectionSnapSource() {
        return null;
    }
    default void betterdogs$setLastCorrectionSnapSource(java.util.UUID source) {
    }
    default long betterdogs$getLastCorrectionSnapExpiry() {
        return 0L;
    }
    default void betterdogs$setLastCorrectionSnapExpiry(long expiryTime) {
    }

    // ========== 10-Minute Penalty State & Subdued Mood Posture (Step 4) ==========
    default int betterdogs$getPlayPenaltyTicks() {
        return 0;
    }
    default void betterdogs$setPlayPenaltyTicks(int ticks) {
    }
    default boolean betterdogs$isSubdued() {
        return betterdogs$getPlayPenaltyTicks() > 0;
    }

    // ========== Pack Audience Spectator Wagging (Step 5) ==========
    default int betterdogs$getSpectatorWagTicks() {
        return 0;
    }
    default void betterdogs$setSpectatorWagTicks(int ticks) {
    }
    default boolean betterdogs$isSpectatorWagging() {
        return betterdogs$getSpectatorWagTicks() > 0;
    }

    default boolean betterdogs$isSittingManually() {
        return false;
    }
    default boolean betterdogs$isSocialModeActive() {
        return false;
    }
}
