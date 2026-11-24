# Better Dogs History & Concept Changelog

## [5.7.24+26.2] - 2026-11-23

### Fixed
- **Sitting Pose State Desync During Movement & AI Transitions ([BL-BD-069])**:
  - **Reported By**: `Apeirohex` via Discord community support (credited in `CHANGELOG.md`).
  - Added single-purpose `WolfSittingPoseHelper` and `WolfExtensions.betterdogs$isPerformingScoot()` seam for future `BL-BD-070` scoot behavior, wired into `WolfMixin` via `syncSittingPoseOnTick` to clear non-ordered sitting pose (`setInSittingPose(false)`) only on real server-side transitions when navigating, targeting, or moving beyond `1.0e-4` horizontal sqr / `0.15` vertical delta (preserving resting `-0.0784` gravity, `isOrderedToSit()`, and `isPassenger()`).
  - Updated `PersonalityFollowOwnerGoal` (`start()` unordered pose clear; `stop()` gated by `canEnterLazyRestingPose(...)` and `(0.0, dm.y, 0.0)` horizontal inertia zeroing so tick-0 follow deceleration does not immediately cancel lazy resting).
  - Wired `WolfMobMixin` (`setTarget` `@At("TAIL")`) and `WolfDamageHandler` (`clearSittingPoseOnDamage` post interception and friendly-fire cancellation checks) to stand lazy-resting dogs up immediately upon combat targeting or non-cancelled damage.
  - Updated companion AI goals (`TamedWanderNearOwnerGoal`, `WanderlustGoal`, `WolfSeekWaterOnFireGoal`, and `WolfGuardGoal`) to clear stale unordered sitting poses, and bound `wasSitting` in `AvoidGhastFireballGoal`, `FleeExplodingHazardGoal`, and `WolfProjectileInterceptGoal` strictly to `isOrderedToSit()` so evasion never promotes lazy rests into permanent ordered sits.
  - Verified with `SittingPoseStateTransitionTest` (`392/392`).

## [5.7.23+26.2] - 2026-11-23

### Fixed
- **Genetic Scale & Voice Pitch Persistence Across Spawn, Breeding, Runt & Cure ([BL-BD-071])**:
  - **Reported By**: `@UnderscoreNash` via GitHub Issue #14 (credited in `CHANGELOG.md`).
  - Routed wild/summoned scale writes (`WolfSpawnMixin`), offspring scale writes (`WolfBreedingMixin`), inbred runt penalties (`WolfInbreedingHelper`), and Golden Apple cure restoration (`WolfCureHelper`) through `WolfExtensions.betterdogs$setSocialScale(float)` to persist `WolfPersistentData.scale` atomically with `Attributes.SCALE`.
  - Updated `WolfBreedingMixin`, `WolfInbreedingHelper`, and `WolfCureHelper` scale reads to prefer persisted `betterdogs$getSocialScale()` (`> 0.0f` fallback `Attributes.SCALE`), eliminating the `WolfSocialMixin.onTickTail` tick-1 snap back to `1.00` Scale / `1.000` Pitch Timbre while leaving existing `1.0f` wolves untouched.
  - Sanitized `NaN`, non-positive (`<= 0`), and infinite float inputs to `1.0f` in `WolfPersistentData`'s compact canonical constructor, `WolfPersistentData.setScale`, and `WolfSocialMixin.betterdogs$setSocialScale`.
  - Added headless verification (`ScalePersistenceTest`, `AdoptionSystemTest.AdoptionStubWolf`).

## [5.7.17+26.2] - 2026-11-16

### Fixed
- **Restored Full Vanilla-Client Dedicated Server Compatibility ([BL-BD-064])**:
  - Special thanks to `QienSnowpoff` for identifying and reporting dedicated server desyncs when unmodded vanilla clients join modded servers!
  - **Dynamic Sound Event Decoupling**: Removed static `BuiltInRegistries.SOUND_EVENT` registration to prevent registry synchronization freezes on unmodded clients. Implemented dual sound routing in `WolfHowlAudioHelper`: sends direct variable-range sound packets to Better Dogs clients, and gracefully falls back to pitched-down vanilla baby wolf whines (`[0.50f, 0.80f]`) for vanilla clients.
  - **Capability Handshake**: Added `BetterDogsClientCapPayload` (`betterdogs:client_audio_cap`) clientbound play payload and receiver for zero-overhead client mod detection via standard channels and local singleplayer addresses.
  - **Synced Entity Data & Mixin Hygiene**: Removed `BETTERDOGS$DATA_SUBDUED` and `BETTERDOGS$DATA_SPECTATOR_WAGGING` `EntityDataAccessor` slots and `WolfRendererMixin`, preserving internal tick logic, personality state, and NBT saves while eliminating entity tracker crashes on vanilla clients.
  - **Translatable Text & Advancement Resilience**: Converted all 20 server-sent player notification components across listeners and helpers to `Component.translatableWithFallback` matching `en_us.json`, preventing untranslated translation key popups on unmodded clients. Added resilient command support footer in `BetterDogsSupportFooter`.
  - **Advancement Fallbacks & Subtitles**: Added `"fallback"` text across all 15 husbandry advancement JSON definitions and added `"subtitles.betterdogs.entity.wolf.howl"` subtitle localization.

