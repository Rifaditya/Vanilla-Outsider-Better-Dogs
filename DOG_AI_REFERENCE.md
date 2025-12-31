# Vanilla Outsider: Better Dogs - AI Reference Guide (Minecraft 1.21.11 Java Edition, Fabric, Kotlin)

---

# ⚙️ CONFIGURATION SYSTEM

> All settings in this document are configurable via **config file** and **Mod Menu** GUI

## Config File Location

```
config/vanilla-outsider-better-dogs.json
```

## Mod Menu Integration

- Uses **Cloth Config API** for in-game settings GUI
- Access via: **Mod Menu → Mods → Vanilla Outsider: Better Dogs → Config**
- Changes apply immediately (some require world reload)

## Config Categories

| Category | Description |
|----------|-------------|
| `personality` | Spawn chances, breeding genetics |
| `aggressive` | Detection range, patrol radius, speed/damage modifiers |
| `pacifist` | Follow distance, speed/damage modifiers |
| `normal` | Vanilla overrides (if any) |
| `combat` | Friendly fire, knockback resistance |
| `healing` | Passive heal rate, combat delay |
| `safety` | Creeper flee, hazard avoidance |
| `wild` | Hunting behavior, food eating |

---

# 🐺 WILD WOLVES

## 🍖 Hunting Behavior (Wild Only)

| Feature | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Hunt only when hurt?** | Yes | Yes |
| **Health threshold to hunt** | Below 50% HP | 50% |
| **Eat dropped food?** | Yes | Yes |
| **Food heals instantly?** | Yes | Yes |

### Prey Animals

| Animal | Hunts? | ✏️ Your Setting |
|--------|--------|-----------------|
| Sheep | ✅ | ✅ |
| Rabbit | ✅ | ✅ |
| Chicken | ✅ | ✅ |
| Fox | ❌ (territorial, not food) | ❌ |
| Baby Turtles | ❌ | ❌ |

### Food Wolves Can Eat

| Item | Heals | ✏️ Your Setting |
|------|-------|-----------------|
| Raw Mutton | 2 HP | 2 HP |
| Raw Rabbit | 2 HP | 2 HP |
| Raw Chicken | 2 HP | 2 HP |
| Raw Beef | 2 HP | 2 HP |
| Raw Porkchop | 2 HP | 2 HP |
| Rotten Flesh | 1 HP | 1 HP |

---

# 🐕 TAMED WOLVES (Personalities)

## 🐕 Personality Types

| Personality | Chance | Particles | ✏️ Edit |
|-------------|--------|-----------|---------|
| **Normal** | 60% | Happy villager (✨) | 60% |
| **Aggressive** | 20% | Angry villager (💢) | 20% |
| **Pacifist** | 20% | Hearts (❤️) | 20% |

---

## 🧬 Breeding Genetics

| Parent 1 | Parent 2 | Offspring Chances |
|----------|----------|-------------------|
| Same | Same | **80%** same, 10% each other |
| Normal | Aggressive | 40% Normal, 40% Aggressive, 20% Pacifist |
| Normal | Pacifist | 40% Normal, 40% Pacifist, 20% Aggressive |
| Aggressive | Pacifist | **50% Normal**, 25% Aggressive, 25% Pacifist |

> Baby wolves inherit personality at birth from parents (no taming particles shown)

---

## 🚶 Roaming & Following Behavior (NEW - TO IMPLEMENT)

### Follow Distance (by Personality)

| Personality | Max Distance from Owner | Teleport Distance | ✏️ Your Setting |
|-------------|------------------------|-------------------|-----------------|
| **Normal** | 10 blocks (vanilla) | >12 blocks (vanilla) | Keep Vanilla |
| **Aggressive** | 20 blocks (wider patrol) | >24 blocks | 20 blocks / 24 teleport |
| **Pacifist** | 8 blocks (stays close) | >10 blocks | 8 blocks / 10 teleport |

### Patrol Behavior (Aggressive Only)

| Setting | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Patrol mode?** | Non-sitting wolves roam | Yes |
| **Patrol radius** | 20 blocks around owner | 20 blocks |
| **Return to owner when?** | Target dies or >24 blocks | 24 blocks |

### Close Guardian Behavior (Pacifist Only)

| Setting | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Stay close?** | Yes, within 8 blocks | Yes |
| **Follow closer in combat?** | Yes, 5 blocks | 5 blocks |
| **Quicker teleport trigger?** | Yes, at 10 blocks | 10 blocks |

---

## 🔴 Aggressive AI Settings

