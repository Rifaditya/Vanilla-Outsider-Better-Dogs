// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.registry;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.platform.Services;

public class BetterDogsGameRules {

    // General & Environmental Safety
    public static GameRules.Key<GameRules.BooleanValue> BD_STORM_ANXIETY;
    public static GameRules.Key<GameRules.BooleanValue> BD_ACTIONBAR_FEEDBACK;
    public static GameRules.Key<GameRules.BooleanValue> BD_CREEPER_AWARENESS;
    public static GameRules.Key<GameRules.BooleanValue> BD_CREEPER_EVASION_ENABLED;
    public static GameRules.Key<GameRules.BooleanValue> BD_CLIFF_SAFETY;
    public static GameRules.Key<GameRules.BooleanValue> BD_FLEE_LOW_HEALTH;
    public static GameRules.Key<GameRules.BooleanValue> BD_DOGS_EAT_RAW_FOOD;
    public static GameRules.Key<GameRules.BooleanValue> BD_DOGS_EAT_COOKED_FOOD;
    public static GameRules.Key<GameRules.BooleanValue> BD_ENABLE_REFUSE_GROUND_FOOD;
    public static GameRules.Key<GameRules.IntegerValue> BD_REFUSE_GROUND_FOOD_CHANCE;
    public static GameRules.Key<GameRules.BooleanValue> BD_DEBUGGING;
    public static GameRules.Key<GameRules.BooleanValue> BD_NEMESIS_SYSTEM;
    public static GameRules.Key<GameRules.IntegerValue> BD_NEMESIS_DURATION_DAYS;
    public static GameRules.Key<GameRules.BooleanValue> BD_FAVORITE_TREATS;
    public static GameRules.Key<GameRules.BooleanValue> BD_PACK_FLANKING_TACTICS;
    public static GameRules.Key<GameRules.BooleanValue> BD_FLANKING_RAYCAST_CHECK;
    public static GameRules.Key<GameRules.BooleanValue> BD_SYNC_OWNER_TELEPORT;
    public static GameRules.Key<GameRules.BooleanValue> BD_FAST_TRAVEL_CATCHUP;
    public static GameRules.Key<GameRules.IntegerValue> BD_HORN_COMMAND_RANGE;
    public static GameRules.Key<GameRules.IntegerValue> BD_HORN_PATHING_TIMEOUT;
    public static GameRules.Key<GameRules.IntegerValue> BD_HORN_OVERRIDE_DURATION;
    public static GameRules.Key<GameRules.BooleanValue> BD_ALLOW_UNRESTRICTED_RIDING;
    public static GameRules.Key<GameRules.IntegerValue> BD_GIFT_FEED_THRESHOLD;
    public static GameRules.Key<GameRules.BooleanValue> BD_DEMERIT_ACCIDENTAL_ATTACKS;
    public static GameRules.Key<GameRules.IntegerValue> BD_TAMED_PACK_SPREAD_MULTIPLIER;
    public static GameRules.Key<GameRules.IntegerValue> BD_TAMED_PACK_SPREAD_MAX;

    // Player Protection
    public static GameRules.Key<GameRules.BooleanValue> BD_FRIENDLY_FIRE;

    // Aggressive Personality
    public static GameRules.Key<GameRules.IntegerValue> BD_AGGRO_HEALTH;
    public static GameRules.Key<GameRules.IntegerValue> BD_AGGRO_SPEED_PCT;
    public static GameRules.Key<GameRules.IntegerValue> BD_AGGRO_DMG_PCT;
    public static GameRules.Key<GameRules.IntegerValue> BD_AGGRO_FOLLOW_START;
    public static GameRules.Key<GameRules.IntegerValue> BD_AGGRO_CHASE_DIST;
    public static GameRules.Key<GameRules.IntegerValue> BD_AGGRO_DETECT_RANGE;
    public static GameRules.Key<GameRules.IntegerValue> BD_AGGRO_FLEE_CHANCE;

