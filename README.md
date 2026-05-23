# 🐕 Better Dogs: A Social & Behavioral Overhaul (v3.4.18)

**Better Dogs** is a comprehensive overhaul of the Minecraft Wolf, replacing robotic behaviors with a dynamic, personality-driven social system. Built for the modern "Post-Obfuscation" era of Minecraft, it enhances the taming loop with genetics, social bonding, and smart survival logic.

## 🔥 New in v3.4.18: Mixin Modularization & Audit Cleanup

This update modularizes codebase complexities and cleans up static audit guidelines:

- **🧩 Mixin Modularization**: Extracted the Social Brain AI, genetics (DNA), affinity mapping, and social scheduler tick loops out of `WolfMixin.java` into a new dedicated `WolfSocialMixin.java` to stay under the 300 LOC limit.
- **🧹 Import Hygiene**: Batch-cleaned all duplicate and unused imports across the 75+ Java/Kotlin files in the project.
- **🏷️ Verified Citations**: Prepended standard `// Verified against: ... (26.1.2+)` headers to the beginning of all source code files.

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
