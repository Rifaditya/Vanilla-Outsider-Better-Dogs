# Better Dogs (v26.3) History Archive — Jun–Jul 2026

> [Back to Active History.md](History.md)

---

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

## [4.15.8-26.2] - 2026-07-01
### Added
- **Dynamic Follow Range Scaling**: Configured tamed wolf `Attributes.FOLLOW_RANGE` to scale dynamically based on personality and active pack spread spacing:
  - **Problem**: Wolves were instantly dropping targets shot by the player at distance (30+ blocks) because vanilla's hardcoded `FOLLOW_RANGE` defaults to 16.0 blocks. Once a wolf's target exceeded this range, `TargetGoal.canContinueToUse()` invalidated it on the very next tick.
  - **Solution**: Applied personality-based base `FOLLOW_RANGE` values in `WolfStatManager` during stat initialization: Aggressive (32.0), Normal (24.0), Pacifist (16.0). Additionally, `PersonalityFollowOwnerGoal` now dynamically applies a transient `AttributeModifier` (`betterdogs:follow_range_spread`) that adds the current follower spacing offset to `FOLLOW_RANGE`. Using `addTransientModifier()` ensures the value updates in real-time without being written to NBT on level saves.

## [4.15.7-26.2] - 2026-06-30
### Fixed
- **Wander Boundary Bouncing**: Replaced the forced return-path fallback in `TamedWanderNearOwnerGoal.getPosition()` with a clean `null` return:
  - **Problem**: Wolves at their outer wander boundary were trapped in a restless, non-stop pacing loop. When all 10 random stroll attempts failed (because every candidate position fell outside the boundary), the fallback called `DefaultRandomPos.getPosTowards()` which forced a path back towards the owner. On the next tick, the wolf would wander outward again, repeating the cycle indefinitely.
  - **Solution**: Returning `null` from `getPosition()` is the standard vanilla API contract for "no valid destination this tick," which causes `RandomStrollGoal` to gracefully cancel the walk action. The wolf now stands still naturally at its perimeter limit instead of pacing.

## [4.15.6-26.2] - 2026-06-30
### Fixed
- **Sit & Flee Low Health Conflict**: Added checks to prevent the low-health flee AI goal (`WolfFleeLowHealthGoal`) from executing when a tamed wolf has been ordered to sit (`isOrderedToSit()`). This resolves the bug where injured dogs would frantically cycle between sitting and standing up to run away.

## [4.15.5-26.2] - 2026-06-30
### Changed
- **Wide-Arc Encirclement & Direct Approach Dampening**: Adjusted flanking parameters to make encirclement patterns visually distinct:
  - **Increased Flanking Radius**: Expanded side flanking coordinate offsets from 2.5 blocks to **4.5 blocks** and rear wrap offsets to **2.0 blocks** relative to the target's orientation. This forces flanking wolves to run in a wide, sweeping curve instead of a tight charge.
  - **Dampened Direct Approach**: Reduced the approach speed of direct-charging wolves (lone/leader/closest wolves) further from 80% to **50% of combat speed** (`speedModifier * 0.5D`) when approaching from a distance (distance > 3 blocks). This creates a steady, slow-advancing central line, allowing flanking sweepers running at full speed to wrap around first.

