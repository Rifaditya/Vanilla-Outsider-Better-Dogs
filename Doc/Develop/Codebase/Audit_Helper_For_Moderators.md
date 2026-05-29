# Audit Helper for Moderators

This document provides technical justifications for patterns in the "Better Dogs" mod that might appear complex or unconventional during platform review.

## v4.5.0: Parent Kinship NBT Tracking & Inbreeding Prevention
**Files**: [WolfBreedingMixin.java](mixin/WolfBreedingMixin.java)
- **Logic**: Breeding checks parent UUIDs. Breeding sibling/parent-child pairings applies an `inbred` status flag.
- **Justification**: Enhances gameplay mechanics through realistic breeding genetics without introducing complex database requirements. The tracking is lightweight and stored directly in the `WolfPersistentData` attachment.

## v4.4.0: Dynamic Health-Based Size Scaling
**Files**: [WolfMixin.java](mixin/WolfMixin.java)
- **Logic**: Applies physical size scales dynamically on load, taming, or personality shifts based on rolled max health bonuses.
- **Justification**: Standard client-server scaling via entity scale attributes. Ensures hitboxes remain vanilla-sized for 100% pathfinding safety.

## v4.3.0: Paper Adoption System
**Files**: [WolfInteractMixin.java](mixin/WolfInteractMixin.java)
- **Logic**: Shift+Right-Clicking a wolf with a sheet of Paper toggles ownership pending status.
- **Justification**: Safely modifies the ownership UUID without bypass commands. Allows players to transfer wolves cleanly in multiplayer.

## v4.0.0: EntityTypes Relocation
**Files**: Mod-wide classes
- **Logic**: Relocated entity constant references to Mojang's new `EntityTypes` registry.
- **Justification**: Aligns with Minecraft 26.2 API shifts to prevent classloading failures.

## v4.5.9 - v4.5.13: Query Throttling and Caching
**Files**: [WildWolfTerritorialGoal.java](ai/WildWolfTerritorialGoal.java), [PersonalityFollowOwnerGoal.java](ai/PersonalityFollowOwnerGoal.java)
- **Logic**: Uses static caches (`FollowerSpacingCache`) and throttles 96-block/32-block scans to once every 20-80 ticks.
- **Justification**: Prevents severe server TPS degradation in high-density entity environments by avoiding per-tick entity sweeps.
