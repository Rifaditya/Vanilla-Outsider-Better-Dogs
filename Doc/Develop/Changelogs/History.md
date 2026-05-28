# Better Dogs - Historical Changelog

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
  - 🟢 **Pacifist**: 100% chance by default.
  - ✨ **Normal**: 50% chance by default.
  - 💢 **Aggressive**: 10% chance by default.
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
  - 💢 **Aggressive**: Unaffected by thunderstorms (fully immune to storm anxiety).
  - 🟢 **Pacifist**: Highly anxious, with a 3x higher trigger chance than normal.
  - ✨ **Normal**: Standard chance (1% per tick / configured value).
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
  - 🔴 **Aggressive**: Red particle (`0xFF3333`)
  - 🟡 **Normal**: Gold/Yellow particle (`0xFFD700`)
  - 🟢 **Pacifist**: Green/Teal particle (`0x00FF88`)
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
- **⚠️ WARNING**: This version requires the newest **`DasikLibrary 1.7.4`** or higher. Older library versions will cause startup crashes.

## [3.6.3+A-26.1.2] - 2026-05-26
### Summary
The **"DasikLibrary GameRule Helper Migration"** update. Refactors Better Dogs' GameRule queries to call the standard helpers in `DasikLibrary` directly.
- **Direct Library Integration**: Completely removed local duplicate GameRule value conversion helper methods (`getPct`, `getProb`, `getChance`, `getDecileFloat`, `getInt`, `getBoolean`) from `BetterDogsGameRules.java`.
- **Mod-Wide Refactoring**: Cleanly refactored all 21 source and mixin classes to query game rules directly via `DynamicGameRuleManager`.
- **⚠️ WARNING**: This version requires the newest **`DasikLibrary 1.7.3`** or higher. Older library versions will cause crash-on-startup due to missing GameRule helper APIs.

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

## [3.4.18+A-26.1.2] - 2026-05-22
### Summary
The **"Mixin Modularization & Audit Cleanup"** update. Implements modular mixins to satisfy codebase complexity limits and resolves all static audit violations.
- **Mixin Modularization**: Extracted the Social Brain AI, genetics (DNA), affinity mapping, and social scheduler tick loops out of `WolfMixin.java` into a new dedicated `WolfSocialMixin.java` to stay under the 300 LOC limit.
- **Import Hygiene**: Batch-cleaned all duplicate and unused imports across the 75+ Java/Kotlin files in the project.
- **Verified Citations**: Prepended standard `// Verified against: ... (26.1.2+)` headers to the beginning of all source code files.
- **Platform Sync**: Updated Modrinth and CurseForge description pages to explicitly declare Java 25 requirements and include the 6 missing features (Litter, Zoomies, Group Howl, Storm Anxiety, Scavenge/Feeding, Dynamic Spawning) for full concept parity. Added raw GitHub URLs to CurseForge media links to prevent cached image load failures.

## [3.4.17+A-26.1.2] - 2026-05-21
### Summary
The **"Continuous Territorial Cascades"** update. Fixes issues with wild wolf pack leaders ignoring each other after initial dispute interactions and enables seamless pack merges.
- **Bi-directional Busy State**: Marks both wolves involved in a territorial dispute as busy. Checks initiator busy state before running.
- **Merge Yielding Fix**: Fully implemented loser yielding (`loseConflict()`) where the initiator merges its pack and followers into the rival's pack when they lose the dominance decision.
- **Fight Defeat Merging**: Symmetrically checks the initiator's health during a territorial war; if the initiator is defeated or killed, their pack merges into the winner's pack.
- **Rapid Debug Cooldown**: Reduced the territorial dispute cooldown to 1 second (20 ticks) when debugging mode is active, allowing quick automated testing of scenarios.
- **Bi-directional War State**: Ensured both disputing wolves' social states are synchronized to war during active combat.

