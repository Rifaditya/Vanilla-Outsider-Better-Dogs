// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 28: Friendly Fire Dampening & Protection Tests")
class FriendlyFireTest {

    @Test
    @DisplayName("Assert standing owner direct melee attack is cancelled under friendly fire protection")
    void testStandingOwnerMeleeProtection() {
        // isTamed=true, isOwner=true, isDirectMelee=true, isSneaking=false, friendlyFireEnabled=true
        boolean protectedFromDamage = WolfFriendlyFireHelper.isFriendlyFireProtected(true, true, true, false, true);
        assertTrue(protectedFromDamage, "Standing owner melee hit should be protected (cancelled)");
    }

    @Test
    @DisplayName("Assert crouching owner melee attack bypasses protection for intentional hits")
    void testCrouchingOwnerMeleeBypass() {
        // isTamed=true, isOwner=true, isDirectMelee=true, isSneaking=true, friendlyFireEnabled=true
        boolean protectedFromDamage = WolfFriendlyFireHelper.isFriendlyFireProtected(true, true, true, true, true);
        assertFalse(protectedFromDamage, "Crouching owner melee hit should bypass protection (allowed)");
    }

    @Test
    @DisplayName("Assert owner projectile attack is allowed to deal damage")
    void testOwnerProjectileAllowance() {
        // isTamed=true, isOwner=true, isDirectMelee=false (projectile), isSneaking=false, friendlyFireEnabled=true
        boolean protectedFromDamage = WolfFriendlyFireHelper.isFriendlyFireProtected(true, true, false, false, true);
        assertFalse(protectedFromDamage, "Owner projectile hit should NOT be blocked by melee protection");
    }

    @Test
    @DisplayName("Assert non-owner or wild wolf attacks are never cancelled by friendly fire")
    void testNonOwnerHostileAllowance() {
        // Untamed wolf
        assertFalse(WolfFriendlyFireHelper.isFriendlyFireProtected(false, false, true, false, true));
        // Tamed wolf hit by non-owner player or monster
        assertFalse(WolfFriendlyFireHelper.isFriendlyFireProtected(true, false, true, false, true));
    }

    @Test
    @DisplayName("Assert friendly fire disabled GameRule allows all hits")
    void testFriendlyFireDisabled() {
        // friendlyFireEnabled=false
        assertFalse(WolfFriendlyFireHelper.isFriendlyFireProtected(true, true, true, false, false));
    }

    @Test
    @DisplayName("Assert strict null safety across WolfFriendlyFireHelper")
    void testNullSafety() {
        assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(null, null, 5.0f));
    }
}
