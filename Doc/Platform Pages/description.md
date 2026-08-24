<p align="center">
    <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <img src="https://img.shields.io/badge/Language-Java_25-orange?style=for-the-badge&logo=java" alt="Java 25">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
    <img src="https://img.shields.io/badge/Minecraft-26.2+-brightgreen?style=for-the-badge" alt="Minecraft 26.2+">
</p>

# 🐕 Better Dogs

- **Compatibility Notice Added**: `⚠️ Incompatible with Jade >= 26.2.11+ | Works on Jade < 26.2.10 or without Jade (Fix available in version 4.24.37+ on early access)`

> **Make Wolves Worthy Companions. Smarter, Safer, Livelier.**

Every Minecraft player knows the pain: you spend hours finding a wolf, tame it, and five minutes later it jumps into lava or walks off a cliff. **Better Dogs** overhauls wolf AI to make them effective partners. Powered by a high-performance **Event-Driven AI Scheduler**, they act efficiently without sacrificing performance.

Part of the **Vanilla Outsider Collection** — mods that refine the vanilla experience with modern standards.

---

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

<blockquote><strong>Guard Mode Particles:</strong> To help you easily identify a patrolling or guarding wolf's personality from a distance, they emit tiny, custom-colored dust particles <strong>only while actively in Guard Mode</strong> (no particles are emitted during normal following or sitting):</blockquote>

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
Easily transfer ownership of your tamed wolves to other players using standard, vanilla-only **Paper**:

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/wolve_is_ready_for_adoption_image.png" alt="Wolf is Ready for Adoption" width="85%">
  <br><br>
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/adoption_called_off_image.png" alt="Adoption Called Off" width="85%">
</p>

- **Pending Adoption State**: Shift + Right-Click your tamed wolf with a sheet of Paper to put them up for adoption. The wolf will sit down, halt pathfinding, clear all aggression, and emit a beautiful, sparkling trail of **Rose Pink** particles.
- **Claiming the Wolf**: Any non-owner player can right-click the adoptable wolf with an empty main hand to claim them as their new companion, instantly triggering heart particles and mutual overlay notifications.
- **Safety Checks**: The adoption state is automatically cancelled if the wolf takes any damage, or if the owner simply right-clicks the dog again normally to cancel, preventing accidental claims.

### 📏 Dynamic Size & Attribute Scaling
In the 26.2+ release stream, physical size is integrated directly with genetics and health using Minecraft's native `Attributes.SCALE`:

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/the%20scale%20image.webp" alt="Dynamic Size & Attribute Scaling" width="85%">
</p>

### 📏 Dynamic Follower Spread Scaling
Follow/spread spacing of wild and tamed wolf packs scales dynamically based on the number of active followers to prevent visual overcrowding.

### 🤝 Advanced Social AI
- **Social Bonding (Affinity)**: Dogs form relationships within their pack. Socializing builds trust and reduces accidental infighting.
- **Adult Correction**: Aggressive adults discipline misbehaving puppies, preventing death loops.
- **Pack Genetics**: Puppies inherit personality traits and stats from their parents.
- **Litter System**: Wolves can produce multiple puppies in a single breed — each with independent personality rolls and stats.
- **Unrelated Mate Prioritization**: Tamed wolves prioritize breeding with unrelated pack mates in range.
- **Selective Ground Food Refusal**: Selective lineages can persistently refuse to eat food scavenged from the ground.

### 🛡️ Smart Survival AI
- **Cliff Safety**: Wolves detect fatal drops and airborne targets, stopping dangerous chases.
- **Hazard & Magma Avoidance**: Wolves intelligently pathfind around lava, fire, drowning hazards, and magma blocks.
- **Collision & Push Safety**: Tamed wolves colliding with an endangered packmate halt navigation to prevent pushing them into danger.
- **Creeper Awareness**: Wolves flee from hissing Creepers!
- **Dog Feeding**: Tamed dogs scavenge dropped raw or cooked food from the ground to restore health.
- **Dynamic Whimpering Thresholds**: Low-health whining is dynamically scaled based on max health (< 50% max HP).

### 📡 Behavioral Specialization & Commands
- **Scouting**: Aggressive dogs proactively range ahead to clear your path.
- **Silent Alarm**: Pacifist dogs emit a high-pitched whine and freeze to point toward nearby monsters.
- **Select-and-Ride Command System**: Sneak-right-click a tamed wolf with a standard Stick (or Blaze Rod / Breeze Rod) to command them to sit or ride inside vehicles, mounts, boats, minecarts, or modded chairs, forcing a proper visual sitting pose.

