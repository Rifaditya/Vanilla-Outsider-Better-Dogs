// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.harness;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * SimulatedEntity: Lightweight in-memory simulated entity for headless behavioral state testing.
 * Operates with 0ms boot overhead and zero OpenGL/chunk dependencies.
 */
public class SimulatedEntity {

    public enum State {
        IDLE,
        ALERTED,
        ACTIVE,
        COOLDOWN
    }

    private final UUID uuid;
    private final String role;
    private double x;
    private double y;
    private double z;

    // Negative Invariant State Flags
    private boolean sitting = false;
    private boolean sittingManually = false;
    private boolean leashed = false;
    private boolean inCombat = false;
    private boolean baby = false;
    private boolean dead = false;
    private boolean quiet = false;
    private boolean guardMode = false;
    private boolean tame = true;

    // Behavioral State Machine
    private State state = State.IDLE;
    private int stateTicks = 0;
    private int delayTicks = 0;
    private int cooldownTicks = 0;
    private String activeSignal = null;
    private Consumer<SimulatedEntity> customTickHook = null;

    public SimulatedEntity(UUID uuid, String role, double x, double y, double z) {
        this.uuid = Objects.requireNonNull(uuid, "uuid cannot be null");
        this.role = Objects.requireNonNull(role, "role cannot be null");
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Consumer<SimulatedEntity> getCustomTickHook() {
        return customTickHook;
    }

    public void setCustomTickHook(Consumer<SimulatedEntity> customTickHook) {
        this.customTickHook = customTickHook;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getRole() {
        return role;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean isSitting() {
        return sitting;
    }

    public void setSitting(boolean sitting) {
        this.sitting = sitting;
    }

    public boolean isSittingManually() {
        return sittingManually;
    }

    public void setSittingManually(boolean sittingManually) {
        this.sittingManually = sittingManually;
    }

    public boolean isLeashed() {
        return leashed;
    }

    public void setLeashed(boolean leashed) {
        this.leashed = leashed;
    }

    public boolean isInCombat() {
        return inCombat;
    }

    public void setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
    }

    public boolean isBaby() {
        return baby;
    }

    public void setBaby(boolean baby) {
        this.baby = baby;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public boolean isGuardMode() {
        return guardMode;
    }

    public void setGuardMode(boolean guardMode) {
        this.guardMode = guardMode;
    }

    public boolean isTame() {
        return tame;
    }

    public void setTame(boolean tame) {
        this.tame = tame;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getStateTicks() {
        return stateTicks;
    }

    public int getDelayTicks() {
        return delayTicks;
    }

    public int getCooldownTicks() {
        return cooldownTicks;
    }

    public String getActiveSignal() {
        return activeSignal;
    }

    /**
     * Checks if the entity is eligible to participate in social behaviors.
     * Enforces negative parity: invalid states immediately disqualify the entity.
     */
    public boolean isAvailableForSocial() {
        return !sitting && !sittingManually && !leashed && !inCombat && !baby && !dead && !quiet && !guardMode && tame;
    }

    /**
     * Attempts to trigger a social signal on this entity with default duration (60) and cooldown (12000).
     */
    public boolean receiveSignal(String signal, int responseDelay) {
        return receiveSignal(signal, responseDelay, 60, 12000);
    }

    /**
     * Attempts to trigger a social signal with configurable duration and cooldown.
     * Rejects if the entity is not available or not idle.
     */
    public boolean receiveSignal(String signal, int responseDelay, int activeDuration, int cooldownAfter) {
        if (!isAvailableForSocial() || state != State.IDLE) {
            return false;
        }
        this.state = State.ALERTED;
        this.delayTicks = Math.max(0, responseDelay);
        this.stateTicks = activeDuration;
        this.cooldownTicks = cooldownAfter;
        this.activeSignal = signal;
        return true;
    }

    /**
     * Triggers active behavioral initiation (e.g. Initiator howls/alerts).
     */
    public boolean initiateBehavior(String signal, int durationTicks, int cooldownAfter) {
        if (!isAvailableForSocial() || state != State.IDLE) {
            return false;
        }
        this.state = State.ACTIVE;
        this.stateTicks = durationTicks;
        this.cooldownTicks = cooldownAfter;
        this.activeSignal = signal;
        return true;
    }

    /**
     * Simulates one game tick of the behavioral state machine.
     * Enforces strict negative parity: if an invalid state occurs mid-execution,
     * the behavior is immediately aborted with zero lingering state leaks.
     */
    public void tick() {
        if (customTickHook != null) {
            customTickHook.accept(this);
            return;
        }

        // Negative Parity Watchdog: abort immediately on disqualifying state
        if (!isAvailableForSocial()) {
            if (state != State.IDLE) {
                abortBehavior();
            }
            return;
        }

        switch (state) {
            case ALERTED -> {
                if (delayTicks > 0) {
                    delayTicks--;
                }
                if (delayTicks <= 0) {
                    state = State.ACTIVE;
                    if (stateTicks <= 0) {
                        stateTicks = 60; // Default active duration
                    }
                }
            }
            case ACTIVE -> {
                if (stateTicks > 0) {
                    stateTicks--;
                }
                if (stateTicks <= 0) {
                    if (cooldownTicks > 0) {
                        state = State.COOLDOWN;
                    } else {
                        state = State.IDLE;
                        activeSignal = null;
                    }
                }
            }
            case COOLDOWN -> {
                if (cooldownTicks > 0) {
                    cooldownTicks--;
                }
                if (cooldownTicks <= 0) {
                    state = State.IDLE;
                    activeSignal = null;
                }
            }
            case IDLE -> {
                // Idle state, waiting for signals
            }
        }
    }

    /**
     * Immediately aborts and resets all active behavioral state.
     */
    public void abortBehavior() {
        this.state = State.IDLE;
        this.stateTicks = 0;
        this.delayTicks = 0;
        this.cooldownTicks = 0;
        this.activeSignal = null;
    }

    /**
     * Calculates 3D Euclidean distance to another entity.
     */
    public double distanceTo(SimulatedEntity other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
