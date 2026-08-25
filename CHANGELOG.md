# Changelog

## [4.24.60+26.2] - 2026-08-25
### Added
- 🧪 **Guard Mode Headless Unit Test Suite Expansion (`GuardModePatrolTest.java`)**:
  - Added JUnit 5 assertions for 4-parameter `canToggleGuard` and `toggleGuardMode` verifying Main Hand enforcement, off-hand rejection, backward-compatible overloads, and strict null safety.

## [4.24.59+26.2] - 2026-08-25
### Changed
- 🧹 **Legacy Bone Deduplication & Guard Sit Handler Fix (`WolfInteractionHelper.java`)**:
  - Purged duplicate legacy bone guard handling block (lines 281–321) that bypassed `WolfGuardHelper`.
  - Removed `!itemStack.is(Items.BONE)` exclusion from the guard dog sitting handler, allowing normal non-sneak right-clicks with bones to cleanly toggle manual sitting/standing at the guard post without consuming items.

## [4.24.58+26.2] - 2026-08-25
### Changed
- 🛡️ **Guard Mode Gating & 6D Interaction Overhaul (`WolfGuardHelper.java`, `WolfInteractMixin.java`)**:
  - Enforced Sneak/Shift requirement (`player.isSecondaryUseActive()`) and `MAIN_HAND` check for Guard Mode bone toggling, ensuring normal non-sneak bone right-clicks safely toggle sitting posture without activating Guard Mode or consuming bones.
  - Implemented dual-direction bone consumption: consuming 1 bone on both Guard Mode activation and deactivation (with Creative mode bypass `!player.getAbilities().instabuild`).
  - Implemented sitting posture preservation: seated dogs maintain their sitting pose upon activating Guard Mode to act as stationary sentries at their assigned guard post.
  - Integrated personality-specific audio pitches (Aggressive: `0.8f`, Normal: `1.2f`, Pacifist: `1.5f` whine sound) and translatable coordinate notifications (`text.betterdogs.guard_activated` / `text.betterdogs.guard_deactivated`).

## [4.24.57+26.2] - 2026-08-22
### Added
- 🍖 **Hoover / Ground Food Scavenger Quirk (`WolfDispositionHelper.java`, `WolfScavengeHelper.java`, `EatGroundFoodGoal.java`)**:
  - Implemented the Hoover behavioral quirk where dogs eagerly scavenge dropped food items from the ground even when at $100\%$ full health.
  - **Base Personality Rates**: Aggressive ($70\%$), Normal ($35\%$), Pacifist ($10\%$) with $[-100\%, +100\%]$ UUID variance offset.
  - **Digestion Cooldown**: Enforced a $160\text{ ticks}$ ($8\text{ seconds}$) cooldown between ground food snacks at full health to prevent vacuuming item stacks.
  - **Posture Safety**: Fully respects sitting commands (`isOrderedToSit()`); seated dogs will never break posture.

## [4.24.56+26.2] - 2026-08-22
### Changed
- 🧬 **Full-Spectrum [-100%, +100%] UUID Behavioral Variance (`WolfDispositionHelper.java`)**:
  - Expanded individual dog UUID offset range to $[-100\%, +100\%]$ (clamped to $[0\%, 100\%]$).
  - Applied base personality values:
    - **Fetch Reluctance**: Aggressive ($10\%$), Normal ($30\%$), Pacifist ($60\%$).
    - **Storm Fearlessness**: Aggressive ($80\%$), Normal ($40\%$), Pacifist ($10\%$).
    - **Quiet Howling**: Aggressive ($10\%$), Normal ($25\%$), Pacifist ($60\%$).

## [4.24.55+26.2] - 2026-08-22
### Changed
- 🧬 **Additive Modifier Behavioral Quirk Model (`WolfDispositionHelper.java`)**:
  - Implemented additive modifier variance model with wide personality spread:
    - **Fetch Reluctance**: Aggressive ($0\text{--}5\%$), Normal ($3\text{--}18\%$), Pacifist ($15\text{--}45\%$).
    - **Storm Fearlessness**: Aggressive ($90\text{--}100\%$), Normal ($10\text{--}35\%$), Pacifist ($0\text{--}15\%$).
    - **Quiet Howling**: Aggressive ($0\text{--}10\%$), Normal ($5\text{--}30\%$), Pacifist ($25\text{--}65\%$).
  - Combined personality baseline percentages with deterministic UUID offsets ($\pm 10\%$ to $\pm 20\%$).

## [4.24.54+26.2] - 2026-08-22
### Added
- 🧬 **Personality + UUID Seeded Behavioral Variance (`WolfDispositionHelper.java`)**:
  - Deterministically seeded individual behavioral nuances using 64-bit bit-mixing hashing over `UUID` + `WolfPersonality` + behavior salt (zero NBT storage).
  - ~5% of dogs are organically reluctant to fetch sticks; non-fetchers either curiously tilt their heads (`setIsInterested(true)`) or naturally ignore nearby thrown sticks.
  - ~10% of dogs are naturally fearless during thunderstorms, bypassing storm panic and whimpering.
  - ~15% of dogs are quiet observers who refrain from participating in nocturnal group howling choruses.

## [4.24.53+26.2] - 2026-08-22
### Changed
- ✨ **Subtle Particle Feedback for Fetch, Gifts & Treat Feeding (`WolfFetchHelper`, `WolfGiftHelper`, `DogTreatHelper`, `WolfParticleHelper`)**:
  - Integrated `WolfParticleHelper.spawnSubtleParticles` to scale subtle interaction cues with `bd_particle_density` GameRule (`NONE`: 0, `LOW`: 1, `MEDIUM`/`HIGH`: 2).
  - Reduced stick/item fetch return and scavenged morning gift delivery particles to 2 subtle emerald glints (`0.15` spread, `0.02` speed).
  - Softened favorite treat feeding visual feedback from 10 happy villager + 5 heart particles to subtle 2 happy villager + 1 heart particle.


## [4.24.52+26.2] - 2026-08-19
### Fixed & Hardened
- **Multi-Version Manifest & Version Guard Parity Sync**:
  - Synchronized SemVer and multi-version integrity checks in lockstep with 26.1 and 26.3.
  - Added **Manifest & Version Guard Integrity Law** to Core Constitution, Skills, and Developer Memory.


## [4.24.51+26.2] - 2026-08-19
### Fixed & Hardened
- **Jade Tooltip Compatibility & NullPointerException Fix (`WolfInfoProvider`, `BetterDogsJadePlugin`, `InbredStatusProvider`)**:
  - Registered `hide_undiscovered_treat` config toggle in `BetterDogsJadePlugin` using `registration.addConfig(WolfInfoProvider.HIDE_UNDISCOVERED_TREAT, true)`.
  - Resolved `java.lang.NullPointerException` in `WolfInfoProvider.appendTooltip` by adding safe defensive try-catch error guards around Jade config queries with fallback defaults.
  - Refactored `WolfInfoProvider` to directly delegate to single-purpose `DogTreatHelper.getFavoriteTreat(wolf)`.
  - Hardened `InbredStatusProvider` with null guards and exception handling to guarantee tooltips never crash or break in Jade HUDs.


## [4.24.50+26.2] - 2026-08-19
### Added & Refactored
- **Expanded Spawning, Dynamic Climate Coats & Alpha Leadership (`BetterDogsSpawning`, `WolfVariantHelper`, `SpawnVariantTest`)**:
  - Implemented single-purpose `BetterDogsSpawning` registering Fabric `BiomeModifications` for expanded biomes (Plains, Savanna, Savanna Plateau, Windswept Savanna, Badlands, Eroded Badlands, Wooded Badlands, Meadow) with cluster size 4 to 8 and spawn weight 8.
  - Enhanced `WolfVariantHelper` with 10% stray diversity rolls, 3-tier climate physics matrix (Snowy, Ashen, Rusty, Striped, Black, Chestnut, Spotted, Pale/Woods), and third-party/custom variant safeguards.
  - Integrated spawn registrations into `BetterDogs.init()`.
  - Added automated test suite `SpawnVariantTest` asserting cluster size bounds (4–8, weight 8), personality distribution percentages (60% normal, 20% aggro, 20% pacifist = 100%), alpha dominance hierarchy logic, and strict null safety.


## [4.24.49+26.2] - 2026-08-19
### Added & Refactored
- **Wanderlust Exploratory Roaming AI (`WanderlustGoal`, `WanderlustHelper`, `WanderlustTest`)**:
  - Implemented single-purpose `WanderlustHelper` encapsulating eligibility validation (tamed, non-sitting, non-leashed, non-combat, non-guarding, owner within 32 blocks), 1-in-400 surge probability rolls, and 28m perimeter exploratory pathing calculations with >24m steering pull.
  - Refactored `WanderlustGoal` to coordinate autonomous exploratory roaming surges delegating to `WanderlustHelper`.
  - Added automated test suite `WanderlustTest` asserting boundary distance constants (28m roam, 24m return, 32m max), eligibility and sit/leash safety checks, 1-in-400 surge probability roll math, and strict null safety across helper methods.


## [4.24.48+26.2] - 2026-08-19
### Added & Refactored
- **Wild Wolf Pack War & Territorial Rivalry Matrix (`WildWolfTerritorialGoal`, `WolfTerritorialRivalryHelper`, `PackWarMatrixTest`)**:
  - Implemented single-purpose `WolfTerritorialRivalryHelper` encapsulating dominance score arithmetic (scale, personality weighting, health ratio), 6-pair matrix GameRule evaluation (`evaluateOutcome`), and pack merger execution (`mergePacks`) with `GroupMember` leader updates and `HAPPY_VILLAGER` particles.
  - Refactored `WildWolfTerritorialGoal` to coordinate standoff behavior machines and conflict resolution through `WolfTerritorialRivalryHelper`.
  - Added automated test suite `PackWarMatrixTest` asserting territorial constants (96-block radius, 1200-tick duration), RivalryOutcome enum states, matrix probability roll math (AA 80% war), and strict null safety across helper methods.


## [4.24.47+26.2] - 2026-08-19
### Added & Refactored
- **Harmless Social Play Sparring AI (`SmallFightGoal`, `SmallFightHelper`, `SmallFightTest`)**:
  - Implemented single-purpose `SmallFightHelper` encapsulating packmate eligibility checks, 6-block partner searches, 6-second sparring sessions (`SPARRING_DURATION_TICKS = 120`), `HAPPY_VILLAGER` particles, playful growl/pant audio cues, +1 bilateral affinity progression, and safe session termination.
  - Refactored `SmallFightGoal` to coordinate non-damaging mock tussles and jump pounces between co-owned packmates, disengaging immediately upon sitting commands, leashing, or combat engagement.
  - Added automated test suite `SmallFightTest` asserting sparring duration and speed constants, partner ownership validation (same owner UUID), combat/sit posture safety checks, and strict null safety across helper methods.


