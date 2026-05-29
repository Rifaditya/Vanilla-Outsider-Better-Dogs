# Better Dogs: Project Summary

**Version:** 4.5.13 (Targeting Minecraft 26.2+)  
**Creator:** Dasik (Rifaditya)  
**Dependencies:** Java 25, Fabric Loader >=0.16.10, DasikLibrary >=1.7.4

## 1. Project Philosophy (Vanilla Outsider)

Adheres to the "One Click, One Action" rule. Better Dogs enhances the vanilla wolf experience without introducing automation.

* **Unique Personalities**: Rolling weights upon spawn or taming.
* **Deterministic DNA**: UUID-based preference rolls for stats and size scale.
* **Game Rule Mastery**: Native Minecraft Game Rules registry and Cloth Config integration.

## 2. Dependencies & Environment

* `minecraft`: `>=26.2-` (Wildcard compatible)
* `java`: `25`
* `fabric-api`: `0.149.2+`
* **Note**: Optionally supports Cloth Config and ModMenu for client configuration GUI.

## 3. Technical Implementation

* **Data Management**: Uses Fabric's native `AttachmentRegistry` to store `WolfPersistentData`.
* **Genetic System**: Parent kinship UUID tracking to apply inbreeding penalties and outcrossing recovery.
* **Size Scaling**: Physical sizes dynamic calculations: `scale = 1.0 + (healthBonus * 0.012)`.
* **Performance Swarm**: Staggered throttles and shared `FollowerSpacingCache` to keep CPU overhead low.
* **Mixins**:
  * `WolfSocialMixin`: Manages social scheduling events and registration.
  * `WolfBreedingMixin`: Implements genetic stat inheritance and kinship checks.
  * `WolfMixin`: Goal injection, hazard safety, and transient caches.

## 4. Audit & Safety

* **Network**: Offline only.
* **Performance**: Raycast bypasses and query caching.
* **Sanitation**: Strict exclusion of marketing files in tracked repository.

## 5. Documentation Map

* [History of Changes](Changelogs/History.md)
* [Moderator Audit Helper](Codebase/Audit_Helper_For_Moderators.md)
* [Technical Logic Guide](Codebase/Logic_VO.md)
* [AI Reference Guide](Architecture/DOG_AI_REFERENCE.md)
* [Current Changelog](../CHANGELOG.md)
