// Verified against: WolfStatManager.java (26.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.util;

import java.util.UUID;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

public class WolfStatManager {

    private static final Identifier BASE_SPEED_ID = Identifier.fromNamespaceAndPath("betterdogs", "base_speed_boost");
    private static final Identifier PACIFIST_KNOCKBACK_ID = Identifier.fromNamespaceAndPath("betterdogs", "pacifist_knockback");
    
    // Legacy Modifier IDs for cleanup
    private static final Identifier AGGRESSIVE_SPEED_ID = Identifier.fromNamespaceAndPath("betterdogs", "aggressive_speed");
    private static final Identifier AGGRESSIVE_DAMAGE_ID = Identifier.fromNamespaceAndPath("betterdogs", "aggressive_damage");
    private static final Identifier PACIFIST_SPEED_ID = Identifier.fromNamespaceAndPath("betterdogs", "pacifist_speed");
    private static final Identifier PACIFIST_DAMAGE_ID = Identifier.fromNamespaceAndPath("betterdogs", "pacifist_damage");
    private static final Identifier AGGRESSIVE_HEALTH_ID = Identifier.fromNamespaceAndPath("betterdogs", "aggressive_health");
    private static final Identifier NORMAL_SPEED_ID = Identifier.fromNamespaceAndPath("betterdogs", "normal_speed");
    private static final Identifier NORMAL_DAMAGE_ID = Identifier.fromNamespaceAndPath("betterdogs", "normal_damage");
    private static final Identifier NORMAL_HEALTH_ID = Identifier.fromNamespaceAndPath("betterdogs", "normal_health");
    private static final Identifier PACIFIST_HEALTH_ID = Identifier.fromNamespaceAndPath("betterdogs", "pacifist_health");

    // New Rolled Modifier IDs (to be cleaned up during migration to DasikLibrary)
    private static final Identifier ROLLED_HEALTH_ID = Identifier.fromNamespaceAndPath("betterdogs", "rolled_health");
    private static final Identifier ROLLED_DAMAGE_ID = Identifier.fromNamespaceAndPath("betterdogs", "rolled_damage");
    private static final Identifier ROLLED_SPEED_ID = Identifier.fromNamespaceAndPath("betterdogs", "rolled_speed");

