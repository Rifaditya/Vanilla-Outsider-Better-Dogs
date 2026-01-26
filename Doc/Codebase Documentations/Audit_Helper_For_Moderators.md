# Audit Helper for Moderators

This document provides technical justification for patterns in the "Better Dogs" mod that might appear suspicious or unconventional during platform review.

## v3.1.14: Dynamic Simulation Capping

This version introduces dynamic adjustment of AI ranges based on the server's simulation distance to prevent dogs from becoming stranded in unloaded chunks.

### [AI] Periodic Simulation Distance Queries

**Files**: [PersonalityFollowOwnerGoal.java](file:///e:/Minecraft%20Project/Vanilla%20Outsider%20Collections/Better%20Dogs/src/main/java/net/vanillaoutsider/betterdogs/ai/PersonalityFollowOwnerGoal.java), [AggressiveTargetGoal.java](file:///e:/Minecraft%20Project/Vanilla%20Outsider%20Collections/Better%20Dogs/src/main/java/net/vanillaoutsider/betterdogs/ai/AggressiveTargetGoal.java)

**Logic**: The AI goals now periodically query `owner.level().getServer().getPlayerList().getSimulationDistance()`.
**Justification**:

- **Safety**: This prevents dogs (especially aggressive ones with high detection ranges) from attempting to pathfind into or target mobs within unloaded chunks, which can lead to AI hang-ups or dogs getting lost.
- **Performance**: To avoid per-tick overhead, the value is cached and only refreshed every 100 ticks (5 seconds). Null-safety guards are implemented for server/owner state transitions.
- **Dynamic Adaptability**: This ensures the mod remains stable even if server administrators change simulation distance settings during runtime.

### [AI] Increased Pathfinding Preference

**Files**: [PersonalityFollowOwnerGoal.java](file:///e:/Minecraft%20Project/Vanilla%20Outsider%20Collections/Better%20Dogs/src/main/java/net/vanillaoutsider/betterdogs/ai/PersonalityFollowOwnerGoal.java)

**Logic**: Dogs now wait until they are `2x` their follow-start distance before teleporting.
**Justification**: This encourages natural movement and "running back" behavior, reducing "vibrating" teleportation artifacts while maintaining a safety net for long-distance owners.
