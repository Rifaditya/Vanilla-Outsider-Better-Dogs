# Milestone: Wild Packs & Territoriality

Introduced in **`v4.3.0` (MC 26.2)** and backported to **`v3.5.0` (MC 26.1.2)**, this milestone overhauls wild wolves into cohesive, territorial social groups that interact dynamically with other packs.

---

## 🐺 Persistent Pack Leaders & Anchoring

Wild wolves no longer wander aimlessly as disjointed single entities. Instead, they form persistent packs of up to 8 members anchored to a dominant leader:
* **Leader Anchor**: Pack followers track their leader's coordinates and stay anchored within a tight perimeter.
* **Wild Personalities**: Wild wolves exhibit unique AI goals depending on their leader's personality (e.g., Aggressive leaders trigger pack-wide hunts, Pacifist leaders lead retreats).
* **Reinforcements**: When a wild pack is engaged or challenged, nearby wild pack members can spawn or run to defend their leader's territory.

---

## 📏 Dynamic Follower Spread Scaling

To prevent multiple wolves from colliding and overlapping while moving, pack spread spacing scales dynamically based on the number of active followers $N$:

\[f(N) = \text{multiplier} \times \sqrt{N - 1}\]

* **Spread Multiplier**: Dynamically increases the separation radius as the pack grows to ensure visual spacing and realistic formations.
* **GameRules**: Fully configurable via separate multipliers and maximum bounds for both wild and tamed packs (`bd_tamed_pack_spread_multiplier`, `bd_wild_pack_spread_multiplier`).

---

## ⚔️ True Pack Hunting Tactics (Flanking)

Unlike vanilla wolves that path directly at their targets in a single-file line, Better Dogs implements advanced pack hunting tactics:
* **Dynamic Flanking**: The pack leader will attack the target directly, while the followers intelligently spread out and attempt to attack the target from the sides or behind.
* **Encirclement**: By spacing out at varying angles around the enemy, the pack can effectively surround their prey, minimizing the chance of being hit by a single sweeping attack or splash potion.
* **Configurable**: This advanced combat AI can be fully controlled via the `bd_pack_flanking_tactics` GameRule.

---

## ⚔️ The Territorial Probability Matrix

When two wild packs cross paths, the mod runs a complex, personality-driven math matrix based on the leaders' personalities to resolve the border dispute:

| Leader A | Leader B | War (Duel/Combat) | Merge (Dominance) | Retreat (Yield) |
| :--- | :--- | :--- | :--- | :--- |
| **Aggressive** | **Aggressive** | **$80\%$** | $10\%$ | $10\%$ |
| **Aggressive** | **Normal** | **$50\%$** | $30\%$ | $20\%$ |
| **Aggressive** | **Pacifist** | $10\%$ | **$60\%$** | $30\%$ |
| **Normal** | **Normal** | $30\%$ | $30\%$ | **$40\%$** |
| **Normal** | **Pacifist** | $10\%$ | $40\%$ | **$50\%$** |
| **Pacifist** | **Pacifist** | $0\%$ | $20\%$ | **$80\%$** |

---

## ⚔️ 1v1 Leader Duels & Yield-and-Merge

If the Territorial Matrix rolls **War**, the dispute can escalate to a dramatic **1v1 Leader Duel**:
* **The Duel Goal**: The two leaders anchor themselves to a small duel arena and fight one-on-one, while their respective followers stand in a circle to watch the combat.
* **The Yield**: When one leader's health falls below $20\%$, it yields to the victor, ending the duel.
* **Pack Merging**: The defeated leader and its followers yield their territorial markers and **merge** into the winning pack. This allows wild packs in the world to naturally consolidate into massive, unified wolf colonies.
