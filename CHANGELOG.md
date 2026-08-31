# Changelog - Vanilla Outsider: Better Dogs (MC 1.20.1)

## [1.0.85+1.20.1]
### Added
- 🌐 **Korean (`ko_kr`) Localization & Player Guide**:
  - Added complete `assets/betterdogs/lang/ko_kr.json` matching all 653 translation keys.
  - Added localized player guide in `Doc/Players/index_ko_kr.md`.

## [1.0.84+1.20.1]
### Added
- 🌐 **French (`fr_fr`, `fr_ca`) Localization & Player Guide**:
  - Added complete `assets/betterdogs/lang/fr_fr.json` and `fr_ca.json` matching all 653 translation keys.
  - Added localized player guide in `Doc/Players/index_fr_fr.md`.

## [1.0.83+1.20.1]
### Added
- 🌐 **Japanese (`ja_jp`) Localization & Player Guide**:
  - Added complete `assets/betterdogs/lang/ja_jp.json` matching all 653 translation keys.
  - Added localized player guide in `Doc/Players/index_ja_jp.md`.

## [1.0.82+1.20.1]
### Added
- 🌐 **Portuguese (`pt_br`, `pt_pt`) Localization & Player Guide**:
  - Added complete `assets/betterdogs/lang/pt_br.json` and `pt_pt.json` matching all 653 translation keys.
  - Added localized player guide in `Doc/Players/index_pt_br.md`.

## [1.0.81+1.20.1]
### Added
- 🌐 **German (`de_de`) Localization & Player Guide**:
  - Added complete `assets/betterdogs/lang/de_de.json` matching all 653 translation keys.
  - Added localized player guide in `Doc/Players/index_de_de.md`.

## [1.0.80+1.20.1]
### Added
- 🌐 **Spanish (`es_es`, `es_mx`) Localization & Player Guide**:
  - Added complete `assets/betterdogs/lang/es_es.json` and `es_mx.json` matching all 653 translation keys.
  - Added localized player guide in `Doc/Players/index_es_es.md`.

## [1.0.79+1.20.1]
### Added
- 🌐 **Russian (`ru_ru`) Localization & Player Guide**:
  - Added complete `assets/betterdogs/lang/ru_ru.json` matching all 653 translation keys.
  - Added localized player guide in `Doc/Players/index_ru_ru.md`.

## [1.0.78+1.20.1]
### Added
- 🌐 **Traditional Chinese (zh_tw, zh_hk) Localization & Player Guide**:
  - Added complete ssets/betterdogs/lang/zh_tw.json and zh_hk.json matching all 653 translation keys.
  - Added localized player guide in Doc/Players/index_zh_tw.md.

## [1.0.77+1.20.1]
### Added
- 🌐 **Simplified Chinese (zh_cn) Localization & Player Guide**:
  - Added complete ssets/betterdogs/lang/zh_cn.json matching all 653 translation keys.
  - Added localized player guide in Doc/Players/index_zh_cn.md.

## [1.0.76+1.20.1]
### Added
- 🌐 **Indonesian (id_id) Full Parity Localization & Player Guide**:
  - Synchronized complete ssets/betterdogs/lang/id_id.json matching all 653 translation keys with zero untranslated strings.
  - Added localized Indonesian player guide in Doc/Players/index_id_id.md.
  - Stripped legacy UTF-8 BOM encoding for pure standard UTF-8 compliance.

## [1.0.75+1.20.1] - 2026-08-22
### Added
- 🍖 **Hoover / Ground Food Scavenger Quirk (`WolfDispositionHelper.java` & `EatGroundFoodGoal.java`)**:
  - Implemented the Hoover behavioral quirk where dogs eagerly scavenge dropped food items from the ground even when at $100\%$ full health.
  - **Base Personality Rates**: Aggressive ($70\%$), Normal ($35\%$), Pacifist ($10\%$) with $[-100\%, +100\%]$ UUID variance offset.
  - **Digestion Cooldown**: Enforced a $160\text{ ticks}$ ($8\text{ seconds}$) cooldown between ground food snacks at full health to prevent vacuuming item stacks.
  - **Posture Safety**: Fully respects sitting commands (`isOrderedToSit()`); seated dogs will never break posture.

## [1.0.74+1.20.1] - 2026-08-22
### Changed
- 🧬 **Full-Spectrum [-100%, +100%] UUID Behavioral Variance (`WolfDispositionHelper.java`)**:
  - Expanded individual dog UUID offset range to $[-100\%, +100\%]$ (clamped to $[0\%, 100\%]$).
  - Applied base personality values:
    - **Fetch Reluctance**: Aggressive ($10\%$), Normal ($30\%$), Pacifist ($60\%$).
    - **Storm Fearlessness**: Aggressive ($80\%$), Normal ($40\%$), Pacifist ($10\%$).
    - **Quiet Howling**: Aggressive ($10\%$), Normal ($25\%$), Pacifist ($60\%$).

## [1.0.73+1.20.1] - 2026-08-22
### Changed
- 🧬 **Additive Modifier Behavioral Quirk Model (`WolfDispositionHelper.java`)**:
  - Implemented additive modifier variance model with wide personality spread:
    - **Fetch Reluctance**: Aggressive ($0\text{--}5\%$), Normal ($3\text{--}18\%$), Pacifist ($15\text{--}45\%$).
    - **Storm Fearlessness**: Aggressive ($90\text{--}100\%$), Normal ($10\text{--}35\%$), Pacifist ($0\text{--}15\%$).
    - **Quiet Howling**: Aggressive ($0\text{--}10\%$), Normal ($5\text{--}30\%$), Pacifist ($25\text{--}65\%$).
  - Combined personality baseline percentages with deterministic UUID offsets ($\pm 10\%$ to $\pm 20\%$).