## [4.15.4-26.2] - 2026-06-30
### Changed
- **Context-Aware Flanking & Side Selection**: Refactored flanking tactics to select roles and directions dynamically based on physical coordinates:
  - **Approach Time Role Assignment**: Instead of relying solely on the rolled speed attributes, each wolf's role is now determined by its *Approach Time*—the mathematical ratio of its physical distance from the target to its active movement speed attribute value ($\text{Approach Time} = \text{distance} / \text{speed}$). Sorting the active pack by this ratio ensures that the dogs closest and fastest to engage the target charge directly (first 50%) to lock it down, while the dogs farther away (remaining 50%) execute flanking maneuvers. This ensures that a close, slower dog does not sluggishly try to run away to "flank" while a distant, fast dog is forced to run straight.
  - **Contextual Left/Right Split**: Flanking dogs choose their flank direction (Left or Right) dynamically based on their current physical position relative to the target's look vector. Using a 2D cross product of the target's horizontal look vector and the vector pointing from the target to the wolf ($\text{cross} = \text{forward.x} \times \text{toWolf.z} - \text{forward.z} \times \text{toWolf.x}$), a wolf already on the target's left side automatically flanks left, and a wolf on the right flanks right. This removes unnecessary, unnatural path crossings and collision intersections, creating a highly fluid and coordinated encirclement.

## [4.15.3-26.2] - 2026-06-30
### Changed
- **Speed-Based Flanker Selection**: Replaced random/uniform flanking with attribute speed-based selection. The top 50% fastest dogs in the local pack execute flanking, while the slower 50% form the direct assault line. Split assignments (Left/Right) are dynamically balanced based on their speed ranking index to ensure a clean wrap-around split.

## [4.15.2-26.2] - 2026-06-30
### Changed
- **Tamed Flanking Coordination**: Refactored the pack coordination behavior. Rather than boosting flanking dogs' speed, straight-charging wolves (lone/leader wolves) are now slowed to 80% speed during the target approach phase (distance > 3 blocks). Flanking followers move at full combat speed, allowing them to wrap around and encircle the target before the direct assault.

## [4.15.1-26.2] - 2026-06-30
### Fixed
- **Tamed Flanking Tactics**: Fixed an issue where the flanking dogs would charge straight due to vanilla's `MeleeAttackGoal` pathing overrides. Overrode the `tick()` method completely to bypass vanilla overrides and added a `1.35x` movement speed boost during flanking to allow dogs to rapidly sweep around targets.

## [4.15.0-26.2] - 2026-06-30
### Added
- **Tamed Pack Flanking Tactics**: Tamed wolves (dogs) now treat their owner (the player) as the pack leader. This enables cooperative flanking attacks during combat—dogs will automatically coordinate and split left and right around the target hostile relative to its direction, rather than running in straight lines.

## [4.14.7-26.2] - 2026-06-28
### Added
- **Hidden Favorite Treats**: The Jade tooltip for a tamed dog's Favorite Treat now supports a "Hidden until discovered" mode. When enabled, the treat displays as `???` until the player successfully feeds the dog its specific favorite treat for the first time.
- **Jade Setting**: Added the "Hide Undiscovered Treat" configuration option to the Jade plugin settings menu. By default, this is set to **OFF** to respect Jade's identity as a quick-info shortcut.

> [!NOTE]
> **Mod Page Update Requirement:** When `4.14.7` is pushed to public release, the CurseForge and Modrinth description pages MUST be updated to include a note about this new feature. It should explain that the "gamified" `???` feature exists for players who want to experiment, but they must manually enable it in their Jade settings.

## [4.14.6-26.2] - 2026-06-28
### Fixed
- **Sanitary Compliance**: Migrated all GameRule registration keys and their corresponding `en_us.json` localization strings from the verbose `vanilla-outsider-better-dogs:` namespace down to the required `betterdogs:` namespace.

## [4.14.5-26.2] - 2026-06-28
### Added
- **Jade Configuration**: Added built-in configuration toggles to the Jade plugin. Players can now toggle "Show Inbred Status" and "Show Wolf Info" directly from the Jade settings menu.

## [4.14.4-26.2] - 2026-06-28
### Fixed
- **Jade Inbred Status**: Fixed an issue where the Jade tooltip would not display the "Inbred" tag on client screens because the genetics data wasn't synced from the server. Implemented a Jade `IServerDataProvider` to correctly sync this boolean to the client.

