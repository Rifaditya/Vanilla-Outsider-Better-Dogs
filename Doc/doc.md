# Better Dogs: Project Summary

**Version:** 1.9.5-26.1 (Targeting Minecraft 26.1-snapshot-4)  
**Creator:** DasikIgaijin  
**Dependencies:** Java 25, Fabric Loader >=0.16.9

## 1. Project Philosophy (Vanilla Outsider)

Adheres to the "One Click, One Action" rule. Better Dogs enhances the vanilla wolf experience without introducing automation or "un-vanilla" complexity.

* **Unique Personalities**: Every wolf spawned gets one of several personalities (Normal, Aggressive, Pacifist, etc.).
* **Dynamic Stats**: Personalities affect speed, health, and damage output.
* **Persistent AI**: Uses the Fabric Attachment API to ensure data survives reloads without the need for complex external databases.

## 2. Dependencies & Environment

Verified against Protocol `Better_modder_agent_protocol.yaml`:

* `fabricloader`: `>=0.16.9`
* `minecraft`: `~26.1-` (Verified: `26.1-snapshot-4`)
* `java`: `>=25`
* `fabric-api`: `*`

## 3. Technical Implementation

* **Data Management**: Uses Fabric's native `AttachmentRegistry` to store `WolfPersistentData` directly on the `Wolf` entity.
* **Mixins**:
  * `WolfMixin`: The core engine. Handles personality rolling on spawn, AI goal injection, and particle effects.
  * **Particle Control**: Overrides vanilla taming particles to reduce visual noise (7 hearts down to 3).
* **AI Enhancements**:
  * Baby protection (babies aren't targeted unless they attack first).
  * Personality-specific goal weighting.

## 4. Audit & Safety

* **Network**: No external requests (Offline only).
* **Data**: No telemetry or data collection. All data is stored locally in the world file.
* **OS**: No binary execution.

## 5. Documentation Map

* [Moderator Audit Helper](Codebase%20Documentations/Audit_Helper_For_Moderators.md)
* [Changelog](../CHANGELOG.md)