## [3.4.16+R-26.1.2] - 2026-05-21
### Summary
The production **Release** version of the **"Clean Gamerules"** and **"Optional GUI Integration"** features.
- **Divider Removal**: Completely deleted all dummy visual divider gamerules (`div_general`, `div_health`, etc.) from the registry, removing interactive ON/OFF buttons on dividers. All Better Dogs settings are now listed under a single, unified category.
- **Optional GUI Support**: Fully integrated with **Cloth Config API** and **ModMenu** to allow GUI-based customization of the mod's 50+ settings.
- **Dedicated Server Compatibility**: GUI construction and Cloth Config calls are lazily loaded inside client-only entrypoints, ensuring safety on dedicated servers.
- **Config Relocation**: Moved global configuration path directly to the config root folder as `config/vanilla-outsider-better-dogs.json` to prevent namespace collisions.
- **Dependency Cleanliness**: Cleaned up the `fabric.mod.json` dependencies, setting `dasik-library` to wildcard dependency `*` for better installation compatibility (since players are unlikely to install outdated library versions, the wildcard is used for development convenience).

## [3.4.15+A-26.1.2] - 2026-05-21
### Summary
The **"Clean Gamerules"** update. Removed visual divider gamerules to avoid showing dummy toggles in the game rules selection screen.
- **Divider Removal**: Completely deleted all dummy visual divider gamerules (`div_general`, `div_health`, etc.) from the registry, removing interactive ON/OFF buttons on dividers. All Better Dogs settings are now listed under a single, unified category clean and simple.

## [3.4.14+A-26.1.2] - 2026-05-21
### Summary
The **"Optional GUI Integration"** update. Implemented optional configuration GUI via Cloth Config and ModMenu.
- **Optional Dependencies**: Mod Menu configuration gear button in the Mods list redirects players to a highly detailed, categorised Cloth Config screen. Added repositories and dependencies in `build.gradle` and suggestions in `fabric.mod.json`.
- **Dedicated Server Compatibility**: GUI construction and Cloth Config calls are lazily-loaded inside client-only entrypoints, ensuring safety on dedicated servers.
- **Config Relocation**: Moved global configuration path from `config/betterdogs/config.json` directly to the config root folder as `config/vanilla-outsider-better-dogs.json` to prevent namespace collisions.

## [3.4.13+A-26.1.2] - 2026-05-21
### Summary
The **"Mixin Refmap"** update. Resolved Mixin refmap warnings in production environments.
- **Refmap Inclusion**: Added an empty `vanilla-outsider-better-dogs-refmap.json` file to the JAR resources. This satisfies the Mixin configuration while avoiding compile-time dependency on nonexistent mappings in the non-obfuscated environment.
- **Warnings Suppressed**: Suppressed Knot/Fabric console warnings regarding missing reference maps during startup.

## [3.4.12+A-26.1.2] - 2026-05-21
### Summary
The **"Config Subdirectory"** update. Relocated the global configuration file and renamed it to `config.json` inside a dedicated `betterdogs` subfolder.
- **Improved Compatibility**: Prevents name collision issues with other mods by organizing configuration within a `betterdogs/` directory (resolving Cloth Config Custom Name auto-detection).
- **Subfolder Layout**: Global configuration path is now `config/betterdogs/config.json`, alongside its respective `.tmp` and `.bak` swap files.

## [3.4.11+A-26.1.2] - 2026-05-21
### Summary
The **"Bundled Config"** update. Bundled a default `betterdogs.json` configuration file inside the mod JAR resources.
- **Modpack Convenience**: Enables modpack creators to easily pre-configure default settings in their packs without launching the game first.
- **Resource Copying**: On first launch, the mod extracts the bundled default configuration directly into the game's `config/` directory instead of programmatically generating empty values.
- **Robust Fallback**: If the bundled resource is missing or copying fails, the mod gracefully falls back to generating code defaults.