## [4.14.3-26.2] - 2026-06-28
### Changed
- **Architectural**: Extracted combat hooks from `WolfMixin` into `WolfCombatMixin` to strictly adhere to the 300 LOC limit.
- **Licensing**: Automatically applied missing GPLv3 SPDX license headers to all Java files.

## [4.14.2-26.2] - 2026-06-28
### Optimized
- **Tick Performance Overhaul**: Fixed massive garbage collector overhead by properly extracting particle options (Aggressive, Pacifist, Guard, and Runt particles) into static fields. Adoptable particle trails and Pacifist buff radius checks are now properly gated via tick modulo, reducing tick allocations and checks by over 95% per wolf.

## [4.14.1-26.2] - 2026-06-28
### Fixed
- **Jade Plugin Registration**: Fixed a critical crash where the `@WailaPlugin` annotation was given an invalid required `modid` ("betterdogs:jade_plugin"), which silently aborted the registration of all Better Dogs Jade providers (Health, Traits, and Inbred statuses) causing the mod to fallback to Jade's text-mode health renderer. All Jade integrations are now fully active!

## [4.14.0-26.2] - 2026-06-28
### Added
- **Jade Inbred Status UI**: Tamed dogs with the `inbred` genetic trait will now display a red "Inbred" tag in their Jade tooltip. This feature includes proper Jade configuration toggles, fully localized in `en_us.json`.

## [4.13.5-26.2] - 2026-06-28
### Summary
The **"Dynamic Jade Health UI"** update. 
- **Jade Integration**: Hooked into the Jade API to display dynamic health values for Better Dogs (who possess randomized max health from genetics exceeding vanilla limits). 
- **Custom Health Element**: Bypassed Jade's hardcoded 20-icon render cap by implementing `BetterDogsHealthElement`. A wolf with 60 HP will now render exactly 3 rows of 10 heart icons each.
- **Priority Fix**: Set the Jade provider priority to `10000` (TAIL) to successfully replace the default health component while preserving adjacent elements (like armor) on the same UI line.
- **Client Sync**: Added explicit attribute modifier synchronization for `MAX_HEALTH` in `WolfStatManager` so that the client UI correctly reflects the server-side rolled genetic max health.).

## [4.12.0-26.2] - 2026-06-27
### Added
- **Favorite Treats Mechanic:** Wolves now have a hidden favorite treat (seeded by their UUID)! Finding and feeding them their favorite treat fully heals them, grants Regeneration II, and triggers their native Zoomies!

## [4.11.2-26.2] - 2026-06-27
### Changed
- **Strict Parity Sync:** Cross-pollinated minor polish and safety checks with 26.1.2.
  - Added safety checks: Grudges will no longer form against Players or other Tamed Animals to prevent infinite PvP loops.
  - Added strict Tamed and GameRule checks before ticking the Nemesis buff.

## [4.11.1-26.2] - 2026-06-27
### ??? Fixed
- **Architectural Audit Fixes:** 
  - Extracted logic out of `WolfMixin` to reduce file size under 300 LOC limit (moved to `WolfCombatHooks`).
  - Added missing GPLv3 headers to Nemesis system files.
  - Added missing localization keys for the Nemesis System GameRules in `en_us.json`.
  - Synced Nemesis Grudge tick frequency to 20 ticks (1 second) for parity.

## [4.11.0-26.2] - 2026-06-26
### Summary
The **"Nemesis (Grudge) System"** update. Implements a grudge system where tamed wolves will actively seek revenge on hostile mobs that kill their pack-mates.
- **Grudge Formation**: Injected `die` inside `WolfMixin` to trigger `WolfCombatHooks.onDeath`. When a tamed wolf is killed by a hostile mob (`LivingEntity`), it broadcasts the nemesis mob type to all tamed wolves with the same owner within a 32-block radius.
- **Persistent Storage**: Utilizes the `WolfPersistentData` attachment to store `nemesisType` (String) and `nemesisExpiry` (Long), allowing the grudge to persist across reloads.
- **AI Targeting**: Added `WolfNemesisTargetGoal` (priority 1 in `targetSelector`) that forces wolves with an active grudge to lock onto targets of the nemesis type.
- **Combat Buffs**: Added `tickNemesisSystem` in `WolfTickHelper` (called from `WolfMixin` tick) which applies `STRENGTH` and `SPEED` buffs and emits angry particles while the wolf is actively attacking its nemesis.
- **Configuration**: Implemented GameRules `bd_nemesis_system` (boolean, default true) and `bd_nemesis_duration_days` (int, default 3) to configure the system.

