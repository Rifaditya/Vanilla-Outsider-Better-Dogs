// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs;

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.vanillaoutsider.betterdogs.ai.WildWolfWanderGoal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ticket BL-BD-040: Untamed Wild Wolves Wandering AI Tests")
class WildWolfWanderGoalTest {

    private static Path findSourceRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        if (Files.exists(dir.resolve("src/main/java"))) {
            return dir.resolve("src/main/java");
        }
        if (Files.exists(dir.resolve("common/src/main/java"))) {
            return dir.resolve("common/src/main/java");
        }
        Path parent = dir.getParent();
        if (parent != null && Files.exists(parent.resolve("common/src/main/java"))) {
            return parent.resolve("common/src/main/java");
        }
        return dir.resolve("src/main/java");
    }

    @Test
    @DisplayName("Assert WildWolfWanderGoal is direct subclass of WaterAvoidingRandomStrollGoal")
    void testGoalInheritance() {
        assertTrue(WaterAvoidingRandomStrollGoal.class.isAssignableFrom(WildWolfWanderGoal.class),
                "WildWolfWanderGoal must extend WaterAvoidingRandomStrollGoal to retain vanilla wandering algorithms");
    }

    @Test
    @DisplayName("Assert strict null safety on canUse and canContinueToUse")
    void testNullSafety() {
        assertFalse(WildWolfWanderGoal.isEligible(null), "Null wolf entity must return false for isEligible");
        WildWolfWanderGoal goal = new WildWolfWanderGoal(null, 1.0D);
        assertDoesNotThrow(() -> assertFalse(goal.canUse(), "Null wolf entity must safely return false on canUse"));
        assertDoesNotThrow(() -> assertFalse(goal.canContinueToUse(), "Null wolf entity must safely return false on canContinueToUse"));
    }

    @Test
    @DisplayName("Assert Zero Stroll Duplication Invariant: Wild vs Tamed mutual exclusivity and dynamic transition")
    void testZeroStrollDuplicationInvariant() {
        // Anti-Goal: Wild wolf wander and tamed wander near owner must have disjoint activation domains.
        // S.isTame == false -> wild wander is eligible
        assertTrue(WildWolfWanderGoal.isEligible(false), "Wild untamed wolf must be eligible for wild wandering");

        // S.isTame == true -> wild wander is strictly ineligible
        assertFalse(WildWolfWanderGoal.isEligible(true), "Tamed wolf must never be eligible for wild wandering");

        // Dynamic transition: A wolf being tamed immediately revokes wandering eligibility
        boolean wolfIsTame = false;
        assertTrue(WildWolfWanderGoal.isEligible(wolfIsTame), "Wolf must wander prior to taming");

        // Player feeds bone -> wolf transitions to tamed
        wolfIsTame = true;
        assertFalse(WildWolfWanderGoal.isEligible(wolfIsTame), "Wolf must immediately discontinue wild wandering upon taming");
    }

    @Test
    @DisplayName("Assert Priority Contract & Mixin Registration Integrity")
    void testPriorityContractAndMixinRegistration() throws IOException {
        Path mixinPath = findSourceRoot().resolve("net/vanillaoutsider/betterdogs/mixin/WolfMixin.java");
        assertTrue(Files.exists(mixinPath), "WolfMixin.java must exist on disk");

        List<String> lines = Files.readAllLines(mixinPath);
        boolean removesVanillaStroll = false;
        boolean registersWildWander = false;
        boolean registersTamedWander = false;
        boolean registersHunt = false;
        boolean registersFollowLeader = false;

        int wildWanderPriority = -1;
        int tamedWanderPriority = -1;
        int huntPriority = -1;
        int followLeaderPriority = -1;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.contains("WaterAvoidingRandomStrollGoal.class")) {
                removesVanillaStroll = true;
            }
            if (trimmed.contains("addGoal") && trimmed.contains("WildWolfWanderGoal")) {
                registersWildWander = true;
                wildWanderPriority = extractPriority(trimmed);
            }
            if (trimmed.contains("addGoal") && trimmed.contains("TamedWanderNearOwnerGoal")) {
                registersTamedWander = true;
                tamedWanderPriority = extractPriority(trimmed);
            }
            if (trimmed.contains("addGoal") && trimmed.contains("WildWolfHuntGoal")) {
                registersHunt = true;
                huntPriority = extractPriority(trimmed);
            }
            if (trimmed.contains("addGoal") && trimmed.contains("WildWolfFollowLeaderGoal")) {
                registersFollowLeader = true;
                followLeaderPriority = extractPriority(trimmed);
            }
        }

        assertTrue(removesVanillaStroll, "WolfMixin must selectively strip vanilla WaterAvoidingRandomStrollGoal.class");
        assertTrue(registersWildWander, "WolfMixin must register WildWolfWanderGoal");
        assertTrue(registersTamedWander, "WolfMixin must register TamedWanderNearOwnerGoal");
        assertTrue(registersHunt, "WolfMixin must register WildWolfHuntGoal");
        assertTrue(registersFollowLeader, "WolfMixin must register WildWolfFollowLeaderGoal");

        assertEquals(8, wildWanderPriority, "WildWolfWanderGoal must be registered at priority 8");
        assertEquals(8, tamedWanderPriority, "TamedWanderNearOwnerGoal must be registered at priority 8");
        assertEquals(4, huntPriority, "WildWolfHuntGoal must be registered at priority 4");
        assertEquals(5, followLeaderPriority, "WildWolfFollowLeaderGoal must be registered at priority 5");

        // Verified priority contract: Hunting (4) and Pack Following (5) precede Wandering (8)
        assertTrue(huntPriority < wildWanderPriority, "Hunting must take precedence over ambient wandering");
        assertTrue(followLeaderPriority < wildWanderPriority, "Pack following must take precedence over ambient wandering");
    }

    private static int extractPriority(String line) {
        int start = line.indexOf("addGoal(");
        if (start != -1) {
            int comma = line.indexOf(",", start);
            if (comma != -1) {
                String prioStr = line.substring(start + 8, comma).trim();
                return Integer.parseInt(prioStr);
            }
        }
        return -1;
    }

    @Test
    @DisplayName("Pillar 5 (Side-Safety): Assert zero client classes in WildWolfWanderGoal")
    void testPillar5SideSafety() throws IOException {
        Path goalPath = findSourceRoot().resolve("net/vanillaoutsider/betterdogs/ai/WildWolfWanderGoal.java");
        assertTrue(Files.exists(goalPath), "WildWolfWanderGoal.java must exist on disk");

        List<String> lines = Files.readAllLines(goalPath);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            assertFalse(line.startsWith("import net.minecraft.client."),
                    "WildWolfWanderGoal must not import net.minecraft.client.* (line " + (i + 1) + ")");
            assertFalse(line.startsWith("import com.mojang.blaze3d."),
                    "WildWolfWanderGoal must not import com.mojang.blaze3d.* (line " + (i + 1) + ")");
            assertFalse(line.contains("net.fabricmc.api.EnvType.CLIENT"),
                    "WildWolfWanderGoal must not reference client environment (line " + (i + 1) + ")");
        }
    }

    @Test
    @DisplayName("Assert single-line GPLv3 license header and single-purpose LOC ceiling")
    void testLicenseHeaderAndLocCeiling() throws IOException {
        Path goalPath = findSourceRoot().resolve("net/vanillaoutsider/betterdogs/ai/WildWolfWanderGoal.java");
        List<String> lines = Files.readAllLines(goalPath);
        assertFalse(lines.isEmpty(), "WildWolfWanderGoal.java must not be empty");

        assertEquals("// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3", lines.get(0).trim(),
                "Must feature exact single-line GPLv3 license header");

        assertTrue(lines.size() <= 300,
                "WildWolfWanderGoal must adhere to the ~300 LOC ceiling law (currently: " + lines.size() + " LOC)");
    }
}
