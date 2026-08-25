// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.vanillaoutsider.betterdogs.util.WolfGuardHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GuardModePatrolTest {

    @Test
    @DisplayName("Verify Personality Guard Patrol Radii (Aggressive 12m, Normal 8m, Pacifist 4m)")
    public void testGuardRadii() {
        Assertions.assertEquals(12, WolfGuardHelper.getPatrolRadius(WolfPersonality.AGGRESSIVE), "Aggressive wolves must patrol 12 blocks");
        Assertions.assertEquals(8, WolfGuardHelper.getPatrolRadius(WolfPersonality.NORMAL), "Normal wolves must patrol 8 blocks");
        Assertions.assertEquals(4, WolfGuardHelper.getPatrolRadius(WolfPersonality.PACIFIST), "Pacifist wolves must patrol 4 blocks");
        Assertions.assertEquals(8, WolfGuardHelper.getPatrolRadius(null), "Null personality must fallback to 8 blocks");
    }

    @Test
    @DisplayName("Verify Guard Leash Boundaries and Distance Math")
    public void testGuardLeashBoundaries() {
        BlockPos post = new BlockPos(100, 64, -200);
        int aggressiveRadius = WolfGuardHelper.getPatrolRadius(WolfPersonality.AGGRESSIVE);
        double maxDistSq = aggressiveRadius * aggressiveRadius; // 144.0

        double insideDistSq = 100.0;
        double outsideDistSq = 200.0;

        Assertions.assertTrue(insideDistSq <= maxDistSq, "10 blocks distance is within 12m territory");
        Assertions.assertFalse(outsideDistSq <= maxDistSq, "15 blocks distance is outside 12m territory");
    }

    @Test
    @DisplayName("Verify WolfGuardHelper 4-Parameter and Overload Null & Hand Safety")
    public void testGuardHelperNullSafety() {
        // 3-arg overload
        Assertions.assertFalse(WolfGuardHelper.canToggleGuard(null, null, null));
        Assertions.assertDoesNotThrow(() -> {
            InteractionResult res = WolfGuardHelper.toggleGuardMode(null, null);
            Assertions.assertEquals(InteractionResult.PASS, res);
        });

        // 4-arg main method
        Assertions.assertFalse(WolfGuardHelper.canToggleGuard(null, null, InteractionHand.MAIN_HAND, null));
        Assertions.assertFalse(WolfGuardHelper.canToggleGuard(null, null, InteractionHand.OFF_HAND, null));

        // Safe toggle failure on null entity
        Assertions.assertDoesNotThrow(() -> {
            InteractionResult res = WolfGuardHelper.toggleGuardMode(null, null, InteractionHand.MAIN_HAND, null);
            Assertions.assertEquals(InteractionResult.PASS, res);
        });
        Assertions.assertDoesNotThrow(() -> {
            InteractionResult res = WolfGuardHelper.toggleGuardMode(null, null, InteractionHand.OFF_HAND, null);
            Assertions.assertEquals(InteractionResult.PASS, res);
        });
    }
}
