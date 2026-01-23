package net.vanillaoutsider.betterdogs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.platform.Services;

/**
 * Persistent wolf data record.
 * Platform-specific attachment logic is in fabric/ and neoforge/ modules.
 */
public record WolfPersistentData(int personalityId, int lastDamageTime) {

    public static final WolfPersistentData DEFAULT = new WolfPersistentData(-1, 0);

    public static final Codec<WolfPersistentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("personality").forGetter(WolfPersistentData::personalityId),
            Codec.INT.fieldOf("lastDamageTime").forGetter(WolfPersistentData::lastDamageTime))
            .apply(instance, WolfPersistentData::new));

    // ========== Static Helper Methods (using Fabric Attachment API) ==========

    public static WolfPersistentData getWolfData(Wolf wolf) {
        return wolf.getAttachedOrCreate(BetterDogs.WOLF_DATA, () -> WolfPersistentData.DEFAULT);
    }

    public static void setWolfData(Wolf wolf, WolfPersistentData data) {
        wolf.setAttached(BetterDogs.WOLF_DATA, data);
    }

    public static WolfPersonality getPersistedPersonality(Wolf wolf) {
        return WolfPersonality.fromId(getWolfData(wolf).personalityId());
    }

    public static void setPersistedPersonality(Wolf wolf, WolfPersonality personality) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(personality.getId(), current.lastDamageTime()));
    }

    public static boolean hasPersistedPersonality(Wolf wolf) {
        return getWolfData(wolf).personalityId() >= 0;
    }

    public static int getPersistedLastDamageTime(Wolf wolf) {
        return getWolfData(wolf).lastDamageTime();
    }

    public static void setPersistedLastDamageTime(Wolf wolf, int time) {
        WolfPersistentData current = getWolfData(wolf);
        setWolfData(wolf, new WolfPersistentData(current.personalityId(), time));
    }
}