## [4.10.1-26.2] - 2026-06-26
### Fixed
- Fixed Code Auditor violations (spacing, unused imports, citations, GPLv3 headers, God Mixin size limit).

## [4.10.0-26.2] - 2026-06-26
### Summary
The **"True Pack Hunting Tactics (Flanking)"** patch. Revolutionizes wolf pack combat by implementing dynamic flanking maneuvers for followers.
- **Flanking Tactics**: When a pack engages a target, only the leader will attack directly. The follower wolves will perform flanking maneuvers, dynamically pathfinding around the target to strike from the sides or behind. This makes packs significantly deadlier and more visually coordinated without causing server strain.
- **Flanking GameRule**: Added `bd_pack_flanking_tactics` (default `true`) to toggle this behavior.

## [4.9.8-26.2] - 2026-06-26
### Summary
The **"Adoption Bugfix"** patch. 
- Fixed an issue where adopted wolves would retain their old pack leader UUID, guard mode status, and old grudges after being claimed by a new owner.

## [4.9.7-26.2] - 2026-06-23
### Summary
The **"Merit-Scaled Gifting Chances"** patch. Rebalanced how gift chances are evaluated to create a true daily chance that scales with interaction merits.
- **Scaled Daily Chance**: Modified `WolfGiftGoal` to evaluate the gift probability exactly once when the cooldown ends. If the random roll fails, the dog's cooldown is reset. The chance scales linearly from the base percentage (default 1%) at the minimum threshold, up to 100% when the dog reaches maximum interaction merits (10,000).

## [4.9.6-26.2] - 2026-06-23
### Summary
The **"Gifting Threshold Balancing"** patch. Adjusted the interaction thresholds and limits for dog gifts.
- **Higher Threshold**: Increased the default required interactions for a gift (`bd_gift_feed_threshold`) from `3` to `10`.
- **Extended Interaction Cap**: Increased the maximum hard-limit of accumulated interaction merits from `10` to `10,000`, allowing players who configure high thresholds to actually reach them without getting capped artificially.

## [4.9.5-26.2] - 2026-06-23
### Summary
The **"Accidental Attack Partial Demerits"** patch. Refined the gifting demerits system.
- **Accidental Penalties**: Intentional attacks (shift-attacking) still fully reset the wolf's gifting merits to 0. Accidental attacks (normal hitting) when `bd_demerit_accidental_attacks` is enabled now only reduce the merit count by 1 instead of a full reset.

## [4.9.4-26.2] - 2026-06-23
### Summary
The **"Gifting Demerits on Owner Attacks Fix"** patch. Resolves an issue where normal/accidental attacks would reset a wolf's interaction count.
- **Crouch Condition**: Modified the demerit logic in `WolfMixin.java` to check if `player.isCrouching()`. Players must now be explicitly crouching/sneaking while attacking their dog to trigger the demerit and reset the feed count to 0.

## [4.9.3-26.2] - 2026-06-23
### Summary
The **"Configurable Interaction Cooldown"** patch. Expose a new GameRule to make the positive interaction cooldown configurable.
- **Configurable Cooldown**: Replaced the hardcoded 5-second cooldown on free interactions with a configurable GameRule `bd_gift_interaction_cooldown` (default: 100 ticks / 5 seconds).
- **Mixin Integration**: Updated `WolfInteractMixin.java` to read the GameRule value dynamically instead of using a hardcoded cooldown.

