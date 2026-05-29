# Vanilla Outsider: Better Dogs - Personality System Design

## 🎯 Core Concept

Tamed and wild wolves roll a **random permanent personality** that determines their attribute ranges and modifies their AI goals. Players learn the personality through observation and guard mode particle hints.

---

## 🐕 Dog Personalities (3 Types)

| Personality | Base Spawn Weight | Primary Guard Patrol Style | Attribute Focus |
|-------------|--------|----------------------------|-----------------|
| 🔴 **Aggressive** | 20% | Perimeter sweeps (80% range) | +Attack Damage, -Max Health |
| 🟢 **Pacifist** | 20% | Orbital circles around post | +Max Health, -Attack Damage, Buffs Allies |
| 🟡 **Normal** | 60% | Post sentry or radial star pacing | Balanced base stats |

---

### 🔴 Aggressive
> *"Fierce guardian - proactive combat and base sweeping"*

- **Targeting**: Auto-attacks hostile mobs within 16-24 blocks of owner or post. Strictly requires line-of-sight to prevent targeting cave monsters.
- **Scouting**: 10% chance per path recalc to sprint 5 blocks ahead of the owner to scout.
- **Guard Mode**: Performs outer sweeps along patrol ranges, pausing to scan outward.

---

### 🟢 Pacifist
> *"Gentle watcher - alerts hostiles and empowers allies"*

- **Watchdog Alarm**: Whines and emits warning `NOTE` particles when monsters are within 16 blocks (with line-of-sight, or within 4 blocks vertically without line-of-sight).
- **Grace Buffs**: Ticking regenerates and resists nearby players and allied tamed wolves within 6 blocks.
- **Guard Mode**: Orbit circles, standing near the post block if alarm ticks are triggered.

---

### 🟡 Normal
> *"Balanced ally - standard vanilla-plus behavior"*

- **Combat**: Classic behavior (attacks targets attacked by the owner or targets that hit the owner).
- **Guard Mode**: Post sentinel posture (sits at post if patrol range is 0) or alternate radial star pacing sweeps.

---

## 🎲 Personality & Attribute Assignment

Personalities and their corresponding stats roll once during the wild spawn cycle, preventing attribute shifts when tamed.
- **Symmetric Triangular Distribution**: rolled max health, attack damage, and speed are rolled within personality-defined min/max ranges.
- **UUID Seeding**: seeded by the entity's UUID, guaranteeing persistent, reload-safe values.

---

## 📐 Dynamic Health-Based Scale
Tamed wolf scale is dynamically adjusted by their rolled max health bonus:
$$\text{scale} = 1.0 + (\text{healthBonus} \times 0.012)$$
- Worst-case Aggressive runt scale: **0.808x**
- Best-case Pacifist giant scale: **1.312x**

---

## 🧬 Breeding Genetics & Inbreeding Penalty
- **Inheritance**: Puppies inherit base stats as the average of parent stats with a minor triangular mutation.
- **Inbreeding Prevention**: Breeding related wolves (parent-child or sibling pairings tracked by parent UUIDs) applies an `inbred` flag, stunting scale to tiny runt levels and heavily penalizing stats.
- **Recovery & Curing**: Outcrossing an inbred runt with an unrelated healthy wolf restores baseline genes to their offspring. Feeding a runt a Golden Apple can optionally cure the inbred status if `bd_enable_inbred_curing` is enabled.

---

## 🔧 Technical Specification

- **Mod ID**: `vanilla-outsider-better-dogs`
- **Environment**: Minecraft 26.2+ (Java 25)
- **Persistence**: Fabric Data Attachment API (`WolfPersistentData` record)
- **GUI Config**: ModMenu & Cloth Config API