## [1.0.72+1.20.1] - 2026-08-22
### Added
- 🧬 **Personality + UUID Seeded Behavioral Variance (`WolfDispositionHelper.java`)**:
  - Deterministically seeded individual behavioral nuances using 64-bit bit-mixing hashing over `UUID` + `WolfPersonality` + behavior salt (zero NBT storage).
  - ~5% of dogs are organically reluctant to fetch sticks; non-fetchers either curiously tilt their heads (`setIsInterested(true)`) or naturally ignore nearby thrown sticks.
  - ~10% of dogs are naturally fearless during thunderstorms, bypassing storm panic and whimpering.
  - ~15% of dogs are quiet observers who refrain from participating in nocturnal group howling choruses.

## [1.0.71+1.20.1] - 2026-08-22
### Changed
- ✨ **Subtle Particle Feedback for Fetch & Gift Delivery (`WolfFetchHelper.java`, `WolfGiftHelper.java`)**:
  - Reduced excessive happy villager particle burst when dogs return fetched sticks from 6 to a subtle 2 particles with tight spread (`0.15`) and gentle velocity (`0.02`).
  - Reduced morning scavenged gift delivery particle burst from 8 to 2 subtle particles, adhering to Vanilla Outsider aesthetic standards.

## [1.0.70+1.20.1] - 2026-08-18
### Fixed
- 🖼️ **Mod Icon Asset Restoration**:
  - Restored `assets/vanilla-outsider-better-dogs/icon.png` and mirrored it to `assets/betterdogs/icon.png`, resolving the blank default cube icon issue in Fabric Loader and Modrinth launcher.

## [1.0.69+1.20.1] - 2026-08-18 [YANKED - MISSING MOD ICON]
### Removed
- 🧹 **Obsolete Asset Namespace Cleanup**:
  - Purged legacy `assets/vanilla-outsider-better-dogs/` folder to optimize JAR distribution size and eliminate asset duplication.
  - Normalized `assets/betterdogs/lang/en_us.json` to clean 2-space indentation.

## [1.0.68+1.20.1] - 2026-08-18
### Changed
- ⚡ **Hot-Path Zero-Allocation Optimizations (`WolfSafetyMixin.java`, `WolfGuardGoal.java`)**:
  - Replaced intermediate `Vec3` lookahead allocations with direct primitive coordinate math (`Mth.floor`) in cliff safety loops.
  - Replaced `new AABB(guardPos).inflate(4.0)` with direct coordinate `AABB` instantiation in the Pacifist guard soothing aura loop.

## [1.0.67+1.20.1] - 2026-08-18
### Added
- 🥩 **Autonomous Ground Feeding Merit Tracking & Advancement (`EatGroundFoodGoal.java`)**:
  - Pre-filtered disliked/refused food items during ground food entity search scans.
  - Tracked feeding count (`feedCount`) on dogs consuming dropped food from the ground.
  - Added treat affinity rolling, zoomies trigger (120 ticks), and advancement dispatch (`favorite_treat`, `zoomies`) to the dog's online owner upon eating dropped favorite treats.

## [1.0.66+1.20.1] - 2026-08-18
### Fixed
- 📜 **Adoption Flow Polish & 6D Interaction Guarding (`WolfAdoptionHelper.java`, `WolfInteractMixin.java`)**:
  - Enforced strict `InteractionHand.MAIN_HAND` debounce across all adoption validation predicates and interaction handlers.
  - Required `Shift + Right-Click` with Paper for cancelling adoption listings, preventing empty-handed shift-click petting from accidentally cancelling adoption status.

## [1.0.65+1.20.1] - 2026-08-18
### Added
- 🐺 **Custom Chorus Howl SoundEvent Registration (`BetterDogsSoundEvents.java`, `sounds.json`, `WolfHowlHelper.java`)**:
  - Registered `betterdogs:entity.wolf.howl` in `BuiltInRegistries.SOUND_EVENT` to provide authentic wolf howling for Minecraft 1.20.1.
  - Defined `sounds.json` mapping pitch-modulated canine vocalizations (`minecraft:mob/wolf/whine`, `minecraft:mob/wolf/growl`) with subtitle support.
  - Implemented natural harmonic pitch spread ($0.85\text{F}$ to $1.20\text{F}$) for cascading pack chorus howls.

## [1.0.64+1.20.1] - 2026-08-17
### Fixed
- 🛡️ **Aggressive Target Goal Creeper Suicide Prevention (`AggressiveTargetGoal.java`)**:
  - Bound target predicate in constructor to filter out Creepers (suicide prevention) and Ghasts (unreachable flight) before target evaluation.
  - Tethered aggressive monster engagement within the owner's detection radius (`bd_aggro_detect_range`, default 16 blocks).

## [1.0.63+1.20.1] - 2026-08-17
### Fixed
- 🐾 **Post-Taming Personality Stat Re-Application (`TamableAnimalMixin.java`, `WolfAdoptionHelper.java`)**:
  - Prevented vanilla tame health reset from clobbering personality attributes (`MAX_HEALTH`, `ATTACK_DAMAGE`, `MOVEMENT_SPEED`, `SCALE`) upon taming.
  - Fully restored newly tamed dog health to 100% of its personality max health upon successful taming.
  - Enhanced adoption flow to re-apply personality stats and automatically un-sit the dog (`wolf.setOrderedToSit(false)`).

## [1.0.62+1.20.1] - 2026-08-17
### Added
- 📐 **Visual Entity Scaling Engine (`WolfRendererMixin.java`, `WolfMixin.java`)**:
  - Implemented network-synchronized physical scaling using `SynchedEntityData` (`DATA_SOCIAL_SCALE`).
  - Added client-side `WolfRendererMixin` scaling dog models with `poseStack.scale(s, s, s)`, ensuring runt puppies ($0.35\text{x}$) and giant dire wolves ($1.45\text{x}$) render with distinct visual stature.
  - Isolated client render mixin in `vanilla-outsider-better-dogs.mixins.json` `"client"` array for 100% headless server classloader safety.