## [4.9.2-26.2] - 2026-06-23
### Summary
The **"Gifting Demerits on Owner Attacks"** patch. Reset a tamed wolf's gifting merits (interaction count) to 0 if attacked by its owner.
- **Demerit Logic**: Hooked into `actuallyHurt` in `WolfMixin.java` to check if the causing entity is the owner player. If so, it resets the wolf's interaction count (`feedCount`) to `0`.
- **Friendly Fire Compatibility**: Runs at the beginning of the damage chain, ensuring demerits are applied even if actual damage is cancelled by friendly fire protection.

## [4.9.1-26.2] - 2026-06-23
### Summary
The **"Expanded Gifting Interaction Gates"** patch. Expands gifting eligibility gates to track all forms of positive player-dog interaction.
- **Universal Interaction Tracker**: Hooked into the return of `mobInteract` to track sitting/standing, calming down, toggling guard mode, commands, and adoption status.
- **Anti-Spam Cooldown**: Added a transient, 5-second (100 ticks) cooldown for free interactions to prevent exploit gating, while feeding actions bypass the cooldown and always count.
- **Rebranding**: Rebranded GameRule descriptions and localizations to **Gift Interaction Threshold**.

## [4.9.0-26.2] - 2026-06-23
### Summary
The **"Feeding-Gated Wolf Gift System"** patch. Tamed wolves will now only bring morning gifts if they are fed regularly by their owner.
- **GameRule & Config**: Registered the Namespaced GameRule `vanilla-outsider-better-dogs:bd_gift_feed_threshold` (default: 3) to configure the feeding threshold.
- **Feeding Counter**: Hooked into the return of `mobInteract` to track successful wolf feeding (healing, breeding, or aging up). Increments a persistent `feedCount` on the tamed wolf (capped at 10 to prevent NBT overflow).
- **Gift AI Goal**: Gated the morning gift AI goal (`WolfGiftGoal`) to require a feed count of at least the threshold.
- **Consumption, Audio, and HUD Overlay**: Consumes the threshold amount of feeds when a gift is generated, plays a happy whine sound effect, and displays a localized action bar overlay notification to the owner.

## [4.8.13-26.2] - 2026-06-21
### Summary
The **"Wolf Spawns Multiplier"** patch. Introduces a configuration setting and game rule `bd_wolf_spawn_multiplier_percent` (default: 1.5x) to scale the spawning weight of wolves in biome settings.
- **NaturalSpawner Mixin**: Injected `NaturalSpawnerMixin.java` targeting `NaturalSpawner.mobsAt()` at return. Intercepts biome spawn settings, checks if `net.minecraft.world.entity.EntityTypes.WOLF` is in the candidate list, and rebuilds the `WeightedList` with the multiplied weight if a non-1.0x setting is configured.
- **Config & Registry**: Added `wolfSpawnMultiplier` double to `BetterDogsConfig` and registered `BD_WOLF_SPAWN_MULTIPLIER_PCT` GameRule. Added screen entry in `ClothConfigScreenHelper`.
- **Translations**: Added localization keys for both ModMenu config screen and GameRule descriptions.