## [5.7.14+26.2] - 2026-11-14

### Fixed
- **Null-Safe Blood Feud Persistence & Adoption Codec Safety ([BL-BD-059])**:
  - Updated `WolfInteractionHelper` non-sneak adoption to invoke `BloodFeudHelper.clearBloodFeud(wolf)` instead of passing `null` into `betterdogs$setBloodFeudTarget(null)`.
  - Aligned `WolfAdoptionHelper.tryHandleAdoption` sneak empty-hand adoption to reset `leaderUuid`, clear blood feuds (`BloodFeudHelper.clearBloodFeud(wolf)`), clear `guardMode`/`guardPos`, and call `wolf.setOrderedToSit(false)`.
  - Added compact canonical constructors on `WolfPersistentData`, `BaseData`, and `ExtraData`, null-safe normalization in `WolfMixin` and `WolfPersistentData` blood feud accessors, and thread-safe `WeakHashMap` `HEADLESS_FALLBACK_DATA` fallback for plain JUnit persistence testing.

## [4.24.85+26.2]
### Added
- 🌐 **Italian (`it_it`) Localization & Player Guide (11-Language Suite Complete)**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/it_it.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_it_it.md`.

## [4.24.84+26.2]
### Added
- 🌐 **Korean (`ko_kr`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/ko_kr.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_ko_kr.md`.

