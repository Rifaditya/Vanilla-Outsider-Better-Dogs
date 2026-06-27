# Changelog

## [Unreleased]

## [3.11.0-26.1.2] - 2026-06-26
### Summary
- **Parity Update to v4.10.0-26.2**: This massive update brings Minecraft 26.1.2 into full feature parity with the latest 26.2 branch! Includes the new Flanking mechanics, Gifting enhancements, and various AI bugfixes.

### Added
- **True Pack Hunting Tactics (Flanking)**: When a pack of wolves engages a target, only the leader will attack directly. The follower wolves will now perform flanking maneuvers, dynamically pathfinding around the target to strike from the sides or behind. This makes packs significantly deadlier and more visually coordinated without causing server strain.
  - Can be toggled off via the `bd_pack_flanking_tactics` GameRule (defaults to `true`).

### Changed
- **Merit-Scaled Gifting Chances**: Rebalanced how gift chances are evaluated to create a true daily chance. The dog's chance to give a gift now scales linearly based on its interaction merits, from the base percentage (default 1%) at the minimum threshold, up to a 100% chance when reaching maximum interaction merits (10,000).
- **Gifting Threshold Balancing**: Increased the default required interactions for a gift (`bd_gift_feed_threshold`) from `3` to `10`. Increased the maximum hard-limit of accumulated interaction merits from `10` to `10,000`.

## [3.10.15-26.1.2] - 2026-06-26
### Fixed
- Fixed an issue where adopted wolves would retain their old pack leader UUID, guard mode status, and old grudges after being claimed by a new owner.

## [3.10.14-26.1.2] - 2026-06-21
### Added
- **Wolf Spawn Multiplier**: Introduced a configuration setting and game rule `bd_wolf_spawn_multiplier_percent` (default: 1.5x) to dynamically adjust wolf spawning weights in biome settings, making them spawn more commonly.

## [3.10.13-26.1.2] - 2026-06-21
### Changed
- **Tamed Wolf Wander Restriction**: Replaced the owner-unaware vanilla wandering behavior (`WaterAvoidingRandomStrollGoal`) with a personality-based `TamedWanderNearOwnerGoal`. Wolves now stay closer to their owner: Aggressive (max 14 blocks), Normal (max 8 blocks), and Pacifist (max 4 blocks), preventing them from drifting too far away.
- **Dynamic Wander Scaling**: Integrated the wander radius with the follower spacing offset. In larger packs, the wander boundaries expand dynamically to prevent clumping and overcrowding.

## [3.10.12-26.1.2] - 2026-06-12
### Changed
- **Guard Alarm Point & Freeze**: Pacifist dogs will now stand up, freeze in place, and look directly at the closest detected hostile mob during sentinel alarms.

## [3.10.11+A-26.1.2] - 2026-06-12
### Changed
- **Directional Guard Alarm Particles**: Modified Pacifist watchdog alarm particles to shoot forward in a 60-degree cone aligning with the wolf's looking direction, spawned at head/mouth level.

## [3.10.10+A-26.1.2] - 2026-06-12
### Added
- **Configurable Guard Particle Density**: Introduced a client/server configuration setting `guardParticleDensity` inside a new "Visual & Performance Options" Cloth Config screen tab to scale or toggle guard alert particles (`high` = 12, `medium` = 6 [default], `low` = 3, `off` = 0).

## [3.10.9+A-26.1.2] - 2026-06-12
### Changed
- **Guarding Pacifist Particles**: Redesigned the alert particles for guarding Pacifist dogs to spawn a highly noticeable, expanding horizontal circular ring of 12 pure red dust particles (0xFF0000) using trigonometric direction vectors and client-side outward velocity.

## [3.10.8+A-26.1.2] - 2026-06-12
### Changed
- **Rare Pack-Wide Howling**: Gated howling chance checks to run only once every 100 ticks (5 seconds) and implemented shared pack-wide cooldowns (10 minutes) when one wolf initiates a pack howl.

