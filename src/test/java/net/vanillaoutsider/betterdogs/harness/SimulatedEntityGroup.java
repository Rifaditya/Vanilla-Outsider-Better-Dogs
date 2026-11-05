// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.harness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * SimulatedEntityGroup: Helper to spawn and manage in-memory clusters of entities
 * (1 initiator + N followers) for headless multi-entity social handoff verification.
 */
public class SimulatedEntityGroup {

    private final SimulatedEntity initiator;
    private final List<SimulatedEntity> followers;
    private final List<SimulatedEntity> allEntities;

    public SimulatedEntityGroup(SimulatedEntity initiator, List<SimulatedEntity> followers) {
        this.initiator = Objects.requireNonNull(initiator, "initiator cannot be null");
        this.followers = Collections.unmodifiableList(new ArrayList<>(followers));
        List<SimulatedEntity> all = new ArrayList<>();
        all.add(initiator);
        all.addAll(followers);
        this.allEntities = Collections.unmodifiableList(all);
    }

    /**
     * Factory method creating a cluster with 1 initiator placed at origin (0, 64, 0)
     * and N followers positioned radially at the specified spacing radius.
     */
    public static SimulatedEntityGroup create(int followerCount, double followerSpacing) {
        SimulatedEntity init = new SimulatedEntity(UUID.randomUUID(), "initiator", 0.0, 64.0, 0.0);
        List<SimulatedEntity> followers = new ArrayList<>();
        for (int i = 0; i < followerCount; i++) {
            double angle = (2.0 * Math.PI / Math.max(1, followerCount)) * i;
            double fx = Math.cos(angle) * followerSpacing;
            double fz = Math.sin(angle) * followerSpacing;
            followers.add(new SimulatedEntity(UUID.randomUUID(), "follower_" + i, fx, 64.0, fz));
        }
        return new SimulatedEntityGroup(init, followers);
    }

    public SimulatedEntity getInitiator() {
        return initiator;
    }

    public List<SimulatedEntity> getFollowers() {
        return followers;
    }

    public List<SimulatedEntity> getAllEntities() {
        return allEntities;
    }

    /**
     * Broadcasts a social signal from a source entity to all group members within radius.
     * Receptive members transition into ALERTED state with staggered delays.
     *
     * @param source The signal emitting entity
     * @param signal Signal identifier (e.g. "pack_howl", "alert")
     * @param radius Maximum effective broadcast radius in blocks
     * @param baseDelay Base delay in ticks before followers activate
     * @param perEntityStagger Additional delay per entity index to simulate organic staggered handoff
     * @return Number of followers that successfully accepted the signal
     */
    public int broadcastSignal(SimulatedEntity source, String signal, double radius, int baseDelay, int perEntityStagger) {
        return broadcastSignal(source, signal, radius, idx -> baseDelay + (idx * perEntityStagger), 60, 12000);
    }

    /**
     * Broadcasts a social signal with dynamic per-entity delay calculator function.
     */
    public int broadcastSignal(SimulatedEntity source, String signal, double radius, ToIntFunction<Integer> delayCalculator, int activeDuration, int cooldownAfter) {
        int acceptedCount = 0;
        int followerIdx = 0;
        for (SimulatedEntity entity : allEntities) {
            if (entity.equals(source)) {
                continue;
            }
            if (source.distanceTo(entity) <= radius) {
                int staggeredDelay = delayCalculator.applyAsInt(followerIdx);
                if (entity.receiveSignal(signal, staggeredDelay, activeDuration, cooldownAfter)) {
                    acceptedCount++;
                }
                followerIdx++;
            }
        }
        return acceptedCount;
    }

    /**
     * Ticks every entity in the group by 1 game tick.
     */
    public void tickAll() {
        for (SimulatedEntity entity : allEntities) {
            entity.tick();
        }
    }

    public void forEach(Consumer<SimulatedEntity> action) {
        allEntities.forEach(action);
    }

    public List<SimulatedEntity> findByState(SimulatedEntity.State state) {
        return allEntities.stream()
                .filter(e -> e.getState() == state)
                .collect(Collectors.toList());
    }

    public long countInState(SimulatedEntity.State state) {
        return allEntities.stream()
                .filter(e -> e.getState() == state)
                .count();
    }
}
