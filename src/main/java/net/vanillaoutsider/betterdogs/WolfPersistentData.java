// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;

public class WolfPersistentData {

    public static final String NBT_KEY_PERSONALITY = "BetterDogsPersonality";
    public static final String NBT_KEY_SOCIAL_SCALE = "BetterDogsSocialScale";
    public static final String NBT_KEY_DNA_SEED = "BetterDogsDnaSeed";
    public static final String NBT_KEY_FAVORITE_TREAT = "BetterDogsFavoriteTreat";
    public static final String NBT_KEY_SOOTHED_TIME = "BetterDogsSoothedTime";
    public static final String NBT_KEY_NEMESIS_TYPE = "BetterDogsNemesisType";
    public static final String NBT_KEY_NEMESIS_EXPIRY = "BetterDogsNemesisExpiry";
    public static final String NBT_KEY_PARENT_1 = "BetterDogsParent1";
    public static final String NBT_KEY_PARENT_2 = "BetterDogsParent2";
    public static final String NBT_KEY_IS_INBRED = "BetterDogsIsInbred";
    public static final String NBT_KEY_IS_GUARDING = "BetterDogsIsGuarding";
    public static final String NBT_KEY_GUARD_X = "BetterDogsGuardX";
    public static final String NBT_KEY_GUARD_Y = "BetterDogsGuardY";
    public static final String NBT_KEY_GUARD_Z = "BetterDogsGuardZ";
    public static final String NBT_KEY_IS_UP_FOR_ADOPTION = "BetterDogsIsUpForAdoption";

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed) {
        writeToNbt(tag, personality, socialScale, dnaSeed, "", 0L, "", 0L, null, null, false, false, null, false);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, 0L, "", 0L, null, null, false, false, null, false);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, "", 0L, null, null, false, false, null, false);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, nemesisType, nemesisExpiry, null, null, false, false, null, false);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry, UUID parent1, UUID parent2, boolean isInbred) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, nemesisType, nemesisExpiry, parent1, parent2, isInbred, false, null, false);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry, UUID parent1, UUID parent2, boolean isInbred, boolean isGuarding, net.minecraft.core.BlockPos guardPos) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, nemesisType, nemesisExpiry, parent1, parent2, isInbred, isGuarding, guardPos, false);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry, UUID parent1, UUID parent2, boolean isInbred, boolean isGuarding, net.minecraft.core.BlockPos guardPos, boolean isUpForAdoption) {
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
            if (parent1 != null) {
                tag.putUUID(NBT_KEY_PARENT_1, parent1);
            }
            if (parent2 != null) {
                tag.putUUID(NBT_KEY_PARENT_2, parent2);
            }
            tag.putBoolean(NBT_KEY_IS_INBRED, isInbred);
            tag.putBoolean(NBT_KEY_IS_GUARDING, isGuarding);
            if (guardPos != null) {
                tag.putInt(NBT_KEY_GUARD_X, guardPos.getX());
                tag.putInt(NBT_KEY_GUARD_Y, guardPos.getY());
                tag.putInt(NBT_KEY_GUARD_Z, guardPos.getZ());
            }
            tag.putBoolean(NBT_KEY_IS_UP_FOR_ADOPTION, isUpForAdoption);
        }
    }

    public static String readNemesisTypeFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_NEMESIS_TYPE)) {
            return tag.getString(NBT_KEY_NEMESIS_TYPE);
        }
        return "";
    }

    public static long readNemesisExpiryFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_NEMESIS_EXPIRY)) {
            return tag.getLong(NBT_KEY_NEMESIS_EXPIRY);
        }
        return 0L;
    }

    public static long readSoothedTimeFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_SOOTHED_TIME)) {
            return tag.getLong(NBT_KEY_SOOTHED_TIME);
        }
        return 0L;
    }

    public static String readFavoriteTreatFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_FAVORITE_TREAT)) {
            return tag.getString(NBT_KEY_FAVORITE_TREAT);
        }
        return "";
    }

    public static WolfPersonality readPersonalityFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_PERSONALITY)) {
            return WolfPersonality.fromString(tag.getString(NBT_KEY_PERSONALITY));
        }
        return WolfPersonality.NORMAL;
    }

    public static float readSocialScaleFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_SOCIAL_SCALE)) {
            return tag.getFloat(NBT_KEY_SOCIAL_SCALE);
        }
        return 1.0f;
    }

    public static long readDnaSeedFromNbt(CompoundTag tag, UUID entityUuid) {
        if (tag != null && tag.contains(NBT_KEY_DNA_SEED)) {
            return tag.getLong(NBT_KEY_DNA_SEED);
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

    public static UUID readParentUUID1FromNbt(CompoundTag tag) {
        if (tag != null && tag.hasUUID(NBT_KEY_PARENT_1)) {
            return tag.getUUID(NBT_KEY_PARENT_1);
        }
        return null;
    }

    public static UUID readParentUUID2FromNbt(CompoundTag tag) {
        if (tag != null && tag.hasUUID(NBT_KEY_PARENT_2)) {
            return tag.getUUID(NBT_KEY_PARENT_2);
        }
        return null;
    }

    public static boolean readIsInbredFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_IS_INBRED)) {
            return tag.getBoolean(NBT_KEY_IS_INBRED);
        }
        return false;
    }

    public static boolean readIsGuardingFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_IS_GUARDING)) {
            return tag.getBoolean(NBT_KEY_IS_GUARDING);
        }
        return false;
    }

    public static net.minecraft.core.BlockPos readGuardPosFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_GUARD_X) && tag.contains(NBT_KEY_GUARD_Y) && tag.contains(NBT_KEY_GUARD_Z)) {
            return new net.minecraft.core.BlockPos(tag.getInt(NBT_KEY_GUARD_X), tag.getInt(NBT_KEY_GUARD_Y), tag.getInt(NBT_KEY_GUARD_Z));
        }
        return null;
    }

    public static boolean readIsUpForAdoptionFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_IS_UP_FOR_ADOPTION)) {
            return tag.getBoolean(NBT_KEY_IS_UP_FOR_ADOPTION);
        }
        return false;
    }
}
