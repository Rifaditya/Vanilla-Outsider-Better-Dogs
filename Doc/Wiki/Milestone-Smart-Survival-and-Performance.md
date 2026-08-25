# Milestone: Smart Survival, Fire Evasion & Performance

Introduced in **`v4.7.0` (MC 26.2)** and refined across modern anchors, this milestone hardens wolf survivability against common environmental hazards, introduces the nearest-water fire evasion AI, data-driven tags, and delivers zero-allocation performance architectures.

---

## 🛡️ Smart Survival & Hazard Evasion AI

To prevent the common tragedies of tamed wolves jumping into lava or walking off high cliffs, Better Dogs completely overhauls wolf navigation:

1. **Wolf Fire Survival AI (Water Sprint & Panic Evasion)**:
   * When a wolf catches fire or stands in lava, it immediately executes an emergency 16-block 3D flood-fill search for the closest safe water block.
   * If water is located, the wolf sprints towards it at $1.35\times$ speed to extinguish itself.
   * If no water is reachable within 16 blocks, it falls back to a fast-paced panic evasion sprint away from fire sources, drastically increasing survival chances.
2. **Cliff Safety Gating**:
   * Inspects terrain drops exceeding 3 blocks. If a push or step would cause severe fall damage without a safe water landing below, the move is rejected.
3. **Thermal Hazard Avoidance**:
   * Enhanced pathfinding penalties are applied to blocks containing fire, lava, campfires, sweet berry bushes, magma blocks, and powder snow.
4. **Creeper Blast Evasion**:
   * When a nearby Creeper begins its fuse (hissing), wolves immediately abort their current attack goal and flee in the opposite direction.
5. **Friendly Fire Sweep Protection**:
   * Tamed wolves ignore sweeping attack damage from their owner, preventing accidental player-inflicted deaths during combat.
6. **Scavenge Feeding**:
   * Tamed wolves scan the ground for dropped edible meat items (`#vanilla-outsider-better-dogs:raw_food`, `#vanilla-outsider-better-dogs:cooked_food`). If injured, they navigate to the item, consume it to heal, and emit heart particles.

---

## 🏷️ Data-Driven Datapack Tags

All environmental block checks and item interactions are fully data-driven via datapack tags with safe in-code fallbacks:

* `#vanilla-outsider-better-dogs:curiosity_blocks`: Foliage, flowers, ferns, crops, and leaves investigated by curious puppies.
* `#vanilla-outsider-better-dogs:treats`: High-value snacks (cooked mutton, rabbit stew, golden apples, baked potatoes, etc.) calculated for individual favorite treat buffs.
* `#vanilla-outsider-better-dogs:seats`: Stair, slab, bed, carpet, and chair blocks where wolves naturally choose to rest.
* `#c:chairs`: Common convention tag supporting modded furniture and seating addons.

---

## ⚡ Zero-Allocation Performance Engineering

Operating large wolf packs in vanilla Minecraft causes significant server lag due to quadratic path checks ($O(N^2)$). Better Dogs solves this through zero-allocation memory design:

### 1. Zero-Allocation FastRandom & Loop Memory
* Replaced standard runtime `new Random()` object instantiations with thread-safe `FastRandom.INSTANCE` and entity-bound `RandomSource`.
* Eliminated transient array instantiations in genetic inheritance and AI loops via static branch matching.

### 2. The Event-Driven AI Scheduler
* Instead of running proximity checks every game tick, wolves register listeners for specific world events (e.g., player damage, mob sounds).
* Tick checks are throttled using time-sliced intervals (e.g., rolling howling checks only once every 5 seconds).

### 3. $O(N)$ Cooperative Boids Caching
* Followers share the path calculation calculated by the leader rather than each running independent pathfinding loops, cutting pathfinding CPU overhead from $O(N^2)$ to linear $O(N)$.
