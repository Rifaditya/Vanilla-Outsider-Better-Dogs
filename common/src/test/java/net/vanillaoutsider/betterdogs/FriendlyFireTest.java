// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FriendlyFireTest {

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
                "WolfFriendlyFireHelper must not invert BD_FRIENDLY_FIRE polarity");

        int gateIdx = helperSource.indexOf("if (!friendlyFireEnabled)");
        int feudIdx = helperSource.indexOf("betterdogs$hasBloodFeud()");
        int coOwnedIdx = helperSource.indexOf("PackAudienceHelper.isCoOwned");
        Assertions.assertTrue(gateIdx > 0 && feudIdx > gateIdx && coOwnedIdx > feudIdx,
                "Friendly-fire gate must run before Blood Feud exemption, which must precede PackAudienceHelper.isCoOwned");

        Assertions.assertTrue(rulesSource.contains("!level.isClientSide()"),
                "BetterDogsGameRules.getBoolean must query server GameRules directly instead of swallowing exceptions to false");
        Assertions.assertTrue(rulesSource.contains("return fallback;"),
                "BetterDogsGameRules.getBoolean must preserve caller fallback when server GameRules are unavailable");
    }
}
