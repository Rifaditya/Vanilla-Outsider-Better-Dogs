// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dasik.social.api.SocialLinks;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dedicated single-purpose Brigadier command tree builder and executor for /betterdogs and /bd.
 */
public class BetterDogsCommand {

    private static final Map<String, GameRules.Key<GameRules.BooleanValue>> BOOL_MAP = new LinkedHashMap<>();
    private static final Map<String, GameRules.Key<GameRules.IntegerValue>> INT_MAP = new LinkedHashMap<>();
    private static final Map<String, Boolean> BOOL_DEFAULTS = new LinkedHashMap<>();
    private static final Map<String, Integer> INT_DEFAULTS = new LinkedHashMap<>();
    private static final Map<String, int[]> INT_BOUNDS = new LinkedHashMap<>();
    private static boolean populated = false;

    static {
        ensureRulesPopulated();
    }

    private static synchronized void ensureRulesPopulated() {
        if (BetterDogsGameRules.BD_STORM_ANXIETY == null) {
            return;
        }
        if (!populated
                || BOOL_MAP.get("bd_storm_anxiety") != BetterDogsGameRules.BD_STORM_ANXIETY
                || INT_MAP.get("bd_interceptor_owner_health_threshold") != BetterDogsGameRules.BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD) {
            populateRules();
            populated = !BOOL_MAP.isEmpty() && !INT_MAP.isEmpty();
        }
    }

