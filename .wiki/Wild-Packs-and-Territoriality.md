# Wild Packs & Territorial Rivalries

*[[Home]] / Wild Packs & Territoriality*

---

## 🐺 Infobox: Wild Pack Dynamics Overview

| Feature | Specification |
| :--- | :--- |
| **Natural Pack Cluster Size** | **4 to 8 wolves** (`bd_wolf_spawn_group_min` to `bd_wolf_spawn_group_max`) |
| **Expanded Spawning Biomes** | Plains, Forests, Meadows, Mountains (`bd_wolf_spawn_expanded_biomes`) |
| **Territorial Scan Radius** | **96 blocks** (`bd_territorial_search_radius`) |
| **Fatal War Chance** | **5%** chance dispute is to the death (`bd_territorial_fatal_chance`) |
| **Exclusive Disputes** | 1v1 dispute locking enabled (`bd_territorial_exclusive_disputes`) |

---

## ⚔️ 1. Territorial Dispute Matrix (`bd_territorial_rivalry`)

Wild pack leaders scan for rival leaders within 96 blocks. Upon encounter, leaders execute a territorial dispute based on personality pairings:

| Encountering Leaders | War (Fight) % | Merge (Yield) % | Retreat (Flee) % |
| :--- | :---: | :---: | :---: |
| **Aggressive vs Aggressive** | 80% (`bd_territorial_matrix_aa_war`) | 10% (`bd_territorial_matrix_aa_merge`) | 10% |
| **Aggressive vs Normal** | 50% (`bd_territorial_matrix_an_war`) | 40% (`bd_territorial_matrix_an_merge`) | 10% |
| **Aggressive vs Pacifist** | 10% (`bd_territorial_matrix_ap_war`) | 50% (`bd_territorial_matrix_ap_merge`) | 40% |
| **Normal vs Normal** | 20% (`bd_territorial_matrix_nn_war`) | 50% (`bd_territorial_matrix_nn_merge`) | 30% |
| **Normal vs Pacifist** | 5% (`bd_territorial_matrix_np_war`) | 45% (`bd_territorial_matrix_np_merge`) | 50% |
| **Pacifist vs Pacifist** | 0% (`bd_territorial_matrix_pp_war`) | 50% (`bd_territorial_matrix_pp_merge`) | 50% |

---

## 🌲 2. Pack Howling & Cluster Spawning

* **Pack Cluster Spawning**: Wild wolves spawn in coherent pack clusters. Each cluster selects an alpha leader to guide pack movement.
* **Group Pack Howling**: Pack leaders have a 1% chance per 50 seconds (`bd_howl_chance`) to initiate a group pack howl (`betterdogs:entity.wolf.howl`), causing nearby pack members to howl in chorus.

---

## 🎨 3. Dynamic Climate Coat Variants (`bd_dynamic_climate_variants`)

When wild or spawned wolves generate in modded biomes (such as **Biomes O' Plenty**, **Terralith**, **Regions Unexplored**, etc.), **Better Dogs** executes a 3-tier priority pipeline to ensure wolves receive contextually appropriate coat textures:

1. **Priority 1 (Custom Mod / Datapack Variants)**: If another mod or datapack explicitly registered a custom coat variant (e.g., `somemod:frost_wolf`), Better Dogs yields immediately and preserves the custom variant 100% untouched.
2. **Priority 2 (Vanilla Tag Match)**: If Vanilla matched a specific specialized tag (Snowy, Ashen, Rusty, Striped, Black, Spotted), native selection is preserved.
3. **Priority 3 (Better Dogs Dynamic Climate Engine)**: If Priorities 1 & 2 returned un-mapped default Pale/Woods fallback in modded biomes, Better Dogs evaluates the biome's actual climate physics at spawn time:
   - **Cold / Snowy** (`temperature < 0.15` / `Precipitation.SNOW`): Assigns **Snowy Wolf** (`minecraft:snowy`).
   - **Hot & Dry / Arid** (`temperature >= 1.0` & no rain): Assigns **Ashen Wolf** (`minecraft:ashen`) or **Striped/Red Wolf** (`minecraft:striped`).
   - **Hot & Humid / Jungle** (`temperature >= 0.8` & high downfall): Assigns **Rusty Wolf** (`minecraft:rusty`).
   - **Dense / Dark Taiga**: Assigns **Black Wolf** (`minecraft:black`).
   - **Cool Taiga**: Assigns **Chestnut Wolf** (`minecraft:chestnut`).
   - **Grove / Meadow**: Assigns **Spotted Wolf** (`minecraft:spotted`).

---

*Back to [[Home]] | View [[Wolf Personalities|Wolf-Personalities]]*
