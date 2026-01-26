package net.vanillaoutsider.betterdogs.util;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;


public class WolfStatManager {

    private static final String AGGRESSIVE_SPEED_ID = "betterdogs:aggressive_speed";
    private static final String AGGRESSIVE_DAMAGE_ID = "betterdogs:aggressive_damage";
    private static final String PACIFIST_SPEED_ID = "betterdogs:pacifist_speed";
    private static final String PACIFIST_DAMAGE_ID = "betterdogs:pacifist_damage";
    private static final String PACIFIST_KNOCKBACK_ID = "betterdogs:pacifist_knockback";
    private static final String BASE_SPEED_ID = "betterdogs:base_speed_boost";
    private static final String AGGRESSIVE_HEALTH_ID = "betterdogs:aggressive_health";
    private static final String NORMAL_SPEED_ID = "betterdogs:normal_speed";
    private static final String NORMAL_DAMAGE_ID = "betterdogs:normal_damage";
    private static final String NORMAL_HEALTH_ID = "betterdogs:normal_health";

    public static void applyPersonalityStats(Wolf wolf, WolfPersonality personality) {
        var speedAttr = wolf.getAttribute(Attributes.MOVEMENT_SPEED);
        var damageAttr = wolf.getAttribute(Attributes.ATTACK_DAMAGE);
        var knockbackAttr = wolf.getAttribute(Attributes.ATTACK_KNOCKBACK);

        if (speedAttr == null || damageAttr == null)
            return;

        Identifier aggressiveSpeedId = Identifier.parse(AGGRESSIVE_SPEED_ID);
        Identifier aggressiveDamageId = Identifier.parse(AGGRESSIVE_DAMAGE_ID);
        Identifier pacifistSpeedId = Identifier.parse(PACIFIST_SPEED_ID);
        Identifier pacifistDamageId = Identifier.parse(PACIFIST_DAMAGE_ID);
        Identifier pacifistKnockbackId = Identifier.parse(PACIFIST_KNOCKBACK_ID);
        Identifier baseSpeedId = Identifier.parse(BASE_SPEED_ID);

        // Remove existing modifiers
        speedAttr.removeModifier(aggressiveSpeedId);
        speedAttr.removeModifier(pacifistSpeedId);
        speedAttr.removeModifier(baseSpeedId);
        damageAttr.removeModifier(aggressiveDamageId);
        damageAttr.removeModifier(pacifistDamageId);

        // Apply Base Speed Boost (Configurable) to all Better Dogs
        // Note: Global Speed Buff remains a Config setting (client/server sync issue potential, but keeping as planned)
        // Wait, plan said migrate everything. Let's see. 
        // Plan Table Pass 3 didn't list strict Global Speed migration. 
        // Let's assume Config for Global for now to be safe, or migrate. 
        // The user said "global setting... world gamerule as per world".
        // Global Speed Buff sounds like a global setting. Leaving it as Config.
        double speedBuff = BetterDogsConfig.get().getGlobalSpeedBuff();
        speedAttr.addPermanentModifier(new AttributeModifier(baseSpeedId, speedBuff,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        if (knockbackAttr != null) {
            knockbackAttr.removeModifier(pacifistKnockbackId);
        }

        switch (personality) {
            case AGGRESSIVE -> {
                double speedMod = BetterDogsGameRules.getPct(wolf.level(), BetterDogsGameRules.BD_AGGRO_SPEED_PCT);
                double damageMod = BetterDogsGameRules.getPct(wolf.level(), BetterDogsGameRules.BD_AGGRO_DMG_PCT);

                speedAttr.addPermanentModifier(new AttributeModifier(aggressiveSpeedId, speedMod,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                damageAttr.addPermanentModifier(new AttributeModifier(aggressiveDamageId, damageMod,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

                var healthAttr = wolf.getAttribute(Attributes.MAX_HEALTH);
                if (healthAttr != null) {
                    Identifier aggressiveHealthId = Identifier.parse(AGGRESSIVE_HEALTH_ID);
                    healthAttr.removeModifier(aggressiveHealthId);

                    double hpBonus = BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_AGGRO_HEALTH);
                    // Handle negative bonus explicitly if needed, but health logic usually handles add_value fine.
                    // Config was -10.0. Game Rule will be -10.
                    
                    if (hpBonus != 0) { // Allow negative HP bonus
                        healthAttr.addPermanentModifier(new AttributeModifier(aggressiveHealthId, hpBonus,
                                AttributeModifier.Operation.ADD_VALUE));
                         // If reducing health, we might need to clamp current health
                        if (wolf.getHealth() > wolf.getMaxHealth()) {
                            wolf.setHealth(wolf.getMaxHealth());
                        }
                    }
                }
            }
            case PACIFIST -> {
                double speedMod = BetterDogsGameRules.getPct(wolf.level(), BetterDogsGameRules.BD_PACI_SPEED_PCT);
                double damageMod = BetterDogsGameRules.getPct(wolf.level(), BetterDogsGameRules.BD_PACI_DMG_PCT);
                double kbMod = BetterDogsGameRules.getPct(wolf.level(), BetterDogsGameRules.BD_PACI_KNOCKBACK_PCT);

                speedAttr.addPermanentModifier(new AttributeModifier(pacifistSpeedId, speedMod,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                damageAttr.addPermanentModifier(new AttributeModifier(pacifistDamageId, damageMod,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                if (knockbackAttr != null) {
                    knockbackAttr.addPermanentModifier(new AttributeModifier(pacifistKnockbackId, kbMod,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }

                var healthAttr = wolf.getAttribute(Attributes.MAX_HEALTH);
                if (healthAttr != null) {
                    Identifier pacifistHealthId = Identifier.parse("betterdogs:pacifist_health");
                    healthAttr.removeModifier(pacifistHealthId);

                    double hpBonus = BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_PACI_HEALTH);
                    if (hpBonus != 0) {
                        healthAttr.addPermanentModifier(new AttributeModifier(pacifistHealthId, hpBonus,
                                AttributeModifier.Operation.ADD_VALUE));
                        if (hpBonus > 0 && wolf.getHealth() < wolf.getMaxHealth()) {
                            wolf.heal((float) hpBonus);
                        }
                    }
                }
            }
            case NORMAL -> {
                double speedMod = BetterDogsGameRules.getPct(wolf.level(), BetterDogsGameRules.BD_NORMAL_SPEED_PCT);
                double damageMod = BetterDogsGameRules.getPct(wolf.level(), BetterDogsGameRules.BD_NORMAL_DMG_PCT);
                double healthMod = BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_NORMAL_HEALTH);

                Identifier normalSpeedId = Identifier.parse(NORMAL_SPEED_ID);
                Identifier normalDamageId = Identifier.parse(NORMAL_DAMAGE_ID);
                Identifier normalHealthId = Identifier.parse(NORMAL_HEALTH_ID);

                if (speedMod != 0.0) {
                    speedAttr.addPermanentModifier(new AttributeModifier(normalSpeedId, speedMod,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
                if (damageMod != 0.0) {
                    damageAttr.addPermanentModifier(new AttributeModifier(normalDamageId, damageMod,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }

                var healthAttr = wolf.getAttribute(Attributes.MAX_HEALTH);
                if (healthAttr != null) {
                    if (healthMod > 0) {
                         healthAttr.addPermanentModifier(new AttributeModifier(normalHealthId, healthMod,
                                AttributeModifier.Operation.ADD_VALUE));
                        if (wolf.getHealth() < wolf.getMaxHealth()) {
                            wolf.heal((float) healthMod);
                        }
                    }
                }
            }
        }
    }
}
