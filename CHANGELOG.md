# Changelog

## v3.1.25

- **Vanilla Spirit Purge**: Removed the `Howl` behavior entirely as it forced dogs to sit/stand, violating player control.
- **Posture Respect**: Updated all social behaviors (Zoomies, Begging, Fetch, Wanderlust) to strictly respect the dog's sitting state. Dogs will no longer perform social actions if they are ordered to sit.
- **Global Cooldown System**: Implemented a per-dog global cooldown in the scheduler. Dogs now have significant downtime (5+ minutes) between social behaviors.
- **Dynamic Durations**: Behaviors now have specific, shorter durations (10-30 seconds instead of 20 minutes) defined in the Social Event core.
- **Reaction Priority**: Critical reactive behaviors (Retaliation, Correction) now bypass global cooldowns for survival but still maintain proper lifecycle durations.

## v3.1.24

- **FIXED**: Critical startup crash caused by missing package declaration in `ServerLevelMixin`.

## v3.1.23 (Self-Healing Update)

- **FIXED**: `AbstractMethodError` crash when loading old worlds with aggressive puppies.
- **ADDED**: "Self-Healing" logic for old worlds (existing wolves automatically join the Hive Mind and receive DNA).
- **HARDENED**: Hive Mind core now includes Level Verification to prevent cross-level pulsing crashes.
- **OPTIMIZED**: Global Pulse moved to `ServerLevel` tick (Highlander Standard) for better server performance.

## v3.1.22 (Hive Mind v6.1 - Character Update)

- **Hive Mind Integration**: Fully integrated the v6.1 social core. Better Dogs now operates within a global social network that automatically merges with other Hive Mind-compatible mods.
- **Digital DNA**: Wolves now possess a 64-bit DNA seed generated at spawn. This seed dictates personality traits, social event acceptance, and physical appearance.
- **Size Variation**: Implemented DNA-driven scaling. Wolves now vary naturally in size (0.9x to 1.1x), rendered natively on the client.
- **Character Events**:
  - **Begging**: DNA-driven "greedy" trait causes wolves to beg for food from their owners.
  - **Fetch**: Wolves in a playful mood will now seek out and retrieve dropped items.
  - **Idle Curiosity**: Ambient social interaction driven by a wolf's curiosity trait.
- **Highlander Pulse Guard**: Drastically optimized AI performance using a version-negotiating master pulse.

## v3.1.21

- **Feature:** Added native Game Rules for Breeding Genetics. This allows per-world configuration of personality inheritance and mixed-breed chances.
- **Maintenance**: Ensured both Taming and Breeding rules are present in the "Better Dogs" category.

## v3.1.20

- **Feature:** Added a custom "Better Dogs" category to the Game Rules screen. All mod-specific rules are now grouped under this new tab instead of the generic "Mobs" category.

## v3.1.19

- **Localization Fix:** Prepended `minecraft.` namespace to all Game Rule translation keys (e.g., `gamerule.minecraft.bd_storm_anxiety`) to match vanilla Game Rule registry behavior. This ensures Game Rules are properly named in the "Edit Game Rules" screen.

## v3.1.18

- **Fix:** Resolved startup crash caused by `IdentifierException`.
- **Technical:** Renamed all Game Rule IDs to lower_snake_case (e.g., `bdStormAnxiety` -> `bd_storm_anxiety`) to comply with Minecraft 1.21+ registry standards.
- **Localization:** Updated `en_us.json` to match new Game Rule keys.

## v3.1.17 (Anti-Crowding Removal)

- **Removed**: The "Anti-Crowding System" (`PackSeparationGoal`) has been removed entirely. Wolves will no longer artificially push each other away, reverting to standard vanilla grouping behavior.
- **Config**: Removed `packSeparationRadius`, `packSeparationInflation`, `packSeparationSpeed`, and `enablePackSeparation` from `betterdogs.json`.

## v3.1.16 (Stable Hotfix: Crash Fix)

- **Fix**: Replaced `WolfMixin` targeting logic with `WolfMobMixin` to robustly intercept `setTarget` without causing transformation errors on startup. This resolves the `MixinApplyError` seen in v3.1.15.

## v3.1.15 (Broken Build - Startup Crash)

- **Hotfix Attempt**: Attempted to fix startup crash but introduced a new Mixin transformation error due to target class mismatch. **DO NOT USE**.