    private static void populateRules() {
        BOOL_MAP.clear();
        INT_MAP.clear();
        BOOL_DEFAULTS.clear();
        INT_DEFAULTS.clear();
        INT_BOUNDS.clear();

        registerBool("bd_storm_anxiety", BetterDogsGameRules.BD_STORM_ANXIETY, true);
        registerBool("bd_actionbar_feedback", BetterDogsGameRules.BD_ACTIONBAR_FEEDBACK, false);
        registerBool("bd_creeper_awareness", BetterDogsGameRules.BD_CREEPER_AWARENESS, true);
        registerBool("bd_creeper_evasion_enabled", BetterDogsGameRules.BD_CREEPER_EVASION_ENABLED, true);
        registerBool("bd_cliff_safety", BetterDogsGameRules.BD_CLIFF_SAFETY, true);
        registerBool("bd_flee_low_health", BetterDogsGameRules.BD_FLEE_LOW_HEALTH, true);
        registerBool("bd_dogs_eat_raw_food", BetterDogsGameRules.BD_DOGS_EAT_RAW_FOOD, true);
        registerBool("bd_dogs_eat_cooked_food", BetterDogsGameRules.BD_DOGS_EAT_COOKED_FOOD, true);
        registerBool("bd_enable_refuse_ground_food", BetterDogsGameRules.BD_ENABLE_REFUSE_GROUND_FOOD, true);
        registerBool("bd_debugging", BetterDogsGameRules.BD_DEBUGGING, false);
        registerBool("bd_nemesis_system", BetterDogsGameRules.BD_NEMESIS_SYSTEM, true);
        registerBool("bd_favorite_treats", BetterDogsGameRules.BD_FAVORITE_TREATS, true);
        registerBool("bd_pack_flanking_tactics", BetterDogsGameRules.BD_PACK_FLANKING_TACTICS, true);
        registerBool("bd_flanking_raycast_check", BetterDogsGameRules.BD_FLANKING_RAYCAST_CHECK, true);
        registerBool("bd_sync_owner_teleport", BetterDogsGameRules.BD_SYNC_OWNER_TELEPORT, true);
        registerBool("bd_fast_travel_catchup", BetterDogsGameRules.BD_FAST_TRAVEL_CATCHUP, true);
        registerBool("bd_friendly_fire_protection", BetterDogsGameRules.BD_FRIENDLY_FIRE, true);
        registerBool("bd_demerit_accidental_attacks", BetterDogsGameRules.BD_DEMERIT_ACCIDENTAL_ATTACKS, true);
        registerBool("bd_territorial_rivalry", BetterDogsGameRules.BD_TERRITORIAL_RIVALRY, true);
        registerBool("bd_territorial_exclusive_disputes", BetterDogsGameRules.BD_TERRITORIAL_EXCLUSIVE_DISPUTES, true);
        registerBool("bd_wild_personality_behavior", BetterDogsGameRules.BD_WILD_PERSONALITY_BEHAVIOR, true);
        registerBool("bd_wolf_spawn_expanded_biomes", BetterDogsGameRules.BD_WOLF_SPAWN_EXPANDED_BIOMES, true);
        registerBool("bd_dynamic_climate_variants", BetterDogsGameRules.BD_DYNAMIC_CLIMATE_VARIANTS, true);

        registerInt("bd_refuse_ground_food_chance", BetterDogsGameRules.BD_REFUSE_GROUND_FOOD_CHANCE, 30);
        registerInt("bd_nemesis_duration_days", BetterDogsGameRules.BD_NEMESIS_DURATION_DAYS, 3);
        registerInt("bd_horn_command_range", BetterDogsGameRules.BD_HORN_COMMAND_RANGE, 64);
        registerInt("bd_horn_pathing_timeout", BetterDogsGameRules.BD_HORN_PATHING_TIMEOUT, 300);
        registerInt("bd_horn_override_duration", BetterDogsGameRules.BD_HORN_OVERRIDE_DURATION, 600);
        registerInt("bd_aggressive_health", BetterDogsGameRules.BD_AGGRO_HEALTH, -10);
        registerInt("bd_aggro_speed_percent", BetterDogsGameRules.BD_AGGRO_SPEED_PCT, 15);
        registerInt("bd_aggro_dmg_percent", BetterDogsGameRules.BD_AGGRO_DMG_PCT, 15);
        registerInt("bd_aggro_follow_start", BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 25);
        registerInt("bd_aggro_chase_dist", BetterDogsGameRules.BD_AGGRO_CHASE_DIST, 50);
        registerInt("bd_aggro_detect_range", BetterDogsGameRules.BD_AGGRO_DETECT_RANGE, 20);
        registerInt("bd_aggro_flee_chance", BetterDogsGameRules.BD_AGGRO_FLEE_CHANCE, 10);
        registerInt("bd_paci_health", BetterDogsGameRules.BD_PACI_HEALTH, 20);
        registerInt("bd_paci_speed_percent", BetterDogsGameRules.BD_PACI_SPEED_PCT, -10);
        registerInt("bd_paci_dmg_percent", BetterDogsGameRules.BD_PACI_DMG_PCT, -15);
        registerInt("bd_paci_knockback_percent", BetterDogsGameRules.BD_PACI_KNOCKBACK_PCT, 50);
        registerInt("bd_paci_follow_start", BetterDogsGameRules.BD_PACI_FOLLOW_START, 5);
        registerInt("bd_paci_flee_chance", BetterDogsGameRules.BD_PACI_FLEE_CHANCE, 100);
        registerInt("bd_normal_follow_start", BetterDogsGameRules.BD_NORMAL_FOLLOW_START, 10);
        registerInt("bd_normal_speed_percent", BetterDogsGameRules.BD_NORMAL_SPEED_PCT, 0);
        registerInt("bd_normal_dmg_percent", BetterDogsGameRules.BD_NORMAL_DMG_PCT, 0);
        registerInt("bd_normal_health", BetterDogsGameRules.BD_NORMAL_HEALTH, 0);
        registerInt("bd_normal_flee_chance", BetterDogsGameRules.BD_NORMAL_FLEE_CHANCE, 50);
        registerInt("bd_baby_mischief_permille", BetterDogsGameRules.BD_BABY_MISCHIEF_PERMILLE, 25);
        registerInt("bd_howl_chance", BetterDogsGameRules.BD_HOWL_CHANCE, 10);
        registerInt("bd_pack_spread", BetterDogsGameRules.BD_PACK_SPREAD, 8);
        registerInt("bd_gift_feed_threshold", BetterDogsGameRules.BD_GIFT_FEED_THRESHOLD, 3);
        registerInt("bd_gift_interaction_cooldown", BetterDogsGameRules.BD_GIFT_INTERACTION_COOLDOWN, 6000);
        registerInt("bd_wolf_min_scale_percent", BetterDogsGameRules.BD_WOLF_MIN_SCALE_PERCENT, 70);
        registerInt("bd_wolf_max_scale_percent", BetterDogsGameRules.BD_WOLF_MAX_SCALE_PERCENT, 145);
        registerInt("bd_blood_feud_percent", BetterDogsGameRules.BD_BLOOD_FEUD_PERCENT, 5);
        registerInt("bd_baby_retaliate_percent", BetterDogsGameRules.BD_BABY_RETALIATE_PERCENT, 50);
        registerInt("bd_terr_aa_war", BetterDogsGameRules.BD_TERR_AA_WAR, 80);
        registerInt("bd_terr_aa_merge", BetterDogsGameRules.BD_TERR_AA_MERGE, 0);
        registerInt("bd_terr_an_war", BetterDogsGameRules.BD_TERR_AN_WAR, 50);
        registerInt("bd_terr_an_merge", BetterDogsGameRules.BD_TERR_AN_MERGE, 0);
        registerInt("bd_terr_ap_war", BetterDogsGameRules.BD_TERR_AP_WAR, 20);
        registerInt("bd_terr_ap_merge", BetterDogsGameRules.BD_TERR_AP_MERGE, 80);
        registerInt("bd_terr_nn_war", BetterDogsGameRules.BD_TERR_NN_WAR, 20);
        registerInt("bd_terr_nn_merge", BetterDogsGameRules.BD_TERR_NN_MERGE, 80);
        registerInt("bd_terr_np_war", BetterDogsGameRules.BD_TERR_NP_WAR, 0);
        registerInt("bd_terr_np_merge", BetterDogsGameRules.BD_TERR_NP_MERGE, 100);
        registerInt("bd_terr_pp_war", BetterDogsGameRules.BD_TERR_PP_WAR, 0);
        registerInt("bd_terr_pp_merge", BetterDogsGameRules.BD_TERR_PP_MERGE, 100);
        registerInt("bd_territorial_fatal_chance", BetterDogsGameRules.BD_TERRITORIAL_FATAL_CHANCE, 10);
        registerInt("bd_territorial_search_radius", BetterDogsGameRules.BD_TERRITORIAL_SEARCH_RADIUS, 32);
        registerInt("bd_wolf_pack_cluster_size", BetterDogsGameRules.BD_WOLF_PACK_CLUSTER_SIZE, 6);
        registerInt("bd_wolf_spawn_density_boost", BetterDogsGameRules.BD_WOLF_SPAWN_DENSITY_BOOST, 100);
        registerInt("bd_wolf_spawn_multiplier_percent", BetterDogsGameRules.BD_WOLF_SPAWN_MULTIPLIER_PCT, 150);
        registerInt("bd_wolf_spawn_group_min", BetterDogsGameRules.BD_WOLF_SPAWN_GROUP_MIN, 4);
        registerInt("bd_wolf_spawn_group_max", BetterDogsGameRules.BD_WOLF_SPAWN_GROUP_MAX, 8);
        registerInt("bd_spawn_normal_percent", BetterDogsGameRules.BD_SPAWN_NORMAL_PERCENT, 60);
        registerInt("bd_spawn_aggro_percent", BetterDogsGameRules.BD_SPAWN_AGGRO_PERCENT, 20);
        registerInt("bd_spawn_paci_percent", BetterDogsGameRules.BD_SPAWN_PACI_PERCENT, 20);
        registerInt("bd_breed_same_chance", BetterDogsGameRules.BD_BREED_SAME_CHANCE, 80);
        registerInt("bd_breed_same_other_chance", BetterDogsGameRules.BD_BREED_SAME_OTHER_CHANCE, 10);
        registerInt("bd_breed_mixed_dominant_chance", BetterDogsGameRules.BD_BREED_MIXED_DOMINANT_CHANCE, 40);
        registerInt("bd_breed_mixed_recessive_chance", BetterDogsGameRules.BD_BREED_MIXED_RECESSIVE_CHANCE, 40);
        registerInt("bd_breed_diluted_normal_chance", BetterDogsGameRules.BD_BREED_DILUTED_NORMAL_CHANCE, 50);
        registerInt("bd_breed_diluted_other_chance", BetterDogsGameRules.BD_BREED_DILUTED_OTHER_CHANCE, 25);
        registerInt("bd_wolf_litter_max_size", BetterDogsGameRules.BD_WOLF_LITTER_MAX_SIZE, 4);

        // Pack Play Sparring & Timeout Penalty
        registerBool("bd_dog_play_fighting", BetterDogsGameRules.BD_DOG_PLAY_FIGHTING, true);
        registerBool("dogplayfighting", BetterDogsGameRules.BD_DOG_PLAY_FIGHTING, true);
        registerInt("bd_dog_play_fight_frequency", BetterDogsGameRules.BD_DOG_PLAY_FIGHT_FREQUENCY, 400, 0, 72000);
        registerInt("playfightfrequency", BetterDogsGameRules.BD_DOG_PLAY_FIGHT_FREQUENCY, 400, 0, 72000);
        registerInt("bd_dog_play_fight_chance", BetterDogsGameRules.BD_DOG_PLAY_FIGHT_CHANCE, 100, 0, 100);
        registerInt("playfightchance", BetterDogsGameRules.BD_DOG_PLAY_FIGHT_CHANCE, 100, 0, 100);
        registerInt("bd_play_penalty_minutes", BetterDogsGameRules.BD_PLAY_PENALTY_MINUTES, 10);
        registerInt("playpenaltyminutes", BetterDogsGameRules.BD_PLAY_PENALTY_MINUTES, 10);

        // Canine Physical Genetics & Scale Variation
        registerBool("bd_dog_genetics_enabled", BetterDogsGameRules.BD_DOG_GENETICS_ENABLED, true);
        registerBool("doggeneticsenabled", BetterDogsGameRules.BD_DOG_GENETICS_ENABLED, true);
        registerInt("bd_max_scale_deviation", BetterDogsGameRules.BD_MAX_SCALE_DEVIATION, 30, 0, 100);
        registerInt("maxscaledeviation", BetterDogsGameRules.BD_MAX_SCALE_DEVIATION, 30, 0, 100);

        // Personality Archetypes (BL-BD-013)
        registerBool("bd_personality_archetypes_enabled", BetterDogsGameRules.BD_ARCHETYPES_ENABLED, true);
        registerBool("personalityarchetypesenabled", BetterDogsGameRules.BD_ARCHETYPES_ENABLED, true);
        registerInt("bd_personality_distribution_mode", BetterDogsGameRules.BD_ARCHETYPE_DISTRIBUTION_MODE, 0, 0, 1);
        registerInt("personalitydistributionmode", BetterDogsGameRules.BD_ARCHETYPE_DISTRIBUTION_MODE, 0, 0, 1);

        // Canine Explosive Reflex (BL-BD-006)
        registerBool("bd_canine_explosive_reflex_enabled", BetterDogsGameRules.BD_CANINE_EXPLOSIVE_REFLEX_ENABLED, true);
        registerBool("canineexplosivereflexenabled", BetterDogsGameRules.BD_CANINE_EXPLOSIVE_REFLEX_ENABLED, true);
        registerInt("bd_hazard_flee_radius", BetterDogsGameRules.BD_HAZARD_FLEE_RADIUS, 14, 4, 64);
        registerInt("hazardfleeradius", BetterDogsGameRules.BD_HAZARD_FLEE_RADIUS, 14, 4, 64);
        registerInt("bd_flee_speed_multiplier", BetterDogsGameRules.BD_FLEE_SPEED_MULTIPLIER, 170, 100, 300);
        registerInt("fleespeedmultiplier", BetterDogsGameRules.BD_FLEE_SPEED_MULTIPLIER, 170, 100, 300);

        // Ballistic Evasion Reflex (BL-BD-020)
        registerBool("bd_ballistic_evasion_enabled", BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED, true);
        registerBool("ballisticevasionenabled", BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED, true);
        registerInt("bd_evasion_reaction_ticks", BetterDogsGameRules.BD_EVASION_REACTION_TICKS, 20, 5, 60);
        registerInt("evasionreactionticks", BetterDogsGameRules.BD_EVASION_REACTION_TICKS, 20, 5, 60);
        registerInt("bd_evasion_lateral_impulse", BetterDogsGameRules.BD_EVASION_LATERAL_IMPULSE, 85, 20, 250);
        registerInt("evasionlateralimpulse", BetterDogsGameRules.BD_EVASION_LATERAL_IMPULSE, 85, 20, 250);

        // Ballistic Interceptor (BL-BD-021)
        registerBool("bd_projectile_intercept", BetterDogsGameRules.BD_PROJECTILE_INTERCEPT, true);
        registerBool("projectileintercept", BetterDogsGameRules.BD_PROJECTILE_INTERCEPT, true);
        registerInt("bd_interceptor_owner_health_threshold", BetterDogsGameRules.BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD, 10, 0, 1000);
        registerInt("interceptorownerhealththreshold", BetterDogsGameRules.BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD, 10, 0, 1000);
    }

