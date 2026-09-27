# Architecture & Symbol Index: Vanilla Outsider: Better Dogs

## 1. Mod Metadata & Entrypoint
- **Mod ID**: `vanilla-outsider-better-dogs`
- **Main Entrypoint**: `net.vanillaoutsider.betterdogs.BetterDogsFabric` (`net.fabricmc.api.ModInitializer`)
- **Client Entrypoint**: `None`

## 2. Bytecode Mixin Target Registry
| Target Vanilla Class | Mixin Class | Purpose |
| :--- | :--- | :--- |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.TamableAnimalMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.OwnerHurtTargetGoalMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.OwnerHurtByTargetGoalMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.HurtByTargetGoalMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfSafetyMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfPushMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.WolfInteractMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterdogs.mixin.InstrumentItemMixin` | Core mixin hook |

## 3. Core Mechanics & Subsystems
- **Source Root**: `src/main/java/`
- **Resource Root**: `src/main/resources/`

## 4. Dynamic GameRules & Commands
- **GameRules / Commands**: Configured dynamically via namespaced keys (`vanilla-outsider-better-dogs:*`).

## 5. Configuration & Sidedness Isolation
- **Sidedness**: Server-safe logic in main, client isolated in `src/client/java` or client entrypoint.
