# Changelog - Vanilla Outsider: Better Dogs (MC 1.21.1)

## [1.0.35+1.21.1] - 2026-08-15
### Added
- 🧬 **Genetic Personality Inheritance**:
  - Implemented weighted parental trait inheritance when breeding tamed wolves (`WolfGeneticsHelper`).
  - **Same-Trait Parents**: 80% chance to inherit parent personality (`bd_breed_same_chance`), with 10% mutation chance (`bd_breed_same_other_chance`).
  - **Mixed-Trait Parents**: 40% Parent A (`bd_breed_mixed_dominant_chance`), 40% Parent B (`bd_breed_mixed_recessive_chance`), 20% Normal dilution (`bd_breed_diluted_normal_chance`).
  - Added automated unit test suite `GeneticsInheritanceTest`.

## [1.0.34+1.21.1] - 2026-08-15
### Added
- 💖 **Personality Dynamic Stat Scaling & Attribute Modifiers**:
  - Dynamically recalculates max health, attack damage, and base movement attributes when personalities are assigned or loaded (`WolfPersonalityStatHelper`).
  - **Aggressive Dogs**: Enhanced attack power (`bd_aggro_damage`), speed scaling (`bd_aggro_speed_percent`), and configured health (`bd_aggro_health`).
  - **Pacifist Dogs**: High-vitality guardians with increased max health (`bd_paci_health`) and gentle damage (`bd_paci_damage`).
  - Added automated unit test suite `PersonalityStatScalingTest`.

## [1.0.33+1.21.1] - 2026-08-15
### Added
- 🚀 **Fast-Travel Sprint Catchup & Dimension Teleport Sync**:
  - Implemented dynamic catch-up sprint speed scaling (up to 2.0x base speed) whenever owners travel rapidly on mounts (horses, donkeys, camels), boats, minecarts, elytra, or high-speed sprinting (`bd_fast_travel_catchup`).
  - Added interdimensional portal teleport sync helper `WolfCatchupHelper.syncOwnerDimensionTeleport` (`bd_sync_owner_teleport`).
  - Added automated unit test suite `FastTravelCatchupTest`.

## [1.0.32+1.21.1] - 2026-08-15
### Added
- 🛡️ **Friendly Fire Protection & Owner Damage Safeguards**:
  - Intercepts and completely cancels accidental player weapon sweeps, stray projectiles/arrows, and direct owner melee hits against owned dogs.
  - Intercepts and cancels friendly infighting between dogs belonging to the same owner.
  - Fully configurable via the namespaced `bd_friendly_fire` GameRule (defaults to `false` = protected; setting `true` enables vanilla damage).
  - Added automated unit test suite `FriendlyFireTest`.

## [1.0.31+1.21.1] - 2026-08-15
### Added
- 😡 **3-Day Nemesis Memory & Pack Vendetta Combat AI**:
  - Tamed wolves now remember and avenge fallen pack mates or owners killed by hostile mobs.
  - When a tamed wolf dies in combat, the killer's `EntityType` is broadcast to all pack wolves belonging to the same owner within 64 blocks, persisting for 3 in-game days (72,000 game ticks, controlled by `bd_nemesis_duration_days`).
  - Added `WolfNemesisTargetGoal` prioritizing the pack's active Nemesis mob type at highest targeting priority.
  - Added automated unit test suite `NemesisGrudgeTest`.

## [1.0.30+1.21.1] - 2026-08-15
### Fixed
- 🥩 **Full-HP Wolf Breeding & Food Refusal Fix**:
  - Corrected `DogTreatHelper.shouldRefuseFood` logic so tamed wolves at full health can still be fed meat to enter breeding love mode (`canFallInLove()`) or grow baby puppies (`isBaby()`).
  - Food refusal now strictly activates when wolves are adult, at full HP, and unable to breed (already in love or on breeding cooldown).

## [1.0.29+1.21.1] - 2026-08-15
### Added
- 📯 **Acoustic Goat Horn Commands & Pack Horn Overrides**:
  - Implemented tactical acoustic pack commanding via Goat Horn variants across a 64-block range (`bd_horn_command_range`).
  - **Ponder Horn**: Emits an Assemble/Rally signal directing following dogs to path to the sounding player's location (`WolfHornGoal`).
  - **Yearn Horn**: Issues a Stand Up/Resume Follow order commanding sitting dogs to immediately stand and follow.
  - **Sing Horn**: Issues a Hold/Sit command ordering active dogs to sit and clearing all combat targets.
  - **Feel Horn**: Emits a 30-second Pacifist/Calm override (`bd_horn_override_duration`) suppressing aggressive hostile target acquisition.
  - **Seek Horn**: Directs the pack to hunt down and attack the owner's crosshair target or nearest hostile monster.
  - Added automated unit test suite `GoatHornCommandTest`.

