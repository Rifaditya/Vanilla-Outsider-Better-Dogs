<div align="center">

![banner AI generated](https://cdn.modrinth.com/data/cached_images/1909cfcc36754c4a370e00e17f1d8ebe7d190405_0.webp)

</div>
<p align="center">
    <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <a href="https://modrinth.com/mod/fabric-language-kotlin"><img src="https://img.shields.io/badge/Language-Kotlin-purple?style=for-the-badge&logo=kotlin" alt="Kotlin"></a>
    <a href="https://modrinth.com/mod/cloth-config"><img src="https://img.shields.io/badge/Config-Cloth_Config-orange?style=for-the-badge" alt="Cloth Config"></a>
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
</p>

# 🐕 Make Wolves Worthy Companions (Now for 26.1 Snapshots!)

**No Backports:** I will **NOT** backport this mod to older versions (1.21, 1.20, etc.). Please do not ask.

Every Minecraft player knows the pain: you spend hours finding a wolf, tame it, and five minutes later it jumps into lava or walks off a cliff.

**Vanilla Outsider: Better Dogs** completely overhauls wolf AI to make them smarter, safer, and livelier. With the new **Personality System**, every dog feels unique—some are aggressive protectors, while others are loyal pacifists.

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

> [!NOTE]
> Personalities are permanent and can be inherited via breeding! Use the config to adjust how common each type is in your world.

### 🎁 The Gift System (New in v1.5.0+)

Like cats, your loyal dogs will now bring you treasures based on their personality!

* **Aggressive**: Brings "trophies" from their hunts (Bones, Rotten Flesh, Arrows, rarely Iron).
* **Pacifist**: Brings "foraged" items from nature (Berries, Seeds, Flowers, rarely Glow Berries).
* *Configurable cooldowns and trigger chances in the settings.*

### 🛡️ Smart Safety AI

Stop worrying about your dogs killing themselves. Better Dogs includes high-level self-preservation:

* **Cliff Safety V2:** Wolves detect "Airborne Targets". If an enemy is knocked back over a void/ravine, the wolf **STOPS** chasing instead of jumping after them.
* **Lava & Fire Avoidance:** Wolves actively pathfind around dangerous blocks.
* **Creeper Awareness:** Wolves will flee when they hear a Creeper hiss!
* **Anti-Stuck:** Improved pathfinding logic to keep them following close.

### 🌩️ Environmental Awareness

* **Storm Anxiety:** Wolves are scared of Thunderstorms! They will whine and shake in fear.
* **Behavior**: If sitting, they stay put. If standing, they pace anxiously.
* *Intensity and trigger chances are fully adjustable in the config.*

---

## ⚙️ Configuration (The "Control Everything" Update)

**Updated in v1.6.0**: We've exposed almost every internal variable to the config menu!

* **Granular Per-Personality Stats**: Set unique Health, Speed, and Damage for Aggressive, Pacifist, and Normal wolves.
* **Custom AI Ranges**: Adjust exactly how far wolves follow you, when they teleport, and how far they chase enemies.
* **Breeding Odds**: Control the genetic pass-through and spawn rates of personalities.
* **Wild Hunt Logic**: Configure wild wolves to only hunt when they are hungry (HP based).

---

## 📦 Installation

1. Download **[Fabric Loader](https://fabricmc.net/)** for Minecraft **1.21.11** or **26.1+** (Snapshot).
2. Install **[Fabric API](https://modrinth.com/mod/fabric-api)** and **[Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)**.
3. Install **[Cloth Config](https://modrinth.com/mod/cloth-config)** (Required).
4. Install **[Mod Menu](https://modrinth.com/mod/modmenu)** (Recommended for in-game config).
5. Download `Vanilla-Outsider-Better-Dogs.jar` and place it in your `mods` folder.

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

If you enjoy **Better Dogs** and the **Vanilla Outsider** philosophy, consider fueling the next update with a coffee!

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/dasikigaijin/tip)

## 📜 Legal & Credits

| Role | Author |
| :--- | :--- |
| **Creator** | DasikIgaijin |
| **Collection** | Vanilla Outsider |
| **License** | GNU GPLv3 |

> [!IMPORTANT]
> This mod is part of the **Vanilla Outsider** collection. You are free to use it in modpacks, videos, and servers.
>
> > [!IMPORTANT]
> > **Modpack Permissions:** You are free to include this mod in modpacks, **provided the modpack is hosted on the same platform** (e.g. Modrinth).
> >
> > **Cross-platform distribution is not permitted.** If you download this mod from Modrinth, your modpack must also be published on Modrinth.

**This Mod made with the assistance of AI**

---

<div align="center">

**Made with ❤️ for the Minecraft community**

*Part of the Vanilla Outsider Collection*

</div>
