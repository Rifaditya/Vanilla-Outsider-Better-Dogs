package net.vanillaoutsider.betterdogs.registry;

import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.flag.FeatureFlagSet;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

public class BetterDogsGameRules {

	public static final GameRuleCategory BETTER_DOGS_GENERAL = GameRuleCategory
			.register(Identifier.parse("vanilla-outsider-better-dogs:better_dogs_general"));
	public static final GameRuleCategory BETTER_DOGS_WAR = GameRuleCategory
			.register(Identifier.parse("vanilla-outsider-better-dogs:better_dogs_war"));
	public static final GameRuleCategory BETTER_DOGS_LITTER = GameRuleCategory
			.register(Identifier.parse("vanilla-outsider-better-dogs:better_dogs_litter"));
	public static final GameRuleCategory BETTER_DOGS_HEALTH = GameRuleCategory
			.register(Identifier.parse("vanilla-outsider-better-dogs:better_dogs_health"));
	public static final GameRuleCategory BETTER_DOGS_SOCIAL = GameRuleCategory
			.register(Identifier.parse("vanilla-outsider-better-dogs:better_dogs_social"));
	public static final GameRuleCategory BETTER_DOGS_SPAWNING = GameRuleCategory
			.register(Identifier.parse("vanilla-outsider-better-dogs:better_dogs_spawning"));

	// Helper to get Percentage (0-100 -> 0.0-1.0)
	public static double getPct(Level level, GameRule<Integer> rule) {
		if (level.isClientSide())
			return 0.0; // Safety
		return ((ServerLevel) level).getGameRules().get(rule) / 100.0;
	}

	// Helper to get Permille (0-1000 -> 0.0-1.0)
	public static float getProb(Level level, GameRule<Integer> rule) {
		if (level.isClientSide())
			return 0.0f;
		return ((ServerLevel) level).getGameRules().get(rule) / 1000.0f;
	}

	// Helper for direct percents/chance (0-100 -> 0.0-1.0)
	public static float getChance(Level level, GameRule<Integer> rule) {
		if (level.isClientSide())
			return 0.0f;
		return ((ServerLevel) level).getGameRules().get(rule) / 100.0f;
	}

	public static int getInt(Level level, GameRule<Integer> rule) {
		if (level.isClientSide())
			return 0;
		return ((ServerLevel) level).getGameRules().get(rule);
	}

	// Helper to get Decile float (0-N -> 0.0-N/10.0 blocks)
	public static float getDecileFloat(Level level, GameRule<Integer> rule) {
		if (level.isClientSide())
			return 0.0f;
		return ((ServerLevel) level).getGameRules().get(rule) / 10.0f;
	}

	public static boolean getBoolean(Level level, GameRule<Boolean> rule) {
		if (level.isClientSide())
			return false;
		return ((ServerLevel) level).getGameRules().get(rule);
	}

	// ====================================================================================
	// RULES
	// ====================================================================================

	// --- Mobs ---
	public static GameRule<Boolean> BD_STORM_ANXIETY;
	public static GameRule<Boolean> BD_CLIFF_SAFETY;
	public static GameRule<Boolean> BD_DOGS_EAT_RAW_FOOD;
	public static GameRule<Boolean> BD_DOGS_EAT_COOKED_FOOD;
	public static GameRule<Boolean> BD_DEBUGGING;

	// --- Player ---
	public static GameRule<Boolean> BD_FRIENDLY_FIRE;

	// --- Aggressive Personality ---
	public static GameRule<Integer> BD_AGGRO_HEALTH; // Direct HP
	public static GameRule<Integer> BD_AGGRO_SPEED_PCT; // %
	public static GameRule<Integer> BD_AGGRO_DMG_PCT; // %
	public static GameRule<Integer> BD_AGGRO_FOLLOW_START; // Blocks
	public static GameRule<Integer> BD_AGGRO_CHASE_DIST; // Blocks
	public static GameRule<Integer> BD_AGGRO_DETECT_RANGE; // Blocks

