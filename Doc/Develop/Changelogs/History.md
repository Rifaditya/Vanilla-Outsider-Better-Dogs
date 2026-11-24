# Better Dogs History & Concept Changelog

## [5.7.24+26.3] - 2026-11-23

### Fixed
- **Sitting Pose State Desync During Movement & AI Transitions ([BL-BD-069])**:
  - **Reported By**: `Apeirohex` via Discord community support (credited in `CHANGELOG.md`).
  - Added single-purpose `WolfSittingPoseHelper` and `WolfExtensions.betterdogs$isPerformingScoot()` seam for future `BL-BD-070` scoot behavior, wired into `WolfMixin` via `syncSittingPoseOnTick` to clear non-ordered sitting pose (`setInSittingPose(false)`) only on real server-side transitions when navigating, targeting, or moving beyond `1.0e-4` horizontal sqr / `0.15` vertical delta (preserving resting `-0.0784` gravity, `isOrderedToSit()`, and `isPassenger()`).
  - Updated `PersonalityFollowOwnerGoal` (`start()` unordered pose clear; `stop()` gated by `canEnterLazyRestingPose(...)` and `(0.0, dm.y, 0.0)` horizontal inertia zeroing so tick-0 follow deceleration does not immediately cancel lazy resting).
  - Wired `WolfMobMixin` (`setTarget` `@At("TAIL")`) and `WolfDamageHandler` (`clearSittingPoseOnDamage` post interception and friendly-fire cancellation checks) to stand lazy-resting dogs up immediately upon combat targeting or non-cancelled damage.
  - Updated companion AI goals (`TamedWanderNearOwnerGoal`, `WanderlustGoal`, `WolfSeekWaterOnFireGoal`, and `WolfGuardGoal`) to clear stale unordered sitting poses, and bound `wasSitting` in `AvoidGhastFireballGoal`, `FleeExplodingHazardGoal`, and `WolfProjectileInterceptGoal` strictly to `isOrderedToSit()` so evasion never promotes lazy rests into permanent ordered sits.
  - Verified with `SittingPoseStateTransitionTest` (`410/410`) and `BetterDogsAIBehaviorGameTest.testLazySittingPoseClearsOnMoveTargetDamageAndPreservesOrderedSit` (`11/11`).

## [5.7.23+26.3] - 2026-11-23

### Fixed
- **Genetic Scale & Voice Pitch Persistence Across Spawn, Breeding, Runt & Cure ([BL-BD-071])**:
  - **Reported By**: `@UnderscoreNash` via GitHub Issue #14 (credited in `CHANGELOG.md`).
  - Routed wild/summoned scale writes (`WolfSpawnMixin`), offspring scale writes (`WolfBreedingMixin`), inbred runt penalties (`WolfInbreedingHelper`), and Golden Apple cure restoration (`WolfCureHelper`) through `WolfExtensions.betterdogs$setSocialScale(float)` to persist `WolfPersistentData.scale` atomically with `Attributes.SCALE`.
  - Updated `WolfBreedingMixin`, `WolfInbreedingHelper`, and `WolfCureHelper` scale reads to prefer persisted `betterdogs$getSocialScale()` (`> 0.0f` fallback `Attributes.SCALE`), eliminating the `WolfSocialMixin.onTickTail` tick-1 snap back to `1.00` Scale / `1.000` Pitch Timbre while leaving existing `1.0f` wolves untouched.
  - Sanitized `NaN`, non-positive (`<= 0`), and infinite float inputs to `1.0f` in `WolfPersistentData`'s compact canonical constructor, `WolfPersistentData.setScale`, and `WolfSocialMixin.betterdogs$setSocialScale`.
  - Added headless verification (`ScalePersistenceTest`, `AdoptionSystemTest.AdoptionStubWolf`) and live `@GameTest` coverage (`BetterDogsAIBehaviorGameTest.testGeneticScalePersistsAcrossTicksAndBreedCure`).

## [5.7.17+26.3] - 2026-11-16

### Fixed
- **Restored Full Vanilla-Client Dedicated Server Compatibility ([BL-BD-064])**:
  - Special thanks to `QienSnowpoff` for identifying and reporting dedicated server desyncs when unmodded vanilla clients join modded servers!
  - **Dynamic Sound Event Decoupling**: Removed static `BuiltInRegistries.SOUND_EVENT` registration to prevent registry synchronization freezes on unmodded clients. Implemented dual sound routing in `WolfHowlAudioHelper`: sends direct variable-range sound packets to Better Dogs clients, and gracefully falls back to pitched-down vanilla baby wolf whines (`[0.50f, 0.80f]`) for vanilla clients.
  - **Capability Handshake**: Added `BetterDogsClientCapPayload` (`betterdogs:client_audio_cap`) clientbound play payload and receiver for zero-overhead client mod detection via standard channels and local singleplayer addresses.
  - **Synced Entity Data & Mixin Hygiene**: Removed `BETTERDOGS$DATA_SUBDUED` and `BETTERDOGS$DATA_SPECTATOR_WAGGING` `EntityDataAccessor` slots and `WolfRendererMixin`, preserving internal tick logic, personality state, and NBT saves while eliminating entity tracker crashes on vanilla clients.
  - **Translatable Text & Advancement Resilience**: Converted all 20 server-sent player notification components across listeners and helpers to `Component.translatableWithFallback` matching `en_us.json`, preventing untranslated translation key popups on unmodded clients. Added resilient command support footer in `BetterDogsSupportFooter`.
  - **Advancement Fallbacks & Subtitles**: Added `"fallback"` text across all 15 husbandry advancement JSON definitions and added `"subtitles.betterdogs.entity.wolf.howl"` subtitle localization.

