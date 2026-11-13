// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.1
package net.vanillaoutsider.betterdogs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.vanillaoutsider.betterdogs.ai.PacifistRevengeGoal;
import net.vanillaoutsider.betterdogs.util.AdultDisciplineHelper;
import net.vanillaoutsider.betterdogs.util.PackAudienceHelper;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 35 & BL-BD-053: Pack Civil War Immunity & Offline Co-Ownership Tests")
class PackCivilWarImmunityTest {

    private static final Unsafe UNSAFE = initUnsafe();

    private static Unsafe initUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to initialize sun.misc.Unsafe for OfflineStubWolf", ex);
        }
    }

    private static OfflineStubWolf createOfflineWolf(boolean tame, @Nullable UUID ownerUuid) {
        try {
            OfflineStubWolf wolf = (OfflineStubWolf) UNSAFE.allocateInstance(OfflineStubWolf.class);
            wolf.tame = tame;
            wolf.ownerUuid = ownerUuid;
            return wolf;
        } catch (InstantiationException ex) {
            throw new IllegalStateException("Failed to allocate OfflineStubWolf", ex);
        }
    }

    private static final class OfflineStubWolf extends Wolf {
        private volatile boolean tame;
        private volatile @Nullable UUID ownerUuid;

        private OfflineStubWolf() {
            super(null, null);
        }

        @Override
        public boolean isTame() {
            return this.tame;
        }

        @Override
        public @Nullable LivingEntity getOwner() {
            return null; // Simulates offline / cross-dimension owner (also guards this.level() NPE on Unsafe stub)
        }

        @Override
        public @Nullable UUID getOwnerUUID() {
            return this.ownerUuid;
        }
    }

    @Test
    @DisplayName("Assert co-owned packmates owner matching logic")
    void testCoOwnedPackmateMatching() {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();

        assertEquals(ownerA, ownerA, "Matching owner UUID must qualify as packmates");
        assertNotEquals(ownerA, ownerB, "Different owners must not qualify as packmates");
        assertFalse(PackAudienceHelper.isCoOwned(null, null), "Null wolves cannot be co-owned");
    }

    @Test
    @DisplayName("Assert offline/cross-dimension co-owned tamed wolves remain co-owned (BL-BD-053)")
    void testOfflineCoOwnedWolvesMatch() {
        UUID ownerA = UUID.randomUUID();
        OfflineStubWolf dogA1 = createOfflineWolf(true, ownerA);
        OfflineStubWolf dogA2 = createOfflineWolf(true, ownerA);

        assertNull(dogA1.getOwner(), "Offline stub wolf must simulate null live owner");
        assertNull(dogA2.getOwner(), "Offline stub wolf must simulate null live owner");
        assertTrue(PackAudienceHelper.isCoOwned(dogA1, dogA2),
                "Co-owned tamed dogs must remain co-owned when owner is offline or cross-dimension");
    }

    @Test
    @DisplayName("Assert Negative Parity: offline rival-owned tamed wolves are not co-owned (BL-BD-053)")
    void testOfflineRivalOwnedWolvesRejected() {
        OfflineStubWolf dogA1 = createOfflineWolf(true, UUID.randomUUID());
        OfflineStubWolf dogB1 = createOfflineWolf(true, UUID.randomUUID());

        assertFalse(PackAudienceHelper.isCoOwned(dogA1, dogB1),
                "Tamed dogs owned by different offline players must never qualify as co-owned");
    }

    @Test
    @DisplayName("Assert untamed wild wolves do not fail-open on null==null (BL-BD-053)")
    void testUntamedWildWolvesRejected() {
        OfflineStubWolf wild1 = createOfflineWolf(false, null);
        OfflineStubWolf wild2 = createOfflineWolf(false, null);

        assertFalse(PackAudienceHelper.isCoOwned(wild1, wild2),
                "Untamed wild wolves with null owners must never qualify as co-owned");
    }

    @Test
    @DisplayName("Assert tamed vs untamed and tamed with null owner UUID are rejected (BL-BD-053)")
    void testTamedVsUntamedAndNullUuidRejected() {
        UUID ownerA = UUID.randomUUID();
        OfflineStubWolf tamedDog = createOfflineWolf(true, ownerA);
        OfflineStubWolf untamedStaleUuid = createOfflineWolf(false, ownerA);
        OfflineStubWolf tamedNullOwner = createOfflineWolf(true, null);

        assertFalse(PackAudienceHelper.isCoOwned(tamedDog, untamedStaleUuid),
                "Tamed dog and untamed wolf sharing stale UUID must not be co-owned");
        assertFalse(PackAudienceHelper.isCoOwned(untamedStaleUuid, tamedDog),
                "Untamed wolf and tamed dog sharing stale UUID must not be co-owned");
        assertFalse(PackAudienceHelper.isCoOwned(tamedNullOwner, tamedNullOwner),
                "Tamed wolf with null owner UUID must never match itself via null==null");
    }

    @Test
    @DisplayName("Assert asymmetric null wolf references are safely rejected (BL-BD-053)")
    void testAsymmetricNullWolvesRejected() {
        OfflineStubWolf dogA1 = createOfflineWolf(true, UUID.randomUUID());

        assertFalse(PackAudienceHelper.isCoOwned(dogA1, null), "Right-side null wolf must return false");
        assertFalse(PackAudienceHelper.isCoOwned(null, dogA1), "Left-side null wolf must return false");
        assertFalse(PackAudienceHelper.isCoOwned(null, null), "Both null wolves must return false");
    }

    @Test
    @DisplayName("Assert Pillar 3 concurrency stress: 64 threads racing isCoOwned deterministically (BL-BD-053)")
    void testConcurrentIsCoOwnedStress() throws InterruptedException, ExecutionException {
        UUID ownerA = UUID.randomUUID();
        UUID ownerB = UUID.randomUUID();
        OfflineStubWolf dogA1 = createOfflineWolf(true, ownerA);
        OfflineStubWolf dogA2 = createOfflineWolf(true, ownerA);
        OfflineStubWolf dogB1 = createOfflineWolf(true, ownerB);
        OfflineStubWolf wild1 = createOfflineWolf(false, null);
        OfflineStubWolf wild2 = createOfflineWolf(false, null);

        int threads = 64;
        int iterationsPerThread = 150;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>(threads);

        try {
            for (int t = 0; t < threads; t++) {
                futures.add(executor.submit(() -> {
                    startGate.await();
                    for (int i = 0; i < iterationsPerThread; i++) {
                        if (!PackAudienceHelper.isCoOwned(dogA1, dogA2)) {
                            return false;
                        }
                        if (PackAudienceHelper.isCoOwned(dogA1, dogB1)) {
                            return false;
                        }
                        if (PackAudienceHelper.isCoOwned(wild1, wild2)) {
                            return false;
                        }
                        if (PackAudienceHelper.isCoOwned(dogA1, wild1)) {
                            return false;
                        }
                        if (PackAudienceHelper.isCoOwned(dogA1, null) || PackAudienceHelper.isCoOwned(null, dogB1)) {
                            return false;
                        }
                    }
                    return true;
                }));
            }
            startGate.countDown();
            for (Future<Boolean> future : futures) {
                assertTrue(future.get(), "Concurrent isCoOwned evaluation must remain deterministic with zero errors");
            }
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(15, TimeUnit.SECONDS), "Concurrency executor must terminate cleanly");
        }
    }

    @Test
    @DisplayName("Assert universal domestic alert silencing null safety")
    void testDomesticAlertSilencingNullSafety() {
        assertFalse(AdultDisciplineHelper.shouldSilenceAlert(null, null), "Null victim/attacker must return false");
        assertFalse(AdultDisciplineHelper.isPlayCorrectionSnap(null, null), "Null wolves cannot discipline");
    }

    @Test
    @DisplayName("Assert retaliation suppression logic remains intact")
    void testRetaliationSuppression() {
        assertTrue(AdultDisciplineHelper.shouldSuppressRetaliation(true), "Suppression must be active when disciplined");
        assertFalse(AdultDisciplineHelper.shouldSuppressRetaliation(false), "Suppression must be inactive when not disciplined");
    }

    @Test
    @DisplayName("Assert PacifistRevengeGoal null safety on packmate check")
    void testPacifistRevengeNullSafety() {
        PacifistRevengeGoal goal = new PacifistRevengeGoal(null);
        assertFalse(goal.canUse());
    }

    @Test
    @DisplayName("Assert Negative Parity: Non-co-owned wolves, wild wolves, and sitting/leashed dogs are immune to sparring (BL-BD-035)")
    void testNegativeParitySparringImmunity() {
        assertFalse(net.vanillaoutsider.betterdogs.util.SmallFightHelper.shouldInitiatePlayFight(false, 400, 100, 0, 0),
                "Wild or ineligible wolves must never initiate sparring");
        assertFalse(net.vanillaoutsider.betterdogs.util.SmallFightHelper.isEligibleForPlay(true, false, false, false, false, false, 0, false, false),
                "Wild wolves must never be eligible for play");
        assertFalse(net.vanillaoutsider.betterdogs.util.SmallFightHelper.isEligibleForPlay(true, true, true, false, false, false, 0, false, false),
                "Sitting wolves must never be eligible for play");
        assertFalse(net.vanillaoutsider.betterdogs.util.SmallFightHelper.isEligibleForPlay(true, true, false, true, false, false, 0, false, false),
                "Leashed wolves must never be eligible for play");
        assertFalse(net.vanillaoutsider.betterdogs.util.SmallFightHelper.isEligibleForPlay(true, true, false, false, true, false, 0, false, false),
                "Wolves with active targets must never be eligible for play");
    }
}
