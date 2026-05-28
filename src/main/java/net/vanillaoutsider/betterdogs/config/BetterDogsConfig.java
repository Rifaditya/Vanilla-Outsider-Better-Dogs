// Verified against: BetterDogsConfig.java (26.1.2+)
package net.vanillaoutsider.betterdogs.config;

public class BetterDogsConfig {

    private static BetterDogsConfig INSTANCE = new BetterDogsConfig();
    private static java.nio.file.Path CONFIG_PATH;

    public static final int VERSION = 3470;
    public int configVersion = VERSION;

    public static synchronized void load(java.nio.file.Path configDir) {
        CONFIG_PATH = configDir.resolve("vanilla-outsider-better-dogs.json");
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("Better Dogs");
        INSTANCE = net.dasik.social.api.config.ConfigHelper.load(
                CONFIG_PATH,
                INSTANCE,
                BetterDogsConfig.class,
                VERSION,
                config -> config.configVersion,
                (config, ver) -> config.configVersion = ver,
                "/vanilla-outsider-better-dogs.json",
                logger
        );
    }

    public static synchronized void save() {
        if (CONFIG_PATH == null) return;
        net.dasik.social.api.config.ConfigHelper.save(
                CONFIG_PATH,
                INSTANCE,
                org.slf4j.LoggerFactory.getLogger("Better Dogs")
        );
    }

    // ========== Game Rule Defaults ==========
    // These values are used to initialize Game Rules for new worlds.
    // Gameplay logic now uses Game Rules (e.g. /gamerule bdAggroSpeed).

    public double globalSpeedBuff = 0.20;
    public boolean enableStormAnxiety = true;
    public boolean enableCliffSafety = true;
    public boolean enableFleeLowHealth = true;
    public boolean enableFriendlyFireProtection = true;
    public boolean enableDogsEatRawGroundFood = true;
    public boolean enableDogsEatCookedGroundFood = true;

    // ========== Aggressive Personality ==========
    public double aggressiveHealthBonus = -10.0;
    public double aggressiveSpeedModifier = 0.15;
    public double aggressiveDetectionRange = 20.0;
   // ...
    public double aggressiveChaseDistance = 50.0;
    public double aggressiveDamageModifier = 0.50;
    public float aggressiveFollowStart = 50.0f;
    public float aggressiveFollowStop = 2.0f;

    // ========== Pacifist Personality ==========
    public double pacifistHealthBonus = 20.0;
    public double pacifistSpeedModifier = -0.10;
    public double pacifistDamageModifier = -0.30;
    public double pacifistKnockbackModifier = 0.5;
    public float pacifistFollowStart = 5.0f;
    public float pacifistFollowStop = 2.0f;

    // ========== Normal Personality ==========
    public double normalHealthBonus = 0.0;
    public double normalSpeedModifier = 0.0;
    public double normalDamageModifier = 0.0;
    public float normalFollowStart = 10.0f;
    public float normalFollowStop = 2.0f;

    // ========== Healing ==========
    public int passiveHealIntervalTicks = 1200;
    public double passiveHealAmount = 1.0;
    public int combatHealDelayTicks = 60;

    // ========== Wild Wolves ==========
    public float wildHuntHealthThreshold = 0.5f;

    // ========== Taming ==========
    public int tamingChanceNormal = 60;
    public int tamingChanceAggressive = 20;
    public int tamingChancePacifist = 20;

    // ========== Breeding Genetics ==========
    public int breedingSameParentChance = 80;
    public int breedingSameParentOtherChance = 10;
    public int breedingMixedDominantChance = 40;
    public int breedingMixedRecessiveChance = 20;
    public int breedingDilutedNormalChance = 50;
    public int breedingDilutedOtherChance = 25;

    // ========== Gifts ==========
    public int giftCooldownMin = 12000;
    public int giftCooldownMax = 18000;
    public float giftTriggerChance = 0.01f;

    // ========== General/Misc expansions ==========
    public float stormAnxietyTriggerChance = 0.01f;
    public float stormWhineChance = 0.02f;
    
    // ========== Baby Wolf Behavior ==========
    public float babyFollowMultiplier = 2.0f;

    // ========== Baby Training System ==========
    public float babyMischiefChance = 2.5f;
    public int bloodFeudChance = 5;
    public int babyRetaliationChance = 75;
    
    // ========== Territorial Matrix (v3.4.6) ==========
    public int terrMatrixAAWar = 80;
    public int terrMatrixAAMerge = 10;
    public int terrMatrixANWar = 50;
    public int terrMatrixANMerge = 40;
    public int terrMatrixAPWar = 10;
    public int terrMatrixAPMerge = 50;
    public int terrMatrixNNWar = 20;
    public int terrMatrixNNMerge = 50;
    public int terrMatrixNPWar = 5;
    public int terrMatrixNPMerge = 45;
    public int terrMatrixPPWar = 0;
    public int terrMatrixPPMerge = 50;