## [3.4.10+A-26.1.2] - 2026-05-21
### Summary
The **"Unified Panel"** update. Consolidated the 6 separate GameRule categories into a single unified category with visual divider rules separating each sub-section.
- **Single Category**: All Better Dogs GameRules now appear under one **"Vanilla Outsider: Better Dogs"** category instead of 6 separate ones.
- **Section Dividers**: Added 6 empty boolean GameRules (`div_general`, `div_health`, `div_social`, `div_war`, `div_litter`, `div_spawning`) that act as visual section headers with `────` decorations.
- **Rule Reordering**: Reorganized registration order so Health, Social, War, Litter, and Spawning rules are grouped contiguously under their respective dividers.
- **Localization**: Full English and Indonesian translations for the unified category and all divider labels.
- **⚠️ Reset Warning**: Existing GameRule values may be reset to defaults due to this structural change.


## [3.4.9+B-26.1.2] - 2026-05-21
### Summary
The **"Sanitary Alignment"** update. Conducted a thorough code audit and resolved technical debt, naming compliance issues, and internal codename references.
- **Mixin Naming Compliance**: Renamed and added `@Unique` to all custom fields and helper methods in `WolfBreedingMixin.java` to prevent naming collisions.
- **Banned Code Cleanup**: Removed `System.out.println` from `AnimalMixin.java` and redirected debug info to the modular `WolfDebugLogger` using the official `Logger` API.
- **Anonymization Compliance**: Fully sanitized entrypoint classes (`BetterDogs.java`) and utility classes (`WolfCommandHelper.java`) to remove any references to internal development codenames.


## [3.4.8+R-26.1.2] - 2026-05-16
### Summary
The **"Categorical Mastery"** update. Reorganized the GameRule configuration UI into themed categories for improved usability and organization.
- **Themed Categorization**: Reorganized all 50+ GameRules into 6 distinct categories:
    - **VO: Better Dogs - War**: Territorial matrix and rivalry settings.
    - **VO: Better Dogs - Litter**: Breeding genetics and litter size rules.
    - **VO: Better Dogs - Health**: HP, speed, and damage modifiers.
    - **VO: Better Dogs - Social**: AI behaviors, howling, and follow distances.
    - **VO: Better Dogs - Spawning**: Cluster sizes and taming chances.
    - **VO: Better Dogs - General**: Utility toggles and environmental reactive rules.
- **Localization**: Added full support for the new category labels in both English (`en_us`) and Indonesian (`id_id`).
- **⚠️ Mandatory Warning**: Your gamerule might be reseted as I split it into many category and i changed stuff.


## [3.4.7] - 2026-05-16
### Summary
The **"Architectural Alignment"** update. Finalized the architectural transition to strict namespacing standards, resolving configuration data loss and logic hoarding.
- **Registry Namespacing**: Refactored all custom GameRules to use the `vanilla-outsider-better-dogs` namespace (e.g., `vanilla-outsider-better-dogs:bd_storm_anxiety`). This prevents silent data loss caused by vanilla registry shadowing.
- **Localization Sync**: Synchronized `en_us.json` and `id_id.json` with the new namespaced GameRule keys for perfect UI parity.
- **Territorial Probability Matrix**: Fully integrated the dynamic outcome matrix into the `WildWolfTerritorialGoal` AI. Pack disputes now follow synchronized, personality-driven probabilities for War, Merge, or Retreat.
- **Config versioning**: Incremented internal config version to `3470` to trigger necessary data migration and backup procedures.
- **Cleanup**: Purged legacy "Snapshot 11" comments and documentation debt from the entrypoint classes.


## [3.4.6] - 2026-05-16
### Summary
The **"Probability Matrix"** update. Overhauled territorial outcomes with a dynamic, personality-driven chance system.
- **Dynamic Outcomes**: Pack disputes now use a weighted probability matrix (War/Merge/Retreat) based on the personality pairing of the leaders:
    - **Aggro vs Aggro**: 80% War, 10% Merge, 10% Run
    - **Aggro vs Normal**: 50% War, 40% Merge, 10% Run
    - **Aggro vs Pacifist**: 10% War, 50% Merge, 40% Run
    - **Normal vs Normal**: 20% War, 50% Merge, 30% Run
    - **Normal vs Pacifist**: 5% War, 45% Merge, 50% Run
    - **Pacifist vs Pacifist**: 0% War, 50% Merge, 50% Run
