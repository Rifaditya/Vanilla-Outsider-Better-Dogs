# Changelog

## [1.8.1-26.1] - 2026-01-23

### 🎉 New Features

- **Passive Baby Wolves**: Baby wolves are now passive by default to protect them from unnecessary combat.
  - Baby wolves will NOT attack what the owner attacks.
  - Baby wolves will NOT defend the owner if the owner is hit.
  - **Exception**: Baby wolves with the **Aggressive** personality will still hunt monsters as usual.
  - **Exception**: Any baby wolf will still defend itself if directly hit by a mob.

### 🔧 Technical Changes

- Added `OwnerHurtByTargetGoalMixin` to inhibit defensive behavior in babies.
- Updated `OwnerHurtTargetGoalMixin` to inhibit offensive behavior in babies.
- Updated `PacifistRevengeGoal` and `WildWolfHuntGoal` to exclude babies.

## [1.8.0-26.1] - 2026-01-23

### 🎉 New Features

- **NeoForge Support**: Better Dogs now supports both Fabric AND NeoForge loaders!
- **Multi-Loader Architecture**: Complete project restructure with common/fabric/neoforge modules.

### 🔧 Technical Changes

- Platform Services abstraction for loader-specific code.
- NeoForge 26.1 compatibility (Optional-based NBT API).
- Uses official Mojang mappings only.

## [1.7.6-26.1] - 2026-01-21

### ⚙️ Technical Changes

- **Standalone Config System**: Replaced the dependency on `AutoConfig` and `Cloth Config` with a custom lightweight JSON loader.
  - *Impact*: Configuration files (`config/betterdogs.json`) are now properly generated and respected again. Users can manually edit this file to change settings (e.g., friendly fire, speed buffs) even though the in-game GUI is currently disabled.

## [1.7.5-26.1] - 2026-01-21

### 🧠 AI Improvements

- **Smart Cliff Safety**: Wolves now actively retreat (walk backwards 4 blocks) when they detect a steep drop or void, rather than just stopping. This prevents them from getting stuck at the edge or sliding off.

## [1.7.4-26.1] - 2026-01-21

### 🛠 Bug Fixes

- **Crash Fix**: Resolved a `NullPointerException` during the "Cliff Safety V2" check.
  - *Details*: The code now correctly exits the tick handler if the target is cleared by the V1 check, preventing subsequent access to invalid target references.

## [1.7.3-26.1] - 2026-01-21

### 🛠 Bug Fixes

- **Crash Fix**: Removed reference to missing `WolfGoalsMixin` which caused a startup crash. Functionality was previously merged into `WolfMixin`.

## [1.7.2-26.1] - 2026-01-21

### 🛠 Technical Changes

- **Dependency Fix**: Relaxed `fabric.mod.json` version constraints (`minecraft` to `~26.1-`) to resolve "Incompatible mods" errors with alpha launchers.

## [1.7.1-26.1] - 2026-01-21

### Ported to Minecraft 26.1 (Snapshot 4)

This release marks the migration to the first **unobfuscated** version of Minecraft.

### 🛠 Technical Changes

- **Language Migration**: Switched codebase from **Kotlin** back to **Java**.
  - *Reasoning*: While Kotlin offers a smoother coding experience, the transition to the unobfuscated 26.1 environment favored the native stability and direct mapping of Java, reducing toolchain complexity during this experimental snapshot phase.
- **Build System**: Updated to Fabric Loom 1.14.7 (`net.fabricmc.fabric-loom`) to support unobfuscated builds.

### ⚠️ Known Issues / Temporary Changes

- **Config GUI Unavailable**: While an alpha build of Mod Menu (18.0.0-alpha.4) exists for 26.1, the required **Cloth Config** library has not yet been updated for the unobfuscated environment (still uses `intermediary` mappings).
  - As a result, the in-game configuration screen is disabled to prevent crashes.
  - Default settings are applied automatically.
