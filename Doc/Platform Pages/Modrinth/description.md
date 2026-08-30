<p align="center">
    <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <a href="https://modrinth.com/mod/dasik-library"><img src="https://img.shields.io/badge/Requires-Dasik_Library-purple?style=for-the-badge" alt="Requires Dasik Library"></a>
    <img src="https://img.shields.io/badge/Language-Java_25-orange?style=for-the-badge&logo=java" alt="Java 25">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
    <img src="https://img.shields.io/badge/Minecraft-26.2+-brightgreen?style=for-the-badge" alt="Minecraft 26.2+">
</p>

# 🐕 Better Dogs

> **"Make Wolves Worthy Companions. Smarter, Safer, Livelier."**

Every Minecraft player knows the pain: you spend hours finding and taming a wolf, and five minutes later it jumps into a 2-block lava pit or walks off a fatal cliff. **Better Dogs** overhauls vanilla wolf AI from the ground up to turn them into intelligent, durable, and highly capable companions. Powered by a high-performance **Event-Driven AI Scheduler**, every mechanic operates with near-zero tick overhead.

Part of the **Vanilla Outsider Collection** — modern mods designed to refine and elevate the vanilla Minecraft experience.

> [!NOTE]
> **1 Jar 1 Version Policy:** I build **1 dedicated JAR for each Minecraft version** (e.g. MC 26.2, MC 26.3). Please download the exact build that matches your Minecraft installation.

---

## ✨ Features

### 🧠 Personality Intelligence
Upon taming, every wolf permanently develops one of three distinct personalities with custom stats, traits, and ambient patrol particles:

<p align="center">
  <img src="https://cdn.modrinth.com/data/cached_images/bcda6a78fa3159bc710566a2cbedfa1e94a03930.png" alt="Aggressive Personality" width="30%">
  <img src="https://cdn.modrinth.com/data/cached_images/6d8c999c94d61346d925ecadd1f2655ef412bb0d.png" alt="Pacifist Personality" width="30%">
  <img src="https://cdn.modrinth.com/data/cached_images/8722821ac9f7e1b6ca653e77cf90cf83dff2c432.png" alt="Normal Personality" width="30%">
</p>

- 💢 **Aggressive (The Vanguard)**: Proactively scouts up to 10 blocks ahead of the owner, targets hostiles up to 20 blocks away (+15% speed, -15% damage, -10 HP offset, 10% low-health flee chance). Emits red patrol dust (`0xFF3333`).
- ❤️ **Pacifist (The Sentinel)**: Follows closely within 6 blocks (+20 HP bonus, -10% speed, +15% damage, +50% knockback, 100% low-health flee chance). Emits green/teal patrol dust (`0x00FF88`). Warns you of nearby monsters with vocal growls and alert stances.
- ✨ **Normal (The Balanced Companion)**: Follows reliably within 10 blocks (balanced stats, 50% low-health flee chance). Emits golden patrol dust (`0xFFD700`).

---

### 🛡️ Tamed Wolf Guard Mode

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/guard_mode_activation.png" alt="Guard Mode Activation" width="48%">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/guard_mode_deactivation.png" alt="Guard Mode Deactivation" width="48%">
</p>

Shift-Right-Click (**Sneak + Right-Click**) your tamed wolf with a **Bone** in your main hand to anchor them to their current spot in **Guard Mode** (consuming 1 bone). 

- **Posture Preservation**: Seated wolves remain sitting in a stationary sentry posture at their guard post without breaking posture.
- **Personality Patrol Styles**:
  - **Aggressive**: Active polygon perimeter sweep patrolling up to 8 blocks around the post.
  - **Normal**: Radial sweeps up to 6 blocks, returning to the center.
  - **Pacifist**: Tight orbital circular sweeps within 4 blocks.
- **Watchdog Alarms & Threat Pointing**: When hostile mobs approach a Pacifist guard, the dog stands, freezes, looks directly at the closest monster, emits directional red dust particles, and growls according to its unique sound variant.

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/particle_aggressive.png" alt="Aggressive Guard Particle" width="30%">&nbsp;<img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/particle_pacifist.png" alt="Pacifist Guard Particle" width="30%">&nbsp;<img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/particle_classic.png" alt="Classic/Normal Guard Particle" width="30%">
</p>

---

### 📄 Wolf Ownership Transfer (Adoption)
Transfer ownership of your tamed companion to another player safely and seamlessly:

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/wolve_is_ready_for_adoption_image.png" alt="Wolf Ready for Adoption" width="85%">
  <br><br>
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/adoption_called_off_image.png" alt="Adoption Called Off" width="85%">
</p>

