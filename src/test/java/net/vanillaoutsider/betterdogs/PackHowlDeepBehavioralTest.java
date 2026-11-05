// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.harness.BehavioralStepRunner;
import net.vanillaoutsider.betterdogs.harness.SimulatedEntity;
import net.vanillaoutsider.betterdogs.harness.SimulatedEntityGroup;
import net.vanillaoutsider.betterdogs.harness.SocialHandoffHarness;
import net.vanillaoutsider.betterdogs.util.WolfHowlHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PackHowlDeepBehavioralTest: Pillar 4 Headless Deep Behavioral Probing Test Suite for Better Dogs Pack Howling.
 *
 * Implements the studio standard 3-Tier Behavioral Probing:
 * - Tier 1: Multi-Entity Social Handoff Verification (Initiator Howl -> Harmonic Signal -> Follower Stagger & Full Convergence)
 * - Tier 2: Full State-Machine Lifecycle Probing (IDLE -> ALERTED -> ACTIVE -> COOLDOWN -> IDLE)
 * - Tier 3: Negative Parity & Anti-Goal Invariance (invalid states rejected, mid-action & mid-countdown aborts)
 */
@DisplayName("Pillar 4: Deep Behavioral Probing & Social Handoff Harness (Better Dogs Pack Howl)")
public class PackHowlDeepBehavioralTest {

