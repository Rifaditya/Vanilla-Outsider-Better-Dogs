// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import com.google.common.collect.ImmutableList;
import java.lang.ref.WeakReference;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.ai.MoveToVehicleGoal;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

public class DogCommandTest {

    private static final Unsafe UNSAFE = initUnsafe();
    private static final Field REMOVAL_REASON_FIELD = resolveEntityField("removalReason");
    private static final Field PASSENGERS_FIELD = resolveEntityField("passengers");
    private static final Field TAGS_FIELD = resolveEntityField("tags");
    private static final Field POSITION_FIELD = resolveEntityField("position");

    private static Unsafe initUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Unsafe", e);
        }
    }

    private static Field resolveEntityField(String name) {
        try {
            Field field = Entity.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve Entity." + name, e);
        }
    }

    private static Interaction allocateInteractionStub() {
        try {
            return (Interaction) UNSAFE.allocateInstance(Interaction.class);
        } catch (InstantiationException e) {
            throw new RuntimeException("Failed to allocate Interaction stub", e);
        }
    }

    private static Interaction allocateTaggedInteractionStub(boolean tagged) {
        Interaction seat = allocateInteractionStub();
        initEntityStubFields(seat, tagged);
        return seat;
    }

    private static Boat allocateBoatStub() {
        try {
            Boat boat = (Boat) UNSAFE.allocateInstance(Boat.class);
            initEntityStubFields(boat, false);
            return boat;
        } catch (InstantiationException e) {
            throw new RuntimeException("Failed to allocate Boat stub", e);
        }
    }

    private static void initEntityStubFields(Entity entity, boolean tagged) {
        try {
            PASSENGERS_FIELD.set(entity, ImmutableList.of());
            POSITION_FIELD.set(entity, Vec3.ZERO);
            Set<String> tags = new HashSet<>();
            if (tagged) {
                tags.add(DogCommandManager.SEAT_ENTITY_TAG);
            }
            TAGS_FIELD.set(entity, tags);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to initialize stub entity fields", e);
        }
    }

    private static void setPassengers(Entity entity, ImmutableList<Entity> passengers) {
        try {
            PASSENGERS_FIELD.set(entity, passengers);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set Entity.passengers", e);
        }
    }

    private static void markRemoved(Entity entity) {
        try {
            REMOVAL_REASON_FIELD.set(entity, Entity.RemovalReason.DISCARDED);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set Entity.removalReason", e);
        }
    }

    private static StubWolf allocateStubWolf(UUID wolfUuid) {
        return allocateStubWolf(wolfUuid, false, false, false);
    }

    private static StubWolf allocateStubWolf(UUID wolfUuid, boolean orderedToSit, boolean sittingPose, boolean passenger) {
        try {
            StubWolf wolf = (StubWolf) UNSAFE.allocateInstance(StubWolf.class);
            initEntityStubFields(wolf, false);
            wolf.stubUuid = wolfUuid;
            wolf.orderedToSit = orderedToSit;
            wolf.sittingPose = sittingPose;
            wolf.passenger = passenger;
            wolf.alive = true;
            return wolf;
        } catch (InstantiationException e) {
            throw new RuntimeException("Failed to allocate StubWolf", e);
        }
    }

    private static final class StubWolf extends Wolf {
        private UUID stubUuid;
        private boolean orderedToSit;
        private boolean sittingPose;
        private boolean passenger;
        private boolean alive;

        private StubWolf(EntityType<? extends Wolf> type, Level level) {
            super(type, level);
        }

        private void setStubOrderedToSit(boolean ordered) {
            this.orderedToSit = ordered;
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
        public boolean isInSittingPose() {
            return this.sittingPose;
        }

        @Override
        public void setInSittingPose(boolean pose) {
            this.sittingPose = pose;
        }

        @Override
        public boolean isPassenger() {
            return this.passenger;
        }

        @Override
        public boolean isAlive() {
            return this.alive;
        }

        @Override
        public Level level() {
            return null;
        }
    }

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testCommandItemValidation() {
        Assertions.assertTrue(DogCommandManager.isCommandItem(new ItemStack(Items.STICK)));
        Assertions.assertTrue(DogCommandManager.isCommandItem(new ItemStack(Items.BLAZE_ROD)));
        Assertions.assertTrue(DogCommandManager.isCommandItem(new ItemStack(Items.BREEZE_ROD)));
        Assertions.assertFalse(DogCommandManager.isCommandItem(new ItemStack(Items.BONE)), "Bone should be excluded from selection to allow guard mode");
        Assertions.assertFalse(DogCommandManager.isCommandItem(ItemStack.EMPTY));
    }

    @Test
    void testSelectionAndVehicleTargetMaps() {
        UUID playerUuid = UUID.randomUUID();
        UUID dogUuid = UUID.randomUUID();
        Interaction liveSeat = allocateInteractionStub();

        try {
            DogCommandManager.selectDog(playerUuid, dogUuid);
            Assertions.assertEquals(dogUuid, DogCommandManager.getSelectedDog(playerUuid));

            DogCommandManager.clearSelection(playerUuid);
            Assertions.assertNull(DogCommandManager.getSelectedDog(playerUuid));

            DogCommandManager.setVehicleTarget(dogUuid, liveSeat);
            Assertions.assertSame(liveSeat, DogCommandManager.getVehicleTarget(dogUuid));

            DogCommandManager.clearVehicleTarget(dogUuid);
            Assertions.assertNull(DogCommandManager.getVehicleTarget(dogUuid));
        } finally {
            DogCommandManager.clearSelection(playerUuid);
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    void testVehicleTargetWeakReferenceReclaimedOnGc() throws InterruptedException {
        UUID dogUuid = UUID.randomUUID();
        int baselineCount = DogCommandManager.getTrackedVehicleTargetCount();

        try {
            Interaction ephemeralSeat = allocateInteractionStub();
            WeakReference<Interaction> probe = new WeakReference<>(ephemeralSeat);
            DogCommandManager.setVehicleTarget(dogUuid, ephemeralSeat);
            Assertions.assertSame(ephemeralSeat, DogCommandManager.getVehicleTarget(dogUuid));
            Assertions.assertEquals(baselineCount + 1, DogCommandManager.getTrackedVehicleTargetCount());

            ephemeralSeat = null;
            for (int attempt = 0; attempt < 30 && probe.get() != null; attempt++) {
                System.gc();
                Thread.sleep(15L);
            }

            Assertions.assertNull(probe.get(), "Unreferenced Interaction stub should be garbage-collected");
            Assertions.assertNull(DogCommandManager.getVehicleTarget(dogUuid), "GC'd vehicle target must return null and auto-prune");
            Assertions.assertEquals(baselineCount, DogCommandManager.getTrackedVehicleTargetCount(),
                    "Auto-prune must drop cleared WeakReference entry from VEHICLE_TARGETS");
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    void testRemovedVehicleTargetRejectedAndAutoPruned() {
        UUID dogUuid = UUID.randomUUID();
        int baselineCount = DogCommandManager.getTrackedVehicleTargetCount();

        try {
            Interaction alreadyRemoved = allocateInteractionStub();
            markRemoved(alreadyRemoved);
            Assertions.assertTrue(alreadyRemoved.isRemoved());

            DogCommandManager.setVehicleTarget(dogUuid, alreadyRemoved);
            Assertions.assertNull(DogCommandManager.getVehicleTarget(dogUuid), "Removed vehicle must be rejected on insertion");
            Assertions.assertEquals(baselineCount, DogCommandManager.getTrackedVehicleTargetCount());

            Interaction liveThenRemoved = allocateInteractionStub();
            DogCommandManager.setVehicleTarget(dogUuid, liveThenRemoved);
            Assertions.assertSame(liveThenRemoved, DogCommandManager.getVehicleTarget(dogUuid));
            Assertions.assertEquals(baselineCount + 1, DogCommandManager.getTrackedVehicleTargetCount());

            markRemoved(liveThenRemoved);
            Assertions.assertNull(DogCommandManager.getVehicleTarget(dogUuid), "Removed vehicle target must return null and auto-prune");
            Assertions.assertEquals(baselineCount, DogCommandManager.getTrackedVehicleTargetCount());
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    void testVehicleTargetCapEnforcedAt1024() {
        Assertions.assertEquals(1024, DogCommandManager.MAX_TRACKED_VEHICLE_TARGETS);
        List<UUID> trackedDogs = new ArrayList<>();
        List<Interaction> strongRefs = new ArrayList<>();
        int totalToInsert = DogCommandManager.MAX_TRACKED_VEHICLE_TARGETS + 64;

        try {
            for (int i = 0; i < totalToInsert; i++) {
                UUID dogUuid = UUID.randomUUID();
                Interaction seat = allocateInteractionStub();
                trackedDogs.add(dogUuid);
                strongRefs.add(seat);
                DogCommandManager.setVehicleTarget(dogUuid, seat);
            }

            Assertions.assertEquals(DogCommandManager.MAX_TRACKED_VEHICLE_TARGETS, DogCommandManager.getTrackedVehicleTargetCount(),
                    "VEHICLE_TARGETS must remain bounded to MAX_TRACKED_VEHICLE_TARGETS");
        } finally {
            for (UUID dogUuid : trackedDogs) {
                DogCommandManager.clearVehicleTarget(dogUuid);
            }
            strongRefs.clear();
        }
    }

    @Test
    void testNullAndInvalidBoundaryNegativeParity() {
        UUID dogUuid = UUID.randomUUID();
        Interaction liveSeat = allocateInteractionStub();

        try {
            Assertions.assertDoesNotThrow(() -> DogCommandManager.selectDog(null, dogUuid));
            Assertions.assertDoesNotThrow(() -> DogCommandManager.selectDog(dogUuid, null));
            Assertions.assertNull(DogCommandManager.getSelectedDog(null));
            Assertions.assertDoesNotThrow(() -> DogCommandManager.clearSelection(null));

            Assertions.assertDoesNotThrow(() -> DogCommandManager.setVehicleTarget(null, liveSeat));
            Assertions.assertNull(DogCommandManager.getVehicleTarget(null));
            Assertions.assertDoesNotThrow(() -> DogCommandManager.clearVehicleTarget(null));

            DogCommandManager.setVehicleTarget(dogUuid, liveSeat);
            Assertions.assertSame(liveSeat, DogCommandManager.getVehicleTarget(dogUuid));
            DogCommandManager.setVehicleTarget(dogUuid, null);
            Assertions.assertNull(DogCommandManager.getVehicleTarget(dogUuid), "Setting null vehicle target must clear existing entry");
        } finally {
            DogCommandManager.clearSelection(dogUuid);
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    void testMoveToVehicleGoalClearsRemovedTargetOnCanUseAndTick() {
        UUID dogUuid = UUID.randomUUID();
        StubWolf wolf = allocateStubWolf(dogUuid);
        MoveToVehicleGoal goal = new MoveToVehicleGoal(wolf);

        try {
            Interaction seat = allocateInteractionStub();
            DogCommandManager.setVehicleTarget(dogUuid, seat);
            markRemoved(seat);
            Assertions.assertFalse(seat.isAlive());

            Assertions.assertFalse(goal.canUse());
            Assertions.assertNull(DogCommandManager.getVehicleTarget(dogUuid),
                    "MoveToVehicleGoal.canUse must clear invalid/non-living vehicle target");

            Interaction seatForTick = allocateInteractionStub();
            DogCommandManager.setVehicleTarget(dogUuid, seatForTick);
            markRemoved(seatForTick);
            Assertions.assertDoesNotThrow(goal::tick);
            Assertions.assertNull(DogCommandManager.getVehicleTarget(dogUuid),
                    "MoveToVehicleGoal.tick must clear invalid/non-living vehicle target");
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    void testOrphanSeatGraceAndStatePredicates() {
        Assertions.assertEquals("betterdogs:seat", DogCommandManager.SEAT_ENTITY_TAG);
        Assertions.assertEquals(200, DogCommandManager.ORPHAN_SEAT_GRACE_TICKS);

        Assertions.assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, false, 0));
        Assertions.assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, false, 199));
        Assertions.assertTrue(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, false, 200));
        Assertions.assertTrue(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, false, 450));
        Assertions.assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(true, true, false, false, 450));
        Assertions.assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, false, false, false, 450));
        Assertions.assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, true, true, false, 450));
        Assertions.assertFalse(DogCommandManager.shouldSweepOrphanedSeatState(false, true, false, true, 450));

        UUID dogUuid = UUID.randomUUID();
        try {
            Interaction seat = allocateTaggedInteractionStub(true);
            seat.tickCount = 199;
            Assertions.assertFalse(DogCommandManager.shouldSweepOrphanedSeat(null, seat),
                    "Newly spawned seat below ORPHAN_SEAT_GRACE_TICKS must not be swept");

            seat.tickCount = 200;
            Assertions.assertTrue(DogCommandManager.shouldSweepOrphanedSeat(null, seat),
                    "Untargeted empty seat at grace threshold must qualify for sweep");

            DogCommandManager.setVehicleTarget(dogUuid, seat);
            Assertions.assertFalse(DogCommandManager.shouldSweepOrphanedSeat(null, seat),
                    "Actively targeted seat must survive periodic sweep even past grace window");
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    void testAbandonedSeatNegativeParity() {
        UUID dogUuid = UUID.randomUUID();
        try {
            Assertions.assertFalse(DogCommandManager.isBetterDogsSeat(null));
            Assertions.assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, null));
            Assertions.assertDoesNotThrow(() -> DogCommandManager.discardIfAbandonedSeat(null, null));

            Interaction untaggedInteraction = allocateTaggedInteractionStub(false);
            untaggedInteraction.getTags().add("datapack:custom_hitbox");
            untaggedInteraction.tickCount = 500;
            Assertions.assertFalse(DogCommandManager.isBetterDogsSeat(untaggedInteraction));
            Assertions.assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, untaggedInteraction));
            Assertions.assertFalse(DogCommandManager.shouldSweepOrphanedSeat(null, untaggedInteraction));
            Assertions.assertDoesNotThrow(() -> DogCommandManager.discardIfAbandonedSeat(null, untaggedInteraction));

            Boat vanillaBoat = allocateBoatStub();
            vanillaBoat.tickCount = 500;
            Assertions.assertFalse(DogCommandManager.isBetterDogsSeat(vanillaBoat));
            Assertions.assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, vanillaBoat));
            Assertions.assertDoesNotThrow(() -> DogCommandManager.discardIfAbandonedSeat(null, vanillaBoat));

            Interaction occupiedSeat = allocateTaggedInteractionStub(true);
            setPassengers(occupiedSeat, ImmutableList.of(allocateStubWolf(UUID.randomUUID(), false, false, true)));
            occupiedSeat.tickCount = 500;
            Assertions.assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, occupiedSeat));
            Assertions.assertFalse(DogCommandManager.shouldSweepOrphanedSeat(null, occupiedSeat));
            Assertions.assertDoesNotThrow(() -> DogCommandManager.discardIfAbandonedSeat(null, occupiedSeat));

            Interaction targetedSeat = allocateTaggedInteractionStub(true);
            DogCommandManager.setVehicleTarget(dogUuid, targetedSeat);
            Assertions.assertTrue(DogCommandManager.isActivelyTargetedByLiveWolf(null, targetedSeat));
            Assertions.assertFalse(DogCommandManager.shouldDiscardAbandonedSeat(null, targetedSeat));
            Assertions.assertDoesNotThrow(() -> DogCommandManager.discardIfAbandonedSeat(null, targetedSeat));

            DogCommandManager.clearVehicleTarget(dogUuid);
            Assertions.assertTrue(DogCommandManager.shouldDiscardAbandonedSeat(null, targetedSeat));
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }

    @Test
    void testMoveToVehicleGoalSitAbortStopsWithoutError() {
        UUID dogUuid = UUID.randomUUID();
        try {
            StubWolf wolf = allocateStubWolf(dogUuid, true, true, false);
            Assertions.assertTrue(wolf.isOrderedToSit());
            Assertions.assertTrue(wolf.isInSittingPose());

            Assertions.assertDoesNotThrow(() -> DogCommandManager.prepareWolfForBoarding(null));
            DogCommandManager.prepareWolfForBoarding(wolf);
            Assertions.assertFalse(wolf.isOrderedToSit(), "prepareWolfForBoarding must clear orderedToSit");
            Assertions.assertFalse(wolf.isInSittingPose(), "prepareWolfForBoarding must clear sittingPose");

            Boat vanillaBoat = allocateBoatStub();
            DogCommandManager.setVehicleTarget(dogUuid, vanillaBoat);

            MoveToVehicleGoal goal = new MoveToVehicleGoal(wolf);
            goal.start();
            Assertions.assertTrue(goal.canContinueToUse(), "Standing dog with live target should continue navigating");

            wolf.setStubOrderedToSit(true);
            Assertions.assertFalse(goal.canContinueToUse(), "Manually ordering sit mid-walk must abort MoveToVehicleGoal");

            Assertions.assertDoesNotThrow(goal::stop, "stop() must remain null-safe on navigation and StubWolf");
            Assertions.assertNull(DogCommandManager.getVehicleTarget(dogUuid), "stop() must clear tracked vehicle target");
            Assertions.assertFalse(vanillaBoat.isRemoved(), "stop() must never discard non-seat vanilla Boat");

            StubWolf mountedWolf = allocateStubWolf(dogUuid, false, false, true);
            Interaction mountedSeat = allocateTaggedInteractionStub(true);
            DogCommandManager.setVehicleTarget(dogUuid, mountedSeat);
            MoveToVehicleGoal mountedGoal = new MoveToVehicleGoal(mountedWolf);
            Assertions.assertDoesNotThrow(mountedGoal::stop);
            Assertions.assertNull(DogCommandManager.getVehicleTarget(dogUuid));
            Assertions.assertFalse(mountedSeat.isRemoved(), "stop() after mounting must not discard the seat");
        } finally {
            DogCommandManager.clearVehicleTarget(dogUuid);
        }
    }
}
