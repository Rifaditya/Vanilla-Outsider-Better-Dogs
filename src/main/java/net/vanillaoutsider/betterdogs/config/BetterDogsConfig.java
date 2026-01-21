package net.vanillaoutsider.betterdogs.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "vanilla-outsider-better-dogs")
public class BetterDogsConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public double globalSpeedBuff = 0.20;

    @ConfigEntry.Gui.Tooltip
    public boolean enableStormAnxiety = true;

    @ConfigEntry.Gui.Tooltip
    public boolean enableCliffSafety = true;

    @ConfigEntry.Gui.Tooltip
    public boolean enableFriendlyFireProtection = true;

    // ========== Aggressive Personality ==========
    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    public double aggressiveHealthBonus = 20.0;

    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    public double aggressiveSpeedModifier = 0.15;

    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    public double aggressiveDetectionRange = 20.0;

    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    public double aggressiveChaseDistance = 50.0;

    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    public double aggressiveDamageModifier = -0.15;

    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    public float aggressiveFollowStart = 10.0f;

    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    public float aggressiveFollowStop = 2.0f;

    // ========== Pacifist Personality ==========
    @ConfigEntry.Category("pacifist")
    @ConfigEntry.Gui.Tooltip
    public double pacifistHealthBonus = 0.0;

    @ConfigEntry.Category("pacifist")
    @ConfigEntry.Gui.Tooltip
    public double pacifistSpeedModifier = -0.10;

    @ConfigEntry.Category("pacifist")
    @ConfigEntry.Gui.Tooltip
    public double pacifistDamageModifier = 0.15;

    @ConfigEntry.Category("pacifist")
    @ConfigEntry.Gui.Tooltip
    public double pacifistKnockbackModifier = 0.5;

    @ConfigEntry.Category("pacifist")
    @ConfigEntry.Gui.Tooltip
    public float pacifistFollowStart = 6.0f;

    @ConfigEntry.Category("pacifist")
    @ConfigEntry.Gui.Tooltip
    public float pacifistFollowStop = 2.0f;

    // ========== Normal Personality ==========
    @ConfigEntry.Category("normal")
    @ConfigEntry.Gui.Tooltip
    public double normalHealthBonus = 0.0;

    @ConfigEntry.Category("normal")
    @ConfigEntry.Gui.Tooltip
    public double normalSpeedModifier = 0.0;

    @ConfigEntry.Category("normal")
    @ConfigEntry.Gui.Tooltip
    public double normalDamageModifier = 0.0;

    @ConfigEntry.Category("normal")
    @ConfigEntry.Gui.Tooltip
    public float normalFollowStart = 10.0f;

    @ConfigEntry.Category("normal")
    @ConfigEntry.Gui.Tooltip
    public float normalFollowStop = 2.0f;

    // ========== Healing ==========
    @ConfigEntry.Category("healing")
    @ConfigEntry.Gui.Tooltip
    public int passiveHealIntervalTicks = 1200;

    @ConfigEntry.Category("healing")
    @ConfigEntry.Gui.Tooltip
    public double passiveHealAmount = 1.0;

    @ConfigEntry.Category("healing")
    @ConfigEntry.Gui.Tooltip
    public int combatHealDelayTicks = 60;

    // ========== Wild Wolves ==========
    @ConfigEntry.Category("wild")
    @ConfigEntry.Gui.Tooltip
    public float wildHuntHealthThreshold = 0.5f;

    // ========== Taming (Wild Wolf Personality) ==========
    @ConfigEntry.Category("taming")
    @ConfigEntry.Gui.Tooltip
    public int tamingChanceNormal = 60;

    @ConfigEntry.Category("taming")
    @ConfigEntry.Gui.Tooltip
    public int tamingChanceAggressive = 20;

    @ConfigEntry.Category("taming")
    @ConfigEntry.Gui.Tooltip
    public int tamingChancePacifist = 20;

    // ========== Breeding Genetics ==========
    @ConfigEntry.Category("breeding")
    @ConfigEntry.Gui.Tooltip
    public int breedingSameParentChance = 80;

    @ConfigEntry.Category("breeding")
    @ConfigEntry.Gui.Tooltip
    public int breedingSameParentOtherChance = 10;

    @ConfigEntry.Category("breeding")
    @ConfigEntry.Gui.Tooltip
    public int breedingMixedDominantChance = 40;

    @ConfigEntry.Category("breeding")
    @ConfigEntry.Gui.Tooltip
    public int breedingMixedRecessiveChance = 20;

    @ConfigEntry.Category("breeding")
    @ConfigEntry.Gui.Tooltip
    public int breedingDilutedNormalChance = 50;

    @ConfigEntry.Category("breeding")
    @ConfigEntry.Gui.Tooltip
    public int breedingDilutedOtherChance = 25;

    // ========== Gifts ==========
    @ConfigEntry.Category("gifts")
    @ConfigEntry.Gui.Tooltip
    public int giftCooldownMin = 12000;

    @ConfigEntry.Category("gifts")
    @ConfigEntry.Gui.Tooltip
    public int giftCooldownMax = 18000;

    @ConfigEntry.Category("gifts")
    @ConfigEntry.Gui.Tooltip
    public float giftTriggerChance = 0.01f;

    // ========== General/Misc expansions ==========
    @ConfigEntry.Gui.Tooltip
    public float stormAnxietyTriggerChance = 0.01f;

    @ConfigEntry.Gui.Tooltip
    public float stormWhineChance = 0.02f;

    public static BetterDogsConfig get() {
        return AutoConfig.getConfigHolder(BetterDogsConfig.class).getConfig();
    }

    // Getters for Mixin usage (since I saw mixins calling getters in Kotlin)
    // Kotlin properties generated getters automatically, in Java fields might be
    // accessed directly.
    // However, the existing mixins called getAggressiveSpeedModifier() etc.
    // I should provide these getters to minimize mixin churn, or update mixins to
    // access fields.
    // Since mixins are already written to use getters (from Kotlin interop), adding
    // them is safer.

    public double getGlobalSpeedBuff() {
        return globalSpeedBuff;
    }

    public boolean getEnableStormAnxiety() {
        return enableStormAnxiety;
    }

    public boolean getEnableCliffSafety() {
        return enableCliffSafety;
    }

    public boolean getEnableFriendlyFireProtection() {
        return enableFriendlyFireProtection;
    }

    public double getAggressiveHealthBonus() {
        return aggressiveHealthBonus;
    }

    public double getAggressiveSpeedModifier() {
        return aggressiveSpeedModifier;
    }

    public double getAggressiveDetectionRange() {
        return aggressiveDetectionRange;
    }

    public double getAggressiveChaseDistance() {
        return aggressiveChaseDistance;
    }

    public double getAggressiveDamageModifier() {
        return aggressiveDamageModifier;
    }

    public float getAggressiveFollowStart() {
        return aggressiveFollowStart;
    }

    public float getAggressiveFollowStop() {
        return aggressiveFollowStop;
    }

    public double getPacifistHealthBonus() {
        return pacifistHealthBonus;
    }

    public double getPacifistSpeedModifier() {
        return pacifistSpeedModifier;
    }

    public double getPacifistDamageModifier() {
        return pacifistDamageModifier;
    }

    public double getPacifistKnockbackModifier() {
        return pacifistKnockbackModifier;
    }

    public float getPacifistFollowStart() {
        return pacifistFollowStart;
    }

    public float getPacifistFollowStop() {
        return pacifistFollowStop;
    }

    public double getNormalHealthBonus() {
        return normalHealthBonus;
    }

    public double getNormalSpeedModifier() {
        return normalSpeedModifier;
    }

    public double getNormalDamageModifier() {
        return normalDamageModifier;
    }

    public float getNormalFollowStart() {
        return normalFollowStart;
    }

    public float getNormalFollowStop() {
        return normalFollowStop;
    }

    public int getPassiveHealIntervalTicks() {
        return passiveHealIntervalTicks;
    }

    public double getPassiveHealAmount() {
        return passiveHealAmount;
    }

    public int getCombatHealDelayTicks() {
        return combatHealDelayTicks;
    }

    // Genetics getters
    public int getBreedingSameParentChance() {
        return breedingSameParentChance;
    }

    public int getBreedingSameParentOtherChance() {
        return breedingSameParentOtherChance;
    }

    public int getBreedingMixedDominantChance() {
        return breedingMixedDominantChance;
    }

    public int getBreedingMixedRecessiveChance() {
        return breedingMixedRecessiveChance;
    }

    public int getBreedingDilutedNormalChance() {
        return breedingDilutedNormalChance;
    }

    public int getBreedingDilutedOtherChance() {
        return breedingDilutedOtherChance;
    }
}