## [1.0.28+1.21.1] - 2026-08-15
### Added
- 🖐️ **Shift + Right-Click Petting & Soothe Calming Mechanic**:
  - Owners can now crouch and right-click their tamed dogs with an empty hand to gently pet them.
  - Petting calms active anger, emits comforting heart and note particles with whimper/pant vocalizations, and soothes thunderstorm anxiety for 10 minutes (12,000 game ticks).
  - Added automated unit test suite `PettingSootheTest`.

## [1.0.27+1.21.1] - 2026-08-15
### Added
- ⚡ **Thunderstorm Anxiety & Shelter Seeking AI**:
  - Tamed dogs exhibit realistic anxiety during thunderstorms, whimpering and actively seeking covered indoor shelter blocks.
  - Added personality-based storm anxiety scaling (Pacifists experience 3x anxiety, Aggressive dogs remain fearless on guard).

## [1.0.26+1.21.1] - 2026-08-15
### Added
- 🥩 **Food Refusal & Favorite Treats Begging AI**:
  - Tamed dogs now refuse player-fed food when at full health with a head-tilt animation and whimper response.
  - Dogs discover a permanent favorite treat affinity upon feeding, granting +100% bonus healing and enhanced begging behavior.

## [1.0.25+1.21.1] - 2026-08-15
### Added
- 🍖 **Autonomous Ground Food Foraging & Healing**:
  - Injured tamed dogs automatically detect and seek dropped meat items within 10 blocks to heal themselves.
  - Added food item classification respecting raw and cooked food GameRule toggles.

## [1.0.24+1.21.1] - 2026-08-15
### Added
- 🐺 **Tactical Pack Flanking Encirclement AI**:
  - Implemented multi-angle pack encirclement AI allowing dogs targeting the same entity to spread out into distinct flanking slots rather than clumping.
  - Added line-of-sight and clear terrain raycasting for tactical approach maneuvers.

## [1.0.23+1.21.1] - 2026-08-15
### Changed
- 🛡️ **Single-Purpose Architecture Alignment**:
  - Decoupled cliff edge safety and thermal hazard helpers into dedicated single-responsibility classes (`WolfCliffSafetyHelper` and `WolfHazardHelper`).

## [1.0.22+1.21.1] - 2026-08-15
### Added
- 🔥 **Emergency Thermal Safety & Hazard Evasion**:
  - Implemented automatic evasive backstep when standing within 1 block of Lava, Fire, Magma, or lit Campfires.
  - Implemented autonomous water-seeking extinguish response when burning.
  - Added automated test suite `HazardReactionTest`.

## [1.0.21+1.21.1] - 2026-08-15
### Added
- 🔥 **Active Hazard Detour Navigation**:
  - Implemented `AvoidHazardsGoal` inspecting forward path nodes and intercepting navigation when trajectories lead into Lava, Fire, Magma, or lit Campfires.
  - Prioritized hazard safety at priority 2 in wolf goal selection.
  - Added automated test suite `HazardDetourTest`.

## [1.0.20+1.21.1] - 2026-08-15
### Added
- 🔥 **Thermal Hazard Identification Utility**:
  - Implemented `WolfHazardHelper` providing centralized thermal scanning and classification for Lava, Fire, Soul Fire, Magma Blocks, and lit Campfires (`CampfireBlock.LIT`).
  - Added fast radius-based proximity checks for hazard avoidance navigation.
  - Added automated test suite `HazardDetectionTest`.

## [1.0.19+1.21.1] - 2026-08-14
### Added
- 🏔️ **Cliff Edge Fall Safety Navigation**:
  - Implemented dynamic drop height probing (`WolfSafetyMixin` and `WolfSafetyHelper`) that halts forward navigation, zeroes horizontal momentum, and triggers sneak stance when approaching lethal ledge drops ($>3$ blocks down).
  - Implemented push collision protection (`WolfPushMixin`) preventing entities and mobs from shoving sitting or standing dogs over cliff edges or into hazards.
  - Implemented combat ledge retreat: wolves break pursuit if their combat target plunges over a cliff.
  - Controlled by GameRule `bd_cliff_safety` (default: `true`).

## [1.0.18+1.21.1] - 2026-08-14
### Changed
- 🎨 **Bold Yellow Category Header**: Styled the `BETTER_DOGS` GameRules category name with bold yellow formatting (`ChatFormatting.BOLD, ChatFormatting.YELLOW`) to seamlessly match vanilla category headers in the Edit Game Rules UI screen.

## [1.0.17+1.21.1] - 2026-08-14
### Added
- 🏷️ **Dedicated 'Better Dogs' GameRules Category**:
  - Registered custom `BETTER_DOGS` category via `CustomGameRuleCategory` so all 80+ mod GameRules appear in their own section in the in-game world creation and edit gamerules menu.
  - Aligned all `en_us.json` GameRule titles and tooltip description keys (`gamerule.bd_*`).

## [1.0.16+1.21.1] - 2026-08-14
### Added
- 🤺 **Pacifist Defensive Retaliation AI (`PacifistRevengeGoal`)**:
  - Pacifist wolves now defensively counter-attack hostiles that deal damage directly to the wolf or its owner (`getLastHurtByMob()`).
  - Strict non-aggression: Pacifist wolves will not assist offensive owner attacks and will never target friendly puppies or creepers.

