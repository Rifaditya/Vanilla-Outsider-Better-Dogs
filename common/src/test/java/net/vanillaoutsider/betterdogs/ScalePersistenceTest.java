// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.genetics.CanineGeneticsHelper;
import net.vanillaoutsider.betterdogs.mixin.WolfMixin;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfCommandHelper;
import net.vanillaoutsider.betterdogs.util.WolfCureHelper;
import net.vanillaoutsider.betterdogs.util.WolfInbreedingHelper;
import net.vanillaoutsider.betterdogs.util.WolfLitterHelper;
import net.vanillaoutsider.betterdogs.util.WolfPersonalityStatHelper;
import net.vanillaoutsider.betterdogs.util.WolfScaleGeneticsHelper;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BL-BD-071: Genetic Scale Persistence Across Spawn, Breeding, Runt, Cure & Reload (1.21.1)")
class ScalePersistenceTest {

    private static Unsafe unsafe;
    private static Field entityPositionField;
    private static Field wolfMixinSocialScaleField;
    private static int nextEntityId = 4100;

    @BeforeAll
    static void setupMinecraftBootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BetterDogsGameRules.init();
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        unsafe = (Unsafe) theUnsafe.get(null);
        entityPositionField = Entity.class.getDeclaredField("position");
        entityPositionField.setAccessible(true);
        wolfMixinSocialScaleField = WolfMixin.class.getDeclaredField("betterdogs$socialScale");
        wolfMixinSocialScaleField.setAccessible(true);
    }

    private static StubWolfMixin createStubWolfMixin(boolean clientSide, long randomSeed) throws Exception {
        StubWolfMixin stub = (StubWolfMixin) unsafe.allocateInstance(StubWolfMixin.class);
        StubServerLevel level = (StubServerLevel) unsafe.allocateInstance(StubServerLevel.class);
        level.init(clientSide);
        stub.init(level, RandomSource.create(randomSeed));
        return stub;
    }

    private static ScaleStubWolf createStubWolf(UUID wolfUuid, @Nullable UUID ownerUuid, boolean tame) throws Exception {
        ScaleStubWolf stub = (ScaleStubWolf) unsafe.allocateInstance(ScaleStubWolf.class);
        stub.setId(nextEntityId++);
        entityPositionField.set(stub, new Vec3(0.0, 64.0, 0.0));
        stub.setBoundingBox(new AABB(0.0, 64.0, 0.0, 1.0, 65.0, 1.0));
        StubServerLevel level = (StubServerLevel) unsafe.allocateInstance(StubServerLevel.class);
        level.init(false);
        stub.init(wolfUuid, ownerUuid, tame, level, RandomSource.create(wolfUuid.getMostSignificantBits()));
        return stub;
    }

    private static StubPlayer createStubPlayer(UUID playerUuid, ItemStack heldItem) throws Exception {
        StubPlayer player = (StubPlayer) unsafe.allocateInstance(StubPlayer.class);
        player.init(playerUuid, heldItem);
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

    @Test
    @DisplayName("WolfMixin socialScale starts at 0.0f, guards client lazy roll, and persists server roll + Attributes.SCALE")
    void testWolfMixinSocialScaleLifecycleAndClientGuard() throws Exception {
        StubWolfMixin clientMixin = createStubWolfMixin(true, 101L);
        assertEquals(0.0f, wolfMixinSocialScaleField.getFloat(clientMixin), 1e-6f,
                "Fresh Unsafe-allocated WolfMixin must start with betterdogs$socialScale == 0.0f");
        assertEquals(1.0f, clientMixin.betterdogs$getSocialScale(), 1e-6f,
                "Client-side uninitialized wolf must return safe 1.0f without rolling random genetics");
        assertEquals(0.0f, wolfMixinSocialScaleField.getFloat(clientMixin), 1e-6f,
                "Client-side getter must not mutate uninitialized betterdogs$socialScale");
        assertEquals(1.0d, clientMixin.getAttribute(Attributes.SCALE).getBaseValue(), 1e-6d,
                "Client-side getter must leave Attributes.SCALE untouched");

        StubWolfMixin serverMixin = null;
        for (long seed = 1L; seed < 100L; seed++) {
            StubWolfMixin candidate = createStubWolfMixin(false, seed);
            float rolled = candidate.betterdogs$getSocialScale();
            if (Math.abs(rolled - 1.0f) >= 0.04f) {
                serverMixin = candidate;
                break;
            }
        }
        assertNotNull(serverMixin, "Server-side WolfMixin must roll a non-1.0f wild scale within deterministic seeds");
        float persistedScale = wolfMixinSocialScaleField.getFloat(serverMixin);
        assertTrue(persistedScale >= 0.70f && persistedScale <= 1.45f,
                "Server-side lazy roll must persist bounded wild scale: " + persistedScale);
        assertEquals(persistedScale, serverMixin.betterdogs$getSocialScale(), 1e-6f,
                "Subsequent getter calls must return persisted scale without re-rolling");
        assertEquals(persistedScale, (float) serverMixin.getAttribute(Attributes.SCALE).getBaseValue(), 1e-6f,
                "Server-side lazy roll must sync Attributes.SCALE base value");
    }

    @Test
    @DisplayName("Wild spawn persists non-1.0f scale, voice pitch modulation, and /betterdogs inspect output")
    void testWildSpawnPersistsScalePitchAndInspect() throws Exception {
        ScaleStubWolf wildWolf = createStubWolf(UUID.randomUUID(), null, false);
        wildWolf.betterdogs$setPersonality(WolfPersonality.NORMAL);

        RandomSource random = RandomSource.create(12345L);
        float wildScale = WolfScaleGeneticsHelper.generateWildWolfScale(wildWolf.level(), random);
        while (Math.abs(wildScale - 1.0f) < 0.08f) {
            wildScale = WolfScaleGeneticsHelper.generateWildWolfScale(wildWolf.level(), random);
        }

        wildWolf.betterdogs$setSocialScale(wildScale);
        WolfPersonalityStatHelper.applyPersonalityStats(wildWolf, WolfPersonality.NORMAL);

        assertEquals(wildScale, wildWolf.betterdogs$getSocialScale(), 1e-5f);
        assertEquals(wildScale, (float) wildWolf.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f);

        float expectedPitch = CanineGeneticsHelper.calculateVoicePitch(1.0f, wildScale);
        float actualPitch = CanineGeneticsHelper.getModulatedVoicePitch(wildWolf, 1.0f);
        assertEquals(expectedPitch, actualPitch, 1e-4f, "Voice pitch must match persisted scale");
        assertNotEquals(1.0f, actualPitch, 0.01f, "Voice pitch must not collapse to 1.000f");

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
    @DisplayName("Breeding, litter, inbred runt penalty, and Golden Apple cure persist updated scale and pitch")
    void testBreedInbredRuntAndCurePersistUpdatedScale() throws Exception {
        UUID ownerUuid = UUID.randomUUID();
        StubPlayer owner = createStubPlayer(ownerUuid, new ItemStack(Items.GOLDEN_APPLE));

        ScaleStubWolf parent1 = createStubWolf(UUID.randomUUID(), ownerUuid, true);
        ScaleStubWolf parent2 = createStubWolf(UUID.randomUUID(), ownerUuid, true);
        parent1.betterdogs$setSocialScale(1.28f);
        parent2.betterdogs$setSocialScale(1.26f);

        ScaleStubWolf baby = createStubWolf(UUID.randomUUID(), ownerUuid, true);
        float babyScale = WolfScaleGeneticsHelper.calculateOffspringScale(
                baby.level(), parent1.betterdogs$getSocialScale(), parent2.betterdogs$getSocialScale(), RandomSource.create(77L));
        assertTrue(babyScale > 1.05f, "Offspring of large parents must be > 1.05f, got " + babyScale);
        baby.betterdogs$setPersonality(WolfPersonality.NORMAL);
        baby.betterdogs$setSocialScale(babyScale);

        int litterSize = WolfLitterHelper.determineLitterSize(baby.level(), RandomSource.create(99L));
        assertTrue(litterSize >= 1 && litterSize <= 4);
        assertEquals(babyScale, baby.betterdogs$getSocialScale(), 1e-5f);
        assertEquals(babyScale, (float) baby.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f);

        UUID sharedGrandparent = UUID.randomUUID();
        parent1.betterdogs$setParentUUID1(sharedGrandparent);
        parent2.betterdogs$setParentUUID1(sharedGrandparent);
        baby.betterdogs$setSocialScale(0.90f);
        WolfInbreedingHelper.applyInbreeding(baby, parent1, parent2);

        assertTrue(baby.betterdogs$isInbred(), "Shared grandparent pair must flag baby as inbred");
        float runtScale = baby.betterdogs$getSocialScale();
        assertTrue(runtScale >= 0.70f && runtScale <= 0.80f,
                "Inbred runt scale must clamp into [0.70f, 0.80f], got " + runtScale);
        assertEquals(runtScale, (float) baby.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f,
                "Inbred runt scale must update Attributes.SCALE");

        InteractionResult cureResult = WolfCureHelper.tryCureInbredWolf(baby, owner, InteractionHand.MAIN_HAND);
        assertTrue(cureResult.consumesAction(), "Golden Apple cure must succeed on server");
        assertFalse(baby.betterdogs$isInbred(), "Cure must clear inbred flag");
        float curedScale = baby.betterdogs$getSocialScale();
        assertTrue(curedScale >= 1.0f, "Cured runt must restore scale >= 1.0f, got " + curedScale);
        assertEquals(curedScale, (float) baby.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f,
                "Cure must sync Attributes.SCALE to cured scale");
        assertEquals(CanineGeneticsHelper.calculateVoicePitch(1.0f, curedScale),
                CanineGeneticsHelper.getModulatedVoicePitch(baby, 1.0f), 1e-4f);

        StubPlayer owner2 = createStubPlayer(ownerUuid, new ItemStack(Items.GOLDEN_APPLE));
        ScaleStubWolf giantRunt = createStubWolf(UUID.randomUUID(), ownerUuid, true);
        giantRunt.betterdogs$setPersonality(WolfPersonality.AGGRESSIVE);
        giantRunt.betterdogs$setSocialScale(1.15f);
        giantRunt.betterdogs$setInbred(true);
        InteractionResult giantCure = WolfCureHelper.tryCureInbredWolf(giantRunt, owner2, InteractionHand.MAIN_HAND);
        assertTrue(giantCure.consumesAction());
        assertEquals(1.15f, giantRunt.betterdogs$getSocialScale(), 1e-5f);
        assertEquals(1.15f, (float) giantRunt.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f);
    }

    @Test
    @DisplayName("CompoundTag save/reload round-trip preserves non-1.0f scale and missing-key 1.0f")
    void testSaveReloadRoundTripAndLegacyDefaultPreservation() throws Exception {
        CompoundTag savedTag = new CompoundTag();
        WolfPersistentData.writeToNbt(savedTag, WolfPersonality.AGGRESSIVE, 1.33f, 987654321L);
        float loadedScale = WolfPersistentData.readSocialScaleFromNbt(savedTag);
        assertEquals(1.33f, loadedScale, 1e-5f, "NBT round-trip must preserve 1.33f scale");

        ScaleStubWolf reloaded = createStubWolf(UUID.randomUUID(), null, false);
        reloaded.betterdogs$setSocialScale(loadedScale);
        WolfPersonalityStatHelper.applyPersonalityStats(reloaded, WolfPersonality.AGGRESSIVE);
        assertEquals(1.33f, (float) reloaded.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f);
        assertEquals(CanineGeneticsHelper.calculateVoicePitch(1.0f, 1.33f),
                CanineGeneticsHelper.getModulatedVoicePitch(reloaded, 1.0f), 1e-4f);

        CompoundTag legacyEmptyTag = new CompoundTag();
        legacyEmptyTag.putString(WolfPersistentData.NBT_KEY_PERSONALITY, WolfPersonality.NORMAL.getId());
        assertEquals(1.0f, WolfPersistentData.readSocialScaleFromNbt(legacyEmptyTag), 1e-6f);

        CompoundTag defaultTag = new CompoundTag();
        WolfPersistentData.writeToNbt(defaultTag, WolfPersonality.NORMAL, 1.0f, 111L);
        assertEquals(1.0f, WolfPersistentData.readSocialScaleFromNbt(defaultTag), 1e-6f);
    }

    @Test
    @DisplayName("Negative Guards: NaN, -1.0f, +Infinity, -Infinity, and 0.0f sanitize to 1.0f")
    void testNegativeGuardsSanitizeCorruptScales() throws Exception {
        float[] invalidFloats = {Float.NaN, -1.0f, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, 0.0f};
        StubWolfMixin stubMixin = createStubWolfMixin(false, 42L);

        for (float invalid : invalidFloats) {
            stubMixin.betterdogs$setSocialScale(1.25f);
            stubMixin.betterdogs$setSocialScale(invalid);
            assertEquals(1.0f, wolfMixinSocialScaleField.getFloat(stubMixin), 1e-6f,
                    "WolfMixin.betterdogs$setSocialScale must sanitize " + invalid + " to 1.0f");
            assertEquals(1.0f, (float) stubMixin.getAttribute(Attributes.SCALE).getBaseValue(), 1e-6f,
                    "WolfMixin Attributes.SCALE must sanitize " + invalid + " to 1.0f");

            CompoundTag corruptNbt = new CompoundTag();
            corruptNbt.putFloat(WolfPersistentData.NBT_KEY_SOCIAL_SCALE, invalid);
            assertEquals(1.0f, WolfPersistentData.readSocialScaleFromNbt(corruptNbt), 1e-6f,
                    "readSocialScaleFromNbt must sanitize " + invalid + " to 1.0f");

            CompoundTag sanitizedNbt = new CompoundTag();
            WolfPersistentData.writeToNbt(sanitizedNbt, WolfPersonality.NORMAL, invalid, 55L);
            assertEquals(1.0f, WolfPersistentData.readSocialScaleFromNbt(sanitizedNbt), 1e-6f,
                    "writeToNbt must sanitize " + invalid + " to 1.0f");
        }
    }

    @Test
    @DisplayName("Pillars 1 & 2: 200-run Chaos Fuzz and 16-Thread Concurrency preserve isolated per-wolf scales")
    void testChaosFuzzAndConcurrencyIsolation() throws Exception {
        RandomSource random = RandomSource.create(0xDA51C071L);
        for (int i = 0; i < 200; i++) {
            float wildScale = WolfScaleGeneticsHelper.generateWildWolfScale(null, random);
            if (Math.abs(wildScale - 1.0f) < 0.02f) {
                wildScale = (i % 2 == 0) ? 1.18f : 0.82f;
            }
            StubWolfMixin wolfMixin = createStubWolfMixin(false, 1000L + i);
            wolfMixin.betterdogs$setSocialScale(wildScale);
            assertEquals(wildScale, wolfMixin.betterdogs$getSocialScale(), 1e-5f,
                    "Fuzz iteration " + i + " lost persisted scale on WolfMixin");
            assertEquals(wildScale, (float) wolfMixin.getAttribute(Attributes.SCALE).getBaseValue(), 1e-5f,
                    "Fuzz iteration " + i + " lost Attributes.SCALE on WolfMixin");

            float pitch = CanineGeneticsHelper.calculateVoicePitch(1.0f, wolfMixin.betterdogs$getSocialScale());
            assertNotEquals(1.0f, pitch, 0.002f, "Fuzz iteration " + i + " collapsed pitch to 1.000f");
        }

        final int threads = 16;
        final int wolvesPerThread = 50;
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger failures = new AtomicInteger(0);

        List<List<StubWolfMixin>> threadWolves = new ArrayList<>(threads);
        for (int t = 0; t < threads; t++) {
            List<StubWolfMixin> list = new ArrayList<>(wolvesPerThread);
            for (int w = 0; w < wolvesPerThread; w++) {
                list.add(createStubWolfMixin(false, 50_000L + t * 100L + w));
            }
            threadWolves.add(list);
        }

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int t = 0; t < threads; t++) {
                final int tid = t;
                final List<StubWolfMixin> myWolves = threadWolves.get(t);
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        start.await();
                        for (int w = 0; w < wolvesPerThread; w++) {
                            StubWolfMixin wolfMixin = myWolves.get(w);
                            float targetScale = 0.72f + ((tid * 3 + w) % 60) * 0.011f;
                            wolfMixin.betterdogs$setSocialScale(targetScale);
                            float stored = wolfMixin.betterdogs$getSocialScale();
                            float attr = (float) wolfMixin.getAttribute(Attributes.SCALE).getBaseValue();
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
    void testMixinAndPersistenceContractsAndSidedness() throws IOException {
        Path root = findMainSourceRoot();
        Path wolfMixin = root.resolve("mixin/WolfMixin.java");
        Path persistentData = root.resolve("WolfPersistentData.java");
        Path inbreedingHelper = root.resolve("util/WolfInbreedingHelper.java");
        Path cureHelper = root.resolve("util/WolfCureHelper.java");
        Path litterHelper = root.resolve("util/WolfLitterHelper.java");

        String mixinSrc = Files.readString(wolfMixin, StandardCharsets.UTF_8);
        String persistentSrc = Files.readString(persistentData, StandardCharsets.UTF_8);
        String inbreedingSrc = Files.readString(inbreedingHelper, StandardCharsets.UTF_8);
        String cureSrc = Files.readString(cureHelper, StandardCharsets.UTF_8);
        String litterSrc = Files.readString(litterHelper, StandardCharsets.UTF_8);

        assertTrue(mixinSrc.contains("private float betterdogs$socialScale = 0.0f;"),
                "WolfMixin must initialize betterdogs$socialScale = 0.0f");
        assertTrue(mixinSrc.contains("isClientSide()"),
                "WolfMixin.betterdogs$getSocialScale must guard lazy roll against client-side");
        assertTrue(mixinSrc.contains("Float.isFinite(scale) && scale > 0.0f"),
                "WolfMixin.betterdogs$setSocialScale must sanitize invalid floats");
        int finalizeIdx = mixinSrc.indexOf("betterdogs$onFinalizeSpawn");
        int wildScaleIdx = mixinSrc.indexOf("WolfScaleGeneticsHelper", finalizeIdx);
        assertTrue(finalizeIdx > 0 && wildScaleIdx > finalizeIdx,
                "WolfMixin.betterdogs$onFinalizeSpawn must roll wildScale via WolfScaleGeneticsHelper");
        int applyScaleIdx = mixinSrc.indexOf("this.betterdogs$setSocialScale(wildScale);", wildScaleIdx);
        assertTrue(applyScaleIdx > wildScaleIdx,
                "WolfMixin.betterdogs$onFinalizeSpawn must persist wildScale via this.betterdogs$setSocialScale(wildScale)");

        assertTrue(persistentSrc.contains("(Float.isFinite(raw) && raw > 0.0f) ? raw : 1.0f"),
                "WolfPersistentData.readSocialScaleFromNbt must sanitize invalid floats");

        assertTrue(inbreedingSrc.contains("betterdogs$setSocialScale("),
                "WolfInbreedingHelper must persist runt scale via betterdogs$setSocialScale");
        assertTrue(cureSrc.contains("betterdogs$setSocialScale("),
                "WolfCureHelper must persist cured scale via betterdogs$setSocialScale");
        assertTrue(litterSrc.contains("betterdogs$setSocialScale("),
                "WolfLitterHelper must persist sibling scale via betterdogs$setSocialScale");

        for (String src : List.of(mixinSrc, persistentSrc, inbreedingSrc, cureSrc, litterSrc)) {
            assertTrue(src.contains("GNU GPLv3"), "Missing GNU GPLv3 header");
            assertFalse(src.contains("import net.minecraft.client."), "Server-side safety violation: client import");
            assertFalse(src.contains("import com.mojang.blaze3d."), "Server-side safety violation: blaze3d import");
        }
    }

    private static final class StubWolfMixin extends WolfMixin {
        private Level level;
        private RandomSource random;
        private AttributeInstance scaleAttribute;

        private StubWolfMixin() {
            super(null, null);
        }

        void init(Level level, RandomSource random) {
            this.level = level;
            this.random = random;
            this.scaleAttribute = new AttributeInstance(Attributes.SCALE, instance -> {});
        }

        @Override
        public Level level() {
            return this.level;
        }

        @Override
        public RandomSource getRandom() {
            return this.random;
        }

        @Override
        public @Nullable AttributeInstance getAttribute(Holder<Attribute> attribute) {
            if (Attributes.SCALE.equals(attribute)) {
                return this.scaleAttribute;
            }
            return null;
        }

        @Override
        public void betterdogs$invokeSetCollarColor(DyeColor color) {
        }

        @Override
        public boolean isFood(ItemStack stack) {
            return false;
        }

        @Override
        public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
            return null;
        }

        @Override
        public Iterable<ItemStack> getArmorSlots() {
            return List.of();
        }

        @Override
        public ItemStack getItemBySlot(EquipmentSlot slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        }

        @Override
        public HumanoidArm getMainArm() {
            return HumanoidArm.RIGHT;
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {
        }

        @Override
        public void readAdditionalSaveData(CompoundTag tag) {
        }

        @Override
        public void addAdditionalSaveData(CompoundTag tag) {
        }
    }

    private static final class StubCommandSourceStack extends CommandSourceStack {
        private List<String> messages;

        private StubCommandSourceStack() {
            super(null, null, null, null, 0, null, null, null, null);
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
        private ItemStack heldItem;

        private StubPlayer() {
            super(null, null, 0.0f, null);
        }

        void init(UUID uuid, ItemStack heldItem) {
            this.uuid = uuid;
            this.abilities = new Abilities();
            this.heldItem = heldItem;
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
        public ItemStack getItemInHand(InteractionHand hand) {
            return this.heldItem != null ? this.heldItem : ItemStack.EMPTY;
        }

        @Override
        public boolean isSpectator() {
            return false;
        }

        @Override
        public boolean isCreative() {
            return false;
        }
    }

    private static final class StubServerLevel extends ServerLevel {
        private boolean clientSide;

        private StubServerLevel() {
            super(null, null, null, null, null, null, null, false, 0L, null, false, null);
        }

        void init(boolean clientSide) {
            this.clientSide = clientSide;
        }

        @Override
        public boolean isClientSide() {
            return this.clientSide;
        }

        @Override
        public void playSound(@Nullable Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch) {
        }

        @Override
        public <T extends ParticleOptions> int sendParticles(T particle, double x, double y, double z, int count, double dx, double dy, double dz, double speed) {
            return count;
        }
    }

    private static final class ScaleStubWolf extends Wolf implements WolfExtensions {
        private UUID uuid;
        private @Nullable UUID ownerUuid;
        private boolean tame;
        private Level level;
        private RandomSource random;
        private AttributeInstance scaleAttribute;
        private float health;
        private WolfPersonality personality;
        private float socialScale;
        private UUID parentUuid1;
        private UUID parentUuid2;
        private boolean inbred;

        private ScaleStubWolf() {
            super(null, null);
        }

        void init(UUID uuid, @Nullable UUID ownerUuid, boolean tame, Level level, RandomSource random) {
            this.uuid = uuid;
            this.ownerUuid = ownerUuid;
            this.tame = tame;
            this.level = level;
            this.random = random;
            this.scaleAttribute = new AttributeInstance(Attributes.SCALE, instance -> {});
            this.health = 20.0f;
            this.personality = WolfPersonality.NORMAL;
            this.socialScale = 0.0f;
            this.parentUuid1 = null;
            this.parentUuid2 = null;
            this.inbred = false;
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
        public RandomSource getRandom() {
            return this.random;
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
        public @Nullable UUID getOwnerUUID() {
            return this.ownerUuid;
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
        public WolfPersonality betterdogs$getPersonality() {
            return this.personality;
        }

        @Override
        public void betterdogs$setPersonality(WolfPersonality personality) {
            this.personality = personality != null ? personality : WolfPersonality.NORMAL;
        }

        @Override
        public boolean betterdogs$hasPersonality() {
            return this.personality != null;
        }

        @Override
        public float betterdogs$getSocialScale() {
            if (this.socialScale <= 0.0f) {
                if (this.level != null && this.level.isClientSide()) {
                    return 1.0f;
                }
                float wildScale = WolfScaleGeneticsHelper.generateWildWolfScale(this.level, this.random);
                this.betterdogs$setSocialScale(wildScale);
            }
            return this.socialScale;
        }

        @Override
        public void betterdogs$setSocialScale(float scale) {
            float safeScale = (Float.isFinite(scale) && scale > 0.0f) ? scale : 1.0f;
            this.socialScale = safeScale;
            if (this.scaleAttribute != null) {
                this.scaleAttribute.setBaseValue(safeScale);
            }
        }

        @Override
        public long betterdogs$getDnaSeed() {
            return WolfPersistentData.generateDnaSeed(this.uuid);
        }

        @Override
        public String betterdogs$getFavoriteTreat() {
            return "";
        }

        @Override
        public void betterdogs$setFavoriteTreat(String treat) {
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
        public void betterdogs$setCollarColor(DyeColor color) {
        }

        @Override
        public @Nullable UUID betterdogs$getParentUUID1() {
            return this.parentUuid1;
        }

        @Override
        public void betterdogs$setParentUUID1(@Nullable UUID uuid) {
            this.parentUuid1 = uuid;
        }

        @Override
        public @Nullable UUID betterdogs$getParentUUID2() {
            return this.parentUuid2;
        }

        @Override
        public void betterdogs$setParentUUID2(@Nullable UUID uuid) {
            this.parentUuid2 = uuid;
        }

        @Override
        public boolean betterdogs$isInbred() {
            return this.inbred;
        }

        @Override
        public void betterdogs$setInbred(boolean inbred) {
            this.inbred = inbred;
        }

        @Override
        public boolean betterdogs$isGuarding() {
            return false;
        }

        @Override
        public void betterdogs$setGuarding(boolean guarding) {
        }

        @Override
        public @Nullable BlockPos betterdogs$getGuardPos() {
            return null;
        }

        @Override
        public void betterdogs$setGuardPos(@Nullable BlockPos pos) {
        }

        @Override
        public boolean betterdogs$isUpForAdoption() {
            return false;
        }

        @Override
        public void betterdogs$setUpForAdoption(boolean adoption) {
        }

        @Override
        public long betterdogs$getLastGiftDay() {
            return -1L;
        }

        @Override
        public void betterdogs$setLastGiftDay(long day) {
        }

        @Override
        public int betterdogs$getZoomiesTicks() {
            return 0;
        }

        @Override
        public void betterdogs$setZoomiesTicks(int ticks) {
        }

        @Override
        public boolean betterdogs$hasFetchedStick() {
            return false;
        }

        @Override
        public void betterdogs$setHasFetchedStick(boolean fetched) {
        }

        @Override
        public int betterdogs$getCalmTicks() {
            return 0;
        }

        @Override
        public void betterdogs$setCalmTicks(int ticks) {
        }

        @Override
        public int betterdogs$getHowlingTicks() {
            return 0;
        }

        @Override
        public void betterdogs$setHowlingTicks(int ticks) {
        }

        @Override
        public int betterdogs$getFeedCount() {
            return 0;
        }

        @Override
        public void betterdogs$setFeedCount(int count) {
        }

        @Override
        public @Nullable LivingEntity betterdogs$getRetaliationTarget() {
            return null;
        }

        @Override
        public void betterdogs$setRetaliationTarget(@Nullable LivingEntity target, int ticks) {
        }

        @Override
        public int betterdogs$getRetaliationTicks() {
            return 0;
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
        public int betterdogs$getPlayFightCooldown() {
            return 0;
        }

        @Override
        public void betterdogs$setPlayFightCooldown(int ticks) {
        }

        @Override
        public @Nullable UUID betterdogs$getLeaderUUID() {
            return null;
        }

        @Override
        public void betterdogs$setLeaderUUID(@Nullable UUID uuid) {
        }

        @Override
        public boolean betterdogs$isPackLeader() {
            return false;
        }

        @Override
        public void betterdogs$setPackLeader(boolean isLeader) {
        }

        @Override
        public int betterdogs$getWanderlustTicks() {
            return 0;
        }

        @Override
        public void betterdogs$setWanderlustTicks(int ticks) {
        }
    }
}
