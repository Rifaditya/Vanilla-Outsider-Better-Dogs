// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class BetterDogsGameRulesTest {

    private static final Unsafe UNSAFE = initUnsafe();

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
    public static void setupMinecraftAndRules() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
        BetterDogsGameRules.init();
    }

    @Test
    public void testNullSafeAccessors() {
        boolean boolVal = BetterDogsGameRules.getBoolean(null, null, true);
        assertTrue(boolVal, "Null level and key should return fallback boolean value true");

        boolean boolValFalse = BetterDogsGameRules.getBoolean(null, null, false);
        assertFalse(boolValFalse, "Null level and key should return fallback boolean value false");

        int intVal = BetterDogsGameRules.getInt(null, null, 64);
        assertEquals(64, intVal, "Null level and key should return fallback integer value");

        assertTrue(BetterDogsGameRules.isDogPlayFightingEnabled(null), "Default for dogPlayFighting should be true");
        assertEquals(10, BetterDogsGameRules.getPlayPenaltyMinutes(null), "Default for playPenaltyMinutes should be 10");
        assertTrue(BetterDogsGameRules.getBoolean(null, BetterDogsGameRules.BD_DOG_PLAY_FIGHTING, true));
        assertEquals(10, BetterDogsGameRules.getInt(null, BetterDogsGameRules.BD_PLAY_PENALTY_MINUTES, 10));

        assertTrue(BetterDogsGameRules.isProjectileInterceptEnabled(null), "Default for projectileIntercept should be true");
        assertEquals(10, BetterDogsGameRules.getInterceptorOwnerHealthThreshold(null), "Default for interceptorOwnerHealthThreshold should be 10");
        assertTrue(BetterDogsGameRules.getBoolean(null, BetterDogsGameRules.BD_PROJECTILE_INTERCEPT, true));
        assertEquals(10, BetterDogsGameRules.getInt(null, BetterDogsGameRules.BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD, 10));
    }

    @Test
    public void testAll124DeclaredGameRuleKeysNonNullAfterInit() throws Exception {
        BetterDogsGameRules.init();

        List<Field> keyFields = new ArrayList<>();
        List<String> nullFields = new ArrayList<>();
        for (Field field : BetterDogsGameRules.class.getDeclaredFields()) {
            int mods = field.getModifiers();
            if (Modifier.isPublic(mods) && Modifier.isStatic(mods) && GameRules.Key.class.isAssignableFrom(field.getType())) {
                keyFields.add(field);
                if (field.get(null) == null) {
                    nullFields.add(field.getName());
                }
            }
        }

        assertEquals(124, keyFields.size(), "Expected exactly 124 public static GameRules.Key fields");
        assertTrue(nullFields.isEmpty(), "All 124 GameRules.Key fields must be non-null after init(); nulls: " + nullFields);
    }

    @Test
    public void testTwentyFourDynamicAliasesRegistered() {
        Map<String, GameRules.Key<?>> dynamicRules = DynamicGameRuleManager.getDynamicRules();

        Map<String, GameRules.Key<?>> expectedAliases = new HashMap<>();
        expectedAliases.put("betterdogs:bd_dog_genetics_enabled", BetterDogsGameRules.BD_DOG_GENETICS_ENABLED);
        expectedAliases.put("betterdogs:dogGeneticsEnabled", BetterDogsGameRules.BD_DOG_GENETICS_ENABLED);
        expectedAliases.put("betterdogs:bd_max_scale_deviation", BetterDogsGameRules.BD_MAX_SCALE_DEVIATION);
        expectedAliases.put("betterdogs:maxScaleDeviation", BetterDogsGameRules.BD_MAX_SCALE_DEVIATION);
        expectedAliases.put("betterdogs:bd_personality_archetypes_enabled", BetterDogsGameRules.BD_ARCHETYPES_ENABLED);
        expectedAliases.put("betterdogs:personalityArchetypesEnabled", BetterDogsGameRules.BD_ARCHETYPES_ENABLED);
        expectedAliases.put("betterdogs:bd_personality_distribution_mode", BetterDogsGameRules.BD_ARCHETYPE_DISTRIBUTION_MODE);
        expectedAliases.put("betterdogs:personalityDistributionMode", BetterDogsGameRules.BD_ARCHETYPE_DISTRIBUTION_MODE);
        expectedAliases.put("betterdogs:bd_canine_explosive_reflex_enabled", BetterDogsGameRules.BD_CANINE_EXPLOSIVE_REFLEX_ENABLED);
        expectedAliases.put("betterdogs:canineExplosiveReflexEnabled", BetterDogsGameRules.BD_CANINE_EXPLOSIVE_REFLEX_ENABLED);
        expectedAliases.put("betterdogs:bd_hazard_flee_radius", BetterDogsGameRules.BD_HAZARD_FLEE_RADIUS);
        expectedAliases.put("betterdogs:hazardFleeRadius", BetterDogsGameRules.BD_HAZARD_FLEE_RADIUS);
        expectedAliases.put("betterdogs:bd_flee_speed_multiplier", BetterDogsGameRules.BD_FLEE_SPEED_MULTIPLIER);
        expectedAliases.put("betterdogs:fleeSpeedMultiplier", BetterDogsGameRules.BD_FLEE_SPEED_MULTIPLIER);
        expectedAliases.put("betterdogs:bd_ballistic_evasion_enabled", BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED);
        expectedAliases.put("betterdogs:ballisticEvasionEnabled", BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED);
        expectedAliases.put("betterdogs:bd_evasion_reaction_ticks", BetterDogsGameRules.BD_EVASION_REACTION_TICKS);
        expectedAliases.put("betterdogs:evasionReactionTicks", BetterDogsGameRules.BD_EVASION_REACTION_TICKS);
        expectedAliases.put("betterdogs:bd_evasion_lateral_impulse", BetterDogsGameRules.BD_EVASION_LATERAL_IMPULSE);
        expectedAliases.put("betterdogs:evasionLateralImpulse", BetterDogsGameRules.BD_EVASION_LATERAL_IMPULSE);
        expectedAliases.put("betterdogs:bd_projectile_intercept", BetterDogsGameRules.BD_PROJECTILE_INTERCEPT);
        expectedAliases.put("betterdogs:projectileIntercept", BetterDogsGameRules.BD_PROJECTILE_INTERCEPT);
        expectedAliases.put("betterdogs:bd_interceptor_owner_health_threshold", BetterDogsGameRules.BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD);
        expectedAliases.put("betterdogs:interceptorOwnerHealthThreshold", BetterDogsGameRules.BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD);

        assertEquals(24, expectedAliases.size());
        for (Map.Entry<String, GameRules.Key<?>> entry : expectedAliases.entrySet()) {
            assertNotNull(entry.getValue(), "Expected key for alias " + entry.getKey() + " must be non-null");
            assertSame(entry.getValue(), dynamicRules.get(entry.getKey()),
                    "DynamicGameRuleManager missing or mismatched alias: " + entry.getKey());
        }
    }

    @Test
    public void testDualPolarityAndServerGameRuleOverrides() {
        Map<GameRules.Key<?>, Boolean> boolOverrides = new HashMap<>();
        Map<GameRules.Key<?>, Integer> intOverrides = new HashMap<>();

        GameRules stubRules = new GameRules() {
            @Override
            public boolean getBoolean(GameRules.Key<GameRules.BooleanValue> key) {
                if (boolOverrides.containsKey(key)) {
                    return boolOverrides.get(key);
                }
                return super.getBoolean(key);
            }

            @Override
            public int getInt(GameRules.Key<GameRules.IntegerValue> key) {
                if (intOverrides.containsKey(key)) {
                    return intOverrides.get(key);
                }
                return super.getInt(key);
            }
        };

        StubServerLevel serverLevel = createStubServerLevel(stubRules);

        // False polarity overrides across all 5 boolean rules
        boolOverrides.put(BetterDogsGameRules.BD_DOG_GENETICS_ENABLED, false);
        boolOverrides.put(BetterDogsGameRules.BD_ARCHETYPES_ENABLED, false);
        boolOverrides.put(BetterDogsGameRules.BD_CANINE_EXPLOSIVE_REFLEX_ENABLED, false);
        boolOverrides.put(BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED, false);
        boolOverrides.put(BetterDogsGameRules.BD_PROJECTILE_INTERCEPT, false);

        assertFalse(BetterDogsGameRules.isDogGeneticsEnabled(serverLevel));
        assertFalse(BetterDogsGameRules.isArchetypesEnabled(serverLevel));
        assertFalse(BetterDogsGameRules.isCanineExplosiveReflexEnabled(serverLevel));
        assertFalse(BetterDogsGameRules.isBallisticEvasionEnabled(serverLevel));
        assertFalse(BetterDogsGameRules.isProjectileInterceptEnabled(serverLevel));

        // True polarity overrides across all 5 boolean rules
        boolOverrides.put(BetterDogsGameRules.BD_DOG_GENETICS_ENABLED, true);
        boolOverrides.put(BetterDogsGameRules.BD_ARCHETYPES_ENABLED, true);
        boolOverrides.put(BetterDogsGameRules.BD_CANINE_EXPLOSIVE_REFLEX_ENABLED, true);
        boolOverrides.put(BetterDogsGameRules.BD_BALLISTIC_EVASION_ENABLED, true);
        boolOverrides.put(BetterDogsGameRules.BD_PROJECTILE_INTERCEPT, true);

        assertTrue(BetterDogsGameRules.isDogGeneticsEnabled(serverLevel));
        assertTrue(BetterDogsGameRules.isArchetypesEnabled(serverLevel));
        assertTrue(BetterDogsGameRules.isCanineExplosiveReflexEnabled(serverLevel));
        assertTrue(BetterDogsGameRules.isBallisticEvasionEnabled(serverLevel));
        assertTrue(BetterDogsGameRules.isProjectileInterceptEnabled(serverLevel));

        // Non-default integer overrides across all 7 integer rules
        intOverrides.put(BetterDogsGameRules.BD_MAX_SCALE_DEVIATION, 75);
        intOverrides.put(BetterDogsGameRules.BD_ARCHETYPE_DISTRIBUTION_MODE, 1);
        intOverrides.put(BetterDogsGameRules.BD_HAZARD_FLEE_RADIUS, 48);
        intOverrides.put(BetterDogsGameRules.BD_FLEE_SPEED_MULTIPLIER, 240);
        intOverrides.put(BetterDogsGameRules.BD_EVASION_REACTION_TICKS, 45);
        intOverrides.put(BetterDogsGameRules.BD_EVASION_LATERAL_IMPULSE, 190);
        intOverrides.put(BetterDogsGameRules.BD_INTERCEPTOR_OWNER_HEALTH_THRESHOLD, 250);

        assertEquals(75, BetterDogsGameRules.getMaxScaleDeviation(serverLevel));
        assertEquals(1, BetterDogsGameRules.getArchetypeDistributionMode(serverLevel));
        assertEquals(48, BetterDogsGameRules.getHazardFleeRadius(serverLevel));
        assertEquals(240, BetterDogsGameRules.getFleeSpeedMultiplier(serverLevel));
        assertEquals(45, BetterDogsGameRules.getEvasionReactionTicks(serverLevel));
        assertEquals(190, BetterDogsGameRules.getEvasionLateralImpulse(serverLevel));
        assertEquals(250, BetterDogsGameRules.getInterceptorOwnerHealthThreshold(serverLevel));

        // Negative parity: null GameRules and empty GameRules preserve factory defaults without NPE
        StubServerLevel nullRulesLevel = createStubServerLevel(null);
        StubServerLevel emptyRulesLevel = createStubServerLevel(new GameRules());
        assertTrue(BetterDogsGameRules.isDogGeneticsEnabled(nullRulesLevel));
        assertTrue(BetterDogsGameRules.isDogGeneticsEnabled(emptyRulesLevel));
        assertEquals(30, BetterDogsGameRules.getMaxScaleDeviation(nullRulesLevel));
        assertEquals(30, BetterDogsGameRules.getMaxScaleDeviation(emptyRulesLevel));
        assertEquals(14, BetterDogsGameRules.getHazardFleeRadius(emptyRulesLevel));
        assertEquals(170, BetterDogsGameRules.getFleeSpeedMultiplier(emptyRulesLevel));
        assertEquals(20, BetterDogsGameRules.getEvasionReactionTicks(emptyRulesLevel));
        assertEquals(85, BetterDogsGameRules.getEvasionLateralImpulse(emptyRulesLevel));
        assertEquals(10, BetterDogsGameRules.getInterceptorOwnerHealthThreshold(emptyRulesLevel));
    }

    @Test
    public void testFiftyThreadConcurrentDynamicRuleAndAccessorStress() throws InterruptedException {
        final int threads = 50;
        final int iterationsPerThread = 200;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        List<Throwable> errors = new CopyOnWriteArrayList<>();

        String[] aliases = {
                "betterdogs:bd_dog_genetics_enabled", "betterdogs:dogGeneticsEnabled",
                "betterdogs:bd_max_scale_deviation", "betterdogs:maxScaleDeviation",
                "betterdogs:bd_personality_archetypes_enabled", "betterdogs:personalityArchetypesEnabled",
                "betterdogs:bd_personality_distribution_mode", "betterdogs:personalityDistributionMode",
                "betterdogs:bd_canine_explosive_reflex_enabled", "betterdogs:canineExplosiveReflexEnabled",
                "betterdogs:bd_hazard_flee_radius", "betterdogs:hazardFleeRadius",
                "betterdogs:bd_flee_speed_multiplier", "betterdogs:fleeSpeedMultiplier",
                "betterdogs:bd_ballistic_evasion_enabled", "betterdogs:ballisticEvasionEnabled",
                "betterdogs:bd_evasion_reaction_ticks", "betterdogs:evasionReactionTicks",
                "betterdogs:bd_evasion_lateral_impulse", "betterdogs:evasionLateralImpulse",
                "betterdogs:bd_projectile_intercept", "betterdogs:projectileIntercept",
                "betterdogs:bd_interceptor_owner_health_threshold", "betterdogs:interceptorOwnerHealthThreshold"
        };

        try {
            for (int t = 0; t < threads; t++) {
                executor.submit(() -> {
                    try {
                        startGate.await(5, TimeUnit.SECONDS);
                        for (int i = 0; i < iterationsPerThread; i++) {
                            String alias = aliases[i % aliases.length];
                            if (DynamicGameRuleManager.getDynamicRules().get(alias) == null) {
                                throw new IllegalStateException("Null dynamic rule for alias: " + alias);
                            }
                            if (!BetterDogsGameRules.isDogGeneticsEnabled(null)
                                    || BetterDogsGameRules.getMaxScaleDeviation(null) != 30
                                    || !BetterDogsGameRules.isArchetypesEnabled(null)
                                    || BetterDogsGameRules.getArchetypeDistributionMode(null) != 0
                                    || !BetterDogsGameRules.isCanineExplosiveReflexEnabled(null)
                                    || BetterDogsGameRules.getHazardFleeRadius(null) != 14
                                    || BetterDogsGameRules.getFleeSpeedMultiplier(null) != 170
                                    || !BetterDogsGameRules.isBallisticEvasionEnabled(null)
                                    || BetterDogsGameRules.getEvasionReactionTicks(null) != 20
                                    || BetterDogsGameRules.getEvasionLateralImpulse(null) != 85
                                    || !BetterDogsGameRules.isProjectileInterceptEnabled(null)
                                    || BetterDogsGameRules.getInterceptorOwnerHealthThreshold(null) != 10) {
                                throw new IllegalStateException("Unexpected fallback value during concurrent read");
                            }
                        }
                    } catch (Throwable ex) {
                        errors.add(ex);
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startGate.countDown();
            assertTrue(doneLatch.await(30, TimeUnit.SECONDS), "50-thread stress test timed out");
            assertTrue(errors.isEmpty(), "Concurrent reads produced errors: " + errors);
        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private static StubServerLevel createStubServerLevel(GameRules rules) {
        try {
            StubServerLevel level = (StubServerLevel) UNSAFE.allocateInstance(StubServerLevel.class);
            level.rules = rules;
            return level;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate StubServerLevel", e);
        }
    }

    private static final class StubServerLevel extends ServerLevel {
        GameRules rules;

        private StubServerLevel() {
            super(null, null, null, null, null, null, null, false, 0L, null, false, null);
        }

        @Override
        public GameRules getGameRules() {
            return this.rules;
        }
    }
}
