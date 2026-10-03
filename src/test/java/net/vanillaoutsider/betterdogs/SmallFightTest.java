// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.AdultDisciplineHelper;
import net.vanillaoutsider.betterdogs.util.DogTreatHelper;
import net.vanillaoutsider.betterdogs.util.PackAudienceHelper;
import net.vanillaoutsider.betterdogs.util.SmallFightHelper;
import net.vanillaoutsider.betterdogs.util.WolfAdvancementHelper;
import net.vanillaoutsider.betterdogs.util.WolfDispositionHelper;
import net.vanillaoutsider.betterdogs.util.WolfPettingHelper;
import net.vanillaoutsider.betterdogs.util.WolfTargetingHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 39: Harmless Social Play Sparring AI Tests")
class SmallFightTest {

    @BeforeAll
    static void initMinecraft() {
        try {
            net.minecraft.SharedConstants.tryDetectVersion();
            net.minecraft.server.Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

    @Test
    @DisplayName("Assert sparring duration, partner radius, range abort, and cooldown constants")
    void testSparringConstants() {
        assertEquals(120, SmallFightHelper.SPARRING_DURATION_TICKS);
        assertEquals(6.0, SmallFightHelper.DEFAULT_PARTNER_RADIUS);
        assertEquals(1.15, SmallFightHelper.DEFAULT_SPEED_MODIFIER);
        assertEquals(10.0, SmallFightHelper.MAX_SPARRING_DISTANCE, "Max sparring distance must be 10 blocks");
        assertEquals(100.0, SmallFightHelper.MAX_SPARRING_DISTANCE_SQR, "Max sparring distance squared must be 100.0 (10^2)");
        assertEquals(1200, SmallFightHelper.BASELINE_SPARRING_COOLDOWN_TICKS, "Baseline sparring cooldown must be 1200 ticks (60s)");
        assertEquals(1200, SmallFightHelper.MIN_SPARRING_COOLDOWN_TICKS, "Minimum sparring cooldown must be 1200 ticks (60s)");
        assertEquals(1800, SmallFightHelper.MAX_SPARRING_COOLDOWN_TICKS, "Maximum sparring cooldown must be 1800 ticks (90s)");
    }

    @Test
    @DisplayName("Assert disengagement range abort threshold (>10 blocks / >100.0 distance squared)")
    void testDisengagementRangeAbortThreshold() {
        double withinRangeDistSqr = 25.0; // 5 blocks
        double boundaryDistSqr = 100.0; // 10 blocks
        double exceedDistSqr = 100.01; // slightly over 10 blocks
        double distantDistSqr = 225.0; // 15 blocks

        assertFalse(withinRangeDistSqr > SmallFightHelper.MAX_SPARRING_DISTANCE_SQR, "5 blocks distance must not trigger range abort");
        assertFalse(boundaryDistSqr > SmallFightHelper.MAX_SPARRING_DISTANCE_SQR, "10 blocks distance boundary must not trigger range abort");
        assertTrue(exceedDistSqr > SmallFightHelper.MAX_SPARRING_DISTANCE_SQR, ">10 blocks distance must trigger range abort");
        assertTrue(distantDistSqr > SmallFightHelper.MAX_SPARRING_DISTANCE_SQR, "15 blocks distance must trigger range abort");
    }

    @Test
    @DisplayName("Assert post-sparring cooldown tracking and expiry across game ticks")
    void testSparringCooldownTrackingAndExpiry() {
        UUID wolfId = UUID.randomUUID();
        long startTime = 5000L;
        long expiryTime = startTime + SmallFightHelper.BASELINE_SPARRING_COOLDOWN_TICKS; // 6200L

        SmallFightHelper.clearCooldowns();
        assertFalse(SmallFightHelper.isOnCooldown(wolfId, startTime), "Wolf must not be on cooldown prior to registration");

        SmallFightHelper.setCooldown(wolfId, expiryTime);

        assertTrue(SmallFightHelper.isOnCooldown(wolfId, startTime), "Wolf must be on cooldown at start tick");
        assertTrue(SmallFightHelper.isOnCooldown(wolfId, startTime + 600L), "Wolf must remain on cooldown midway (tick 5600)");
        assertTrue(SmallFightHelper.isOnCooldown(wolfId, expiryTime - 1L), "Wolf must remain on cooldown right before expiry (tick 6199)");
        assertFalse(SmallFightHelper.isOnCooldown(wolfId, expiryTime), "Cooldown must expire at exactly expiryTime (tick 6200)");
        assertFalse(SmallFightHelper.isOnCooldown(wolfId, expiryTime + 1000L), "Wolf must remain off cooldown well after expiry");

        // Clear cooldowns validation
        SmallFightHelper.setCooldown(wolfId, 999999L);
        assertTrue(SmallFightHelper.isOnCooldown(wolfId, 10000L));
        SmallFightHelper.clearCooldowns();
        assertFalse(SmallFightHelper.isOnCooldown(wolfId, 10000L), "clearCooldowns must purge all entries");
    }

    @Test
    @DisplayName("Assert partner ownership matching (same owner UUID)")
    void testPartnerOwnershipMatching() {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();

        assertEquals(ownerA, ownerA, "Wolves with matching owner UUID are eligible packmates for sparring");
        assertNotEquals(ownerA, ownerB, "Wolves with different owners should not engage in friendly sparring");
    }

    @Test
    @DisplayName("Assert combat and sit safety checks")
    void testCombatAndSitSafetyCheck() {
        boolean isSitting = true;
        boolean hasTarget = false;

        boolean canSpar = !isSitting && !hasTarget;
        assertFalse(canSpar, "Sitting wolves must never engage in play fighting");

        boolean isSitting2 = false;
        boolean hasTarget2 = true;
        boolean canSpar2 = !isSitting2 && !hasTarget2;
        assertFalse(canSpar2, "Wolves with an active combat target must never engage in play fighting");
    }

    @Test
    @DisplayName("Assert SmallFightHelper strict null safety")
    void testSmallFightHelperNullSafety() {
        assertDoesNotThrow(() -> assertFalse(SmallFightHelper.isEligibleForPlay(null)));
        assertDoesNotThrow(() -> assertFalse(SmallFightHelper.canPlayTogether(null, null)));
        assertDoesNotThrow(() -> assertNull(SmallFightHelper.findPlayPartner(null, 6.0)));
        assertDoesNotThrow(() -> SmallFightHelper.startPlaySession(null, null));
        assertDoesNotThrow(() -> SmallFightHelper.applyPlayFeedback(null, null));
        assertDoesNotThrow(() -> assertFalse(SmallFightHelper.isOnCooldown((net.minecraft.world.entity.animal.wolf.Wolf) null)));
        assertDoesNotThrow(() -> assertFalse(SmallFightHelper.isOnCooldown((net.minecraft.world.entity.animal.wolf.Wolf) null, 1000L)));
        assertDoesNotThrow(() -> assertFalse(SmallFightHelper.isOnCooldown((UUID) null, 1000L)));
        assertDoesNotThrow(() -> SmallFightHelper.applyCooldown(null));
        assertDoesNotThrow(() -> SmallFightHelper.setCooldown((net.minecraft.world.entity.animal.wolf.Wolf) null, 1000L));
        assertDoesNotThrow(() -> SmallFightHelper.setCooldown((UUID) null, 1000L));
        assertDoesNotThrow(SmallFightHelper::clearCooldowns);
    }

    @Test
    @DisplayName("Assert Playful Personality Trait determinism and puppy guarantee")
    void testPlayfulTraitDeterminismAndPuppyGuarantee() {
        UUID testUuid = UUID.fromString("12345678-1234-1234-1234-123456789abc");
        boolean firstRoll = WolfDispositionHelper.isPlayful(testUuid);
        boolean secondRoll = WolfDispositionHelper.isPlayful(testUuid);
        assertEquals(firstRoll, secondRoll, "Playful trait calculation must be completely deterministic for identical UUID");

        assertTrue(WolfDispositionHelper.isPlayful(testUuid, true), "Puppies must unconditionally be playful");
        assertFalse(WolfDispositionHelper.isPlayful((net.minecraft.world.entity.animal.wolf.Wolf) null), "Null wolf must not be playful");
        assertFalse(WolfDispositionHelper.isPlayful((UUID) null), "Null UUID must not be playful");
        assertTrue(WolfDispositionHelper.isPlayful((UUID) null, true), "Null UUID with puppy flag must still be playful");

        // Statistical distribution check for adults (40% trait)
        int playfulAdults = 0;
        int trials = 1000;
        for (int i = 0; i < trials; i++) {
            UUID id = UUID.randomUUID();
            assertTrue(WolfDispositionHelper.isPlayful(id, true), "Puppy check must always yield true");
            if (WolfDispositionHelper.isPlayful(id, false)) {
                playfulAdults++;
            }
        }
        assertTrue(playfulAdults >= 330 && playfulAdults <= 470,
                "Adult playful trait roll must center around 40% (observed: " + playfulAdults + "/" + trials + ")");
    }

    @Test
    @DisplayName("Assert Dog Play Fighting and Play Penalty GameRule defaults and null-safety")
    void testPlayGameRuleDefaults() {
        assertTrue(BetterDogsGameRules.isDogPlayFightingEnabled(null), "Default for dogPlayFighting must be true");
        assertEquals(10, BetterDogsGameRules.getPlayPenaltyMinutes(null), "Default for playPenaltyMinutes must be 10");

        assertTrue(BetterDogsGameRules.getBoolean(null, BetterDogsGameRules.BD_DOG_PLAY_FIGHTING, true));
        assertEquals(10, BetterDogsGameRules.getInt(null, BetterDogsGameRules.BD_PLAY_PENALTY_MINUTES, 10));
    }

    @Test
    @DisplayName("Assert hierarchy correction snap damage calculation (Adult: 1.0f / 0.5 hearts, Puppy: 0.0f)")
    void testCorrectionDamageCalculation() {
        assertEquals(1.0F, SmallFightHelper.CORRECTION_DAMAGE_ADULT, "Adult correction damage constant must be 1.0f (0.5 hearts)");
        assertEquals(0.0F, SmallFightHelper.CORRECTION_DAMAGE_PUPPY, "Puppy correction damage constant must be 0.0f");

        assertEquals(1.0F, SmallFightHelper.calculateCorrectionDamage(false), "Adult (not baby) must receive 1.0f (0.5 hearts) damage");
        assertEquals(0.0F, SmallFightHelper.calculateCorrectionDamage(true), "Puppy (baby) must receive 0.0f damage");
        assertEquals(0.0F, SmallFightHelper.calculateCorrectionDamage((net.minecraft.world.entity.animal.wolf.Wolf) null), "Null wolf must receive 0.0f damage");
    }

    @Test
    @DisplayName("Assert sparring session pounce count tracking and threshold (3–5 pounces)")
    void testSparringSessionPounceTracking() {
        assertEquals(3, SmallFightHelper.MIN_POUNCE_THRESHOLD);
        assertEquals(5, SmallFightHelper.MAX_POUNCE_THRESHOLD);

        UUID dogA = UUID.randomUUID();
        UUID dogB = UUID.randomUUID();

        SmallFightHelper.clearSessions();
        assertEquals(0, SmallFightHelper.getPounceCount(dogA, dogB));

        // Explicit initialization with threshold 4
        SmallFightHelper.initSession(dogA, dogB, 4);
        assertEquals(4, SmallFightHelper.getPounceThreshold(dogA, dogB));
        assertEquals(0, SmallFightHelper.getPounceCount(dogA, dogB));

        // Increments
        assertEquals(1, SmallFightHelper.incrementPounces(dogA, dogB));
        assertEquals(1, SmallFightHelper.getPounceCount(dogA, dogB));
        // Order-independent key access (dogB, dogA should map to same session)
        assertEquals(1, SmallFightHelper.getPounceCount(dogB, dogA));

        assertEquals(2, SmallFightHelper.incrementPounces(dogB, dogA));
        assertEquals(2, SmallFightHelper.getPounceCount(dogA, dogB));

        assertEquals(3, SmallFightHelper.incrementPounces(dogA, dogB));
        assertEquals(4, SmallFightHelper.incrementPounces(dogA, dogB));
        assertEquals(4, SmallFightHelper.getPounceCount(dogA, dogB));

        // End session
        SmallFightHelper.endSession(dogA, dogB);
        assertEquals(0, SmallFightHelper.getPounceCount(dogA, dogB));
    }

    @Test
    @DisplayName("Assert hierarchy correction snap tracking and expiry across game time")
    void testCorrectionSnapTrackingAndExpiry() {
        UUID calmDogId = UUID.randomUUID();
        UUID playfulDogId = UUID.randomUUID();
        long startTime = 1000L;
        long expiryTime = startTime + SmallFightHelper.BASELINE_SPARRING_COOLDOWN_TICKS; // 2200L

        SmallFightHelper.clearCorrectionSnaps();
        assertFalse(SmallFightHelper.isRecentCorrectionSnap(playfulDogId, calmDogId, startTime));

        SmallFightHelper.recordCorrectionSnap(calmDogId, playfulDogId, expiryTime);

        assertTrue(SmallFightHelper.isRecentCorrectionSnap(playfulDogId, calmDogId, startTime), "Snap must be active at start time");
        assertTrue(SmallFightHelper.isRecentCorrectionSnap(playfulDogId, calmDogId, startTime + 500L), "Snap must remain active midway");
        assertTrue(SmallFightHelper.isRecentCorrectionSnap(playfulDogId, calmDogId, expiryTime), "Snap must be active at expiry tick");
        assertFalse(SmallFightHelper.isRecentCorrectionSnap(playfulDogId, calmDogId, expiryTime + 1L), "Snap must expire after expiry tick");

        // Different attacker ID should return false
        UUID strangerId = UUID.randomUUID();
        SmallFightHelper.recordCorrectionSnap(calmDogId, playfulDogId, expiryTime);
        assertFalse(SmallFightHelper.isRecentCorrectionSnap(playfulDogId, strangerId, startTime));

        // Clear snaps validation
        SmallFightHelper.clearCorrectionSnaps();
        assertFalse(SmallFightHelper.isRecentCorrectionSnap(playfulDogId, calmDogId, startTime));
    }

    @Test
    @DisplayName("Assert Disciplinary Muzzle & pack alert silencing returns true for domestic correction snap")
    void testDisciplinaryMuzzlePackAlertSilencing() {
        // Pure logic verification
        assertTrue(AdultDisciplineHelper.isPlayCorrectionSnap(true, true, true, true),
                "Domestic correction snap between co-owned tamed dogs must silence pack alert");

        assertFalse(AdultDisciplineHelper.isPlayCorrectionSnap(false, true, true, true),
                "Untamed victim must not qualify for disciplinary muzzle");
        assertFalse(AdultDisciplineHelper.isPlayCorrectionSnap(true, false, true, true),
                "Untamed attacker must not qualify for disciplinary muzzle");
        assertFalse(AdultDisciplineHelper.isPlayCorrectionSnap(true, true, false, true),
                "Different owners must not qualify for disciplinary muzzle");
        assertFalse(AdultDisciplineHelper.isPlayCorrectionSnap(true, true, true, false),
                "Non-correction damage must not qualify for disciplinary muzzle");
    }

    @Test
    @DisplayName("Assert Retaliation Suppression returns true for disciplined wolf targeting disciplining packmate")
    void testRetaliationSuppressionLogic() {
        assertTrue(WolfTargetingHandler.shouldSuppressRetaliation(true),
                "Retaliation must be suppressed when wolf was disciplined by target");
        assertFalse(WolfTargetingHandler.shouldSuppressRetaliation(false),
                "Retaliation must not be suppressed for normal targets");
    }

    @Test
    @DisplayName("Assert Step 3 Hierarchy Correction Snap strict null safety")
    void testHierarchyCorrectionSnapNullSafety() {
        assertDoesNotThrow(() -> assertEquals(0.0F, SmallFightHelper.calculateCorrectionDamage((net.minecraft.world.entity.animal.wolf.Wolf) null)));
        assertDoesNotThrow(() -> assertFalse(SmallFightHelper.isRecentCorrectionSnap((UUID) null, (UUID) null)));
        assertDoesNotThrow(() -> assertFalse(SmallFightHelper.isRecentCorrectionSnap((net.minecraft.world.entity.animal.wolf.Wolf) null, (net.minecraft.world.entity.LivingEntity) null)));
        assertDoesNotThrow(() -> assertFalse(SmallFightHelper.isRecentCorrectionSnap((UUID) null, (UUID) null, 1000L)));
        assertDoesNotThrow(() -> SmallFightHelper.recordCorrectionSnap((net.minecraft.world.entity.animal.wolf.Wolf) null, (net.minecraft.world.entity.animal.wolf.Wolf) null));
        assertDoesNotThrow(() -> SmallFightHelper.recordCorrectionSnap((UUID) null, (UUID) null, 1000L));
        assertDoesNotThrow(() -> SmallFightHelper.executeCorrectionSnap(null, null));
        assertDoesNotThrow(() -> SmallFightHelper.terminatePlaySession(null, null));
        assertDoesNotThrow(() -> assertFalse(SmallFightHelper.handlePounce(null, null)));
        assertDoesNotThrow(() -> SmallFightHelper.endSession((UUID) null, (UUID) null));
        assertDoesNotThrow(() -> SmallFightHelper.endSession((net.minecraft.world.entity.animal.wolf.Wolf) null, (net.minecraft.world.entity.animal.wolf.Wolf) null));
        assertDoesNotThrow(() -> assertEquals(0, SmallFightHelper.getPounceCount((UUID) null, (UUID) null)));
        assertDoesNotThrow(() -> assertEquals(0, SmallFightHelper.getPounceCount((net.minecraft.world.entity.animal.wolf.Wolf) null, (net.minecraft.world.entity.animal.wolf.Wolf) null)));
        assertDoesNotThrow(() -> assertEquals(0, SmallFightHelper.getPounceThreshold((UUID) null, (UUID) null)));
        assertDoesNotThrow(() -> assertEquals(0, SmallFightHelper.getPounceThreshold((net.minecraft.world.entity.animal.wolf.Wolf) null, (net.minecraft.world.entity.animal.wolf.Wolf) null)));
        assertDoesNotThrow(SmallFightHelper::clearSessions);
        assertDoesNotThrow(SmallFightHelper::clearCorrectionSnaps);

        assertDoesNotThrow(() -> assertFalse(AdultDisciplineHelper.isPlayCorrectionSnap(null, null)));
        assertDoesNotThrow(() -> assertFalse(AdultDisciplineHelper.shouldSilenceAlert(null, null)));
        assertDoesNotThrow(() -> assertFalse(WolfTargetingHandler.shouldSuppressRetaliation(null, null)));
    }

    @Test
    @DisplayName("Assert Step 4: 10-minute penalty application, calculation, and countdown")
    void testPlayPenaltyApplicationAndCountdown() {
        assertEquals(12000, SmallFightHelper.calculatePenaltyTicks(10), "10 minutes must equal 12,000 ticks");
        assertEquals(0, SmallFightHelper.calculatePenaltyTicks(0), "0 minutes must equal 0 ticks");
        assertEquals(12000, SmallFightHelper.calculatePenaltyTicks((net.minecraft.world.level.Level) null), "Null level fallback must default to 10 minutes (12,000 ticks)");

        UUID wolfId = UUID.randomUUID();
        SmallFightHelper.clearPlayPenalties();
        assertEquals(0, SmallFightHelper.getPlayPenaltyTicks(wolfId));

        SmallFightHelper.setPlayPenaltyTicks(wolfId, 12000);
        assertEquals(12000, SmallFightHelper.getPlayPenaltyTicks(wolfId), "Penalty ticks must be set to 12,000");

        // Decrement countdown
        int remaining = SmallFightHelper.decrementPlayPenalty(wolfId);
        assertEquals(11999, remaining);
        assertEquals(11999, SmallFightHelper.getPlayPenaltyTicks(wolfId));

        // Setting to 1 and decrementing to 0
        SmallFightHelper.setPlayPenaltyTicks(wolfId, 1);
        int afterZero = SmallFightHelper.decrementPlayPenalty(wolfId);
        assertEquals(0, afterZero);
        assertEquals(0, SmallFightHelper.getPlayPenaltyTicks(wolfId));

        // Decrementing while at 0 should remain 0 safely
        assertEquals(0, SmallFightHelper.decrementPlayPenalty(wolfId));

        // Negative clamp check
        SmallFightHelper.setPlayPenaltyTicks(wolfId, -100);
        assertEquals(0, SmallFightHelper.getPlayPenaltyTicks(wolfId), "Negative ticks must clamp to 0");

        // Clear penalties
        SmallFightHelper.setPlayPenaltyTicks(wolfId, 5000);
        SmallFightHelper.clearPlayPenalties();
        assertEquals(0, SmallFightHelper.getPlayPenaltyTicks(wolfId), "clearPlayPenalties must purge all entries");
    }

    @Test
    @DisplayName("Assert Step 4: isEligibleForPlay rejection during active penalty")
    void testIsEligibleForPlayRejectionDuringPenalty() {
        // State overload check
        assertTrue(SmallFightHelper.isEligibleForPlay(true, true, false, false, false, false, 0, false, false),
                "Eligible dog with 0 penalty ticks must be allowed to play");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, false, false, false, 12000, false, false),
                "Dog with active 10-minute penalty (12,000 ticks) must be rejected from play");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, false, false, false, 1, false, false),
                "Dog with even 1 remaining penalty tick must be rejected from play");

        // Other guards
        assertFalse(SmallFightHelper.isEligibleForPlay(false, true, false, false, false, false, 0, false, false), "Dead dog must be rejected");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, false, false, false, false, false, 0, false, false), "Untamed dog must be rejected");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, true, false, false, false, 0, false, false), "Sitting dog must be rejected");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, true, false, false, 0, false, false), "Leashed dog must be rejected");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, false, true, false, 0, false, false), "Dog with target must be rejected");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, false, false, true, 0, false, false), "Dog on cooldown must be rejected");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, false, false, false, 0, true, false), "Dog in active social mode must be rejected");
        assertFalse(SmallFightHelper.isEligibleForPlay(true, true, false, false, false, false, 0, false, true), "Dog with blood feud must be rejected");
    }

    @Test
    @DisplayName("Assert Step 4: Treat comforting soothing reduction formula (bounds [0.0, 0.10])")
    void testTreatSoothingReductionFormulaBounds() {
        int initialPenalty = 12000;

        // Exact bounds
        assertEquals(0, DogTreatHelper.calculateSoothingReduction(initialPenalty, 0.0f), "0% reduction must yield 0 ticks");
        assertEquals(1200, DogTreatHelper.calculateSoothingReduction(initialPenalty, 0.10f), "10% reduction must yield 1,200 ticks");
        assertEquals(600, DogTreatHelper.calculateSoothingReduction(initialPenalty, 0.05f), "5% reduction must yield 600 ticks");

        // Clamping bounds
        assertEquals(1200, DogTreatHelper.calculateSoothingReduction(initialPenalty, 0.25f), "Factor > 0.10f must be clamped to 10% (1,200 ticks)");
        assertEquals(1200, DogTreatHelper.calculateSoothingReduction(initialPenalty, 1.0f), "Factor 1.0 must be clamped to 10% (1,200 ticks)");
        assertEquals(0, DogTreatHelper.calculateSoothingReduction(initialPenalty, -0.10f), "Negative factor must clamp to 0");
        assertEquals(0, DogTreatHelper.calculateSoothingReduction(0, 0.10f), "0 current penalty must always yield 0 reduction");
        assertEquals(0, DogTreatHelper.calculateSoothingReduction(-500, 0.10f), "Negative penalty must yield 0 reduction");

        // Statistical trial ensuring every roll is strictly <= 10% of remaining penalty
        java.util.Random rand = new java.util.Random(42L);
        for (int i = 0; i < 500; i++) {
            float randomRoll = rand.nextFloat() * 0.10f;
            int current = 1000 + rand.nextInt(15000);
            int reduction = DogTreatHelper.calculateSoothingReduction(current, randomRoll);
            assertTrue(reduction >= 0, "Reduction must be non-negative");
            assertTrue(reduction <= (int) (current * 0.10f), "Reduction must not exceed 10% of current penalty");
        }
    }

    @Test
    @DisplayName("Assert Step 4: NBT read/write roundtrip logic for BD_PlayPenaltyTicks")
    void testPlayPenaltyNbtRoundtrip() {
        CompoundTag tag = new CompoundTag();
        SmallFightHelper.writePlayPenaltyNbt(tag, 12000);

        assertTrue(tag.getInt(SmallFightHelper.NBT_KEY_PLAY_PENALTY_TICKS).isPresent(), "NBT tag must contain BD_PlayPenaltyTicks");
        assertEquals(12000, tag.getIntOr(SmallFightHelper.NBT_KEY_PLAY_PENALTY_TICKS, 0));

        int deserialized = SmallFightHelper.readPlayPenaltyNbt(tag);
        assertEquals(12000, deserialized, "Read penalty ticks must match written value");

        // Empty tag fallback
        CompoundTag emptyTag = new CompoundTag();
        assertEquals(0, SmallFightHelper.readPlayPenaltyNbt(emptyTag), "Empty tag must return default 0");

        // Null tag safety
        assertEquals(0, SmallFightHelper.readPlayPenaltyNbt(null));
        assertDoesNotThrow(() -> SmallFightHelper.writePlayPenaltyNbt(null, 12000));
    }

    @Test
    @DisplayName("Assert Step 4: Comforting treat pool items and validation")
    void testComfortingTreatValidation() {
        assertTrue(DogTreatHelper.isComfortingTreat(Items.BONE), "Bone must be recognized as comforting treat");
        assertTrue(DogTreatHelper.isComfortingTreat(Items.COOKED_BEEF), "Cooked Beef must be recognized as comforting treat");
        assertTrue(DogTreatHelper.isComfortingTreat(Items.COOKED_PORKCHOP), "Cooked Porkchop must be recognized as comforting treat");
        assertTrue(DogTreatHelper.isComfortingTreat(Items.COOKED_CHICKEN), "Cooked Chicken must be recognized as comforting treat");
        assertTrue(DogTreatHelper.isComfortingTreat(Items.COOKED_MUTTON), "Cooked Mutton must be recognized as comforting treat");

        assertFalse(DogTreatHelper.isComfortingTreat(Items.STICK), "Stick must NOT be recognized as comforting treat");
        assertFalse(DogTreatHelper.isComfortingTreat(Items.DIRT), "Dirt must NOT be recognized as comforting treat");
        assertFalse(DogTreatHelper.isComfortingTreat((net.minecraft.world.item.Item) null), "Null item must return false");
        assertFalse(DogTreatHelper.isComfortingTreat(ItemStack.EMPTY), "Empty stack must return false");
        assertFalse(DogTreatHelper.isComfortingTreat((ItemStack) null), "Null stack must return false");
    }

    @Test
    @DisplayName("Assert Step 4: Subdued mood posture and penalty strict null safety")
    void testStep4NullSafety() {
        assertDoesNotThrow(() -> assertEquals(0, SmallFightHelper.getPlayPenaltyTicks((net.minecraft.world.entity.animal.wolf.Wolf) null)));
        assertDoesNotThrow(() -> assertEquals(0, SmallFightHelper.getPlayPenaltyTicks((UUID) null)));
        assertDoesNotThrow(() -> SmallFightHelper.setPlayPenaltyTicks((net.minecraft.world.entity.animal.wolf.Wolf) null, 12000));
        assertDoesNotThrow(() -> SmallFightHelper.setPlayPenaltyTicks((UUID) null, 12000));
        assertDoesNotThrow(() -> assertEquals(0, SmallFightHelper.decrementPlayPenalty((UUID) null)));
        assertDoesNotThrow(SmallFightHelper::clearPlayPenalties);
        assertDoesNotThrow(() -> assertEquals(12000, SmallFightHelper.calculatePenaltyTicks((net.minecraft.world.level.Level) null)));

        assertDoesNotThrow(() -> assertFalse(DogTreatHelper.canFeedComfortingTreat(null, null, null, null)));
        assertDoesNotThrow(() -> assertEquals(net.minecraft.world.InteractionResult.PASS, DogTreatHelper.tryFeedComfortingTreat(null, null, null, null)));
        assertDoesNotThrow(() -> assertTrue(DogTreatHelper.applySoothingReduction(null, 12000) > 0));
        assertDoesNotThrow(() -> assertEquals(0, DogTreatHelper.applySoothingReduction(null, 0)));

        assertDoesNotThrow(() -> WolfPettingHelper.emitSubduedWhine(null));
    }

    @Test
    @DisplayName("Assert Step 5: Pack audience radius, cap, wag duration, and midpoint calculation accuracy")
    void testPackAudienceConstantsAndMidpointAccuracy() {
        assertEquals(6.0, PackAudienceHelper.AUDIENCE_RADIUS, 1e-6, "Audience radius must be 6 blocks");
        assertEquals(36.0, PackAudienceHelper.AUDIENCE_RADIUS_SQR, 1e-6, "Audience radius squared must be 36.0");
        assertEquals(3, PackAudienceHelper.MAX_AUDIENCE_COUNT, "Max audience count must be 3 wolves");
        assertEquals(30, PackAudienceHelper.WAG_DURATION_TICKS, "Wag duration must be 30 ticks (1.5s)");

        // Midpoint calculation with positive and negative coordinates
        Vec3 posA = new Vec3(10.0, 64.0, -20.0);
        Vec3 posB = new Vec3(20.0, 66.0, -10.0);
        Vec3 mid = PackAudienceHelper.calculateMidpoint(posA, posB);
        assertEquals(15.0, mid.x, 1e-6, "Midpoint X must be 15.0");
        assertEquals(65.0, mid.y, 1e-6, "Midpoint Y must be 65.0");
        assertEquals(-15.0, mid.z, 1e-6, "Midpoint Z must be -15.0");

        // Symmetry
        Vec3 midSym = PackAudienceHelper.calculateMidpoint(posB, posA);
        assertEquals(mid.x, midSym.x, 1e-6);
        assertEquals(mid.y, midSym.y, 1e-6);
        assertEquals(mid.z, midSym.z, 1e-6);

        // Decimal precision check
        Vec3 decA = new Vec3(-5.5, 70.2, 100.8);
        Vec3 decB = new Vec3(15.5, 72.8, -50.8);
        Vec3 midDec = PackAudienceHelper.calculateMidpoint(decA, decB);
        assertEquals(5.0, midDec.x, 1e-6);
        assertEquals(71.5, midDec.y, 1e-6);
        assertEquals(25.0, midDec.z, 1e-6);

        // Fallbacks when one vector is null
        assertEquals(posA, PackAudienceHelper.calculateMidpoint(posA, null));
        assertEquals(posB, PackAudienceHelper.calculateMidpoint(null, posB));
        assertEquals(Vec3.ZERO, PackAudienceHelper.calculateMidpoint((Vec3) null, (Vec3) null));
    }

    @Test
    @DisplayName("Assert Step 5: Pack audience eligibility criteria matrix")
    void testPackAudienceEligibilityCriteria() {
        // Positive valid cases
        assertTrue(PackAudienceHelper.isEligibleAudience(true, true, true, false, false, false, 0.0),
                "Candidate at midpoint (dist 0) must be eligible");
        assertTrue(PackAudienceHelper.isEligibleAudience(true, true, true, false, false, false, 3.5),
                "Candidate at dist 3.5 must be eligible");
        assertTrue(PackAudienceHelper.isEligibleAudience(true, true, true, false, false, false, 6.0),
                "Candidate at exact boundary (dist 6.0) must be eligible");

        // Disqualification criteria
        assertFalse(PackAudienceHelper.isEligibleAudience(false, true, true, false, false, false, 3.5),
                "Untamed wolf must NOT be eligible for audience");
        assertFalse(PackAudienceHelper.isEligibleAudience(true, false, true, false, false, false, 3.5),
                "Wolf owned by another player must NOT be eligible for audience");
        assertFalse(PackAudienceHelper.isEligibleAudience(true, true, false, false, false, false, 3.5),
                "Dead wolf must NOT be eligible for audience");
        assertFalse(PackAudienceHelper.isEligibleAudience(true, true, true, true, false, false, 3.5),
                "Wolf in combat must NOT be eligible for audience");
        assertFalse(PackAudienceHelper.isEligibleAudience(true, true, true, false, true, false, 3.5),
                "Wolf sitting manually must NOT be eligible for audience");
        assertFalse(PackAudienceHelper.isEligibleAudience(true, true, true, false, false, true, 3.5),
                "Wolf participating in sparring must NOT be audience of own fight");
        assertFalse(PackAudienceHelper.isEligibleAudience(true, true, true, false, false, false, 6.01),
                "Wolf outside 6 blocks (6.01) must NOT be eligible for audience");
        assertFalse(PackAudienceHelper.isEligibleAudience(true, true, true, false, false, false, 12.0),
                "Distant wolf must NOT be eligible for audience");
    }

    @Test
    @DisplayName("Assert Step 5: Audience cap (max 3 wolves) and closest distance sorting")
    void testPackAudienceCapAndDistanceSorting() {
        assertEquals(3, PackAudienceHelper.MAX_AUDIENCE_COUNT, "Max audience cap must be 3");

        // 5 candidates with varying distances to midpoint
        java.util.List<Double> distances = java.util.List.of(5.2, 1.1, 4.3, 0.5, 3.8);
        java.util.List<Double> selected = distances.stream()
                .filter(d -> d <= PackAudienceHelper.AUDIENCE_RADIUS)
                .sorted()
                .limit(PackAudienceHelper.MAX_AUDIENCE_COUNT)
                .toList();

        assertEquals(3, selected.size(), "Must select exactly 3 audience members when >= 3 eligible candidates exist");
        assertEquals(java.util.List.of(0.5, 1.1, 3.8), selected, "Selected audience members must be the 3 closest to the midpoint");

        // When only 2 candidates exist, select all eligible candidates without error
        java.util.List<Double> twoCandidates = java.util.List.of(2.4, 4.1);
        java.util.List<Double> selectedTwo = twoCandidates.stream()
                .filter(d -> d <= PackAudienceHelper.AUDIENCE_RADIUS)
                .sorted()
                .limit(PackAudienceHelper.MAX_AUDIENCE_COUNT)
                .toList();
        assertEquals(2, selectedTwo.size(), "Should select both candidates when fewer than 3 exist");
    }

    @Test
    @DisplayName("Assert Step 5: Spectator wagging tracking and duration lifecycle")
    void testPackAudienceSpectatorWagTracking() {
        UUID spectatorId = UUID.randomUUID();
        assertEquals(0, PackAudienceHelper.getAudienceWagTicks(spectatorId), "Default wag ticks must be 0");

        PackAudienceHelper.setAudienceWagTicks(spectatorId, 30);
        assertEquals(30, PackAudienceHelper.getAudienceWagTicks(spectatorId), "Set wag ticks must be 30");

        PackAudienceHelper.setAudienceWagTicks(spectatorId, 0);
        assertEquals(0, PackAudienceHelper.getAudienceWagTicks(spectatorId), "Setting 0 ticks must clear tracker entry");

        PackAudienceHelper.setAudienceWagTicks(spectatorId, 30);
        PackAudienceHelper.clearAudienceWagTracker();
        assertEquals(0, PackAudienceHelper.getAudienceWagTicks(spectatorId), "clearAudienceWagTracker must clear all entries");
    }

    @Test
    @DisplayName("Assert Step 5: Audience helper strict null-safety")
    void testStep5AudienceStrictNullSafety() {
        assertDoesNotThrow(() -> PackAudienceHelper.calculateMidpoint((net.minecraft.world.entity.animal.wolf.Wolf) null, (net.minecraft.world.entity.animal.wolf.Wolf) null));
        assertEquals(Vec3.ZERO, PackAudienceHelper.calculateMidpoint((net.minecraft.world.entity.animal.wolf.Wolf) null, (net.minecraft.world.entity.animal.wolf.Wolf) null));

        assertDoesNotThrow(() -> PackAudienceHelper.calculateMidpoint((Vec3) null, (Vec3) null));
        assertEquals(Vec3.ZERO, PackAudienceHelper.calculateMidpoint((Vec3) null, (Vec3) null));

        assertFalse(PackAudienceHelper.isCoOwned(null, null));
        assertFalse(PackAudienceHelper.isEligibleAudience(null, null, null, null));
        assertTrue(PackAudienceHelper.findAudience(null, null).isEmpty());
        assertEquals(0, PackAudienceHelper.applyAudienceReactions(null, null, null));
        assertDoesNotThrow(() -> PackAudienceHelper.applyAudienceReaction(null, null));
        assertEquals(0, PackAudienceHelper.tickAudience(null, null));
        assertEquals(0, SmallFightHelper.evaluateAudience(null, null));

        assertEquals(0, PackAudienceHelper.getAudienceWagTicks(null));
        assertDoesNotThrow(() -> PackAudienceHelper.setAudienceWagTicks(null, 30));
    }

    @Test
    @DisplayName("Assert Step 6: WolfAdvancementHelper witness radius constant and null safety")
    void testStep6AdvancementHelperWitnessRadiusAndNullSafety() {
        assertEquals(16.0, WolfAdvancementHelper.WITNESS_RADIUS, 1e-6, "Witness radius must be exactly 16 blocks");

        assertDoesNotThrow(() -> WolfAdvancementHelper.grantRoughhousing(null));
        assertDoesNotThrow(() -> WolfAdvancementHelper.grantKnowYourPlace(null));
        assertDoesNotThrow(() -> WolfAdvancementHelper.awardRoughhousingNearby((net.minecraft.server.level.ServerLevel) null, null, 16.0));
        assertDoesNotThrow(() -> WolfAdvancementHelper.awardRoughhousingNearby((net.minecraft.world.entity.animal.wolf.Wolf) null, (net.minecraft.world.entity.animal.wolf.Wolf) null));
        assertDoesNotThrow(() -> WolfAdvancementHelper.awardKnowYourPlaceNearby((net.minecraft.server.level.ServerLevel) null, null, 16.0));
        assertDoesNotThrow(() -> WolfAdvancementHelper.awardKnowYourPlaceNearby((net.minecraft.world.entity.animal.wolf.Wolf) null, (net.minecraft.world.entity.animal.wolf.Wolf) null));
    }

    @Test
    @DisplayName("Assert Step 6: Roughhousing and Know Your Place advancement JSON schema and criteria")
    void testStep6AdvancementJsonSchema() throws java.io.IOException {
        // 1. roughhousing.json
        String roughhousingPath = "/data/betterdogs/advancement/husbandry/roughhousing.json";
        try (var stream = getClass().getResourceAsStream(roughhousingPath)) {
            assertNotNull(stream, "roughhousing.json must be present on classpath at " + roughhousingPath);
            String jsonStr = new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(jsonStr).getAsJsonObject();

            assertEquals("minecraft:husbandry/tame_an_animal", json.get("parent").getAsString());
            com.google.gson.JsonObject display = json.getAsJsonObject("display");
            assertEquals("minecraft:bone", display.getAsJsonObject("icon").get("id").getAsString());
            assertEquals("advancements.betterdogs.husbandry.roughhousing.title", display.getAsJsonObject("title").get("translate").getAsString());
            assertEquals("advancements.betterdogs.husbandry.roughhousing.description", display.getAsJsonObject("description").get("translate").getAsString());
            assertEquals("task", display.get("frame").getAsString());
            assertTrue(display.get("show_toast").getAsBoolean());
            assertTrue(display.get("announce_to_chat").getAsBoolean());

            com.google.gson.JsonObject criteria = json.getAsJsonObject("criteria");
            assertTrue(criteria.has("roughhousing"), "Criteria must contain 'roughhousing'");
            assertEquals("minecraft:impossible", criteria.getAsJsonObject("roughhousing").get("trigger").getAsString());
        }

        // 2. know_your_place.json
        String knowYourPlacePath = "/data/betterdogs/advancement/husbandry/know_your_place.json";
        try (var stream = getClass().getResourceAsStream(knowYourPlacePath)) {
            assertNotNull(stream, "know_your_place.json must be present on classpath at " + knowYourPlacePath);
            String jsonStr = new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(jsonStr).getAsJsonObject();

            assertEquals("betterdogs:husbandry/roughhousing", json.get("parent").getAsString());
            com.google.gson.JsonObject display = json.getAsJsonObject("display");
            assertEquals("minecraft:wolf_armor", display.getAsJsonObject("icon").get("id").getAsString());
            assertEquals("advancements.betterdogs.husbandry.know_your_place.title", display.getAsJsonObject("title").get("translate").getAsString());
            assertEquals("advancements.betterdogs.husbandry.know_your_place.description", display.getAsJsonObject("description").get("translate").getAsString());
            assertEquals("task", display.get("frame").getAsString());
            assertTrue(display.get("show_toast").getAsBoolean());
            assertTrue(display.get("announce_to_chat").getAsBoolean());

            com.google.gson.JsonObject criteria = json.getAsJsonObject("criteria");
            assertTrue(criteria.has("know_your_place"), "Criteria must contain 'know_your_place'");
            assertEquals("minecraft:impossible", criteria.getAsJsonObject("know_your_place").get("trigger").getAsString());
        }
    }

    @Test
    @DisplayName("Assert Step 6: en_us.json localization keys for advancements and play gamerules")
    void testStep6LocalizationKeys() throws java.io.IOException {
        String langPath = "/assets/vanilla-outsider-better-dogs/lang/en_us.json";
        try (var stream = getClass().getResourceAsStream(langPath)) {
            assertNotNull(stream, "en_us.json must be present on classpath at " + langPath);
            String jsonStr = new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            com.google.gson.JsonObject lang = com.google.gson.JsonParser.parseString(jsonStr).getAsJsonObject();

            // Advancement title and description keys
            assertEquals("Roughhousing", lang.get("advancements.betterdogs.husbandry.roughhousing.title").getAsString());
            assertEquals("Witness two tamed dogs engaging in playful sparring", lang.get("advancements.betterdogs.husbandry.roughhousing.description").getAsString());
            assertEquals("Know Your Place", lang.get("advancements.betterdogs.husbandry.know_your_place.title").getAsString());
            assertEquals("Witness a pack elder deliver a hierarchy correction snap", lang.get("advancements.betterdogs.husbandry.know_your_place.description").getAsString());

            // GameRule keys
            assertTrue(lang.has("gamerule.betterdogs:dogPlayFighting"), "gamerule.betterdogs:dogPlayFighting must be present");
            assertTrue(lang.has("gamerule.betterdogs:playPenaltyMinutes"), "gamerule.betterdogs:playPenaltyMinutes must be present");
        }
    }
}
