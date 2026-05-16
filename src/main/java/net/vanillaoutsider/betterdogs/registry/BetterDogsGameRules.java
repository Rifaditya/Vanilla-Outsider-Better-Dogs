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

        public static final GameRuleCategory BETTER_DOGS = GameRuleCategory
                        .register(Identifier.parse("vanilla-outsider-better-dogs:better_dogs"));

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
        public static GameRule<Integer> BD_TERRITORIAL_YIELD_ON_ONE_SIDED_CHANCE;
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

                BD_STORM_ANXIETY = registerBoolean("bd.general.storm_anxiety", BETTER_DOGS, config.getEnableStormAnxiety());
                BD_CLIFF_SAFETY = registerBoolean("bd.general.cliff_safety", BETTER_DOGS, config.getEnableCliffSafety());

                BD_DOGS_EAT_RAW_FOOD = registerBoolean("bd.general.eat_raw", BETTER_DOGS,
                                config.getEnableDogsEatRawGroundFood());
                BD_DOGS_EAT_COOKED_FOOD = registerBoolean("bd.general.eat_cooked", BETTER_DOGS,
                                config.getEnableDogsEatCookedGroundFood());

                BD_FRIENDLY_FIRE = registerBoolean("bd.general.friendly_fire", BETTER_DOGS,
                                config.getEnableFriendlyFireProtection());

                BD_DEBUGGING = registerBoolean("bd.debug.enabled", BETTER_DOGS, false);

                // Aggressive
                BD_AGGRO_HEALTH = registerInteger("bd.personality.aggro.hp", BETTER_DOGS,
                                (int) config.getAggressiveHealthBonus());
                BD_AGGRO_SPEED_PCT = registerInteger("bd.personality.aggro.speed", BETTER_DOGS,
                                (int) (config.getAggressiveSpeedModifier() * 100));
                BD_AGGRO_DMG_PCT = registerInteger("bd.personality.aggro.dmg", BETTER_DOGS,
                                (int) (config.getAggressiveDamageModifier() * 100));
                BD_AGGRO_FOLLOW_START = registerInteger("bd.personality.aggro.follow", BETTER_DOGS,
                                (int) config.getAggressiveFollowStart());
                BD_AGGRO_CHASE_DIST = registerInteger("bd.personality.aggro.chase", BETTER_DOGS,
                                (int) config.getAggressiveChaseDistance());
                BD_AGGRO_DETECT_RANGE = registerInteger("bd.personality.aggro.detect", BETTER_DOGS,
                                (int) config.getAggressiveDetectionRange());

                // Pacifist
                BD_PACI_HEALTH = registerInteger("bd.personality.paci.hp", BETTER_DOGS,
                                (int) config.getPacifistHealthBonus());
                BD_PACI_SPEED_PCT = registerInteger("bd.personality.paci.speed", BETTER_DOGS,
                                (int) (config.getPacifistSpeedModifier() * 100));
                BD_PACI_DMG_PCT = registerInteger("bd.personality.paci.dmg", BETTER_DOGS,
                                (int) (config.getPacifistDamageModifier() * 100));
                BD_PACI_KNOCKBACK_PCT = registerInteger("bd.personality.paci.knockback", BETTER_DOGS,
                                (int) (config.getPacifistKnockbackModifier() * 100));
                BD_PACI_FOLLOW_START = registerInteger("bd.personality.paci.follow", BETTER_DOGS,
                                (int) config.getPacifistFollowStart());

                // Normal
                BD_NORMAL_FOLLOW_START = registerInteger("bd.personality.normal.follow", BETTER_DOGS,
                                (int) config.getNormalFollowStart());
                BD_NORMAL_SPEED_PCT = registerInteger("bd.personality.normal.speed", BETTER_DOGS,
                                (int) (config.getNormalSpeedModifier() * 100));
                BD_NORMAL_DMG_PCT = registerInteger("bd.personality.normal.dmg", BETTER_DOGS,
                                (int) (config.getNormalDamageModifier() * 100));
                BD_NORMAL_HEALTH = registerInteger("bd.personality.normal.hp", BETTER_DOGS,
                                (int) config.getNormalHealthBonus());

                // Misc
                BD_BABY_MISCHIEF_PERMILLE = registerInteger("bd.mischief.baby_chance", BETTER_DOGS,
                                (int) (config.getBabyMischiefChance() * 10));
                BD_HOWL_CHANCE = registerInteger("bd.social.howl", BETTER_DOGS, 10); // 10 permille = 1%
                BD_PACK_SPREAD = registerInteger("bd.social.pack_spread", BETTER_DOGS, 20); // 20 deciles = 2.0 blocks

                // Discipline
                BD_BLOOD_FEUD_PERCENT = registerInteger("bd.social.blood_feud", BETTER_DOGS,
                                config.getBloodFeudChance());
                BD_BABY_RETALIATE_PERCENT = registerInteger("bd.mischief.retaliate", BETTER_DOGS,
                                config.getBabyRetaliationChance());

                // Taming
                BD_TAME_NORMAL_PERCENT = registerInteger("bd.taming.normal", BETTER_DOGS,
                                config.tamingChanceNormal);
                BD_TAME_AGGRO_PERCENT = registerInteger("bd.taming.aggro", BETTER_DOGS,
                                config.tamingChanceAggressive);
                BD_TAME_PACI_PERCENT = registerInteger("bd.taming.paci", BETTER_DOGS,
                                config.tamingChancePacifist);

                // Breeding
                BD_BREED_SAME_CHANCE = registerInteger("bd.breeding.inherit", BETTER_DOGS,
                                config.getBreedingSameParentChance());
                BD_BREED_SAME_OTHER_CHANCE = registerInteger("bd.breeding.variant", BETTER_DOGS,
                                config.getBreedingSameParentOtherChance());
                BD_BREED_MIXED_DOMINANT_CHANCE = registerInteger("bd.breeding.dominant", BETTER_DOGS,
                                config.getBreedingMixedDominantChance());
                BD_BREED_MIXED_RECESSIVE_CHANCE = registerInteger("bd.breeding.recessive", BETTER_DOGS,
                                config.getBreedingMixedRecessiveChance());
                BD_BREED_DILUTED_NORMAL_CHANCE = registerInteger("bd.breeding.diluted_normal", BETTER_DOGS,
                                config.getBreedingDilutedNormalChance());
                BD_BREED_DILUTED_OTHER_CHANCE = registerInteger("bd.breeding.diluted_variant", BETTER_DOGS,
                                config.getBreedingDilutedOtherChance());

                // Litter Size (v3.4.0)
                BD_WOLF_LITTER_MAX_SIZE = registerInteger("bd.breeding.litter_max", BETTER_DOGS, 4);
                BD_WOLF_LITTER_EXTRA_CHANCE = registerInteger("bd.breeding.litter_extra", BETTER_DOGS, 20);

                // Territorial
                BD_TERRITORIAL_RIVALRY = registerBoolean("bd.territory.main.rivalry", BETTER_DOGS, true);
                // Territorial Matrix (v3.4.6)
                BD_TERR_AA_WAR = registerInteger("bd.territory.matrix.aa_war", BETTER_DOGS, 80);
                BD_TERR_AA_MERGE = registerInteger("bd.territory.matrix.aa_merge", BETTER_DOGS, 10);
                BD_TERR_AN_WAR = registerInteger("bd.territory.matrix.an_war", BETTER_DOGS, 50);
                BD_TERR_AN_MERGE = registerInteger("bd.territory.matrix.an_merge", BETTER_DOGS, 40);
                BD_TERR_AP_WAR = registerInteger("bd.territory.matrix.ap_war", BETTER_DOGS, 10);
                BD_TERR_AP_MERGE = registerInteger("bd.territory.matrix.ap_merge", BETTER_DOGS, 50);
                BD_TERR_NN_WAR = registerInteger("bd.territory.matrix.nn_war", BETTER_DOGS, 20);
                BD_TERR_NN_MERGE = registerInteger("bd.territory.matrix.nn_merge", BETTER_DOGS, 50);
                BD_TERR_NP_WAR = registerInteger("bd.territory.matrix.np_war", BETTER_DOGS, 5);
                BD_TERR_NP_MERGE = registerInteger("bd.territory.matrix.np_merge", BETTER_DOGS, 45);
                BD_TERR_PP_WAR = registerInteger("bd.territory.matrix.pp_war", BETTER_DOGS, 0);
                BD_TERR_PP_MERGE = registerInteger("bd.territory.matrix.pp_merge", BETTER_DOGS, 50);
                BD_TERRITORIAL_FATAL_CHANCE = registerInteger("bd.territory.main.fatal", BETTER_DOGS, 5);
                BD_TERRITORIAL_YIELD_ON_ONE_SIDED_CHANCE = registerInteger("bd.territory.main.yield_chance", BETTER_DOGS, 50);
                BD_TERRITORIAL_EXCLUSIVE_DISPUTES = registerBoolean("bd.territory.main.exclusive", BETTER_DOGS, true);
                BD_WILD_PERSONALITY_BEHAVIOR = registerBoolean("bd.territory.main.wild_ai", BETTER_DOGS, true);
                BD_TERRITORIAL_SEARCH_RADIUS = registerInteger("bd.territory.main.search_radius", BETTER_DOGS, 96);
                BD_WOLF_PACK_CLUSTER_SIZE = registerInteger("bd.spawning.cluster_size", BETTER_DOGS, 8);
                BD_WOLF_SPAWN_DENSITY_BOOST = registerInteger("bd.spawning.density_boost", BETTER_DOGS, 0);
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