	// --- Pacifist Personality ---
	public static GameRule<Integer> BD_PACI_HEALTH; // Direct HP
	public static GameRule<Integer> BD_PACI_SPEED_PCT; // %
	public static GameRule<Integer> BD_PACI_DMG_PCT; // %
	public static GameRule<Integer> BD_PACI_KNOCKBACK_PCT; // %
	public static GameRule<Integer> BD_PACI_FOLLOW_START; // Blocks

	// --- Normal Personality ---
	public static GameRule<Integer> BD_NORMAL_FOLLOW_START; // Blocks
	public static GameRule<Integer> BD_NORMAL_SPEED_PCT; // %
	public static GameRule<Integer> BD_NORMAL_DMG_PCT; // %
	public static GameRule<Integer> BD_NORMAL_HEALTH; // Direct HP

	// --- Misc ---
	public static GameRule<Integer> BD_BABY_MISCHIEF_PERMILLE;
	public static GameRule<Integer> BD_HOWL_CHANCE;
	public static GameRule<Integer> BD_PACK_SPREAD; // Decile blocks (10 = 1.0 block)

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
	public static GameRule<Integer> BD_WOLF_SPAWN_DENSITY_BOOST; // % chance for reinforcement pack

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
	public static GameRule<Integer> BD_TAME_NORMAL_PERCENT;
	public static GameRule<Integer> BD_TAME_AGGRO_PERCENT;
	public static GameRule<Integer> BD_TAME_PACI_PERCENT;