## [4.24.46+26.2] - 2026-08-19
### Added & Refactored
- **Persistent Wolf Vendetta Blood Feuds (`BloodFeudGoal`, `BloodFeudHelper`, `BloodFeudTest`)**:
  - Implemented single-purpose `BloodFeudHelper` encapsulating UUID string parsing, 20-block vicinity entity sweeps, 5% escalation rolls via `betterdogs:bd_blood_feud_percent`, and grudge state management.
  - Refactored `BloodFeudGoal` to duel rival wolves until one perishes, strictly respecting player sit commands (`wolf.isOrderedToSit()`) without accidental posture overrides, and clearing vendetta state upon rival defeat.
  - Added automated test suite `BloodFeudTest` asserting Codec-based UUID serialization and deserialization, 5% escalation roll probability math, empty/unset feud validation, and strict null safety across helper methods.


## [4.24.45+26.2] - 2026-08-19
### Added & Refactored
- **Desperate Low-Health Wild Wolf Hunting (`HuntWhenHurtGoal`, `WildHuntHelper`, `WildHuntingTest`, `BetterDogsGameRules`)**:
  - Registered `betterdogs:bd_wild_hunt_health_threshold` GameRule (default: 50%, bounds: 0..100) with English and Indonesian translations, and added to Brigadier command suggestions.
  - Implemented single-purpose `WildHuntHelper` encapsulating prey classification (`Sheep`, `Rabbit`, `Chicken`, `Fox`), health threshold evaluation (<50%), stop threshold check (>=80%), and pure Java +4.0 HP (2 hearts) sustenance healing upon defeat.
  - Refactored `HuntWhenHurtGoal` and registered it in `WolfAIMixin` target selector (priority 3) for selective low-health sustenance hunting.
  - Added automated test suite `WildHuntingTest` asserting health threshold calculations (<50%), sustenance heal arithmetic (+4.0 HP), stop thresholds (>=80%), and strict null safety across helper methods.


## [4.24.44+26.2] - 2026-08-19
### Added & Refactored
- **Feisty Puppy Retaliation AI (`BabyBiteBackGoal`, `BabyRetaliationHelper`, `BabyRetaliationTest`)**:
  - Implemented single-purpose `BabyRetaliationHelper` encapsulating eligibility checks, probability rolls governed by `betterdogs:bd_baby_retaliate_percent` (default: 50%), and audio cues (`SoundEvents.WOLF_GROWL_BABY`).
  - Refactored `BabyBiteBackGoal` to execute single-purpose snap retaliation: Aggressive puppies deliver a 1.0 HP nip when provoked or disciplined, subsequently prompting domestic discipline from nearby co-owned adult wolves via `CorrectionDogEvent`.
  - Added automated test suite `BabyRetaliationTest` asserting personality gating (Aggressive only), probability rolls (50%), timer decay (100 ticks), and strict null safety across helper methods.


## [4.24.43+26.2] - 2026-08-19
### Added & Refactored
- **Visual Advancements & Milestones (`WolfAdvancementHelper`, `AdvancementCriteriaTest`)**:
  - Implemented single-purpose `WolfAdvancementHelper` safely resolving and awarding canine husbandry advancement criteria across `minecraft:husbandry/` and `betterdogs:husbandry/` with server-side validation.
  - Validated all 13 canine husbandry advancement JSON files on the classpath: `a_pack_of_guardians`, `a_pack_of_personalities`, `cure_runt`, `inbred_runt`, `litter_two`, `litter_three`, `litter_four`, `litter_legend`, `on_guard`, `on_patrol`, `outcross_runt`, `put_up_for_adoption`, and `self_service`.
  - Added automated test suite `AdvancementCriteriaTest` asserting 100% classpath existence of advancement JSON configurations and strict null safety across helper methods.


## [4.24.42+26.2] - 2026-08-19
### Added & Refactored
- **In-Game Brigadier Command Suite (`BetterDogsCommand`, `CommandSuggestionsHelper`, `CommandSuiteTest`)**:
  - Registered full `/betterdogs` and `/bd` Brigadier command suite with subcommands: `help`, `status`, `get <rule>`, `set <rule> <val>`, `reset`, `reload`, and `debug`.
  - Implemented single-purpose `CommandSuggestionsHelper` providing GameRule name normalization (handling namespace prefixes and aliases) and tab-completion for all 50+ GameRules.
  - Implemented 2-tier permission gating: Level 0 for player queries (`help`, `status`, `get`), Level 2 (`LEVEL_GAMEMASTERS`) for admin mutators (`set`, `reset`, `reload`, `debug`).
  - Added automated test suite `CommandSuiteTest` asserting registration of all command nodes, GameRule classification, normalization logic, and 50+ rule coverage.


## [4.24.41+26.2] - 2026-08-19
### Added & Refactored
- **Multi-Language Localized Subtitle Audio & Actionbar Feedback (`WolfFeedbackHelper`, `BetterDogsGameRules`, Translations)**:
  - Registered `betterdogs:bd_actionbar_feedback` GameRule (default: `false` for organic vanilla-like immersion).
  - Encapsulated actionbar overlay messaging and client dispatch gating into single-purpose `WolfFeedbackHelper`.
  - Enforced 100% complete translation parity across English (`en_us.json`) and Indonesian (`id_id.json`) for all GameRules, commands, status overlays, death logs, and audio subtitles.
  - Added automated test suite `SubtitleFeedbackTest` asserting default `false` GameRule configuration and strict null safety across helper methods.


## [4.24.40+26.2] - 2026-08-19
### Added & Refactored
- **Adult Puppy Discipline & Target Silencing AI (`AdultCorrectionGoal`, `AdultDisciplineHelper`, `HurtByTargetGoalMixin`)**:
  - Encapsulated adult-to-baby discipline eligibility, domestic alert silencing checks ("The Muzzle"), affinity-scaled blood feud risk math, and sensory feedback into single-purpose `AdultDisciplineHelper`.
  - Implemented domestic dispute alert silencing in `HurtByTargetGoalMixin`: intercepts and cancels `alertOthers()` when a puppy is nipped by a co-owned adult wolf, completely preventing civil war in the pack.
  - Implemented adult correction AI: adult wolves approach misbehaving puppies, deliver a warning nip, play an adult warning growl, and prompt a puppy submissive whine with angry villager particles.
  - Added automated test suite `AdultDisciplineTest` asserting affinity modulation on feud chance (halved at max affinity, increased on negative affinity), domestic alert silencing rules, and strict null safety.


## [4.24.39+26.2] - 2026-08-19
### Added & Refactored
- **Puppy Playful Exploration AI (`BabyCuriosityGoal`, `BabyCuriosityHelper`)**:
  - Encapsulated puppy curiosity eligibility, harmless passive mob filtering (animals, players, packmates), nature foliage recognition (flowers, tall grass, crops, leaves, pumpkins, melons), and feedback cues into single-purpose `BabyCuriosityHelper`.
  - Implemented personality-scaled curiosity frequency: Pacifist puppies explore frequently (every 2–3 seconds), Normal puppies explore periodically (every 4–6 seconds), and Aggressive puppies remain focused on combat discipline (disinterested).
  - Puppies approach interesting targets at $0.8\times$ speed, gaze/sniff for 2–6 seconds, emit subtle happy villager particles via `WolfParticleHelper`, and play soft baby ambient audio.
  - Added automated test suite `BabyCuriosityTest` asserting personality trigger intervals, distance bounds ($100.0\text{m}^2$, $6.25\text{m}^2$), foliage block validation, and strict null safety.


## [4.24.38+26.2] - 2026-08-19
### Added & Refactored
- **Vehicle Navigation & Auto-Boarding System (`MoveToVehicleGoal`, `DogSeatHelper`, `DogCommandManager`)**:
  - Encapsulated vehicle/seat detection, passenger vacancy validation, command item checks, and outward lateral dismount vector math into single-purpose `DogSeatHelper`.
  - Implemented stick/rod command selection and auto-boarding AI: dogs navigate to commanded Boats, Minecarts, Saddled Mounts/Camels, modded chairs, and Stair blocks within 12 blocks (`144.0D` distance squared) at $1.25\times$ speed and board when within 1.5 blocks.
  - Implemented safe lateral dismount pushing the dog 0.8 blocks outward along the player's horizontal look vector, preventing collision clipping or immediate re-boarding loops.
  - Added automated test suite `VehicleBoardingTest` asserting distance constants, lateral dismount vector calculations, command selection tracking, and strict null safety.


## [4.24.37+26.2] - 2026-08-19
### Added & Refactored
- **Morning Gift Bringing & Feeding Merits System (`WolfGiftGoal`, `WolfGiftHelper`, `WolfPersistentData`)**:
  - Implemented morning approach AI where healthy, monster-free dogs approach waking owners at $1.25\times$ speed to present scavenged gifts.
  - Enforced 10-meal feeding merit threshold (`bd_gift_feed_threshold`), consuming 10 merits upon delivery with strict 1-day cooldown tracking.
  - Implemented personality-themed scavenged gift loot pools (Aggressive: combat drops/bones/eyes; Pacifist: berries/apples/flowers; Normal: utility/sticks/feathers) plus 5% rare treasures (gold nuggets, emeralds, name tags, bone meal).
  - Emits happy villager celebration particles via `WolfParticleHelper` and ambient audio cues upon gift delivery.
  - Added automated test suite `MorningGiftTest` asserting threshold gating, merit deduction, daily cooldown math, health eligibility, and null safety.

## [4.24.36+26.2] - 2026-08-19 - ⚠️ Incompatible with Jade >= 26.2.11+ (Works on Jade < 26.2.10 or without Jade)
### Added & Refactored
- **Friendly Fire Dampening & Protection (`WolfFriendlyFireHelper`, `WolfCombatHooks`)**:
  - Implemented standing owner melee attack cancellation against owned dogs when `bd_friendly_fire_protection` is enabled, eliminating accidental friendly hits during combat.
  - Allowed crouching owner melee attacks to bypass protection for intentional discipline and bonding demerits.
  - Preserved owner projectile damage and enforced lethal friendly fire health clamp at 1.0 HP.
  - Decoupled friendly fire logic from monolithic `WolfCombatHooks` into single-purpose `WolfFriendlyFireHelper`.
  - Added automated test suite `FriendlyFireTest` asserting standing melee cancellation, crouching bypass, projectile damage, lethal clamping, and null safety.

