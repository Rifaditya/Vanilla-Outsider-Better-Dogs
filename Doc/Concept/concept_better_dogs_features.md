# 🐶 Vanilla Outsider: Better Dogs — Master Feature & Behavior Catalog

**Mod ID**: `vanilla-outsider-better-dogs`  
**Design Philosophy**: **Vanilla Outsider (VO)** — Enhancing vanilla canine companions with organic depth, psychological profiles, genetic inheritance, smart environmental survival, and emergent pack dynamics while preserving zero-invasive vanilla asset harmony.

---

## 🧭 Multi-Era Target Matrix

| Technical Area | MC 1.20.1 Legacy | MC 1.21.1 / 1.21.11 Trans. | MC 26.x+ Sovereign |
| :--- | :--- | :--- | :--- |
| **Java Release Level** | `Java 17` (`release = 17`) | `Java 21` (`release = 21`) | `Java 25+` (`release = 25`) |
| **Visual Scaling Engine**| `SynchedEntityData` + `WolfRendererMixin` | Native `Attributes.SCALE` | Native `Attributes.SCALE` |
| **Resource Locations** | `new Identifier("ns", "path")` | `Identifier.of("ns", "path")` | `Identifier.fromNamespaceAndPath(ns, path)` |
| **GameRule Engine** | Fabric `GameRuleRegistry.register` | Fabric `GameRuleRegistry.register` | Vanilla Registries / DynamicGameRuleManager |
| **Persistence Engine** | NBT `CompoundTag` | NBT / `DataComponents` | Codec `SavedDataType` Records |

---

# 📚 Exhaustive Mechanics & Feature Catalog

```
═════════════════════════════════════════════════════════════════════════════════
          COMPLETE 39-FEATURE BEHAVIORAL & ARCHITECTURAL SPECIFICATION
═════════════════════════════════════════════════════════════════════════════════
```

---

## 🧠 Domain 1: Canine Personalities & Psychological Profiles

Every wolf in the world possesses one of three distinct personality archetypes rolled at birth or spawn:

### 1. ⚔️ Aggressive Personality
* **Combat Stance**: Proactive hunter and fierce defender. Actively scans up to 20 blocks for hostile monsters (`AggressiveTargetGoal`) and prioritizes engaging threats before they reach the owner.
* **Stat Attributes**: $+15\%$ Attack Damage, $+15\%$ Sprinting Speed, $-10$ Base Max Health offset (20 HP tamed).
* **Follow Distance**: Loose leash (50 blocks follow start radius); roams wide while defending perimeter.
* **Flee Chance on Critical HP**: Only $10\%$ chance to retreat when severely injured.
* **Vocalizations**: Low, resonant growls and sharp barks (0.8x sound pitch).
* **Guard Mode**: 12-block active combat patrol radius.

### 2. 🛡️ Pacifist Personality
* **Combat Stance**: Gentle guardian and companion. Never initiates combat; only retaliates defensively when self or owner is struck (`PacifistRevengeGoal`).
* **Stat Attributes**: $+20$ Base Max Health (50 HP tamed), $+50\%$ Defensive Knockback Dealt, $-15\%$ Attack Damage, $-10\%$ Movement Speed.
* **Follow Distance**: Tight leash (5 blocks follow start radius); stays glued to the owner's side.
* **Flee Chance on Critical HP**: $100\%$ tactical retreat when health drops below $30\%$.
* **Vocalizations**: Gentle whines and soft high-pitched barks (1.5x sound pitch).
* **Guard Mode**: 4-block compact soothing post with passive **Regeneration I aura** for injured owners/allies.

### 3. ⚖️ Normal Personality
* **Combat Stance**: Balanced vanilla-plus temperament. Defends owner when attacked or when owner attacks.
* **Stat Attributes**: Baseline stats (30 HP tamed, 4.0 attack damage, 0.30 movement speed).
* **Follow Distance**: Balanced 10-block follow start radius.
* **Flee Chance on Critical HP**: $50\%$ tactical retreat chance on critical health.
* **Vocalizations**: Standard canine acoustic set (1.2x sound pitch).
* **Guard Mode**: 8-block sentry patrol radius.

