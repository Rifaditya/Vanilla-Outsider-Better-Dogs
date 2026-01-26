<div align="center">

![banner AI generated](https://cdn.modrinth.com/data/cached_images/1909cfcc36754c4a370e00e17f1d8ebe7d190405_0.webp)

</div>
<p align="center">
    <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <img src="https://img.shields.io/badge/Language-Java_25-red?style=for-the-badge&logo=java" alt="Java 25">
    <img src="https://img.shields.io/badge/Config-Standalone-orange?style=for-the-badge" alt="Standalone Config">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
</p>

# 🐕 Make Wolves Worthy Companions (Fabric Edition)

Every Minecraft player knows the pain: you spend hours finding a wolf, tame it, and five minutes later it jumps into lava or walks off a cliff.

**Vanilla Outsider: Better Dogs** completely overhauls wolf AI to make them smarter, safer, and livelier. Now powered by a high-performance **Event-Driven Scheduler (v3.0+)**, wolves act efficiently without lagging your world.

> [!CAUTION]
> **No Backports:** I will **NOT** backport this mod to older versions. Please do not ask.

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

### 🐕 Advanced Social AI (The "Snitch" System)

Wolves now interact with each other in complex, organic ways using our new **Event-Driven AI**:

* **Baby Retaliation (The One-Bite Rule):** If you hit a baby wolf, it bites you back **EXACTLY ONCE** to teach you a lesson, then forgives you.
* **Adult Correction (The Snitch System):** If a baby bites you, nearby Aggressive Adults will intervene. The baby "snitches" on itself, causing one adult to walk over and discipline it with **ONE BITE**.
* **Blood Feuds:** Sometimes, discipline goes too far. There is a small chance (5%) that a correction turns into a permanent **Blood Feud**.
* **Play Fighting:** Large packs engage in safe, non-lethal play fights to burn off energy.
* **Pack Separation (v3.1.10):** Wolves respect "personal space" and spread out if crowded.
* **Natural Follow (v3.1.11):** Dogs try to **catch up by running faster** before teleporting. Teleport distance increased to 18 blocks.
* **Personality-Scaled Teleportation (v3.1.13):** Teleport thresholds scale with personality (0.5x to 5.0x range).
* **Ultraguard Sync (v3.1.13):** Config files automatically merge new settings and protect against corruption with atomic writes.

### 🛡️ Smart Safety AI

* **Cliff Safety:** Wolves detect airborne targets and **STOP** chasing instead of jumping.
* **Lava & Fire Avoidance:** Improved pathfinding around hazards.
* **Creeper Awareness:** Wolves flee when they hear a Creeper hiss!

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

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/D1D81SP3P7)

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