## [4.8.12-26.2] - 2026-06-21
### Summary
The **"Tamed Wolf Wander Restriction"** patch. Replaces the owner-unaware vanilla wandering behavior (`WaterAvoidingRandomStrollGoal`) with a personality-based `TamedWanderNearOwnerGoal` to restrict tamed wolf wandering radius around their owners.
- **Wander Radius Limits**: Restricts tamed wolf wandering radius to: 14.0 blocks for `AGGRESSIVE`, 8.0 blocks for `NORMAL`/Classic, and 4.0 blocks for `PACIFIST`.
- **Navigation Return Pathing**: If the wolf drifts outside its personality's max radius, it generates a pathing target directly towards the owner using `DefaultRandomPos.getPosTowards()`.
- **Target Search Loop**: Evaluates standard stroll positions up to 10 times; if all are outside the radius, it defaults to moving towards the owner.
- **Mixin Registration**: Refactored `WolfAIMixin.java` to scan, isolate, and remove the vanilla `WaterAvoidingRandomStrollGoal.class` (preserving subclasses like `WanderlustGoal`) and inject `TamedWanderNearOwnerGoal` at priority `8`.
- **Gated Checks**: Optimizes server performance by short-circuiting AI evaluations if the wolf is untamed, sitting, leashed, or in Guard Mode.
- **Dynamic Wander Scaling**: Links the wander `maxRadius` calculation in `TamedWanderNearOwnerGoal.java` to `PersonalityFollowOwnerGoal.FollowerSpacingCache` to retrieve the owner's active follower count. If $N > 1$, it adds the square-root-based follower spacing offset to `maxRadius`, naturally expanding the boundary to prevent clumping and overcrowding in larger packs.

## [4.8.11-26.2] - 2026-06-12
### Summary
The **"Guard Alarm Point & Freeze"** patch. Configures Pacifist watchdogs to perform a hunting-dog-style pointing freeze during active alarms, locking onto the closest hostile target.
- **Pointing Target Sweep**: Updated the alarm detection routine to track the closest hostile target.
- **Target Tracking & Posture**: Forced the dog to stand up (breaking sitting pose) and orientation-lock towards the target every tick using look control.

## [4.8.10+A-26.2] - 2026-06-12
### Summary
The **"Directional Guard Alarm Particles"** patch. Aligns the Pacifist watchdog alert particles with the wolf's looking direction, creating a 3D forward-pointing warning cone spray.
- **Directional Cone Spray**: Modified `WolfGuardGoal.java` to calculate a 3D cone spray spanning 60 degrees centered around the wolf's exact looking vector (pitch and yaw).
- **Mouth Emittance**: Set particle spawn position slightly in front of the wolf's face at mouth level (`y = eyeHeight - 0.1`) to represent a warning bark/breath.

## [4.8.9+A-26.2] - 2026-06-12
### Summary
The **"Configurable Guard Particle Density"** patch. Introduces a client/server configuration setting `guardParticleDensity` inside a new "Visual & Performance Options" Cloth Config screen tab to scale or toggle guard alert particles.
- **Configurable Particle Scaling**: Added `guardParticleDensity` setting to `BetterDogsConfig` supporting `high`, `medium` (default), `low`, and `off` options.
- **Visual & Performance Category**: Created a dedicated Cloth Config category for Visual & Performance options that is separated from baseline game rule default configuration warnings.
- **Conditional Alert Loop**: Updated `WolfGuardGoal.java` to scale trigonometry loop size and angle steps based on user preference to optimize client-side rendering.

## [4.8.8+A-26.2] - 2026-06-12
### Summary
The **"Trigonometric Pacifist Sentinel Warning Circle"** patch. Redesigns the alert particles for guarding Pacifist dogs to spawn a highly noticeable, expanding horizontal circular ring of 12 pure red dust particles.
- **Trigonometric Ring Spawning**: Modified `WolfGuardGoal.java` to calculate a 12-particle circle using `cos` and `sin` over $360^\circ$ at a 0.5 blocks radius offset.
- **Expanding Velocity**: Spawns particles with `count = 0` and outward directional velocity vectors scaled by speed `0.5` to make the circle physically expand.

## [4.8.7+A-26.2] - 2026-06-12
### Summary
The **"Rare Pack-Wide Howling"** patch. Reduces howling frequency and applies shared cooldowns to pack members to make howling a rare, ambient event.
- **Pack-Wide Shared Cooldowns**: Modified `GroupHowlGoal.java` so that when a wolf spreads its howl to nearby pack members, it sets their goal cooldowns to `HOWL_COOLDOWN` (10 minutes) as well.
- **Gated Howl Chance Checks**: Gated the howling chance check to run only once every 100 ticks (5 seconds) instead of every tick.