    @Test
    @DisplayName("Tier 1: Multi-Entity Pack Howl Chorus Propagation, Harmonic Pitch & Full Convergence Cascade")
    void testPackChorusHowlPropagationAndStaggeredConvergence() {
        // Setup cluster: 1 initiator + 3 followers at 8m spacing
        BehavioralStepRunner runner = SocialHandoffHarness.setupClusterWithNegativeInvariants(3, 8.0);
        SimulatedEntityGroup group = runner.getGroup();
        SimulatedEntity initiator = group.getInitiator();

        // 1. Initial State: all entities start in IDLE state with default pitch
        runner.assertInitiator(e -> e.getState() == SimulatedEntity.State.IDLE, "Initiator must start IDLE")
                .assertAllFollowers(e -> e.getState() == SimulatedEntity.State.IDLE, "Followers must start IDLE")
                .assertAllEntities(e -> e.getPitch() == 1.0f, "All entities must start with 1.0f baseline pitch");

        // 2. Initiator howls (BASE_HOWL_DURATION = 60 ticks, cooldown = 12000 ticks, pitch = 1.0f)
        boolean initiated = initiator.initiateBehavior("pack_howl", WolfHowlHelper.BASE_HOWL_DURATION, 12000, 1.0f);
        assertTrue(initiated, "Initiator must successfully initiate nocturnal chorus howl");
        assertEquals(SimulatedEntity.State.ACTIVE, initiator.getState());
        assertEquals(60, initiator.getStateTicks());
        assertEquals(1.0f, initiator.getPitch(), 0.001f);

        // 3. Broadcast chorus signal with staggered delays and harmonic pitch variation
        // Follower 0 (offset 0): delay 10 ticks, pitch = calculateHarmonicPitch(0.1f) = 0.885f
        // Follower 1 (offset 6): delay 16 ticks, pitch = calculateHarmonicPitch(0.5f) = 1.025f
        // Follower 2 (offset 12): delay 22 ticks, pitch = calculateHarmonicPitch(0.9f) = 1.165f
        int accepted = group.broadcastSignal(
                initiator,
                "pack_howl",
                WolfHowlHelper.DEFAULT_HOWL_RADIUS, // 24.0 blocks
                idx -> WolfHowlHelper.calculateChorusDelay(idx * 6),
                idx -> WolfHowlHelper.calculateHarmonicPitch(idx * 0.4f + 0.1f),
                WolfHowlHelper.BASE_HOWL_DURATION,
                12000
        );
        assertEquals(3, accepted, "All 3 eligible followers within 24m must accept the chorus howl signal");

        // 4. At tick 0 post-broadcast: followers are in ALERTED state with respective delay countdowns and harmonic pitches
        runner.assertFollower(0, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 10,
                "Follower 0 delay must be 10 ticks (calculateChorusDelay(0))")
                .assertFollower(1, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 16,
                        "Follower 1 delay must be 16 ticks (calculateChorusDelay(6))")
                .assertFollower(2, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 22,
                        "Follower 2 delay must be 22 ticks (calculateChorusDelay(12))");

        // Assert harmonic pitches strictly follow WolfHowlHelper mathematical bounds
        assertEquals(WolfHowlHelper.calculateHarmonicPitch(0.1f), group.getFollowers().get(0).getPitch(), 0.001f);
        assertEquals(WolfHowlHelper.calculateHarmonicPitch(0.5f), group.getFollowers().get(1).getPitch(), 0.001f);
        assertEquals(WolfHowlHelper.calculateHarmonicPitch(0.9f), group.getFollowers().get(2).getPitch(), 0.001f);

        // 5. Step 10 ticks: Follower 0 delay expires and transitions to ACTIVE vocalization
        runner.step(10);
        runner.assertFollower(0, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 60,
                "Follower 0 must transition to ACTIVE howling at tick 10")
                .assertFollower(1, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 6,
                        "Follower 1 must still be ALERTED with 6 ticks remaining")
                .assertFollower(2, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 12,
                        "Follower 2 must still be ALERTED with 12 ticks remaining")
                .assertInitiator(e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 50,
                        "Initiator must still be howling with 50 ticks remaining");

        // 6. Step 6 more ticks (total tick 16): Follower 1 transitions to ACTIVE vocalization
        runner.step(6);
        runner.assertFollower(0, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 54,
                "Follower 0 still howling")
                .assertFollower(1, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 60,
                        "Follower 1 must transition to ACTIVE howling at tick 16")
                .assertFollower(2, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 6,
                        "Follower 2 must still be ALERTED with 6 ticks remaining")
                .assertInitiator(e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 44,
                        "Initiator must still be howling with 44 ticks remaining");

        // 7. Step 6 more ticks (total tick 22): Follower 2 transitions to ACTIVE vocalization
        // FULL CHORUS CONVERGENCE: All 4 entities (initiator + 3 followers) are actively howling together!
        runner.step(6);
        runner.assertAllFollowers(e -> e.getState() == SimulatedEntity.State.ACTIVE,
                "All 3 followers must converge into ACTIVE chorus vocalization by tick 22");
        runner.assertInitiator(e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 38,
                "Initiator must still be howling at convergence with 38 ticks remaining");
        runner.assertTotalCountInState(SimulatedEntity.State.ACTIVE, 4,
                "All 4 entities (initiator + 3 followers) must be howling concurrently at full convergence");

        // 8. Step 38 more ticks (total tick 60):
        // Initiator has howled for 60 ticks and transitions to COOLDOWN!
        runner.step(38);
        runner.assertInitiator(e -> e.getState() == SimulatedEntity.State.COOLDOWN,
                "Initiator must transition to COOLDOWN after 60 ticks of howling");
        runner.assertFollower(0, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 10,
                "Follower 0 still howling (10 ticks remaining)");
        runner.assertFollower(1, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 16,
                "Follower 1 still howling (16 ticks remaining)");
        runner.assertFollower(2, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 22,
                "Follower 2 still howling (22 ticks remaining)");

        // 9. Step 10 more ticks (total tick 70): Follower 0 transitions to COOLDOWN!
        runner.step(10);
        runner.assertFollower(0, e -> e.getState() == SimulatedEntity.State.COOLDOWN,
                "Follower 0 must transition to COOLDOWN after 60 ticks of vocalization");
        runner.assertFollower(1, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 6,
                "Follower 1 still howling (6 ticks remaining)");
        runner.assertFollower(2, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 12,
                "Follower 2 still howling (12 ticks remaining)");

        // 10. Step 6 more ticks (total tick 76): Follower 1 transitions to COOLDOWN!
        runner.step(6);
        runner.assertFollower(1, e -> e.getState() == SimulatedEntity.State.COOLDOWN,
                "Follower 1 must transition to COOLDOWN after 60 ticks of vocalization");
        runner.assertFollower(2, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 6,
                "Follower 2 still howling (6 ticks remaining)");

        // 11. Step 6 more ticks (total tick 82): Follower 2 transitions to COOLDOWN!
        // COMPLETE CASCADE RESOLUTION: All 4 entities have cleanly completed vocalization
        runner.step(6);
        runner.assertFollower(2, e -> e.getState() == SimulatedEntity.State.COOLDOWN,
                "Follower 2 must transition to COOLDOWN after 60 ticks of vocalization");
        runner.assertTotalCountInState(SimulatedEntity.State.ACTIVE, 0,
                "Zero entities must be actively howling after tick 82");
        runner.assertTotalCountInState(SimulatedEntity.State.COOLDOWN, 4,
                "All 4 entities must be cleanly in COOLDOWN state");
    }

