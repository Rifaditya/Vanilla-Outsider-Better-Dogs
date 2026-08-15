// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose helper for curing inbred runt dogs with Golden Apples.
 */
public class WolfCureHelper {

    public static boolean canCure(Wolf wolf, ItemStack stack) {
        if (wolf == null || stack == null || stack.isEmpty()) {
            return false;
        }

        if (!(wolf instanceof WolfExtensions ext) || !ext.betterdogs$isInbred()) {
            return false;
        }

        return stack.is(Items.GOLDEN_APPLE) || stack.is(Items.ENCHANTED_GOLDEN_APPLE);
    }

    public static InteractionResult tryCureInbredWolf(Wolf wolf, Player player, InteractionHand hand) {
        if (wolf == null || player == null) {
            return InteractionResult.PASS;
        }

        ItemStack held = player.getItemInHand(hand);
        if (!canCure(wolf, held)) {
            return InteractionResult.PASS;
        }

        Level level = wolf.level();
        if (level == null) {
            return InteractionResult.PASS;
        }

        boolean isEnchanted = held.is(Items.ENCHANTED_GOLDEN_APPLE);

        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }

        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setInbred(false);

            // Restore scale to healthy stature (minimum 1.0x)
            float currentScale = ext.betterdogs$getSocialScale();
            if (currentScale < 1.0f) {
                float naturalScale = Math.max(1.0f, WolfScaleGeneticsHelper.generateWildWolfScale(level, wolf.getRandom()));
                ext.betterdogs$setSocialScale(naturalScale);
            }

            // Restore combat attributes to healthy personality stats
            WolfPersonalityStatHelper.applyPersonalityStats(wolf, ext.betterdogs$getPersonality());

            if (isEnchanted) {
                wolf.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1));
                wolf.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0));
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 15, 0.35, 0.35, 0.35, 0.05);
            level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8f, 1.2f);
            level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.WOLF_SHAKE, SoundSource.NEUTRAL, 1.0f, 1.2f);
        }

        return InteractionResult.SUCCESS;
    }
}
