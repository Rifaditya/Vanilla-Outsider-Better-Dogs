# Minecraft 26.2 Guide & Release Notes

*[[Home]] / [[Version Compatibility|Version-Compatibility]] / Minecraft 26.2 Guide*

---

## 📦 Version Information

| Property | Value |
| :--- | :--- |
| **Minecraft Target** | `26.2` (Stable Primary Target) |
| **Mod Version** | `4.24.1+26.2` |
| **Fabric Loader** | `>=0.16.9` |
| **DasikLibrary Dependency** | `>=1.8.3` |
| **Java JDK Requirement** | JDK 25 |
| **Build Tooling** | Loom 1.15+ / Gradle 9.3+ |
| **Jar Naming Convention** | `vanilla-outsider-better-dogs-4.24.1+26.2.jar` |

---

## 🌟 Overview & Core Mechanics

Minecraft 26.2 serves as the primary stable release version for **Vanilla Outsider: Better Dogs**. It includes the complete feature set across all gameplay pillars:

### 1. Distinct Personalities (Aggressive, Pacifist, Normal)
* **Aggressive (20% Spawn Weight)**: +15% sprint speed, +20 block detection range, but lower HP (-10 half-hearts) and higher storm anxiety.
* **Pacifist (20% Spawn Weight)**: +20 HP bonus (+10 hearts), +50% knockback on attack, but reduced attack damage (-15%) and speed (-10%).
* **Normal (60% Spawn Weight)**: Balanced vanilla-style combat stats and baseline follow distance.

### 2. Guard Mode & Sentinels
* Shift+Right-Click an owned wolf with a bone to activate Guard Mode at its current position.
* Aggressive guards patrol a 12-block radius, Normal guards hold position, and Pacifist guards patrol a 3-block radius while applying Regeneration and Resistance buffs to nearby owners (`bd_pacifist_guard_buffs`).

### 3. Tactical Goat Horn Commands & Vehicle Boarding
* Sounding a goat horn within 64 blocks commands all owned wolves:
  * **Ponder Horn**: Orders wolves to enter Guard Mode at their current locations.
  * **Sing Horn**: Toggles Tactical Override, commanding Aggressive wolves to adopt Pacifist defensive rules for 30 seconds (`bd_horn_override_duration`).
  * **Seek/Call Horn**: Recalls distant wolves directly to your side.
* Shift+Right-Click a dog to select it, then Shift+Right-Click boats, minecarts, or horses to command the dog to board as a passenger (`bd_allow_unrestricted_dog_riding`).

### 4. Wild Pack Dynamics & Territorial Rivalries
* Wild wolves spawn in natural pack clusters (min 4, max 8) across biomes including plains, forests, meadows, and mountains (`bd_wolf_spawn_expanded_biomes`).
* Pack leaders initiate territorial dominance disputes (wars, merges, retreats) upon detecting rival pack leaders within 96 blocks (`bd_territorial_rivalry`).

---

## 🔧 Installation & Requirements

1. Install **Fabric Loader** `0.16.9` or higher for Minecraft `26.2`.
2. Place `vanilla-outsider-better-dogs-4.24.1+26.2.jar` into your server or client `mods/` directory.
3. Ensure **DasikLibrary** `1.8.3` or higher is installed.
4. Launch the game using **JDK 25**.

---

*Back to [[Home]] | View [[Version Compatibility|Version-Compatibility]]*