1. **Initiate Adoption**: Shift-Right-Click your tamed wolf with **Paper**. The wolf sits down, clears aggression, and emits a sparkling **Rose Pink** particle trail.
2. **Claiming**: Any non-owner player can right-click the adoptable wolf with an empty main hand to claim them as their new companion.
3. **Safety Protection**: Taking any damage or a normal right-click by the original owner immediately cancels the adoption state.

---

### 🦯 Select-and-Ride Command System
Command your tamed wolves to ride vehicles, mounts, boats, minecarts, or modded chairs with accurate sitting poses:

<p align="center">
  <strong>🎬 Video Tutorial: Select-and-Ride Command System</strong><br>
  <em>Click the thumbnail or button below to watch the feature showcase on YouTube</em><br><br>
  <iframe width="560" height="315" src="https://www.youtube-nocookie.com/embed/pv7-6xaip-Y" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe><br><br>
  <a href="https://youtu.be/pv7-6xaip-Y" target="_blank" rel="noopener">
    <img src="https://img.shields.io/badge/▶️_Watch_Video-Play_on_YouTube-FF0000?style=for-the-badge&logo=youtube&logoColor=white" alt="▶️ Play Video on YouTube">
  </a>
</p>

- **Selection**: Shift-Right-Click your tamed wolf with a standard **Stick**, **Blaze Rod**, or **Breeze Rod** to select them (plays a chime and emits a golden particle ring).
- **Mount Target**: Right-click any vehicle (Boat, Minecart, Horse, Camel, Donkey, Mule, Skeleton Horse, or modded Chair/Stair) to direct the wolf into the seat.
- **Natural Sitting Pose**: Wolves maintain a natural, upright sitting pose while riding vehicles instead of floating or standing awkwardly.

---

### 📏 Dynamic Size & Attribute Scaling
Every wolf features continuous, genetics-driven body scaling (70% to 145%) utilizing Minecraft's native `Attributes.SCALE`:

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/the%20scale%20image.webp" alt="Dynamic Size & Attribute Scaling" width="85%">
</p>

- **Physical & Stat Correlation**: Physical size scales health pools and attack damage proportionally.
- **Unique Sound Variant Lottery**: Scale, genetics, and personality deterministically seed a unique audio sound variant (Classic, Big, Cute, Puglin, Angry, Grumpy, Sad) with individual pitch offsets.

---

### 🤝 Advanced Social AI & Pack Genetics
- **Multi-Puppy Litters**: Breeding can produce litters of 1 to 4 puppies, each rolling independent personality traits, mutations, and scale factors.
- **Adult Disciplinary Correction**: Aggressive adult pack members gently discipline mischievous puppies to prevent chaotic death loops.
- **Food Quirks**: Dogs possess individual dietary traits — enthusiastic "Hoovers" eagerly scavenge dropped food from the ground, while selective lineages refuse ground scraps.
- **Social Bonding & Affinity**: Packmates build mutual affinity over time, preventing accidental infighting and suppressing blood feud retaliation.

---

### 🛡️ Smart Survival AI
- **Cliff Safety**: Wolves detect fatal falls (>3 blocks) and ledge edges, halting dangerous chases.
- **Hazard Avoidance**: Intelligently paths around lava, fire, drowning hazards, and **Magma Blocks**.
- **Wolf-on-Wolf Collision Push Safety**: When colliding with a packmate perched near a cliff or hazard, wolves immediately halt navigation to avoid pushing them into danger.
- **Fire Survival AI**: Burning wolves break their sitting posture in an emergency and sprint at 1.4x speed toward the nearest water body within 16 blocks to extinguish themselves.
- **Creeper Blast Evasion**: Tamed dogs detect swelling creepers and sprint radially away at 1.5x speed.
- **Scaled Whimpering**: Low-health whimpering dynamically scales to `< 50% max HP` (preventing healthy runts from constant crying).

---

### 🏰 Dynamic Territoriality & Wild Pack Wars
Wild wolf packs roam as organized units led by an Alpha leader:
- **Territorial Encounters**: When rival packs meet, leaders negotiate outcomes based on personality:
  - **Aggressive vs. Aggressive**: Triggers an intense 1v1 Leader Duel.
  - **Aggressive vs. Pacifist / Normal**: Forces submissive pack mergers or peaceful retreats.
  - **Pacifist vs. Pacifist**: Both packs peacefully retreat in opposite directions.

---

### 🌕 Restored Howling Soundscapes & Zoomies
- **Pack-Wide Howling**: Under a full moon, pack leaders initiate howling choruses using restored classic wolf howl sound files.
- **Morning & Rain Zoomies**: Happy, healthy dogs occasionally burst into playful high-speed sprints in the morning or during fresh rainfall.

