// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import net.vanillaoutsider.betterdogs.util.WolfLitterHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin {

    @Inject(method = "spawnChildFromBreeding", at = @At("TAIL"))
    private void betterdogs$spawnExtraBabies(ServerLevel level, Animal partner, CallbackInfo ci) {
        Animal parent1 = (Animal) (Object) this;
        WolfLitterHelper.processBreedingLitter(level, parent1, partner);
    }
}
