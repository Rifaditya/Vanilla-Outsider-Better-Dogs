# Architecture & Symbol Index: Vanilla Outsider: Better Dogs (Modern 26.1.2)

## 1. Multi-Loader Architecture & Metadata
- **Architecture**: 3-Module Standard (`:common`, `:fabric`, `:neoforge`) via Architectury Loom 1.14 (No-Remap)
- **Fabric Mod ID**: `vanilla-outsider-better-dogs`
- **NeoForge Mod ID**: `better_dogs` (snake_case compliant with NeoForge regex)
- **Common Module (`:common`)**: Universal domain logic, genetics, AI goals, and cross-loader hooks (`net.vanillaoutsider.betterdogs.BetterDogs`)
- **Fabric Entrypoint (`:fabric`)**: `net.vanillaoutsider.betterdogs.BetterDogsFabric` (`net.fabricmc.api.ModInitializer`)
- **Fabric Client Entrypoint (`:fabric`)**: `net.vanillaoutsider.betterdogs.client.BetterDogsClientFabric`
- **NeoForge Entrypoint (`:neoforge`)**: `net.vanillaoutsider.betterdogs.BetterDogsNeoForge` (`@Mod("better_dogs")`)
- **Java SPI Abstraction**: Zero runtime library bloat via `net.vanillaoutsider.betterdogs.platform.Services`:
  - `IPlatformHelper` -> `FabricPlatformHelper` / `NeoForgePlatformHelper`
  - `IWolfDataHelper` -> `FabricWolfDataHelper` / `NeoForgeWolfDataHelper`

## 2. Bytecode Mixin Target Registry
Common mixins are registered in `vanilla-outsider-better-dogs-common.mixins.json`:

| Target Vanilla Class | Mixin Class | Purpose |
| :--- | :--- | :--- |
| `Animal` | `net.vanillaoutsider.betterdogs.mixin.AnimalMixin` | Inbreeding and passive breeding hooks |
| `BreedGoal` | `net.vanillaoutsider.betterdogs.mixin.BreedGoalMixin` | Multi-puppy litter breeding and genetics propagation |
| `Entity` | `net.vanillaoutsider.betterdogs.mixin.EntityMixin` | Collision separation and entity lifecycle hooks |
| `Wolf` | `net.vanillaoutsider.betterdogs.mixin.WolfMixin` | Base entity extension, collar inheritance, state sync |
| `Wolf` | `net.vanillaoutsider.betterdogs.mixin.WolfCombatMixin` | Combat damage scaling, sparring safety, ballistic intercepts |
| `Wolf` | `net.vanillaoutsider.betterdogs.mixin.WolfSocialMixin` | Pack play sparring, social dynamics, and leader-follower sync |
| `Wolf` | `net.vanillaoutsider.betterdogs.mixin.WolfGuardMixin` | Sentinel guard mode patrol and defense behaviors |
| `Wolf` | `net.vanillaoutsider.betterdogs.mixin.WolfAIMixin` | Goal selector initialization and custom goal priority ordering |
| `Wolf` | `net.vanillaoutsider.betterdogs.mixin.WolfInteractMixin` | Feeding, petting soothing, stick commands, and adoption |
| `Wolf` | `net.vanillaoutsider.betterdogs.mixin.WolfPushMixin` | Overlap separation physics and cliff-safety edge guards |
| `Wolf` | `net.vanillaoutsider.betterdogs.mixin.WolfSpawnMixin` | Natural spawn personality and genetics baseline assignment |
| `Wolf` | `net.vanillaoutsider.betterdogs.mixin.WolfGroupMixin` | Pack hierarchy, leader election, and howling chorus |
| `TamableAnimal` | `net.vanillaoutsider.betterdogs.mixin.TamableAnimalMixin` | Taming status transitions and owner binding logic |
| `OwnerHurtTargetGoal` | `net.vanillaoutsider.betterdogs.mixin.OwnerHurtTargetGoalMixin` | Target validation, friendly fire prevention, and civil war immunity |
| `OwnerHurtByTargetGoal` | `net.vanillaoutsider.betterdogs.mixin.OwnerHurtByTargetGoalMixin` | Retaliation targeting with civil war suppression |
| `Wolf` | `net.vanillaoutsider.betterdogs.mixin.WolfBreedingMixin` | Puppy genetics inheritance and collar color blending |
| `Wolf` | `net.vanillaoutsider.betterdogs.mixin.WolfAccessor` | Accessor for private vanilla wolf fields and methods |
| `Mob` | `net.vanillaoutsider.betterdogs.mixin.WolfMobMixin` | Goal selector intercept and ambient vocalization control |
| `ServerLevel` | `net.vanillaoutsider.betterdogs.mixin.ServerLevelMixin` | World tick processing and ambient weather anxiety checks |
| `ServerPlayer` | `net.vanillaoutsider.betterdogs.mixin.ServerPlayerMixin` | Player command tool interactions and goat horn commands |
| `ServerPlayer` | `net.vanillaoutsider.betterdogs.mixin.ServerPlayerTickMixin` | Per-player pack follower telemetry and catch-up travel |
| `WalkNodeEvaluator` | `net.vanillaoutsider.betterdogs.mixin.WalkNodeEvaluatorMixin` | Pathfinding detour avoidance for hazards and fireballs |
| `NaturalSpawner` | `net.vanillaoutsider.betterdogs.mixin.NaturalSpawnerMixin` | Wild pack leader generation in natural biomes |
| `InstrumentItem` | `net.vanillaoutsider.betterdogs.mixin.InstrumentItemMixin` | Goat horn acoustic command dispatching |
| `HurtByTargetGoal` | `net.vanillaoutsider.betterdogs.mixin.HurtByTargetGoalMixin` | Retaliation alarm containment and pack civil war prevention |
| `Wolf` | `net.vanillaoutsider.betterdogs.mixin.WolfSoundMixin` | Pitch timbre genetics and sound variant modulation |
| `WolfRenderer` | `net.vanillaoutsider.betterdogs.mixin.WolfRendererMixin` | Client-side wolf scale genetics and rendering adjustments |