## v3.1.14 (Better Teleportation & Dynamic Ranges)

### Added

- **Dynamic Simulation Capping**: Aggressive dog follow and detection ranges now automatically scale to stay within the server's simulation distance, preventing them from being left behind in unloaded chunks.
- **Improved Teleportation (The "2x" Rule)**: Optimized follow logic so dogs prefer running back to the owner. They will only teleport if they fall behind by more than twice their follow start distance.

### Changed

- **Simplified Config**: Consolidated various personality-specific teleport settings into a single `teleportMultiplier` (default 2.0x).

## [3.1.13] - 2026-01-26

### Added

- **Ultraguard Sync**: Implemented a hardened persistence system with atomic writes, automatic merging, and legacy field purging.
- **Auto-Backups**: Config now clones to `betterdogs.json.bak` before any version-jump migrations.
- **Completed AI Migration**: Finalized the movement of all 30+ AI magic numbers to `BetterDogsConfig`.

### Changed

- Updated platform descriptions (Modrinth/CurseForge) with the new Config Mastery feature suite.

## [3.1.12] - 2026-01-25

### Added

- **Personality-Scaled Teleportation**: Aggressive dogs wander deeper (5x), Pacifists stay close (0.5x).
- **Config Persistence Protocol**: Fixed the reset-on-load bug and established new modder standards (`12_Config_AI_Standards.yaml`).
- **Emergency Restoration**: Recovered all AI implementation files and project properties after a critical data loss event.

## [3.1.11] - 2026-01-25

### Changed

- **Optimized Follow Behavior**: Dogs now try to run to the player when falling behind instead of immediately teleporting.
- **Improved Teleportation**: Increased the teleport distance by 1.5x (from 12 to 18 blocks) to give dogs more time to catch up naturally.
- **New Config**: Added `followCatchUpSpeed` (default: 1.5) to control how fast dogs run when they are far from the owner.

## [3.1.10] - 2026-01-25

### Added

- **New Feature: Pack Separation**
  - Dogs now respect each other's "personal space".
  - If too many dogs are crowded together, they will gently spread out to form a more natural pack layout.
  - Configurable radius (`packSeparationRadius`) and speed (`packSeparationSpeed`) in `betterdogs.json`.
- **Developer Protocol Update**: Enforced "Zero Technical Debt" policy for mod development.

## [3.1.9] - 2026-01-25

### Fixed

- **Critical Fix: Howl State Leak**: Fixed a major bug where wolves would stay in the sitting pose permanently after a Group Howl event. They now correctly restore their previous state (sitting or standing) when the event ends.
- **Maintenance**: Cleaned up code debt, removed redundant AI goals, and optimized imports across the entire AI package.

## [3.1.8] - 2026-01-25

### Fixed

- Fixed crash / build failure caused by incorrect `SoundEvents` usage for Howl event.
- Fixed `SmallFightGoal` incorrectly passing `Level` instead of `ServerLevel`.
- Implemented `WolfAccessor` to correctly access native wolf ambient sounds.
- Native API compliance for all 26.1 Snapshot methods.

- **New Feature: Individual DNA**
  - Every dog now has a unique "preference roll" (based on their UUID) that determines if they like specific social events.
  - This ensures that even two dogs with the same Personality will behave differently (e.g., one loves Zoomies, the other thinks it's undignified).

## [v3.1.7] - 2026-01-25

- **New Feature: Individual DNA**
  - Every dog now has a unique "preference roll" (based on their UUID) that determines if they like specific social events.
- **New Feature: Zoomies**
  - Wolves now have a chance to get the "Zoomies" (hyperactive running) in the morning or after rain.
- **New Feature: Group Howl**
  - Wolves may start a group howl at night.

## [v3.1.6] - 2026-01-25

- **New Feature: Play Fighting**
  - Large packs of aggressive wolves (>10) will now occasionally "play fight" for 10 seconds.
- **Refactor**: Renamed internal event classes to `*DogEvent` for clarity.

## [3.1.5] - 2026-01-25

### 🐞 Bug Fix

- **Blood Feud Probability**: Fixed a critical math error where the Blood Feud chance was being calculated as 100% instead of the intended 5%.

### ✨ New Feature

- **Configurable Retaliation**: Added `babyRetaliationChance` to `config/betterdogs.json`. (Default: 75%).

## Archives

Looking for older versions? Check the [Historical Changelogs](Doc/Changelogs/History.md).
