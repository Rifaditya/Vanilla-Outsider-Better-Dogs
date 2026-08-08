# Breeding, Genetics & Litter Sizes

*[[Home]] / [[Player Guide & Mechanics|Player-Guide-and-Mechanics]] / Breeding & Genetics*

---

## 🧬 Infobox: Genetics Summary

| Parameter | Specification |
| :--- | :--- |
| **Max Litter Size** | Up to **4 puppies** (`bd_wolf_litter_max_size`) |
| **Extra Puppy Chance** | **20%** per additional puppy (`bd_wolf_litter_extra_chance`) |
| **Same Parent Inheritance** | **80%** chance to inherit exact trait (`bd_breed_same_chance`) |
| **Same Parent Mutation** | **10%** chance to mutate variant (`bd_breed_same_other_chance`) |
| **Mixed Dominant Chance** | **40%** Normal inheritance (`bd_breed_mixed_dominant_chance`) |
| **Mixed Recessive Chance** | **40%** Variant inheritance (`bd_breed_mixed_recessive_chance`) |
| **Diluted Normal Chance** | **50%** Normal inheritance when breeding Aggro + Paci (`bd_breed_diluted_normal_chance`) |
| **Physical Scale Range** | **70%** (`bd_wolf_min_scale_percent`) to **145%** (`bd_wolf_max_scale_percent`) |
| **Inbreeding Cure** | Golden Apple (`minecraft:golden_apple`) (`bd_enable_inbred_curing`) |

---

## 🎲 1. Genetic Inheritance Probabilities

When two tamed wolves are bred using meat, offspring personality is determined using dynamic weighted random calculations:

$$\text{Offspring Personality} = \begin{cases} 
\text{Parent Trait} & \text{Prob} = 80\% \quad (\text{Matching Parents}) \\
\text{Variant Mutation} & \text{Prob} = 10\% \quad (\text{Matching Parents}) \\
\text{Dominant Normal} & \text{Prob} = 40\% \quad (\text{Normal + Variant}) \\
\text{Recessive Variant} & \text{Prob} = 40\% \quad (\text{Normal + Variant}) \\
\text{Diluted Reversion} & \text{Prob} = 50\% \quad (\text{Aggressive + Pacifist})
\end{cases}$$

---

## 🐕 2. Variable Litter Sizes

Unlike vanilla breeding (which yields 1 puppy), **Better Dogs** calculates dynamic litter sizes from 1 to 4:

1. **First Puppy**: Guaranteed (100%).
2. **Second Puppy**: 20% roll (`litter_two` advancement).
3. **Third Puppy**: 20% roll (`litter_three` advancement).
4. **Fourth Puppy**: 20% roll (`litter_four` advancement).

---

## 🧬 3. Inbreeding & Lineage Recovery

* **Inbred Runt Penalty**: Linebreeding closely related wolves (siblings or parent/child) tags offspring as `is_inbred: 1b`. Inbred runts suffer a max scale cap of 80% and emit subtle rotten flesh particles (`bd_show_runt_particles`).
* **Golden Apple Cure**: Feeding a Golden Apple to an inbred runt cures penalties, grants **A Fresh Start** advancement (+100 XP), and sets `is_inbred: 0b`.
* **Outcrossing**: Breeding an inbred runt with an unrelated wolf yields a healthy puppy (`outcross_runt` advancement).

---

*Back to [[Home]] | View [[Wolf-Personalities]]*
