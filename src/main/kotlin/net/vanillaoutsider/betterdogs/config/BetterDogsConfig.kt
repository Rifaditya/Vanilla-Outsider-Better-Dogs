package net.vanillaoutsider.betterdogs.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry
import me.shedaniel.autoconfig.AutoConfig

@Config(name = "vanilla-outsider-better-dogs")
class BetterDogsConfig : ConfigData {

    @ConfigEntry.Gui.Tooltip
    var globalSpeedBuff: Double = 0.20

    @ConfigEntry.Gui.Tooltip
    var enableStormAnxiety: Boolean = true

    @ConfigEntry.Gui.Tooltip
    var enableCliffSafety: Boolean = true

    @ConfigEntry.Gui.Tooltip
    var enableFriendlyFireProtection: Boolean = true

    // ========== Movement & Follow (Global Defaults / Deprecated or Base) ==========
    // We will use specific ones, but might keep these as fallbacks or remove them.
    // For "Granular", we should move them to categories.
    // Removing these generic ones to force granular usage.

    // ========== Aggressive Personality ==========
    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    var aggressiveHealthBonus: Double = 20.0

    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    var aggressiveSpeedModifier: Double = 0.15

    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    var aggressiveDetectionRange: Double = 20.0

    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    var aggressiveChaseDistance: Double = 50.0

    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    var aggressiveDamageModifier: Double = -0.15

    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    var aggressiveFollowStart: Float = 10.0f

    @ConfigEntry.Category("aggressive")
    @ConfigEntry.Gui.Tooltip
    var aggressiveFollowStop: Float = 2.0f

    // ========== Pacifist Personality ==========
    @ConfigEntry.Category("pacifist")
    @ConfigEntry.Gui.Tooltip
    var pacifistHealthBonus: Double = 0.0

    @ConfigEntry.Category("pacifist")
    @ConfigEntry.Gui.Tooltip
    var pacifistSpeedModifier: Double = -0.10

    @ConfigEntry.Category("pacifist")
    @ConfigEntry.Gui.Tooltip
    var pacifistDamageModifier: Double = 0.15
    
    @ConfigEntry.Category("pacifist")
    @ConfigEntry.Gui.Tooltip
    var pacifistKnockbackModifier: Double = 0.5 

    @ConfigEntry.Category("pacifist")
    @ConfigEntry.Gui.Tooltip
    var pacifistFollowStart: Float = 6.0f

    @ConfigEntry.Category("pacifist")
    @ConfigEntry.Gui.Tooltip
    var pacifistFollowStop: Float = 2.0f

    // ========== Normal Personality ==========
    @ConfigEntry.Category("normal")
    @ConfigEntry.Gui.Tooltip
    var normalHealthBonus: Double = 0.0

    @ConfigEntry.Category("normal")
    @ConfigEntry.Gui.Tooltip
    var normalSpeedModifier: Double = 0.0

    @ConfigEntry.Category("normal")
    @ConfigEntry.Gui.Tooltip
    var normalDamageModifier: Double = 0.0

    @ConfigEntry.Category("normal")
    @ConfigEntry.Gui.Tooltip
    var normalFollowStart: Float = 10.0f

    @ConfigEntry.Category("normal")
    @ConfigEntry.Gui.Tooltip
    var normalFollowStop: Float = 2.0f

    // ========== Healing ==========
    @ConfigEntry.Category("healing")
    @ConfigEntry.Gui.Tooltip
    var passiveHealIntervalTicks: Int = 1200 

    @ConfigEntry.Category("healing")
    @ConfigEntry.Gui.Tooltip
    var passiveHealAmount: Double = 1.0

    @ConfigEntry.Category("healing")
    @ConfigEntry.Gui.Tooltip
    var combatHealDelayTicks: Int = 60

    // ========== Wild Wolves ==========
    @ConfigEntry.Category("wild")
    @ConfigEntry.Gui.Tooltip
    var wildHuntHealthThreshold: Float = 0.5f

    // ========== Taming (Wild Wolf Personality) ==========
    @ConfigEntry.Category("taming")
    @ConfigEntry.Gui.Tooltip
    var tamingChanceNormal: Int = 60

    @ConfigEntry.Category("taming")
    @ConfigEntry.Gui.Tooltip
    var tamingChanceAggressive: Int = 20

    @ConfigEntry.Category("taming")
    @ConfigEntry.Gui.Tooltip
    var tamingChancePacifist: Int = 20

    // ========== Breeding Genetics ==========
    @ConfigEntry.Category("breeding")
    @ConfigEntry.Gui.Tooltip
    var breedingSameParentChance: Int = 80

    @ConfigEntry.Category("breeding")
    @ConfigEntry.Gui.Tooltip
    var breedingSameParentOtherChance: Int = 10