## [1.0.61+1.20.1] - 2026-08-17
### Fixed
- 🐕 **Multi-Puppy Litter Spawning & Co-Parent Inheritance Fix (`WolfLitterHelper.java`, `CommandSuggestionsHelper.java`)**:
  - Fixed coordinate calculation for extra litter siblings (puppies 2–4): siblings now spawn in a safe $\pm 0.4$-block cluster adjacent to `parentA` rather than $(0, 0, 0)$ in the void.
  - Implemented 50/50 co-parent collar color inheritance: extra puppies roll a 50% chance to inherit Parent A's collar dye and 50% chance to inherit Parent B's collar dye.
  - Linked wild puppy pack hierarchy: untamed puppies seamlessly inherit their mother's pack leader UUID upon birth.
  - Cleaned up wildcard imports in `CommandSuggestionsHelper.java`.

## [1.0.60+1.20.1] - 2026-08-17
### Fixed
- 🐾 **Petting & Guard Mode Interaction Disentanglement (`WolfGuardHelper.java`, `WolfPettingHelper.java`, `WolfInteractMixin.java`)**:
  - Re-mapped Guard Mode sentinel post toggling to strictly require holding a **Bone** (`Items.BONE`) while sneaking, consuming 1 Bone per toggle (with creative bypass).
  - Activating Guard Mode now automatically un-sits the dog (`wolf.setOrderedToSit(false)`) so it immediately begins its designated territory patrol.
  - Shift + Right-Click with an **Empty Hand** is now exclusively reserved for intimate human-canine interactions (petting, clearing anger, soothing thunderstorm anxiety).
  - Added a 20-tick (1-second) anti-spam debounce on petting to prevent audio and particle spam when holding right-click.

## [1.0.59+1.20.1] - 2026-08-16
### Added
- 🌍 **Expanded Biome Spawning & Dynamic Climate Variants (`BetterDogsSpawning.java`, `WolfVariantHelper.java`)**:
  - Registered wild wolf spawning across expanded biomes (Plains, Savanna, Savanna Plateau, Windswept Savanna, Badlands, Wooded Badlands, Eroded Badlands, Meadow) using Fabric Biome API (`BiomeModifications.addSpawn`).
  - Implemented dynamic climate-personality trait biasing: wolves spawned in hot/arid biomes receive +20% Aggressive trait tendencies, while wolves spawned in cold/snowy biomes receive +20% Pacifist trait tendencies.
  - Implemented immediate spawn-time wild pack cluster synchronization, electing dominant Alpha leaders and linking pack follower UUIDs.
- Added automated test suite `WolfSpawnTest`.

## [1.0.58+1.20.1] - 2026-08-16
### Fixed
- 🚨 **Resolved Startup Crash (Superclass Mixin Target Mismatch)**:
  - Removed illegal `@Inject(method = "doHurtTarget")` from `WolfMixin.java` which failed Knot Mixin class transformation at bootstrap because `Wolf` does not declare `doHurtTarget` (it is inherited from `Mob.class`).
  - Moved the `4.0 HP` sustenance kill healing cleanly inside `HuntWhenHurtGoal.java` in pure Java, guaranteeing zero mixin bootstrap errors.
  - Supersedes yanked versions `1.0.53+1.20.1` through `1.0.57+1.20.1`.

## [1.0.57+1.20.1] - 2026-08-16 [YANKED / BROKEN ON STARTUP]
### Added
- 🌲 **Wanderlust Roaming AI (`WanderlustGoal.java`)**:
  - Tamed dogs occasionally experience spontaneous exploratory wandering surges (1 in 400 calm ticks, lasting 10 seconds / 200 ticks).
  - Explores the surrounding environment safely within an expanded 28-block perimeter from their owner.
  - Strictly respects sit commands, leash state, guard mode, and cancels immediately if threats arise or owner moves beyond 32 blocks.
- Added automated test suite `WanderlustTest`.

## [1.0.56+1.20.1] - 2026-08-16 [YANKED / BROKEN ON STARTUP]
### Added
- ⚔️ **Wild Wolf Pack War & Territorial Rivalry Matrix (`WildWolfPackWarGoal.java`, `WildWolfFollowLeaderGoal.java`, `WildWolfTerritorialGoal.java`)**:
  - Implemented dynamic wild pack alpha election and follower synchronization (`WildWolfFollowLeaderGoal`).
  - Added territorial howling standoffs between rival wild packs evaluating configured GameRule matrix chances (`bd_terr_*_war`, `bd_terr_*_merge`).
  - During pack wars, followers fight enemy pack followers within 16 blocks while alphas duel 1v1 (`WildWolfPackWarGoal`).
  - Upon alpha defeat/yield (health < 20%), remaining pack members submit and merge under the victorious alpha (`WolfTerritorialRivalryHelper`).
- Added automated test suite `PackWarTest`.

## [1.0.55+1.20.1] - 2026-08-16 [YANKED / BROKEN ON STARTUP]
### Added
- 🤺 **Play Fighting & Social Sparring AI (`SmallFightGoal.java`)**:
  - Tamed dogs of the same pack engage in harmless playful sparring/tussles with mock pounces and circling.
  - Spawns happy villager particles, plays panting/playful growl sounds, and deals 0 damage.
  - Concludes after ~6-7 seconds and applies a mutual 30-second cooldown (`betterdogs$setPlayFightCooldown(600)`).
  - Respects player sit commands and immediately breaks off if combat starts.
- Added automated test suite `SmallFightTest`.

## [1.0.54+1.20.1] - 2026-08-16 [YANKED / BROKEN ON STARTUP]
### Added
- 🩸 **Entity-to-Entity Blood Feud AI (`BloodFeudGoal.java`, `bd_blood_feud_percent`)**:
  - Implemented persistent wolf-to-wolf vendettas by entity UUID.
  - Wolves form lasting blood feuds upon severe disciplinary escalation (`bd_blood_feud_percent`, default 5%) or when a packmate is harmed.
  - Feuding wolves hunt down and duel their nemesis within 20 blocks until one perishes, bypassing standard friendly fire between rivals while respecting player sit commands.
- Added automated test suite `BloodFeudTest`.