    // ========== Pack Wander Scaling (v3.7.0) ==========
    public int tamedPackSpreadMultiplier = 120;
    public int tamedPackSpreadMax = 60;
    public int wildPackSpreadMultiplier = 80;
    public int wildPackSpreadMax = 40;

    // ========== Pack Separation (REMOVED v3.1.17) ==========
    // public boolean enablePackSeparation = true;


    public double followCatchUpSpeed = 1.5;
    public double followCatchUpThreshold = 10.0;
    public double teleportToOwnerSpread = 4.0;
    public double teleportMultiplier = 2.0;
    public float aggressiveDetectionBuffer = 2.0f;

    // ========== Events/Social Behavior ==========
    public int wanderlustRange = 40;
    public int wanderlustVerticalRange = 7;
    public double zoomiesSpeedModifier = 1.4;
    public int zoomiesNewSpotChance = 10;
    public float zoomiesLookAroundChance = 0.05f;
    public int zoomiesRange = 10;
    public int zoomiesVerticalRange = 4;

    public int stormAnxietyPaceRange = 3;
    public int stormAnxietyPaceVerticalRange = 2;
    public double stormAnxietyLookSpread = 10.0;
    public float stormAnxietyLookChance = 0.05f;
    public float stormAnxietyStopChance = 0.02f;

    // ========== Group Howl ==========
    public double howlSpreadRange = 20.0;

    public float playFightLookSpeed = 30.0f;
    public double playFightSpeedModifier = 1.0;

    // ========== Discipline & Training ==========
    public float biteBackLookSpeed = 30.0f;
    public double biteBackSpeedModifier = 1.2;
    public double biteBackReachBuffer = 4.0;
    public int biteBackAttackDelay = 20;

    public float correctionLookSpeed = 30.0f;
    public double correctionSpeedModifier = 1.0;
    public double correctionReachBuffer = 1.0;
    public double correctionSearchRange = 10.0;
    public int correctionDuration = 100;

    // ========== Hazards ==========
    public int maxSafeFall = 3;
    public int hazardCheckLimit = 5;
    public int hazardFallSearchLimit = 10;

    // Gift probabilities
    public int aggressiveGiftBone = 40;
    public int aggressiveGiftFlesh = 35;
    public int aggressiveGiftArrow = 15;
    public int pacifistGiftBerries = 30;
    public int pacifistGiftSeeds = 25;
    public int pacifistGiftFlower = 20;
    public int pacifistGiftMushroom = 15;

    public static BetterDogsConfig get() {
        return INSTANCE;
    }

    public double getGlobalSpeedBuff() { return globalSpeedBuff; }
    public boolean getEnableStormAnxiety() { return enableStormAnxiety; }
    public boolean getEnableCliffSafety() { return enableCliffSafety; }
    public boolean getEnableFleeLowHealth() { return enableFleeLowHealth; }
    public boolean getEnableFriendlyFireProtection() { return enableFriendlyFireProtection; }
    public boolean getEnableDogsEatRawGroundFood() { return enableDogsEatRawGroundFood; }
    public boolean getEnableDogsEatCookedGroundFood() { return enableDogsEatCookedGroundFood; }
    public double getAggressiveHealthBonus() { return aggressiveHealthBonus; }
    public double getAggressiveSpeedModifier() { return aggressiveSpeedModifier; }
    public double getAggressiveDetectionRange() { return aggressiveDetectionRange; }
    public double getAggressiveChaseDistance() { return aggressiveChaseDistance; }
    public double getAggressiveDamageModifier() { return aggressiveDamageModifier; }
    public float getAggressiveFollowStart() { return aggressiveFollowStart; }
    public float getAggressiveFollowStop() { return aggressiveFollowStop; }
    public double getPacifistHealthBonus() { return pacifistHealthBonus; }
    public double getPacifistSpeedModifier() { return pacifistSpeedModifier; }
    public double getPacifistDamageModifier() { return pacifistDamageModifier; }
    public double getPacifistKnockbackModifier() { return pacifistKnockbackModifier; }
    public float getPacifistFollowStart() { return pacifistFollowStart; }
    public float getPacifistFollowStop() { return pacifistFollowStop; }
    public double getNormalHealthBonus() { return normalHealthBonus; }
    public double getNormalSpeedModifier() { return normalSpeedModifier; }
    public double getNormalDamageModifier() { return normalDamageModifier; }
    public float getNormalFollowStart() { return normalFollowStart; }
    public float getNormalFollowStop() { return normalFollowStop; }
    public int getPassiveHealIntervalTicks() { return passiveHealIntervalTicks; }
    public double getPassiveHealAmount() { return passiveHealAmount; }
    public int getCombatHealDelayTicks() { return combatHealDelayTicks; }
    public int getBreedingSameParentChance() { return breedingSameParentChance; }
    public int getBreedingSameParentOtherChance() { return breedingSameParentOtherChance; }
    public int getBreedingMixedDominantChance() { return breedingMixedDominantChance; }
    public int getBreedingMixedRecessiveChance() { return breedingMixedRecessiveChance; }
    public int getBreedingDilutedNormalChance() { return breedingDilutedNormalChance; }
    public int getBreedingDilutedOtherChance() { return breedingDilutedOtherChance; }
    public float getBabyFollowMultiplier() { return babyFollowMultiplier; }
    public float getBabyMischiefChance() { return babyMischiefChance; }
    public int getBloodFeudChance() { return bloodFeudChance; }
    public int getBabyRetaliationChance() { return babyRetaliationChance; }

