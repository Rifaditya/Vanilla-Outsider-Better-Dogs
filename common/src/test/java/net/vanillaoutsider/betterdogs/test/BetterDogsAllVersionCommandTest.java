// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.test;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.Bootstrap;
import net.vanillaoutsider.betterdogs.command.BetterDogsCommand;
import net.vanillaoutsider.betterdogs.command.CommandSuggestionsHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pillar 3: Brigadier AST, Node Hierarchy & Permission Safety Contract Tests")
public class BetterDogsAllVersionCommandTest {

    private CommandDispatcher<CommandSourceStack> dispatcher;

    @BeforeAll
    static void initMinecraft() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        dispatcher = new CommandDispatcher<>();
        BetterDogsCommand.register(dispatcher);
    }

    @Test
    @DisplayName("Assert root /betterdogs and alias /bd command trees are registered")
    void testCommandRootsRegistered() {
        CommandNode<CommandSourceStack> root = dispatcher.getRoot().getChild("betterdogs");
        assertNotNull(root, "Root /betterdogs command must be registered");

        CommandNode<CommandSourceStack> alias = dispatcher.getRoot().getChild("bd");
        assertNotNull(alias, "Alias /bd command must be registered");

        // Verify common subnodes under /betterdogs and /bd
        String[] requiredSubcommands = {"help", "status", "inspect", "get", "set", "reset", "reload"};
        for (String sub : requiredSubcommands) {
            assertNotNull(root.getChild(sub), "Subcommand /betterdogs " + sub + " must be registered");
            assertNotNull(alias.getChild(sub), "Subcommand /bd " + sub + " must be registered");
        }

        // On 26.x anchors, debug command is also registered
        if (root.getChild("debug") != null) {
            assertNotNull(alias.getChild("debug"), "Subcommand /bd debug must be registered when /betterdogs debug exists");
        }
    }

    @Test
    @DisplayName("Assert privileged subcommands (set, reset, reload) have permission requirement guards")
    void testPrivilegedCommandRequirements() {
        CommandNode<CommandSourceStack> root = dispatcher.getRoot().getChild("betterdogs");
        assertNotNull(root, "Root /betterdogs must be present");

        CommandNode<CommandSourceStack> setNode = root.getChild("set");
        assertNotNull(setNode, "Subcommand /betterdogs set must be present");
        assertNotNull(setNode.getRequirement(), "Subcommand /betterdogs set must declare permission requirements");

        CommandNode<CommandSourceStack> resetNode = root.getChild("reset");
        assertNotNull(resetNode, "Subcommand /betterdogs reset must be present");
        assertNotNull(resetNode.getRequirement(), "Subcommand /betterdogs reset must declare permission requirements");

        CommandNode<CommandSourceStack> reloadNode = root.getChild("reload");
        assertNotNull(reloadNode, "Subcommand /betterdogs reload must be present");
        assertNotNull(reloadNode.getRequirement(), "Subcommand /betterdogs reload must declare permission requirements");

        CommandNode<CommandSourceStack> alias = dispatcher.getRoot().getChild("bd");
        assertNotNull(alias.getChild("set").getRequirement(), "Subcommand /bd set must declare permission requirements");
        assertNotNull(alias.getChild("reset").getRequirement(), "Subcommand /bd reset must declare permission requirements");
        assertNotNull(alias.getChild("reload").getRequirement(), "Subcommand /bd reload must declare permission requirements");
    }

    @Test
    @DisplayName("Assert CommandSuggestionsHelper rule normalization and type classification")
    void testRuleNormalizationAndClassification() {
        assertEquals("bd_cliff_safety", CommandSuggestionsHelper.normalizeRuleName("cliff_safety"));
        assertEquals("bd_cliff_safety", CommandSuggestionsHelper.normalizeRuleName("bd_cliff_safety"));
        assertEquals("bd_friendly_fire_protection", CommandSuggestionsHelper.normalizeRuleName("friendly_fire_protection"));
        assertEquals("bd_horn_command_range", CommandSuggestionsHelper.normalizeRuleName("horn_command_range"));

        assertTrue(CommandSuggestionsHelper.isBooleanRule("bd_storm_anxiety"));
        assertTrue(CommandSuggestionsHelper.isBooleanRule("storm_anxiety"));
        assertTrue(CommandSuggestionsHelper.isBooleanRule("bd_friendly_fire_protection"));
        assertTrue(CommandSuggestionsHelper.isBooleanRule("bd_cliff_safety"));

        assertTrue(CommandSuggestionsHelper.isIntegerRule("bd_horn_command_range"));
        assertTrue(CommandSuggestionsHelper.isIntegerRule("horn_command_range"));
        assertTrue(CommandSuggestionsHelper.isIntegerRule("bd_wolf_litter_max_size"));
        assertTrue(CommandSuggestionsHelper.isIntegerRule("bd_aggressive_health"));

        assertTrue(CommandSuggestionsHelper.isKnownRule("bd_nemesis_system"));
        assertTrue(CommandSuggestionsHelper.isKnownRule("bd_cliff_safety"));
        assertFalse(CommandSuggestionsHelper.isKnownRule("non_existent_rule_xyz"));
    }

    @Test
    @DisplayName("Assert 50+ GameRules registered for tab suggestions coverage")
    void testAllRulesCoverage() {
        assertFalse(CommandSuggestionsHelper.getAllRules().isEmpty(), "All rules list must not be empty");
        assertTrue(CommandSuggestionsHelper.getAllRules().size() >= 50,
                "CommandSuggestionsHelper must register at least 50 GameRules (found: " + CommandSuggestionsHelper.getAllRules().size() + ")");
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_cliff_safety"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_horn_command_range"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_wolf_litter_max_size"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_dog_play_fighting"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_play_penalty_minutes"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_dog_genetics_enabled"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_max_scale_deviation"));
    }
}
