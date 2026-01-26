package net.vanillaoutsider.social.core;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The Highlander Control Center: Ensures only one master pulse ticks the global brain.
 */
public class GlobalSocialSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger("HiveMind");
    
    // The Version Handshake: Increment this as the engine evolves
    public static final int ENGINE_VERSION = 100; // v1.0.0
    private static int activeEngineVersion = -1;

    // The Pulse Guard: Idempotent execution
    private static final AtomicLong LAST_TICK = new AtomicLong(-1);
    private static final Random RANDOM = new Random();

    /**
     * The Master Pulse: Finds one random entity in the Hive Mind and ticks its brain.
     */
    public static void pulse(ServerLevel level) {
        long time = level.getGameTime();
        
        // Highlander logic: Only the highest version wins (simplified for now)
        if (activeEngineVersion < ENGINE_VERSION) {
            activeEngineVersion = ENGINE_VERSION;
        }

        // Pulse logic: Only one execution per tick
        if (LAST_TICK.getAndSet(time) == time) return;

        // O(1) Global Selection: Pick one random entity across ALL mods
        List<LivingEntity> entities = SocialRegistry.getTrackedEntities();
        if (entities.isEmpty()) return;

        LivingEntity selection = entities.get(RANDOM.nextInt(entities.size()));
        // Cross-World Safety: Only pulse if in the current ticking level
        if (selection.level() != level) return;

        if (selection instanceof SocialEntity social) {
            EntitySocialScheduler scheduler = social.betterdogs$getScheduler();
            if (scheduler != null) {
                scheduler.tryStartEvent();
            }
        }
    }

    /**
     * Universal Signal Broadcasting
     */
    public static void broadcastSignal(SignalType type, LivingEntity source, Scope scope) {
        // Implementation for instant reactions across entries
    }

    public enum SignalType {
        OWNER_EATING,
        THUNDER,
        DANGER,
        SOCIAL_INVITE
    }

    public enum Scope {
        PUBLIC,
        DIRECTED,
        PRIVATE
    }
}
