# Better Dogs - Historical Changelogs

This file contains the archived changelogs for historical versions of Better Dogs for the 26.1 Snapshot series.

## [v3.1.26] - 2026-01-26

### [v3.1.26] Optimization

- **Performance**: Implemented "Zero-Overhead" Social AI using Ghost Brain architecture and Allocation-Free Registry pools.
- **Memory**: Schedulers are now lazy and transient, freeing RAM when entities are idle.
- **IO**: Implemented "Data Silence" to eliminate unnecessary disk saves and network sync for social behaviors.

## [v3.1.21] - 2026-01-26

### [v3.1.21] Features

- **Feature:** Added native Game Rules for Breeding Genetics. This allows per-world configuration of personality inheritance and mixed-breed chances.
- **Maintenance**: Ensured both Taming and Breeding rules are present in the "Better Dogs" category.

## [v3.1.20] - 2026-01-26

### [v3.1.20] Features

- **Feature:** Added a custom "Better Dogs" category to the Game Rules screen. All mod-specific rules are now grouped under this new tab instead of the generic "Mobs" category.

## [v3.1.19] - 2026-01-26

### [v3.1.19] Fixes

- **Localization Fix:** Prepended `minecraft.` namespace to all Game Rule translation keys (e.g., `gamerule.minecraft.bd_storm_anxiety`) to match vanilla Game Rule registry behavior. This ensures Game Rules are properly named in the "Edit Game Rules" screen.

## [v3.1.18] - 2026-01-26

### [v3.1.18] Fixes

- **Fix:** Resolved startup crash caused by `IdentifierException`.
- **Technical:** Renamed all Game Rule IDs to lower_snake_case (e.g., `bdStormAnxiety` -> `bd_storm_anxiety`) to comply with Minecraft 1.21+ registry standards.
- **Localization:** Updated `en_us.json` to match new Game Rule keys.

## [v3.1.17] - 2026-01-26

### [v3.1.17] Changes

- **Removed**: The "Anti-Crowding System" (`PackSeparationGoal`) has been removed entirely. Wolves will no longer artificially push each other away, reverting to standard vanilla grouping behavior.
- **Config**: Removed `packSeparationRadius`, `packSeparationInflation`, `packSeparationSpeed`, and `enablePackSeparation` from `betterdogs.json`.

## [v3.1.16] - 2026-01-26

### [v3.1.16] Fixes

- **Fix**: Replaced `WolfMixin` targeting logic with `WolfMobMixin` to robustly intercept `setTarget` without causing transformation errors on startup. This resolves the `MixinApplyError` seen in v3.1.15.

## [v3.1.15] - 2026-01-26

### [v3.1.15] Hotfix

- **Hotfix Attempt**: Attempted to fix startup crash but introduced a new Mixin transformation error due to target class mismatch. **DO NOT USE**.

## [v3.1.14] - 2026-01-26

### [v3.1.14] Added

- **Dynamic Simulation Capping**: Aggressive dog follow and detection ranges now automatically scale to stay within the server's simulation distance, preventing them from being left behind in unloaded chunks.
- **Improved Teleportation (The "2x" Rule)**: Optimized follow logic so dogs prefer running back to the owner. They will only teleport if they fall behind by more than twice their follow start distance.

### [v3.1.14] Changed

- **Simplified Config**: Consolidated various personality-specific teleport settings into a single `teleportMultiplier` (default 2.0x).

## [v3.1.13] - 2026-01-26

### [v3.1.13] Added

- **Ultraguard Sync**: Implemented a hardened persistence system with atomic writes, automatic merging, and legacy field purging.
- **Auto-Backups**: Config now clones to `betterdogs.json.bak` before any version-jump migrations.
- **Completed AI Migration**: Finalized the movement of all 30+ AI magic numbers to `BetterDogsConfig`.

### [v3.1.13] Changed

- Updated platform descriptions (Modrinth/CurseForge) with the new Config Mastery feature suite.

## [v3.1.12] - 2026-01-25

### [v3.1.12] Added

- **Personality-Scaled Teleportation**: Aggressive dogs wander deeper (5x), Pacifists stay close (1.5x).
- **Config Persistence Protocol**: Fixed the reset-on-load bug and established new modder standards (`12_Config_AI_Standards.yaml`).
- **Emergency Restoration**: Recovered all AI implementation files and project properties after a critical data loss event.

