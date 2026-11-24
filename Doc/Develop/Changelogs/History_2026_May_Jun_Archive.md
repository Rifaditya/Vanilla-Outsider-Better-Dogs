# Better Dogs (v26.3) History Archive — May–Jun 2026

> [Back to Active History.md](History.md)

---

## [4.6.22+R-26.2] - 2026-06-04
### Summary
- **Release Promotion**: Promoted all recent codebase optimizations and refactoring alignments to a stable production Release.
- **Genetics & Breeding Info**: Players can selectively breed dogs over generations to optimize attribute combinations (such as breeding massive high-health Aggressive watchdogs or fast Pacifists), creating unique and specialized companions.
- **Naming Polish**: Renamed the debugging GameRule from `betterdogdebugging` to namespaced `vanilla-outsider-better-dogs:bd_debugging` (along with english and indonesian translation key updates).
- **API Cleanups**: Replaced legacy `Identifier.parse` calls with the modern `Identifier.fromNamespaceAndPath` API, pre-allocating static fields inside `WolfStatManager` to eliminate dynamic parser allocations.
- **Config Sync**: Synchronized the template config JSON with all newly introduced configuration settings.

## [4.6.21+A-26.2] - 2026-06-04
### Summary
- **Refactor**: Cleaned up and modularized player-wolf right-click interactions. Extracted all interaction handlers from `WolfInteractMixin` into a new utility class, `WolfInteractionHelper`, successfully reducing the Mixin code size from 347 lines to 92 lines to comply with a self-imposed 300 LOC limit.

## [4.6.20+A-26.2] - 2026-06-04
### Summary
- **Performance**: Optimized `PersonalityFollowOwnerGoal` by purging dynamic lambda and closure allocations in its tick and offset calculations. Switched from inline functional predicates to standard loops and static predicate fields to eliminate heap garbage.

## [4.6.19+A-26.2] - 2026-06-04
### Summary
- **Performance**: Optimized `EatGroundFoodGoal` by throttling nearby item scanning to a random 10-20 tick cooldown, and replaced Java Stream API/lambdas with a standard loop to eliminate heap allocations.

## [4.6.18+A-26.2] - 2026-06-04
### Summary
- **Performance**: Optimized `AvoidHazardsGoal` by using `BlockPos.MutableBlockPos` instead of allocating new `BlockPos` objects in the hot AI path, eliminating heap allocations.

## [4.6.17+A-26.2] - 2026-05-30
### Summary
- **Bug Fix**: Resolved size scaling override conflict in `WolfSocialMixin` where the genetic scale calculation was replaced by a random value during DNA initialization.
- **New Feature**: Added a deterministic random offset of between `-0.10` and `+0.10` based on the wolf's UUID seed to the final genetic scale in `WolfStatManager`.

## [4.6.16+A-26.2] - 2026-05-30
### Summary
- **Bug Fix**: Resolved an issue where toggling the storm anxiety GameRule (`vanilla-outsider-better-dogs:bd_storm_anxiety`) mid-game had no effect on already-spawned wolves.
- **Implementation**: Registered `WolfStormAnxietyGoal` unconditionally in `WolfAIMixin` and moved the dynamic GameRule check inside `WolfStormAnxietyGoal`'s `canUse()` and `canContinueToUse()` methods to evaluate it dynamically.

## [4.6.15+R-26.2] - 2026-05-30
### Summary
- **Stable Release**: Promoted the custom advancements/achievements system for Minecraft 26.2 to a stable production Release.
- **Bug Fix**: Includes the critical startup crash fix (`InvalidInjectionException` for `killedEntity` in `WolfMixin`) that impacted versions `4.6.11` to `4.6.13`.
- **Custom Advancements**:
  - **A Pack of Personalities**: Tame all three wolf personality types (Normal, Aggressive, and Pacifist).
  - **On Guard!**: Place a tamed wolf into Guard Mode for the first time.
  - **A Pack of Guardians**: Place a tamed wolf of each personality type into Guard Mode.
  - **Looking for a Home**: Put a tamed dog up for adoption by sneak-right-clicking them with a piece of paper.
  - **On Patrol**: Defeat a monster using an Aggressive dog in Guard Mode within its patrol radius.
  - **Self-Service**: Tamed dog automatically eats food dropped on the ground by its owner to heal itself.
  - **Keep it in the family**: Breed closely related dogs to produce an inbred runt.
  - **Fresh Blood**: Breed an inbred runt with an unrelated dog to recover the lineage.
  - **A Fresh Start**: Cure an inbred runt using a Golden Apple.
  - **Litter Size Trackers**: Double Trouble (2 puppies), Triple Threat (3 puppies), Puppy Rain (4 puppies), and the Litter Legend challenge advancement (experiencing all).

## [4.6.14+B-26.2] - 2026-05-30
### Summary
- **Bug Fix**: Resolved a critical startup crash (`InvalidInjectionException` for `killedEntity` in `WolfMixin`) that caused the game to crash.
- **Technical Detail**: Replaced the invalid inherited method injection in `WolfMixin` with a new `EntityMixin` targeting the base `Entity` class, dynamically resolving the wolf instance at runtime.