## [3.10.7+A-26.1.2] - 2026-06-12
### Changed
- **Red Alert Dust Particles**: Replaced the default music note particles with an optimized horizontal burst of 6 red dust particles (`DustParticleOptions`), sent in a single network packet to avoid performance overhead while providing a clear threat/warning color.

## [3.10.6+A-26.1.2] - 2026-06-12
### Changed
- **Sound Variant Weighted Lottery**: Transitioned the sound variant selection from a strict deterministic max-score model to a UUID-seeded weighted lottery. This ensures every individual dog gets a highly unique, stable, and randomish sound variant that aligns with its personality and genetics.

## [3.10.5+A-26.1.2] - 2026-06-12
### Added
- **Dynamic Sound Variant Mapping**: Implemented a mathematical scoring system that deterministically assigns the wolf's sound variant (Classic, Big, Cute, Puglin, Angry, Grumpy, Sad) based on its rolled genetics, scale, and personality type rather than relying on vanilla's random selection.

## [3.10.4+A-26.1.2] - 2026-06-11
### Changed
- **Pacifist Guard warning sound**: Changed the sentinel alarm sound for Pacifist dogs in Guard Mode from a whine to the specific growl sound corresponding to the wolf's sound variant itself (pitch adjusted to 1.0f).

## [3.10.3+A-26.1.2] - 2026-06-11
### Fixed
- **Low-Health Whimpering Scaling**: Fixed low-health whining behavior by replacing the vanilla absolute health `< 20.0f` threshold check with a dynamic check scaled to the dog's maximum health (`< 50% max health`). This prevents dogs with low max health (such as Runts and puppies) from whimpering constantly when fully healthy.

## [3.10.2+A-26.1.2] - 2026-06-11
### Added
- **Low Health Fleeing Goal**: Backported `WolfFleeLowHealthGoal` from 26.2 to 26.1.2 to align concept parity. Wolves will now retreat from combat at low health (<30% health) depending on their personality probability.
### Fixed
- **Storm Anxiety AI Registration**: Refactored `WolfAIMixin` to register `WolfStormAnxietyGoal` unconditionally to enable dynamic runtime GameRule toggling.
- **Storm Anxiety Personality Modifiers**: Updated `WolfStormAnxietyGoal` to query the GameRule state inside the goal methods, and added personality-based trigger modifiers (Pacifist 3x risk, Normal 1x, Aggressive immune).

## [3.10.1+A-26.1.2] - 2026-06-11
### Fixed
- **Classloader Mixin Shadow Crash**: Fixed a startup and runtime crash when trying to locate `@Shadow Mob mob` field inside `WalkNodeEvaluatorMixin` by making the mixin class inherit from `NodeEvaluator` directly.

## [3.10.0+A-26.1.2] - 2026-06-11 [DEPRECATED - CRITICAL BUG]
> [!WARNING]
> This version contains a classloader startup crash (InvalidMixinException) in `WalkNodeEvaluatorMixin` and is deprecated. Use `3.10.1+A-26.1.2` instead.

### Added
- **Restored Howling Sounds**: Bundled original vanilla wolf howling sound effects (`Wolf_howl1.ogg` and `Wolf_howl2.ogg`) within the mod and registered the `betterdogs:entity.wolf.howl` sound event.
- **AI Integration**: Updated `GroupHowlGoal` to play the authentic wolf howl sound instead of the baby whine fallback.

## [3.9.5+A-26.1.2] - 2026-06-11 [DEPRECATED - CRITICAL BUG]
> [!WARNING]
> This version contains a classloader startup crash (InvalidMixinException) in `WalkNodeEvaluatorMixin` and is deprecated. Use `3.10.1+A-26.1.2` instead.

### Added
- **Alternative Pathfinding on Push**: Added checks to see if another path exists to get to the target area (even if longer) before giving up and halting navigation.

## [3.9.4+A-26.1.2] - 2026-06-11
### Added
- **Ground Food Refusal Trait**: Added a feature where some tamed-from-birth dogs (bred from parents, not wild-tamed) persistently refuse to eat food from the ground.
  - Adds `bd_enable_refuse_ground_food` GameRule and toggle setting to enable/disable the feature globally.
  - Adds `bd_refuse_ground_food_chance` GameRule and setting to configure the percentage chance (default: 30%) that a puppy receives the refusal trait at birth.

