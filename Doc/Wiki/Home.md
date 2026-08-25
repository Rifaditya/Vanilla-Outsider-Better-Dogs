# Welcome to the Better Dogs Wiki

🌐 **Languages**: [[🇺🇸 English|Home]] | [[🇮🇩 Bahasa Indonesia|id_id-Home]]

Welcome to the microscopic reference wiki for **Better Dogs**, a gameplay overhaul mod for Minecraft wolves. This wiki is structured version-by-version around the major milestones that shaped the mod's mechanics, architecture, and configuration options.

> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 🎮 Version Streams Compatibility

Better Dogs is actively maintained across distinct Minecraft version streams in full feature parity:

| Minecraft Version | Target Mod Release | Dependency Mappings |
| :--- | :--- | :--- |
| **Minecraft 26.3** | `5.0.76+26.3` | Java 25+, Fabric API, DasikLibrary |
| **Minecraft 26.2** | `4.24.74+26.2` | Java 25+, Fabric API, DasikLibrary |
| **Minecraft 26.1.2** | `3.10.x+26.1.2` | Java 25+, Fabric API, DasikLibrary |
| **Minecraft 1.21.11** | `2.x+1.21.11` | Java 21, Fabric API |
| **Minecraft 1.21.1** | `1.x+1.21.1` | Java 21, Fabric API |
| **Minecraft 1.20.1** | `1.0.x+1.20.1` | Java 17, Fabric API |

---

## 🗺️ Wiki Milestone Navigation

Explore the wiki pages sorted by the major milestones they were introduced in:

### 1. [[Milestone: Core Personalities, Breeding & Genetics|Milestone-Core-Personalities-and-Genetics]]
*The update that laid the genetics foundations.* Includes personality matrices (Aggressive, Normal, Pacifist), UUID-based DNA rolls, stat inheritance calculations, kinship tracking, inbred runts, Golden Apple outcross cures, Morning Gift Loot Tables, and the Litter System.

### 2. [[Milestone: Wild Packs & Territoriality|Milestone-Wild-Packs-and-Territoriality]]
*The update that brought wild packs to life.* Includes persistent leader anchors, dynamic follower spread scaling math, the leader-personality dispute matrix, 1v1 cinematic duels, and yield-and-merge pack mechanics.

### 3. [[Milestone: Smart Survival, Fire Evasion & Performance|Milestone-Smart-Survival-and-Performance]]
*The engine update for wolf longevity and TPS safety.* Includes nearest water sprint & panic fire survival AI, cliff safety path-gating, fire/lava avoidance, Creeper panic evasion, data-driven tags (`#curiosity_blocks`, `#treats`, `#seats`), scavenge feeding logic, and zero-allocation FastRandom memory management.

### 4. [[Milestone: Guard Mode & Sentinels|Milestone-Guard-Mode-and-Sentinels]]
*The update that refined tamed guard behaviors.* Includes 6D interaction debounce, sneaking bone toggles, personality patrol sweeps, target-sweep caps, point-and-freeze alert stances, directional trigonometric particle sprays, and Lunar group howling shared cooldowns.

### 5. [[Config & GameRules Technical Reference|Config-and-GameRules]]
*The complete parameters sheet.* Includes a categorized, exhaustive table of all 50+ GameRules, default configuration templates, and ModMenu/Cloth Config visual GUI options.

---

## 🌟 Mod Philosophy: Vanilla Outsider

Better Dogs strictly adheres to the **Vanilla Outsider** design philosophy:
1. **Enhance, Don't Replace**: Build upon vanilla entity concepts. Wolves remain loyal companions but operate with intelligent, life-like autonomy.
2. **Remove AI Jank**: Prevent frustrating vanilla deaths (falling off cliffs, burning in fire/lava) without making wolves immortal or overpowered.
3. **Vanilla Client Compatibility**: 100% server-side optional. Vanilla clients can join servers running Better Dogs seamlessly.