## [5.7.14+26.3] - 2026-11-14

### Fixed
- **Null-Safe Blood Feud Persistence & Adoption Codec Safety ([BL-BD-059])**:
  - Updated `WolfInteractionHelper` non-sneak adoption to invoke `BloodFeudHelper.clearBloodFeud(wolf)` instead of passing `null` into `betterdogs$setBloodFeudTarget(null)`.
  - Aligned `WolfAdoptionHelper.tryHandleAdoption` sneak empty-hand adoption to reset `leaderUuid`, clear blood feuds (`BloodFeudHelper.clearBloodFeud(wolf)`), clear `guardMode`/`guardPos`, and call `wolf.setOrderedToSit(false)`.
  - Added compact canonical constructors on `WolfPersistentData`, `BaseData`, and `ExtraData`, null-safe normalization in `WolfMixin` and `WolfPersistentData` blood feud accessors, and thread-safe `WeakHashMap` `HEADLESS_FALLBACK_DATA` fallback for plain JUnit persistence testing.

## [5.1.7+26.3] - 2026-10-17

### Fixed
- **GameRule Identifier Crash Resolution ([BL-BD-016])**: Standardized GameRule registration identifiers to strictly valid lowercase snake_case (`betterdogs:bd_dog_play_fighting`, `betterdogs:bd_play_penalty_minutes`), preventing startup `IdentifierException` crashes on Minecraft 26.3 while maintaining full backward-compatible translation aliases.
- **Contract Verification Harness**: Implemented `GameRuleRegistryContractTest.java` enforcing lowercase formatting, regex validation (`^[a-z0-9_.-]+:[a-z0-9/._-]+$`), and localization key parity at compile time.
- **Dasik Library Defensive Hardening**: Hardened `DynamicGameRuleManager` with automated lowercase normalization and reflection-based headless registry unfreezing fallback in Dasik Library 1.11.2.
- **Systemic Broken Build Quarantine**: Quarantined `5.1.6+26.3` with `- [!]` in `RELEASE_QUEUE.md` and purged broken JARs from release hub and test profiles.

## [5.1.6+26.3] - 2026-10-03

