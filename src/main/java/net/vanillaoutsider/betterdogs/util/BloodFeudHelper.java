// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.jspecify.annotations.Nullable;

import java.util.Random;
import java.util.UUID;

/**
 * Dedicated single-purpose helper for wolf vendetta blood feuds.
 */
public final class BloodFeudHelper {

    public static final double DEFAULT_SEARCH_RADIUS = 20.0;

    private BloodFeudHelper() {
    }

    /**
     * Checks whether the wolf currently holds an active blood feud.
     */
    public static boolean hasBloodFeud(Wolf wolf) {
        if (wolf instanceof WolfExtensions ext) {
            return ext.betterdogs$hasBloodFeud();
        }
        return false;
    }

    /**
     * Retrieves the string UUID representation of the nemesis wolf.
     */
    public static String getBloodFeudTarget(@Nullable Wolf wolf) {
        if (wolf instanceof WolfExtensions ext) {
            String target = ext.betterdogs$getBloodFeudTarget();
            return target != null ? target : "";
        }
        return "";
    }

    /**
     * Evaluates whether {@code source} holds an active blood feud targeting {@code target} without throwing on null/empty UUIDs.
     */
    public static boolean matchesBloodFeudTarget(@Nullable Wolf source, @Nullable Wolf target) {
        return source instanceof WolfExtensions ext && matchesBloodFeudTarget(ext, target);
    }

    /**
     * Null- and empty-safe blood feud target UUID comparison for mixins and combat helpers.
     */
    public static boolean matchesBloodFeudTarget(@Nullable WolfExtensions sourceExt, @Nullable Wolf target) {
        if (sourceExt == null || target == null || !sourceExt.betterdogs$hasBloodFeud()) {
            return false;
        }
        String feudTarget = sourceExt.betterdogs$getBloodFeudTarget();
        if (feudTarget == null || feudTarget.isEmpty()) {
            return false;
        }
        String targetUuid = target.getStringUUID();
        return targetUuid != null && !targetUuid.isEmpty() && feudTarget.equals(targetUuid);
    }

    /**
     * Sets or clears the nemesis wolf UUID for a blood feud.
     */
    public static void setBloodFeudTarget(Wolf wolf, String targetUuid) {
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setBloodFeudTarget(targetUuid != null ? targetUuid : "");
        }
    }

    /**
     * Clears any active blood feud target on the wolf.
     */
    public static void clearBloodFeud(Wolf wolf) {
        setBloodFeudTarget(wolf, "");
    }

    /**
     * Evaluates probability roll for triggering a blood feud upon conflict escalation.
     */
    public static boolean shouldTriggerBloodFeud(Wolf wolf, Random random) {
        if (wolf == null || wolf.level() == null || random == null) {
            return false;
        }
        int feudChance = DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_BLOOD_FEUD_PERCENT);
        if (feudChance <= 0) {
            return false;
        }
        if (feudChance >= 100) {
            return true;
        }
        return random.nextInt(100) < feudChance;
    }

    /**
     * Finds the nemesis wolf in the local vicinity matching the stored UUID.
     */
    public static Wolf findNemesis(Wolf wolf, String uuidString, double searchRadius) {
        if (wolf == null || wolf.level() == null || uuidString == null || uuidString.isEmpty()) {
            return null;
        }
        try {
            UUID uuid = UUID.fromString(uuidString);
            AABB searchBox = wolf.getBoundingBox().inflate(searchRadius);

            for (Wolf w : wolf.level().getEntitiesOfClass(Wolf.class, searchBox)) {
                if (w != wolf && w.isAlive() && w.getUUID().equals(uuid)) {
                    return w;
                }
            }
        } catch (IllegalArgumentException ignored) {
            clearBloodFeud(wolf);
        }
        return null;
    }
}