---

## 🧬 Domain 2: Genetics, Anatomy & Physical Scaling

### 4. 📏 Visual Social Scaling & DNA Seeds
* **Scale Range**: $0.70\text{x}$ (Runt) up to $1.45\text{x}$ (Giant Dire Wolf).
* **DNA Seed**: Persistent long derived uniquely from the wolf's UUID (`WolfPersistentData`).
* **Biometric Inheritance**: Offspring compute their adult scale from the arithmetic mean of both parents with $\pm 10\%$ genetic variance (`WolfScaleGeneticsHelper`).
* **Advancement**: Breeding a dog to $\ge 1.25\text{x}$ scale unlocks the *"Dire Wolf Bloodline"* (`giant_lineage`) advancement.

### 5. ⚠️ Inbreeding Lineage Tracking & Runt Penalties
* **Lineage Tracking**: Every dog remembers its Parent 1 UUID and Parent 2 UUID in persistent NBT (`WolfInbreedingHelper`).
* **Detection**: Sibling $\times$ sibling or parent $\times$ offspring breeding triggers inbreeding.
* **Runt Penalty**: Inbred puppies are strictly size-capped to $0.70\text{x} - 0.80\text{x}$, suffer a $25\%$ reduction in health/attack damage and $15\%$ speed penalty, and emit a subtle gray smoke particle trail when moving.

### 6. 🍏 Golden Apple Runt Curing
* **Interaction**: Feeding a Golden Apple (or Enchanted Golden Apple) to an inbred runt cures the genetic defect (`WolfCureHelper`).
* **Effects**: Cleanses runt NBT tag, restores scale to healthy $\ge 1.0\text{x}$ stature, recalculates personality stats, plays zombie villager cure chimes with green emerald sparkles, and grants the *"A Second Chance"* (`cure_inbred`) advancement.

---

## 🐾 Domain 3: Reproduction & Multi-Puppy Litters

### 7. 🐕 Variable Multi-Puppy Litter Spawning
* **Litter Size**: Breeding wolves rolls a dynamic litter of **1 to 4 puppies** ($45\%$ 1, $35\%$ 2, $15\%$ 3, $5\%$ 4 puppies, capped by `bd_wolf_litter_max_size`) via `WolfLitterHelper`.
* **Parental Trait Distribution**: Each sibling independently rolls its personality, coat coloration, collar color, and physical scale from parental genetic matrices.
* **XP Balance**: Extra siblings spawn without duplicating player XP orbs or love particles.

---

## ⚔️ Domain 4: Combat Intelligence & Tactical AI

### 8. 📐 Tactical Raycast Pack Flanking Encirclement (`WolfFlankAttackGoal`)
* **Behavior**: When multiple dogs engage the same target, they dynamically calculate angular offsets ($\pm 60^\circ, \pm 120^\circ$) via raycasting around the victim rather than marching in a single-file line.
* **Result**: Pack surrounds the enemy in an encirclement ring, splitting mob aggro and eliminating friendly entity collisions.

### 9. 💀 3-Day Nemesis Memory & Pack Vendetta (`WolfNemesisTargetGoal`)
* **Trigger**: When an owned dog or its owner is slain, the killer mob's Entity Type is recorded as a persistent `Nemesis` (`WolfNemesisHelper`).
* **Behavior**: For **3 in-game days** (72,000 ticks), all pack dogs gain $+20\%$ speed and Strength buffs against that mob type, prioritizing hunting them down with angry villager particle trails.

### 10. 🩸 Entity-to-Entity Blood Feud AI (`BloodFeudGoal`)
* **Behavior**: Disputing rival wolves can form permanent personal vendettas against a specific rival UUID. They duel to the finish while strictly respecting owner sit commands.

