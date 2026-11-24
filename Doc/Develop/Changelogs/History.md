# Better Dogs - Historical Changelog

## [5.7.24+26.1.2] - 2026-11-23

### Fixed
- **Sitting Pose State Desync During Movement & AI Transitions ([BL-BD-069])**:
  - **Reported By**: `Apeirohex` via Discord community support (credited in `CHANGELOG.md`).
  - Added single-purpose `WolfSittingPoseHelper` and `WolfExtensions.betterdogs$isPerformingScoot()` seam for future `BL-BD-070` scoot behavior across Fabric and NeoForge, wired into `WolfMixin` via `syncSittingPoseOnTick` to clear non-ordered sitting pose (`setInSittingPose(false)`) only on real server-side transitions when navigating, targeting, or moving beyond `1.0e-4` horizontal sqr / `0.15` vertical delta (preserving resting `-0.0784` gravity, `isOrderedToSit()`, and `isPassenger()`).
  - Updated `PersonalityFollowOwnerGoal` (`start()` unordered pose clear; `stop()` gated by `canEnterLazyRestingPose(...)` and `(0.0, dm.y, 0.0)` horizontal inertia zeroing so tick-0 follow deceleration does not immediately cancel lazy resting).
  - Wired `WolfMobMixin` (`setTarget` `@At("TAIL")`) and `WolfCombatHooks` (`clearSittingPoseOnDamage` post interception and friendly-fire cancellation checks) to stand lazy-resting dogs up immediately upon combat targeting or non-cancelled damage.
  - Updated companion AI goals (`TamedWanderNearOwnerGoal`, `WanderlustGoal`, `WolfSeekWaterOnFireGoal`, and `WolfGuardGoal`) to clear stale unordered sitting poses, and bound `wasSitting` in `AvoidGhastFireballGoal`, `FleeExplodingHazardGoal`, and `WolfProjectileInterceptGoal` strictly to `isOrderedToSit()` so evasion never promotes lazy rests into permanent ordered sits.
  - Verified across Fabric and NeoForge with `SittingPoseStateTransitionTest` (`510/510`).

## [5.7.23+26.1.2] - 2026-11-23

### Fixed
- **Genetic Scale & Voice Pitch Persistence Across Spawn, Breeding, Runt & Cure ([BL-BD-071])**:
  - **Reported By**: `@UnderscoreNash` via GitHub Issue #14 (credited in `CHANGELOG.md`).
  - Routed wild/summoned scale writes (`WolfSpawnMixin`), offspring scale writes (`WolfBreedingMixin`), inbred runt penalties (`WolfInbreedingHelper`), and Golden Apple cure restoration (`WolfCureHelper`) through `WolfExtensions.betterdogs$setSocialScale(float)` across Fabric and NeoForge to persist `WolfPersistentData.scale` atomically with `Attributes.SCALE`.
  - Updated `WolfBreedingMixin`, `WolfInbreedingHelper`, and `WolfCureHelper` scale reads to prefer persisted `betterdogs$getSocialScale()` (`> 0.0f` fallback `Attributes.SCALE`), eliminating the `WolfSocialMixin.onTickTail` tick-1 snap back to `1.00` Scale / `1.000` Pitch Timbre while leaving existing `1.0f` wolves untouched.
  - Sanitized `NaN`, non-positive (`<= 0`), and infinite float inputs to `1.0f` in `WolfPersistentData`'s compact canonical constructor, `WolfPersistentData.setScale`, and `WolfSocialMixin.betterdogs$setSocialScale`.
  - Added headless verification (`ScalePersistenceTest`, `AdoptionSystemTest.AdoptionStubWolf`) and synchronized `TestWolfDataHelper` fallback storage.

## [5.7.17+26.1.2] - 2026-11-16