## [4.6.13+R-26.2] - 2026-05-30 [CRASHING - DEPRECATED]
### Summary
- **Stable Release**: Promoted the custom advancements/achievements system for Minecraft 26.2 to a stable production Release.
- **Custom Advancements**:
  - **A Pack of Personalities**: Tame all three wolf personality types (Normal, Aggressive, and Pacifist).
  - **On Guard!**: Place a tamed wolf into Guard Mode for the first time.
  - **A Pack of Guardians**: Place a tamed wolf of each personality type into Guard Mode.
  - **Looking for a Home**: Put a tamed dog up for adoption by sneak-right-clicking them with a piece of paper.
  - **On Patrol**: Defeat a monster using an Aggressive dog in Guard Mode within its patrol radius.
  - **Self-Service**: Tamed dog automatically eats food dropped on the ground by its owner to heal itself.
  - **Keep it in the family**: Breed closely related dogs to produce an inbred runt.
  - **Fresh Blood**: Breed an inbred runt with an unrelated dog to recover the lineage.
  - **A Fresh Start**: Cure an inbred runt using a Golden Apple.
  - **Litter Size Trackers**: Double Trouble (2 puppies), Triple Threat (3 puppies), Puppy Rain (4 puppies), and the Litter Legend challenge advancement (experiencing all).


## [4.6.12+A-26.2] - 2026-05-30 [CRASHING - DEPRECATED]
### Summary
- **New Feature**: Added custom advancement "Self-Service" (Indonesian: "Makan Mandiri") parented to "Tame an Animal" (`minecraft:husbandry/tame_an_animal`). The advancement is awarded when a player's tamed dog heals itself by automatically eating food dropped on the ground by its owner. It uses Cooked Beef as its icon and a Task frame.

## [4.6.11+A-26.2] - 2026-05-30 [CRASHING - DEPRECATED]
### Summary
- **New Feature**: Added custom advancement "On Patrol" (Indonesian: "Patroli Aktif") parented to "On Guard!" (`minecraft:husbandry/on_guard`). The advancement is awarded when a player's guarding Aggressive dog defeats a monster within its patrol radius. It uses an Iron Sword as its icon and a Task frame.

## [4.6.10+A-26.2] - 2026-05-30
### Summary
- **New Feature**: Added custom advancement "Looking for a Home" under the Husbandry tab, parented to the vanilla "Tame an Animal" advancement. The advancement is awarded when a player puts one of their tamed dogs up for adoption (by sneak right-clicking them with a piece of paper). It uses Paper as its icon and a Task frame.

## [4.6.9+A-26.2] - 2026-05-30
### Summary
- **New Feature**: Added custom advancements to track wolf litter sizes when breeding tamed wolves:
  - **Double Trouble** (2 puppies): Awarded when breeding tamed wolves results in a litter of at least 2 puppies (displays with 2 Wolf Spawn Eggs icon).
  - **Triple Threat** (3 puppies): Awarded when breeding tamed wolves results in a litter of at least 3 puppies (displays with 3 Wolf Spawn Eggs icon).
  - **Puppy Rain** (4 puppies): Awarded when breeding tamed wolves results in a maximum litter of 4 puppies (displays with 4 Wolf Spawn Eggs icon).
  - **Litter Legend** (Experiencing all): A challenge advancement awarded when the player experiences a litter of exactly 2, exactly 3, and exactly 4 puppies (displays with a Golden Carrot icon).

## [4.6.8+A-26.2] - 2026-05-30
### Summary
- **New Feature**: Added custom advancement "A Fresh Start" (`minecraft:husbandry/cure_runt`) parented to "Keep it in the family" (`minecraft:husbandry/inbred_runt`). Awarded when the player cures an inbred runt using a Golden Apple, displaying with a Golden Apple icon and a Goal frame.
- **New Feature**: Added subtle rotten flesh particle emitter for tamed runt (inbred) wolves, so players can visually identify them. This effect is optional and can be enabled/disabled via the custom GameRule `vanilla-outsider-better-dogs:bd_show_runt_particles` or debug mode `betterdogdebugging`.
- **Configuration**: Changed the default value of the `bd_enable_inbred_curing` GameRule and the config default to `true`.

## [4.6.7+A-26.2] - 2026-05-30
### Summary
- **New Feature**: Added custom advancement "Fresh Blood" parented to "Keep it in the family" (`minecraft:husbandry/inbred_runt`). The advancement is awarded when a player breeds an inbred runt parent with an unrelated wolf, successfully recovering the lineage. It uses Raw Beef as its icon and a Task frame.

## [4.6.6+A-26.2] - 2026-05-30
### Summary
- **New Feature**: Added custom advancement "Keep it in the family" parented to "The Parrots and the Bats" (`minecraft:husbandry/breed_an_animal`). The advancement is awarded when a player breeds two closely related wolves and produces an inbred runt. It uses Rotten Flesh as its icon and a Task frame.

