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
import java.util.UUID;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.util.BloodFeudHelper;
import net.vanillaoutsider.betterdogs.util.WolfAdoptionHelper;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

public class AdoptionSystemTest {

    private static Unsafe unsafe;
    private static Field entityPositionField;
    private static int nextEntityId = 2000;

    @BeforeAll
    static void setupMinecraftBootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        unsafe = (Unsafe) theUnsafe.get(null);
        entityPositionField = Entity.class.getDeclaredField("position");
        entityPositionField.setAccessible(true);
    }

    private static AdoptionStubWolf createStubWolf(UUID wolfUuid, @Nullable UUID ownerUuid, boolean tame, boolean adoptable) throws Exception {
        AdoptionStubWolf stub = (AdoptionStubWolf) unsafe.allocateInstance(AdoptionStubWolf.class);
        stub.setId(nextEntityId++);
        entityPositionField.set(stub, new Vec3(0.0, 64.0, 0.0));
        stub.setBoundingBox(new AABB(0.0, 64.0, 0.0, 1.0, 65.0, 1.0));
        StubServerLevel level = (StubServerLevel) unsafe.allocateInstance(StubServerLevel.class);
        stub.init(wolfUuid, ownerUuid, tame, adoptable, level);
        return stub;
    }

    private static StubServerPlayer createStubPlayer(UUID playerUuid, boolean sneaking) throws InstantiationException {
        StubServerPlayer player = (StubServerPlayer) unsafe.allocateInstance(StubServerPlayer.class);
        player.init(playerUuid, sneaking);
        return player;
    }

    private static Path findProjectRoot() {
        Path cursor = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (cursor != null) {
            if (Files.exists(cursor.resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfInteractionHelper.java"))) {
                return cursor;
            }
            cursor = cursor.getParent();
        }
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

    @Test
    @DisplayName("Verify WolfAdoptionHelper Null Safety and Predicates")
    public void testAdoptionNullSafety() {
        Assertions.assertFalse(WolfAdoptionHelper.canListForAdoption(null, null, null));
        Assertions.assertFalse(WolfAdoptionHelper.canCancelAdoption(null, null, null));
        Assertions.assertFalse(WolfAdoptionHelper.canAdopt(null, null, null));
        Assertions.assertDoesNotThrow(() -> WolfAdoptionHelper.tryHandleAdoption(null, null, null));
        Assertions.assertDoesNotThrow(() -> WolfAdoptionHelper.tickAdoptionAmbientParticles(null));
    }

    @Test
    @DisplayName("BL-BD-059: Sneak empty-hand adoption clears blood feud, leader, guard state, ordered sit, and serializes cleanly")
    public void testSneakAdoptionClearsBloodFeudAndSocialGuardState() throws Exception {
        UUID oldOwnerUuid = UUID.randomUUID();
        UUID newOwnerUuid = UUID.randomUUID();
        UUID nemesisUuid = UUID.randomUUID();
        UUID leaderUuid = UUID.randomUUID();

        AdoptionStubWolf wolf = createStubWolf(UUID.randomUUID(), oldOwnerUuid, true, true);
        wolf.setOrderedToSit(true);
        wolf.betterdogs$setBloodFeudTarget(nemesisUuid.toString());
        wolf.betterdogs$setLeaderUuid(leaderUuid);
        wolf.betterdogs$setGuardMode(true);
        wolf.betterdogs$setGuardPos(new BlockPos(12, 64, -8));

        Assertions.assertTrue(wolf.betterdogs$hasBloodFeud());
        Assertions.assertTrue(BloodFeudHelper.hasBloodFeud(wolf));
        Assertions.assertTrue(wolf.betterdogs$isGuardMode());
        Assertions.assertEquals(leaderUuid, wolf.betterdogs$getLeaderUuid());

        StubServerPlayer newOwner = createStubPlayer(newOwnerUuid, true);
        InteractionResult result = WolfAdoptionHelper.tryHandleAdoption(wolf, newOwner, ItemStack.EMPTY);

        Assertions.assertEquals(InteractionResult.SUCCESS, result);
        Assertions.assertTrue(wolf.isOwnedBy(newOwner), "New player must now own the adopted wolf");
        Assertions.assertFalse(wolf.isOrderedToSit(), "Adoption must clear ordered sit state");
        Assertions.assertFalse(wolf.betterdogs$isAdoptable(), "Adoption must clear adoptable flag");
        Assertions.assertEquals("", wolf.betterdogs$getBloodFeudTarget(), "Adoption must clear blood feud target to empty string");
        Assertions.assertFalse(wolf.betterdogs$hasBloodFeud(), "hasBloodFeud must be false after adoption");
        Assertions.assertFalse(BloodFeudHelper.hasBloodFeud(wolf), "BloodFeudHelper.hasBloodFeud must be false after adoption");
        Assertions.assertNull(wolf.betterdogs$getLeaderUuid(), "Adoption must reset old pack leader UUID");
        Assertions.assertFalse(wolf.betterdogs$isGuardMode(), "Adoption must reset guard mode");
        Assertions.assertNull(wolf.betterdogs$getGuardPos(), "Adoption must reset guard position");

        WolfPersistentData persisted = WolfPersistentData.getWolfData(wolf);
        DataResult<Tag> encoded = Assertions.assertDoesNotThrow(() -> WolfPersistentData.CODEC.encodeStart(NbtOps.INSTANCE, persisted));
        Assertions.assertTrue(encoded.result().isPresent(), "Adopted WolfPersistentData must encode via CODEC");

        DataResult<WolfPersistentData> decoded = WolfPersistentData.CODEC.parse(NbtOps.INSTANCE, encoded.result().get());
        Assertions.assertTrue(decoded.result().isPresent(), "Adopted WolfPersistentData must decode via CODEC");
        WolfPersistentData roundTripped = decoded.result().get();
        Assertions.assertEquals("", roundTripped.bloodFeudTarget());
        Assertions.assertFalse(roundTripped.adoptable());
        Assertions.assertTrue(roundTripped.leaderUuid().isEmpty());
        Assertions.assertFalse(roundTripped.guardMode());
        Assertions.assertTrue(roundTripped.guardPos().isEmpty());
    }

    @Test
    @DisplayName("BL-BD-059 Negative Parity: Non-adoptable wolf returns InteractionResult.PASS and retains existing state")
    public void testNonAdoptableWolfReturnsPassAndKeepsState() throws Exception {
        UUID oldOwnerUuid = UUID.randomUUID();
        UUID strangerUuid = UUID.randomUUID();
        UUID nemesisUuid = UUID.randomUUID();

        AdoptionStubWolf nonAdoptable = createStubWolf(UUID.randomUUID(), oldOwnerUuid, true, false);
        nonAdoptable.betterdogs$setBloodFeudTarget(nemesisUuid.toString());
        nonAdoptable.betterdogs$setGuardMode(true);

        StubServerPlayer stranger = createStubPlayer(strangerUuid, true);
        InteractionResult result = WolfAdoptionHelper.tryHandleAdoption(nonAdoptable, stranger, ItemStack.EMPTY);

        Assertions.assertEquals(InteractionResult.PASS, result, "Non-adoptable wolf must return InteractionResult.PASS");
        Assertions.assertEquals(oldOwnerUuid, nonAdoptable.ownerUuid, "Non-adoptable wolf must keep original owner");
        Assertions.assertEquals(nemesisUuid.toString(), nonAdoptable.betterdogs$getBloodFeudTarget(), "Non-adoptable wolf must retain blood feud");
        Assertions.assertTrue(nonAdoptable.betterdogs$isGuardMode(), "Non-adoptable wolf must retain guard mode");
    }

    @Test
    @DisplayName("BL-BD-059 Contract: WolfInteractionHelper uses BloodFeudHelper.clearBloodFeud(wolf) and never passes null to setter")
    public void testWolfInteractionHelperClearBloodFeudContract() throws IOException {
        Path helperPath = findProjectRoot().resolve("common/src/main/java/net/vanillaoutsider/betterdogs/util/WolfInteractionHelper.java");
        String source = Files.readString(helperPath, StandardCharsets.UTF_8);

        Assertions.assertTrue(source.contains("BloodFeudHelper.clearBloodFeud(wolf);"),
                "WolfInteractionHelper must clear blood feud via BloodFeudHelper.clearBloodFeud(wolf)");
        Assertions.assertFalse(source.contains("betterdogs$setBloodFeudTarget(null)"),
                "WolfInteractionHelper must never invoke betterdogs$setBloodFeudTarget(null)");
    }

    private static final class StubServerPlayer extends ServerPlayer {
        private UUID uuid;
        private boolean sneaking;

        private StubServerPlayer() {
            super(null, null, null, null);
        }

        void init(UUID uuid, boolean sneaking) {
            this.uuid = uuid;
            this.sneaking = sneaking;
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public boolean isShiftKeyDown() {
            return this.sneaking;
        }

        @Override
        public boolean isSecondaryUseActive() {
            return this.sneaking;
        }

        @Override
        public boolean isCrouching() {
            return this.sneaking;
        }

        @Override
        public void sendOverlayMessage(Component message) {
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

    private static final class AdoptionStubWolf extends Wolf implements WolfExtensions {
        private UUID uuid;
        private @Nullable UUID ownerUuid;
        private boolean tame;
        private boolean orderedToSit;
        private Level level;

        private AdoptionStubWolf() {
            super(null, null);
        }

        void init(UUID uuid, @Nullable UUID ownerUuid, boolean tame, boolean adoptable, Level level) {
            this.uuid = uuid;
            this.ownerUuid = ownerUuid;
            this.tame = tame;
            this.level = level;
            WolfPersistentData.setPersistedAdoptable(this, adoptable);
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
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
        public void tame(Player player) {
            this.tame = true;
            this.ownerUuid = player.getUUID();
        }

        @Override
        public @Nullable EntityReference<LivingEntity> getOwnerReference() {
            return this.ownerUuid != null ? EntityReference.of(this.ownerUuid) : null;
        }

        @Override
        public boolean isOwnedBy(LivingEntity entity) {
            return entity != null && this.ownerUuid != null && this.ownerUuid.equals(entity.getUUID());
        }

        @Override
        public boolean isOrderedToSit() {
            return this.orderedToSit;
        }

        @Override
        public void setOrderedToSit(boolean sit) {
            this.orderedToSit = sit;
        }

        @Override
        public boolean hasCustomName() {
            return false;
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
            return WolfPersistentData.isPersistedSubmissive(this);
        }

        @Override
        public void betterdogs$setSubmissive(boolean submissive) {
            WolfPersistentData.setPersistedSubmissive(this, submissive);
        }

        @Override
        public String betterdogs$getBloodFeudTarget() {
            String target = WolfPersistentData.getPersistedBloodFeudTarget(this);
            return target != null ? target : "";
        }

        @Override
        public void betterdogs$setBloodFeudTarget(String targetUuid) {
            WolfPersistentData.setPersistedBloodFeudTarget(this, targetUuid != null ? targetUuid : "");
        }

        @Override
        public boolean betterdogs$hasBloodFeud() {
            return WolfPersistentData.hasBloodFeud(this);
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
            return WolfPersistentData.getPersistedAffinity(this, targetUuid);
        }

        @Override
        public void betterdogs$adjustAffinity(String targetUuid, int delta) {
            WolfPersistentData.adjustPersistedAffinity(this, targetUuid, delta);
        }

        @Override
        public @Nullable UUID betterdogs$getLeaderUuid() {
            return WolfPersistentData.getPersistedLeaderUuid(this).orElse(null);
        }

        @Override
        public void betterdogs$setLeaderUuid(@Nullable UUID uuid) {
            WolfPersistentData.setPersistedLeaderUuid(this, uuid);
        }

        @Override
        public boolean betterdogs$isGuardMode() {
            return WolfPersistentData.isPersistedGuardMode(this);
        }

        @Override
        public void betterdogs$setGuardMode(boolean guardMode) {
            WolfPersistentData.setPersistedGuardMode(this, guardMode);
        }

        @Override
        public @Nullable BlockPos betterdogs$getGuardPos() {
            return WolfPersistentData.getPersistedGuardPos(this).orElse(null);
        }

        @Override
        public void betterdogs$setGuardPos(@Nullable BlockPos pos) {
            WolfPersistentData.setPersistedGuardPos(this, pos);
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
            return WolfPersistentData.isPersistedAdoptable(this);
        }

        @Override
        public void betterdogs$setAdoptable(boolean adoptable) {
            WolfPersistentData.setPersistedAdoptable(this, adoptable);
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
            return WolfPersistentData.getPersistedNemesisType(this);
        }

        @Override
        public void betterdogs$setNemesisEntityType(String type) {
        }

        @Override
        public long betterdogs$getNemesisExpiryTime() {
            return WolfPersistentData.getPersistedNemesisExpiry(this);
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
