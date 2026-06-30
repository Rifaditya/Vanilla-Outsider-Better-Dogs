<p align="center">
    <a href="https://www.curseforge.com/minecraft/mc-mods/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <img src="https://img.shields.io/badge/Language-Java_25-orange?style=for-the-badge&logo=java" alt="Java 25">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
    <img src="https://img.shields.io/badge/Minecraft-26.1.2%20%2F%2026.2+-brightgreen?style=for-the-badge" alt="Minecraft 26.1.2 / 26.2+">
</p>

# 🐕 Better Dogs

### 🎮 Version Compatibility & Parity

This mod is active and fully supported across both major version streams:
* **Minecraft 26.2+**: Current public release — **`v4.5.18`**
* **Minecraft 26.1.2**: Current public release — **`v3.7.1`**

While both versions receive ongoing support and bug fixes, **Minecraft 26.2+ contains additional advanced features** (such as Pack Genetics, the Litter System, and the dynamic Gifting System) that are currently unavailable in the 26.1.2 release.

<blockquote><strong>Make Wolves Worthy Companions. Smarter, Safer, Livelier.</strong></blockquote>

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

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/wolve_is_ready_for_adoption_image.png" alt="Wolf is Ready for Adoption" width="85%">
  <br><br>
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/adoption_called_off_image.png" alt="Adoption Called Off" width="85%">
</p>

Easily transfer ownership of your tamed wolves to other players using standard, vanilla-only **Paper**:
- **Pending Adoption State**: Shift + Right-Click your tamed wolf with a sheet of Paper to put them up for adoption. The wolf will sit down, halt pathfinding, clear all aggression, and emit a beautiful, sparkling trail of **Rose Pink** particles.
- **Claiming the Wolf**: Any non-owner player can right-click the adoptable wolf with an empty main hand to claim them as their new companion, instantly triggering heart particles and mutual overlay notifications.
- **Safety Checks**: The adoption state is automatically cancelled if the wolf takes any damage, or if the owner simply right-clicks the dog again normally to cancel, preventing accidental claims.

