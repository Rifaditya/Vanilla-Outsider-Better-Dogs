// Verified against: Wolf.java (26.1.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Mixin for Wolf entity to implement Guard Mode getters and setters.
 */
@Mixin(Wolf.class)
public abstract class WolfGuardMixin implements WolfExtensions {

    @Unique
    private boolean betterdogs$sittingManually = false;

    @Override
    public boolean betterdogs$isSittingManually() {
        return this.betterdogs$sittingManually;
    }

    @Override
    public void betterdogs$setSittingManually(boolean sitting) {
        this.betterdogs$sittingManually = sitting;
    }

    @Override
    public boolean betterdogs$isGuardMode() {
        return WolfPersistentData.isPersistedGuardMode((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setGuardMode(boolean guardMode) {
        WolfPersistentData.setPersistedGuardMode((Wolf) (Object) this, guardMode);
    }

    @Override
    public @Nullable BlockPos betterdogs$getGuardPos() {
        return WolfPersistentData.getPersistedGuardPos((Wolf) (Object) this).orElse(null);
    }

    @Override
    public void betterdogs$setGuardPos(@Nullable BlockPos pos) {
        WolfPersistentData.setPersistedGuardPos((Wolf) (Object) this, pos);
    }
}
