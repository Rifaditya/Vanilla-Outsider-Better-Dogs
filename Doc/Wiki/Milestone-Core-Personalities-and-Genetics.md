# Milestone: Core Personalities, Breeding & Genetics

Introduced in **`v4.0.0` (MC 26.2)** and backported across all version anchors, this milestone overhauls tamed wolves by adding permanent personalities, genetic DNA rolls, stat heredity, parent kinship checks (inbreeding), runts, dynamic size scaling, and morning gift loot foraging.

---

## 🧠 The Personality Matrix

When tamed, every wolf rolls a permanent personality type. Personalities alter their base combat stats and determine their behaviors in **Guard Mode**:

| Personality | Stat Bias | Visual Particles | Patrol Pattern | Primary Role |
| :--- | :--- | :--- | :--- | :--- |
| **Aggressive** | +Speed, +Attack, -Health | Red (`0xFF3333`) | Sweeping outer sweeps (80% range) | Combat Scout |
| **Normal** | Balanced vanilla-plus | Gold (`0xFFD700`) | Sentinel post or radial stars | Versatile Classic |
| **Pacifist** | +Health, +Resistance, -Damage | Teal/Green (`0x00FF88`) | Tight protective orbital loops | Sentinel Alarm / Healer |

---

## 🧬 Individual DNA & UUID Lottery

Every wolf possesses a unique genetic identity determined by its UUID. To prevent determinism from being exploited, personality traits, sound variant lottery rolls, and social behaviors are seeded via a UUID bitwise XOR formula:

\[\text{Seed} = \text{UUID.getMostSignificantBits()} \oplus \text{UUID.getLeastSignificantBits()} \oplus 7381940\text{L}\]

This seed is passed to a pseudo-random number generator to ensure each wolf's personality and stats are stable, persistent, and unique.

---

## 📏 Dynamic Size Scaling

Wolves scale physically in the game world based on their max health bonus. Healthy, tanky wolves grow larger, while runts or low-health wolves remain small:

\[\text{Scale} = 1.0 + (\text{healthBonus} \times 0.012)\]

* **Max Scaling Bounds**:
  * **Aggressive Runt (Worst Case)**: $0.808\times$ visual size.
  * **Pacifist Champion (Best Case)**: $1.312\times$ visual size.

---

## 🧪 Stat Heredity & Kinship Engine

### 1. Stats Inheritance
When breeding two wolves, the offspring inherits a blend of the parents' stats (Max Health, Attack Damage, Speed) with a triangular mutation offset:

\[\text{BabyStat} = \frac{\text{Parent1Stat} + \text{Parent2Stat}}{2} + \text{TriangularMutationRoll}\]

### 2. Personalities Inheritance Matrix
Personality traits are passed down from parents. If both parents share a personality, the offspring has a higher chance of inheriting it:
* **Same-Personality Parents**: $80\%$ chance to inherit the shared personality; remaining $20\%$ split between the other two types.
* **Mixed-Personality Parents**: $40\%$ chance to inherit Parent 1's personality, $40\%$ for Parent 2's, and $20\%$ for the remaining type.

### 3. Kinship NBT & Inbreeding Checks
The mod tracks ancestry to prevent inbreeding loops. Kinship data is persisted directly to the wolf's custom data attachments / NBT:
* **Ancestry keys**: `parent1Uuid` and `parent2Uuid`.
* **Inbreeding check**: If Parent 1 and Parent 2 share parent UUIDs (siblings) or if one parent is the other's ancestor, the check triggers an **Inbred Runt** status.

### 4. The Inbred Runt Penalty
Inbred puppies suffer from severe genetic penalties:
* **Stat Penalties**: Max health reduced by $50\%$, speed reduced by $30\%$, and attack damage capped at minimum.
* **Size**: Capped at $0.808\times$ scale.
* **Curing**: Runts can be cured of their genetic defects using a **Golden Apple**, restoring normal growth curves. Alternatively, outcrossing the runt with an unrelated, healthy wolf will yield healthy puppies.

---

## 🐕 Litter System

Breeding wolves no longer guarantees exactly one puppy. The **Litter System** rolls a probability curve allowing wolves to produce multiple puppies per breed:
* Each puppy in the litter rolls its personality, stats, and scale **independently**.
* The maximum litter size and frequency are fully configurable via native GameRules (`bd_litter_max_size`).

---

## 🎁 Morning Gifts & Data-Driven Loot Tables

Tamed wolves that have formed a strong bond with their owner can bring morning gifts:
1. **Trigger Conditions**: Occurs either upon owner waking up from a bed or naturally during early morning hours (world time $0 \le t \le 2000$ ticks).
2. **Qualification Requirements**:
   * Wolf must be at **100% full health** (`wolf.getHealth() >= wolf.getMaxHealth()`).
   * No hostile monsters within a 16-block radius.
   * Wolf must meet positive interaction merits (feeding, sitting, petting).
   * Daily cooldown: maximum 1 gift per in-game day.
3. **Data-Driven Personality Loot**:
   Gifts are queried directly from datapack loot tables in `data/vanilla-outsider-better-dogs/loot_table/morning_gift/`:
   * `aggressive.json`: Bones, rotten flesh, mob drops, arrows.
   * `pacifist.json`: Flowers, sweet berries, apples, seeds.
   * `normal.json`: Sticks, rabbit feet, leather, feathers.
   * `rare_treasure.json`: 5% chance roll for enchanted books, name tags, golden apples, or music discs.

---

## 🍖 Favorite Treats & Puppy Curiosity

* **Favorite Treats**: Each wolf deterministically calculates a preferred favorite treat from `#vanilla-outsider-better-dogs:treats` based on its UUID. Feeding a wolf its favorite treat triggers double heart particles and grants temporary speed/regeneration buffs.
* **Puppy Curiosity**: Young puppies naturally wander towards nearby curious foliage, flowers, and crops defined in `#vanilla-outsider-better-dogs:curiosity_blocks` to sniff and inspect them with happy particles.
