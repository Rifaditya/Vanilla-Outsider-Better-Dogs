# Version Compatibility & Lifecycle Policy

*[[Home]] / Version Compatibility*

---

## 🗺️ Minecraft Version Compatibility Matrix

**Vanilla Outsider: Better Dogs** follows the **1 Jar 1 Version** architecture law. Dedicated binaries are compiled and tagged specifically per Minecraft release version:

| Minecraft Version | Mod Release Tag | DasikLibrary Bounds | JDK | Support Status | Target Guide |
| :--- | :--- | :--- | :---: | :---: | :--- |
| **MC 26.3** | `v5.0.16+26.3` | `>=1.8.8` | JDK 25 | 🟢 Active (Snapshot) | [[MC 26.3 Guide|Minecraft-26.3-Guide]] |
| **MC 26.2** | `v4.24.1+26.2` | `>=1.8.3` | JDK 25 | 🟢 Primary Target | [[MC 26.2 Guide|Minecraft-26.2-Guide]] |
| **MC 26.1.2** | `v4.24.1+26.1.2` | `>=1.8.2` | JDK 25 | 🟡 Maintenance | [[MC 26.1 Guide|Minecraft-26.1-Guide]] |
| **MC 1.21.11** | Legacy `1.x` Series | Legacy | Java 21 | 🔴 Deprecated | Legacy Archive Only |

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
  * **Dismounting**: Shift + Right-Click with Stick to dismount from seats/boats/minecarts.
* **Genetics & Breeding**: Offspring bred from pre-existing wolves will fully utilize the new genetics, litter size calculations (1-4 pups), personality inheritance, and stat variance systems.

### What Happens When Removing Mid-Game (Uninstallation)?
* **100% Safe Uninstallation**: Removing the mod from a server or singleplayer world mid-game will **never** corrupt save files or cause "missing entity/item registry" startup crashes.
* **Zero Custom Registry Bloat**: Better Dogs introduces zero custom entity types, zero custom blocks, and zero custom items—relying entirely on vanilla `Wolf` entities and standard vanilla items (Bones, Paper, Goat Horns, Sticks).
* **Vanilla Behavior Reversion**: Wolves seamlessly revert to standard vanilla wolf AI while retaining their tamed owner UUIDs, custom names, current health, and collar colors.

---

*Back to [[Home]]*