### Stat Modifiers (Aggressive Only)

| Stat | Modifier | ✏️ Your Setting |
|------|----------|-----------------|
| **Movement Speed** | +15% faster | +15% |
| **Attack Damage** | -15% lower | -15% |

> Aggressive wolves are fast scouts but deal slightly less damage per hit

| Setting | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Detection range** | 20 blocks from owner | 20 blocks |
| **Attack creepers?** | No (too risky) | No |
| **Attack phantoms?** | Yes | Yes |
| **Max chase distance** | 20 blocks from owner | 20 blocks |
| **Abandon target if too far?** | Yes | Yes |
| **Attack what owner attacks?** | Yes (vanilla behavior) | Yes |

### Target Mobs (✅ = attack, ❌ = ignore)

| Mob | Default | ✏️ Your Setting |
|-----|---------|-----------------|
| Zombie | ✅ | ✅ |
| Skeleton | ✅ | ✅ |
| Spider | ✅ | ✅ |
| Creeper | ❌ | ❌ |
| Enderman | ✅ | ✅ |
| Witch | ✅ | ✅ |
| Slime | ✅ | ✅ |
| Phantom | ✅ | ✅ |
| Drowned | ✅ | ✅ |
| Husk | ✅ | ✅ |
| Stray | ✅ | ✅ |
| Pillager | ✅ | ✅ |
| Vindicator | ✅ | ✅ |
| Evoker | ✅ | ✅ |
| Ravager | ✅ | ✅ |
| Vex | ✅ | ✅ |
| Hoglin | ✅ | ✅ |
| Piglin (hostile) | ✅ | ✅ |
| Warden | ❌ | ❌ |
| Wither | ❌ | ❌ |
| Ender Dragon | ❌ | ❌ |

---

## 🟢 Pacifist AI Settings

### Stat Modifiers (Pacifist Only)

| Stat | Modifier | ✏️ Your Setting |
|------|----------|-----------------|
| **Movement Speed** | -10% slower (still faster than skeletons) | ~0.27 speed |
| **Attack Damage** | +15% higher | +15% |
| **Knockback** | +50% stronger | +50% |

> Pacifist wolves are "Guardian" protectors - slower but still able to catch skeletons, hit hard and knock enemies away

| Setting | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Trigger** | Owner takes mob damage | Owner takes mob damage |
| **Attack what owner attacks?** | ❌ **NO** | No (defensive only) |
| **React to fall damage?** | No | No |
| **React to fire damage?** | No | No |
| **React to drowning?** | No | No |
| **Attack the specific attacker?** | Yes | Yes |
| **Attack all hostiles after trigger?** | No | No |
| **Stay aggressive duration** | Until target dead | Until target dead |

---

## 🟡 Normal AI Settings

| Setting | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Behavior** | 100% Vanilla | Keep Vanilla |
| **Auto-attack skeletons?** | Yes (vanilla) | Keep Vanilla |
| **Revenge on self-damage?** | Yes (vanilla) | Keep Vanilla |
| **Attack what owner attacks?** | Yes (vanilla) | Yes |

> **Note:** ALL personalities auto-target skeletons (vanilla wolf behavior, not modified)

---

## 🎭 Personality-Based Idle Behaviors

> Makes wolves feel alive and distinct even when not in combat

### Aggressive - "The Vigilant Sentinel"

| Behavior | Description | ✏️ Setting |
|----------|-------------|------------|
| **Standing Look Direction** | When NOT sitting, looks outward scanning | Yes |
| **Mob Detection Growl** | Low growl + smoke when mob behind wall | Yes |
| **Head Movement** | Constantly scanning back and forth | Yes |

### Pacifist - "The Social Butterfly"

| Behavior | Description | ✏️ Setting |
|----------|-------------|------------|
| **Friendly to Passives** | Walks up to cows/pigs and "boops" them | Yes |
| **Curious Head Tilt** | Tilts head for non-meat items (flowers) | Yes |
| **Gentle Whine** | Soft whine when owner is low health | Yes |

### Normal - "The Classic Companion"

| Behavior | Description | ✏️ Setting |
|----------|-------------|------------|
| **Classic Head Tilt** | Vanilla head tilt | Keep Vanilla |
| **Stare at Meat** | Looks at player holding meat | Keep Vanilla |

---

## 🎁 Gift System (NEW)

> Like cats bringing gifts, wolves bring you items based on personality!

### Aggressive - "The Hunter's Trophy"

