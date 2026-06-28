# 🐕 Better Dogs: A Social & Behavioral Overhaul (v4.14.7)

> [!IMPORTANT]
> **Early Access & Latest Builds:** The latest versions of this mod may be delayed on public mainstream platforms (Modrinth/CurseForge). If you want immediate access to the newest updates, you can always compile the code directly from this repository, or support me on **[Ko-fi](https://ko-fi.com/dasikigaijin)** to get pre-compiled early access builds!

**Better Dogs** is a comprehensive overhaul of the Minecraft Wolf, replacing robotic behaviors with a dynamic, personality-driven social system. Built for the modern "Post-Obfuscation" era of Minecraft, it enhances the taming loop with genetics, social bonding, and smart survival logic.

## 🔥 New in v4.14.7: Nemesis System & Hidden Treats

- **Nemesis (Grudge) System**: Tamed wolves will now actively form a blood feud against hostile mobs that kill their pack-mates! Wolves with an active grudge gain `STRENGTH` and `SPEED` when attacking their nemesis.
- **Hidden Favorite Treats**: Every dog has a hidden favorite treat! Finding and feeding them their favorite treat fully heals them, grants Regeneration II, and triggers their native Zoomies! (The specific treat can be hidden in Jade until discovered).
- **Jade UI Overhaul**: Full Jade integration for Dynamic Max Health rendering (bypassing the 20-icon limit) and a togglable "Inbred" trait status tag.
- **True Pack Hunting (Flanking Tactics)**: Wolves no longer charge mindlessly in a single-file line. Followers intelligently spread out to flank and encircle targets, maximizing combat effectiveness and dodging splash attacks.

---

## 🌟 Recent Highlights (v4.6.15 - v4.10.1)

- **Morning Gifts & Interaction Rewards**: Positive interactions (feeding, sitting, calming down) accumulate hidden merits. Build up enough merits, and your dog has a chance to wake you up with a random gift in its mouth after you sleep!
- **Custom Advancements**: A full set of 12 custom husbandry achievements to reward personality taming, guard patrol training, breeding lineage recovery, inbred runt curing, litter sizes, and ground feeding.
- **P2P Multiplayer Compatibility**: In theory, singleplayer hosts opening their world to multiplayer using the new Minecraft 26.2 P2P friends system will work seamlessly with unmodded vanilla clients (joining players do not need to install the mod).
- **Guarding Sit Lock Fix**: Resolves manual sitting issues for guarding wolves of all personalities. Normal sentinel wolves remain sitting and locked from movement/attack, pausing the active `WolfGuardGoal`.
- **Dynamic Follower Spread Scaling**: Tamed and wild wolves now space themselves out wider as the pack size $N$ increases. Spacing is calculated mathematically using the square root formula: $f(N) = \text{multiplier} \times \sqrt{N - 1}$.


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
- **Target**: Minecraft 26.2+
- **Language**: Java 25
- **Logic**: Event-Driven AI Scheduler (Dasik Social AI).
- **Uninstall Safe**: Adheres to the "Vanilla Outsider" Non-Destructive Modding pillar. Uses zero custom blocks, items, or entity registries. Custom data is stored as hidden NBT tags on vanilla wolves. **You can uninstall this mod from a long-term survival world at any time without causing world corruption or missing registry crashes.**

---

## 👤 Credits & Support
- **Creator**: DasikIgaijin (Vanilla Outsider Collection)
- **License**: GNU GPLv3
- **Source Code**: [GitHub Repository](https://github.com/Rifaditya/Vanilla-Outsider-Better-Dogs)
- **Support Me**: [Ko-fi](https://ko-fi.com/dasikigaijin)

### 💖 Support the Project
Keeping this mod open-source, up-to-date, and completely free takes a massive amount of time and dedication. If you like the mod, please support me! Even if you build and use the latest code straight from this repository, **downloading the mod on Modrinth or CurseForge** generates crucial support that keeps this project alive.

You can also donate directly to help cover hosting and development costs. Every single download, share, and donation really helps me keep this mod open-source and active!
- **Download on Modrinth**: [Modrinth Page](https://modrinth.com/mod/vanilla-outsider-better-dogs)
- **Download on CurseForge**: [CurseForge Page](https://www.curseforge.com/minecraft/mc-mods/vanilla-outsider-better-dogs)

### 🤖 AI Assistance
This project utilizes AI-assisted development tools (like Antigravity) to help with rapid prototyping, architectural planning, and documentation, allowing for faster iterations and complex feature implementations while adhering to strict code quality standards.
