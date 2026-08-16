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
    public static final String NBT_KEY_PARENT_1 = "BetterDogsParent1";
    public static final String NBT_KEY_PARENT_2 = "BetterDogsParent2";
    public static final String NBT_KEY_IS_INBRED = "BetterDogsIsInbred";
    public static final String NBT_KEY_IS_GUARDING = "BetterDogsIsGuarding";
    public static final String NBT_KEY_GUARD_X = "BetterDogsGuardX";
    public static final String NBT_KEY_GUARD_Y = "BetterDogsGuardY";
    public static final String NBT_KEY_GUARD_Z = "BetterDogsGuardZ";
    public static final String NBT_KEY_IS_UP_FOR_ADOPTION = "BetterDogsIsUpForAdoption";
    public static final String NBT_KEY_LAST_GIFT_DAY = "BetterDogsLastGiftDay";
    public static final String NBT_KEY_FEED_COUNT = "BetterDogsFeedCount";

    // ========== Modern 1.21.11 Save Data APIs (ValueOutput / ValueInput) ==========

    public static void writeToSaveData(ValueOutput output, WolfPersonality personality, float socialScale, long dnaSeed) {
        writeToSaveData(output, personality, socialScale, dnaSeed, "", 0L, "", 0L, null, null, false, false, null, false, -1L);
    }

    public static void writeToSaveData(ValueOutput output, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat) {
        writeToSaveData(output, personality, socialScale, dnaSeed, favoriteTreat, 0L, "", 0L, null, null, false, false, null, false, -1L);
    }

    public static void writeToSaveData(ValueOutput output, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime) {
        writeToSaveData(output, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, "", 0L, null, null, false, false, null, false, -1L);
    }

    public static void writeToSaveData(ValueOutput output, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry) {
        writeToSaveData(output, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, nemesisType, nemesisExpiry, null, null, false, false, null, false, -1L);
    }

    public static void writeToSaveData(ValueOutput output, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry, UUID parent1, UUID parent2, boolean isInbred) {
        writeToSaveData(output, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, nemesisType, nemesisExpiry, parent1, parent2, isInbred, false, null, false, -1L);
    }

    public static void writeToSaveData(ValueOutput output, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry, UUID parent1, UUID parent2, boolean isInbred, boolean isGuarding, net.minecraft.core.BlockPos guardPos) {
        writeToSaveData(output, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, nemesisType, nemesisExpiry, parent1, parent2, isInbred, isGuarding, guardPos, false, -1L);
    }

    public static void writeToSaveData(ValueOutput output, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry, UUID parent1, UUID parent2, boolean isInbred, boolean isGuarding, net.minecraft.core.BlockPos guardPos, boolean isUpForAdoption) {
        writeToSaveData(output, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, nemesisType, nemesisExpiry, parent1, parent2, isInbred, isGuarding, guardPos, isUpForAdoption, -1L);
    }

    public static void writeToSaveData(ValueOutput output, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry, UUID parent1, UUID parent2, boolean isInbred, boolean isGuarding, net.minecraft.core.BlockPos guardPos, boolean isUpForAdoption, long lastGiftDay) {
        writeToSaveData(output, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, nemesisType, nemesisExpiry, parent1, parent2, isInbred, isGuarding, guardPos, isUpForAdoption, lastGiftDay, 0);
    }

    public static void writeToSaveData(ValueOutput output, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry, UUID parent1, UUID parent2, boolean isInbred, boolean isGuarding, net.minecraft.core.BlockPos guardPos, boolean isUpForAdoption, long lastGiftDay, int feedCount) {
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
            if (parent1 != null) {
                output.putString(NBT_KEY_PARENT_1, parent1.toString());
            }
            if (parent2 != null) {
                output.putString(NBT_KEY_PARENT_2, parent2.toString());
            }
            output.putInt(NBT_KEY_IS_INBRED, isInbred ? 1 : 0);
            output.putInt(NBT_KEY_IS_GUARDING, isGuarding ? 1 : 0);
            if (guardPos != null) {
                output.putInt(NBT_KEY_GUARD_X, guardPos.getX());
                output.putInt(NBT_KEY_GUARD_Y, guardPos.getY());
                output.putInt(NBT_KEY_GUARD_Z, guardPos.getZ());
            }
            output.putInt(NBT_KEY_IS_UP_FOR_ADOPTION, isUpForAdoption ? 1 : 0);
            if (lastGiftDay >= 0L) {
                output.putLong(NBT_KEY_LAST_GIFT_DAY, lastGiftDay);
            }
            if (feedCount > 0) {
                output.putInt(NBT_KEY_FEED_COUNT, feedCount);
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

    public static UUID readParentUUID1FromSaveData(ValueInput input) {
        if (input != null) {
            return input.getString(NBT_KEY_PARENT_1).map(s -> {
                try { return UUID.fromString(s); } catch (Exception e) { return null; }
            }).orElse(null);
        }
        return null;
    }

    public static UUID readParentUUID2FromSaveData(ValueInput input) {
        if (input != null) {
            return input.getString(NBT_KEY_PARENT_2).map(s -> {
                try { return UUID.fromString(s); } catch (Exception e) { return null; }
            }).orElse(null);
        }
        return null;
    }

    public static boolean readIsInbredFromSaveData(ValueInput input) {
        if (input != null) {
            return input.getInt(NBT_KEY_IS_INBRED).map(v -> v != 0).orElse(false);
        }
        return false;
    }

    public static boolean readIsGuardingFromSaveData(ValueInput input) {
        if (input != null) {
            return input.getInt(NBT_KEY_IS_GUARDING).map(v -> v != 0).orElse(false);
        }
        return false;
    }

    public static boolean readIsUpForAdoptionFromSaveData(ValueInput input) {
        if (input != null) {
            return input.getInt(NBT_KEY_IS_UP_FOR_ADOPTION).map(v -> v != 0).orElse(false);
        }
        return false;
    }

    public static long readLastGiftDayFromSaveData(ValueInput input) {
        if (input != null) {
            return input.getLong(NBT_KEY_LAST_GIFT_DAY).orElse(-1L);
        }
        return -1L;
    }

    public static int readFeedCountFromSaveData(ValueInput input) {
        if (input != null) {
            return input.getInt(NBT_KEY_FEED_COUNT).orElse(0);
        }
        return 0;
    }

    public static net.minecraft.core.BlockPos readGuardPosFromSaveData(ValueInput input) {
        if (input != null) {
            var optX = input.getInt(NBT_KEY_GUARD_X);
            var optY = input.getInt(NBT_KEY_GUARD_Y);
            var optZ = input.getInt(NBT_KEY_GUARD_Z);
            if (optX.isPresent() && optY.isPresent() && optZ.isPresent()) {
                return new net.minecraft.core.BlockPos(optX.get(), optY.get(), optZ.get());
            }
        }
        return null;
    }

    // ========== NBT CompoundTag Helpers (Unit Testing & Legacy Compatibility) ==========

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed) {
        writeToNbt(tag, personality, socialScale, dnaSeed, "", 0L, "", 0L, null, null, false);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, 0L, "", 0L, null, null, false);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, "", 0L, null, null, false);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, nemesisType, nemesisExpiry, null, null, false);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry, UUID parent1, UUID parent2, boolean isInbred) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, nemesisType, nemesisExpiry, parent1, parent2, isInbred, false, null, false);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry, UUID parent1, UUID parent2, boolean isInbred, boolean isGuarding, net.minecraft.core.BlockPos guardPos) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, nemesisType, nemesisExpiry, parent1, parent2, isInbred, isGuarding, guardPos, false, -1L);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry, UUID parent1, UUID parent2, boolean isInbred, boolean isGuarding, net.minecraft.core.BlockPos guardPos, boolean isUpForAdoption) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, nemesisType, nemesisExpiry, parent1, parent2, isInbred, isGuarding, guardPos, isUpForAdoption, -1L);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry, UUID parent1, UUID parent2, boolean isInbred, boolean isGuarding, net.minecraft.core.BlockPos guardPos, boolean isUpForAdoption, long lastGiftDay) {
        writeToNbt(tag, personality, socialScale, dnaSeed, favoriteTreat, soothedTime, nemesisType, nemesisExpiry, parent1, parent2, isInbred, isGuarding, guardPos, isUpForAdoption, lastGiftDay, 0);
    }

    public static void writeToNbt(CompoundTag tag, WolfPersonality personality, float socialScale, long dnaSeed, String favoriteTreat, long soothedTime, String nemesisType, long nemesisExpiry, UUID parent1, UUID parent2, boolean isInbred, boolean isGuarding, net.minecraft.core.BlockPos guardPos, boolean isUpForAdoption, long lastGiftDay, int feedCount) {
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
                tag.putString(NBT_KEY_PARENT_1, parent1.toString());
            }
            if (parent2 != null) {
                tag.putString(NBT_KEY_PARENT_2, parent2.toString());
            }
            tag.putBoolean(NBT_KEY_IS_INBRED, isInbred);
            tag.putBoolean(NBT_KEY_IS_GUARDING, isGuarding);
            if (guardPos != null) {
                tag.putInt(NBT_KEY_GUARD_X, guardPos.getX());
                tag.putInt(NBT_KEY_GUARD_Y, guardPos.getY());
                tag.putInt(NBT_KEY_GUARD_Z, guardPos.getZ());
            }
            tag.putBoolean(NBT_KEY_IS_UP_FOR_ADOPTION, isUpForAdoption);
            if (lastGiftDay >= 0L) {
                tag.putLong(NBT_KEY_LAST_GIFT_DAY, lastGiftDay);
            }
            if (feedCount > 0) {
                tag.putInt(NBT_KEY_FEED_COUNT, feedCount);
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

    public static UUID readParentUUID1FromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_PARENT_1)) {
            return tag.getString(NBT_KEY_PARENT_1).map(s -> {
                try { return UUID.fromString(s); } catch (Exception e) { return null; }
            }).orElse(null);
        }
        return null;
    }

    public static UUID readParentUUID2FromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_PARENT_2)) {
            return tag.getString(NBT_KEY_PARENT_2).map(s -> {
                try { return UUID.fromString(s); } catch (Exception e) { return null; }
            }).orElse(null);
        }
        return null;
    }

    public static boolean readIsInbredFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_IS_INBRED)) {
            return tag.getBoolean(NBT_KEY_IS_INBRED).orElse(false);
        }
        return false;
    }

    public static boolean readIsGuardingFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_IS_GUARDING)) {
            return tag.getBoolean(NBT_KEY_IS_GUARDING).orElse(false);
        }
        return false;
    }

    public static net.minecraft.core.BlockPos readGuardPosFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_GUARD_X) && tag.contains(NBT_KEY_GUARD_Y) && tag.contains(NBT_KEY_GUARD_Z)) {
            var optX = tag.getInt(NBT_KEY_GUARD_X);
            var optY = tag.getInt(NBT_KEY_GUARD_Y);
            var optZ = tag.getInt(NBT_KEY_GUARD_Z);
            if (optX.isPresent() && optY.isPresent() && optZ.isPresent()) {
                return new net.minecraft.core.BlockPos(optX.get(), optY.get(), optZ.get());
            }
        }
        return null;
    }

    public static boolean readIsUpForAdoptionFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_IS_UP_FOR_ADOPTION)) {
            return tag.getBoolean(NBT_KEY_IS_UP_FOR_ADOPTION).orElse(false);
        }
        return false;
    }

    public static long readLastGiftDayFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_LAST_GIFT_DAY)) {
            return tag.getLong(NBT_KEY_LAST_GIFT_DAY).orElse(-1L);
        }
        return -1L;
    }

    public static int readFeedCountFromNbt(CompoundTag tag) {
        if (tag != null && tag.contains(NBT_KEY_FEED_COUNT)) {
            return tag.getInt(NBT_KEY_FEED_COUNT).orElse(0);
        }
        return 0;
    }
}