## [4.24.35+26.2] - 2026-08-19
### Added & Refactored
- **Creeper Threat Repulsion AI (`FleeCreeperGoal`, `WolfAIMixin`)**:
  - Implemented 10-block emergency evasion fleeing at $1.5\times$ sprint speed upon detecting swelling (`getSwellDir() > 0`) or ignited (`isIgnited()`) Creepers.
  - Dispatches emergency alarm whine on start and emits sprint smoke trails at the dog's paws.
- **Global Particle Density System (`ParticleDensity`, `WolfParticleHelper`, `BetterDogsGameRules`, `BetterDogsConfig`)**:
  - Added 4-tier configurable particle density: `NONE` (0), `LOW` (1), `MEDIUM` (3, Default), and `HIGH` (6).
  - Integrated `/gamerule bd_particle_density <0-3>` and `BetterDogsConfig.json` (`particleDensity: "medium"`).
  - Added automated test suite `CreeperEvasionTest` asserting 10m evasion math, $1.5\times$ sprint speed, fuse sensitivity logic, particle density scaling, and strict null safety.

## [4.24.34+26.2] - 2026-08-18
### Added & Refactored
- **Aggressive Autonomous Stalking AI (`AggressiveTargetGoal`, `WolfAIMixin`)**:
  - Implemented proactive 16-block hostile monster scanning perimeter for Aggressive personality wolves and guard post sentries.
  - Added intimidating growl audio cue and angry alert particles (`ParticleTypes.ANGRY_VILLAGER`) upon acquiring targets with line-of-sight.
  - Enforced strict safety exemptions for Creepers (blast griefing avoidance) and Wardens (suicide charge avoidance).
  - Added automated test suite `AggressiveTargetTest` asserting 16m detection math, guard mode perimeter, and strict null safety.

## [4.24.33+26.2] - 2026-08-18
### Added & Refactored
- **Pacifist Threat Fleeing AI (`PacifistRevengeGoal`, `WolfAIMixin`)**:
  - Implemented dedicated non-violent threat evasion goal for Pacifist personality wolves.
  - When harmed or threatened, Pacifist wolves emit distress audio/particles and alert nearby packmates within 16 blocks to defend them.
  - Pacifist wolves tactically flee away from attackers at $1.25\times$ speed toward their owner or safety, preserving their gentle, non-violent nature.
  - Added automated test suite `PacifistRevengeTest` asserting defense alert radius math ($16\text{m}$), flee speed ($1.25\times$), personality filtering, and strict null safety.

## [4.24.32+26.2] - 2026-08-18
### Added & Refactored
- **Dynamic Owner Begging Proximity AI (`WolfBegGoal`, `DogTreatHelper`, `WolfAIMixin`)**:
  - Implemented single-purpose `WolfBegGoal` replacing legacy begging code, activating vanilla interest head-tilt (`wolf.setIsInterested(true)`) when standing dogs are within 5 blocks of a player holding treats or food.
  - Added food and treat detection helper `DogTreatHelper.isHoldingFoodOrTreat` supporting favorite treats, canine meats, and bones.
  - Smoothly tracks player eye height (`getEyeY()`) and halts navigation to preserve natural posture.
  - Added automated test suite `BeggingProximityTest` asserting 5-block distance threshold math and null safety.

## [4.24.31+26.2] - 2026-08-18
### Added & Refactored
- **Tamed Spontaneous Pack Howling AI (`WolfHowlHelper`, `GroupHowlGoal`, `WolfExtensions`, `WolfMixin`)**:
  - Implemented single-purpose `WolfHowlHelper` for nocturnal pack chorus propagation within 24 blocks (`BetterDogsConfig.get().getHowlSpreadRange()`).
  - Added mathematical harmonic pitch variation calculation ($0.85\text{F} \sim 1.20\text{F}$) and staggered response delays ($10\sim34$ ticks) for responding packmates.
  - Refactored `GroupHowlGoal` to handle skyward head orientation ($-45^\circ$), look targeting, and timer coordination.
  - Added musical note particle emissions (`ParticleTypes.NOTE`) and howling tick tracking in `WolfExtensions`/`WolfMixin`.
  - Added automated test suite `PackHowlTest` asserting harmonic pitch ranges, chorus delays, and null safety.

## [4.24.30+26.2] - 2026-08-18
### Fixed & Improved
- **Ground-Tracing Flight Catch-Up & Zero Sky Teleportation (`WolfTeleportHelper`, `WolfCatchupHelper`, `PersonalityFollowOwnerGoal`)**:
  - Eliminated airborne sky teleportation bug when players are flying in Creative mode (`player.getAbilities().flying`) or gliding with Elytra (`player.isFallFlying()`).
  - Added hazard-aware downward ground scanning in `WolfTeleportHelper.findSafeGroundPosBelow` utilizing heightmaps and walkability checks, allowing dogs to continuously trace safe solid ground directly beneath flying owners without mid-air spawns.
  - Refactored `PersonalityFollowOwnerGoal.teleportToOwner` to use `WolfTeleportHelper.findSafeTeleportPos`.
  - Added flight speed/distance dynamic throttling ($> 32$ blocks) in `WolfCatchupHelper.checkAndPerformCatchUp`.

## [4.24.29+26.2] - 2026-08-18
### Added & Refactored
- **High-Value Dog Treat Buff System (`DogTreatHelper`, `WolfInteractMixin`, `WolfInteractionHelper`)**:
  - Implemented single-purpose `DogTreatHelper` managing zero-allocation deterministic UUID bit-mixing favorite treat preference hashing, treat holding detection, and 6D-guarded treat feeding interactions.
  - Feeding favorite treats provides potent rejuvenation: full health restoration, Regeneration II (45s), joyful Zoomies running bursts, soothed timestamp updates, Jade tooltip discovery (`discoveredTreat`), ambient audio, and `HAPPY_VILLAGER` + `HEART` particle emissions.
  - Refactored `WolfInteractMixin` and cleaned duplicate treat logic from `WolfInteractionHelper`.
  - Added automated test suite: `FavoriteTreatTest.java` (24 total test suites).

## [4.24.28+26.2] - 2026-08-18
### Added & Refactored
- **Autonomous Low-Health Scavenging AI (`WolfScavengeHelper`, `EatGroundFoodGoal`, `BetterDogs`)**:
  - Implemented single-purpose `WolfScavengeHelper` managing food edibility filtering (`RAW_FOOD`/`COOKED_FOOD`), dynamic nutrition scaling via `DataComponents.FOOD` (`nutrition / 2.0f`, Rotten Flesh 1.0f), ground food refusal checks, eating audio/particles, and `SELF_SERVICE` advancement dispatch.
  - Refactored `EatGroundFoodGoal` to cleanly delegate all business logic to `WolfScavengeHelper`.
  - Added automated test suite: `GroundFeedingTest.java` (23 total test suites).

## [4.24.27+26.2] - 2026-08-18
### Added & Refactored
- **Selective Litter Sizing & Allele Inheritance (`WolfLitterHelper`, `AnimalMixin`, `BetterDogsGameRules`)**:
  - Extracted and implemented single-purpose `WolfLitterHelper` managing dynamic multi-puppy litter calculation (`calculateLitterSize`) and sibling puppy spawning (`processBreedingLitter`).
  - Refactored `AnimalMixin` to decouple inline breeding logic, allowing each sibling puppy to be generated independently with full genetic inheritance (personality, scale variance, coat variants, inbreeding checks), heart particles, bonus XP, and `WOLF_LITTER` advancement triggers for litters $\ge 2$.
  - Added automated test suite: `VariableLitterTest.java` (22 total test suites).

## [4.24.26+26.2] - 2026-08-18
### Added & Refactored
- **Defect Curing via Golden Apple (`WolfCureHelper`, `WolfInteractMixin`, `BetterDogs`)**:
  - Implemented single-purpose `WolfCureHelper` managing Golden Apple and Enchanted Golden Apple curing interactions for inbred runt dogs.
  - Curing clears inbred genetic flags, restores scale to normal stature ($\ge 1.0\times$), applies healthy personality combat stats, emits `ParticleTypes.HAPPY_VILLAGER` sparkle particles + levelup audio, and fires `CURE_INBRED` advancement trigger.
  - Enchanted Golden Apples grant additional Regeneration II (20s) and Absorption I (2m) status effects.
  - Added automated test suite: `InbredCureTest.java` (21 total test suites).

## [4.24.25+26.2] - 2026-08-18
### Added & Refactored
- **Lineage Tracking & Inbreeding Defects (`WolfInbreedingHelper`, `WolfBreedingMixin`, `BetterDogs`)**:
  - Extracted and implemented single-purpose `WolfInbreedingHelper` managing 3-generation parent UUID lineage verification, runt scale penalties ($0.7\times$ scale reduction), smoke particle effects (`ParticleTypes.SMOKE`), and advancement triggers.
  - Refactored `WolfBreedingMixin` to delegate lineage processing and runt penalty effects to `WolfInbreedingHelper`, triggering `INBRED_WOLF` and `OUTCROSS_RUNT` criteria for the breeding player.
  - Added automated test suite: `InbreedingLineageTest.java` (20 total test suites).

## [4.24.24+26.2] - 2026-08-18
### Added & Refactored
- **Low-Health Tactical Disengagement AI (`WolfFleeHelper`, `WolfFleeLowHealthGoal`, `BetterDogsGameRules`)**:
  - Extracted and implemented single-purpose `WolfFleeHelper` managing 30% Max HP threshold evaluation, personality flee probability scaling (Pacifist 100%, Normal 50%, Aggressive 10%), escape vector calculation away from attackers (`DefaultRandomPos.getPosAway`), and server-side disengagement feedback.
  - Refactored `WolfFleeLowHealthGoal` to delegate condition checks and escape pathing to `WolfFleeHelper`, playing `SoundEvents.WOLF_WHINE_BABY` audio with pitch modulation and spawning 3 `ParticleTypes.SPLASH` sweat droplet particles upon tactical retreat.
  - Added automated test suite: `LowHealthFleeTest.java` (19 total test suites).

