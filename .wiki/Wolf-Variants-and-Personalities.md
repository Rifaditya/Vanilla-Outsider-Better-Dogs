# Wolf Variants, Genetics & Personality AI Guide

*[[Home]] / Wolf Variants & Personalities*

---

## 🐺 1. Comprehensive Personality Specification

Every wolf in **Vanilla Outsider: Better Dogs** is generated with an immutable personality trait stored in persistent entity data (`WolfPersistentData`). Personalities dictate combat attributes, follow parameters, fleeing logic, and guard capabilities:

### Personality Comparison Table

| Attribute / Parameter | Aggressive | Normal | Pacifist |
| :--- | :---: | :---: | :---: |
| **Natural Spawn Weight** | 20% (`bd_spawn_aggro_percent`) | 60% (`bd_spawn_normal_percent`) | 20% (`bd_spawn_paci_percent`) |
| **Health Points Modifier** | -10 HP (-5 ❤️) | Baseline (20 HP / 10 ❤️) | +20 HP (+10 ❤️) |
| **Sprint Speed Modifier** | +15% (`bd_aggro_speed_percent`) | 0% (`bd_normal_speed_percent`) | -10% (`bd_paci_speed_percent`) |
| **Attack Damage Modifier** | Baseline | Baseline | -15% (`bd_paci_dmg_percent`) |
| **Attack Knockback Modifier** | Baseline | Baseline | +50% (`bd_paci_knockback_percent`) |
| **Detection Range** | 20 blocks (`bd_aggro_detect_range`) | 16 blocks (Vanilla) | 16 blocks (Vanilla) |
| **Follow Start Distance** | 10 blocks (`bd_aggro_follow_start`) | 10 blocks (`bd_normal_follow_start`) | 6 blocks (`bd_paci_follow_start`) |
| **Chase Distance Limit** | 50 blocks (`bd_aggro_chase_dist`) | Baseline | Baseline |
| **Flee Low Health Chance** | 10% (`bd_aggro_flee_chance`) | 50% (`bd_normal_flee_chance`) | 100% (`bd_paci_flee_chance`) |
| **Guard Patrol Radius** | 12 blocks | 0 blocks (Stationary Post) | 3 blocks |
| **Guard Special Aura** | None | None | Regeneration & Resistance to Owner |
| **Storm Anxiety Sensitivity** | 3x Whine & Shake Rate | Standard Rate | Immune to Storm Fear |

---

## 🧬 2. Genetics & Breeding Mathematics

Personality genetics pass dynamically from parents to offspring during breeding:

```
                      Parent Pair
                          │
         ┌────────────────┼────────────────┐
         ▼                ▼                ▼
   Same Personality  Normal + Variant  Aggro + Pacifist
   (Aggro+Aggro or   (Normal + Aggro   (Diluted Hybrid)
    Paci+Paci)        or Normal+Paci)      │
         │                │                ├─► 50% Dilute to Normal
         ├─► 80% Inherit  ├─► 40% Dominant └─► 25% Variant Parent
         └─► 10% Mutate       Normal
                          └─► 40% Recessive
                              Variant
```

### Inbreeding & Lineage Recovery
* **Linebreeding Penalty**: Breeding parents with offspring or siblings produces an **Inbred Runt** with reduced max scale (capped at 80%) and subtle rotten flesh particles (`bd_show_runt_particles`).
* **Golden Apple Restoration**: Right-clicking an inbred runt with a Golden Apple (`bd_enable_inbred_curing`) cures all linebreeding penalties.
* **Genetic Outcrossing**: Breeding an inbred runt with an unrelated wolf produces healthy outcrossed puppies, clearing the lineage.

---

## 📏 3. Physical Scale System

Wolves inherit physical scale attributes bounded between **70%** (`bd_wolf_min_scale_percent`) and **145%** (`bd_wolf_max_scale_percent`):

* **Rendering & Hitboxes**: Scale attributes (`minecraft:scale`) dynamically resize entity rendering models and physical collision bounding boxes simultaneously.
* **Scale Genetics**: Offspring scale is calculated as the average of parent scales plus a small Gaussian mutation variance.

---

## ⚔️ 4. Wild Territorial Dispute Matrix (`bd_territorial_rivalry`)

Wild pack leaders scan for rival leaders within **96 blocks** (`bd_territorial_search_radius`). Upon encountering a rival pack, leaders execute a territorial dispute based on their personalities:

| Encountering Leaders | War (Fight) % | Merge (Yield) % | Retreat (Flee) % |
| :--- | :---: | :---: | :---: |
| **Aggressive vs Aggressive** | 80% (`bd_territorial_matrix_aa_war`) | 10% (`bd_territorial_matrix_aa_merge`) | 10% |
| **Aggressive vs Normal** | 50% (`bd_territorial_matrix_an_war`) | 40% (`bd_territorial_matrix_an_merge`) | 10% |
| **Aggressive vs Pacifist** | 10% (`bd_territorial_matrix_ap_war`) | 50% (`bd_territorial_matrix_ap_merge`) | 40% |
| **Normal vs Normal** | 20% (`bd_territorial_matrix_nn_war`) | 50% (`bd_territorial_matrix_nn_merge`) | 30% |
| **Normal vs Pacifist** | 5% (`bd_territorial_matrix_np_war`) | 45% (`bd_territorial_matrix_np_merge`) | 50% |
| **Pacifist vs Pacifist** | 0% (`bd_territorial_matrix_pp_war`) | 50% (`bd_territorial_matrix_pp_merge`) | 50% |

---

*Back to [[Home]]*