    @Test
    @DisplayName("Tier 2: Full State-Machine Lifecycle Probing (Idle -> Alerted -> Active -> Cooldown -> Reset)")
    void testFullChorusLifecycleProbing() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(1, 6.0);
        SimulatedEntity initiator = runner.getGroup().getInitiator();
        SimulatedEntity follower = runner.getGroup().getFollowers().get(0);

        initiator.initiateBehavior("pack_howl", 60, 100);
        int delay = WolfHowlHelper.calculateChorusDelay(4); // 10 + 4 = 14 ticks
        assertEquals(14, delay);

        int accepted = runner.getGroup().broadcastSignal(
                initiator,
                "pack_howl",
                WolfHowlHelper.DEFAULT_HOWL_RADIUS,
                idx -> delay,
                60,
                50 // 50 ticks cooldown for quick test assertion
        );
        assertEquals(1, accepted);

        // State 1: ALERTED (countdown = 14 ticks)
        assertEquals(SimulatedEntity.State.ALERTED, follower.getState());
        assertEquals(14, follower.getDelayTicks());

        // State 2: Transition to ACTIVE at tick 14
        runner.step(14);
        assertEquals(SimulatedEntity.State.ACTIVE, follower.getState());
        assertEquals(60, follower.getStateTicks());

        // State 3: Transition to COOLDOWN at tick 74 (14 + 60)
        runner.step(60);
        assertEquals(SimulatedEntity.State.COOLDOWN, follower.getState());
        assertEquals(50, follower.getCooldownTicks());

