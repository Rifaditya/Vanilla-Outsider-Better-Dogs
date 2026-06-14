# Milestone: Smart Survival & Performance

Introduced in **`v4.7.0` (MC 26.2)** and backported to **`v3.9.0` (MC 26.1.2)**, this milestone hardens wolf survivability against common environmental deaths and introduces critical performance optimizations to support massive packs without dropping TPS.

---

## 🛡️ Smart Survival AI

To prevent the common tragedies of tamed wolves jumping into lava or walking off high cliffs, this milestone overhauls their pathfinding navigation:
1. **Cliff Safety Gating**: Modifies the navigation path evaluator (`WalkNodeEvaluatorMixin`) to detect drops that would cause more than $4$ points of fall damage. If detected, the path node is blocked, preventing the wolf from jumping off.
2. **Hazard Avoidance**: Enhanced pathfinding weight penalties are applied to blocks containing fire, lava, campfire, sweet berry bushes, and drowning hazards.
3. **Creeper Panic**: When a nearby Creeper begins its fuse (hissing), wolves immediately abort their current attack goal and flee in the opposite direction.
4. **Friendly Fire Sweep Protection**: Tamed wolves ignore sweeping attack damage from their owner, preventing accidental player-inflicted deaths during combat.
5. **Scavenge Feeding**: Tamed wolves scan the ground for dropped edible items (raw/cooked meat). If found, they navigate to the item, consume it (deleting the item stack), and heal themselves, eliminating the need for manual feeding.

---

## ⚡ Performance Engineering

Operating large wolf packs in vanilla Minecraft causes significant server lag due to quadratic path checks ($O(N^2)$). Better Dogs addresses this with three performance architectures:

### 1. The Event-Driven AI Scheduler
Replaces vanilla's active tick polling with event triggers:
* Instead of running proximity checks every game tick, wolves register listeners for specific world events (e.g., player damage, mob sounds).
* Tick checks are throttled using time-sliced intervals (e.g., rolling howling checks only once every 5 seconds).

### 2. $O(N)$ Cooperative Boids Caching
When a pack of wolves follows their leader or owner, the mod caches pathfinding routes:
* Followers share the path calculation calculated by the leader rather than each running independent pathfinding loops.
* This reduces pathfinding CPU usage from quadratic $O(N^2)$ to linear $O(N)$.

### 3. Multi-Threading & C2ME Safety
* **Thread Isolation**: All custom pathfinding node evaluations are thread-safe and isolated from the main tick thread.
* **C2ME Compatibility**: Compatible with the **C2ME (Chunk Map Multi-Threaded Engine)** mod, ensuring multi-threaded chunk loading does not cause race conditions during wolf coordinate calculations.
* **DasikLibrary Integration**: Leverages the high-performance genetics and scheduling API surface of **DasikLibrary 1.8.0** for optimized execution.
