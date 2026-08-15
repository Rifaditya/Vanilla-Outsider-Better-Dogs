// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WolfRadiusAiTest {

    @Test
    public void testPersonalityFollowStartDistances() {
        assertEquals(50.0f, getExpectedFollowStart(WolfPersonality.AGGRESSIVE), 0.001f);
        assertEquals(5.0f, getExpectedFollowStart(WolfPersonality.PACIFIST), 0.001f);
        assertEquals(10.0f, getExpectedFollowStart(WolfPersonality.NORMAL), 0.001f);
    }

    @Test
    public void testPersonalityTeleportThresholds() {
        assertEquals(32.0f, getExpectedTeleportThreshold(WolfPersonality.AGGRESSIVE), 0.001f);
        assertEquals(10.0f, getExpectedTeleportThreshold(WolfPersonality.PACIFIST), 0.001f);
        assertEquals(20.0f, getExpectedTeleportThreshold(WolfPersonality.NORMAL), 0.001f);
    }

    @Test
    public void testPersonalityTamedRoamRadii() {
        assertEquals(14.0, getExpectedRoamRadius(WolfPersonality.AGGRESSIVE), 0.001);
        assertEquals(4.0, getExpectedRoamRadius(WolfPersonality.PACIFIST), 0.001);
        assertEquals(8.0, getExpectedRoamRadius(WolfPersonality.NORMAL), 0.001);
    }

    private float getExpectedFollowStart(WolfPersonality personality) {
        return switch (personality) {
            case AGGRESSIVE -> 50.0f;
            case PACIFIST -> 5.0f;
            case NORMAL -> 10.0f;
        };
    }

    private float getExpectedTeleportThreshold(WolfPersonality personality) {
        float startDist = getExpectedFollowStart(personality);
        if (startDist > 16.0f) {
            return 32.0f;
        }
        return startDist * 2.0f;
    }

    private double getExpectedRoamRadius(WolfPersonality personality) {
        return switch (personality) {
            case AGGRESSIVE -> 14.0;
            case PACIFIST -> 4.0;
            case NORMAL -> 8.0;
        };
    }
}
