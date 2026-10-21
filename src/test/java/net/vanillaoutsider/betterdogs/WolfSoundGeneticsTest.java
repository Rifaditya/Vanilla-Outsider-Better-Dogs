// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.Bootstrap;
import net.vanillaoutsider.betterdogs.command.BetterDogsCommand;
import net.vanillaoutsider.betterdogs.command.CommandSuggestionsHelper;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 4: Wolf Sound Genetics & Command Suite Tests (1.20.1)")
public class WolfSoundGeneticsTest {

    @BeforeAll
    static void initMinecraft() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
        try {
            BetterDogsGameRules.register();
        } catch (Throwable ignored) {
        }
    }

    @Test
    @DisplayName("Pillar 1: Acoustic Pitch Timbre Formula Verification")
    public void testAcousticPitchFormula() {
        float basePitch = 1.0f;

        // Scale 0.70x -> ~1.195x pitch
        float pitch070 = CanineGeneticsHelper.calculateVoicePitch(basePitch, 0.70f);
        assertEquals(1.1952286f, pitch070, 0.005f, "Pitch for scale 0.70x should be approximately 1.195x");

        // Scale 1.00x -> 1.000x pitch
        float pitch100 = CanineGeneticsHelper.calculateVoicePitch(basePitch, 1.00f);
        assertEquals(1.0f, pitch100, 0.001f, "Pitch for scale 1.00x should be exactly 1.0x");

        // Scale 1.30x -> ~0.877x pitch
        float pitch130 = CanineGeneticsHelper.calculateVoicePitch(basePitch, 1.30f);
        assertEquals(0.877058f, pitch130, 0.005f, "Pitch for scale 1.30x should be approximately 0.877x");

        // Saturated Clamp Limits: Scale 0.01x should clamp at MAX_PITCH (2.0f)
        float maxClamped = CanineGeneticsHelper.calculateVoicePitch(basePitch, 0.01f);
        assertEquals(CanineGeneticsHelper.MAX_PITCH, maxClamped, 0.0001f, "Extreme low scale should clamp to MAX_PITCH");

        // Saturated Clamp Limits: Scale 10.0x should clamp at MIN_PITCH (0.5f)
        float minClamped = CanineGeneticsHelper.calculateVoicePitch(basePitch, 10.0f);
        assertEquals(CanineGeneticsHelper.MIN_PITCH, minClamped, 0.0001f, "Extreme high scale should clamp to MIN_PITCH");

        // Variable base pitch test
        float highBasePitch = CanineGeneticsHelper.calculateVoicePitch(1.5f, 1.0f);
        assertEquals(1.5f, highBasePitch, 0.001f, "Base pitch modulation should scale proportionately");
    }

    @Test
    @DisplayName("Pillar 2: Brigadier Inspect Subcommand Registration & Hierarchy")
    public void testInspectCommandRegistration() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        BetterDogsCommand.register(dispatcher);

        CommandNode<CommandSourceStack> root = dispatcher.getRoot().getChild("betterdogs");
        assertNotNull(root, "Root /betterdogs command should be registered");

        CommandNode<CommandSourceStack> inspectNode = root.getChild("inspect");
        assertNotNull(inspectNode, "Subcommand /betterdogs inspect should be registered");

        CommandNode<CommandSourceStack> targetsNode = inspectNode.getChild("targets");
        assertNotNull(targetsNode, "Argument node 'targets' under /betterdogs inspect should be registered");

        // Check alias /bd inspect
        CommandNode<CommandSourceStack> aliasRoot = dispatcher.getRoot().getChild("bd");
        assertNotNull(aliasRoot, "Alias /bd command should be registered");
        assertNotNull(aliasRoot.getChild("inspect"), "Subcommand /bd inspect should be registered");
        assertNotNull(aliasRoot.getChild("inspect").getChild("targets"), "Argument 'targets' under /bd inspect should be registered");
    }

    @Test
    @DisplayName("Pillar 3: Dog Genetics GameRule Registration, Defaults & Aliases")
    public void testGeneticsGameRules() {
        // Suggestions helper classification
        assertTrue(CommandSuggestionsHelper.isBooleanRule("bd_dog_genetics_enabled"),
                "bd_dog_genetics_enabled must be recognized as boolean rule");
        assertTrue(CommandSuggestionsHelper.isBooleanRule("doggeneticsenabled"),
                "doggeneticsenabled alias must be recognized as boolean rule");
        assertTrue(CommandSuggestionsHelper.isIntegerRule("bd_max_scale_deviation"),
                "bd_max_scale_deviation must be recognized as integer rule");
        assertTrue(CommandSuggestionsHelper.isIntegerRule("maxscaledeviation"),
                "maxscaledeviation alias must be recognized as integer rule");

        // Null-safe accessors
        assertTrue(BetterDogsGameRules.isDogGeneticsEnabled(null), "Default isDogGeneticsEnabled(null) should be true");
        assertEquals(30, BetterDogsGameRules.getMaxScaleDeviation(null), "Default getMaxScaleDeviation(null) should be 30");

        if (BetterDogsGameRules.BD_DOG_GENETICS_ENABLED != null) {
            assertEquals("bd_dog_genetics_enabled", BetterDogsGameRules.BD_DOG_GENETICS_ENABLED.getId());
        }
        if (BetterDogsGameRules.BD_MAX_SCALE_DEVIATION != null) {
            assertEquals("bd_max_scale_deviation", BetterDogsGameRules.BD_MAX_SCALE_DEVIATION.getId());
        }
    }

    @Test
    @DisplayName("Pillar 4 (Side Safety): Zero Client Imports in Common Genetics / Mixin Classes")
    public void testZeroClientImportsInCommon() throws IOException {
        List<Path> targets = List.of(
                Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "genetics"),
                Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "command"),
                Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "registry"),
                Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "mixin", "WolfSoundMixin.java"),
                Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "mixin", "WolfMixin.java"),
                Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "util", "WolfCommandHelper.java")
        );

        for (Path target : targets) {
            if (Files.isDirectory(target)) {
                try (Stream<Path> stream = Files.walk(target)) {
                    List<Path> javaFiles = stream.filter(p -> p.toString().endsWith(".java")).toList();
                    for (Path file : javaFiles) {
                        assertZeroClientImports(file);
                    }
                }
            } else if (Files.isRegularFile(target)) {
                assertZeroClientImports(target);
            }
        }
    }

    private void assertZeroClientImports(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        for (int lineNum = 0; lineNum < lines.size(); lineNum++) {
            String line = lines.get(lineNum).trim();
            if (line.startsWith("import net.minecraft.client.")
                    || line.startsWith("import com.mojang.blaze3d.")) {
                fail("Server classloading hazard: Client import detected in common file "
                        + file.getFileName() + " at line " + (lineNum + 1) + ": " + line);
            }
        }
    }
}
