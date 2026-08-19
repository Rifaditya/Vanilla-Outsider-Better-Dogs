// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Dedicated single-purpose tab-completion and rule normalization helper for Brigadier commands.
 */
public class CommandSuggestionsHelper {

    private static final Set<String> BOOLEAN_RULES = new LinkedHashSet<>(Arrays.asList(
            "bd_storm_anxiety",
            "bd_actionbar_feedback",
            "bd_creeper_awareness",
            "bd_creeper_evasion_enabled",
            "bd_cliff_safety",
            "bd_flee_low_health",
            "bd_dogs_eat_raw_food",
            "bd_dogs_eat_cooked_food",
            "bd_enable_refuse_ground_food",
            "bd_debugging",
            "bd_nemesis_system",
            "bd_favorite_treats",
            "bd_pack_flanking_tactics",
            "bd_flanking_raycast_check",
            "bd_sync_owner_teleport",
            "bd_fast_travel_catchup",
            "bd_friendly_fire_protection",
            "bd_demerit_accidental_attacks",
            "bd_territorial_rivalry",
            "bd_territorial_exclusive_disputes",
            "bd_wild_personality_behavior",
            "bd_wolf_spawn_expanded_biomes",
            "bd_dynamic_climate_variants",
            "bd_fetch_enabled",
            "bd_zoomies_enabled",
            "bd_allow_unrestricted_dog_riding",
            "bd_pacifist_guard_buffs",
            "bd_enable_inbred_curing",
            "bd_show_runt_particles"
    ));

    private static final Set<String> INTEGER_RULES = new LinkedHashSet<>(Arrays.asList(
            "bd_refuse_ground_food_chance",
            "bd_nemesis_duration_days",
            "bd_horn_command_range",
            "bd_horn_pathing_timeout",
            "bd_horn_override_duration",
            "bd_fetch_range",
            "bd_zoomies_duration_ticks",
            "bd_aggressive_health",
            "bd_aggro_speed_percent",
            "bd_aggro_dmg_percent",
            "bd_aggro_follow_start",
            "bd_aggro_chase_dist",
            "bd_aggro_detect_range",
            "bd_aggro_flee_chance",
            "bd_paci_health",
            "bd_paci_speed_percent",
            "bd_paci_dmg_percent",
            "bd_paci_knockback_percent",
            "bd_paci_follow_start",
            "bd_paci_flee_chance",
            "bd_normal_follow_start",
            "bd_normal_speed_percent",
            "bd_normal_dmg_percent",
            "bd_normal_health",
            "bd_normal_flee_chance",
            "bd_baby_mischief_permille",
            "bd_howl_chance",
            "bd_pack_spread",
            "bd_gift_feed_threshold",
            "bd_gift_interaction_cooldown",
            "bd_wolf_min_scale_percent",
            "bd_wolf_max_scale_percent",
            "bd_blood_feud_percent",
            "bd_baby_retaliate_percent",
            "bd_terr_aa_war",
            "bd_terr_aa_merge",
            "bd_terr_an_war",
            "bd_terr_an_merge",
            "bd_terr_ap_war",
            "bd_terr_ap_merge",
            "bd_terr_nn_war",
            "bd_terr_nn_merge",
            "bd_terr_np_war",
            "bd_terr_np_merge",
            "bd_terr_pp_war",
            "bd_terr_pp_merge",
            "bd_territorial_fatal_chance",
            "bd_territorial_search_radius",
            "bd_wolf_pack_cluster_size",
            "bd_wolf_spawn_density_boost",
            "bd_wolf_spawn_multiplier_percent",
            "bd_wolf_spawn_group_min",
            "bd_wolf_spawn_group_max",
            "bd_spawn_normal_percent",
            "bd_spawn_aggro_percent",
            "bd_spawn_paci_percent",
            "bd_breed_same_chance",
            "bd_breed_same_other_chance",
            "bd_breed_mixed_dominant_chance",
            "bd_breed_mixed_recessive_chance",
            "bd_breed_diluted_normal_chance",
            "bd_breed_diluted_other_chance",
            "bd_wolf_litter_max_size",
            "bd_wolf_litter_extra_chance",
            "bd_particle_density",
            "bd_guard_patrol_range_aggressive",
            "bd_guard_patrol_range_normal",
            "bd_guard_patrol_range_pacifist",
            "bd_tamed_pack_spread_multiplier",
            "bd_tamed_pack_spread_max",
            "bd_wild_pack_spread_multiplier",
            "bd_wild_pack_spread_max"
    ));

    private static final List<String> ALL_RULES;

    static {
        List<String> combined = new ArrayList<>(BOOLEAN_RULES);
        combined.addAll(INTEGER_RULES);
        Collections.sort(combined);
        ALL_RULES = Collections.unmodifiableList(combined);
    }

    public static List<String> getAllRules() {
        return ALL_RULES;
    }

    public static boolean isBooleanRule(String ruleName) {
        String normalized = normalizeRuleName(ruleName);
        return BOOLEAN_RULES.contains(normalized);
    }

    public static boolean isIntegerRule(String ruleName) {
        String normalized = normalizeRuleName(ruleName);
        return INTEGER_RULES.contains(normalized);
    }

    public static boolean isKnownRule(String ruleName) {
        String normalized = normalizeRuleName(ruleName);
        return BOOLEAN_RULES.contains(normalized) || INTEGER_RULES.contains(normalized);
    }

    public static String normalizeRuleName(String input) {
        if (input == null) {
            return "";
        }
        String clean = input.trim().toLowerCase(Locale.ROOT);
        if (clean.startsWith("vanilla-outsider-better-dogs:")) {
            clean = clean.substring("vanilla-outsider-better-dogs:".length());
        } else if (clean.startsWith("betterdogs:")) {
            clean = clean.substring("betterdogs:".length());
        }
        if (!clean.startsWith("bd_") && !clean.isEmpty()) {
            String withPrefix = "bd_" + clean;
            if (BOOLEAN_RULES.contains(withPrefix) || INTEGER_RULES.contains(withPrefix)) {
                return withPrefix;
            }
        }
        return clean;
    }

    public static CompletableFuture<Suggestions> suggestRules(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(ALL_RULES, builder);
    }

    public static CompletableFuture<Suggestions> suggestValues(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        try {
            String ruleArg = context.getArgument("rule", String.class);
            String normalized = normalizeRuleName(ruleArg);
            if (BOOLEAN_RULES.contains(normalized)) {
                return SharedSuggestionProvider.suggest(Arrays.asList("true", "false"), builder);
            } else if (INTEGER_RULES.contains(normalized)) {
                return SharedSuggestionProvider.suggest(Arrays.asList("0", "1", "10", "50", "100"), builder);
            }
        } catch (Exception ignored) {
        }
        return SharedSuggestionProvider.suggest(Arrays.asList("true", "false", "0", "1", "10", "50", "100"), builder);
    }
}
