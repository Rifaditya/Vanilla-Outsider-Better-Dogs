# Changelog

## [3.4.6] - 2026-05-16
### Summary
The **"Probability Matrix"** update. Overhauled territorial outcomes with a dynamic, personality-driven chance system.
- **Dynamic Outcomes**: Pack disputes now use a weighted probability matrix (War/Merge/Retreat):
    - **Aggro vs Aggro**: 80% War, 10% Merge, 10% Run
    - **Aggro vs Normal**: 50% War, 40% Merge, 10% Run
    - **Aggro vs Pacifist**: 10% War, 50% Merge, 40% Run
    - **Normal vs Normal**: 20% War, 50% Merge, 30% Run
    - **Normal vs Pacifist**: 5% War, 45% Merge, 50% Run
    - **Pacifist vs Pacifist**: 0% War, 50% Merge, 50% Run
- **Hierarchy Polish**: During a merge, leadership is granted based on personality rank (Aggressive > Normal > Pacifist).


## [3.4.5] - 2026-05-16
### Summary
The **"Nuanced Leadership"** update. Refined pack interactions to respect personality instincts and autonomy.
- **Pacifist Autonomy**: Pacifist leaders now prioritize their pack's independence, choosing to **Retreat** rather than merge into rival packs.
- **Hierarchy Polish**: Aggressive leaders only force automatic merges against Normal leaders who don't want to fight.
- **Combat Logic**: If a leader (regardless of rank) chooses to fight, they will engage in a standard duel.

## [3.4.4] - 2026-05-16
### Summary
The **"Dominance Hierarchy"** update. Implemented a strict personality-based ranking for pack disputes.
- **Leadership Hierarchy**: Aggressive > Normal > Pacifist. Higher-ranked leaders now automatically win disputes against lower-ranked rivals unless a duel is triggered.

## [3.4.3] - 2026-05-16
### Summary
The **"Command UX"** update. Fixed visibility issues with the debug command suite.
- **UX Fix**: Resolved "Red Text" (Unknown command) issues for OPs. Commands are now always visible and logically gated behind the debugging GameRule.

## [3.4.2] - 2026-05-16
### Summary
The **"C2ME Compatibility"** update. Resolved critical multi-threading crashes and production stability issues.
- **Performance**: Fully compatible with multi-threaded chunk generation (C2ME).
- **Dependency Hardening**: Enforced `dasik-library >= 1.7.0` to prevent binary mismatch crashes.
- **Refmap Fix**: Resolved production Mixin errors by standardizing internal refmap naming.


## [3.4.2+build.1] - 2026-05-16
### Summary
The **"Debug Expansion"** update. Significantly enhanced the wolf interaction testing suite and improved production stability.
- **Debug Action Expansion**: Expanded `/betterdogs debug action` with secondary target support and automatic neighbor detection.
- **New Actions**: Added support for `play_fight`, `retaliation`, `discipline`, `territorial_dispute`, and `territorial_war`.
- **GameRule Protection**: Gated the `/betterdogs` command tree behind the `betterdogdebugging` GameRule for safer production use.
- **Stability Fix**: Added missing `refmap` to Mixin configuration to ensure compatibility in remapped/production environments.

## [3.4.1] - 2026-05-16
### Summary
The **"Technical Patching"** update. Focused on asset cleanup and debug command stability for territorial testing.
- **Asset Purge**: Removed unimplemented "Play Bow" animation references from the codebase to maintain architectural integrity.
- **Debug Stability**: Refactored the `/betterdogs debug territory` command to anchor wolf spawning to the surface level using the Heightmap API, ensuring reliable test scenarios in varying terrain.

## [3.4.0] - 2026-05-12
### Summary
The **"Wolf Litters"** update. Tamed wolves can now have multiple puppies in one breed, mirroring real-world dog litters.
- Added `bd_wolf_litter_max_size` and `bd_wolf_litter_extra_chance` GameRules.
- Puppies in a litter inherit parent traits independently.

## [3.3.1] - 2026-05-11
### Added
- **🤝 Social Politeness**: New GameRule `bd_territorial_exclusive_disputes` (Default: true) ensures territorial disputes are strictly 1v1.
- **Queuing Logic**: Leaders will now wait for a rival to be "free" before initiating a challenge, preventing chaotic crowd disputes in dense areas.

## [3.3.0] - 2026-05-11
### Added
- **🌍 Territorial Scaling**: New GameRule `bd_territorial_search_radius` to control the distance at which pack leaders engage in disputes.
- **🐺 Pack Dynamics**:
    - **Configurable Cluster Size**: New GameRule `bd_wolf_pack_cluster_size` to control the maximum size of naturally spawning packs.
    - **Density Boosting**: New GameRule `bd_wolf_spawn_density_boost` (% chance) to trigger a "Reinforcement Spawn" nearby when a pack spawns, increasing regional territorial friction.