## [3.9.3+A-26.1.2] - 2026-06-11
### Added
- **Wolf-on-Wolf Collision Push Safety**: Added cooperative collision handling between tamed wolves.
  - Tamed wolves colliding with a sitting, guarding, or endangered dog (facing a cliff/lava/magma/fire hazard) will immediately halt their navigation path.
  - A patience timer (`pushWaitTimer` of 60 ticks / 3 seconds) is applied to the pushing dog to prevent continuous pathfinding attempts and jittering.
  - Player-to-dog pushes are exempted to prevent player blocking.

## [3.9.2+R-26.1.2] - 2026-06-06
### Summary
- **Release Promotion**: Promoted the Select-and-Ride Dog Command System and Cloth Config GUI warning layout fixes to a stable production Release.

## [3.9.1+A-26.1.2] - 2026-06-06
### Added
- **Optional GUI Integration**: Upgraded config screen classloading to resolve via `GuiHelper` in `DasikLibrary` 1.8.2.
- **UI Warning Polish**: Removed repetitive hover tooltips from config options and moved them to category headers.

## [3.9.0+A-26.1.2] - 2026-06-06
### Added
- **Select-and-Ride Dog Command System**: Command dogs to sit/ride in vehicles, mounts, and modded chairs using a Stick tool.
- **Visual Sitting Pose**: Force visual sitting pose on wolves while riding any vehicle.
- **Dismount Controls**: Dismount using Stick (dismount & stand) or Empty-hand Shift+Right-click (dismount & sit).
- **Optional Unrestricted Riding GameRule**: Added `betterdogs:allow_unrestricted_dog_riding` to allow dogs to ride any entity (e.g. Ghasts).

## [3.8.4+A-26.1.2] - 2026-06-06
### Added
- **Unrelated Mate Prioritization**: Added `BreedGoalMixin` to prioritize breeding with unrelated wolves in range. If no unrelated wolves are available, they will fallback to related ones.

## [3.8.3+R-26.1.2] - 2026-06-06
### Summary
The **"Stability & Loot API Alignment Release"** promotion.
- **Release Promotion**: Promoted the genetics calculation engine integration and dependency constraints updates to a stable production Release.
- **Dependency Alignment**: Compiled against `DasikLibrary` `v1.8.1`.

## [3.8.2+A-26.1.2] - 2026-06-05
### Summary
The **"Library Dependency Realignment"** update.
- **Dependency Constraint Update**: Updated `fabric.mod.json` depends block to require `"dasik-library": ">=1.8.0"` (preventing startup crashes with older library versions due to missing genetics API classes).
- **Library Realignment**: Re-aligned and compiled against `DasikLibrary` `v1.8.1`.

## [3.8.1+A-26.1.2] - 2026-06-04
### Summary
The **"Genetics Library Migration"** update. Abstracts and migrates all selective breeding, inbreeding, and outcross recovery calculations to `DasikLibrary` `v1.8.0`.
- **Genetics Centralization**: Migrated `WolfPersistentData` genetics fields to the new library-provided `dasik-library:genetics` attachment type.
- **Thin Mod Architecture**: Delegated breeding calculations, inbreeding checks, and outcross recovery rules to `GeneticsEngine.inheritGenetics`, keeping the mod lightweight and clean.