- **Merge Hierarchy**: During a merge, leadership is automatically granted to the higher-ranked personality (Aggressive > Normal > Pacifist).


## [3.4.5] - 2026-05-16
### Summary
The **"Nuanced Leadership"** update. Further refined territorial outcomes to respect Pacifist instincts and combat priority.
- **Merge Logic Refinement**: Pacifist leaders now always choose to retreat rather than merge into another pack, prioritizing their own pack's autonomy.
- **Combat Priority**: If both leaders choose to fight (Aggressive vs Normal), they will now engage in a duel regardless of rank, rather than an automatic merge.
- **Aggressive Dominance**: Aggressive leaders still win automatic merges against Normal leaders who choose not to fight.


## [3.4.4] - 2026-05-16
### Summary
The **"Dominance Hierarchy"** update. Refined the territorial dispute logic to enforce personality-based leadership outcomes.
- **Merge Logic**: Implemented a strict personality hierarchy (Aggressive > Normal > Pacifist). Higher-ranked leaders now automatically win dominance disputes against lower-ranked rivals, merging their packs.
- **Consistency**: Disputes between leaders of the same rank still use the existing chance-based duel/retreat logic.


## [3.4.3] - 2026-05-16
### Summary
The **"Command UX"** update. Fixed command synchronization issues that caused debug commands to appear red (Unknown) in the chat bar.
- **UX Improvement**: Moved GameRule logic to execution phase. Commands are now always visible to OPs, preventing "Red Text" confusion when toggling debug mode.
- **Production Alignment**: Incremented version for final release parity.


## [3.4.2] - 2026-05-16
### Summary
The **"C2ME Compatibility"** update. Resolved a critical binary mismatch with DasikLibrary that caused crashes during multi-threaded chunk generation.
- **Dependency Hardening**: Updated `fabric.mod.json` to strictly require `dasik-library >= 1.7.0`.
- **C2ME Conflict Fix**: Successfully resolved `NoSuchMethodError` for `GroupParameters` during worker-thread mob spawning.
- **Production Stability**: Fixed refmap naming mismatch to ensure stable Mixin transformation in non-development environments.

## [3.4.2+build.1] - 2026-05-16
### Summary
The **"Debug Expansion"** update. Significantly enhanced the wolf interaction testing suite and improved production stability.
- **Debug Action Expansion**: Expanded `/betterdogs debug action` with secondary target support and automatic neighbor detection.
- **New Actions**: Added support for `play_fight`, `retaliation`, `discipline`, `territorial_dispute`, and `territorial_war`.
- **GameRule Protection**: Gated the `/betterdogs` command tree behind the `betterdogdebugging` GameRule for safer production use.
- **Stability Fix**: Added missing `refmap` to Mixin configuration to ensure compatibility in remapped/production environments.

## [3.4.1] - 2026-05-16
### Summary
The **"Technical Patching"** update. Focused on asset cleanup and debug command stability for territorial testing.
- **Asset Purge**: Removed unimplemented "Play Bow" animation references from the codebase to maintain architectural integrity.
- **Debug Stability**: Refactored the `/betterdogs debug territory` command to anchor wolf spawning to the surface level using the Heightmap API, ensuring reliable test scenarios in varying terrain.

## [3.4.0] - 2026-05-12
### Summary
The **"Wolf Litters"** update. Tamed wolves can now have multiple puppies in one breed, mirroring real-world dog litters.
- Added `bd_wolf_litter_max_size` and `bd_wolf_litter_extra_chance` GameRules.
- Puppies in a litter inherit parent traits independently.

