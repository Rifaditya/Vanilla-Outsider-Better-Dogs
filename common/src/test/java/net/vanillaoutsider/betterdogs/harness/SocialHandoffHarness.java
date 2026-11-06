// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.harness;

/**
 * SocialHandoffHarness: Standardized test harness for 3-Tier Deep Behavioral Probing in Better Dogs.
 *
 * Tier 1: Multi-Entity Social Handoff Verification (Initiator -> Signal -> Followers)
 * Tier 2: Full State-Machine Lifecycle Probing (Idle -> Alerted -> Active -> Cooldown -> Reset)
 * Tier 3: Negative Parity & Anti-Goal Invariance (sitting, leashed, combat, baby, dead, quiet, guard)
 */
public class SocialHandoffHarness {

    /**
     * Creates a clean test cluster with standard follower spacing.
     */
    public static BehavioralStepRunner setupCluster(int followerCount, double spacing) {
        SimulatedEntityGroup group = SimulatedEntityGroup.create(followerCount, spacing);
        return BehavioralStepRunner.of(group);
    }

    /**
     * Creates a test cluster equipped with continuous negative invariant watchdogs.
     * Guarantees that invalid entities can NEVER enter ACTIVE or ALERTED states at any tick.
     */
    public static BehavioralStepRunner setupClusterWithNegativeInvariants(int followerCount, double spacing) {
        SimulatedEntityGroup group = SimulatedEntityGroup.create(followerCount, spacing);
        return BehavioralStepRunner.of(group)
                .addInvariant("Sitting entities must never enter ACTIVE or ALERTED state", g ->
                        g.getAllEntities().stream().filter(e -> e.isSitting() || e.isSittingManually())
                                .noneMatch(e -> e.getState() == SimulatedEntity.State.ACTIVE || e.getState() == SimulatedEntity.State.ALERTED))
                .addInvariant("Leashed entities must never enter ACTIVE or ALERTED state", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isLeashed)
                                .noneMatch(e -> e.getState() == SimulatedEntity.State.ACTIVE || e.getState() == SimulatedEntity.State.ALERTED))
                .addInvariant("Combat-engaged entities must never enter ACTIVE or ALERTED state", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isInCombat)
                                .noneMatch(e -> e.getState() == SimulatedEntity.State.ACTIVE || e.getState() == SimulatedEntity.State.ALERTED))
                .addInvariant("Baby entities must never enter ACTIVE or ALERTED state", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isBaby)
                                .noneMatch(e -> e.getState() == SimulatedEntity.State.ACTIVE || e.getState() == SimulatedEntity.State.ALERTED))
                .addInvariant("Dead entities must never enter ACTIVE or ALERTED state", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isDead)
                                .noneMatch(e -> e.getState() == SimulatedEntity.State.ACTIVE || e.getState() == SimulatedEntity.State.ALERTED))
                .addInvariant("Quiet trait entities must never enter ACTIVE or ALERTED state", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isQuiet)
                                .noneMatch(e -> e.getState() == SimulatedEntity.State.ACTIVE || e.getState() == SimulatedEntity.State.ALERTED))
                .addInvariant("Guard-mode entities must never enter ACTIVE or ALERTED state", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isGuardMode)
                                .noneMatch(e -> e.getState() == SimulatedEntity.State.ACTIVE || e.getState() == SimulatedEntity.State.ALERTED))
                .addInvariant("Untamed entities must never enter ACTIVE or ALERTED state", g ->
                        g.getAllEntities().stream().filter(e -> !e.isTame())
                                .noneMatch(e -> e.getState() == SimulatedEntity.State.ACTIVE || e.getState() == SimulatedEntity.State.ALERTED));
    }
}
