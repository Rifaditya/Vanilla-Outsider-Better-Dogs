// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 28: Friendly Fire Dampening & Protection Tests")
class FriendlyFireTest {

    @BeforeAll
    static void setupMinecraftBootstrap() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

    private static Path findProjectRoot() {
        Path cursor = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (cursor != null) {
            if (Files.exists(cursor.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfFriendlyFireHelper.java"))) {
                return cursor;
            }
            cursor = cursor.getParent();
        }
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

    @Test
    @DisplayName("Assert standing owner direct melee attack is cancelled under friendly fire protection")
    void testStandingOwnerMeleeProtection() {
        // isTamed=true, isOwner=true, isDirectMelee=true, isSneaking=false, friendlyFireEnabled=true
        boolean protectedFromDamage = WolfFriendlyFireHelper.isFriendlyFireProtected(true, true, true, false, true);
        assertTrue(protectedFromDamage, "Standing owner melee hit should be protected (cancelled)");
    }

    @Test
    @DisplayName("Assert crouching owner melee attack bypasses protection for intentional hits")
    void testCrouchingOwnerMeleeBypass() {
        // isTamed=true, isOwner=true, isDirectMelee=true, isSneaking=true, friendlyFireEnabled=true
        boolean protectedFromDamage = WolfFriendlyFireHelper.isFriendlyFireProtected(true, true, true, true, true);
        assertFalse(protectedFromDamage, "Crouching owner melee hit should bypass protection (allowed)");
    }

    @Test
    @DisplayName("Assert owner projectile attack is allowed to deal damage")
    void testOwnerProjectileAllowance() {
        // isTamed=true, isOwner=true, isDirectMelee=false (projectile), isSneaking=false, friendlyFireEnabled=true
        boolean protectedFromDamage = WolfFriendlyFireHelper.isFriendlyFireProtected(true, true, false, false, true);
        assertFalse(protectedFromDamage, "Owner projectile hit should NOT be blocked by melee protection");
    }

    @Test
    @DisplayName("Assert non-owner or wild wolf attacks are never cancelled by friendly fire")
    void testNonOwnerHostileAllowance() {
        // Untamed wolf
        assertFalse(WolfFriendlyFireHelper.isFriendlyFireProtected(false, false, true, false, true));
        // Tamed wolf hit by non-owner player or monster
        assertFalse(WolfFriendlyFireHelper.isFriendlyFireProtected(true, false, true, false, true));
    }

    @Test
    @DisplayName("Assert friendly fire disabled GameRule allows all hits")
    void testFriendlyFireDisabled() {
        // friendlyFireEnabled=false
        assertFalse(WolfFriendlyFireHelper.isFriendlyFireProtected(true, true, true, false, false));
    }

    @Test
    @DisplayName("Assert strict null safety across WolfFriendlyFireHelper")
    void testNullSafety() {
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(null, null, 5.0f));
        assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(null, null));
    }

    @Test
    @DisplayName("BL-BD-041: Assert bidirectional Blood Feud exemption, symmetry, and bystander protection")
    void testBloodFeudBidirectionalExemption() {
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
        assertTrue(WolfFriendlyFireHelper.isBloodFeudPair(dogA, dogB), "Aggressor -> Victim must be in feud pair");
        assertTrue(WolfFriendlyFireHelper.isBloodFeudPair(dogB, dogA), "Defending Victim -> Aggressor must be in feud pair (bidirectional)");

        // 2. Anti-Cross-Bleed Protection (Anti-Goal 2: Bystander co-owned dogs remain fully protected)
        assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogA, dogC), "Aggressor with bystander must NOT be in feud pair");
        assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogC, dogA), "Bystander with aggressor must NOT be in feud pair");
        assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogB, dogC), "Victim with bystander must NOT be in feud pair");

        // 3. Null & Empty Safety (PAT-20261114-001)
        assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(null, null), "Null wolves must return false");
        assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogA, null), "Null target must return false");
        assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(null, dogB), "Null source must return false");

        // Empty feud target
        dogA.betterdogs$setBloodFeudTarget("");
        assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(dogA, dogB), "Empty feud target must return false");
    }

    @Test
    @DisplayName("BL-BD-063: Assert BD_FRIENDLY_FIRE gates lethal clamp, co-owned ally hits, offline UUID & Social/Blood Feud bypasses")
    void testFriendlyFireSourceContracts() throws IOException {
        String source = Files.readString(
                findProjectRoot().resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfFriendlyFireHelper.java"),
                StandardCharsets.UTF_8);

        int socialBypassIdx = source.indexOf("betterdogs$getSocialTarget() == wolf");
        int feudBypassIdx = source.indexOf("betterdogs$hasBloodFeud()");
        int ruleEvalIdx = source.indexOf("BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_FRIENDLY_FIRE, true)");
        int block1Idx = source.indexOf("1. Lethal Friendly Fire Clamp");
        int block2Idx = source.indexOf("2. Owner Melee & Co-Owned Packmate Friendly Fire Cancellation");
        int allyBlockIdx = source.indexOf("if (isAllyWolf)");

        assertTrue(socialBypassIdx > 0 && feudBypassIdx > socialBypassIdx,
                "Social-channel & Blood Feud bypasses must precede damage cancellation");
        assertTrue(ruleEvalIdx > feudBypassIdx,
                "BD_FRIENDLY_FIRE must be evaluated via BetterDogsGameRules.getBoolean(..., true)");
        assertTrue(block1Idx > ruleEvalIdx,
                "BD_FRIENDLY_FIRE check must gate Block 1 lethal clamp");
        assertTrue(block2Idx > block1Idx && allyBlockIdx > block2Idx,
                "Non-lethal co-owned ally wolf protection must live inside Block 2 after BD_FRIENDLY_FIRE gate");
        assertTrue(source.contains("PlayChoreographyHelper.getOwnerUUID(wolf)"),
                "Owner matching must include PlayChoreographyHelper.getOwnerUUID offline fallback");
        assertTrue(source.contains("isBloodFeudPair("),
                "WolfFriendlyFireHelper must invoke isBloodFeudPair");
    }
}
