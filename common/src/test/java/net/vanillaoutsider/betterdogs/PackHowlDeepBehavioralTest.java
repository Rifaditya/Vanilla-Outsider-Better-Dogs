// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
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
 * - Tier 1: Multi-Entity Social Handoff Verification (Initiator Howl -> Chorus Signal -> Staggered Follower Convergence)
 * - Tier 2: Full State-Machine Lifecycle Probing (IDLE -> ALERTED -> ACTIVE -> COOLDOWN -> IDLE)
 * - Tier 3: Negative Parity & Anti-Goal Invariance (sitting, leashed, combat, baby, quiet, guard-mode rejections & mid-action aborts)
 */
@DisplayName("Pillar 4: Deep Behavioral Probing & Social Handoff Harness (Better Dogs Pack Howl)")
public class PackHowlDeepBehavioralTest {

    @Test
    @DisplayName("Tier 1: Multi-Entity Pack Howl Chorus Propagation & Staggered Convergence")
    void testPackChorusHowlPropagationAndStaggeredConvergence() {
        // Setup cluster: 1 initiator + 3 followers at 8m spacing
        BehavioralStepRunner runner = SocialHandoffHarness.setupClusterWithNegativeInvariants(3, 8.0);
        SimulatedEntityGroup group = runner.getGroup();
        SimulatedEntity initiator = group.getInitiator();

        // 1. Initial State: all entities start in IDLE state
        runner.assertInitiator(e -> e.getState() == SimulatedEntity.State.IDLE, "Initiator must start IDLE")
                .assertAllFollowers(e -> e.getState() == SimulatedEntity.State.IDLE, "Followers must start IDLE");

        // 2. Initiator howls (BASE_HOWL_DURATION = 60 ticks, cooldown = 12000 ticks)
        boolean initiated = initiator.initiateBehavior("pack_howl", WolfHowlHelper.BASE_HOWL_DURATION, 12000);
        assertTrue(initiated, "Initiator must successfully initiate nocturnal chorus howl");
        assertEquals(SimulatedEntity.State.ACTIVE, initiator.getState());
        assertEquals(60, initiator.getStateTicks());

        // 3. Broadcast chorus signal to pack cluster using WolfHowlHelper constants
        // Staggered delays using WolfHowlHelper.calculateChorusDelay:
        // Follower 0 (offset 0): 10 ticks
        // Follower 1 (offset 6): 16 ticks
        // Follower 2 (offset 12): 22 ticks
        int accepted = group.broadcastSignal(
                initiator,
                "pack_howl",
                WolfHowlHelper.DEFAULT_HOWL_RADIUS, // 24.0 blocks
                idx -> WolfHowlHelper.calculateChorusDelay(idx * 6),
                WolfHowlHelper.BASE_HOWL_DURATION,
                12000
        );
        assertEquals(3, accepted, "All 3 eligible followers within 24m must accept the chorus howl signal");

        // 4. At tick 0 post-broadcast: followers are in ALERTED state with respective delay countdowns
        runner.assertFollower(0, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 10,
                "Follower 0 delay must be 10 ticks (calculateChorusDelay(0))")
                .assertFollower(1, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 16,
                        "Follower 1 delay must be 16 ticks (calculateChorusDelay(6))")
                .assertFollower(2, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 22,
                        "Follower 2 delay must be 22 ticks (calculateChorusDelay(12))");

        // 5. Step 10 ticks: Follower 0 delay expires and transitions to ACTIVE vocalization
        runner.step(10);
        runner.assertFollower(0, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 60,
                "Follower 0 must transition to ACTIVE howling at tick 10")
                .assertFollower(1, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 6,
                        "Follower 1 must still be ALERTED with 6 ticks remaining")
                .assertFollower(2, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 12,
                        "Follower 2 must still be ALERTED with 12 ticks remaining");

        // 6. Step 6 more ticks (total tick 16): Follower 1 transitions to ACTIVE vocalization
        runner.step(6);
        runner.assertFollower(0, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 54,
                "Follower 0 still howling")
                .assertFollower(1, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 60,
                        "Follower 1 must transition to ACTIVE howling at tick 16")
                .assertFollower(2, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 6,
                        "Follower 2 must still be ALERTED with 6 ticks remaining");

        // 7. Step 6 more ticks (total tick 22): Follower 2 transitions to ACTIVE vocalization
        // FULL CHORUS CONVERGENCE: All 3 followers are actively howling together!
        runner.step(6);
        runner.assertAllFollowers(e -> e.getState() == SimulatedEntity.State.ACTIVE,
                "All 3 followers must converge into ACTIVE chorus vocalization by tick 22");
        runner.assertFollowersCountInState(SimulatedEntity.State.ACTIVE, 3,
                "Exactly 3 followers must be howling concurrently");

        // 8. Step 48 more ticks (total tick 70):
        // Follower 0 has howled for 60 ticks (ticks 11 to 70 inclusive) and transitions to COOLDOWN!
        runner.step(48);
        runner.assertFollower(0, e -> e.getState() == SimulatedEntity.State.COOLDOWN,
                "Follower 0 must transition to COOLDOWN after 60 ticks of howling")
                .assertFollower(1, e -> e.getState() == SimulatedEntity.State.ACTIVE,
                        "Follower 1 still howling (6 ticks remaining)")
                .assertFollower(2, e -> e.getState() == SimulatedEntity.State.ACTIVE,
                        "Follower 2 still howling (12 ticks remaining)");
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
        // Setup 8 followers with continuous negative invariant watchdogs
        BehavioralStepRunner runner = SocialHandoffHarness.setupClusterWithNegativeInvariants(8, 6.0);
        SimulatedEntityGroup group = runner.getGroup();

        SimulatedEntity normalFollower = group.getFollowers().get(0);
        SimulatedEntity sittingFollower = group.getFollowers().get(1);
        SimulatedEntity manualSittingFollower = group.getFollowers().get(2);
        SimulatedEntity combatFollower = group.getFollowers().get(3);
        SimulatedEntity leashedFollower = group.getFollowers().get(4);
        SimulatedEntity babyFollower = group.getFollowers().get(5);
        SimulatedEntity quietFollower = group.getFollowers().get(6);
        SimulatedEntity guardFollower = group.getFollowers().get(7);

        // Apply invalid negative states
        sittingFollower.setSitting(true);
        manualSittingFollower.setSittingManually(true);
        combatFollower.setInCombat(true);
        leashedFollower.setLeashed(true);
        babyFollower.setBaby(true);
        quietFollower.setQuiet(true);
        guardFollower.setGuardMode(true);

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
    }

