# Better Dogs: Technical Manual (v4.5.13)

**Philosophy**: "Vanilla Outsider" - Mechanics should be invisible, intuitive, and seamlessly integrated into the base game. **Native Game Rules**, **ModMenu Configuration**, and **Social DNA** provide deep immersion.

---

## 1. Personality & Genetic DNA System

Every wolf possesses permanent **Social DNA** (64-bit seed) and a calculated **Social Scale** (Size variation) stored via the **Fabric Data Attachment API** inside the `WolfPersistentData` record.

### A. Attributes & UUID Seeding
- Derived from the wolf's unique UUID.
- Determines rolled Max Health, Attack Damage, and Speed via triangular distributions.

### B. Size Scaling
- Visual size scales dynamically: `scale = 1.0 + (healthBonus * 0.012)`.
- Re-scales range from a tiny **0.808x** (worst-case Aggressive runt) to a giant **1.312x** (best-case Pacifist).
- Hitboxes remain vanilla-sized for 100% pathfinding compatibility.

### C. Breeding & Inbreeding Prevention
- Puppies inherit traits based on parent averages.
- Breeding related wolves (parent-child or sibling pairings tracked via NBT UUIDs) triggers a severe inbreeding penalty stunting the baby to runt scale.
- Recovery is possible by outcrossing with unrelated healthy wolves or by feeding a Golden Apple (gated by `bd_enable_inbred_curing`).

---

## 2. Guard Mode & Sentry Patrols

Anchored to block positions via Shift+Right-Click with a bone (consuming exactly 1 bone).

- **Perimeter Sweeps**: Aggressive guards sweep outer circles and scan outward.
- **Radial Star Patrols**: Normal guards alternate walking out and back or stand at post.
- **Orbital Circles**: Pacifist guards orbit closely, whining and emitting `NOTE` particles when hostiles approach.
- **Line-of-Sight Filtering**: Normal and Aggressive guarding wolves strictly require line-of-sight to target hostiles, preventing them from targeting cave monsters.
- **Grace Buffs**: Pacifists tick regeneration and resistance to owners/allies within 6 blocks of the post/wolf.

---

## 3. Immersive Interactions

- **Calm Down**: Shift+Right-Click with an empty hand sits the wolf down, clearing navigation, targets, and anger.
- **Paper Adoption**: Shift+Right-Click with a sheet of Paper triggers adoption pending; another player can right-click to take ownership.
- **Low Health Fleeing**: AI makes wolves run away when health drops below 30%.
- **Storm Anxiety**: Reactive thunderstorms behavior gated by personality.

---

## 4. Performance & Swarm Optimization

### A. Throttling Checks
- **Wild territorial scans**: 96-block rival pack search is throttled to run once every **40 to 80 ticks** (2-4 seconds) instead of every tick.
- **Active follower spacing scans**: Follower spacing updates are throttled to run once every **20-40 ticks** (1-2 seconds) using staggered randomized timers.

### B. Cooperative Spacing Cache
- Pack members share active follower counts through a static registry `FollowerSpacingCache` mapped to their owner's UUID, reducing scans to a single request per interval for the entire pack.
- Spacing scan radius scales dynamically based on follower count: $\min(32.0 + N \times 0.5, 64.0)$.

### C. Transient JVM-Level Caches
- Volatile ticks (like `lastDamageTime`) are stored in `@Unique` JVM fields inside the mixin rather than writing to the 19-field immutable attachment record every tick, writing back to persistent data only during entity save/load operations (`readAdditionalSaveData`/`addAdditionalSaveData`).

---
*Documentation for v4.5.13*
