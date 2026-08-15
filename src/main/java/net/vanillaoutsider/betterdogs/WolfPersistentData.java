// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class WolfPersistentData {

    public static final String NBT_KEY_PERSONALITY = "BetterDogsPersonality";
    public static final String NBT_KEY_SOCIAL_SCALE = "BetterDogsSocialScale";
    public static final String NBT_KEY_DNA_SEED = "BetterDogsDnaSeed";
    public static final String NBT_KEY_FAVORITE_TREAT = "BetterDogsFavoriteTreat";
    public static final String NBT_KEY_SOOTHED_TIME = "BetterDogsSoothedTime";
    public static final String NBT_KEY_NEMESIS_TYPE = "BetterDogsNemesisType";
    public static final String NBT_KEY_NEMESIS_EXPIRY = "BetterDogsNemesisExpiry";

    // ========== Modern 1.21.11 Save Data APIs (ValueOutput / ValueInput) ==========

    public static void writeToSaveData(ValueOutput output, WolfPersonality personality, float socialScale, long dnaSeed) {
        writeToSaveData(output, personality, socialScale, dnaSeed, "", 0L, "", 0L);
    }

    public static void writeToSaveData(ValueOutput output, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat) {
        writeToSaveData(output, personality, socialScale, dnaSeed, favoriteTreat, 0L, "", 0L);
    }

    public static void writeToSaveData(ValueOutput output, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime) {
        writeToSaveData(output, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, "", 0L);
    }

    public static void writeToSaveData(ValueOutput output, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry) {
        if (output != null) {
            if (personality != null) {
                output.putString(NBT_KEY_PERSONALITY, personality.getId());
            }
            output.putInt(NBT_KEY_SOCIAL_SCALE, Math.round(socialScale * 10000.0f));
            output.putLong(NBT_KEY_DNA_SEED, dnaSeed);
            if (favoriteTreat != null && !favoriteTreat.isEmpty()) {
                output.putString(NBT_KEY_FAVORITE_TREAT, favoriteTreat);
            }
            if (soothedTime > 0L) {
                output.putLong(NBT_KEY_SOOTHED_TIME, soothedTime);
            }
            if (nemesisType != null && !nemesisType.isEmpty()) {
                output.putString(NBT_KEY_NEMESIS_TYPE, nemesisType);
            }
            if (nemesisExpiry > 0L) {
                output.putLong(NBT_KEY_NEMESIS_EXPIRY, nemesisExpiry);
            }
        }
    }

    public static String readNemesisTypeFromSaveData(ValueInput input) {
        if (input != null) {
            return input.getString(NBT_KEY_NEMESIS_TYPE).orElse("");
        }
        return "";
    }

    public static long readNemesisExpiryFromSaveData(ValueInput input) {
        if (input != null) {
            return input.getLong(NBT_KEY_NEMESIS_EXPIRY).orElse(0L);
        }
        return 0L;
    }

    public static long readSoothedTimeFromSaveData(ValueInput input) {
        if (input != null) {
            return input.getLong(NBT_KEY_SOOTHED_TIME).orElse(0L);
        }
        return 0L;
    }

    public static String readFavoriteTreatFromSaveData(ValueInput input) {
        if (input != null) {
            return input.getString(NBT_KEY_FAVORITE_TREAT).orElse("");
        }
        return "";
    }

    public static WolfPersonality readPersonalityFromSaveData(ValueInput input) {
        if (input != null) {
            return WolfPersonality.fromString(input.getString(NBT_KEY_PERSONALITY).orElse("normal"));
        }
        return WolfPersonality.NORMAL;
    }

    public static float readSocialScaleFromSaveData(ValueInput input) {
        if (input != null) {
            return input.getInt(NBT_KEY_SOCIAL_SCALE).map(v -> v / 10000.0f).orElse(1.0f);
        }
        return 1.0f;
    }

    public static long readDnaSeedFromSaveData(ValueInput input, UUID entityUuid) {
        if (input != null) {
            return input.getLong(NBT_KEY_DNA_SEED).orElseGet(() -> {
                if (entityUuid != null) {
                    return entityUuid.getMostSignificantBits() ^ entityUuid.getLeastSignificantBits();
                }
                return 0L;
            });
        }
        if (entityUuid != null) {
            return entityUuid.getMostSignificantBits() ^ entityUuid.getLeastSignificantBits();
        }
        return 0L;
    }

    // ========== NBT CompoundTag Helpers (Unit Testing & Legacy Compatibility) ==========

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed) {
        writeToNbt(tag, personality, socialScale, dnaSeed, "", 0L, "", 0L);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, 0L, "", 0L);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, "", 0L);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry) {
        if (tag != null) {
            if (personality != null) {
                tag.putString(NBT_KEY_PERSONALITY, personality.getId());
            }
            tag.putFloat(NBT_KEY_SOCIAL_SCALE, socialScale);
            tag.putLong(NBT_KEY_DNA_SEED, dnaSeed);
            if (favoriteTreat != null && !favoriteTreat.isEmpty()) {
                tag.putString(NBT_KEY_FAVORITE_TREAT, favoriteTreat);
            }
            if (soothedTime > 0L) {
                tag.putLong(NBT_KEY_SOOTHED_TIME, soothedTime);
            }
            if (nemesisType != null && !nemesisType.isEmpty()) {
                tag.putString(NBT_KEY_NEMESIS_TYPE, nemesisType);
            }
            if (nemesisExpiry > 0L) {
                tag.putLong(NBT_KEY_NEMESIS_EXPIRY, nemesisExpiry);
            }
        }
    }

    public static String readNemesisTypeFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_NEMESIS_TYPE)) {
            return tag.getString(NBT_KEY_NEMESIS_TYPE).orElse("");
        }
        return "";
    }

    public static long readNemesisExpiryFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_NEMESIS_EXPIRY)) {
            return tag.getLong(NBT_KEY_NEMESIS_EXPIRY).orElse(0L);
        }
        return 0L;
    }

    public static long readSoothedTimeFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_SOOTHED_TIME)) {
            return tag.getLong(NBT_KEY_SOOTHED_TIME).orElse(0L);
        }
        return 0L;
    }

    public static String readFavoriteTreatFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_FAVORITE_TREAT)) {
            return tag.getString(NBT_KEY_FAVORITE_TREAT).orElse("");
        }
        return "";
    }

    public static WolfPersonality readPersonalityFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_PERSONALITY)) {
            return WolfPersonality.fromString(tag.getString(NBT_KEY_PERSONALITY).orElse("normal"));
        }
        return WolfPersonality.NORMAL;
    }

    public static float readSocialScaleFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_SOCIAL_SCALE)) {
            return tag.getFloat(NBT_KEY_SOCIAL_SCALE).orElse(1.0f);
        }
        return 1.0f;
    }

    public static long readDnaSeedFromNbt(CompoundTag tag, UUID entityUuid) {
        if (tag != null && tag.contains(NBT_KEY_DNA_SEED)) {
            return tag.getLong(NBT_KEY_DNA_SEED).orElse(0L);
        }
        if (entityUuid != null) {
            return entityUuid.getMostSignificantBits() ^ entityUuid.getLeastSignificantBits();
        }
        return 0L;
    }

    public static long generateDnaSeed(UUID uuid) {
        if (uuid == null) {
            return 0L;
        }
        return uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits() ^ 5829103L;
    }
}