    public double getFollowCatchUpSpeed() { return followCatchUpSpeed; }
    public double getFollowCatchUpThreshold() { return followCatchUpThreshold; }
    public double getTeleportToOwnerSpread() { return teleportToOwnerSpread; }
    public double getTeleportMultiplier() { return teleportMultiplier; }
    public float getAggressiveDetectionBuffer() { return aggressiveDetectionBuffer; }
    public int getWanderlustRange() { return wanderlustRange; }
    public int getWanderlustVerticalRange() { return wanderlustVerticalRange; }
    public double getZoomiesSpeedModifier() { return zoomiesSpeedModifier; }
    public int getZoomiesNewSpotChance() { return zoomiesNewSpotChance; }
    public float getZoomiesLookAroundChance() { return zoomiesLookAroundChance; }
    public int getZoomiesRange() { return zoomiesRange; }
    public int getZoomiesVerticalRange() { return zoomiesVerticalRange; }
    public int getStormAnxietyPaceRange() { return stormAnxietyPaceRange; }
    public int getStormAnxietyPaceVerticalRange() { return stormAnxietyPaceVerticalRange; }
    public double getStormAnxietyLookSpread() { return stormAnxietyLookSpread; }
    public float getStormAnxietyLookChance() { return stormAnxietyLookChance; }
    public float getStormAnxietyStopChance() { return stormAnxietyStopChance; }
    public float getPlayFightLookSpeed() { return playFightLookSpeed; }
    public double getPlayFightSpeedModifier() { return playFightSpeedModifier; }
    public float getBiteBackLookSpeed() { return biteBackLookSpeed; }
    public double getBiteBackSpeedModifier() { return biteBackSpeedModifier; }
    public double getBiteBackReachBuffer() { return biteBackReachBuffer; }
    public int getBiteBackAttackDelay() { return biteBackAttackDelay; }
    public float getCorrectionLookSpeed() { return correctionLookSpeed; }
    public double getCorrectionSpeedModifier() { return correctionSpeedModifier; }
    public double getCorrectionReachBuffer() { return correctionReachBuffer; }
    public double getCorrectionSearchRange() { return correctionSearchRange; }
    public int getCorrectionDuration() { return correctionDuration; }
    public int getMaxSafeFall() { return maxSafeFall; }
    public int getHazardCheckLimit() { return hazardCheckLimit; }
    public int getHazardFallSearchLimit() { return hazardFallSearchLimit; }
    public double getHowlSpreadRange() { return howlSpreadRange; }
    
    public int getTerrMatrixAAWar() { return terrMatrixAAWar; }
    public int getTerrMatrixAAMerge() { return terrMatrixAAMerge; }
    public int getTerrMatrixANWar() { return terrMatrixANWar; }
    public int getTerrMatrixANMerge() { return terrMatrixANMerge; }
    public int getTerrMatrixAPWar() { return terrMatrixAPWar; }
    public int getTerrMatrixAPMerge() { return terrMatrixAPMerge; }
    public int getTerrMatrixNNWar() { return terrMatrixNNWar; }
    public int getTerrMatrixNNMerge() { return terrMatrixNNMerge; }
    public int getTerrMatrixNPWar() { return terrMatrixNPWar; }
    public int getTerrMatrixNPMerge() { return terrMatrixNPMerge; }
    public int getTerrMatrixPPWar() { return terrMatrixPPWar; }
    public int getTerrMatrixPPMerge() { return terrMatrixPPMerge; }

    public int getTamedPackSpreadMultiplier() { return tamedPackSpreadMultiplier; }
    public int getTamedPackSpreadMax() { return tamedPackSpreadMax; }
    public int getWildPackSpreadMultiplier() { return wildPackSpreadMultiplier; }
    public int getWildPackSpreadMax() { return wildPackSpreadMax; }
}
