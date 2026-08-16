// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.registry;

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class BetterDogsGameRules {

    public static final CustomGameRuleCategory BETTER_DOGS = new CustomGameRuleCategory(
            new ResourceLocation("betterdogs", "betterdogs"),
            Component.translatable("gamerule.category.betterdogs").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW)
    );

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

    // Correction & Retaliation
    public static GameRules.Key<GameRules.IntegerValue> BD_BLOOD_FEUD_PERCENT;
    public static GameRules.Key<GameRules.IntegerValue> BD_BABY_RETALIATE_PERCENT;

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

    public static void init() {
        // General
        BD_STORM_ANXIETY = GameRuleRegistry.register("bd_storm_anxiety", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_ACTIONBAR_FEEDBACK = GameRuleRegistry.register("bd_actionbar_feedback", BETTER_DOGS, GameRuleFactory.createBooleanRule(false));
        BD_CREEPER_AWARENESS = GameRuleRegistry.register("bd_creeper_awareness", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_CREEPER_EVASION_ENABLED = GameRuleRegistry.register("bd_creeper_evasion_enabled", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_CLIFF_SAFETY = GameRuleRegistry.register("bd_cliff_safety", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_FLEE_LOW_HEALTH = GameRuleRegistry.register("bd_flee_low_health", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_DOGS_EAT_RAW_FOOD = GameRuleRegistry.register("bd_dogs_eat_raw_food", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_DOGS_EAT_COOKED_FOOD = GameRuleRegistry.register("bd_dogs_eat_cooked_food", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_ENABLE_REFUSE_GROUND_FOOD = GameRuleRegistry.register("bd_enable_refuse_ground_food", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_REFUSE_GROUND_FOOD_CHANCE = GameRuleRegistry.register("bd_refuse_ground_food_chance", BETTER_DOGS, GameRuleFactory.createIntRule(30));
        BD_DEBUGGING = GameRuleRegistry.register("bd_debugging", BETTER_DOGS, GameRuleFactory.createBooleanRule(false));
        BD_NEMESIS_SYSTEM = GameRuleRegistry.register("bd_nemesis_system", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_NEMESIS_DURATION_DAYS = GameRuleRegistry.register("bd_nemesis_duration_days", BETTER_DOGS, GameRuleFactory.createIntRule(3));
        BD_FAVORITE_TREATS = GameRuleRegistry.register("bd_favorite_treats", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_PACK_FLANKING_TACTICS = GameRuleRegistry.register("bd_pack_flanking_tactics", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_FLANKING_RAYCAST_CHECK = GameRuleRegistry.register("bd_flanking_raycast_check", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_SYNC_OWNER_TELEPORT = GameRuleRegistry.register("bd_sync_owner_teleport", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_FAST_TRAVEL_CATCHUP = GameRuleRegistry.register("bd_fast_travel_catchup", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_HORN_COMMAND_RANGE = GameRuleRegistry.register("bd_horn_command_range", BETTER_DOGS, GameRuleFactory.createIntRule(64));
        BD_HORN_PATHING_TIMEOUT = GameRuleRegistry.register("bd_horn_pathing_timeout", BETTER_DOGS, GameRuleFactory.createIntRule(300));
        BD_HORN_OVERRIDE_DURATION = GameRuleRegistry.register("bd_horn_override_duration", BETTER_DOGS, GameRuleFactory.createIntRule(600));
        BD_ALLOW_UNRESTRICTED_RIDING = GameRuleRegistry.register("bd_allow_unrestricted_dog_riding", BETTER_DOGS, GameRuleFactory.createBooleanRule(false));
        BD_GIFT_FEED_THRESHOLD = GameRuleRegistry.register("bd_gift_feed_threshold", BETTER_DOGS, GameRuleFactory.createIntRule(10));
        BD_DEMERIT_ACCIDENTAL_ATTACKS = GameRuleRegistry.register("bd_demerit_accidental_attacks", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_TAMED_PACK_SPREAD_MULTIPLIER = GameRuleRegistry.register("bd_tamed_pack_spread_multiplier", BETTER_DOGS, GameRuleFactory.createIntRule(100));
        BD_TAMED_PACK_SPREAD_MAX = GameRuleRegistry.register("bd_tamed_pack_spread_max", BETTER_DOGS, GameRuleFactory.createIntRule(50));

        // Player
        BD_FRIENDLY_FIRE = GameRuleRegistry.register("bd_friendly_fire_protection", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));

        // Aggressive
        BD_AGGRO_HEALTH = GameRuleRegistry.register("bd_aggressive_health", BETTER_DOGS, GameRuleFactory.createIntRule(-10));
        BD_AGGRO_SPEED_PCT = GameRuleRegistry.register("bd_aggro_speed_percent", BETTER_DOGS, GameRuleFactory.createIntRule(15));
        BD_AGGRO_DMG_PCT = GameRuleRegistry.register("bd_aggro_dmg_percent", BETTER_DOGS, GameRuleFactory.createIntRule(15));
        BD_AGGRO_FOLLOW_START = GameRuleRegistry.register("bd_aggro_follow_start", BETTER_DOGS, GameRuleFactory.createIntRule(50));
        BD_AGGRO_CHASE_DIST = GameRuleRegistry.register("bd_aggro_chase_dist", BETTER_DOGS, GameRuleFactory.createIntRule(50));
        BD_AGGRO_DETECT_RANGE = GameRuleRegistry.register("bd_aggro_detect_range", BETTER_DOGS, GameRuleFactory.createIntRule(20));
        BD_AGGRO_FLEE_CHANCE = GameRuleRegistry.register("bd_aggro_flee_chance", BETTER_DOGS, GameRuleFactory.createIntRule(10));

        // Pacifist
        BD_PACI_HEALTH = GameRuleRegistry.register("bd_paci_health", BETTER_DOGS, GameRuleFactory.createIntRule(20));
        BD_PACI_SPEED_PCT = GameRuleRegistry.register("bd_paci_speed_percent", BETTER_DOGS, GameRuleFactory.createIntRule(-10));
        BD_PACI_DMG_PCT = GameRuleRegistry.register("bd_paci_dmg_percent", BETTER_DOGS, GameRuleFactory.createIntRule(-15));
        BD_PACI_KNOCKBACK_PCT = GameRuleRegistry.register("bd_paci_knockback_percent", BETTER_DOGS, GameRuleFactory.createIntRule(50));
        BD_PACI_FOLLOW_START = GameRuleRegistry.register("bd_paci_follow_start", BETTER_DOGS, GameRuleFactory.createIntRule(5));
        BD_PACI_FLEE_CHANCE = GameRuleRegistry.register("bd_paci_flee_chance", BETTER_DOGS, GameRuleFactory.createIntRule(100));

        // Normal
        BD_NORMAL_FOLLOW_START = GameRuleRegistry.register("bd_normal_follow_start", BETTER_DOGS, GameRuleFactory.createIntRule(10));
        BD_NORMAL_SPEED_PCT = GameRuleRegistry.register("bd_normal_speed_percent", BETTER_DOGS, GameRuleFactory.createIntRule(0));
        BD_NORMAL_DMG_PCT = GameRuleRegistry.register("bd_normal_dmg_percent", BETTER_DOGS, GameRuleFactory.createIntRule(0));
        BD_NORMAL_HEALTH = GameRuleRegistry.register("bd_normal_health", BETTER_DOGS, GameRuleFactory.createIntRule(0));
        BD_NORMAL_FLEE_CHANCE = GameRuleRegistry.register("bd_normal_flee_chance", BETTER_DOGS, GameRuleFactory.createIntRule(50));

        // Misc
        BD_BABY_MISCHIEF_PERMILLE = GameRuleRegistry.register("bd_baby_mischief_permille", BETTER_DOGS, GameRuleFactory.createIntRule(25));
        BD_HOWL_CHANCE = GameRuleRegistry.register("bd_howl_chance", BETTER_DOGS, GameRuleFactory.createIntRule(10));
        BD_PACK_SPREAD = GameRuleRegistry.register("bd_pack_spread", BETTER_DOGS, GameRuleFactory.createIntRule(8));
        BD_GIFT_INTERACTION_COOLDOWN = GameRuleRegistry.register("bd_gift_interaction_cooldown", BETTER_DOGS, GameRuleFactory.createIntRule(6000));
        BD_WOLF_MIN_SCALE_PERCENT = GameRuleRegistry.register("bd_wolf_min_scale_percent", BETTER_DOGS, GameRuleFactory.createIntRule(70));
        BD_WOLF_MAX_SCALE_PERCENT = GameRuleRegistry.register("bd_wolf_max_scale_percent", BETTER_DOGS, GameRuleFactory.createIntRule(145));

        // Correction
        BD_BLOOD_FEUD_PERCENT = GameRuleRegistry.register("bd_blood_feud_percent", BETTER_DOGS, GameRuleFactory.createIntRule(5));
        BD_BABY_RETALIATE_PERCENT = GameRuleRegistry.register("bd_baby_retaliate_percent", BETTER_DOGS, GameRuleFactory.createIntRule(50));

        // Territorial
        BD_TERRITORIAL_RIVALRY = GameRuleRegistry.register("bd_territorial_rivalry", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_TERR_AA_WAR = GameRuleRegistry.register("bd_terr_aa_war", BETTER_DOGS, GameRuleFactory.createIntRule(80));
        BD_TERR_AA_MERGE = GameRuleRegistry.register("bd_terr_aa_merge", BETTER_DOGS, GameRuleFactory.createIntRule(0));
        BD_TERR_AN_WAR = GameRuleRegistry.register("bd_terr_an_war", BETTER_DOGS, GameRuleFactory.createIntRule(50));
        BD_TERR_AN_MERGE = GameRuleRegistry.register("bd_terr_an_merge", BETTER_DOGS, GameRuleFactory.createIntRule(0));
        BD_TERR_AP_WAR = GameRuleRegistry.register("bd_terr_ap_war", BETTER_DOGS, GameRuleFactory.createIntRule(20));
        BD_TERR_AP_MERGE = GameRuleRegistry.register("bd_terr_ap_merge", BETTER_DOGS, GameRuleFactory.createIntRule(80));
        BD_TERR_NN_WAR = GameRuleRegistry.register("bd_terr_nn_war", BETTER_DOGS, GameRuleFactory.createIntRule(20));
        BD_TERR_NN_MERGE = GameRuleRegistry.register("bd_terr_nn_merge", BETTER_DOGS, GameRuleFactory.createIntRule(80));
        BD_TERR_NP_WAR = GameRuleRegistry.register("bd_terr_np_war", BETTER_DOGS, GameRuleFactory.createIntRule(0));
        BD_TERR_NP_MERGE = GameRuleRegistry.register("bd_terr_np_merge", BETTER_DOGS, GameRuleFactory.createIntRule(100));
        BD_TERR_PP_WAR = GameRuleRegistry.register("bd_terr_pp_war", BETTER_DOGS, GameRuleFactory.createIntRule(0));
        BD_TERR_PP_MERGE = GameRuleRegistry.register("bd_terr_pp_merge", BETTER_DOGS, GameRuleFactory.createIntRule(100));
        BD_TERRITORIAL_FATAL_CHANCE = GameRuleRegistry.register("bd_territorial_fatal_chance", BETTER_DOGS, GameRuleFactory.createIntRule(10));
        BD_TERRITORIAL_EXCLUSIVE_DISPUTES = GameRuleRegistry.register("bd_territorial_exclusive_disputes", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_WILD_PERSONALITY_BEHAVIOR = GameRuleRegistry.register("bd_wild_personality_behavior", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_TERRITORIAL_SEARCH_RADIUS = GameRuleRegistry.register("bd_territorial_search_radius", BETTER_DOGS, GameRuleFactory.createIntRule(32));

        // Spawning
        BD_WOLF_PACK_CLUSTER_SIZE = GameRuleRegistry.register("bd_wolf_pack_cluster_size", BETTER_DOGS, GameRuleFactory.createIntRule(6));
        BD_WOLF_SPAWN_DENSITY_BOOST = GameRuleRegistry.register("bd_wolf_spawn_density_boost", BETTER_DOGS, GameRuleFactory.createIntRule(100));
        BD_WOLF_SPAWN_MULTIPLIER_PCT = GameRuleRegistry.register("bd_wolf_spawn_multiplier_percent", BETTER_DOGS, GameRuleFactory.createIntRule(150));
        BD_WOLF_SPAWN_GROUP_MIN = GameRuleRegistry.register("bd_wolf_spawn_group_min", BETTER_DOGS, GameRuleFactory.createIntRule(4));
        BD_WOLF_SPAWN_GROUP_MAX = GameRuleRegistry.register("bd_wolf_spawn_group_max", BETTER_DOGS, GameRuleFactory.createIntRule(8));
        BD_WOLF_SPAWN_EXPANDED_BIOMES = GameRuleRegistry.register("bd_wolf_spawn_expanded_biomes", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));
        BD_DYNAMIC_CLIMATE_VARIANTS = GameRuleRegistry.register("bd_dynamic_climate_variants", BETTER_DOGS, GameRuleFactory.createBooleanRule(true));

        // Breeding & Genetics
        BD_SPAWN_NORMAL_PERCENT = GameRuleRegistry.register("bd_spawn_normal_percent", BETTER_DOGS, GameRuleFactory.createIntRule(60));
        BD_SPAWN_AGGRO_PERCENT = GameRuleRegistry.register("bd_spawn_aggro_percent", BETTER_DOGS, GameRuleFactory.createIntRule(20));
        BD_SPAWN_PACI_PERCENT = GameRuleRegistry.register("bd_spawn_paci_percent", BETTER_DOGS, GameRuleFactory.createIntRule(20));
        BD_BREED_SAME_CHANCE = GameRuleRegistry.register("bd_breed_same_chance", BETTER_DOGS, GameRuleFactory.createIntRule(80));
        BD_BREED_SAME_OTHER_CHANCE = GameRuleRegistry.register("bd_breed_same_other_chance", BETTER_DOGS, GameRuleFactory.createIntRule(10));
        BD_BREED_MIXED_DOMINANT_CHANCE = GameRuleRegistry.register("bd_breed_mixed_dominant_chance", BETTER_DOGS, GameRuleFactory.createIntRule(40));
        BD_BREED_MIXED_RECESSIVE_CHANCE = GameRuleRegistry.register("bd_breed_mixed_recessive_chance", BETTER_DOGS, GameRuleFactory.createIntRule(40));
        BD_BREED_DILUTED_NORMAL_CHANCE = GameRuleRegistry.register("bd_breed_diluted_normal_chance", BETTER_DOGS, GameRuleFactory.createIntRule(50));
        BD_BREED_DILUTED_OTHER_CHANCE = GameRuleRegistry.register("bd_breed_diluted_other_chance", BETTER_DOGS, GameRuleFactory.createIntRule(25));
        BD_WOLF_LITTER_MAX_SIZE = GameRuleRegistry.register("bd_wolf_litter_max_size", BETTER_DOGS, GameRuleFactory.createIntRule(4));
    }

    public static boolean getBoolean(Level level, GameRules.Key<GameRules.BooleanValue> key, boolean fallback) {
        if (level == null || level.getGameRules() == null || key == null) {
            return fallback;
        }
        try {
            return level.getGameRules().getBoolean(key);
        } catch (Exception e) {
            return fallback;
        }
    }

    public static int getInt(Level level, GameRules.Key<GameRules.IntegerValue> key, int fallback) {
        if (level == null || level.getGameRules() == null || key == null) {
            return fallback;
        }
        try {
            return level.getGameRules().getInt(key);
        } catch (Exception e) {
            return fallback;
        }
    }
}
