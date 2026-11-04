# Vanilla Outsider: Better Dogs (Minecraft 1.21.11 / Winter Drop)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-brightgreen.svg)](https://minecraft.net)
[![Fabric Loader](https://img.shields.io/badge/Fabric-0.16.0+-blue.svg)](https://fabricmc.net)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net)
[![License: GPLv3](https://img.shields.io/badge/License-GPLv3-yellow.svg)](https://www.gnu.org/licenses/gpl-3.0)

Welcome to the dedicated documentation for **Vanilla Outsider: Better Dogs** on **Minecraft 1.21.11** (Release `1.0.73+1.21.11`).

---

## 🎮 Gameplay Mechanics Matrix

Explore detailed guides tailored to Minecraft 1.21.11:

* [[Wolf Personalities & Combat AI|Wolf-Personalities]] — Aggressive, Pacifist, and Normal traits, combat follow distances, and storm anxiety.
* [[Guard Mode & Sentinel Patrols|Guard-Mode-and-Sentinels]] — Sentinel patrol radiuses (12 blk Aggressive, 3 blk Pacifist, 0 blk Normal) and soothing healing aura.
* [[Breeding, Genetics & Litter Sizes|Breeding-and-Genetics]] — Litters (1 to 4 puppies), inbreeding runt penalties, and scale genetics (70% - 145%) powered by native `Attributes.SCALE`.
* [[Goat Horns & Tactical Commands|Goat-Horns-and-Tactical-Commands]] — Acoustic tactical commands (Ponder, Sing, Seek/Call) across a 64-block range.
* [[Adoption System|Adoption-System]] — Paper adoption certificates, listing states, and secure ownership transfers.
* [[Dog Riding & Vehicle Transport|Dog-Riding-and-Vehicles]] — Boarding boats, minecarts, and horses via Shift+Stick interaction.
* [[Wild Packs & Territoriality|Wild-Packs-and-Territoriality]] — Pack alpha elections, territorial disputes, and custom Chorus Howl audio.
* [[Ground Feeding & Favorite Treats|Ground-Feeding-and-Favorite-Treats]] — Autonomous ground food consumption, `feedCount` tracking, and zoomies.
* [[Advancements & Achievements|Advancements]] — Complete 13-advancement Husbandry tree.
* [[Namespaced GameRules Reference|GameRules]] — All 22 namespaced GameRules (`betterdogs:bd_*`).
* [[Brigadier Command Suite|Commands]] — Full `/betterdogs` command suite.
* [[Configuration & GUI Integration|Configuration]] — Server GameRule authority and ModMenu client GUI.
* [[HUD & Diagnostics|HUD-and-Diagnostics]] — Visual particles and diagnostic tools.

---

## 💻 Developer & Contributor Reference

* [[Developer Setup & Building|Developer-Setup-and-Building]] — Setup with Java 21, Gradle 9.3+, Loom Remap 1.15-SNAPSHOT, and `./gradlew build --no-daemon`.
* [[Architecture & Mixins|Architecture-and-Mixins]] — Relocated entity architecture (`net.minecraft.world.entity.animal.wolf.Wolf`), 9 Mixin targets, and AI priority stack.
* [[API & Addon Integration|API-and-Addon-Integration]] — Event hooks, data attachments, and treat registrations.