## [4.24.23+26.2] - 2026-08-17
### Added & Refactored
- **Tactical Pack Flanking Coordination AI (`WolfFlankingHelper`, `WolfFlankAttackGoal`, `BetterDogsGameRules`)**:
  - Extracted and implemented single-purpose `WolfFlankingHelper` managing approach-time calculation ($t = \frac{\text{dist}}{\text{speed}}$), deterministic tie-breaker sorting, dynamic bounding box clearance scaling ($\max(3.0, \text{bbWidth} \times 2.5)$), and raycast line-of-sight collision checks.
  - Refactored `WolfFlankAttackGoal` to cleanly delegate tactical pack coordination: closest 50% of the pack charges directly (at 50% approach speed until melee), while slower 50% execute multi-angle flanking arcs with opposite-side fallback if terrain is blocked.
  - Added automated test suite: `PackFlankingTest.java` (18 total test suites).

## [4.24.22+26.2] - 2026-08-17
### Added & Refactored
- **Playful Hyperactive Zoomies AI (`WolfZoomiesHelper`, `ZoomiesGoal`, `ZoomiesDogEvent`, `BetterDogsGameRules`, `WolfExtensions`, `WolfMixin`)**:
  - Implemented single-purpose `WolfZoomiesHelper` managing playful zoomies activation, multi-event triggers (morning dawn wakeup, post-feeding meals/treats, standing up from rest), and real-time particle dispatch.
  - Modernized `ZoomiesGoal` for $1.5\times$ rapid arc sprinting around the owner (`DefaultRandomPos.getPosTowards`) with real-time emerald sparkles (`HAPPY_VILLAGER`) and paw dust poofs (`POOF`).
  - Added strict 6D safety: ordering dogs to sit immediately halts zoomies, and dogs in Guard Mode or active combat never trigger zoomies.
  - Registered dynamic GameRules: `betterdogs:bd_zoomies_enabled` and `betterdogs:bd_zoomies_duration_ticks`.
  - Added automated test suite: `ZoomiesSprintTest.java`.

## [4.24.21+26.2] - 2026-08-17
### Added & Refactored
- **Stick & Bone Fetch Retrieval AI (`WolfFetchHelper`, `WolfFetchGoal`, `BetterDogsGameRules`, `WolfExtensions`, `WolfMixin`)**:
  - Implemented single-purpose `WolfFetchHelper` managing autonomous detection of dropped sticks and bones (`Items.STICK`, `Items.BONE`, and `betterdogs:fetch_items` tag) within 16 blocks.
  - Modernized `WolfFetchGoal` for rapid $1.25\times$ sprint retrieval, holding items in jaws, and returning directly to the owner's feet ($2.5\text{m}$ arrival threshold).
  - Added ground item delivery with `setDefaultPickUpDelay()`, happy ambient bark audio, 6 `HAPPY_VILLAGER` particles, and `"fetch_stick"` advancement trigger.
  - Added strict 6D safety: sitting dogs, guard mode sentries, and dogs in active combat will never break posture or state to fetch.
  - Registered dynamic GameRules: `betterdogs:bd_fetch_enabled` and `betterdogs:bd_fetch_range`.
  - Added automated test suite: `FetchRetrievalTest.java`.

## [4.24.20+26.2] - 2026-08-17
### Added & Refactored
- **Revenge Grudge Nemesis AI (`WolfNemesisHelper`, `WolfNemesisTargetGoal`, `WolfAIMixin`, `WolfMixin`)**:
  - Implemented single-purpose `WolfNemesisHelper` managing pack revenge broadcasts: when a tamed dog is slain, the killer entity type ID is broadcast across a 64-block radius to all tamed pack dogs of the same owner.
  - Avenging pack dogs enter a 3-day active revenge grudge (governed by `betterdogs:bd_nemesis_duration_days` and `betterdogs:bd_nemesis_system`), displaying angry villager particles and warning growls.
  - Implemented single-purpose `WolfNemesisTargetGoal` providing 20m target scanning with strict sitting posture preservation (`!isInSittingPose()`) and Guard Mode boundary respect.
  - Added multi-tier immunity safeguards protecting owners, friendly packmates, and adult disciplinary corrections.
  - Added automated test suite: `NemesisGrudgeTest.java`.

## [4.24.19+26.2] - 2026-08-17
### Fixed & Remediated
- **Registered Missing Mixin (`HurtByTargetGoalMixin`)**:
  - Registered `HurtByTargetGoalMixin` in `vanilla-outsider-better-dogs.mixins.json` to ensure gentle adult puppy discipline target silencing ("Muzzle") loads properly at runtime.
- **Eliminated Mixin Push Collision (`EntityMixin`)**:
  - Removed duplicate `push` injection and obsolete detour helper in `EntityMixin.java`, delegating collision safety cleanly and exclusively to `WolfPushMixin.java` and `WolfCliffSafetyHelper.java`.
- **Zero-Allocation Favorite Treat Hashing (`WolfInteractionHelper`)**:
  - Replaced hot-path `new Random(seed)` instantiation in `getFavoriteTreat` with deterministic UUID bit-mixing hash.
- **Template Clean-Up**:
  - Purged leftover `template-mod.client.mixins.json`.

## [4.24.18+26.2] - 2026-08-17
### Added & Refactored
- **Lost Stray Collarless Adoption (`WolfAdoptionHelper`, `WolfInteractMixin`, `WolfMixin`)**:
  - Implemented single-purpose `WolfAdoptionHelper` managing Paper Certificate listing (consumes 1 Paper, plays page turn sound, emits sparkles), ambient sparkles every 2s, peaceful owner cancellation, and empty-hand claiming by new adopters (12 heart particles, level-up chime, ownership transfer).
  - Integrated adoption interaction handling into `WolfInteractMixin` and ambient particle ticking into `WolfMixin`.
  - Added automated test suite: `AdoptionSystemTest.java`.

## [4.24.17+26.2] - 2026-08-17
### Added & Refactored
- **Stationary Bone Guard Mode AI (`WolfGuardHelper`, `WolfGuardGoal`, `WolfInteractMixin`)**:
  - Implemented single-purpose `WolfGuardHelper` managing Bone right-click toggling, action bar feedback, sound/particles, and personality patrol radii (Aggressive 12m, Normal 8m, Pacifist 4m).
  - Modernized single-purpose `WolfGuardGoal` providing strict territory boundary leashing ($1.25\times$ return sprint), subtle ambient paw dust, and Pacifist defensive regeneration pulses to nearby injured allies.
  - Intercepted Bone interaction directly in `WolfInteractMixin`.
  - Added automated test suite: `GuardModePatrolTest.java`.

## [4.24.16+26.2] - 2026-08-17
### Added & Refactored
- **Acoustic Goat Horn Command Suite (`WolfHornCommandHelper`, `WolfHornGoal`, `InstrumentItemMixin`)**:
  - Implemented single-purpose `WolfHornCommandHelper` managing 64-block acoustic pack command broadcasting across 5 tactical horn variants (Yearn=Stand/Follow, Sing=Sit/Stay, Ponder=Rally, Seek=32m Crosshair Raycast Attack, Feel=Pacify/Calm for 30s) with note, happy, and angry particle feedback.
  - Implemented single-purpose `WolfHornGoal` handling wolf acoustic pathfinding towards horn sound coordinates.
  - Refactored `InstrumentItemMixin` from a 262-line monolith into a surgical delegation mixin.
  - Added automated test suite: `GoatHornCommandTest.java`.

## [4.24.15+26.2] - 2026-08-17
### Added & Refactored
- **Puppy Miscellaneous Mischief & Adult Discipline (`WolfMischiefHelper`, `BabyMischiefGoal`)**:
  - Implemented single-purpose `WolfMischiefHelper` managing multi-target playful puppy mischief (adult dogs, chickens, rabbits, players) and personality-scaled adult discipline (warning growls, 160-tick calm discipline, and aggressive puppy retaliation/blood feud chances).
  - Modernized single-purpose `BabyMischiefGoal` managing joyful bounding movements and happy villager sparkles without causing harm.
  - Added automated test suite: `PuppyMischiefTest.java`.

## [4.24.14+26.2] - 2026-08-17
### Added & Refactored
- **Thunderstorm Fear & Shelter AI (`WolfStormHelper`, `WolfStormAnxietyGoal`)**:
  - Implemented single-purpose `WolfStormHelper` managing thunderstorm weather evaluation, personality fear multipliers (Pacifist $3.0\times$ fear, Normal $1.0\times$ mild anxiety, Aggressive $0.0\times$ fearless/immune), 10-minute soothed immunity, and indoor overhead shelter searching with owner huddle comfort.
  - Implemented single-purpose `WolfStormAnxietyGoal` providing sitting whimper/tremble preservation, nervous head pitch/yaw looks, water/splash droplets, and navigation to covered shelter.
  - Added automated test suite: `StormAnxietyTest.java`.

## [4.24.13+26.2] - 2026-08-17
### Added & Refactored
- **Interactive Petting & Soothing Interaction (`WolfPettingHelper`)**:
  - Implemented dedicated single-purpose `WolfPettingHelper` handling sneak-right click empty hand petting across both sitting and standing postures without modifying sit state.
  - Clears combat anger and hostile target instantly, plays happy whining audio (`SoundEvents.WOLF_WHINE` pitch 1.2), emits 3 Heart and 4 Note particles, triggers hand swing animation with 1-second debounce, and applies a 10-minute (12,000-tick) soothing state against thunderstorm anxiety.
  - Intercepted petting directly at `WolfInteractMixin.mobInteract` HEAD.
  - Added automated test suite: `PettingSootheTest.java`.

## [4.24.12+26.2] - 2026-08-17
### Added & Refactored
- **Fast Travel Catch-up & Interdimensional Teleport Sync (`WolfCatchupHelper`)**:
  - Implemented single-purpose `WolfCatchupHelper` calculating dynamic high-speed catch-up acceleration ($1.5\times$ for $>10$m or mount riding, $2.0\times$ for $>20$m sprint) and handling interdimensional teleport synchronization across Nether/End portals and `/tp`.
  - Integrated `WolfCatchupHelper.calculateCatchupSpeed` into `PersonalityFollowOwnerGoal` and streamlined `ServerPlayerTickMixin`.
  - Removed duplicate/legacy `WolfCatchUpHelper.java`.
  - Added automated test suite: `FastTravelCatchupTest.java`.

