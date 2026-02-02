# Better Dogs: Project Summary

**Version:** 3.1.21 (Targeting Minecraft 26.1-snapshot-4)  
**Creator:** DasikIgaijin  
**Dependencies:** Java 25, Fabric Loader >=0.16.10

## 1. Project Philosophy (Vanilla Outsider)

Adheres to the "One Click, One Action" rule. Better Dogs enhances the vanilla wolf experience without introducing automation.

* **Unique Personalities**: Rolling (Normal, Aggressive, Pacifist) upon taming or spawn.
* **Individual DNA**: UUID-based preference rolls for social events.
* **Game Rule Mastery**: Moved from Cloth Config to native Minecraft Game Rules for snapshot compatibility.

## 2. Dependencies & Environment

Verified against Protocol `Better_modder_agent_protocol.yaml`:

* `minecraft`: `~26.1-` (Verified: `26.1-snapshot-4`)
* `java`: `>=25`
* `fabric-api`: `*`
* **Note**: No longer requires Cloth Config.

## 3. Technical Implementation

* **Data Management**: Uses Fabric's native `AttachmentRegistry` to store `WolfPersistentData` (States: Grudges, Mischief Day, Social Mode).
* **AI Logic**:
  * **WolfScheduler**: Centralized event brain manages Zoomies, Howling, and play fights.
  * **Social Engine**: Handles hierarchy (Correction) and vendettas (Blood Feud).
* **Mixins**:
  * `WolfMobMixin`: Intercepts target-setting for social overrides.
  * `WolfBreedingMixin`: Implements the genetics inheritance engine.
  * `WolfMixin`: Goal injection, hazard avoidance, and storm anxiety.
* **UI Integration**: Registered `GameRuleCategory.BETTER_DOGS` to create a dedicated tab in the vanilla UI.

## 4. Audit & Safety

* **Network**: Offline only.
* **Safety**: Implementation of Cliff/Lava node evaluation in pathfinding.
* **Sanitation**: Periodic purge of build logs and sensitive absolute paths in documentation.

## 5. Documentation Map

* [History of Changes](Changelogs/History.md)
* [Moderator Audit Helper](Codebase%20Documentations/Audit_Helper_For_Moderators.md)
* [Technical Logic Guide](Codebase%20Documentations/Logic_VO.md)
* [AI Magic Number Reference](DOG_AI_REFERENCE.md)
* [Current Changelog](../CHANGELOG.md)
