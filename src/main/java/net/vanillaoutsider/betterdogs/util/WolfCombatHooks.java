package net.vanillaoutsider.betterdogs.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.scheduler.events.RetaliationDogEvent;

public class WolfCombatHooks {

    /**
     * Handles the 'actuallyHurt' logic for wolves.
     * @return true if the damage should be CANCELLED, false otherwise.
     */
    public static boolean onActuallyHurt(Wolf wolf, DamageSource source, float amount) {
        if (!wolf.isTame()) return false;

        Entity entityAttacker = source.getEntity();
        if (!(entityAttacker instanceof LivingEntity attacker)) return false;

        boolean isOwner = attacker instanceof Player player && wolf.isOwnedBy(player);
        boolean isSneaking = attacker instanceof Player player && player.isShiftKeyDown();
        boolean isAllyWolf = attacker instanceof Wolf attackerWolf && attackerWolf.isTame() 
            && (wolf.isOwnedBy(attackerWolf.getOwner()) || (attackerWolf.getOwner() != null && attackerWolf.isOwnedBy(wolf.getOwner())));

        // 0. SOCIAL CHANNEL BYPASS: If attacker is using Social AI on us, ALLOW damage/trigger
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
                // Determine if we should reduce damage? 
                // Maybe just let normal damage happen until 1 HP.
            }
        } else if ((isAllyWolf || isOwner) && !isSneaking) {
            float currentHealth = wolf.getHealth();
            if (amount >= currentHealth - 1.0f) {
                if (currentHealth > 1.0f) {
                    wolf.setHealth(1.0f);
                }
                return true; // Cancel lethal friendly fire
            }
        }

        // 2. Baby Training: If aggressive baby hit by owner (non-sneaking) → set SOCIAL target
        if (isOwner && !isSneaking && wolf.isBaby()) {
            if (wolf instanceof WolfExtensions ext && ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE) {
                // COMMAND CENTER: Inject Retaliation Behavior
                // 100 ticks = 5 seconds
                // Pass attacker context explicitly because vanilla hasn't updated lastHurtByMob yet!
                
                // FIX: Don't Spam duplicate events if already active!
                // CONFIG: Check configurable retaliation chance (percentage)
                net.vanillaoutsider.social.core.EntitySocialScheduler scheduler = ext.betterdogs$getScheduler();
                if (scheduler == null || !scheduler.isEventActive(RetaliationDogEvent.ID)) {
                     // Get chance from Game Rules (Stored as integer Percent e.g. 75)
                     float chance = BetterDogsGameRules.getChance(wolf.level(), BetterDogsGameRules.BD_BABY_RETALIATE_PERCENT);
                     if (wolf.getRandom().nextFloat() < chance) {
                         ext.betterdogs$getOrInitializeScheduler().injectBehavior(
                            RetaliationDogEvent.ID, 
                            100,
                            attacker
                        );
                     }
                }
            }
        }

        // 4. Owner friendly fire protection cancellation
        // Use GameRule BD_FRIENDLY_FIRE
        if (isOwner && !isSneaking) {
            boolean friendlyFireProto = BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_FRIENDLY_FIRE);
            if (friendlyFireProto) {
            // EXCEPTION: Allows "Provocation" taps on Aggressive Babies to trigger retaliation
            if (wolf.isBaby() && wolf instanceof WolfExtensions ext && ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE) {
                // Allow non-lethal damage (capped at 2.0f to prevent accidental one-shots with swords)
                if (amount > 2.0f) {
                    wolf.setHealth(wolf.getHealth() + (amount - 2.0f)); 
                }
                // Do NOT cancel. Let the hit land.
                return false; 
            } else {
                return true; // Cancel damage
            }
            }
        }
        
        return false; // Default: Do not cancel
    }

    /**
     * Handles 'wantsToAttack' logic.
     * @return Boolean.TRUE to allow (override), Boolean.FALSE to deny (override), or null to pass to super.
     */
    public static Boolean wantsToAttack(Wolf wolf, LivingEntity target, LivingEntity owner) {
        if (!(wolf instanceof WolfExtensions ext)) return null;

        // 0. RETALIATION OVERRIDE: If this is an authorized Retaliation vs the specific target, ALLOW IT.
        // This overrides TamableAnimal.wantsToAttack() which normally blocks attacking the owner.
        if (ext.betterdogs$isSocialModeActive() 
            && ext.betterdogs$getSocialTarget() == target) {
            
            // Allow Retaliation OR Play Fight
            if (ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.RETALIATION ||
                ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.PLAY_FIGHT) {
                return true;
            }
        }

        // Blood Feud: ALLOW attacking nemesis (bypass all protection)
        if (target instanceof Wolf targetWolf && ext.betterdogs$hasBloodFeud()) {
            if (ext.betterdogs$getBloodFeudTarget().equals(targetWolf.getStringUUID())) {
                return true;
            }
        }

        // Submissive: Cannot attack pack members
        if (ext.betterdogs$isSubmissive()) {
            boolean isPackMember = target instanceof Wolf targetWolf && targetWolf.isTame() 
                && targetWolf.getOwner() != null && targetWolf.getOwner().equals(owner);
            if (isPackMember) {
                return false;
            }
        }

        // Baby wolves cannot attack adult pack members (can still target owner for retaliation)
        if (wolf.isBaby() && target instanceof Wolf targetWolf && targetWolf.isTame() && wolf.isTame()) {
            if (!targetWolf.isBaby() && targetWolf.getOwner() != null && targetWolf.getOwner().equals(wolf.getOwner())) {
                return false;
            }
        }
        
        return null; // Pass to super
    }
}
