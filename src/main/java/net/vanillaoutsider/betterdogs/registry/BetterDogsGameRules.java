// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.registry;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public class BetterDogsGameRules {

    public static final GameRuleCategory BETTER_DOGS = DynamicGameRuleManager
            .registerCategory(Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "better_dogs"));

    // General & Environmental Safety
    public static GameRule<Boolean> BD_STORM_ANXIETY;
    public static GameRule<Boolean> BD_ACTIONBAR_FEEDBACK;
    public static GameRule<Boolean> BD_CREEPER_AWARENESS;
    public static GameRule<Boolean> BD_CREEPER_EVASION_ENABLED;
    public static GameRule<Boolean> BD_CLIFF_SAFETY;
    public static GameRule<Boolean> BD_FLEE_LOW_HEALTH;
    public static GameRule<Boolean> BD_DOGS_EAT_RAW_FOOD;
    public static GameRule<Boolean> BD_DOGS_EAT_COOKED_FOOD;
    public static GameRule<Boolean> BD_ENABLE_REFUSE_GROUND_FOOD;
    public static GameRule<Integer> BD_REFUSE_GROUND_FOOD_CHANCE;
    public static GameRule<Boolean> BD_DEBUGGING;
    public static GameRule<Boolean> BD_NEMESIS_SYSTEM;
    public static GameRule<Integer> BD_NEMESIS_DURATION_DAYS;
    public static GameRule<Boolean> BD_FAVORITE_TREATS;
    public static GameRule<Boolean> BD_PACK_FLANKING_TACTICS;
    public static GameRule<Boolean> BD_FLANKING_RAYCAST_CHECK;
    public static GameRule<Boolean> BD_SYNC_OWNER_TELEPORT;
    public static GameRule<Boolean> BD_FAST_TRAVEL_CATCHUP;
    public static GameRule<Boolean> BD_HORN_COMMANDS_ENABLED;
    public static GameRule<Integer> BD_HORN_COMMAND_RANGE;
    public static GameRule<Integer> BD_HORN_PATHING_TIMEOUT;
    public static GameRule<Integer> BD_HORN_OVERRIDE_DURATION;
    public static GameRule<Boolean> BD_FETCH_ENABLED;
    public static GameRule<Integer> BD_FETCH_RANGE;
    public static GameRule<Boolean> BD_ZOOMIES_ENABLED;
    public static GameRule<Integer> BD_ZOOMIES_DURATION_TICKS;
    public static GameRule<Boolean> BD_WOLVES_SEEK_WATER_ON_FIRE;
    public static GameRule<Boolean> BD_WOLVES_BREAK_SIT_ON_FIRE;
    public static GameRule<Boolean> BD_ALLOW_UNRESTRICTED_RIDING;
    public static GameRule<Integer> BD_TAMED_PACK_SPREAD_MULTIPLIER;
    public static GameRule<Integer> BD_TAMED_PACK_SPREAD_MAX;

    // Social / Play Sparring
    public static GameRule<Boolean> BD_DOG_PLAY_FIGHTING;
    public static GameRule<Integer> BD_PLAY_PENALTY_MINUTES;
    public static GameRule<Boolean> BD_DOG_PLAY_CHOREOGRAPHY_ENABLED;
    public static GameRule<Integer> BD_PLAY_CHOREOGRAPHY_FREQUENCY;
    public static GameRule<Integer> BD_MAX_PLAY_DISTANCE;

    // Player Protection
    public static GameRule<Boolean> BD_FRIENDLY_FIRE;

    // Aggressive Personality
    public static GameRule<Integer> BD_AGGRO_HEALTH;
    public static GameRule<Integer> BD_AGGRO_SPEED_PCT;
    public static GameRule<Integer> BD_AGGRO_DMG_PCT;
    public static GameRule<Integer> BD_AGGRO_FOLLOW_START;
    public static GameRule<Integer> BD_AGGRO_CHASE_DIST;
    public static GameRule<Integer> BD_AGGRO_DETECT_RANGE;
    public static GameRule<Integer> BD_AGGRO_FLEE_CHANCE;

    // Pacifist Personality
    public static GameRule<Integer> BD_PACI_HEALTH;
    public static GameRule<Integer> BD_PACI_SPEED_PCT;
    public static GameRule<Integer> BD_PACI_DMG_PCT;
    public static GameRule<Integer> BD_PACI_KNOCKBACK_PCT;
    public static GameRule<Integer> BD_PACI_FOLLOW_START;
    public static GameRule<Integer> BD_PACI_FLEE_CHANCE;

    // Normal Personality
    public static GameRule<Integer> BD_NORMAL_FOLLOW_START;
    public static GameRule<Integer> BD_NORMAL_SPEED_PCT;
    public static GameRule<Integer> BD_NORMAL_DMG_PCT;
    public static GameRule<Integer> BD_NORMAL_HEALTH;
    public static GameRule<Integer> BD_NORMAL_FLEE_CHANCE;

    // Misc & Scaling
    public static GameRule<Integer> BD_BABY_MISCHIEF_PERMILLE;
    public static GameRule<Integer> BD_HOWL_CHANCE;
    public static GameRule<Integer> BD_PACK_SPREAD;
    public static GameRule<Integer> BD_GIFT_FEED_THRESHOLD;
    public static GameRule<Integer> BD_GIFT_INTERACTION_COOLDOWN;
    public static GameRule<Boolean> BD_DEMERIT_ACCIDENTAL_ATTACKS;
    public static GameRule<Integer> BD_WOLF_MIN_SCALE_PERCENT;
    public static GameRule<Integer> BD_WOLF_MAX_SCALE_PERCENT;

    // Correction & Retaliation & Hunting
    public static GameRule<Integer> BD_BLOOD_FEUD_PERCENT;
    public static GameRule<Integer> BD_BABY_RETALIATE_PERCENT;
    public static GameRule<Integer> BD_WILD_HUNT_HEALTH_THRESHOLD;

    // Territorial Matrix
    public static GameRule<Boolean> BD_TERRITORIAL_RIVALRY;
    public static GameRule<Integer> BD_TERR_AA_WAR;
    public static GameRule<Integer> BD_TERR_AA_MERGE;
    public static GameRule<Integer> BD_TERR_AN_WAR;
    public static GameRule<Integer> BD_TERR_AN_MERGE;
    public static GameRule<Integer> BD_TERR_AP_WAR;
    public static GameRule<Integer> BD_TERR_AP_MERGE;
    public static GameRule<Integer> BD_TERR_NN_WAR;
    public static GameRule<Integer> BD_TERR_NN_MERGE;
    public static GameRule<Integer> BD_TERR_NP_WAR;
    public static GameRule<Integer> BD_TERR_NP_MERGE;
    public static GameRule<Integer> BD_TERR_PP_WAR;
    public static GameRule<Integer> BD_TERR_PP_MERGE;
    public static GameRule<Integer> BD_TERRITORIAL_FATAL_CHANCE;
    public static GameRule<Boolean> BD_TERRITORIAL_EXCLUSIVE_DISPUTES;
    public static GameRule<Boolean> BD_WILD_PERSONALITY_BEHAVIOR;
    public static GameRule<Integer> BD_TERRITORIAL_SEARCH_RADIUS;

    // Spawning & Population
    public static GameRule<Integer> BD_WOLF_PACK_CLUSTER_SIZE;
    public static GameRule<Integer> BD_WOLF_SPAWN_DENSITY_BOOST;
    public static GameRule<Integer> BD_WOLF_SPAWN_MULTIPLIER_PCT;
    public static GameRule<Integer> BD_WOLF_SPAWN_GROUP_MIN;
    public static GameRule<Integer> BD_WOLF_SPAWN_GROUP_MAX;
    public static GameRule<Boolean> BD_WOLF_SPAWN_EXPANDED_BIOMES;
    public static GameRule<Boolean> BD_DYNAMIC_CLIMATE_VARIANTS;

    // Physical Canine Genetics
    public static GameRule<Boolean> BD_DOG_GENETICS_ENABLED;
    public static GameRule<Integer> BD_MAX_SCALE_DEVIATION;

    // Personality Archetypes (BL-BD-013)
    public static GameRule<Boolean> BD_ARCHETYPES_ENABLED;
    public static GameRule<Integer> BD_ARCHETYPE_DISTRIBUTION_MODE;

    // Canine Explosive Reflex (BL-BD-006)
    public static GameRule<Boolean> BD_CANINE_EXPLOSIVE_REFLEX_ENABLED;
    public static GameRule<Integer> BD_HAZARD_FLEE_RADIUS;
    public static GameRule<Integer> BD_FLEE_SPEED_MULTIPLIER;

    // Ballistic Evasion Reflex (BL-BD-020)
    public static GameRule<Boolean> BD_BALLISTIC_EVASION_ENABLED;
    public static GameRule<Integer> BD_EVASION_REACTION_TICKS;
    public static GameRule<Integer> BD_EVASION_LATERAL_IMPULSE;

    // Breeding & Genetics
    public static GameRule<Integer> BD_SPAWN_NORMAL_PERCENT;
    public static GameRule<Integer> BD_SPAWN_AGGRO_PERCENT;
    public static GameRule<Integer> BD_SPAWN_PACI_PERCENT;
    public static GameRule<Integer> BD_BREED_SAME_CHANCE;
    public static GameRule<Integer> BD_BREED_SAME_OTHER_CHANCE;
    public static GameRule<Integer> BD_BREED_MIXED_DOMINANT_CHANCE;
    public static GameRule<Integer> BD_BREED_MIXED_RECESSIVE_CHANCE;
    public static GameRule<Integer> BD_BREED_DILUTED_NORMAL_CHANCE;
    public static GameRule<Integer> BD_BREED_DILUTED_OTHER_CHANCE;
    public static GameRule<Integer> BD_WOLF_LITTER_MAX_SIZE;
    public static GameRule<Integer> BD_WOLF_LITTER_EXTRA_CHANCE;

    // Guard Mode
    public static GameRule<Boolean> BD_PACIFIST_GUARD_BUFFS;
    public static GameRule<Boolean> BD_ENABLE_INBRED_CURING;
    public static GameRule<Boolean> BD_SHOW_RUNT_PARTICLES;
    public static GameRule<Integer> BD_PARTICLE_DENSITY;
    public static GameRule<Integer> BD_GUARD_PATROL_RANGE_AGGRESSIVE;
    public static GameRule<Integer> BD_GUARD_PATROL_RANGE_NORMAL;
    public static GameRule<Integer> BD_GUARD_PATROL_RANGE_PACIFIST;

    // Pack Spread Scaling
    public static GameRule<Integer> BD_WILD_PACK_SPREAD_MULTIPLIER;
    public static GameRule<Integer> BD_WILD_PACK_SPREAD_MAX;

    public static void register() {
        init();
    }

    public static void init() {
        // General
        BD_STORM_ANXIETY = registerBoolean("vanilla-outsider-better-dogs:bd_storm_anxiety", BETTER_DOGS, true);
        BD_ACTIONBAR_FEEDBACK = registerBoolean("vanilla-outsider-better-dogs:bd_actionbar_feedback", BETTER_DOGS, false);
        BD_CREEPER_AWARENESS = registerBoolean("vanilla-outsider-better-dogs:bd_creeper_awareness", BETTER_DOGS, true);
        BD_CREEPER_EVASION_ENABLED = registerBoolean("vanilla-outsider-better-dogs:bd_creeper_evasion_enabled", BETTER_DOGS, true);
        BD_CLIFF_SAFETY = registerBoolean("vanilla-outsider-better-dogs:bd_cliff_safety", BETTER_DOGS, true);
        BD_FLEE_LOW_HEALTH = registerBoolean("vanilla-outsider-better-dogs:bd_flee_low_health", BETTER_DOGS, true);
        BD_DOGS_EAT_RAW_FOOD = registerBoolean("vanilla-outsider-better-dogs:bd_dogs_eat_raw_food", BETTER_DOGS, true);
        BD_DOGS_EAT_COOKED_FOOD = registerBoolean("vanilla-outsider-better-dogs:bd_dogs_eat_cooked_food", BETTER_DOGS, true);
        BD_ENABLE_REFUSE_GROUND_FOOD = registerBoolean("vanilla-outsider-better-dogs:bd_enable_refuse_ground_food", BETTER_DOGS, true);
        BD_REFUSE_GROUND_FOOD_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_refuse_ground_food_chance", BETTER_DOGS, 30);
        BD_DEBUGGING = registerBoolean("vanilla-outsider-better-dogs:bd_debugging", BETTER_DOGS, false);
        BD_NEMESIS_SYSTEM = registerBoolean("vanilla-outsider-better-dogs:bd_nemesis_system", BETTER_DOGS, true);
        BD_NEMESIS_DURATION_DAYS = registerInteger("vanilla-outsider-better-dogs:bd_nemesis_duration_days", BETTER_DOGS, 3);
        BD_FAVORITE_TREATS = registerBoolean("vanilla-outsider-better-dogs:bd_favorite_treats", BETTER_DOGS, true);
        BD_PACK_FLANKING_TACTICS = registerBoolean("vanilla-outsider-better-dogs:bd_pack_flanking_tactics", BETTER_DOGS, true);
        BD_FLANKING_RAYCAST_CHECK = registerBoolean("vanilla-outsider-better-dogs:bd_flanking_raycast_check", BETTER_DOGS, true);
        BD_SYNC_OWNER_TELEPORT = registerBoolean("vanilla-outsider-better-dogs:bd_sync_owner_teleport", BETTER_DOGS, true);
        BD_FAST_TRAVEL_CATCHUP = registerBoolean("vanilla-outsider-better-dogs:bd_fast_travel_catchup", BETTER_DOGS, true);
        BD_HORN_COMMANDS_ENABLED = registerBoolean("vanilla-outsider-better-dogs:bd_horn_commands_enabled", BETTER_DOGS, true);
        BD_HORN_COMMAND_RANGE = registerInteger("vanilla-outsider-better-dogs:bd_horn_command_range", BETTER_DOGS, 64);
        BD_HORN_PATHING_TIMEOUT = registerInteger("vanilla-outsider-better-dogs:bd_horn_pathing_timeout", BETTER_DOGS, 300);
        BD_HORN_OVERRIDE_DURATION = registerInteger("vanilla-outsider-better-dogs:bd_horn_override_duration", BETTER_DOGS, 600);
        BD_FETCH_ENABLED = registerBoolean("vanilla-outsider-better-dogs:bd_fetch_enabled", BETTER_DOGS, true);
        BD_FETCH_RANGE = registerInteger("vanilla-outsider-better-dogs:bd_fetch_range", BETTER_DOGS, 16);
        BD_ZOOMIES_ENABLED = registerBoolean("vanilla-outsider-better-dogs:bd_zoomies_enabled", BETTER_DOGS, true);
        BD_ZOOMIES_DURATION_TICKS = registerInteger("vanilla-outsider-better-dogs:bd_zoomies_duration_ticks", BETTER_DOGS, 160);
        BD_WOLVES_SEEK_WATER_ON_FIRE = registerBoolean("vanilla-outsider-better-dogs:bd_wolves_seek_water_on_fire", BETTER_DOGS, true);
        BD_WOLVES_BREAK_SIT_ON_FIRE = registerBoolean("vanilla-outsider-better-dogs:bd_wolves_break_sit_on_fire", BETTER_DOGS, true);
        BD_ALLOW_UNRESTRICTED_RIDING = registerBoolean("vanilla-outsider-better-dogs:bd_allow_unrestricted_dog_riding", BETTER_DOGS, false);
        BD_TAMED_PACK_SPREAD_MULTIPLIER = registerInteger("vanilla-outsider-better-dogs:bd_tamed_pack_spread_multiplier", BETTER_DOGS, 100);
        BD_TAMED_PACK_SPREAD_MAX = registerInteger("vanilla-outsider-better-dogs:bd_tamed_pack_spread_max", BETTER_DOGS, 50);

        // Social / Play Sparring
        BD_DOG_PLAY_FIGHTING = registerBoolean("vanilla-outsider-better-dogs:bd_dog_play_fighting", BETTER_DOGS, true);
        BD_PLAY_PENALTY_MINUTES = DynamicGameRuleManager.integerRule("vanilla-outsider-better-dogs:bd_play_penalty_minutes", BETTER_DOGS, 10)
                .range(0, Integer.MAX_VALUE).register();
        BD_DOG_PLAY_CHOREOGRAPHY_ENABLED = registerBoolean("vanilla-outsider-better-dogs:bd_dog_play_choreography_enabled", BETTER_DOGS, true);
        BD_PLAY_CHOREOGRAPHY_FREQUENCY = registerInteger("vanilla-outsider-better-dogs:bd_play_choreography_frequency", BETTER_DOGS, 2400, 20, 72000);
        BD_MAX_PLAY_DISTANCE = registerInteger("vanilla-outsider-better-dogs:bd_max_play_distance", BETTER_DOGS, 16, 4, 128);

        DynamicGameRuleManager.getDynamicRules().put("betterdogs:bd_dog_play_choreography_enabled", BD_DOG_PLAY_CHOREOGRAPHY_ENABLED);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:bd_play_choreography_frequency", BD_PLAY_CHOREOGRAPHY_FREQUENCY);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:bd_max_play_distance", BD_MAX_PLAY_DISTANCE);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:dogPlayChoreographyEnabled", BD_DOG_PLAY_CHOREOGRAPHY_ENABLED);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:playChoreographyFrequency", BD_PLAY_CHOREOGRAPHY_FREQUENCY);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:maxPlayDistance", BD_MAX_PLAY_DISTANCE);

        // Player
        BD_FRIENDLY_FIRE = registerBoolean("vanilla-outsider-better-dogs:bd_friendly_fire_protection", BETTER_DOGS, true);

        // Aggressive
        BD_AGGRO_HEALTH = registerInteger("vanilla-outsider-better-dogs:bd_aggressive_health", BETTER_DOGS, -10);
        BD_AGGRO_SPEED_PCT = registerInteger("vanilla-outsider-better-dogs:bd_aggro_speed_percent", BETTER_DOGS, 15);
        BD_AGGRO_DMG_PCT = registerInteger("vanilla-outsider-better-dogs:bd_aggro_dmg_percent", BETTER_DOGS, 15);
        BD_AGGRO_FOLLOW_START = registerInteger("vanilla-outsider-better-dogs:bd_aggro_follow_start", BETTER_DOGS, 50);
        BD_AGGRO_CHASE_DIST = registerInteger("vanilla-outsider-better-dogs:bd_aggro_chase_dist", BETTER_DOGS, 50);
        BD_AGGRO_DETECT_RANGE = registerInteger("vanilla-outsider-better-dogs:bd_aggro_detect_range", BETTER_DOGS, 20);
        BD_AGGRO_FLEE_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_aggro_flee_chance", BETTER_DOGS, 10);

        // Pacifist
        BD_PACI_HEALTH = registerInteger("vanilla-outsider-better-dogs:bd_paci_health", BETTER_DOGS, 20);
        BD_PACI_SPEED_PCT = registerInteger("vanilla-outsider-better-dogs:bd_paci_speed_percent", BETTER_DOGS, -10);
        BD_PACI_DMG_PCT = registerInteger("vanilla-outsider-better-dogs:bd_paci_dmg_percent", BETTER_DOGS, -15);
        BD_PACI_KNOCKBACK_PCT = registerInteger("vanilla-outsider-better-dogs:bd_paci_knockback_percent", BETTER_DOGS, 50);
        BD_PACI_FOLLOW_START = registerInteger("vanilla-outsider-better-dogs:bd_paci_follow_start", BETTER_DOGS, 5);
        BD_PACI_FLEE_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_paci_flee_chance", BETTER_DOGS, 100);

        // Normal
        BD_NORMAL_FOLLOW_START = registerInteger("vanilla-outsider-better-dogs:bd_normal_follow_start", BETTER_DOGS, 10);
        BD_NORMAL_SPEED_PCT = registerInteger("vanilla-outsider-better-dogs:bd_normal_speed_percent", BETTER_DOGS, 0);
        BD_NORMAL_DMG_PCT = registerInteger("vanilla-outsider-better-dogs:bd_normal_dmg_percent", BETTER_DOGS, 0);
        BD_NORMAL_HEALTH = registerInteger("vanilla-outsider-better-dogs:bd_normal_health", BETTER_DOGS, 0);
        BD_NORMAL_FLEE_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_normal_flee_chance", BETTER_DOGS, 50);

        // Misc
        BD_BABY_MISCHIEF_PERMILLE = registerInteger("vanilla-outsider-better-dogs:bd_baby_mischief_permille", BETTER_DOGS, 25);
        BD_HOWL_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_howl_chance", BETTER_DOGS, 10);
        BD_PACK_SPREAD = registerInteger("vanilla-outsider-better-dogs:bd_pack_spread", BETTER_DOGS, 8);
        BD_GIFT_FEED_THRESHOLD = registerInteger("vanilla-outsider-better-dogs:bd_gift_feed_threshold", BETTER_DOGS, 10);
        BD_GIFT_INTERACTION_COOLDOWN = registerInteger("vanilla-outsider-better-dogs:bd_gift_interaction_cooldown", BETTER_DOGS, 6000);
        BD_DEMERIT_ACCIDENTAL_ATTACKS = registerBoolean("vanilla-outsider-better-dogs:bd_demerit_accidental_attacks", BETTER_DOGS, true);
        BD_WOLF_MIN_SCALE_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_wolf_min_scale_percent", BETTER_DOGS, 70);
        BD_WOLF_MAX_SCALE_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_wolf_max_scale_percent", BETTER_DOGS, 145);

        // Correction & Hunting
        BD_BLOOD_FEUD_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_blood_feud_percent", BETTER_DOGS, 5);
        BD_BABY_RETALIATE_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_baby_retaliate_percent", BETTER_DOGS, 50);
        BD_WILD_HUNT_HEALTH_THRESHOLD = registerInteger("vanilla-outsider-better-dogs:bd_wild_hunt_health_threshold", BETTER_DOGS, 50);

        // Territorial
        BD_TERRITORIAL_RIVALRY = registerBoolean("vanilla-outsider-better-dogs:bd_territorial_rivalry", BETTER_DOGS, true);
        BD_TERR_AA_WAR = registerInteger("vanilla-outsider-better-dogs:bd_terr_aa_war", BETTER_DOGS, 80);
        BD_TERR_AA_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_terr_aa_merge", BETTER_DOGS, 0);
        BD_TERR_AN_WAR = registerInteger("vanilla-outsider-better-dogs:bd_terr_an_war", BETTER_DOGS, 50);
        BD_TERR_AN_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_terr_an_merge", BETTER_DOGS, 0);
        BD_TERR_AP_WAR = registerInteger("vanilla-outsider-better-dogs:bd_terr_ap_war", BETTER_DOGS, 20);
        BD_TERR_AP_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_terr_ap_merge", BETTER_DOGS, 80);
        BD_TERR_NN_WAR = registerInteger("vanilla-outsider-better-dogs:bd_terr_nn_war", BETTER_DOGS, 20);
        BD_TERR_NN_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_terr_nn_merge", BETTER_DOGS, 80);
        BD_TERR_NP_WAR = registerInteger("vanilla-outsider-better-dogs:bd_terr_np_war", BETTER_DOGS, 0);
        BD_TERR_NP_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_terr_np_merge", BETTER_DOGS, 100);
        BD_TERR_PP_WAR = registerInteger("vanilla-outsider-better-dogs:bd_terr_pp_war", BETTER_DOGS, 0);
        BD_TERR_PP_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_terr_pp_merge", BETTER_DOGS, 100);
        BD_TERRITORIAL_FATAL_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_fatal_chance", BETTER_DOGS, 10);
        BD_TERRITORIAL_EXCLUSIVE_DISPUTES = registerBoolean("vanilla-outsider-better-dogs:bd_territorial_exclusive_disputes", BETTER_DOGS, true);
        BD_WILD_PERSONALITY_BEHAVIOR = registerBoolean("vanilla-outsider-better-dogs:bd_wild_personality_behavior", BETTER_DOGS, true);
        BD_TERRITORIAL_SEARCH_RADIUS = registerInteger("vanilla-outsider-better-dogs:bd_territorial_search_radius", BETTER_DOGS, 32);

        // Spawning
        BD_WOLF_PACK_CLUSTER_SIZE = registerInteger("vanilla-outsider-better-dogs:bd_wolf_pack_cluster_size", BETTER_DOGS, 6);
        BD_WOLF_SPAWN_DENSITY_BOOST = registerInteger("vanilla-outsider-better-dogs:bd_wolf_spawn_density_boost", BETTER_DOGS, 100);
        BD_WOLF_SPAWN_MULTIPLIER_PCT = registerInteger("vanilla-outsider-better-dogs:bd_wolf_spawn_multiplier_percent", BETTER_DOGS, 150);
        BD_WOLF_SPAWN_GROUP_MIN = registerInteger("vanilla-outsider-better-dogs:bd_wolf_spawn_group_min", BETTER_DOGS, 4);
        BD_WOLF_SPAWN_GROUP_MAX = registerInteger("vanilla-outsider-better-dogs:bd_wolf_spawn_group_max", BETTER_DOGS, 8);
        BD_WOLF_SPAWN_EXPANDED_BIOMES = registerBoolean("vanilla-outsider-better-dogs:bd_wolf_spawn_expanded_biomes", BETTER_DOGS, true);
        BD_DYNAMIC_CLIMATE_VARIANTS = registerBoolean("vanilla-outsider-better-dogs:bd_dynamic_climate_variants", BETTER_DOGS, true);

        // Physical Canine Genetics
        BD_DOG_GENETICS_ENABLED = registerBoolean("vanilla-outsider-better-dogs:bd_dog_genetics_enabled", BETTER_DOGS, true);
        BD_MAX_SCALE_DEVIATION = registerInteger("vanilla-outsider-better-dogs:bd_max_scale_deviation", BETTER_DOGS, 30, 0, 100);

        DynamicGameRuleManager.getDynamicRules().put("betterdogs:bd_dog_genetics_enabled", BD_DOG_GENETICS_ENABLED);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:bd_max_scale_deviation", BD_MAX_SCALE_DEVIATION);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:dogGeneticsEnabled", BD_DOG_GENETICS_ENABLED);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:maxScaleDeviation", BD_MAX_SCALE_DEVIATION);

        // Personality Archetypes (BL-BD-013)
        BD_ARCHETYPES_ENABLED = registerBoolean("vanilla-outsider-better-dogs:bd_personality_archetypes_enabled", BETTER_DOGS, true);
        BD_ARCHETYPE_DISTRIBUTION_MODE = registerInteger("vanilla-outsider-better-dogs:bd_personality_distribution_mode", BETTER_DOGS, 0, 0, 1);

        DynamicGameRuleManager.getDynamicRules().put("betterdogs:bd_personality_archetypes_enabled", BD_ARCHETYPES_ENABLED);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:bd_personality_distribution_mode", BD_ARCHETYPE_DISTRIBUTION_MODE);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:personalityArchetypesEnabled", BD_ARCHETYPES_ENABLED);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:personalityDistributionMode", BD_ARCHETYPE_DISTRIBUTION_MODE);

        // Canine Explosive Reflex (BL-BD-006)
        BD_CANINE_EXPLOSIVE_REFLEX_ENABLED = registerBoolean("betterdogs:bd_canine_explosive_reflex_enabled", BETTER_DOGS, true);
        BD_HAZARD_FLEE_RADIUS = registerInteger("betterdogs:bd_hazard_flee_radius", BETTER_DOGS, 14, 4, 64);
        BD_FLEE_SPEED_MULTIPLIER = registerInteger("betterdogs:bd_flee_speed_multiplier", BETTER_DOGS, 170, 100, 300);

        DynamicGameRuleManager.getDynamicRules().put("betterdogs:bd_canine_explosive_reflex_enabled", BD_CANINE_EXPLOSIVE_REFLEX_ENABLED);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:bd_hazard_flee_radius", BD_HAZARD_FLEE_RADIUS);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:bd_flee_speed_multiplier", BD_FLEE_SPEED_MULTIPLIER);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:canineExplosiveReflexEnabled", BD_CANINE_EXPLOSIVE_REFLEX_ENABLED);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:hazardFleeRadius", BD_HAZARD_FLEE_RADIUS);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:fleeSpeedMultiplier", BD_FLEE_SPEED_MULTIPLIER);

        // Ballistic Evasion Reflex (BL-BD-020)
        BD_BALLISTIC_EVASION_ENABLED = registerBoolean("betterdogs:bd_ballistic_evasion_enabled", BETTER_DOGS, true);
        BD_EVASION_REACTION_TICKS = registerInteger("betterdogs:bd_evasion_reaction_ticks", BETTER_DOGS, 20, 5, 60);
        BD_EVASION_LATERAL_IMPULSE = registerInteger("betterdogs:bd_evasion_lateral_impulse", BETTER_DOGS, 85, 20, 250);

        DynamicGameRuleManager.getDynamicRules().put("betterdogs:bd_ballistic_evasion_enabled", BD_BALLISTIC_EVASION_ENABLED);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:bd_evasion_reaction_ticks", BD_EVASION_REACTION_TICKS);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:bd_evasion_lateral_impulse", BD_EVASION_LATERAL_IMPULSE);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:ballisticEvasionEnabled", BD_BALLISTIC_EVASION_ENABLED);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:evasionReactionTicks", BD_EVASION_REACTION_TICKS);
        DynamicGameRuleManager.getDynamicRules().put("betterdogs:evasionLateralImpulse", BD_EVASION_LATERAL_IMPULSE);

        // Breeding & Genetics
        BD_SPAWN_NORMAL_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_spawn_normal_percent", BETTER_DOGS, 60);
        BD_SPAWN_AGGRO_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_spawn_aggro_percent", BETTER_DOGS, 20);
        BD_SPAWN_PACI_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_spawn_paci_percent", BETTER_DOGS, 20);
        BD_BREED_SAME_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_same_chance", BETTER_DOGS, 80);
        BD_BREED_SAME_OTHER_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_same_other_chance", BETTER_DOGS, 10);
        BD_BREED_MIXED_DOMINANT_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_mixed_dominant_chance", BETTER_DOGS, 40);
        BD_BREED_MIXED_RECESSIVE_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_mixed_recessive_chance", BETTER_DOGS, 40);
        BD_BREED_DILUTED_NORMAL_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_diluted_normal_chance", BETTER_DOGS, 50);
        BD_BREED_DILUTED_OTHER_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_diluted_other_chance", BETTER_DOGS, 25);
        BD_WOLF_LITTER_MAX_SIZE = registerInteger("vanilla-outsider-better-dogs:bd_wolf_litter_max_size", BETTER_DOGS, 4);
        BD_WOLF_LITTER_EXTRA_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_wolf_litter_extra_chance", BETTER_DOGS, 20);

        // Guard Mode
        BD_PACIFIST_GUARD_BUFFS = registerBoolean("vanilla-outsider-better-dogs:bd_pacifist_guard_buffs", BETTER_DOGS, false);
        BD_ENABLE_INBRED_CURING = registerBoolean("vanilla-outsider-better-dogs:bd_enable_inbred_curing", BETTER_DOGS, true);
        BD_SHOW_RUNT_PARTICLES = registerBoolean("vanilla-outsider-better-dogs:bd_show_runt_particles", BETTER_DOGS, false);
        BD_PARTICLE_DENSITY = registerInteger("vanilla-outsider-better-dogs:bd_particle_density", BETTER_DOGS, 2);
        BD_GUARD_PATROL_RANGE_AGGRESSIVE = registerInteger("vanilla-outsider-better-dogs:bd_guard_patrol_range_aggressive", BETTER_DOGS, 12);
        BD_GUARD_PATROL_RANGE_NORMAL = registerInteger("vanilla-outsider-better-dogs:bd_guard_patrol_range_normal", BETTER_DOGS, 0);
        BD_GUARD_PATROL_RANGE_PACIFIST = registerInteger("vanilla-outsider-better-dogs:bd_guard_patrol_range_pacifist", BETTER_DOGS, 3);

        // Pack Spread Scaling
        BD_WILD_PACK_SPREAD_MULTIPLIER = registerInteger("vanilla-outsider-better-dogs:bd_wild_pack_spread_multiplier", BETTER_DOGS, 80);
        BD_WILD_PACK_SPREAD_MAX = registerInteger("vanilla-outsider-better-dogs:bd_wild_pack_spread_max", BETTER_DOGS, 40);
    }

    private static GameRule<Boolean> registerBoolean(String id, GameRuleCategory category, boolean defaultValue) {
        return DynamicGameRuleManager.booleanRule(id, category, defaultValue).register();
    }

    private static GameRule<Integer> registerInteger(String id, GameRuleCategory category, int defaultValue) {
        return DynamicGameRuleManager.integerRule(id, category, defaultValue).register();
    }

    private static GameRule<Integer> registerInteger(String id, GameRuleCategory category, int defaultValue, int min, int max) {
        return DynamicGameRuleManager.integerRule(id, category, defaultValue).range(min, max).register();
    }

    public static boolean isHornCommandsEnabled(Level level) {
        return getBoolean(level, BD_HORN_COMMANDS_ENABLED, true);
    }

    public static boolean isFetchEnabled(Level level) {
        return getBoolean(level, BD_FETCH_ENABLED, true);
    }

    public static int getFetchRange(Level level) {
        return getInt(level, BD_FETCH_RANGE, 16);
    }

    public static boolean isZoomiesEnabled(Level level) {
        return getBoolean(level, BD_ZOOMIES_ENABLED, true);
    }

    public static int getZoomiesDurationTicks(Level level) {
        return getInt(level, BD_ZOOMIES_DURATION_TICKS, 160);
    }

    public static boolean isDogPlayFightingEnabled(Level level) {
        return getBoolean(level, BD_DOG_PLAY_FIGHTING, true);
    }

    public static int getPlayPenaltyMinutes(Level level) {
        return getInt(level, BD_PLAY_PENALTY_MINUTES, 10);
    }

    public static boolean isDogPlayChoreographyEnabled(Level level) {
        return getBoolean(level, BD_DOG_PLAY_CHOREOGRAPHY_ENABLED, true);
    }

    public static int getPlayChoreographyFrequency(Level level) {
        return getInt(level, BD_PLAY_CHOREOGRAPHY_FREQUENCY, 2400);
    }

    public static int getMaxPlayDistance(Level level) {
        return getInt(level, BD_MAX_PLAY_DISTANCE, 16);
    }

    public static boolean isDogGeneticsEnabled(Level level) {
        return getBoolean(level, BD_DOG_GENETICS_ENABLED, true);
    }

    public static int getMaxScaleDeviation(Level level) {
        return getInt(level, BD_MAX_SCALE_DEVIATION, 30);
    }

    public static boolean isArchetypesEnabled(Level level) {
        return getBoolean(level, BD_ARCHETYPES_ENABLED, true);
    }

    public static int getArchetypeDistributionMode(Level level) {
        return getInt(level, BD_ARCHETYPE_DISTRIBUTION_MODE, 0);
    }

    public static boolean isCanineExplosiveReflexEnabled(Level level) {
        return getBoolean(level, BD_CANINE_EXPLOSIVE_REFLEX_ENABLED, true);
    }

    public static int getHazardFleeRadius(Level level) {
        return getInt(level, BD_HAZARD_FLEE_RADIUS, 14);
    }

    public static int getFleeSpeedMultiplier(Level level) {
        return getInt(level, BD_FLEE_SPEED_MULTIPLIER, 170);
    }

    public static boolean isBallisticEvasionEnabled(Level level) {
        return getBoolean(level, BD_BALLISTIC_EVASION_ENABLED, true);
    }

    public static int getEvasionReactionTicks(Level level) {
        return getInt(level, BD_EVASION_REACTION_TICKS, 20);
    }

    public static int getEvasionLateralImpulse(Level level) {
        return getInt(level, BD_EVASION_LATERAL_IMPULSE, 85);
    }

    public static boolean getBoolean(Level level, GameRule<Boolean> rule, boolean fallback) {
        if (level == null || rule == null) {
            return fallback;
        }
        return DynamicGameRuleManager.getBoolean(level, rule);
    }

    public static int getInt(Level level, GameRule<Integer> rule, int fallback) {
        if (level == null || rule == null) {
            return fallback;
        }
        return DynamicGameRuleManager.getInt(level, rule);
    }
}