---

### 🔍 Jade Mod HUD Integration
- **Dynamic Numeric Health**: Displays exact numeric health values (`HP: X / Y`) and hearts.
- **Inbred Status Tag**: Displays genetic lineage and inbred warnings in tooltips.
- **Favorite Treat Discovery**: Identifies your dog's secret favorite treat once fed.

---

### 💻 Server-Side Optional & Client Gating
- **Server-Side Optional**: Fully functional on dedicated servers even if connecting clients do not have the mod installed!
- **In-Game GUI Config**: Seamlessly integrates with **ModMenu** + **Cloth Config** / **YACL v3**.

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/Cloth%20config%20sreen.webp" alt="Cloth Config Screen" width="85%">
</p>

---

## 📊 Quick Reference & Mechanics Matrix

### 🐺 Personality Comparison Table

| Personality | Base HP Offset | Speed Modifier | Damage Modifier | Follow Distance | Low-HP Flee Chance | Patrol Pattern | Patrol Particles |
| :--- | :---: | :---: | :---: | :---: | :---: | :--- | :--- |
| **Aggressive** 💢 | `-10 HP` | `+15%` | `-15%` | `10 blocks` | `10%` | Perimeter Sweep (8m) | Pure Red (`0xFF3333`) |
| **Pacifist** ❤️ | `+20 HP` | `-10%` | `+15%` | `6 blocks` | `100%` | Orbital Sweep (4m) | Teal / Green (`0x00FF88`) |
| **Normal** ✨ | `+0 HP` | `+0%` | `+0%` | `10 blocks` | `50%` | Radial Sentry (6m) | Golden Yellow (`0xFFD700`) |

### 🧬 Breeding Genetics Inheritance Matrix

| Parent Personalities | Primary Offspring Outcome | Secondary Mutation Outcome |
| :--- | :--- | :--- |
| **Same Traits** (e.g. Aggro + Aggro) | `80%` Inherits Parent Personality | `10%` Mutates to alternative personality |
| **Normal + Non-Normal** | `40%` Dominant Normal | `40%` Recessive Non-Normal |
| **Aggressive + Pacifist** | `50%` Dilutes into Normal | `25%` Mutates to Aggressive / Pacifist |

---

## ⌨️ In-Game Commands

All commands are registered under `/betterdogs` (alias `/bd`) and support rich tab-completion:

| Command | Permission | Description |
| :--- | :---: | :--- |
| `/betterdogs help` | All | Displays the in-game command reference handbook. |
| `/betterdogs status` | All | Displays current mod configuration and core active parameters. |
| `/betterdogs get <rule>` | All | Reads the live value of any namespaced GameRule. |
| `/betterdogs set <rule> <value>` | OP (Level 2) | Updates the live value of a GameRule in real-time. |
| `/betterdogs reset` | OP (Level 2) | Restores all Better Dogs GameRules to factory defaults. |
| `/betterdogs reload` | OP (Level 2) | Reloads configuration files from disk. |
| `/betterdogs debug action <entity> <action>` | OP (Level 2) | Triggers actions (`howl`, `zoomies`, `mischief`, `play_fight`, `retaliation`). |
| `/betterdogs debug personality <entity> <type>` | OP (Level 2) | Forces a wolf's personality (`aggressive`, `pacifist`, `normal`). |
| `/betterdogs debug territory <entity>` | OP (Level 2) | Tests wild territorial pack negotiations. |

---

## ⚙️ Native GameRules & Configuration

> [!IMPORTANT]
> **💡 Config vs. In-Game GameRules:** The global configuration file only defines default values for newly created worlds. In existing worlds, change settings in-game via the **Edit Game Rules** UI screen or the `/gamerule` command.

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/Gamerule%20Screen%20options.webp" alt="Native GameRules UI" width="85%">
</p>

### Key GameRules Overview