## [1.0.15+1.21.1] - 2026-08-14
### Added
- ⚙️ **80+ GameRules Registry Foundation**:
  - Implemented `BetterDogsGameRules` registering all 80+ namespaced GameRules via Fabric API `GameRuleRegistry.register("bd_*", ...)`.
  - Added NPE-safe static helper accessors `BetterDogsGameRules.getBoolean()` and `BetterDogsGameRules.getInt()`.
  - Added automated test suite `BetterDogsGameRulesTest` asserting registry keys and null-safety.

## [1.0.14+1.21.1] - 2026-08-14
### Changed
- **NBT & DNA Determinism Alignment**: Harmonized `WolfPersistentData` constants and deterministic DNA seed salt (`^ 5829103L`).
- **Exact Minecraft Target Constraint**: Updated `fabric.mod.json` `minecraft` dependency bound to target exact version `"minecraft": "1.21.1"`.

## [1.0.13+1.21.1] - 2026-08-14
### Added
- ⚔️ **Combat AI & Target Selection Mixins**:
  - `OwnerHurtTargetGoalMixin`: Enforces strict Pacifist combat ethics (Pacifist dogs never initiate or assist offensive player attacks; non-aggressive puppies skip offensive targets).
  - `OwnerHurtByTargetGoalMixin`: Implements the domestic Mercy Rule (adult dogs will not attack pet puppies even if the owner took damage from puppy discipline).
  - `HurtByTargetGoalMixin`: Silences pack-wide alarm broadcasts during domestic puppy scuffles.

## [1.0.12+1.21.1] - 2026-08-14
### Added
- 🖼️ **Mod Icon Asset**: Deployed official `icon.png` (235 KB) into `assets/vanilla-outsider-better-dogs/` and registered it in `fabric.mod.json`.
- 📜 **License Packaging**: Embedded GNU GPLv3 `LICENSE` file into release JAR distribution.
- 🎵 **Sound Resources & Audio**: Added `sounds.json` and custom wolf howling audio tracks (`howl1.ogg`, `howl2.ogg`).
- 🌐 **Base Localization**: Deployed `lang/en_us.json` resource bundle.
- 📦 **Dependency Declaration**: Added `"fabric-api": "*"` explicitly to `fabric.mod.json`.

## [1.0.11+1.21.1] - 2026-08-14
### Added
- 💣 **`FleeCreeperGoal` (100% 26.2 Parity)**: Tamed wolves detect ignited or swelling Creepers within 10 blocks and sprint away at `1.5x` speed with smoke particle trails.
- 🩹 **`WolfFleeLowHealthGoal` (100% 26.2 Parity)**: Wolves below 30% health automatically retreat from combat to preserve life.

## [1.0.10+1.21.1] - 2026-08-14
### Changed
- **Wolf Movement & Sprint Speed Boost**: Upgraded `PersonalityFollowOwnerGoal` base follow speed modifier from `1.0` to `1.25` (fast trot), with `1.35x` sprint catch-up multiplier when > 8 blocks away.

## [1.0.9+1.21.1] - 2026-08-14
### Fixed
- **`fabric.mod.json` `${version}` Expansion Fix**: Added `processResources` property expansion block into `build.gradle`.

## [1.0.8+1.21.1] - 2026-08-14
### Fixed
- 💥 **Taming Particle Level Crash Fix (`NoSuchMethodError`)**: Replaced `wolf.level()` with `wolf.getCommandSenderWorld()`.

## [1.0.7+1.21.1] - 2026-08-14
### Fixed
- 💥 **`TamedWanderNearOwnerGoal` Crash Fix (`NoSuchMethodError`)**: Replaced `owner.position()` with primitive getters.

## [1.0.6+1.21.1] - 2026-08-14
### Fixed
- 💥 **Taming Crash Fix (`NoSuchMethodError`)**: Replaced `wolf.level()` with `wolf.getRandom()`.

## [1.0.5+1.21.1] - 2026-08-14
### Added
- **Proactive `AggressiveTargetGoal` (`NearestAttackableTargetGoal<Monster>`)**.

## [1.0.4+1.21.1] - 2026-08-13
### Fixed
- **Entity AI Goal Registration (`Wolf.registerGoals`)**.

## [1.0.3+1.21.1] - 2026-08-13
### Fixed
- **Mixin Injection Safety**.

## [1.0.2+1.21.1] - 2026-08-13
### Added
- **Personality Combat AI & Target Selection (`PersonalityTargetGoal`)**.

## [1.0.1+1.21.1] - 2026-08-13
### Added
- **Personality Follow & Teleport AI System (`PersonalityFollowOwnerGoal`)**.

## [1.0.0+1.21.1] - 2026-08-13
### Added
- Initial subproject setup for Minecraft 1.21.1.
