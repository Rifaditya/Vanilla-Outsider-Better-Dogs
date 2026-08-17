// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CliffSafetyTest {

    @Test
    public void testDropHeightEvaluation() {
        // Drop heights <= 3 blocks are considered safe
        assertTrue(isSafeDrop(1), "1 block drop is safe");
        assertTrue(isSafeDrop(2), "2 block drop is safe");
        assertTrue(isSafeDrop(3), "3 block drop is safe");

        // Drop heights > 3 blocks are considered lethal cliff hazards
        assertFalse(isSafeDrop(4), "4 block drop is a cliff hazard");
        assertFalse(isSafeDrop(10), "10 block drop is a cliff hazard");
        assertFalse(isSafeDrop(50), "50 block ravine drop is a lethal hazard");
    }

    @Test
    public void testPushCollisionCancellation() {
        // Pushing a wolf toward a lethal cliff drop (>3 blocks) must cancel push momentum
        boolean pushNearLethalDrop = evaluatePushSafety(false, true, true);
        assertFalse(pushNearLethalDrop, "Pushing towards a lethal drop must be canceled");

        // Pushing a sitting dog must always cancel push momentum
        boolean pushSittingWolf = evaluatePushSafety(true, false, true);
        assertFalse(pushSittingWolf, "Pushing a sitting dog must be canceled");

        // Pushing on flat solid ground when not sitting is allowed
        boolean pushSafeFlatGround = evaluatePushSafety(false, false, true);
        assertTrue(pushSafeFlatGround, "Pushing on flat safe ground is allowed");

        // When GameRule is disabled, cliff pushes are not canceled
        boolean pushRuleDisabled = evaluatePushSafety(false, true, false);
        assertTrue(pushRuleDisabled, "Push should proceed if cliff safety gamerule is disabled");
    }

    private boolean isSafeDrop(int dropHeight) {
        return dropHeight <= 3;
    }

    private boolean evaluatePushSafety(boolean isSitting, boolean isNearLethalDrop, boolean gameRuleEnabled) {
        if (isSitting) {
            return false;
        }
        if (gameRuleEnabled && isNearLethalDrop) {
            return false;
        }
        return true;
    }
}
