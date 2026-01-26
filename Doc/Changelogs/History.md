# Better Dogs - Historical Changelogs

This file contains the archived changelogs for historical versions of Better Dogs for the 26.1 Snapshot series.

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
