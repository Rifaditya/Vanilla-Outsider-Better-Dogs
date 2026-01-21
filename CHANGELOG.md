# Changelog

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
