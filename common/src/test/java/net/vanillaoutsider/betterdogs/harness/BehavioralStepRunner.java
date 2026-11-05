// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.harness;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * BehavioralStepRunner: Utility to advance entity ticks and assert state transitions over time
 * without spinning up full OpenGL or world chunks.
 *
 * Supports timed step advances, per-tick watchdog invariants, and fluent assertion chaining.
 */
public class BehavioralStepRunner {

    private final SimulatedEntityGroup group;
    private int currentTick = 0;
    private final List<Consumer<Integer>> tickListeners = new ArrayList<>();
    private final List<InvariantCheck> invariants = new ArrayList<>();

    public record InvariantCheck(String description, Predicate<SimulatedEntityGroup> check) {}

    public BehavioralStepRunner(SimulatedEntityGroup group) {
        this.group = Objects.requireNonNull(group, "group cannot be null");
    }

    public static BehavioralStepRunner of(SimulatedEntityGroup group) {
        return new BehavioralStepRunner(group);
    }

    public BehavioralStepRunner addInvariant(String description, Predicate<SimulatedEntityGroup> check) {
        invariants.add(new InvariantCheck(description, check));
        return this;
    }

    public BehavioralStepRunner onTick(Consumer<Integer> listener) {
        tickListeners.add(listener);
        return this;
    }

    public BehavioralStepRunner step() {
        return step(1);
    }

    /**
     * Advances simulation by N discrete ticks.
     * Evaluates all registered invariant watchdogs on every tick.
     */
    public BehavioralStepRunner step(int ticks) {
        if (ticks < 0) {
            throw new IllegalArgumentException("Ticks cannot be negative: " + ticks);
        }
        for (int i = 0; i < ticks; i++) {
            currentTick++;
            group.tickAll();

            for (Consumer<Integer> listener : tickListeners) {
                listener.accept(currentTick);
            }

            // Continuous Invariant Watchdog
            for (InvariantCheck inv : invariants) {
                if (!inv.check().test(group)) {
                    throw new AssertionError("Invariant violated at tick " + currentTick + ": " + inv.description());
                }
            }
        }
        return this;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public SimulatedEntityGroup getGroup() {
        return group;
    }

    public BehavioralStepRunner assertEntity(SimulatedEntity entity, Predicate<SimulatedEntity> predicate, String failureMessage) {
        if (!predicate.test(entity)) {
            throw new AssertionError("Assertion failed at tick " + currentTick + " for " + entity.getRole() + ": " + failureMessage);
        }
        return this;
    }

    public BehavioralStepRunner assertInitiator(Predicate<SimulatedEntity> predicate, String failureMessage) {
        return assertEntity(group.getInitiator(), predicate, failureMessage);
    }

    public BehavioralStepRunner assertAllFollowers(Predicate<SimulatedEntity> predicate, String failureMessage) {
        for (SimulatedEntity follower : group.getFollowers()) {
            if (!predicate.test(follower)) {
                throw new AssertionError("Assertion failed at tick " + currentTick + " for follower " + follower.getRole() + ": " + failureMessage);
            }
        }
        return this;
    }

    public BehavioralStepRunner assertFollower(int index, Predicate<SimulatedEntity> predicate, String failureMessage) {
        List<SimulatedEntity> followers = group.getFollowers();
        if (index < 0 || index >= followers.size()) {
            throw new IndexOutOfBoundsException("Invalid follower index: " + index + " (total: " + followers.size() + ")");
        }
        return assertEntity(followers.get(index), predicate, failureMessage);
    }

    /**
     * Immediately evaluates all registered invariant watchdogs against current state.
     */
    public BehavioralStepRunner evaluateInvariants() {
        for (InvariantCheck inv : invariants) {
            if (!inv.check().test(group)) {
                throw new AssertionError("Invariant violated at tick " + currentTick + ": " + inv.description());
            }
        }
        return this;
    }

    public BehavioralStepRunner assertAllEntities(Predicate<SimulatedEntity> predicate, String failureMessage) {
        for (SimulatedEntity entity : group.getAllEntities()) {
            if (!predicate.test(entity)) {
                throw new AssertionError("Assertion failed at tick " + currentTick + " for " + entity.getRole() + ": " + failureMessage);
            }
        }
        return this;
    }

    public BehavioralStepRunner assertFollowersCountInState(SimulatedEntity.State state, long expectedCount, String failureMessage) {
        long actual = group.getFollowers().stream().filter(e -> e.getState() == state).count();
        if (actual != expectedCount) {
            throw new AssertionError("Assertion failed at tick " + currentTick + ": " + failureMessage + " (expected: " + expectedCount + ", actual: " + actual + ")");
        }
        return this;
    }

    public BehavioralStepRunner assertTotalCountInState(SimulatedEntity.State state, long expectedCount, String failureMessage) {
        long actual = group.getAllEntities().stream().filter(e -> e.getState() == state).count();
        if (actual != expectedCount) {
            throw new AssertionError("Assertion failed at tick " + currentTick + ": " + failureMessage + " (expected: " + expectedCount + ", actual: " + actual + ")");
        }
        return this;
    }
}
