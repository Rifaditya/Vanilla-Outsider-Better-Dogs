# 🐕 Better Dogs: A Social & Behavioral Overhaul (v4.14.7)

> [!IMPORTANT]
> **Early Access & Latest Builds:** The latest versions of this mod may be delayed on public mainstream platforms (Modrinth/CurseForge). If you want immediate access to the newest updates, you can always compile the code directly from this repository, or support me on **[Ko-fi](https://ko-fi.com/dasikigaijin)** to get pre-compiled early access builds!

> [!NOTE]
> **🙏 I am begging you!** If you are enjoying this mod or compiling from source, please take a moment to visit and download the mod on **[Modrinth](https://modrinth.com/mod/vanilla-outsider-better-dogs)** or **[CurseForge](https://www.curseforge.com/minecraft/mc-mods/vanilla-outsider-better-dogs)**! Every single download on those pages gives me a little bit of money that helps me pay the bills and keeps this project alive. Thank you!

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
## ✨ Features

### 🧠 Personality Intelligence
When tamed, wolves develop one of three permanent personalities:

<p align="center">
  <img src="https://cdn.modrinth.com/data/cached_images/bcda6a78fa3159bc710566a2cbedfa1e94a03930.png" alt="Aggressive dog particle" width="30%">
  <img src="https://cdn.modrinth.com/data/cached_images/6d8c999c94d61346d925ecadd1f2655ef412bb0d.png" alt="Pacifist all love particle" width="30%">
  <img src="https://cdn.modrinth.com/data/cached_images/8722821ac9f7e1b6ca653e77cf90cf83dff2c432.png" alt="Vanilla/Normal" width="30%">
</p>

- 💢 **Aggressive**: The Guardian. Proactively attacks hostile mobs and scouts ahead. Emits red dust particles (`0xFF3333`) on patrol.
- ❤️ **Pacifist**: The Healer. Avoids combat unless you are hurt. High health, low damage. Emits green/teal dust particles (`0x00FF88`) on patrol.
- ✨ **Normal**: The Classic. Balanced stats and standard vanilla-plus behavior. Emits gold/yellow dust particles (`0xFFD700`) on patrol.

### 🛡️ Tamed Wolf Guard Mode

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/guard_mode_activation.png" alt="Guard Mode Activation" width="48%">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/guard_mode_deactivation.png" alt="Guard Mode Deactivation" width="48%">
</p>

Right-click a tamed wolf with a bone while sneaking (Shift + Right Click) to toggle **Guard Mode** (consuming exactly 1 bone) and anchor the wolf to its current spot. Guard Mode is locked to the owner.

> [!NOTE]
> **Guard Mode Particles**: To help you easily identify a patrolling or guarding wolf's personality from a distance, they emit tiny, custom-colored dust particles **only while actively in Guard Mode** (no particles are emitted during normal following or sitting):

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/particle_aggressive.png" alt="Aggressive Guard Particle" width="30%">&nbsp;<img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/particle_pacifist.png" alt="Pacifist Guard Particle" width="30%">&nbsp;<img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/particle_classic.png" alt="Classic/Normal Guard Particle" width="30%">
</p>

- **Patrol Patterns**:
  - **Aggressive**: Paces in a circular/polygon shape along its outer perimeter sweep (80% range), pausing to scan outward for threats.
  - **Normal**: Sentry posture at the guard post (range = 0), or radial patrols outward and back (range > 0).
  - **Pacifist**: Close protective orbital circular pacing around the post.
- **Auto-Targeting & Chase Caps**: Normal and Aggressive guards automatically attack hostiles within their range but are capped from chasing targets too far to prevent them from being lured away.
- **Watchdog Alarms & Alert Stance**: Pacifist sentinels whine, freeze in a pointing/alert stance facing the direction of detected threats, and emit warning note particles when hostiles approach, applying Regeneration and Resistance to owners/allies if enabled.

### 📄 Wolf Ownership Transfer (Adoption)

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/wolve_is_ready_for_adoption_image.png" alt="Wolf is Ready for Adoption" width="85%">
  <br><br>
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/adoption_called_off_image.png" alt="Adoption Called Off" width="85%">
</p>

Easily transfer ownership of your tamed wolves to other players using standard, vanilla-only **Paper**:
- **Pending Adoption State**: Shift + Right-Click your tamed wolf with a sheet of Paper to put them up for adoption. The wolf will sit down, halt pathfinding, clear all aggression, and emit a beautiful, sparkling trail of **Rose Pink** particles.
- **Claiming the Wolf**: Any non-owner player can right-click the adoptable wolf with an empty main hand to claim them as their new companion, instantly triggering heart particles and mutual overlay notifications.
- **Safety Checks**: The adoption state is automatically cancelled if the wolf takes any damage, or if the owner simply right-clicks the dog again normally to cancel, preventing accidental claims.

> ⚠️ **Notice**: This feature is **unavailable in the 26.1.2 version**.

### 🤝 Advanced Social AI
- **Social Bonding (Affinity)**: Dogs form relationships within their pack. Socializing builds trust and reduces accidental infighting.
- **Adult Correction**: Aggressive adults discipline misbehaving puppies, preventing death loops.
- **Pack Genetics**: Puppies inherit personality traits and stats from their parents. *(Feature unavailable in 26.1.2)*
- **Litter System**: Wolves can produce multiple puppies in a single breed — each with independent personality rolls and stats. *(Feature unavailable in 26.1.2)*

### 🛡️ Smart Survival AI
- **Cliff Safety**: Wolves detect fatal drops and airborne targets, stopping dangerous chases.
- **Hazard Awareness**: Improved pathfinding around lava, fire, and drowning hazards.
- **Creeper Awareness**: Wolves flee from hissing Creepers!
- **Dog Feeding**: Tamed dogs scavenge dropped raw or cooked food from the ground to restore health — no manual feeding required.

### 🌤️ Immersive Events
- **Zoomies**: Dogs burst into hyperactive sprints in the morning or when it starts raining — pure joy!
- **Group Howl**: Under a full moon, wolves trigger pack-wide howling sessions that spread to nearby pack members.
- **Storm Anxiety**: Thunderstorms make dogs anxious — they whine, tremble, and pace nervously until the storm passes. This is highly dependent on their personality: Pacifist dogs are extremely prone to anxiety, Normal dogs have standard chances, and Aggressive dogs are completely immune.

### 🏰 Wild Wolf Territoriality
Wild wolf packs are now dynamic, territorial entities led by a dominant leader:
- **Territorial Probability Matrix**: Pack interactions now utilize a complex, personality-driven matrix. No two disputes are the same!
- **Dynamic Outcomes**: Depending on leader personalities, packs may **Merge** (Dominance based), engage in **War** (1v1 Duels or Pack Combat), or peacefully **Retreat** to maintain their own borders.
- **Configurable Instincts**: Use GameRules to fine-tune exactly how Aggressive, Normal, and Pacifist leaders react to each other.
- **1v1 Leader Duels**: If a dispute escalates to War, leaders engage in a cinematic 1v1 duel to settle dominance.
- **Yield & Merge**: Defeated packs yield and **merge** with the winning pack, allowing for the natural formation of massive wolf colonies.
- **Wild Personality AI (New Default)**: Out of the box, wild pack members exhibit unique behaviors (like Aggressive hunting or Pack Retreats) while anchored to their leader.
- **Dynamic Spawning & Reinforcements**: When a pack goes to war or is challenged, nearby wild wolves can spawn or join as reinforcements to defend their pack's territory.

### 💻 Server-Side Optional & Client Gating
- **Server-Side Optional**: Better Dogs is fully compatible with vanilla clients! Vanilla clients can connect to servers running the mod without installing it.
- **Dedicated Server Support**: Client-only rendering logic is isolated to prevent classloading crashes on headless dedicated servers.
- **GUI Config Integration**: Fully supports configuration GUI integrations via **ModMenu** + **Cloth Config** in singleplayer mode.
  <p align="center">
    <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/Cloth%20config%20sreen.webp" alt="Cloth Config Screen" width="85%">
  </p>
- **Production Stable**: Fully compatible with high-performance engines like **C2ME**, ensuring safe multi-threaded AI execution.
- **Performance Hardened**: All AI logic is performance-optimized using **DasikLibrary 1.8.0**, ensuring zero console spam and smooth server TPS even with massive packs.

---

## ⚙️ Native Configuration
Better Dogs uses the **Native Minecraft Game Rules** system. All 50+ mod parameters are grouped into a dedicated **"Better Dogs"** category in the official UI.

> [!TIP]
> **Too many rules?** If the game rule screen feels cluttered, we highly recommend installing **[Collapsible Game Rules](https://modrinth.com/mod/collapsible-gamerules)**. It will automatically group the new Personality Matrix settings into clean, expandable folders!

<p align="center">
<img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/Gamerule%20Screen%20options.webp" alt="Native gamerule UI">
</p>

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

<p align="center">

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/dasikigaijin/tip)
[![SocioBuzz](https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge)](https://sociabuzz.com/dasikigaijin/tribe)
[![Saweria](https://img.shields.io/badge/Saweria-Local_Support-FFA500?style=for-the-badge)](https://saweria.co/DasikIgaijinn)

</p>

> [!NOTE]
> **Indonesian Users:** SocioBuzz and Saweria support local payment methods (Gopay, OVO, Dana, etc.) if you want to support me without using PayPal/Ko-fi!

You can also donate directly to help cover hosting and development costs. Every single download, share, and donation really helps me keep this mod open-source and active!
- **Download on Modrinth**: [Modrinth Page](https://modrinth.com/mod/vanilla-outsider-better-dogs)
- **Download on CurseForge**: [CurseForge Page](https://www.curseforge.com/minecraft/mc-mods/vanilla-outsider-better-dogs)

### 🤖 AI Assistance
This project utilizes AI-assisted development tools (like Antigravity) to help with rapid prototyping, architectural planning, and documentation, allowing for faster iterations and complex feature implementations while adhering to strict code quality standards.