    @ConfigEntry.Category("breeding")
    @ConfigEntry.Gui.Tooltip
    var breedingMixedDominantChance: Int = 40

    @ConfigEntry.Category("breeding")
    @ConfigEntry.Gui.Tooltip
    var breedingMixedRecessiveChance: Int = 20

    @ConfigEntry.Category("breeding")
    @ConfigEntry.Gui.Tooltip
    var breedingDilutedNormalChance: Int = 50

    @ConfigEntry.Category("breeding")
    @ConfigEntry.Gui.Tooltip
    var breedingDilutedOtherChance: Int = 25

    // ========== Gifts ==========
    @ConfigEntry.Category("gifts")
    @ConfigEntry.Gui.Tooltip
    var giftCooldownMin: Int = 12000

    @ConfigEntry.Category("gifts")
    @ConfigEntry.Gui.Tooltip
    var giftCooldownMax: Int = 18000

    @ConfigEntry.Category("gifts")
    @ConfigEntry.Gui.Tooltip
    var giftTriggerChance: Float = 0.01f

    // ========== General/Misc expansions ==========
    @ConfigEntry.Gui.Tooltip
    var stormAnxietyTriggerChance: Float = 0.01f

    @ConfigEntry.Gui.Tooltip
    var stormWhineChance: Float = 0.02f

    // ========== Baby Mischief (New in 1.21.11 Parity) ==========
    @ConfigEntry.Category("mischief")
    @ConfigEntry.Gui.Tooltip
    var babyMischiefChance: Double = 0.025 // 2.5%

    // ========== Training & Discipline (New in 1.21.11 Parity) ==========
    @ConfigEntry.Category("training")
    @ConfigEntry.Gui.Tooltip
    var bloodFeudChance: Int = 5 // 5%

    @ConfigEntry.Category("training")
    @ConfigEntry.Gui.Tooltip
    var babyRetaliationChance: Int = 50 // 50%

    @ConfigEntry.Category("training")
    @ConfigEntry.Gui.Tooltip
    var correctionDamage: Float = 1.0f

    @ConfigEntry.Category("training")
    @ConfigEntry.Gui.Tooltip
    var correctionSpeedModifier: Double = 1.2

    @ConfigEntry.Category("training")
    @ConfigEntry.Gui.Tooltip
    var correctionLookSpeed: Float = 10.0f

    @ConfigEntry.Category("training")
    @ConfigEntry.Gui.Tooltip
    var correctionReachBuffer: Double = 2.0

    @ConfigEntry.Category("training")
    @ConfigEntry.Gui.Tooltip
    var correctionSearchRange: Double = 15.0

    @ConfigEntry.Category("training")
    @ConfigEntry.Gui.Tooltip
    var correctionDuration: Int = 100 // 5 seconds (20 ticks * 5)

    @ConfigEntry.Category("training")
    @ConfigEntry.Gui.Tooltip
    var biteBackSpeedModifier: Double = 1.2

    @ConfigEntry.Category("training")
    @ConfigEntry.Gui.Tooltip
    var biteBackLookSpeed: Float = 20.0f

    @ConfigEntry.Category("training")
    @ConfigEntry.Gui.Tooltip
    var biteBackAttackDelay: Int = 10

    @ConfigEntry.Category("training")
    @ConfigEntry.Gui.Tooltip
    var biteBackReachBuffer: Double = 2.0


    // ========== Events (Zoomies / Howl) (New in 1.21.11 Parity) ==========
    @ConfigEntry.Category("events")
    @ConfigEntry.Gui.Tooltip
    var zoomiesChance: Double = 0.005 // 0.5% chance per tick check (not every tick, check logic)

    @ConfigEntry.Category("events")
    @ConfigEntry.Gui.Tooltip
    var zoomiesSpeedModifier: Double = 1.5

    @ConfigEntry.Category("events")
    @ConfigEntry.Gui.Tooltip
    var zoomiesRange: Int = 10

    @ConfigEntry.Category("events")
    @ConfigEntry.Gui.Tooltip
    var zoomiesVerticalRange: Int = 4

    @ConfigEntry.Category("events")
    @ConfigEntry.Gui.Tooltip
    var zoomiesNewSpotChance: Int = 10 

    @ConfigEntry.Category("events")
    @ConfigEntry.Gui.Tooltip
    var zoomiesLookAroundChance: Float = 0.05f

    @ConfigEntry.Category("events")
    @ConfigEntry.Gui.Tooltip
    var howlChance: Double = 0.01 // 1% check

    companion object {
        fun get(): BetterDogsConfig {
            return AutoConfig.getConfigHolder(BetterDogsConfig::class.java).config
        }
    }
}