### 11. 🏃 Low-Health Tactical Evasion (`WolfFleeLowHealthGoal`)
* **Behavior**: When a dog's health falls below $30\%$ HP, it disengages from combat, sprints away at $1.4\text{x}$ speed to safe distance, and seeks cover until healed.

### 12. 💥 Swelling Creeper Blast Evasion (`FleeCreeperGoal`)
* **Behavior**: Dogs detect ignited/swelling Creepers within 8 blocks and immediately execute a $1.5\text{x}$ radial emergency sprint away from the blast radius.

### 13. 🛡️ Friendly Fire Safeguards (`WolfFriendlyFireHelper`)
* **Protection**: Owned dogs are immune to accidental owner sword sweep attacks, arrows, tridents, and friendly fire from fellow pack dogs under the same owner (`bd_friendly_fire_protection`).

### 14. 🏋️ Knockback Resistance
* **Modifier**: Baseline $+50\%$ knockback resistance applied to all wolves, preventing them from being bounced helplessly across the screen by rapid attacks.

---

## 🌲 Domain 5: Environmental Survival & Pathfinding

### 15. 🧗 Cliff Edge Fall Safety & Anti-Push Engine (`WolfCliffSafetyHelper`, `WolfPushMixin`)
* **Behavior**: Pathfinding raycasts probe 4 blocks ahead for drops $>3$ blocks. Dogs refuse to walk off lethal precipices.
* **Anti-Push**: Prevents crowding dogs from shoving sitting or guarding packmates off cliffs or into ravines.

### 16. 🌋 Thermal Hazard Detour Pathfinding (`AvoidHazardsGoal`, `WolfHazardHelper`)
* **Behavior**: Pathfinding continuously scans ahead for Lava, Fire, Magma Blocks, and Lit Campfires, treating them as impassable walls.
* **Panic Extinguish**: Burning dogs immediately path to nearby water blocks within 16 blocks to extinguish themselves.

### 17. 🚀 Fast-Travel Sprint Catchup & Dimension Sync (`WolfCatchupHelper`)
* **Sprint Scaling**: When the owner travels at high velocity (riding a horse, boat, minecart, or Elytra gliding), following dogs scale their sprint speed up to $2.5\text{x}$ to keep pace without rubberbanding.
* **Portal Sync**: Synchronizes dog teleportation through Nether/End portals when owner transitions.

### 18. 👥 Follower Pack Spacing Offset (`PersonalityFollowOwnerGoal`)
* **Behavior**: Follower stopping distance scales by $\sqrt{N - 1}$ based on total pack count, preventing large dog packs from clustering directly inside the player's legs and camera.

---

## 🥩 Domain 6: Nutrition, Treats & Autonomous Sustenance

### 19. 🍖 Autonomous Self-Service Ground Feeding (`EatGroundFoodGoal`)
* **Behavior**: Injured dogs autonomously search within a 10-block radius for dropped raw/cooked meats (`DogFoodHelper`), approach, eat them, play eating sounds, and heal health proportional to meat nutrition.

### 20. 🚫 Satiated Food Refusal (`DogTreatHelper`)
* **Behavior**: Full-health adult dogs refuse regular meat feedings with a polite head-shake whine rather than wastefully consuming items.

### 21. 🍬 Favorite Treats & Hyperactive Zoomies (`DogTreatHelper`, `ZoomiesGoal`)
* **Mechanic**: Every dog has a unique favorite treat item (e.g., Cooked Mutton, Rabbit Stew, Pumpkin Pie, Baked Potato) determined by its DNA.
* **Reward**: Feeding a dog its discovered favorite treat grants instant full healing, Regeneration II, heart/villager particles, and triggers a 6-second playful sprint (**The Zoomies**).

### 22. 🎁 Morning Gift Bringing AI (`WolfGiftHelper`, `WolfGiftGoal`)
* **Prerequisites**: Dog must be healthy, well-fed ($\ge 10$ meals, tracked by `feedCount`), sleeping near the owner's bed, and no monsters nearby.
* **Delivery**: Upon the owner waking up in the morning, the dog retrieves a foraging gift (bones, leather, feathers, apples, sweet berries, or a 5% chance of rare loot like Gold Nuggets, Emeralds, Name Tags).

