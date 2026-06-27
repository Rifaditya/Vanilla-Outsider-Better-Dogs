// Verified against: GameRules.java (26.1.2+)
package net.vanillaoutsider.betterdogs.registry;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

public class BetterDogsGameRules {

	// Single unified category
	public static final GameRuleCategory BETTER_DOGS = GameRuleCategory
			.register(Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "better_dogs"));



	// ====================================================================================
	// RULES
	// ====================================================================================

	// --- General ---
	public static GameRule<Boolean> BD_STORM_ANXIETY;
	public static GameRule<Boolean> BD_CREEPER_AWARENESS;
	public static GameRule<Boolean> BD_PACK_FLANKING_TACTICS;
	public static GameRule<Boolean> BD_CLIFF_SAFETY;
	public static GameRule<Boolean> BD_FLEE_LOW_HEALTH;
	public static GameRule<Boolean> BD_DOGS_EAT_RAW_FOOD;
	public static GameRule<Boolean> BD_DOGS_EAT_COOKED_FOOD;
	public static GameRule<Boolean> BD_ENABLE_REFUSE_GROUND_FOOD;
	public static GameRule<Integer> BD_REFUSE_GROUND_FOOD_CHANCE;
	public static GameRule<Boolean> BD_DEBUGGING;
	public static GameRule<Boolean> BD_NEMESIS_SYSTEM;
	public static GameRule<Integer> BD_NEMESIS_DURATION_DAYS;

	// --- Player ---
	public static GameRule<Boolean> BD_FRIENDLY_FIRE;

	// --- Aggressive Personality ---
	public static GameRule<Integer> BD_AGGRO_HEALTH;
	public static GameRule<Integer> BD_AGGRO_SPEED_PCT;
	public static GameRule<Integer> BD_AGGRO_DMG_PCT;
	public static GameRule<Integer> BD_AGGRO_FOLLOW_START;
	public static GameRule<Integer> BD_AGGRO_CHASE_DIST;
	public static GameRule<Integer> BD_AGGRO_DETECT_RANGE;
	public static GameRule<Integer> BD_AGGRO_FLEE_CHANCE;

	// --- Pacifist Personality ---
	public static GameRule<Integer> BD_PACI_HEALTH;
	public static GameRule<Integer> BD_PACI_SPEED_PCT;
	public static GameRule<Integer> BD_PACI_DMG_PCT;
	public static GameRule<Integer> BD_PACI_KNOCKBACK_PCT;
	public static GameRule<Integer> BD_PACI_FOLLOW_START;
	public static GameRule<Integer> BD_PACI_FLEE_CHANCE;

	// --- Normal Personality ---
	public static GameRule<Integer> BD_NORMAL_FOLLOW_START;
	public static GameRule<Integer> BD_NORMAL_SPEED_PCT;
	public static GameRule<Integer> BD_NORMAL_DMG_PCT;
	public static GameRule<Integer> BD_NORMAL_HEALTH;
	public static GameRule<Integer> BD_NORMAL_FLEE_CHANCE;

	// --- Misc ---
	public static GameRule<Integer> BD_BABY_MISCHIEF_PERMILLE;
	public static GameRule<Integer> BD_HOWL_CHANCE;
	public static GameRule<Integer> BD_PACK_SPREAD;
	public static GameRule<Integer> BD_GIFT_FEED_THRESHOLD;
	public static GameRule<Integer> BD_GIFT_INTERACTION_COOLDOWN;
	public static GameRule<Boolean> BD_DEMERIT_ACCIDENTAL_ATTACKS;

	// --- Correction/Retaliation ---
	public static GameRule<Integer> BD_BLOOD_FEUD_PERCENT;
	public static GameRule<Integer> BD_BABY_RETALIATE_PERCENT;

	// --- Territorial ---
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

	// --- Spawning ---
	public static GameRule<Integer> BD_WOLF_PACK_CLUSTER_SIZE;
	public static GameRule<Integer> BD_WOLF_SPAWN_DENSITY_BOOST;
	public static GameRule<Integer> BD_WOLF_SPAWN_MULTIPLIER_PCT;

	// --- Breeding Genetics ---
	public static GameRule<Integer> BD_BREED_SAME_CHANCE;
	public static GameRule<Integer> BD_BREED_SAME_OTHER_CHANCE;
	public static GameRule<Integer> BD_BREED_MIXED_DOMINANT_CHANCE;
	public static GameRule<Integer> BD_BREED_MIXED_RECESSIVE_CHANCE;
	public static GameRule<Integer> BD_BREED_DILUTED_NORMAL_CHANCE;
	public static GameRule<Integer> BD_BREED_DILUTED_OTHER_CHANCE;
	public static GameRule<Integer> BD_WOLF_LITTER_MAX_SIZE;
	public static GameRule<Integer> BD_WOLF_LITTER_EXTRA_CHANCE;

	// --- Taming Chances ---
	public static GameRule<Integer> BD_SPAWN_NORMAL_PERCENT;
	public static GameRule<Integer> BD_SPAWN_AGGRO_PERCENT;
	public static GameRule<Integer> BD_SPAWN_PACI_PERCENT;

	// --- Guard Mode ---
	public static GameRule<Boolean> BD_PACIFIST_GUARD_BUFFS;
	public static GameRule<Boolean> BD_ENABLE_INBRED_CURING;
	public static GameRule<Boolean> BD_SHOW_RUNT_PARTICLES;
	public static GameRule<Integer> BD_GUARD_PATROL_RANGE_AGGRESSIVE;
	public static GameRule<Integer> BD_GUARD_PATROL_RANGE_NORMAL;
	public static GameRule<Integer> BD_GUARD_PATROL_RANGE_PACIFIST;

	// --- Pack Spread Scaling ---
	public static GameRule<Integer> BD_TAMED_PACK_SPREAD_MULTIPLIER;
	public static GameRule<Integer> BD_TAMED_PACK_SPREAD_MAX;
	public static GameRule<Integer> BD_WILD_PACK_SPREAD_MULTIPLIER;
	public static GameRule<Integer> BD_WILD_PACK_SPREAD_MAX;

	// --- Commands / Sitting ---
	public static GameRule<Boolean> BD_ALLOW_UNRESTRICTED_RIDING;

	public static void register() {
		BetterDogsConfig config = BetterDogsConfig.get();



		BD_STORM_ANXIETY = registerBoolean("vanilla-outsider-better-dogs:bd_storm_anxiety", BETTER_DOGS,
				config.getEnableStormAnxiety());
		BD_CLIFF_SAFETY = registerBoolean("vanilla-outsider-better-dogs:bd_cliff_safety", BETTER_DOGS,
				config.getEnableCliffSafety());
		BD_FLEE_LOW_HEALTH = registerBoolean("vanilla-outsider-better-dogs:bd_flee_low_health", BETTER_DOGS,
				config.getEnableFleeLowHealth());
		BD_DOGS_EAT_RAW_FOOD = registerBoolean("vanilla-outsider-better-dogs:bd_dogs_eat_raw_food", BETTER_DOGS,
				config.getEnableDogsEatRawGroundFood());
		BD_DOGS_EAT_COOKED_FOOD = registerBoolean("vanilla-outsider-better-dogs:bd_dogs_eat_cooked_food", BETTER_DOGS,
				config.getEnableDogsEatCookedGroundFood());
		BD_ENABLE_REFUSE_GROUND_FOOD = registerBoolean("vanilla-outsider-better-dogs:bd_enable_refuse_ground_food", BETTER_DOGS,
				config.getEnableRefuseGroundFood());
		BD_REFUSE_GROUND_FOOD_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_refuse_ground_food_chance", BETTER_DOGS,
				config.getRefuseGroundFoodChance());
		BD_FRIENDLY_FIRE = registerBoolean("vanilla-outsider-better-dogs:bd_friendly_fire_protection", BETTER_DOGS,
				config.getEnableFriendlyFireProtection());
		BD_CREEPER_AWARENESS = registerBoolean("vanilla-outsider-better-dogs:bd_creeper_awareness",
				BETTER_DOGS, BetterDogsConfig.get().creeperAwareness);
		
		BD_PACK_FLANKING_TACTICS = registerBoolean("vanilla-outsider-better-dogs:bd_pack_flanking_tactics",
				BETTER_DOGS, BetterDogsConfig.get().enablePackFlankingTactics);
		BD_DEBUGGING = registerBoolean("vanilla-outsider-better-dogs:bd_debugging", BETTER_DOGS, false);
		BD_NEMESIS_SYSTEM = registerBoolean("vanilla-outsider-better-dogs:bd_nemesis_system", BETTER_DOGS, true);
		BD_NEMESIS_DURATION_DAYS = registerInteger("vanilla-outsider-better-dogs:bd_nemesis_duration_days", BETTER_DOGS, 3);



		// Aggressive
		BD_AGGRO_HEALTH = registerInteger("vanilla-outsider-better-dogs:bd_aggressive_health", BETTER_DOGS,
				(int) config.getAggressiveHealthBonus());
		BD_AGGRO_SPEED_PCT = registerInteger("vanilla-outsider-better-dogs:bd_aggro_speed_percent", BETTER_DOGS,
				(int) (config.getAggressiveSpeedModifier() * 100));
		BD_AGGRO_DMG_PCT = registerInteger("vanilla-outsider-better-dogs:bd_aggro_dmg_percent", BETTER_DOGS,
				(int) (config.getAggressiveDamageModifier() * 100));
		BD_AGGRO_FLEE_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_aggro_flee_chance", BETTER_DOGS,
				config.getAggressiveFleeChance());
		// Pacifist
		BD_PACI_HEALTH = registerInteger("vanilla-outsider-better-dogs:bd_paci_health", BETTER_DOGS,
				(int) config.getPacifistHealthBonus());
		BD_PACI_SPEED_PCT = registerInteger("vanilla-outsider-better-dogs:bd_paci_speed_percent", BETTER_DOGS,
				(int) (config.getPacifistSpeedModifier() * 100));
		BD_PACI_DMG_PCT = registerInteger("vanilla-outsider-better-dogs:bd_paci_dmg_percent", BETTER_DOGS,
				(int) (config.getPacifistDamageModifier() * 100));
		BD_PACI_KNOCKBACK_PCT = registerInteger("vanilla-outsider-better-dogs:bd_paci_knockback_percent", BETTER_DOGS,
				(int) (config.getPacifistKnockbackModifier() * 100));
		BD_PACI_FLEE_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_paci_flee_chance", BETTER_DOGS,
				config.getPacifistFleeChance());
		// Normal
		BD_NORMAL_HEALTH = registerInteger("vanilla-outsider-better-dogs:bd_normal_health", BETTER_DOGS,
				(int) config.getNormalHealthBonus());
		BD_NORMAL_SPEED_PCT = registerInteger("vanilla-outsider-better-dogs:bd_normal_speed_percent", BETTER_DOGS,
				(int) (config.getNormalSpeedModifier() * 100));
		BD_NORMAL_DMG_PCT = registerInteger("vanilla-outsider-better-dogs:bd_normal_dmg_percent", BETTER_DOGS,
				(int) (config.getNormalDamageModifier() * 100));
		BD_NORMAL_FLEE_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_normal_flee_chance", BETTER_DOGS,
				config.getNormalFleeChance());



		BD_AGGRO_FOLLOW_START = registerInteger("vanilla-outsider-better-dogs:bd_aggro_follow_start", BETTER_DOGS,
				(int) config.getAggressiveFollowStart());
		BD_AGGRO_CHASE_DIST = registerInteger("vanilla-outsider-better-dogs:bd_aggro_chase_dist", BETTER_DOGS,
				(int) config.getAggressiveChaseDistance());
		BD_AGGRO_DETECT_RANGE = registerInteger("vanilla-outsider-better-dogs:bd_aggro_detect_range", BETTER_DOGS,
				(int) config.getAggressiveDetectionRange());
		BD_PACI_FOLLOW_START = registerInteger("vanilla-outsider-better-dogs:bd_paci_follow_start", BETTER_DOGS,
				(int) config.getPacifistFollowStart());
		BD_NORMAL_FOLLOW_START = registerInteger("vanilla-outsider-better-dogs:bd_normal_follow_start", BETTER_DOGS,
				(int) config.getNormalFollowStart());

		// Misc Social
		BD_BABY_MISCHIEF_PERMILLE = registerInteger("vanilla-outsider-better-dogs:bd_baby_mischief_permille", BETTER_DOGS,
				(int) (config.getBabyMischiefChance() * 10));
		BD_HOWL_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_howl_chance", BETTER_DOGS, 10);
		BD_PACK_SPREAD = registerInteger("vanilla-outsider-better-dogs:bd_pack_spread", BETTER_DOGS, 20);
		BD_GIFT_FEED_THRESHOLD = registerInteger("vanilla-outsider-better-dogs:bd_gift_feed_threshold", BETTER_DOGS, config.getGiftFeedThreshold());
		BD_GIFT_INTERACTION_COOLDOWN = registerInteger("vanilla-outsider-better-dogs:bd_gift_interaction_cooldown", BETTER_DOGS, config.getGiftInteractionCooldown());
		BD_DEMERIT_ACCIDENTAL_ATTACKS = registerBoolean("vanilla-outsider-better-dogs:bd_demerit_accidental_attacks", BETTER_DOGS, config.getDemeritAccidentalAttacks());

		// Discipline
		BD_BLOOD_FEUD_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_blood_feud_percent", BETTER_DOGS,
				config.getBloodFeudChance());
		BD_BABY_RETALIATE_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_baby_retaliate_percent", BETTER_DOGS,
				config.getBabyRetaliationChance());



		BD_TERRITORIAL_RIVALRY = registerBoolean("vanilla-outsider-better-dogs:bd_territorial_rivalry", BETTER_DOGS, true);
		// Territorial Matrix (v3.4.6)
		BD_TERR_AA_WAR = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_aa_war", BETTER_DOGS,
				BetterDogsConfig.get().getTerrMatrixAAWar());
		BD_TERR_AA_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_aa_merge", BETTER_DOGS,
				BetterDogsConfig.get().getTerrMatrixAAMerge());
		BD_TERR_AN_WAR = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_an_war", BETTER_DOGS,
				BetterDogsConfig.get().getTerrMatrixANWar());
		BD_TERR_AN_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_an_merge", BETTER_DOGS,
				BetterDogsConfig.get().getTerrMatrixANMerge());
		BD_TERR_AP_WAR = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_ap_war", BETTER_DOGS,
				BetterDogsConfig.get().getTerrMatrixAPWar());
		BD_TERR_AP_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_ap_merge", BETTER_DOGS,
				BetterDogsConfig.get().getTerrMatrixAPMerge());
		BD_TERR_NN_WAR = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_nn_war", BETTER_DOGS,
				BetterDogsConfig.get().getTerrMatrixNNWar());
		BD_TERR_NN_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_nn_merge", BETTER_DOGS,
				BetterDogsConfig.get().getTerrMatrixNNMerge());
		BD_TERR_NP_WAR = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_np_war", BETTER_DOGS,
				BetterDogsConfig.get().getTerrMatrixNPWar());
		BD_TERR_NP_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_np_merge", BETTER_DOGS,
				BetterDogsConfig.get().getTerrMatrixNPMerge());
		BD_TERR_PP_WAR = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_pp_war", BETTER_DOGS,
				BetterDogsConfig.get().getTerrMatrixPPWar());
		BD_TERR_PP_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_pp_merge", BETTER_DOGS,
				BetterDogsConfig.get().getTerrMatrixPPMerge());
		BD_TERRITORIAL_FATAL_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_fatal_chance", BETTER_DOGS, 5);
		BD_TERRITORIAL_EXCLUSIVE_DISPUTES = registerBoolean("vanilla-outsider-better-dogs:bd_territorial_exclusive_disputes",
				BETTER_DOGS, true);
		BD_WILD_PERSONALITY_BEHAVIOR = registerBoolean("vanilla-outsider-better-dogs:bd_wild_personality_behavior",
				BETTER_DOGS, true);
		BD_TERRITORIAL_SEARCH_RADIUS = registerInteger("vanilla-outsider-better-dogs:bd_territorial_search_radius", BETTER_DOGS,
				96);



		BD_BREED_SAME_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_same_chance", BETTER_DOGS,
				config.getBreedingSameParentChance());
		BD_BREED_SAME_OTHER_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_same_other_chance", BETTER_DOGS,
				config.getBreedingSameParentOtherChance());
		BD_BREED_MIXED_DOMINANT_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_mixed_dominant_chance",
				BETTER_DOGS, config.getBreedingMixedDominantChance());
		BD_BREED_MIXED_RECESSIVE_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_mixed_recessive_chance",
				BETTER_DOGS, config.getBreedingMixedRecessiveChance());
		BD_BREED_DILUTED_NORMAL_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_diluted_normal_chance",
				BETTER_DOGS, config.getBreedingDilutedNormalChance());
		BD_BREED_DILUTED_OTHER_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_diluted_other_chance",
				BETTER_DOGS, config.getBreedingDilutedOtherChance());
		// Litter Size (v3.4.0)
		BD_WOLF_LITTER_MAX_SIZE = registerInteger("vanilla-outsider-better-dogs:bd_wolf_litter_max_size", BETTER_DOGS, 4);
		BD_WOLF_LITTER_EXTRA_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_wolf_litter_extra_chance", BETTER_DOGS,
				20);



		BD_SPAWN_NORMAL_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_spawn_normal_percent", BETTER_DOGS,
				config.spawnChanceNormal);
		BD_SPAWN_AGGRO_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_spawn_aggro_percent", BETTER_DOGS,
				config.spawnChanceAggressive);
		BD_SPAWN_PACI_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_spawn_paci_percent", BETTER_DOGS,
				config.spawnChancePacifist);
		BD_WOLF_PACK_CLUSTER_SIZE = registerInteger("vanilla-outsider-better-dogs:bd_wolf_pack_cluster_size",
				BETTER_DOGS, 8);
		BD_WOLF_SPAWN_DENSITY_BOOST = registerInteger("vanilla-outsider-better-dogs:bd_wolf_spawn_density_boost",
				BETTER_DOGS, 0);
		BD_WOLF_SPAWN_MULTIPLIER_PCT = registerInteger("vanilla-outsider-better-dogs:bd_wolf_spawn_multiplier_percent",
				BETTER_DOGS, (int)(config.getWolfSpawnMultiplier() * 100));

		// Guard Mode
		BD_PACIFIST_GUARD_BUFFS = registerBoolean("vanilla-outsider-better-dogs:bd_pacifist_guard_buffs", BETTER_DOGS, config.getPacifistGuardBuffs());
		BD_ENABLE_INBRED_CURING = registerBoolean("vanilla-outsider-better-dogs:bd_enable_inbred_curing", BETTER_DOGS, config.getEnableInbredCuring());
		BD_SHOW_RUNT_PARTICLES = registerBoolean("vanilla-outsider-better-dogs:bd_show_runt_particles", BETTER_DOGS, config.getShowRuntParticles());
		BD_GUARD_PATROL_RANGE_AGGRESSIVE = registerInteger("vanilla-outsider-better-dogs:bd_guard_patrol_range_aggressive", BETTER_DOGS, 12);
		BD_GUARD_PATROL_RANGE_NORMAL = registerInteger("vanilla-outsider-better-dogs:bd_guard_patrol_range_normal", BETTER_DOGS, 0);
		BD_GUARD_PATROL_RANGE_PACIFIST = registerInteger("vanilla-outsider-better-dogs:bd_guard_patrol_range_pacifist", BETTER_DOGS, 3);

		// Pack Spread Scaling
		BD_TAMED_PACK_SPREAD_MULTIPLIER = registerInteger("vanilla-outsider-better-dogs:bd_tamed_pack_spread_multiplier", BETTER_DOGS,
				config.getTamedPackSpreadMultiplier());
		BD_TAMED_PACK_SPREAD_MAX = registerInteger("vanilla-outsider-better-dogs:bd_tamed_pack_spread_max", BETTER_DOGS,
				config.getTamedPackSpreadMax());
		BD_WILD_PACK_SPREAD_MULTIPLIER = registerInteger("vanilla-outsider-better-dogs:bd_wild_pack_spread_multiplier", BETTER_DOGS,
				config.getWildPackSpreadMultiplier());
		BD_WILD_PACK_SPREAD_MAX = registerInteger("vanilla-outsider-better-dogs:bd_wild_pack_spread_max", BETTER_DOGS,
				config.getWildPackSpreadMax());

		BD_ALLOW_UNRESTRICTED_RIDING = registerBoolean("vanilla-outsider-better-dogs:bd_allow_unrestricted_dog_riding", BETTER_DOGS, false);
	}

        // Internal Registration Helpers
        private static GameRule<Boolean> registerBoolean(String id, GameRuleCategory category, boolean defaultValue) {
                return Registry.register(BuiltInRegistries.GAME_RULE, id, new GameRule<>(
                                category,
                                GameRuleType.BOOL,
                                BoolArgumentType.bool(),
                                GameRuleTypeVisitor::visitBoolean,
                                Codec.BOOL,
                                b -> b ? 1 : 0,
                                defaultValue,
                                FeatureFlagSet.of()));
        }

        private static GameRule<Integer> registerInteger(String id, GameRuleCategory category, int defaultValue) {
                return Registry.register(BuiltInRegistries.GAME_RULE, id, new GameRule<>(
                                category,
                                GameRuleType.INT,
                                IntegerArgumentType.integer(),
                                GameRuleTypeVisitor::visitInteger,
                                Codec.INT,
                                i -> i,
                                defaultValue,
                                FeatureFlagSet.of()));
        }
}
