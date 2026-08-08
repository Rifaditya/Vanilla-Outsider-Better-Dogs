# Wild Packs & Territorial Rivalries

*[[Home]] / [[Player Guide & Mechanics|Player-Guide-and-Mechanics]] / Wild Packs*

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

*Back to [[Home]] | View [[Wolf-Personalities]]*
