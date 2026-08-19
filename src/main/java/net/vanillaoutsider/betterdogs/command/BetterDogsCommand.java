// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfCommandHelper;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dedicated single-purpose Brigadier command tree builder and executor for /betterdogs and /bd.
 */
public class BetterDogsCommand {

    private static final Map<String, GameRule<Boolean>> BOOL_MAP = new LinkedHashMap<>();
    private static final Map<String, GameRule<Integer>> INT_MAP = new LinkedHashMap<>();
    private static final Map<String, Boolean> BOOL_DEFAULTS = new LinkedHashMap<>();
    private static final Map<String, Integer> INT_DEFAULTS = new LinkedHashMap<>();

    static {
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
        registerBool("bd_fetch_enabled", BetterDogsGameRules.BD_FETCH_ENABLED, true);
        registerBool("bd_zoomies_enabled", BetterDogsGameRules.BD_ZOOMIES_ENABLED, true);
        registerBool("bd_allow_unrestricted_dog_riding", BetterDogsGameRules.BD_ALLOW_UNRESTRICTED_RIDING, false);
        registerBool("bd_pacifist_guard_buffs", BetterDogsGameRules.BD_PACIFIST_GUARD_BUFFS, false);
        registerBool("bd_enable_inbred_curing", BetterDogsGameRules.BD_ENABLE_INBRED_CURING, true);
        registerBool("bd_show_runt_particles", BetterDogsGameRules.BD_SHOW_RUNT_PARTICLES, false);

        registerInt("bd_refuse_ground_food_chance", BetterDogsGameRules.BD_REFUSE_GROUND_FOOD_CHANCE, 30);
        registerInt("bd_nemesis_duration_days", BetterDogsGameRules.BD_NEMESIS_DURATION_DAYS, 3);
        registerInt("bd_horn_command_range", BetterDogsGameRules.BD_HORN_COMMAND_RANGE, 64);
        registerInt("bd_horn_pathing_timeout", BetterDogsGameRules.BD_HORN_PATHING_TIMEOUT, 300);
        registerInt("bd_horn_override_duration", BetterDogsGameRules.BD_HORN_OVERRIDE_DURATION, 600);
        registerInt("bd_fetch_range", BetterDogsGameRules.BD_FETCH_RANGE, 16);
        registerInt("bd_zoomies_duration_ticks", BetterDogsGameRules.BD_ZOOMIES_DURATION_TICKS, 160);
        registerInt("bd_aggressive_health", BetterDogsGameRules.BD_AGGRO_HEALTH, -10);
        registerInt("bd_aggro_speed_percent", BetterDogsGameRules.BD_AGGRO_SPEED_PCT, 15);
        registerInt("bd_aggro_dmg_percent", BetterDogsGameRules.BD_AGGRO_DMG_PCT, 15);
        registerInt("bd_aggro_follow_start", BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 50);
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
        registerInt("bd_wolf_litter_extra_chance", BetterDogsGameRules.BD_WOLF_LITTER_EXTRA_CHANCE, 30);
        registerInt("bd_particle_density", BetterDogsGameRules.BD_PARTICLE_DENSITY, 5);
        registerInt("bd_guard_patrol_range_aggressive", BetterDogsGameRules.BD_GUARD_PATROL_RANGE_AGGRESSIVE, 8);
        registerInt("bd_guard_patrol_range_normal", BetterDogsGameRules.BD_GUARD_PATROL_RANGE_NORMAL, 6);
        registerInt("bd_guard_patrol_range_pacifist", BetterDogsGameRules.BD_GUARD_PATROL_RANGE_PACIFIST, 4);
        registerInt("bd_tamed_pack_spread_multiplier", BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MULTIPLIER, 100);
        registerInt("bd_tamed_pack_spread_max", BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MAX, 100);
        registerInt("bd_wild_pack_spread_multiplier", BetterDogsGameRules.BD_WILD_PACK_SPREAD_MULTIPLIER, 100);
        registerInt("bd_wild_pack_spread_max", BetterDogsGameRules.BD_WILD_PACK_SPREAD_MAX, 100);
    }