## [v3.1.11] - 2026-01-25

### [v3.1.11] Changed

- **Optimized Follow Behavior**: Dogs now try to run to the player when falling behind instead of immediately teleporting.
- **Improved Teleportation**: Increased the teleport distance by 1.5x (from 12 to 18 blocks) to give dogs more time to catch up naturally.
- **New Config**: Added `followCatchUpSpeed` (default: 1.5) to control how fast dogs run when they are far from the owner.

## [v3.1.10] - 2026-01-25

### Added

- **New Feature: Pack Separation**
  - Dogs now respect each other's "personal space".
  - If too many dogs are crowded together, they will gently spread out to form a more natural pack layout.
  - Configurable radius (`packSeparationRadius`) and speed (`packSeparationSpeed`) in `betterdogs.json`.
- **Developer Protocol Update**: Enforced "Zero Technical Debt" policy for mod development.

## [v3.1.9] - 2026-01-25

### [v3.1.9] Fixed

- **Critical Fix: Howl State Leak**: Fixed a major bug where wolves would stay in the sitting pose permanently after a Group Howl event. They now correctly restore their previous state (sitting or standing) when the event ends.
- **Maintenance**: Cleaned up code debt, removed redundant AI goals, and optimized imports across the entire AI package.

## [v3.1.8] - 2026-01-25

### [v3.1.8] Fixed

- Fixed crash / build failure caused by incorrect `SoundEvents` usage for Howl event.
- Fixed `SmallFightGoal` incorrectly passing `Level` instead of `ServerLevel`.
- Implemented `WolfAccessor` to correctly access native wolf ambient sounds.
- Native API compliance for all 26.1 Snapshot methods.

- **New Feature: Individual DNA**
  - Every dog now has a unique "preference roll" (based on their UUID) that determines if they like specific social events.
  - This ensures that even two dogs with the same Personality will behave differently.

---

## [v3.1.7] - 2026-01-25

### ✨ New Features

- **Individual DNA System**:
  - Implemented a unique "Characteristic Roll" for every dog derived from its UUID.
  - This roll is combined with their Personality Threshold to determine if they participate in social events.
  - Result: Each dog feels like a unique individual with its own likes/dislikes.

- **Zoomies Event**:
  - **Behavior**: Hyperactive random running for 5-8 seconds.
  - **Triggers**:
    - Morning (0-2000 ticks): 10% chance.
    - Wet (Rain/Water): 20% chance.
  - **Participation**:
    - Babies: 100% (Always love to play).
    - Normal: 50%.
    - Pacifist: 20%.
    - Aggressive: 0% (Too cool for this).

- **Group Howl Event**:
  - **Behavior**: A Leader starts howling, and nearby pack members sit and join in.
  - **Triggers**:
    - Full Moon: 100% chance.
    - Regular Night: 5% chance.
  - **Participation**:
    - Aggressive: 100% (Love to assert dominance).
    - Normal: 80%.
    - Pacifist: 40%.

---

## [v3.1.6]

### 🏈 Play Fighting

- **Concept**: Large packs of aggressive wolves (>10) will now occasionally "play fight" to burn off energy.
- **Behavior**: Two wolves will chase and attack each other for 10 seconds.
- **Safety**: **100% Non-Lethal**. Health is hard-capped at 1 HP during these events.
- **Optimization**:
  - **Staggered Checks**: Checks occur only once per in-game minute per wolf (modulo based).
  - **Sampling**: Detection creates a sample of max 15 neighbors.
  - **Performance**: O(1) logic ensures zero lag even in massive wolf farms.

### Refactoring

- Renamed `CorrectionEvent` to `CorrectionDogEvent`.
- Renamed `RetaliationEvent` to `RetaliationDogEvent`.
- Renamed `WanderlustEvent` to `WanderlustDogEvent`.

---

## [1.09.009] - 2026-01-24

### ⚔️ Domestic Retaliation & Intervention

- **Owner-Attack Bypass**: Overrode hardcoded vanilla blocks in `TamableAnimal.canAttack` and `Wolf.wantsToAttack`.
  - *Utility*: Allows baby wolves to correctly retaliate when struck by their owners.
  - *Intervention*: Enables aggressive adults to target misbehaving babies to "enforce" manners.