---

## 🎮 Domain 7: Commands & Player Interactions

### 23. ✋ Shift + Right-Click Empty Hand Petting & Calming (`WolfPettingHelper`)
* **Interaction**: Crouch + Right-Click with an empty hand.
* **Effects**: Clears persistent anger, resets hostile targets, soothes thunderstorm anxiety for **10 minutes** (12,000 ticks), plays happy whine sounds, and emits heart + musical note particles.

### 24. ⛈️ Thunderstorm Anxiety & Shelter Seeking (`WolfStormAnxietyGoal`)
* **Behavior**: During thunderstorms, un-soothed dogs tremble, whimper, and actively path to covered shelter blocks with solid roofs (scaled by personality: Pacifist 3.0x, Normal 1.0x, Aggressive immune).

### 25. 🦴 Shift + Right-Click Bone Guard Mode (`WolfGuardHelper`, `WolfGuardGoal`)
* **Interaction**: Crouch + Right-Click with a **Bone** in hand.
* **Effects**: Anchors the dog to its current coordinates as a permanent sentinel post.
  * *Aggressive*: 12-block territory patrol; attacks intruding monsters.
  * *Normal*: 8-block sentry patrol.
  * *Pacifist*: 4-block cozy post; emits a periodic **Regeneration aura** to nearby owners and allied dogs.

### 26. 🛶 Shift + Stick Vehicle / Seat Boarding Command (`DogCommandManager`, `MoveToVehicleGoal`)
* **Selection**: Crouch + Right-Click dog with a **Stick** to select.
* **Boarding**: Click any empty Boat, Minecart, Saddled Horse, Donkey, Mule, Camel, or Chair block within 12 blocks to command the dog to path and board.
* **Dismount**: Crouch + Right-Click sitting passenger dog with stick to command dismount.

### 27. 📜 Paper Adoption Certificate & Ownership Transfer (`WolfAdoptionHelper`)
* **Listing**: Owner Crouch + Right-Clicks dog with **Paper** to put up for adoption (dog sits and emits pink sparkle trails).
* **Adopting**: Any other player right-clicks with an empty hand to adopt the dog, transferring complete ownership and collar bond.

### 28. 🎺 Tactical Goat Horn Commands (`WolfHornCommandHelper`, `WolfHornGoal`)
* **Range**: 64-block pack-wide broadcast.
* **Horn Instruments**:
  * 📯 *Ponder Horn*: **Rally** — All dogs immediately path to the horn blower's coordinates.
  * 📯 *Sing Horn*: **Hold / Sit** — Commands all pack dogs to sit and hold position.
  * 📯 *Yearn Horn*: **Stand / Follow** — Commands all sitting pack dogs to stand and follow.
  * 📯 *Feel Horn*: **Pacify / Calm** — Overrides combat aggro, soothing dogs into peaceful state for 30s.
  * 📯 *Seek Horn*: **Target & Attack** — Designates crosshair entity or nearest hostile for pack assault.

### 29. 🪵 Stick Fetching Mini-Game (`WolfFetchGoal`, `WolfFetchHelper`)
* **Interaction**: Throwing a Stick item near a tamed dog prompts the dog to run, pick up the stick, return it to the owner, and drop it at the player's feet with happy villager particles.

---

## 🐺 Domain 8: Wild Pack Dynamics & Social Ecology

### 30. 👑 Wild Pack Hierarchy & Alpha Election (`WolfVariantHelper`)
* **Election**: Wild wolf packs compute dominance scores based on physical scale, health, and personality to elect a pack **Alpha**.
* **Boids Follow Leader**: Pack members maintain coordinated formation behind their elected Alpha (`WildWolfFollowLeaderGoal`).

