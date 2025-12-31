# 🖼️ Icon Patch (v1.7.1)

* Added custom mod icon to Mod Menu

---

# 🧬 Config Clarity Update (v1.7.0)

## ✨ New Features

### Separated Taming & Breeding Settings

The config menu now clearly distinguishes between:

* **Taming Personality** - Controls personality chances when taming wild wolves
  * Normal Chance (Def: 60)
  * Aggressive Chance (Def: 20)
  * Pacifist Chance (Def: 20)

* **Breeding Genetics** - NEW! Configure inheritance rules when breeding wolves
  * Same Parents % (Def: 80) - Chance offspring inherits same personality
  * Same Parents Other % (Def: 10) - Chance for each other personality
  * Mixed Dominant % (Def: 40) - Chance for each parent's personality (Normal+Other)
  * Mixed Recessive % (Def: 20) - Chance for third personality (Normal+Other)
  * Diluted Normal % (Def: 50) - Chance for Normal (Aggressive+Pacifist)
  * Diluted Other % (Def: 25) - Chance for each parent (Aggressive+Pacifist)

### Fully Configurable Breeding

Previously hardcoded breeding genetics are now fully adjustable! Create aggressive-only bloodlines or make rare personalities more common through selective breeding.

---

# 🐕 Initial Release - The "Worthy Companions" Update (v1.6.0)

Welcome to the official Modrinth launch of **Vanilla Outsider: Better Dogs**! While this is the first public upload, the version is marked as **1.6.0** to reflect the intensive internal development, balancing, and feature refinement that took place before this release.

This mod transforms your wolves from fragile followers into intelligent, capable, and unique companions.

## 🌟 Key Pillars of this Release

### 🧠 The Personality System

Every wolf you tame now receives a permanent personality:

* **Normal**: The classic vanilla experience with modern speed/stat buffs.
* **Aggressive**: A proactive protector that hunts hostiles near you.
* **Pacifist**: A gentle loyalist that only fights when you are directly hurt.

### 🛡️ Technical AI & Safety

* **Cliff Safety V2**: Dogs will no longer suicide-jump off cliffs or into ravines while chasing enemies.
* **Creeper Awareness**: Dogs are smart enough to flee when a Creeper is about to blow.
* **Fire/Lava Avoidance**: Significantly improved pathfinding around hazards.

### ⚔️ Combat & Survival

* **Armor Compatibility**: Full support for vanilla wolf armor.
* **Passive Healing**: Dogs now slowly recover health over time when out of combat.
* **The Gift System**: Loyal dogs will occasionally bring you loot based on their personality (Hunter's trophies vs. Nature foraging).
* **Friendly Fire Protection**: No more accidental sword sweeps killing your best friend.

### 🎚️ Extreme Configuration (v1.6.0+)

We have exposed almost every internal setting to the **Cloth Config** menu. You can now adjust:

* Personality spawn weights (e.g., make aggressive dogs rarer).
* Per-personality stats (Health, Speed, Damage, Knockback).
* Follow and Teleport distances.
* Gift cooldowns and Storm Anxiety frequency.

## 🛠️ Internal Fixes (Pre-Release Polish)

* Fixed a crash related to personality persistence in NBT data.
* Refactored attribute modifiers to use direct Mojang mappings (improving performance).
* Optimized skeleton-targeting logic for aggressive wolves.
* Fully localized all config keys into English (`en_us`).

***

**Welcome to the pack!** 🐺✨
