# Vanilla Outsider: Better Dogs - AI Reference Guide (v3.1.37+build.5)

---

# ⚙️ CONFIGURATION SYSTEM - "GAME RULES MASTERY"

> All settings in this mod are now exposed as **Native Minecraft Game Rules**. Access them via the "Edit Game Rules" screen in the world creation or world settings menu, under the **Better Dogs** category.

## Features

- **Native UI Integration**: No extra mods (like Cloth Config) required.
- **Per-World Persistence**: Settings are saved into the `level.dat`, allowing different rules for different worlds.
- **Descriptive Tooltips**: Each rule includes a detailed description in the UI.

---

# 🐺 WILD WOLVES

## 🍖 Hunting Behavior (Wild Only)

| Feature | Default | Game Rule |
|---------|---------|-----------|
| **Health threshold to hunt** | < 50% HP (0.5) | `bd_wild_hunt_threshold` |
| **Eat dropped food?** | Yes | `bd_wild_eat_drops` |

---

# 🐕 TAMED WOLVES (Personalities)

## 🐕 Personality Types

| Personality | Tame Chance | Particles | Game Rule |
|-------------|--------|-----------|-----------|
| **Normal** | 60% | Happy villager (✨) | `bd_tame_normal_percent` |
| **Aggressive** | 20% | Angry villager (💢) | `bd_tame_aggro_percent` |
| **Pacifist** | 20% | Hearts (❤️) | `bd_tame_paci_percent` |

## 🧬 Breeding Genetics

| Parent 1 | Parent 2 | Result | Game Rule |
|----------|----------|--------|-----------|
| Match | Match | Same Type (80%) | `bd_breed_same_chance` |
| Match | Match | Variant (10% each) | `bd_breed_same_other_chance` |
| Mixed | Mixed | Dominant (40%) | `bd_breed_mixed_dominant_chance` |
| Mixed | Mixed | Recessive (40%) | `bd_breed_mixed_recessive_chance` |
| Diluted | Diluted | Normal (50%) | `bd_breed_diluted_normal_chance` |

---

# 🏃 MOVEMENT & FOLLOWING

## Follow Behavior (Configurable)

| Setting | Aggressive | Pacifist | Normal |
|---------|------------|----------|--------|
| **Start Distance** | 50.0 | 5.0 | 10.0 |
| **Stop Distance** | 2.0 | 2.0 | 2.0 |
| **Teleport Threshold**| 90.0 (5x) | 9.0 (0.5x) | 18.0 (1x) |
| **Special Behavior** | **Scouting** (wanders ahead) | **Alarm Whine** (hostile alert) | Standard follow |

---

# 🤝 SOCIAL BONDING (v3.1.37)

The bonding system tracks "Affinity" between pack members (from -100 to 100).

- **How it's gained**: Howling together (+2), Play fighting (+1 per hit).
- **How it's lost**: Retaliating after being hit (-15).
- **Effects**:
  - High affinity (>50%) **suppresses** existing Blood Feuds.
  - Bonding reduces the chance that an adult correction triggers a new Blood Feud.

> **Note**: These are governed by the `bd_teleport_multiplier` rule.

---

# 🎭 SOCIAL INTERACTIONS (SCHEDULER)

## ⚡ Events

| Event | Behavior | Game Rule |
|-------|----------|-----------|
| **Zoomies** | Morning/Rain running | `bd_enable_zoomies` |
| **Group Howl** | Night vocalization | `bd_enable_howl` |
| **Play Fight** | Safe sparring | `bd_enable_play_fight` |
| **Retaliation** | Puppy bite back | `bd_baby_retaliation_percent` |
| **Correction** | Adult correction | `bd_enable_correction` |
| **Blood Feud** | Permanent vendetta | `bd_blood_feud_percent` |

---

# 🎁 GIFT SYSTEM

| Personality | Gifts | Game Rule |
|-------------|--------|-----------|
| **Aggressive** | Bones, Flesh, Arrows | `bd_enable_gifts` |
| **Pacifist** | Berries, Seeds, Flowers | `bd_enable_gifts` |

---

# 🛡️ SAFETY & HEALING

- **Cliff Safety**: Prevents jumping off cliffs (`bd_enable_cliff_safety`).
- **Creeper Flee**: Runs when hiss detected (`bd_enable_creeper_flee`).
- **Passive Healing**: Slow HP recovery (`bd_enable_passive_healing`).

---
*Last Updated: March 2026 | v3.1.37+build.5*
