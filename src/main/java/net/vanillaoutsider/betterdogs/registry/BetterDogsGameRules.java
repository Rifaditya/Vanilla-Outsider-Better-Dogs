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
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

public class BetterDogsGameRules {

    // Helper to get Percentage (0-100 -> 0.0-1.0)
    public static double getPct(Level level, GameRule<Integer> rule) {
        if (level.isClientSide()) return 0.0; // Safety
        return ((ServerLevel)level).getGameRules().get(rule) / 100.0;
    }

    // Helper to get Permille (0-1000 -> 0.0-1.0)
    public static float getProb(Level level, GameRule<Integer> rule) {
        if (level.isClientSide()) return 0.0f;
        return ((ServerLevel)level).getGameRules().get(rule) / 1000.0f;
    }

    // Helper for direct percents/chance (0-100 -> 0.0-1.0)
    public static float getChance(Level level, GameRule<Integer> rule) {
        if (level.isClientSide()) return 0.0f;
        return ((ServerLevel)level).getGameRules().get(rule) / 100.0f;
    }

    public static int getInt(Level level, GameRule<Integer> rule) {
        if (level.isClientSide()) return 0;
        return ((ServerLevel)level).getGameRules().get(rule);
    }

    public static boolean getBoolean(Level level, GameRule<Boolean> rule) {
        if (level.isClientSide()) return false;
        return ((ServerLevel)level).getGameRules().get(rule);
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
    public static GameRule<Integer> BD_AGGRO_HEALTH;        // Direct HP
    public static GameRule<Integer> BD_AGGRO_SPEED_PCT;     // %
    public static GameRule<Integer> BD_AGGRO_DMG_PCT;       // %
    public static GameRule<Integer> BD_AGGRO_FOLLOW_START;  // Blocks
    public static GameRule<Integer> BD_AGGRO_CHASE_DIST;    // Blocks
    public static GameRule<Integer> BD_AGGRO_DETECT_RANGE;  // Blocks

    // --- Pacifist Personality ---
    public static GameRule<Integer> BD_PACI_HEALTH;         // Direct HP
    public static GameRule<Integer> BD_PACI_SPEED_PCT;      // %
    public static GameRule<Integer> BD_PACI_DMG_PCT;        // %
    public static GameRule<Integer> BD_PACI_KNOCKBACK_PCT;  // %
    public static GameRule<Integer> BD_PACI_FOLLOW_START;   // Blocks

    // --- Normal Personality ---
    public static GameRule<Integer> BD_NORMAL_FOLLOW_START; // Blocks
    public static GameRule<Integer> BD_NORMAL_SPEED_PCT;    // %
    public static GameRule<Integer> BD_NORMAL_DMG_PCT;      // %
    public static GameRule<Integer> BD_NORMAL_HEALTH;       // Direct HP

    // --- Misc ---
    public static GameRule<Integer> BD_BABY_MISCHIEF_PERMILLE; 
    
    // --- Correction/Retaliation ---
    public static GameRule<Integer> BD_BLOOD_FEUD_PERCENT;
    public static GameRule<Integer> BD_BABY_RETALIATE_PERCENT;

    // --- Taming Chances ---
    public static GameRule<Integer> BD_TAME_NORMAL_PERCENT;
    public static GameRule<Integer> BD_TAME_AGGRO_PERCENT;
    public static GameRule<Integer> BD_TAME_PACI_PERCENT;


    public static void register() {
        BetterDogsConfig config = BetterDogsConfig.get();

        BD_STORM_ANXIETY = registerBoolean("bdStormAnxiety", GameRuleCategory.MOBS, config.getEnableStormAnxiety());
        BD_CLIFF_SAFETY = registerBoolean("bdCliffSafety", GameRuleCategory.MOBS, config.getEnableCliffSafety());

        BD_FRIENDLY_FIRE = registerBoolean("bdFriendlyFireProtection", GameRuleCategory.PLAYER, config.getEnableFriendlyFireProtection());

        // Aggressive
        BD_AGGRO_HEALTH = registerInteger("bdAggroHealth", GameRuleCategory.MOBS, (int)config.getAggressiveHealthBonus());
        BD_AGGRO_SPEED_PCT = registerInteger("bdAggroSpeedPercent", GameRuleCategory.MOBS, (int)(config.getAggressiveSpeedModifier() * 100));
        BD_AGGRO_DMG_PCT = registerInteger("bdAggroDmgPercent", GameRuleCategory.MOBS, (int)(config.getAggressiveDamageModifier() * 100));
        BD_AGGRO_FOLLOW_START = registerInteger("bdAggroFollowStart", GameRuleCategory.MOBS, (int)config.getAggressiveFollowStart());
        BD_AGGRO_CHASE_DIST = registerInteger("bdAggroChaseDist", GameRuleCategory.MOBS, (int)config.getAggressiveChaseDistance());
        BD_AGGRO_DETECT_RANGE = registerInteger("bdAggroDetectRange", GameRuleCategory.MOBS, (int)config.getAggressiveDetectionRange());

        // Pacifist
        BD_PACI_HEALTH = registerInteger("bdPaciHealth", GameRuleCategory.MOBS, (int)config.getPacifistHealthBonus());
        BD_PACI_SPEED_PCT = registerInteger("bdPaciSpeedPercent", GameRuleCategory.MOBS, (int)(config.getPacifistSpeedModifier() * 100));
        BD_PACI_DMG_PCT = registerInteger("bdPaciDmgPercent", GameRuleCategory.MOBS, (int)(config.getPacifistDamageModifier() * 100));
        BD_PACI_KNOCKBACK_PCT = registerInteger("bdPaciKnockbackPercent", GameRuleCategory.MOBS, (int)(config.getPacifistKnockbackModifier() * 100));
        BD_PACI_FOLLOW_START = registerInteger("bdPaciFollowStart", GameRuleCategory.MOBS, (int)config.getPacifistFollowStart());

        // Normal
        BD_NORMAL_FOLLOW_START = registerInteger("bdNormalFollowStart", GameRuleCategory.MOBS, (int)config.getNormalFollowStart());
        BD_NORMAL_SPEED_PCT = registerInteger("bdNormalSpeedPercent", GameRuleCategory.MOBS, (int)(config.getNormalSpeedModifier() * 100));
        BD_NORMAL_DMG_PCT = registerInteger("bdNormalDmgPercent", GameRuleCategory.MOBS, (int)(config.getNormalDamageModifier() * 100));
        BD_NORMAL_HEALTH = registerInteger("bdNormalHealth", GameRuleCategory.MOBS, (int)config.getNormalHealthBonus());

        // Misc
        BD_BABY_MISCHIEF_PERMILLE = registerInteger("bdBabyMischiefPermille", GameRuleCategory.MOBS, (int)(config.getBabyMischiefChance() * 10));

        // Discipline
        BD_BLOOD_FEUD_PERCENT = registerInteger("bdBloodFeudPercent", GameRuleCategory.MOBS, config.getBloodFeudChance());
        BD_BABY_RETALIATE_PERCENT = registerInteger("bdBabyRetaliatePercent", GameRuleCategory.MOBS, config.getBabyRetaliationChance());

        // Taming
        BD_TAME_NORMAL_PERCENT = registerInteger("bdTameNormalPercent", GameRuleCategory.MOBS, config.tamingChanceNormal);
        BD_TAME_AGGRO_PERCENT = registerInteger("bdTameAggroPercent", GameRuleCategory.MOBS, config.tamingChanceAggressive);
        BD_TAME_PACI_PERCENT = registerInteger("bdTamePaciPercent", GameRuleCategory.MOBS, config.tamingChancePacifist);
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
            FeatureFlagSet.of()
        ));
    }

    private static GameRule<Integer> registerInteger(String id, GameRuleCategory category, int defaultValue) {
        // Use standard integer range (Min value to Max Value usually, or 0 to Max?)
        // We'll allow negative values for stats penalties, so Integer.MIN_VALUE is appropriate.
        return Registry.register(BuiltInRegistries.GAME_RULE, id, new GameRule<>(
            category, 
            GameRuleType.INT, 
            IntegerArgumentType.integer(), 
            GameRuleTypeVisitor::visitInteger, 
            Codec.INT, 
            i -> i, 
            defaultValue, 
            FeatureFlagSet.of()
        ));
    }
}
