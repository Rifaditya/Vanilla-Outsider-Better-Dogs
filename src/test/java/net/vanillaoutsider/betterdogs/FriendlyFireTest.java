// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FriendlyFireTest {

    @Test
    @DisplayName("Verify Friendly Fire Null Safety")
    public void testNullSafety() {
        Assertions.assertFalse(WolfFriendlyFireHelper.shouldCancelDamage(null, null));
    }
}
