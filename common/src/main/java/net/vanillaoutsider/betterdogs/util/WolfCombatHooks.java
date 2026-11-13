// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import net.minecraft.network.chat.Component;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.scheduler.events.RetaliationDogEvent;

public class WolfCombatHooks {

    /**
     * Handles the 'actuallyHurt' logic for wolves.
     * 
     * @return true if the damage should be CANCELLED, false otherwise.
     */
    public static boolean onActuallyHurt(Wolf wolf, DamageSource source, float amount) {
        // Heroic Sacrifice Damage Routing & Anti-One-Shot Protection (BL-BD-021)
        if (net.vanillaoutsider.betterdogs.ai.WolfProjectileInterceptHelper.handleInterceptDamage(wolf, source, amount)) {
            return true;
        }

        if (source.getEntity() instanceof net.minecraft.world.entity.player.Player player && wolf.isTame() && wolf.isOwnedBy(player)) {
            boolean demeritAccidental = net.dasik.social.api.gamerule.DynamicGameRuleManager.getBoolean(wolf.level(), net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.BD_DEMERIT_ACCIDENTAL_ATTACKS);
            if (player.isCrouching()) {
                net.vanillaoutsider.betterdogs.WolfPersistentData.setPersistedFeedCount(wolf, 0);
                WolfDebugLogger.log(wolf, "Interaction", "Owner intentionally attacked dog (crouching), resetting interaction/feed count to 0");
            } else if (demeritAccidental) {
                int current = net.vanillaoutsider.betterdogs.WolfPersistentData.getPersistedFeedCount(wolf);
                int newValue = Math.max(0, current - 1);
                net.vanillaoutsider.betterdogs.WolfPersistentData.setPersistedFeedCount(wolf, newValue);
                WolfDebugLogger.log(wolf, "Interaction", "Owner accidentally attacked dog, reducing interaction/feed count by 1 (Current: " + newValue + ")");
            }
        }

        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setLastDamageTime(wolf.tickCount);
            if (ext.betterdogs$isAdoptable()) {
                ext.betterdogs$setAdoptable(false);
                LivingEntity owner = wolf.getOwner();
                if (owner instanceof net.minecraft.world.entity.player.Player player) {
                    player.sendOverlayMessage(Component.translatable("text.betterdogs.adoption_cancelled_damage", wolf.getName()));
                }
            }
        }
        
        WolfDebugLogger.log(wolf, "Hurt", "Source: " + source.getMsgId() + ", Amount: " + amount);

        if (!wolf.isTame())
            return false;

        if (!(source.getEntity() instanceof LivingEntity attacker))
            return false;

        boolean isOwner = attacker instanceof Player player && wolf.isOwnedBy(player);
        boolean isSneaking = attacker instanceof Player player && player.isShiftKeyDown();
        boolean isAllyWolf = attacker instanceof Wolf attackerWolf && attackerWolf.isTame()
                && (wolf.isOwnedBy(attackerWolf.getOwner())
                        || (attackerWolf.getOwner() != null && attackerWolf.isOwnedBy(wolf.getOwner())));

        // 0. SOCIAL CHANNEL BYPASS: If attacker is using Social AI on us, ALLOW
        // damage/trigger
        if (attacker instanceof WolfExtensions ext && ext.betterdogs$getSocialTarget() == wolf) {
            // This is a deliberate social interaction (Correction/Retaliation/PlayFight).
            // Allow it to proceed through standard logic (don't cancel).

            // SPECIAL CASE: Play Fighting Safety (Non-Lethal)
            if (ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.PLAY_FIGHT) {
                float currentHealth = wolf.getHealth();
                // If damage would kill or drop below 1.0f, cancel it or clamp it.
                if (amount >= currentHealth - 1.0f) {
                    wolf.setHealth(1.0f); // Clamp to 1 HP
                    return true; // Cancel the killing blow
                }
            }
        }

        // Friendly Fire & Melee Protection
        if (WolfFriendlyFireHelper.shouldCancelDamage(wolf, source, amount)) {
            return true;
        }

        // 2. Baby Training: If aggressive baby hit by owner (non-sneaking) → set SOCIAL target
        if (isOwner && !isSneaking && wolf.isBaby()) {
            if (wolf instanceof WolfExtensions ext && ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE) {
                EntitySocialScheduler scheduler = ext.betterdogs$getScheduler();
                if (scheduler == null || !scheduler.isEventActive(RetaliationDogEvent.ID)) {
                    float chance = DynamicGameRuleManager.getChance(wolf.level(),
                            BetterDogsGameRules.BD_BABY_RETALIATE_PERCENT);
                    if (wolf.getRandom().nextFloat() < chance) {
                        ext.betterdogs$getOrInitializeScheduler().schedule(
                                new RetaliationDogEvent(attacker));
                    }
                }
            }
        }

        return false; // Default: Do not cancel
    }
}