## [4.6.5+A-26.2] - 2026-05-30
### Summary
- **New Feature**: Added custom advancement "On Guard!" parented to "Tame an Animal". The advancement is awarded when a player places a tamed wolf into Guard Mode for the first time. It uses a Bone as its icon and uses a Task frame.

## [4.6.4+A-26.2] - 2026-05-30
### Summary
- **New Feature**: Added custom advancement "A Pack of Guardians" parented to "A Pack of Personalities". The advancement is awarded when a player places a tamed wolf of each personality type (Normal, Aggressive, and Pacifist) into Guard Mode (by sneak right-clicking with a bone). It uses a Bone as its icon and uses a Goal frame.

## [4.6.3+A-26.2] - 2026-05-30
### Summary
- **Visual Polish**: Changed the custom advancement "A Pack of Personalities" display frame shape from Challenge to Goal.

## [4.6.2+A-26.2] - 2026-05-30
### Summary
- **Visual Polish**: Changed the custom advancement "A Pack of Personalities" icon from a Wolf Spawn Egg to a Bone, and its border shape to a Challenge frame.

## [4.6.1+A-26.2] - 2026-05-30
### Summary
- **Bug Fix**: Renamed the custom taming advancement and item tag data folders to singular (`advancement` and `tags/item`) to align with Minecraft 26.2+ data pack directory naming conventions. This fixes the custom advancement and food items tags not loading in-game.

## [4.6.0+A-26.2] - 2026-05-30
### Summary
- **New Feature Planning**: Initializing version 4.6.0 for a new feature implementation. Corrected all mixin file header comments and dynamic mod version logging statements.

## [4.5.18+A-26.2] - 2026-05-30
### Summary
- **Standard Alignment**: Added a custom advancement "A Pack of Personalities" under the Husbandry tab, parented by the vanilla "Tame an Animal" advancement. The advancement triggers when a player tames a wolf of each personality type: Normal, Aggressive, and Pacifist. Uses the Wolf Spawn Egg as its visual icon.

## [4.5.17+R-26.2] - 2026-05-29
### Summary
- **Stable Release**: Promoted the migration of the wolf/dog scaling system to the native Minecraft `Attributes.SCALE` attribute to a stable production Release.

## [4.5.16+A-26.2] - 2026-05-29
### Summary
- **Standard Alignment**: Migrated the custom wolf/dog scaling system to the native Minecraft `Attributes.SCALE` attribute introduced in 1.20.5+ / 26.2+. This resolves the client-side visual scaling sync issues and enables native physical size scaling (hitboxes, eye height, step height, passenger offsets, and interaction range) out of the box. Deleted redundant custom client rendering mixins (`WolfRendererMixin`, `WolfRenderStateMixin`, and `WolfRenderStateExtensions`).

## [4.5.15+R-26.2] - 2026-05-29
### Summary
- **Stable Release**: Promoted all recent codebase sanitary refactoring, optimized Pacifist sentinel scans, transient damage cooldown serialization, and cooperative follower cache spacing offsets to a stable production Release.

## [4.5.14+A-26.2] - 2026-05-29
### Summary
- **Standard Alignment**: Resolved code quality and modularity violations from the sanitary audit. Cleaned up unused imports in `WolfMixin` and `WolfGuardGoal`. Modularized `WolfMixin` by extracting complex tick-handler calculations (colored particle emitting, watchdog grace buffs, adoptable particles, and passive healing calculations) into a new dedicated helper class `WolfTickHelper`, bringing `WolfMixin` well under the 300 LOC limit (down to 276 LOC). Formatted single-line conditional blocks with brackets in `WolfMixin` and `PersonalityFollowOwnerGoal` to satisfy style guidelines.

## [4.5.13+A-26.2] - 2026-05-29
### Summary
- **Standard Alignment**: Optimized the performance of Pacifist sentinel watchdogs in `WolfGuardGoal`. Refactored the monster scan filter lambda to check vertical distance first (`dy <= 4.0`). For monsters within 4 blocks vertically, the goal immediately counts them and skips the expensive `hasLineOfSight` raycast calculation. This bypasses raycasting for over 90% of scanned mobs, preserving server TPS.

## [4.5.12+A-26.2] - 2026-05-29
### Summary
- **Standard Alignment**: Optimized allocation performance and fixed a passive healing freeze bug in `WolfMixin`. Replaced the persistent `lastDamageTime` updates in the 19-field `WolfPersistentData` attachment with a transient, `@Unique` JVM-level field in the mixin. This prevents expensive record re-creations during combat and tick updates. To preserve state persistence across reloads/restarts, the remaining combat cooldown ticks are dynamically serialized to and from the attachment record during entity save/load operations (`addAdditionalSaveData`/`readAdditionalSaveData`).

## [4.5.11+A-26.2] - 2026-05-29
### Summary
- **Standard Alignment**: Implemented a cooperative cache and dynamic scan radius for follow goal spacing offsets in `PersonalityFollowOwnerGoal`. Wolves now share active follower counts through a static registry `FollowerSpacingCache` mapped to their owner's UUID, reducing scans to a single request per interval for the entire pack. Additionally, the scan radius scales dynamically based on the last known follower count ($\min(32.0 + N \times 0.5, 64.0)$) to ensure outer dogs are counted in larger packs without bloated queries for smaller packs.