## [3.8.0+A-26.1.2] - 2026-06-04
### Summary
The **"Feature Parity Backport"** update. Backports the advanced genetics, dynamic sizing, custom advancements, and performance optimizations from the Minecraft 26.2 (`v4.6.22`) codebase.
- **Custom Advancements & Triggers**: Registered and configured 9 custom criteria triggers (`TameWolfPersonalityTrigger`, `GuardWolfPersonalityTrigger`, `InbredWolfTrigger`, `OutcrossRuntTrigger`, `CureInbredTrigger`, `WolfLitterTrigger`, `PutUpForAdoptionTrigger`, `OnPatrolTrigger`, `SelfServiceTrigger`) and implemented 13 custom advancements JSON files under the 26.1.2 plural resource path (`data/minecraft/advancements/husbandry/`).
- **Genetic Breeding & Sizing**: Ported inheritance, mutations, runt penalties, and sibling breeding checks. Players can selectively breed dogs over generations to optimize attribute combinations (such as breeding massive high-health Aggressive watchdogs or fast Pacifists). Implemented UUID-based size scaling mapped directly to `Attributes.SCALE`, automatically syncing physical sizes and hitboxes to match genetic health stats.
- **AI Performance Optimizations**:
  - Throttled search scans in `EatGroundFoodGoal` to a random 10-20 tick cooldown and replaced allocations/streams with enhanced `for` loops.
  - Purged closures and lambda allocations in `PersonalityFollowOwnerGoal` using static final predicates and cached cooperative spacing counts.
- **Code Splitting**: Extracted taming, paper adoption, debug stick and bone guard mode handlers to `WolfInteractionHelper` to keep Mixin sizes modular and well below the 300 LOC limit.

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
- **Rule Reordering**: Reorganized registration order so that all Health rules, all Social rules, etc. are grouped together contiguously under their respective dividers.
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
The **"Alignment"** update. Finalized the architectural transition to strict namespacing guidelines, resolving configuration data loss and logic hoarding.
- **Registry Namespacing**: Refactored all custom GameRules to use the `vanilla-outsider-better-dogs` namespace (e.g., `vanilla-outsider-better-dogs:bd_storm_anxiety`). This prevents silent data loss caused by vanilla registry shadowing.
- **Localization Sync**: Synchronized `en_us.json` and `id_id.json` with the new namespaced GameRule keys for perfect UI parity.
- **Territorial Probability Matrix**: Fully integrated the dynamic outcome matrix into the `WildWolfTerritorialGoal` AI. Pack disputes now follow synchronized, personality-driven probabilities for War, Merge, or Retreat.
- **Config versioning**: Incremented internal config version to `3470` to trigger necessary data migration and backup procedures.
- **Cleanup**: Purged legacy "Snapshot 11" comments and documentation debt from the entrypoint classes.


## [3.4.6] - 2026-05-16
### Summary
The **"Probability Matrix"** update. Overhauled territorial outcomes with a dynamic, personality-driven chance system.
- **Dynamic Outcomes**: Pack disputes now use a weighted probability matrix (War/Merge/Retreat):
    - **Aggro vs Aggro**: 80% War, 10% Merge, 10% Run
    - **Aggro vs Normal**: 50% War, 40% Merge, 10% Run
    - **Aggro vs Pacifist**: 10% War, 50% Merge, 40% Run
    - **Normal vs Normal**: 20% War, 50% Merge, 30% Run
    - **Normal vs Pacifist**: 5% War, 45% Merge, 50% Run
    - **Pacifist vs Pacifist**: 0% War, 50% Merge, 50% Run
- **Hierarchy Polish**: During a merge, leadership is granted based on personality rank (Aggressive > Normal > Pacifist).


## [3.4.5] - 2026-05-16
### Summary
The **"Nuanced Leadership"** update. Refined pack interactions to respect personality instincts and autonomy.
- **Pacifist Autonomy**: Pacifist leaders now prioritize their pack's independence, choosing to **Retreat** rather than merge into rival packs.
- **Hierarchy Polish**: Aggressive leaders only force automatic merges against Normal leaders who don't want to fight.
- **Combat Logic**: If a leader (regardless of rank) chooses to fight, they will engage in a standard duel.

## [3.4.4] - 2026-05-16
### Summary
The **"Dominance Hierarchy"** update. Implemented a strict personality-based ranking for pack disputes.
- **Leadership Hierarchy**: Aggressive > Normal > Pacifist. Higher-ranked leaders now automatically win disputes against lower-ranked rivals unless a duel is triggered.

