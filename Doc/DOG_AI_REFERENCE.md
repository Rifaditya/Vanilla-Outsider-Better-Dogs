# Vanilla Outsider: Better Dogs - AI Reference Guide (v3.1.13)

---

# ⚙️ CONFIGURATION SYSTEM - "CONFIG MASTERY"

> All settings in this document are configurable via the **betterdogs.json** config file.

## Config File Location

```
config/betterdogs.json
```

## Features

- **Ultraguard Sync**: Configs use atomic writes and automatic secondary backups (`.json.bak`).
- **Config Mastery**: Over 30 AI magic numbers exposed for tuning.
- **Additive Sync**: New settings merge automatically without resetting user preferences.

---

# 🐺 WILD WOLVES

## 🍖 Hunting Behavior (Wild Only)

| Feature | Default |
|---------|---------|
| **Health threshold to hunt** | < 50% HP (0.5) |
| **Eat dropped food?** | Yes |

---

# 🐕 TAMED WOLVES (Personalities)

## 🐕 Personality Types

| Personality | Tame Chance | Particles |
|-------------|--------|-----------|
| **Normal** | 60% | Happy villager (✨) |
| **Aggressive** | 20% | Angry villager (💢) |
| **Pacifist** | 20% | Hearts (❤️) |

## 🧬 Breeding Genetics

| Parent 1 | Parent 2 | Offspring Chances |
|----------|----------|-------------------|
| Same | Same | **80%** same, 10% each other |
| Normal | Aggressive | 40% Normal, 40% Aggressive, 20% Pacifist |
| Normal | Pacifist | 40% Normal, 40% Pacifist, 20% Aggressive |
| Aggressive | Pacifist | **50% Normal**, 25% Aggressive, 25% Pacifist |

---

# 🏃‍♀️ MOVEMENT & FOLLOWING

## Follow Behavior (Configurable)

| Setting | Normal | Aggressive | Pacifist |
|---------|--------|------------|----------|
| **Follow Start (Dist)** | 10.0 | 50.0 | 5.0 |
| **Follow Stop (Dist)** | 2.0 | 2.0 | 2.0 |
| **Teleport Multiplier**| 1.0x (18b) | 5.0x (90b) | 0.5x (9b) |

> **Note**: Base catch-up speed is **1.5x**.

---

# 🔴 AGGRESSIVE PERSONALITY

| Stat | Modifier | Def. Value |
|------|----------|------------|
| **Health Bonus** | -10.0 HP | 10 HP Total |
| **Speed Modifier** | +0.15 | Fast |
| **Damage Modifier** | +0.50 | +50% Dmg |
| **Detection Range** | 20.0 blocks | |
| **Max Chase Dist** | 50.0 blocks | |

---

# 🟢 PACIFIST PERSONALITY

| Stat | Modifier | Def. Value |
|------|----------|------------|
| **Health Bonus** | +20.0 HP | 40 HP Total |
| **Speed Modifier** | -0.10 | Slower |
| **Damage Modifier** | -0.30 | -30% Dmg |
| **Knockback Mod** | +0.50 | Stronger |

---

# 🎭 SOCIAL INTERACTIONS (SCHEDULER)

## ⚡ Events

| Event | Trigger | Behavior |
|-------|---------|----------|
| **Zoomies** | Morning/Post-Rain | Hyper running (1.4x speed) |
| **Group Howl** | Night/Full Moon | Pack vocalization |
| **Play Fight** | Large Packs (>10) | Safe sparring (Capped 1 HP) |
| **Retaliation** | Owner hits Baby | **ONE BITE** (Bite back chance: 75%) |
| **Correction** | Baby bites Owner | One adult bites baby ONCE |

---

# 🎁 GIFT SYSTEM

| Personality | Gifts | Trigger Chance |
|-------------|--------|----------------|
| **Aggressive** | Bone (40%), Flesh (35%), Arrow (15%) | 1% while alive |
| **Pacifist** | Berries (30%), Seeds (25%), Flowers (20%) | 1% while alive |

---

# 🌩️ ENVIRONMENTAL REACTIONS

- **Storm Anxiety**: Wolves pace nervously during thunder. Whine chance: 2%.

---

# 🛡️ SAFETY & HEALING

- **Cliff Safety**: Stops chase if >3 block drop detected with no ground within 4 blocks below.
- **Creeper Flee**: Runs when hiss detected.
- **Passive Healing**: 1.0 HP every 60s (1200 ticks) when out of combat for 3s.

---
*Last Updated: January 2026 | v3.1.13*
