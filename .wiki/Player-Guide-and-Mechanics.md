# Player Guide & Gameplay Mechanics

*[[Home]] / Player Guide & Mechanics*

---

## 🐺 1. Wolf Personality System

In **Vanilla Outsider: Better Dogs**, every wolf spawns with one of three distinct personality types. Personalities dynamically adjust combat statistics, movement speeds, detection ranges, and survival behaviors:

| Personality | Natural Spawn Chance | HP Bonus | Speed Mod | Attack Damage | Follow Start Dist | Flee Low HP Chance | Special Trait |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :--- |
| **Aggressive** | 20% | -10 HP (-5 ❤️) | +15% | Baseline | 10 blocks | 10% | +20 block detection range, 50 block chase limit, higher storm anxiety |
| **Normal** | 60% | 0 HP | 0% | Baseline | 10 blocks | 50% | Balanced vanilla behavior, dominant genetics in cross-breeding |
| **Pacifist** | 20% | +20 HP (+10 ❤️) | -10% | -15% | 6 blocks | 100% | +50% knockback, applies Regeneration & Resistance to owners when guarding |

---

## 🧬 2. Genetics, Breeding & Inbreeding

### Personality Inheritance Rules
When breeding two tamed wolves, personality traits pass to offspring according to dynamic genetic probabilities:

* **Same Personality Parents**: 80% chance offspring inherits the exact same personality (`bd_breed_same_chance`), 10% chance to mutate into a variant personality (`bd_breed_same_other_chance`).
* **Normal + Variant Parents**: 40% chance dominant Normal (`bd_breed_mixed_dominant_chance`), 40% chance recessive variant trait (`bd_breed_mixed_recessive_chance`).
* **Aggressive + Pacifist Parents**: 50% chance to dilute to Normal (`bd_breed_diluted_normal_chance`), 25% chance to inherit parent variant (`bd_breed_diluted_other_chance`).

### Variable Litter Sizes
Breeding wolves yields dynamic litters ranging from 1 up to **4 puppies** (`bd_wolf_litter_max_size`). Each additional puppy has a 20% extra chance to spawn (`bd_wolf_litter_extra_chance`).

### Inbreeding & Recovery
* **Inbred Runt Status**: Repeatedly linebreeding closely related wolves (parents/siblings) produces an *Inbred Runt* with reduced max scale and subtle rotten flesh particles (`bd_show_runt_particles`).
* **Golden Apple Cure**: Feeding a Golden Apple to an inbred wolf immediately cures its genetic penalties, restoring healthy attributes (`bd_enable_inbred_curing`).
* **Outcrossing**: Breeding an inbred runt with an unrelated wild or imported wolf yields healthy outcrossed offspring, recovering the family lineage.

---

## 📏 3. Physical Scale Genetics

Wolves express physical scale variations bounded between **70%** (`bd_wolf_min_scale_percent`) and **145%** (`bd_wolf_max_scale_percent`). Physical rendering scale dynamically matches collision bounding boxes.

> **Config vs GameRule Warning**: Global configuration options (`wolfMinScale`, `wolfMaxScale`) set baseline defaults for **NEWLY GENERATED** worlds only. To modify scale bounds in existing worlds, use the in-game command `/gamerule betterdogs:bd_wolf_min_scale_percent <value>` or the GameRules edit GUI screen.

---

## 🛡️ 4. Guard Mode & Sentinel Patrols

Command your tamed dogs to secure bases, outposts, or pastures:

1. **Activation**: Hold a **Bone** and **Shift + Right-Click** a tamed wolf.
2. **Behavior by Personality**:
   * **Aggressive Guard**: Patrols a **12-block radius** around its assigned post (`bd_guard_patrol_range_aggressive`), attacking hostile mobs on sight.
   * **Normal Guard**: Holds a stationary post (`bd_guard_patrol_range_normal`), defending against nearby threats.
   * **Pacifist Guard**: Patrols a **3-block radius** (`bd_guard_patrol_range_pacifist`). When guarding, it radiates **Regeneration** and **Resistance** status effects to nearby owners (`bd_pacifist_guard_buffs`).

---

## 📯 5. Tactical Goat Horn Commands & Vehicle Boarding

### Goat Horn Commands (Range: 64 Blocks)
Sounding a goat horn near your tamed wolves issues tactical commands (`bd_horn_command_range`):

* **Ponder Horn**: Orders all nearby owned wolves to enter Guard Mode at their current locations.
* **Sing Horn**: Toggles Tactical Override, commanding Aggressive wolves to adopt Pacifist defensive rules for 30 seconds (`bd_horn_override_duration`).
* **Seek/Call Horn**: Recalls distant following wolves back to your side.

### Vehicle Boarding System
1. **Shift + Right-Click** a tamed dog to select it for transport command (`bd_allow_unrestricted_dog_riding`).
2. **Shift + Right-Click** a vehicle (Boat, Minecart, Horse) to command the dog to board as a passenger.

---

## 🎁 6. Ground Feeding, Survival & Morning Gifts

* **Automatic Ground Feeding**: Injured tamed dogs automatically eat dropped raw or cooked meat from the ground to heal (`bd_dogs_eat_raw_food`, `bd_dogs_eat_cooked_food`).
* **Creeper Blast Evasion**: Tamed dogs detect swelling creepers and sprint radially away at 1.5x speed (`bd_creeper_awareness`, `bd_creeper_evasion_enabled`).
* **Cliff Safety**: Prevents wolves from chasing targets off steep precipices or into void drops (`bd_cliff_safety`).
* **Morning Gifts**: Tamed wolves with high positive interaction counts (`bd_gift_feed_threshold`) bring morning gifts upon sunrise (Aggressive: bones, flesh, arrows; Pacifist: berries, seeds, flowers, mushrooms).

---

*Back to [[Home]]*
