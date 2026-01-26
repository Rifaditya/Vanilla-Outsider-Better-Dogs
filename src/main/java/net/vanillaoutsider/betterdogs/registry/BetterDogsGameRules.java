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

        // --- Correction/Retaliation ---
        public static GameRule<Integer> BD_BLOOD_FEUD_PERCENT;
        public static GameRule<Integer> BD_BABY_RETALIATE_PERCENT;

        // --- Breeding Genetics ---
        public static GameRule<Integer> BD_BREED_SAME_CHANCE;
        public static GameRule<Integer> BD_BREED_SAME_OTHER_CHANCE;
        public static GameRule<Integer> BD_BREED_MIXED_DOMINANT_CHANCE;
        public static GameRule<Integer> BD_BREED_MIXED_RECESSIVE_CHANCE;
        public static GameRule<Integer> BD_BREED_DILUTED_NORMAL_CHANCE;
        public static GameRule<Integer> BD_BREED_DILUTED_OTHER_CHANCE;

        // --- Taming Chances ---
        public static GameRule<Integer> BD_TAME_NORMAL_PERCENT;
        public static GameRule<Integer> BD_TAME_AGGRO_PERCENT;
        public static GameRule<Integer> BD_TAME_PACI_PERCENT;

        public static void register() {
                BetterDogsConfig config = BetterDogsConfig.get();

                BD_STORM_ANXIETY = registerBoolean("bd_storm_anxiety", BETTER_DOGS, config.getEnableStormAnxiety());
                BD_CLIFF_SAFETY = registerBoolean("bd_cliff_safety", BETTER_DOGS, config.getEnableCliffSafety());

                BD_FRIENDLY_FIRE = registerBoolean("bd_friendly_fire_protection", BETTER_DOGS,
                                config.getEnableFriendlyFireProtection());

                // Aggressive
                BD_AGGRO_HEALTH = registerInteger("bd_aggro_health", BETTER_DOGS,
                                (int) config.getAggressiveHealthBonus());
                BD_AGGRO_SPEED_PCT = registerInteger("bd_aggro_speed_percent", BETTER_DOGS,
                                (int) (config.getAggressiveSpeedModifier() * 100));
                BD_AGGRO_DMG_PCT = registerInteger("bd_aggro_dmg_percent", BETTER_DOGS,
                                (int) (config.getAggressiveDamageModifier() * 100));
                BD_AGGRO_FOLLOW_START = registerInteger("bd_aggro_follow_start", BETTER_DOGS,
                                (int) config.getAggressiveFollowStart());
                BD_AGGRO_CHASE_DIST = registerInteger("bd_aggro_chase_dist", BETTER_DOGS,
                                (int) config.getAggressiveChaseDistance());
                BD_AGGRO_DETECT_RANGE = registerInteger("bd_aggro_detect_range", BETTER_DOGS,
                                (int) config.getAggressiveDetectionRange());

                // Pacifist
                BD_PACI_HEALTH = registerInteger("bd_paci_health", BETTER_DOGS,
                                (int) config.getPacifistHealthBonus());
                BD_PACI_SPEED_PCT = registerInteger("bd_paci_speed_percent", BETTER_DOGS,
                                (int) (config.getPacifistSpeedModifier() * 100));
                BD_PACI_DMG_PCT = registerInteger("bd_paci_dmg_percent", BETTER_DOGS,
                                (int) (config.getPacifistDamageModifier() * 100));
                BD_PACI_KNOCKBACK_PCT = registerInteger("bd_paci_knockback_percent", BETTER_DOGS,
                                (int) (config.getPacifistKnockbackModifier() * 100));
                BD_PACI_FOLLOW_START = registerInteger("bd_paci_follow_start", BETTER_DOGS,
                                (int) config.getPacifistFollowStart());

                // Normal
                BD_NORMAL_FOLLOW_START = registerInteger("bd_normal_follow_start", BETTER_DOGS,
                                (int) config.getNormalFollowStart());
                BD_NORMAL_SPEED_PCT = registerInteger("bd_normal_speed_percent", BETTER_DOGS,
                                (int) (config.getNormalSpeedModifier() * 100));
                BD_NORMAL_DMG_PCT = registerInteger("bd_normal_dmg_percent", BETTER_DOGS,
                                (int) (config.getNormalDamageModifier() * 100));
                BD_NORMAL_HEALTH = registerInteger("bd_normal_health", BETTER_DOGS,
                                (int) config.getNormalHealthBonus());

                // Misc
                BD_BABY_MISCHIEF_PERMILLE = registerInteger("bd_baby_mischief_permille", BETTER_DOGS,
                                (int) (config.getBabyMischiefChance() * 10));

                // Discipline
                BD_BLOOD_FEUD_PERCENT = registerInteger("bd_blood_feud_percent", BETTER_DOGS,
                                config.getBloodFeudChance());
                BD_BABY_RETALIATE_PERCENT = registerInteger("bd_baby_retaliate_percent", BETTER_DOGS,
                                config.getBabyRetaliationChance());

                // Taming
                BD_TAME_NORMAL_PERCENT = registerInteger("bd_tame_normal_percent", BETTER_DOGS,
                                config.tamingChanceNormal);
                BD_TAME_AGGRO_PERCENT = registerInteger("bd_tame_aggro_percent", BETTER_DOGS,
                                config.tamingChanceAggressive);
                BD_TAME_PACI_PERCENT = registerInteger("bd_tame_paci_percent", BETTER_DOGS,
                                config.tamingChancePacifist);

                // Breeding
                BD_BREED_SAME_CHANCE = registerInteger("bd_breed_same_chance", BETTER_DOGS,
                                config.getBreedingSameParentChance());
                BD_BREED_SAME_OTHER_CHANCE = registerInteger("bd_breed_same_other_chance", BETTER_DOGS,
                                config.getBreedingSameParentOtherChance());
                BD_BREED_MIXED_DOMINANT_CHANCE = registerInteger("bd_breed_mixed_dominant_chance", BETTER_DOGS,
                                config.getBreedingMixedDominantChance());
                BD_BREED_MIXED_RECESSIVE_CHANCE = registerInteger("bd_breed_mixed_recessive_chance", BETTER_DOGS,
                                config.getBreedingMixedRecessiveChance());
                BD_BREED_DILUTED_NORMAL_CHANCE = registerInteger("bd_breed_diluted_normal_chance", BETTER_DOGS,
                                config.getBreedingDilutedNormalChance());
                BD_BREED_DILUTED_OTHER_CHANCE = registerInteger("bd_breed_diluted_other_chance", BETTER_DOGS,
                                config.getBreedingDilutedOtherChance());
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
