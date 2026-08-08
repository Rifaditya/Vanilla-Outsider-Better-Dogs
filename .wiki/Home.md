# Vanilla Outsider: Better Dogs

[![Minecraft](https://img.shields.io/badge/Minecraft-26.2-brightgreen.svg)](https://minecraft.net)
[![Fabric Loader](https://img.shields.io/badge/Fabric-0.19.1+-blue.svg)](https://fabricmc.net)
[![DasikLibrary](https://img.shields.io/badge/DasikLibrary-1.8.3+-orange.svg)](https://github.com/Rifaditya/DasikLibrary-Rebuilt)
[![License: GPLv3](https://img.shields.io/badge/License-GPLv3-yellow.svg)](https://www.gnu.org/licenses/gpl-3.0)

Welcome to the official GitHub Wiki for **Vanilla Outsider: Better Dogs**!

**Vanilla Outsider: Better Dogs** transforms Minecraft's vanilla wolves into intelligent, expressive companions with distinct personality traits, dynamic genetics, realistic physical scaling, tactical pack AI, and dedicated guard capabilities—all designed to seamlessly blend into vanilla survival gameplay.

---

## 📦 Minecraft Version Directory

Select your target Minecraft version to view dedicated installation guides, dependency bounds, and release notes:

* [[Minecraft 26.3 Guide & Release Notes|Minecraft-26.3-Guide]] — *Latest Snapshot Build*
* [[Minecraft 26.2 Guide & Release Notes|Minecraft-26.2-Guide]] — *Primary Stable Target*
* [[Minecraft 26.1 Guide & Release Notes|Minecraft-26.1-Guide]] — *Legacy Maintenance Build*
* [[Version Compatibility & History|Version-Compatibility]] — *Full version matrix and legacy 1.21.11 evolution*

---

## 🎮 Player & Administrator Guides

Learn how to interact with, breed, command, and configure your wolf packs in survival gameplay:

* [[Player Guide & Mechanics|Player-Guide-and-Mechanics]] — Personalities, genetics, scale genetics, storm anxiety, cliff safety, creeper evasion, guard mode, goat horn commands, dog riding, and morning gifts.
* [[GameRules & Commands|GameRules-and-Commands]] — Complete namespaced GameRules reference table (`betterdogs:*`), default values, category toggles, and Brigadier command suite syntax (`/betterdogs`).
* [[Configuration Guide|Configuration-Guide]] — Server-side GameRule authority, client GUI integration via ModMenu & YACL v3, and config precedence.

---

## 💻 Developer & Contributor Reference

Technical documentation for building from source, analyzing mixin injection points, and developing addons:

* [[Developer Setup & Building|Developer-Setup-and-Building]] — Environment setup (JDK 25, Gradle 9.3+, Loom 1.15+) and building tagged release JARs (`./gradlew build --no-daemon`).
* [[Architecture & Mixins|Architecture-and-Mixins]] — Package organization, complete Mixin target breakdown (25 mixins), non-blocking AI goal priority stack, and thread looper safety.
* [[API & Addon Integration|API-and-Addon-Integration]] — DasikLibrary integration (`DynamicGameRuleManager`, `ModVersionGuard`), custom wolf data codecs, Jade WTHIT integration, and extension event hooks.
