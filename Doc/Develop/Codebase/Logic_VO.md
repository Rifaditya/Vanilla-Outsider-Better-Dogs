# Core Logic: Better Dogs (v4.5.13)

This document explains the internal logic governing wolf personalities, genetics, and performance-minded optimization architectures.

## 1. Personality & Stat Rolling Logic

Personalities and stat ranges roll once per wolf life cycle, typically when wild wolves spawn.

- **UUID-based Seeding**: A deterministic seed based on the wolf's unique UUID is used to roll attribute modifiers (Max Health, Attack Damage, Speed) using a symmetric triangular distribution.
- **Taming**: Personality and attributes persist upon successful taming (preventing size jumps).
- **Spawn Chance Rules**: `bd_spawn_normal_percent`, `bd_spawn_aggro_percent`, and `bd_spawn_paci_percent` govern the relative distribution weights of spawned wild personalities.

## 2. Stat Modifiers & Scaling
Max health modifiers translate directly to visual size scales:
$$\text{scale} = 1.0 + (\text{healthBonus} \times 0.012)$$
- Re-scales range from a tiny **0.808x** (worst-case Aggressive runt) to a giant **1.312x** (best-case Pacifist).
- Hitboxes remain vanilla-sized for pathfinding compatibility.

## 3. Persistent Data (Fabric Attachment API)
Stored in the immutable `WolfPersistentData` record via Fabric's Data Attachment API:
- `personalityId`: Trait ID.
- `parent1Uuid`, `parent2Uuid`: DNA markers for parent lineage tracking.
- `inbred`: Boolean flag denoting genetic stunting.
- `guardMode`, `guardPos`: Position sentinel markers.

## 4. Breeding & Inbreeding Prevention Logic
- **Genetic Stat Inheritance**: Offspring inherit attributes as the average of their parents' stats plus a minor mutation.
- **Inbreeding Check**: If `parent1Uuid` or `parent2Uuid` match between the parents (sibling or parent-child pairings), the child is flagged as `inbred`.
- **Runt Penalties**: Inbred puppies spawn at a stunted size with heavily penalized HP, damage, and speed.
- **Outcrossing & Curing**: Outcrossing an inbred runt with an unrelated healthy wolf restores baseline genes to their offspring. Feeding a runt a Golden Apple can cure it if enabled by `bd_enable_inbred_curing` GameRule.

## 5. Performance Optimization Logic
- **Query Throttling**: Heavy searches (96-block pack leader scans, 32-block follower scans) are throttled to run every 20-80 ticks via staggering timers, preventing server TPS degradation.
- **Static Spacing Cache**: Pack followers coordinate using a shared `FollowerSpacingCache` mapped to the owner's UUID, reducing scans to a single request per interval.
- **Transient Memory**: Ticking/volatile values are stored in JVM memory as `@Unique` fields to avoid allocation churn, serialized to/from NBT only when saving/loading.
