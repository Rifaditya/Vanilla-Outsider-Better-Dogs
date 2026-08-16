// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.vanillaoutsider.betterdogs.command.BetterDogsCommand;
import net.vanillaoutsider.betterdogs.command.CommandSuggestionsHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandSuiteTest {

    private CommandDispatcher<CommandSourceStack> dispatcher;

    @BeforeAll
    public static void initMinecraft() {
        try {
            net.minecraft.SharedConstants.tryDetectVersion();
            net.minecraft.server.Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

    @BeforeEach
    public void setUp() {
        dispatcher = new CommandDispatcher<>();
        BetterDogsCommand.register(dispatcher);
    }

    @Test
    public void testCommandNodesRegistered() {
        CommandNode<CommandSourceStack> root = dispatcher.getRoot().getChild("betterdogs");
        assertNotNull(root, "Root /betterdogs command should be registered in dispatcher");

        CommandNode<CommandSourceStack> alias = dispatcher.getRoot().getChild("bd");
        assertNotNull(alias, "Short alias /bd should be registered in dispatcher");

        // Verify subnodes under /betterdogs
        assertNotNull(root.getChild("help"), "Subcommand /betterdogs help should be present");
        assertNotNull(root.getChild("status"), "Subcommand /betterdogs status should be present");
        assertNotNull(root.getChild("get"), "Subcommand /betterdogs get should be present");
        assertNotNull(root.getChild("set"), "Subcommand /betterdogs set should be present");
        assertNotNull(root.getChild("reset"), "Subcommand /betterdogs reset should be present");
        assertNotNull(root.getChild("reload"), "Subcommand /betterdogs reload should be present");

        // Verify subnodes under /bd
        assertNotNull(alias.getChild("help"), "Subcommand /bd help should be present");
        assertNotNull(alias.getChild("status"), "Subcommand /bd status should be present");
        assertNotNull(alias.getChild("get"), "Subcommand /bd get should be present");
        assertNotNull(alias.getChild("set"), "Subcommand /bd set should be present");
        assertNotNull(alias.getChild("reset"), "Subcommand /bd reset should be present");
        assertNotNull(alias.getChild("reload"), "Subcommand /bd reload should be present");
    }

    @Test
    public void testRuleNormalizationAndClassification() {
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
        assertFalse(CommandSuggestionsHelper.isKnownRule("non_existent_rule_xyz"));
    }

    @Test
    public void testAllRulesCoverage() {
        assertFalse(CommandSuggestionsHelper.getAllRules().isEmpty(), "All rules list should not be empty");
        assertTrue(CommandSuggestionsHelper.getAllRules().size() >= 50, "Should register all 50+ GameRules");
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_cliff_safety"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_horn_command_range"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_wolf_litter_max_size"));
    }
}
