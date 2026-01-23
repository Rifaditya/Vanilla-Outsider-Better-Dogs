![Banner](https://i.imgur.com/LnG7vm3.png)

[![Requires Fabric API](https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric)](https://www.curseforge.com/minecraft/mc-mods/fabric-api) ![NeoForge Support](https://img.shields.io/badge/Loader-NeoForge-orange?style=for-the-badge) ![Standalone Config](https://img.shields.io/badge/Config-Standalone-orange?style=for-the-badge) ![License](https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge)

# 🐕 Make Wolves Worthy Companions

**No Backports:** I will **NOT** backport this mod to older versions (1.21, 1.20, etc.). Please do not ask.

Every Minecraft player knows the pain: you spend hours finding a wolf, tame it, and five minutes later it jumps into lava or walks off a cliff.

**Vanilla Outsider: Better Dogs** completely overhauls wolf AI to make them smarter, safer, and livelier. Now natively supporting both **Fabric** and **NeoForge**, every dog feels unique with the **Personality System**.

> [!IMPORTANT]
> **Beta Release for Minecraft 26.1 Snapshots**
> This version is built on the latest standard for Minecraft 26.1. It includes significant AI rewrites. Please report any bugs on GitHub!

---

## ✨ Key Features

### 🧠 Unique Personality System

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

> [!TIP]
> **Untrained Baby Nature (v1.8.3):** Tamed baby wolves are more curious and independent. They wander further (2x) and teleport less frequently by default, giving them a more "wild" feel until they grow up. These multipliers are fully configurable!
>
> [!NOTE]
> **Breeding & Inheritance:** Personalities are permanent and can be inherited via breeding! Note that **collar colors** follow vanilla mechanics (inherited from parents) and **do not** indicate personality types—always look for the unique particle effects!

### 🐕 Baby Wolf Behavior System (v1.8.x)

Baby wolves now have unique behavior rules:

* **Passive by Default:** Babies won't attack what owner attacks or defend unless Aggressive or directly hit.
* **Domestic Retaliation:** Accidentally hit your puppy? It retaliates with **2 strikes** then forgives!
* **Adult Intervention:** Aggressive adults discipline babies that attack their owner.
* **Puppy Protection:** Other wolves won't target tamed baby wolves.
* **Untrained Nature:** Babies wander further (2x) and teleport less frequently.

### 🛡️ Smart Safety AI (v1.8.4+)

**Wolves are finally smart enough to NOT jump.**

* **Baby Curiosity:** Passive/Normal babies investigate blocks and stare at entities.
* **Reckless Aggression:** Aggressive babies engage hostiles immediately.
* **Active Retreat:** Wolves walk backwards from cliff edges instead of jumping.
* **Lava & Fire Warnings:** Wolves pathfind away from hazardous blocks.

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

1. Download the latest **Minecraft 26.1 Snapshot**.
2. **Fabric Users:**
   * Install **[Fabric Loader](https://fabricmc.net/)**.
   * Install **[Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)**.
3. **NeoForge Users:**
   * Install the latest **NeoForge** for 26.1.
4. Place the `Vanilla-Outsider-Better-Dogs.jar` in your `mods` folder.
5. *(Optional)* Edit `config/betterdogs.json` after the first launch to customize your experience.

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
> **Modpack Permissions:** You are free to include this mod in modpacks, **provided the modpack is hosted on the same platform** (e.g. CurseForge). Cross-platform distribution is not permitted.

---

<div align="center">

**Made with ❤️ for the Minecraft community**

*Part of the Vanilla Outsider Collection*

</div>
