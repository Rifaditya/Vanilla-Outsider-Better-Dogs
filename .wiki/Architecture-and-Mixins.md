# Architecture & Mixin Injection Points

*[[Home]] / Architecture & Mixins*

---

## 🏗️ Architecture & Package Layout

**Vanilla Outsider: Better Dogs** strictly adheres to the **1 File, 1 Function Law**. Code logic is decoupled into specialized packages under `net.vanillaoutsider.betterdogs`:

```
net.vanillaoutsider.betterdogs
├── BetterDogs.java               # Main entrypoint & GameRules initialization
├── WolfExtensions.java           # Extension interface for wolf persistent state
├── WolfPersistentData.java       # NBT/Codec data container for personality & scale
├── WolfPersonality.java          # Personality enum (NORMAL, AGGRESSIVE, PACIFIST)
├── ai                            # AI Goal implementations (30 specialized goals)
│   ├── AggressiveTargetGoal.java
│   ├── PersonalityFollowOwnerGoal.java
│   ├── WolfGuardGoal.java
│   ├── WolfFlankAttackGoal.java
│   ├── WildWolfTerritorialGoal.java
│   ├── WolfStormAnxietyGoal.java
│   └── ...
├── command                       # Brigadier command registration
│   └── BetterDogsCommand.java
├── config                        # Configuration screens & YACL v3 integration
├── mixin                         # Java Mixin injection suite (25 mixins)
└── util                          # Math, raycasting, and thread-safe looper utilities
```

---

## 💉 Complete Mixin Target Reference Table (25 Mixins)

The mod injects non-intrusive hooks into vanilla Minecraft classes to expand wolf behaviors without breaking compatibility:

| Mixin Class | Target Minecraft Class | Injection Point / Purpose |
| :--- | :--- | :--- |
| `AnimalMixin` | `net.minecraft.world.entity.animal.Animal` | Intercepts animal breeding rules for litter size scaling. |
| `BreedGoalMixin` | `net.minecraft.world.entity.ai.goal.BreedGoal` | Hooks breeding completion to process personality genetics inheritance. |
| `EntityMixin` | `net.minecraft.world.entity.Entity` | Intercepts bounding box recalculation for custom physical scale rendering. |
| `HurtByTargetGoalMixin` | `net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal` | Filters retaliation targets based on friendly fire protection rules. |
| `InstrumentItemMixin` | `net.minecraft.item.InstrumentItem` | Intercepts goat horn usage to broadcast tactical commands to owned wolves. |
| `NaturalSpawnerMixin` | `net.minecraft.world.level.NaturalSpawner` | Hooks natural spawning weights for `bd_wolf_spawn_multiplier_percent`. |
| `OwnerHurtByTargetGoalMixin` | `net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal` | Adjusts owner defense targeting for Pacifist and Aggressive traits. |
| `OwnerHurtTargetGoalMixin` | `net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal` | Modifies attack target priorities based on wolf personality. |
| `ServerLevelMixin` | `net.minecraft.server.level.ServerLevel` | Handles level tick listeners for pack howl schedules and storm anxiety. |
| `ServerPlayerMixin` | `net.minecraft.server.level.ServerPlayer` | Syncs wolf selection and vehicle boarding commands across network. |
| `ServerPlayerTickMixin` | `net.minecraft.server.level.ServerPlayer` | Manages fast-travel catch-up safety teleportation for owned wolves. |
| `TamableAnimalMixin` | `net.minecraft.world.entity.TamableAnimal` | Extends tamed entity follow distance ranges and owner teleport logic. |
| `WalkNodeEvaluatorMixin` | `net.minecraft.world.level.pathfinder.WalkNodeEvaluator` | Implements cliff safety path evaluation (`bd_cliff_safety`). |
| `WolfAIMixin` | `net.minecraft.world.entity.animal.Wolf` | Registers custom AI goals into the wolf goal selector hierarchy. |
| `WolfAccessor` | `net.minecraft.world.entity.animal.Wolf` | Accessor interface exposing protected wolf sound and animation triggers. |
| `WolfBreedingMixin` | `net.minecraft.world.entity.animal.Wolf` | Intercepts wolf feeding and breeding interactions. |
| `WolfCombatMixin` | `net.minecraft.world.entity.animal.Wolf` | Applies damage, knockback, and sprint speed modifiers during combat. |
| `WolfGroupMixin` | `net.minecraft.world.entity.animal.Wolf` | Manages wild pack cluster formation and leader assignment. |
| `WolfGuardMixin` | `net.minecraft.world.entity.animal.Wolf` | Controls Guard Mode state transitions and sentinel patrol anchors. |
| `WolfInteractMixin` | `net.minecraft.world.entity.animal.Wolf` | Handles Shift+Right-Click interaction triggers (Guard Mode, Adoption). |
| `WolfMixin` | `net.minecraft.world.entity.animal.Wolf` | Core mixin attaching `WolfExtensions` persistent data container. |
| `WolfMobMixin` | `net.minecraft.world.entity.Mob` | Modifies mob navigation pathfinding parameters for pack flanking. |
| `WolfSafetyMixin` | `net.minecraft.world.entity.animal.Wolf` | Prevents accidental friendly fire damage from owner sweeping attacks. |
| `WolfSocialMixin` | `net.minecraft.world.entity.animal.Wolf` | Implements territorial rivalry, pack wars, and group howl triggers. |
| `WolfSpawnMixin` | `net.minecraft.world.entity.animal.Wolf` | Assigns random personality traits and scale factors upon initial spawn. |

---

## ⚡ Thread Safety & Performance Constraints

* **Non-Blocking Loopers**: All spatial scans (such as pack leader detection and creeper raycasting) execute asynchronously or are throttled via interval tick gates, preventing main-thread server lag spikes.
* **Single-Function Cohesion**: Each AI goal (e.g. `WolfFlankAttackGoal`, `WolfGuardGoal`) handles strictly one behavioral responsibility, prioritizing clean execution paths over monolithic loop structures.

---

*Back to [[Home]]*