## [1.0.53+1.20.1] - 2026-08-16 [YANKED / BROKEN ON STARTUP]
### Added
- 🐺 **Desperate Low-Health Wild Wolf Hunting (`HuntWhenHurtGoal.java`, `bd_wild_hunt_health_threshold`)**:
  - Wild wolves no longer indiscriminately massacre livestock at 100% full health.
  - Injured wild wolves with health falling below `bd_wild_hunt_health_threshold` (default `50%`) enter desperate hunting mode, targeting small prey (Sheep, Rabbits, Chickens, Foxes) within 16 blocks.
  - Slaying prey in low-health hunting mode directly grants `4.0 HP` (2 hearts) sustenance healing to simulate feasting on the kill.
- Added automated test suite `WildHuntTest`.

## [1.0.52+1.20.1] - 2026-08-16
### Added
- 💥 **Aggressive Puppy Retaliation & Bite Back AI (`BabyBiteBackGoal.java`, `bd_baby_retaliate_percent`)**:
  - Aggressive puppies no longer purely whine submissively when disciplined or attacked; they now retaliate with snap bites and growls.
  - Gated by `bd_baby_retaliate_percent` (default `50%`) upon adult discipline or incoming damage.
  - Delivers a 1.0 HP (0.5 heart) feisty snap nip with attack swing animation and growl sound before resuming normal behavior.
- Added automated test suite `BabyRetaliationTest`.

## [1.0.51+1.20.1] - 2026-08-16
### Added
- 🥩 **Feeding-Gated Merits & Gift System (`WolfGiftGoal.java`, `WolfGiftHelper.java`, `WolfPersistentData.java`)**:
  - Implemented persistent feeding merits tracking (`feedCount`) incremented each time the dog is fed.
  - Dogs require $\ge 10$ feeding merits (`bd_gift_feed_threshold`) before bringing gifts, consuming 10 merits upon delivery.
  - Added accidental player attack demerit penalty (`bd_demerit_accidental_attacks`) resetting accumulated merits upon friendly fire.
- 🐾 **Follower Pack Spacing Offset (`PersonalityFollowOwnerGoal.java`)**:
  - Implemented `FollowerSpacingCache` and dynamic $\sqrt{N-1}$ pack spread offset expanding follower start/stop distances, eliminating clustering and crowding at the player's feet.
  - Added GameRules `bd_tamed_pack_spread_multiplier` (default `100`) and `bd_tamed_pack_spread_max` (default `50`).
- 🌸 **Puppy Curiosity AI (`BabyCuriosityGoal.java`)**:
  - Puppies explore natural vegetation and observe critters when idle.
- Added automated test suite `WolfGiftMeritsTest` and `BabyCuriosityTest`.

## [1.0.50+1.20.1] - 2026-08-16
### Added
- 🪑 **Dog Mounting & Vehicle/Seat Boarding Command Suite (`DogCommandManager.java`, `MoveToVehicleGoal.java`, `BetterDogsTags.java`)**:
  - Implemented stick/command item interaction allowing players crouching with a command item (Stick, Blaze Rod) to select an owned tamed dog and command it to board a vehicle (Boat, Minecart, Horse, Camel, Saddled Pig/Strider) or stair/chair block within 12 blocks.
  - Added `MoveToVehicleGoal` enabling dogs to navigate at 1.25x speed directly to the targeted seat and mount smoothly.
  - Added shift-right-click dismounting command when interacting with a seated dog using a command item.
  - Registered `#vanilla-outsider-better-dogs:command_items` item tag.
  - Added namespaced boolean GameRule `bd_allow_unrestricted_dog_riding` (default: `false`).
  - Added automated unit test suite `DogCommandTest`.

## [1.0.49+1.20.1] - 2026-08-16
### Added
- ⚡ **Full Brigadier `/betterdogs` & `/bd` In-Game Command Suite (`BetterDogsCommand.java`, `CommandSuggestionsHelper.java`)**:
  - Implemented complete in-game Brigadier command tree with root `/betterdogs` and convenient short alias `/bd`.
  - **Welcome & Help** (`/betterdogs`, `/betterdogs help`): Displays formatted syntax reference and command guides for players and administrators.
  - **Categorized Companion Status** (`/betterdogs status`): Visual, color-coded diagnostic overview displaying active states across Personalities & Stats, Environmental Safety, Combat/Tactics, and Genetics/Breeding.
  - **Rule Querying with Flexible Resolution** (`/betterdogs get <rule>`): Queries active GameRule values with full tab completion and shorthand matching (e.g., `cliff_safety` resolves to `bd_cliff_safety`).
  - **Permission-Gated Rule Modification** (`/betterdogs set <rule> <val>`): Allows operators (Permission Level 2) to dynamically adjust boolean toggles and integer modifiers on the fly.
  - **Factory Reset & State Sync** (`/betterdogs reset`, `/betterdogs reload`): Instantly restores all 80+ companion GameRules to factory defaults or confirms active state synchronization.
  - Added automated unit test suite `CommandSuiteTest`.

## [1.0.48+1.20.1] - 2026-08-16
### Added
- 🏆 **Dedicated 13 Husbandry Advancements Tree (`WolfAdvancementHelper.java`)**:
  - Implemented 13 custom advancements nested under Vanilla's Husbandry tree (`minecraft:husbandry/tame_an_animal`):
    - **Man's Best Companion** (`tame_wolf`, Task): Tame a canine companion with an authentic personality.
    - **Who's a Good Dog?** (`pet_dog`, Task): Pet a tamed dog with an empty hand.
    - **Safe and Sound** (`soothe_dog`, Task): Soothe a trembling or weather-panicked dog during a thunderstorm.
    - **Gourmet Canine** (`favorite_treat`, Task): Discover and feed your companion their individual favorite treat.
    - **The Zoomies** (`zoomies`, Task): Trigger a sprint burst by giving your dog its favorite snack.
    - **Fetch Master** (`fetch_stick`, Task): Throw a stick and have your dog retrieve it for you.
    - **Sentinel on Duty** (`guard_mode`, Task): Place a loyal dog on sentinel guard patrol mode.
    - **Morning Surprise** (`morning_gift`, Task): Receive a morning gift from your sleeping companion.
    - **Second Chance** (`adopt_dog`, Task): Successfully adopt a listed dog using an adoption paper certificate.
    - **Pack Conductor** (`horn_command`, Task): Command a pack of dogs using a Goat Horn.
    - **Genetic Restoration** (`cure_inbred`, Goal): Cure an inbred runt defect with a Golden Apple.
    - **Nocturnal Symphony** (`chorus_howl`, Goal): Witness or participate in a nocturnal pack chorus howl under the moonlight.
    - **Apex Dynasty** (`giant_lineage`, Challenge): Successfully breed a giant-scale (1.25x+) companion dog through generational lineage.
  - Added automated unit test suite `AdvancementCriteriaTest`.

