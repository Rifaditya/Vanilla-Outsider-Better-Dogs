<div align="center">

![banner AI generated](https://cdn.modrinth.com/data/cached_images/1909cfcc36754c4a370e00e17f1d8ebe7d190405_0.webp)

</div>
<p align="center">
    <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <img src="https://img.shields.io/badge/Loader-NeoForge-orange?style=for-the-badge" alt="NeoForge Support">
    <img src="https://img.shields.io/badge/Config-Standalone-orange?style=for-the-badge" alt="Standalone Config">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
</p>

# 🐕 Make Wolves Worthy Companions (Multi-Loader: Fabric & NeoForge)

**No Backports:** I will **NOT** backport this mod to older versions (1.21, 1.20, etc.). Please do not ask.

Every Minecraft player knows the pain: you spend hours finding a wolf, tame it, and five minutes later it jumps into lava or walks off a cliff.

**Vanilla Outsider: Better Dogs** completely overhauls wolf AI to make them smarter, safer, and livelier. Now natively supporting both **Fabric** and **NeoForge**, every dog feels unique with the **Personality System**.

---

## ✨ Key Features

### 🧠 Unique Personality System

When you tame a wolf, it develops one of three permanent personalities, visually distinct by their particle effects. As of **v1.6.0**, you can even configure the chances of each personality appearing!

<p align="center">
  <img src="https://cdn.modrinth.com/data/cached_images/bcda6a78fa3159bc710566a2cbedfa1e94a03930.png" alt="Aggressive dog particle" width="30%">
  <img src="https://cdn.modrinth.com/data/cached_images/6d8c999c94d61346d925ecadd1f2655ef412bb0d.png" alt="Pacifist all love particle" width="30%">
  <img src="https://cdn.modrinth.com/data/cached_images/8722821ac9f7e1b6ca653e77cf90cf83dff2c432.png" alt="Vanilla/Normal" width="30%">
</p>

| Icon | Personality | Default Chance | Behavior |
| :---: | :--- | :--- | :--- |
| 💢 | **Aggressive** | **20%** | The Guardian. Proactively attacks hostile mobs near you. Now with configurable detection ranges! |
| ❤️ | **Pacifist** | **20%** | The Loyal Friend. Only attacks mobs that hurt *you* first. Won't start fights, keeping itself safe. |
| ✨ | **Normal** | **60%** | The Classic. Custom speeds and damage can now be applied to normal wolves via config. |

> [!TIP]
> **Untrained Baby Nature (v1.8.3):** Tamed baby wolves are more curious and independent. They wander further (2x) and teleport less frequently by default, giving them a more "wild" feel until they grow up. These multipliers are fully configurable!
>
> [!NOTE]
> **Breeding & Inheritance:** Personalities are permanent and can be inherited via breeding! Note that **collar colors** follow vanilla mechanics (inherited from parents) and **do not** indicate personality types—always look for the unique particle effects!

### 🎁 The Gift System (New in v1.5.0+)

Like cats, your loyal dogs will now bring you treasures based on their personality!

* **Aggressive**: Brings "trophies" from their hunts (Bones, Rotten Flesh, Arrows, rarely Iron).
* **Pacifist**: Brings "foraged" items from nature (Berries, Seeds, Flowers, rarely Glow Berries).
* *Configurable cooldowns and trigger chances in the settings.*

### 🐕 Baby Wolf Behavior System (New v1.8.x)

Baby wolves now have their own unique behavior rules:

* **Passive by Default (v1.8.1):** Baby wolves won't attack what the owner attacks or defend the owner unless they have an Aggressive personality or are directly hit.
* **Domestic Retaliation (v1.8.2):** If you accidentally hit your puppy, it will retaliate with **2 strikes** then forgive you!
* **Adult Intervention (v1.8.2):** Aggressive adults will discipline a baby that attacks its owner (3-strike sequence).
* **Puppy Protection (v1.8.2):** Other wolves (wild or tamed) won't target tamed baby wolves.
* **Untrained Nature (v1.8.3):** Babies wander further (2x) and teleport less frequently until grown up.

### 🛡️ Smart Safety AI (v1.8.4+)

Stop worrying about your dogs killing themselves:

* **Baby Curiosity:** Passive/Normal babies investigate interesting blocks and stare at nearby entities.
* **Reckless Aggression:** Aggressive babies engage hostiles immediately regardless of owner distance.
* **Cliff Safety V2:** Wolves detect airborne targets and **STOP** chasing instead of jumping after them.
* **Lava & Fire Avoidance:** Wolves actively pathfind around dangerous blocks.
* **Creeper Awareness:** Wolves flee when they hear a Creeper hiss!
* **Anti-Stuck:** Improved pathfinding to keep them following close.

### 🌩️ Environmental Awareness

* **Storm Anxiety:** Wolves are scared of Thunderstorms! They will whine and shake in fear.
* **Behavior**: If sitting, they stay put. If standing, they pace anxiously.
* *Intensity and trigger chances are fully adjustable in the config.*

---

## ⚙️ Configuration (Standalone & Built-in)

**Updated for v1.7.6+**: We've removed the heavy dependencies on `Cloth Config` and `AutoConfig` in favor of a lightweight, **standalone JSON loader**.

* **No Dependencies Required:** Works out of the box on snapshots where other config libraries might be broken.
* **Granular Per-Personality Stats**: Set unique Health, Speed, and Damage for Aggressive, Pacifist, and Normal wolves.
* **Custom AI Ranges**: Adjust exactly how far wolves follow you, when they teleport, and how far they chase enemies.
* **Breeding Odds**: Control the genetic pass-through and spawn rates of personalities.
* **Wild Hunt Logic**: Configure wild wolves to only hunt when they are hungry (HP based).

---

## 📦 Installation

1. Download the latest **Minecraft 26.1 Snapshot**.
2. **Fabric Users:**
   * Install **[Fabric Loader](https://fabricmc.net/)**.
   * Install **[Fabric API](https://modrinth.com/mod/fabric-api)**.
3. **NeoForge Users:**
   * Install the latest **NeoForge** for 26.1.
4. Place the `Vanilla-Outsider-Better-Dogs.jar` in your `mods` folder.

---

## 🧩 Compatibility

| Feature | Fabric (1.21.11 & 26.1+) |
| :--- | :---: |
| Multiplayer | ✅ |
| Mob Mods | ✅ |
| Wolf Visual Mods | ✅ |
| New Trims | ✅ |

---

## ☕ Support the Development

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/dasikigaijin/tip)
[![SocioBuzz](https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge)](https://sociabuzz.com/dasikigaijin/tribe)

> [!NOTE]
> **Indonesian Users:** SocioBuzz supports local payment methods (Gopay, OVO, Dana, etc.) if you want to support me without using PayPal/Ko-fi!

---

## 📜 Legal & Credits

| Role | Author |
| :--- | :--- |
| **Creator** | DasikIgaijin |
| **Collection** | Vanilla Outsider |
| **License** | GNU GPLv3 |

---

> [!IMPORTANT]
> This mod is part of the **Vanilla Outsider** collection. You are free to use it in modpacks, videos, and servers.
>
> > [!IMPORTANT]
> > **Modpack Permissions:** You are free to include this mod in modpacks, **provided the modpack is hosted on the same platform** (e.g. Modrinth).
> >
> > **Cross-platform distribution is not permitted.** If you download this mod from Modrinth, your modpack must also be published on Modrinth.

---

<div align="center">

**Made with ❤️ for the Minecraft community**

*Part of the Vanilla Outsider Collection*

</div>