    // Pacifist Personality
    public static GameRules.Key<GameRules.IntegerValue> BD_PACI_HEALTH;
    public static GameRules.Key<GameRules.IntegerValue> BD_PACI_SPEED_PCT;
    public static GameRules.Key<GameRules.IntegerValue> BD_PACI_DMG_PCT;
    public static GameRules.Key<GameRules.IntegerValue> BD_PACI_KNOCKBACK_PCT;
    public static GameRules.Key<GameRules.IntegerValue> BD_PACI_FOLLOW_START;
    public static GameRules.Key<GameRules.IntegerValue> BD_PACI_FLEE_CHANCE;

    // Normal Personality
    public static GameRules.Key<GameRules.IntegerValue> BD_NORMAL_FOLLOW_START;
    public static GameRules.Key<GameRules.IntegerValue> BD_NORMAL_SPEED_PCT;
    public static GameRules.Key<GameRules.IntegerValue> BD_NORMAL_DMG_PCT;
    public static GameRules.Key<GameRules.IntegerValue> BD_NORMAL_HEALTH;
    public static GameRules.Key<GameRules.IntegerValue> BD_NORMAL_FLEE_CHANCE;

    // Miscellaneous & Social Dynamics
    public static GameRules.Key<GameRules.IntegerValue> BD_BABY_MISCHIEF_PERMILLE;
    public static GameRules.Key<GameRules.IntegerValue> BD_HOWL_CHANCE;
    public static GameRules.Key<GameRules.IntegerValue> BD_PACK_SPREAD;
    public static GameRules.Key<GameRules.IntegerValue> BD_GIFT_INTERACTION_COOLDOWN;
    public static GameRules.Key<GameRules.IntegerValue> BD_WOLF_MIN_SCALE_PERCENT;
    public static GameRules.Key<GameRules.IntegerValue> BD_WOLF_MAX_SCALE_PERCENT;

    // Correction & Retaliation & Hunting
    public static GameRules.Key<GameRules.IntegerValue> BD_BLOOD_FEUD_PERCENT;
    public static GameRules.Key<GameRules.IntegerValue> BD_BABY_RETALIATE_PERCENT;
    public static GameRules.Key<GameRules.IntegerValue> BD_WILD_HUNT_HEALTH_THRESHOLD;

    // Territorial
    public static GameRules.Key<GameRules.BooleanValue> BD_TERRITORIAL_RIVALRY;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERR_AA_WAR;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERR_AA_MERGE;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERR_AN_WAR;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERR_AN_MERGE;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERR_AP_WAR;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERR_AP_MERGE;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERR_NN_WAR;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERR_NN_MERGE;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERR_NP_WAR;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERR_NP_MERGE;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERR_PP_WAR;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERR_PP_MERGE;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERRITORIAL_FATAL_CHANCE;
    public static GameRules.Key<GameRules.BooleanValue> BD_TERRITORIAL_EXCLUSIVE_DISPUTES;
    public static GameRules.Key<GameRules.BooleanValue> BD_WILD_PERSONALITY_BEHAVIOR;
    public static GameRules.Key<GameRules.IntegerValue> BD_TERRITORIAL_SEARCH_RADIUS;

    // Spawning & Biome Integration
    public static GameRules.Key<GameRules.IntegerValue> BD_WOLF_PACK_CLUSTER_SIZE;
    public static GameRules.Key<GameRules.IntegerValue> BD_WOLF_SPAWN_DENSITY_BOOST;
    public static GameRules.Key<GameRules.IntegerValue> BD_WOLF_SPAWN_MULTIPLIER_PCT;
    public static GameRules.Key<GameRules.IntegerValue> BD_WOLF_SPAWN_GROUP_MIN;
    public static GameRules.Key<GameRules.IntegerValue> BD_WOLF_SPAWN_GROUP_MAX;
    public static GameRules.Key<GameRules.BooleanValue> BD_WOLF_SPAWN_EXPANDED_BIOMES;
    public static GameRules.Key<GameRules.BooleanValue> BD_DYNAMIC_CLIMATE_VARIANTS;