## [1.0.47+1.20.1] - 2026-08-16
### Added
- 💬 **Configurable Subtitle & Actionbar Feedback Options (`WolfFeedbackHelper.java`, `BetterDogsGameRules.java`)**:
  - Added namespaced boolean GameRule `bd_actionbar_feedback` (default: `false`) to give players and server hosts full control over companion HUD notifications.
  - When disabled (`false`), HUD/actionbar subtitle text is kept clean and unobtrusive while preserving all natural in-world particle bursts and authentic sound effects.
  - Gated text notifications include: Taming Personality Announcements, Adoption Listing/Cancellation/Adoption alerts, Guard Mode Coordinate Status, and Morning Gift Arrivals.
  - Added automated unit test suite `SubtitleFeedbackTest`.

## [1.0.46+1.20.1] - 2026-08-16
### Added
- 🐺 **Wild Pack Dynamics & Territorial Chorus Howling (`WolfHowlHelper.java`, `GroupHowlGoal.java`, `WildWolfTerritorialGoal.java`)**:
  - Implemented nocturnal skyward chorus howling where wolves tilt their heads skyward (pitch $-45^\circ$) for 3 seconds (60 ticks), prompting nearby pack members within 24 blocks to join in sequential chorus with natural randomized pitch variations.
  - Full moon phases significantly increase the frequency of nocturnal pack howling.
  - Implemented untamed pack territorial dynamics where wild wolves encountering rival wolves within 12 blocks perform threat posturing: Aggressive wolves aggressively challenge and stalk intruders, Pacifist wolves yield and retreat, and Normal wolves hold territorial standoff boundaries.
  - Added automated unit test suite `WildPackDynamicsTest`.

## [1.0.45+1.20.1] - 2026-08-16
### Added
- 🐾 **Puppy Mischief & Adult Disciplinary Correction AI (`WolfMischiefHelper.java`, `BabyMischiefGoal.java`, `AdultCorrectionGoal.java`)**:
  - Puppies playfully bound and stalk towards nearby adult wolves (within 8 blocks) or small critters (chickens, rabbits) at $1.2\times$ speed, nipping playful tail/paw bounces.
  - Adult dogs discipline overly hyperactive misbehaving puppies with a low warning growl (`SoundEvents.WOLF_GROWL`) and a harmless 0-damage disciplinary tap.
  - Disciplined puppies immediately whimper softly (`SoundEvents.WOLF_WHINE`) and enter an 8-second (160 ticks) calm submissive state before resuming normal play.
  - Added automated unit test suite `PuppyMischiefTest`.

## [1.0.44+1.20.1] - 2026-08-16
### Added
- 🎾 **Stick Fetching AI & Favorite Treat Zoomies (`WolfFetchHelper.java`, `WolfFetchGoal.java`, `ZoomiesGoal.java`)**:
  - Implemented stick-throwing fetch mini-game where companion dogs actively track down dropped sticks within 16 blocks at $1.25\times$ speed, pick them up, and return them directly to the owner's feet with happy barks and green sparkles (`ParticleTypes.HAPPY_VILLAGER`).
  - Implemented favorite treat "zoomies" behavior where dogs fed their individual favorite treat enter a 6-second (120 ticks) burst of playful $1.5\times$ sprint loops around the owner with heart/cloud/sparkle trails.
  - Added automated unit test suite `FetchAndZoomiesTest`.

## [1.0.43+1.20.1] - 2026-08-16
### Added
- 🎁 **Morning Gift Bringing AI (`WolfGiftHelper.java`, `WolfGiftGoal.java`)**:
  - Healthy, full-health companion dogs sleeping near their owner wake up and deliver daily morning gifts upon the owner waking from bed.
  - Implemented personality-themed foraging loot pools:
    - **Aggressive**: Bone, Leather, Rotten Flesh, Rabbit Hide, Spider Eye, Arrow.
    - **Pacifist**: Sweet Berries, Apple, Dandelion, Poppy, Honeycomb, Wheat Seeds.
    - **Normal**: Stick, Feather, Flint, String, Clay Ball.
    - **5% Rare Treasure Tier**: Gold Nugget, Emerald, Name Tag, Bone Meal.
  - Companion approaches owner, drops item drop entity at player's feet, barks happily with green sparkle particles (`ParticleTypes.HAPPY_VILLAGER`), and displays Action Bar notice.
  - Tracks `BetterDogsLastGiftDay` NBT key ensuring exactly 1 gift delivery per in-game day.
  - Added automated unit test suite `MorningGiftTest`.

## [1.0.42+1.20.1] - 2026-08-15
### Added
- 📄 **Paper Adoption Ownership Transfer System**:
  - Sneak (Shift) + Right-clicking a tamed dog with `Items.PAPER` lists the dog for adoption (`WolfAdoptionHelper`).
  - Added ambient subtle green/golden sparkles (`ParticleTypes.HAPPY_VILLAGER`) and Action Bar notification indicating the dog is up for adoption.
  - Another player can adopt the listed dog by Sneak (Shift) + Right-clicking with an empty hand, transferring full ownership with heart explosion FX (`ParticleTypes.HEART`) and chime celebration (`SoundEvents.PLAYER_LEVELUP`).
  - The original owner can cancel adoption listing at any time by Sneak + Right-clicking again.
  - Added automated unit test suite `AdoptionSystemTest`.

