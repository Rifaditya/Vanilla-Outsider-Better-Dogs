// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.command.BetterDogsCommand;
import net.vanillaoutsider.betterdogs.command.CommandSuggestionsHelper;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CommandSuiteTest {

    private static final Unsafe UNSAFE = initUnsafe();
    private CommandDispatcher<CommandSourceStack> dispatcher;

    private static Unsafe initUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Unsafe", e);
        }
    }

    @BeforeAll
    public static void initMinecraftAndRules() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
        BetterDogsGameRules.init();
    }

    @BeforeEach
    public void setUp() {
        BetterDogsGameRules.init();
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
        assertNotNull(root.getChild("inspect"), "Subcommand /betterdogs inspect should be present");
        assertNotNull(root.getChild("get"), "Subcommand /betterdogs get should be present");
        assertNotNull(root.getChild("set"), "Subcommand /betterdogs set should be present");
        assertNotNull(root.getChild("reset"), "Subcommand /betterdogs reset should be present");
        assertNotNull(root.getChild("reload"), "Subcommand /betterdogs reload should be present");

        // Verify subnodes under /bd
        assertNotNull(alias.getChild("help"), "Subcommand /bd help should be present");
        assertNotNull(alias.getChild("status"), "Subcommand /bd status should be present");
        assertNotNull(alias.getChild("inspect"), "Subcommand /bd inspect should be present");
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
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_dog_play_fighting"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_play_penalty_minutes"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_dog_genetics_enabled"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_max_scale_deviation"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_personality_archetypes_enabled"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_personality_distribution_mode"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_canine_explosive_reflex_enabled"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_hazard_flee_radius"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_flee_speed_multiplier"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_ballistic_evasion_enabled"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_evasion_reaction_ticks"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_evasion_lateral_impulse"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_projectile_intercept"));
        assertTrue(CommandSuggestionsHelper.getAllRules().contains("bd_interceptor_owner_health_threshold"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCommandMapsContainZeroNullsAndCorrectDefaults() throws Exception {
        Map<String, GameRules.Key<GameRules.BooleanValue>> boolMap =
                (Map<String, GameRules.Key<GameRules.BooleanValue>>) getStaticField(BetterDogsCommand.class, "BOOL_MAP");
        Map<String, GameRules.Key<GameRules.IntegerValue>> intMap =
                (Map<String, GameRules.Key<GameRules.IntegerValue>>) getStaticField(BetterDogsCommand.class, "INT_MAP");
        Map<String, Integer> intDefaults =
                (Map<String, Integer>) getStaticField(BetterDogsCommand.class, "INT_DEFAULTS");

        assertFalse(boolMap.isEmpty(), "BOOL_MAP must not be empty");
        assertFalse(intMap.isEmpty(), "INT_MAP must not be empty");

        for (Map.Entry<String, GameRules.Key<GameRules.BooleanValue>> e : boolMap.entrySet()) {
            assertNotNull(e.getValue(), "BOOL_MAP contains null key for rule: " + e.getKey());
        }
        for (Map.Entry<String, GameRules.Key<GameRules.IntegerValue>> e : intMap.entrySet()) {
            assertNotNull(e.getValue(), "INT_MAP contains null key for rule: " + e.getKey());
        }

        // Verify reconciled default 30 for bd_max_scale_deviation
        assertEquals(30, intDefaults.get("bd_max_scale_deviation"));
        assertEquals(30, intDefaults.get("maxscaledeviation"));

        // Verify all 12 rules and their aliases are wired in BOOL_MAP / INT_MAP
        assertSame(BetterDogsGameRules.BD_DOG_GENETICS_ENABLED, boolMap.get("bd_dog_genetics_enabled"));
        assertSame(BetterDogsGameRules.BD_DOG_GENETICS_ENABLED, boolMap.get("doggeneticsenabled"));
        assertSame(BetterDogsGameRules.BD_ARCHETYPES_ENABLED, boolMap.get("bd_personality_archetypes_enabled"));
        assertSame(BetterDogsGameRules.BD_ARCHETYPES_ENABLED, boolMap.get("personalityarchetypesenabled"));
        assertSame(BetterDogsGameRules.BD_CANINE_EXPLOSIVE_REFLEX_ENABLED, boolMap.get("bd_canine_explosive_reflex_enabled"));
        assertSame(BetterDogsGameRules.BD_CANINE_EXPLOSIVE_REFLEX_ENABLED, boolMap.get("canineexplosivereflexenabled"));
        assertSame(BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED, boolMap.get("bd_ballistic_evasion_enabled"));
        assertSame(BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED, boolMap.get("ballisticevasionenabled"));
        assertSame(BetterDogsGameRules.BD_PROJECTILE_INTERCEPT, boolMap.get("bd_projectile_intercept"));
        assertSame(BetterDogsGameRules.BD_PROJECTILE_INTERCEPT, boolMap.get("projectileintercept"));

        assertSame(BetterDogsGameRules.BD_MAX_SCALE_DEVIATION, intMap.get("bd_max_scale_deviation"));
        assertSame(BetterDogsGameRules.BD_MAX_SCALE_DEVIATION, intMap.get("maxscaledeviation"));
        assertSame(BetterDogsGameRules.BD_ARCHETYPE_DISTRIBUTION_MODE, intMap.get("bd_personality_distribution_mode"));
        assertSame(BetterDogsGameRules.BD_ARCHETYPE_DISTRIBUTION_MODE, intMap.get("personalitydistributionmode"));
        assertSame(BetterDogsGameRules.BD_HAZARD_FLEE_RADIUS, intMap.get("bd_hazard_flee_radius"));
        assertSame(BetterDogsGameRules.BD_HAZARD_FLEE_RADIUS, intMap.get("hazardfleeradius"));
        assertSame(BetterDogsGameRules.BD_FLEE_SPEED_MULTIPLIER, intMap.get("bd_flee_speed_multiplier"));
        assertSame(BetterDogsGameRules.BD_FLEE_SPEED_MULTIPLIER, intMap.get("fleespeedmultiplier"));
        assertSame(BetterDogsGameRules.BD_EVASION_REACTION_TICKS, intMap.get("bd_evasion_reaction_ticks"));
        assertSame(BetterDogsGameRules.BD_EVASION_REACTION_TICKS, intMap.get("evasionreactionticks"));
        assertSame(BetterDogsGameRules.BD_EVASION_LATERAL_IMPULSE, intMap.get("bd_evasion_lateral_impulse"));
        assertSame(BetterDogsGameRules.BD_EVASION_LATERAL_IMPULSE, intMap.get("evasionlateralimpulse"));
        assertSame(BetterDogsGameRules.BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD, intMap.get("bd_interceptor_owner_health_threshold"));
        assertSame(BetterDogsGameRules.BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD, intMap.get("interceptorownerhealththreshold"));
    }

    @Test
    public void testTwelveRulesNormalizationAndFuzzSafety() {
        Map<String, String> aliasCases = Map.ofEntries(
                Map.entry("dogGeneticsEnabled", "bd_dog_genetics_enabled"),
                Map.entry("betterdogs:dogGeneticsEnabled", "bd_dog_genetics_enabled"),
                Map.entry("maxScaleDeviation", "bd_max_scale_deviation"),
                Map.entry("personalityArchetypesEnabled", "bd_personality_archetypes_enabled"),
                Map.entry("personalityDistributionMode", "bd_personality_distribution_mode"),
                Map.entry("canineExplosiveReflexEnabled", "bd_canine_explosive_reflex_enabled"),
                Map.entry("hazardFleeRadius", "bd_hazard_flee_radius"),
                Map.entry("fleeSpeedMultiplier", "bd_flee_speed_multiplier"),
                Map.entry("ballisticEvasionEnabled", "bd_ballistic_evasion_enabled"),
                Map.entry("evasionReactionTicks", "bd_evasion_reaction_ticks"),
                Map.entry("evasionLateralImpulse", "bd_evasion_lateral_impulse"),
                Map.entry("projectileIntercept", "bd_projectile_intercept"),
                Map.entry("interceptorOwnerHealthThreshold", "bd_interceptor_owner_health_threshold"),
                Map.entry("dogPlayFighting", "bd_dog_play_fighting"),
                Map.entry("playFightFrequency", "bd_dog_play_fight_frequency"),
                Map.entry("playFightChance", "bd_dog_play_fight_chance"),
                Map.entry("playPenaltyMinutes", "bd_play_penalty_minutes")
        );

        for (Map.Entry<String, String> entry : aliasCases.entrySet()) {
            assertEquals(entry.getValue(), CommandSuggestionsHelper.normalizeRuleName(entry.getKey()),
                    "Alias failed canonical normalization: " + entry.getKey());
            assertTrue(CommandSuggestionsHelper.isKnownRule(entry.getKey()),
                    "Alias should be known: " + entry.getKey());
        }

        assertEquals("", CommandSuggestionsHelper.normalizeRuleName(null));
        assertEquals("", CommandSuggestionsHelper.normalizeRuleName("   "));
        Random rand = new Random(0x5EED1234L);
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_:./- ";
        for (int i = 0; i < 1000; i++) {
            int len = rand.nextInt(48);
            StringBuilder sb = new StringBuilder(len);
            for (int c = 0; c < len; c++) {
                sb.append(alphabet.charAt(rand.nextInt(alphabet.length())));
            }
            String input = sb.toString();
            assertNotNull(CommandSuggestionsHelper.normalizeRuleName(input));
            assertDoesNotThrow(() -> CommandSuggestionsHelper.isKnownRule(input));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBrigadierExecutionStatusGetSetResetAndBoundsValidation() throws Exception {
        Map<String, GameRules.Key<GameRules.BooleanValue>> boolMap =
                (Map<String, GameRules.Key<GameRules.BooleanValue>>) getStaticField(BetterDogsCommand.class, "BOOL_MAP");
        Map<String, GameRules.Key<GameRules.IntegerValue>> intMap =
                (Map<String, GameRules.Key<GameRules.IntegerValue>>) getStaticField(BetterDogsCommand.class, "INT_MAP");
        Map<String, Boolean> boolDefaults =
                (Map<String, Boolean>) getStaticField(BetterDogsCommand.class, "BOOL_DEFAULTS");
        Map<String, Integer> intDefaults =
                (Map<String, Integer>) getStaticField(BetterDogsCommand.class, "INT_DEFAULTS");

        PopulatedGameRules liveRules = new PopulatedGameRules();
        for (Map.Entry<String, GameRules.Key<GameRules.BooleanValue>> entry : boolMap.entrySet()) {
            boolean def = boolDefaults.getOrDefault(entry.getKey(), true);
            liveRules.putBool(entry.getValue(), def);
        }
        for (Map.Entry<String, GameRules.Key<GameRules.IntegerValue>> entry : intMap.entrySet()) {
            int def = intDefaults.getOrDefault(entry.getKey(), 0);
            liveRules.putInt(entry.getValue(), def);
        }

        List<String> messages = new ArrayList<>();
        CommandSourceStack opSource = createSourceStack(createStubServer(liveRules), 2, messages);

        // 1. /betterdogs status and /bd status succeed with genetics enabled
        assertEquals(1, dispatcher.execute("betterdogs status", opSource));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Genetics:") && m.contains("Enabled")));
        messages.clear();
        assertEquals(1, dispatcher.execute("bd status", opSource));
        assertFalse(messages.isEmpty());

        // 2. Dual polarity get/set across all 5 newly registered boolean rules + alias
        String[][] boolRules = {
                {"bd_dog_genetics_enabled", "dogGeneticsEnabled"},
                {"bd_personality_archetypes_enabled", "personalityArchetypesEnabled"},
                {"bd_canine_explosive_reflex_enabled", "canineExplosiveReflexEnabled"},
                {"bd_ballistic_evasion_enabled", "ballisticEvasionEnabled"},
                {"bd_projectile_intercept", "projectileIntercept"}
        };
        for (String[] pair : boolRules) {
            String canonical = pair[0];
            String alias = pair[1];
            GameRules.Key<GameRules.BooleanValue> key = boolMap.get(canonical);
            assertNotNull(key);

            assertEquals(1, dispatcher.execute("betterdogs set " + canonical + " false", opSource));
            assertFalse(liveRules.getRule(key).get(), canonical + " must become false");

            messages.clear();
            assertEquals(1, dispatcher.execute("betterdogs get " + alias, opSource));
            assertTrue(messages.get(0).contains(canonical) && messages.get(0).contains("Disabled"),
                    "Get via alias should display canonical name and Disabled: " + messages);

            assertEquals(1, dispatcher.execute("betterdogs set " + alias + " true", opSource));
            assertTrue(liveRules.getRule(key).get(), canonical + " must become true via alias");

            assertEquals(0, dispatcher.execute("betterdogs set " + canonical + " invalid", opSource));
            assertTrue(liveRules.getRule(key).get(), "Invalid bool must not mutate rule");
        }

        // 3. Bounds validation across all integer rules with explicit ranges
        int[][] specs = {
                {0, 100},   // bd_max_scale_deviation
                {0, 1},     // bd_personality_distribution_mode
                {4, 64},    // bd_hazard_flee_radius
                {100, 300}, // bd_flee_speed_multiplier
                {5, 60},    // bd_evasion_reaction_ticks
                {20, 250},  // bd_evasion_lateral_impulse
                {0, 1000},  // bd_interceptor_owner_health_threshold
                {0, 72000}, // bd_dog_play_fight_frequency
                {0, 100}    // bd_dog_play_fight_chance
        };
        String[] intNames = {
                "bd_max_scale_deviation",
                "bd_personality_distribution_mode",
                "bd_hazard_flee_radius",
                "bd_flee_speed_multiplier",
                "bd_evasion_reaction_ticks",
                "bd_evasion_lateral_impulse",
                "bd_interceptor_owner_health_threshold",
                "bd_dog_play_fight_frequency",
                "bd_dog_play_fight_chance"
        };

        for (int i = 0; i < intNames.length; i++) {
            String rule = intNames[i];
            int min = specs[i][0];
            int max = specs[i][1];
            GameRules.Key<GameRules.IntegerValue> key = intMap.get(rule);
            assertNotNull(key);

            assertEquals(1, dispatcher.execute("betterdogs set " + rule + " " + min, opSource));
            assertEquals(min, liveRules.getRule(key).get());

            assertEquals(1, dispatcher.execute("betterdogs set " + rule + " " + max, opSource));
            assertEquals(max, liveRules.getRule(key).get());

            assertEquals(0, dispatcher.execute("betterdogs set " + rule + " " + (min - 1), opSource),
                    rule + " should reject value below min");
            assertEquals(max, liveRules.getRule(key).get());

            assertEquals(0, dispatcher.execute("betterdogs set " + rule + " " + (max + 1), opSource),
                    rule + " should reject value above max");
            assertEquals(max, liveRules.getRule(key).get());

            assertEquals(0, dispatcher.execute("betterdogs set " + rule + " notAnInt", opSource));
        }

        // 4. Mutate all booleans to false and execute /betterdogs reset
        assertEquals(1, dispatcher.execute("betterdogs set bd_dog_genetics_enabled false", opSource));
        assertEquals(1, dispatcher.execute("betterdogs set maxScaleDeviation 95", opSource));
        assertEquals(95, liveRules.getRule(BetterDogsGameRules.BD_MAX_SCALE_DEVIATION).get());

        assertEquals(1, dispatcher.execute("betterdogs reset", opSource));
        assertTrue(liveRules.getRule(BetterDogsGameRules.BD_DOG_GENETICS_ENABLED).get());
        assertEquals(30, liveRules.getRule(BetterDogsGameRules.BD_MAX_SCALE_DEVIATION).get());
        assertEquals(0, liveRules.getRule(BetterDogsGameRules.BD_ARCHETYPE_DISTRIBUTION_MODE).get());
        assertEquals(14, liveRules.getRule(BetterDogsGameRules.BD_HAZARD_FLEE_RADIUS).get());
        assertEquals(170, liveRules.getRule(BetterDogsGameRules.BD_FLEE_SPEED_MULTIPLIER).get());
        assertEquals(20, liveRules.getRule(BetterDogsGameRules.BD_EVASION_REACTION_TICKS).get());
        assertEquals(85, liveRules.getRule(BetterDogsGameRules.BD_EVASION_LATERAL_IMPULSE).get());
        assertEquals(10, liveRules.getRule(BetterDogsGameRules.BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD).get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNegativeParityEmptyGameRulesAndDeferredInit() throws Exception {
        List<String> messages = new ArrayList<>();
        CommandSourceStack emptyRulesSource = createSourceStack(createStubServer(new GameRules()), 2, messages);

        // Empty GameRules has no registered keys: status and reset must NEVER throw NPE
        assertEquals(1, dispatcher.execute("betterdogs status", emptyRulesSource));
        assertEquals(1, dispatcher.execute("betterdogs reset", emptyRulesSource));
        assertEquals(0, dispatcher.execute("betterdogs get bd_dog_genetics_enabled", emptyRulesSource));
        assertEquals(0, dispatcher.execute("betterdogs set bd_dog_genetics_enabled false", emptyRulesSource));

        // Verify ensureRulesPopulated recovers if command class was loaded before BetterDogsGameRules.init()
        Field stormField = BetterDogsGameRules.class.getDeclaredField("BD_STORM_ANXIETY");
        Field populatedField = BetterDogsCommand.class.getDeclaredField("populated");
        populatedField.setAccessible(true);
        try {
            stormField.set(null, null);
            populatedField.setBoolean(null, false);
            Method ensureMethod = BetterDogsCommand.class.getDeclaredMethod("ensureRulesPopulated");
            ensureMethod.setAccessible(true);
            ensureMethod.invoke(null);
            // When BD_STORM_ANXIETY is null, populated remains false; once init() runs, register() populates all keys
            assertFalse(populatedField.getBoolean(null));
            BetterDogsGameRules.init();
            BetterDogsCommand.register(new CommandDispatcher<>());
            assertTrue(populatedField.getBoolean(null));
            Map<String, GameRules.Key<GameRules.IntegerValue>> intMap =
                    (Map<String, GameRules.Key<GameRules.IntegerValue>>) getStaticField(BetterDogsCommand.class, "INT_MAP");
            assertNotNull(intMap.get("bd_max_scale_deviation"));
        } finally {
            BetterDogsGameRules.init();
            BetterDogsCommand.register(new CommandDispatcher<>());
        }
    }

    private static Object getStaticField(Class<?> clazz, String name) throws Exception {
        Field f = clazz.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(null);
    }

    private static CommandSourceStack createSourceStack(MinecraftServer server, int permLevel, List<String> captured) {
        CommandSource source = new CommandSource() {
            @Override
            public void sendSystemMessage(Component component) {
                captured.add(component.getString());
            }

            @Override
            public boolean acceptsSuccess() {
                return true;
            }

            @Override
            public boolean acceptsFailure() {
                return true;
            }

            @Override
            public boolean shouldInformAdmins() {
                return false;
            }
        };
        return new CommandSourceStack(source, Vec3.ZERO, Vec2.ZERO, null, permLevel, "TestOp", Component.literal("TestOp"), server, null);
    }

    private static StubMinecraftServer createStubServer(GameRules rules) {
        try {
            StubMinecraftServer server = (StubMinecraftServer) UNSAFE.allocateInstance(StubMinecraftServer.class);
            server.rules = rules;
            return server;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubMinecraftServer", e);
        }
    }

    private static final class PopulatedGameRules extends GameRules {
        private final Map<Key<?>, Value<?>> map = new HashMap<>();

        void putBool(Key<BooleanValue> key, boolean def) {
            map.put(key, new BooleanValue(null, def) {
                @Override
                protected void onChanged(MinecraftServer s) {
                }
            });
        }

        void putInt(Key<IntegerValue> key, int def) {
            map.put(key, new IntegerValue(null, def) {
                @Override
                protected void onChanged(MinecraftServer s) {
                }
            });
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Value<T>> T getRule(Key<T> key) {
            return (T) map.get(key);
        }
    }

    private static final class StubMinecraftServer extends MinecraftServer {
        GameRules rules;

        private StubMinecraftServer() {
            super(null, null, null, null, null, null, null, null);
        }

        @Override
        public GameRules getGameRules() {
            return this.rules;
        }

        @Override protected boolean initServer() { return true; }
        @Override public int getOperatorUserPermissionLevel() { return 4; }
        @Override public int getFunctionCompilationLevel() { return 2; }
        @Override public boolean shouldRconBroadcast() { return false; }
        @Override public SystemReport fillServerSystemReport(SystemReport report) { return report; }
        @Override public boolean isDedicatedServer() { return true; }
        @Override public int getRateLimitPacketsPerSecond() { return 0; }
        @Override public boolean isEpollEnabled() { return false; }
        @Override public boolean isCommandBlockEnabled() { return true; }
        @Override public boolean isPublished() { return false; }
        @Override public boolean shouldInformAdmins() { return false; }
        @Override public boolean isSingleplayerOwner(GameProfile profile) { return false; }
    }
}