## [4.5.10+A-26.2] - 2026-05-29
### Summary
- **Standard Alignment**: Optimized the performance of tamed wolves by throttling the active follower spacing search in `PersonalityFollowOwnerGoal`. Instead of scanning a 32-block bounding box for all other tamed followers on every tick when the goal is inactive/active, the query runs once every 20-40 ticks (1-2 seconds) using a staggered, entity-randomized throttle timer. This prevents severe server TPS degradation when players have large packs of wolves.

## [4.5.9+A-26.2] - 2026-05-29
### Summary
- **Standard Alignment**: Optimized the performance of wild wolf pack leader interactions by throttling the 96-block rival pack search in `WildWolfTerritorialGoal`. Instead of scanning for rival leaders every tick when none are nearby, the search is restricted to run once every 40-80 ticks (2-4 seconds), protecting server TPS when multiple packs are loaded.

## [4.5.8+A-26.2] - 2026-05-29
### Summary
- **Standard Alignment**: Limited the range and line-of-sight conditions for guarding wolves. Normal/Aggressive guarding wolves now strictly require line-of-sight to target hostile mobs, preventing them from targeting cave monsters. Pacifist sentinels use a hybrid model: detecting mobs up to 16 blocks vertically with line-of-sight, but only up to 4 blocks vertically without line-of-sight (hearing through solid walls). Also adjusted pacing look targets to eye level (+1.0 block).

## [4.5.7+A-26.2] - 2026-05-29
### Summary
- **Standard Alignment**: Implemented a breeding-based genetic outcrossing recovery system. Breeding an inbred runt wolf with a healthy, unrelated wolf will now produce healthy offspring that inherit the parent's reconstructed, unpenalized baseline stats instead of the parent's stunted/penalized values. The active Golden Apple cure remains available but is off by default.

## [4.5.6+A-26.2] - 2026-05-29
### Summary
- **Standard Alignment**: Added a genetic recovery system (off by default) to cure inbred runt wolves. Feeding a tamed, inbred wolf a Golden Apple will clear the inbred status, reverse the inbreeding modifiers to normal levels, and dynamically update the wolf's scale and attributes. Exposes the curing feature under a configurable GameRule `bd_enable_inbred_curing` and Cloth Config GUI.

## [4.5.5+A-26.2] - 2026-05-28
### Summary
- **Standard Alignment**: Fixed Pacifist watchdog grace buff area validation and config integration. Moved grace buff application from goal ticking to entity ticking so it persists during active combat. Exposed `pacifistGuardBuffs` in configuration files and the Cloth Config GUI screen.

## [4.5.4+A-26.2] - 2026-05-28
### Summary
- **Standard Alignment**: Expanded GameRule screen tooltips with detailed explanations of behavior and default values for all configuration parameters, complying with localization guidelines.

## [4.5.3+A-26.2] - 2026-05-28
### Summary
- **Standard Alignment**: Wild wolves now roll and apply their personality-based stats (Max Health, Attack Damage, Movement Speed, and calculated Scale) immediately when they spawn, eliminating visual scale jumps and sudden attribute shifts during taming. Tamed wolves are now also correctly full-healed upon taming.

## [4.5.2+A-26.2] - 2026-05-28
### Summary
- **Standard Alignment**: Rebranded "Taming Chance" gamerules, config settings, and translations to "Spawn Chance" to better match the mechanic where wolves roll their personality when they spawn as wild wolves, not when they are tamed.


## [4.5.1+A-26.2] - 2026-05-28
### Summary
- **Breeding Mixin Performance Optimization**: Optimized the breeding method by caching the data record lookups for the parents (reducing attachment lookups by over 70%) and refactoring UUID comparisons to use plain null-gated checks, eliminating short-lived object allocations.

## [4.5.0+A-26.2] - 2026-05-28
### Summary
The **"Wolf Stat Inheritance & Inbreeding Prevention"** minor release.
- **Genetic Stat Inheritance**: Bred tamed wolves now inherit their Max Health, Attack Damage, and Movement Speed based on their parents' genes (calculated as the average of the parents' stats plus a minor triangular mutation roll).
- **Inbreeding Prevention & Penalties**: Breeding related wolves (siblings or parent-child pairings) triggers a severe genetic penalty, rendering the baby wolf tiny (runt scale), weak, slow, and fragile.
- **Kinship NBT Tracking**: Added tracking for `parent1Uuid` and `parent2Uuid` and an `inbred` status flag.

## [4.4.0+R-26.2] - 2026-05-28
### Summary
The production **Release** version of the **"Health-Based Dog Scaling"** features.
- **Dynamic Size Scaling**: Tamed wolf scales are now dynamically calculated and applied based on their rolled max health bonus.
  - Formula: `scale = 1.0 + (healthBonus * 0.012)`
  - Re-scales range from a tiny **0.808x** (worst-case Aggressive) to a massive **1.312x** (best-case Pacifist) size.
