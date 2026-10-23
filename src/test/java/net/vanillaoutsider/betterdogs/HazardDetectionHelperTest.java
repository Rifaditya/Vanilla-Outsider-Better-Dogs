// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.HazardDetectionHelper;
import net.vanillaoutsider.betterdogs.ai.HazardDetectionHelper.DetonationHazard;
import net.vanillaoutsider.betterdogs.ai.HazardDetectionHelper.HazardType;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 1: Core Hazard Detection Engine & Dynamic GameRules Tests [BL-BD-006]")
public class HazardDetectionHelperTest {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BetterDogsGameRules.register();
    }

    @Test
    @DisplayName("Assert canine explosive reflex GameRules are registered and non-null")
    public void testGameRulesRegistered() {
        assertNotNull(BetterDogsGameRules.BD_CANINE_EXPLOSIVE_REFLEX_ENABLED,
                "BD_CANINE_EXPLOSIVE_REFLEX_ENABLED must be registered and non-null");
        assertNotNull(BetterDogsGameRules.BD_HAZARD_FLEE_RADIUS,
                "BD_HAZARD_FLEE_RADIUS must be registered and non-null");
        assertNotNull(BetterDogsGameRules.BD_FLEE_SPEED_MULTIPLIER,
                "BD_FLEE_SPEED_MULTIPLIER must be registered and non-null");
    }

    @Test
    @DisplayName("Assert GameRule default values are correct")
    public void testGameRuleDefaults() {
        assertTrue(BetterDogsGameRules.BD_CANINE_EXPLOSIVE_REFLEX_ENABLED.defaultValue(),
                "Default canineExplosiveReflexEnabled must be true");
        assertEquals(14, BetterDogsGameRules.BD_HAZARD_FLEE_RADIUS.defaultValue(),
                "Default hazardFleeRadius must be 14 blocks");
        assertEquals(170, BetterDogsGameRules.BD_FLEE_SPEED_MULTIPLIER.defaultValue(),
                "Default fleeSpeedMultiplier must be 170 (1.7x sprint speed)");
    }

    @Test
    @DisplayName("Assert static null-safe accessors return expected defaults with null level")
    public void testStaticGettersWithNullLevel() {
        assertTrue(BetterDogsGameRules.isCanineExplosiveReflexEnabled(null),
                "isCanineExplosiveReflexEnabled(null) should return true");
        assertEquals(14, BetterDogsGameRules.getHazardFleeRadius(null),
                "getHazardFleeRadius(null) should return 14");
        assertEquals(170, BetterDogsGameRules.getFleeSpeedMultiplier(null),
                "getFleeSpeedMultiplier(null) should return 170");
    }

    @Test
    @DisplayName("Assert DynamicGameRuleManager resolves both bd_ and camelCase alias keys")
    public void testDynamicGameRuleAliases() {
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_canine_explosive_reflex_enabled"),
                "DynamicGameRuleManager must find bd_canine_explosive_reflex_enabled");
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_hazard_flee_radius"),
                "DynamicGameRuleManager must find bd_hazard_flee_radius");
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:bd_flee_speed_multiplier"),
                "DynamicGameRuleManager must find bd_flee_speed_multiplier");

        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:canineExplosiveReflexEnabled"),
                "DynamicGameRuleManager must resolve alias canineExplosiveReflexEnabled");
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:hazardFleeRadius"),
                "DynamicGameRuleManager must resolve alias hazardFleeRadius");
        assertNotNull(DynamicGameRuleManager.getDynamicRules().get("betterdogs:fleeSpeedMultiplier"),
                "DynamicGameRuleManager must resolve alias fleeSpeedMultiplier");
    }

    @Test
    @DisplayName("Assert HazardType enum constants and valueOf parsing")
    public void testHazardTypeEnum() {
        assertEquals(HazardType.PRIMED_TNT, HazardType.valueOf("PRIMED_TNT"));
        assertEquals(HazardType.END_CRYSTAL, HazardType.valueOf("END_CRYSTAL"));
        assertEquals(HazardType.CREEPER, HazardType.valueOf("CREEPER"));
        assertEquals(3, HazardType.values().length, "HazardType must contain exactly 3 hazard types");
    }

    @Test
    @DisplayName("Assert DetonationHazard record stores components accurately")
    public void testDetonationHazardRecord() {
        Vec3 pos = new Vec3(10.5, 64.0, -12.5);
        DetonationHazard hazard = new DetonationHazard(null, pos, HazardType.PRIMED_TNT, 49.0);

        assertNull(hazard.entity());
        assertEquals(pos, hazard.position());
        assertEquals(HazardType.PRIMED_TNT, hazard.type());
        assertEquals(49.0, hazard.distanceSq(), 1e-6);
    }

    @Test
    @DisplayName("Assert HazardDetectionHelper null-safety on null inputs and edge cases")
    public void testHazardNullSafety() {
        assertNull(HazardDetectionHelper.classifyHazard(null));
        assertFalse(HazardDetectionHelper.isHazard(null));

        assertNull(HazardDetectionHelper.findNearestHazard((net.minecraft.world.entity.animal.wolf.Wolf) null, 14.0));
        assertNull(HazardDetectionHelper.findNearestHazard((net.minecraft.world.entity.animal.wolf.Wolf) null, -1.0));
        assertNull(HazardDetectionHelper.findNearestHazard((net.minecraft.world.level.Level) null, new Vec3(0, 0, 0), 14.0));
        assertNull(HazardDetectionHelper.findNearestHazard((net.minecraft.world.level.Level) null, null, 14.0));
        assertNull(HazardDetectionHelper.findNearestHazard(Collections.emptyList(), new Vec3(0, 0, 0), 14.0));
        assertNull(HazardDetectionHelper.findNearestHazard((List<net.minecraft.world.entity.Entity>) null, new Vec3(0, 0, 0), 14.0));
        assertNull(HazardDetectionHelper.findNearestHazard(List.of(), null, 14.0));
        assertNull(HazardDetectionHelper.findNearestHazard(List.of(), new Vec3(0, 0, 0), -5.0));
        assertNull(HazardDetectionHelper.findNearestHazard(List.of(), new Vec3(0, 0, 0), 0.0));
    }

    @Test
    @DisplayName("Assert 14-block default flee radius and distanceSq thresholding")
    public void testRadiusThresholdMath() {
        double radius = 14.0;
        double radiusSq = radius * radius;
        assertEquals(196.0, radiusSq, 1e-6);

        Vec3 origin = new Vec3(0.0, 0.0, 0.0);
        Vec3 inRangePos = new Vec3(8.0, 0.0, 6.0); // dist = 10.0, distSq = 100.0
        Vec3 edgePos = new Vec3(14.0, 0.0, 0.0);   // dist = 14.0, distSq = 196.0
        Vec3 outOfRangePos = new Vec3(14.1, 0.0, 0.0); // dist = 14.1, distSq = 198.81

        assertTrue(origin.distanceToSqr(inRangePos) <= radiusSq, "In-range position must be <= radiusSq");
        assertTrue(origin.distanceToSqr(edgePos) <= radiusSq, "Edge position must be <= radiusSq");
        assertFalse(origin.distanceToSqr(outOfRangePos) <= radiusSq, "Out-of-range position must exceed radiusSq");
    }
}
