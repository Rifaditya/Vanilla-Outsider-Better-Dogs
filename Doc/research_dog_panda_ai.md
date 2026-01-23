# Dog & Panda AI Research - Minecraft 1.21.11

## Overview
Research compiled for **Vanilla Outsider: Better Dogs** mod development, covering vanilla AI behaviors, known issues, and improvement opportunities.

---

## 🐺 Wolf/Dog AI (Vanilla 1.21.11)

### Current Behavior

#### Untamed Wolves
| Behavior | Description |
|----------|-------------|
| **Hostility** | Neutral to players; hostile to sheep, rabbits, foxes, baby turtles, skeletons |
| **Avoidance** | Avoid llamas (unless attacked by one) |
| **Pack Spawning** | Spawn in packs of 2-8 depending on biome |

#### Tamed Wolves
| Mode | Behavior |
|------|----------|
| **Follow Mode** | Follow owner, teleport if >12 blocks away, attack owner's targets |
| **Sitting Mode** | Stay in place, do not move or attack |

#### Wolf Variants (1.21)
| Variant | Biome | Pack Size |
|---------|-------|-----------|
| Pale Wolf | Taiga | 4 |
| Woods Wolf | Forest | 4 |
| Ashen Wolf | Snowy Taiga | 4 |
| Black Wolf | Old Growth Pine Taiga | 2-4 |
| Chestnut Wolf | Old Growth Spruce Taiga | 2-4 |
| Rusty Wolf | Sparse Jungle | 2-4 |
| Spotted Wolf | Savanna Plateau | 4-8 |
| Striped Wolf | Wooded Badlands | 4-8 |
| Snowy Wolf | Grove | 1+ |

#### Wolf Armor (1.21+)
- Crafted from **Armadillo Scutes**
- Absorbs all damage until armor breaks
- Repairable with scutes while wolf is sitting

---

### ⚠️ Known Problems

| Issue | Description |
|-------|-------------|
| **Poor Pathfinding** | Walk into lava, off cliffs, drown in water |
| **No Hazard Avoidance** | Don't avoid cacti, creepers, or fire |
| **Ineffective Combat** | Attack from too far, jump unnecessarily, miss targets |
| **Wrong Prioritization** | Attack skeletons over immediate player threats |
| **Friendly Fire** | Players can accidentally damage their own wolves |
| **Limited Utility** | Mostly aesthetic, lack depth as companions |
| **Healing Issues** | Cumbersome to heal multiple wolves |

---

### 💡 Community-Suggested Improvements

#### Pathfinding & Safety
- [ ] Recognize and avoid lava, fire, high falls
- [ ] Flee from exploding creepers (like Hoglins avoid warped fungi)
- [ ] Smarter pathfinding to avoid getting stuck

#### Combat Enhancements
- [ ] More efficient targeting and faster response times
- [ ] Increased knockback resistance
- [ ] Prioritize threats to player over ambient hostiles
- [ ] Prevent player from damaging own tamed wolves

#### Survival Instincts
- [ ] Run away or "play dead" when critically injured
- [ ] Life-stealing on kills (heal from damage dealt)

#### Commands & Control
- [ ] Dog whistle item for commanding attacks/retreats
- [ ] Ability to set specific mob targets to attack or ignore
- [ ] Multiple behavior modes (guard, patrol, hunt)

#### Companion Features
- [ ] Danger alert: growl at nearby enemies
- [ ] Enemy memory: more aggressive to repeat attackers
- [ ] Saturation system: eat food to heal over time
- [ ] Stamina for chasing targets
- [ ] Retrieve loot from killed mobs

#### Pack Behavior (Wild Wolves)
- [ ] Pack leader system for wild wolves
- [ ] Only hunt when hungry
- [ ] Wolves howl at night

---

### 🔧 Existing Mods for Reference

| Mod | Features |
|-----|----------|
| **RevampedWolf** | Pack leaders, howling, eat found meat, wolf armor, better AI |
| **Cozy's Improved Wolves** | Revival mechanic, affection/bonding, unique stats, classes (Hunter/Gatherer), gender, gift/dig abilities |
| **Wolves Of Other Furs** | New variants, dog beds, dog bowls |
| **Sophisticated Wolves** | Avoid fire/lava, growl at creepers, responsive AI |

---

## 🐼 Panda AI (Vanilla 1.21.11)

### Current Behavior

| Behavior | Description |
|----------|-------------|
| **Movement** | Wander aimlessly, avoid dangerous falls, water, lava |
| **Following** | Follow players holding bamboo (stop if >16 blocks away) |
| **Eating** | Adults seek and eat bamboo and cake |
| **Weather** | Whimper during thunderstorms |
| **Babies** | Sneeze occasionally (drops slimeball), causes nearby pandas to jump |

---

### Personality Types (7 Total)

| Personality | Appearance | Unique Behavior |
|-------------|------------|-----------------|
| **Normal** | Frown | No unique actions |
| **Lazy** | Smiley face | Lie on back, slowest mob in game, won't follow bamboo while on back |
| **Worried** | Teary eyes | Avoid players and hostile mobs, shake during thunderstorms |
| **Playful** | Tongue out | Roll over and jump around (can roll off cliffs!) |
| **Aggressive** | Thick eyebrows, tight frown | Attack continuously until target defeated or out of range |
| **Weak** | Teary eyes, snotty nose | Half health, sneeze more often as babies |
| **Brown** | Brown color | Rarest variant, behaves like Normal |

### Genetics System
- Each panda has a **main gene** and **hidden gene**
- **Dominant traits**: Normal, Aggressive, Lazy, Worried, Playful
- **Recessive traits**: Weak, Brown (require both genes to match)

---

### Breeding Mechanics

| Requirement | Java Edition | Bedrock Edition |
|-------------|--------------|-----------------|
| **Bamboo nearby** | 1 block within 5-7 blocks | 8 blocks within 5 blocks of *each* panda |
| **Feed** | Bamboo (right-click) | Bamboo (right-click) |
| **Cooldown** | 5 minutes | 5 minutes |

#### Offspring Inheritance
- Each parent passes **one gene** (main or hidden) to offspring
- **1/32 chance** for each gene to mutate into a different gene
- Mutations favor: Normal, Weak, Brown

---

### 💡 Potential Improvements for Pandas

- [ ] More responsive personality behaviors
- [ ] Better hazard awareness (especially for Playful pandas rolling off cliffs)
- [ ] Personality-specific abilities or interactions
- [ ] Easier breeding conditions outside jungle biomes
- [ ] Worried pandas could alert player to nearby dangers
- [ ] Aggressive pandas could be trained/tamed as guards

---

## 📋 Summary: Mod Development Opportunities

### High Priority (Dog AI Focus)
1. **Better pathfinding** - Avoid hazards like lava, fire, cliffs
2. **Smarter combat** - Better targeting, don't attack from too far
3. **Companion commands** - Whistles, attack modes, guard behavior
4. **Survival instincts** - Retreat when low health

### Medium Priority
5. **Pack behavior** - Leader system, hunting mechanics
6. **Healing system** - Saturation from eating, easier mass healing
7. **Bonding mechanics** - Affection, loyalty progression

### Optional/Future
8. **Panda improvements** - If expanding scope beyond dogs
9. **Visual indicators** - Health bars, mood indicators
10. **Wolf variants with unique abilities** - Snowy = freeze resist, etc.

---

*Research compiled: December 29, 2025*
*Target version: Minecraft 1.21.11 (Fabric)*