- **Dynamic Updates**: Recalculates and adjusts the scale automatically if a wolf is tamed, loaded, or if its personality is cycled via command/debug stick.
- **Stable Release**: Promoted the health-based dynamic size scaling mechanics to a stable production Release.

## [4.3.1+R-26.2] - 2026-05-28
### Summary
The production **Release** version of the **"Personality-Based Range Stats"** features.
- **Range-Based Stats**: Wolf stats (Max Health, Attack Damage, and Movement Speed) are now determined by rolling within personality-defined ranges using a symmetric triangular distribution.
  - Worst-case rolls are significantly lowered to allow for weak or "handicapped" wolves.
- **UUID Seeding**: Seeding utilizes the wolf's unique UUID, ensuring that rolled stats are deterministic, persistent, and do not change across reloads.
- **Backward Compatibility**: Existing tamed wolves automatically roll their stats once upon load if they have not been rolled yet.
- **Stable Release**: Promoted the personality range stats rolling and deterministic UUID seeding to a stable production Release.

## [4.3.0+R-26.2] - 2026-05-28
### Summary
The production **Release** version of the **"Paper Ownership Transfer (Adoption)"** features.
- **Paper Adoption Mechanics**: Owners can shift+right-click their tamed wolves with a sheet of Paper (`Items.PAPER`) to toggle them into a pending adoption state.
  - Commands the wolf to sit, stops navigation, clears target/anger, and spawns beautiful custom Rose Pink `trail` particles (`0xFF99BB`) floating towards a target offset.
  - Another player can right-click the adoptable wolf with an empty hand to adopt it, immediately shifting ownership.
  - The adoption is cancelled if the owner interacts normally with the wolf or if the wolf takes damage.
- **Stable Release**: Promoted the Paper Adoption ownership transfer mechanics and custom Rose Pink particle trails to a stable production Release. Archived previous `4.3.0+A-26.2` jars.

## [4.2.2+R-26.2] - 2026-05-28
### Summary
The production **Release** version of the **"Calm Down Interaction & Named Guard Mode Overlay"** features.
- **Stable Release**: Promoted the Calm Down empty-hand Shift-Right Click interaction and the Named Guard Mode activation/deactivation overlays to a stable production Release.

## [4.2.1+A-26.2] - 2026-05-28
### Summary
The **"Named Guard Mode Overlay"** patch.
- **Named Guard Mode overlay**: Modified the Guard Mode activation and deactivation messages to include the name of the wolf (e.g., "Guard Mode Activated for Buddy at X, Y, Z").

## [4.2.0+A-26.2] - 2026-05-28
### Summary
The **"Calm Down Interaction"** minor release.
- **Calm Down Interaction**: Added shift+right-click empty hand interaction for owners to calm down their tamed wolves.
  - Commands the wolf to sit, stops navigation, and completely clears its attack target and anger state (`stopBeingAngry()`).
  - Plays a whine sound and spawns smoke particles to indicate calming down.
  - Displays an action bar/overlay message: `"Calmed down [Wolf Name]"`.

## [4.1.2+R-26.2] - 2026-05-28
### Summary
The production **Release** version of the **"Personality-Based Flee Probability & Low Health Fleeing"** features.
- **Stable Release**: Promoted the low health fleeing behavior and personality-based fleeing probabilities to a stable production Release.

## [4.1.1+A-26.2] - 2026-05-28
### Summary
The **"Personality-Based Flee Probability"** patch.
- **Personality-Based Fleeing**: Refactored low health fleeing behavior so that a wolf's probability of running away is checked based on its personality.
  - ?? **Pacifist**: 100% chance by default.
  - ? **Normal**: 50% chance by default.
  - ?? **Aggressive**: 10% chance by default.
- **Gamerule & Config Integration**: Exposes the three personality-based flee probability settings via new GameRules (`bd_paci_flee_chance`, `bd_normal_flee_chance`, `bd_aggro_flee_chance`) and corresponding config values.
- **Strict Versioning**: Bumped the patch version to `4.1.1+A-26.2` and archived previous `4.1.0` jars.

## [4.1.0+A-26.2] - 2026-05-28
### Summary
The **"Low Health Fleeing & Version Upgrade"** minor release.
- **Low Health Fleeing AI**: Added a new survival AI goal where all wolves (both tamed and wild) attempt to flee when their health falls below 30% of their maximum health.
- **Hybrid Configuration**: Exposed the fleeing behavior under a new namespaced game rule `bd_flee_low_health` and a config toggle in `BetterDogsConfig` and the ModMenu/Cloth Config screen.
- **Strict Versioning**: Bumped the minor version to `4.1.0+A-26.2` and archived previous `4.0.1` jars.

## [4.0.1+A-26.2] - 2026-05-27
### Summary
The **"Storm Anxiety Personality Gating"** patch. Gates the Storm Anxiety behavior based on the wolf's personality.
- **Storm Anxiety Personality Gating**: Wolf reaction to thunderstorms is now personality-dependent.
  - ?? **Aggressive**: Unaffected by thunderstorms (fully immune to storm anxiety).
  - ?? **Pacifist**: Highly anxious, with a 3x higher trigger chance than normal.
  - ? **Normal**: Standard chance (1% per tick / configured value).
