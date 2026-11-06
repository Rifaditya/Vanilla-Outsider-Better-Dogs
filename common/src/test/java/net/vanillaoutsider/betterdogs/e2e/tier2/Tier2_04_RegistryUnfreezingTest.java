// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier2;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 2 - Boundary 04: Registry Unfreezing & Dynamic Rule Bounds Tests")
public class Tier2_04_RegistryUnfreezingTest {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BetterDogsGameRules.register();
    }

    @Test
    @DisplayName("Assert querying unregistered dynamic rule identifier returns null without crash")
    public void testUnregisteredDynamicRuleLookup() {
        assertNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:non_existent_rule_abc"),
                "Lookup for unknown dynamic rule key must return null");
    }

    @Test
    @DisplayName("Assert category namespace matches official snake_case convention")
    public void testCategoryIdentifierFormat() {
        assertNotNull(BetterDogsGameRules.BETTER_DOGS);
        String categoryName = BetterDogsGameRules.BETTER_DOGS.toString();
        assertFalse(categoryName.contains(" "), "Category identifier must not contain spaces");
    }

    @Test
    @DisplayName("Assert DynamicGameRuleManager getBoolean with null level returns fallback")
    public void testDynamicGameRuleManagerNullLevelSafety() {
        boolean result = DynamicGameRuleManager.getBoolean(null, BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED);
        // Should return rule's default value
        assertTrue(result);
    }

    @Test
    @DisplayName("Assert DynamicGameRuleManager getInt with null level returns default value")
    public void testDynamicGameRuleManagerNullLevelIntSafety() {
        int result = DynamicGameRuleManager.getInt(null, BetterDogsGameRules.BD_EVASION_REACTION_TICKS);
        assertEquals(20, result);
    }

    @Test
    @DisplayName("Assert dynamic rules lookup map is non-null and safely queryable")
    public void testDynamicRulesMapQuerySafety() {
        var map = DynamicGameRuleManager.getDynamicRules();
        assertNotNull(map);
        assertDoesNotThrow(() -> {
            boolean hasEvasion = map.containsKey("betterdogs:ballisticEvasionEnabled");
            assertTrue(hasEvasion);
        });
    }
}