### Fixed
- **Restored Full Vanilla-Client Dedicated Server Compatibility ([BL-BD-064])**:
  - Special thanks to `QienSnowpoff` for identifying and reporting dedicated server desyncs when unmodded vanilla clients join modded servers!
  - **Dynamic Sound Event Decoupling**: Removed static `BuiltInRegistries.SOUND_EVENT` registration to prevent registry synchronization freezes on unmodded clients. Implemented dual sound routing in `WolfHowlAudioHelper`: sends direct variable-range sound packets to Better Dogs clients, and gracefully falls back to pitched-down vanilla baby wolf whines (`[0.50f, 0.80f]`) for vanilla clients.
  - **Capability Handshake**: Added `BetterDogsClientCapPayload` (`betterdogs:client_audio_cap`) clientbound play payload and receiver across Fabric and NeoForge for zero-overhead client mod detection via standard channels and local singleplayer addresses.
  - **Synced Entity Data & Mixin Hygiene**: Removed `BETTERDOGS$DATA_SUBDUED` and `BETTERDOGS$DATA_SPECTATOR_WAGGING` `EntityDataAccessor` slots and `WolfRendererMixin`, preserving internal tick logic, personality state, and NBT saves while eliminating entity tracker crashes on vanilla clients.
  - **Translatable Text & Advancement Resilience**: Converted all 20 server-sent player notification components across listeners and helpers to `Component.translatableWithFallback` matching `en_us.json`, preventing untranslated translation key popups on unmodded clients. Added resilient command support footer in `BetterDogsSupportFooter`.
  - **Advancement Fallbacks & Subtitles**: Added `"fallback"` text across all 28 husbandry advancement JSON definitions and added `"subtitles.betterdogs.entity.wolf.howl"` subtitle localization.

## [5.7.14+26.1.2] - 2026-11-14

### Fixed
- **Null-Safe Blood Feud Persistence & Adoption Codec Safety ([BL-BD-059])**:
  - Updated `WolfInteractionHelper` non-sneak adoption to invoke `BloodFeudHelper.clearBloodFeud(wolf)` instead of passing `null` into `betterdogs$setBloodFeudTarget(null)`.
  - Aligned `WolfAdoptionHelper.tryHandleAdoption` sneak empty-hand adoption to reset `leaderUuid`, clear blood feuds (`BloodFeudHelper.clearBloodFeud(wolf)`), clear `guardMode`/`guardPos`, and call `wolf.setOrderedToSit(false)`.
  - Added compact canonical constructors on `WolfPersistentData`, `BaseData`, and `ExtraData`, null-safe normalization in `WolfMixin` and `WolfPersistentData` blood feud accessors, and null-safe `Services.WOLF_DATA` SPI persistence handling (`TestWolfDataHelper`, `FabricWolfDataHelper`, `NeoForgeWolfDataHelper`).

## [4.24.68+26.1.2]
### Added
- 🌐 **Italian (`it_it`) Localization & Player Guide (11-Language Suite Complete)**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/it_it.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_it_it.md`.

## [4.24.67+26.1.2]
### Added
- 🌐 **Korean (`ko_kr`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/ko_kr.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_ko_kr.md`.