### 31. 🌕 Full Moon Chorus Howling (`WolfHowlHelper`, `GroupHowlGoal`)
* **Behavior**: At night during full moons, the pack Alpha initiates a howling vocalization which triggers a staggered, harmonic chorus howl across all pack wolves.

### 32. ⚔️ Wild Pack Territorial Rivalry & Standoffs (`WildWolfTerritorialGoal`, `WildWolfPackWarGoal`)
* **Standoff**: Rival wild packs meeting in overlapping territory engage in an 80-tick growling standoff.
* **Matrix Resolution**:
  * *Aggressive $\times$ Aggressive*: $80\%$ chance of Pack War battle.
  * *Pacifist $\times$ Pacifist*: $100\%$ chance of peaceful Pack Merger.
  * *Normal / Mixed*: Weighted war vs merge probabilities.
* **Post-War Merger**: When a rival Alpha is defeated, the surviving pack members submit and merge into the winning Alpha's pack (`mergePacks`).

### 33. 🤼 Play Fighting & Social Sparring (`SmallFightGoal`)
* **Behavior**: Compatible idle tamed dogs of the same owner engage in 6-second playful mock pounces and sparring tussles (dealing 0 damage) with happy particles to burn energy.

### 34. 🐾 Puppy Idle Curiosity (`BabyCuriosityGoal`)
* **Behavior**: Puppies playfully inspect flowers, ferns, pumpkins, sweet berry bushes, and watch passive critters.

### 35. 🎭 Puppy Mischief & Adult Correction (`BabyMischiefGoal`, `AdultCorrectionGoal`)
* **Behavior**: Puppies playfully nip adults and chase small mobs; adult dogs administer gentle, low-damage corrective nips without triggering pack-wide anger.

### 36. 🗺️ Wanderlust Roaming (`WanderlustGoal`)
* **Behavior**: Non-sitting tamed dogs safely explore within a 12-block radius around the owner when idle.

### 37. 🏜️ Expanded Biome Spawns (`BetterDogsSpawning`)
* **Biomes**: Naturally spawns wild wolf packs in Plains, Savannas, Windswept Savannas, Badlands, and Meadows.

---

## ⌨️ Domain 9: Commands, GameRules & Advancements

### 38. 💻 Full In-Game Brigadier Command Suite
* `/betterdogs help` — Displays command guide.
* `/betterdogs status` — Displays global active features and counts.
* `/betterdogs get <rule>` — Queries GameRule value.
* `/betterdogs set <rule> <value>` — Live updates GameRule with 2-way sync.
* `/betterdogs reset` — Restores all settings to defaults.
* `/betterdogs reload` — Reloads configuration.

### 39. 🏆 13 Custom Husbandry Advancements
1. 🦴 `tame_wolf` — *"Best Friend"* (Tame a wolf).
2. 🐾 `pet_dog` — *"Good Boy!"* (Pet your dog with empty hand).
3. 🍵 `soothe_dog` — *"Thunder Buddy"* (Soothe a dog trembling in a thunderstorm).
4. 🍖 `favorite_treat` — *"Culinary Canine"* (Discover your dog's favorite treat).
5. ⚡ `zoomies` — *"The Zoomies!"* (Trigger hyperactivity sprint).
6. 🐕 `giant_lineage` — *"Dire Wolf Bloodline"* (Breed a giant dog $\ge 1.25\text{x}$).
7. 🍎 `cure_inbred` — *"A Second Chance"* (Cure an inbred runt with a Golden Apple).
8. 🛡️ `guard_mode` — *"On Watch"* (Set a dog to Guard Mode sentinel post).
9. 📜 `adopt_dog` — *"Forever Home"* (Adopt a dog listed with paper).
10. 📯 `horn_command` — *"Pack Commander"* (Command pack with a Goat Horn).
11. 🪵 `fetch_stick` — *"Fetch Master"* (Play fetch with a stick).
12. 🎁 `morning_gift` — *"Morning Delivery"* (Receive a morning gift from your dog).
13. 🌕 `chorus_howl` — *"Call of the Wild"* (Listen to a full moon pack chorus howl).
