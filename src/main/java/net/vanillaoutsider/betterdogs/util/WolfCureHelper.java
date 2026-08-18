// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.genetics.GeneticsEngine;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.vanillaoutsider.betterdogs.BetterDogs;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.WolfPersistentData;

import java.util.UUID;

/**
 * Single-purpose helper for curing inbred runt dogs with Golden Apples.
 */
public final class WolfCureHelper {

    private WolfCureHelper() {
    }

    /**
     * Pure scale calculation math for restoring cured runt scale to normal size.
     */
    public static float calculateCuredScale(float currentScale) {
        if (currentScale < 1.0f) {
            return 1.0f;
        }
        return currentScale;
    }

    /**
     * Checks if the item held can cure an inbred wolf.
     */
    public static boolean isCureItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.is(Items.GOLDEN_APPLE) || stack.is(Items.ENCHANTED_GOLDEN_APPLE);
    }

    /**
     * Checks if a wolf is eligible to be cured by the given player and item.
     */
    public static boolean canCure(Wolf wolf, Player player, InteractionHand hand, ItemStack stack) {
        if (wolf == null || player == null || stack == null || stack.isEmpty()) {
            return false;
        }

        if (hand != InteractionHand.MAIN_HAND) {
            return false;
        }

        if (!wolf.isTame() || !wolf.isOwnedBy(player)) {
            return false;
        }

        if (!isCureItem(stack)) {
            return false;
        }

        return GeneticsEngine.getGenetics(wolf).inbred();
    }

    /**
     * Attempts to cure an inbred wolf with a Golden Apple, applying scale restoration,
     * status buffs, particle feedback, and advancement triggers.
     */
    public static InteractionResult tryCureInbredWolf(Wolf wolf, Player player, InteractionHand hand, ItemStack stack) {
        if (!canCure(wolf, player, hand, stack)) {
            return InteractionResult.PASS;
        }

        if (wolf.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (wolf.level() instanceof ServerLevel serverLevel) {
            boolean isEnchanted = stack.is(Items.ENCHANTED_GOLDEN_APPLE);

            // 1. Consume Golden Apple (Creative Bypass)
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            // 2. Clear Inbred Status in Genetics
            UUID p1 = WolfPersistentData.getPersistedParent1Uuid(wolf).orElse(null);
            UUID p2 = WolfPersistentData.getPersistedParent2Uuid(wolf).orElse(null);
            WolfPersistentData.setPersistedParentsAndInbred(wolf, p1, p2, false);

            // 3. Restore Scale to Normal
            var scaleAttr = wolf.getAttribute(Attributes.SCALE);
            if (scaleAttr != null) {
                float currentScale = (float) scaleAttr.getBaseValue();
                scaleAttr.setBaseValue(calculateCuredScale(currentScale));
            }

            // 4. Restore Personality Combat Stats
            if (wolf instanceof WolfExtensions ext) {
                WolfPersonality personality = ext.betterdogs$getPersonality();
                if (personality != null) {
                    WolfPersonalityStatHelper.applyPersonalityStats(wolf, personality);
                }
            }

            // 5. Apply Enchanted Golden Apple Buffs
            if (isEnchanted) {
                wolf.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1));
                wolf.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0));
            }

            // 6. Play Audio & Golden Cure Particles
            playCureFeedback(wolf, serverLevel);

            // 7. Fire Advancement Trigger
            if (player instanceof ServerPlayer serverPlayer) {
                BetterDogs.CURE_INBRED.trigger(serverPlayer);
            }
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Emits happy villager golden sparkle particles and level-up audio.
     */
    public static void playCureFeedback(Wolf wolf, ServerLevel level) {
        if (wolf == null || level == null || level.isClientSide()) {
            return;
        }

        level.sendParticles(
                ParticleTypes.HAPPY_VILLAGER,
                wolf.getX(),
                wolf.getY() + 0.5,
                wolf.getZ(),
                15,
                0.35,
                0.35,
                0.35,
                0.05
        );
        level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8f, 1.2f);
        if (wolf instanceof net.vanillaoutsider.betterdogs.mixin.WolfAccessor accessor) {
            var ambientSound = accessor.betterdogs$invokeGetAmbientSound();
            if (ambientSound != null) {
                level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), ambientSound, wolf.getSoundSource(), 1.0f, 1.2f);
            }
        }
    }
}
