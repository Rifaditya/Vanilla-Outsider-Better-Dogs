package net.vanillaoutsider.betterdogs.platform;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.neoforged.fml.loading.FMLPaths;
import net.vanillaoutsider.betterdogs.WolfPersistentData;

import java.nio.file.Path;
import java.util.WeakHashMap;

/**
 * NeoForge implementation of platform services.
 * Uses a simple WeakHashMap cache since NeoForge doesn't have Fabric's Attachment API.
 * Data is persisted via EntityDataSerializers/NBT in mixins.
 */
public class NeoForgePlatformServices implements PlatformServices {

    // Cache for wolf data (entity -> data)
    private static final WeakHashMap<Wolf, WolfPersistentData> WOLF_DATA_CACHE = new WeakHashMap<>();

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public WolfPersistentData getWolfData(Wolf wolf) {
        return WOLF_DATA_CACHE.getOrDefault(wolf, WolfPersistentData.DEFAULT);
    }

    @Override
    public void setWolfData(Wolf wolf, WolfPersistentData data) {
        WOLF_DATA_CACHE.put(wolf, data);
    }

    @Override
    public void initAttachments() {
        // NeoForge uses NBT-based persistence via mixins
        // No special initialization needed
    }

    // ========== NBT Helpers for Mixin persistence ==========

    public static void saveToNbt(Wolf wolf, CompoundTag tag) {
        WolfPersistentData data = WOLF_DATA_CACHE.get(wolf);
        if (data != null) {
            CompoundTag betterDogsTag = new CompoundTag();
            betterDogsTag.putInt("personality", data.personalityId());
            betterDogsTag.putInt("lastDamageTime", data.lastDamageTime());
            tag.put("BetterDogsData", betterDogsTag);
        }
    }

    public static void loadFromNbt(Wolf wolf, CompoundTag tag) {
        if (tag.contains("BetterDogsData")) {
            // NeoForge 26.1 uses Optional-based NBT API
            tag.getCompound("BetterDogsData").ifPresent(betterDogsTag -> {
                int personality = betterDogsTag.getInt("personality").orElse(0);
                int lastDamageTime = betterDogsTag.getInt("lastDamageTime").orElse(0);
                WOLF_DATA_CACHE.put(wolf, new WolfPersistentData(personality, lastDamageTime));
            });
        }
    }
}