## [4.8.6+A-26.2] - 2026-06-12
### Summary
The **"Optimized Red Alert Dust Particles"** patch. Replaces the note particles during Pacifist sentinel guarding alarms with low-overhead, red dust alert particles.
- **Red Alert Dust**: Replaced the default music note particles with an optimized horizontal burst of 6 red dust particles (`DustParticleOptions`), sent in a single network packet to avoid performance overhead while providing a clear threat/warning color.

## [4.8.5+A-26.2] - 2026-06-12
### Summary
The **"Sound Variant Weighted Lottery"** patch. Transitions the sound variant selection from a strict deterministic max-score model to a UUID-seeded weighted lottery.
- **Weighted Lottery Math**: Replaced maximum score logic with a UUID-seeded weighted lottery using `Math.max(0.0f, ...)` trait weight modifiers. This ensures every individual dog gets a highly unique, stable, and randomish sound variant that aligns with its personality and genetics.

## [4.8.4+A-26.2] - 2026-06-12
### Summary
The **"Dynamic Sound Variant Mapping"** patch. Implements a mathematical decision-tree/scoring system that resolves the wolf's sound variant dynamically based on genetics, scale, and personality.
- **Dynamic Sound Variant Mapping**: Implemented a mathematical scoring system that deterministically assigns the wolf's sound variant (Classic, Big, Cute, Puglin, Angry, Grumpy, Sad) based on its rolled genetics, scale, and personality type rather than relying on vanilla's random selection.

## [4.8.3+A-26.2] - 2026-06-11
### Summary
The **"Pacifist Guard Sentinel Growl Alert"** patch. Changes the sentinel alarm sound for Pacifist dogs in Guard Mode from a whine to the specific growl sound corresponding to the wolf's sound variant itself.
- **Pacifist Guard warning sound**: Changed the sentinel alarm sound for Pacifist dogs in Guard Mode from a whine to the specific growl sound corresponding to the wolf's sound variant itself (pitch adjusted to 1.0f).

## [4.8.2+A-26.2] - 2026-06-11
### Summary
The **"Low-Health Whimpering Scaling Fix"** patch. Scales the low-health whine sound threshold dynamically based on the dog's maximum health percentage to prevent Runts/puppies from whimpering constantly when healthy.
- **Low-Health Whimpering Scaling**: Fixed low-health whining behavior by replacing the vanilla absolute health `< 20.0f` threshold check with a dynamic check scaled to the dog's maximum health (`< 50% max health`). This prevents dogs with low max health (such as Runts and puppies) from whimpering constantly when fully healthy.

## [4.8.1+A-26.2] - 2026-06-11
### Summary
The **"Classloader Mixin Shadow Crash Fix"** patch. Resolves a startup crash caused by an invalid `@Shadow` on the inherited `mob` field in `WalkNodeEvaluatorMixin`.
- **Classloader Mixin Shadow Crash**: Fixed a startup and runtime crash when trying to locate `@Shadow Mob mob` field inside `WalkNodeEvaluatorMixin` by making the mixin class inherit from `NodeEvaluator` directly.

## [4.8.0+A-26.2] - 2026-06-11 [DEPRECATED - CRITICAL BUG]
> [!WARNING]
> This version contains a classloader startup crash (InvalidMixinException) in `WalkNodeEvaluatorMixin` and is deprecated. Use `4.8.1+A-26.2` instead.

