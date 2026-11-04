# Version Compatibility & Lifecycle Policy

*[[Home]] / Version Compatibility*

---

## 🗺️ Minecraft Version Compatibility Matrix

**Vanilla Outsider: Better Dogs** follows the **1 Jar 1 Version** architecture law. Dedicated binaries are compiled and tagged specifically per Minecraft release version:

| Minecraft Version | Mod Release Tag | Java Version | Loom & Tooling | Support Status | Target Guide |
| :--- | :--- | :---: | :--- | :---: | :--- |
| **MC 1.20.1** | `1.0.70+1.20.1` | Java 17 | Loom 1.10.2 / Mojang Mappings | 🟢 Active Release | [[MC 1.20.1 Guide\|Minecraft-1.20.1-Guide]] |
| **MC 1.21.1** | `1.0.70+1.21.1` | Java 21 | Loom 1.10.2 / Mojang Mappings | 🟢 Active Release | [[MC 1.21.1 Guide\|Minecraft-1.21.1-Guide]] |
| **MC 1.21.11** | `1.0.73+1.21.11` | Java 21 | Loom-Remap 1.15-SNAPSHOT | 🟢 Active Release | [[MC 1.21.11 Guide\|Minecraft-1.21.11-Guide]] |
| **MC 26.1.2** | `4.24.1+26.1.2` | Java 25 | Loom 1.15+ / Gradle 9.3+ | 🟢 Active Release | [[MC 26.1.2 Guide\|Minecraft-26.1.2-Guide]] |
| **MC 26.2** | `4.24.8+26.2` | Java 25 | Loom 1.15+ / Gradle 9.3+ | 🟢 Primary Target | [[MC 26.2 Guide\|Minecraft-26.2-Guide]] |
| **MC 26.3** | `4.24.8+26.3` | Java 25 | Loom 1.15+ / Gradle 9.3+ | 🟢 Snapshot Build | [[MC 26.3 Guide\|Minecraft-26.3-Guide]] |

---

## 📜 Version History & 26.x Migration

### Origins (MC 1.21.11)
**Better Dogs** originally launched during the Minecraft 1.21.x era. Initial builds established the core concept: giving vanilla wolves unique personality traits (Aggressive, Pacifist, Normal) and physical scale variation without introducing non-vanilla items or entity types.

### The 26.x Modernization Era
Following the transition to modern Minecraft 26.x:
1. **Zero-Dependency Guard**: `ModVersionGuard` was integrated via DasikLibrary to protect servers against classloader mismatch crashes.
2. **Modern Identifier Syntax**: All resource identifiers migrated from `Identifier.of()` to `Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", path)`.
3. **Open-Ended Dependency Bounds**: `fabric.mod.json` uses open-ended lower bounds (e.g. `"minecraft": ">=26.2-"`) to prevent strict launcher version locks while enforcing exact compatibility thresholds.

---

## ⏳ Version Support & Deprecation Lifecycle Policy

1. **Primary Target (MC 26.2)**: Receives all new gameplay features, AI optimizations, performance updates, and instant bug fixes.
2. **Snapshot Tracking (MC 26.3)**: Tracks upcoming Mojang snapshot API changes to ensure zero-day readiness upon major game updates.
3. **Maintenance Builds (MC 26.1.2)**: Receives critical bug fixes and security patches backported from the primary release line.

---

## 🔌 Multi-Loader Architecture (Fabric & NeoForge)

**Better Dogs** features native Multi-Loader support across the legacy and transitional anchors (**Minecraft 1.20.1, 1.21.1, and 1.21.11**). Each release builds dual standalone binaries:

| Minecraft Anchor | Mod Loaders Supported | Dual Release JARs | Dependencies |
| :--- | :--- | :--- | :--- |
| **MC 1.20.1** | Fabric & NeoForge (Forge SRG) | `better-dogs-*-fabric.jar`<br>`better-dogs-*-neoforge.jar` | Fabric: Fabric API<br>NeoForge: Zero extra APIs |
| **MC 1.21.1** | Fabric & NeoForge | `better-dogs-*-fabric.jar`<br>`better-dogs-*-neoforge.jar` | Fabric: Fabric API<br>NeoForge: Zero extra APIs |
| **MC 1.21.11** | Fabric & NeoForge | `better-dogs-*-fabric.jar`<br>`better-dogs-*-neoforge.jar` | Fabric: Fabric API<br>NeoForge: Zero extra APIs |
| **MC 26.x** | Fabric (Modern Sovereign) | `better-dogs-*-fabric.jar` | Fabric API |

---

## ❓ Frequently Asked Questions: Console & Startup Logs

### NeoForge Startup Notice: "Identified an attempt to add synced data to a foreign entity"

