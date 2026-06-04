// Verified against: AnimalMixin.java (26.1.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.gamerules.GameRules;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfDebugLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin {

    @Inject(method = "spawnChildFromBreeding", at = @At("TAIL"))
    private void betterdogs$spawnExtraBabies(ServerLevel level, Animal partner, CallbackInfo ci) {
        Animal parent1 = (Animal) (Object) this;
        
        // Only target tamed wolves as per requirement
        if (parent1 instanceof Wolf wolf && partner instanceof Wolf partnerWolf && wolf.isTame()) {
            int maxSize = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_WOLF_LITTER_MAX_SIZE);
            int extraChance = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_WOLF_LITTER_EXTRA_CHANCE);
            int litterSize = 1;

            // Already spawned one baby in the original method.
            // Loop for additional puppies up to max size.
            for (int i = 1; i < maxSize; i++) {
                if (level.getRandom().nextInt(100) < extraChance) {
                    AgeableMob extraOffspring = parent1.getBreedOffspring(level, partner);
                    if (extraOffspring != null) {
                        extraOffspring.setBaby(true);
                        // Position slightly offset or at parent
                        extraOffspring.snapTo(parent1.getX(), parent1.getY(), parent1.getZ(), 0.0f, 0.0f);
                        
                        level.addFreshEntityWithPassengers(extraOffspring);
                        litterSize++;
                        
                        // Visual feedback: Hearts
                        level.broadcastEntityEvent(parent1, (byte)18);
                        
                        // Extra XP if enabled (Standard behavior per breeding event, here per puppy)
                        if (level.getGameRules().get(GameRules.MOB_DROPS)) {
                            level.addFreshEntity(new ExperienceOrb(level, parent1.getX(), parent1.getY(), parent1.getZ(), level.getRandom().nextInt(7) + 1));
                        }
                        
                        WolfDebugLogger.log(wolf, "Breeding", "Extra puppy born! Litter size incremented for tamed wolf pair.");
                    }
                } else {
                    // Probability chain: If one fails, stop (prevents large litters being too common)
                    break;
                }
            }

            if (litterSize >= 2) {
                net.minecraft.server.level.ServerPlayer player = wolf.getLoveCause();
                if (player == null) {
                    player = partnerWolf.getLoveCause();
                }
                if (player != null) {
                    net.vanillaoutsider.betterdogs.BetterDogs.WOLF_LITTER.trigger(player, litterSize);
                }
            }
        }
    }
}