## [4.24.11+26.2] - 2026-08-17
### Added & Refactored
- **Scale Variance, Offspring Inheritance & Personality Stat Scaling (`WolfScaleGeneticsHelper`, `WolfPersonalityStatHelper`)**:
  - Implemented single-purpose `WolfScaleGeneticsHelper` generating wild wolf scales via Gaussian bell curve centered at $1.0\times$ (std dev $0.12$, bounded by `bd_wolf_min_scale_percent` and `bd_wolf_max_scale_percent`) and calculating offspring scale inheritance with continuous $\pm 10\%$ genetic variance.
  - Implemented single-purpose `WolfPersonalityStatHelper` managing dynamic personality attribute modifiers (Aggressive $+20\%$ damage / $+15\%$ speed / $+10\%$ HP, Pacifist $-20\%$ damage / $+10\%$ speed / $+20\%$ HP / $+0.5$ knockback, Normal baseline speed boost) with inbreeding runt penalty scaling.
  - Decoupled and deleted legacy monolithic `WolfStatManager.java`, redirecting all spawning, breeding, interaction, and command mixins to dedicated helpers.
  - Added automated test suites: `ScaleGeneticsTest.java` and `PersonalityStatScalingTest.java`.

## [4.24.10+26.2] - 2026-08-17
### Fixed & Refactored
- **Wolf Movement Stutter & AI Checker Velocity-Zeroing Elimination (`WolfSafetyMixin`, `WolfPushMixin`, `PersonalityFollowOwnerGoal`)**:
  - Completely removed legacy `WolfSafetyMixin` tick loop that was zeroing velocity (`Vec3.ZERO`) and freezing navigation when walking down slopes, hills, stairs, or mid-jump.
  - Implemented gold-standard push interception via `WolfPushMixin` and single-purpose `WolfCliffSafetyHelper`, preventing players and entities from shoving wolves off cliffs (>3 block drop) or pushing sitting dogs with zero tick overhead on normal walking movement.
  - Streamlined `AvoidHazardsGoal` to scan exclusively for lethal thermal hazards (lava, fire, magma, lit campfires) via dedicated `WolfHazardHelper`, eliminating erroneous navigation aborts on normal terrain slopes.
  - Refactored `PersonalityFollowOwnerGoal` to provide smooth, uninterrupted continuous owner tracking without start/stop jitter.
  - Removed obsolete ambient `PathToSoundLocationGoal` movement interruption from `WolfAIMixin`.
  - Added automated test suites: `HazardDetectionTest.java`, `CliffSafetyTest.java`, and `HazardDetourTest.java`.

## [4.24.9+26.2] - 2026-08-17
### Added & Polished
- **Dynamic GameRules Null-Safe Accessors & Tag Baseline (`BetterDogsGameRules.java`, `BetterDogsTags.java`)**:
  - Implemented static null-safe fallback accessors (`getBoolean` and `getInt`) on `BetterDogsGameRules` delegating cleanly to `DynamicGameRuleManager` from `dasik-library`.
  - Added automated test suite `BetterDogsGameRulesTest.java` verifying fallback retrieval and null-safety across all 80+ dynamic GameRules.

## [4.24.8+26.2] - 2026-08-16
### Fixed
- **Minecart & Vehicle Dismount Interaction Interception (`DogCommandManager.java`, `WolfInteractionHelper.java`)**:
  - Resolved raycast hitbox interception when clicking directly on Minecarts, Boats, or Seats containing owned dogs with a command item or empty hand.
  - Implemented anti-collision lateral offset positioning (`0.8` blocks away from vehicle bounding box) on dismount, completely preventing vanilla `AbstractMinecart` collision logic from immediately re-boarding the wolf.
  - Ensured active vehicle navigation targets and selections are cleanly cleared on dismount, accompanied by cloud particles and dismount audio.

## [4.24.7+26.2] - 2026-08-11
### Refactored & Polished
- **Minimal Tame Particle Polish (`WolfParticleHandler`)**: Polished first-time taming particle feedback with a clean, subtle Minimal Signature (~5-6 particles) per personality:
  - **Aggressive**: 3 `ANGRY_VILLAGER` icons + 3 subtle crimson dust sparkles (`0xFF3333`).
  - **Normal**: 3 `HAPPY_VILLAGER` emerald stars + 3 subtle golden dust sparkles (`0xFFD700`).
  - **Pacifist**: 3 `HEART` icons + 3 subtle mint dust sparkles (`0x00FF88`).

## [4.24.6+26.2] - 2026-08-10
### Changed
- **Dismount Overlay Message Translation (`en_us.json` & `id_id.json`)**: Updated `text.betterdogs.dog_dismounted` overlay text to `"%s hopped out of the seat."` (Indonesian: `"%s keluar dari tempat duduk."`) for improved clarity when commanding dogs to dismount vehicles and seats.

## [4.24.5+26.2] - 2026-08-10
### Fixed
- **YACL Config Option Descriptions (`YaclScreenHelper` & Translation Sheets)**:
  - **Attached Descriptions**: Added missing `.description(OptionDescription.of(...))` builders to every option across all 6 YACL config categories (General, Personalities, Breeding, Territoriality, Gifts, Visual & Performance Options).
  - **Translation Keys**: Added complete `.description` translation keys to both `en_us.json` and `id_id.json` for all options, ensuring informative hover tooltips are displayed cleanly in the YACL v3 GUI screen.

## [4.24.4+26.2] - 2026-08-10
### Fixed
- **Dog Adoption Crash Fix (`WolfInteractionHelper` & `WolfStatManager`)**:
  - **Personality Null Guard**: Added null personality safeguards to `WolfStatManager.applyPersonalityStats` (`if (personality == null) personality = WolfPersonality.NORMAL;`), preventing `NullPointerException` crashes when adopting vanilla or unassigned wolves.
  - **Personality Pre-Assignment**: Automatically initializes a random personality on adoption if the adoptable wolf lacks one prior to applying owner stats.
  - **Safe Owner UUID Lookup**: Replaced unsafe `getOwnerReference()` with safe `getOwnerUUID()`, preventing null dereference exceptions when former owners are offline or unlinked.

## [4.24.3+26.2] - 2026-08-10
### Fixed & Added
- **Litematica Compatibility & Command Item Expansion (`DogCommandManager`)**:
  - **Litematica Tool Conflict Fix**: Resolved issue where sneaking with a stick to dismount or command mount tame wolves onto seats/vehicles failed when Litematica was installed due to Litematica intercepting `minecraft:stick` right-clicks on client side.
  - **Data-Driven Command Item Tag (`vanilla-outsider-better-dogs:command_items`)**: Created `#vanilla-outsider-better-dogs:command_items` item tag containing `minecraft:stick`, `minecraft:blaze_rod`, `minecraft:breeze_rod`, and `#c:tools/sticks`.
  - **Alternative Command Tools**: Players can now use **Blaze Rods** or **Breeze Rods** (or any stick-like tool) to dismount and command mount dogs seamlessly alongside standard sticks.
  - **Bone Reservation Safeguard**: Explicitly excluded `minecraft:bone` from command items so Bone remains 100% reserved for toggling Guard Mode (`bd_guard_mode`).

