// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs;

import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.Item;
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
    private static Field passengersField;
    private static Field tagsField;
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
            passengersField = Entity.class.getDeclaredField("passengers");
            passengersField.setAccessible(true);
            tagsField = Entity.class.getDeclaredField("tags");
            tagsField.setAccessible(true);
            positionField = Entity.class.getDeclaredField("position");
            positionField.setAccessible(true);
        } catch (Throwable ignored) {
        }
    }

    private static Interaction allocateStubVehicle() throws Exception {
        return (Interaction) UNSAFE.allocateInstance(Interaction.class);
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
    @DisplayName("BL-BD-045: Orphan seat grace window and pure state predicates")
    void testOrphanSeatGraceAndStatePredicates() throws Exception {
        assertEquals("betterdogs:seat", DogCommandManager.SEAT_ENTITY_TAG);
        assertEquals(200, DogCommandManager.ORPHAN_SEAT_GRACE_TICKS);

        assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, false, 0));
        assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, false, 199));
        assertTrue(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, false, 200));
        assertTrue(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, false, 450));
        assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(true, true, false, false, 450));
        assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, false, false, false, 450));
        assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, true, true, false, 450));
        assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, true, 450));

        UUID dogUuid = UUID.randomUUID();
        try {
            Interaction seat = allocateStubInteraction(true);
            seat.tickCount = 199;
            assertFalse(DogCommandManager.shouldSweepOrphanedSeat(null, seat),
                    "Newly spawned seat below ORPHAN_SEAT_GRACE_TICKS must not be swept");

            seat.tickCount = 200;
            assertTrue(DogCommandManager.shouldSweepOrphanedSeat(null, seat),
                    "Untargeted empty seat at grace threshold must qualify for sweep");

            DogCommandManager.setVehicleTarget(dogUuid, seat);
            assertFalse(DogCommandManager.shouldSweepOrphanedSeat(null, seat),
                    "Actively targeted seat must survive periodic sweep even past grace window");
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    @DisplayName("BL-BD-045: Abandoned seat cleanup preserves untagged interactions, occupied seats, targeted seats, and vanilla vehicles")
    void testAbandonedSeatNegativeParity() throws Exception {
        UUID dogUuid = UUID.randomUUID();
        try {
            assertFalse(DogCommandManager.isBetterDogsSeat(null));
            assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, null));
            assertDoesNotThrow(() -> DogCommandManager.discardIfAbandonedSeat(null, null));

            Interaction untaggedInteraction = allocateStubInteraction(false);
            untaggedInteraction.entityTags().add("datapack:custom_hitbox");
            untaggedInteraction.tickCount = 500;
            assertFalse(DogCommandManager.isBetterDogsSeat(untaggedInteraction));
            assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, untaggedInteraction));
            assertFalse(DogCommandManager.shouldSweepOrphanedSeat(null, untaggedInteraction));
            assertDoesNotThrow(() -> DogCommandManager.discardIfAbandonedSeat(null, untaggedInteraction));

            Boat vanillaBoat = allocateBoatStub();
            vanillaBoat.tickCount = 500;
            assertFalse(DogCommandManager.isBetterDogsSeat(vanillaBoat));
            assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, vanillaBoat));
            assertDoesNotThrow(() -> DogCommandManager.discardIfAbandonedSeat(null, vanillaBoat));

            Interaction occupiedSeat = allocateStubInteraction(true);
            setPassengers(occupiedSeat, ImmutableList.of(allocateWolfStub(UUID.randomUUID(), false, false, true)));
            occupiedSeat.tickCount = 500;
            assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, occupiedSeat));
            assertFalse(DogCommandManager.shouldSweepOrphanedSeat(null, occupiedSeat));
            assertDoesNotThrow(() -> DogCommandManager.discardIfAbandonedSeat(null, occupiedSeat));

            Interaction targetedSeat = allocateStubInteraction(true);
            DogCommandManager.setVehicleTarget(dogUuid, targetedSeat);
            assertTrue(DogCommandManager.isActivelyTargetedByLiveWolf(null, targetedSeat));
            assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, targetedSeat));
            assertDoesNotThrow(() -> DogCommandManager.discardIfAbandonedSeat(null, targetedSeat));

            DogCommandManager.clearVehicleTarget(dogUuid);
            assertTrue(DogCommandManager.shouldDiscardAbandonedSeat(null, targetedSeat));
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    @DisplayName("BL-BD-045: prepareWolfForBoarding clears sit & pose and MoveToVehicleGoal aborts cleanly on sit toggle")
    void testMoveToVehicleGoalSitAbortStopsWithoutError() throws Exception {
        UUID dogUuid = UUID.randomUUID();
        try {
            StubWolf wolf = allocateWolfStub(dogUuid, true, true, false);
            assertTrue(wolf.isOrderedToSit());
            assertTrue(wolf.isInSittingPose());

            assertDoesNotThrow(() -> DogCommandManager.prepareWolfForBoarding(null));
            DogCommandManager.prepareWolfForBoarding(wolf);
            assertFalse(wolf.isOrderedToSit(), "prepareWolfForBoarding must clear orderedToSit");
            assertFalse(wolf.isInSittingPose(), "prepareWolfForBoarding must clear sittingPose");

            Boat vanillaBoat = allocateBoatStub();
            DogCommandManager.setVehicleTarget(dogUuid, vanillaBoat);

            MoveToVehicleGoal goal = new MoveToVehicleGoal(wolf);
            goal.start();
            assertTrue(goal.canContinueToUse(), "Standing dog with live target should continue navigating");

            wolf.setOrderedToSit(true);
            assertFalse(goal.canContinueToUse(), "Manually ordering sit mid-walk must abort MoveToVehicleGoal");

            assertDoesNotThrow(goal::stop, "stop() must remain null-safe on navigation and StubWolf");
            assertNull(DogCommandManager.getVehicleTarget(dogUuid), "stop() must clear tracked vehicle target");
            assertFalse(vanillaBoat.isRemoved(), "stop() must never discard non-seat vanilla Boat");

            // Negative parity: mounted wolf stopping goal clears target without attempting seat discard
            StubWolf mountedWolf = allocateWolfStub(dogUuid, false, false, true);
            Interaction mountedSeat = allocateStubInteraction(true);
            DogCommandManager.setVehicleTarget(dogUuid, mountedSeat);
            MoveToVehicleGoal mountedGoal = new MoveToVehicleGoal(mountedWolf);
            assertDoesNotThrow(mountedGoal::stop);
            assertNull(DogCommandManager.getVehicleTarget(dogUuid));
            assertFalse(mountedSeat.isRemoved(), "stop() after mounting must not discard the seat");
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    private static StubWolf allocateWolfStub(UUID uuid, boolean orderedToSit, boolean sittingPose, boolean passenger) throws Exception {
        StubWolf wolf = (StubWolf) UNSAFE.allocateInstance(StubWolf.class);
        initEntityStubFields(wolf, false);
        wolf.uuid = uuid;
        wolf.orderedToSit = orderedToSit;
        wolf.sittingPose = sittingPose;
        wolf.passenger = passenger;
        wolf.alive = true;
        return wolf;
    }

    private static Interaction allocateStubInteraction(boolean tagged) throws Exception {
        Interaction seat = (Interaction) UNSAFE.allocateInstance(Interaction.class);
        initEntityStubFields(seat, tagged);
        return seat;
    }

    private static Boat allocateBoatStub() throws Exception {
        Boat boat = (Boat) UNSAFE.allocateInstance(Boat.class);
        initEntityStubFields(boat, false);
        return boat;
    }

    private static void initEntityStubFields(Entity entity, boolean tagged) throws Exception {
        if (passengersField == null) {
            passengersField = Entity.class.getDeclaredField("passengers");
            passengersField.setAccessible(true);
        }
        if (tagsField == null) {
            tagsField = Entity.class.getDeclaredField("tags");
            tagsField.setAccessible(true);
        }
        if (positionField == null) {
            positionField = Entity.class.getDeclaredField("position");
            positionField.setAccessible(true);
        }
        passengersField.set(entity, ImmutableList.of());
        positionField.set(entity, Vec3.ZERO);
        Set<String> tags = new HashSet<>();
        if (tagged) {
            tags.add(DogCommandManager.SEAT_ENTITY_TAG);
        }
        tagsField.set(entity, tags);
    }

    private static void setPassengers(Entity entity, ImmutableList<Entity> passengers) throws Exception {
        if (passengersField == null) {
            passengersField = Entity.class.getDeclaredField("passengers");
            passengersField.setAccessible(true);
        }
        passengersField.set(entity, passengers);
    }

    private static final class StubWolf extends Wolf {
        UUID uuid;
        boolean orderedToSit;
        boolean sittingPose;
        boolean passenger;
        boolean alive;

        private StubWolf() {
            super(EntityType.WOLF, null);
        }

        @Override public UUID getUUID() { return this.uuid; }
        @Override public boolean isOrderedToSit() { return this.orderedToSit; }
        @Override public void setOrderedToSit(boolean ordered) { this.orderedToSit = ordered; }
        @Override public boolean isInSittingPose() { return this.sittingPose; }
        @Override public void setInSittingPose(boolean pose) { this.sittingPose = pose; }
        @Override public boolean isPassenger() { return this.passenger; }
        @Override public boolean isAlive() { return this.alive; }
        @Override public Level level() { return null; }
    }
}
