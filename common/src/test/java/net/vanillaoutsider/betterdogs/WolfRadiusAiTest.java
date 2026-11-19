// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.ai.PersonalityFollowOwnerGoal;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WolfRadiusAiTest {

    @Test
    public void testPersonalityFollowStartDistances() {
        assertEquals(25.0f, getExpectedFollowStart(WolfPersonality.AGGRESSIVE), 0.001f);
        assertEquals(5.0f, getExpectedFollowStart(WolfPersonality.PACIFIST), 0.001f);
        assertEquals(10.0f, getExpectedFollowStart(WolfPersonality.NORMAL), 0.001f);
    }

    @Test
    public void testPersonalityTeleportThresholds() {
        assertEquals(41.0f, getExpectedTeleportThreshold(WolfPersonality.AGGRESSIVE), 0.001f);
        assertEquals(32.0f, getExpectedTeleportThreshold(WolfPersonality.PACIFIST), 0.001f);
        assertEquals(32.0f, getExpectedTeleportThreshold(WolfPersonality.NORMAL), 0.001f);
    }

    @Test
    public void testPersonalityTamedRoamRadii() {
        assertEquals(14.0, getExpectedRoamRadius(WolfPersonality.AGGRESSIVE), 0.001);
        assertEquals(4.0, getExpectedRoamRadius(WolfPersonality.PACIFIST), 0.001);
        assertEquals(8.0, getExpectedRoamRadius(WolfPersonality.NORMAL), 0.001);
    }

    private float getExpectedFollowStart(WolfPersonality personality) {
        BetterDogsConfig config = new BetterDogsConfig();
        return switch (personality) {
            case AGGRESSIVE -> config.getAggressiveFollowStart();
            case PACIFIST -> config.pacifistFollowStart;
            case NORMAL -> config.normalFollowStart;
        };
    }

    private float getExpectedTeleportThreshold(WolfPersonality personality) {
        return PersonalityFollowOwnerGoal.calculateTeleportThreshold(getExpectedFollowStart(personality));
    }

    private double getExpectedRoamRadius(WolfPersonality personality) {
        return switch (personality) {
            case AGGRESSIVE -> 14.0;
            case PACIFIST -> 4.0;
            case NORMAL -> 8.0;
        };
    }
}
