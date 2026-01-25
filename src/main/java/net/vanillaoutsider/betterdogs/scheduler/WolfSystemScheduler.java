package net.vanillaoutsider.betterdogs.scheduler;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.BetterDogs;
import net.vanillaoutsider.betterdogs.WolfExtensions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global centralized scheduler for Wolf AI events.
 * Replaces per-wolf polling with a system that randomly selects wolves to process.
 * Lazy: Only runs if there are wolves. Checks simulation distance.
 */
public class WolfSystemScheduler {

    private static final WolfSystemScheduler INSTANCE = new WolfSystemScheduler();
    
    // Track wolves per dimension (Level -> Set of Wolves)
    // Using WeakHashMap for levels could be safer, but levels are long-lived usually.
    // Using simple Map with cleanup logic.
    private final Map<ServerLevel, Set<Wolf>> trackedWolves = new ConcurrentHashMap<>();

    private WolfSystemScheduler() {}

    public static WolfSystemScheduler get() {
        return INSTANCE;
    }

    /**
     * Called when a wolf is spawned or loaded.
     */
    public void add(Wolf wolf) {
        if (wolf.level() instanceof ServerLevel level) {
            trackedWolves.computeIfAbsent(level, k -> Collections.synchronizedSet(new HashSet<>())).add(wolf);
        }
    }

    /**
     * Called when a wolf is removed or unloaded.
     */
    public void remove(Wolf wolf) {
        if (wolf.level() instanceof ServerLevel level) {
            Set<Wolf> wolves = trackedWolves.get(level);
            if (wolves != null) {
                wolves.remove(wolf);
                if (wolves.isEmpty()) {
                    trackedWolves.remove(level);
                }
            }
        }
    }

    /**
     * Global Tick. Called by ServerLevel tick mixin.
     */
    public void tick(ServerLevel level) {
        Set<Wolf> wolves = trackedWolves.get(level);
        if (wolves == null || wolves.isEmpty()) return;

        // Lazy Logic: Pick 1 random wolf to attempt an event start.
        // This spreads out the load and ensures we don't scan everyone every tick.
        
        // Convert to list for random access (expensive if done every tick? 
        // HashSet iteration is arguably okay if we just take the first one or skip N?
        // For distinct randomness, we might want a better structure, but for now:
        
        // Optimisation: Just iterate and stop at random index?
        // Or cleaner: Maintain a List alongside Set? Set is needed for O(1) remove.
        // Let's do a quick iteration skip.
        
        int size = wolves.size();
        if (size == 0) return;
        
        // 1 attempt per tick? Or 1 attempt per second?
        // If we have 100 wolves, 1 attempt/tick = 20 attempts/sec. 
        // Previously: 1 check/sec per wolf.
        // New: 20 checks/sec total. 
        // This scales better if wolves > 20.
        
        int jump = level.getRandom().nextInt(size);
        Iterator<Wolf> iterator = wolves.iterator();
        Wolf selected = null;
        
        int i = 0;
        while(iterator.hasNext()) {
            Wolf w = iterator.next();
            if (i == jump) {
                selected = w;
                break;
            }
            i++;
        }
        
        if (selected != null && selected.isAlive()) {
            // Lazy: Check Simulation Distance
            if (level.isPositionEntityTicking(selected.blockPosition())) {
                // Attempt to wake up scheduler
                if (selected instanceof WolfExtensions ext) {
                     WolfScheduler scheduler = ext.betterdogs$getScheduler();
                     if (scheduler != null) {
                         scheduler.tryStartEvent(); // "Dawn Check" logic inside here
                     }
                }
            }
        }
    }
}
