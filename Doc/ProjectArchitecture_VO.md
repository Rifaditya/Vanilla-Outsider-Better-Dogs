# Project Architecture: Vanilla Outsider - Better Dogs (v3.1.21)

## Philosophy

**"Immersion via Systems (Native Integration)."**
Better Dogs enhances wolf behavior through hidden complexity. We use the **WolfScheduler** and **Data Attachments** to create behaviors that feel organic. In v3.1.20+, we transitioned from external configuration to **Native Minecraft Game Rules** for a "Mod-less" feel.

## Structure

- **Core**: Single-module Java 25 project.
- **Engine**:
  - `scheduler/`: The heart of the new AI. Handles time-based and social events.
  - `ai/`: Custom Goals (Mischief, Correction, Howl, Zoomies, PlayFight).
  - `mixin/`: System-level injections.
  - `registry/`: Handles Game Rule registration and Custom UI Category.

## Key Components

- **WolfScheduler**: The central brain. Manages "Social Mode" and event broadcasting.
- **WolfExtensions**: Interface injected into `Wolf` to give it state memory and personality data.
- **BetterDogsGameRules**: Registers 40+ rules under the `BETTER_DOGS` category, replacing `betterdogs.json` dependency.
- **WolfPersistentData**: Uses Fabric Attachment API for cross-session state (Grudges, Personality).

---
*Last Updated: January 2026*