<p align="center">
  <strong>🎬 Video Tutorial: Select-and-Ride Command System</strong><br>
  <em>Click the thumbnail or button below to watch the feature showcase on YouTube</em><br><br>
  <a href="https://youtu.be/pv7-6xaip-Y" target="_blank" rel="noopener">
    <img src="https://img.youtube.com/vi/pv7-6xaip-Y/maxresdefault.jpg" alt="▶️ Click to Watch Video Tutorial on YouTube" width="85%">
  </a><br><br>
  <a href="https://youtu.be/pv7-6xaip-Y" target="_blank" rel="noopener">
    <img src="https://img.shields.io/badge/▶️_Watch_Video-Play_on_YouTube-FF0000?style=for-the-badge&logo=youtube&logoColor=white" alt="▶️ Play Video on YouTube">
  </a>
</p>

- **Gift System**: Loyal dogs bring you treasures based on their personality.
- **Debug Tools**: Use `/betterdogs debug` to test pack interactions and behaviors (gated behind OP permissions).

### 🏆 Custom Advancement System
A collection of custom advancements rewards exploring all mechanics:

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/a%20pack%20of%20personality%20advancement%20image.webp" alt="A Pack of Personalities Advancement" width="85%">
</p>

### 🔍 Jade Mod HUD Integration
- **Dynamic Health Display**: Overrides Jade's default health bar to render exact numeric values (`HP: X / Y`) and hearts.
- **Inbred Tag Indicator**: Displays genetic inbred status directly in the Jade HUD tooltip.
- **Favorite Treat Tooltip**: Displays the dog's hidden Favorite Treat in Jade tooltips once discovered.

### 🌤️ Immersive Events
- **Zoomies**: Dogs burst into hyperactive sprints in the morning or when it starts raining.
- **Restored Soundscapes & Group Howl**: Under a full moon, wolves trigger pack-wide howling sessions.
- **Storm Anxiety**: Thunderstorms make dogs anxious — they whine, tremble, and pace nervously until comforted or sheltered.

### 🏰 Wild Wolf Territoriality
Wild wolf packs are dynamic, territorial entities led by a dominant leader with personality-driven outcomes (War, Merge, or Retreat) and 1v1 Leader Duels.

### 💻 Server-Side Optional & Client Gating
- **Server-Side Optional**: Fully compatible with vanilla clients!
- **Dedicated Server Support**: Client-only rendering logic is isolated to prevent server crashes.
- **GUI Config Integration**: Supports **ModMenu** + **Cloth Config** / **YACL v3** in singleplayer mode.
  <p align="center">
    <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/Cloth%20config%20sreen.webp" alt="Cloth Config Screen" width="85%">
  </p>
- **Performance Hardened**: All AI logic is performance-optimized using **DasikLibrary**.

---

## ⚙️ Configuration (Native Game Rules)

> 💡 **Config vs. In-Game GameRules:** The global configuration file only defines default values for new worlds at creation time. If you have already created/opened a world, change settings in-game using the Edit Game Rules UI screen or the `/gamerule` command.

No messy config files. Better Dogs uses the <strong>Native Minecraft Game Rules</strong> system. All mod parameters are grouped into a dedicated <strong>"Better Dogs"</strong> category in the official UI.

If the game rule screen feels cluttered, we highly recommend installing [Collapsible Game Rules](https://www.curseforge.com/minecraft/mc-mods/collapsible-gamerules) to group settings into clean folders.

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/Gamerule%20Screen%20options.webp" alt="Native gamerule UI">
</p>

---

## ☕ Support

<p align="center">
  <a href="https://ko-fi.com/dasikigaijin/tip"><img src="https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white" alt="Ko-fi"></a>
  <a href="https://sociabuzz.com/dasikigaijin/tribe"><img src="https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge" alt="SocioBuzz"></a>
  <a href="https://saweria.co/DasikIgaijinn"><img src="https://img.shields.io/badge/Saweria-Local_Support-FFA500?style=for-the-badge" alt="Saweria"></a>
</p>

> **🇮🇩 Indonesian Users:** SocioBuzz and Saweria support local payment methods (Gopay, OVO, Dana, etc.) if you want to support me without using PayPal/Ko-fi!

---

## 📜 Credits

| Role | Author |
| :--- | :--- |
| **Creator** | **Dasik** (Rifaditya) |
| **Collection** | Vanilla Outsider |
| **License** | GPLv3 |

> **📦 Modpack Permissions & Distribution:**<br>
> You are free to include this mod in any modpack on any platform. However, the mod itself must be downloaded from its official distribution pages on **Modrinth** or **CurseForge**. Re-uploading or redistributing the mod jar file to third-party sites is strictly prohibited unless explicitly permitted by the creator.
> <br><br>
> **License & Forks:**<br>
> Since the source code is licensed under **GNU GPLv3**, you are fully permitted to fork the repository, make modifications, build your own versions, and distribute them under the terms of the GPLv3.

---

<div align="center">
  <p><strong>Made with ❤️ for the Minecraft community</strong></p>
  <p><em>Part of the Vanilla Outsider Collection</em></p>
</div>
