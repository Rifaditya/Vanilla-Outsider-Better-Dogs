// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2 Multi-Loader Specification
package net.vanillaoutsider.betterdogs.e2e.tier2;

import net.vanillaoutsider.betterdogs.ai.AvoidGhastFireballGoal;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.personality.CaninePersonalityHelper;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier 2 - Boundary 02: Null Entities & Level Interaction Tests")
public class Tier2_02_NullInteractionsTest {

    @Test
    @DisplayName("Assert CanineGeneticsHelper.getModulatedVoicePitch with null wolf returns base pitch")
    public void testNullWolfInVoicePitch() {
        float result = CanineGeneticsHelper.getModulatedVoicePitch(null, 1.25f);
        assertEquals(1.25f, result, 1e-4f, "Null wolf must fallback directly to base pitch");
    }

    @Test
    @DisplayName("Assert AvoidGhastFireballGoal.isThreateningProjectile with null entity safely returns false")
    public void testNullProjectileThreatCheck() {
        assertFalse(AvoidGhastFireballGoal.isThreateningProjectile(null),
                "Null entity must safely return false without NullPointerException");
    }

    @Test
    @DisplayName("Assert CaninePersonalityHelper.getOrRollArchetype with null wolf returns GUARDIAN")
    public void testNullWolfInPersonalityHelper() {
        CanineArchetype archetype = CaninePersonalityHelper.getOrRollArchetype(null);
        assertSame(CanineArchetype.GUARDIAN, archetype, "Null wolf must safely return GUARDIAN fallback");
    }

    @Test
    @DisplayName("Assert CaninePersonalityHelper.rollFromUUID with null UUID returns GUARDIAN")
    public void testNullUuidInRollFromUuid() {
        CanineArchetype archetype = CaninePersonalityHelper.rollFromUUID(null, null);
        assertSame(CanineArchetype.GUARDIAN, archetype, "Null UUID must safely return GUARDIAN fallback");
    }

    @Test
    @DisplayName("Assert GameRule getters with null level return provided fallback without exception")
    public void testNullLevelGameRuleAccessors() {
        boolean boolVal = BetterDogsGameRules.getBoolean(null, BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED, true);
        assertTrue(boolVal, "Null level must return fallback boolean value");

        int intVal = BetterDogsGameRules.getInt(null, BetterDogsGameRules.BD_EVASION_REACTION_TICKS, 25);
        assertEquals(25, intVal, "Null level must return fallback integer value");
    }
}
