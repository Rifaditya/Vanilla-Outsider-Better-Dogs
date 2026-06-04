// Verified against: WolfStatManager.java (26.2+)
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
    }
}