	public static void register() {
		BetterDogsConfig config = BetterDogsConfig.get();

		BD_STORM_ANXIETY = registerBoolean("vanilla-outsider-better-dogs:bd_storm_anxiety", BETTER_DOGS_GENERAL,
				config.getEnableStormAnxiety());
		BD_CLIFF_SAFETY = registerBoolean("vanilla-outsider-better-dogs:bd_cliff_safety", BETTER_DOGS_GENERAL,
				config.getEnableCliffSafety());

		BD_DOGS_EAT_RAW_FOOD = registerBoolean("vanilla-outsider-better-dogs:bd_dogs_eat_raw_food", BETTER_DOGS_GENERAL,
				config.getEnableDogsEatRawGroundFood());
		BD_DOGS_EAT_COOKED_FOOD = registerBoolean("vanilla-outsider-better-dogs:bd_dogs_eat_cooked_food", BETTER_DOGS_GENERAL,
				config.getEnableDogsEatCookedGroundFood());

		BD_FRIENDLY_FIRE = registerBoolean("vanilla-outsider-better-dogs:bd_friendly_fire_protection", BETTER_DOGS_GENERAL,
				config.getEnableFriendlyFireProtection());

		BD_DEBUGGING = registerBoolean("vanilla-outsider-better-dogs:betterdogdebugging", BETTER_DOGS_GENERAL, false);

		// Aggressive
		BD_AGGRO_HEALTH = registerInteger("vanilla-outsider-better-dogs:bd_aggro_health", BETTER_DOGS_HEALTH,
				(int) config.getAggressiveHealthBonus());
		BD_AGGRO_SPEED_PCT = registerInteger("vanilla-outsider-better-dogs:bd_aggro_speed_percent", BETTER_DOGS_HEALTH,
				(int) (config.getAggressiveSpeedModifier() * 100));
		BD_AGGRO_DMG_PCT = registerInteger("vanilla-outsider-better-dogs:bd_aggro_dmg_percent", BETTER_DOGS_HEALTH,
				(int) (config.getAggressiveDamageModifier() * 100));
		BD_AGGRO_FOLLOW_START = registerInteger("vanilla-outsider-better-dogs:bd_aggro_follow_start", BETTER_DOGS_SOCIAL,
				(int) config.getAggressiveFollowStart());
		BD_AGGRO_CHASE_DIST = registerInteger("vanilla-outsider-better-dogs:bd_aggro_chase_dist", BETTER_DOGS_SOCIAL,
				(int) config.getAggressiveChaseDistance());
		BD_AGGRO_DETECT_RANGE = registerInteger("vanilla-outsider-better-dogs:bd_aggro_detect_range", BETTER_DOGS_SOCIAL,
				(int) config.getAggressiveDetectionRange());

		// Pacifist
		BD_PACI_HEALTH = registerInteger("vanilla-outsider-better-dogs:bd_paci_health", BETTER_DOGS_HEALTH,
				(int) config.getPacifistHealthBonus());
		BD_PACI_SPEED_PCT = registerInteger("vanilla-outsider-better-dogs:bd_paci_speed_percent", BETTER_DOGS_HEALTH,
				(int) (config.getPacifistSpeedModifier() * 100));
		BD_PACI_DMG_PCT = registerInteger("vanilla-outsider-better-dogs:bd_paci_dmg_percent", BETTER_DOGS_HEALTH,
				(int) (config.getPacifistDamageModifier() * 100));
		BD_PACI_KNOCKBACK_PCT = registerInteger("vanilla-outsider-better-dogs:bd_paci_knockback_percent", BETTER_DOGS_HEALTH,
				(int) (config.getPacifistKnockbackModifier() * 100));
		BD_PACI_FOLLOW_START = registerInteger("vanilla-outsider-better-dogs:bd_paci_follow_start", BETTER_DOGS_SOCIAL,
				(int) config.getPacifistFollowStart());

		// Normal
		BD_NORMAL_FOLLOW_START = registerInteger("vanilla-outsider-better-dogs:bd_normal_follow_start", BETTER_DOGS_SOCIAL,
				(int) config.getNormalFollowStart());
		BD_NORMAL_SPEED_PCT = registerInteger("vanilla-outsider-better-dogs:bd_normal_speed_percent", BETTER_DOGS_HEALTH,
				(int) (config.getNormalSpeedModifier() * 100));
		BD_NORMAL_DMG_PCT = registerInteger("vanilla-outsider-better-dogs:bd_normal_dmg_percent", BETTER_DOGS_HEALTH,
				(int) (config.getNormalDamageModifier() * 100));
		BD_NORMAL_HEALTH = registerInteger("vanilla-outsider-better-dogs:bd_normal_health", BETTER_DOGS_HEALTH,
				(int) config.getNormalHealthBonus());

		// Misc
		BD_BABY_MISCHIEF_PERMILLE = registerInteger("vanilla-outsider-better-dogs:bd_baby_mischief_permille", BETTER_DOGS_SOCIAL,
				(int) (config.getBabyMischiefChance() * 10));
		BD_HOWL_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_howl_chance", BETTER_DOGS_SOCIAL, 10); // 10 permille = 1%
		BD_PACK_SPREAD = registerInteger("vanilla-outsider-better-dogs:bd_pack_spread", BETTER_DOGS_SOCIAL, 20); // 20 deciles = 2.0 blocks

		// Discipline
		BD_BLOOD_FEUD_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_blood_feud_percent", BETTER_DOGS_SOCIAL,
				config.getBloodFeudChance());
		BD_BABY_RETALIATE_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_baby_retaliate_percent", BETTER_DOGS_SOCIAL,
				config.getBabyRetaliationChance());

		// Taming
		BD_TAME_NORMAL_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_tame_normal_percent", BETTER_DOGS_SPAWNING,
				config.tamingChanceNormal);
		BD_TAME_AGGRO_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_tame_aggro_percent", BETTER_DOGS_SPAWNING,
				config.tamingChanceAggressive);
		BD_TAME_PACI_PERCENT = registerInteger("vanilla-outsider-better-dogs:bd_tame_paci_percent", BETTER_DOGS_SPAWNING,
				config.tamingChancePacifist);

		// Breeding
		BD_BREED_SAME_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_same_chance", BETTER_DOGS_LITTER,
				config.getBreedingSameParentChance());
		BD_BREED_SAME_OTHER_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_same_other_chance", BETTER_DOGS_LITTER,
				config.getBreedingSameParentOtherChance());
		BD_BREED_MIXED_DOMINANT_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_mixed_dominant_chance",
				BETTER_DOGS_LITTER, config.getBreedingMixedDominantChance());
		BD_BREED_MIXED_RECESSIVE_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_mixed_recessive_chance",
				BETTER_DOGS_LITTER, config.getBreedingMixedRecessiveChance());
		BD_BREED_DILUTED_NORMAL_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_diluted_normal_chance",
				BETTER_DOGS_LITTER, config.getBreedingDilutedNormalChance());
		BD_BREED_DILUTED_OTHER_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_breed_diluted_other_chance",
				BETTER_DOGS_LITTER, config.getBreedingDilutedOtherChance());

		// Litter Size (v3.4.0)
		BD_WOLF_LITTER_MAX_SIZE = registerInteger("vanilla-outsider-better-dogs:bd_wolf_litter_max_size", BETTER_DOGS_LITTER, 4);
		BD_WOLF_LITTER_EXTRA_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_wolf_litter_extra_chance", BETTER_DOGS_LITTER,
				20);

		// Territorial
		BD_TERRITORIAL_RIVALRY = registerBoolean("vanilla-outsider-better-dogs:bd_territorial_rivalry", BETTER_DOGS_WAR, true);
		// Territorial Matrix (v3.4.6)
		BD_TERR_AA_WAR = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_aa_war", BETTER_DOGS_WAR,
				BetterDogsConfig.get().getTerrMatrixAAWar());
		BD_TERR_AA_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_aa_merge", BETTER_DOGS_WAR,
				BetterDogsConfig.get().getTerrMatrixAAMerge());
		BD_TERR_AN_WAR = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_an_war", BETTER_DOGS_WAR,
				BetterDogsConfig.get().getTerrMatrixANWar());
		BD_TERR_AN_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_an_merge", BETTER_DOGS_WAR,
				BetterDogsConfig.get().getTerrMatrixANMerge());
		BD_TERR_AP_WAR = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_ap_war", BETTER_DOGS_WAR,
				BetterDogsConfig.get().getTerrMatrixAPWar());
		BD_TERR_AP_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_ap_merge", BETTER_DOGS_WAR,
				BetterDogsConfig.get().getTerrMatrixAPMerge());
		BD_TERR_NN_WAR = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_nn_war", BETTER_DOGS_WAR,
				BetterDogsConfig.get().getTerrMatrixNNWar());
		BD_TERR_NN_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_nn_merge", BETTER_DOGS_WAR,
				BetterDogsConfig.get().getTerrMatrixNNMerge());
		BD_TERR_NP_WAR = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_np_war", BETTER_DOGS_WAR,
				BetterDogsConfig.get().getTerrMatrixNPWar());
		BD_TERR_NP_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_np_merge", BETTER_DOGS_WAR,
				BetterDogsConfig.get().getTerrMatrixNPMerge());
		BD_TERR_PP_WAR = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_pp_war", BETTER_DOGS_WAR,
				BetterDogsConfig.get().getTerrMatrixPPWar());
		BD_TERR_PP_MERGE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_matrix_pp_merge", BETTER_DOGS_WAR,
				BetterDogsConfig.get().getTerrMatrixPPMerge());
		BD_TERRITORIAL_FATAL_CHANCE = registerInteger("vanilla-outsider-better-dogs:bd_territorial_fatal_chance", BETTER_DOGS_WAR, 5);
		BD_TERRITORIAL_EXCLUSIVE_DISPUTES = registerBoolean("vanilla-outsider-better-dogs:bd_territorial_exclusive_disputes",
				BETTER_DOGS_WAR, true);
		BD_WILD_PERSONALITY_BEHAVIOR = registerBoolean("vanilla-outsider-better-dogs:bd_wild_personality_behavior",
				BETTER_DOGS_WAR, true);
		BD_TERRITORIAL_SEARCH_RADIUS = registerInteger("vanilla-outsider-better-dogs:bd_territorial_search_radius", BETTER_DOGS_WAR,
				96);
		BD_WOLF_PACK_CLUSTER_SIZE = registerInteger("vanilla-outsider-better-dogs:bd_wolf_pack_cluster_size",
				BETTER_DOGS_SPAWNING, 8);
		BD_WOLF_SPAWN_DENSITY_BOOST = registerInteger("vanilla-outsider-better-dogs:bd_wolf_spawn_density_boost",
				BETTER_DOGS_SPAWNING, 0);
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
                // Use standard integer range (Min value to Max Value usually, or 0 to Max?)
                // We'll allow negative values for stats penalties, so Integer.MIN_VALUE is
                // appropriate.
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
