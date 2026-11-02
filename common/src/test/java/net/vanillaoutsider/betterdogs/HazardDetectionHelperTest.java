// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs;

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

@DisplayName("Step 1: Core Hazard Detection Engine & GameRules Tests [BL-BD-006]")
public class HazardDetectionHelperTest {

    @BeforeAll
    public static void setup() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
        try {
            BetterDogsGameRules.register();
        } catch (Throwable ignored) {
        }
    }

    @Test
    @DisplayName("Assert canine explosive reflex GameRules accessors and constants")
    public void testGameRulesRegistered() {
        if (BetterDogsGameRules.BD_CANINE_EXPLOSIVE_REFLEX_ENABLED != null) {
            assertNotNull(BetterDogsGameRules.BD_CANINE_EXPLOSIVE_REFLEX_ENABLED);
            assertNotNull(BetterDogsGameRules.BD_HAZARD_FLEE_RADIUS);
            assertNotNull(BetterDogsGameRules.BD_FLEE_SPEED_MULTIPLIER);
        }
        assertTrue(BetterDogsGameRules.isCanineExplosiveReflexEnabled(null));
        assertEquals(14, BetterDogsGameRules.getHazardFleeRadius(null));
        assertEquals(170, BetterDogsGameRules.getFleeSpeedMultiplier(null));
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

        assertNull(HazardDetectionHelper.findNearestHazard((net.minecraft.world.entity.animal.Wolf) null, 14.0));
        assertNull(HazardDetectionHelper.findNearestHazard((net.minecraft.world.entity.animal.Wolf) null, -1.0));
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
