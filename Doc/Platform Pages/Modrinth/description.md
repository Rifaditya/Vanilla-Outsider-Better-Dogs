<div align="center">

<img src="https://cdn.modrinth.com/data/cached_images/1909cfcc36754c4a370e00e17f1d8ebe7d190405_0.webp" alt="Better Dogs Banner">

</div>
<p align="center">
    <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <img src="https://img.shields.io/badge/Language-Java_25-orange?style=for-the-badge&logo=java" alt="Java 25">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
    <img src="https://img.shields.io/badge/Minecraft-26.1+-brightgreen?style=for-the-badge" alt="Minecraft 26.1+">
</p>

# 🐕 Better Dogs

**No Backports:** This mod targets **Minecraft 26.1+**. Older versions are unsupported.

> **Make Wolves Worthy Companions. Smarter, Safer, Livelier.**

Every Minecraft player knows the pain: you spend hours finding a wolf, tame it, and five minutes later it jumps into lava or walks off a cliff. **Better Dogs** overhauls wolf AI to make them effective partners. Powered by a high-performance **Event-Driven AI Scheduler**, they act efficiently without sacrificing performance.

Part of the **Vanilla Outsider Collection** — mods that refine the vanilla experience with modern standards.

---

## ✨ Features

### 🧠 Personality Intelligence
When tamed, wolves develop one of three permanent personalities, visible via custom particles:

<p align="center">
  <img src="https://cdn.modrinth.com/data/cached_images/bcda6a78fa3159bc710566a2cbedfa1e94a03930.png" alt="Aggressive dog particle" width="30%">
  <img src="https://cdn.modrinth.com/data/cached_images/6d8c999c94d61346d925ecadd1f2655ef412bb0d.png" alt="Pacifist all love particle" width="30%">
  <img src="https://cdn.modrinth.com/data/cached_images/8722821ac9f7e1b6ca653e77cf90cf83dff2c432.png" alt="Vanilla/Normal" width="30%">
</p>

- 💢 **Aggressive**: The Guardian. Proactively attacks hostile mobs and scouts ahead.
- ❤️ **Pacifist**: The Healer. Avoids combat unless you are hurt. High health, low damage.
- ✨ **Normal**: The Classic. Balanced stats and standard vanilla-plus behavior.

### 🤝 Advanced Social AI
- **Social Bonding (Affinity)**: Dogs form relationships within their pack. Socializing builds trust and reduces accidental infighting.
- **Adult Correction**: Aggressive adults discipline misbehaving puppies, preventing death loops.
- **Pack Genetics**: Puppies inherit personality traits and stats from their parents.
- **Litter System**: Wolves can produce multiple puppies in a single breed — each with independent personality rolls and stats.

### 🛡️ Smart Survival AI
- **Cliff Safety**: Wolves detect fatal drops and airborne targets, stopping dangerous chases.
- **Hazard Awareness**: Improved pathfinding around lava, fire, and drowning hazards.
- **Creeper Awareness**: Wolves flee from hissing Creepers!
- **Dog Feeding**: Tamed dogs scavenge dropped raw or cooked food from the ground to restore health — no manual feeding required.

### 📡 Behavioral Specialization
- **Scouting**: Aggressive dogs proactively range ahead to clear your path.
- **Silent Alarm**: Pacifist dogs emit a high-pitched whine when they detect nearby monsters.
- **Gift System**: Loyal dogs bring you treasures based on their personality.
- **Debug Tools**: Use `/betterdogs debug` to test pack interactions and behaviors (Locked behind OP permissions and a safety GameRule).

### 🌤️ Immersive Events
- **Zoomies**: Dogs burst into hyperactive sprints in the morning or when it starts raining — pure joy!
- **Group Howl**: Under a full moon, wolves trigger pack-wide howling sessions that spread to nearby pack members.
- **Storm Anxiety**: Thunderstorms make dogs anxious — they whine, tremble, and pace nervously until the storm passes.

### 🏰 Wild Wolf Territoriality
Wild wolf packs are now dynamic, territorial entities led by a dominant leader:
- **Territorial Probability Matrix**: Pack interactions now utilize a complex, personality-driven matrix. No two disputes are the same!
- **Dynamic Outcomes**: Depending on leader personalities, packs may **Merge** (Dominance based), engage in **War** (1v1 Duels or Pack Combat), or peacefully **Retreat** to maintain their own borders.
- **Configurable Instincts**: Use GameRules to fine-tune exactly how Aggressive, Normal, and Pacifist leaders react to each other.
- **1v1 Leader Duels**: If a dispute escalates to War, leaders engage in a cinematic 1v1 duel to settle dominance.
- **Yield & Merge**: Defeated packs are not lost; they yield and **merge** with the winning pack, allowing for the natural formation of massive wolf colonies.
- **Wild Personality AI (New Default)**: Out of the box, wild pack members exhibit unique behaviors (like Aggressive hunting or Pack Retreats) while anchored to their leader.
- **Dynamic Spawning & Reinforcements**: When a pack goes to war or is challenged, nearby wild wolves can spawn or join as reinforcements to defend their pack's territory.
- **Production Stable**: Fully compatible with high-performance engines like **C2ME**, ensuring safe multi-threaded AI execution.
- **Performance Hardened**: All AI logic is performance-optimized using **DasikLibrary 1.7.0**, ensuring zero console spam and smooth server TPS even with massive packs.

---

## ⚙️ Configuration (Native Game Rules)

No messy config files. Better Dogs uses the **Native Minecraft Game Rules** system. All 50+ mod parameters are grouped into a dedicated **"Better Dogs"** category in the official UI.

> [!TIP]
> **Too many rules?** If the game rule screen feels cluttered, we highly recommend installing **[Collapsible Game Rules](https://modrinth.com/mod/collapsible-gamerules)**. It will automatically group the new Personality Matrix settings into clean, expandable folders!

<p align="center">
<img src="https://cdn.modrinth.com/data/e7H8SUmG/images/0a47c04889b8829f5b5bbead3a7847cb63d69a3b.png" alt="Native gamerule UI">
</p>

---

## ☕ Support

If you enjoy the **Vanilla Outsider** collection, consider fueling the next update!

<p align="center">

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/dasikigaijin/tip)
[![SocioBuzz](https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge)](https://sociabuzz.com/dasikigaijin/tribe)
[![Saweria](https://img.shields.io/badge/Saweria-Local_Support-FFA500?style=for-the-badge)](https://saweria.co/DasikIgaijinn)

</p>

> [!NOTE]
> **Indonesian Users:** SocioBuzz and Saweria support local payment methods (Gopay, OVO, Dana, etc.) if you want to support me without using PayPal/Ko-fi!

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
