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

    private static final String BASE_SPEED_ID = "betterdogs:base_speed_boost";
    private static final String PACIFIST_KNOCKBACK_ID = "betterdogs:pacifist_knockback";
    
    // Legacy Modifier IDs for cleanup
    private static final String AGGRESSIVE_SPEED_ID = "betterdogs:aggressive_speed";
    private static final String AGGRESSIVE_DAMAGE_ID = "betterdogs:aggressive_damage";
    private static final String PACIFIST_SPEED_ID = "betterdogs:pacifist_speed";
    private static final String PACIFIST_DAMAGE_ID = "betterdogs:pacifist_damage";
    private static final String AGGRESSIVE_HEALTH_ID = "betterdogs:aggressive_health";
    private static final String NORMAL_SPEED_ID = "betterdogs:normal_speed";
    private static final String NORMAL_DAMAGE_ID = "betterdogs:normal_damage";
    private static final String NORMAL_HEALTH_ID = "betterdogs:normal_health";
    private static final String PACIFIST_HEALTH_ID = "betterdogs:pacifist_health";

    // New Rolled Modifier IDs
    private static final String ROLLED_HEALTH_ID = "betterdogs:rolled_health";
    private static final String ROLLED_DAMAGE_ID = "betterdogs:rolled_damage";
    private static final String ROLLED_SPEED_ID = "betterdogs:rolled_speed";

    public static void applyPersonalityStats(Wolf wolf, WolfPersonality personality) {
        var speedAttr = wolf.getAttribute(Attributes.MOVEMENT_SPEED);
        var damageAttr = wolf.getAttribute(Attributes.ATTACK_DAMAGE);
        var knockbackAttr = wolf.getAttribute(Attributes.ATTACK_KNOCKBACK);
        var healthAttr = wolf.getAttribute(Attributes.MAX_HEALTH);

        if (speedAttr == null || damageAttr == null)
            return;

        Identifier aggressiveSpeedId = Identifier.parse(AGGRESSIVE_SPEED_ID);
        Identifier aggressiveDamageId = Identifier.parse(AGGRESSIVE_DAMAGE_ID);
        Identifier pacifistSpeedId = Identifier.parse(PACIFIST_SPEED_ID);
        Identifier pacifistDamageId = Identifier.parse(PACIFIST_DAMAGE_ID);
        Identifier pacifistKnockbackId = Identifier.parse(PACIFIST_KNOCKBACK_ID);
        Identifier baseSpeedId = Identifier.parse(BASE_SPEED_ID);
        Identifier aggressiveHealthId = Identifier.parse(AGGRESSIVE_HEALTH_ID);
        Identifier normalSpeedId = Identifier.parse(NORMAL_SPEED_ID);
        Identifier normalDamageId = Identifier.parse(NORMAL_DAMAGE_ID);
        Identifier normalHealthId = Identifier.parse(NORMAL_HEALTH_ID);
        Identifier pacifistHealthId = Identifier.parse(PACIFIST_HEALTH_ID);

        Identifier rolledHealthId = Identifier.parse(ROLLED_HEALTH_ID);
        Identifier rolledDamageId = Identifier.parse(ROLLED_DAMAGE_ID);
        Identifier rolledSpeedId = Identifier.parse(ROLLED_SPEED_ID);

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

        // Remove new modifiers before re-applying to prevent stacking
        speedAttr.removeModifier(rolledSpeedId);
        damageAttr.removeModifier(rolledDamageId);

        // Apply Base Speed Boost (Configurable) to all Better Dogs
        double speedBuff = BetterDogsConfig.get().getGlobalSpeedBuff();
        speedAttr.addPermanentModifier(new AttributeModifier(baseSpeedId, speedBuff,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

        // 1. Check/Roll stats
        if (!WolfPersistentData.arePersistedStatsRolled(wolf)) {
            UUID uuid = wolf.getUUID();
            long seed = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();
            RandomSource rand = RandomSource.create(seed);

            float rolledHp = 0.0f;
            float rolledDmg = 0.0f;
            float rolledSpeed = 0.0f;

            switch (personality) {
                case NORMAL -> {
                    rolledHp = rand.triangle(-2.0f, 10.0f);
                    rolledDmg = rand.triangle(-0.05f, 0.25f);
                    rolledSpeed = rand.triangle(-0.025f, 0.175f);
                }
                case AGGRESSIVE -> {
                    rolledHp = rand.triangle(-5.0f, 11.0f);
                    rolledDmg = rand.triangle(0.15f, 0.25f);
                    rolledSpeed = rand.triangle(0.075f, 0.175f);
                }
                case PACIFIST -> {
                    rolledHp = rand.triangle(11.0f, 15.0f);
                    rolledDmg = rand.triangle(-0.20f, 0.30f);
                    rolledSpeed = rand.triangle(-0.15f, 0.20f);
                }
            }

            // Save rolled stats to persistent data
            WolfPersistentData.setPersistedHealthBonus(wolf, rolledHp);
            WolfPersistentData.setPersistedDamageMod(wolf, rolledDmg);
            WolfPersistentData.setPersistedSpeedMod(wolf, rolledSpeed);
            WolfPersistentData.setPersistedStatsRolled(wolf, true);
        }

        // 2. Retrieve rolled stats
        float healthBonus = WolfPersistentData.getPersistedHealthBonus(wolf);
        float damageMod = WolfPersistentData.getPersistedDamageMod(wolf);
        float speedMod = WolfPersistentData.getPersistedSpeedMod(wolf);

        // Calculate and apply dynamic scale based on health bonus (Option 2: 0.808x to 1.312x range)
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

        // 3. Apply Attack Damage Modifier
        damageAttr.addPermanentModifier(new AttributeModifier(rolledDamageId, damageMod,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        // 4. Apply Movement Speed Modifier
        speedAttr.addPermanentModifier(new AttributeModifier(rolledSpeedId, speedMod,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        // 5. Apply Max Health Modifier
        if (healthAttr != null) {
            float prevMaxHealth = wolf.getMaxHealth();
            float prevHealth = wolf.getHealth();
            boolean wasAtFullHealth = prevHealth >= prevMaxHealth;

            if (healthBonus != 0.0f) {
                healthAttr.addPermanentModifier(new AttributeModifier(rolledHealthId, healthBonus,
                        AttributeModifier.Operation.ADD_VALUE));

                float newMaxHealth = wolf.getMaxHealth();
                if (healthBonus < 0.0f) {
                    if (wolf.getHealth() > newMaxHealth) {
                        wolf.setHealth(newMaxHealth);
                    }
                } else {
                    if (wasAtFullHealth && wolf.getHealth() < newMaxHealth) {
                        wolf.heal(newMaxHealth - prevMaxHealth);
                    }
                }
            }
        }

        // 6. Apply Pacifist Knockback Modifier (Static GameRule value)
        if (personality == WolfPersonality.PACIFIST && knockbackAttr != null) {
            double kbMod = DynamicGameRuleManager.getPct(wolf.level(), BetterDogsGameRules.BD_PACI_KNOCKBACK_PCT);
            knockbackAttr.addPermanentModifier(new AttributeModifier(pacifistKnockbackId, kbMod,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }
}