#### 1. What does the message look like?
On NeoForge client or server startup across **all NeoForge version anchors (MC 1.20.1, 1.21.1, and 1.21.11)**, the console log prints:
```text
[modloading-sync-worker/WARN]: Identified an attempt to add synced data to a foreign entity: this is highly discouraged.
Entity class: net.minecraft.world.entity.animal.wolf.Wolf
Mixins into entity class: net.vanillaoutsider.betterdogs.mixin.WolfMixin
Modders should use syncable data attachments instead, as they do not suffer from potential ID mismatches.
Please refer to the data attachments documentation available at https://docs.neoforged.net/docs/datastorage/attachments.
This message will only be printed once per class
```

#### 2. Is this an error or a crash?
**No. It is 100% harmless.**
* It is a single **`WARN`** level advisory notice printed **only once** per game launch.
* It does **not** crash the game, cause lag, produce packet errors, desync multiplayer dogs, or corrupt world save files.

#### 3. Why does this notice appear?
Better Dogs needs to synchronize two visual wolf states between the server and the player's screen:
1. `isSubdued` — Displays the playful submissive posture when an adult dog is playfully defeated by a puppy.
2. `isSpectatorWagging` — Makes a dog wag its tail at a player who is invisible in Spectator Mode.

Better Dogs synchronizes these states using Minecraft's official built-in **`SynchedEntityData`** (`EntityDataAccessor<Boolean>`)—the exact same core system vanilla Minecraft uses to synchronize wolf collar colors, sitting posture, and health.

NeoForge prefers developers to use their own proprietary **Data Attachments** system instead of vanilla's data syncher when modifying vanilla entities ("foreign entities"), so NeoForge emits a developer advisory notice.

#### 4. Why does Better Dogs use vanilla SynchedEntityData instead of NeoForge Data Attachments?
* **Universal Multi-Loader Compatibility**: Better Dogs is a native multi-loader mod built from a clean, shared `:common` engine module. Fabric does not have NeoForge's proprietary Data Attachments system.
* **Zero Runtime Bloat**: Using vanilla Minecraft's standard `SynchedEntityData` means Better Dogs does **not** require heavy third-party runtime bridge libraries (such as Architectury API) or custom network packet handlers.
* **Identical Behavior**: The exact same battle-tested logic executes identically across both Fabric and NeoForge.
* **Defensive Fail-Safe Gating**: All synched data reads and writes in `WolfMixin` are wrapped in guarded try-catch blocks with static default fallbacks, guaranteeing zero runtime crashes.

---

## 🌍 Existing World Compatibility & Retrofitting

**Vanilla Outsider: Better Dogs** is 100% backwards-compatible and completely safe to **add or remove** mid-playthrough on existing Minecraft save files.

### What Happens When Adding Mid-Game?
* **Zero Data Loss**: Existing wolves remain tamed to their respective owners. Their custom names, current health, collar dyes, and equipment are preserved.
* **Automatic Attachment Initialization**: As soon as pre-existing wolves load in the world, the Fabric Data Attachment API initializes default persistence records without entity resets or health loss.
* **Default Trait Assignment**: Existing wolves default to standard `NORMAL` personality behavior and base physical scales.
* **Immediate Feature Unlock**: Pre-existing wolves immediately gain full access to all mod features:
  * **Guard Mode**: Shift + Right-Click with a Bone to assign sentinel guard positions.
  * **Favorite Treats**: Feeding favorite treats fully heals and grants regeneration buffs.
  * **Tactical Horn Commands**: Respond to Goat Horn tactical orders (Call, Attack, Guard, Follow).
  * **Adoption System**: Shift + Right-Click with Paper to enable ownership transfer to other players.
  * **Dismounting**: Shift + Right-Click with Command Tools (Stick, Blaze Rod, Breeze Rod) to dismount from seats/boats/minecarts.
* **Genetics & Breeding**: Offspring bred from pre-existing wolves will fully utilize the new genetics, litter size calculations (1-4 pups), personality inheritance, and stat variance systems.

### What Happens When Removing Mid-Game (Uninstallation)?
* **100% Safe Uninstallation**: Removing the mod from a server or singleplayer world mid-game will **never** corrupt save files or cause "missing entity/item registry" startup crashes.
* **Zero Custom Registry Bloat**: Better Dogs introduces zero custom entity types, zero custom blocks, and zero custom items—relying entirely on vanilla `Wolf` entities and standard vanilla items (Bones, Paper, Goat Horns, Sticks, Blaze Rods, Breeze Rods).
* **Vanilla Behavior Reversion**: Wolves seamlessly revert to standard vanilla wolf AI while retaining their tamed owner UUIDs, custom names, current health, and collar colors.

---

*Back to [[Home]]*