- **Versioning Alignment**: Bumped the patch version to `4.0.1+A-26.2` following the Strict Versioning Mandate, and archived previous `4.0.0` jars to `/Archive/builds/`.

## [4.0.0+A-26.2] - 2026-05-27
### Summary
The Minecraft **26.2 Pre-Release 1 Port** and **Compatibility Upgrade**.
- **Minecraft 26.2 Port**: Ported the codebase to Minecraft `26.2-pre-1` and aligned with Fabric API `0.149.2`.
- **Entity Registry Relocation**: Refactored entity type references to use the new `EntityTypes` registry class, replacing legacy `EntityType` references (e.g. `EntityType.WOLF` to `EntityTypes.WOLF`) to prevent compilation and runtime errors.
- **Pre-Release & Future Compatibility**: Updated `fabric.mod.json` dependency constraints to `"minecraft": ">=26.2-"` (wildcard range) to allow the mod to load on both `26.2` pre-releases and the final/future `26.2` releases.
- **Gradle & Toolchain Updates**: Configured the build toolchain to compile using JDK 25 and added automatic local Maven repository lookup for locally compiled dependencies.

## [3.7.1+A-26.1.2] - 2026-05-26
### Summary
The **"Guarding Sit Lock Fix"** patch. Resolves sit command issues for guarding wolves of all personalities.
- **Normal Guarding Sit Fix**: Fixed a bug where Normal personality sentinel wolves immediately stood back up when manually ordered to sit.
- **Guarding Sit Lock**: Manual sitting now correctly pauses the active `WolfGuardGoal` and gates custom target selectors (e.g. `AggressiveTargetGoal`), ensuring guarding wolves remain sitting and locked from movement/attack, matching vanilla behavior.

## [3.7.0+A-26.1.2] - 2026-05-26
### Summary
The **"Dynamic Follower Spread Scaling"** update. Scales the follow/spread spacing of wild and tamed wolf packs dynamically based on the number of active followers.
- **Dynamic Pack Spread Scaling**: Tamed and wild wolves now space themselves out wider as the pack size $N$ increases. Spacing is calculated mathematically using the square root formula: $f(N) = \text{multiplier} \times \sqrt{N - 1}$.
- **Tamed Follow Spacing**: Tamed wolves following a player dynamically increase their follow start and stop thresholds, preventing overcrowding.
- **Wild Flock Spacing**: Wild pack members dynamically adjust cohesion and separation radii during flocking, resulting in organic pack formations.
- **New Spacing GameRules**: Added 4 new native GameRules to configure the spacing multipliers and limits:
  - `bd_tamed_pack_spread_multiplier` (Default: 120 = 1.2x)
  - `bd_tamed_pack_spread_max` (Default: 60 = 6.0 blocks max extra)
  - `bd_wild_pack_spread_multiplier` (Default: 80 = 0.8x)
  - `bd_wild_pack_spread_max` (Default: 40 = 4.0 blocks max extra)

## [3.6.8+R-26.1.2] - 2026-05-26
### Summary
The production **Release** version of the **"Always-On Guard Mode Personality Particles & Server-Side Compatibility"** updates.
- **Removed Debug Gate on Guard Mode Particles**: Personality particles (red, yellow, and green dust particles) are now always emitted when a tamed wolf is in active Guard Mode, regardless of whether the debug game rule is enabled.
- **Client-Side Optional Isolation**: Cleanly separates client-only rendering modules (`WolfRendererMixin` and `WolfRenderStateMixin`) into a client-exclusive mixin configuration with client environment gating. Allows headless dedicated servers to run without client-side classes.
- **Dynamic GameRule Routing**: Ensures all custom mod settings and parameters safely resolve via `DynamicGameRuleManager` dimension hooks, seamlessly checking integrated server states inside singleplayer clients while cleanly falling back to defaults on multiplayer connections to prevent desyncs.

## [3.6.7+R-26.1.2] - 2026-05-26
### Summary
The production **Release** version of the **"Sentinel Watchdog Alarm Particle Height Tuning"** patch.
- **Raised Sentinel Alarm Note Particles**: Elevated the spawn offset for Pacifist sentinel warning `NOTE` particles from `Y + 0.8` to `Y + 1.2` to ensure they spawn above the wolf rather than inside its model.

## [3.6.6+R-26.1.2] - 2026-05-26
### Summary
The production **Release** version of the **"Colored Dust Particles & Guard Mode Particle Fix"** updates.
- **Subtle Colored Dust Particles**: Replaced ambient guard mode particles (formerly `ASH`, `WHITE_ASH`, `MYCELIUM`) and debug particles (formerly `FLAME`, `NOTE`, `HAPPY_VILLAGER`) with tiny, custom-colored `dust` particles at a subtle `0.5f`/`0.6f` scale.
  - ?? **Aggressive**: Red particle (`0xFF3333`)
  - ?? **Normal**: Gold/Yellow particle (`0xFFD700`)
  - ?? **Pacifist**: Green/Teal particle (`0x00FF88`)
