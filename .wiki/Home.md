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

## 🎮 Player & Gameplay Mechanics Matrix

Explore detailed encyclopedic guides for every feature pillar:

* [[Wolf Personalities & Combat AI|Wolf-Personalities]] — Aggressive, Pacifist, and Normal personality traits, stat modifiers, follow distances, and storm anxiety.
* [[Guard Mode & Sentinel Patrols|Guard-Mode-and-Sentinels]] — Sentinel patrol radiuses (12 blk Aggro, 3 blk Pacifist, 0 blk Normal), Regeneration/Resistance owner aura, and alarm particles.
* [[Breeding, Genetics & Litter Sizes|Breeding-and-Genetics]] — Personality inheritance formulas, litters (1 to 4 puppies), inbreeding runt penalties, Golden Apple curing, and physical scale genetics (70% - 145%).
* [[Goat Horns & Tactical Commands|Goat-Horns-and-Tactical-Commands]] — Goat Horn tactical orders (Ponder, Sing, Seek/Call), 64-block command range, and tactical override durations.
* [[Adoption System|Adoption-System]] — Paper adoption system, pending states, ownership transfers, and damage cancellation logic.
* [[Dog Riding & Vehicle Transport|Dog-Riding-and-Vehicles]] — Vehicle boarding (Boats, Minecarts, Horses) and riding permission rules (`bd_allow_unrestricted_dog_riding`).
* [[Wild Packs & Territorial Rivalries|Wild-Packs-and-Territoriality]] — Pack cluster sizes (4-8 wolves), expanded biome spawning, and the Territorial Dispute Matrix (War %, Merge %, Retreat %).
* [[Ground Feeding & Favorite Treats|Ground-Feeding-and-Favorite-Treats]] — Automatic ground meat feeding, low health fleeing, ground food refusal, UUID-seeded favorite treats, zoomies, and morning gifts.
* [[Advancements & Achievements|Advancements]] — Complete Husbandry advancement tree guide detailing all 13 custom advancements, criteria, and rewards.
* [[Namespaced GameRules Reference|GameRules]] — Complete reference table of all 60+ namespaced GameRules (`betterdogs:bd_*`), data types, default values, and category toggles.
* [[Brigadier Command Suite|Commands]] — Brigadier command tree (`/betterdogs`) and debug subtrees (`personality`, `action`, `territory`).
* [[Configuration & GUI Integration|Configuration]] — Server-side GameRule authority, client GUI integration via ModMenu & YACL v3, and config precedence.
* [[HUD & Diagnostics|HUD-and-Diagnostics]] — Jade/WTHIT HUD overlay, debug stick shortcuts, runt particles, and diagnostic administration.

---

## 💻 Developer & Contributor Reference

Technical documentation for building from source, analyzing mixin injection points, and developing addons:

* [[Developer Setup & Building|Developer-Setup-and-Building]] — Environment setup (JDK 25, Gradle 9.3+, Loom 1.15+) and building tagged release JARs (`./gradlew build --no-daemon`).
* [[Architecture & Mixins|Architecture-and-Mixins]] — Package organization, complete Mixin target breakdown (25 mixins), non-blocking AI goal priority stack, and thread looper safety.
* [[API & Addon Integration|API-and-Addon-Integration]] — DasikLibrary integration (`DynamicGameRuleManager`, `ModVersionGuard`), custom wolf data codecs, Jade WTHIT integration, and extension event hooks.