    private static void registerBool(String name, GameRules.Key<GameRules.BooleanValue> key, boolean def) {
        if (key != null) {
            BOOL_MAP.put(name, key);
            BOOL_DEFAULTS.put(name, def);
        }
    }

    private static void registerInt(String name, GameRules.Key<GameRules.IntegerValue> key, int def) {
        if (key != null) {
            INT_MAP.put(name, key);
            INT_DEFAULTS.put(name, def);
        }
    }

    private static void registerInt(String name, GameRules.Key<GameRules.IntegerValue> key, int def, int min, int max) {
        registerInt(name, key, def);
        if (key != null) {
            INT_BOUNDS.put(name, new int[]{min, max});
        }
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        ensureRulesPopulated();
        LiteralArgumentBuilder<CommandSourceStack> root = buildCommandTree("betterdogs");
        LiteralArgumentBuilder<CommandSourceStack> alias = buildCommandTree("bd");
        dispatcher.register(root);
        dispatcher.register(alias);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildCommandTree(String literalName) {
        return Commands.literal(literalName)
                .executes(context -> {
                    context.getSource().sendSuccess(() -> Component.literal(
                            "§6[Better Dogs]§r Type §a/" + literalName + " help§r for command reference."), false);
                    return 1;
                })
                .then(Commands.literal("help")
                        .executes(context -> executeHelp(context.getSource(), literalName)))
                .then(Commands.literal("status")
                        .executes(context -> executeStatus(context.getSource())))
                .then(Commands.literal("inspect")
                        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
                                .executes(context -> net.vanillaoutsider.betterdogs.util.WolfCommandHelper.inspectTargets(
                                        context.getSource(),
                                        net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets")
                                ))))
                .then(Commands.literal("get")
                        .then(Commands.argument("rule", StringArgumentType.word())
                                .suggests(CommandSuggestionsHelper::suggestRules)
                                .executes(context -> executeGet(context.getSource(), StringArgumentType.getString(context, "rule")))))
                .then(Commands.literal("set")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("rule", StringArgumentType.word())
                                .suggests(CommandSuggestionsHelper::suggestRules)
                                .then(Commands.argument("value", StringArgumentType.word())
                                        .suggests(CommandSuggestionsHelper::suggestValues)
                                        .executes(context -> executeSet(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "rule"),
                                                StringArgumentType.getString(context, "value")
                                        )))))
                .then(Commands.literal("reset")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> executeReset(context.getSource())))
                .then(Commands.literal("reload")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> executeReload(context.getSource())));
    }

    private static int executeHelp(CommandSourceStack source, String literalName) {
        String kofi = SocialLinks.getKofiUri() != null ? SocialLinks.KOFI_URL : SocialLinks.KOFI_URL;
        source.sendSuccess(() -> Component.literal(
                "§6--- Vanilla Outsider: Better Dogs Commands ---§r\n" +
                "§a/" + literalName + " status§r - Display categorized summary of companion GameRules\n" +
                "§a/" + literalName + " inspect <targets>§r - Inspect physical traits and voice pitch of targeted dogs\n" +
                "§a/" + literalName + " get <rule>§r - Query current value of a GameRule\n" +
                "§a/" + literalName + " set <rule> <val>§r - Modify a GameRule value (Permission 2)\n" +
                "§a/" + literalName + " reset§r - Reset all Better Dogs GameRules to factory defaults (Permission 2)\n" +
                "§a/" + literalName + " reload§r - Validate and re-sync active GameRules (Permission 2)\n" +
                "§6Support & Community: §e" + kofi + " §7| §e" + SocialLinks.DISCORD_INVITE_URL
        ), false);
        return 1;
    }

    private static int executeStatus(CommandSourceStack source) {
        ensureRulesPopulated();
        MinecraftServer server = source.getServer();
        if (server == null) {
            source.sendFailure(Component.literal("§c[Better Dogs] Server is unavailable."));
            return 0;
        }
        GameRules rules = server.getGameRules();

        StringBuilder sb = new StringBuilder();
        sb.append("§6=== Better Dogs Companion Status ===§r\n");

        sb.append("§e[Personalities & Stats]§r\n");
        sb.append(" §7Aggro: §fHP ").append(getSafeInt(rules, BetterDogsGameRules.BD_AGGRO_HEALTH, -10))
                .append(", SPD +").append(getSafeInt(rules, BetterDogsGameRules.BD_AGGRO_SPEED_PCT, 15))
                .append("%, DMG +").append(getSafeInt(rules, BetterDogsGameRules.BD_AGGRO_DMG_PCT, 15)).append("%\n");
        sb.append(" §7Pacifist: §fHP +").append(getSafeInt(rules, BetterDogsGameRules.BD_PACI_HEALTH, 20))
                .append(", SPD ").append(getSafeInt(rules, BetterDogsGameRules.BD_PACI_SPEED_PCT, -10))
                .append("%, DMG ").append(getSafeInt(rules, BetterDogsGameRules.BD_PACI_DMG_PCT, -15)).append("%\n");

        sb.append("§e[Environmental & Safety]§r\n");
        sb.append(" §7Storm Anxiety: ").append(formatBool(getSafeBool(rules, BetterDogsGameRules.BD_STORM_ANXIETY, true)))
                .append(" §7| Cliff Safety: ").append(formatBool(getSafeBool(rules, BetterDogsGameRules.BD_CLIFF_SAFETY, true)))
                .append(" §7| Creeper Evasion: ").append(formatBool(getSafeBool(rules, BetterDogsGameRules.BD_CREEPER_EVASION_ENABLED, true))).append("\n");
        sb.append(" §7Friendly Fire Protection: ").append(formatBool(getSafeBool(rules, BetterDogsGameRules.BD_FRIENDLY_FIRE, true)))
                .append(" §7| Actionbar Subtitles: ").append(formatBool(getSafeBool(rules, BetterDogsGameRules.BD_ACTIONBAR_FEEDBACK, false))).append("\n");

        sb.append("§e[Tactics & Utilities]§r\n");
        sb.append(" §7Goat Horn Range: §a").append(getSafeInt(rules, BetterDogsGameRules.BD_HORN_COMMAND_RANGE, 64)).append(" blocks§r")
                .append(" §7| Flanking Tactics: ").append(formatBool(getSafeBool(rules, BetterDogsGameRules.BD_PACK_FLANKING_TACTICS, true)))
                .append(" §7| Nemesis Memory: ").append(formatBool(getSafeBool(rules, BetterDogsGameRules.BD_NEMESIS_SYSTEM, true))).append("\n");

        sb.append("§e[Genetics & Breeding]§r\n");
        sb.append(" §7Max Litter Size: §a").append(getSafeInt(rules, BetterDogsGameRules.BD_WOLF_LITTER_MAX_SIZE, 4))
                .append(" §7| Scale Range: §a").append(getSafeInt(rules, BetterDogsGameRules.BD_WOLF_MIN_SCALE_PERCENT, 70)).append("%-")
                .append(getSafeInt(rules, BetterDogsGameRules.BD_WOLF_MAX_SCALE_PERCENT, 145)).append("%§r")
                .append(" §7| Genetics: ").append(formatBool(getSafeBool(rules, BetterDogsGameRules.BD_DOG_GENETICS_ENABLED, true)))
                .append(" §7| Autonomous Feeding: ").append(formatBool(getSafeBool(rules, BetterDogsGameRules.BD_DOGS_EAT_RAW_FOOD, true)));

        source.sendSuccess(() -> Component.literal(sb.toString()), false);
        return 1;
    }

    private static boolean getSafeBool(GameRules rules, GameRules.Key<GameRules.BooleanValue> key, boolean fallback) {
        if (rules == null || key == null) {
            return fallback;
        }
        GameRules.BooleanValue ruleVal = rules.getRule(key);
        return ruleVal != null ? ruleVal.get() : fallback;
    }

    private static int getSafeInt(GameRules rules, GameRules.Key<GameRules.IntegerValue> key, int fallback) {
        if (rules == null || key == null) {
            return fallback;
        }
        GameRules.IntegerValue ruleVal = rules.getRule(key);
        return ruleVal != null ? ruleVal.get() : fallback;
    }

    private static String formatBool(boolean val) {
        return val ? "§aEnabled§r" : "§cDisabled§r";
    }

    private static int executeGet(CommandSourceStack source, String ruleArg) {
        ensureRulesPopulated();
        MinecraftServer server = source.getServer();
        if (server == null) {
            source.sendFailure(Component.literal("§c[Better Dogs] Server is unavailable."));
            return 0;
        }
        GameRules rules = server.getGameRules();
        String normalized = CommandSuggestionsHelper.normalizeRuleName(ruleArg);

        if (BOOL_MAP.containsKey(normalized)) {
            GameRules.Key<GameRules.BooleanValue> key = BOOL_MAP.get(normalized);
            GameRules.BooleanValue ruleVal = (rules != null && key != null) ? rules.getRule(key) : null;
            if (ruleVal == null) {
                source.sendFailure(Component.literal("§c[Better Dogs] GameRule is unavailable on server: " + normalized));
                return 0;
            }
            boolean val = ruleVal.get();
            source.sendSuccess(() -> Component.literal("§6[Better Dogs]§r Rule §e" + normalized + "§r is currently: " + formatBool(val)), false);
            return 1;
        } else if (INT_MAP.containsKey(normalized)) {
            GameRules.Key<GameRules.IntegerValue> key = INT_MAP.get(normalized);
            GameRules.IntegerValue ruleVal = (rules != null && key != null) ? rules.getRule(key) : null;
            if (ruleVal == null) {
                source.sendFailure(Component.literal("§c[Better Dogs] GameRule is unavailable on server: " + normalized));
                return 0;
            }
            int val = ruleVal.get();
            source.sendSuccess(() -> Component.literal("§6[Better Dogs]§r Rule §e" + normalized + "§r is currently: §b" + val + "§r"), false);
            return 1;
        }

        source.sendFailure(Component.literal("§c[Better Dogs] Unknown GameRule: " + ruleArg));
        return 0;
    }

    private static int executeSet(CommandSourceStack source, String ruleArg, String valueArg) {
        ensureRulesPopulated();
        MinecraftServer server = source.getServer();
        if (server == null) {
            source.sendFailure(Component.literal("§c[Better Dogs] Server is unavailable."));
            return 0;
        }
        GameRules rules = server.getGameRules();
        String normalized = CommandSuggestionsHelper.normalizeRuleName(ruleArg);

        if (BOOL_MAP.containsKey(normalized)) {
            GameRules.Key<GameRules.BooleanValue> key = BOOL_MAP.get(normalized);
            if (!valueArg.equalsIgnoreCase("true") && !valueArg.equalsIgnoreCase("false")) {
                source.sendFailure(Component.literal("§c[Better Dogs] Value for " + normalized + " must be true or false."));
                return 0;
            }
            GameRules.BooleanValue ruleVal = (rules != null && key != null) ? rules.getRule(key) : null;
            if (ruleVal == null) {
                source.sendFailure(Component.literal("§c[Better Dogs] GameRule is unavailable on server: " + normalized));
                return 0;
            }
            boolean parsed = Boolean.parseBoolean(valueArg);
            ruleVal.set(parsed, server);
            source.sendSuccess(() -> Component.literal("§6[Better Dogs]§r Rule §e" + normalized + "§r set to: " + formatBool(parsed)), true);
            return 1;
        } else if (INT_MAP.containsKey(normalized)) {
            GameRules.Key<GameRules.IntegerValue> key = INT_MAP.get(normalized);
            int parsed;
            try {
                parsed = Integer.parseInt(valueArg);
            } catch (NumberFormatException e) {
                source.sendFailure(Component.literal("§c[Better Dogs] Value for " + normalized + " must be a valid integer."));
                return 0;
            }
            int[] bounds = INT_BOUNDS.get(normalized);
            if (bounds != null && (parsed < bounds[0] || parsed > bounds[1])) {
                source.sendFailure(Component.literal(
                        "§c[Better Dogs] " + normalized + " must be between " + bounds[0] + " and " + bounds[1] + "."));
                return 0;
            }
            GameRules.IntegerValue ruleVal = (rules != null && key != null) ? rules.getRule(key) : null;
            if (ruleVal == null) {
                source.sendFailure(Component.literal("§c[Better Dogs] GameRule is unavailable on server: " + normalized));
                return 0;
            }
            ruleVal.set(parsed, server);
            source.sendSuccess(() -> Component.literal("§6[Better Dogs]§r Rule §e" + normalized + "§r set to: §b" + parsed + "§r"), true);
            return 1;
        }

        source.sendFailure(Component.literal("§c[Better Dogs] Unknown GameRule: " + ruleArg));
        return 0;
    }

    private static int executeReset(CommandSourceStack source) {
        ensureRulesPopulated();
        MinecraftServer server = source.getServer();
        if (server == null) {
            source.sendFailure(Component.literal("§c[Better Dogs] Server is unavailable."));
            return 0;
        }
        GameRules rules = server.getGameRules();
        if (rules == null) {
            source.sendFailure(Component.literal("§c[Better Dogs] GameRules are unavailable."));
            return 0;
        }

        for (Map.Entry<String, GameRules.Key<GameRules.BooleanValue>> entry : BOOL_MAP.entrySet()) {
            GameRules.Key<GameRules.BooleanValue> key = entry.getValue();
            if (key == null) {
                continue;
            }
            GameRules.BooleanValue ruleVal = rules.getRule(key);
            if (ruleVal != null) {
                boolean def = BOOL_DEFAULTS.getOrDefault(entry.getKey(), true);
                ruleVal.set(def, server);
            }
        }
        for (Map.Entry<String, GameRules.Key<GameRules.IntegerValue>> entry : INT_MAP.entrySet()) {
            GameRules.Key<GameRules.IntegerValue> key = entry.getValue();
            if (key == null) {
                continue;
            }
            GameRules.IntegerValue ruleVal = rules.getRule(key);
            if (ruleVal != null) {
                int def = INT_DEFAULTS.getOrDefault(entry.getKey(), 0);
                ruleVal.set(def, server);
            }
        }

        source.sendSuccess(() -> Component.literal("§6[Better Dogs]§r All Better Dogs GameRules have been reset to factory defaults."), true);
        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("§6[Better Dogs]§r Active companion GameRules and settings synchronized successfully."), true);
        return 1;
    }
}
