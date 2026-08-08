# Goat Horns & Tactical Pack Commands

*[[Home]] / [[Items & Interactions|Items-and-Interactions]] / Goat Horns*

---

## 📯 Infobox: Goat Horn Command System

| Parameter | Specification |
| :--- | :--- |
| **Item Used** | Vanilla Goat Horn (`minecraft:goat_horn`) |
| **Command Effective Range** | **64 blocks** (`bd_horn_command_range`) |
| **Pathing Timeout** | **400 ticks** (20 seconds) (`bd_horn_pathing_timeout`) |
| **Override Duration** | **600 ticks** (30 seconds) (`bd_horn_override_duration`) |
| **Affected Entities** | Tamed wolves owned by the player within range |

---

## 🎵 1. Horn Types & Tactical Orders

Sounding a goat horn issues tactical orders to all owned wolves within a 64-block spherical radius:

| Goat Horn Type | Tactical Order | Behavioral Effect |
| :--- | :--- | :--- |
| **Ponder Horn** | **Set Guard Posts** | Orders all nearby following wolves to enter Guard Mode at their current locations. |
| **Sing Horn** | **Tactical Override** | Commands Aggressive wolves to adopt Pacifist defensive rules for 30 seconds (`bd_horn_override_duration`), preventing reckless attacks during hazardous encounters. |
| **Seek / Call Horn** | **Recall Pack** | Recalls distant following wolves back to the player's position, canceling active chase AI goals. |

---

## ⏱️ 2. Pathing & Override Timeouts

* **Pathing Timeout**: Wolves pathfinding to a sounded horn location attempt pathfinding for up to **400 ticks (20s)** (`bd_horn_pathing_timeout`). If pathing is obstructed by lava or steep cliffs, the wolf aborts pathing safely.
* **Override Duration**: Tactical Pacifist override remains active for **600 ticks (30s)** (`bd_horn_override_duration`) before wolves revert to standard personality AI rules.

---

*Back to [[Home]] | View [[Items-and-Interactions]]*
