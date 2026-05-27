# 🐕 Better Dogs: A Social & Behavioral Overhaul (v3.7.1+A-26.1.2)

**Better Dogs** is a comprehensive overhaul of the Minecraft Wolf, replacing robotic behaviors with a dynamic, personality-driven social system. Built for the modern "Post-Obfuscation" era of Minecraft, it enhances the taming loop with genetics, social bonding, and smart survival logic.

## 🔥 New in v3.7.1+A-26.1.2: Guarding Sit Lock Fix (Alpha)

This patch resolves manual sitting issues for guarding wolves of all personalities:
- **Normal Guarding Sit Fix**: Fixed a bug where Normal personality sentinel wolves immediately stood back up when manually ordered to sit.
- **Guarding Sit Lock**: Manual sitting now correctly pauses the active `WolfGuardGoal` and gates custom target selectors (e.g. `AggressiveTargetGoal`), ensuring guarding wolves remain sitting and locked from movement/attack, matching vanilla behavior.
- **Dynamic Follower Spread Scaling (v3.7.0)**: Tamed and wild wolves now space themselves out wider as the pack size $N$ increases. Spacing is calculated mathematically using the square root formula: $f(N) = \text{multiplier} \times \sqrt{N - 1}$.

This update scales the follow/spread spacing of wild and tamed wolf packs dynamically based on the number of active followers:

- **Dynamic Pack Spread Scaling**: Tamed and wild wolves now space themselves out wider as the pack size $N$ increases. Spacing is calculated mathematically using the square root formula: $f(N) = \text{multiplier} \times \sqrt{N - 1}$.
- **Tamed Follow Spacing**: Tamed wolves following a player dynamically increase their follow start and stop thresholds, preventing overcrowding.
- **Wild Flock Spacing**: Wild pack members dynamically adjust cohesion and separation radii during flocking, resulting in organic pack formations.
- **New Spacing GameRules**: Added 4 new native GameRules to configure the spacing multipliers and limits:
  - `bd_tamed_pack_spread_multiplier` (Default: 120 = 1.2x)
  - `bd_tamed_pack_spread_max` (Default: 60 = 6.0 blocks max extra)
  - `bd_wild_pack_spread_multiplier` (Default: 80 = 0.8x)
  - `bd_wild_pack_spread_max` (Default: 40 = 4.0 blocks max extra)
- **Subtle Colored Dust Particles (v3.6.6)**: Replaced ambient guard mode particles and debug particles with tiny, custom-colored `dust` particles at a subtle `0.5f`/`0.6f` scale.
  - 🔴 **Aggressive**: Red particle (`0xFF3333`)
  - 🟡 **Normal**: Gold/Yellow particle (`0xFFD700`)
  - 🟢 **Pacifist**: Green/Teal particle (`0x00FF88`)
- **Gated Personality Particles**: Personality particles now strictly emit only when the wolf is on active Guard Mode.
- **Client Synchronization**: Particles tick exclusively on the server side using `serverLevel.sendParticles()`, ensuring the correct personality visuals show on clients without client-side attachment sync.
- **ConfigHelper Migration (v3.6.4)**: Refactored `BetterDogsConfig` to delegate configuration serialization to `DasikLibrary`'s `ConfigHelper`.

---

- **🏰 Guard Mode Activation**: Right-clicking a tamed wolf with a bone toggles Guard Mode (consuming exactly 1 bone) and anchors the wolf to its current block position.
- **📐 Mathematical Patrols**: Anchored guards perform patrolling patterns matching their personality (Aggressive = outer perimeter sweeps; Normal = post sentinels or radial star sweeps; Pacifist = close orbital pacing).
- **🛡️ Auto-Targeting & Chase Caps**: Normal and Aggressive guards automatically attack hostiles within range (16/24 blocks) but are capped from chasing too far (20/32 blocks) to prevent them being lured away.
- **🔊 Watchdog Alarms & Buffs**: Pacifist guards whine and emit notes when hostiles approach, and apply optional Regeneration and Resistance buffs to allies (gated by GameRule).
- **🍃 Subtle Foot Particles**: Displays very subtle, non-distracting foot particles every 4 seconds (`ASH` for Aggressive, `WHITE_ASH` for Normal, `MYCELIUM` for Pacifist) to indicate guard status.
- **⚡ Performance & Security Polish**: Heavy entity queries and effect allocations are time-sliced (running once per second). Debug stick interactions on wolves are gated behind operator permission checks.

---

## ✨ Key Features

### 🧠 Unique Personality System
Every dog is assigned one of three personalities with unique traits:
- **Aggressive**: Fierce protectors that attack hostiles on sight. Proactively **Scouts** ahead.
- **Pacifist**: Gentle companions that avoid combat. Acts as a **Silent Alarm** for monsters.
- **Normal**: Balanced companions with standard vanilla-plus behavior.

### 🐕 Advanced Social AI
- **The Snitch System (Adult Correction)**: Adults intervene to discipline misbehaving puppies using the `AdultCorrectionGoal`.
- **Wild Wolf Packs**: Powered by **DasikLibrary**, wild wolves form cohesive packs (up to 8) with leaders and follower logic.
- **Social Events**: Morning zoomies, full-moon howling, and non-lethal social interactions.
- **Genetics**: Puppies inherit personality traits and stats from their parents via the `WolfBreedingMixin`.

### 🛡️ Smart Survival & Quality of Life
- **Cliff Safety**: Wolves detect fatal falls and stop chasing targets over voids or high drops.
- **Hazard Avoidance**: Improved pathfinding around lava, fire, and drowning hazards.
- **Dog Feeding**: Tamed dogs can scavenge dropped raw or cooked food from the ground to restore health via `EatGroundFoodGoal`.

---

## ⚙️ Native Configuration
Better Dogs uses the **Native Minecraft Game Rules** system. All 50+ parameters are grouped into a dedicated category for zero-dependency tuning.

![Native gamerule UI](Doc/Media/Gallery/native_game_rules_ui.png)

---

## 🏗️ Project Architecture
- **Target**: Minecraft 26.1-snapshot-11+
- **Language**: Java 25
- **Logic**: Event-Driven AI Scheduler (Dasik Social AI).

---

## 👤 Credits & Support
- **Creator**: DasikIgaijin (Vanilla Outsider Collection)
- **License**: GNU GPLv3
- **Dev**: [GitHub Repository](https://github.com/Rifaditya/Vanilla-Outsider-Better-Dogs)
