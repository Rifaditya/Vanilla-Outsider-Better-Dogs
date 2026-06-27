# Milestone: Core Personalities & Genetics

Introduced in **`v4.0.0` (MC 26.2)** and backported to **`v3.4.0` (MC 26.1.2)**, this milestone overhauls tamed wolves by adding permanent personalities, genetic DNA rolls, stat heredity, parent kinship checks (inbreeding), runts, and size scaling.

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

Every wolf possesses a unique genetic identity determined by its UUID. To prevent determinism from being exploited, personality traits, sound variant lottery rolls, and social behaviors are seeded via aUUID bitwise XOR formula:

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
The mod tracks ancestry to prevent inbreeding loops. Kinship data is persisted directly to the wolf's NBT:
* **NBT tags**: `parent1Uuid` and `parent2Uuid`.
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
* The maximum litter size and frequency are fully configurable via native GameRules.

---

## 🤝 Quality of Life Features

* **Paper Adoption**: Sneak + Right-Clicking a tamed wolf with Paper sets it into a pending adoption state with pink particle indicators. Any other player can then Right-Click to claim ownership of the dog.
* **Calm Down**: Sneak + Right-Clicking a wolf with an empty hand forces it to sit, immediately purging its anger state and clearing its current attack target.
* **Gifting & Rewards**: Interact positively with your dog (feeding, sitting, or calming down) to build up its merit points over time. Once the threshold is met, the dog may wake you up with a gift the next time you sleep in a bed! Accidental attacks will reset their merit progress unless `bd_demerit_accidental_attacks` is turned off.