| Setting | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Trigger** | While patrolling, finds mob | Yes |
| **Behavior** | "Kills" off-screen mob, brings loot | Yes |
| **Cooldown** | Every 10-15 minutes | 10-15 min |

| Gift | Chance | Source |
|------|--------|--------|
| Bone | 40% | From skeletons |
| Rotten Flesh | 35% | From zombies |
| Arrow | 15% | From skeletons |
| Iron Nugget | 10% | Rare drop |

### Pacifist - "The Forager"

| Setting | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Trigger** | While owner is mining/chopping | Yes |
| **Behavior** | Sniffs around nearby grass, drops items | Yes |
| **Cooldown** | Every 5-10 minutes | 5-10 min |

| Gift | Chance | Notes |
|------|--------|-------|
| Sweet Berries | 30% | Common |
| Seeds (any type) | 25% | Common |
| Flowers (random) | 20% | Pretty! |
| Mushroom | 15% | Forest biome |
| Glow Berries | 10% | Rare, valuable |

### Normal - No Gift Behavior

> Normal wolves keep vanilla behavior (no gifts)

---

## 🌩️ Environmental Reactions (NEW)

> Wolves react to the world around them based on personality

### Thunderstorms

| Personality | Reaction | ✏️ Setting |
|-------------|----------|------------|
| **Aggressive** | Restless - barks at sky, paces around | Yes |
| **Pacifist** | Scared - stays very close to player (within 2 blocks) | Yes |
| **Normal** | Vanilla (no special reaction) | Keep Vanilla |

### Village Life

| Personality | Reaction | ✏️ Setting |
|-------------|----------|------------|
| **Aggressive** | Intimidating - villagers flee slightly faster | Yes |
| **Pacifist** | Village Pet - happy particles near children | Yes |
| **Normal** | Vanilla (neutral to villagers) | Keep Vanilla |

### Other Environments

| Environment | Aggressive | Pacifist | Normal |
|-------------|------------|----------|--------|
| **Near campfire** | Normal behavior | Walks near campfire, follows when player leaves | Vanilla |
| **In snow biome** | Normal behavior | Normal behavior | Vanilla |
| **Near beehive** | Ignores | Curious approach (no aggro) | Vanilla |

---

## 🛡️ Pathfinding & Safety (All Personalities)

| Feature | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Avoid lava?** | Yes | Yes |
| **Avoid fire?** | Yes | Yes |
| **Avoid high falls?** | Yes (>3 blocks) | Yes |
| **Flee from creepers?** | Yes (when about to explode) | Yes |
| **Smarter pathfinding?** | Yes (avoid getting stuck) | Yes |

### Creeper Flee Behavior

| Setting | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Flee trigger** | Creeper starts hissing | Creeper hissing |
| **Flee distance** | 6 blocks away | 6 blocks |
| **Resume after explosion?** | Yes | Yes |

### Fall Avoidance

| Setting | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Max safe fall** | 3 blocks | 3 blocks |
| **Avoid during chase?** | Yes | Yes |

---

## ⚔️ Combat Improvements (All Personalities)

| Feature | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Increased knockback resistance?** | Yes | Yes |
| **Knockback resistance amount** | 0.5 (50% reduction) | 0.5 |
| **Prevent owner from damaging?** | Yes | Yes |
| **Allow damage with sneak+attack?** | Yes (emergency kill) | Yes |

---

## ❤️ Healing System (All Personalities)

| Feature | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Passive healing?** | Yes (very slow) | Yes |
| **Passive heal rate** | 1 HP every 60 seconds | 1 HP / 60 sec |
| **Only when not in combat?** | Yes | Yes |
| **Feed healing** | Instant (vanilla) | Keep Vanilla |

---

## 🔧 Technical Settings

| Setting | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Personality stored in** | EntityDataAccessor (like Panda) | EntityDataAccessor |
| **Personality changeable?** | No (permanent) | No |
| **Puppies inherit?** | ✅ Yes (genetics-based) | Yes |

---

## 💡 Notes

### Tamed Wolves

- Personality is assigned **randomly on tame** OR **inherited from parents**
- Wild wolves have **no personality** until tamed
- Bred puppies get personality at birth based on parent genetics

### Wild Wolves (Also Affected!)

- ✅ Better pathfinding (avoid lava, fire, falls)
- ✅ Only hunt when **low on health** (not random aggression)
- ✅ Eat dropped food to heal
- ❌ Flee from creepers don't need they don't attack creepers

---

*Last Updated: December 2025 | Minecraft 1.21.11 Java Edition | Fabric + Kotlin*