- **Guard Mode Particle Gating**: Modified personality particle ticking (Flame, Note, Happy Villager) in `WolfMixin.java` to strictly check `betterdogs$isGuardMode()`. Personality particles now only emit when the wolf is on active Guard Mode.
- **Client Synchronization Fix**: Migrated debugging particle ticks in `WolfMixin.java` to run exclusively on the server side and transmit using `serverLevel.sendParticles()`. This ensures clients receive the actual, correct personality particles (Flame for Aggressive, Note for Pacifist, Happy Villager for Normal) without requiring network sync for the client-side attachments.

## [3.6.5+A-26.1.2] - 2026-05-26
### Summary
The **"Guard Mode Particle Fix"** patch. Restricts personality particle emissions to Guard Mode only and implements server-side synchronization.
- **Guard Mode Particle Gating**: Modified personality particle ticking (Flame, Note, Happy Villager) in `WolfMixin.java` to strictly check `betterdogs$isGuardMode()`. Personality particles now only emit when the wolf is on active Guard Mode.
- **Client Synchronization Fix**: Migrated debugging particle ticks in `WolfMixin.java` to run exclusively on the server side and transmit using `serverLevel.sendParticles()`. This ensures clients receive the actual, correct personality particles (Flame for Aggressive, Note for Pacifist, Happy Villager for Normal) without requiring network sync for the client-side attachments.

## [3.6.4+A-26.1.2] - 2026-05-26
### Summary
The **"ConfigHelper Migration"** update. Refactors configuration loading and saving to use the standard centralized API in `DasikLibrary`.
- **Config Migration**: Refactored `BetterDogsConfig` to delegate all deserialization, serialization, backup, size limit checking, and atomic swap writes to the library's centralized `ConfigHelper` class.
- **Runtime Dependency Guard**: Added a runtime version verification at startup. If `DasikLibrary` version is less than `1.7.4` (or `ConfigHelper` is missing), the game aborts and throws a Minecraft `ReportedException` wrapping a descriptive `CrashReport`: `"Better Dogs: DasikLibrary version mismatch! Requires version 1.7.4 or higher. Please update your mods."`
- **?? WARNING**: This version requires the newest **`DasikLibrary 1.7.4`** or higher. Older library versions will cause startup crashes.

## [3.6.3+A-26.1.2] - 2026-05-26
### Summary
The **"DasikLibrary GameRule Helper Migration"** update. Refactors Better Dogs' GameRule queries to call the standard helpers in `DasikLibrary` directly.
- **Direct Library Integration**: Completely removed local duplicate GameRule value conversion helper methods (`getPct`, `getProb`, `getChance`, `getDecileFloat`, `getInt`, `getBoolean`) from `BetterDogsGameRules.java`.
- **Mod-Wide Refactoring**: Cleanly refactored all 21 source and mixin classes to query game rules directly via `DynamicGameRuleManager`.
- **?? WARNING**: This version requires the newest **`DasikLibrary 1.7.3`** or higher. Older library versions will cause crash-on-startup due to missing GameRule helper APIs.

## [3.6.2+A-26.1.2] - 2026-05-24
### Summary
The **"Centralized Dual-Side Game Rule Lookup"** update. Refactors game rule querying to support dual-side client/server execution using DasikLibrary 1.7.2.
- **Dynamic Game Rule Delegation**: Refactored `BetterDogsGameRules.java` to delegate config queries to the new dual-side getters in `DynamicGameRuleManager` from DasikLibrary.
- **Client-Side Integrated Server Fetch**: Accesses the integrated server from the client thread in singleplayer mode, enabling accurate local gamerule checks without client-side config desync.
- **Library Version Bump**: Updated project dependencies to require `dasik-library` version `1.7.2`.

## [3.6.1+R-26.1.2] - 2026-05-23
### Summary
The production **Release** version of the **"Guard Attack Hotfix & Server-Side Optional"** updates. Resolves target attack issues for guard dogs and optimizes mixin loading for dedicated servers.
- **Combat AI Yielding**: Modified `WolfGuardGoal.java` so that it yields and stops running when a valid target is within its chase range. This releases the movement (`MOVE`) and looking (`LOOK`) flags, allowing the wolf's melee attack goals (`MeleeAttackGoal`) to execute attacks correctly.
- **Chase Boundary Enforcement**: Once a target is killed, lost, or escapes past the max chase boundaries, the `WolfGuardGoal` automatically re-asserts itself, clears the target, and resumes patrol/sentry duties.

## [3.6.0+A-26.1.2] - 2026-05-23
### Summary
The **"Server-Side Optional & Client Mixin Separation"** update. Optimizes mixin loading to support dedicated server environments and allow vanilla client compatibility.
- **Client Mixin Isolation**: Extracted `WolfRendererMixin` and `WolfRenderStateMixin` into a dedicated `vanilla-outsider-better-dogs.client.mixins.json` configuration file.
- **Dedicated Server Protection**: Marked the new client mixin configuration as `"environment": "client"`, ensuring the Fabric Loader ignores client-only mixins on headless dedicated servers and preventing classloading crashes.
- **Vanilla Client Compatibility**: Ensured the mod works fully on the server-side, enabling vanilla clients to connect without requiring mod installation, while maintaining complete feature parity in singleplayer.

