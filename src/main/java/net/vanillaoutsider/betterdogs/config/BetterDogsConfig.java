package net.vanillaoutsider.betterdogs.config;

public class BetterDogsConfig {

    private static BetterDogsConfig INSTANCE = new BetterDogsConfig();
    private static final com.google.gson.Gson GSON = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
    private static java.nio.file.Path CONFIG_PATH;

    public int configVersion = 3117; // Version matching mod v3.1.17

    public static synchronized void load(java.nio.file.Path configDir) {
        CONFIG_PATH = configDir.resolve("betterdogs.json");
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("Better Dogs");
        
        if (!java.nio.file.Files.exists(CONFIG_PATH)) {
            logger.info("No config found, generating defaults");
            save();
            return;
        }

        try {
            // Pass 4: Size Guard (1MB limit)
            long size = java.nio.file.Files.size(CONFIG_PATH);
            if (size > 1024 * 1024) {
                logger.error("Config file too large ({} bytes). Using defaults for safety!", size);
                return;
            }

            // Pass 5: UTF-8 Enforcement
            try (java.io.Reader reader = java.nio.file.Files.newBufferedReader(CONFIG_PATH, java.nio.charset.StandardCharsets.UTF_8)) {
                BetterDogsConfig tempInstance = GSON.fromJson(reader, BetterDogsConfig.class);
                
                if (tempInstance != null) {
                    // Pass 3: Version Awareness & Backup
                    boolean needsSync = false;
                    if (tempInstance.configVersion < INSTANCE.configVersion) {
                        logger.info("Old config version {} detected. Backing up and updating to {}...", tempInstance.configVersion, INSTANCE.configVersion);
                        createBackup();
                        tempInstance.configVersion = INSTANCE.configVersion;
                        needsSync = true;
                    }

                    INSTANCE = tempInstance;
                    
                    // Pass 2 & 5: Additive Sync (Merge new defaults into existing file)
                    // If we added new fields to the class, GSON will write them out now.
                    if (needsSync) {
                        save();
                    } else {
                        // Optional: Always save to ensure stale fields are purged? 
                        // User specifically asked for "new config version update to change the user settings to default"
                        // wait, "i dont want the new config version update to change the user settings to default"
                        // Correct: save() will write back CURRENT java fields, which merges new ones and preserves old ones.
                        save(); 
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Critical error loading config. Preserving file and using defaults.", e);
        }
    }

    public static synchronized void save() {
        if (CONFIG_PATH == null) return;
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("Better Dogs");
        
        try {
            // Pass 2: Directory Guard
            java.nio.file.Files.createDirectories(CONFIG_PATH.getParent());

            // Pass 4: Atomic Swapping
            java.nio.file.Path tempPath = CONFIG_PATH.resolveSibling("betterdogs.json.tmp");
            
            try (java.io.Writer writer = java.nio.file.Files.newBufferedWriter(tempPath, java.nio.charset.StandardCharsets.UTF_8)) {
                GSON.toJson(INSTANCE, writer);
            }

            try {
                java.nio.file.Files.move(tempPath, CONFIG_PATH, java.nio.file.StandardCopyOption.REPLACE_EXISTING, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
            } catch (java.io.IOException e) {
                // Pass 5: Fallback for non-atomic FS
                java.nio.file.Files.move(tempPath, CONFIG_PATH, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            logger.error("Failed to save config safely!", e);
        }
    }

    private static void createBackup() {
        try {
            java.nio.file.Path backupPath = CONFIG_PATH.resolveSibling("betterdogs.json.bak");
            java.nio.file.Files.copy(CONFIG_PATH, backupPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger("Better Dogs").error("Failed to create config backup!", e);
        }
    }

    // ========== Game Rule Defaults ==========
    // These values are used to initialize Game Rules for new worlds.
    // Gameplay logic now uses Game Rules (e.g. /gamerule bdAggroSpeed).

    public double globalSpeedBuff = 0.20;
    public boolean enableStormAnxiety = true;
    public boolean enableCliffSafety = true;
    public boolean enableFriendlyFireProtection = true;

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
    public boolean getEnableFriendlyFireProtection() { return enableFriendlyFireProtection; }
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
}