## [3.3.1] - 2026-05-11
### Summary
The **"Social Politeness"** update. Implemented 1v1 dispute locking via the `bd_territorial_exclusive_disputes` GameRule to prevent chaotic interactions in high-density pack scenarios.
- Leaders now check if a rival is "Busy" before challenging.
- Added `TERRITORIAL_DISPUTE` social state.


## [3.3.0] - 2026-05-11
### Summary
The **"Territorial Balancing"** update. Introduced granular controls for pack spawn rates and territorial interaction distances to allow server owners to fine-tune the frequency of pack wars and social friction.

### Changes
- Added `bd_territorial_search_radius` GameRule.
- Added `bd_wolf_pack_cluster_size` GameRule.
- Added `bd_wolf_spawn_density_boost` GameRule.
- Implemented `Reinforcement Spawn` logic in `WolfMixin`.
- Updated `WildWolfTerritorialGoal` to support dynamic search radii.
- Incremented version to `3.3.0`.

This file contains the archived changelogs for historical versions of Better Dogs for the 26.1 Snapshot series.

## [3.2.0] - 2026-05-11

### Added
- **Feature: Wild Wolf Territorial Rivalry (Final Release)**:
  - **1v1 Leader Duels**: Enforced isolation between rival leaders during wars via `WolfCombatHooks`.
  - **Pack Member Brawls**: Integrated `WildWolfPackWarGoal` allowing followers to engage in secondary combat.
  - **Personality Logic**: Pacifist members now avoid wars unless attacked, while Aggressive members join immediately.
  - **Fatal Outcomes**: Integrated `bd_territorial_fatal_chance` (0-100) for lethal dispute resolution.
- **Feature: Wild Personality AI (New Default)**:
  - New GameRule: `bd_wild_personality_behavior` (Boolean).
  - Wild wolves now naturally hunt, socialise, and defend territory out of the box in new worlds.
- **Hardening & Optimization**:
  - **Logic Hardening**: Moved all custom entity logging (Spawn, Tame, Social, Ambient) to the `betterdogdebugging` GameRule gate.
  - **Architectural Cleanup**: Removed duplicate debug log entries in Mixin layers.
  - **Performance Optimization**: Powered by **DasikLibrary 1.7.0** for high-efficiency AI anchoring.
- **Debug Tools**:
  - Added `/betterdogs debug territory` command to spawn dual-pack test scenarios.
- **API Alignment**:
  - Refactored all entity movement calls from `moveTo` to `snapTo` to comply with **26.1.2 Mojang Mappings**.
  - Updated `setTame` signatures for 26.x environment.

## [3.1.37+build.10] - 2026-05-12

### Added
- **AI Refactor: Stable Wild Pack Leadership**:
  - **Refactored `WildWolfFollowLeaderGoal`**: Wild wolves now follow their pack leader with logic mirroring vanilla `FollowOwnerGoal` (teleportation and consistent movement).
  - **Stable Leadership**: Pack members now lock onto their leader persistently. Leadership is saved to NBT and survives world reloads.
  - **Tame Interaction**: Taming a leader now allows the wild pack to follow the player by proxy. Taming a member correctly transitions them to standard owner-following behavior.
  - **Improved Performance**: Reduced frequency of expensive social pathfinding calculations.

## [3.1.37+build.9] - 2026-05-11

### Added
- **Debugging Mode**: Introduced the `betterdogdebugging` GameRule.
  - Enables detailed AI logging in the server console.
  - Adds visual particles (Flame/Note/Happy Villager) above wolves to indicate personality.
  - **Debug Stick Integration**: Allows cycling through personalities and social scales using the vanilla Debug Stick.
- **Maintenance**: Fixed compilation errors related to `DasikLibrary` API changes in `WildWolfFollowLeaderGoal`.

## [3.1.37+build.5] - 2026-03-04

### Changed
- **Sound Polish**: Replaced the low-pitch `WOLF_SHAKE` howl placeholder with `WOLF_WHINE` to sound more natural and less like a monster.