        // State 4: Transition to IDLE at tick 124 (74 + 50)
        runner.step(50);
        assertEquals(SimulatedEntity.State.IDLE, follower.getState());
        assertNull(follower.getActiveSignal(), "Active signal must be null after clean full-lifecycle reset");
    }

    @Test
    @DisplayName("Tier 3: Negative Parity & Anti-Goal Invariance (All Invalid States Strictly Reject Signal)")
    void testNegativeParityInvalidStateRejections() {
        // Setup 9 followers with continuous negative invariant watchdogs
        BehavioralStepRunner runner = SocialHandoffHarness.setupClusterWithNegativeInvariants(9, 6.0);
        SimulatedEntityGroup group = runner.getGroup();

        SimulatedEntity normalFollower = group.getFollowers().get(0);
        SimulatedEntity sittingFollower = group.getFollowers().get(1);
        SimulatedEntity manualSittingFollower = group.getFollowers().get(2);
        SimulatedEntity combatFollower = group.getFollowers().get(3);
        SimulatedEntity leashedFollower = group.getFollowers().get(4);
        SimulatedEntity babyFollower = group.getFollowers().get(5);
        SimulatedEntity quietFollower = group.getFollowers().get(6);
        SimulatedEntity guardFollower = group.getFollowers().get(7);
        SimulatedEntity untamedFollower = group.getFollowers().get(8);

        // Apply invalid negative states
        sittingFollower.setSitting(true);
        manualSittingFollower.setSittingManually(true);
        combatFollower.setInCombat(true);
        leashedFollower.setLeashed(true);
        babyFollower.setBaby(true);
        quietFollower.setQuiet(true);
        guardFollower.setGuardMode(true);
        untamedFollower.setTame(false);

        SimulatedEntity initiator = group.getInitiator();
        initiator.initiateBehavior("pack_howl", 60, 12000);

        int accepted = group.broadcastSignal(
                initiator,
                "pack_howl",
                WolfHowlHelper.DEFAULT_HOWL_RADIUS,
                10,
                0
        );

        // Anti-Goal: Exactly 1 entity (normalFollower) accepted the chorus signal
        assertEquals(1, accepted, "Only available, non-disqualified packmates must accept chorus signals");
        assertEquals(SimulatedEntity.State.ALERTED, normalFollower.getState());

        // All disqualified wolves remain strictly IDLE
        assertEquals(SimulatedEntity.State.IDLE, sittingFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, manualSittingFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, combatFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, leashedFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, babyFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, quietFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, guardFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, untamedFollower.getState());

        // Advance simulation: continuous invariant watchdogs enforce 0 violations on every tick
        runner.step(25);
        assertEquals(SimulatedEntity.State.ACTIVE, normalFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, sittingFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, manualSittingFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, combatFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, leashedFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, babyFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, quietFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, guardFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, untamedFollower.getState());
    }

    @Test
    @DisplayName("Tier 3: Mid-Action Interruption Aborts Howl Immediately (Sitting, Combat, Guard, Leash, Death, Untamed)")
    void testMidActionInterruptionAbortsChorusInstantly() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(6, 5.0);
        SimulatedEntity initiator = runner.getGroup().getInitiator();
        initiator.initiateBehavior("pack_howl", 60, 12000);
        runner.getGroup().broadcastSignal(initiator, "pack_howl", 24.0, 5, 0);

        runner.step(5);
        runner.assertAllFollowers(e -> e.getState() == SimulatedEntity.State.ACTIVE, "All followers actively howling");

        SimulatedEntity sitWolf = runner.getGroup().getFollowers().get(0);
        SimulatedEntity combatWolf = runner.getGroup().getFollowers().get(1);
        SimulatedEntity guardWolf = runner.getGroup().getFollowers().get(2);
        SimulatedEntity leashWolf = runner.getGroup().getFollowers().get(3);
        SimulatedEntity deadWolf = runner.getGroup().getFollowers().get(4);
        SimulatedEntity untamedWolf = runner.getGroup().getFollowers().get(5);

        // Apply distinct interruptions mid-action
        sitWolf.setSitting(true);
        combatWolf.setInCombat(true);
        guardWolf.setGuardMode(true);
        leashWolf.setLeashed(true);
        deadWolf.setDead(true);
        untamedWolf.setTame(false);

        // Advance 1 tick: all interrupted wolves immediately abort to IDLE with 0 remaining ticks
        runner.step(1);
        runner.assertAllFollowers(e -> e.getState() == SimulatedEntity.State.IDLE,
                "All interrupted wolves must instantly abort mid-action howling to IDLE");
        runner.assertAllFollowers(e -> e.getStateTicks() == 0 && e.getActiveSignal() == null && e.getPitch() == 1.0f,
                "All interrupted wolves must reset ticks, active signal, and pitch cleanly");
    }

    @Test
    @DisplayName("Tier 3: Mid-Countdown Interruption Aborts ALERTED State Instantly (Before Vocalization Begins)")
    void testMidCountdownInterruptionAbortsAlertedStateInstantly() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(5, 5.0);
        SimulatedEntity initiator = runner.getGroup().getInitiator();
        initiator.initiateBehavior("pack_howl", 60, 12000);

        // Broadcast with 20 ticks delay
        runner.getGroup().broadcastSignal(initiator, "pack_howl", 24.0, 20, 0);
        runner.assertAllFollowers(e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 20,
                "All followers must start in ALERTED state with 20 ticks delay");

        // Step 10 ticks into countdown (10 ticks remaining)
        runner.step(10);
        runner.assertAllFollowers(e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 10,
                "All followers still counting down delay");

        SimulatedEntity sitWolf = runner.getGroup().getFollowers().get(0);
        SimulatedEntity combatWolf = runner.getGroup().getFollowers().get(1);
        SimulatedEntity guardWolf = runner.getGroup().getFollowers().get(2);
        SimulatedEntity leashWolf = runner.getGroup().getFollowers().get(3);
        SimulatedEntity deadWolf = runner.getGroup().getFollowers().get(4);

        // Interrupt during delay window
        sitWolf.setSitting(true);
        combatWolf.setInCombat(true);
        guardWolf.setGuardMode(true);
        leashWolf.setLeashed(true);
        deadWolf.setDead(true);

        // Next tick: all interrupted followers abort to IDLE immediately without ever transitioning to ACTIVE
        runner.step(1);
        runner.assertAllFollowers(e -> e.getState() == SimulatedEntity.State.IDLE,
                "Interruption during delay countdown must immediately abort to IDLE");
        runner.assertAllFollowers(e -> e.getDelayTicks() == 0 && e.getStateTicks() == 0,
                "Delay and state ticks must be reset to 0");
    }

    @Test
    @DisplayName("Pillar 4: Duplicate Broadcast Immunity During Active Delay Countdown")
    void testDuplicateBroadcastImmunityDuringDelayCountdown() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(1, 5.0);
        SimulatedEntity follower = runner.getGroup().getFollowers().get(0);
        SimulatedEntity initiator = runner.getGroup().getInitiator();

        initiator.initiateBehavior("pack_howl", 60, 12000);
        int acceptedFirst = runner.getGroup().broadcastSignal(initiator, "pack_howl", 24.0, 20, 0);
        assertEquals(1, acceptedFirst);
        assertEquals(SimulatedEntity.State.ALERTED, follower.getState());
        assertEquals(20, follower.getDelayTicks());

        // Step 5 ticks: delay down to 15
        runner.step(5);
        assertEquals(15, follower.getDelayTicks());

        // Secondary broadcast occurs from another nearby wolf
        int acceptedSecond = runner.getGroup().broadcastSignal(initiator, "pack_howl", 24.0, 10, 0);
        assertEquals(0, acceptedSecond, "Alerted follower must REJECT duplicate broadcast to prevent clobbering");
        assertEquals(15, follower.getDelayTicks(), "Existing delay countdown must NOT be modified by duplicate signal");
    }

    @Test
    @DisplayName("Pillar 4: Duplicate Broadcast Immunity During Active Vocalization (Sound Stacking Prevention)")
    void testDuplicateBroadcastImmunityDuringActiveVocalization() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(1, 5.0);
        SimulatedEntity follower = runner.getGroup().getFollowers().get(0);
        SimulatedEntity initiator = runner.getGroup().getInitiator();

        initiator.initiateBehavior("pack_howl", 60, 12000);
        runner.getGroup().broadcastSignal(initiator, "pack_howl", 24.0, 5, 0);

        // Step 5 ticks: follower transitions to ACTIVE howling
        runner.step(5);
        assertEquals(SimulatedEntity.State.ACTIVE, follower.getState());
        assertEquals(60, follower.getStateTicks());

        // Step 30 ticks: mid-howl (30 ticks remaining)
        runner.step(30);
        assertEquals(30, follower.getStateTicks());

        // Secondary broadcast occurs while actively howling
        int acceptedSecond = runner.getGroup().broadcastSignal(initiator, "pack_howl", 24.0, 10, 0);
        assertEquals(0, acceptedSecond, "Actively howling wolf must REJECT duplicate broadcast to prevent sound stacking");
        assertEquals(30, follower.getStateTicks(), "Active vocalization timer must not be clobbered by duplicate broadcast");
        assertEquals(SimulatedEntity.State.ACTIVE, follower.getState());
    }

    @Test
    @DisplayName("Pillar 4: Spatial Distance Boundary Precision (24.0m Radius Threshold)")
    void testSpatialDistanceBoundaryPrecision() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(2, 10.0);
        SimulatedEntity exactBoundaryFollower = runner.getGroup().getFollowers().get(0);
        SimulatedEntity beyondBoundaryFollower = runner.getGroup().getFollowers().get(1);

        // Place exact boundary follower at exactly 24.0m (24, 64, 0)
        exactBoundaryFollower.setPosition(24.0, 64.0, 0.0);

        // Place beyond boundary follower at 24.01m (24.01, 64, 0)
        beyondBoundaryFollower.setPosition(24.01, 64.0, 0.0);

        runner.getGroup().getInitiator().initiateBehavior("pack_howl", 60, 12000);

        int accepted = runner.getGroup().broadcastSignal(
                runner.getGroup().getInitiator(),
                "pack_howl",
                WolfHowlHelper.DEFAULT_HOWL_RADIUS, // 24.0 blocks
                10,
                0
        );

        assertEquals(1, accepted, "Only entity within or at exact 24.0m radius must accept signal");
        assertEquals(SimulatedEntity.State.ALERTED, exactBoundaryFollower.getState(), "Entity at 24.0m must accept");
        assertEquals(SimulatedEntity.State.IDLE, beyondBoundaryFollower.getState(), "Entity at 24.01m must reject");
    }

    @Test
    @DisplayName("Pillar 4: Initiator Mid-Howl Abortion Decoupled From Follower Vocalization")
    void testInitiatorMidHowlAbortionDecoupledFromFollowers() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(1, 5.0);
        SimulatedEntity initiator = runner.getGroup().getInitiator();
        SimulatedEntity follower = runner.getGroup().getFollowers().get(0);

        initiator.initiateBehavior("pack_howl", 60, 12000);
        runner.getGroup().broadcastSignal(initiator, "pack_howl", 24.0, 10, 0);

        // Step 5 ticks: initiator is ACTIVE, follower is ALERTED
        runner.step(5);
        assertEquals(SimulatedEntity.State.ACTIVE, initiator.getState());
        assertEquals(SimulatedEntity.State.ALERTED, follower.getState());

        // Initiator takes combat damage or is commanded to sit
        initiator.setInCombat(true);

        // Next tick: initiator aborts to IDLE immediately
        runner.step(1);
        assertEquals(SimulatedEntity.State.IDLE, initiator.getState(), "Initiator in combat must abort to IDLE");

        // Follower's independently scheduled chorus countdown continues unimpeded
        assertEquals(SimulatedEntity.State.ALERTED, follower.getState(), "Follower must maintain chorus countdown independently");
        assertEquals(4, follower.getDelayTicks(), "Follower remaining delay must be 4 ticks");

        // Advance 4 more ticks: follower transitions to ACTIVE howling
        runner.step(4);
        assertEquals(SimulatedEntity.State.ACTIVE, follower.getState(), "Follower must smoothly transition to ACTIVE chorus");
    }

    @Test
    @DisplayName("Pillar 4: Solo Howl Initiator with Zero Receptive Followers")
    void testSoloHowlZeroReceptiveFollowers() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(0, 0.0);
        SimulatedEntity initiator = runner.getGroup().getInitiator();

        boolean initiated = initiator.initiateBehavior("pack_howl", 60, 12000);
        assertTrue(initiated);

        int accepted = runner.getGroup().broadcastSignal(initiator, "pack_howl", 24.0, 10, 0);
        assertEquals(0, accepted, "Zero followers in cluster must result in 0 accepted signals");

        // Initiator completes full solo howl of 60 ticks without throwing
        runner.step(60);
        assertEquals(SimulatedEntity.State.COOLDOWN, initiator.getState(), "Solo initiator must transition to COOLDOWN");
        assertEquals(12000, initiator.getCooldownTicks());
    }

    @Test
    @DisplayName("Tier 3: Watchdog Catches Sitting Wolf Inadvertently Entering ALERTED State")
    void testWatchdogCatchesSittingWolfInAlertedState() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupClusterWithNegativeInvariants(1, 5.0);
        SimulatedEntity follower = runner.getGroup().getFollowers().get(0);
        follower.setSitting(true);

        // Simulate a broken implementation that erroneously pushes state without checking eligibility
        follower.setState(SimulatedEntity.State.ALERTED);
        follower.setCustomTickHook(e -> {}); // retains invalid state

        AssertionError error = assertThrows(AssertionError.class, () -> runner.step(1),
                "Continuous watchdog must immediately catch sitting entity in ALERTED state");
        assertTrue(error.getMessage().contains("Sitting entities must never enter ACTIVE or ALERTED state"),
                "Error message must cite negative invariant watchdog");
    }

    @Test
    @DisplayName("Tier 3: evaluateInvariants Evaluates Pre-Existing Invalid State at Tick Zero")
    void testEvaluateInvariantsCatchesPreExistingInvalidState() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupClusterWithNegativeInvariants(1, 5.0);
        SimulatedEntity follower = runner.getGroup().getFollowers().get(0);
        follower.setSitting(true);
        follower.setState(SimulatedEntity.State.ACTIVE);

        // Immediate evaluation before any step ticks occur
        AssertionError error = assertThrows(AssertionError.class, runner::evaluateInvariants,
                "evaluateInvariants must catch pre-existing invariant violations at tick 0");
        assertTrue(error.getMessage().contains("Sitting entities must never enter ACTIVE or ALERTED state"));
    }
}