    // Breeding & Genetics
    public static GameRules.Key<GameRules.IntegerValue> BD_SPAWN_NORMAL_PERCENT;
    public static GameRules.Key<GameRules.IntegerValue> BD_SPAWN_AGGRO_PERCENT;
    public static GameRules.Key<GameRules.IntegerValue> BD_SPAWN_PACI_PERCENT;
    public static GameRules.Key<GameRules.IntegerValue> BD_BREED_SAME_CHANCE;
    public static GameRules.Key<GameRules.IntegerValue> BD_BREED_SAME_OTHER_CHANCE;
    public static GameRules.Key<GameRules.IntegerValue> BD_BREED_MIXED_DOMINANT_CHANCE;
    public static GameRules.Key<GameRules.IntegerValue> BD_BREED_MIXED_RECESSIVE_CHANCE;
    public static GameRules.Key<GameRules.IntegerValue> BD_BREED_DILUTED_NORMAL_CHANCE;
    public static GameRules.Key<GameRules.IntegerValue> BD_BREED_DILUTED_OTHER_CHANCE;
    public static GameRules.Key<GameRules.IntegerValue> BD_WOLF_LITTER_MAX_SIZE;
    public static GameRules.Key<GameRules.IntegerValue> BD_WOLF_LITTER_EXTRA_CHANCE;

    public static GameRules.Key<GameRules.BooleanValue> BD_HORN_COMMANDS_ENABLED;
    public static GameRules.Key<GameRules.BooleanValue> BD_FETCH_ENABLED;
    public static GameRules.Key<GameRules.IntegerValue> BD_FETCH_RANGE;
    public static GameRules.Key<GameRules.BooleanValue> BD_ZOOMIES_ENABLED;
    public static GameRules.Key<GameRules.IntegerValue> BD_ZOOMIES_DURATION_TICKS;
    public static GameRules.Key<GameRules.BooleanValue> BD_WOLVES_SEEK_WATER_ON_FIRE;
    public static GameRules.Key<GameRules.BooleanValue> BD_WOLVES_BREAK_SIT_ON_FIRE;

    // Guard Mode
    public static GameRules.Key<GameRules.BooleanValue> BD_PACIFIST_GUARD_BUFFS;
    public static GameRules.Key<GameRules.BooleanValue> BD_ENABLE_INBRED_CURING;
    public static GameRules.Key<GameRules.BooleanValue> BD_SHOW_RUNT_PARTICLES;
    public static GameRules.Key<GameRules.IntegerValue> BD_PARTICLE_DENSITY;
    public static GameRules.Key<GameRules.IntegerValue> BD_GUARD_PATROL_RANGE_AGGRESSIVE;
    public static GameRules.Key<GameRules.IntegerValue> BD_GUARD_PATROL_RANGE_NORMAL;
    public static GameRules.Key<GameRules.IntegerValue> BD_GUARD_PATROL_RANGE_PACIFIST;

    // Pack Spread Scaling
    public static GameRules.Key<GameRules.IntegerValue> BD_WILD_PACK_SPREAD_MULTIPLIER;
    public static GameRules.Key<GameRules.IntegerValue> BD_WILD_PACK_SPREAD_MAX;