## [4.24.66+26.1.2]
### Added
- 🌐 **French (`fr_fr`, `fr_ca`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/fr_fr.json` and `fr_ca.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_fr_fr.md`.

## [4.24.65+26.1.2]
### Added
- 🌐 **Japanese (`ja_jp`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/ja_jp.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_ja_jp.md`.

## [4.24.64+26.1.2]
### Added
- 🌐 **Portuguese (`pt_br`, `pt_pt`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/pt_br.json` and `pt_pt.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_pt_br.md`.

## [4.24.63+26.1.2]
### Added
- 🌐 **German (`de_de`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/de_de.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_de_de.md`.

## [4.24.62+26.1.2]
### Added
- 🌐 **Spanish (`es_es`, `es_mx`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/es_es.json` and `es_mx.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_es_es.md`.

## [4.24.61+26.1.2]
### Added
- 🌐 **Russian (`ru_ru`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/ru_ru.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_ru_ru.md`.

## [4.24.60+26.1.2]
### Added
- 🌐 **Traditional Chinese (zh_tw, zh_hk) Localization & Player Guide**:
  - Added complete ssets/vanilla-outsider-better-dogs/lang/zh_tw.json and zh_hk.json matching all 428 translation keys.
  - Added localized player guide in Doc/Players/index_zh_tw.md.

## [4.24.59+26.1.2]
### Added
- 🌐 **Simplified Chinese (zh_cn) Localization & Player Guide**:
  - Added complete ssets/vanilla-outsider-better-dogs/lang/zh_cn.json matching all 428 translation keys.
  - Added localized player guide in Doc/Players/index_zh_cn.md.

## [4.24.58+26.1.2]
### Added
- 🌐 **Indonesian (id_id) Full Parity Localization & Player Guide**:
  - Audited and synchronized ssets/vanilla-outsider-better-dogs/lang/id_id.json for 100% key parity with en_us.json.
  - Added localized Indonesian player guide in Doc/Players/index_id_id.md.

## [4.24.57+26.1.2] - 2026-08-22
### Added
- **Hoover / Ground Food Scavenger Quirk (`WolfDispositionHelper.java`, `WolfScavengeHelper.java`, `EatGroundFoodGoal.java`)**:
  - Implemented the Hoover behavioral quirk where dogs eagerly scavenge dropped food items from the ground even when at $100\%$ full health.
  - **Base Personality Rates**: Aggressive ($70\%$), Normal ($35\%$), Pacifist ($10\%$) with $[-100\%, +100\%]$ UUID variance offset.
  - **Digestion Cooldown**: Enforced a $160\text{ ticks}$ ($8\text{ seconds}$) cooldown between ground food snacks at full health to prevent vacuuming item stacks.
  - **Posture Safety**: Fully respects sitting commands (`isOrderedToSit()`); seated dogs will never break posture.

## [4.24.56+26.1.2] - 2026-08-22
### Changed
- **Full-Spectrum [-100%, +100%] UUID Behavioral Variance (`WolfDispositionHelper.java`)**:
  - Expanded individual dog UUID offset range to $[-100\%, +100\%]$ (clamped to $[0\%, 100\%]$).
  - Applied base personality values:
    - **Fetch Reluctance**: Aggressive ($10\%$), Normal ($30\%$), Pacifist ($60\%$).
    - **Storm Fearlessness**: Aggressive ($80\%$), Normal ($40\%$), Pacifist ($10\%$).
    - **Quiet Howling**: Aggressive ($10\%$), Normal ($25\%$), Pacifist ($60\%$).

## [4.24.55+26.1.2] - 2026-08-22
### Changed
- **Additive Modifier Behavioral Quirk Model (`WolfDispositionHelper.java`)**:
  - Implemented additive modifier variance model with wide personality spread:
    - **Fetch Reluctance**: Aggressive ($0\text{--}5\%$), Normal ($3\text{--}18\%$), Pacifist ($15\text{--}45\%$).
    - **Storm Fearlessness**: Aggressive ($90\text{--}100\%$), Normal ($10\text{--}35\%$), Pacifist ($0\text{--}15\%$).
    - **Quiet Howling**: Aggressive ($0\text{--}10\%$), Normal ($5\text{--}30\%$), Pacifist ($25\text{--}65\%$).
  - Combined personality baseline percentages with deterministic UUID offsets ($\pm 10\%$ to $\pm 20\%$).

## [4.24.54+26.1.2] - 2026-08-22
### Added
- **Personality + UUID Seeded Behavioral Variance (`WolfDispositionHelper.java`)**:
  - Deterministically seeded individual behavioral nuances using 64-bit bit-mixing hashing over `UUID` + `WolfPersonality` + behavior salt (zero NBT storage).
  - ~5% of dogs are organically reluctant to fetch sticks; non-fetchers either curiously tilt their heads (`setIsInterested(true)`) or naturally ignore nearby thrown sticks.
  - ~10% of dogs are naturally fearless during thunderstorms, bypassing storm panic and whimpering.
  - ~15% of dogs are quiet observers who refrain from participating in nocturnal group howling choruses.

## [4.24.53+26.1.2] - 2026-08-22
### Changed
- **Subtle Particle Feedback for Fetch, Gifts & Treat Feeding (`WolfFetchHelper`, `WolfGiftHelper`, `DogTreatHelper`, `WolfParticleHelper`)**:
  - Integrated `WolfParticleHelper.spawnSubtleParticles` to scale subtle cues with `bd_particle_density` GameRule (`NONE`: 0, `LOW`: 1, `MEDIUM`/`HIGH`: 2).
  - Reduced stick fetch and morning gift particles to 2 subtle emerald glints (`0.15` spread, `0.02` speed).
  - Softened favorite treat feeding visual feedback to 2 happy villager + 1 heart particle.

## [4.24.37+26.1.2] - 2026-08-19
### Fixed
- **Jade Provider Separation ("1 File, 1 Purpose")**:
  - Separated client tooltip component providers (`InbredStatusProvider`, `WolfInfoProvider`) from server data sync providers (`InbredStatusDataProvider`, `WolfInfoDataProvider`).
  - Restored 100% full compatibility across newer Jade releases (`Jade >= 26.2.11+`), older Jade releases, and standalone Fabric.

## [4.24.36+26.1.2] - 2026-08-19 - ⚠️ Incompatible with Jade >= 26.2.11+ (Works on Jade < 26.2.10 or without Jade)

## [4.24.7+26.1.2] - 2026-08-11
### Refactored & Polished
- **Minimal Tame Particle Polish (`WolfParticleHandler`)**: Refactored first-time tame particles into a clean ~5-6 particle Minimal Signature per personality (Aggressive: crimson dust + angry villager; Normal: golden dust + happy villager; Pacifist: mint dust + heart).

## [4.24.6+26.1.2] - 2026-08-10
### Fixed
- **YACL Config Option Descriptions (`YaclScreenHelper` & Translation Sheets)**: Added missing `.description(...)` OptionDescription builders in `YaclScreenHelper` across all 6 config categories and synchronized complete `.description` keys in `en_us.json` and `id_id.json`.

## [4.24.4+26.1.2] - 2026-08-10
### Fixed
- **Dog Adoption Crash Fix (`WolfInteractionHelper` & `WolfStatManager`)**: Fixed `NullPointerException` crash during adoption by adding personality null guards in `WolfStatManager` and pre-assigning random personalities for uninitialized dogs.

## [4.24.3+26.1.2] - 2026-08-10
### Fixed & Added
- **Litematica Compatibility & Command Item Expansion (`DogCommandManager`)**:
  - **Litematica Tool Conflict Fix**: Resolved stick tool interaction collision with Litematica by introducing `#vanilla-outsider-better-dogs:command_items` item tag (Stick, Blaze Rod, Breeze Rod, `#c:tools/sticks`).
  - **Alternative Command Tools**: Enables Blaze Rods and Breeze Rods as alternative command tools alongside standard sticks for dismounting and seat commanding.
  - **Bone Reservation Safeguard**: Explicitly excluded `minecraft:bone` from command items so Bone remains 100% reserved for Guard Mode (`bd_guard_mode`).

## [4.24.1+26.1.2] - 2026-08-01
### 🔄 Backport Release
- **Full Parity Backport to Minecraft 26.1.2**: Backported all features, AI goals, Goat Horn Command systems, Creeper Evasion, Jade integration, and stability fixes from version `4.24.1+26.2`.
- **API Mapping Adjustments**: Adapted Mojang registry and advancement APIs for Minecraft 26.1.2 compatibility (`EntityType`, `advancements.criterion`, `Gui.HeartType`).

## [3.12.2-26.1.2] - 2026-06-27
### Changed
- **Strict Parity Sync:** Cross-pollinated minor polish and safety checks with 26.2.
  - Added logging on Grudge expiration.
  - Tweaked Nemesis angry particle spread.
  - Tamed wolves will no longer form grudges against Players or other Tamed Animals to prevent infinite PvP loops.

## [3.12.1-26.1.2] - 2026-06-27
### 🛠️ Fixed
- **Architectural Audit Fixes:** 
  - Extracted logic out of `WolfMixin` to reduce file size under 300 LOC limit (moved to `WolfCombatHooks`).
  - Added missing GPLv3 headers to Nemesis system files.
  - Added missing localization keys for the Nemesis System GameRules in `en_us.json`.
  - Synced Nemesis Grudge tick frequency to 20 ticks (1 second) for parity.

## [3.12.0-26.1.2] - 2026-06-26
### Added
- Backported the Nemesis Grudge System from 26.2 (v4.11.0).
- If a non-player entity kills a tamed wolf, all nearby tamed wolves of the same owner gain a permanent grudge against that entity type.
- When attacking their nemesis, wolves gain Strength and Speed buffs and emit angry particles.
- The grudge expires after 3 in-game days by default.
- New GameRules: `bd_nemesis_system` (default: true) and `bd_nemesis_duration_days` (default: 3).

### Summary
The **"True Pack Hunting Tactics (Flanking)"** patch. Revolutionizes wolf pack combat by implementing dynamic flanking maneuvers for followers.
- **Flanking Tactics**: When a pack engages a target, only the leader will attack directly. The follower wolves will perform flanking maneuvers, dynamically pathfinding around the target to strike from the sides or behind. This makes packs significantly deadlier and more visually coordinated without causing server strain.
- **Flanking GameRule**: Added `bd_pack_flanking_tactics` (default `true`) to toggle this behavior.

## [3.10.15-26.1.2] - 2026-06-26
### Fixed
- Fixed an issue where adopted wolves would retain their old pack leader UUID, guard mode status, and old grudges after being claimed by a new owner.

## [3.10.14-26.1.2] - 2026-06-21
### Summary
The **"Wolf Spawns Multiplier"** patch. Introduces a configuration setting and game rule `bd_wolf_spawn_multiplier_percent` (default: 1.5x) to scale the spawning weight of wolves in biome settings.
- **NaturalSpawner Mixin**: Injected `NaturalSpawnerMixin.java` targeting `NaturalSpawner.mobsAt()` at return. Intercepts biome spawn settings, checks if `net.minecraft.world.entity.EntityType.WOLF` is in the candidate list, and rebuilds the `WeightedList` with the multiplied weight if a non-1.0x setting is configured.
- **Config & Registry**: Added `wolfSpawnMultiplier` double to `BetterDogsConfig` and registered `BD_WOLF_SPAWN_MULTIPLIER_PCT` GameRule. Added screen entry in `ClothConfigScreenHelper`.
- **Translations**: Added localization keys for both ModMenu config screen and GameRule descriptions.

## [3.10.13-26.1.2] - 2026-06-21
### Summary
The **"Tamed Wolf Wander Restriction"** patch. Replaces the owner-unaware vanilla wandering behavior (`WaterAvoidingRandomStrollGoal`) with a personality-based `TamedWanderNearOwnerGoal` to restrict tamed wolf wandering radius around their owners.
- **Wander Radius Limits**: Restricts tamed wolf wandering radius to: 14.0 blocks for `AGGRESSIVE`, 8.0 blocks for `NORMAL`/Classic, and 4.0 blocks for `PACIFIST`.
- **Navigation Return Pathing**: If the wolf drifts outside its personality's max radius, it generates a pathing target directly towards the owner using `DefaultRandomPos.getPosTowards()`.
- **Target Search Loop**: Evaluates standard stroll positions up to 10 times; if all are outside the radius, it defaults to moving towards the owner.
- **Mixin Registration**: Refactored `WolfAIMixin.java` to scan, isolate, and remove the vanilla `WaterAvoidingRandomStrollGoal.class` (preserving subclasses like `WanderlustGoal`) and inject `TamedWanderNearOwnerGoal` at priority `8`.
- **Gated Checks**: Optimizes server performance by short-circuiting AI evaluations if the wolf is untamed, sitting, leashed, or in Guard Mode.
- **Dynamic Wander Scaling**: Links the wander `maxRadius` calculation in `TamedWanderNearOwnerGoal.java` to `PersonalityFollowOwnerGoal.FollowerSpacingCache` to retrieve the owner's active follower count. If $N > 1$, it adds the square-root-based follower spacing offset to `maxRadius`, naturally expanding the boundary to prevent clumping and overcrowding in larger packs.

## [3.10.12-26.1.2] - 2026-06-12
### Summary
The **"Guard Alarm Point & Freeze"** patch. Configures Pacifist watchdogs to perform a hunting-dog-style pointing freeze during active alarms, locking onto the closest hostile target.
- **Pointing Target Sweep**: Updated the alarm detection routine to track the closest hostile target.
- **Target Tracking & Posture**: Forced the dog to stand up (breaking sitting pose) and orientation-lock towards the target every tick using look control.

## [3.10.11+A-26.1.2] - 2026-06-12
### Summary
The **"Directional Guard Alarm Particles"** patch. Aligns the Pacifist watchdog alert particles with the wolf's looking direction, creating a 3D forward-pointing warning cone spray.
- **Directional Cone Spray**: Modified `WolfGuardGoal.java` to calculate a 3D cone spray spanning 60 degrees centered around the wolf's exact looking vector (pitch and yaw).
- **Mouth Emittance**: Set particle spawn position slightly in front of the wolf's face at mouth level (`y = eyeHeight - 0.1`) to represent a warning bark/breath.

## [3.10.10+A-26.1.2] - 2026-06-12
### Summary
The **"Configurable Guard Particle Density"** patch. Introduces a client/server configuration setting `guardParticleDensity` inside a new "Visual & Performance Options" Cloth Config screen tab to scale or toggle guard alert particles.
- **Configurable Particle Scaling**: Added `guardParticleDensity` setting to `BetterDogsConfig` supporting `high`, `medium` (default), `low`, and `off` options.
- **Visual & Performance Category**: Created a dedicated Cloth Config category for Visual & Performance options that is separated from baseline game rule default configuration warnings.
- **Conditional Alert Loop**: Updated `WolfGuardGoal.java` to scale trigonometry loop size and angle steps based on user preference to optimize client-side rendering.

## [3.10.9+A-26.1.2] - 2026-06-12
### Summary
The **"Trigonometric Pacifist Sentinel Warning Circle"** patch. Redesigns the alert particles for guarding Pacifist dogs to spawn a highly noticeable, expanding horizontal circular ring of 12 pure red dust particles.
- **Trigonometric Ring Spawning**: Modified `WolfGuardGoal.java` to calculate a 12-particle circle using `cos` and `sin` over $360^\circ$ at a 0.5 blocks radius offset.
- **Expanding Velocity**: Spawns particles with `count = 0` and outward directional velocity vectors scaled by speed `0.5` to make the circle physically expand.

## [3.10.8+A-26.1.2] - 2026-06-12
### Summary
The **"Rare Pack-Wide Howling"** patch. Reduces howling frequency and applies shared cooldowns to pack members to make howling a rare, ambient event.
- **Pack-Wide Shared Cooldowns**: Modified `GroupHowlGoal.java` so that when a wolf spreads its howl to nearby pack members, it sets their goal cooldowns to `HOWL_COOLDOWN` (10 minutes) as well.
- **Gated Howl Chance Checks**: Gated the howling chance check to run only once every 100 ticks (5 seconds) instead of every tick.

## [3.10.7+A-26.1.2] - 2026-06-12
### Summary
The **"Optimized Red Alert Dust Particles"** patch. Replaces the note particles during Pacifist sentinel guarding alarms with low-overhead, red dust alert particles.
- **Red Alert Dust**: Replaced the default music note particles with an optimized horizontal burst of 6 red dust particles (`DustParticleOptions`), sent in a single network packet to avoid performance overhead while providing a clear threat/warning color.

## [3.10.6+A-26.1.2] - 2026-06-12
### Summary
The **"Sound Variant Weighted Lottery"** patch. Transitions the sound variant selection from a strict deterministic max-score model to a UUID-seeded weighted lottery.
- **Weighted Lottery Math**: Replaced maximum score logic with a UUID-seeded weighted lottery using `Math.max(0.0f, ...)` trait weight modifiers. This ensures every individual dog gets a highly unique, stable, and randomish sound variant that aligns with its personality and genetics.

## [3.10.5+A-26.1.2] - 2026-06-12
### Summary
The **"Dynamic Sound Variant Mapping"** patch. Implements a mathematical decision-tree/scoring system that resolves the wolf's sound variant dynamically based on genetics, scale, and personality.
- **Dynamic Sound Variant Mapping**: Implemented a mathematical scoring system that deterministically assigns the wolf's sound variant (Classic, Big, Cute, Puglin, Angry, Grumpy, Sad) based on its rolled genetics, scale, and personality type rather than relying on vanilla's random selection.

## [3.10.4+A-26.1.2] - 2026-06-11
### Summary
The **"Pacifist Guard Sentinel Growl Alert"** patch. Changes the sentinel alarm sound for Pacifist dogs in Guard Mode from a whine to the specific growl sound corresponding to the wolf's sound variant itself.
- **Pacifist Guard warning sound**: Changed the sentinel alarm sound for Pacifist dogs in Guard Mode from a whine to the specific growl sound corresponding to the wolf's sound variant itself (pitch adjusted to 1.0f).

## [3.10.3+A-26.1.2] - 2026-06-11
### Summary
The **"Low-Health Whimpering Scaling Fix"** patch. Scales the low-health whine sound threshold dynamically based on the dog's maximum health percentage to prevent Runts/puppies from whimpering constantly when healthy.
- **Low-Health Whimpering Scaling**: Fixed low-health whining behavior by replacing the vanilla absolute health `< 20.0f` threshold check with a dynamic check scaled to the dog's maximum health (`< 50% max health`). This prevents dogs with low max health (such as Runts and puppies) from whimpering constantly when fully healthy.

## [3.10.2+A-26.1.2] - 2026-06-11
### Summary
The **"Concept Parity Alignment"** patch. Backports the low health fleeing goal and fixes static registration and missing personality multipliers for Storm Anxiety goal in 26.1.
- **Low Health Fleeing Goal**: Backported `WolfFleeLowHealthGoal` from 26.2 to 26.1.2 to align concept parity. Wolves will now retreat from combat at low health (<30% health) depending on their personality probability.
- **Storm Anxiety AI Registration**: Refactored `WolfAIMixin` to register `WolfStormAnxietyGoal` unconditionally to enable dynamic runtime GameRule toggling.
- **Storm Anxiety Personality Modifiers**: Updated `WolfStormAnxietyGoal` to query the GameRule state inside the goal methods, and added personality-based trigger modifiers (Pacifist 3x risk, Normal 1x, Aggressive immune).

## [3.10.1+A-26.1.2] - 2026-06-11
### Summary
The **"Classloader Mixin Shadow Crash Fix"** patch. Resolves a startup crash caused by an invalid `@Shadow` on the inherited `mob` field in `WalkNodeEvaluatorMixin`.
- **Classloader Mixin Shadow Crash**: Fixed a startup and runtime crash when trying to locate `@Shadow Mob mob` field inside `WalkNodeEvaluatorMixin` by making the mixin class inherit from `NodeEvaluator` directly.

## [3.10.0+A-26.1.2] - 2026-06-11 [DEPRECATED - CRITICAL BUG]
> [!WARNING]
> This version contains a classloader startup crash (InvalidMixinException) in `WalkNodeEvaluatorMixin` and is deprecated. Use `3.10.1+A-26.1.2` instead.

### Summary
The **"Restored Howling Sounds"** update. Restores original wolf howling sounds when wolves howl.
- **Restored Howling Sounds**: Bundled original vanilla wolf howling sound effects (`Wolf_howl1.ogg` and `Wolf_howl2.ogg`) within the mod and registered the `betterdogs:entity.wolf.howl` sound event.
- **AI Integration**: Updated `GroupHowlGoal` to play the authentic wolf howl sound instead of the baby whine fallback.

## [3.9.5+A-26.1.2] - 2026-06-11 [DEPRECATED - CRITICAL BUG]
> [!WARNING]
> This version contains a classloader startup crash (InvalidMixinException) in `WalkNodeEvaluatorMixin` and is deprecated. Use `3.10.1+A-26.1.2` instead.

### Summary
The **"Alternative Pathfinding on Push"** update. Adds checks to see if another path exists to get to the target area (even if longer) before giving up and halting navigation.
- **Alternative Pathfinding on Push**: Added checks to see if another path exists to get to the target area (even if longer) before giving up and halting navigation.

## [3.9.4+A-26.1.2] - 2026-06-11
### Summary
The **"Ground Food Refusal Trait"** update. Adds a feature where some tamed-from-birth dogs persistently refuse to eat food from the ground.
- **Ground Food Refusal Trait**: Added a feature where some tamed-from-birth dogs (bred from parents, not wild-tamed) persistently refuse to eat food from the ground.
  - Adds `bd_enable_refuse_ground_food` GameRule and toggle setting to enable/disable the feature globally.
  - Adds `bd_refuse_ground_food_chance` GameRule and setting to configure the percentage chance (default: 30%) that a puppy receives the refusal trait at birth.

## [3.9.3+A-26.1.2] - 2026-06-11
### Summary
The **"Wolf-on-Wolf Collision Push Safety"** update. Adds cooperative collision handling between tamed wolves.
- **Wolf-on-Wolf Collision Push Safety**: Added cooperative collision handling between tamed wolves.
  - Tamed wolves colliding with a sitting, guarding, or endangered dog (facing a cliff/lava/magma/fire hazard) will immediately halt their navigation path.
  - A patience timer (`pushWaitTimer` of 60 ticks / 3 seconds) is applied to the pushing dog to prevent continuous pathfinding attempts and jittering.
  - Player-to-dog pushes are exempted to prevent player blocking.

## [3.9.2+R-26.1.2] - 2026-06-06
### Summary
- **Release Promotion**: Promoted the Select-and-Ride Dog Command System and Cloth Config GUI warning layout fixes to a stable production Release.

## [3.9.1+A-26.1.2] - 2026-06-06
### Summary
- **Optional GUI Integration**: Upgraded config screen classloading to resolve via `GuiHelper` in `DasikLibrary` 1.8.2.
- **UI Warning Polish**: Removed repetitive hover tooltips from config options and moved them to category headers.

## [3.9.0+A-26.1.2] - 2026-06-06
### Summary
The **"Select-and-Ride Dog Command System"** update. Adds a Stick-based dog selection, mounting, and dismounting system with custom visual sitting poses.
- **Select-and-Ride Dog Command System**: Command dogs to sit/ride in vehicles, mounts, and modded chairs using a Stick tool.
- **Visual Sitting Pose**: Force visual sitting pose on wolves while riding any vehicle.
- **Dismount Controls**: Dismount using Stick (dismount & stand) or Empty-hand Shift+Right-click (dismount & sit).
- **Optional Unrestricted Riding GameRule**: Added `betterdogs:allow_unrestricted_dog_riding` to allow dogs to ride any entity (e.g. Ghasts).

## [3.8.4+A-26.1.2] - 2026-06-06
### Summary
The **"Unrelated Mate Prioritization"** update. Prioritizes breeding with unrelated wolves in range.
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

---

## 📚 Historical Archives

Archived chronologically to stay within the 46,080-byte ceiling:

- [`History_2026_Jan_May23_Archive.md`](History_2026_Jan_May23_Archive.md) — `3.6.1+R-26.1.2` .. `v1.8.0-26.1` (`68` releases)
