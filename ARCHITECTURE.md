# Architecture & Symbol Index: Vanilla Outsider: Better Dogs

## 1. Mod Metadata & Entrypoint
- **Mod ID**: `vanilla-outsider-better-dogs`
- **Main Entrypoint**: `net.vanillaoutsider.betterdogs.BetterDogsFabric` (`net.fabricmc.api.ModInitializer`)
- **Client Entrypoint**: `net.vanillaoutsider.betterdogs.client.BetterDogsClientFabric`

## 2. Bytecode Mixin Target Registry
| Target Vanilla Class | Mixin Class | Purpose |
| :--- | :--- | :--- |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.BreedGoalMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.EntityMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfCombatMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfSocialMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfGuardMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfAIMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfInteractMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfPushMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfSpawnMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfGroupMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.TamableAnimalMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.OwnerHurtTargetGoalMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.OwnerHurtByTargetGoalMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfBreedingMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfAccessor` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfMobMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.ServerLevelMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.ServerPlayerMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.ServerPlayerTickMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.AnimalMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WalkNodeEvaluatorMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.NaturalSpawnerMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.InstrumentItemMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.HurtByTargetGoalMixin` | Core mixin hook |

## 3. Core Mechanics & Subsystems
- **Source Root**: `src/main/java/`
- **Resource Root**: `src/main/resources/`

## 4. Dynamic GameRules & Commands
- **GameRules / Commands**: Configured dynamically via namespaced keys (`vanilla-outsider-better-dogs:*`).

## 5. Configuration & Sidedness Isolation
- **Sidedness**: Server-safe logic in main, client isolated in `src/client/java` or client entrypoint.