## [1.0.41+1.20.1] - 2026-08-15
### Added
- 🛡️ **Guard Mode & Sentinel Patrol AI**:
  - Sneak (Shift) + Right-clicking a tamed dog with an empty hand toggles anchored Guard Mode on/off at its current location (`WolfGuardHelper`).
  - Added modern Action Bar HUD feedback displaying `"Guard Mode: Active (X, Y, Z)"` or `"Guard Mode: Inactive"` with shield FX (`ParticleTypes.ENCHANT`) and armor chime.
  - Implemented personality-scaled patrol radii: Aggressive (12-block combat patrol), Pacifist (4-block perimeter + periodic Regeneration I aura), Standard (8-block territory patrol).
  - Dogs actively patrol between waypoints within their assigned territory and automatically leash/sprint back if pushed or lured outside (`WolfGuardGoal`).
  - Added automated unit test suite `GuardModePatrolTest`.

## [1.0.40+1.20.1] - 2026-08-15
### Added
- 🍏 **Golden Apple Inbred Curing Interaction**:
  - Feeding a Golden Apple or Enchanted Golden Apple to an inbred runt dog completely cleanses inbred status (`WolfCureHelper`).
  - Restores full adult size scale potential ($\ge 1.0x$ scale) and full personality combat attributes (removing runt penalties).
  - Enchanted Golden Apples grant temporary Absorption and Regeneration status buffs.
  - Added rewarding celebration FX: green sparkle burst (`ParticleTypes.HAPPY_VILLAGER`) and uplifting level-up chime (`SoundEvents.PLAYER_LEVELUP`).
  - Added automated unit test suite `InbredCureTest`.

## [1.0.39+1.20.1] - 2026-08-15
### Added
- 🧪 **Inbreeding Lineage Tracking & Runt Stat Penalties**:
  - Implemented 1-generation immediate family lineage tracking storing parent UUIDs (`parentUUID1`, `parentUUID2`) on all dogs.
  - Automatically detects parent-child and sibling (full/half) incestuous pairings upon breeding (`WolfInbreedingHelper`).
  - Marked inbred dogs receive authentic runt debuffs: adult scale capped to 0.70x–0.80x, -25% Max Health, -25% Attack Damage, and -15% Movement Speed.
  - Added visual smoke tell: dense smoke puff on birth and subtle ambient smoke particles while moving.
  - Added automated unit test suite `InbreedingLineageTest`.

## [1.0.38+1.20.1] - 2026-08-15
### Added
- 🐕 **Variable Multi-Puppy Litter Sizes**:
  - Implemented realistic canine litter sizes rolling 1 to 4 puppies (`bd_wolf_litter_max_size`, `WolfLitterHelper`).
  - Sibling puppies independently roll personality and adult scale genetics from parents, inheriting owner and mother collar color.
  - Added subtle celebration birth FX with heart particles and gentle high-pitched puppy whimpers (`SoundEvents.WOLF_AMBIENT`).
  - Added automated unit test suite `VariableLitterTest`.

## [1.0.37+1.20.1] - 2026-08-15
### Added
- 📏 **Parental Size Inheritance & Scale Variance**:
  - Implemented mathematical adult size inheritance from parents with natural ±10% genetic variance (`WolfScaleGeneticsHelper`).
  - Clamped strictly between `bd_wolf_min_scale_percent` (0.70x) and `bd_wolf_max_scale_percent` (1.45x).
  - Added natural Gaussian bell-curve generation for freshly spawned wild wolves.
  - Added automated unit test suite `ScaleGeneticsTest`.

## [1.0.36+1.20.1] - 2026-08-15
### Added
- 🧬 **Genetic Personality Inheritance**:
  - Implemented weighted parental trait inheritance when breeding tamed wolves (`WolfGeneticsHelper`).
  - **Same-Trait Parents**: 80% chance to inherit parent personality (`bd_breed_same_chance`), with 10% mutation chance (`bd_breed_same_other_chance`).
  - **Mixed-Trait Parents**: 40% Parent A (`bd_breed_mixed_dominant_chance`), 40% Parent B (`bd_breed_mixed_recessive_chance`), 20% Normal dilution (`bd_breed_diluted_normal_chance`).
  - Added automated unit test suite `GeneticsInheritanceTest`.

## [1.0.35+1.20.1] - 2026-08-15
### Added
- 💖 **Personality Dynamic Stat Scaling & Attribute Modifiers**:
  - Dynamically recalculates max health, attack damage, and base movement attributes when personalities are assigned or loaded (`WolfPersonalityStatHelper`).
  - **Aggressive Dogs**: Enhanced attack power (`bd_aggro_damage`), speed scaling (`bd_aggro_speed_percent`), and configured health (`bd_aggro_health`).
  - **Pacifist Dogs**: High-vitality guardians with increased max health (`bd_paci_health`) and gentle damage (`bd_paci_damage`).
  - Added automated unit test suite `PersonalityStatScalingTest`.

## [1.0.34+1.20.1] - 2026-08-15
### Added
- 🚀 **Fast-Travel Sprint Catchup & Dimension Teleport Sync**:
  - Implemented dynamic catch-up sprint speed scaling (up to 2.0x base speed) whenever owners travel rapidly on mounts (horses, donkeys, camels), boats, minecarts, elytra, or high-speed sprinting (`bd_fast_travel_catchup`).
  - Added interdimensional portal teleport sync helper `WolfCatchupHelper.syncOwnerDimensionTeleport` (`bd_sync_owner_teleport`).
  - Added automated unit test suite `FastTravelCatchupTest`.

## [1.0.33+1.20.1] - 2026-08-15
### Added
- 🛡️ **Friendly Fire Protection & Owner Damage Safeguards**:
  - Intercepts and completely cancels accidental player weapon sweeps, stray projectiles/arrows, and direct owner melee hits against owned dogs.
  - Intercepts and cancels friendly infighting between dogs belonging to the same owner.
  - Fully configurable via the namespaced `bd_friendly_fire` GameRule (defaults to `false` = protected; setting `true` enables vanilla damage).
  - Added automated unit test suite `FriendlyFireTest`.

