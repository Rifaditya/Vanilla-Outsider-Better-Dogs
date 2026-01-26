# Changelog

## v3.1.17 (Anti-Crowding Removal)

- **Removed**: The "Anti-Crowding System" (`PackSeparationGoal`) has been removed entirely. Wolves will no longer artificially push each other away, reverting to standard vanilla grouping behavior.
- **Config**: Removed `packSeparationRadius`, `packSeparationInflation`, `packSeparationSpeed`, and `enablePackSeparation` from `betterdogs.json`.

## v3.1.16 (Stable Hotfix: Crash Fix)

- **Fix**: Replaced `WolfMixin` targeting logic with `WolfMobMixin` to robustly intercept `setTarget` without causing transformation errors on startup. This resolves the `MixinApplyError` seen in v3.1.15.

## v3.1.15 (Broken Build - Startup Crash)

- **Hotfix Attempt**: Attempted to fix startup crash but introduced a new Mixin transformation error due to target class mismatch. **DO NOT USE**.

## v3.1.14 (Better Teleportation & Dynamic Ranges)d

### Added

- **Dynamic Simulation Capping**: Aggressive dog follow and detection ranges now automatically scale to stay within the server's simulation distance, preventing them from being left behind in unloaded chunks.
- **Improved Teleportation (The "2x" Rule)**: Optimized follow logic so dogs prefer running back to the owner. They will only teleport if they fall behind by more than twice their follow start distance.

### Changed

- **Simplified Config**: Consolidated various personality-specific teleport settings into a single `teleportMultiplier` (default 2.0x).

## [3.1.13] - 2026-01-26

### Added

- **Ultraguard Sync**: Implemented a hardened persistence system with atomic writes, automatic merging, and legacy field purging.
- **Auto-Backups**: Config now clones to `betterdogs.json.bak` before any version-jump migrations.
- **Completed AI Migration**: Finalized the movement of all 30+ AI magic numbers to `BetterDogsConfig`.

### Changed

- Updated platform descriptions (Modrinth/CurseForge) with the new Config Mastery feature suite.

## [3.1.12] - 2026-01-25

### Added

- **Personality-Scaled Teleportation**: Aggressive dogs wander deeper (5x), Pacifists stay close (0.5x).
- **Config Persistence Protocol**: Fixed the reset-on-load bug and established new modder standards (`12_Config_AI_Standards.yaml`).
- **Emergency Restoration**: Recovered all AI implementation files and project properties after a critical data loss event.

## [3.1.11] - 2026-01-25

### Changed

- **Optimized Follow Behavior**: Dogs now try to run to the player when falling behind instead of immediately teleporting.
- **Improved Teleportation**: Increased the teleport distance by 1.5x (from 12 to 18 blocks) to give dogs more time to catch up naturally.
- **New Config**: Added `followCatchUpSpeed` (default: 1.5) to control how fast dogs run when they are far from the owner.

## [3.1.10] - 2026-01-25

### Added

- **New Feature: Pack Separation**
  - Dogs now respect each other's "personal space".
  - If too many dogs are crowded together, they will gently spread out to form a more natural pack layout.
  - Configurable radius (`packSeparationRadius`) and speed (`packSeparationSpeed`) in `betterdogs.json`.
- **Developer Protocol Update**: Enforced "Zero Technical Debt" policy for mod development.

## [3.1.9] - 2026-01-25

### Fixed

- **Critical Fix: Howl State Leak**: Fixed a major bug where wolves would stay in the sitting pose permanently after a Group Howl event. They now correctly restore their previous state (sitting or standing) when the event ends.
- **Maintenance**: Cleaned up code debt, removed redundant AI goals, and optimized imports across the entire AI package.

## [3.1.8] - 2026-01-25

### Fixed

- Fixed crash/build failure caused by incorrect `SoundEvents` usage for Howl event.
- Fixed `SmallFightGoal` incorrectly passing `Level` instead of `ServerLevel`.
- Implemented `WolfAccessor` to correctly access native wolf ambient sounds.
- Native API compliance for all 26.1 Snapshot methods.

