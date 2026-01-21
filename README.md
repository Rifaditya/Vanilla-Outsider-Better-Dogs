<div align="center">

![banner AI generated](https://cdn.modrinth.com/data/cached_images/1909cfcc36754c4a370e00e17f1d8ebe7d190405_0.webp)

</div>
<p align="center">
    <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <a href="https://modrinth.com/mod/fabric-language-kotlin"><img src="https://img.shields.io/badge/Language-Kotlin-purple?style=for-the-badge&logo=kotlin" alt="Kotlin"></a>
    <img src="https://img.shields.io/badge/Config-Standalone-orange?style=for-the-badge" alt="Standalone Config">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
</p>

# 🐕 Make Wolves Worthy Companions (Now for 26.1 Snapshots!)

Every Minecraft player knows the pain: you spend hours finding a wolf, tame it, and five minutes later it jumps into lava or walks off a cliff.

**Vanilla Outsider: Better Dogs** completely overhauls wolf AI to make them smarter, safer, and livelier. With the new **Personality System**, every dog feels unique—some are aggressive protectors, while others are loyal pacifists.

> [!IMPORTANT]
> **Beta Release for Minecraft 26.1 Snapshots**
> This version is built on the latest standard for Minecraft 26.1. It includes significant behavior logic rewrites. Please report any bugs on GitHub!
>
> **No Backports:** I will **NOT** backport this mod to older versions (1.21, 1.20, etc.). Please do not ask.

---

## ✨ Key Features

### � Unique Personality System

When you tame a wolf, it develops one of three permanent personalities, visually distinct by their particle effects.

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

### 🛡️ Smart Cliff Safety (Updated v1.7.5)

**Wolves are finally smart enough to NOT jump.**

* **Active Retreat:** When a wolf detects a steep drop (3+ blocks) or checks a target floating over the void, it doesn't just stop—it **actively walks backwards** 4 blocks away from the edge.
* **Airborne Checks:** If you knock a zombie off a cliff, your wolf won't dive after it.
* **Lava & Fire Warnings:** Wolves actively pathfind away from hazardous blocks.

### ⚙️ Standalone Config System (New v1.7.6)

Due to changes in the 26.1 snapshot environment, we have replaced the heavy `Cloth Config` dependency with a lightweight **Standalone JSON Loader**.

* **No GUI Needed:** Configuration is fully supported even in snapshot versions where Mod Menu isn't available.
* **Manual Control:** Edit `config/betterdogs.json` to tweak everything: Friendly Fire, Speed Buffs, Breeding Chances, and more.

### 🎁 The Gift System

Like cats, your loyal dogs will now bring you treasures based on their personality!

* **Aggressive**: Trophy items (Bones, Rotten Flesh).
* **Pacifist**: Foraged items (Berries, Flowers).

### 🌩️ Environmental Awareness

* **Storm Anxiety**: Wolves are scared of Thunderstorms! They will whine and shake in fear unless they are safe indoors or sitting.

---

## 📦 Installation

1. Download **[Fabric Loader](https://fabricmc.net/)** for the latest **Minecraft 26.1 Snapshot**.
2. Install **[Fabric API](https://modrinth.com/mod/fabric-api)**.
3. Download `Vanilla-Outsider-Better-Dogs.jar` and place it in your `mods` folder.
4. *(Optional)* Edit `config/betterdogs.json` after the first launch to customize your experience.

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
> **Modpack Permissions:** You are free to include this mod in modpacks, **provided the modpack is hosted on the same platform** (e.g. Modrinth). Cross-platform distribution is not permitted.

---

<div align="center">

**Made with ❤️ for the Minecraft community**

*Part of the Vanilla Outsider Collection*

</div>
