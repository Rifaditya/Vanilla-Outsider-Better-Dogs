// Verified against: Wolf.java (26.2+)
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * Helper utility to manage tick-level computations and particle spawning
 * for Wolf entities, keeping WolfMixin size within guidelines.
 */
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class WolfTickHelper {

    public static void tickGuardMode(Wolf wolf, WolfExtensions ext, ServerLevel serverLevel) {
        WolfPersonality personality = ext.betterdogs$getPersonality();
        double px = wolf.getRandomX(0.5);
        double py = wolf.getRandomY() + 0.5;
        double pz = wolf.getRandomZ(0.5);

        if (personality == WolfPersonality.AGGRESSIVE) {
            serverLevel.sendParticles(new DustParticleOptions(0xFF3333, 0.6f), px, py, pz, 1, 0, 0.05, 0, 0.0);
        } else if (personality == WolfPersonality.PACIFIST) {
            serverLevel.sendParticles(new DustParticleOptions(0x00FF88, 0.6f), px, py, pz, 1, 0, 0.05, 0, 0.0);

            // Watchdog Grace Buff (Regeneration and Resistance to owner/allies within 6 blocks of wolf OR guard post)
            if (DynamicGameRuleManager.getBoolean(serverLevel, BetterDogsGameRules.BD_PACIFIST_GUARD_BUFFS)) {
                double buffRangeSqr = 36.0; // 6 blocks
                Player owner = wolf.getOwner() instanceof Player ? (Player) wolf.getOwner() : null;
                if (owner != null) {
                    if (owner.isAlive()) {
                        boolean isNearWolf = wolf.distanceToSqr(owner) <= buffRangeSqr;
                        BlockPos post = ext.betterdogs$getGuardPos();
                        boolean isNearPost = post != null && owner.distanceToSqr(post.getX() + 0.5, post.getY() + 0.5, post.getZ() + 0.5) <= buffRangeSqr;

                        if (isNearWolf || isNearPost) {
                            owner.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, true, true));
                            owner.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 80, 0, true, true));
                        }
                    }

                    // Buff allied wolves within 6 blocks of this wolf
                    List<Wolf> allies = serverLevel.getEntitiesOfClass(Wolf.class, wolf.getBoundingBox().inflate(6.0), w -> w.isTame() && w.getOwner() == owner);
                    for (Wolf ally : allies) {
                        ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, true, true));
                        ally.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 80, 0, true, true));
                    }
                }
            }
        } else {
            serverLevel.sendParticles(new DustParticleOptions(0xFFD700, 0.6f), px, py, pz, 1, 0, 0.05, 0, 0.0);
        }
    }

    public static void tickAdoptableParticles(Wolf wolf, ServerLevel serverLevel) {
        RandomSource random = serverLevel.getRandom();
        for (int i = 0; i < 4; ++i) {
            Vec3 source = wolf.position().add(random.nextDouble() * 0.6 - 0.3, random.nextDouble() * 0.5, random.nextDouble() * 0.6 - 0.3);
            Vec3 destination = wolf.position().add(random.nextDouble() * 0.8 - 0.4, wolf.getEyeHeight() + 0.5 + random.nextDouble() * 0.5, random.nextDouble() * 0.8 - 0.4);
            TrailParticleOption trail = new TrailParticleOption(destination, 0xFF99BB, random.nextInt(20) + 15);
            serverLevel.sendParticles(trail, true, true, source.x, source.y, source.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    public static int tickPassiveHealing(Wolf wolf, WolfExtensions ext, int healTimer) {
        int lastDamageTime = ext.betterdogs$getLastDamageTime();
        if (wolf.tickCount - lastDamageTime > BetterDogsConfig.get().getCombatHealDelayTicks()
                && wolf.getHealth() < wolf.getMaxHealth()) {
            healTimer++;
            if (healTimer >= BetterDogsConfig.get().getPassiveHealIntervalTicks()) {
                wolf.heal((float) BetterDogsConfig.get().getPassiveHealAmount());
                return 0;
            }
            return healTimer;
        } else {
            return 0;
        }
    }

    public static void tickRuntParticles(Wolf wolf, ServerLevel serverLevel) {
        if (DynamicGameRuleManager.getBoolean(serverLevel, BetterDogsGameRules.BD_SHOW_RUNT_PARTICLES)
                || DynamicGameRuleManager.getBoolean(serverLevel, BetterDogsGameRules.BD_DEBUGGING)) {
            double px = wolf.getRandomX(0.4);
            double py = wolf.getRandomY() + 0.2;
            double pz = wolf.getRandomZ(0.4);
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, Items.ROTTEN_FLESH), px, py, pz, 1, 0.01, 0.01, 0.01, 0.01);
        }
    }

    public static void handleAmbientSound(Wolf wolf, net.minecraft.world.entity.animal.wolf.WolfSoundVariant.WolfSoundSet soundSet, CallbackInfoReturnable<net.minecraft.sounds.SoundEvent> cir) {
        if (wolf.isAngry()) {
            cir.setReturnValue(soundSet.growlSound().value());
            return;
        }
        if (wolf.getRandom().nextInt(3) == 0) {
            if (wolf.isTame() && wolf.getHealth() < wolf.getMaxHealth() * 0.5f) {
                cir.setReturnValue(soundSet.whineSound().value());
                return;
            }
            cir.setReturnValue(soundSet.pantSound().value());
            return;
        }
        cir.setReturnValue(soundSet.ambientSound().value());
    }
}
