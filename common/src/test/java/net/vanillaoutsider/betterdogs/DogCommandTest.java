// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.ai.MoveToVehicleGoal;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

public class DogCommandTest {

    private static final Unsafe UNSAFE = initUnsafe();
    private static final Field REMOVAL_REASON_FIELD = resolveRemovalReasonField();

    private static Unsafe initUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Unsafe", e);
        }
    }

    private static Field resolveRemovalReasonField() {
        try {
            Field field = Entity.class.getDeclaredField("removalReason");
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve Entity.removalReason", e);
        }
    }

    private static Interaction allocateInteractionStub() {
        try {
            return (Interaction) UNSAFE.allocateInstance(Interaction.class);
        } catch (InstantiationException e) {
            throw new RuntimeException("Failed to allocate Interaction stub", e);
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
        try {
            StubWolf wolf = (StubWolf) UNSAFE.allocateInstance(StubWolf.class);
            wolf.stubUuid = wolfUuid;
            return wolf;
        } catch (InstantiationException e) {
            throw new RuntimeException("Failed to allocate StubWolf", e);
        }
    }

    private static final class StubWolf extends Wolf {
        private UUID stubUuid;

        private StubWolf(EntityType<? extends Wolf> type, Level level) {
            super(type, level);
        }

        @Override
        public UUID getUUID() {
            return this.stubUuid;
        }

        @Override
        public boolean isOrderedToSit() {
            return false;
        }

        @Override
        public boolean isPassenger() {
            return false;
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
}
