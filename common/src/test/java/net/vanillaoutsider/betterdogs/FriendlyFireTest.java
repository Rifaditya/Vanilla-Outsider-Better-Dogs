// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.11
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FriendlyFireTest {

    @BeforeAll
    public static void setupMinecraftBootstrap() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

    private static Path findProjectRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (dir != null) {
            if (Files.exists(dir.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfFriendlyFireHelper.java"))) {
                return dir;
            }
            dir = dir.getParent();
        }
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

    @Test
    @DisplayName("Verify Friendly Fire Null Safety")
    public void testNullSafety() {
        Assertions.assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(null, null));
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(null, null));
    }

    @Test
    @DisplayName("BL-BD-041: Assert bidirectional Blood Feud exemption, symmetry, and bystander protection")
    public void testBloodFeudBidirectionalExemption() {
        UUID ownerId = UUID.randomUUID();
        UUID dogAId = UUID.randomUUID();
        UUID dogBId = UUID.randomUUID();
        UUID dogCId = UUID.randomUUID();

        PackCivilWarImmunityTest.OfflineStubWolf dogA = PackCivilWarImmunityTest.createOfflineWolf(dogAId, ownerId, true, false);
        PackCivilWarImmunityTest.OfflineStubWolf dogB = PackCivilWarImmunityTest.createOfflineWolf(dogBId, ownerId, true, false);
        PackCivilWarImmunityTest.OfflineStubWolf dogC = PackCivilWarImmunityTest.createOfflineWolf(dogCId, ownerId, true, false);

        // Dog A initiates blood feud against Dog B
        dogA.betterdogs$setBloodFeudTarget(dogBId.toString());

        // 1. Bidirectional Feud Pair Symmetry (Crucial fix for defender retaliation)
        Assertions.assertTrue(WolfFriendlyFireHelper.isBloodFeudPair(dogA, dogB), "Aggressor -> Victim must be in feud pair");
        Assertions.assertTrue(WolfFriendlyFireHelper.isBloodFeudPair(dogB, dogA), "Defending Victim -> Aggressor must be in feud pair (bidirectional)");

        // 2. Anti-Cross-Bleed Protection (Anti-Goal 2: Bystander co-owned dogs remain fully protected)
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogA, dogC), "Aggressor with bystander must NOT be in feud pair");
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogC, dogA), "Bystander with aggressor must NOT be in feud pair");
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogB, dogC), "Victim with bystander must NOT be in feud pair");

        // 3. Null & Empty Safety (PAT-20261114-001)
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(null, null), "Null wolves must return false");
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogA, null), "Null target must return false");
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(null, dogB), "Null source must return false");

        // Empty feud target
        dogA.betterdogs$setBloodFeudTarget("");
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogA, dogB), "Empty feud target must return false");
    }

    @Test
    @DisplayName("BL-BD-063: Verify WolfFriendlyFireHelper & BetterDogsGameRules positive polarity and server fallback contract")
    public void testFriendlyFireSourceContracts() throws IOException {
        Path root = findProjectRoot();
        String helperSource = Files.readString(
                root.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfFriendlyFireHelper.java"),
                StandardCharsets.UTF_8);
        String rulesSource = Files.readString(
                root.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/registry/BetterDogsGameRules.java"),
                StandardCharsets.UTF_8);

        Assertions.assertTrue(helperSource.contains("BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_FRIENDLY_FIRE, true)"),
                "WolfFriendlyFireHelper must read BD_FRIENDLY_FIRE with default-true protection fallback");
        Assertions.assertFalse(helperSource.contains("BetterDogsGameRules.BD_FRIENDLY_FIRE, false"),
                "WolfFriendlyFireHelper must never fall back to false for BD_FRIENDLY_FIRE");
        Assertions.assertFalse(helperSource.contains("!BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_FRIENDLY_FIRE"),
                "WolfFriendlyFireHelper must not negate getBoolean directly");

        int gateIdx = helperSource.indexOf("if (!friendlyFireEnabled)");
        int feudIdx = helperSource.indexOf("betterdogs$hasBloodFeud()");
        int coOwnedIdx = helperSource.indexOf("PackAudienceHelper.isCoOwned");
        Assertions.assertTrue(gateIdx > 0 && feudIdx > gateIdx && coOwnedIdx > feudIdx,
                "Friendly-fire gate must run before Blood Feud exemption, which must precede PackAudienceHelper.isCoOwned");

        Assertions.assertTrue(helperSource.contains("isBloodFeudPair("),
                "WolfFriendlyFireHelper must invoke isBloodFeudPair");

        Assertions.assertTrue(rulesSource.contains("level instanceof ServerLevel serverLevel"),
                "BetterDogsGameRules.getBoolean must query ServerLevel.getGameRules() directly before fallback");
        Assertions.assertTrue(rulesSource.contains("catch (Throwable t)"),
                "BetterDogsGameRules.getBoolean must preserve caller fallback cleanly on Throwable");
    }
}