    @Test
    @DisplayName("Tier 3: Mid-Action Interruption Aborts Howl Immediately (Sitting Command)")
    void testMidActionSittingAbortsChorusInstantly() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(1, 5.0);
        SimulatedEntity follower = runner.getGroup().getFollowers().get(0);

        runner.getGroup().getInitiator().initiateBehavior("pack_howl", 60, 12000);
        runner.getGroup().broadcastSignal(runner.getGroup().getInitiator(), "pack_howl", 24.0, 5, 0);

        runner.step(5);
        assertEquals(SimulatedEntity.State.ACTIVE, follower.getState(), "Follower must be actively howling");

        // Player commands wolf to sit mid-howl
        follower.setSitting(true);

        // Next tick: Negative parity watchdog immediately aborts howling to IDLE
        runner.step(1);
        assertEquals(SimulatedEntity.State.IDLE, follower.getState(),
                "Sitting mid-howl must instantly abort goal to IDLE without lingering ticks");
        assertEquals(0, follower.getStateTicks(), "Active howling ticks must be reset to 0");
        assertNull(follower.getActiveSignal(), "Active signal must be reset to null");
    }

    @Test
    @DisplayName("Tier 3: Mid-Action Combat Target Acquisition Aborts Howl Immediately")
    void testMidActionCombatEngagementAbortsChorusInstantly() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(1, 5.0);
        SimulatedEntity follower = runner.getGroup().getFollowers().get(0);

        runner.getGroup().getInitiator().initiateBehavior("pack_howl", 60, 12000);
        runner.getGroup().broadcastSignal(runner.getGroup().getInitiator(), "pack_howl", 24.0, 5, 0);

        runner.step(5);
        assertEquals(SimulatedEntity.State.ACTIVE, follower.getState(), "Follower must be actively howling");

        // Hostile mob strikes packmate; wolf acquires target mid-howl
        follower.setInCombat(true);

        // Next tick: AI watchdog immediately aborts howling to IDLE
        runner.step(1);
        assertEquals(SimulatedEntity.State.IDLE, follower.getState(),
                "Entering combat mid-howl must instantly abort goal to IDLE");
        assertEquals(0, follower.getStateTicks());
        assertNull(follower.getActiveSignal());
    }

    @Test
    @DisplayName("Tier 3: Mid-Action Guard Mode Toggle Aborts Howl Immediately")
    void testMidActionGuardModeAbortsChorusInstantly() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(1, 5.0);
        SimulatedEntity follower = runner.getGroup().getFollowers().get(0);

        runner.getGroup().getInitiator().initiateBehavior("pack_howl", 60, 12000);
        runner.getGroup().broadcastSignal(runner.getGroup().getInitiator(), "pack_howl", 24.0, 5, 0);

        runner.step(5);
        assertEquals(SimulatedEntity.State.ACTIVE, follower.getState(), "Follower must be actively howling");

        // Player puts dog into guard mode
        follower.setGuardMode(true);

        // Next tick: Guard mode watchdog immediately aborts howling to IDLE
        runner.step(1);
        assertEquals(SimulatedEntity.State.IDLE, follower.getState(),
                "Guard mode toggle mid-howl must instantly abort goal to IDLE");
        assertEquals(0, follower.getStateTicks());
        assertNull(follower.getActiveSignal());
    }

    @Test
    @DisplayName("Pillar 4: Spatial Distance Culling (DEFAULT_HOWL_RADIUS = 24.0m)")
    void testSpatialDistanceCullingForPackChorus() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(2, 12.0);
        SimulatedEntity nearFollower = runner.getGroup().getFollowers().get(0);
        SimulatedEntity farFollower = runner.getGroup().getFollowers().get(1);

        // Position far follower beyond 24.0m radius (e.g. 32.0m)
        farFollower.setPosition(32.0, 64.0, 0.0);

        runner.getGroup().getInitiator().initiateBehavior("pack_howl", 60, 12000);

        int accepted = runner.getGroup().broadcastSignal(
                runner.getGroup().getInitiator(),
                "pack_howl",
                WolfHowlHelper.DEFAULT_HOWL_RADIUS, // 24.0 blocks
                10,
                0
        );

        assertEquals(1, accepted, "Only near follower within 24m radius must accept signal");
        assertEquals(SimulatedEntity.State.ALERTED, nearFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, farFollower.getState(),
                "Packmate beyond 24m must remain IDLE and not join chorus");
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
}