### Fixed
- **Wolf Attack Swing Compatibility (GitHub Issue #9 [BL-BD-015])**: Updated wolf combat goals (`WolfFlankAttackGoal`, `AdultCorrectionGoal`, `BabyBiteBackGoal`) to call `wolf.swingForAttack(InteractionHand.MAIN_HAND)`, preventing `NoSuchMethodError` crashes during pack flanking and retaliation in Minecraft 26.3.
- **Manifest Contact Metadata**: Added official GitHub Issues tracker URL and Modrinth project metadata to `fabric.mod.json`.

## [5.1.0+26.3] - 2026-10-03

### Added
- **Harmless Pack Play Sparring AI Suite ([BL-BD-008])**: Implemented playful sparring AI with circles, dynamic pouncing, spectator dog reactions, and tail-wagging.
- **Adult Discipline & Hierarchy Correction Snap**: Non-lethal hierarchy correction snaps (0.5 hearts to adults, 0 damage to puppies with gentle growl) upon excessive pounces (3-5 pounces), silencing pack alerts and suppressing retaliation.
- **Play Timeout Penalties & Mood Postures**: 10-minute penalty timer, subdued tail posture, and gentle submissive whines.
- **Comforting Treat Soothing**: Feeding comforting treats (bones, meats) soothes mood and reduces penalty by up to 10% per treat.
- **Pack Audience Reactions**: Nearby packmate dogs spectate sparring matches with excited tail wags (`PackAudienceHelper`).
- **Advancements & GameRules**: Added `Roughhousing` and `Know Your Place` husbandry advancements with `betterdogs:dogPlayFighting` and `betterdogs:playPenaltyMinutes`.

## [5.0.99+26.3] - 2026-09-07

### Fixed
- **Wolf Attack Swing Method Compatibility ([BL-DOG-002] / [ERR-20260907-007])**: Updated `WolfFlankAttackGoal`, `AdultCorrectionGoal`, and `BabyBiteBackGoal` to call `wolf.swingForAttack(InteractionHand.MAIN_HAND)` instead of the removed `wolf.swing(InteractionHand)`, resolving a `NoSuchMethodError` crash on entity attack in Minecraft 26.3. `LivingEntity.swing(InteractionHand)` was removed in 26.3-pre-2; `Mob.swingForAttack(InteractionHand)` is the correct combat animation method matching vanilla `MeleeAttackGoal`.

## [5.0.98+26.3] - 2026-09-07

### Fixed
- **NaturalSpawner Mixin Signature Compatibility ([BL-DOG-001] / [ERR-20260907-004])**: Updated injection descriptor for `NaturalSpawner.mobsAt` to adapt to Minecraft 26.3 dropping the `Holder<Biome>` parameter (5 arguments instead of 6). Evaluated `level.getBiome(pos)` for expanded biomes spawning.

## [5.0.87+26.3]
### Added
- 🌐 **Italian (`it_it`) Localization & Player Guide (11-Language Suite Complete)**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/it_it.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_it_it.md`.

## [5.0.86+26.3]
### Added
- 🌐 **Korean (`ko_kr`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/ko_kr.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_ko_kr.md`.

## [5.0.85+26.3]
### Added
- 🌐 **French (`fr_fr`, `fr_ca`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/fr_fr.json` and `fr_ca.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_fr_fr.md`.

## [5.0.84+26.3]
### Added
- 🌐 **Japanese (`ja_jp`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/ja_jp.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_ja_jp.md`.

## [5.0.83+26.3]
### Added
- 🌐 **Portuguese (`pt_br`, `pt_pt`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/pt_br.json` and `pt_pt.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_pt_br.md`.

## [5.0.82+26.3]
### Added
- 🌐 **German (`de_de`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/de_de.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_de_de.md`.

## [5.0.81+26.3]
### Added
- 🌐 **Spanish (`es_es`, `es_mx`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/es_es.json` and `es_mx.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_es_es.md`.

## [5.0.80+26.3]
### Added
- 🌐 **Russian (`ru_ru`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/ru_ru.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_ru_ru.md`.

## [5.0.79+26.3]
### Added
- 🌐 **Traditional Chinese (zh_tw, zh_hk) Localization & Player Guide**:
  - Added complete ssets/vanilla-outsider-better-dogs/lang/zh_tw.json and zh_hk.json matching all 428 translation keys.
  - Added localized player guide in Doc/Players/index_zh_tw.md.

## [5.0.78+26.3]
### Added
- 🌐 **Simplified Chinese (zh_cn) Localization & Player Guide**:
  - Added complete ssets/vanilla-outsider-better-dogs/lang/zh_cn.json matching all 428 translation keys.
  - Added localized player guide in Doc/Players/index_zh_cn.md.

## [5.0.77+26.3]
### Added
- 🌐 **Indonesian (id_id) Full Parity Localization & Player Guide**:
  - Audited and synchronized ssets/vanilla-outsider-better-dogs/lang/id_id.json for 100% key parity with en_us.json.
  - Added localized Indonesian player guide in Doc/Players/index_id_id.md.

## [5.0.76+26.3] - 2026-08-25
### Changed
- **Milestone Backlog Pruning & Clean State Reset**:
  - Successfully resolved and verified all backlog items (`[BL-BD-001]` through `[BL-BD-007]`).
  - Pruned completed backlog sections adhering to the Backlog Resolution & Pruning Law, resetting the summary table to a clean ready state.

## [5.0.75+26.3] - 2026-08-25
### Added
- **Master Full-Suite Verification & Helper Contract Audit**:
  - Implemented `BetterDogsFullSuiteTest` running exhaustive contract assertions across all 12 modular helper classes (`WolfCliffSafetyHelper`, `WolfHazardHelper`, `WolfStormHelper`, `WolfBeggingHelper`, `WolfFetchHelper`, `WolfGuardHelper`, `WolfInbreedingHelper`, `WolfScaleGeneticsHelper`, `WolfPersonalityStatHelper`, `DogTreatHelper`, `WolfGiftHelper`, `BabyCuriosityHelper`).

## [5.0.74+26.3] - 2026-08-25
### Added
- **Data-Driven Tags & Zero-Allocation Performance Test Suite**:
  - Created `DataDrivenAndPerformanceTest` asserting `FastRandom.INSTANCE` integer distribution, `BetterDogsTags` registration completeness, `BabyCuriosityHelper` fallback resilience, and `DogTreatHelper` pool constancy.

## [5.0.73+26.3] - 2026-08-25
### Changed
- **Zero-Allocation FastRandom & Loop Memory Overhaul**:
  - Replaced static `new Random()` instances in `WolfPersonality`, `WolfBreedingMixin`, and `WolfLitterHelper` with `FastRandom.INSTANCE` and entity `RandomSource`.
  - Replaced transient array allocations in breeding genetics calculations with zero-allocation static pattern switches.

## [5.0.72+26.3] - 2026-08-25
### Changed
- **Data-Driven Datapack Tag Migration (Foliage, Treats, Seats)**:
  - Created `#vanilla-outsider-better-dogs:curiosity_blocks` replacing hardcoded curiosity block checks.
  - Created `#vanilla-outsider-better-dogs:treats` for favorite dog treat customization.
  - Created `#vanilla-outsider-better-dogs:seats` and `#c:chairs` for dog seat candidates.
  - Registered tags in `BetterDogsTags` and integrated with safe in-code fallbacks.

## [5.0.71+26.3] - 2026-08-25
### Added
- **Morning Gift Headless JUnit 5 Test Suite Expansion**:
  - Expanded `MorningGiftTest` covering strict 100% full health requirement, morning time window boundary calculations, peaceful monster proximity rejection, calendar cooldown limits, and personality fallback item roll validity.

## [5.0.70+26.3] - 2026-08-25
### Added
- **Data-Driven Morning Gift Loot Tables (`loot_table/morning_gift/`)**:
  - Implemented datapack loot tables for `aggressive.json` (mob trophies), `pacifist.json` (nature finds), `normal.json` (domestic foraging), and `rare_treasure.json` (gold nuggets, emeralds, name tags, leads).
  - Updated `WolfGiftHelper` to roll loot through server `LootTable` registries with 100% safe in-code fallback.

## [5.0.69+26.3] - 2026-08-25
### Changed
- **Morning Gift Qualification Conditions & Dual Wake-Up Trigger (`WolfGiftHelper`)**:
  - Implemented dual trigger: immediately on waking up from bed, or during early morning (ticks 0–2000).
  - Enforced strict 100% full dog health requirement (`wolf.getHealth() >= wolf.getMaxHealth()`).
  - Enforced 1 gift per calendar day limit (`lastGiftDay < currentDay`).
  - Enforced peaceful surrounding check (zero hostile monsters within 16 blocks).

## [5.0.68+26.3] - 2026-08-25
### Added
- **Wolf Fire Survival AI: Headless JUnit 5 Test Suite Expansion**:
  - Implemented `WolfFireSurvivalTest` covering burning activation qualification, passenger rejection, emergency break-sit and posture restoration cycle, water search 16m proximity math, safe water drop cliff bypass, and fallback panic evasion logic.

## [5.0.67+26.3] - 2026-08-25
### Changed
- **Wolf Fire Survival AI: Safe Water Drop Cliff Bypass (`WolfCliffSafetyHelper`)**:
  - Implemented `isSafeWaterLanding` to detect water bodies below ledges, bridges, and riverbanks.
  - Allowed wolves leaping or pushed towards water to bypass false-positive cliff drop (>3 blocks) freezes and safely extinguish themselves.

## [5.0.66+26.3] - 2026-08-25
### Added
- **Wolf Fire Survival AI: Goal & Extinguishing Logic (`WolfSeekWaterOnFireGoal`)**:
  - Registered priority 1 AI goal activating when wolves catch fire.
  - Implemented 16m nearest water source detection and sprint at `1.4x` movement speed.
  - Implemented fallback erratic panic run (`1.3x` speed) with whimper vocalizations and smoke particles if no water is reachable.
  - Implemented emergency break-sit mechanic to save sitting dogs from burning in place, restoring posture upon extinguishing.
  - Registered GameRules `betterdogs:bd_wolves_seek_water_on_fire` and `betterdogs:bd_wolves_break_sit_on_fire`.

## [5.0.65+26.3] - 2026-08-25
### Changed
- **Clean "1 File, 1 Purpose" Architecture: Command & Listener Modularization**:
  - Extracted `DogEntityCommandListener.java` for entity-targeted riding, commands, and dismounting interactions.
  - Extracted `DogBlockCommandListener.java` for chair/stair seat interaction and invisible interaction entity management.
  - Refactored `DogCommandManager.java` into a thread-safe repository for selection and target state management.

## [5.0.64+26.3] - 2026-08-25
### Changed
- **Clean "1 File, 1 Purpose" Architecture: Tick & Health Modularization**:
  - Extracted single-purpose `WolfHealingHelper.java` for passive out-of-combat regeneration timing.
  - Extracted single-purpose `WolfSoundHelper.java` for ambient vocalizations, health-dependent whining, and sound overrides.
  - Moved `tickGuardMode` to `WolfGuardHelper.java`, `tickNemesisSystem` to `WolfNemesisHelper.java`, and `tickRuntParticles`/`tickAdoptableParticles` to `WolfParticleHelper.java`.
  - Updated `WolfMixin.java` to call specialized single-purpose helpers directly.
  - Purged obsolete `WolfTickHelper.java` class.

## [5.0.63+26.3] - 2026-08-25
### Changed
- **Clean "1 File, 1 Purpose" Architecture: Combat & Targeting Modularization**:
  - Deconstructed composite `WolfCombatHooks` into two single-responsibility handlers:
    - `WolfDamageHandler.java`: Handles `actuallyHurt` logic (accidental/intentional feed demerits, adoption cancellation on hit, play-fight 1-HP safety clamp, friendly fire delegation, and baby retaliation scheduling).
    - `WolfTargetingHandler.java`: Handles `wantsToAttack` target qualification overrides (social actions, 1v1 territorial war leader protection, blood feud nemesis targeting with affinity suppression, submissive pack protection, and baby attack restrictions).
  - Updated `WolfCombatMixin.java` to route directly to `WolfDamageHandler` and `WolfTargetingHandler`.
  - Purged obsolete `WolfCombatHooks.java` class.

## [5.0.62+26.3] - 2026-08-25
### Added
- **Guard Mode Headless Unit Test Suite Expansion (`GuardModePatrolTest.java`)**:
  - Added JUnit 5 assertions for 4-parameter `canToggleGuard` and `toggleGuardMode` verifying Main Hand enforcement, off-hand rejection, backward-compatible overloads, and strict null safety.

## [5.0.61+26.3] - 2026-08-25
### Changed
- **Legacy Bone Deduplication & Guard Sit Handler Fix (`WolfInteractionHelper.java`)**:
  - Purged duplicate legacy bone guard handling block (lines 281–321) that bypassed `WolfGuardHelper`.
  - Removed `!itemStack.is(Items.BONE)` exclusion from the guard dog sitting handler, allowing normal non-sneak right-clicks with bones to cleanly toggle manual sitting/standing at the guard post without consuming items.

## [5.0.60+26.3] - 2026-08-25
### Changed
- **Guard Mode Gating & 6D Interaction Overhaul (`WolfGuardHelper.java`, `WolfInteractMixin.java`)**:
  - Enforced Sneak/Shift requirement (`player.isSecondaryUseActive()`) and `MAIN_HAND` check for Guard Mode bone toggling, ensuring normal non-sneak bone right-clicks safely toggle sitting posture without activating Guard Mode or consuming bones.
  - Implemented dual-direction bone consumption: consuming 1 bone on both Guard Mode activation and deactivation (with Creative mode bypass `!player.getAbilities().instabuild`).
  - Implemented sitting posture preservation: seated dogs maintain their sitting pose upon activating Guard Mode to act as stationary sentries at their assigned guard post.
  - Integrated personality-specific audio pitches (Aggressive: `0.8f`, Normal: `1.2f`, Pacifist: `1.5f` whine sound) and translatable coordinate notifications (`text.betterdogs.guard_activated` / `text.betterdogs.guard_deactivated`).

## [5.0.59+26.3] - 2026-08-22
### Added
- **Hoover / Ground Food Scavenger Quirk (`WolfDispositionHelper.java`, `WolfScavengeHelper.java`, `EatGroundFoodGoal.java`)**:
  - Implemented the Hoover behavioral quirk where dogs eagerly scavenge dropped food items from the ground even when at $100\%$ full health.
  - **Base Personality Rates**: Aggressive ($70\%$), Normal ($35\%$), Pacifist ($10\%$) with $[-100\%, +100\%]$ UUID variance offset.
  - **Digestion Cooldown**: Enforced a $160\text{ ticks}$ ($8\text{ seconds}$) cooldown between ground food snacks at full health to prevent vacuuming item stacks.
  - **Posture Safety**: Fully respects sitting commands (`isOrderedToSit()`); seated dogs will never break posture.

## [5.0.58+26.3] - 2026-08-22
### Changed
- **Full-Spectrum [-100%, +100%] UUID Behavioral Variance (`WolfDispositionHelper.java`)**:
  - Expanded individual dog UUID offset range to $[-100\%, +100\%]$ (clamped to $[0\%, 100\%]$).
  - Applied base personality values:
    - **Fetch Reluctance**: Aggressive ($10\%$), Normal ($30\%$), Pacifist ($60\%$).
    - **Storm Fearlessness**: Aggressive ($80\%$), Normal ($40\%$), Pacifist ($10\%$).
    - **Quiet Howling**: Aggressive ($10\%$), Normal ($25\%$), Pacifist ($60\%$).

## [5.0.57+26.3] - 2026-08-22
### Changed
- **Additive Modifier Behavioral Quirk Model (`WolfDispositionHelper.java`)**:
  - Implemented additive modifier variance model with wide personality spread:
    - **Fetch Reluctance**: Aggressive ($0\text{--}5\%$), Normal ($3\text{--}18\%$), Pacifist ($15\text{--}45\%$).
    - **Storm Fearlessness**: Aggressive ($90\text{--}100\%$), Normal ($10\text{--}35\%$), Pacifist ($0\text{--}15\%$).
    - **Quiet Howling**: Aggressive ($0\text{--}10\%$), Normal ($5\text{--}30\%$), Pacifist ($25\text{--}65\%$).
  - Combined personality baseline percentages with deterministic UUID offsets ($\pm 10\%$ to $\pm 20\%$).

## [5.0.56+26.3] - 2026-08-22
### Added
- **Personality + UUID Seeded Behavioral Variance (`WolfDispositionHelper.java`)**:
  - Deterministically seeded individual behavioral nuances using 64-bit bit-mixing hashing over `UUID` + `WolfPersonality` + behavior salt (zero NBT storage).
  - ~5% of dogs are organically reluctant to fetch sticks; non-fetchers either curiously tilt their heads (`setIsInterested(true)`) or naturally ignore nearby thrown sticks.
  - ~10% of dogs are naturally fearless during thunderstorms, bypassing storm panic and whimpering.
  - ~15% of dogs are quiet observers who refrain from participating in nocturnal group howling choruses.

## [5.0.55+26.3] - 2026-08-22
### Changed
- **Subtle Particle Feedback for Fetch, Gifts & Treat Feeding (`WolfFetchHelper`, `WolfGiftHelper`, `DogTreatHelper`, `WolfParticleHelper`)**:
  - Integrated `WolfParticleHelper.spawnSubtleParticles` to scale subtle cues with `bd_particle_density` GameRule (`NONE`: 0, `LOW`: 1, `MEDIUM`/`HIGH`: 2).
  - Reduced stick fetch and morning gift particles to 2 subtle emerald glints (`0.15` spread, `0.02` speed).
  - Softened favorite treat feeding visual feedback to 2 happy villager + 1 heart particle.

## [5.0.39+26.3] - 2026-08-19
### Fixed
- **Jade Provider Separation ("1 File, 1 Purpose")**:
  - Separated client tooltip component providers (`InbredStatusProvider`, `WolfInfoProvider`) from server data sync providers (`InbredStatusDataProvider`, `WolfInfoDataProvider`).
  - Restored 100% full compatibility across newer Jade releases (`Jade >= 26.2.11+`), older Jade releases, and standalone Fabric.

## [5.0.38+26.3] - 2026-08-19 - ⚠️ Incompatible with Jade >= 26.2.11+ (Works on Jade < 26.2.10 or without Jade)

## [5.0.22+26.3] - 2026-08-11
### Refactored & Polished
- **Minimal Tame Particle Polish (`WolfParticleHandler`)**: Refactored first-time tame particles into a clean ~5-6 particle Minimal Signature per personality (Aggressive: crimson dust + angry villager; Normal: golden dust + happy villager; Pacifist: mint dust + heart).

## [5.0.21+26.3] - 2026-08-10
### Fixed
- **YACL Config Option Descriptions (`YaclScreenHelper` & Translation Sheets)**: Added missing `.description(...)` OptionDescription builders in `YaclScreenHelper` across all 6 config categories and synchronized complete `.description` keys in `en_us.json` and `id_id.json`.

## [5.0.19+26.3] - 2026-08-10
### Fixed
- **Dog Adoption Crash Fix (`WolfInteractionHelper` & `WolfStatManager`)**: Fixed `NullPointerException` crash during adoption by adding personality null guards in `WolfStatManager` and pre-assigning random personalities for uninitialized dogs.

## [5.0.18+26.3] - 2026-08-10
### Fixed & Added
- **Litematica Compatibility & Command Item Expansion (`DogCommandManager`)**:
  - **Litematica Tool Conflict Fix**: Resolved stick tool interaction collision with Litematica by introducing `#vanilla-outsider-better-dogs:command_items` item tag (Stick, Blaze Rod, Breeze Rod, `#c:tools/sticks`).
  - **Alternative Command Tools**: Enables Blaze Rods and Breeze Rods as alternative command tools alongside standard sticks for dismounting and seat commanding.
  - **Bone Reservation Safeguard**: Explicitly excluded `minecraft:bone` from command items so Bone remains 100% reserved for Guard Mode (`bd_guard_mode`).

## [5.0.17+26.3] - 2026-08-10
### Added
- **Dynamic Climate-Aware Wolf Coat Variants (`WolfVariantHelper`)**: Automatically assigns coat variants based on biome physical climate properties when Vanilla tag lookup defaults to Pale.

## [5.0.16+26.3] - 2026-07-31
### Fixed
- **Automated GameTest Execution & Final Audit Pass**: Purged unused `HashMap` import in `BetterDogsGameTests.java` and executed automated GameTest checks (`./gradlew check`) under Core-auditor directives.

## [5.0.15+26.3] - 2026-07-31
### Added
- **Additional GameTest Expansion**: Added `testWolfFetchGoalTriggering`, `testZoomiesGoalTriggering`, `testStormAnxietySoothedState`, and `testAvoidHazardsGoalGating` to `BetterDogsGameTests.java` for automated headless verification of AI goal event scheduling, storm soothing, and hazard gating.

## [5.0.14+26.3] - 2026-07-31
### Fixed
- **Unused Imports Cleanup & 100% Audit Pass**: Purged unused imports in `WolfBreedingMixin` and `WolfGuardGoal` to achieve a 100% clean audit pass under Core-auditor rules.

## [5.0.13+26.3] - 2026-07-31
### Fixed
- **Audit Remediation & Code Quality Cleanup**: Fixed license headers to standard `// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3` across `WolfPersistentData`, `WolfBreedingMixin`, `WolfNemesisTargetGoal`, and `WolfGuardGoal`. Added missing citation comment to `WolfNemesisTargetGoal` and removed unused `ParticleTypes` import in `WolfGuardGoal`.

## [5.0.12+26.3] - 2026-07-31
### Changed
- **AI Goal Tick Throttling & Spatial Search Optimization**: Added entity ID staggered modulo gating to `FleeCreeperGoal`, `WolfNemesisTargetGoal`, and `WolfGuardGoal` to prevent all wolves from running expensive spatial bounding-box searches on the same tick frame.

## [5.0.11+26.3] - 2026-07-31
### Added
- **Automated GameTest Expansion**: Added `testGeneticsScaleClamping`, `testKinshipAndInbreedingRisk`, `testTraitMutationAndReset`, and `testGuardModePosAnchoring` to `BetterDogsGameTests.java`.

## [5.0.10+26.3] - 2026-07-31
### Added
- **Dynamic Trait Modifiers & Stat Reset API Integration**: Integrated `setTrait`, `modifyTrait`, and `resetGenetics` methods from `DasikAnimalGeneticsAPI` in `DasikLibrary` v1.8.8. Delegated `setPersistedHealthBonus`, `setPersistedDamageMod`, and `setPersistedSpeedMod` in `WolfPersistentData.java` directly to `DasikAnimalGeneticsAPI.setTrait`.

## [5.0.9+26.3] - 2026-07-31
### Added
- **Kinship & Pedigree API Expansion**: Added `isRelated`, `isParentOf`, `areSiblings`, and `predictInbreedingRiskPercent` helper methods to `DasikAnimalGeneticsAPI` in `DasikLibrary` v1.8.7 for family tree tracking and inbreeding risk prediction.

## [5.0.8+26.3] - 2026-07-31
### Added
- **Size-Stats & Scale API Expansion**: Added universal `getScale`, `setScale`, `isRunt`, and `isGiant` helper methods to `DasikAnimalGeneticsAPI` in `DasikLibrary`. Delegated wolf scale querying in `WolfPersistentData.java` directly to `DasikAnimalGeneticsAPI.getScale(wolf)`.

## [5.0.7+26.3] - 2026-07-31
### Added
- **Universal AnimalGeneticsAPI Integration**: Created `net.dasik.social.api.genetics.AnimalGeneticsAPI` in `DasikLibrary` providing a standardized, multi-animal genetics API facade (`hasGenetics`, `getTrait`, `getHealthBonus`, `getDamageMod`, `getSpeedMod`, `isInbred`, `inherit`, `rollStats`) usable across all animal mods. Integrated API calls into `WolfPersistentData.java` and `WolfBreedingMixin.java`.

## [5.0.6+26.3] - 2026-07-31
### Refactored
- **NBT Data & Codec Serialization Hardening**: Added null-safety fallback guards to `WolfPersistentData.getWolfData(wolf)` returning `DEFAULT` if attachment decoding yields null. Added NaN/Infinity checks and scale clamping (`Math.clamp(scale, 0.5f, 2.0f)`) to prevent rendering and hitbox corruption from legacy or malformed entity NBT tags.

## [5.0.5+26.3] - 2026-07-31
### Added
- **Automated GameTest Framework Suite & Core Mandate**: Established project-wide `gradle-tester` skill and `Automated GameTest Verification Law` in Core Constitution. Implemented automated GameTest suite in `BetterDogsGameTests.java` covering wolf initialization, Guard Mode anchoring, and state cleanup verification.

## [5.0.4+26.3] - 2026-07-31
### Refactored
- **Memory Leak & Entity Removal Cleanup Hooks**: Implemented `betterdogs$clearTransientState()` and injected `die` and `remove` hooks in `WolfMixin.java`. Nullifies transient `socialTarget` entity references, resets spatial targets (`soundLocationTarget`, `pathfindAvoidPos`), clears Goat Horn override timers (`passiveOverrideTicks`), and cancels running scheduler tasks when a wolf dies or is removed.

## [5.0.3+26.3] - 2026-07-31
### Refactored
- **C2ME & Multi-Threading Asynchronous Chunk Safety**: Added strict `level.hasChunkAt(pos)` guards across AI goals (`WolfStormAnxietyGoal`, `EatGroundFoodGoal`) and teleportation helpers (`WolfTeleportHelper`, `WolfCatchUpHelper`) to prevent chunk loading deadlocks and race conditions under multi-threaded chunk generators (C2ME / Noisium).

## [5.0.2+26.3] - 2026-07-31
### Refactored
- **Mixin Security & Naming Compliance**: Audited all 25 Mixin classes to enforce `@Unique` annotations on all custom fields and helper methods, and ensured strict `betterdogs$` member prefixing across all injection points.
- **Architectural Extraction (Goat Horn Handler)**: Extracted 260-line Goat Horn command logic (Ponder, Feel, Sing, Yearn, Seek) out of `InstrumentItemMixin.java` into standalone helper class `WolfHornHelper.java`, keeping `InstrumentItemMixin` clean and minimal ("1 File, 1 Purpose").

## [5.0.1+26.3] - 2026-07-30
### Fixed
- **MC 26.3 NaturalSpawner Mixin Fix**: Updated `@Inject` parameter descriptor in `NaturalSpawnerMixin.java` for method `mobsAt` (removed `Holder<Biome>` parameter to match MC 26.3 `NaturalSpawner` refactor).
- **GameRule Registry Fix**: Removed duplicate registration line for `betterdogs:bd_creeper_awareness` in `BetterDogsGameRules.java`.

## [5.0.0+26.3] - 2026-07-30
### Added
- **Minecraft 26.3 Upgrade**:
  - Upgraded mod target to **Minecraft 26.3** (`5.0.0+26.3`).
  - **Advancement Trigger API Fix**: Fixed `GuardWolfPersonalityTrigger` codec compatibility for MC 26.3 advancement predicate refactors (`EntityPredicate.CODEC`), resolving world loading crash.

## [4.24.0+26.2] - 2026-07-30
### Added
- **AI Refinement: Creeper Blast Evasion**:
  - **Problem**: When a creeper ignites its fuse near a player or pack, vanilla wolves often stay in close proximity or attack the creeper, taking fatal explosion damage.
  - **Solution**: Enhanced `FleeCreeperGoal.java`. When a nearby creeper begins swelling/igniting within 10 blocks, tamed wolves sprint radially away at maximum speed (`1.5x`).
  - **Smoke Particle Trails**: Emits emergency sprint smoke trails (`ParticleTypes.SMOKE`) at the feet of the fleeing wolf.
  - **GameRules**: Registered `betterdogs:bd_creeper_evasion_enabled` (Boolean, default `true`).

## [4.23.0+26.2] - 2026-07-30
### Added
- **Goat Horn Command System (Stage 5: Seek Horn - Search & Track Target Command)**:
  - **Problem**: In chaotic combat, commanding a wolf pack to focus fire on a specific dangerous mob (e.g. Evoker, Ravager, Warden) or search for hidden hostile mobs is unreliable with vanilla AI.
  - **Solution**: Intercepted `InstrumentItem.use` on server side. Blowing the Seek Goat Horn (`seek_goat_horn`) commands active following wolves in range (governed by `betterdogs:bd_horn_command_range`, default: `64` blocks) to focus fire on a targeted entity at `1.3x` speed.
  - **Cone-Inflated Raycasting**: Created `EntityRaycastHelper.findCrosshairTarget` to reliably select targets along player's view vector up to 32 blocks.
  - **Fallback Area Search**: If no entity is highlighted under crosshair, wolves automatically search `bd_horn_command_range` for hostiles targeting the owner or pack. Emits smoke particles if no hostile targets exist.
  - **Preemption**: Clears assemble pathing targets and Tactical Pacifist override timers.

## [4.22.0+26.2] - 2026-07-30
### Added
- **Goat Horn Command System (Stage 4: Yearn Horn - Resume Follow Command)**:
  - **Problem**: Standing up a pack of scattered sitting dogs individually across a base or field requires walking to each dog and manually right-clicking.
  - **Solution**: Intercepted `InstrumentItem.use` on server side. Blowing the Yearn Goat Horn (`yearn_goat_horn`) orders all sitting owned wolves in range (governed by `betterdogs:bd_horn_command_range`, default: `64` blocks) to stand up (`setOrderedToSit(false)` & `setSittingManually(false)`) and resume follow mode.
  - **Dedicated Filter**: Added `WolfTeleportHelper.isEligibleSittingWolf` to specifically target owned, non-leashed, non-guarding sitting dogs.
  - **State Reset**: Clears assemble pathing targets and Tactical Pacifist override timers. Emits joyful musical note and green particles.

## [4.21.0+26.2] - 2026-07-30
### Added
- **Goat Horn Command System (Stage 3: Sing Horn - Hold Command)**:
  - **Problem**: When owner needs a large pack of active following wolves to hold position immediately across distance, manually right-clicking each dog is slow and inefficient.
  - **Solution**: Intercepted `InstrumentItem.use` on server side. Blowing the Sing Goat Horn (`sing_goat_horn`) orders all active following wolves in range (governed by `betterdogs:bd_horn_command_range`, default: `64` blocks) to sit down in place (`setOrderedToSit(true)` & `setSittingManually(true)`).
  - **Immediate Combat Clear**: Clears active attack targets (`setTarget(null)`, `setLastHurtByMob(null)`), resets social interactions, and halts navigation.
  - **Preemption Handling**: Blowing Sing Horn clears active assemble pathing (`soundLocationTarget = null`) and active Tactical Pacifist overrides (`passiveOverrideTicks = 0`).

## [4.20.0+26.2] - 2026-07-30
### Added
- **Goat Horn Command System (Stage 2: Feel Horn - Tactical Pacifist Override)**:
  - **Problem**: In dangerous combat situations, calling wolves to disengage requires waiting for hostile mobs to die or manually calling individual dogs, risking dog casualties.
  - **Solution**: Blowing the Feel Goat Horn (`feel_goat_horn`) triggers a 30-second (`600` ticks) Tactical Pacifist override on all active following wolves in range (governed by `betterdogs:bd_horn_command_range`, default: `64` blocks).
  - **Immediate Combat Clear**: Wolves instantly clear target references (`setTarget(null)`, `setLastHurtByMob(null)`), halt combat navigation, and emit single-burst green happy villager particles.
  - **Preemption Handling**: Blowing Feel Horn cancels active assemble pathing, and blowing Ponder Horn cancels active Tactical Pacifist mode.

## [4.19.0+26.2] - 2026-07-30
### Added
- **Goat Horn Command System (Stage 1: Ponder Horn - Assemble Call)**:
  - **Problem**: Calling and grouping a large pack of active following wolves across complex terrain requires cumbersome manual fetching or waiting for slow pathing.
  - **Solution**: Intercepted `InstrumentItem.use` on server side. Blowing the Ponder Goat Horn (`ponder_goat_horn`) issues an Assemble Call to all active following wolves in range (governed by `betterdogs:bd_horn_command_range`, default: `64` blocks), commanding them to pathfind to the sounded location at `1.25x` speed.
  - **Visual Confirmation**: Wolves emit single-burst musical note particles upon receiving the call without visual noise.
  - **Strict Follow Filter**: Sitting, leashed, or guarding dogs remain stationed at their posts and ignore the call.

## [4.18.0+26.2] - 2026-07-21
### Added
- **Fast Travel & Chunk-Unload Catch-Up Teleport Safety**:
  - **Problem**: When an owner moves rapidly (via Elytra, horses, speed potions, or boats), following wolves fall far behind and get trapped in unloaded or frozen border chunks before standard vanilla pathfinding teleports them.
  - **Solution**: Intercepted player tick (`ServerPlayer.tick`) to perform a 1-second interval catch-up scan for active following wolves. If a wolf is further than 40 blocks away from the player, it is automatically teleported to a safe ground location near the owner before its chunk unloads.
  - **Strict Follow Filter**: Only teleports wolves in active follow state (`!wolf.unableToMoveToOwner()`), strictly excluding dogs that are sitting, leashed, or assigned to Guard/Sentry Mode. Governed by `betterdogs:bd_fast_travel_catchup`.

## [4.17.0+26.2] - 2026-07-21
### Added
- **Configurable Wild Wolf Pack Group Sizes**:
  - **Problem**: Wild wolf packs were hardcoded to vanilla group sizes (4-4), making it tedious to encounter natural larger packs or adjust spawning behavior for modpacks.
  - **Solution**: Added GameRules `betterdogs:bd_wolf_spawn_group_min` (default: 4) and `betterdogs:bd_wolf_spawn_group_max` (default: 8) to dynamically adjust wild pack group sizes.
- **Expanded Biomes Natural Spawning**:
  - **Problem**: Wild wolves only spawned naturally in sparse taigas and snowy slopes, making them rare and grindy to locate in survival mode.
  - **Solution**: Added GameRule `betterdogs:bd_wolf_spawn_expanded_biomes` (default: false). When enabled, dynamically injects natural wild wolf spawns into plains, meadows, forests, and mountain biomes without modifying biome files.

## [4.16.0+26.2] - 2026-07-21
### Added
- **Owner Teleport Synchronization**:
  - **Problem**: When a player teleports over long distances (via `/tp` or waypoints) or transitions across dimensions, active following wolves are left behind in unloaded origin chunks, requiring tedious manual commands or travel to recover them.
  - **Solution**: Intercepted player teleportation (`ServerPlayer.teleport`) to scan origin vicinity (32-block radius) for active following wolves.
  - **Strict Follow-State Filtering**: Teleportation is strictly restricted to wolves in active follow state (`!wolf.unableToMoveToOwner()`, not sitting, not leashed, and not in Guard/Sentry Mode). Governed by `betterdogs:bd_sync_owner_teleport`.

---

## 📚 Historical Archives

Archived chronologically to stay within the 46,080-byte ceiling:

- [`History_2026_Jun_Jul_Archive.md`](History_2026_Jun_Jul_Archive.md) — `4.15.18+26.2` .. `4.6.23+A-26.2` (`68` releases)
- [`History_2026_May_Jun_Archive.md`](History_2026_May_Jun_Archive.md) — `4.6.22+R-26.2` .. `3.5.0+A-26.1.2` (`69` releases)
- [`History_2026_Jan_May_Archive.md`](History_2026_Jan_May_Archive.md) — `3.4.18+A-26.1.2` .. `v1.8.0-26.1` (`61` releases)
