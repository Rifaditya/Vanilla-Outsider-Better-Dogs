# Concept: Configurable Wolf Spawning & Commonality

This document outlines the design and implementation specifications for the **Configurable Wolf Spawning & Commonality** feature under the Vanilla Outsider (VO) philosophy. It allows players and modpack creators to customize how frequently and in what group sizes wild wolves spawn naturally in the world.

---

## 🏛️ Core Design Philosophy (Vanilla Outsider)
* **Respect Player's Time**: Reduces the friction and grinding required to find wolves in survival mode, especially when trying to build large packs.
* **Biome Consistency**: Modifies spawning parameters dynamically within existing, logical vanilla biomes (Taigas, Forests, Snowy slopes) rather than adding custom biomes.
* **Dynamic Adjustability**: Exposes spawn weights, pack sizes, and biome options through dynamic configuration/GameRules so they can be changed on the fly without restarting the game.
* **Compatibility Gating**: Modifies the spawn listings cleanly during registration or via server-side spawning injection, maintaining full client-side compatibility.

---

## 📅 Implementation Stages

### 🎬 Stage 1: Spawn Weight & Pack Size Multipliers
This stage introduces the core configuration settings to adjust spawn weights and pack group sizes in vanilla biomes.
* **Dynamic Spawn Weight Injection**:
  * Registers a new GameRule: `vanilla-outsider-better-dogs:bd_wolf_spawn_weight_multiplier` (Default: `100` = 1.0x). Allowed range: `0` (disable spawn) to `1000` (10.0x weight).
  * Hooks into biome load/registration events or intercepts natural spawner weight queries to multiply the base spawn weight of wolves.
* **Configurable Pack Size Spawning**:
  * Registers GameRules:
    * `vanilla-outsider-better-dogs:bd_wolf_spawn_group_min` (Default: `4`).
    * `vanilla-outsider-better-dogs:bd_wolf_spawn_group_max` (Default: `8`).
  * Intercepts spawning limits to allow wild packs to spawn in larger groups, naturally generating wild wolf packs out of the box.

---

### 🎬 Stage 2: Biome Expansion & Habitat Settings
This stage allows wolves to spawn in a broader set of biomes if configured.
* **Habitat Expansion**:
  * Registers GameRule: `vanilla-outsider-better-dogs:bd_wolf_spawn_expanded_biomes` (Default: `false`).
  * When set to `true`, injects wolf spawn entries into similar or adjacent biomes:
    * **Plains & Meadows**: Adds sparse wolf spawns (lower weight).
    * **Forests (Standard)**: Allows standard wolves to spawn in all standard forest types, not just sparse taigas.
    * **Extreme Hills / Mountains**: Adds snowy/cold variants.
* **Habitat Preservation Safeguard**:
  * Ensures that custom biome settings respect the vanilla biome tags so they only apply to biomes in the `minecraft:has_structure/ruined_portal_forest` or standard temperate/cold category.

---

## ⚙️ Configuration (GameRules & Config)

All parameters are exposed via the **Vanilla Outsider: Better Dogs** GameRule category and Cloth Config GUI:

| GameRule Key | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `vanilla-outsider-better-dogs:bd_wolf_spawn_weight_multiplier` | Integer | `100` | Multiplies spawn weight (100 = 1.0x, 200 = 2.0x, etc.). |
| `vanilla-outsider-better-dogs:bd_wolf_spawn_group_min` | Integer | `4` | Minimum size of a naturally spawning wild wolf pack. |
| `vanilla-outsider-better-dogs:bd_wolf_spawn_group_max` | Integer | `8` | Maximum size of a naturally spawning wild wolf pack. |
| `vanilla-outsider-better-dogs:bd_wolf_spawn_expanded_biomes` | Boolean | `false` | Enables wolves spawning in plains, meadows, and regular forests. |

---

## 🧪 QA & Verification

### Automated Verification
* Biome modification injections will be checked during startup to ensure no null pointer exceptions or overlapping category registration conflicts occur.

### Manual Test Cases
1. **Spawn Frequency Check**:
   * Set multiplier to `500` (5.0x) and use `/kill @e[type=wolf]` followed by `/spawn` tick cycles.
   * **Expected Result**: Wolves spawn noticeably more frequently in Taiga biomes.
2. **Biome Extension Check**:
   * Set `bd_wolf_spawn_expanded_biomes` to `true` and check local plains biome spawns.
   * **Expected Result**: Wolves spawn in Plains/Meadow biomes.