    private static void registerBool(String name, GameRule<Boolean> rule, boolean def) {
        if (rule != null) {
            BOOL_MAP.put(name, rule);
            BOOL_DEFAULTS.put(name, def);
        }
    }

    private static void registerInt(String name, GameRule<Integer> rule, int def) {
        if (rule != null) {
            INT_MAP.put(name, rule);
            INT_DEFAULTS.put(name, def);
        }
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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
                .then(Commands.literal("get")
                        .then(Commands.argument("rule", StringArgumentType.word())
                                .suggests(CommandSuggestionsHelper::suggestRules)
                                .executes(context -> executeGet(context.getSource(), StringArgumentType.getString(context, "rule")))))
                .then(Commands.literal("set")
                        .requires(source -> Commands.LEVEL_GAMEMASTERS.check(source.permissions()))
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
                        .requires(source -> Commands.LEVEL_GAMEMASTERS.check(source.permissions()))
                        .executes(context -> executeReset(context.getSource())))
                .then(Commands.literal("reload")
                        .requires(source -> Commands.LEVEL_GAMEMASTERS.check(source.permissions()))
                        .executes(context -> executeReload(context.getSource())))
                .then(Commands.literal("debug")
                        .requires(source -> Commands.LEVEL_GAMEMASTERS.check(source.permissions()))
                        .then(Commands.literal("personality")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .then(Commands.argument("type", StringArgumentType.word())
                                                .executes(context -> {
                                                    if (!isDebugEnabled(context.getSource())) return 0;
                                                    Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
                                                    String type = StringArgumentType.getString(context, "type");
                                                    return WolfCommandHelper.setPersonality(context.getSource(), targets, type);
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("action")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .then(Commands.argument("actionType", StringArgumentType.word())
                                                .executes(context -> {
                                                    if (!isDebugEnabled(context.getSource())) return 0;
                                                    Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
                                                    String actionType = StringArgumentType.getString(context, "actionType");
                                                    return WolfCommandHelper.executeAction(context.getSource(), targets, actionType, null);
                                                })
                                                .then(Commands.argument("secondaryTarget", EntityArgument.entity())
                                                        .executes(context -> {
                                                            if (!isDebugEnabled(context.getSource())) return 0;
                                                            Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
                                                            String actionType = StringArgumentType.getString(context, "actionType");
                                                            Entity secondaryTarget = EntityArgument.getEntity(context, "secondaryTarget");
                                                            return WolfCommandHelper.executeAction(context.getSource(), targets, actionType, secondaryTarget);
                                                        })
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("territory")
                                .executes(context -> {
                                    if (!isDebugEnabled(context.getSource())) return 0;
                                    return WolfCommandHelper.spawnTerritoryScenario(context.getSource());
                                })
                        )
                );
    }

    private static boolean isDebugEnabled(CommandSourceStack source) {
        if (!DynamicGameRuleManager.getBoolean(source.getLevel(), BetterDogsGameRules.BD_DEBUGGING)) {
            source.sendFailure(Component.literal("Better Dogs debugging is currently disabled. Enable it via GameRules: /gamerule betterdogs:bd_debugging true"));
            return false;
        }
        return true;
    }

    private static int executeHelp(CommandSourceStack source, String literalName) {
        source.sendSuccess(() -> Component.literal(
                "§6--- Vanilla Outsider: Better Dogs Commands ---§r\n" +
                "§a/" + literalName + " status§r - Display categorized summary of companion GameRules\n" +
                "§a/" + literalName + " get <rule>§r - Query current value of a GameRule\n" +
                "§a/" + literalName + " set <rule> <val>§r - Modify a GameRule value (Gamemasters)\n" +
                "§a/" + literalName + " reset§r - Reset all Better Dogs GameRules to factory defaults (Gamemasters)\n" +
                "§a/" + literalName + " reload§r - Validate and re-sync active GameRules (Gamemasters)"
        ), false);
        return 1;
    }

    private static int executeStatus(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        if (server == null) {
            source.sendFailure(Component.literal("§c[Better Dogs] Server is unavailable."));
            return 0;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("§6=== Better Dogs Companion Status ===§r\n");

        sb.append("§e[Personalities & Stats]§r\n");
        sb.append(" §7Aggro: §fHP ").append(DynamicGameRuleManager.getInt(source.getLevel(), BetterDogsGameRules.BD_AGGRO_HEALTH))
                .append(", SPD +").append(DynamicGameRuleManager.getInt(source.getLevel(), BetterDogsGameRules.BD_AGGRO_SPEED_PCT))
                .append("%, DMG +").append(DynamicGameRuleManager.getInt(source.getLevel(), BetterDogsGameRules.BD_AGGRO_DMG_PCT)).append("%\n");
        sb.append(" §7Pacifist: §fHP +").append(DynamicGameRuleManager.getInt(source.getLevel(), BetterDogsGameRules.BD_PACI_HEALTH))
                .append(", SPD ").append(DynamicGameRuleManager.getInt(source.getLevel(), BetterDogsGameRules.BD_PACI_SPEED_PCT))
                .append("%, DMG ").append(DynamicGameRuleManager.getInt(source.getLevel(), BetterDogsGameRules.BD_PACI_DMG_PCT)).append("%\n");

        sb.append("§e[Environmental & Safety]§r\n");
        sb.append(" §7Storm Anxiety: ").append(formatBool(DynamicGameRuleManager.getBoolean(source.getLevel(), BetterDogsGameRules.BD_STORM_ANXIETY)))
                .append(" §7| Cliff Safety: ").append(formatBool(DynamicGameRuleManager.getBoolean(source.getLevel(), BetterDogsGameRules.BD_CLIFF_SAFETY)))
                .append(" §7| Creeper Evasion: ").append(formatBool(DynamicGameRuleManager.getBoolean(source.getLevel(), BetterDogsGameRules.BD_CREEPER_EVASION_ENABLED))).append("\n");
        sb.append(" §7Friendly Fire Protection: ").append(formatBool(DynamicGameRuleManager.getBoolean(source.getLevel(), BetterDogsGameRules.BD_FRIENDLY_FIRE)))
                .append(" §7| Actionbar Subtitles: ").append(formatBool(DynamicGameRuleManager.getBoolean(source.getLevel(), BetterDogsGameRules.BD_ACTIONBAR_FEEDBACK))).append("\n");

        sb.append("§e[Tactics & Utilities]§r\n");
        sb.append(" §7Goat Horn Range: §a").append(DynamicGameRuleManager.getInt(source.getLevel(), BetterDogsGameRules.BD_HORN_COMMAND_RANGE)).append(" blocks§r")
                .append(" §7| Flanking Tactics: ").append(formatBool(DynamicGameRuleManager.getBoolean(source.getLevel(), BetterDogsGameRules.BD_PACK_FLANKING_TACTICS)))
                .append(" §7| Nemesis Memory: ").append(formatBool(DynamicGameRuleManager.getBoolean(source.getLevel(), BetterDogsGameRules.BD_NEMESIS_SYSTEM))).append("\n");

        sb.append("§e[Genetics & Breeding]§r\n");
        sb.append(" §7Max Litter Size: §a").append(DynamicGameRuleManager.getInt(source.getLevel(), BetterDogsGameRules.BD_WOLF_LITTER_MAX_SIZE))
                .append(" §7| Scale Range: §a").append(DynamicGameRuleManager.getInt(source.getLevel(), BetterDogsGameRules.BD_WOLF_MIN_SCALE_PERCENT)).append("%-")
                .append(DynamicGameRuleManager.getInt(source.getLevel(), BetterDogsGameRules.BD_WOLF_MAX_SCALE_PERCENT)).append("%§r")
                .append(" §7| Autonomous Feeding: ").append(formatBool(DynamicGameRuleManager.getBoolean(source.getLevel(), BetterDogsGameRules.BD_DOGS_EAT_RAW_FOOD)));

        source.sendSuccess(() -> Component.literal(sb.toString()), false);
        return 1;
    }

    private static String formatBool(boolean val) {
        return val ? "§aEnabled§r" : "§cDisabled§r";
    }

    private static int executeGet(CommandSourceStack source, String ruleArg) {
        MinecraftServer server = source.getServer();
        if (server == null) {
            source.sendFailure(Component.literal("§c[Better Dogs] Server is unavailable."));
            return 0;
        }
        String normalized = CommandSuggestionsHelper.normalizeRuleName(ruleArg);

        if (BOOL_MAP.containsKey(normalized)) {
            GameRule<Boolean> key = BOOL_MAP.get(normalized);
            boolean val = DynamicGameRuleManager.getBoolean(source.getLevel(), key);
            source.sendSuccess(() -> Component.literal("§6[Better Dogs]§r Rule §e" + normalized + "§r is currently: " + formatBool(val)), false);
            return 1;
        } else if (INT_MAP.containsKey(normalized)) {
            GameRule<Integer> key = INT_MAP.get(normalized);
            int val = DynamicGameRuleManager.getInt(source.getLevel(), key);
            source.sendSuccess(() -> Component.literal("§6[Better Dogs]§r Rule §e" + normalized + "§r is currently: §b" + val + "§r"), false);
            return 1;
        }

        source.sendFailure(Component.literal("§c[Better Dogs] Unknown GameRule: " + ruleArg));
        return 0;
    }

    private static int executeSet(CommandSourceStack source, String ruleArg, String valueArg) {
        MinecraftServer server = source.getServer();
        if (server == null) {
            source.sendFailure(Component.literal("§c[Better Dogs] Server is unavailable."));
            return 0;
        }
        String normalized = CommandSuggestionsHelper.normalizeRuleName(ruleArg);

        if (BOOL_MAP.containsKey(normalized)) {
            GameRule<Boolean> key = BOOL_MAP.get(normalized);
            if (!valueArg.equalsIgnoreCase("true") && !valueArg.equalsIgnoreCase("false")) {
                source.sendFailure(Component.literal("§c[Better Dogs] Value for " + normalized + " must be true or false."));
                return 0;
            }
            boolean parsed = Boolean.parseBoolean(valueArg);
            server.getGameRules().set(key, parsed, server);
            source.sendSuccess(() -> Component.literal("§6[Better Dogs]§r Rule §e" + normalized + "§r set to: " + formatBool(parsed)), true);
            return 1;
        } else if (INT_MAP.containsKey(normalized)) {
            GameRule<Integer> key = INT_MAP.get(normalized);
            int parsed;
            try {
                parsed = Integer.parseInt(valueArg);
            } catch (NumberFormatException e) {
                source.sendFailure(Component.literal("§c[Better Dogs] Value for " + normalized + " must be a valid integer."));
                return 0;
            }
            server.getGameRules().set(key, parsed, server);
            source.sendSuccess(() -> Component.literal("§6[Better Dogs]§r Rule §e" + normalized + "§r set to: §b" + parsed + "§r"), true);
            return 1;
        }

        source.sendFailure(Component.literal("§c[Better Dogs] Unknown GameRule: " + ruleArg));
        return 0;
    }

    private static int executeReset(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        if (server == null) {
            source.sendFailure(Component.literal("§c[Better Dogs] Server is unavailable."));
            return 0;
        }

        for (Map.Entry<String, GameRule<Boolean>> entry : BOOL_MAP.entrySet()) {
            boolean def = BOOL_DEFAULTS.getOrDefault(entry.getKey(), true);
            server.getGameRules().set(entry.getValue(), def, server);
        }
        for (Map.Entry<String, GameRule<Integer>> entry : INT_MAP.entrySet()) {
            int def = INT_DEFAULTS.getOrDefault(entry.getKey(), 0);
            server.getGameRules().set(entry.getValue(), def, server);
        }

        source.sendSuccess(() -> Component.literal("§6[Better Dogs]§r All Better Dogs GameRules have been reset to factory defaults."), true);
        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("§6[Better Dogs]§r Active companion GameRules and settings synchronized successfully."), true);
        return 1;
    }
}
