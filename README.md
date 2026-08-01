# 🐕 Better Dogs: A Social & Behavioral Overhaul (v4.24.1+26.1.2)

> [!IMPORTANT]
> **Release Availability Note:** Version `4.24.1+26.1.2` (Minecraft 26.1.2 backport) will be officially published on Modrinth and CurseForge concurrently when `4.24.1+26.2` is released! If you wish to use it prior to release, you can compile the JAR directly from this repository or access pre-compiled builds via **[Ko-fi](https://ko-fi.com/dasikigaijin)**.

> [!NOTE]
> **🙏 I am begging you!** If you are enjoying this mod or compiling from source, please take a moment to visit and download the mod on **[Modrinth](https://modrinth.com/mod/vanilla-outsider-better-dogs)** or **[CurseForge](https://www.curseforge.com/minecraft/mc-mods/vanilla-outsider-better-dogs)**! Every single download on those pages gives me a little bit of money that helps me pay the bills and keeps this project alive. Thank you!

**Better Dogs** is a comprehensive overhaul of the Minecraft Wolf, replacing robotic behaviors with a dynamic, personality-driven social system. Built for the modern "Post-Obfuscation" era of Minecraft, it enhances the taming loop with genetics, social bonding, and smart survival logic.

## 🔥 Backport Highlights (v4.24.1+26.1.2 - Minecraft 26.1.2 Parity)

- **Goat Horn Command System**: Command your pack using Seek Goat Horns! Tamed wolves in range focus fire on highlighted target entities or sweep the area at `1.3x` speed.
- **Creeper Blast Evasion**: Tamed wolves detect swelling/igniting creepers within 10 blocks and sprint radially away at `1.5x` speed emitting emergency smoke trails.
- **Nemesis (Grudge) System**: Tamed wolves actively form blood feuds against hostile mob types that kill their pack-mates! Wolves with an active grudge gain `STRENGTH` and `SPEED` when attacking their nemesis.
- **Jade UI Overhaul**: Full Jade integration for Dynamic Max Health rendering and togglable trait status tags adapted for Minecraft 26.1.2 APIs (`Gui.HeartType`).
- **Ground Scavenging & Feeding**: Tamed wolves automatically eat dropped raw and cooked food to restore health.
- **Guarding Sit Lock Fix**: Resolves manual sitting issues for guarding wolves of all personalities. Sentinel wolves remain sitting and locked from movement/attack, pausing the active `WolfGuardGoal`.

---

## ✨ Features

### 🧠 Personality Intelligence
When tamed, wolves develop one of three permanent personalities:

<p align="center">
  <img src="https://cdn.modrinth.com/data/cached_images/bcda6a78fa3159bc710566a2cbedfa1e94a03930.png" alt="Aggressive dog particle" width="30%">
  <img src="https://cdn.modrinth.com/data/cached_images/6d8c999c94d61346d925ecadd1f2655ef412bb0d.png" alt="Pacifist all love particle" width="30%">
  <img src="https://cdn.modrinth.com/data/cached_images/8722821ac9f7e1b6ca653e77cf90cf83dff2c432.png" alt="Vanilla/Normal" width="30%">
</p>

- 💢 **Aggressive**: The Guardian. Proactively attacks hostile mobs, scouts ahead, and wanders in a wider outer circle far from the player to patrol the perimeter. Emits red dust particles (`0xFF3333`) on patrol.
- ❤️ **Pacifist**: The Healer. Avoids combat unless you are hurt. High health, low damage. Emits green/teal dust particles (`0x00FF88`) on patrol.
- ✨ **Normal**: The Classic. Balanced stats and standard vanilla-plus behavior. Emits gold/yellow dust particles (`0xFFD700`) on patrol.

### 🛡️ Tamed Wolf Guard Mode

Right-click a tamed wolf with a bone while sneaking (Shift + Right Click) to toggle **Guard Mode** (consuming exactly 1 bone) and anchor the wolf to its current spot. Guard Mode is locked to the owner.

- **Patrol Patterns**:
  - **Aggressive**: Paces in a circular/polygon shape along its outer perimeter sweep (80% range), pausing to scan outward for threats.
  - **Normal**: Sentry posture at the guard post (range = 0), or radial patrols outward and back (range > 0).
  - **Pacifist**: Close protective orbital circular pacing around the post.
- **Auto-Targeting & Chase Caps**: Normal and Aggressive guards automatically attack hostiles within their range but are capped from chasing targets too far to prevent them from being lured away.
- **Watchdog Alarms & Alert Stance**: Pacifist sentinels whine, freeze in a pointing/alert stance facing the direction of detected threats, and emit warning note particles when hostiles approach, applying Regeneration and Resistance to owners/allies if enabled.

### 🤝 Advanced Social AI
- **Social Bonding (Affinity)**: Dogs form relationships within their pack. Socializing builds trust and reduces accidental infighting.
- **Adult Correction**: Aggressive adults discipline misbehaving puppies, preventing death loops.

### 🛡️ Smart Survival AI
- **Cliff Safety**: Wolves detect fatal drops and airborne targets, stopping dangerous chases.
- **Hazard Awareness**: Improved pathfinding around lava, fire, and drowning hazards.
- **Creeper Awareness**: Wolves flee from hissing Creepers!
- **Dog Feeding**: Tamed dogs scavenge dropped raw or cooked food from the ground to restore health — no manual feeding required.

### 🌤️ Immersive Events
- **Zoomies**: Dogs burst into hyperactive sprints in the morning or when it starts raining — pure joy!
- **Group Howl**: Under a full moon, wolves trigger pack-wide howling sessions that spread to nearby pack members.
- **Storm Anxiety**: Thunderstorms make dogs anxious — they whine, tremble, and pace nervously until the storm passes.

### 🏰 Wild Wolf Territoriality
Wild wolf packs are dynamic, territorial entities led by a dominant leader:
- **Dynamic Outcomes**: Packs may **Merge**, engage in **War**, or peacefully **Retreat** to maintain their own borders.
- **1v1 Leader Duels**: If a dispute escalates to War, leaders engage in a 1v1 duel to settle dominance.

### 💻 Server-Side Optional & Client Gating
- **Server-Side Optional**: Better Dogs is fully compatible with vanilla clients! Vanilla clients can connect to servers running the mod without installing it.
- **GUI Config Integration**: Fully supports configuration GUI integrations via **ModMenu** + **Cloth Config** in singleplayer mode.
- **Production Stable**: Fully compatible with high-performance engines like **C2ME**, ensuring safe multi-threaded AI execution.

---

## ⚙️ Native Configuration
Better Dogs uses the **Native Minecraft Game Rules** system. All 50+ mod parameters are grouped into a dedicated **"Better Dogs"** category in the official UI.

---

## 🏗️ Project Architecture
- **Target**: Minecraft 26.1.2
- **Language**: Java 25
- **Logic**: Event-Driven AI Scheduler (Dasik Social AI).
- **Uninstall Safe**: Adheres to the "Vanilla Outsider" Non-Destructive Modding pillar. Custom data is stored as hidden NBT tags on vanilla wolves. **You can uninstall this mod from a long-term survival world at any time without causing world corruption.**

---

## 👤 Credits & Support
- **Creator**: DasikIgaijin (Vanilla Outsider Collection)
- **License**: GNU GPLv3
- **Source Code**: [GitHub Repository](https://github.com/Rifaditya/Vanilla-Outsider-Better-Dogs)
- **Support Me**: [Ko-fi](https://ko-fi.com/dasikigaijin)

<p align="center">

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/dasikigaijin/tip)

</p>