    public static void applyPersonalityStats(Wolf wolf, WolfPersonality personality) {
        var speedAttr = wolf.getAttribute(Attributes.MOVEMENT_SPEED);
        var damageAttr = wolf.getAttribute(Attributes.ATTACK_DAMAGE);
        var knockbackAttr = wolf.getAttribute(Attributes.ATTACK_KNOCKBACK);
        var healthAttr = wolf.getAttribute(Attributes.MAX_HEALTH);

        if (speedAttr == null || damageAttr == null)
            return;

        Identifier aggressiveSpeedId = AGGRESSIVE_SPEED_ID;
        Identifier aggressiveDamageId = AGGRESSIVE_DAMAGE_ID;
        Identifier pacifistSpeedId = PACIFIST_SPEED_ID;
        Identifier pacifistDamageId = PACIFIST_DAMAGE_ID;
        Identifier pacifistKnockbackId = PACIFIST_KNOCKBACK_ID;
        Identifier baseSpeedId = BASE_SPEED_ID;
        Identifier aggressiveHealthId = AGGRESSIVE_HEALTH_ID;
        Identifier normalSpeedId = NORMAL_SPEED_ID;
        Identifier normalDamageId = NORMAL_DAMAGE_ID;
        Identifier normalHealthId = NORMAL_HEALTH_ID;
        Identifier pacifistHealthId = PACIFIST_HEALTH_ID;

        Identifier rolledHealthId = ROLLED_HEALTH_ID;
        Identifier rolledDamageId = ROLLED_DAMAGE_ID;
        Identifier rolledSpeedId = ROLLED_SPEED_ID;

        // Remove ALL legacy modifiers to prevent conflict/stacking
        speedAttr.removeModifier(aggressiveSpeedId);
        speedAttr.removeModifier(pacifistSpeedId);
        speedAttr.removeModifier(normalSpeedId);
        speedAttr.removeModifier(baseSpeedId);

        damageAttr.removeModifier(aggressiveDamageId);
        damageAttr.removeModifier(pacifistDamageId);
        damageAttr.removeModifier(normalDamageId);

        if (knockbackAttr != null) {
            knockbackAttr.removeModifier(pacifistKnockbackId);
        }

        if (healthAttr != null) {
            healthAttr.removeModifier(aggressiveHealthId);
            healthAttr.removeModifier(normalHealthId);
            healthAttr.removeModifier(pacifistHealthId);
            healthAttr.removeModifier(rolledHealthId);
        }

        // Remove previous rolled modifiers before re-applying to prevent stacking
        speedAttr.removeModifier(rolledSpeedId);
        damageAttr.removeModifier(rolledDamageId);

        // Apply Base Speed Boost (Configurable) to all Better Dogs
        double speedBuff = BetterDogsConfig.get().getGlobalSpeedBuff();
        speedAttr.addPermanentModifier(new AttributeModifier(baseSpeedId, speedBuff,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

        // 1. Check/Roll stats via DasikLibrary Genetics Engine
        if (!net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf).traitsRolled()) {
            net.dasik.social.api.genetics.GeneticsEngine.rollInitialStats(wolf, personality.name().toLowerCase(java.util.Locale.ROOT));
        } else {
            net.dasik.social.api.genetics.GeneticsEngine.applyGeneticsModifiers(wolf);
        }

        // 2. Retrieve rolled stats for scale calculation
        float healthBonus = WolfPersistentData.getPersistedHealthBonus(wolf);
        
        // 2.5 Apply health modifier explicitly so it syncs to the client
        if (healthAttr != null && healthBonus != 0) {
            healthAttr.addPermanentModifier(new AttributeModifier(rolledHealthId, healthBonus, AttributeModifier.Operation.ADD_VALUE));
        }

        // Calculate and apply dynamic scale based on health bonus
        float calculatedScale = 1.0f + (healthBonus * 0.012f);

        // Add dynamic, deterministic random offset between -0.10 and +0.10 based on UUID
        UUID uuid = wolf.getUUID();
        long seed = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits() ^ 5829103L;
        net.minecraft.util.RandomSource scaleRand = net.minecraft.util.RandomSource.create(seed);
        float offset = -0.10f + (scaleRand.nextFloat() * 0.20f);
        calculatedScale += offset;

        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialScale(calculatedScale);
        }

        // Apply scale directly to standard SCALE attribute in 26.2
        var scaleAttr = wolf.getAttribute(Attributes.SCALE);
        if (scaleAttr != null && scaleAttr.getBaseValue() != calculatedScale) {
            scaleAttr.setBaseValue(calculatedScale);
        }

        // 3. Apply Pacifist Knockback Modifier (Static GameRule value)
        if (personality == WolfPersonality.PACIFIST && knockbackAttr != null) {
            double kbMod = DynamicGameRuleManager.getPct(wolf.level(), BetterDogsGameRules.BD_PACI_KNOCKBACK_PCT);
            knockbackAttr.addPermanentModifier(new AttributeModifier(pacifistKnockbackId, kbMod,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }

        // 4. Determine and apply Sound Variant based on genetics/stats mapping
        determineAndApplySoundVariant(wolf, personality, calculatedScale);
    }

    public static void determineAndApplySoundVariant(Wolf wolf, WolfPersonality personality, float scale) {
        if (wolf.level().isClientSide()) return;

        net.dasik.social.api.genetics.EntityGenetics genetics = net.dasik.social.api.genetics.GeneticsEngine.getGenetics(wolf);
        float healthBonus = genetics.traits().getOrDefault("max_health", 0.0f);
        float damageMod = genetics.traits().getOrDefault("attack_damage", 0.0f);
        float speedMod = genetics.traits().getOrDefault("movement_speed", 0.0f);
        boolean isInbred = genetics.inbred();

        // Base weights (Classic is highly weighted as standard fallback, others have a small base probability)
        float classicWeight = 3.0f;
        float bigWeight = 0.5f;
        float cuteWeight = 0.5f;
        float puglinWeight = 0.5f;
        float angryWeight = 0.5f;
        float grumpyWeight = 0.5f;
        float sadWeight = 0.5f;

        // Apply stat modifiers to weights (ensuring no weight goes below 0.0)
        bigWeight = Math.max(0.0f, bigWeight + (scale - 1.0f) * 10.0f + (healthBonus > 0 ? healthBonus * 0.5f : 0));
        cuteWeight = Math.max(0.0f, cuteWeight + (1.0f - scale) * 10.0f + (personality == WolfPersonality.PACIFIST ? 3.0f : 0) + (speedMod > 0 ? speedMod * 5.0f : 0));
        puglinWeight = Math.max(0.0f, puglinWeight + (isInbred ? 6.0f : 0.0f) + (scale < 0.85f ? 4.0f : 0.0f) + (speedMod < 0 ? -speedMod * 10.0f : 0));
        angryWeight = Math.max(0.0f, angryWeight + (personality == WolfPersonality.AGGRESSIVE ? 5.0f : 0.0f) + (damageMod > 0 ? damageMod * 15.0f : 0) + (speedMod > 0 ? speedMod * 5.0f : 0));
        grumpyWeight = Math.max(0.0f, grumpyWeight + (personality == WolfPersonality.AGGRESSIVE ? 3.0f : 0.0f) + (damageMod > 0 ? damageMod * 10.0f : 0) + (speedMod < 0 ? -speedMod * 15.0f : 0));
        sadWeight = Math.max(0.0f, sadWeight + (personality == WolfPersonality.PACIFIST ? 5.0f : 0.0f) + (healthBonus < 0 ? -healthBonus * 1.5f : 0) + (damageMod < 0 ? -damageMod * 10.0f : 0));

        // Seed a RandomSource with the wolf's unique UUID to ensure stable selection
        java.util.UUID uuid = wolf.getUUID();
        long seed = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits() ^ 7381940L;
        net.minecraft.util.RandomSource rand = net.minecraft.util.RandomSource.create(seed);

        // Sum total weight and select via lottery
        float totalWeight = classicWeight + bigWeight + cuteWeight + puglinWeight + angryWeight + grumpyWeight + sadWeight;
        float target = rand.nextFloat() * totalWeight;

        net.minecraft.resources.ResourceKey<net.minecraft.world.entity.animal.wolf.WolfSoundVariant> bestVariant = net.minecraft.world.entity.animal.wolf.WolfSoundVariants.CLASSIC;
        float cumulative = 0.0f;

        cumulative += classicWeight;
        if (target <= cumulative) {
            bestVariant = net.minecraft.world.entity.animal.wolf.WolfSoundVariants.CLASSIC;
        } else {
            cumulative += bigWeight;
            if (target <= cumulative) {
                bestVariant = net.minecraft.world.entity.animal.wolf.WolfSoundVariants.BIG;
            } else {
                cumulative += cuteWeight;
                if (target <= cumulative) {
                    bestVariant = net.minecraft.world.entity.animal.wolf.WolfSoundVariants.CUTE;
                } else {
                    cumulative += puglinWeight;
                    if (target <= cumulative) {
                        bestVariant = net.minecraft.world.entity.animal.wolf.WolfSoundVariants.PUGLIN;
                    } else {
                        cumulative += angryWeight;
                        if (target <= cumulative) {
                            bestVariant = net.minecraft.world.entity.animal.wolf.WolfSoundVariants.ANGRY;
                        } else {
                            cumulative += grumpyWeight;
                            if (target <= cumulative) {
                                bestVariant = net.minecraft.world.entity.animal.wolf.WolfSoundVariants.GRUMPY;
                            } else {
                                cumulative += sadWeight;
                                if (target <= cumulative) {
                                    bestVariant = net.minecraft.world.entity.animal.wolf.WolfSoundVariants.SAD;
                                }
                            }
                        }
                    }
                }
            }
        }

        var soundRegistry = wolf.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.WOLF_SOUND_VARIANT);
        soundRegistry.get(bestVariant).ifPresent(holder -> {
            ((net.vanillaoutsider.betterdogs.mixin.WolfAccessor) wolf).betterdogs$invokeSetSoundVariant(holder);
        });
    }
}
