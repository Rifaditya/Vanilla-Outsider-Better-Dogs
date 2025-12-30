# Vanilla Outsider: Better Dogs - AI Reference Guide (Minecraft 1.21.11 Java Edition, Fabric, Kotlin)

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

| Personality | Chance | ✏️ Edit |
|-------------|--------|---------|
| **Aggressive** | 33% | 33% |
| **Pacifist** | 33% | 33% |
| **Normal** | 34% | 34% |

---

## 🔴 Aggressive AI Settings

| Setting | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Detection range** | 16 blocks from owner | 16 blocks |
| **Attack creepers?** | No (too risky) | No |
| **Attack phantoms?** | Yes | Yes |
| **Max chase distance** | 20 blocks from owner | 20 blocks |
| **Abandon target if too far?** | Yes | Yes |

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

| Setting | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Trigger** | Owner takes mob damage | Owner takes mob damage |
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

---

## 👁️ Visual Feedback (On Tame Only)

| Personality | Particles | ✏️ Your Setting |
|-------------|-----------|-----------------|
| Aggressive | Angry villager (💢) | Angry villager |
| Pacifist | Hearts (❤️) | Hearts |
| Normal | Happy villager (✨) | Happy villager |

### Chat Message Format

| Setting | Default | ✏️ Your Setting |
|---------|---------|-----------------|
| **Show message?** | Yes | No, to make the player learn their tamed dog's personality |
| **Format** | "[Wolf] has a {personality} personality!" | Keep Default |

---

## �️ Pathfinding & Safety (All Personalities)

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
| **Personality stored in** | Wolf NBT data | NBT |
| **Personality changeable?** | No (permanent) | No |
| **Puppies inherit?** | No (random on tame) | No |

---

## 💡 Notes

### Tamed Wolves

- Personality is assigned **randomly on tame**, not at spawn
- Wild wolves have **no personality** until tamed

### Wild Wolves (Also Affected!)

- ✅ Better pathfinding (avoid lava, fire, falls)
- ✅ Only hunt when **low on health** (not random aggression)
- ✅ Eat dropped food to heal
- ❌ Flee from creepers don't need they don't attack creepers

---

*Last Updated: December 2025 | Minecraft 1.21.11 Java Edition | Fabric + Kotlin*