### Summary
The **"Custom Howling Sound Restoration"** minor release. Packages and registers custom howling sound events to play the authentic wolf howling sound clips that were removed from the vanilla game.
- **Custom Howling Sound Restoration**: Packaged and restored the deleted vanilla `Wolf_howl1.ogg` and `Wolf_howl2.ogg` sound files into the mod assets.
- **Custom Sound Event Registration**: Registered the `betterdogs:entity.wolf.howl` sound event.
- **AI Group Howl update**: Updated `GroupHowlGoal` to play the authentic howling sound effects instead of the baby whine fallback.

## [4.7.6+A-26.2] - 2026-06-11 [DEPRECATED - CRITICAL BUG]
> [!WARNING]
> This version contains a classloader startup crash (InvalidMixinException) in `WalkNodeEvaluatorMixin` and is deprecated. Use `4.8.1+A-26.2` instead.

### Summary
The **"Alternative Pathfinding on Push"** update. Adds checks to see if another path exists to get to the target area (even if longer) before giving up and halting navigation.
- **Alternative Pathfinding on Push**: Added checks to see if another path exists to get to the target area (even if longer) before giving up and halting navigation.

## [4.7.5+A-26.2] - 2026-06-11
### Summary
The **"Ground Food Refusal Trait"** update. Adds a feature where some tamed-from-birth dogs persistently refuse to eat food from the ground.
- **Ground Food Refusal Trait**: Added a feature where some tamed-from-birth dogs (bred from parents, not wild-tamed) persistently refuse to eat food from the ground.
  - Adds `bd_enable_refuse_ground_food` GameRule and toggle setting to enable/disable the feature globally.
  - Adds `bd_refuse_ground_food_chance` GameRule and setting to configure the percentage chance (default: 30%) that a puppy receives the refusal trait at birth.

## [4.7.4+A-26.2] - 2026-06-11
### Summary
- **Wolf-on-Wolf Collision Push Safety**: Added cooperative collision handling between tamed wolves.
  - Tamed wolves colliding with a sitting, guarding, or endangered dog (facing a cliff/lava/magma/fire hazard) will immediately halt their navigation path.
  - A patience timer (`pushWaitTimer` of 60 ticks / 3 seconds) is applied to the pushing dog to prevent continuous pathfinding attempts and jittering.
  - Player-to-dog pushes are exempted to prevent player blocking.

## [4.7.3+A-26.2] - 2026-06-11
### Summary
- **Magma Block Avoidance**: Added `Blocks.MAGMA_BLOCK` to the hazard list in `AvoidHazardsGoal`, preventing wolves from walking onto magma blocks.
- **Lava Safety Verification**: Confirmed that any level of lava (source or flowing) is avoided.

## [4.7.2+R-26.2] - 2026-06-06
### Summary
- **Release Promotion**: Promoted the Select-and-Ride Dog Command System and Cloth Config GUI warning layout fixes to a stable production Release.

## [4.7.1+A-26.2] - 2026-06-06
### Summary
- **Optional GUI Integration**: Upgraded config screen classloading to resolve via `GuiHelper` in `DasikLibrary` 1.8.2.
- **UI Warning Polish**: Removed repetitive hover tooltips from config options and moved them to category headers.

## [4.7.0+A-26.2] - 2026-06-06
### Summary
The **"Select-and-Ride Dog Command System"** update. Adds a Stick-based dog selection, mounting, and dismounting system with custom visual sitting poses.
- **Select-and-Ride Dog Command System**: Command dogs to sit/ride in vehicles, mounts, and modded chairs using a Stick tool.
- **Visual Sitting Pose**: Force visual sitting pose on wolves while riding any vehicle.
- **Dismount Controls**: Dismount using Stick (dismount & stand) or Empty-hand Shift+Right-click (dismount & sit).
- **Optional Unrestricted Riding GameRule**: Added `betterdogs:allow_unrestricted_dog_riding` to allow dogs to ride any entity (e.g. Ghasts).

## [4.6.26+A-26.2] - 2026-06-06
### Summary
The **"Unrelated Mate Prioritization"** update. Prioritizes breeding with unrelated wolves in range.
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
