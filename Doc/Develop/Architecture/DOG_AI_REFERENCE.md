# Vanilla Outsider: Better Dogs - AI Reference Guide (v4.5.13)

---

# ⚙️ CONFIGURATION SYSTEM - "GAME RULES MASTERY"

> All settings in this mod are exposed as **Native Minecraft Game Rules** and fully integrated with **Cloth Config / ModMenu** for optional GUI client-side configuration.

## Features
- **Native UI Integration**: Per-world persistence saved into `level.dat`.
- **Cloth Config API Support**: Optional settings panel with description tooltips in singleplayer/client screens, decoupled safely to prevent dedicated server classloading crashes.

---

# 🐺 WILD WOLVES & PACK DYNAMICS

## 🍖 Hunting & Spawning (Wild Only)

| Feature | Default | Game Rule |
|---------|---------|-----------|
| **Health threshold to hunt** | < 50% HP (0.5) | `bd_wild_hunt_threshold` |
| **Eat dropped food?** | Yes | `bd_wild_eat_drops` |
| **Spawn cluster limit** | 8 wolves max | `bd_wolf_pack_cluster_size` |
| **Density reinforcement chance** | 0% | `bd_wolf_spawn_density_boost` |

---

# 🐕 TAMED WOLVES (Personalities & Scaling)

## 🐕 Personality Types

| Personality | Spawn Chance | Particles (Guard Mode) | Game Rule |
|-------------|--------|-----------|-----------|
| **Normal** | 60% | Gold/Yellow Dust (✨) | `bd_spawn_normal_percent` |
| **Aggressive** | 20% | Red Dust (💢) | `bd_spawn_aggro_percent` |
| **Pacifist** | 20% | Green/Teal Dust (❤️) | `bd_spawn_paci_percent` |

*Note: Personality traits and stat ranges are rolled deterministically at spawn using the wolf's unique UUID.*

## 🧬 Breeding Genetics & Inbreeding

| Parent 1 | Parent 2 | Result / Penalty | Game Rule / Toggle |
|----------|----------|--------|-----------|
| Match | Match | Same Type (80%) | `bd_breed_same_chance` |
| Match | Match | Variant (10% each) | `bd_breed_same_other_chance` |
| Mixed | Mixed | Dominant (40%) | `bd_breed_mixed_dominant_chance` |
| Related (Inbred) | Related (Inbred) | **Runt (Stunted Size, fragile HP, slow)** | (Automatic Kinship check) |
| Inbred Runt | Unrelated Healthy | **Healthy recovery outcrossing** | (Automatic outcrossing) |
| Inbred Runt | Golden Apple | **Cured (Restored baseline attributes)** | `bd_enable_inbred_curing` (Default: False) |

## 📐 Dynamic Health-Based Size Scaling
Wolves apply physical size scales based on rolled max health bonuses:
$$\text{scale} = 1.0 + (\text{healthBonus} \times 0.012)$$
- Worst-case Aggressive runt scale: **0.808x**
- Best-case Pacifist giant scale: **1.312x**

---

# 🛡️ GUARD MODE (Domestic Sentinels)

Anchored to current BlockPos via Shift+Right-Click with a bone (consuming exactly 1 bone).

| Personality | Patrol Shape | Range / Chase Limit | Special / Watchdog Actions |
|-------------|--------------|-------------------|----------------------------|
| **Aggressive** | Perimeter circular sweep | 80% range / 32 block chase | Auto-attacks; Ash/Red particles |
| **Normal** | Radial star sweeps / sentry | Post (0) / 20 block chase | Auto-attacks; White ash/Gold particles |
| **Pacifist** | Orbital protective circle | orbital (r=2) / none | Whines when monsters within 16 blocks; Regenerates/Resists allies |

### Guard Target Filtering & Optimization
- **Line-of-Sight Restriction**: Normal/Aggressive guards strictly require line-of-sight to target hostiles, preventing them from trying to fight cave monsters through solid ground.
- **Pacifist Hybrid Scan**: Detects monsters up to 16 blocks with line-of-sight. If vertical distance (`dy <= 4.0`) is within hearing range, warns through solid walls, optimizing performance by bypassing raycasts for over 90% of blocked mobs.

---

# 🏃 MOVEMENT & FOLLOWING

## Follow Behavior (Configurable)

| Setting | Aggressive | Pacifist | Normal |
|---------|------------|----------|--------|
| **Start Distance** | 50.0 | 5.0 | 10.0 |
| **Stop Distance** | 2.0 | 2.0 | 2.0 |
| **Special Behavior** | **Scouting** (10% chance to wander 5 blocks ahead) | **Alarm Whine** (hostile alert) | Standard follow |

### Spacing & Flocking Spacing
Wolves space themselves dynamically as pack size ($N$) increases using:
$$f(N) = \text{multiplier} \times \sqrt{N - 1}$$
- **Tamed Spacing Rules**: `bd_tamed_pack_spread_multiplier` (Default: 1.2x), `bd_tamed_pack_spread_max` (Default: 6.0 blocks extra).
- **Wild Spacing Rules**: `bd_wild_pack_spread_multiplier` (Default: 0.8x), `bd_wild_pack_spread_max` (Default: 4.0 blocks extra).
- **Performance Optimization**: Active follower counts are shared using `FollowerSpacingCache` and query checks are throttled to once every 20-40 ticks.

---

# 🤝 SOCIAL BONDING & IMMERSIVE EVENTS

- **Affinity**: Bonding scores (-100 to 100) are built via play fighting and howling, suppressing Blood Feuds.
- **Calm Down**: Shift+Right-Click with an empty hand makes the wolf sit and clears anger/grudges.
- **Paper Adoption**: Shift+Right-Click with Paper triggers adoption pending (Rose Pink `trail` particles); another player can right-click to take ownership.
- **Low Health Fleeing**: Flee behavior under 30% health. (Pacifist 100% chance, Normal 50%, Aggressive 10%).

---
*Last Updated: May 2026 | v4.5.13*
