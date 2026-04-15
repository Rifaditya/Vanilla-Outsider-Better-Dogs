# Vanilla Outsider: Better Dogs - Personality System Design

## 🎯 Core Concept

Tamed wolves get a **random permanent personality** that affects combat behavior.  
**Players cannot change it** — this makes each dog unique and worth seeking out!

---

## 🐕 Dog Personalities (3 Types)

| Personality | Chance | Behavior |
|-------------|--------|----------|
| 🔴 **Aggressive** | 33% | Auto-attacks hostile mobs; **Scouts ahead** |
| 🟢 **Pacifist** | 33% | Only fights when you're hurt; **Silent Alarm** |
| 🟡 **Normal** | 34% | Attacks when player attacks (vanilla) |

---

### 🔴 Aggressive
>
> *"Guardian - attacks threats before they attack you"*

- **Trigger:** Hostile mob within ~16 blocks of player
- **Targets:** Zombies, Skeletons, Spiders, Creepers, Endermen, etc.
- **Scouting Agent:** Occasionally wanders 3-5 blocks ahead or to the side of the owner to "scout" the area.
- **Best for:** Cave exploration, night travel, base defense

---

### 🟢 Pacifist
>
> *"Loyal defender - only fights when you're hurt"*

- **Trigger:** Player takes damage from a mob
- **Targets:** Only the mob that hurt the player
- **Silent Alarm:** Emits a "Whine" sound if a Monster is within 12 blocks but not yet in combat.
- **Best for:** Building, farming, keeping dog safe

---

### 🟡 Normal (Vanilla-like)
>
> *"Fighting partner - attacks what you attack"*

- **Trigger:** Player attacks a mob
- **Targets:** The mob player attacked
- **Best for:** Full control during combat

---

## 🎲 Personality Assignment

```
On Tame:
  Roll random 1-100
  1-33   → Aggressive
  34-66  → Pacifist
  67-100 → Normal
  
  Personality is PERMANENT (cannot be changed)
```

---

## 👁️ Visual Feedback (One-time on Tame)

| Personality | Particles |
|-------------|-----------|
| 🔴 Aggressive | 💢 Angry particles |
| 🟢 Pacifist | ❤️ Heart particles |
| 🟡 Normal | ✨ Happy villager particles |

**No chat message** — players learn personality through observation.

---

## 🛡️ Pathfinding & Safety (All Personalities)

| Feature | Behavior |
|---------|----------|
| **Avoid lava** | Don't walk into lava blocks |
| **Avoid fire** | Don't walk into fire |
| **Avoid high falls** | Don't jump off >3 blocks |
| **Flee from creepers** | Run away when creeper hisses |
| **Smarter pathfinding** | Avoid getting stuck on obstacles |

---

## ⚔️ Combat Improvements (All Personalities)

| Feature | Behavior |
|---------|----------|
| **Knockback resistance** | 50% reduction |
| **Friendly fire protection** | Owner can't damage own wolves |
| **Emergency kill** | Sneak+attack overrides protection |

---

## 🔧 Technical Implementation

### NBT Data

```
Wolf Entity NBT:
- "Personality": byte (0 = Normal, 1 = Aggressive, 2 = Pacifist)
```

### AI Goals by Personality

**Aggressive:** Add goal to target hostile mobs near owner (16 block range)

**Pacifist:** Remove proactive attack goal, only defend when owner takes damage

**Normal:** Keep vanilla behavior unchanged

---

## 📋 Implementation Checklist

### Phase 1: Core

- [ ] Add `personality` field to Wolf entity (Mixin)
- [ ] Randomize personality on tame event
- [ ] Save/load personality in NBT
- [ ] Display particles on tame

### Phase 2: Personality AI

- [ ] Aggressive: Add hostile mob scanning goal
- [ ] Pacifist: Only react to owner damage
- [ ] Normal: No changes (vanilla)

### Phase 3: Safety AI

- [ ] Avoid lava/fire blocks in pathfinding
- [ ] Avoid high falls (>3 blocks)
- [ ] Flee from hissing creepers

### Phase 4: Combat Improvements

- [ ] Add knockback resistance attribute
- [ ] Block owner damage (except sneak+attack)

---

## 🎨 Mod Info

| Property | Value |
|----------|-------|
| **Mod Name** | Vanilla Outsider: Better Dogs |
| **Mod ID** | `vanilla-outsider-better-dogs` |
| **MC Version** | 1.21.11 |
| **Loader** | Fabric |
| **Language** | Kotlin |

---

*Design document - December 29, 2025*