- **Localization**: Full descriptive strings for all new balancing parameters in `en_us.json`.

### Refactored
- **Versioning**: Incremented to `3.3.0` to reflect significant configuration and world-gen logic shifts.
- **AI Hardening**: `WildWolfTerritorialGoal` now fully respects dynamic search radius updates without requiring a world reload.


## [3.2.0] - 2026-05-11
### Added
- **🏰 Territorial Handshake**: New negotiation logic for wild pack leaders.
    - If both leaders want war -> Cinematic 1v1 Duel.
    - If only one wants war -> Negotiated Yield (B yields/merges) or Retreat based on `bd_territorial_yield_on_one_sided_chance`.
    - If neither wants war -> Peaceful Retreat (96 blocks).
- **Wild Personality AI**: Wild members (not leaders) exhibit unique behaviors (e.g., Aggressive wolves hunting monsters) while anchored to their pack leader.
- **Debug Tooling**:
    - Added `/betterdogs debug territory` for immediate pack interaction testing.
    - **Territorial Debug Logging**: New detailed console logs for pack interactions (Wars, Merges, Retreats) enabled via `betterdogdebugging` GameRule.

### Hardening
- **Logic Hardening**: Moved all custom entity logging (Spawn, Tame, Social, Ambient) to the `betterdogdebugging` GameRule gate.
- **Default Alignment**: Wild Personality Behavior is now enabled by default for new worlds.
- **Architectural Cleanup**: Removed duplicate debug log entries in Mixin layers.

### Refactored
- **Architectural Alignment**: Refactored `WildWolfFollowLeaderGoal` to use the modernized **DasikLibrary 1.7.0** base goal.
    - Optimized pack following with distance-based "Movement Triggers" (start/stop thresholds).
    - Reduced mod code footprint by leveraging library-side stable physics.
- **Mapping Compliance**: Fully refactored for **Minecraft 26.1.2** ("Tiny Takeover") mapping signatures.
- **Deterministic AI**: Territorial decisions are now synchronized between entities using a shared seeded random.

## [3.1.37+build.10] - 2026-05-12

### Added
- **AI Refactor: Stable Wild Pack Leadership**:
  - **Refactored `WildWolfFollowLeaderGoal`**: Wild wolves now follow their pack leader with logic mirroring vanilla `FollowOwnerGoal` (teleportation and consistent movement).
  - **Stable Leadership**: Pack members now lock onto their leader persistently. Leadership is saved to NBT and survives world reloads.
  - **Tame Interaction**: Taming a leader now allows the wild pack to follow the player by proxy. Taming a member correctly transitions them to standard owner-following behavior.
  - **Improved Performance**: Reduced frequency of expensive social pathfinding calculations.

## [3.1.37+build.9] - 2026-05-11

### Added
- **Debugging Mode**: Introduced the `betterdogdebugging` GameRule.
  - Enables detailed AI logging in the server console.
  - Adds visual particles (Flame/Note/Happy Villager) above wolves to indicate personality.
  - **Debug Stick Integration**: Allows cycling through personalities and social scales using the vanilla Debug Stick.
- **Maintenance**: Fixed compilation errors related to `DasikLibrary` API changes in `WildWolfFollowLeaderGoal`.

## [3.1.37+build.8] - 2026-04-16

### Added
- **Pack Spread Control**: New GameRule `bd_pack_spread` (default: 20 = 2.0 blocks) sets the minimum separation distance between wild wolves in a pack.
  - Each integer unit = 0.1 blocks. Example: `/gamerule bd_pack_spread 50` = 5.0 block spacing.
  - Updates are dynamic — wolves respond to GameRule changes within 40 ticks, staggered per entity to prevent TPS spikes.
  - Default separation raised from 1.5 to 2.0 to reduce visual overcrowding.

### Dependency: DasikLibrary Build 22
- Requires `FollowLeaderGoal.setParameters()` to apply runtime AI parameter changes.

## [3.1.37+build.7] - 2026-04-16

### Refactored
- **Pack AI: Sovereign Migration** — Migrated group size tracking from an O(N) hand-rolled bounding-box scan (`WolfMixin.getGroupSize()`) to the DasikLibrary `FlockState` cache. Pack size is now computed once by the leader and shared across all followers.
- **Removed** dead fields `betterdogs$groupSize` and `betterdogs$groupSizeCheckTicks`.

### Dependency: DasikLibrary Build 21
- Added `FlockState.getMemberCount()` API to DasikLibrary, enabling mods to query cached pack size without iterating entities.
- Added `GroupManager.computeFlockState()` now sets `memberCount` alongside existing center-of-mass and velocity aggregates.

