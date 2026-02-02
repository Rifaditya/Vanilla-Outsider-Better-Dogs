# Audit Helper for Moderators

This document provides technical justification for patterns in the "Better Dogs" mod that might appear suspicious or unconventional during platform review.

## v3.1.20: Native Game Rule Registration

This version introduces the registration of custom Game Rules and a dedicated category within the vanilla Minecraft UI.

### [Registry] Native GameRuleCategory Injection

**Files**: [BetterDogsGameRules.java](registry/BetterDogsGameRules.java)
**Logic**: Uses `GameRuleCategory.register()` to create a dedicated tab for the mod.
**Justification**: This is a standard but advanced use of the Minecraft registry intended to improve user experience by grouping mod-specific world settings together, preventing clutter in the "Mobs" category.

## v3.1.18: lower_snake_case Migration

**Logic**: All Game Rule identifiers were renamed from camelCase to lower_snake_case.
**Justification**: This aligns with the transition to official Mojang standards for 26.x snapshot development, ensuring long-term compatibility with vanilla command systems.

## v3.1.16: WolfMobMixin Redirect

**File**: [WolfMobMixin.java](mixin/WolfMobMixin.java)
**Logic**: Redirects `setTarget` from `LivingEntity` level.
**Justification**: This "wide" injection is used to safely intercept target-setting logic for wolves during social events (Retaliation/Correction) without causing transformation errors on the `Wolf` class itself, which is heavily modified by other mods and vanilla.

## v3.1.14: Dynamic Simulation Capping

**Files**: [PersonalityFollowOwnerGoal.java](ai/PersonalityFollowOwnerGoal.java), [AggressiveTargetGoal.java](ai/AggressiveTargetGoal.java)
**Logic**: The AI queries `getSimulationDistance()` every 100 ticks.
**Justification**: Prevents AI pathfinding into unloaded chunks (Server-Side Safety). Value is cached to prevent per-tick performance impact.

## v3.1.13: Ultraguard Sync

**Logic**: Uses atomic file writes (`StandardOpenOption.DSYNC`) for configuration persistence.
**Justification**: Prevents data corruption during sudden server crashes or power loss by ensuring the config file is fully committed to disk before returning.
