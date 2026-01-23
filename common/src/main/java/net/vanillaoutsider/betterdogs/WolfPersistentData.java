package net.vanillaoutsider.betterdogs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.wolf.Wolf;

/**
 * Persistent wolf data using Fabric Data Attachment API.
 */
public record WolfPersistentData(int personalityId, int lastDamageTime) {

    public static final WolfPersistentData DEFAULT = new WolfPersistentData(-1, 0);

    public static final Codec<WolfPersistentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("personality").forGetter(WolfPersistentData::personalityId),
            Codec.INT.fieldOf("lastDamageTime").forGetter(WolfPersistentData::lastDamageTime))
            .apply(instance, WolfPersistentData::new));

    /**
     * Attachment type registration.
     */
    public static class Attachments {
        public static final AttachmentType<WolfPersistentData> WOLF_DATA = AttachmentRegistry.createPersistent(
                ResourceLocation.parse("betterdogs:wolf_data"),
                CODEC);

        public static void init() {
            // Force class loading
            @SuppressWarnings("unused")
            var ignored = WOLF_DATA;
        }
    }

    // ========== Static Helper Methods (replacing Kotlin extensions) ==========

    public static WolfPersistentData getWolfData(Wolf wolf) {
        return wolf.getAttachedOrCreate(Attachments.WOLF_DATA, () -> DEFAULT);
    }

    public static void setWolfData(Wolf wolf, WolfPersistentData data) {
        wolf.setAttached(Attachments.WOLF_DATA, data);
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
