// Verified against: Wolf.java (26.2+)
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
 * v4.3.1: Updated for personality-based range stats.
 */
public record WolfPersistentData(int personalityId, int lastDamageTime, boolean submissive, String bloodFeudTarget, long lastMischiefDay, long dna, float scale, Map<String, Integer> affinityMap, Optional<UUID> leaderUuid, boolean guardMode, Optional<BlockPos> guardPos, boolean adoptable, float healthBonus, float damageMod, float speedMod, boolean statsRolled) {

    public static final WolfPersistentData DEFAULT = new WolfPersistentData(-1, 0, false, "", 0L, 0L, 1.0f, Map.of(), Optional.empty(), false, Optional.empty(), false, 0.0f, 0.0f, 0.0f, false);

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
            BlockPos.CODEC.optionalFieldOf("guardPos").forGetter(WolfPersistentData::guardPos),
            Codec.BOOL.optionalFieldOf("adoptable", false).forGetter(WolfPersistentData::adoptable),
            Codec.FLOAT.optionalFieldOf("healthBonus", 0.0f).forGetter(WolfPersistentData::healthBonus),
            Codec.FLOAT.optionalFieldOf("damageMod", 0.0f).forGetter(WolfPersistentData::damageMod),
            Codec.FLOAT.optionalFieldOf("speedMod", 0.0f).forGetter(WolfPersistentData::speedMod),
            Codec.BOOL.optionalFieldOf("statsRolled", false).forGetter(WolfPersistentData::statsRolled))
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
        boolean changed = current.personalityId() != personality.getId();
        float healthBonus = changed ? 0.0f : current.healthBonus();
        float damageMod = changed ? 0.0f : current.damageMod();
        float speedMod = changed ? 0.0f : current.speedMod();
        boolean statsRolled = !changed && current.statsRolled();
        setWolfData(wolf, new WolfPersistentData(personality.getId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable(), healthBonus, damageMod, speedMod, statsRolled));
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
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), time, current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable(), current.healthBonus(), current.damageMod(), current.speedMod(), current.statsRolled()));
    }

    // ========== Submissive (baby cannot attack pack after correction) ==========

    public static boolean isPersistedSubmissive(Wolf wolf) {
        return getWolfData(wolf).submissive();
    }

    public static void setPersistedSubmissive(Wolf wolf, boolean submissive) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), submissive, current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable(), current.healthBonus(), current.damageMod(), current.speedMod(), current.statsRolled()));
    }

    // ========== Blood Feud (permanent vendetta) ==========

    public static String getPersistedBloodFeudTarget(Wolf wolf) {
        return getWolfData(wolf).bloodFeudTarget();
    }

    public static void setPersistedBloodFeudTarget(Wolf wolf, String targetUuid) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), targetUuid, current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable(), current.healthBonus(), current.damageMod(), current.speedMod(), current.statsRolled()));
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
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), day, current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable(), current.healthBonus(), current.damageMod(), current.speedMod(), current.statsRolled()));
    }

    // ========== DNA & Scale (Social Core) ==========

    public static long getDNA(Wolf wolf) {
        return getWolfData(wolf).dna();
    }

    public static void setDNA(Wolf wolf, long dna) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), dna, current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable(), current.healthBonus(), current.damageMod(), current.speedMod(), current.statsRolled()));
    }

    public static float getScale(Wolf wolf) {
        return getWolfData(wolf).scale();
    }

    public static void setScale(int personalityId, int lastDamageTime, boolean submissive, @Nullable String bloodFeudTarget, long lastMischiefDay, long dna, Wolf wolf, float scale, Map<String, Integer> affinityMap, Optional<UUID> leaderUuid, boolean guardMode, Optional<BlockPos> guardPos, boolean adoptable) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(personalityId, lastDamageTime, submissive, bloodFeudTarget, lastMischiefDay, dna, scale, affinityMap, leaderUuid, guardMode, guardPos, adoptable, current.healthBonus(), current.damageMod(), current.speedMod(), current.statsRolled()));
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
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), newMap, current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable(), current.healthBonus(), current.damageMod(), current.speedMod(), current.statsRolled()));
    }

    // ========== Leader UUID (v3.1.37) ==========

    public static Optional<UUID> getPersistedLeaderUuid(Wolf wolf) {
        return getWolfData(wolf).leaderUuid();
    }

    public static void setPersistedLeaderUuid(Wolf wolf, @Nullable UUID uuid) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), Optional.ofNullable(uuid), current.guardMode(), current.guardPos(), current.adoptable(), current.healthBonus(), current.damageMod(), current.speedMod(), current.statsRolled()));
    }

    // ========== Guard Mode (v3.5.0) ==========

    public static boolean isPersistedGuardMode(Wolf wolf) {
        return getWolfData(wolf).guardMode();
    }

    public static void setPersistedGuardMode(Wolf wolf, boolean guardMode) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), guardMode, current.guardPos(), current.adoptable(), current.healthBonus(), current.damageMod(), current.speedMod(), current.statsRolled()));
    }

    public static Optional<BlockPos> getPersistedGuardPos(Wolf wolf) {
        return getWolfData(wolf).guardPos();
    }

    public static void setPersistedGuardPos(Wolf wolf, @Nullable BlockPos pos) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), Optional.ofNullable(pos), current.adoptable(), current.healthBonus(), current.damageMod(), current.speedMod(), current.statsRolled()));
    }

    // ========== Adoption System (v4.3.0) ==========

    public static boolean isPersistedAdoptable(Wolf wolf) {
        return getWolfData(wolf).adoptable();
    }

    public static void setPersistedAdoptable(Wolf wolf, boolean adoptable) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), adoptable, current.healthBonus(), current.damageMod(), current.speedMod(), current.statsRolled()));
    }

    // ========== Range Stats (v4.3.1) ==========

    public static float getPersistedHealthBonus(Wolf wolf) {
        return getWolfData(wolf).healthBonus();
    }

    public static void setPersistedHealthBonus(Wolf wolf, float hp) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable(), hp, current.damageMod(), current.speedMod(), current.statsRolled()));
    }

    public static float getPersistedDamageMod(Wolf wolf) {
        return getWolfData(wolf).damageMod();
    }

    public static void setPersistedDamageMod(Wolf wolf, float dmg) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable(), current.healthBonus(), dmg, current.speedMod(), current.statsRolled()));
    }

    public static float getPersistedSpeedMod(Wolf wolf) {
        return getWolfData(wolf).speedMod();
    }

    public static void setPersistedSpeedMod(Wolf wolf, float speed) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable(), current.healthBonus(), current.damageMod(), speed, current.statsRolled()));
    }

    public static boolean arePersistedStatsRolled(Wolf wolf) {
        return getWolfData(wolf).statsRolled();
    }

    public static void setPersistedStatsRolled(Wolf wolf, boolean rolled) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable(), current.healthBonus(), current.damageMod(), current.speedMod(), rolled));
    }
}