## [4.24.83+26.2]
### Added
- 🌐 **French (`fr_fr`, `fr_ca`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/fr_fr.json` and `fr_ca.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_fr_fr.md`.

## [4.24.82+26.2]
### Added
- 🌐 **Japanese (`ja_jp`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/ja_jp.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_ja_jp.md`.

## [4.24.81+26.2]
### Added
- 🌐 **Portuguese (`pt_br`, `pt_pt`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/pt_br.json` and `pt_pt.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_pt_br.md`.

## [4.24.80+26.2]
### Added
- 🌐 **German (`de_de`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/de_de.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_de_de.md`.

## [4.24.79+26.2]
### Added
- 🌐 **Spanish (`es_es`, `es_mx`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/es_es.json` and `es_mx.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_es_es.md`.

## [4.24.78+26.2]
### Added
- 🌐 **Russian (`ru_ru`) Localization & Player Guide**:
  - Added complete `assets/vanilla-outsider-better-dogs/lang/ru_ru.json` matching all 428 translation keys.
  - Added localized player guide in `Doc/Players/index_ru_ru.md`.

## [4.24.77+26.2]
### Added
- 🌐 **Traditional Chinese (zh_tw, zh_hk) Localization & Player Guide**:
  - Added complete ssets/vanilla-outsider-better-dogs/lang/zh_tw.json and zh_hk.json matching all 428 translation keys.
  - Added localized player guide in Doc/Players/index_zh_tw.md.

## [4.24.76+26.2]
### Added
- 🌐 **Simplified Chinese (zh_cn) Localization & Player Guide**:
  - Added complete ssets/vanilla-outsider-better-dogs/lang/zh_cn.json matching all 428 translation keys.
  - Added localized player guide in Doc/Players/index_zh_cn.md.

## [4.24.75+26.2]
### Added
- 🌐 **Indonesian (id_id) Full Parity Localization & Player Guide**:
  - Audited and synchronized ssets/vanilla-outsider-better-dogs/lang/id_id.json for 100% key parity with en_us.json.
  - Added localized Indonesian player guide in Doc/Players/index_id_id.md.

## [4.24.74+26.2] - 2026-08-25
### Changed
- **Milestone Backlog Pruning & Clean State Reset**:
  - Successfully resolved and verified all backlog items (`[BL-BD-001]` through `[BL-BD-007]`).
  - Pruned completed backlog sections adhering to the Backlog Resolution & Pruning Law, resetting the summary table to a clean ready state.

## [4.24.73+26.2] - 2026-08-25
### Added
- **Master Full-Suite Verification & Helper Contract Audit**:
  - Implemented `BetterDogsFullSuiteTest` running exhaustive contract assertions across all 12 modular helper classes (`WolfCliffSafetyHelper`, `WolfHazardHelper`, `WolfStormHelper`, `WolfBeggingHelper`, `WolfFetchHelper`, `WolfGuardHelper`, `WolfInbreedingHelper`, `WolfScaleGeneticsHelper`, `WolfPersonalityStatHelper`, `DogTreatHelper`, `WolfGiftHelper`, `BabyCuriosityHelper`).

## [4.24.72+26.2] - 2026-08-25
### Added
- **Data-Driven Tags & Zero-Allocation Performance Test Suite**:
  - Created `DataDrivenAndPerformanceTest` asserting `FastRandom.INSTANCE` integer distribution, `BetterDogsTags` registration completeness, `BabyCuriosityHelper` fallback resilience, and `DogTreatHelper` pool constancy.

## [4.24.71+26.2] - 2026-08-25
### Changed
- **Zero-Allocation FastRandom & Loop Memory Overhaul**:
  - Replaced static `new Random()` instances in `WolfPersonality`, `WolfBreedingMixin`, and `WolfLitterHelper` with `FastRandom.INSTANCE` and entity `RandomSource`.
  - Replaced transient array allocations in breeding genetics calculations with zero-allocation static pattern switches.

## [4.24.70+26.2] - 2026-08-25
### Changed
- **Data-Driven Datapack Tag Migration (Foliage, Treats, Seats)**:
  - Created `#vanilla-outsider-better-dogs:curiosity_blocks` replacing hardcoded curiosity block checks.
  - Created `#vanilla-outsider-better-dogs:treats` for favorite dog treat customization.
  - Created `#vanilla-outsider-better-dogs:seats` and `#c:chairs` for dog seat candidates.
  - Registered tags in `BetterDogsTags` and integrated with safe in-code fallbacks.

## [4.24.69+26.2] - 2026-08-25
### Added
- **Morning Gift Headless JUnit 5 Test Suite Expansion**:
  - Expanded `MorningGiftTest` covering strict 100% full health requirement, morning time window boundary calculations, peaceful monster proximity rejection, calendar cooldown limits, and personality fallback item roll validity.

## [4.24.68+26.2] - 2026-08-25
### Added
- **Data-Driven Morning Gift Loot Tables (`loot_table/morning_gift/`)**:
  - Implemented datapack loot tables for `aggressive.json` (mob trophies), `pacifist.json` (nature finds), `normal.json` (domestic foraging), and `rare_treasure.json` (gold nuggets, emeralds, name tags, leads).
  - Updated `WolfGiftHelper` to roll loot through server `LootTable` registries with 100% safe in-code fallback.

## [4.24.67+26.2] - 2026-08-25
### Changed
- **Morning Gift Qualification Conditions & Dual Wake-Up Trigger (`WolfGiftHelper`)**:
  - Implemented dual trigger: immediately on waking up from bed, or during early morning (ticks 0–2000).
  - Enforced strict 100% full dog health requirement (`wolf.getHealth() >= wolf.getMaxHealth()`).
  - Enforced 1 gift per calendar day limit (`lastGiftDay < currentDay`).
  - Enforced peaceful surrounding check (zero hostile monsters within 16 blocks).

## [4.24.66+26.2] - 2026-08-25
### Added
- **Wolf Fire Survival AI: Headless JUnit 5 Test Suite Expansion**:
  - Implemented `WolfFireSurvivalTest` covering burning activation qualification, passenger rejection, emergency break-sit and posture restoration cycle, water search 16m proximity math, safe water drop cliff bypass, and fallback panic evasion logic.

## [4.24.65+26.2] - 2026-08-25
### Changed
- **Wolf Fire Survival AI: Safe Water Drop Cliff Bypass (`WolfCliffSafetyHelper`)**:
  - Implemented `isSafeWaterLanding` to detect water bodies below ledges, bridges, and riverbanks.
  - Allowed wolves leaping or pushed towards water to bypass false-positive cliff drop (>3 blocks) freezes and safely extinguish themselves.

## [4.24.64+26.2] - 2026-08-25
### Added
- **Wolf Fire Survival AI: Goal & Extinguishing Logic (`WolfSeekWaterOnFireGoal`)**:
  - Registered priority 1 AI goal activating when wolves catch fire.
  - Implemented 16m nearest water source detection and sprint at `1.4x` movement speed.
  - Implemented fallback erratic panic run (`1.3x` speed) with whimper vocalizations and smoke particles if no water is reachable.
  - Implemented emergency break-sit mechanic to save sitting dogs from burning in place, restoring posture upon extinguishing.
  - Registered GameRules `betterdogs:bd_wolves_seek_water_on_fire` and `betterdogs:bd_wolves_break_sit_on_fire`.

## [4.24.63+26.2] - 2026-08-25
### Changed
- **Clean "1 File, 1 Purpose" Architecture: Command & Listener Modularization**:
  - Extracted `DogEntityCommandListener.java` for entity-targeted riding, commands, and dismounting interactions.
  - Extracted `DogBlockCommandListener.java` for chair/stair seat interaction and invisible interaction entity management.
  - Refactored `DogCommandManager.java` into a thread-safe repository for selection and target state management.

## [4.24.62+26.2] - 2026-08-25
### Changed
- **Clean "1 File, 1 Purpose" Architecture: Tick & Health Modularization**:
  - Extracted single-purpose `WolfHealingHelper.java` for passive out-of-combat regeneration timing.
  - Extracted single-purpose `WolfSoundHelper.java` for ambient vocalizations, health-dependent whining, and sound overrides.
  - Moved `tickGuardMode` to `WolfGuardHelper.java`, `tickNemesisSystem` to `WolfNemesisHelper.java`, and `tickRuntParticles`/`tickAdoptableParticles` to `WolfParticleHelper.java`.
  - Updated `WolfMixin.java` to call specialized single-purpose helpers directly.
  - Purged obsolete `WolfTickHelper.java` class.

## [4.24.61+26.2] - 2026-08-25
### Changed
- **Clean "1 File, 1 Purpose" Architecture: Combat & Targeting Modularization**:
  - Deconstructed composite `WolfCombatHooks` into two single-responsibility handlers:
    - `WolfDamageHandler.java`: Handles `actuallyHurt` logic (accidental/intentional feed demerits, adoption cancellation on hit, play-fight 1-HP safety clamp, friendly fire delegation, and baby retaliation scheduling).
    - `WolfTargetingHandler.java`: Handles `wantsToAttack` target qualification overrides (social actions, 1v1 territorial war leader protection, blood feud nemesis targeting with affinity suppression, submissive pack protection, and baby attack restrictions).
  - Updated `WolfCombatMixin.java` to route directly to `WolfDamageHandler` and `WolfTargetingHandler`.
  - Purged obsolete `WolfCombatHooks.java` class.

## [4.24.60+26.2] - 2026-08-25
### Added
- **Guard Mode Headless Unit Test Suite Expansion (`GuardModePatrolTest.java`)**:
  - Added JUnit 5 assertions for 4-parameter `canToggleGuard` and `toggleGuardMode` verifying Main Hand enforcement, off-hand rejection, backward-compatible overloads, and strict null safety.

## [4.24.59+26.2] - 2026-08-25
### Changed
- **Legacy Bone Deduplication & Guard Sit Handler Fix (`WolfInteractionHelper.java`)**:
  - Purged duplicate legacy bone guard handling block (lines 281–321) that bypassed `WolfGuardHelper`.
  - Removed `!itemStack.is(Items.BONE)` exclusion from the guard dog sitting handler, allowing normal non-sneak right-clicks with bones to cleanly toggle manual sitting/standing at the guard post without consuming items.

## [4.24.58+26.2] - 2026-08-25
### Changed
- **Guard Mode Gating & 6D Interaction Overhaul (`WolfGuardHelper.java`, `WolfInteractMixin.java`)**:
  - Enforced Sneak/Shift requirement (`player.isSecondaryUseActive()`) and `MAIN_HAND` check for Guard Mode bone toggling, ensuring normal non-sneak bone right-clicks safely toggle sitting posture without activating Guard Mode or consuming bones.
  - Implemented dual-direction bone consumption: consuming 1 bone on both Guard Mode activation and deactivation (with Creative mode bypass `!player.getAbilities().instabuild`).
  - Implemented sitting posture preservation: seated dogs maintain their sitting pose upon activating Guard Mode to act as stationary sentries at their assigned guard post.
  - Integrated personality-specific audio pitches (Aggressive: `0.8f`, Normal: `1.2f`, Pacifist: `1.5f` whine sound) and translatable coordinate notifications (`text.betterdogs.guard_activated` / `text.betterdogs.guard_deactivated`).

## [4.24.57+26.2] - 2026-08-22
### Added
- **Hoover / Ground Food Scavenger Quirk (`WolfDispositionHelper.java`, `WolfScavengeHelper.java`, `EatGroundFoodGoal.java`)**:
  - Implemented the Hoover behavioral quirk where dogs eagerly scavenge dropped food items from the ground even when at $100\%$ full health.
  - **Base Personality Rates**: Aggressive ($70\%$), Normal ($35\%$), Pacifist ($10\%$) with $[-100\%, +100\%]$ UUID variance offset.
  - **Digestion Cooldown**: Enforced a $160\text{ ticks}$ ($8\text{ seconds}$) cooldown between ground food snacks at full health to prevent vacuuming item stacks.
  - **Posture Safety**: Fully respects sitting commands (`isOrderedToSit()`); seated dogs will never break posture.

## [4.24.56+26.2] - 2026-08-22
### Changed
- **Full-Spectrum [-100%, +100%] UUID Behavioral Variance (`WolfDispositionHelper.java`)**:
  - Expanded individual dog UUID offset range to $[-100\%, +100\%]$ (clamped to $[0\%, 100\%]$).
  - Applied base personality values:
    - **Fetch Reluctance**: Aggressive ($10\%$), Normal ($30\%$), Pacifist ($60\%$).
    - **Storm Fearlessness**: Aggressive ($80\%$), Normal ($40\%$), Pacifist ($10\%$).
    - **Quiet Howling**: Aggressive ($10\%$), Normal ($25\%$), Pacifist ($60\%$).

## [4.24.55+26.2] - 2026-08-22
### Changed
- **Additive Modifier Behavioral Quirk Model (`WolfDispositionHelper.java`)**:
  - Implemented additive modifier variance model with wide personality spread:
    - **Fetch Reluctance**: Aggressive ($0\text{--}5\%$), Normal ($3\text{--}18\%$), Pacifist ($15\text{--}45\%$).
    - **Storm Fearlessness**: Aggressive ($90\text{--}100\%$), Normal ($10\text{--}35\%$), Pacifist ($0\text{--}15\%$).
    - **Quiet Howling**: Aggressive ($0\text{--}10\%$), Normal ($5\text{--}30\%$), Pacifist ($25\text{--}65\%$).
  - Combined personality baseline percentages with deterministic UUID offsets ($\pm 10\%$ to $\pm 20\%$).

## [4.24.54+26.2] - 2026-08-22
### Added
- **Personality + UUID Seeded Behavioral Variance (`WolfDispositionHelper.java`)**:
  - Deterministically seeded individual behavioral nuances using 64-bit bit-mixing hashing over `UUID` + `WolfPersonality` + behavior salt (zero NBT storage).
  - ~5% of dogs are organically reluctant to fetch sticks; non-fetchers either curiously tilt their heads (`setIsInterested(true)`) or naturally ignore nearby thrown sticks.
  - ~10% of dogs are naturally fearless during thunderstorms, bypassing storm panic and whimpering.
  - ~15% of dogs are quiet observers who refrain from participating in nocturnal group howling choruses.

## [4.24.53+26.2] - 2026-08-22
### Changed
- **Subtle Particle Feedback for Fetch, Gifts & Treat Feeding (`WolfFetchHelper`, `WolfGiftHelper`, `DogTreatHelper`, `WolfParticleHelper`)**:
  - Integrated `WolfParticleHelper.spawnSubtleParticles` to scale subtle cues with `bd_particle_density` GameRule (`NONE`: 0, `LOW`: 1, `MEDIUM`/`HIGH`: 2).
  - Reduced stick fetch and morning gift particles to 2 subtle emerald glints (`0.15` spread, `0.02` speed).
  - Softened favorite treat feeding visual feedback to 2 happy villager + 1 heart particle.

## [4.24.37+26.2] - 2026-08-19
### Fixed
- **Jade Provider Separation ("1 File, 1 Purpose")**:
  - Separated client tooltip component providers (`InbredStatusProvider`, `WolfInfoProvider`) from server data sync providers (`InbredStatusDataProvider`, `WolfInfoDataProvider`).
  - Restored 100% full compatibility across newer Jade releases (`Jade >= 26.2.11+`), older Jade releases, and standalone Fabric.

## [4.24.36+26.2] - 2026-08-19 - ⚠️ Incompatible with Jade >= 26.2.11+ (Works on Jade < 26.2.10 or without Jade)

## [4.24.8+26.2] - 2026-08-16
### Fixed
- **Minecart & Vehicle Dismount Interaction Interception (`DogCommandManager.java`, `WolfInteractionHelper.java`)**:
  - Fixed raycast hitbox interception when clicking directly on Minecarts, Boats, or Seats containing owned dogs with a command item or empty hand.
  - Added safe lateral position offset (0.8 blocks) on dismount to prevent vanilla `AbstractMinecart` collision logic from immediately re-boarding the wolf.
  - Cleared vehicle targets and selections on dismount with audio and particle feedback.

## [4.24.7+26.2] - 2026-08-11
### Refactored & Polished
- **Minimal Tame Particle Polish (`WolfParticleHandler`)**: Refactored first-time tame particles into a clean ~5-6 particle Minimal Signature per personality (Aggressive: crimson dust + angry villager; Normal: golden dust + happy villager; Pacifist: mint dust + heart).

## [4.24.6+26.2] - 2026-08-10
### Fixed
- **YACL Config Option Descriptions (`YaclScreenHelper` & Translation Sheets)**: Added missing `.description(...)` OptionDescription builders in `YaclScreenHelper` across all 6 config categories and synchronized complete `.description` keys in `en_us.json` and `id_id.json`.

## [4.24.4+26.2] - 2026-08-10
### Fixed
- **Dog Adoption Crash Fix (`WolfInteractionHelper` & `WolfStatManager`)**: Fixed `NullPointerException` crash during adoption by adding personality null guards in `WolfStatManager` and pre-assigning random personalities for uninitialized dogs, alongside safe `getOwnerUUID()` resolution.

## [4.24.3+26.2] - 2026-08-10
### Fixed & Added
- **Litematica Compatibility & Command Item Expansion (`DogCommandManager`)**:
  - **Litematica Tool Conflict Fix**: Resolved stick tool interaction collision with Litematica by introducing `#vanilla-outsider-better-dogs:command_items` item tag (Stick, Blaze Rod, Breeze Rod, `#c:tools/sticks`).
  - **Alternative Command Tools**: Enables Blaze Rods and Breeze Rods as alternative command tools alongside standard sticks for dismounting and seat commanding.
  - **Bone Reservation Safeguard**: Explicitly excluded `minecraft:bone` from command items so Bone remains 100% reserved for Guard Mode (`bd_guard_mode`).

## [4.24.2+26.2] - 2026-08-10
### Added
- **Dynamic Climate-Aware Wolf Coat Variants (`WolfVariantHelper`)**: Automatically assigns coat variants based on biome physical climate properties when Vanilla tag lookup defaults to Pale.

## [4.24.1+26.2] - 2026-07-30
### ⚠️ Incompatibility Warning
- **Minecraft 26.2 Target Only**: `4.24.1+26.2` is strictly compiled for Minecraft 26.2. Running on Minecraft 26.3 snapshot instances will crash on startup due to upstream Minecraft 26.3 API refactors (`EntityPredicate.ADVANCEMENT_CODEC` removal and `DynamicGameRuleManager` breaking changes). Upgrade to `5.0.0+26.3` for Minecraft 26.3+.

### Fixed
- **Startup Crash / GameRule Registration Fix**:
  - Refactored `BetterDogsGameRules` registration methods to delegate through `DynamicGameRuleManager` from `dasik-library`, resolving duplicate key registry exceptions on startup.

## [4.24.0+26.2] - 2026-07-30
### ⚠️ Incompatibility & Startup Crash Warning
- **Minecraft 26.3 Startup Crash & Dynamic GameRule Crash**: `4.24.0+26.2` crashes on Fabric 26.3 snapshot instances (`NoSuchFieldError: EntityPredicate.ADVANCEMENT_CODEC`) and can crash on MC 26.2 startup if duplicate GameRule keys are registered. Upgrade to `4.24.1+26.2` for Minecraft 26.2 or `5.0.0+26.3` for Minecraft 26.3+.

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

## [4.15.18+26.2] - 2026-07-21
### Added
- **Farmer's Delight Favorite Treats Integration**:
  - **Problem**: Favorite Treats only supported 9 vanilla Minecraft food items. Players using popular food mods like Farmer's Delight Refabricated could not use custom modded meats or dog food as Favorite Treats.
  - **Solution**: Dynamically resolve Farmer's Delight items (`farmersdelight:dog_food`, `minced_beef`, `mutton_chops`, `cooked_mutton_chops`, `bacon`, `cooked_bacon`, `chicken_cuts`, `cooked_chicken_cuts`, `ham`, `smoked_ham`, `beef_stew`, `chicken_soup`, `vegetable_soup`, `fish_stew`) at runtime when the `farmersdelight` mod is installed.
  - **Crash-Safe Vanilla Fallback**: Uses runtime `BuiltInRegistries.ITEM` queries. If Farmer's Delight is absent, the treat pool defaults seamlessly to the standard 9 vanilla items without throwing errors on vanilla clients or servers.

## [4.15.17+26.2] - 2026-07-12
### Added
- **Dynamic Flanking Distance Scaling**:
  - **Problem**: Flanking offsets were previously hardcoded to 4.5 blocks (side) and 2.0 blocks (rear). While this worked for standard humanoid mobs, it was ineffective for larger targets (such as Ravagers, Ghasts, or Wardens) causing dogs to run inside the collision box, or smaller targets where they spread out too far.
  - **Solution**: Scaled flanking radius to `Math.max(3.0, targetWidth * 2.5)` and rear shift to `Math.max(1.0, targetWidth * 1.1)`. This scales flanking sweeps dynamically based on the targeted mob's bounding box size.
- **Flanking Path Raycast Verification**:
  - **Problem**: Dogs had no safety verification before committing to flanking positions, causing them to run into solid walls, clip into dead ends, or get stuck in deep water.
  - **Solution**: Added a namespaced GameRule `betterdogs:bd_flanking_raycast_check` that defaults to `true`. When enabled, flanking dogs raycast using `ClipContext` from their eye level to the destination. If the path is blocked by solid obstacles or water, the dog attempts to flank via the opposite side. If both sides are blocked, it falls back to standard direct charge pathing.

## [4.15.16+26.2] - 2026-07-12
### Added
- **Cozy Storm Shelter**:
  - **Problem**: During thunder storms, storm-anxious dogs would pacify in random locations using a basic pacing goal. They did not actively seek shelter blocks to escape the lightning and rain, nor did they try to stay close to their owner.
  - **Solution**: Updated `WolfStormAnxietyGoal.java` to scan a 12x4x12 area for covered blocks where the sky is not visible. Prioritizes the owner's location if the owner is within 32 blocks. If the dog is already sheltered near the owner, it stays put. Otherwise, it paths to the shelter block.
- **Comfort Soothing**:
  - **Problem**: There was no way for players to calm down their anxious, whimpering dogs during a storm, forcing them to listen to whimpers until the storm ended.
  - **Solution**: Added soothing capability via sneaking right-click petting (with an empty hand) or feeding the dog its Favorite Treat. Comforting sets a transient `betterdogs$soothedTime` field, disabling the anxiety pacing and whimpering goals for 10 minutes (12000 ticks) and playing heart/note particles alongside a low-pitched whimper comfort sound.

## [4.15.15+26.2] - 2026-07-12
### Added
- **Smart Creeper Blast Evasion**:
  - **Problem**: Previously, tamed dogs fled creepers within a short `6.0`-block range at standard speeds, which was too late to escape active explosions. Also, the behavior failed to respect the `bd_creeper_awareness` GameRule.
  - **Solution**: Increased range to `10.0` blocks, adjusted walking/sprinting speeds to `1.2`/`1.6`, added a GameRule check inside `canUse()`, and spawned visual panic smoke trails at the feet of fleeing dogs in `tick()`.

## [4.15.14+26.2] - 2026-07-12
### Added
- **Configurable Wolf Size Range limits**:
  - **Problem**: Wolves had their visual scale calculated using a fixed formula based on health and UUID random offset, with no upper/lower boundary settings. Players could end up with extremely large or small wolves that clipped through blocks or became hard to see, and server admins could not enforce size limits.
  - **Solution**: Added global configuration fields `wolfMinScale` and `wolfMaxScale` to `BetterDogsConfig`. Added namespaced GameRules `betterdogs:bd_wolf_min_scale_percent` and `betterdogs:bd_wolf_max_scale_percent` which default to the configuration values. Added dynamic clamping of the final visual scale in `WolfStatManager.java` using these GameRules.
  - **YACL UI Integration**: Integrated sliders for minimum and maximum size configuration in the Breeding & Genetics settings screen, including notice descriptions about per-world isolation.
  - **API Hardening**: Upgraded dependency constraint to `dasik-library >=1.8.3` to consume the new dynamic genetics limit APIs.

## [4.15.13-26.2] - 2026-07-11
### Added
- **YACL Option Descriptions Tooltips**:
  - **Problem**: Option descriptions in the YACL screen were completely blank when hovering, making it hard for players to understand settings.
  - **Solution**: Injected `.description(...)` calls in `YaclScreenHelper.java`. Added translation keys for config-only options to `en_us.json`. Created a dynamic mapping helper `getDescription` that loads GameRule and config translation keys and automatically appends the gold warning notice for GameRule-default configuration settings.

## [4.15.12-26.2] - 2026-07-11
### Fixed
- **ModMenu Config Screen Integration**:
  - **Problem**: ModMenu did not display the gear button and crashed due to an invalid YACL mod ID check.
  - **Solution**: Changed YACL mod ID string from `"yet-another-config-lib"` to `"yet_another_config_lib_v3"` in `ModMenuIntegration.java`.

## [4.15.11-26.2] - 2026-07-07
### Changed
- **Migrated Config GUI to YetAnotherConfigLib (YACL)**: Replaced optional Cloth Config integration with YACL (3.9.5+26.2-fabric):
  - **Problem**: Cloth Config is legacy and less stable on newer environments, and the project policy requires using YACL for consistent and modernized UI controls.
  - **Solution**: Removed Cloth Config dependencies and class files. Added `yet-another-config-lib` to suggests block in `fabric.mod.json`, declared it `compileOnly` in `build.gradle`, and registered the repository. Created `YaclScreenHelper` defining all categories (General, Personalities, Breeding, Territoriality, Gifts, Performance) with YACL-specific sliders, field inputs, tick boxes, and dropdown elements.
  - **Environment Safety**: Utilized a standalone reflection check for `"yet-another-config-lib"` inside `ModMenuIntegration` to defer loading YACL classes. This ensures the mod remains 100% crash-safe on server environments or client installations that do not have YACL.

## [4.15.10-26.2] - 2026-07-05
### Changed
- **Increased Tamed Pack Spread Multiplier**: Raised the default `tamedPackSpreadMultiplier` from `120` (1.2x) to `280` (2.8x):
  - **Problem**: With the previous `1.2x` multiplier, a player would need over 150 wolves following to reach the new `15.0` block spacing max cap. This was far too high for standard gameplay where pack sizes are typically 5–30 wolves.
  - **Solution**: Adjusted the default multiplier to `280` (2.8x). This mathematically scales follow distances such that a standard large pack of 30 following wolves ($\sqrt{30-1} \times 2.8 \approx 15.07$ blocks) naturally hits the `15.0` blocks spacing limit. Smaller packs (e.g. 5 wolves) now spread out up to 5.6 blocks, creating distinct coordination in normal play.

## [4.15.9-26.2] - 2026-07-05
### Changed
- **Increased Tamed Pack Spread Max**: Raised the default `tamedPackSpreadMax` from `60` (6.0 blocks) to `150` (15.0 blocks):
  - **Problem**: The previous 6.0-block hard cap was too restrictive for players with larger tamed wolf packs (10+ wolves). Wolves would cluster tightly once the cap was hit, reducing the visual impact of pack spreading and causing pathfinding congestion.
  - **Solution**: Increased the decile value to `150` in both `BetterDogsConfig.java` and the JSON config default. Updated GameRule and Cloth Config descriptions in `en_us.json` and `id_id.json` to reflect the new default. The formula ($\text{offset} = \min(\sqrt{N-1} \times \text{multiplier}, \text{maxExtra})$) remains unchanged — only the ceiling is raised.

---

## 📚 Historical Archives

Archived chronologically to stay within the 46,080-byte ceiling:

- [`History_2026_May30_Jul01_Archive.md`](History_2026_May30_Jul01_Archive.md) — `4.15.8-26.2` .. `4.6.8+A-26.2` (`73` releases)
- [`History_2026_May16_May30_Archive.md`](History_2026_May16_May30_Archive.md) — `4.6.7+A-26.2` .. `3.4.8+R-26.1.2` (`65` releases)
- [`History_2026_Jan_May16_Archive.md`](History_2026_Jan_May16_Archive.md) — `3.4.7` .. `v1.8.0-26.1` (`50` releases)
