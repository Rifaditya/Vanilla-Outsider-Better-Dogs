# Welcome to the Better Dogs Wiki

Welcome to the microscopic reference wiki for **Better Dogs**, a gameplay overhaul mod for Minecraft wolves. This wiki is structured version-by-version around the major milestones that shaped the mod's mechanics, architecture, and configuration options.

---

## 🎮 Version Streams Compatibility

Better Dogs is actively maintained across two distinct Minecraft version streams in full feature parity:

| Minecraft Version | Target Mod Release | Dependency Mappings |
| :--- | :--- | :--- |
| **Minecraft 26.2+** | `4.8.x-26.2` (and newer) | Java 25, Fabric API, DasikLibrary 1.8.0 |
| **Minecraft 26.1.2** | `3.10.x-26.1.2` (and newer) | Java 25, Fabric API, DasikLibrary 1.8.0 |

---

## 🗺️ Wiki Milestone Navigation

Explore the wiki pages sorted by the major milestones they were introduced in:

### 1. [[Milestone: Core Personalities & Genetics (v4.0.x / v3.4.x)|Milestone-Core-Personalities-and-Genetics]]
*The update that laid the genetics foundations.* Includes personality matrices (Aggressive, Normal, Pacifist), UUID-based DNA rolls, stat inheritance calculations, kinship tracking, inbred runts, Golden Apple outcross cures, and the Litter System.

### 2. [[Milestone: Wild Packs & Territoriality (v4.3.x / v3.5.x)|Milestone-Wild-Packs-and-Territoriality]]
*The update that brought wild packs to life.* Includespersistent leader anchors, dynamic follower spread scaling math, the leader-personality dispute matrix, 1v1 cinematic duels, and yield-and-merge pack mechanics.

### 3. [[Milestone: Smart Survival & Performance (v4.7.x / v3.9.x)|Milestone-Smart-Survival-and-Performance]]
*The engine update for TPS safety.* Includes cliff safety path-gating, fire/lava avoidance, Creeper alarm panning, scavenge feeding logic, the Event-Driven AI Scheduler, boids-inspired $O(N)$ caching, and multi-thread/C2ME safety.

### 4. [[Milestone: Guard Mode & Sentinels (v4.8.x / v3.10.x)|Milestone-Guard-Mode-and-Sentinels]]
*The update that refined tamed guard behaviors.* Includes sneaking bone toggles, personality patrol sweeps, target-sweep caps, point-and-freeze alert stances, directional trigonometric particle sprays, and Lunar group howling shared cooldowns.

### 5. [[Config & GameRules Technical Reference|Config-and-GameRules]]
*The complete parameters sheet.* Includes a categorized, exhaustive table of all 50+ GameRules, default configuration templates, and ModMenu/Cloth Config visual GUI options.

---

## 🌟 Mod Philosophy: Vanilla Outsider

Better Dogs strictly adheres to the **Vanilla Outsider** design philosophy:
1. **Enhance, Don't Replace**: Build upon vanilla entity concepts. Wolves remain loyal companions but operate with intelligent, life-like autonomy.
2. **Remove AI Jank**: Prevent frustrating vanilla deaths (falling off cliffs, running into lava) without making wolves immortal or overpowered.
3. **Vanilla Client Compatibility**: 100% server-side optional. Vanilla clients can join servers running Better Dogs seamlessly.