## 3. Core Mechanics & Subsystems
- **Common Module (`common/`)**:
  - `net.vanillaoutsider.betterdogs.ai.*` — AI Goal implementations (Ballistic Interceptors, Evasion, Group Howl, Play)
  - `net.vanillaoutsider.betterdogs.genetics.*` — Canine physical genetics (Speed, Health, Scale, Timbre)
  - `net.vanillaoutsider.betterdogs.command.*` — Universal Brigadier command logic (`/betterdogs`)
  - `net.vanillaoutsider.betterdogs.config.*` — Dynamic GameRules, YACL GUI builders, JSON fallbacks
  - `net.vanillaoutsider.betterdogs.compat.jade.*` — Jade Waila tooltip integration
- **Fabric Module (`fabric/`)**:
  - `net.vanillaoutsider.betterdogs.BetterDogsFabric` — ModInitializer entrypoint
  - `net.vanillaoutsider.betterdogs.client.BetterDogsClientFabric` — ClientModInitializer
  - `net.vanillaoutsider.betterdogs.config.ModMenuIntegration` — ModMenu config screen entrypoint
  - `net.vanillaoutsider.betterdogs.world.BetterDogsSpawningFabric` — Fabric Biome API spawn registration
- **NeoForge Module (`neoforge/`)**:
  - `net.vanillaoutsider.betterdogs.BetterDogsNeoForge` — `@Mod("better_dogs")` entrypoint and NeoForge event bus listeners
  - `data/better_dogs/neoforge/biome_modifier/expanded_wolf_spawns.json` — Data-driven biome modifier spawn registration

## 4. Dynamic GameRules & Commands
- **GameRules**: Registered dynamically with `bd_` prefix (e.g., `bd_projectile_interception`, `bd_dog_play_fight_chance`).
- **Brigadier Command**: `/betterdogs` registered on Fabric via `CommandRegistrationCallback` and on NeoForge via `RegisterCommandsEvent`.

## 5. Configuration & Sidedness Isolation
- **Sidedness**: All game logic and AI goals in `:common` are 100% server-safe.
- **Client Guards**: Client rendering (`WolfRendererMixin`) isolated in mixin JSON `client` block; client config screens (`ModMenuIntegration`, `YaclScreenHelper`) gated against server classloading crashes.
