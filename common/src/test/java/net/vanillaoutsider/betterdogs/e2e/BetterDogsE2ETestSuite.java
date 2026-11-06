// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Master E2E Test Suite Runner for Better Dogs 26.1.2 Multi-Loader Port.
 */
@DisplayName("Better Dogs 26.1.2 Multi-Loader E2E Master Test Suite")
public class BetterDogsE2ETestSuite {

    @BeforeAll
    public static void initMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BetterDogsGameRules.register();
    }

    @Test
    @DisplayName("Assert Headless Minecraft Bootstrap and GameRules Initialized")
    public void testBootstrapInitialized() {
        assertNotNull(BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED, "GameRule must be non-null after bootstrap");
        assertTrue(BetterDogsGameRules.BD_EVASION_REACTION_TICKS != null, "Reaction ticks rule must be registered");
    }
}
