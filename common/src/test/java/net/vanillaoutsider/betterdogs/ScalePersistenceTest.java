// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import com.mojang.serialization.DataResult;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.dasik.social.api.genetics.EntityGenetics;
import net.dasik.social.api.genetics.GeneticsHolder;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import net.vanillaoutsider.betterdogs.util.WolfCommandHelper;
import net.vanillaoutsider.betterdogs.util.WolfCureHelper;
import net.vanillaoutsider.betterdogs.util.WolfInbreedingHelper;
import net.vanillaoutsider.betterdogs.util.WolfScaleGeneticsHelper;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BL-BD-071: Genetic Scale Persistence Across Spawn, Ticks, Breeding, Runt, Cure & Reload")
class ScalePersistenceTest {

    private static Unsafe unsafe;
    private static Field entityPositionField;
    private static int nextEntityId = 4000;

    @BeforeAll
    static void setupMinecraftBootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        unsafe = (Unsafe) theUnsafe.get(null);
        entityPositionField = Entity.class.getDeclaredField("position");
        entityPositionField.setAccessible(true);
        WolfPersistentData.getWolfData(null);
        ScaleStubWolf warmup = createStubWolf(UUID.randomUUID(), null, false);
        WolfPersistentData.getWolfData(warmup);
    }

    private static ScaleStubWolf createStubWolf(UUID wolfUuid, @Nullable UUID ownerUuid, boolean tame) throws Exception {
        ScaleStubWolf stub = (ScaleStubWolf) unsafe.allocateInstance(ScaleStubWolf.class);
        stub.setId(nextEntityId++);
        entityPositionField.set(stub, new Vec3(0.0, 64.0, 0.0));
        stub.setBoundingBox(new AABB(0.0, 64.0, 0.0, 1.0, 65.0, 1.0));
        StubServerLevel level = (StubServerLevel) unsafe.allocateInstance(StubServerLevel.class);
        stub.init(wolfUuid, ownerUuid, tame, level);
        return stub;
    }

    private static StubPlayer createStubPlayer(UUID playerUuid) throws Exception {
        StubPlayer player = (StubPlayer) unsafe.allocateInstance(StubPlayer.class);
        player.init(playerUuid);
        return player;
    }

    private static StubCommandSourceStack createCommandSource() throws Exception {
        StubCommandSourceStack source = (StubCommandSourceStack) unsafe.allocateInstance(StubCommandSourceStack.class);
        source.init();
        return source;
    }

    private static Path findMainSourceRoot() {
        Path cursor = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (cursor != null) {
            Path multi = cursor.resolve("common/src/main/java/net/vanillaoutsider/betterdogs");
            if (Files.exists(multi)) {
                return multi;
            }
            Path single = cursor.resolve("src/main/java/net/vanillaoutsider/betterdogs");
            if (Files.exists(single)) {
                return single;
            }
            cursor = cursor.getParent();
        }
        throw new IllegalStateException("Could not locate Better Dogs main source root from " + System.getProperty("user.dir"));
    }

    /**
     * Simulates WolfSocialMixin.onTickTail persistence sync check at tick 1 and periodic tick 100.
     */
    private static void simulateSocialTickTail(ScaleStubWolf wolf, int tickCount) {
        wolf.tickCount = tickCount;
        if (!wolf.socialInitialized || wolf.tickCount % 100 == 0) {
            if (wolf.betterdogs$getDNA() == 0L) {
                wolf.betterdogs$setDNA(wolf.getUUID().getMostSignificantBits());
            } else {
                var scaleAttr = wolf.getAttribute(Attributes.SCALE);
                if (scaleAttr != null && scaleAttr.getBaseValue() != wolf.betterdogs$getSocialScale()) {
                    scaleAttr.setBaseValue(wolf.betterdogs$getSocialScale());
                }
            }
            wolf.socialInitialized = true;
        }
    }

    @Test
    @DisplayName("Wild spawn persists non-1.0f scale across ticks 1 & 100, pitch modulation, and /betterdogs inspect")
    void testWildSpawnPersistsScalePitchAndInspectAcrossTicks() throws Exception {
        ScaleStubWolf wildWolf = createStubWolf(UUID.randomUUID(), null, false);
        wildWolf.betterdogs$setDNA(0x1122334455667788L);
        wildWolf.betterdogs$setPersonality(WolfPersonality.NORMAL);

        RandomSource random = RandomSource.create(12345L);
        float wildScale = WolfScaleGeneticsHelper.generateWildWolfScale(null, random);
        while (Math.abs(wildScale - 1.0f) < 0.08f) {
            wildScale = WolfScaleGeneticsHelper.generateWildWolfScale(null, random);
        }

        // WolfSpawnMixin path: persist via betterdogs$setSocialScale
        wildWolf.betterdogs$setSocialScale(wildScale);

        simulateSocialTickTail(wildWolf, 1);
        assertEquals(wildScale, WolfPersistentData.getScale(wildWolf), 1e-5f,
                "Tick 1 must retain persisted wild scale");
        assertEquals(wildScale, (float) wildWolf.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f,
                "Tick 1 must retain Attributes.SCALE base value without snapping to 1.0f");

        simulateSocialTickTail(wildWolf, 100);
        assertEquals(wildScale, WolfPersistentData.getScale(wildWolf), 1e-5f,
                "Tick 100 must retain persisted wild scale");
        assertEquals(wildScale, (float) wildWolf.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f,
                "Tick 100 must retain Attributes.SCALE base value");

        float expectedPitch = CanineGeneticsHelper.calculateVoicePitch(1.0f, wildScale);
        float actualPitch = CanineGeneticsHelper.getModulatedVoicePitch(wildWolf, 1.0f);
        assertEquals(expectedPitch, actualPitch, 1e-4f, "Voice pitch must match persisted scale");
        assertNotEquals(1.0f, actualPitch, 0.01f, "Voice pitch must not collapse to 1.000f");

        // Pillar 3: /betterdogs inspect reads persisted Attributes.SCALE and non-1.000 pitch timbre
        StubCommandSourceStack source = createCommandSource();
        int inspected = WolfCommandHelper.inspectTargets(source, List.of(wildWolf));
        assertEquals(1, inspected);
        assertFalse(source.messages.isEmpty());
        String inspectLine = source.messages.get(0);
        String expectedScaleToken = String.format(Locale.ROOT, "%.2fx", wildScale);
        String expectedPitchToken = String.format(Locale.ROOT, "%.3fx", expectedPitch);
        assertTrue(inspectLine.contains(expectedScaleToken),
                "Inspect output must display persisted scale " + expectedScaleToken + " but was: " + inspectLine);
        assertTrue(inspectLine.contains(expectedPitchToken),
                "Inspect output must display modulated pitch " + expectedPitchToken + " but was: " + inspectLine);
    }

    @Test
    @DisplayName("Breeding, inbred runt penalty, and Golden Apple cure persist updated scale across ticks")
    void testBreedRuntAndCurePersistUpdatedScaleAcrossTicks() throws Exception {
        UUID ownerUuid = UUID.randomUUID();
        StubPlayer owner = createStubPlayer(ownerUuid);
        StubServerLevel serverLevel = (StubServerLevel) unsafe.allocateInstance(StubServerLevel.class);

        ScaleStubWolf parent1 = createStubWolf(UUID.randomUUID(), ownerUuid, true);
        ScaleStubWolf parent2 = createStubWolf(UUID.randomUUID(), ownerUuid, true);
        parent1.betterdogs$setSocialScale(1.28f);
        parent2.betterdogs$setSocialScale(1.26f);

        // Simulate WolfBreedingMixin reading parent scale (persisted > 0 fallback attribute) & writing baby scale
        float p1Scale = parent1.betterdogs$getSocialScale() > 0.0f
                ? parent1.betterdogs$getSocialScale()
                : (float) parent1.getAttribute(Attributes.SCALE).getBaseValue();
        float p2Scale = parent2.betterdogs$getSocialScale() > 0.0f
                ? parent2.betterdogs$getSocialScale()
                : (float) parent2.getAttribute(Attributes.SCALE).getBaseValue();
        assertEquals(1.28f, p1Scale, 1e-5f);
        assertEquals(1.26f, p2Scale, 1e-5f);

        ScaleStubWolf baby = createStubWolf(UUID.randomUUID(), ownerUuid, true);
        baby.betterdogs$setPersonality(WolfPersonality.NORMAL);
        float babyScale = WolfScaleGeneticsHelper.calculateOffspringScale(null, p1Scale, p2Scale, RandomSource.create(77L));
        assertTrue(babyScale > 1.05f, "Offspring of large parents must be > 1.05f, got " + babyScale);
        baby.betterdogs$setSocialScale(babyScale);

        // Tick 1 initializes DNA on bred puppy, Tick 100 verifies sync guard preserves babyScale
        simulateSocialTickTail(baby, 1);
        simulateSocialTickTail(baby, 100);
        assertEquals(babyScale, WolfPersistentData.getScale(baby), 1e-5f);
        assertEquals(babyScale, (float) baby.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f);

        // Mark inbred and scale down baby to < 1.0f so runt and cure transitions are distinct
        baby.betterdogs$setSocialScale(0.90f);
        WolfPersistentData.setPersistedParentsAndInbred(baby, parent1.getUUID(), parent2.getUUID(), true);
        WolfInbreedingHelper.processBreedingLineage(baby, parent1, parent2, serverLevel);

        float expectedRuntScale = WolfInbreedingHelper.calculateRuntScale(0.90f, WolfInbreedingHelper.RUNT_SCALE_MULTIPLIER);
        assertEquals(expectedRuntScale, WolfPersistentData.getScale(baby), 1e-5f,
                "Inbred runt scale must persist into WolfPersistentData");
        assertEquals(expectedRuntScale, (float) baby.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f,
                "Inbred runt scale must update Attributes.SCALE");

        simulateSocialTickTail(baby, 200);
        assertEquals(expectedRuntScale, (float) baby.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f,
                "Runt scale must persist across subsequent social sync ticks");

        // Cure inbred wolf with Golden Apple
        ItemStack apple = new ItemStack(Holder.direct(Items.GOLDEN_APPLE));
        InteractionResult cureResult = WolfCureHelper.tryCureInbredWolf(baby, owner, InteractionHand.MAIN_HAND, apple);
        assertEquals(InteractionResult.SUCCESS, cureResult, "Golden Apple cure must succeed");
        assertFalse(WolfPersistentData.isPersistedInbred(baby), "Cure must clear inbred flag");
        assertEquals(1.0f, WolfPersistentData.getScale(baby), 1e-5f,
                "Cure must persist restored 1.0f scale in WolfPersistentData");
        assertEquals(1.0f, (float) baby.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f,
                "Cure must update Attributes.SCALE to 1.0f");

        simulateSocialTickTail(baby, 300);
        assertEquals(1.0f, (float) baby.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f,
                "Cured scale must persist across subsequent ticks");

        // Giant inbred dog (> 1.0f after runt multiplier) must retain its giant scale after Golden Apple cure
        ScaleStubWolf giantRunt = createStubWolf(UUID.randomUUID(), ownerUuid, true);
        giantRunt.betterdogs$setPersonality(WolfPersonality.AGGRESSIVE);
        giantRunt.betterdogs$setSocialScale(1.15f);
        WolfPersistentData.setPersistedParentsAndInbred(giantRunt, parent1.getUUID(), parent2.getUUID(), true);
        InteractionResult giantCure = WolfCureHelper.tryCureInbredWolf(giantRunt, owner, InteractionHand.MAIN_HAND, new ItemStack(Holder.direct(Items.GOLDEN_APPLE)));
        assertEquals(InteractionResult.SUCCESS, giantCure);
        assertEquals(1.15f, WolfPersistentData.getScale(giantRunt), 1e-5f,
                "Curing giant inbred wolf (> 1.0f) must preserve its persisted giant scale");
        simulateSocialTickTail(giantRunt, 100);
        assertEquals(1.15f, (float) giantRunt.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f);
    }

    @Test
    @DisplayName("WolfPersistentData.CODEC encode/decode round-trip and reload sync preserve non-1.0f scale & pitch")
    void testCodecRoundTripAndReloadPreservesScaleAndPitch() throws Exception {
        ScaleStubWolf original = createStubWolf(UUID.randomUUID(), null, false);
        original.betterdogs$setDNA(987654321L);
        original.betterdogs$setPersonality(WolfPersonality.AGGRESSIVE);
        original.betterdogs$setSocialScale(1.33f);

        WolfPersistentData saved = WolfPersistentData.getWolfData(original);
        DataResult<Tag> encoded = WolfPersistentData.CODEC.encodeStart(NbtOps.INSTANCE, saved);
        assertTrue(encoded.result().isPresent(), "WolfPersistentData must encode to NBT");

        DataResult<WolfPersistentData> decoded = WolfPersistentData.CODEC.parse(NbtOps.INSTANCE, encoded.result().get());
        assertTrue(decoded.result().isPresent(), "WolfPersistentData must decode from NBT");
        WolfPersistentData roundTripped = decoded.result().get();
        assertEquals(1.33f, roundTripped.scale(), 1e-5f, "Decoded scale must match saved 1.33f");

        // Simulate entity reload where fresh attribute starts at 1.0 until tick 1 sync runs
        ScaleStubWolf reloaded = createStubWolf(original.getUUID(), null, false);
        WolfPersistentData.setWolfData(reloaded, roundTripped);
        assertEquals(1.0d, reloaded.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5d);

        simulateSocialTickTail(reloaded, 1);
        assertEquals(1.33f, (float) reloaded.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f,
                "Tick 1 after reload must restore Attributes.SCALE from persisted WolfPersistentData");
        assertEquals(CanineGeneticsHelper.calculateVoicePitch(1.0f, 1.33f),
                CanineGeneticsHelper.getModulatedVoicePitch(reloaded, 1.0f), 1e-4f);
    }

    @Test
    @DisplayName("Negative Guards: NaN, <= 0, and Infinity sanitize to 1.0f while DEFAULT 1.0f wolves stay untouched")
    void testNegativeGuardsAndDefaultWolfPreservation() throws Exception {
        float[] invalidInputs = {Float.NaN, -1.0f, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, 0.0f};
        for (float invalid : invalidInputs) {
            WolfPersistentData constructed = new WolfPersistentData(
                    0, 0, false, "", 0L, 42L, invalid, Map.of(), Optional.empty(), false, Optional.empty(),
                    false, false, 0, "", 0L, false, ""
            );
            assertEquals(1.0f, constructed.scale(), 1e-6f,
                    "Compact canonical constructor must sanitize invalid scale " + invalid + " to 1.0f");

            ScaleStubWolf wolf = createStubWolf(UUID.randomUUID(), null, false);
            WolfPersistentData.setScale(-1, 0, false, "", 0L, 42L, wolf, invalid, Map.of(), Optional.empty(), false, Optional.empty(), false);
            assertEquals(1.0f, WolfPersistentData.getScale(wolf), 1e-6f,
                    "WolfPersistentData.setScale must sanitize invalid scale " + invalid + " to 1.0f");

            wolf.betterdogs$setSocialScale(1.25f);
            wolf.betterdogs$setSocialScale(invalid);
            assertEquals(1.0f, WolfPersistentData.getScale(wolf), 1e-6f,
                    "betterdogs$setSocialScale must sanitize invalid scale " + invalid + " to 1.0f");
            assertEquals(1.0f, (float) wolf.getAttribute(Attributes.SCALE).getBaseValue(), 1e-6f,
                    "Attributes.SCALE must sanitize invalid scale " + invalid + " to 1.0f");

            CompoundTag corruptNbt = new CompoundTag();
            corruptNbt.putFloat("scale", invalid);
            DataResult<WolfPersistentData> parsed = WolfPersistentData.CODEC.parse(NbtOps.INSTANCE, corruptNbt);
            assertTrue(parsed.result().isPresent(), "CODEC decode of invalid NBT scale must succeed safely");
            assertEquals(1.0f, parsed.result().get().scale(), 1e-6f,
                    "CODEC decode must sanitize invalid NBT scale " + invalid + " to 1.0f");
        }

        // Anti-Goal #4: Existing wolves with DEFAULT (1.0f) scale stay 1.0f across ticks
        ScaleStubWolf existingDefaultWolf = createStubWolf(UUID.randomUUID(), null, false);
        existingDefaultWolf.betterdogs$setDNA(777777L);
        assertEquals(1.0f, WolfPersistentData.getScale(existingDefaultWolf), 1e-6f);
        simulateSocialTickTail(existingDefaultWolf, 1);
        simulateSocialTickTail(existingDefaultWolf, 100);
        assertEquals(1.0f, WolfPersistentData.getScale(existingDefaultWolf), 1e-6f);
        assertEquals(1.0f, (float) existingDefaultWolf.getAttribute(Attributes.SCALE).getBaseValue(), 1e-6f);
    }

    @Test
    @DisplayName("Pillars 1 & 2: 200-run Chaos Fuzz and 16-Thread Concurrency preserve isolated per-wolf scales")
    void testChaosFuzzAndConcurrencyIsolation() throws Exception {
        // Pillar 1: 200 randomized spawn -> tick -> breed -> runt -> cure cycles
        RandomSource random = RandomSource.create(0xDA51C071L);
        for (int i = 0; i < 200; i++) {
            float wildScale = WolfScaleGeneticsHelper.generateWildWolfScale(null, random);
            if (Math.abs(wildScale - 1.0f) < 0.02f) {
                wildScale = (i % 2 == 0) ? 1.18f : 0.82f;
            }

            ScaleStubWolf wolf = createStubWolf(UUID.randomUUID(), null, false);
            wolf.betterdogs$setDNA(1000L + i);
            wolf.betterdogs$setSocialScale(wildScale);

            simulateSocialTickTail(wolf, 1);
            simulateSocialTickTail(wolf, 100);
            assertEquals(wildScale, WolfPersistentData.getScale(wolf), 1e-5f,
                    "Fuzz iteration " + i + " lost persisted scale");
            assertEquals(wildScale, (float) wolf.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f,
                    "Fuzz iteration " + i + " lost Attributes.SCALE");
            assertNotEquals(1.0f, CanineGeneticsHelper.getModulatedVoicePitch(wolf, 1.0f), 0.002f,
                    "Fuzz iteration " + i + " collapsed pitch to 1.000f");
        }

        // Pillar 2: Concurrent per-wolf writes across threads maintain isolated WolfPersistentData
        final int threads = 16;
        final int wolvesPerThread = 50;
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger failures = new AtomicInteger(0);

        List<List<ScaleStubWolf>> threadWolves = new ArrayList<>(threads);
        for (int t = 0; t < threads; t++) {
            List<ScaleStubWolf> list = new ArrayList<>(wolvesPerThread);
            for (int w = 0; w < wolvesPerThread; w++) {
                list.add(createStubWolf(UUID.randomUUID(), null, false));
            }
            threadWolves.add(list);
        }

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int t = 0; t < threads; t++) {
                final int tid = t;
                final List<ScaleStubWolf> myWolves = threadWolves.get(t);
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        start.await();
                        for (int w = 0; w < wolvesPerThread; w++) {
                            ScaleStubWolf wolf = myWolves.get(w);
                            float targetScale = 0.72f + ((tid * 3 + w) % 60) * 0.011f;
                            wolf.betterdogs$setDNA(50_000L + tid * 1000L + w);
                            wolf.betterdogs$setSocialScale(targetScale);
                            simulateSocialTickTail(wolf, 1);
                            float stored = WolfPersistentData.getScale(wolf);
                            float attr = (float) wolf.getAttribute(Attributes.SCALE).getBaseValue();
                            if (Math.abs(stored - targetScale) > 1e-5f || Math.abs(attr - targetScale) > 1e-5f) {
                                failures.incrementAndGet();
                            }
                        }
                    } catch (Throwable ex) {
                        failures.incrementAndGet();
                    } finally {
                        done.countDown();
                    }
                });
            }
            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            assertTrue(done.await(15, TimeUnit.SECONDS));
        }
        assertEquals(0, failures.get(), "Concurrent per-wolf scale writes must remain isolated without corruption");
    }

    @Test
    @DisplayName("Pillar 5: Mixin/Helper persistence contracts, zero client imports, and GPLv3 headers intact")
    void testMixinAndHelperContractsAndSidedness() throws IOException {
        Path root = findMainSourceRoot();
        Path spawnMixin = root.resolve("mixin/WolfSpawnMixin.java");
        Path breedingMixin = root.resolve("mixin/WolfBreedingMixin.java");
        Path socialMixin = root.resolve("mixin/WolfSocialMixin.java");
        Path inbreedingHelper = root.resolve("util/WolfInbreedingHelper.java");
        Path cureHelper = root.resolve("util/WolfCureHelper.java");
        Path persistentData = root.resolve("WolfPersistentData.java");

        String spawnSrc = Files.readString(spawnMixin, StandardCharsets.UTF_8);
        String breedingSrc = Files.readString(breedingMixin, StandardCharsets.UTF_8);
        String socialSrc = Files.readString(socialMixin, StandardCharsets.UTF_8);
        String inbreedingSrc = Files.readString(inbreedingHelper, StandardCharsets.UTF_8);
        String cureSrc = Files.readString(cureHelper, StandardCharsets.UTF_8);
        String persistentSrc = Files.readString(persistentData, StandardCharsets.UTF_8);

        assertTrue(spawnSrc.contains("ext.betterdogs$setSocialScale(wildScale);"),
                "WolfSpawnMixin must persist wildScale via ext.betterdogs$setSocialScale(wildScale)");
        assertFalse(spawnSrc.contains("scaleAttr.setBaseValue(wildScale)"),
                "WolfSpawnMixin must not bypass persistence with raw scaleAttr.setBaseValue(wildScale)");

        assertTrue(breedingSrc.contains("ext1.betterdogs$getSocialScale()"),
                "WolfBreedingMixin must prefer persisted social scale on parent1");
        assertTrue(breedingSrc.contains("ext2.betterdogs$getSocialScale()"),
                "WolfBreedingMixin must prefer persisted social scale on parent2");
        assertTrue(breedingSrc.contains("babyExt.betterdogs$setSocialScale(babyScale);"),
                "WolfBreedingMixin must persist babyScale via babyExt.betterdogs$setSocialScale(babyScale)");
        assertFalse(breedingSrc.contains("babyScaleAttr.setBaseValue(babyScale)"),
                "WolfBreedingMixin must not bypass persistence with raw babyScaleAttr.setBaseValue(babyScale)");

        assertTrue(socialSrc.contains("Float.isFinite(scale) && scale > 0.0f"),
                "WolfSocialMixin.betterdogs$setSocialScale must sanitize invalid floats");
        assertTrue(socialSrc.contains("WolfPersistentData.setScale("),
                "WolfSocialMixin.betterdogs$setSocialScale must persist via WolfPersistentData.setScale");

        assertTrue(inbreedingSrc.contains("import net.vanillaoutsider.betterdogs.WolfExtensions;"),
                "WolfInbreedingHelper must import WolfExtensions");
        assertTrue(inbreedingSrc.contains("ext.betterdogs$setSocialScale(runtScale);"),
                "WolfInbreedingHelper must persist runtScale via ext.betterdogs$setSocialScale(runtScale)");

        assertTrue(cureSrc.contains("ext.betterdogs$setSocialScale(curedScale);"),
                "WolfCureHelper must persist curedScale via ext.betterdogs$setSocialScale(curedScale)");

        assertTrue(persistentSrc.contains("scale = (Float.isFinite(scale) && scale > 0.0f) ? scale : 1.0f;"),
                "WolfPersistentData compact constructor must sanitize invalid scales");
        assertTrue(persistentSrc.contains("float safeScale = (Float.isFinite(scale) && scale > 0.0f) ? scale : 1.0f;"),
                "WolfPersistentData.setScale must sanitize invalid scales");

        for (String src : List.of(spawnSrc, breedingSrc, socialSrc, inbreedingSrc, cureSrc, persistentSrc)) {
            assertTrue(src.contains("GNU GPLv3"), "Missing GNU GPLv3 header");
            assertFalse(src.contains("import net.minecraft.client."), "Server-side safety violation: client import");
            assertFalse(src.contains("import com.mojang.blaze3d."), "Server-side safety violation: blaze3d import");
        }
    }

    private static final class StubCommandSourceStack extends CommandSourceStack {
        private List<String> messages;

        private StubCommandSourceStack() {
            super(null, null, null, null, null, null, null, null, null);
        }

        void init() {
            this.messages = new ArrayList<>();
        }

        @Override
        public void sendSuccess(Supplier<Component> message, boolean broadcastToOps) {
            this.messages.add(message.get().getString());
        }

        @Override
        public void sendFailure(Component message) {
            this.messages.add(message.getString());
        }
    }

    private static final class StubPlayer extends Player {
        private UUID uuid;
        private Abilities abilities;

        private StubPlayer() {
            super(null, null);
        }

        void init(UUID uuid) {
            this.uuid = uuid;
            this.abilities = new Abilities();
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public Abilities getAbilities() {
            return this.abilities;
        }

        @Override
        public GameType gameMode() {
            return GameType.SURVIVAL;
        }
    }

    private static final class StubServerLevel extends ServerLevel {
        private StubServerLevel() {
            super(null, null, null, null, null, null, false, 0L, null, false);
        }

        @Override
        public boolean isClientSide() {
            return false;
        }

        @Override
        public void playSound(@Nullable Entity source, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch) {
        }

        @Override
        public <T extends ParticleOptions> int sendParticles(T particle, double x, double y, double z, int count, double dx, double dy, double dz, double speed) {
            return count;
        }
    }

    private static final class ScaleStubWolf extends Wolf implements WolfExtensions, GeneticsHolder {
        private UUID uuid;
        private @Nullable UUID ownerUuid;
        private boolean tame;
        private Level level;
        private AttributeInstance scaleAttribute;
        private float health;
        private EntityGenetics genetics;
        boolean socialInitialized;

        private ScaleStubWolf() {
            super(null, null);
        }

        void init(UUID uuid, @Nullable UUID ownerUuid, boolean tame, Level level) {
            this.uuid = uuid;
            this.ownerUuid = ownerUuid;
            this.tame = tame;
            this.level = level;
            this.scaleAttribute = new AttributeInstance(Attributes.SCALE, instance -> {});
            this.health = 20.0f;
            this.genetics = EntityGenetics.DEFAULT;
            this.socialInitialized = false;
        }

        @Override
        public EntityGenetics dasik$getGenetics() {
            return this.genetics;
        }

        @Override
        public void dasik$setGenetics(EntityGenetics genetics) {
            this.genetics = genetics != null ? genetics : EntityGenetics.DEFAULT;
        }

        @Override
        public @Nullable AttributeInstance getAttribute(Holder<Attribute> attribute) {
            if (Attributes.SCALE.equals(attribute)) {
                return this.scaleAttribute;
            }
            return null;
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public String getStringUUID() {
            return this.uuid.toString();
        }

        @Override
        public Component getDisplayName() {
            return Component.literal("ScaleTestWolf");
        }

        @Override
        public EntityType<?> getType() {
            return EntityType.WOLF;
        }

        @Override
        public Level level() {
            return this.level;
        }

        @Override
        public boolean isTame() {
            return this.tame;
        }

        @Override
        public boolean isOwnedBy(LivingEntity entity) {
            return entity != null && this.ownerUuid != null && this.ownerUuid.equals(entity.getUUID());
        }

        @Override
        public float getHealth() {
            return this.health;
        }

        @Override
        public void setHealth(float health) {
            this.health = health;
        }

        @Override
        public @Nullable EntityReference<LivingEntity> getOwnerReference() {
            return this.ownerUuid != null ? EntityReference.of(this.ownerUuid) : null;
        }

        @Override
        public WolfPersonality betterdogs$getPersonality() {
            return WolfPersistentData.getPersistedPersonality(this);
        }

        @Override
        public void betterdogs$setPersonality(WolfPersonality personality) {
            WolfPersistentData.setPersistedPersonality(this, personality);
        }

        @Override
        public boolean betterdogs$hasPersonality() {
            return WolfPersistentData.hasPersistedPersonality(this);
        }

        @Override
        public int betterdogs$getLastDamageTime() {
            return 0;
        }

        @Override
        public void betterdogs$setLastDamageTime(int time) {
        }

        @Override
        public boolean betterdogs$isSubmissive() {
            return false;
        }

        @Override
        public void betterdogs$setSubmissive(boolean submissive) {
        }

        @Override
        public String betterdogs$getBloodFeudTarget() {
            return "";
        }

        @Override
        public void betterdogs$setBloodFeudTarget(String targetUuid) {
        }

        @Override
        public boolean betterdogs$hasBloodFeud() {
            return false;
        }

        @Override
        public long betterdogs$getLastMischiefDay() {
            return 0L;
        }

        @Override
        public void betterdogs$setLastMischiefDay(long day) {
        }

        @Override
        public boolean betterdogs$isBeingDisciplined() {
            return false;
        }

        @Override
        public void betterdogs$setBeingDisciplined(boolean isBeingDisciplined) {
        }

        @Override
        public void betterdogs$setSocialState(@Nullable LivingEntity target, SocialAction action, int maxDurationTicks) {
        }

        @Override
        public @Nullable LivingEntity betterdogs$getSocialTarget() {
            return null;
        }

        @Override
        public SocialAction betterdogs$getSocialAction() {
            return SocialAction.NONE;
        }

        @Override
        public boolean betterdogs$isSocialModeActive() {
            return false;
        }

        @Override
        public void betterdogs$tickSocialMode() {
        }

        @Override
        public long betterdogs$getDNA() {
            return WolfPersistentData.getDNA(this);
        }

        @Override
        public void betterdogs$setDNA(long dna) {
            WolfPersistentData.setDNA(this, dna);
        }

        @Override
        public float betterdogs$getSocialScale() {
            return WolfPersistentData.getScale(this);
        }

        @Override
        public void betterdogs$setSocialScale(float scale) {
            float safeScale = (Float.isFinite(scale) && scale > 0.0f) ? scale : 1.0f;
            WolfPersistentData current = WolfPersistentData.getWolfData(this);
            WolfPersistentData.setScale(current.personalityId(), current.lastDamageTime(), current.submissive(),
                    current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), this, safeScale,
                    current.affinityMap(), current.leaderUuid(), current.guardMode(), current.guardPos(), current.adoptable());
            AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
            if (scaleAttr != null) {
                scaleAttr.setBaseValue(safeScale);
            }
        }

        @Override
        public @Nullable EntitySocialScheduler betterdogs$getScheduler() {
            return null;
        }

        @Override
        public EntitySocialScheduler betterdogs$getOrInitializeScheduler() {
            return null;
        }

        @Override
        public void betterdogs$tickScheduler() {
        }

        @Override
        public int betterdogs$getAffinity(String targetUuid) {
            return 0;
        }

        @Override
        public void betterdogs$adjustAffinity(String targetUuid, int delta) {
        }

        @Override
        public @Nullable UUID betterdogs$getLeaderUuid() {
            return null;
        }

        @Override
        public void betterdogs$setLeaderUuid(@Nullable UUID uuid) {
        }

        @Override
        public boolean betterdogs$isGuardMode() {
            return false;
        }

        @Override
        public void betterdogs$setGuardMode(boolean guardMode) {
        }

        @Override
        public @Nullable BlockPos betterdogs$getGuardPos() {
            return null;
        }

        @Override
        public void betterdogs$setGuardPos(@Nullable BlockPos pos) {
        }

        @Override
        public boolean betterdogs$isSittingManually() {
            return false;
        }

        @Override
        public void betterdogs$setSittingManually(boolean sitting) {
        }

        @Override
        public boolean betterdogs$isAdoptable() {
            return false;
        }

        @Override
        public void betterdogs$setAdoptable(boolean adoptable) {
        }

        @Override
        public float betterdogs$getHealthBonus() {
            return 0.0f;
        }

        @Override
        public void betterdogs$setHealthBonus(float hp) {
        }

        @Override
        public float betterdogs$getDamageMod() {
            return 0.0f;
        }

        @Override
        public void betterdogs$setDamageMod(float dmg) {
        }

        @Override
        public float betterdogs$getSpeedMod() {
            return 0.0f;
        }

        @Override
        public void betterdogs$setSpeedMod(float speed) {
        }

        @Override
        public boolean betterdogs$areStatsRolled() {
            return false;
        }

        @Override
        public void betterdogs$setStatsRolled(boolean rolled) {
        }

        @Override
        public int betterdogs$getPushWaitTimer() {
            return 0;
        }

        @Override
        public void betterdogs$setPushWaitTimer(int ticks) {
        }

        @Override
        public @Nullable BlockPos betterdogs$getPathfindAvoidPos() {
            return null;
        }

        @Override
        public void betterdogs$setPathfindAvoidPos(@Nullable BlockPos pos) {
        }

        @Override
        public GoalSelector betterdogs$getGoalSelector() {
            return null;
        }

        @Override
        public long betterdogs$getSoothedTime() {
            return 0L;
        }

        @Override
        public void betterdogs$setSoothedTime(long time) {
        }

        @Override
        public @Nullable BlockPos betterdogs$getSoundLocationTarget() {
            return null;
        }

        @Override
        public void betterdogs$setSoundLocationTarget(@Nullable BlockPos pos) {
        }

        @Override
        public int betterdogs$getPassiveOverrideTicks() {
            return 0;
        }

        @Override
        public void betterdogs$setPassiveOverrideTicks(int ticks) {
        }

        @Override
        public String betterdogs$getNemesisEntityType() {
            return "";
        }

        @Override
        public void betterdogs$setNemesisEntityType(String type) {
        }

        @Override
        public long betterdogs$getNemesisExpiryTime() {
            return 0L;
        }

        @Override
        public void betterdogs$setNemesisExpiryTime(long time) {
        }

        @Override
        public boolean betterdogs$hasFetchedItem() {
            return false;
        }

        @Override
        public void betterdogs$setHasFetchedItem(boolean fetched) {
        }

        @Override
        public @Nullable ItemStack betterdogs$getFetchedItemStack() {
            return null;
        }

        @Override
        public void betterdogs$setFetchedItemStack(@Nullable ItemStack stack) {
        }

        @Override
        public int betterdogs$getZoomiesTicks() {
            return 0;
        }

        @Override
        public void betterdogs$setZoomiesTicks(int ticks) {
        }

        @Override
        public int betterdogs$getHowlingTicks() {
            return 0;
        }

        @Override
        public void betterdogs$setHowlingTicks(int ticks) {
        }
    }
}
