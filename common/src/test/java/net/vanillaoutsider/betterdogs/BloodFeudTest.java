// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.item.DyeColor;
import net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

public class BloodFeudTest {

    private static final Unsafe UNSAFE = initUnsafe();

    @BeforeAll
    static void setupMinecraftBootstrap() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

    private static Unsafe initUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to initialize sun.misc.Unsafe for NullFeudStubWolf", ex);
        }
    }

    private static NullFeudStubWolf createStubWolf(UUID uuid, boolean hasFeud, @Nullable String feudTarget) {
        try {
            NullFeudStubWolf wolf = (NullFeudStubWolf) UNSAFE.allocateInstance(NullFeudStubWolf.class);
            wolf.init(uuid, hasFeud, feudTarget);
            return wolf;
        } catch (InstantiationException ex) {
            throw new IllegalStateException("Failed to allocate NullFeudStubWolf", ex);
        }
    }

    private static Path findCommonRoot() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (dir != null) {
            if (Files.exists(dir.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfFriendlyFireHelper.java"))) {
                return dir.resolve("common");
            }
            if (Files.exists(dir.resolve("src/main/java/net/vanillaoutsider/betterdogs/util/WolfFriendlyFireHelper.java"))) {
                return dir;
            }
            dir = dir.getParent();
        }
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

    @Test
    public void testBloodFeudNbtSerialization() {
        CompoundTag tag = new CompoundTag();
        UUID targetUuid = UUID.randomUUID();

        WolfPersistentData.writeToNbt(
            tag,
            WolfPersonality.AGGRESSIVE,
            1.0f,
            12345L,
            "minecraft:cooked_beef",
            0L,
            "",
            0L,
            null,
            null,
            false,
            false,
            null,
            false,
            -1L,
            0,
            targetUuid.toString()
        );

        String readUuid = WolfPersistentData.readBloodFeudTargetFromNbt(tag);
        Assertions.assertEquals(targetUuid.toString(), readUuid, "Blood feud target UUID should match after NBT read/write");
    }

    @Test
    public void testBloodFeudEscalationChance() {
        int feudChance = 5;
        int triggered = 0;
        java.util.Random rand = new java.util.Random(42L);

        for (int i = 0; i < 1000; i++) {
            if (rand.nextInt(100) < feudChance) {
                triggered++;
            }
        }

        Assertions.assertTrue(triggered > 20 && triggered < 80, "5% roll should trigger roughly 50 times in 1000 trials (got " + triggered + ")");
    }

    @Test
    public void testEmptyFeudValidation() {
        CompoundTag emptyTag = new CompoundTag();
        String target = WolfPersistentData.readBloodFeudTargetFromNbt(emptyTag);
        Assertions.assertTrue(target.isEmpty(), "Unset blood feud target should be empty string");
    }

    @Test
    @DisplayName("BL-BD-056: Null feud target with active hasBloodFeud returns false without NPE and performs zero state mutation")
    public void testNullFeudTargetWithActiveFlagDoesNotThrowOrMutate() {
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        NullFeudStubWolf source = createStubWolf(sourceId, true, null);
        NullFeudStubWolf target = createStubWolf(targetId, false, "");

        Assertions.assertFalse(WolfFriendlyFireHelper.matchesBloodFeudTarget((Wolf) source, target));
        Assertions.assertFalse(WolfFriendlyFireHelper.matchesBloodFeudTarget((WolfExtensions) source, target));
        Assertions.assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(source, target));

        // Anti-Goal #2: read-only helper must never mutate Blood Feud state
        Assertions.assertEquals(0, source.setBloodFeudCalls, "Matching helper must not call betterdogs$setBloodFeudTarget");
        Assertions.assertTrue(source.betterdogs$hasBloodFeud(), "Source Blood Feud flag must remain untouched");
    }

    @Test
    @DisplayName("BL-BD-056: Negative Parity for null/empty source, target, and UUID edge cases")
    public void testNegativeParityNullAndEmptyUuids() {
        UUID targetId = UUID.randomUUID();
        NullFeudStubWolf validTarget = createStubWolf(targetId, false, "");
        NullFeudStubWolf emptyUuidTarget = createStubWolf(null, false, "");
        NullFeudStubWolf emptyFeudSource = createStubWolf(UUID.randomUUID(), true, "");

        Assertions.assertFalse(WolfFriendlyFireHelper.matchesBloodFeudTarget((Wolf) null, (Wolf) null));
        Assertions.assertFalse(WolfFriendlyFireHelper.matchesBloodFeudTarget((WolfExtensions) null, (Wolf) null));
        Assertions.assertFalse(WolfFriendlyFireHelper.matchesBloodFeudTarget((Wolf) emptyFeudSource, (Wolf) null));
        Assertions.assertFalse(WolfFriendlyFireHelper.matchesBloodFeudTarget((Wolf) emptyFeudSource, validTarget));
        Assertions.assertFalse(WolfFriendlyFireHelper.matchesBloodFeudTarget(
                (Wolf) createStubWolf(UUID.randomUUID(), true, ""), emptyUuidTarget));
        Assertions.assertFalse(WolfFriendlyFireHelper.matchesBloodFeudTarget(
                (Wolf) createStubWolf(UUID.randomUUID(), true, targetId.toString()), emptyUuidTarget));
        Assertions.assertFalse(WolfFriendlyFireHelper.matchesBloodFeudTarget(
                (WolfExtensions) createStubWolf(UUID.randomUUID(), true, targetId.toString()), emptyUuidTarget));
    }

    @Test
    @DisplayName("BL-BD-056: Anti-Goal #1 matching non-empty UUID returns true, mismatch & inactive flag return false")
    public void testMatchingAndMismatchingUuids() {
        UUID targetId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        NullFeudStubWolf target = createStubWolf(targetId, false, "");
        NullFeudStubWolf matchingSource = createStubWolf(UUID.randomUUID(), true, targetId.toString());
        NullFeudStubWolf mismatchSource = createStubWolf(UUID.randomUUID(), true, otherId.toString());
        NullFeudStubWolf inactiveSource = createStubWolf(UUID.randomUUID(), false, targetId.toString());

        Assertions.assertTrue(WolfFriendlyFireHelper.matchesBloodFeudTarget((Wolf) matchingSource, target));
        Assertions.assertTrue(WolfFriendlyFireHelper.matchesBloodFeudTarget((WolfExtensions) matchingSource, target));
        Assertions.assertFalse(WolfFriendlyFireHelper.matchesBloodFeudTarget((Wolf) mismatchSource, target));
        Assertions.assertFalse(WolfFriendlyFireHelper.matchesBloodFeudTarget((Wolf) inactiveSource, target));
    }

    @Test
    @DisplayName("BL-BD-056: Source contract scan enforces null-safe Blood Feud matching across common/src/main/java")
    public void testBloodFeudNullSafetySourceContract() throws IOException {
        Path commonRoot = findCommonRoot();
        Path mainJava = commonRoot.resolve("src/main/java");

        try (Stream<Path> walk = Files.walk(mainJava)) {
            List<Path> javaFiles = walk.filter(p -> p.toString().endsWith(".java")).toList();
            Assertions.assertFalse(javaFiles.isEmpty(), "Expected Java source files under " + mainJava);
            for (Path file : javaFiles) {
                String content = Files.readString(file, StandardCharsets.UTF_8);
                Assertions.assertFalse(
                        content.contains("betterdogs$getBloodFeudTarget().equals("),
                        "Raw unguarded betterdogs$getBloodFeudTarget().equals( banned in " + file);
            }
        }

        String mobMixin = Files.readString(
                mainJava.resolve("net/vanillaoutsider/betterdogs/mixin/WolfMobMixin.java"),
                StandardCharsets.UTF_8);
        Assertions.assertTrue(
                mobMixin.contains("WolfFriendlyFireHelper.matchesBloodFeudTarget("),
                "WolfMobMixin must delegate Blood Feud matching to WolfFriendlyFireHelper.matchesBloodFeudTarget");

        String wolfMixin = Files.readString(
                mainJava.resolve("net/vanillaoutsider/betterdogs/mixin/WolfMixin.java"),
                StandardCharsets.UTF_8);
        Assertions.assertTrue(
                wolfMixin.contains("this.betterdogs$bloodFeudTarget != null ? this.betterdogs$bloodFeudTarget : \"\""),
                "WolfMixin.betterdogs$getBloodFeudTarget must null-guard this.betterdogs$bloodFeudTarget");
        Assertions.assertTrue(
                wolfMixin.contains("return this.betterdogs$bloodFeudTarget != null && !this.betterdogs$bloodFeudTarget.isEmpty();"),
                "WolfMixin.betterdogs$hasBloodFeud must null-guard this.betterdogs$bloodFeudTarget");
    }

    private static final class NullFeudStubWolf extends Wolf implements WolfExtensions {
        private @Nullable UUID uuid;
        private boolean hasFeud;
        private @Nullable String feudTarget;
        private int setBloodFeudCalls;

        private NullFeudStubWolf() {
            super(null, null);
        }

        void init(@Nullable UUID uuid, boolean hasFeud, @Nullable String feudTarget) {
            this.uuid = uuid;
            this.hasFeud = hasFeud;
            this.feudTarget = feudTarget;
            this.setBloodFeudCalls = 0;
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public String getStringUUID() {
            return this.uuid != null ? this.uuid.toString() : null;
        }

        @Override
        public WolfPersonality betterdogs$getPersonality() {
            return WolfPersonality.NORMAL;
        }

        @Override
        public void betterdogs$setPersonality(WolfPersonality personality) {
        }

        @Override
        public boolean betterdogs$hasPersonality() {
            return false;
        }

        @Override
        public float betterdogs$getSocialScale() {
            return 1.0f;
        }

        @Override
        public void betterdogs$setSocialScale(float scale) {
        }

        @Override
        public long betterdogs$getDnaSeed() {
            return 0L;
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
            return null;
        }

        @Override
        public void betterdogs$setParentUUID1(@Nullable UUID uuid) {
        }

        @Override
        public @Nullable UUID betterdogs$getParentUUID2() {
            return null;
        }

        @Override
        public void betterdogs$setParentUUID2(@Nullable UUID uuid) {
        }

        @Override
        public boolean betterdogs$isInbred() {
            return false;
        }

        @Override
        public void betterdogs$setInbred(boolean inbred) {
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
        public @Nullable Holder<WolfVariant> betterdogs$getVariant() {
            return null;
        }

        @Override
        public void betterdogs$setVariant(@Nullable Holder<WolfVariant> variant) {
        }

        @Override
        public @Nullable Holder<WolfSoundVariant> betterdogs$getSoundVariant() {
            return null;
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
        public @Nullable String betterdogs$getBloodFeudTarget() {
            return this.feudTarget;
        }

        @Override
        public void betterdogs$setBloodFeudTarget(@Nullable String targetUuid) {
            this.setBloodFeudCalls++;
            this.feudTarget = targetUuid;
        }

        @Override
        public boolean betterdogs$hasBloodFeud() {
            return this.hasFeud;
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
