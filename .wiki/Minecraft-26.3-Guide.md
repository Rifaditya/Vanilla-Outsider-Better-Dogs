# Minecraft 26.3 Guide & Release Notes

*[[Home]] / [[Version Compatibility|Version-Compatibility]] / Minecraft 26.3 Guide*

---

## 📦 Version Information

| Property | Value |
| :--- | :--- |
| **Minecraft Target** | `26.3` (Snapshot 26.3-snapshot-6+) |
| **Mod Version** | `5.0.16+26.3` |
| **Fabric Loader** | `>=0.19.3` |
| **DasikLibrary Dependency** | `>=1.8.8` |
| **Java JDK Requirement** | JDK 25 |
| **Build Tooling** | Loom 1.15+ / Gradle 9.3+ |
| **Jar Naming Convention** | `vanilla-outsider-better-dogs-5.0.16+26.3.jar` |

---

## 🌟 Overview & Snapshot Changes

The Minecraft 26.3 release of **Vanilla Outsider: Better Dogs** tracks upcoming 26.3 snapshot features and snapshot entity changes. It introduces major architectural enhancements to entity codecs, pack spatial hierarchy, and dynamic entity scaling.

### Key Highlights in 26.3
* **Entity Codec Synchronization**: Full migration of persistent wolf personality state (`WolfPersistentData`) to modern 26.3 component serialization codecs.
* **DasikLibrary 1.8.8 Alignment**: Updated integration with `DynamicGameRuleManager` 1.8.8+, providing dynamic hot-reloading for namespaced rules without server restarts.
* **Advanced Flanking & AI Raycasting**: Enhanced spatial path evaluation (`bd_flanking_raycast_check`) to prevent pathfinding deadlocks around steep cliffs and tight caves.

---

## 🔧 Installation & Setup

1. Install **Fabric Loader** `0.19.3` or higher for Minecraft `26.3`.
2. Place `vanilla-outsider-better-dogs-5.0.16+26.3.jar` into your server or client `mods/` directory.
3. Ensure **DasikLibrary** `1.8.8` or higher is installed.
4. Launch the game using **JDK 25**.

---

## ⚠️ Compatibility Notes

* **Save Compatibility**: World saves created under 26.2 are compatible; wolf personality NBT and scale attributes automatically migrate to 26.3 component format upon loading.
* **Experimental Status**: Features on 26.3 track ongoing Mojang snapshot changes. For production survival servers requiring peak stability, we recommend using [[Minecraft 26.2 Guide|Minecraft-26.2-Guide]].

---

*Back to [[Home]] | View [[Version Compatibility|Version-Compatibility]]*