### Infrastructure
- **Upgraded**: Fabric Loader to `0.19.1` — native Java 25 Mixin subsystem support (no Knot warning).
- **Minecraft Support**: Shifted to `~26.x` compatible range (`>=26.1`) for **Minecraft 26.2** readiness.
- **Upgraded**: Fabric API to `0.145.4+26.1.2`.
- **Dependency**: Synchronized with `DasikLibrary` Build 21.

## [3.1.37+build.6] - 2026-04-15

### Added
- **DasikLibrary Build 16 Sync**: Integrated the **Cached Boids Pattern**. Wolf packs now utilize $O(N)$ aggregated state computation, resolving performance leaks during large pack gatherings.
- **Biomechanical Smoothing**: Leverages the new library-side Lerp interpolation for smoother pack following without visual jitter.

### Changed
- **Release Alignment**: Synchronized workspace with Minecraft **26.1.2 ("Tiny Takeover") Release** and Fabric API `0.145.4`.

## [3.1.37+build.5] - 2026-03-04

### Changed
- **Sound Polish**: Replaced the low-pitch `WOLF_SHAKE` howl placeholder with `WOLF_WHINE` to sound more natural and less like a monster.

## [3.1.37+build.4] - 2026-03-04

### Added
- **Debug Commands**: Added `/betterdogs debug` commands to force personality changes and trigger social actions.
  - *Example (Personality)*: `/betterdogs debug personality @e[type=wolf,distance=..5] aggressive`
  - *Example (Action)*: `/betterdogs debug action @e[type=wolf,distance=..5] howl`
  - *Actions available*: `howl`, `zoomies`, `mischief`, `disciplined`.
- **Snapshot Support**: Fully migrated API calls to match Minecraft 26.x Snapshot 11 (e.g. `getWorldClockTime()`, `LivingEntity` leadership).
- **Workspace Consolidation**: Refactored the environment to maintain a single source of truth for mod development in a cleaner folder structure.

### Changed
- **Modularity**: Split command execution logic into a dedicated Zenith-compliant `WolfCommandHelper.java`.

## [3.1.37+build.3] - 2026-03-04

### Changed
- **Documentation**: Updated platform documentation (`Description Page.md`) to clearly detail the new **Unique Personality System** (Aggressive, Pacifist, Normal) with corresponding particle effects and behaviors.

## [3.1.37+build.2] - 2026-03-03

### Fixed
- **Dasik Library Integration**: Resolved build failures in project configuration by aligning Gradle mappings to the official 26.1 snapshot standards.

## [3.1.37+build.1] - 2026-03-03
- **Social Bonding**: New system where wolves track affinity with each other. High affinity suppresses "Blood Feuds".

- **Personality Traits**:
  - **Aggressive**: Wolves now scouting ahead of their owner.
  - **Pacifist**: Wolves now whine to alert hostiles within range.

### Fixed
- **Pattern Matching**: Refactored combat logic to focus on Java 25 performance standards.

## [3.1.36+build.5] - 2026-03-02

### Added
- **Java Upgrade**: Upgraded to Java 25 to support Minecraft 26.1 snapshots.

### Fixed
- **API Integration**: Resolved compilation errors in `EatGroundFoodGoal.java` caused by Minecraft API changes in data components and registry access.

## [3.1.36+build.4] - 2026-03-02

### Added

- **Feeding**: Tamed dogs can now eat dropped raw and cooked food from the ground to restore health.
- **Toggles**: Added `bd_dogs_eat_raw_food` and `bd_dogs_eat_cooked_food` gamerules for granular control.
- **Mod Compatibility**: Features full support for modded foods via tags and name-based heuristics.

## [3.1.36+build.3] - 2026-02-21

### Fixed

- **Compatibility**: Reverted Mixin compatibility level from `JAVA_25` to `JAVA_22` to resolve warning.

## [3.1.36+build.2] - 2026-02-19

### Added

- **Leader-Follower Integration**: Wild wolves now naturally form packs using DasikLibrary's Leader-Follower API, with a deterministic max pack size of 8.

### Fixed

- **Ambient Event Spam**: Restored proper cooldown logic for ambient behaviors (like begging), preventing them from executing every tick.

## [3.1.36+build.1] - 2026-02-19

### Changed

- **DasikLibrary Integration**: Switched to standalone dependency (JiJ removed).
- **Versioning**: Adopted strict Build Number policy.

## [3.1.36] - 2026-02-16

### Fixed

- Dependency conflict: Allow `DasikLibrary` >= 1.0.1 (removed < 2.0.0 cap) to support version 2.0.0.

## [3.1.35] - 2026-02-03

### Added

- Detailed descriptions for all gamerules, including the previously missing `bd_howl_chance`.

---
