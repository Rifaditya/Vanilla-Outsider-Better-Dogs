// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.MoveToVehicleGoal;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;
import net.vanillaoutsider.betterdogs.util.DogSeatHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 30: Vehicle Navigation & Auto-Boarding Tests")
class VehicleBoardingTest {

    private static final Unsafe UNSAFE = initUnsafe();
    private static Field removalReasonField;
    private static Field positionField;

    private static Unsafe initUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Unsafe", e);
        }
    }

    @BeforeAll
    static void initMinecraft() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
            removalReasonField = Entity.class.getDeclaredField("removalReason");
            removalReasonField.setAccessible(true);
            positionField = Entity.class.getDeclaredField("position");
            positionField.setAccessible(true);
        } catch (Throwable ignored) {
        }
    }

    private static Interaction allocateStubVehicle() throws Exception {
        Interaction vehicle = (Interaction) UNSAFE.allocateInstance(Interaction.class);
        if (positionField == null) {
            positionField = Entity.class.getDeclaredField("position");
            positionField.setAccessible(true);
        }
        positionField.set(vehicle, Vec3.ZERO);
        return vehicle;
    }

    private static void markRemoved(Entity entity) throws Exception {
        if (removalReasonField == null) {
            removalReasonField = Entity.class.getDeclaredField("removalReason");
            removalReasonField.setAccessible(true);
        }
        removalReasonField.set(entity, Entity.RemovalReason.DISCARDED);
    }

    private static void assignTransientVehicle(UUID dogUuid) throws Exception {
        Interaction transientVehicle = allocateStubVehicle();
        DogCommandManager.setVehicleTarget(dogUuid, transientVehicle);
        assertSame(transientVehicle, DogCommandManager.getVehicleTarget(dogUuid));
    }

    @Test
    @DisplayName("Assert distance constants match design specifications")
    void testDistanceConstants() {
        assertEquals(144.0D, DogSeatHelper.MAX_COMMAND_DISTANCE_SQ, 0.001D, "Max command range must be 12 blocks (144 distance squared)");
        assertEquals(2.25D, DogSeatHelper.MOUNT_DISTANCE_SQ, 0.001D, "Mount distance must be 1.5 blocks (2.25 distance squared)");
        assertEquals(0.8D, DogSeatHelper.DEFAULT_DISMOUNT_DISTANCE, 0.001D, "Default dismount offset must be 0.8 blocks");
    }

    @Test
    @DisplayName("Assert lateral dismount vector calculation pushes outward with zero vertical displacement")
    void testDismountVectorCalculation() {
        Vec3 lookX = new Vec3(1.0D, 0.5D, 0.0D);
        Vec3 offset = DogSeatHelper.calculateDismountOffset(lookX, 0.8D);

        assertEquals(-0.8D, offset.x, 0.001D, "X offset should be opposite to player look angle scaled by distance");
        assertEquals(0.0D, offset.y, 0.001D, "Y offset must strictly remain 0 to prevent vertical flight or suffocation");
        assertEquals(0.0D, offset.z, 0.001D, "Z offset should remain 0");

        Vec3 lookZ = new Vec3(0.0D, -0.9D, 1.0D);
        Vec3 offsetZ = DogSeatHelper.calculateDismountOffset(lookZ, 0.8D);
        assertEquals(0.0D, offsetZ.x, 0.001D);
        assertEquals(0.0D, offsetZ.y, 0.001D);
        assertEquals(-0.8D, offsetZ.z, 0.001D);
    }

    @Test
    @DisplayName("Assert command manager selection and target tracking")
    void testSelectionTracking() {
        UUID playerUuid = UUID.randomUUID();
        UUID dogUuid = UUID.randomUUID();

        DogCommandManager.selectDog(playerUuid, dogUuid);
        assertEquals(dogUuid, DogCommandManager.getSelectedDog(playerUuid));

        DogCommandManager.clearSelection(playerUuid);
        assertNull(DogCommandManager.getSelectedDog(playerUuid));

        DogCommandManager.clearVehicleTarget(dogUuid);
        assertNull(DogCommandManager.getVehicleTarget(dogUuid));
    }

    @Test
    @DisplayName("Assert strict null safety across seat and command helper methods")
    void testNullSafety() {
        assertFalse(DogSeatHelper.isSitTarget(null));
        assertFalse(DogSeatHelper.hasPassengerSpace(null));
        assertFalse(DogSeatHelper.isCommandItem(null));
        assertFalse(DogSeatHelper.isChairBlock(null));
        assertEquals(Vec3.ZERO, DogSeatHelper.calculateDismountOffset(null, 0.8D));
        assertDoesNotThrow(() -> DogCommandManager.clearSelection(null));
        assertDoesNotThrow(() -> DogCommandManager.clearVehicleTarget(null));
    }

    @Test
    @DisplayName("BL-BD-060: Live non-removed vehicle target returns identically and clears cleanly")
    void testLiveVehicleTargetReturnsAndClears() throws Exception {
        UUID dogUuid = UUID.randomUUID();
        try {
            Interaction vehicle = allocateStubVehicle();
            DogCommandManager.setVehicleTarget(dogUuid, vehicle);
            assertSame(vehicle, DogCommandManager.getVehicleTarget(dogUuid));
            assertEquals(1, DogCommandManager.getTrackedVehicleTargetCount());

            DogCommandManager.clearVehicleTarget(dogUuid);
            assertNull(DogCommandManager.getVehicleTarget(dogUuid));
            assertEquals(0, DogCommandManager.getTrackedVehicleTargetCount());
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    @DisplayName("BL-BD-060: WeakReference vehicle target is reclaimed on GC and auto-pruned")
    void testWeakReferenceGcReclamation() throws Exception {
        UUID dogUuid = UUID.randomUUID();
        try {
            assignTransientVehicle(dogUuid);

            for (int attempt = 0; attempt < 30; attempt++) {
                System.gc();
                if (DogCommandManager.getVehicleTarget(dogUuid) == null) {
                    break;
                }
                Thread.sleep(20);
            }

            assertNull(DogCommandManager.getVehicleTarget(dogUuid),
                    "Unreferenced vehicle target should be reclaimed by GC and pruned");
            assertEquals(0, DogCommandManager.getTrackedVehicleTargetCount(),
                    "Pruned WeakReference entry must be removed from VEHICLE_TARGETS");
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    @DisplayName("BL-BD-060: Removed vehicle target auto-prunes on lookup and is rejected on set")
    void testRemovedVehiclePruningAndRejection() throws Exception {
        UUID dogUuid = UUID.randomUUID();
        try {
            Interaction removedVehicle = allocateStubVehicle();
            DogCommandManager.setVehicleTarget(dogUuid, removedVehicle);
            assertSame(removedVehicle, DogCommandManager.getVehicleTarget(dogUuid));

            markRemoved(removedVehicle);
            assertTrue(removedVehicle.isRemoved(), "Stub vehicle must report isRemoved() == true");
            assertNull(DogCommandManager.getVehicleTarget(dogUuid),
                    "Lookup of removed vehicle must return null");
            assertEquals(0, DogCommandManager.getTrackedVehicleTargetCount(),
                    "Lookup of removed vehicle must auto-prune map entry");

            Interaction liveVehicle = allocateStubVehicle();
            DogCommandManager.setVehicleTarget(dogUuid, liveVehicle);
            assertSame(liveVehicle, DogCommandManager.getVehicleTarget(dogUuid));

            DogCommandManager.setVehicleTarget(dogUuid, removedVehicle);
            assertNull(DogCommandManager.getVehicleTarget(dogUuid),
                    "Setting a removed vehicle must clear any existing target for that dog");
            assertEquals(0, DogCommandManager.getTrackedVehicleTargetCount());
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    @DisplayName("BL-BD-060: Bounded capacity reclaims stale entries before eviction and caps at MAX_TRACKED_VEHICLE_TARGETS")
    void testBoundedVehicleTargetCapacityAndStalePrune() throws Exception {
        List<UUID> trackedDogs = new ArrayList<>();
        List<Entity> strongRefs = new ArrayList<>();
        try {
            UUID staleDog = UUID.randomUUID();
            Interaction staleVehicle = allocateStubVehicle();
            trackedDogs.add(staleDog);
            strongRefs.add(staleVehicle);
            DogCommandManager.setVehicleTarget(staleDog, staleVehicle);

            for (int i = 1; i < DogCommandManager.MAX_TRACKED_VEHICLE_TARGETS; i++) {
                UUID dogId = UUID.randomUUID();
                Interaction liveVehicle = allocateStubVehicle();
                trackedDogs.add(dogId);
                strongRefs.add(liveVehicle);
                DogCommandManager.setVehicleTarget(dogId, liveVehicle);
            }
            assertEquals(DogCommandManager.MAX_TRACKED_VEHICLE_TARGETS,
                    DogCommandManager.getTrackedVehicleTargetCount());

            // Mark one tracked entry removed; inserting a new target must prune it before evicting live targets
            markRemoved(staleVehicle);
            UUID replacementDog = UUID.randomUUID();
            Interaction replacementVehicle = allocateStubVehicle();
            trackedDogs.add(replacementDog);
            strongRefs.add(replacementVehicle);
            DogCommandManager.setVehicleTarget(replacementDog, replacementVehicle);

            assertEquals(DogCommandManager.MAX_TRACKED_VEHICLE_TARGETS,
                    DogCommandManager.getTrackedVehicleTargetCount());
            assertNull(DogCommandManager.getVehicleTarget(staleDog));
            assertSame(replacementVehicle, DogCommandManager.getVehicleTarget(replacementDog));
            assertNotNull(DogCommandManager.getVehicleTarget(trackedDogs.get(1)),
                    "Live entry should not be evicted when stale entry can be pruned");

            // Overflow past MAX_TRACKED_VEHICLE_TARGETS with all live references must stay strictly bounded
            for (int i = 0; i < 200; i++) {
                UUID extraDog = UUID.randomUUID();
                Interaction extraVehicle = allocateStubVehicle();
                trackedDogs.add(extraDog);
                strongRefs.add(extraVehicle);
                DogCommandManager.setVehicleTarget(extraDog, extraVehicle);
            }
            assertTrue(DogCommandManager.getTrackedVehicleTargetCount() <= DogCommandManager.MAX_TRACKED_VEHICLE_TARGETS);
            assertEquals(DogCommandManager.MAX_TRACKED_VEHICLE_TARGETS,
                    DogCommandManager.getTrackedVehicleTargetCount());
        } finally {
            for (UUID dogId : trackedDogs) {
                DogCommandManager.clearVehicleTarget(dogId);
            }
        }
    }

    @Test
    @DisplayName("BL-BD-060: Null dogUuid or null vehicle target never throw and clear existing entry")
    void testVehicleTargetNullAndNegativeParity() throws Exception {
        UUID dogUuid = UUID.randomUUID();
        try {
            Interaction vehicle = allocateStubVehicle();
            assertDoesNotThrow(() -> DogCommandManager.setVehicleTarget(null, vehicle));
            assertNull(DogCommandManager.getVehicleTarget(null));

            DogCommandManager.setVehicleTarget(dogUuid, vehicle);
            assertSame(vehicle, DogCommandManager.getVehicleTarget(dogUuid));

            assertDoesNotThrow(() -> DogCommandManager.setVehicleTarget(dogUuid, null));
            assertNull(DogCommandManager.getVehicleTarget(dogUuid));
            assertEquals(0, DogCommandManager.getTrackedVehicleTargetCount());
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    @DisplayName("BL-BD-045: Orphan seat grace window and state predicate boundaries")
    void testOrphanSeatGraceAndStatePredicates() {
        assertEquals(200, DogCommandManager.ORPHAN_SEAT_GRACE_TICKS,
                "Orphan seat grace window must equal 200 ticks (10 seconds)");
        assertEquals("betterdogs:seat", DogCommandManager.SEAT_ENTITY_TAG);

        // Young seat (< 200 ticks) must never be swept
        assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, false, 0));
        assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, false, 199));

        // Actively targeted seat must never be swept even past grace window
        assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, true, 200));
        assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, true, 5000));

        // Occupied seat must never be swept
        assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, true, true, false, 200));

        // Untagged Interaction or vanilla entity must never be swept
        assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, false, false, false, 200));

        // Already removed entity must never be swept again
        assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(true, true, false, false, 200));

        // Only unremoved, tagged, empty, untargeted seat >= 200 ticks qualifies
        assertTrue(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, false, 200));
        assertTrue(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, false, 1200));
    }

    @Test
    @DisplayName("BL-BD-045: Negative parity on untagged/removed/null vehicles and null-level active target protection")
    void testAbandonedSeatNegativeParity() throws Exception {
        UUID dogUuid = UUID.randomUUID();
        try {
            assertFalse(DogCommandManager.isBetterDogsSeat(null));
            assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, null));
            assertFalse(DogCommandManager.discardIfAbandonedSeat(null, null));
            assertFalse(DogCommandManager.shouldSweepOrphanedSeat(null, null));
            assertFalse(DogCommandManager.isActivelyTargetedByLiveWolf(null, null));

            // Untagged Unsafe Interaction stub (tags == null) must never be treated as a Better Dogs seat
            Interaction untaggedStub = allocateStubVehicle();
            assertFalse(DogCommandManager.isBetterDogsSeat(untaggedStub));
            assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, untaggedStub));
            assertFalse(DogCommandManager.discardIfAbandonedSeat(null, untaggedStub));
            assertFalse(DogCommandManager.shouldSweepOrphanedSeat(null, untaggedStub));

            // Removed entity must never trigger discard even if looked up
            Interaction removedStub = allocateStubVehicle();
            markRemoved(removedStub);
            assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, removedStub));
            assertFalse(DogCommandManager.discardIfAbandonedSeat(null, removedStub));
            assertFalse(DogCommandManager.shouldSweepOrphanedSeat(null, removedStub));

            // Null ServerLevel protects live tracked vehicle from premature target pruning
            Interaction activeVehicle = allocateStubVehicle();
            DogCommandManager.setVehicleTarget(dogUuid, activeVehicle);
            assertTrue(DogCommandManager.isActivelyTargetedByLiveWolf(null, activeVehicle));
            assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, activeVehicle));
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    @DisplayName("BL-BD-045: MoveToVehicleGoal aborts on sit toggle and stops cleanly with null navigation")
    void testMoveToVehicleGoalSitAbortStopsWithoutError() throws Exception {
        UUID dogUuid = UUID.randomUUID();
        try {
            StubWolf stubWolf = (StubWolf) UNSAFE.allocateInstance(StubWolf.class);
            stubWolf.initStub(dogUuid);
            Interaction targetVehicle = allocateStubVehicle();

            DogCommandManager.setVehicleTarget(dogUuid, targetVehicle);
            MoveToVehicleGoal goal = new MoveToVehicleGoal(stubWolf);

            stubWolf.setOrderedToSit(false);
            assertTrue(goal.canContinueToUse(),
                    "Standing StubWolf with live target must be allowed to continue MoveToVehicleGoal");

            stubWolf.setOrderedToSit(true);
            assertFalse(goal.canContinueToUse(),
                    "Sit toggle must immediately cause MoveToVehicleGoal.canContinueToUse() to return false");

            assertDoesNotThrow(goal::stop,
                    "MoveToVehicleGoal.stop() must clear VEHICLE_TARGETS cleanly even with null level and navigation");
            assertNull(DogCommandManager.getVehicleTarget(dogUuid),
                    "MoveToVehicleGoal.stop() must clear tracked vehicle target");
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    private static final class StubWolf extends Wolf {
        private UUID stubUuid;
        private boolean orderedToSit;

        private StubWolf() {
            super(null, null);
        }

        void initStub(UUID uuid) throws Exception {
            this.stubUuid = uuid;
            this.orderedToSit = false;
            if (positionField == null) {
                positionField = Entity.class.getDeclaredField("position");
                positionField.setAccessible(true);
            }
            positionField.set(this, Vec3.ZERO);
        }

        @Override
        public UUID getUUID() {
            return this.stubUuid;
        }

        @Override
        public boolean isOrderedToSit() {
            return this.orderedToSit;
        }

        @Override
        public void setOrderedToSit(boolean ordered) {
            this.orderedToSit = ordered;
        }

        @Override
        public boolean isPassenger() {
            return false;
        }

        @Override
        public Level level() {
            return null;
        }

        @Override
        public PathNavigation getNavigation() {
            return null;
        }
    }
}