## [3.5.4+R-26.1.2] - 2026-05-23
### Summary
The production **Release** version of the **"Guard Mode Gating & Shift-Toggle"** features.
- **Shift + Right Click Toggle**: Guard Mode toggles now strictly require Shift + Right Click (sneaking) when holding a bone, preventing accidental activation or mode changes during wolf taming.
- **Ownership Gating**: Enforced `wolf.isOwnedBy(player)` check on Guard Mode and manual sitting interactions to prevent other players from toggling or controlling the guard status of your tamed wolves.
- **Bone Sitting Prevention**: Explicitly exempted `Items.BONE` from triggering manual sitting, ensuring bone clicks strictly toggle Guard Mode and do not trigger sitting.
- **Follow Owner Toggle**: Ensures deactivating Guard Mode clears coordinates, resets manual sitting, stands the wolf up, and allows follow-owner AI to resume cleanly.

## [3.5.3+A-26.1.2] - 2026-05-23
### Summary
The **"Guard Mode Gating & Follow-Owner Toggle"** patch. Restricts Guard Mode toggling to owners and refines sitting posture gating.
- **Ownership Gating**: Enforced `wolf.isOwnedBy(player)` check on Guard Mode and manual sitting interactions to prevent other players from toggling or controlling the guard status of your tamed wolves.
- **Bone Sitting Prevention**: Explicitly exempted `Items.BONE` from triggering manual sitting, ensuring bone clicks strictly toggle Guard Mode and do not trigger sitting.
- **Follow Owner Toggle**: Ensures deactivating Guard Mode clears coordinates, resets manual sitting, stands the wolf up, and allows follow-owner AI to resume cleanly.

## [3.5.2+A-26.1.2] - 2026-05-23
### Summary
The **"Guard Mode Stand-up Alignment"** patch. Resolves visual desyncs and posture errors when toggling Guard Mode.
- **Client-Server Standing Alignment**: Forces the wolf to stand up (`setOrderedToSit(false)`) on both client and server when toggling Guard Mode, eliminating client-side predictive sitting desyncs.
- **Stand Up on Deactivation**: Resets manual sitting states and forces the wolf to stand up when Guard Mode is deactivated, allowing it to immediately follow its owner.

## [3.5.1+A-26.1.2] - 2026-05-23
### Summary
The **"Tamed Wolf Guard Mode Alpha Polish"** release. Polishing the Guard Mode feature with performance optimizations, security gating, and localizations.
- **Audit Compliance Cleanup**: Addressed wildcard imports and converted verification citations to point to targeted vanilla classes (`Wolf.java` and `NearestAttackableTargetGoal.java`).
- **Tick Performance Optimization**: Time-sliced the heavy entity scanning queries and `MobEffectInstance` allocations inside `WolfGuardGoal.java` to run once every 20 ticks (1 second) instead of every tick in the hot path.
- **Debug Stick Security Gating**: Gated the Debug Stick wolf interaction behind `COMMANDS_GAMEMASTER` permission level checks to prevent regular survival players from utilizing developer features.
- **Localization Alignment**: Synced missing config category keys in `id_id.json` with fully localized Indonesian translations.

## [3.5.0+A-26.1.2] - 2026-05-23
### Summary
The **"Tamed Wolf Guard Mode"** update. Adds guard mode behavior for tamed wolves.
- **Guard Mode Activation**: Right-clicking a tamed wolf with a bone toggles Guard Mode (consuming exactly 1 bone) and anchors the wolf to its current block.
- **Mathematical Patrol Patterns**:
  - **Aggressive**: Paces in a circular/polygon shape along its outer perimeter sweep (80% range), pausing to scan outward for threats.
  - **Normal**: Sit-sentry posture at the guard post (range = 0), or radial star patrols outward and back (range > 0).
  - **Pacifist**: Close protective orbital circular pacing around the post.
- **Auto-Targeting & Chase Caps**: Normal and Aggressive guards automatically attack hostiles within their range (16/24 blocks) but are capped from chasing targets too far (20/32 blocks) to prevent them being lured away.
- **Watchdog Alarms & Grace Buffs**: Pacifist sentinels whine and emit warning note particles when hostiles approach, and optionally apply Regeneration and Resistance to owners/allies if `bd_pacifist_guard_buffs` gamerule is enabled.
- **Subtle Foot Particles**: Displays very subtle, non-distracting foot particles every 4 seconds (`ASH` for Aggressive, `WHITE_ASH` for Normal, `MYCELIUM` for Pacifist) to distinguish guard mode.
- **Manual Sitting Standby**: Right-clicking with an empty hand sits the dog down manually, which temporarily pauses guard updates.
- **Gamerule Customization**: Added configurable patrol range rules (`bd_guard_patrol_range_aggressive`, `bd_guard_patrol_range_normal`, `bd_guard_patrol_range_pacifist`).