## [1.0.32+1.20.1] - 2026-08-15
### Added
- 😡 **3-Day Nemesis Memory & Pack Vendetta Combat AI**:
  - Tamed wolves now remember and avenge fallen pack mates or owners killed by hostile mobs.
  - When a tamed wolf dies in combat, the killer's `EntityType` is broadcast to all pack wolves belonging to the same owner within 64 blocks, persisting for 3 in-game days (72,000 game ticks, controlled by `bd_nemesis_duration_days`).
  - Added `WolfNemesisTargetGoal` prioritizing the pack's active Nemesis mob type at highest targeting priority.
  - Added automated unit test suite `NemesisGrudgeTest`.

## [1.0.31+1.20.1] - 2026-08-15
### Fixed
- 🥩 **Full-HP Wolf Breeding & Food Refusal Fix**:
  - Corrected `DogTreatHelper.shouldRefuseFood` logic so tamed wolves at full health can still be fed meat to enter breeding love mode (`canFallInLove()`) or grow baby puppies (`isBaby()`).
  - Food refusal now strictly activates when wolves are adult, at full HP, and unable to breed (already in love or on breeding cooldown).

## [1.0.30+1.20.1] - 2026-08-15
### Added
- 📯 **Acoustic Goat Horn Commands & Pack Horn Overrides**:
  - Implemented tactical acoustic pack commanding via Goat Horn variants across a 64-block range (`bd_horn_command_range`).
  - **Ponder Horn**: Emits an Assemble/Rally signal directing following dogs to path to the sounding player's location (`WolfHornGoal`).
  - **Yearn Horn**: Issues a Stand Up/Resume Follow order commanding sitting dogs to immediately stand and follow.
  - **Sing Horn**: Issues a Hold/Sit command ordering active dogs to sit and clearing all combat targets.
  - **Feel Horn**: Emits a 30-second Pacifist/Calm override (`bd_horn_override_duration`) suppressing aggressive hostile target acquisition.
  - **Seek Horn**: Directs the pack to hunt down and attack the owner's crosshair target or nearest hostile monster.
  - Added automated unit test suite `GoatHornCommandTest`.

## [1.0.29+1.20.1] - 2026-08-15
### Added
- 🖐️ **Shift + Right-Click Petting & Soothe Calming Mechanic**:
  - Owners can now crouch and right-click their tamed dogs with an empty hand to gently pet them.
  - Petting calms active anger, emits comforting heart and note particles with whimper/pant vocalizations, and soothes thunderstorm anxiety for 10 minutes (12,000 game ticks).
  - Added automated unit test suite `PettingSootheTest`.

## [1.0.28+1.20.1] - 2026-08-15
### Added
- ⚡ **Thunderstorm Anxiety & Shelter Seeking AI**:
  - Tamed dogs exhibit realistic anxiety during thunderstorms, whimpering and actively seeking covered indoor shelter blocks.
  - Added personality-based storm anxiety scaling (Pacifists experience 3x anxiety, Aggressive dogs remain fearless on guard).

## [1.0.27+1.20.1] - 2026-08-15
### Added
- 🥩 **Food Refusal & Favorite Treats Begging AI**:
  - Tamed dogs now refuse player-fed food when at full health with a head-tilt animation and whimper response.
  - Dogs discover a permanent favorite treat affinity upon feeding, granting +100% bonus healing and enhanced begging behavior.

## [1.0.26+1.20.1] - 2026-08-15
### Added
- 🍖 **Autonomous Ground Food Foraging & Healing**:
  - Injured tamed dogs automatically detect and seek dropped meat items within 10 blocks to heal themselves.
  - Added food item classification respecting raw and cooked food GameRule toggles.

## [1.0.25+1.20.1] - 2026-08-15
### Added
- 🐺 **Tactical Pack Flanking Encirclement AI**:
  - Implemented multi-angle pack encirclement AI allowing dogs targeting the same entity to spread out into distinct flanking slots rather than clumping.
  - Added line-of-sight and clear terrain raycasting for tactical approach maneuvers.

## [1.0.24+1.20.1] - 2026-08-15
### Changed
- 🛡️ **Single-Purpose Architecture Alignment**:
  - Decoupled cliff edge safety and thermal hazard helpers into dedicated single-responsibility classes (`WolfCliffSafetyHelper` and `WolfHazardHelper`).

## [1.0.23+1.20.1] - 2026-08-15
### Added
- 🔥 **Emergency Thermal Safety & Hazard Evasion**:
  - Implemented automatic evasive backstep when standing within 1 block of Lava, Fire, Magma, or lit Campfires.
  - Implemented autonomous water-seeking extinguish response when burning.
  - Added automated test suite `HazardReactionTest`.

## [1.0.22+1.20.1] - 2026-08-15
### Added
- 🔥 **Active Hazard Detour Navigation**:
  - Implemented `AvoidHazardsGoal` inspecting forward path nodes and intercepting navigation when trajectories lead into Lava, Fire, Magma, or lit Campfires.
  - Prioritized hazard safety at priority 2 in wolf goal selection.
  - Added automated test suite `HazardDetourTest`.

## [1.0.21+1.20.1] - 2026-08-15
### Added
- 🔥 **Thermal Hazard Identification Utility**:
  - Implemented `WolfHazardHelper` providing centralized thermal scanning and classification for Lava, Fire, Soul Fire, Magma Blocks, and lit Campfires (`CampfireBlock.LIT`).
  - Added fast radius-based proximity checks for hazard avoidance navigation.
  - Added automated test suite `HazardDetectionTest`.

## [1.0.20+1.20.1] - 2026-08-14
### Added
- 🏔️ **Cliff Edge Fall Safety Navigation**:
  - Implemented dynamic drop height probing (`WolfSafetyMixin` and `WolfSafetyHelper`) that halts forward navigation, zeroes horizontal momentum, and triggers sneak stance when approaching lethal ledge drops ($>3$ blocks down).
  - Implemented push collision protection (`WolfPushMixin`) preventing entities and mobs from shoving sitting or standing dogs over cliff edges or into hazards.
  - Implemented combat ledge retreat: wolves break pursuit if their combat target plunges over a cliff.
  - Controlled by GameRule `bd_cliff_safety` (default: `true`).