| Category | GameRule Identifier | Default | Type | Description |
| :--- | :--- | :---: | :---: | :--- |
| **Survival** | `betterdogs:bd_cliff_safety` | `true` | Boolean | Prevents wolves from walking off fatal cliffs (>3 blocks). |
| **Survival** | `betterdogs:bd_wolves_seek_water_on_fire` | `true` | Boolean | Burning wolves sprint to water within 16 blocks. |
| **Survival** | `betterdogs:bd_creeper_awareness` | `true` | Boolean | Tamed dogs evade hissing creepers. |
| **Survival** | `betterdogs:bd_friendly_fire_protection` | `true` | Boolean | Protects tamed wolves from accidental owner hits. |
| **Behavior** | `betterdogs:bd_storm_anxiety` | `true` | Boolean | Enables thunderstorm trembling and whimpering. |
| **Behavior** | `betterdogs:bd_zoomies_enabled` | `true` | Boolean | Enables morning and rainfall sprint zoomies. |
| **Behavior** | `betterdogs:bd_fetch_enabled` | `true` | Boolean | Enables stick fetch games. |
| **Guard** | `betterdogs:bd_guard_patrol_range_aggressive` | `8` | Integer | Patrol radius for Aggressive guard dogs (blocks). |
| **Guard** | `betterdogs:bd_guard_patrol_range_normal` | `6` | Integer | Patrol radius for Normal guard dogs (blocks). |
| **Guard** | `betterdogs:bd_guard_patrol_range_pacifist` | `4` | Integer | Patrol radius for Pacifist guard dogs (blocks). |
| **Genetics** | `betterdogs:bd_wolf_litter_max_size` | `4` | Integer | Maximum puppy count in a single litter (1–4). |
| **Scale** | `betterdogs:bd_wolf_min_scale_percent` | `70` | Integer | Minimum scale percentage (70%). |
| **Scale** | `betterdogs:bd_wolf_max_scale_percent` | `145` | Integer | Maximum scale percentage (145%). |

---

## 📖 In-Depth How-To & Gameplay Playbook

Master every interaction in Better Dogs with this comprehensive operational playbook:

### 1. Taming & Discovering Personality
- Feed bones to any wild wolf. Upon taming, inspect the floating particle color (Red = Aggressive, Green/Teal = Pacifist, Gold = Normal) or check their Jade HUD tooltip.

### 2. Shift+Right-Click Gesture Playbook
- **Bone + Shift-Right-Click**: Toggles **Guard Mode** (consumes 1 bone). The dog anchors to the spot. Seated dogs remain sitting as sentries.
- **Paper + Shift-Right-Click**: Puts your dog up for **Adoption** (sparkling Rose Pink trail). Any non-owner player can right-click with an empty hand to claim them.
- **Stick / Blaze Rod / Breeze Rod + Shift-Right-Click**: **Selects** the dog (golden particle ring). Then right-click any vehicle (Boat, Minecart, Horse, Camel, Chair) to command the dog to mount with a seated pose.

### 3. Selective Breeding & Litter Genetics
- Feed meat to two tamed wolves. Litters produce 1–4 puppies. Puppies inherit parental scale and personality with a small chance of mutation. Outcrossing prevents inbreeding debuffs.

### 4. Reading Audio Cues & Whining
- Whimpering only triggers when health falls below **50% of the dog's maximum HP**. Feed meat to restore health.
- Under a full moon, enjoy the ambient chorus of nocturnal pack howling.

### 5. In-Game Configuration
- Open **World Settings &rarr; Edit Game Rules** to browse all Better Dogs settings in a dedicated folder, or use `/betterdogs set <rule> <value>` in-game.

---

## ☕ Support

<p align="center">
  <a href="https://ko-fi.com/dasikigaijin/tip"><img src="https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white" alt="Ko-fi"></a>
  <a href="https://sociabuzz.com/dasikigaijin/tribe"><img src="https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge" alt="SocioBuzz"></a>
  <a href="https://saweria.co/DasikIgaijinn"><img src="https://img.shields.io/badge/Saweria-Local_Support-FFA500?style=for-the-badge" alt="Saweria"></a>
</p>

> [!NOTE]
> **🇮🇩 Indonesian Users:** SocioBuzz and Saweria support local payment methods (Gopay, OVO, Dana, etc.) if you want to support me without using PayPal/Ko-fi!

---

## 📜 Credits & Permissions

| Role | Author |
| :--- | :--- |
| **Creator** | **Dasik** (Rifaditya) |
| **Collection** | Vanilla Outsider |
| **License** | GNU GPLv3 |

> [!IMPORTANT]
> **📦 Modpack Permissions & Distribution:**<br>
> You are free to include this mod in any modpack on any platform. However, the mod itself must be downloaded from its official distribution pages on **Modrinth** or **CurseForge**. Re-uploading or redistributing the mod jar file to third-party sites is strictly prohibited unless explicitly permitted by the creator.
> <br><br>
> **License & Forks:**<br>
> Since the source code is licensed under **GNU GPLv3**, you are fully permitted to fork the repository, make modifications, build your own versions, and distribute them under the terms of the GPLv3.

---

<p align="center">
  <strong>Made with ❤️ for the Minecraft community</strong><br>
  <em>Part of the Vanilla Outsider Collection</em>
</p>