- **New Feature: Individual DNA**
  - Every dog now has a unique "preference roll" (based on their UUID) that determines if they like specific social events.
  - This ensures that even two dogs with the same Personality will behave differently (e.g., one loves Zoomies, the other thinks it's undignified).

## [v3.1.7] - 2026-01-25

- **New Feature: Individual DNA**
  - Every dog now has a unique "preference roll" (based on their UUID) that determines if they like specific social events.
  - This ensures that even two dogs with the same Personality will behave differently (e.g., one loves Zoomies, the other thinks it's undignified).

- **New Feature: Zoomies**
  - Wolves now have a chance to get the "Zoomies" (hyperactive running) in the morning or after rain.
  - **Triggers**: Morning (10% chance), Wet/Rain (20% chance).
  - **Acceptance**: Babies (Always), Normal (50%), Pacifist (20%), Aggressive (Never).

- **New Feature: Group Howl**
  - Wolves may start a group howl at night.
  - **Triggers**: Full Moon (100% chance), Regular Night (5% chance).
  - **Leader/Follower**: One wolf starts, nearby pack members join in if they pass their DNA check.

## [v3.1.6] - 2026-01-25

- **New Feature: Play Fighting**
  - Large packs of aggressive wolves (>10) will now occasionally "play fight" for 10 seconds.
  - Optimized with staggered checks and sampling to prevent lag.
  - Includes safety hooks to ensure no wolf ever dies during these scuffles (1 HP cap).
- **Refactor**: Renamed internal event classes to `*DogEvent` for clarity.

## [3.1.5] - 2026-01-25

### 🐞 Bug Fix

- **Blood Feud Probability**: Fixed a critical math error where the Blood Feud chance was being calculated as 100% instead of the intended 5%. The probability is now correctly derived from the config percentage.

### ✨ New Feature

- **Configurable Retaliation**: Added `babyRetaliationChance` to `config/betterdogs.json`. You can now adjust the probability of a baby wolf biting back (default: 75%).

## [3.1.4] - 2026-01-25

### 🚀 Optimization: The "Snitch" System (Adult Correction)

- **Event-Driven AI**: Completely rewrote the "Adult Correction" system.
  - **Old Way**: Adults constantly scanned their surroundings every tick looking for misbehaving babies (High CPU).
  - **New Way**: When a baby bites the owner, it literally "snitches" on itself by broadcasting a `CorrectionEvent` to the nearest Aggressive Adult.
- **The One Bite Rule**: Because this is now controlled by a strict event timer (5 seconds), the adult intervention is guaranteed to be a single, precise disciplinary strike rather than a protracted brawl.

### ❓ FAQ: Where is v2?

**I skipped v2.0 entirely.** The transition from the 1.x "Dual Channel" architecture to the 3.x "Event-Driven Scheduler" was so fundamental—effectively rewriting the entire AI core—that I felt it deserved a generational leap. v2.0 represents the internal "dark ages" of prototyping that paved the way for the stable v3.0 era.

## [3.1.3] - 2026-01-25

### 🐞 Bug Fix

- **The Paradox Fix**: Fixed a logical contradiction in `BabyBiteBackGoal` where the AI would refuse to run because "Social Mode was active", ignoring the fact that the Retaliation Event *is* what activated Social Mode. The baby will now correctly realize "Oh wait, I'm supposed to be angry" and actually bite you.

## [3.1.2] - 2026-01-25

### 🐞 Bug Fix

- **Log Spam (The Infinite Loop)**: Fixed a logic error in `WolfCombatHooks` that was blindly re-injecting the `RetaliationEvent` every single tick when an aggressive baby was hurt, causing massive console spam. Added a check to ensure we don't inject if the event is already active.
- **Console Hygiene**: Silenced several `INFO` level logs in the Scheduler and Event system to reduce noise in production.

## [3.1.1-snapshot] - 2026-01-25

### 🐞 Bug Fixes

- **Retaliation Targeting**: Fixed a critical issue where aggressive baby wolves would start the retaliation event but refuse to bite their owner due to vanilla friendly-fire protections. `WolfCombatHooks` now explicitly authorizes these attacks.

## [3.1.0-snapshot] - 2026-01-25

### 🏗 Architecture Refactor: The "Anti-Bloat" Update

- **Code Cleanup**: Split the massive `WolfMixin` (God Class) into targeted helper/logic classes.
  - `WolfStatManager`: Handles all attribute modifiers and personality stats.
  - `WolfCombatHooks`: Handles damage protection, friendly fire, and retaliation logic.
  - `WolfParticleHandler`: Handles taming and emotion particles.
- **Stability**: This makes the codebase significantly easier to maintain and less prone to "spaghetti code" bugs in the future.

## [3.0.0-snapshot] - 2026-01-25

### 🚀 Major Feature: Centralized Wolf Scheduler (V3.0)

- **System-Wide Intelligence**: Moved from a "Per-Wolf" ticking system to a **Global Scheduler**.
  - **Lazy Optimization**: The scheduler now only checks a few random wolves per tick, drastically reducing CPU usage for massive wolf packs.
  - **Simulation Distance**: The system intelligently ignores wolves in unloaded/non-ticking chunks.
- **Reactive Command Center**: The scheduler now supports **Behavior Injection**, allowing external events to immediately force strict behaviors.
  - **Retaliation V3**: When an Aggressive Baby Wolf is punched by its owner, the Command Center immediately injects a "Retaliation" event.
  - **Strict Control**: This event overrides all other AI for 5 seconds (100 ticks), forces a single bite, and then cleanly exits. This is the most robust implementation of the "One Bite" rule yet.

## [1.12.1] - 2026-01-25

### 🚀 New Feature: Wolf Scheduler (V3.0)

- **Long-Term AI**: Implemented a modular, persistent event scheduler for wolves.
- **Wanderlust Event**: Wolves now have a small daily chance (1%) to gain "Wanderlust", significantly increasing their roaming range for the day.
- **Architecture**: Built on the new `WolfEvent` and `ValueOutput` persistence system, ensuring stability and modularity.

### 🐛 Fixed

- **Runtime Crash**: Fixed a critical `InvalidInjectionException` caused by `WolfMixin`. Replaced faulty `@Inject` with `@Override` for the `setTarget` Gatekeeper logic.

## [1.12.0] - 2026-01-24

### 🧠 Major Refactor: The "Two-Brain" AI System

- **Master Channel (Personality)**: The default AI for hunting/defending.
- **Social Channel (Scenarios)**: A higher-priority channel for training, discipline, and play.
- **The Gatekeeper**: A strict new system that **silences** the Master Channel when the Social Channel is active.
  - Prevents "confused" AI (e.g., trying to hunt zombies while being disciplined).
  - Enforces strict "One Task at a Time" logic.
- **Failsafe System**: Social interactions now have a safety timer (3-5s) to prevent wolves from getting stuck in a passive state.

### Changed (v1.12.0)

- **Refactored AI Architecture:** Moved from specific boolean flags to a modular "Social Channel" system.
- **WolfMixin:** Implemented "Gatekeeper" logic to enforce AI exclusivity.

### Fixed (v1.10.2)

- Fixed bug where multiple adults would gang up on a baby.

### Fixed (v1.10.1)

- Fixed crash when baby wolf retaliates against owner.

### Fixed (v1.9.0)

- Optimized pathfinding needed for cliffs.

### Fixed (v1.8.5)

- Fixed issue with tamed wolves attacking each other.

### Fixed (v1.8.0)

- Fixed crash on server start.

### Fixed (v1.7.0)

- Fixed rare desync issue.

### Fixed (v1.6.0)

- Fixed compatibility with other mod loaders.

### Fixed (v1.5.0)

- Fixed memory leak in AI mapping.

### Fixed (v1.4.0)

- Fixed jittery movement in tight spaces.

### Fixed (v1.3.0)

- Fixed "spin" animation glitch.

### Fixed (v1.2.0)

- Fixed wolves ignoring "Stay" command.

### Fixed (v1.1.0)

- Fixed default aggressive behavior.

### Fixed (v1.0.0)

- Initial Release Fixes.

## [1.11.1] - 2026-01-24

### 🐞 Bug Fix: Baby Retaliation Logic

- **Fixed Baby Retaliation**: Found and removed a safety check that was accidentally deleting the target immediately after it was set. Aggressive babies will now correctly bite back when hit by their owner.

## [1.11.0] - 2026-01-24

### 🏗 Refactor: Dual-Channel Targeting

- **New "Social" Logic Channel**: Formalized the "Side-Channel" concept into a generic "Secondary Target" system (`betterdogs$secondaryTarget`).
  - **Main Channel** (`Target`): Used for lethal combat, hunting, and protection. Triggers growling, angry textures, and pack alerts.
  - **Secondary Channel** (`SecondaryTarget`): Used for social interactions (Discipline, Warning Bites). Invisible to the standard AI.
- **Adult Correction**: Adult wolves disciplining babies now use the Secondary Channel.
  - Adults no longer look "Angry" when correcting a baby.
  - Other wolves will no longer "gang up" on the baby, as the aggression is invisible to them.
- **Baby Retaliation**: Updated to use the generic Secondary Channel, maintaining the "One-Bite" behavior.

## [1.10.5] - 2026-01-24

### 🛠 Hotfix

- **Transient Target Switching**: Implemented a "Switch-Hit-Switch" logic for baby wolf retaliation.
  - When the baby decides to bite, it *briefly* switches its targeting channel to the main "Aggressive" channel (so the game engine registers a valid attack).
  - **Immediately** after the bite lands, it switches back to the "Passive" channel (Target = Null).
  - This ensures the bite actually happens (fixing the "no attack" bug) while still preventing the "Death Loop" (standard AI remains asleep).
- **Absolute Priority**: The Retaliation Goal is now **Priority 0** (highest possible), ensuring no other behaviors (like Panicking) can override it.

## [1.10.4] - 2026-01-24

### 🛠 Hotfix

- **Retaliation Reach Tuning**: Significantly increased the attack range for baby wolves during retaliation. They will now reliably land their "warning bite" even if they can't get perfectly close to the player.
- **Priority Logic**: Ensured that the "Blood Feud" logic (Fight to Death) always takes priority over the "Side-Channel Retaliation" logic (One Bite). If a feud starts, the baby will fight for real.

## [1.10.3] - 2026-01-24

### 🛠 Hotfix

- **Side-Channel Retaliation**: Completely refactored the "Bite Back" logic for baby wolves. It now uses a separate, hidden targeting system that keeps the standard AI (pack mentality, growling) completely asleep. This prevents death loops and ensures the baby strictly bites **once** before returning to normal.
- **Lazy AI**: The retaliation goal is now completely dormant until the exact moment of specific provocation, preventing any accidental overlap with other behaviors.

## [1.10.2] - 2026-01-24

### 🛠 Hotfix

- **Fight Club Logic**: Fixed a bug where the entire wolf pack would gang up on a baby wolf during disciplinary correction. Now, standard pack members will accurately recognize that "this doesn't concern them," ensuring fights are strictly 1v1 (Adult vs Baby or Feud Rivals).

## [1.10.1] - 2026-01-24

### 🛠 Hotfix

- **Infinite Attack Loop**: Fixed a critical bug where baby wolves would get stuck in an infinite loop of attacking the owner after the first bite. The "revenge memory" is now properly cleared after the single warning bite.

## [1.10.0] - 2026-01-24

### ⚔️ New Mechanics: Baby Training System

A complete overhaul of how baby wolves handle discipline, disputes, and aggression.

- **Baby Bite-Back**: Aggressive baby wolves will now bite their owner ONCE if hit (unless the owner is sneaking). This is a "teaching moment" - they are testing boundaries.
- **Adult Correction**: If an aggressive baby attacks the owner, an aggressive adult wolf (the "enforcer") will intervene and hit the baby ONCE.
- **Submissive State**: After being corrected by an adult, the baby becomes "Submissive" and cannot attack other pack members for a duration (reset on reload/death).
- **Natural Selection (Mischief)**: Aggressive babies have a daily 2.5% chance to randomly attack a nearby entity (even dangerous ones like Iron Golems) to learn valuable life lessons.
- **Blood Feud**: When an adult corrects a baby, there is a configurable 5% chance (default) of a "Blood Feud" triggering. If this happens, the two wolves will fight to the death.

### 🛠 Technical Changes

- **Cleanup**: Removed the old, buggy "Domestic Retaliation" and "Intervention" systems (strikes, lockouts) in favor of the new, cleaner event-driven system.
- **Data Persistence**: Moved all tracking (submissive state, blood feud targets) to the new attached data system, ensuring it persists across world saves.

## [1.09.018] - 2026-01-24

### Refined

- **Hit-First Rule Fix**: Adults now strictly wait until a baby actually hits the owner before intervening.
- **Domestic Safety Valve**: Dog-on-dog combat is now strictly non-lethal (capped at 0.5 hearts/1.0 HP).
- **Nuanced Lethality**: Player-on-dog damage is only lethal when sneaking (intentional kill). Regular hits are now non-lethal.
- **Fast Resolve & Target Lock**: Added a 50% "Mercy Chance" to clear targets upon ally hits, with a 2-second hard lock to prevent immediate re-engagement.
- **Emergency Calming**: Dogs drop into sitting pose and clear targets if reduced to critical health by an ally.
- **Hit Counter Reliability**: Fixed a bug where intervention hits were bypassing the 3-hit limit.

## [1.09.017] - 2026-01-24

### Added

- **Hard Ally-Hit Limit**: Any dog can now only hit an ally (other pets or the owner) up to 3 times.
- **5-Minute Reset**: The hit counter resets after 5 minutes of active gameplay.
- **Absolute Target Purge**: When a dog reaches the 3-hit limit, ALL its targets (hostile or not) are immediately cleared for maximum safety.

## [1.09.016] - 2026-01-24

### Fixed

- **Persistent Combat Safety**: Domestic strikes and combat lockout timers are now saved to the world data. Fights will no longer resume after reloading the world if a lockout was active.

## [1.09.015] - 2026-01-24

### Refined

- **Domestic Combat Lockout**: Implemented a hard 10-second cooldown after any domestic dispute. During this lockout, wolves are hardcoded to ignore attacks from the owner or ally dogs, finally putting an end to lethal combat loops.

## [1.09.014] - 2026-01-24

### Refined

- **Hit-First Rule**: Aggressive adults now only intervene if the owner was actually hit by the baby recently, preventing premature attacks.
- **Domestic Mercy Rule**: Regular pets (adults and babies) no longer defend the owner against their own pet babies. Disciplinary action by the owner is now a private matter.
- **Hard Combat Killswitch**: Reinforcing the logic that prevents babies from counter-attacking ally adults during domestic disputes.
- **Self-Defense Exception**: Prevented adults from using the intervention system to "correct" babies they are already fighting in self-defense.

## [1.09.013] - 2026-01-24

### ⚔️ AI Refinements

- **Domestic Conflict Killswitch**: Babies hit by an adult "enforcer" now have their target cleared immediately. This prevents the baby from stubbornly continuing the fight against the adult.
- **Improved Loop Prevention**: Standardized target-nulling for all domestic dispute actors after correction strikes.

---

## [1.09.012] - 2026-01-24

### ⚔️ AI Refinements

- **Domestic Strike Reform**: Reduced adult intervention to strictly **1 strike**. This ensures the adult "enforces" the rules without starting a prolonged brawl.
- **Combat Loop Prevention**: Baby wolves now explicitly recognize ally adults (owner's other dogs) and will no longer retaliate against them if hit during an intervention. This stops never-ending fights.

---

## [1.09.011] - 2026-01-24

### ⚔️ AI Refinements

- **Intervention Lock**: Restricted "Domestic Intervention" behavior to strictly **Aggressive** wolves. Normal ("Classic") and Pacifist wolves will no longer attack ally babies to defend the owner, reducing combat chaos.
- **BABY Safety**: Improved safety checks for baby wolves to ensure they aren't targeted by friendly adults unless the adult is an "Aggressive" enforcer.

---

## [1.09.010] - 2026-01-24

### 🛠 Bug Fixes

- **Refmap Crash Fix**: Moved `canAttack` injection to a superclass Mixin (`TamableAnimalMixin`). This resolves the `InvalidInjectionException` caused by targeting inherited methods without a refmap.
- **Improved Compatibility**: Standardized target selection for inherited entity methods in the 26.1 snapshot era.

---

## [1.09.009] - 2026-01-24

### 🛠 Bug Fixes

- **Crash Fix**: Resolved a critical startup crash caused by structural malformation in `WolfMixin.java` (nested methods).
- **Stability**: Standardized Mixin method-to-class balance to ensure reliable bytecode transformation.

---

## [1.09.008] - 2026-01-24

### 🛠 Bug Fixes

- **Retaliation Bypass**: Overrode `canAttack` and `wantsToAttack` in `WolfMixin` to bypass hardcoded vanilla blocks that prevent wolves from attacking their owners during domestic disputes.
- **Intervention Logic**: Aggressive adults now use priority 0 for their intervention goals and correctly set their aggressive state when defending owners from misbehaving babies.

---

## [1.9.7-26.1] - 2026-01-24

### 🛠 Bug Fixes

- **Domestic Retaliation**: Fixed an issue where aggressive baby wolves would not retaliate when punched by their owner.
- **Adult Intervention**: Fixed aggressive adult intervention logic to properly trigger when a baby attacks its owner.
- **Strike System**: Corrected the strike-limited counter-attack system (2 hits for babies, 3 hits for adult enforcers).
- **Mixin Compatibility**: Updated `MobMixin` to use correct mapping behavior for the unobfuscated environment.

---

## [1.9.6-26.1] - 2026-01-24

### 🏗 Architecture Reversion

- **Fabric Native Reversion**: Pivot from Multi-Loader (NeoForge/Fabric) back to it being **Fabric only**.
  - *Reasoning*: Maintaining two loaders is way too hard and I'm too stupid to keep fighting NeoForge's snapshot jank. We're going Fabric-only to save my sanity.
  - *The Technicality*: Removing the platform services abstraction reduces build overhead and resolves persistent conflicts between the abstraction layer and unobfuscated mappings in the 26.1 environment.
  - **Source Consolidation**: Smashed `common` and `fabric` into one root structure. Deleted NeoForge and all the extra dependencies because one loader is much easier to manage than two.

### 🛠 Technical Changes

- **Java 25 Standardization**: Fully migrated the build system and toolchain to **Java 25**.
  - *Impact*: Required for compatibility with Minecraft 26.1-snapshot-4 and the latest Fabric Loom releases.
- **Fabric Attachment API**: Fully integrated Fabric's native Attachment API for persistent wolf data, replacing the platform-agnostic service abstraction.
- **Build System**: Standardized on Fabric Loom 1.14.7. Successfully resolved evaluation errors by aligning mappings with snapshot manifest requirements.
- **Bug Fix**: Resolved a critical startup crash (`MixinApplyError`) by redirecting taming logic injection from `setTame` (non-overridden) to `applyTamingSideEffects` (native override).
- **Deployment**: Verified clean build and proper refmap generation for version 1.9.6.

---

## [1.7.6-26.1] - 2026-01-21

### ⚙️ Technical Changes

- **Standalone Config System**: Replaced the dependency on `AutoConfig` and `Cloth Config` with a custom lightweight JSON loader.
  - *Impact*: Configuration files (`config/betterdogs.json`) are now properly generated and respected again. Users can manually edit this file to change settings (e.g., friendly fire, speed buffs) even though the in-game GUI is currently disabled.

## [1.7.5-26.1] - 2026-01-21

### 🧠 AI Improvements

- **Smart Cliff Safety**: Wolves now actively retreat (walk backwards 4 blocks) when they detect a steep drop or void, rather than just stopping. This prevents them from getting stuck at the edge or sliding off.

## [1.7.4-26.1] - 2026-01-21

### 🛠 Bug Fixes

- **Crash Fix**: Resolved a `NullPointerException` during the "Cliff Safety V2" check.
  - *Details*: The code now correctly exits the tick handler if the target is cleared by the V1 check, preventing subsequent access to invalid target references.

## [1.7.3-26.1] - 2026-01-21

### 🛠 Bug Fixes

- **Crash Fix**: Removed reference to missing `WolfGoalsMixin` which caused a startup crash. Functionality was previously merged into `WolfMixin`.

## [1.7.2-26.1] - 2026-01-21

### 🛠 Technical Changes

- **Dependency Fix**: Relaxed `fabric.mod.json` version constraints (`minecraft` to `~26.1-`) to resolve "Incompatible mods" errors with alpha launchers.

## [1.7.1-26.1] - 2026-01-21

### Ported to Minecraft 26.1 (Snapshot 4)

This release marks the migration to the first **unobfuscated** version of Minecraft.

### 🛠 Technical Changes

- **Language Migration**: Switched codebase from **Kotlin** back to **Java**.
  - *Reasoning*: While Kotlin offers a smoother coding experience, the transition to the unobfuscated 26.1 environment favored the native stability and direct mapping of Java, reducing toolchain complexity during this experimental snapshot phase.
- **Build System**: Updated to Fabric Loom 1.14.7 (`net.fabricmc.fabric-loom`) to support unobfuscated builds.

### ⚠️ Known Issues / Temporary Changes

- **Config GUI Unavailable**: While an alpha build of Mod Menu (18.0.0-alpha.4) exists for 26.1, the required **Cloth Config** library has not yet been updated for the unobfuscated environment (still uses `intermediary` mappings).
  - As a result, the in-game configuration screen is disabled to prevent crashes.
  - Default settings are applied automatically.