## [1.0.19+1.20.1] - 2026-08-14
### Changed
- 🎨 **Bold Yellow Category Header**: Styled the `BETTER_DOGS` GameRules category name with bold yellow formatting (`ChatFormatting.BOLD, ChatFormatting.YELLOW`) to seamlessly match vanilla category headers in the Edit Game Rules UI screen.

## [1.0.18+1.20.1] - 2026-08-14
### Added
- 🏷️ **Dedicated 'Better Dogs' GameRules Category**:
  - Registered custom `BETTER_DOGS` category via `CustomGameRuleCategory` so all 80+ mod GameRules appear in their own section in the in-game world creation and edit gamerules menu.
  - Aligned all `en_us.json` GameRule titles and tooltip description keys (`gamerule.bd_*`).

## [1.0.17+1.20.1] - 2026-08-14
### Added
- 🤺 **Pacifist Defensive Retaliation AI (`PacifistRevengeGoal`)**:
  - Pacifist wolves now defensively counter-attack hostiles that deal damage directly to the wolf or its owner (`getLastHurtByMob()`).
  - Strict non-aggression: Pacifist wolves will not assist offensive owner attacks and will never target friendly puppies or creepers.

## [1.0.16+1.20.1] - 2026-08-14
### Added
- ⚙️ **80+ GameRules Registry Foundation**:
  - Implemented `BetterDogsGameRules` registering all 80+ namespaced GameRules via Fabric API `GameRuleRegistry.register("bd_*", ...)`.
  - Added NPE-safe static helper accessors `BetterDogsGameRules.getBoolean()` and `BetterDogsGameRules.getInt()`.
  - Added automated test suite `BetterDogsGameRulesTest` asserting registry keys and null-safety.

## [1.0.15+1.20.1] - 2026-08-14
### Changed
- **Exact Minecraft Target Constraint**: Updated `fabric.mod.json` `minecraft` dependency bound to target exact version `"minecraft": "1.20.1"`.

## [1.0.14+1.20.1] - 2026-08-14
### Added
- ⚔️ **Combat AI & Target Selection Mixins**:
  - `OwnerHurtTargetGoalMixin`: Enforces strict Pacifist combat ethics (Pacifist dogs never initiate or assist offensive player attacks; non-aggressive puppies skip offensive targets).
  - `OwnerHurtByTargetGoalMixin`: Implements the domestic Mercy Rule (adult dogs will not attack pet puppies even if the owner took damage from puppy discipline).
  - `HurtByTargetGoalMixin`: Silences pack-wide alarm broadcasts during domestic puppy scuffles.

## [1.0.13+1.20.1] - 2026-08-14
### Added
- 🖼️ **Mod Icon Asset**: Deployed official `icon.png` (235 KB) into `assets/vanilla-outsider-better-dogs/` and registered it in `fabric.mod.json`.
- 📜 **License Packaging**: Embedded GNU GPLv3 `LICENSE` file into release JAR distribution.
- 🎵 **Sound Resources & Audio**: Added `sounds.json` and custom wolf howling audio tracks (`howl1.ogg`, `howl2.ogg`).
- 🌐 **Base Localization**: Deployed `lang/en_us.json` resource bundle.

## [1.0.12+1.20.1] - 2026-08-14
### Added
- 💣 **`FleeCreeperGoal` (100% 26.2 Parity)**: Tamed wolves detect ignited or swelling Creepers within 10 blocks and sprint away at `1.5x` speed with smoke particle trails.
- 🩹 **`WolfFleeLowHealthGoal` (100% 26.2 Parity)**: Wolves below 30% health automatically retreat from combat to preserve life.

## [1.0.11+1.20.1] - 2026-08-14
### Changed
- **Wolf Movement & Sprint Speed Boost**: Upgraded `PersonalityFollowOwnerGoal` base follow speed modifier from `1.0` to `1.25` (fast trot), with `1.35x` sprint catch-up multiplier when > 8 blocks away.

## [1.0.10+1.20.1] - 2026-08-14
### Fixed
- **`fabric.mod.json` `${version}` Expansion Fix**: Added `processResources` property expansion block into `build.gradle`.

## [1.0.9+1.20.1] - 2026-08-14
### Fixed
- 💥 **Taming Particle Level Crash Fix (`NoSuchMethodError`)**: Replaced `wolf.level()` with `wolf.getCommandSenderWorld()`.

## [1.0.8+1.20.1] - 2026-08-14
### Fixed
- 💥 **`TamedWanderNearOwnerGoal` Crash Fix (`NoSuchMethodError`)**: Replaced `owner.position()` with primitive getters.

## [1.0.7+1.20.1] - 2026-08-14
### Fixed
- 💥 **Taming Crash Fix (`NoSuchMethodError`)**: Replaced `wolf.level()` with `wolf.getRandom()`.

## [1.0.6+1.20.1] - 2026-08-14
### Added
- **Proactive `AggressiveTargetGoal` (`NearestAttackableTargetGoal<Monster>`)**.

## [1.0.5+1.20.1] - 2026-08-13
### Fixed
- **Entity AI Goal Registration (`Wolf.registerGoals`)**.

## [1.0.4+1.20.1] - 2026-08-13
### Added
- **Personality Combat AI & Target Selection Rules (`PersonalityTargetGoal`)**.

## [1.0.3+1.20.1] - 2026-08-13
### Added
- **Personality Follow & Teleport AI System (`PersonalityFollowOwnerGoal`)**.

## [1.0.2+1.20.1] - 2026-08-11
### Fixed
- **One-Time Tame Notification**.

## [1.0.1+1.20.1] - 2026-08-11
### Added
- **Distinct Tame Particles**.

## [1.0.0+1.20.1] - 2026-08-11
### Added
- Initial project scaffolding for Minecraft 1.20.1.
