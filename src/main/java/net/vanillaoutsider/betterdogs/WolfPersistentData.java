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
 * v4.3.1: Updated for personality-based range stats.
 * v4.4.0: Genetics fields migrated to DasikLibrary.
 */
public record WolfPersistentData(int personalityId, int lastDamageTime, boolean submissive, String bloodFeudTarget, long lastMischiefDay, long dna, float scale, Map<String, Integer> affinityMap, Optional<UUID> leaderUuid, boolean guardMode, Optional<BlockPos> guardPos, boolean adoptable) {

    public static final WolfPersistentData DEFAULT = new WolfPersistentData(-1, 0, false, "", 0L, 0L, 1.0f, Map.of(), Optional.empty(), false, Optional.empty(), false);

    private record BaseData(int personalityId, int lastDamageTime, boolean submissive, String bloodFeudTarget, long lastMischiefDay, long dna, float scale, Map<String, Integer> affinityMap, Optional<UUID> leaderUuid, boolean guardMode) {
        static final com.mojang.serialization.MapCodec<BaseData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.optionalFieldOf("personality", -1).forGetter(BaseData::personalityId),
                Codec.INT.optionalFieldOf("lastDamageTime", 0).forGetter(BaseData::lastDamageTime),
                Codec.BOOL.optionalFieldOf("submissive", false).forGetter(BaseData::submissive),
                Codec.STRING.optionalFieldOf("bloodFeudTarget", "").forGetter(BaseData::bloodFeudTarget),
                Codec.LONG.optionalFieldOf("lastMischiefDay", 0L).forGetter(BaseData::lastMischiefDay),
                Codec.LONG.optionalFieldOf("dna", 0L).forGetter(BaseData::dna),
                Codec.FLOAT.optionalFieldOf("scale", 1.0f).forGetter(BaseData::scale),
                Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("affinityMap", Map.of()).forGetter(BaseData::affinityMap),
                net.minecraft.core.UUIDUtil.CODEC.optionalFieldOf("leaderUuid").forGetter(BaseData::leaderUuid),
                Codec.BOOL.optionalFieldOf("guardMode", false).forGetter(BaseData::guardMode)
        ).apply(instance, BaseData::new));
    }

    private record ExtraData(Optional<BlockPos> guardPos, boolean adoptable) {
        static final com.mojang.serialization.MapCodec<ExtraData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BlockPos.CODEC.optionalFieldOf("guardPos").forGetter(ExtraData::guardPos),
                Codec.BOOL.optionalFieldOf("adoptable", false).forGetter(ExtraData::adoptable)
        ).apply(instance, ExtraData::new));
    }

    public static final Codec<WolfPersistentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BaseData.CODEC.forGetter(data -> new BaseData(data.personalityId(), data.lastDamageTime(), data.submissive(), data.bloodFeudTarget(), data.lastMischiefDay(), data.dna(), data.scale(), data.affinityMap(), data.leaderUuid(), data.guardMode())),
            ExtraData.CODEC.forGetter(data -> new ExtraData(data.guardPos(), data.adoptable()))
    ).apply(instance, (base, extra) -> new WolfPersistentData(
            base.personalityId(), base.lastDamageTime(), base.submissive(), base.bloodFeudTarget(), base.lastMischiefDay(), base.dna(), base.scale(), base.affinityMap(), base.leaderUuid(), base.guardMode(),
            extra.guardPos(), extra.adoptable()
    )));

    // ========== Static Helper Methods (using Fabric Attachment API) ==========

    public static WolfPersistentData getWolfData(Wolf wolf) {
        return wolf.getAttachedOrCreate(BetterDogs.WOLF_DATA, () -> WolfPersistentData.DEFAULT);
    }

    public static void setWolfData(Wolf wolf, WolfPersistentData data) {
        wolf.setAttached(BetterDogs.WOLF_DATA, data);
    }

    // ========== Personality ==========

    public static int getPersistedPersonalityId(Wolf wolf) {
        return getWolfData(wolf).personalityId();
    }

    public static WolfPersonality getPersistedPersonality(Wolf wolf) {
        return WolfPersonality.fromId(getWolfData(wolf).personalityId());
    }

    public static void setPersistedPersonalityId(Wolf wolf, int id) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(id, current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable()));
    }

    public static void setPersistedPersonality(Wolf wolf, WolfPersonality personality) {
        setPersistedPersonalityId(wolf, personality.getId());
    }

    public static boolean hasPersistedPersonality(Wolf wolf) {
        return getWolfData(wolf).personalityId() >= 0;
    }

    // ========== Personality Correction (last correction tracking) ==========

    public static int getPersistedLastDamageTime(Wolf wolf) {
        return getWolfData(wolf).lastDamageTime();
    }

    public static void setPersistedLastDamageTime(Wolf wolf, int time) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), time, current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable()));
    }

    // ========== Submissive (baby cannot attack pack after correction) ==========

    public static boolean isPersistedSubmissive(Wolf wolf) {
        return getWolfData(wolf).submissive();
    }

    public static void setPersistedSubmissive(Wolf wolf, boolean submissive) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), submissive, current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable()));
    }

    // ========== Blood Feud (permanent vendetta) ==========

    public static String getPersistedBloodFeudTarget(Wolf wolf) {
        return getWolfData(wolf).bloodFeudTarget();
    }

    public static void setPersistedBloodFeudTarget(Wolf wolf, String targetUuid) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), targetUuid, current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable()));
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
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), day, current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable()));
    }

    // ========== DNA & Scale (Social Core) ==========

    public static long getDNA(Wolf wolf) {
        return getWolfData(wolf).dna();
    }

    public static void setDNA(Wolf wolf, long dna) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), dna, current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable()));
    }

    public static float getScale(Wolf wolf) {
        return getWolfData(wolf).scale();
    }

    public static void setScale(int personalityId, int lastDamageTime, boolean submissive, @Nullable String bloodFeudTarget, long lastMischiefDay, long dna, Wolf wolf, float scale, Map<String, Integer> affinityMap, Optional<UUID> leaderUuid, boolean guardMode, Optional<BlockPos> guardPos, boolean adoptable) {
        setWolfData(wolf, new WolfPersistentData(personalityId, lastDamageTime, submissive, bloodFeudTarget, lastMischiefDay, dna, scale, affinityMap, leaderUuid, guardMode, guardPos, adoptable));
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
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), newMap, current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable()));
    }

    // ========== Leader UUID (v3.1.37) ==========

    public static Optional<UUID> getPersistedLeaderUuid(Wolf wolf) {
        return getWolfData(wolf).leaderUuid();
    }

    public static void setPersistedLeaderUuid(Wolf wolf, @Nullable UUID uuid) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), Optional.ofNullable(uuid), current.guardMode(), current.guardPos(), current.adoptable()));
    }

    // ========== Guard Mode (v3.5.0) ==========

    public static boolean isPersistedGuardMode(Wolf wolf) {
        return getWolfData(wolf).guardMode();
    }

    public static void setPersistedGuardMode(Wolf wolf, boolean guardMode) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), guardMode, current.guardPos(), current.adoptable()));
    }

    public static Optional<BlockPos> getPersistedGuardPos(Wolf wolf) {
        return getWolfData(wolf).guardPos();
    }

    public static void setPersistedGuardPos(Wolf wolf, @Nullable BlockPos pos) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), Optional.ofNullable(pos), current.adoptable()));
    }

    // ========== Adoption System (v4.3.0) ==========

    public static boolean isPersistedAdoptable(Wolf wolf) {
        return getWolfData(wolf).adoptable();
    }

    public static void setPersistedAdoptable(Wolf wolf, boolean adoptable) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), current.scale(), current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), adoptable));
    }

    // ========== Range Stats (v4.3.1) - Migrated to DasikLibrary ==========

    public static float getPersistedHealthBonus(Wolf wolf) {
        net.dasik.social.api.genetics.EntityGenetics genetics = net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf);
        return genetics.traits().getOrDefault("max_health", 0.0f);
    }

    public static void setPersistedHealthBonus(Wolf wolf, float hp) {
        net.dasik.social.api.genetics.EntityGenetics genetics = net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf);
        java.util.Map<String, Float> traits = new HashMap<>(genetics.traits());
        traits.put("max_health", hp);
        net.dasik.social.api.genetics.GeneticsEngine.setGenetics(wolf, new net.dasik.social.api.genetics.EntityGenetics(
            genetics.parent1Uuid(),
            genetics.parent2Uuid(),
            genetics.inbred(),
            genetics.traitsRolled(),
            traits
        ));
    }

    public static float getPersistedDamageMod(Wolf wolf) {
        net.dasik.social.api.genetics.EntityGenetics genetics = net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf);
        return genetics.traits().getOrDefault("attack_damage", 0.0f);
    }

    public static void setPersistedDamageMod(Wolf wolf, float dmg) {
        net.dasik.social.api.genetics.EntityGenetics genetics = net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf);
        java.util.Map<String, Float> traits = new HashMap<>(genetics.traits());
        traits.put("attack_damage", dmg);
        net.dasik.social.api.genetics.GeneticsEngine.setGenetics(wolf, new net.dasik.social.api.genetics.EntityGenetics(
            genetics.parent1Uuid(),
            genetics.parent2Uuid(),
            genetics.inbred(),
            genetics.traitsRolled(),
            traits
        ));
    }

    public static float getPersistedSpeedMod(Wolf wolf) {
        net.dasik.social.api.genetics.EntityGenetics genetics = net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf);
        return genetics.traits().getOrDefault("movement_speed", 0.0f);
    }

    public static void setPersistedSpeedMod(Wolf wolf, float speed) {
        net.dasik.social.api.genetics.EntityGenetics genetics = net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf);
        java.util.Map<String, Float> traits = new HashMap<>(genetics.traits());
        traits.put("movement_speed", speed);
        net.dasik.social.api.genetics.GeneticsEngine.setGenetics(wolf, new net.dasik.social.api.genetics.EntityGenetics(
            genetics.parent1Uuid(),
            genetics.parent2Uuid(),
            genetics.inbred(),
            genetics.traitsRolled(),
            traits
        ));
    }

    public static boolean arePersistedStatsRolled(Wolf wolf) {
        return net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf).traitsRolled();
    }

    public static void setPersistedStatsRolled(Wolf wolf, boolean rolled) {
        net.dasik.social.api.genetics.EntityGenetics genetics = net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf);
        net.dasik.social.api.genetics.GeneticsEngine.setGenetics(wolf, new net.dasik.social.api.genetics.EntityGenetics(
            genetics.parent1Uuid(),
            genetics.parent2Uuid(),
            genetics.inbred(),
            rolled,
            genetics.traits()
        ));
    }

    // ========== Kinship / Parents ==========

    public static Optional<UUID> getPersistedParent1Uuid(Wolf wolf) {
        return net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf).parent1Uuid();
    }

    public static Optional<UUID> getPersistedParent2Uuid(Wolf wolf) {
        return net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf).parent2Uuid();
    }

    public static boolean isPersistedInbred(Wolf wolf) {
        return net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf).inbred();
    }

    public static void setPersistedParentsAndInbred(Wolf wolf, @Nullable UUID p1, @Nullable UUID p2, boolean inbred) {
        net.dasik.social.api.genetics.EntityGenetics genetics = net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf);
        net.dasik.social.api.genetics.GeneticsEngine.setGenetics(wolf, new net.dasik.social.api.genetics.EntityGenetics(
            Optional.ofNullable(p1),
            Optional.ofNullable(p2),
            inbred,
            genetics.traitsRolled(),
            genetics.traits()
        ));
    }
}