> ⚠️ **Notice**: This feature is **unavailable in the 26.1.2 version**, and is **currently bugged in the 26.2+ (v4.5.18) version** (adopted wolves will still pathfind and follow their original owner's dogs, retain guard posts, and keep old grudges). This is fully fixed and backported in the upcoming `v4.9.8-26.2` and `v3.10.15-26.1.2` updates.

### 📏 Dynamic Follower Spread Scaling
Follow/spread spacing of wild and tamed wolf packs scales dynamically based on the number of active followers:
- Spacing increases mathematically based on the square root formula: `f(N) = multiplier * sqrt(N - 1)` to prevent visual overcrowding.
- Fully configurable via 4 native GameRules: `bd_tamed_pack_spread_multiplier`, `bd_tamed_pack_spread_max`, `bd_wild_pack_spread_multiplier`, `bd_wild_pack_spread_max`.

### 📏 Dynamic Size & Attribute Scaling (Minecraft 26.2+)
In the 26.2+ release stream, I integrated wolf physical size directly with their genetics and health using Minecraft's native `Attributes.SCALE` attribute:
- **Health-Based Sizing**: A wolf's physical size scales dynamically based on its rolled max health. Sizes range from a tiny **0.808x** (for weaker/smaller wolves) up to a massive **1.312x** (for high-health champions).
- **Physical Collision & Eye Height**: Because this utilizes Minecraft's native attributes, the scale changes apply to their actual server-side collision hitboxes, eye heights, passenger offsets, step heights, and interaction ranges out of the box.
- **UUID Seeded Stats**: Max Health, Attack Damage, and Movement Speed are rolled deterministically using the wolf's unique UUID, ensuring stats remain persistent and stable across game reloads.

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/the%20scale%20image.webp" alt="Genetics-Based Size Scaling" width="85%">
</p>

### 🤝 Advanced Social AI
- **Social Bonding (Affinity)**: Dogs form relationships within their pack. Socializing builds trust and reduces accidental infighting.
- **Adult Correction**: Aggressive adults discipline misbehaving puppies, preventing death loops.
- **Pack Genetics**: Puppies inherit personality traits and stats from their parents. *(Feature unavailable in 26.1.2)*
- **Litter System**: Wolves can produce multiple puppies in a single breed — each with independent personality rolls and stats. *(Feature unavailable in 26.1.2)*
- **Inbreeding Penalties & Kinship Tracking**: Breeding closely related wolves (parent-child or sibling-sibling) triggers a severe genetic penalty, producing a tiny, weak, slow, and fragile "inbred runt." Parent UUIDs are tracked to prevent accidental inbreeding.
- **Genetic Recovery & Curing**:
  - **Outcrossing Recovery**: Breeding an inbred runt parent with an unrelated, healthy wolf will produce healthy offspring that inherit recovered baseline stats instead of the parent's stunted/penalized values.
  - **Golden Apple Cure**: Feeding an inbred runt a Golden Apple cures its genetic status, restoring normal size and attribute multipliers. (Configurable via gamerule).

<blockquote>⚠️ <strong>Inbreeding Note:</strong> Inbreeding penalties, outcrossing, and curing systems are only active in the 26.2+ release stream (v4.5.0+). I recommend keeping your breeding lineages clean to avoid stunting your dogs!</blockquote>

### 🛡️ Smart Survival AI
- **Cliff Safety**: Wolves detect fatal drops and airborne targets, stopping dangerous chases.
- **Hazard Awareness**: Improved pathfinding around lava, fire, and drowning hazards.
- **Creeper Awareness**: Wolves flee from hissing Creepers!
- **Dog Feeding**: Tamed dogs scavenge dropped raw or cooked food from the ground to restore health — no manual feeding required.

### 📡 Behavioral Specialization
- **Scouting**: Aggressive dogs proactively range ahead to clear your path.
- **Silent Alarm**: Pacifist dogs emit a high-pitched whine and freeze to point toward nearby monsters.
- **Gift System**: Loyal dogs bring you treasures based on their personality.
- **Debug Tools**: Use `/betterdogs debug` to test pack interactions and behaviors (Locked behind OP permissions and a safety GameRule).

### 🌤️ Immersive Events
- **Zoomies**: Dogs burst into hyperactive sprints in the morning or when it starts raining — pure joy!
- **Group Howl**: Under a full moon, wolves trigger pack-wide howling sessions that spread to nearby pack members.
- **Storm Anxiety**: Thunderstorms make dogs anxious — they whine, tremble, and pace nervously until the storm passes. This is highly dependent on their personality: Pacifist dogs are extremely prone to anxiety, Normal dogs have standard chances, and Aggressive dogs are completely immune.

### 🏆 Custom Advancement System (Minecraft 26.2+)
I added a collection of custom advancements to reward you for exploring all the new mechanics in the 4.5.x updates:
- 🐕 **A Pack of Personalities**: Tame one of each wolf personality type (Normal, Aggressive, and Pacifist).

<p align="center">
  <img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/a%20pack%20of%20personality%20advancement%20image.webp" alt="A Pack of Personalities Advancement" width="85%">
</p>

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
  - ⚡ **AI Query Throttling**: Heavy spatial search queries (such as scanning a 96-block radius for neighboring wild leaders in territorial goals, or tracking active follower counts in follow owner goals) are optimized. The AI uses cooperative caching (`FollowerSpacingCache`), dynamic-radius search scaling, and randomized execution cooldowns (2–4 seconds / 40–80 ticks) to completely prevent TPS spikes.

---

## ⚙️ Configuration (Native Game Rules)

No messy config files. Better Dogs uses the **Native Minecraft Game Rules** system. All 50+ mod parameters are grouped into a dedicated **"Better Dogs"** category in the official UI.

<blockquote>💡 <strong>Too many rules?</strong> If the game rule screen feels cluttered, we highly recommend installing <a href="https://www.curseforge.com/minecraft/mc-mods/collapsible-gamerules">Collapsible Game Rules</a>. It will automatically group the new Personality Matrix settings into clean, expandable folders!</blockquote>

<p align="center">
<img src="https://raw.githubusercontent.com/Rifaditya/Vanilla-Outsider-Better-Dogs/26.2-core-alignment/Doc/Media/Gallery/Gamerule%20Screen%20options.webp" alt="Native gamerule UI">
</p>

---

## ☕ Support

If you enjoy the **Vanilla Outsider** collection, consider fueling the next update!

<p align="center">

<a href="https://ko-fi.com/dasikigaijin/tip"><img src="https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white" alt="Ko-fi"></a>
<a href="https://sociabuzz.com/dasikigaijin/tribe"><img src="https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge" alt="SocioBuzz"></a>
<a href="https://saweria.co/DasikIgaijinn"><img src="https://img.shields.io/badge/Saweria-Local_Support-FFA500?style=for-the-badge" alt="Saweria"></a>

</p>

<blockquote><strong>🇮🇩 Indonesian Users:</strong> SocioBuzz and Saweria support local payment methods (Gopay, OVO, Dana, etc.) if you want to support me without using PayPal/Ko-fi!</blockquote>

---

## 📦 Modpack Permissions

<blockquote><strong>Modpack Distribution Policy:</strong><br>
I grant permission to include this mod in any modpack, provided the modpack is hosted and distributed exclusively on the same platform (CurseForge). Redistribution of this mod or its compiled binaries on other platforms or third-party hosting sites without my explicit consent is strictly prohibited.</blockquote>

---

## 📜 Credits

| Role | Author |
| :--- | :--- |
| **Creator** | **Dasik** (Rifaditya) |
| **Collection** | Vanilla Outsider |
| **License** | GPLv3 |

---

<div align="center">

**Made with ❤️ for the Minecraft community**

*Part of the Vanilla Outsider Collection*

</div>
