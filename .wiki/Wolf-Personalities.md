# Wolf Personalities & Combat AI

*[[Home]] / Wolf Personalities*

---

## 🐺 Infobox: Entity Data Overview

| Attribute | Aggressive | Normal | Pacifist |
| :--- | :---: | :---: | :---: |
| **NBT Enum ID** | `1` (`AGGRESSIVE`) | `0` (`NORMAL`) | `2` (`PACIFIST`) |
| **Natural Spawn Weight** | 20% (`bd_spawn_aggro_percent`) | 60% (`bd_spawn_normal_percent`) | 20% (`bd_spawn_paci_percent`) |
| **Max Health Bonus** | -10 HP (-5 ❤️) | Baseline (20 HP / 10 ❤️) | +20 HP (+10 ❤️) |
| **Sprint Speed Mod** | +15% (`bd_aggro_speed_percent`) | 0% (`bd_normal_speed_percent`) | -10% (`bd_paci_speed_percent`) |
| **Attack Damage Mod** | Baseline (4 HP / 2 ❤️) | Baseline (4 HP / 2 ❤️) | -15% (`bd_paci_dmg_percent`) |
| **Attack Knockback Mod** | Baseline | Baseline | +50% (`bd_paci_knockback_percent`) |
| **Detection Range** | 20 blocks (`bd_aggro_detect_range`) | 16 blocks (Vanilla) | 16 blocks (Vanilla) |
| **Follow Start Distance** | 10 blocks (`bd_aggro_follow_start`) | 10 blocks (`bd_normal_follow_start`) | 6 blocks (`bd_paci_follow_start`) |
| **Chase Distance Limit** | 50 blocks (`bd_aggro_chase_dist`) | Baseline | Baseline |
| **Low HP Flee Threshold** | < 30% Health | < 30% Health | < 30% Health |
| **Flee Probability** | 10% (`bd_aggro_flee_chance`) | 50% (`bd_normal_flee_chance`) | 100% (`bd_paci_flee_chance`) |
| **Guard Patrol Radius** | 12 blocks | 0 blocks (Stationary Post) | 3 blocks |

---

## 🔬 1. Technical Mechanics & Formulas

### A. Health Attribute Formula
Max health for a wolf entity is calculated upon spawn or taming by applying the personality attribute modifier to the vanilla baseline ($20.0 \text{ HP}$):

$$\text{Max Health} = \text{Baseline HP} + \text{Personality HP Bonus} + \text{Inbred Penalty}$$

* **Aggressive**: $20.0 - 10.0 = 10.0 \text{ HP}$ ($5 \text{ Hearts}$)
* **Normal**: $20.0 + 0.0 = 20.0 \text{ HP}$ ($10 \text{ Hearts}$)
* **Pacifist**: $20.0 + 20.0 = 40.0 \text{ HP}$ ($20 \text{ Hearts}$)

### B. Combat Speed Calculation
Movement speed during active combat target engagement uses attribute modifiers attached to `minecraft:generic.movement_speed`:

$$\text{Sprint Speed} = \text{Base Speed} \times \left(1 + \frac{\text{bd\_aggro\_speed\_percent}}{100}\right)$$

---

## 🧬 2. NBT Component Storage (`betterdogs:wolf_data`)

Personality data is stored directly inside the custom entity attachment codec `betterdogs:wolf_data`:

```snbt
{
  "betterdogs:wolf_data": {
    "personality": 1,
    "scale": 1.15,
    "is_inbred": 0b,
    "is_guarding": 0b,
    "guard_x": 128,
    "guard_y": 64,
    "guard_z": -256,
    "favorite_treat": "minecraft:cooked_porkchop",
    "gift_merits": 8
  }
}
```

---

## ⚡ 3. Behavior Breakdown

### Aggressive Personality
* **AI Behavior**: Actively scans a **20-block radius** for hostile monsters, charging immediately upon detection.
* **Chase Limit**: Pursues targets up to **50 blocks** away from its owner before pathfinding back.
* **Storm Anxiety**: Whines and paces at **3x standard frequency** during active thunderstorms.

### Pacifist Personality
* **AI Behavior**: Prefers defense over offense. Attacks only when the owner takes damage or when defending nearby allies.
* **Knockback Buff**: Applies **+50% knockback** to hit targets, pushing hostiles back safely.
* **Sentinel Aura**: When placed in Guard Mode, radiates **Regeneration I** and **Resistance I** to nearby owners within 3 blocks.

---

*Back to [[Home]] | View [[Guard Mode & Sentinels|Guard-Mode-and-Sentinels]]*
