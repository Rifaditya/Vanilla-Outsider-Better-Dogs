// Verified against: Wolf.java (26.1.2+)
package net.vanillaoutsider.betterdogs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

/**
 * Persistent wolf data record.
 * v1.10.000: Simplified for baby training system.
 */
public record WolfPersistentData(int personalityId, int lastDamageTime, boolean submissive, String bloodFeudTarget, long lastMischiefDay, long dna, float scale, Map<String, Integer> affinityMap, Optional<UUID> leaderUuid, boolean guardMode, Optional<BlockPos> guardPos) {

    public static final WolfPersistentData DEFAULT = new WolfPersistentData(-1, 0, false, "", 0L, 0L, 1.0f, Map.of(), Optional.empty(), false, Optional.empty());

    public static final Codec<WolfPersistentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("personality", -1).forGetter(WolfPersistentData::personalityId),
            Codec.INT.optionalFieldOf("lastDamageTime", 0).forGetter(WolfPersistentData::lastDamageTime),
            Codec.BOOL.optionalFieldOf("submissive", false).forGetter(WolfPersistentData::submissive),
            Codec.STRING.optionalFieldOf("bloodFeudTarget", "").forGetter(WolfPersistentData::bloodFeudTarget),
            Codec.LONG.optionalFieldOf("lastMischiefDay", 0L).forGetter(WolfPersistentData::lastMischiefDay),
            Codec.LONG.optionalFieldOf("dna", 0L).forGetter(WolfPersistentData::dna),
            Codec.FLOAT.optionalFieldOf("scale", 1.0f).forGetter(WolfPersistentData::scale),
            Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("affinityMap", Map.of()).forGetter(WolfPersistentData::affinityMap),
            net.minecraft.core.UUIDUtil.CODEC.optionalFieldOf("leaderUuid").forGetter(WolfPersistentData::leaderUuid),
            Codec.BOOL.optionalFieldOf("guardMode", false).forGetter(WolfPersistentData::guardMode),
            BlockPos.CODEC.optionalFieldOf("guardPos").forGetter(WolfPersistentData::guardPos))
            .apply(instance, WolfPersistentData::new));

    // ========== Static Helper Methods (using Fabric Attachment API) ==========

    public static WolfPersistentData getWolfData(Wolf wolf) {
        return wolf.getAttachedOrCreate(BetterDogs.WOLF_DATA, () -> WolfPersistentData.DEFAULT);
    }

    public static void setWolfData(Wolf wolf, WolfPersistentData data) {
        wolf.setAttached(BetterDogs.WOLF_DATA, data);
    }

    // ========== Personality ==========

    public static WolfPersonality getPersistedPersonality(Wolf wolf) {
        return WolfPersonality.fromId(getWolfData(wolf).personalityId());
    }

    public static void setPersistedPersonality(Wolf wolf, WolfPersonality personality) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(personality.getId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos()));
    }

    public static boolean hasPersistedPersonality(Wolf wolf) {
        return getWolfData(wolf).personalityId() >= 0;
    }

    // ========== Last Damage Time (for passive healing) ==========

    public static int getPersistedLastDamageTime(Wolf wolf) {
        return getWolfData(wolf).lastDamageTime();
    }

    public static void setPersistedLastDamageTime(Wolf wolf, int time) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), time, current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos()));
    }

    // ========== Submissive (baby cannot attack pack after correction) ==========

    public static boolean isPersistedSubmissive(Wolf wolf) {
        return getWolfData(wolf).submissive();
    }

    public static void setPersistedSubmissive(Wolf wolf, boolean submissive) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), submissive, current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos()));
    }

    // ========== Blood Feud (permanent vendetta) ==========

    public static String getPersistedBloodFeudTarget(Wolf wolf) {
        return getWolfData(wolf).bloodFeudTarget();
    }

    public static void setPersistedBloodFeudTarget(Wolf wolf, String targetUuid) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), targetUuid, current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos()));
    }

    public static boolean hasBloodFeud(Wolf wolf) {
        return !getWolfData(wolf).bloodFeudTarget().isEmpty();
    }

    // ========== Last Mischief Day (daily mischief tracking) ==========

    public static long getPersistedLastMischiefDay(Wolf wolf) {
        return getWolfData(wolf).lastMischiefDay();
    }

    public static void setPersistedLastMischiefDay(Wolf wolf, long day) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), day, current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos()));
    }

    // ========== DNA & Scale (Social Core) ==========

    public static long getDNA(Wolf wolf) {
        return getWolfData(wolf).dna();
    }

    public static void setDNA(Wolf wolf, long dna) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), dna, current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos()));
    }

    public static float getScale(Wolf wolf) {
        return getWolfData(wolf).scale();
    }

    public static void setScale(int personalityId, int lastDamageTime, boolean submissive, @Nullable String bloodFeudTarget, long lastMischiefDay, long dna, Wolf wolf, float scale, Map<String, Integer> affinityMap, Optional<UUID> leaderUuid, boolean guardMode, Optional<BlockPos> guardPos) {
        setWolfData(wolf, new WolfPersistentData(personalityId, lastDamageTime, submissive, bloodFeudTarget, lastMischiefDay, dna, scale, affinityMap, leaderUuid, guardMode, guardPos));
    }

    // ========== Social Bonding (v3.1.37) ==========

    public static int getPersistedAffinity(Wolf wolf, String targetUuid) {
        return getWolfData(wolf).affinityMap().getOrDefault(targetUuid, 0);
    }

    public static void adjustPersistedAffinity(Wolf wolf, String targetUuid, int delta) {
        WolfPersistentData current = getWolfData(wolf);
        Map<String, Integer> newMap = new HashMap<>(current.affinityMap());
        int newValue = Math.clamp(newMap.getOrDefault(targetUuid, 0) + delta, -100, 100);
        newMap.put(targetUuid, newValue);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), newMap, current.leaderUuid(), current.guardMode(), current.guardPos()));
    }

    // ========== Leader UUID (v3.1.37) ==========

    public static Optional<UUID> getPersistedLeaderUuid(Wolf wolf) {
        return getWolfData(wolf).leaderUuid();
    }

    public static void setPersistedLeaderUuid(Wolf wolf, @Nullable UUID uuid) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), Optional.ofNullable(uuid), current.guardMode(), current.guardPos()));
    }

    // ========== Guard Mode (v3.5.0) ==========

    public static boolean isPersistedGuardMode(Wolf wolf) {
        return getWolfData(wolf).guardMode();
    }

    public static void setPersistedGuardMode(Wolf wolf, boolean guardMode) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), guardMode, current.guardPos()));
    }

    public static Optional<BlockPos> getPersistedGuardPos(Wolf wolf) {
        return getWolfData(wolf).guardPos();
    }

    public static void setPersistedGuardPos(Wolf wolf, @Nullable BlockPos pos) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), Optional.ofNullable(pos)));
    }
}