## [3.1.37+build.4] - 2026-03-04

### Added
- **Debug Commands**: Added `/betterdogs debug` commands to force personality changes and trigger social actions.
  - *Example (Personality)*: `/betterdogs debug personality @e[type=wolf,distance=..5] aggressive`
  - *Example (Action)*: `/betterdogs debug action @e[type=wolf,distance=..5] howl`
  - *Actions available*: `howl`, `zoomies`, `mischief`, `disciplined`.
- **Snapshot Support**: Fully migrated API calls to match Minecraft 26.x Snapshot 11 (e.g. `getWorldClockTime()`, `LivingEntity` leadership).
- **Workspace Consolidation**: Refactored the environment to maintain a single source of truth for mod development in a cleaner folder structure.

### Changed
- **Modularity**: Split command execution logic into a dedicated standard-compliant `WolfCommandHelper.java`.

## [3.1.37+build.3] - 2026-03-04

### Changed
- **Documentation**: Updated platform documentation (`Description Page.md`) to clearly detail the new **Unique Personality System** (Aggressive, Pacifist, Normal) with corresponding particle effects and behaviors.

## [3.1.37+build.2] - 2026-03-03

### Fixed
- **Dasik Library Integration**: Resolved build failures in project configuration by aligning Gradle mappings to the official 26.1 snapshot standards.

## [3.1.37+build.1] - 2026-03-03

### Added
- **Social Bonding**: New system where wolves track affinity with each other. High affinity suppresses "Blood Feuds".
- **Personality Traits**:
    - **Territorial War**: If both leaders want war, they engage in a 1v1 duel for dominance.
    - **Negotiated Yield (Handshake)**: If only one leader wants war, there is a configurable chance (`bd_territorial_yield_on_one_sided_chance`) for the other to yield and merge immediately.
    - **Avoidance/Retreat**: If both leaders are peaceful or the handshake fails, they retreat in opposite directions.

### Fixed
- **Pattern Matching**: Refactored combat logic to focus on Java 25 performance standards.

## [3.1.36+build.5] - 2026-03-02

### Added
- **Java Upgrade**: Upgraded to Java 25 to support Minecraft 26.1 snapshots.

### Fixed
- **API Integration**: Resolved compilation errors in `EatGroundFoodGoal.java` caused by Minecraft API changes in data components and registry access.

## [3.1.36+build.4] - 2026-03-02

### Added
- **Feeding**: Tamed dogs can now eat dropped raw and cooked food from the ground to restore health.
- **Toggles**: Added `bd_dogs_eat_raw_food` and `bd_dogs_eat_cooked_food` gamerules for granular control.
- **Mod Compatibility**: Features full support for modded foods via tags and name-based heuristics.

## [3.1.36+build.3] - 2026-02-21

### Fixed

- **Compatibility**: Reverted Mixin compatibility level from `JAVA_25` to `JAVA_22` to resolve warning.

## [v3.1.36+build.2] - 2026-02-20

### [v3.1.36+build.2] Features & Fixes

- **Feature**: Integrated the `DasikLibrary` Leader-Follower API for Wild Wolves. Packs of up to 8 wild wolves will now naturally group together and follow a leader using terrestrial pathfinding.
- **Fix**: Restored the Cooldown Logic for ambient/social dog events (like begging) to prevent performance drops or log spam.
- **Dependency**: Bumped `DasikLibrary` dependency to `1.6.9+build.4`.

## [v3.1.36] - 2026-02-16

- **Dependency Conflict**: Relaxed `DasikLibrary` dependency constraint. Now accepts versions `>=1.0.1` (removed `<2.0.0` cap), resolving launch crashes when `DasikLibrary` 2.0.0 is installed.

## [v3.1.35] - 2026-02-03

### [v3.1.35] Added

- **Localization**: Added descriptions for all Game Rules (including `bd_howl_chance`) to `en_us.json`.

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
