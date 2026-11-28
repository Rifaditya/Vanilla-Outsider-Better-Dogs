// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import com.mojang.serialization.DataResult;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.util.BloodFeudHelper;
import net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper;
import net.vanillaoutsider.betterdogs.util.WolfTargetingHandler;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 38: Persistent Wolf Vendetta Blood Feuds Tests")
class BloodFeudTest {

    private static Unsafe unsafe;
    private static int nextEntityId = 1000;

    @BeforeAll
    static void setupMinecraftBootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        unsafe = (Unsafe) theUnsafe.get(null);
    }

    private static Wolf allocateTestWolf() throws InstantiationException {
        Wolf wolf = (Wolf) unsafe.allocateInstance(Wolf.class);
        wolf.setId(nextEntityId++);
        return wolf;
    }

    @Test
    @DisplayName("Assert Blood Feud target UUID serialization and deserialization via Codec")
    void testBloodFeudCodecSerialization() {
        UUID targetUuid = UUID.randomUUID();
        WolfPersistentData original = new WolfPersistentData(
                WolfPersonality.AGGRESSIVE.getId(),
                0,
                false,
                targetUuid.toString(),
                12345L,
                0L,
                1.0f,
                Map.of(),
                Optional.empty(),
                false,
                Optional.empty(),
                false,
                false,
                0,
                "",
                0L,
                false
        );

        DataResult<Tag> encoded = WolfPersistentData.CODEC.encodeStart(NbtOps.INSTANCE, original);
        assertTrue(encoded.result().isPresent(), "Codec encoding should succeed");

        Tag tag = encoded.result().get();
        DataResult<WolfPersistentData> decoded = WolfPersistentData.CODEC.parse(NbtOps.INSTANCE, tag);
        assertTrue(decoded.result().isPresent(), "Codec decoding should succeed");

        WolfPersistentData result = decoded.result().get();
        assertEquals(targetUuid.toString(), result.bloodFeudTarget(), "Blood feud target UUID should match after encode/decode");
    }

    @Test
    @DisplayName("BL-BD-059: Canonical constructor normalizes nullable fields and round-trips cleanly via Codec")
    void testCanonicalConstructorAndCodecNullNormalization() {
        WolfPersistentData normalized = new WolfPersistentData(
                WolfPersonality.PACIFIST.getId(),
                42,
                true,
                null,
                99L,
                123456L,
                1.15f,
                null,
                null,
                false,
                null,
                true,
                false,
                3,
                null,
                0L,
                true,
                null
        );

        assertEquals("", normalized.bloodFeudTarget(), "Null bloodFeudTarget must normalize to empty string");
        assertEquals(Map.of(), normalized.affinityMap(), "Null affinityMap must normalize to empty map");
        assertEquals(Optional.empty(), normalized.leaderUuid(), "Null leaderUuid must normalize to Optional.empty()");
        assertEquals(Optional.empty(), normalized.guardPos(), "Null guardPos must normalize to Optional.empty()");
        assertEquals("", normalized.nemesisType(), "Null nemesisType must normalize to empty string");
        assertEquals("", normalized.canineArchetype(), "Null canineArchetype must normalize to empty string");

        DataResult<Tag> encoded = WolfPersistentData.CODEC.encodeStart(NbtOps.INSTANCE, normalized);
        assertTrue(encoded.result().isPresent(), "Normalized WolfPersistentData must encode via CODEC without NPE");

        DataResult<WolfPersistentData> decoded = WolfPersistentData.CODEC.parse(NbtOps.INSTANCE, encoded.result().get());
        assertTrue(decoded.result().isPresent(), "Normalized WolfPersistentData must decode cleanly via CODEC");

        WolfPersistentData roundTripped = decoded.result().get();
        assertEquals("", roundTripped.bloodFeudTarget());
        assertEquals(Map.of(), roundTripped.affinityMap());
        assertEquals(Optional.empty(), roundTripped.leaderUuid());
        assertEquals(Optional.empty(), roundTripped.guardPos());
        assertEquals("", roundTripped.nemesisType());
        assertEquals("", roundTripped.canineArchetype());
        assertEquals(WolfPersonality.PACIFIST.getId(), roundTripped.personalityId());
        assertEquals(1.15f, roundTripped.scale());
        assertEquals(123456L, roundTripped.dna());
    }

    @Test
    @DisplayName("BL-BD-059: setPersistedBloodFeudTarget(wolf, null) prevents NPE, serializes safely, and preserves unrelated fields")
    void testPersistedBloodFeudNullSetterPreventsNpeAndSerializes() throws Exception {
        Wolf wolf = allocateTestWolf();
        WolfPersistentData initial = new WolfPersistentData(
                WolfPersonality.AGGRESSIVE.getId(),
                0,
                false,
                "",
                0L,
                987654321L,
                1.25f,
                Map.of(),
                Optional.empty(),
                false,
                Optional.empty(),
                false,
                false,
                0,
                "",
                0L,
                false,
                "guardian"
        );
        WolfPersistentData.setWolfData(wolf, initial);

        UUID targetUuid = UUID.randomUUID();
        WolfPersistentData.setPersistedBloodFeudTarget(wolf, targetUuid.toString());
        assertTrue(WolfPersistentData.hasBloodFeud(wolf), "Setting a valid feud UUID must mark wolf as having blood feud");
        assertEquals(targetUuid.toString(), WolfPersistentData.getPersistedBloodFeudTarget(wolf));

        assertDoesNotThrow(() -> WolfPersistentData.setPersistedBloodFeudTarget(wolf, null),
                "Clearing blood feud with null must never throw");
        assertEquals("", WolfPersistentData.getPersistedBloodFeudTarget(wolf),
                "Clearing blood feud with null must normalize persisted target to empty string");
        assertFalse(WolfPersistentData.hasBloodFeud(wolf),
                "hasBloodFeud(wolf) must return false without NPE after null clear");

        WolfPersistentData persisted = WolfPersistentData.getWolfData(wolf);
        DataResult<Tag> encoded = assertDoesNotThrow(() -> WolfPersistentData.CODEC.encodeStart(NbtOps.INSTANCE, persisted),
                "CODEC encoding after null blood feud clear must not throw");
        assertTrue(encoded.result().isPresent(), "CODEC encoding after null clear must produce NBT result");

        DataResult<WolfPersistentData> decoded = WolfPersistentData.CODEC.parse(NbtOps.INSTANCE, encoded.result().get());
        assertTrue(decoded.result().isPresent(), "CODEC decoding after null clear must succeed");
        assertEquals("", decoded.result().get().bloodFeudTarget());

        // Negative parity: unrelated fields remain intact
        assertEquals(WolfPersonality.AGGRESSIVE.getId(), persisted.personalityId(), "personalityId must remain intact");
        assertEquals(1.25f, persisted.scale(), "scale must remain intact");
        assertEquals(987654321L, persisted.dna(), "dna must remain intact");
    }

    @Test
    @DisplayName("Assert Blood Feud escalation probability roll math at 5% default")
    void testBloodFeudEscalationChance() {
        int feudChance = 5;
        int triggered = 0;
        Random rand = new Random(42L);

        for (int i = 0; i < 1000; i++) {
            if (rand.nextInt(100) < feudChance) {
                triggered++;
            }
        }

        assertTrue(triggered > 20 && triggered < 80,
                "5% roll should trigger roughly 50 times in 1000 trials (got " + triggered + ")");
    }

    @Test
    @DisplayName("Assert empty/unset Blood Feud target validation")
    void testEmptyFeudValidation() {
        WolfPersistentData defaultData = WolfPersistentData.DEFAULT;
        assertEquals("", defaultData.bloodFeudTarget(), "Unset blood feud target should be empty string");
    }

    @Test
    @DisplayName("Assert BloodFeudHelper strict null safety")
    void testBloodFeudHelperNullSafety() {
        assertDoesNotThrow(() -> assertFalse(BloodFeudHelper.hasBloodFeud(null)));
        assertDoesNotThrow(() -> assertEquals("", BloodFeudHelper.getBloodFeudTarget(null)));
        assertDoesNotThrow(() -> BloodFeudHelper.setBloodFeudTarget(null, null));
        assertDoesNotThrow(() -> BloodFeudHelper.clearBloodFeud(null));
        assertDoesNotThrow(() -> assertFalse(BloodFeudHelper.shouldTriggerBloodFeud(null, null)));
        assertDoesNotThrow(() -> assertNull(BloodFeudHelper.findNemesis(null, null, 20.0)));
        assertDoesNotThrow(() -> assertFalse(BloodFeudHelper.matchesBloodFeudTarget((Wolf) null, null)));
        assertDoesNotThrow(() -> assertFalse(BloodFeudHelper.matchesBloodFeudTarget((WolfExtensions) null, null)));
    }

    @Test
    @DisplayName("BL-BD-056: Null feud target with hasBloodFeud()==true fails safely without NPE or silent feud mutation")
    void testNullFeudTargetAgainstValidUuidFailsSafelyWithoutMutation() throws Exception {
        UUID ownerUuid = UUID.randomUUID();
        UUID wolfUuid = UUID.randomUUID();
        UUID targetUuid = UUID.randomUUID();

        NullFeudStubWolf wolf = createStubWolf(wolfUuid, ownerUuid, true, null);
        NullFeudStubWolf target = createStubWolf(targetUuid, ownerUuid, false, "");

        assertEquals("", assertDoesNotThrow(() -> BloodFeudHelper.getBloodFeudTarget(wolf)));
        assertFalse(assertDoesNotThrow(() -> BloodFeudHelper.matchesBloodFeudTarget((Wolf) wolf, target)));
        assertFalse(assertDoesNotThrow(() -> BloodFeudHelper.matchesBloodFeudTarget((WolfExtensions) wolf, target)));
        assertFalse(assertDoesNotThrow(() -> WolfFriendlyFireHelper.isBloodFeudPair(wolf, target)));
        assertEquals(Boolean.FALSE, assertDoesNotThrow(() -> WolfTargetingHandler.wantsToAttack(wolf, target, null)));

        // Anti-Goal #2: Zero silent state destruction
        assertEquals(0, wolf.getSetBloodFeudCalls(), "Read-only Blood Feud matching must not mutate feud state");
        assertTrue(wolf.betterdogs$hasBloodFeud(), "Active blood feud flag must remain intact after read checks");
    }

    @Test
    @DisplayName("BL-BD-056: Negative parity (null, null) and (\"\", \"\") never match as Blood Feud")
    void testNegativeParityNullAndEmptyTargetUuidsNeverMatch() throws Exception {
        UUID ownerUuid = UUID.randomUUID();

        // Case 1: (null, null)
        NullFeudStubWolf nullFeudWolf = createStubWolf(UUID.randomUUID(), ownerUuid, true, null);
        NullFeudStubWolf nullTargetUuidWolf = createStubWolf(UUID.randomUUID(), ownerUuid, false, "");
        nullTargetUuidWolf.setStringUuidOverride(null);

        assertFalse(assertDoesNotThrow(() -> BloodFeudHelper.matchesBloodFeudTarget((Wolf) nullFeudWolf, nullTargetUuidWolf)));
        assertFalse(assertDoesNotThrow(() -> BloodFeudHelper.matchesBloodFeudTarget((WolfExtensions) nullFeudWolf, nullTargetUuidWolf)));
        assertFalse(assertDoesNotThrow(() -> WolfFriendlyFireHelper.isBloodFeudPair(nullFeudWolf, nullTargetUuidWolf)));
        assertEquals(Boolean.FALSE, assertDoesNotThrow(() -> WolfTargetingHandler.wantsToAttack(nullFeudWolf, nullTargetUuidWolf, null)));
        assertEquals(0, nullFeudWolf.getSetBloodFeudCalls());
        assertTrue(nullFeudWolf.betterdogs$hasBloodFeud());

        // Case 2: ("", "")
        NullFeudStubWolf emptyFeudWolf = createStubWolf(UUID.randomUUID(), ownerUuid, true, "");
        NullFeudStubWolf emptyTargetUuidWolf = createStubWolf(UUID.randomUUID(), ownerUuid, false, "");
        emptyTargetUuidWolf.setStringUuidOverride("");

        assertFalse(assertDoesNotThrow(() -> BloodFeudHelper.matchesBloodFeudTarget((Wolf) emptyFeudWolf, emptyTargetUuidWolf)));
        assertFalse(assertDoesNotThrow(() -> BloodFeudHelper.matchesBloodFeudTarget((WolfExtensions) emptyFeudWolf, emptyTargetUuidWolf)));
        assertFalse(assertDoesNotThrow(() -> WolfFriendlyFireHelper.isBloodFeudPair(emptyFeudWolf, emptyTargetUuidWolf)));
        assertEquals(Boolean.FALSE, assertDoesNotThrow(() -> WolfTargetingHandler.wantsToAttack(emptyFeudWolf, emptyTargetUuidWolf, null)));
        assertEquals(0, emptyFeudWolf.getSetBloodFeudCalls());
        assertTrue(emptyFeudWolf.betterdogs$hasBloodFeud());
    }

    @Test
    @DisplayName("BL-BD-056: Anti-Goal #1 active Blood Feud parity for matching, mismatched, and inactive flags")
    void testActiveFeudMatchingMismatchedAndInactiveParity() throws Exception {
        UUID ownerUuid = UUID.randomUUID();
        UUID nemesisUuid = UUID.randomUUID();
        UUID bystanderUuid = UUID.randomUUID();

        NullFeudStubWolf nemesis = createStubWolf(nemesisUuid, ownerUuid, false, "");
        NullFeudStubWolf bystander = createStubWolf(bystanderUuid, ownerUuid, false, "");

        // Active matching feud
        NullFeudStubWolf activeWolf = createStubWolf(UUID.randomUUID(), ownerUuid, true, nemesisUuid.toString());
        assertEquals(nemesisUuid.toString(), BloodFeudHelper.getBloodFeudTarget(activeWolf));
        assertTrue(BloodFeudHelper.matchesBloodFeudTarget((Wolf) activeWolf, nemesis));
        assertTrue(BloodFeudHelper.matchesBloodFeudTarget((WolfExtensions) activeWolf, nemesis));
        assertTrue(WolfFriendlyFireHelper.isBloodFeudPair(activeWolf, nemesis));
        assertEquals(Boolean.TRUE, WolfTargetingHandler.wantsToAttack(activeWolf, nemesis, null));

        // Mismatched UUID
        assertFalse(BloodFeudHelper.matchesBloodFeudTarget((Wolf) activeWolf, bystander));
        assertFalse(BloodFeudHelper.matchesBloodFeudTarget((WolfExtensions) activeWolf, bystander));
        assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(activeWolf, bystander));
        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(activeWolf, bystander, null));

        // Inactive flag even when backing string is populated
        NullFeudStubWolf inactiveWolf = createStubWolf(UUID.randomUUID(), ownerUuid, false, nemesisUuid.toString());
        assertFalse(BloodFeudHelper.matchesBloodFeudTarget((Wolf) inactiveWolf, nemesis));
        assertFalse(BloodFeudHelper.matchesBloodFeudTarget((WolfExtensions) inactiveWolf, nemesis));
        assertFalse(WolfFriendlyFireHelper.isBloodFeudPair(inactiveWolf, nemesis));
        assertEquals(Boolean.FALSE, WolfTargetingHandler.wantsToAttack(inactiveWolf, nemesis, null));
    }

    @Test
    @DisplayName("BL-BD-056: Source-contract guard ensures zero raw betterdogs$getBloodFeudTarget().equals( calls remain")
    void testSourceContractNoRawBloodFeudEqualsCallsRemain() throws IOException {
        Path mainJava = resolveCommonMainJava();
        assertTrue(Files.isDirectory(mainJava), "Expected common/src/main/java directory under " + mainJava.toAbsolutePath());

        List<Path> javaFiles;
        try (Stream<Path> walk = Files.walk(mainJava)) {
            javaFiles = walk.filter(p -> p.toString().endsWith(".java")).toList();
        }
        assertFalse(javaFiles.isEmpty(), "Expected Java source files in common/src/main/java");

        for (Path file : javaFiles) {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            assertFalse(content.contains("betterdogs$getBloodFeudTarget().equals("),
                    "Raw unguarded betterdogs$getBloodFeudTarget().equals( found in " + file);
        }

        String wolfMobMixin = Files.readString(
                mainJava.resolve("net/vanillaoutsider/betterdogs/mixin/WolfMobMixin.java"),
                StandardCharsets.UTF_8);
        assertTrue(wolfMobMixin.contains("BloodFeudHelper.matchesBloodFeudTarget"),
                "WolfMobMixin must delegate blood feud matching to BloodFeudHelper.matchesBloodFeudTarget");

        String tamableAnimalMixin = Files.readString(
                mainJava.resolve("net/vanillaoutsider/betterdogs/mixin/TamableAnimalMixin.java"),
                StandardCharsets.UTF_8);
        assertTrue(tamableAnimalMixin.contains("BloodFeudHelper.matchesBloodFeudTarget"),
                "TamableAnimalMixin must delegate blood feud matching to BloodFeudHelper.matchesBloodFeudTarget");
    }

    private static Path resolveCommonMainJava() {
        Path cursor = Path.of("").toAbsolutePath();
        for (int i = 0; i < 4 && cursor != null; i++) {
            Path multiLoaderMain = cursor.resolve("common").resolve("src").resolve("main").resolve("java");
            if (Files.isDirectory(multiLoaderMain)) {
                return multiLoaderMain;
            }
            Path singleMain = cursor.resolve("src").resolve("main").resolve("java");
            if (Files.isDirectory(singleMain)) {
                return singleMain;
            }
            cursor = cursor.getParent();
        }
        return Path.of("common", "src", "main", "java");
    }

    private static NullFeudStubWolf createStubWolf(
            UUID uuid,
            @Nullable UUID ownerUuid,
            boolean hasBloodFeud,
            @Nullable String bloodFeudTarget) throws InstantiationException {
        NullFeudStubWolf stub = (NullFeudStubWolf) unsafe.allocateInstance(NullFeudStubWolf.class);
        stub.init(uuid, ownerUuid, hasBloodFeud, bloodFeudTarget);
        return stub;
    }

    private static final class NullFeudStubWolf extends Wolf implements WolfExtensions {
        private UUID uuid;
        private @Nullable String stringUuidOverride;
        private boolean useStringUuidOverride;
        private @Nullable UUID ownerUuid;
        private boolean hasBloodFeud;
        private @Nullable String bloodFeudTarget;
        private int setBloodFeudCalls;

        private NullFeudStubWolf() {
            super(null, null);
        }

        void init(UUID uuid, @Nullable UUID ownerUuid, boolean hasBloodFeud, @Nullable String bloodFeudTarget) {
            this.uuid = uuid;
            this.ownerUuid = ownerUuid;
            this.hasBloodFeud = hasBloodFeud;
            this.bloodFeudTarget = bloodFeudTarget;
            this.useStringUuidOverride = false;
            this.setBloodFeudCalls = 0;
        }

        void setStringUuidOverride(@Nullable String override) {
            this.useStringUuidOverride = true;
            this.stringUuidOverride = override;
        }

        int getSetBloodFeudCalls() {
            return this.setBloodFeudCalls;
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public String getStringUUID() {
            return this.useStringUuidOverride ? this.stringUuidOverride : this.uuid.toString();
        }

        @Override
        public boolean isTame() {
            return this.ownerUuid != null;
        }

        @Override
        public boolean isBaby() {
            return false;
        }

        @Override
        public int getAge() {
            return 0;
        }

        @Override
        public @Nullable EntityReference<LivingEntity> getOwnerReference() {
            return this.ownerUuid != null ? EntityReference.of(this.ownerUuid) : null;
        }

        @Override
        public @Nullable LivingEntity getOwner() {
            return null;
        }

        @Override
        public @Nullable Level level() {
            return null;
        }

        @Override public WolfPersonality betterdogs$getPersonality() { return WolfPersonality.NORMAL; }
        @Override public void betterdogs$setPersonality(WolfPersonality personality) {}
        @Override public boolean betterdogs$hasPersonality() { return true; }
        @Override public int betterdogs$getLastDamageTime() { return 0; }
        @Override public void betterdogs$setLastDamageTime(int time) {}
        @Override public boolean betterdogs$isSubmissive() { return false; }
        @Override public void betterdogs$setSubmissive(boolean submissive) {}
        @Override public String betterdogs$getBloodFeudTarget() { return this.bloodFeudTarget; }
        @Override public void betterdogs$setBloodFeudTarget(String targetUuid) {
            this.setBloodFeudCalls++;
            this.bloodFeudTarget = targetUuid;
            this.hasBloodFeud = targetUuid != null && !targetUuid.isEmpty();
        }
        @Override public boolean betterdogs$hasBloodFeud() { return this.hasBloodFeud; }
        @Override public long betterdogs$getLastMischiefDay() { return 0L; }
        @Override public void betterdogs$setLastMischiefDay(long day) {}
        @Override public boolean betterdogs$isBeingDisciplined() { return false; }
        @Override public void betterdogs$setBeingDisciplined(boolean isBeingDisciplined) {}
        @Override public void betterdogs$setSocialState(@Nullable LivingEntity target, SocialAction action, int maxDurationTicks) {}
        @Override public @Nullable LivingEntity betterdogs$getSocialTarget() { return null; }
        @Override public SocialAction betterdogs$getSocialAction() { return SocialAction.NONE; }
        @Override public boolean betterdogs$isSocialModeActive() { return false; }
        @Override public void betterdogs$tickSocialMode() {}
        @Override public long betterdogs$getDNA() { return 0L; }
        @Override public void betterdogs$setDNA(long dna) {}
        @Override public float betterdogs$getSocialScale() { return 1.0f; }
        @Override public void betterdogs$setSocialScale(float scale) {}
        @Override public @Nullable EntitySocialScheduler betterdogs$getScheduler() { return null; }
        @Override public EntitySocialScheduler betterdogs$getOrInitializeScheduler() { return null; }
        @Override public void betterdogs$tickScheduler() {}
        @Override public int betterdogs$getAffinity(String targetUuid) { return 0; }
        @Override public void betterdogs$adjustAffinity(String targetUuid, int delta) {}
        @Override public @Nullable UUID betterdogs$getLeaderUuid() { return null; }
        @Override public void betterdogs$setLeaderUuid(@Nullable UUID uuid) {}
        @Override public boolean betterdogs$isGuardMode() { return false; }
        @Override public void betterdogs$setGuardMode(boolean guardMode) {}
        @Override public @Nullable BlockPos betterdogs$getGuardPos() { return null; }
        @Override public void betterdogs$setGuardPos(@Nullable BlockPos pos) {}
        @Override public boolean betterdogs$isSittingManually() { return false; }
        @Override public void betterdogs$setSittingManually(boolean sitting) {}
        @Override public boolean betterdogs$isAdoptable() { return false; }
        @Override public void betterdogs$setAdoptable(boolean adoptable) {}
        @Override public float betterdogs$getHealthBonus() { return 0.0f; }
        @Override public void betterdogs$setHealthBonus(float hp) {}
        @Override public float betterdogs$getDamageMod() { return 0.0f; }
        @Override public void betterdogs$setDamageMod(float dmg) {}
        @Override public float betterdogs$getSpeedMod() { return 0.0f; }
        @Override public void betterdogs$setSpeedMod(float speed) {}
        @Override public boolean betterdogs$areStatsRolled() { return false; }
        @Override public void betterdogs$setStatsRolled(boolean rolled) {}
        @Override public int betterdogs$getPushWaitTimer() { return 0; }
        @Override public void betterdogs$setPushWaitTimer(int ticks) {}
        @Override public @Nullable BlockPos betterdogs$getPathfindAvoidPos() { return null; }
        @Override public void betterdogs$setPathfindAvoidPos(@Nullable BlockPos pos) {}
        @Override public GoalSelector betterdogs$getGoalSelector() { return null; }
        @Override public long betterdogs$getSoothedTime() { return 0L; }
        @Override public void betterdogs$setSoothedTime(long time) {}
        @Override public @Nullable BlockPos betterdogs$getSoundLocationTarget() { return null; }
        @Override public void betterdogs$setSoundLocationTarget(@Nullable BlockPos pos) {}
        @Override public int betterdogs$getPassiveOverrideTicks() { return 0; }
        @Override public void betterdogs$setPassiveOverrideTicks(int ticks) {}
        @Override public String betterdogs$getNemesisEntityType() { return ""; }
        @Override public void betterdogs$setNemesisEntityType(String type) {}
        @Override public long betterdogs$getNemesisExpiryTime() { return 0L; }
        @Override public void betterdogs$setNemesisExpiryTime(long time) {}
        @Override public boolean betterdogs$hasFetchedItem() { return false; }
        @Override public void betterdogs$setHasFetchedItem(boolean fetched) {}
        @Override public @Nullable ItemStack betterdogs$getFetchedItemStack() { return null; }
        @Override public void betterdogs$setFetchedItemStack(@Nullable ItemStack stack) {}
        @Override public int betterdogs$getZoomiesTicks() { return 0; }
        @Override public void betterdogs$setZoomiesTicks(int ticks) {}
        @Override public int betterdogs$getHowlingTicks() { return 0; }
        @Override public void betterdogs$setHowlingTicks(int ticks) {}
    }
}