## [3.4.3] - 2026-05-16
### Summary
The **"Command UX"** update. Fixed visibility issues with the debug command suite.
- **UX Fix**: Resolved "Red Text" (Unknown command) issues for OPs. Commands are now always visible and logically gated behind the debugging GameRule.

## [3.4.2] - 2026-05-16
### Summary
The **"C2ME Compatibility"** update. Resolved critical multi-threading crashes and production stability issues.
- **Performance**: Fully compatible with multi-threaded chunk generation (C2ME).
- **Dependency Hardening**: Enforced `dasik-library >= 1.7.0` to prevent binary mismatch crashes.
- **Refmap Fix**: Resolved production Mixin errors by standardizing internal refmap naming.


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
### Added
- **🤝 Social Politeness**: New GameRule `bd_territorial_exclusive_disputes` (Default: true) ensures territorial disputes are strictly 1v1.
- **Queuing Logic**: Leaders will now wait for a rival to be "free" before initiating a challenge, preventing chaotic crowd disputes in dense areas.

## [3.3.0] - 2026-05-11
### Added
- **🌍 Territorial Scaling**: New GameRule `bd_territorial_search_radius` to control the distance at which pack leaders engage in disputes.
- **🐺 Pack Dynamics**:
    - **Configurable Cluster Size**: New GameRule `bd_wolf_pack_cluster_size` to control the maximum size of naturally spawning packs.
    - **Density Boosting**: New GameRule `bd_wolf_spawn_density_boost` (% chance) to trigger a "Reinforcement Spawn" nearby when a pack spawns, increasing regional territorial friction.
- **Localization**: Full descriptive strings for all new balancing parameters in `en_us.json`.

### Refactored
- **Versioning**: Incremented to `3.3.0` to reflect significant configuration and world-gen logic shifts.
- **AI Hardening**: `WildWolfTerritorialGoal` now fully respects dynamic search radius updates without requiring a world reload.


## [3.2.0] - 2026-05-11
### Added
- **🏰 Territorial Handshake**: New negotiation logic for wild pack leaders.
    - If both leaders want war -> Cinematic 1v1 Duel.
    - If only one wants war -> Negotiated Yield (B yields/merges) or Retreat based on `bd_territorial_yield_on_one_sided_chance`.
    - If neither wants war -> Peaceful Retreat (96 blocks).
- **Wild Personality AI**: Wild members (not leaders) exhibit unique behaviors (e.g., Aggressive wolves hunting monsters) while anchored to their pack leader.
- **Debug Tooling**:
    - Added `/betterdogs debug territory` for immediate pack interaction testing.
    - **Territorial Debug Logging**: New detailed console logs for pack interactions (Wars, Merges, Retreats) enabled via `betterdogdebugging` GameRule.

### Hardening
- **Logic Hardening**: Moved all custom entity logging (Spawn, Tame, Social, Ambient) to the `betterdogdebugging` GameRule gate.
- **Default Alignment**: Wild Personality Behavior is now enabled by default for new worlds.
- **Architectural Cleanup**: Removed duplicate debug log entries in Mixin layers.

### Refactored
- **Architectural Alignment**: Refactored `WildWolfFollowLeaderGoal` to use the modernized **DasikLibrary 1.7.0** base goal.
    - Optimized pack following with distance-based "Movement Triggers" (start/stop thresholds).
    - Reduced mod code footprint by leveraging library-side stable physics.
- **Mapping Compliance**: Fully refactored for **Minecraft 26.1.2** ("Tiny Takeover") mapping signatures.
- **Deterministic AI**: Territorial decisions are now synchronized between entities using a shared seeded random.

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

## [3.1.37+build.8] - 2026-04-16

### Added
- **Pack Spread Control**: New GameRule `bd_pack_spread` (default: 20 = 2.0 blocks) sets the minimum separation distance between wild wolves in a pack.
  - Each integer unit = 0.1 blocks. Example: `/gamerule bd_pack_spread 50` = 5.0 block spacing.
  - Updates are dynamic — wolves respond to GameRule changes within 40 ticks, staggered per entity to prevent TPS spikes.
  - Default separation raised from 1.5 to 2.0 to reduce visual overcrowding.