    public static void init() {
        // General
        BD_STORM_ANXIETY = Services.GAMERULES.registerBoolean("bd_storm_anxiety", GameRules.Category.MISC, true);
        BD_ACTIONBAR_FEEDBACK = Services.GAMERULES.registerBoolean("bd_actionbar_feedback", GameRules.Category.MISC, false);
        BD_CREEPER_AWARENESS = Services.GAMERULES.registerBoolean("bd_creeper_awareness", GameRules.Category.MISC, true);
        BD_CREEPER_EVASION_ENABLED = Services.GAMERULES.registerBoolean("bd_creeper_evasion_enabled", GameRules.Category.MISC, true);
        BD_CLIFF_SAFETY = Services.GAMERULES.registerBoolean("bd_cliff_safety", GameRules.Category.MISC, true);
        BD_FLEE_LOW_HEALTH = Services.GAMERULES.registerBoolean("bd_flee_low_health", GameRules.Category.MISC, true);
        BD_DOGS_EAT_RAW_FOOD = Services.GAMERULES.registerBoolean("bd_dogs_eat_raw_food", GameRules.Category.MISC, true);
        BD_DOGS_EAT_COOKED_FOOD = Services.GAMERULES.registerBoolean("bd_dogs_eat_cooked_food", GameRules.Category.MISC, true);
        BD_ENABLE_REFUSE_GROUND_FOOD = Services.GAMERULES.registerBoolean("bd_enable_refuse_ground_food", GameRules.Category.MISC, true);
        BD_REFUSE_GROUND_FOOD_CHANCE = Services.GAMERULES.registerInt("bd_refuse_ground_food_chance", GameRules.Category.MISC, 30);
        BD_DEBUGGING = Services.GAMERULES.registerBoolean("bd_debugging", GameRules.Category.MISC, false);
        BD_NEMESIS_SYSTEM = Services.GAMERULES.registerBoolean("bd_nemesis_system", GameRules.Category.MISC, true);
        BD_NEMESIS_DURATION_DAYS = Services.GAMERULES.registerInt("bd_nemesis_duration_days", GameRules.Category.MISC, 3);
        BD_FAVORITE_TREATS = Services.GAMERULES.registerBoolean("bd_favorite_treats", GameRules.Category.MISC, true);
        BD_PACK_FLANKING_TACTICS = Services.GAMERULES.registerBoolean("bd_pack_flanking_tactics", GameRules.Category.MISC, true);
        BD_FLANKING_RAYCAST_CHECK = Services.GAMERULES.registerBoolean("bd_flanking_raycast_check", GameRules.Category.MISC, true);
        BD_SYNC_OWNER_TELEPORT = Services.GAMERULES.registerBoolean("bd_sync_owner_teleport", GameRules.Category.MISC, true);
        BD_FAST_TRAVEL_CATCHUP = Services.GAMERULES.registerBoolean("bd_fast_travel_catchup", GameRules.Category.MISC, true);
        BD_HORN_COMMAND_RANGE = Services.GAMERULES.registerInt("bd_horn_command_range", GameRules.Category.MISC, 64);
        BD_HORN_PATHING_TIMEOUT = Services.GAMERULES.registerInt("bd_horn_pathing_timeout", GameRules.Category.MISC, 300);
        BD_HORN_OVERRIDE_DURATION = Services.GAMERULES.registerInt("bd_horn_override_duration", GameRules.Category.MISC, 600);
        BD_ALLOW_UNRESTRICTED_RIDING = Services.GAMERULES.registerBoolean("bd_allow_unrestricted_dog_riding", GameRules.Category.MISC, false);
        BD_GIFT_FEED_THRESHOLD = Services.GAMERULES.registerInt("bd_gift_feed_threshold", GameRules.Category.MISC, 10);
        BD_DEMERIT_ACCIDENTAL_ATTACKS = Services.GAMERULES.registerBoolean("bd_demerit_accidental_attacks", GameRules.Category.MISC, true);
        BD_TAMED_PACK_SPREAD_MULTIPLIER = Services.GAMERULES.registerInt("bd_tamed_pack_spread_multiplier", GameRules.Category.MISC, 100);
        BD_TAMED_PACK_SPREAD_MAX = Services.GAMERULES.registerInt("bd_tamed_pack_spread_max", GameRules.Category.MISC, 50);

        // Player
        BD_FRIENDLY_FIRE = Services.GAMERULES.registerBoolean("bd_friendly_fire_protection", GameRules.Category.MISC, true);

        // Aggressive
        BD_AGGRO_HEALTH = Services.GAMERULES.registerInt("bd_aggressive_health", GameRules.Category.MISC, -10);
        BD_AGGRO_SPEED_PCT = Services.GAMERULES.registerInt("bd_aggro_speed_percent", GameRules.Category.MISC, 15);
        BD_AGGRO_DMG_PCT = Services.GAMERULES.registerInt("bd_aggro_dmg_percent", GameRules.Category.MISC, 15);
        BD_AGGRO_FOLLOW_START = Services.GAMERULES.registerInt("bd_aggro_follow_start", GameRules.Category.MISC, 50);
        BD_AGGRO_CHASE_DIST = Services.GAMERULES.registerInt("bd_aggro_chase_dist", GameRules.Category.MISC, 50);
        BD_AGGRO_DETECT_RANGE = Services.GAMERULES.registerInt("bd_aggro_detect_range", GameRules.Category.MISC, 20);
        BD_AGGRO_FLEE_CHANCE = Services.GAMERULES.registerInt("bd_aggro_flee_chance", GameRules.Category.MISC, 10);

        // Pacifist
        BD_PACI_HEALTH = Services.GAMERULES.registerInt("bd_paci_health", GameRules.Category.MISC, 20);
        BD_PACI_SPEED_PCT = Services.GAMERULES.registerInt("bd_paci_speed_percent", GameRules.Category.MISC, -10);
        BD_PACI_DMG_PCT = Services.GAMERULES.registerInt("bd_paci_dmg_percent", GameRules.Category.MISC, -15);
        BD_PACI_KNOCKBACK_PCT = Services.GAMERULES.registerInt("bd_paci_knockback_percent", GameRules.Category.MISC, 50);
        BD_PACI_FOLLOW_START = Services.GAMERULES.registerInt("bd_paci_follow_start", GameRules.Category.MISC, 5);
        BD_PACI_FLEE_CHANCE = Services.GAMERULES.registerInt("bd_paci_flee_chance", GameRules.Category.MISC, 100);

        // Normal
        BD_NORMAL_FOLLOW_START = Services.GAMERULES.registerInt("bd_normal_follow_start", GameRules.Category.MISC, 10);
        BD_NORMAL_SPEED_PCT = Services.GAMERULES.registerInt("bd_normal_speed_percent", GameRules.Category.MISC, 0);
        BD_NORMAL_DMG_PCT = Services.GAMERULES.registerInt("bd_normal_dmg_percent", GameRules.Category.MISC, 0);
        BD_NORMAL_HEALTH = Services.GAMERULES.registerInt("bd_normal_health", GameRules.Category.MISC, 0);
        BD_NORMAL_FLEE_CHANCE = Services.GAMERULES.registerInt("bd_normal_flee_chance", GameRules.Category.MISC, 50);

        // Misc
        BD_BABY_MISCHIEF_PERMILLE = Services.GAMERULES.registerInt("bd_baby_mischief_permille", GameRules.Category.MISC, 25);
        BD_HOWL_CHANCE = Services.GAMERULES.registerInt("bd_howl_chance", GameRules.Category.MISC, 10);
        BD_PACK_SPREAD = Services.GAMERULES.registerInt("bd_pack_spread", GameRules.Category.MISC, 8);
        BD_GIFT_INTERACTION_COOLDOWN = Services.GAMERULES.registerInt("bd_gift_interaction_cooldown", GameRules.Category.MISC, 6000);
        BD_WOLF_MIN_SCALE_PERCENT = Services.GAMERULES.registerInt("bd_wolf_min_scale_percent", GameRules.Category.MISC, 70);
        BD_WOLF_MAX_SCALE_PERCENT = Services.GAMERULES.registerInt("bd_wolf_max_scale_percent", GameRules.Category.MISC, 145);

        // Correction & Hunting
        BD_BLOOD_FEUD_PERCENT = Services.GAMERULES.registerInt("bd_blood_feud_percent", GameRules.Category.MISC, 5);
        BD_BABY_RETALIATE_PERCENT = Services.GAMERULES.registerInt("bd_baby_retaliate_percent", GameRules.Category.MISC, 50);
        BD_WILD_HUNT_HEALTH_THRESHOLD = Services.GAMERULES.registerInt("bd_wild_hunt_health_threshold", GameRules.Category.MISC, 50);

        // Territorial
        BD_TERRITORIAL_RIVALRY = Services.GAMERULES.registerBoolean("bd_territorial_rivalry", GameRules.Category.MISC, true);
        BD_TERR_AA_WAR = Services.GAMERULES.registerInt("bd_terr_aa_war", GameRules.Category.MISC, 80);
        BD_TERR_AA_MERGE = Services.GAMERULES.registerInt("bd_terr_aa_merge", GameRules.Category.MISC, 0);
        BD_TERR_AN_WAR = Services.GAMERULES.registerInt("bd_terr_an_war", GameRules.Category.MISC, 50);
        BD_TERR_AN_MERGE = Services.GAMERULES.registerInt("bd_terr_an_merge", GameRules.Category.MISC, 0);
        BD_TERR_AP_WAR = Services.GAMERULES.registerInt("bd_terr_ap_war", GameRules.Category.MISC, 20);
        BD_TERR_AP_MERGE = Services.GAMERULES.registerInt("bd_terr_ap_merge", GameRules.Category.MISC, 80);
        BD_TERR_NN_WAR = Services.GAMERULES.registerInt("bd_terr_nn_war", GameRules.Category.MISC, 20);
        BD_TERR_NN_MERGE = Services.GAMERULES.registerInt("bd_terr_nn_merge", GameRules.Category.MISC, 80);
        BD_TERR_NP_WAR = Services.GAMERULES.registerInt("bd_terr_np_war", GameRules.Category.MISC, 0);
        BD_TERR_NP_MERGE = Services.GAMERULES.registerInt("bd_terr_np_merge", GameRules.Category.MISC, 100);
        BD_TERR_PP_WAR = Services.GAMERULES.registerInt("bd_terr_pp_war", GameRules.Category.MISC, 0);
        BD_TERR_PP_MERGE = Services.GAMERULES.registerInt("bd_terr_pp_merge", GameRules.Category.MISC, 100);
        BD_TERRITORIAL_FATAL_CHANCE = Services.GAMERULES.registerInt("bd_territorial_fatal_chance", GameRules.Category.MISC, 10);
        BD_TERRITORIAL_EXCLUSIVE_DISPUTES = Services.GAMERULES.registerBoolean("bd_territorial_exclusive_disputes", GameRules.Category.MISC, true);
        BD_WILD_PERSONALITY_BEHAVIOR = Services.GAMERULES.registerBoolean("bd_wild_personality_behavior", GameRules.Category.MISC, true);
        BD_TERRITORIAL_SEARCH_RADIUS = Services.GAMERULES.registerInt("bd_territorial_search_radius", GameRules.Category.MISC, 32);

        // Spawning
        BD_WOLF_PACK_CLUSTER_SIZE = Services.GAMERULES.registerInt("bd_wolf_pack_cluster_size", GameRules.Category.MISC, 6);
        BD_WOLF_SPAWN_DENSITY_BOOST = Services.GAMERULES.registerInt("bd_wolf_spawn_density_boost", GameRules.Category.MISC, 100);
        BD_WOLF_SPAWN_MULTIPLIER_PCT = Services.GAMERULES.registerInt("bd_wolf_spawn_multiplier_percent", GameRules.Category.MISC, 150);
        BD_WOLF_SPAWN_GROUP_MIN = Services.GAMERULES.registerInt("bd_wolf_spawn_group_min", GameRules.Category.MISC, 4);
        BD_WOLF_SPAWN_GROUP_MAX = Services.GAMERULES.registerInt("bd_wolf_spawn_group_max", GameRules.Category.MISC, 8);
        BD_WOLF_SPAWN_EXPANDED_BIOMES = Services.GAMERULES.registerBoolean("bd_wolf_spawn_expanded_biomes", GameRules.Category.MISC, true);
        BD_DYNAMIC_CLIMATE_VARIANTS = Services.GAMERULES.registerBoolean("bd_dynamic_climate_variants", GameRules.Category.MISC, true);

        // Breeding & Genetics
        BD_SPAWN_NORMAL_PERCENT = Services.GAMERULES.registerInt("bd_spawn_normal_percent", GameRules.Category.MISC, 60);
        BD_SPAWN_AGGRO_PERCENT = Services.GAMERULES.registerInt("bd_spawn_aggro_percent", GameRules.Category.MISC, 20);
        BD_SPAWN_PACI_PERCENT = Services.GAMERULES.registerInt("bd_spawn_paci_percent", GameRules.Category.MISC, 20);
        BD_BREED_SAME_CHANCE = Services.GAMERULES.registerInt("bd_breed_same_chance", GameRules.Category.MISC, 80);
        BD_BREED_SAME_OTHER_CHANCE = Services.GAMERULES.registerInt("bd_breed_same_other_chance", GameRules.Category.MISC, 10);
        BD_BREED_MIXED_DOMINANT_CHANCE = Services.GAMERULES.registerInt("bd_breed_mixed_dominant_chance", GameRules.Category.MISC, 40);
        BD_BREED_MIXED_RECESSIVE_CHANCE = Services.GAMERULES.registerInt("bd_breed_mixed_recessive_chance", GameRules.Category.MISC, 40);
        BD_BREED_DILUTED_NORMAL_CHANCE = Services.GAMERULES.registerInt("bd_breed_diluted_normal_chance", GameRules.Category.MISC, 50);
        BD_BREED_DILUTED_OTHER_CHANCE = Services.GAMERULES.registerInt("bd_breed_diluted_other_chance", GameRules.Category.MISC, 25);
        BD_WOLF_LITTER_MAX_SIZE = Services.GAMERULES.registerInt("bd_wolf_litter_max_size", GameRules.Category.MISC, 4);
        BD_WOLF_LITTER_EXTRA_CHANCE = Services.GAMERULES.registerInt("bd_wolf_litter_extra_chance", GameRules.Category.MISC, 20);

        BD_HORN_COMMANDS_ENABLED = Services.GAMERULES.registerBoolean("bd_horn_commands_enabled", GameRules.Category.MISC, true);
        BD_FETCH_ENABLED = Services.GAMERULES.registerBoolean("bd_fetch_enabled", GameRules.Category.MISC, true);
        BD_FETCH_RANGE = Services.GAMERULES.registerInt("bd_fetch_range", GameRules.Category.MISC, 16);
        BD_ZOOMIES_ENABLED = Services.GAMERULES.registerBoolean("bd_zoomies_enabled", GameRules.Category.MISC, true);
        BD_ZOOMIES_DURATION_TICKS = Services.GAMERULES.registerInt("bd_zoomies_duration_ticks", GameRules.Category.MISC, 160);
        BD_WOLVES_SEEK_WATER_ON_FIRE = Services.GAMERULES.registerBoolean("bd_wolves_seek_water_on_fire", GameRules.Category.MISC, true);
        BD_WOLVES_BREAK_SIT_ON_FIRE = Services.GAMERULES.registerBoolean("bd_wolves_break_sit_on_fire", GameRules.Category.MISC, true);

        // Guard Mode
        BD_PACIFIST_GUARD_BUFFS = Services.GAMERULES.registerBoolean("bd_pacifist_guard_buffs", GameRules.Category.MISC, false);
        BD_ENABLE_INBRED_CURING = Services.GAMERULES.registerBoolean("bd_enable_inbred_curing", GameRules.Category.MISC, true);
        BD_SHOW_RUNT_PARTICLES = Services.GAMERULES.registerBoolean("bd_show_runt_particles", GameRules.Category.MISC, false);
        BD_PARTICLE_DENSITY = Services.GAMERULES.registerInt("bd_particle_density", GameRules.Category.MISC, 2);
        BD_GUARD_PATROL_RANGE_AGGRESSIVE = Services.GAMERULES.registerInt("bd_guard_patrol_range_aggressive", GameRules.Category.MISC, 12);
        BD_GUARD_PATROL_RANGE_NORMAL = Services.GAMERULES.registerInt("bd_guard_patrol_range_normal", GameRules.Category.MISC, 0);
        BD_GUARD_PATROL_RANGE_PACIFIST = Services.GAMERULES.registerInt("bd_guard_patrol_range_pacifist", GameRules.Category.MISC, 3);

        // Pack Spread Scaling
        BD_WILD_PACK_SPREAD_MULTIPLIER = Services.GAMERULES.registerInt("bd_wild_pack_spread_multiplier", GameRules.Category.MISC, 80);
        BD_WILD_PACK_SPREAD_MAX = Services.GAMERULES.registerInt("bd_wild_pack_spread_max", GameRules.Category.MISC, 40);
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

    public static boolean getBoolean(Level level, GameRules.Key<GameRules.BooleanValue> key, boolean fallback) {
        if (level == null || key == null) {
            return fallback;
        }
        try {
            return DynamicGameRuleManager.getBoolean(level, key);
        } catch (Throwable t) {
            try {
                return level.getGameRules() != null ? level.getGameRules().getBoolean(key) : fallback;
            } catch (Exception e) {
                return fallback;
            }
        }
    }

    public static int getInt(Level level, GameRules.Key<GameRules.IntegerValue> key, int fallback) {
        if (level == null || key == null) {
            return fallback;
        }
        try {
            return DynamicGameRuleManager.getInt(level, key);
        } catch (Throwable t) {
            try {
                return level.getGameRules() != null ? level.getGameRules().getInt(key) : fallback;
            } catch (Exception e) {
                return fallback;
            }
        }
    }
}