- **AI Priority Re-tuning**: Set domestic retaliation and intervention goals to priority 0 for instantaneous response.
- **Aggressive State Handling**: Intervention now correctly triggers the wolf's aggressive visual state.

### 🏗 Convention & Standards

- **New Versioning Strategy**: Transitioned to the `x.xx.xxx` Semantic Versioning format (e.g., 1.09.008).
- **Protocol Update**: Documented the "Owner-Attack Barrier" in `Better_modder_agent_protocol.yaml` for consistency across projects.

### 🛠 Technical Fixes

- **Mixin Consolidation**: Cleaned up `WolfMixin` logic to use proper `@Inject` patterns for bypasses instead of direct overrides where possible.
- **Logging**: Added server-side logging for adult intervention events.

---

## [1.9.5-26.1] - 2026-01-23

### 🏗 Architecture Reversion

- **Fabric Native Reversion**: Pivot from Multi-Loader (NeoForge/Fabric) back to it being **Fabric only**.
- **Source Consolidation**: Smashed `common` and `fabric` into one root structure. Deleted NeoForge and all the extra dependencies.

### 🛠 Technical Changes

- **Java 25 Standardization**: Fully migrated the build system and toolchain to **Java 25**.
- **Fabric Attachment API**: Fully integrated Fabric's native Attachment API for persistent wolf data.
- **Build System**: Standardized on Fabric Loom 1.14.7.
- **Critical Bug Fix**: Resolved a startup crash (`MixinApplyError`) by redirecting taming logic injection from `setTame` to `applyTamingSideEffects`.

### ⚙️ Mod Configuration

- **Standalone JSON Loader**: Uses `config/betterdogs.json` instead of Cloth Config to ensure compatibility with snapshot versions where Mod Menu is absent.

---

## [v1.8.7-26.1] - 2026-01-23

### 🛠 Bug Fixes

#### Stale Class Cleanup

- Clean rebuild to eliminate stale `betterdogs$afterHurt` mixin injection that was causing crash on game launch.

### Technical Details

This release fixes a critical startup crash caused by stale compiled class files that remained in the build artifacts after previous changes.

---

## [v1.8.5-26.1] - 2026-01-23

### 🛠 Bug Fixes

#### Persistent Crash Fix

- Stabilized Mixin transformation in the 26.1 snapshot environment by adding a mandatory `refmap` and using `remap = false` for inherited `doHurtTarget` injections.

#### License Alignment

- Aligned mod metadata with the project's GPL-3.0 license in both loaders.

---

## [v1.8.4-26.1]

### 🧠 Baby Curiosity (Passive/Normal)

- **Curiosty AI**: Non-aggressive baby wolves now exhibit natural curiosity.
- **Entity Observation**: They will approach nearby mobs (passive or hostile) and players to "stare" at them for 2-6 seconds.
- **Environmental Focus**: They will wander towards interesting blocks like flowers, grass, and trees to investigate their surroundings.

### ⚔️ Reckless Aggression (Aggressive)

- **Immediate Engagement**: Aggressive baby wolves now immediately engage and attack hostile mobs they detect.
- **Recklessness**: Aggressive babies will chase and attack monsters regardless of owner proximity.

---

## [v1.8.3-26.1]

### 🐾 Untrained Baby Wolf Behavior

- **2x Follow Radius (Default)**: Tamed baby wolves have double the follow distances compared to adults.
- **2x Teleport Threshold (Default)**: Threshold increased to 24 blocks for babies.

---

## [v1.8.2-26.1] - 2026-01-23

### 🎉 New Features

- **Domestic Aggression & Retaliation**: Baby wolves retaliate with 2 strikes if hit by owner.
- **Aggressive Adult Intervention**: Adults intervene to protect owner from misbehaving babies.
- **Disciplined Combat**: 3-strike sequence for enforcers.

### 🛠 Bug Fixes

- **Personality Stats Rebalance**: Fixed swapped Aggressive/Pacifist defaults.

---

## [v1.8.1-26.1] - 2026-01-23

### 🎉 New Features

- **Passive Baby Wolves**: Babies are now passive by default (except Aggressive personality hunting monsters).

---

## [v1.8.0-26.1] - 2026-01-23

### 🎉 New Features

- **NeoForge Support**: Multi-loader architecture (Fabric & NeoForge).
- **Official Mappings**: Switched to official Mojang mappings.

---