### Dependency: DasikLibrary Build 22
- Requires `FollowLeaderGoal.setParameters()` to apply runtime AI parameter changes.

## [3.1.37+build.7] - 2026-04-16

### Refactored
- **Pack AI: Sovereign Migration** — Migrated group size tracking from an O(N) hand-rolled bounding-box scan (`WolfMixin.getGroupSize()`) to the DasikLibrary `FlockState` cache. Pack size is now computed once by the leader and shared across all followers.
- **Removed** dead fields `betterdogs$groupSize` and `betterdogs$groupSizeCheckTicks`.

### Dependency: DasikLibrary Build 21
- Added `FlockState.getMemberCount()` API to DasikLibrary, enabling mods to query cached pack size without iterating entities.
- Added `GroupManager.computeFlockState()` now sets `memberCount` alongside existing center-of-mass and velocity aggregates.

### Infrastructure
- **Upgraded**: Fabric Loader to `0.19.1` — native Java 25 Mixin subsystem support (no Knot warning).
- **Minecraft Support**: Shifted to `~26.x` compatible range (`>=26.1`) for **Minecraft 26.2** readiness.
- **Upgraded**: Fabric API to `0.145.4+26.1.2`.
- **Dependency**: Synchronized with `DasikLibrary` Build 21.

## [3.1.37+build.6] - 2026-04-15

### Added
- **DasikLibrary Build 16 Sync**: Integrated the **Cached Boids Pattern**. Wolf packs now utilize $O(N)$ aggregated state computation, resolving performance leaks during large pack gatherings.
- **Biomechanical Smoothing**: Leverages the new library-side Lerp interpolation for smoother pack following without visual jitter.

### Changed
- **Release Alignment**: Synchronized workspace with Minecraft **26.1.2 ("Tiny Takeover") Release** and Fabric API `0.145.4`.

## [3.1.37+build.5] - 2026-03-04

### Changed
- **Sound Polish**: Replaced the low-pitch `WOLF_SHAKE` howl placeholder with `WOLF_WHINE` to sound more natural and less like a monster.

## [3.1.37+build.4] - 2026-03-04

### Added
- **Debug Commands**: Added `/betterdogs debug` commands to force personality changes and trigger social actions.
  - *Example (Personality)*: `/betterdogs debug personality @e[type=wolf,distance=..5] aggressive`
  - *Example (Action)*: `/betterdogs debug action @e[type=wolf,distance=..5] howl`
  - *Actions available*: `howl`, `zoomies`, `mischief`, `disciplined`.
- **Version Support**: Fully migrated API calls to match Minecraft 26.x development builds (e.g. `getWorldClockTime()`, `LivingEntity` leadership).
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
- **Social Bonding**: New system where wolves track affinity with each other. High affinity suppresses "Blood Feuds".

- **Personality Traits**:
  - **Aggressive**: Wolves now scouting ahead of their owner.
  - **Pacifist**: Wolves now whine to alert hostiles within range.

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

## [3.1.36+build.2] - 2026-02-19

### Added

- **Leader-Follower Integration**: Wild wolves now naturally form packs using DasikLibrary's Leader-Follower API, with a deterministic max pack size of 8.

### Fixed

- **Ambient Event Spam**: Restored proper cooldown logic for ambient behaviors (like begging), preventing them from executing every tick.

## [3.1.36+build.1] - 2026-02-19

### Changed

- **DasikLibrary Integration**: Switched to standalone dependency (JiJ removed).
- **Versioning**: Adopted strict Build Number policy.

## [3.1.36] - 2026-02-16

### Fixed

- Dependency conflict: Allow `DasikLibrary` >= 1.0.1 (removed < 2.0.0 cap) to support version 2.0.0.

## [3.1.35] - 2026-02-03

### Added

- Detailed descriptions for all gamerules, including the previously missing `bd_howl_chance`.

---