## [4.24.2+26.2] - 2026-08-10
### Added
- **Dynamic Climate-Aware Wolf Coat Variants (`WolfVariantHelper`)**:
  - **Universal Biome Compatibility**: Introduced dynamic climate coat variant resolution (`betterdogs:bd_dynamic_climate_variants`, default `true`). When wolves spawn in modded or un-mapped biomes (Biomes O' Plenty, Terralith, Regions Unexplored, etc.) and Vanilla tag lookup defaults to Pale, Better Dogs evaluates the biome's physical climate properties (temperature, precipitation, downfall) at spawn time.
  - **Dynamic Climate Assignment**: Automatically assigns Snowy (cold/snowy), Ashen/Red (arid/hot), Rusty (jungle/humid), Black (dark taiga), or Chestnut (cool taiga) coat variants based on real climate data.
  - **3-Tier Priority Safeguard**: Preserves third-party custom mod variants (`somemod:custom_wolf`) and native Vanilla tag matches 100% untouched, acting as a smart fallback only when default Pale/Woods fallback occurs.

## [4.24.1+26.2] - 2026-07-30
### ⚠️ Incompatibility Warning
- **Minecraft 26.2 Target Only**: `4.24.1+26.2` is strictly compiled for Minecraft 26.2. Running on Minecraft 26.3 snapshot instances will crash on startup due to upstream Minecraft 26.3 API refactors (`EntityPredicate.ADVANCEMENT_CODEC` removal and `DynamicGameRuleManager` breaking changes). Upgrade to `5.0.0+26.3` for Minecraft 26.3+.

### Fixed
- **Startup Crash / GameRule Registration Idempotency**:
  - **Dynamic Registry Delegation**: Refactored `BetterDogsGameRules` registration methods (`registerBoolean` and `registerInteger`) to delegate through `DynamicGameRuleManager` from `dasik-library`.
  - **Duplicate Key Exception Fix**: Resolves `IllegalStateException: Adding duplicate key 'ResourceKey[minecraft:game_rule / betterdogs:bd_creeper_awareness]' to registry` on Fabric 26.2 client/server startup.

## [4.24.0+26.2] - 2026-07-30
### ⚠️ Incompatibility & Startup Crash Warning
- **Minecraft 26.3 Startup Crash & Dynamic GameRule Crash**: `4.24.0+26.2` crashes on Fabric 26.3 snapshot instances (`NoSuchFieldError: EntityPredicate.ADVANCEMENT_CODEC`) and can crash on MC 26.2 startup if duplicate GameRule keys are registered. Upgrade to `4.24.1+26.2` for Minecraft 26.2 or `5.0.0+26.3` for Minecraft 26.3+.

### Added
- **AI Refinement: Creeper Blast Evasion**:
  - **Swelling Fuse Detection**: Enhanced creeper awareness (`FleeCreeperGoal.java`). When a nearby creeper begins swelling/igniting within 10 blocks, tamed wolves sprint radially away at maximum speed (`1.5x`).
  - **Smoke Particle Trails**: Emits emergency sprint smoke trails (`ParticleTypes.SMOKE`) at the feet of the fleeing wolf.
  - **GameRules**: Governed by `betterdogs:bd_creeper_evasion_enabled` (default `true`).

## [4.23.0+26.2] - 2026-07-30
### Added
- **Goat Horn Command System (Stage 5: Seek Horn - Search & Track Target Command)**:
  - **Seek Goat Horn (Focus Fire & Area Search)**: Blowing the Seek Goat Horn (`seek_goat_horn`) commands active following wolves in range (default: `64` blocks) to focus fire on a targeted entity or enter aggressive area search mode at `1.3x` speed.
  - **Cone-Inflated Crosshair Raycast (`EntityRaycastHelper`)**: Reliable 32-block cone raycast intersects target entity under player's crosshair.
  - **Fallback Area Search**: If no entity is highlighted under crosshair, wolves automatically search `bd_horn_command_range` for hostiles targeting the owner or pack. Emits smoke particles if no hostile targets are found.

## [4.22.0+26.2] - 2026-07-30
### Added
- **Goat Horn Command System (Stage 4: Yearn Horn - Resume Follow Command)**:
  - **Yearn Goat Horn (Resume Follow / Stand Up)**: Blowing the Yearn Goat Horn (`yearn_goat_horn`) orders all sitting owned wolves in range (default: `64` blocks) to stand up (`setOrderedToSit(false)` & `setSittingManually(false)`) and resume following.
  - **Dedicated Filter (`isEligibleSittingWolf`)**: Specifically targets owned, non-leashed, non-guarding sitting dogs.
  - **State Reset**: Clears assemble pathing targets and Tactical Pacifist override timers. Emits joyful musical note and green particles.

## [4.21.0+26.2] - 2026-07-30
### Added
- **Goat Horn Command System (Stage 3: Sing Horn - Hold Command)**:
  - **Sing Goat Horn (Hold Command / Sit)**: Blowing the Sing Goat Horn (`sing_goat_horn`) orders all active following wolves in range (default: `64` blocks) to sit down in place (`setOrderedToSit(true)` & `setSittingManually(true)`).
  - **Preemption Handling**: Blowing Sing Horn clears active assemble pathing (`soundLocationTarget = null`) and active Tactical Pacifist overrides (`passiveOverrideTicks = 0`).
  - **Combat Disengagement**: Instantly clears attack targets, hostility, and navigation. Emits single-burst musical note particles.

## [4.20.0+26.2] - 2026-07-30
### Added
- **Goat Horn Command System (Stage 2: Feel Horn - Tactical Pacifist Override)**:
  - **Feel Goat Horn (Tactical Pacifist - 30s)**: Blowing the Feel Goat Horn (`feel_goat_horn`) triggers a 30-second (`600` ticks) Tactical Pacifist override on all active following wolves in range (default: `64` blocks).
  - **Immediate Combat Clear**: Wolves instantly drop active attack targets, clear hostility, stop navigation, and emit single-burst green happy villager particles.
  - **Preemption Handling**: Blowing Feel Horn cancels any active Ponder assemble pathing, and blowing Ponder Horn immediately cancels Tactical Pacifist override.
  - **GameRules**: Added `betterdogs:bd_horn_override_duration` (default `600` ticks).

## [4.19.0+26.2] - 2026-07-30
### Added
- **Goat Horn Command System (Stage 1: Ponder Horn)**:
  - **Ponder Goat Horn (Assemble Call)**: Blowing the Ponder Goat Horn (`ponder_goat_horn`) issues an Assemble Call, commanding all active following wolves in range (default: `64` blocks) to pathfind directly to the location where the horn was blown at `1.25x` speed.
  - **Single-Burst Note Particles**: Wolves emit single-burst musical note particles upon receiving the call without visual noise.
  - **Strict Follow Filtering**: Sitting, leashed, or guarding dogs remain stationed at their posts and ignore the call.
  - **GameRules**: Added `betterdogs:bd_horn_command_range` (default `64`) and `betterdogs:bd_horn_pathing_timeout` (default `400` ticks).

## [4.18.2+26.2] - 2026-07-22

### ⚠️ Version Guard Notice
- Includes zero-dependency ModVersionGuard pre-release protection. Halts startup with an explicit warning banner if run on incompatible Minecraft drops or missing core dependencies to prevent world save corruption.

### Fixed
- **ModVersionGuard Protection Banner**: Updated ModVersionGuard.java to use Knot ClassLoader resolution (Thread.currentThread().getContextClassLoader()) and display explicit pre-release protection warnings upon an API mismatch.

## [4.18.1+26.2] - 2026-07-22 Do not upload

### Added
- **Forward Compatibility & Version Guard**: Configured `fabric.mod.json` with `"minecraft": ">=26.2-"` for open-ended forward compatibility. Added zero-dependency `ModVersionGuard` check on startup to display human-readable guidance if an incompatible Minecraft API version is encountered.

## [4.18.0+26.2] - 2026-07-21
### Added
- **Fast Travel & Chunk-Unload Catch-Up Teleport Safety**: Added automatic periodic catch-up teleportation for standing following wolves when the owner moves rapidly (via Elytra, horses, or speed buffs) and distance exceeds 40 blocks before entering unloaded chunks.
- **Strict Follow Filter**: Only teleports active following wolves (`!unableToMoveToOwner()`), strictly ignoring dogs that are sitting, leashed, or assigned to Guard/Sentry Mode. Controlled via `betterdogs:bd_fast_travel_catchup`.

## [4.17.0+26.2] - 2026-07-21
### Added
- **Configurable Wild Wolf Pack Group Sizes**: Added namespaced GameRules `betterdogs:bd_wolf_spawn_group_min` (default: `4`) and `betterdogs:bd_wolf_spawn_group_max` (default: `8`) to dynamically configure the minimum and maximum group size of naturally spawning wild wolf packs.
- **Expanded Biomes Natural Spawning**: Added GameRule `betterdogs:bd_wolf_spawn_expanded_biomes` (default: `false`). When enabled, naturally expands wild wolf spawning to plains, meadows, forests, and mountain biomes cleanly without hard biome edits.
- **YACL Controls**: Integrated controls for group size limits and expanded biomes into the YACL configuration screen.

## [4.16.0+26.2] - 2026-07-21
### Added
- **Owner Teleport Synchronization**: Standing tamed wolves in the active follow state now automatically teleport alongside their owner during long-distance (> 24 blocks) or cross-dimension teleports.
- **Strict Follow-State Filtering**: Restricts teleportation strictly to active following wolves. Wolves that are manually sitting, leashed, or assigned to Guard/Sentry Mode remain at their posts. Controlled via namespaced GameRule `betterdogs:bd_sync_owner_teleport`.

## [4.15.18+26.2] - 2026-07-21
### Added
- **Farmer's Delight Favorite Treats Integration**: Tamed wolf Favorite Treats dynamically incorporate Farmer's Delight Refabricated food items (`farmersdelight:dog_food`, `minced_beef`, `mutton_chops`, `cooked_mutton_chops`, `bacon`, `cooked_bacon`, `chicken_cuts`, `cooked_chicken_cuts`, `ham`, `smoked_ham`, `beef_stew`, `chicken_soup`, `vegetable_soup`, `fish_stew`) into the candidate treat pool when Farmer's Delight is installed.
- **Crash-Safe Vanilla Fallback**: Uses lazy runtime registry queries via `BuiltInRegistries.ITEM`. If Farmer's Delight is absent or on vanilla clients/servers, the system seamlessly defaults to the 9 standard vanilla treat items without errors or missing item references.

## [4.15.17+26.2] - 2026-07-12
### Added
- **Dynamic Flanking Distance Scaling**: Tamed and wild pack flanking maneuvers now scale flanking offset distances dynamically based on the width of the targeted entity's bounding box. Radius scales to `Math.max(3.0, targetWidth * 2.5)` and rear shift scales to `Math.max(1.0, targetWidth * 1.1)`.
- **Flanking Path Raycast Verification**: Added a namespaced GameRule `betterdogs:bd_flanking_raycast_check` that controls path raycast checking. When enabled, flanking dogs raycast from their eye height to the calculated destination to verify that paths are clear of solid obstacles and deep water before executing flanking loops.
- **Raycast Fail Fallbacks**: If the primary flanking side is blocked, dogs try the opposite side. If both sides are blocked, they fall back to standard direct melee charging to prevent getting stuck in dead ends.

## [4.15.16+26.2] - 2026-07-12
### Added
- **Cozy Storm Shelter**: Dogs suffering from storm anxiety now search for covered blocks where they cannot see the sky (in a 12x4x12 area, prioritizing the owner's vicinity). They will navigate to shelter if idle instead of pacing randomly.
- **Comfort Soothing**: Players can comfort their anxious dogs during a storm by sneaking and right-clicking them with an empty hand, or by feeding them their Favorite Treat. Petting plays a comforting low-pitched whimper and note/heart particles, soothing their storm anxiety for 10 minutes (12000 ticks) and disabling whining and pacing.

## [4.15.15+26.2] - 2026-07-12
### Added
- **Smart Creeper Blast Evasion**: Increased dog detection/avoidance range of active/swelling creepers to `10` blocks, raised walking/sprinting evasion speeds to `1.2`/`1.6`, and added visual smoke trails at their feet when running away.
- **GameRule Compliance**: Properly checked the `betterdogs:bd_creeper_awareness` GameRule inside `FleeCreeperGoal` to support in-game toggling.

## [4.15.14+26.2] - 2026-07-12
### Added
- **Configurable Wolf Size Range**: Added `wolfMinScale` and `wolfMaxScale` global options to config, allowing visual scale limits to be customized.
- **Dynamic Size Range GameRules**: Added namespaced GameRules `betterdogs:bd_wolf_min_scale_percent` and `betterdogs:bd_wolf_max_scale_percent` to allow administrators to dynamically clamp scale on the logical server per-world.
- **YACL UI Integration**: Integrated sliders for minimum and maximum size configuration in the Breeding & Genetics settings screen, including notice descriptions about per-world isolation.
- **API Hardening**: Upgraded dependency constraint to `dasik-library >=1.8.3` to consume the new dynamic genetics limit APIs.

## [4.15.13-26.2] - 2026-07-11
### Added
- **YACL Option Descriptions**: Implemented description tooltips for all configuration options.
- **Dynamic Warning Notice**: Added a dynamic helper that appends a gold warning notice (`§6Notice:§r...`) to all GameRule-default configuration settings, notifying players that settings for existing worlds must be changed in-game.
- **Added Localization Keys**: Registered custom description translations for the 11 configuration-only (non-GameRule) options in the mod's `en_us.json` file.

## [4.15.12-26.2] - 2026-07-11
### Fixed
- **ModMenu Config Screen Integration**: Corrected the YACL mod ID check in `ModMenuIntegration` from `"yet-another-config-lib"` to `"yet_another_config_lib_v3"`, resolving the issue where the config button would not display or crashed when YACL v3 was installed.

## [4.15.11-26.2] - 2026-07-07
### Changed
- **YACL Config Screen Migration**: Replaced optional Cloth Config integration with YetAnotherConfigLib (YACL) v3. Removed Cloth Config dependencies and classes. Added suggestions and configuration categories inside the client-only entrypoint, using a reflection-based ModMenu screen factory loader to ensure server-side compatibility.

## [4.15.10-26.2] - 2026-07-05
### Changed
- **Tamed Pack Spacing Multiplier**: Increased default `tamedPackSpreadMultiplier` to `280` (2.8x) to mathematically scale follow distances so that standard large packs (approx. 30 wolves) reach the maximum follow range spacing limit.

## [4.15.9-26.2] - 2026-07-05
### Changed
- **Tamed Pack Spacing Limit**: Increased default `tamedPackSpreadMax` to `150` (15.0 blocks) to prevent crowding when running large packs of tamed wolves.

## [4.15.8-26.2] - 2026-07-01
### Changed
- **Dynamic Follow Range Scaling**: Scaled tamed wolf `Attributes.FOLLOW_RANGE` dynamically based on personality base stats (Aggressive: 32, Normal: 24, Pacifist: 16) plus a transient follow range spread offset to prevent wolves from dropping target too quickly in combat.

## [4.15.7-26.2] - 2026-06-30
### Fixed
- **Tamed Wander Boundary Pacing**: Replaced the return-path fallback inside `TamedWanderNearOwnerGoal` with a clean null return. Wolves now naturally stand still at their perimeter boundary instead of pacing back and forth.

## [4.15.6-26.2] - 2026-06-30
### Fixed
- **Sit and Flee Low Health Conflict**: Added checks to prevent low-health fleeing goals from activating when a tamed wolf is sitting.

## [4.15.5-26.2] - 2026-06-30
### Changed
- **Wide-Arc Encirclement Adjustment**: Increased flanking coordinate side offset to 4.5 blocks and rear wrap to 2.0 blocks. Dampened direct approach speed to 50% during distant approaches.

## [4.15.4-26.2] - 2026-06-30
### Changed
- **Context-Aware Flanking Selection**: Flanker role assignments now factor in both distance and speed (Approach Time). Dogs engagement directions (left/right) are chosen contextually based on their current physical side to prevent crossing.

## [4.15.3-26.2] - 2026-06-30
### Changed
- **Speed-Based Flanker Selection**: The top 50% fastest dogs in the local pack execute flanking, while the slower 50% form the direct assault line.

## [4.15.2-26.2] - 2026-06-30
### Changed
- **Tamed Flanking Coordination**: Direct-charging wolves slow down to 80% speed during target approach, allowing flanking followers at normal speed to successfully encircle the target first.

## [4.15.1-26.2] - 2026-06-30
### Fixed
- **Tamed Flanking Navigation**: Overrode the `tick()` method in flanking goals to bypass vanilla pathing overrides and added a 1.35x speed boost to allow flanking dogs to sweep around targets dynamically.

## [4.15.0-26.2] - 2026-06-30
### Added
- **Tamed Pack Flanking Tactics**: Tamed wolves now treat their owner as the pack leader, enabling cooperative flanking AI during combat (splitting left/right around targets).

## [4.14.7-26.2] - 2026-06-28
### Added
- **Hidden Favorite Treats**: Added a "Hidden until discovered" mode to the Jade tooltip for a tamed dog's Favorite Treat.

## [4.14.6-26.2] - 2026-06-28
### Changed
- **Sanitary Compliance**: Migrated all GameRule keys to the `betterdogs` namespace.

## [4.14.5-26.2] - 2026-06-28
### Added
- **Jade Config**: Added built-in configuration toggles to the Jade plugin.

## [4.14.4-26.2] - 2026-06-28
### Fixed
- **Jade Sync**: Fixed client-side desync for the Inbred tag display by implementing a Jade `IServerDataProvider` sync package.

## [4.14.3-26.2] - 2026-06-28
### Changed
- **Refactor**: Extracted combat hooks from `WolfMixin` to `WolfCombatMixin` to stay under the 300 LOC limit.

## [4.14.2-26.2] - 2026-06-28
### Optimized
- **Performance Optimization**: Extracted particle options into static fields and throttled adoptable particle trails and Pacifist buff checks via tick modulo.

## [4.14.1-26.2] - 2026-06-28
### Fixed
- **Jade Registration**: Resolved Jade plugin modid registration crashes.

## [4.14.0-26.2] - 2026-06-28
### Added
- **Jade Inbred Tag**: Displays inbred genetic tags in Jade HUD tooltips.

## [4.13.5-26.2] - 2026-06-28
### Added
- **Dynamic Jade Health UI**: Added a custom heart element to bypass Jade's hardcoded 20-icon render cap, and implemented client synchronization for Max Health attribute modifications.

## [4.13.2-26.2] - 2026-06-28 (Broken - Skip)
### Fixed
- **Jade Health Override Priority**: Fixed an issue where the custom Jade health provider was executing before Jade's default provider, causing the accurate dynamic hearts to be immediately overwritten by the default vanilla capped hearts. The custom provider now correctly replaces the default health component.

## [4.13.0-26.2] - 2026-06-28 (Broken - Skip)
### Added
- **Jade Mod Integration**: Added official support for the Jade HUD tooltip mod.
  - Now correctly overrides Jade's default health renderer to display the accurate dynamic health/max health of wolves, accounting for Better Dogs' genetic scaling and personality traits.
  - Adds a custom tooltip line for tamed dogs showing their hidden "Favorite Treat" if the feature is enabled.
  - The plugin is built as an isolated, optional dependency; the mod will not crash on clients/servers running without Jade.

## [4.12.1-26.2] - 2026-06-27
### Fixed
- **Localization & GUI Quality Polish**: Cleaned up duplicate blocks and malformed JSON syntax in `id_id.json`. Added missing config keys for Creeper Avoidance and Pack Flanking Tactics in `en_us.json`.
- **Tooltip Precision**: Corrected default value descriptions for Aggressive HP (from `20` to `-10`) and Pacifist HP (from `0` to `20`) in both language sheets to match the active code constants.

## [4.12.0-26.2] - 2026-06-27
### Added
- **Favorite Treats Mechanic**: Tamed wolves now have a hidden favorite treat determined dynamically by their UUID. Feeding them their favorite treat fully heals them, grants Regeneration II, and triggers their morning Zoomies.
- **Dynamic Config/GameRules**: Toggles for the mechanic are integrated in Cloth Config GUI and registered via the `bd_favorite_treats` gamerule.

## [4.11.0-26.2] - 2026-06-27
### Added
- **Nemesis (Grudge) System**: When a tamed wolf dies to a hostile mob (LivingEntity), all other tamed wolves of the same owner within a 32-block radius form a 'Nemesis' grudge against that mob type.
  - While holding a grudge, wolves will actively seek out and attack any mob of the nemesis type.
  - When attacking their nemesis, wolves gain Strength and Speed buffs and emit angry particles.
  - Grudges last for a configurable duration (default: 3 in-game days).
  - Can be toggled via GameRule `bd_nemesis_system` (default: true).
  - Duration is configurable via GameRule `bd_nemesis_duration_days` (default: 3).

## [4.10.0-26.2] - 2026-06-26
### Added
- **True Pack Hunting Tactics (Flanking)**: When a pack of wolves engages a target, only the leader will attack directly. The follower wolves will now perform flanking maneuvers, dynamically pathfinding around the target to strike from the sides or behind. This makes packs significantly deadlier and more visually coordinated without causing server strain.
  - Can be toggled off via the `bd_pack_flanking_tactics` GameRule (defaults to `true`).

## [4.9.8-26.2] - 2026-06-26
### Fixed
- Fixed an issue where adopted wolves would retain their old pack leader UUID, guard mode status, and old grudges after being claimed by a new owner.

## [4.9.7-26.2] - 2026-06-23
### Changed
- **Merit-Scaled Gifting Chances**: Rebalanced how gift chances are evaluated to create a true daily chance. The dog's chance to give a gift now scales linearly based on its interaction merits, from the base percentage (default 1%) at the minimum threshold, up to a 100% chance when reaching maximum interaction merits (10,000).

## [4.9.6-26.2] - 2026-06-23
### Changed
- **Gifting Threshold Balancing**: Increased the default required interactions for a gift (`bd_gift_feed_threshold`) from `3` to `10`. Increased the maximum hard-limit of accumulated interaction merits from `10` to `10,000`, allowing players who configure high thresholds to actually reach them.

## [4.9.5-26.2] - 2026-06-23
### Changed
- **Gifting Demerits Refinement**: Refined the demerit system. Intentional attacks (shift-attacking) still fully reset the wolf's gifting merits to 0. Accidental attacks (normal hitting) when `bd_demerit_accidental_attacks` is enabled now only reduce the merit count by 1 instead of a full reset.

## [4.9.4-26.2] - 2026-06-23
### Fixed
- **Gifting Demerits on Owner Attacks Fix**: Fixed a bug where any accidental attack on a tamed dog would reset its interaction merits. Players must now be sneaking/crouching while attacking their dog to trigger the demerit/reset logic.

## [4.9.3-26.2] - 2026-06-23
### Added
- **Configurable Interaction Cooldown**: Replaced the hardcoded 5-second cooldown on free player-wolf interactions (like sitting/standing) with a configurable GameRule `bd_gift_interaction_cooldown` (default: 100 ticks / 5 seconds) to prevent exploit spamming.

## [4.9.2-26.2] - 2026-06-23
### Added
- **Gifting Demerits on Owner Attacks**: Attacking your own tamed wolf now immediately resets its interaction/gifting merits to `0`. The wolf will not bring morning gifts again until positive interactions are rebuilt back to the threshold. This demerit applies to all attacks (including sneak-attacks) even if friendly fire protection is enabled and blocks the actual damage.

## [4.9.1-26.2] - 2026-06-23
### Added
- **Expanded Gifting Interaction Gates**: Expanded the wolf gifting gates to track all forms of positive player-wolf interaction instead of just feeding.
  - Interacting to sit/stand, calming down, toggling guard mode, stick commands, and putting up for adoption now count towards the gift eligibility threshold.
  - Implemented a 5-second (100 ticks) anti-spam cooldown for free interactions to prevent exploit gating, while feeding actions bypass the cooldown and always count.
  - Rebranded the GameRule to **Gift Interaction Threshold** (`bd_gift_feed_threshold`) and updated localizations.

## [4.9.0-26.2] - 2026-06-23
### Added
- **Feeding-Gated Wolf Gift System**: Tamed wolves will now only bring morning gifts if they are fed regularly by their owner.
  - Added namespaced GameRule `vanilla-outsider-better-dogs:bd_gift_feed_threshold` (default: 3) to configure the feeding threshold.
  - Feeding a tamed wolf its favorite food increments its persistent feed count (capped at 10 to prevent overflow).
  - Spawning a morning gift consumes the threshold amount of feeds, triggers a happy whine sound effect, and displays an action bar notification to the owner.

## [4.8.13-26.2] - 2026-06-21
### Added
- **Wolf Spawn Multiplier**: Introduced a configuration setting and game rule `bd_wolf_spawn_multiplier_percent` (default: 1.5x) to dynamically adjust wolf spawning weights in biome settings, making them spawn more commonly.

## [4.8.12-26.2] - 2026-06-21
### Changed
- **Tamed Wolf Wander Restriction**: Replaced the owner-unaware vanilla wandering behavior (`WaterAvoidingRandomStrollGoal`) with a personality-based `TamedWanderNearOwnerGoal`. Wolves now stay closer to their owner: Aggressive (max 14 blocks), Normal (max 8 blocks), and Pacifist (max 4 blocks), preventing them from drifting too far away.
- **Dynamic Wander Scaling**: Integrated the wander radius with the follower spacing offset. In larger packs, the wander boundaries expand dynamically to prevent clumping and overcrowding.

## [4.8.11-26.2] - 2026-06-12
### Changed
- **Guard Alarm Point & Freeze**: Pacifist dogs will now stand up, freeze in place, and look directly at the closest detected hostile mob during sentinel alarms.

## [4.8.10+A-26.2] - 2026-06-12
### Changed
- **Directional Guard Alarm Particles**: Modified Pacifist watchdog alarm particles to shoot forward in a 60-degree cone aligning with the wolf's looking direction, spawned at head/mouth level.

## [4.8.9+A-26.2] - 2026-06-12
### Added
- **Configurable Guard Particle Density**: Introduced a client/server configuration setting `guardParticleDensity` inside a new "Visual & Performance Options" Cloth Config screen tab to scale or toggle guard alert particles (`high` = 12, `medium` = 6 [default], `low` = 3, `off` = 0).

## [4.8.8+A-26.2] - 2026-06-12
### Changed
- **Guarding Pacifist Particles**: Redesigned the alert particles for guarding Pacifist dogs to spawn a highly noticeable, expanding horizontal circular ring of 12 pure red dust particles (0xFF0000) using trigonometric direction vectors and client-side outward velocity.

## [4.8.7+A-26.2] - 2026-06-12
### Changed
- **Rare Pack-Wide Howling**: Gated howling chance checks to run only once every 100 ticks (5 seconds) and implemented shared pack-wide cooldowns (10 minutes) when one wolf initiates a pack howl.

## [4.8.6+A-26.2] - 2026-06-12
### Changed
- **Red Alert Dust Particles**: Replaced the default music note particles with an optimized horizontal burst of 6 red dust particles (`DustParticleOptions`), sent in a single network packet to avoid performance overhead while providing a clear threat/warning color.

## [4.8.5+A-26.2] - 2026-06-12
### Changed
- **Sound Variant Weighted Lottery**: Transitioned the sound variant selection from a strict deterministic max-score model to a UUID-seeded weighted lottery. This ensures every individual dog gets a highly unique, stable, and randomish sound variant that aligns with its personality and genetics.

## [4.8.4+A-26.2] - 2026-06-12
### Added
- **Dynamic Sound Variant Mapping**: Implemented a mathematical scoring system that deterministically assigns the wolf's sound variant (Classic, Big, Cute, Puglin, Angry, Grumpy, Sad) based on its rolled genetics, scale, and personality type rather than relying on vanilla's random selection.

## [4.8.3+A-26.2] - 2026-06-11
### Changed
- **Pacifist Guard warning sound**: Changed the sentinel alarm sound for Pacifist dogs in Guard Mode from a whine to the specific growl sound corresponding to the wolf's sound variant itself (pitch adjusted to 1.0f).

## [4.8.2+A-26.2] - 2026-06-11
### Fixed
- **Low-Health Whimpering Scaling**: Fixed low-health whining behavior by replacing the vanilla absolute health `< 20.0f` threshold check with a dynamic check scaled to the dog's maximum health (`< 50% max health`). This prevents dogs with low max health (such as Runts and puppies) from whimpering constantly when fully healthy.

## [4.8.1+A-26.2] - 2026-06-11
### Fixed
- **Classloader Mixin Shadow Crash**: Fixed a startup and runtime crash when trying to locate `@Shadow Mob mob` field inside `WalkNodeEvaluatorMixin` by making the mixin class inherit from `NodeEvaluator` directly.

## [4.8.0+A-26.2] - 2026-06-11 [DEPRECATED - CRITICAL BUG]
> [!WARNING]
> This version contains a classloader startup crash (InvalidMixinException) in `WalkNodeEvaluatorMixin` and is deprecated. Use `4.8.1+A-26.2` instead.

### Added
- **Custom Howling Sound Restoration**: Packaged and restored the deleted vanilla `Wolf_howl1.ogg` and `Wolf_howl2.ogg` sound files into the mod assets.
- **Custom Sound Event Registration**: Registered the `betterdogs:entity.wolf.howl` sound event.
- **AI Group Howl update**: Updated `GroupHowlGoal` to play the authentic howling sound effects instead of the baby whine fallback.

## [4.7.6+A-26.2] - 2026-06-11 [DEPRECATED - CRITICAL BUG]
> [!WARNING]
> This version contains a classloader startup crash (InvalidMixinException) in `WalkNodeEvaluatorMixin` and is deprecated. Use `4.8.1+A-26.2` instead.

### Added
- **Alternative Pathfinding on Push**: Added checks to see if another path exists to get to the target area (even if longer) before giving up and halting navigation.

## [4.7.5+A-26.2] - 2026-06-11
### Added
- **Ground Food Refusal Trait**: Added a feature where some tamed-from-birth dogs (bred from parents, not wild-tamed) persistently refuse to eat food from the ground.
  - Adds `bd_enable_refuse_ground_food` GameRule and toggle setting to enable/disable the feature globally.
  - Adds `bd_refuse_ground_food_chance` GameRule and setting to configure the percentage chance (default: 30%) that a puppy receives the refusal trait at birth.

## [4.7.4+A-26.2] - 2026-06-11
### Added
- **Wolf-on-Wolf Collision Push Safety**: Added cooperative collision handling between tamed wolves.
  - Tamed wolves colliding with a sitting, guarding, or endangered dog (facing a cliff/lava/magma/fire hazard) will immediately halt their navigation path.
  - A patience timer (`pushWaitTimer` of 60 ticks / 3 seconds) is applied to the pushing dog to prevent continuous pathfinding attempts and jittering.
  - Player-to-dog pushes are exempted to prevent player blocking.

## [4.7.3+A-26.2] - 2026-06-11
### Added
- **Magma Block Avoidance**: Added `Blocks.MAGMA_BLOCK` to the hazard list in `AvoidHazardsGoal`, preventing wolves from walking onto magma blocks.
- **Lava Safety Verification**: Confirmed that any level of lava (source or flowing) is avoided.

## [4.7.2+R-26.2] - 2026-06-06
### Summary
- **Release Promotion**: Promoted the Select-and-Ride Dog Command System and Cloth Config GUI warning layout fixes to a stable production Release.

## [4.7.1+A-26.2] - 2026-06-06
### Added
- **Optional GUI Integration**: Upgraded config screen classloading to resolve via `GuiHelper` in `DasikLibrary` 1.8.2.
- **UI Warning Polish**: Removed repetitive hover tooltips from config options and moved them to category headers.

## [4.7.0+A-26.2] - 2026-06-06
### Added
- **Select-and-Ride Dog Command System**: Command dogs to sit/ride in vehicles, mounts, and modded chairs using a Stick tool.
- **Visual Sitting Pose**: Force visual sitting pose on wolves while riding any vehicle.
- **Dismount Controls**: Dismount using Stick (dismount & stand) or Empty-hand Shift+Right-click (dismount & sit).
- **Optional Unrestricted Riding GameRule**: Added `betterdogs:allow_unrestricted_dog_riding` to allow dogs to ride any entity (e.g. Ghasts).

## [4.6.26+A-26.2] - 2026-06-06
### Added
- **Unrelated Mate Prioritization**: Added `BreedGoalMixin` to prioritize breeding with unrelated wolves in range. If no unrelated wolves are available, they will fallback to related ones.

## [4.6.25+R-26.2] - 2026-06-06
### Summary
The **"Stability & Loot API Alignment Release"** promotion.
- **Release Promotion**: Promoted the genetics calculation engine integration and dependency constraints updates to a stable production Release.
- **Dependency Alignment**: Compiled against `DasikLibrary` `v1.8.1`.

## [4.6.24+A-26.2] - 2026-06-05
### Summary
The **"Library Dependency Realignment"** update.
- **Dependency Constraint Update**: Updated `fabric.mod.json` depends block to require `"dasik-library": ">=1.8.0"` (preventing startup crashes with older library versions due to missing genetics API classes).
- **Library Realignment**: Re-aligned and compiled against `DasikLibrary` `v1.8.1`.

## [4.6.23+A-26.2] - 2026-06-04
### Summary
The **"Genetics Library Migration"** update. Abstracts and migrates all selective breeding, inbreeding, and outcross recovery calculations to `DasikLibrary` `v1.8.0`.
- **Genetics Centralization**: Migrated `WolfPersistentData` genetics fields to the library-provided `dasik-library:genetics` attachment type.
- **Thin Mod Architecture**: Delegated breeding calculations, inbreeding checks, and outcross recovery rules to `GeneticsEngine.inheritGenetics`, keeping the mod lightweight and clean.

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


## [4.0.0+A-26.2] - 2026-05-27
### Summary
The Minecraft **26.2 Pre-Release 1 Port** and **Compatibility Upgrade**.
- **Minecraft 26.2 Port**: Ported the codebase to Minecraft `26.2-pre-1` and aligned with Fabric API `0.149.2`.
- **Entity Registry Relocation**: Refactored entity type references to use the new `EntityTypes` registry class, replacing legacy `EntityType` references (e.g. `EntityType.WOLF` to `EntityTypes.WOLF`) to prevent compilation and runtime errors.
- **Pre-Release & Future Compatibility**: Updated `fabric.mod.json` dependency constraints to `"minecraft": ">=26.2-"` (wildcard range) to allow the mod to load on both `26.2` pre-releases and the final/future `26.2` releases.
- **Gradle & Toolchain Updates**: Configured the build toolchain to compile using JDK 25 and added automatic local Maven repository lookup for locally compiled dependencies.

## [3.7.1+A-26.1.2] - 2026-05-26 (Skip) immidiet to 4.0.0 with the 1.7.4
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




