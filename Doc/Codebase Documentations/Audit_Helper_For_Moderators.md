# Moderator Audit Helper: Better Dogs

**Mod Name:** Vanilla Outsider: Better Dogs
**Mod ID:** `vanilla-outsider-better-dogs` (Fabric)
**Version:** 1.9.6 (Targeting Minecraft 26.1 Snapshot 4)
**Creator:** DasikIgaijin

## 🛡️ Safety & Compliance Statement

To assist Platform Moderators (Modrinth/CurseForge) in auditing this project, I certify the following:

1. **No External Network Connections**: This mod does **NOT** make any HTTP/Web requests. It runs entirely offline within the Minecraft game loop.
2. **No Data Collection**: This mod does **NOT** collect, store, or transmit any user data, telemetry, or analytics.
3. **No Binary Execution**: This mod does **NOT** execute any external binaries or OS-level commands.

## 📂 Codebase Overview for Reviewers

The codebase is focused on extending the Vanilla Wolf entity with a personality and stat system.

| Feature | Source File | Description |
| :--- | :--- | :--- |
| **Data Persistence** | `net.vanillaoutsider.betterdogs.WolfPersistentData` | Records the personality ID and last damage time. Integrated via Fabric Attachment API. |
| **Core Logic** | `net.vanillaoutsider.betterdogs.mixin.WolfMixin` | Handles the randomization of personalities and the application of stat buffs/AI changes. |
| **Entrypoint** | `net.vanillaoutsider.betterdogs.BetterDogs` | Initializes the `AttachmentType` and mod constants. |

## 🔍 Specific "Suspicious" Patterns Explained

Moderators looking at Mixins might flag the following patterns. Here is the justification:

### 1. `betterdogs$onApplyTamingSideEffects` Injection

* **Location**: `WolfMixin.java` -> `betterdogs$onApplyTamingSideEffects`
* **Reason**: **Reliable Logic Hook.** In the unobfuscated 26.1 snapshot environment, the standard `setTame` method is not explicitly overridden in `Wolf.java`, which can lead to Mixin transformation failures if targeted directly. I inject into `applyTamingSideEffects` instead, which is a native override in `Wolf.java` used to apply tame-status changes. This ensures the mod correctly assigns its custom personalities when a wolf is tamed.

### 2. `spawnTamingParticles` Override

* **Location**: `WolfMixin.java` -> `betterdogs$protectBabies`
* **Reason**: **Gameplay Enhancement.** This prevents other entities (or other wolves) from targeting baby wolves unless the baby is the aggressor. This is a common feature in "Better" mob mods to prevent AI-driven "baby loss" in the wild.

### 3. Usage of `AttachmentRegistry` (Fabric API)

* **Reason**: This is the standard, modern way to store data on entities in Fabric 1.20+. It replaces the old requirement for custom NBT manual handling in `read/writeAdditionalSaveData`. The data is still stored in the world's NBT, but handled by the Fabric API for better mod compatibility.

## 🛠️ Build & Dependencies

* **Loader**: Fabric Loader
* **Mappings**: Official Minecraft Mappings (Mojang/Unobfuscated)
* **External Libs**: None (Uses Fabric API).
